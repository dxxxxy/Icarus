package studio.dreamys.icarus.platform;

/** Opaque handle to a texture created by {@link Renderer#createTexture}. */
public interface Texture {
    int getWidth();

    int getHeight();
}
