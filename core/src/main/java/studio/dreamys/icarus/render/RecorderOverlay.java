package studio.dreamys.icarus.render;

import studio.dreamys.icarus.platform.RecorderShield;
import studio.dreamys.icarus.platform.Renderer;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Shows draw lists in the {@link RecorderShield} instead of the game. During a frame, whatever is hidden from
 * recorders is {@link #submit submitted} (each mod's HUD, the open menu, the toasts over it); at the end of the frame
 * {@link #flush} shows each list in its own layer, stacked in submission order, and hides the layers of lists that
 * weren't submitted.
 * <p>
 * Each layer is rasterised on the CPU into the smallest rectangle that holds its list. A list identical to its
 * previous frame, or moved by whole pixels (dragging the menu), isn't rasterised again, and other layers aren't
 * touched, so a still HUD costs nothing while the menu moves. Steady frames don't allocate.
 */
public final class RecorderOverlay {
    /** Layers are hidden when nothing was presented for this long (the HUD stopped drawing, a screen was replaced). */
    static final long STALE_NANOS = 150_000_000L;

    private final Map<DrawList, LayerState> states = new IdentityHashMap<>();
    private final List<LayerState> all = new ArrayList<>();
    private DrawList[] pending = new DrawList[8];
    private int pendingCount;
    private LayerState[] order = new LayerState[8], previousOrder = new LayerState[8];
    private int previousOrderCount;
    private final int[] bounds = new int[4];
    private boolean shown;
    private long lastShown;
    /** Lists actually rasterised (tests). */
    int rasterisations;

    /** Adds a list to show in the hidden layers this frame, above those submitted before it. */
    public void submit(DrawList list) {
        if (list == null || list.isEmpty()) return;
        for (int i = 0; i < pendingCount; i++) if (pending[i] == list) return;
        if (pendingCount == pending.length) pending = java.util.Arrays.copyOf(pending, pendingCount * 2);
        pending[pendingCount++] = list;
    }

    /** Whether there is anything to show or hide (so {@link #flush} can be skipped when hiding isn't used). */
    public boolean hasWork() {
        return pendingCount > 0 || shown;
    }

    /**
     * Ends the frame: shows the submitted lists, each in its own layer, and hides the rest. Fragments below
     * {@code alphaCutoff} are dropped like the platform's shader does ({@link Renderer#getAlphaCutoff()}). If the
     * shield can't hide them, the submitted lists are drawn in game with {@code fallback} instead and false is
     * returned.
     */
    public boolean flush(RecorderShield shield, FontAtlas atlas, float alphaCutoff, int framebufferWidth, int framebufferHeight, Renderer fallback) {
        int count = pendingCount;
        pendingCount = 0;
        boolean ok = shield.isAvailable();
        if (order.length < count) {
            order = new LayerState[count * 2];
            previousOrder = new LayerState[count * 2];
        }
        for (int i = 0; ok && i < count; i++) {
            LayerState state = states.get(pending[i]);
            if (state == null) {
                RecorderShield.Layer layer = shield.createLayer();
                if (layer == null) {
                    ok = false;
                    break;
                }
                state = new LayerState(layer);
                states.put(pending[i], state);
                all.add(state);
            }
            state.used = true;
            order[i] = state;
            ok = state.present(pending[i], atlas, alphaCutoff, framebufferWidth, framebufferHeight);
        }
        if (!ok) {
            hideAll();
            for (int i = 0; i < count; i++) fallback.render(pending[i]);
            clear(count);
            return false;
        }
        for (int i = 0; i < all.size(); i++) {
            LayerState state = all.get(i);
            if (!state.used) state.hide();
            state.used = false;
        }
        //restack only when the order changed; each layer goes right under the one drawn after it
        boolean restack = count != previousOrderCount;
        for (int i = 0; !restack && i < count; i++) restack = order[i] != previousOrder[i] || order[i].reshown;
        if (restack) for (int i = count - 2; i >= 0; i--) order[i].layer.placeUnder(order[i + 1].layer);
        for (int i = 0; i < count; i++) {
            order[i].reshown = false;
            previousOrder[i] = order[i];
        }
        previousOrderCount = count;
        shown = count > 0;
        if (shown) lastShown = System.nanoTime();
        clear(count);
        return true;
    }

    /** Hides every layer (hiding was turned off). */
    public void hide() {
        if (!shown) return;
        hideAll();
    }

    /** Hides every layer if nothing was presented for a while. Called every client tick. */
    public void hideIfStale() {
        if (shown && System.nanoTime() - lastShown > STALE_NANOS) hideAll();
    }

    /** Forgets every layer (the shield was replaced). */
    public void reset() {
        states.clear();
        all.clear();
        pendingCount = 0;
        previousOrderCount = 0;
        shown = false;
    }

    public boolean isShown() {
        return shown;
    }

    private void hideAll() {
        for (int i = 0; i < all.size(); i++) all.get(i).hide();
        previousOrderCount = 0;
        shown = false;
    }

    private void clear(int count) {
        for (int i = 0; i < count; i++) pending[i] = null;
    }

    /** One list's layer: its raster and what it last showed. */
    private final class LayerState {
        final RecorderShield.Layer layer;
        final SoftwareRaster raster = new SoftwareRaster();
        final ListCopy previous = new ListCopy();
        int previousX0, previousY0, previousW, previousH;
        int previousAtlasSize = -1, previousGeneration = -1;
        float previousCutoff = -1;
        boolean previousValid, previousClamped, visible, used, reshown;

        LayerState(RecorderShield.Layer layer) {
            this.layer = layer;
        }

        boolean present(DrawList list, FontAtlas atlas, float alphaCutoff, int framebufferWidth, int framebufferHeight) {
            SoftwareRaster.emptyBounds(bounds);
            SoftwareRaster.unionBounds(list, framebufferWidth, framebufferHeight, bounds);
            int x0 = bounds[0], y0 = bounds[1], w = bounds[2] - bounds[0], h = bounds[3] - bounds[1];
            boolean clamped = isClamped(list, framebufferWidth, framebufferHeight);
            boolean same = previousValid && !clamped && !previousClamped && w == previousW && h == previousH
                    && atlas.getSize() == previousAtlasSize && atlas.getGeneration() == previousGeneration
                    && alphaCutoff == previousCutoff && previous.matches(list, x0 - previousX0, y0 - previousY0);
            if (!same) {
                raster.setAlphaCutoff(alphaCutoff);
                raster.begin(x0, y0, w, h, 0);
                raster.draw(list, atlas);
                rasterisations++;
                previous.copy(list);
                previousAtlasSize = atlas.getSize();
                previousGeneration = atlas.getGeneration();
                previousCutoff = alphaCutoff;
                previousClamped = clamped;
                previousValid = true;
            }
            previousX0 = x0;
            previousY0 = y0;
            previousW = w;
            previousH = h;
            //on a pure move the raster's own origin is stale, but the layer only needs the new position;
            //a hidden layer gets the pixels again, the raster still holds them
            if (!layer.show(raster.getPixels(), raster.getWidth(), w, h, x0, y0, !same || !visible)) {
                previousValid = false;
                visible = false;
                return false;
            }
            if (!visible) reshown = true;
            visible = true;
            return true;
        }

        void hide() {
            if (!visible) return;
            visible = false;
            layer.hide();
        }
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
