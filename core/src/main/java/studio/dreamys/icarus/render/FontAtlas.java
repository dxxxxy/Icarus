package studio.dreamys.icarus.render;

import studio.dreamys.icarus.platform.Renderer;
import studio.dreamys.icarus.platform.Texture;

import java.util.HashMap;
import java.util.Map;

/**
 * The single texture everything Icarus draws comes from: glyphs (rasterised on demand), generated images, and a
 * white block used for solid shapes. Because all primitives share it, a frame is normally one draw call.
 * <p>
 * Regions are packed in shelves. When the atlas fills up it doubles (up to {@link #MAX_SIZE}); beyond that it's
 * cleared and {@link #getGeneration()} increments so caches re-add what they need. The CPU copy is uploaded in one
 * dirty rectangle per frame by {@link #sync(Renderer)}.
 */
public final class FontAtlas {
    static final int INITIAL_SIZE = 1024;
    static final int MAX_SIZE = 2048;
    private static final int PADDING = 1;

    private int size;
    private int[] pixels;
    private int shelfX, shelfY, shelfHeight;
    private int dirtyX0, dirtyY0, dirtyX1, dirtyY1;
    private int generation;

    private Texture texture;
    private int textureSize;

    private final Map<String, FontFace> faces = new HashMap<>();

    public FontAtlas() {
        this(INITIAL_SIZE);
    }

    FontAtlas(int initialSize) {
        allocate(initialSize);
    }

    private void allocate(int newSize) {
        size = newSize;
        pixels = new int[size * size];
        shelfX = 0;
        shelfY = 0;
        shelfHeight = 0;
        //white block at the origin for solid shapes (sampled at its centre)
        for (int y = 0; y < 4; y++) for (int x = 0; x < 4; x++) pixels[y * size + x] = 0xFFFFFFFF;
        shelfX = 4 + PADDING;
        shelfHeight = 4;
        markDirty(0, 0, size, size);
    }

    public int getSize() {
        return size;
    }

    /** Increments whenever the atlas is cleared; cached regions from an older generation are gone. */
    public int getGeneration() {
        return generation;
    }

    /** U/V of a pixel inside the white block. */
    public float whiteU() {
        return 2f / size;
    }

    public float whiteV() {
        return 2f / size;
    }

    /**
     * Reserves a {@code w}×{@code h} region and copies {@code argb} into it. Returns {x, y} or null if the image
     * can't fit even in a cleared atlas. May grow or clear the atlas (check {@link #getGeneration()}).
     */
    public int[] add(int[] argb, int w, int h) {
        if (w + PADDING > MAX_SIZE || h + PADDING > MAX_SIZE) return null;
        int[] at = place(w, h);
        if (at == null) {
            if (size < MAX_SIZE) {
                grow();
            } else {
                clear();
            }
            at = place(w, h);
            if (at == null) return null;
        }
        for (int row = 0; row < h; row++) System.arraycopy(argb, row * w, pixels, (at[1] + row) * size + at[0], w);
        markDirty(at[0], at[1], at[0] + w, at[1] + h);
        return at;
    }

    private int[] place(int w, int h) {
        if (shelfX + w + PADDING > size) {
            shelfY += shelfHeight + PADDING;
            shelfX = 0;
            shelfHeight = 0;
        }
        if (shelfY + h + PADDING > size) return null;
        int[] at = {shelfX, shelfY};
        shelfX += w + PADDING;
        shelfHeight = Math.max(shelfHeight, h);
        return at;
    }

    private void grow() {
        int oldSize = size;
        int[] old = pixels;
        int oldShelfX = shelfX, oldShelfY = shelfY, oldShelfHeight = shelfHeight;
        size = oldSize * 2;
        pixels = new int[size * size];
        for (int row = 0; row < oldSize; row++) System.arraycopy(old, row * oldSize, pixels, row * size, oldSize);
        //keep packing below the old content; the old shelves' right side stays unused
        shelfX = 0;
        shelfY = oldShelfY + oldShelfHeight + PADDING;
        shelfHeight = 0;
        if (oldShelfX == 0 && oldShelfHeight == 0) shelfY = oldShelfY;
        markDirty(0, 0, size, size);
    }

    private void clear() {
        allocate(size);
        generation++;
        for (FontFace face : faces.values()) face.clearGlyphs();
    }

    private void markDirty(int x0, int y0, int x1, int y1) {
        if (dirtyX1 <= dirtyX0 || dirtyY1 <= dirtyY0) {
            dirtyX0 = x0;
            dirtyY0 = y0;
            dirtyX1 = x1;
            dirtyY1 = y1;
        } else {
            dirtyX0 = Math.min(dirtyX0, x0);
            dirtyY0 = Math.min(dirtyY0, y0);
            dirtyX1 = Math.max(dirtyX1, x1);
            dirtyY1 = Math.max(dirtyY1, y1);
        }
    }

    /** Creates/recreates the GPU texture and uploads pending changes. Call on the render thread before drawing. */
    public Texture sync(Renderer renderer) {
        if (texture == null || textureSize != size) {
            if (texture != null) renderer.deleteTexture(texture);
            texture = renderer.createTexture(size, size);
            textureSize = size;
            markDirty(0, 0, size, size);
        }
        if (dirtyX1 > dirtyX0 && dirtyY1 > dirtyY0) {
            renderer.upload(texture, dirtyX0, dirtyY0, dirtyX1 - dirtyX0, dirtyY1 - dirtyY0, pixels, dirtyY0 * size + dirtyX0, size);
            dirtyX0 = dirtyY0 = dirtyX1 = dirtyY1 = 0;
        }
        return texture;
    }

    /** The current texture (after {@link #sync}). */
    public Texture getTexture() {
        return texture;
    }

    /** Drops the GPU texture (e.g. after the platform lost its GL context); it's recreated on the next sync. */
    public void invalidateTexture() {
        texture = null;
    }

    /** A rasterised face of {@code family}/{@code style} at {@code pixelSize} real pixels (cached). */
    public FontFace face(String family, int style, int pixelSize) {
        String key = family + '\u0000' + style + '\u0000' + pixelSize;
        FontFace face = faces.get(key);
        if (face == null) {
            face = new FontFace(this, family, style, pixelSize);
            faces.put(key, face);
        }
        return face;
    }

    /** The CPU copy, {@link #getSize()} pixels square, ARGB (for software rendering; don't modify). */
    int[] pixels() {
        return pixels;
    }

    /** Pixel at (x, y) of the CPU copy (tests). */
    int pixel(int x, int y) {
        return pixels[y * size + x];
    }
}
