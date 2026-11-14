package studio.dreamys.icarus.platform;

/**
 * The drawing primitives Icarus is built on. Coordinates are GUI-scaled and affected by the
 * current matrix; colors are ARGB ints. Every draw blends with (src alpha, 1 - src alpha).
 */
public interface Renderer {
    /** Solid quad from (x1, y1) to (x2, y2), drawn exactly at the given (possibly fractional) coordinates. */
    void fill(double x1, double y1, double x2, double y2, int color);

    /** Quad with {@code topColor} along its top edge fading vertically into {@code bottomColor}. */
    void fillGradient(double x, double y, double width, double height, int topColor, int bottomColor);

    /**
     * Uploads an ARGB image. Must be called on the render thread.
     *
     * @param linear use linear magnification filtering instead of nearest
     */
    Texture createTexture(int width, int height, int[] argb, boolean linear);

    /** Textured quad, with the texture's colors multiplied by {@code tint}. */
    void drawTexture(Texture texture, double x, double y, double width, double height, float u0, float v0, float u1, float v1, int tint);

    void pushMatrix();

    void scale(float x, float y);

    void popMatrix();
}
