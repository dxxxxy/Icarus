package studio.dreamys.icarus;

import studio.dreamys.icarus.input.Keys;

import java.awt.Color;

/** Optional settings for {@link Icarus#create(String, studio.dreamys.icarus.theme.Theme, IcarusOptions)}. */
public final class IcarusOptions {
    boolean configUi = true;
    boolean themeSelector = true;
    int menuKey = Keys.KEY_RSHIFT;
    Integer accent;
    double menuScale = 1;

    public static IcarusOptions defaults() {
        return new IcarusOptions();
    }

    public static IcarusOptions builder() {
        return new IcarusOptions();
    }

    /**
     * The built-in config UI (named configs with load/save/delete/reset/import/export), on by default. While it's
     * on, changes are only written when the user presses Save. Turn it off for a single config that auto-saves.
     */
    public IcarusOptions configUi(boolean enabled) {
        configUi = enabled;
        return this;
    }

    /** Lets players switch the menu theme (on by default). */
    public IcarusOptions themeSelector(boolean enabled) {
        themeSelector = enabled;
        return this;
    }

    /** Key that opens the menu (a {@link Keys} code), Right Shift by default. */
    public IcarusOptions menuKey(int key) {
        menuKey = key;
        return this;
    }

    /** Accent colour; the theme's own accent is used if unset. */
    public IcarusOptions accent(Color color) {
        accent = color == null ? null : color.getRGB();
        return this;
    }

    /** Extra menu scale on top of Minecraft's GUI scale (1 = the theme's natural size). */
    public IcarusOptions menuScale(double scale) {
        if (!(scale > 0)) throw new IllegalArgumentException("menu scale must be positive");
        menuScale = scale;
        return this;
    }
}
