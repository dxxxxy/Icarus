package studio.dreamys.icarus.settings;

import studio.dreamys.icarus.input.Keys;

import java.util.IdentityHashMap;
import java.util.Map;

/** Current key and {@link KeybindMode} of each bindable setting ({@link Keys} codes). */
public final class Keybinds {
    /** Notified when a binding changes. */
    public interface Listener {
        void keybindChanged(Setting<?> setting, boolean byUser);
    }

    private final Map<Setting<?>, Integer> keys = new IdentityHashMap<>();
    private final Map<Setting<?>, KeybindMode> modes = new IdentityHashMap<>();
    private Listener listener;

    public Keybinds(SettingsModel model) {
        for (Setting<?> setting : model.getSettings()) {
            if (!setting.isBindable()) continue;
            keys.put(setting, setting.getDefaultKey());
            modes.put(setting, setting.getDefaultMode());
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public Map<Setting<?>, Integer> asMap() {
        return keys;
    }

    /** The bound key, {@link Keys#KEY_NONE}, or -1 if the setting isn't bindable. */
    public int get(Setting<?> setting) {
        Integer key = keys.get(setting);
        return key == null ? -1 : key;
    }

    /** The binding's mode (meaningful for checkboxes), or null if the setting isn't bindable. */
    public KeybindMode getMode(Setting<?> setting) {
        return modes.get(setting);
    }

    /** A user change (e.g. rebinding in the menu). */
    public void set(Setting<?> setting, int key) {
        put(setting, key, true);
    }

    /** A user change of the mode. */
    public void setMode(Setting<?> setting, KeybindMode mode) {
        putMode(setting, mode, true);
    }

    /** A binding applied from a config file. */
    public void load(Setting<?> setting, int key) {
        put(setting, key, false);
    }

    /** A mode applied from a config file. */
    public void loadMode(Setting<?> setting, KeybindMode mode) {
        putMode(setting, mode, false);
    }

    private void put(Setting<?> setting, int key, boolean byUser) {
        if (!keys.containsKey(setting)) throw new IllegalArgumentException(setting + " is not bindable");
        if (keys.get(setting) == key) return;
        keys.put(setting, key);
        if (listener != null) listener.keybindChanged(setting, byUser);
    }

    private void putMode(Setting<?> setting, KeybindMode mode, boolean byUser) {
        if (!modes.containsKey(setting)) throw new IllegalArgumentException(setting + " is not bindable");
        if (mode == null || modes.get(setting) == mode) return;
        modes.put(setting, mode);
        if (listener != null) listener.keybindChanged(setting, byUser);
    }

    public void resetToDefaults() {
        for (Setting<?> setting : keys.keySet()) {
            load(setting, setting.getDefaultKey());
            loadMode(setting, setting.getDefaultMode());
        }
    }
}
