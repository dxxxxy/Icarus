package studio.dreamys.icarus.settings;

/** How a bound checkbox follows its key. */
public enum KeybindMode {
    /** Always on; the key is ignored. */
    ALWAYS_ON("Always on"),
    /** On while the key is held. */
    ON_HOTKEY("On hotkey"),
    /** Each press flips it. */
    TOGGLE("Toggle"),
    /** Off while the key is held. */
    OFF_HOTKEY("Off hotkey");

    private final String label;

    KeybindMode(String label) {
        this.label = label;
    }

    /** Name shown in the menu. */
    public String getLabel() {
        return label;
    }

    /** Lenient lookup for config files; null if unknown. */
    public static KeybindMode byName(String name) {
        for (KeybindMode mode : values()) if (mode.name().equalsIgnoreCase(name)) return mode;
        return null;
    }
}
