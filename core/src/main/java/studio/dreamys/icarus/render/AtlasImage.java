package studio.dreamys.icarus.render;

/** An image drawn from the shared atlas (e.g. a generated texture). It's (re)added to the atlas when needed. */
public final class AtlasImage {
    private final int[] argb;
    private final int width, height;

    private FontAtlas atlas;
    private int generation = -1;
    private int x, y;
    private boolean placed;

    public AtlasImage(int[] argb, int width, int height) {
        if (argb.length != width * height) throw new IllegalArgumentException("argb has " + argb.length + " pixels, expected " + width * height);
        this.argb = argb.clone();
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /** Makes sure the image is in {@code atlas}; returns false if it doesn't fit. */
    boolean ensure(FontAtlas atlas) {
        if (this.atlas != atlas || generation != atlas.getGeneration()) {
            int[] at = atlas.add(argb, width, height);
            this.atlas = atlas;
            generation = atlas.getGeneration();
            placed = at != null;
            if (placed) {
                x = at[0];
                y = at[1];
            }
        }
        return placed;
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }
}
