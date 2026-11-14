package studio.dreamys.icarus.util;

import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.platform.Texture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/** Icarus' bundled images, loaded from its own resources and uploaded on first use. */
public enum Textures {
    BACKGROUND("/assets/icarus/bg.png"),
    WATERMARK("/assets/icarus/watermark.png");

    private final String path;
    private Texture texture;

    Textures(String path) {
        this.path = path;
    }

    /** Must be called on the render thread. */
    public Texture get() {
        if (texture == null) {
            BufferedImage image = read(path);
            int[] argb = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
            texture = Icarus.getPlatform().getRenderer().createTexture(image.getWidth(), image.getHeight(), argb, false);
        }
        return texture;
    }

    private static BufferedImage read(String path) {
        try (InputStream in = Textures.class.getResourceAsStream(path)) {
            if (in == null) throw new IllegalStateException("Missing Icarus resource " + path);
            return ImageIO.read(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read Icarus resource " + path, e);
        }
    }
}
