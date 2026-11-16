package studio.dreamys.icarus.ui;

import studio.dreamys.icarus.render.AtlasImage;
import studio.dreamys.icarus.render.Images;

/**
 * A tab's or sub-tab's icon, as written in {@code @IPage(icon)} or {@code @ISubPage(icon)}. It is one of:
 * <ul>
 *     <li>a PNG in the mod's own resources: an absolute path such as {@code "/assets/mymod/icons/radar.png"};</li>
 *     <li>the name of one of the theme's built-in icons, such as skeet's {@code "rage"};</li>
 *     <li>empty: no icon.</li>
 * </ul>
 * Themes turn it into an image with {@code BaseTheme.image(Icon)}.
 */
public final class Icon {
    static final Icon NONE = new Icon("", null, null);

    private final String name;
    private final String modid;
    private final Class<?> owner;
    private AtlasImage image;
    private boolean loaded;

    Icon(String name, String modid, Class<?> owner) {
        this.name = name == null ? "" : name;
        this.modid = modid;
        this.owner = owner;
    }

    /** The icon as written: a resource path, a built-in name, or empty. */
    public String getName() {
        return name;
    }

    public boolean isEmpty() {
        return name.isEmpty();
    }

    /** Whether this is a PNG in the mod's resources (the name starts with {@code /}) rather than a built-in name. */
    public boolean isResource() {
        return name.startsWith("/");
    }

    /** The mod's PNG, loaded on first use; null for a built-in name or a missing file. */
    public AtlasImage getImage() {
        if (!loaded) {
            loaded = true;
            if (isResource()) image = Images.fromMod(modid, owner, name);
        }
        return image;
    }
}
