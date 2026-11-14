package studio.dreamys.icarus.theme;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

/** Picks installed font families and registers bundled ones. */
public final class FontFamilies {
    private static final Logger LOG = Logger.getLogger("Icarus");
    private static Set<String> installed;

    private FontFamilies() {
    }

    private static synchronized Set<String> installed() {
        if (installed == null) {
            installed = new HashSet<>();
            for (String family : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(Locale.ROOT)) {
                installed.add(family.toLowerCase(Locale.ROOT));
            }
        }
        return installed;
    }

    /** The first installed family of {@code candidates}, or Java's logical "SansSerif" (with a warning). */
    public static String first(String... candidates) {
        for (String candidate : candidates) {
            if (installed().contains(candidate.toLowerCase(Locale.ROOT))) return candidate;
        }
        LOG.warning("[Icarus] None of the fonts " + Arrays.toString(candidates) + " is installed; using SansSerif");
        return Font.SANS_SERIF;
    }

    /** Registers a TrueType font shipped as a resource; returns its family name (or SansSerif if it fails). */
    public static synchronized String register(Class<?> owner, String resource) {
        try (InputStream in = owner.getResourceAsStream(resource)) {
            if (in == null) throw new IOException("missing resource " + resource);
            Font font = Font.createFont(Font.TRUETYPE_FONT, in);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
            if (installed != null) installed.add(font.getFamily().toLowerCase(Locale.ROOT));
            return font.getFamily();
        } catch (IOException | FontFormatException e) {
            LOG.warning("[Icarus] Couldn't load font " + resource + ": " + e);
            return Font.SANS_SERIF;
        }
    }
}
