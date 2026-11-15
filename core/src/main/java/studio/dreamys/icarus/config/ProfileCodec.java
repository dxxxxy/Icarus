package studio.dreamys.icarus.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import studio.dreamys.icarus.settings.GroupNode;
import studio.dreamys.icarus.settings.KeybindMode;
import studio.dreamys.icarus.settings.Keybinds;
import studio.dreamys.icarus.settings.Setting;
import studio.dreamys.icarus.settings.SettingsModel;
import studio.dreamys.icarus.settings.SubPageNode;

/**
 * Converts settings and keybinds to and from a config profile:
 * {@code {"version": 1, "settings": {page: {[subPage:] {group: {field: value}}}}, "keybinds": {same path: key}}}.
 */
public final class ProfileCodec {
    public static final int VERSION = 1;

    private ProfileCodec() {
    }

    public static JsonObject encode(SettingsModel model, Keybinds keybinds) {
        JsonObject root = new JsonObject();
        root.addProperty("version", VERSION);
        JsonObject settings = new JsonObject();
        JsonObject keys = new JsonObject();
        for (Setting<?> setting : model.getSettings()) {
            if (setting.isPersistent()) groupObject(settings, setting.getGroup()).add(setting.getName(), toJson(setting));
            int key = keybinds.get(setting);
            if (key < 0) continue;
            KeybindMode mode = keybinds.getMode(setting);
            if (mode == null || mode == setting.getDefaultMode()) {
                groupObject(keys, setting.getGroup()).addProperty(setting.getName(), key);
            } else {
                //{"key": 7, "mode": "on_hotkey"}; a plain number (older configs) means the default mode
                JsonObject binding = new JsonObject();
                binding.addProperty("key", key);
                binding.addProperty("mode", mode.name().toLowerCase(java.util.Locale.ROOT));
                groupObject(keys, setting.getGroup()).add(setting.getName(), binding);
            }
        }
        root.add("settings", settings);
        root.add("keybinds", keys);
        return root;
    }

    private static <T> JsonElement toJson(Setting<T> setting) {
        return setting.getType().toJson(setting.get());
    }

    /** Applies a profile; values that are missing or invalid keep their current value. */
    public static void apply(JsonObject root, SettingsModel model, Keybinds keybinds) {
        JsonObject settings = object(root, "settings");
        JsonObject keys = object(root, "keybinds");
        for (Setting<?> setting : model.getSettings()) {
            if (settings != null && setting.isPersistent()) {
                JsonObject group = findGroup(settings, setting.getGroup());
                if (group != null && group.has(setting.getName())) applyValue(setting, group.get(setting.getName()));
            }
            if (keys != null && setting.isBindable()) {
                JsonObject group = findGroup(keys, setting.getGroup());
                if (group != null) {
                    JsonElement key = group.get(setting.getName());
                    if (key != null && key.isJsonPrimitive() && key.getAsJsonPrimitive().isNumber()) {
                        keybinds.load(setting, key.getAsInt());
                        keybinds.loadMode(setting, setting.getDefaultMode());
                    } else if (key != null && key.isJsonObject()) {
                        JsonObject binding = key.getAsJsonObject();
                        JsonElement code = binding.get("key"), mode = binding.get("mode");
                        if (code != null && code.isJsonPrimitive() && code.getAsJsonPrimitive().isNumber()) keybinds.load(setting, code.getAsInt());
                        KeybindMode parsed = mode != null && mode.isJsonPrimitive() ? KeybindMode.byName(mode.getAsString()) : null;
                        keybinds.loadMode(setting, parsed != null ? parsed : setting.getDefaultMode());
                    }
                }
            }
        }
    }

    static <T> void applyValue(Setting<T> setting, JsonElement json) {
        if (json == null || json.isJsonNull()) return;
        T value = setting.getType().fromJson(json, setting);
        if (value != null) setting.load(value);
    }

    private static JsonObject groupObject(JsonObject root, GroupNode group) {
        SubPageNode sub = group.getSubPage();
        JsonObject parent = root;
        if (sub != null) {
            parent = child(parent, sub.getPage().getName());
            if (!sub.isImplicit()) parent = child(parent, sub.getName());
        }
        return child(parent, group.getName());
    }

    private static JsonObject findGroup(JsonObject root, GroupNode group) {
        SubPageNode sub = group.getSubPage();
        JsonObject parent = root;
        if (sub != null) {
            parent = object(parent, sub.getPage().getName());
            if (parent != null && !sub.isImplicit()) parent = object(parent, sub.getName());
        }
        return parent == null ? null : object(parent, group.getName());
    }

    private static JsonObject child(JsonObject parent, String name) {
        JsonObject child = object(parent, name);
        if (child == null) {
            child = new JsonObject();
            parent.add(name, child);
        }
        return child;
    }

    static JsonObject object(JsonObject parent, String name) {
        JsonElement element = parent.get(name);
        return element != null && element.isJsonObject() ? element.getAsJsonObject() : null;
    }

    /** Reads {@code "version"} (0 if absent). */
    static int version(JsonObject root) {
        JsonElement v = root.get("version");
        return v instanceof JsonPrimitive && ((JsonPrimitive) v).isNumber() ? v.getAsInt() : 0;
    }
}
