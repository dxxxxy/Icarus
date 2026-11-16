package studio.dreamys.icarus.render;

import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.platform.Platform;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Loads PNG images (icons) for the atlas. */
public final class Images {
    private static final Logger LOG = Logger.getLogger("Icarus");

    private Images() {
    }

    /** Decodes a PNG (or any format ImageIO reads); null if it can't. Doesn't close {@code in}. */
    public static AtlasImage read(InputStream in) throws IOException {
        BufferedImage img = ImageIO.read(in);
        if (img == null) return null;
        int[] argb = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
        return new AtlasImage(argb, img.getWidth(), img.getHeight());
    }

    /** A resource next to {@code owner} (an absolute path like {@code "/assets/icarus/skeet/rage.png"}), or null. */
    public static AtlasImage resource(Class<?> owner, String path) {
        try (InputStream in = owner.getResourceAsStream(path)) {
            return in == null ? null : read(in);
        } catch (IOException e) {
            LOG.log(Level.WARNING, "[Icarus] Couldn't read " + path, e);
            return null;
        }
    }

    /**
     * A PNG from a mod's own resources, e.g. {@code "/assets/mymod/icons/radar.png"}; null (with a warning) if it isn't
     * there.
     * <p>
     * Looked up next to {@code owner} (a class of that mod, usually the annotated page) first, which reaches the mod's
     * jar whether Icarus is shaded or a separate Jar-in-Jar mod (Fabric's shared class loader, and NeoForge's module
     * layer, where a resource outside the mod's packages is readable by any module). Then in the mod's own jars and
     * directories as the loader reports them ({@link Platform#getModRoots}), the way the pages themselves are found.
     */
    public static AtlasImage fromMod(String modid, Class<?> owner, String path) {
        if (owner != null) {
            AtlasImage image = resource(owner, path);
            if (image != null) return image;
        }
        for (Path root : modRoots(modid)) {
            try {
                if (Files.isDirectory(root)) {
                    AtlasImage image = file(root.resolve(path.substring(1)));
                    if (image != null) return image;
                } else if (Files.isRegularFile(root)) {
                    try (FileSystem fs = FileSystems.newFileSystem(root, (ClassLoader) null)) {
                        AtlasImage image = file(fs.getPath(path));
                        if (image != null) return image;
                    }
                }
            } catch (IOException | RuntimeException e) {
                LOG.log(Level.WARNING, "[Icarus] Couldn't read " + path + " from " + root, e);
            }
        }
        LOG.warning("[Icarus] Icon " + path + " not found in mod " + modid);
        return null;
    }

    private static AtlasImage file(Path file) throws IOException {
        if (!Files.isRegularFile(file)) return null;
        try (InputStream in = Files.newInputStream(file)) {
            return read(in);
        }
    }

    private static Collection<Path> modRoots(String modid) {
        Platform platform = Icarus.getPlatform();
        if (platform == null || modid == null) return Collections.emptyList();
        try {
            return platform.getModRoots(modid);
        } catch (RuntimeException e) { //not a loaded mod (detached mods, tests)
            return Collections.emptyList();
        }
    }
}
