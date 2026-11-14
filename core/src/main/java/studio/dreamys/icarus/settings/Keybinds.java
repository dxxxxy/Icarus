package studio.dreamys.icarus.settings;

import studio.dreamys.icarus.input.Keys;

import java.util.IdentityHashMap;
import java.util.Map;

/** Current key bound to each bindable setting ({@link Keys} codes). */
public final class Keybinds {
    /** Notified when a binding changes. */
    public interface Listener {
        void keybindChanged(Setting<?> setting, boolean byUser);
    }

    private final Map<Setting<?>, Integer> keys = new IdentityHashMap<>();
    private Listener listener;

    public Keybinds(SettingsModel model) {
        for (Setting<?> setting : model.getSettings()) {
            if (setting.isBindable()) keys.put(setting, setting.getDefaultKey());
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

    /** A user change (e.g. rebinding in the menu). */
    public void set(Setting<?> setting, int key) {
        put(setting, key, true);
    }

    /** A binding applied from a config file. */
    public void load(Setting<?> setting, int key) {
        put(setting, key, false);
    }

    private void put(Setting<?> setting, int key, boolean byUser) {
        if (!keys.containsKey(setting)) throw new IllegalArgumentException(setting + " is not bindable");
        if (keys.get(setting) == key) return;
        keys.put(setting, key);
        if (listener != null) listener.keybindChanged(setting, byUser);
    }

    public void resetToDefaults() {
        for (Setting<?> setting : keys.keySet()) load(setting, setting.getDefaultKey());
    }
}
