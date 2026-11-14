package studio.dreamys.icarus.platform;

import studio.dreamys.icarus.render.DrawList;

/**
 * The drawing a platform has to provide: RGBA textures and rendering a {@link DrawList} (indexed triangles in
 * framebuffer pixels, one texture per command). All methods are called on the render thread.
 */
public interface Renderer {
    /** A new RGBA texture with nearest filtering and undefined contents. */
    Texture createTexture(int width, int height);

    /**
     * Replaces a region of a texture. {@code argb[offset + row * stride + column]} is the pixel at
     * ({@code x + column}, {@code y + row}).
     */
    void upload(Texture texture, int x, int y, int width, int height, int[] argb, int offset, int stride);

    void deleteTexture(Texture texture);

    /** Draws the list over whatever is on screen, with alpha blending, in the current GUI pass. */
    void render(DrawList list);
}
