package studio.dreamys.icarus.render;

import studio.dreamys.icarus.platform.RecorderShield;

/**
 * Shows painted draw lists in the {@link RecorderShield} instead of the game: rasterises them on the CPU into the
 * smallest rectangle that holds them and hands the pixels over. Frames identical to the previous one, or the previous
 * one moved by whole pixels (dragging the window), aren't rasterised again. Steady frames don't allocate.
 */
public final class RecorderOverlay {
    /** The shield is hidden when nothing was shown for this long (a screen replaced without being closed). */
    static final long STALE_NANOS = 150_000_000L;

    private final SoftwareRaster raster = new SoftwareRaster();
    private final int[] bounds = new int[4];
    private final ListCopy[] previous = {new ListCopy(), new ListCopy()};
    private int previousX0, previousY0, previousX1, previousY1;
    private int previousAtlasSize = -1, previousGeneration = -1;
    private boolean previousValid, previousClamped;
    private boolean shown;
    private long lastShown;
    /** Frames actually rasterised (tests). */
    int rasterisations;

    /**
     * Shows {@code first} with {@code second} (may be null) drawn over it, dropping fragments below
     * {@code alphaCutoff} like the platform's shader ({@link studio.dreamys.icarus.platform.Renderer#getAlphaCutoff()}).
     * Returns false if the shield can't hide them; the caller then draws them in game.
     */
    public boolean present(RecorderShield shield, FontAtlas atlas, float alphaCutoff, int framebufferWidth, int framebufferHeight, DrawList first, DrawList second) {
        if (!shield.isAvailable()) return false;
        raster.setAlphaCutoff(alphaCutoff);
        SoftwareRaster.emptyBounds(bounds);
        SoftwareRaster.unionBounds(first, framebufferWidth, framebufferHeight, bounds);
        if (second != null) SoftwareRaster.unionBounds(second, framebufferWidth, framebufferHeight, bounds);
        int x0 = bounds[0], y0 = bounds[1], w = bounds[2] - bounds[0], h = bounds[3] - bounds[1];
        boolean clamped = isClamped(first, framebufferWidth, framebufferHeight) || second != null && isClamped(second, framebufferWidth, framebufferHeight);

        int dx = x0 - previousX0, dy = y0 - previousY0;
        boolean same = previousValid && !clamped && !previousClamped && w == previousX1 - previousX0 && h == previousY1 - previousY0
                && atlas.getSize() == previousAtlasSize && atlas.getGeneration() == previousGeneration
                && previous[0].matches(first, dx, dy) && previous[1].matches(second, dx, dy);
        if (!same) {
            raster.begin(x0, y0, w, h, 0);
            raster.draw(first, atlas);
            if (second != null) raster.draw(second, atlas);
            rasterisations++;
            previous[0].copy(first);
            previous[1].copy(second);
            previousAtlasSize = atlas.getSize();
            previousGeneration = atlas.getGeneration();
            previousClamped = clamped;
            previousValid = true;
        }
        previousX0 = x0;
        previousY0 = y0;
        previousX1 = x0 + w;
        previousY1 = y0 + h;
        //on a pure move the raster's own origin is stale, but the shield only needs the new position;
        //a hidden shield gets the pixels again, the raster still holds them
        if (!shield.show(raster.getPixels(), raster.getWidth(), w, h, x0, y0, !same || !shown)) {
            previousValid = false;
            shown = false;
            return false;
        }
        shown = true;
        lastShown = System.nanoTime();
        return true;
    }

    /** Hides the shield (the menu closed, or hiding was turned off). */
    public void hide(RecorderShield shield) {
        if (!shown) return;
        shown = false;
        shield.hide();
    }

    /** Hides the shield if nothing was presented for a while. Called every client tick. */
    public void hideIfStale(RecorderShield shield) {
        if (shown && System.nanoTime() - lastShown > STALE_NANOS) hide(shield);
    }

    /** Forgets the previous frame (the shield was replaced). */
    public void reset() {
        previousValid = false;
        shown = false;
    }

    public boolean isShown() {
        return shown;
    }

    /** Whether any vertex lies outside the framebuffer, so its bounds were cut and a move can't be reused. */
    private static boolean isClamped(DrawList list, int width, int height) {
        float[] xy = list.getPositions();
        for (int i = 0, n = list.getVertexCount(); i < n; i++) {
            float x = xy[i * 2], y = xy[i * 2 + 1];
            if (x < 0 || y < 0 || x > width || y > height) return true;
        }
        return false;
    }

    /** What a draw list held, to tell whether the next frame is the same (possibly moved by whole pixels). */
    private static final class ListCopy {
        float[] xy = new float[0], uv = new float[0];
        int[] colors = new int[0], indices = new int[0];
        int vertices = -1, indexCount;
        int commands;
        Object[] textures = new Object[0];
        int[] commandStarts = new int[0], commandCounts = new int[0];

        void copy(DrawList list) {
            if (list == null) {
                vertices = -1;
                return;
            }
            vertices = list.getVertexCount();
            indexCount = 0;
            commands = list.getCommandCount();
            if (textures.length < commands) {
                textures = new Object[commands * 2];
                commandStarts = new int[commands * 2];
                commandCounts = new int[commands * 2];
            }
            for (int c = 0; c < commands; c++) {
                textures[c] = list.getCommandTexture(c);
                commandStarts[c] = list.getCommandIndexStart(c);
                commandCounts[c] = list.getCommandIndexCount(c);
                indexCount = Math.max(indexCount, commandStarts[c] + commandCounts[c]);
            }
            if (xy.length < vertices * 2) {
                xy = new float[vertices * 4];
                uv = new float[vertices * 4];
                colors = new int[vertices * 2];
            }
            if (indices.length < indexCount) indices = new int[indexCount * 2];
            System.arraycopy(list.getPositions(), 0, xy, 0, vertices * 2);
            System.arraycopy(list.getUvs(), 0, uv, 0, vertices * 2);
            System.arraycopy(list.getColors(), 0, colors, 0, vertices);
            System.arraycopy(list.getIndices(), 0, indices, 0, indexCount);
        }

        /** Whether {@code list} equals the copy with every vertex moved by ({@code dx}, {@code dy}). */
        boolean matches(DrawList list, int dx, int dy) {
            if (list == null) return vertices == -1;
            if (list.getVertexCount() != vertices || list.getCommandCount() != commands) return false;
            for (int c = 0; c < commands; c++) {
                if (list.getCommandTexture(c) != textures[c] || list.getCommandIndexStart(c) != commandStarts[c]
                        || list.getCommandIndexCount(c) != commandCounts[c]) return false;
            }
            float[] nxy = list.getPositions(), nuv = list.getUvs();
            int[] ncolors = list.getColors(), nindices = list.getIndices();
            for (int i = 0; i < vertices; i++) {
                if (nxy[i * 2] != xy[i * 2] + dx || nxy[i * 2 + 1] != xy[i * 2 + 1] + dy) return false;
                if (nuv[i * 2] != uv[i * 2] || nuv[i * 2 + 1] != uv[i * 2 + 1] || ncolors[i] != colors[i]) return false;
            }
            for (int i = 0; i < indexCount; i++) if (nindices[i] != indices[i]) return false;
            return true;
        }
    }
}
