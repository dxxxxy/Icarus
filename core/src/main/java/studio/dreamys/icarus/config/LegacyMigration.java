package studio.dreamys.icarus.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import studio.dreamys.icarus.settings.GroupNode;
import studio.dreamys.icarus.settings.Keybinds;
import studio.dreamys.icarus.settings.Setting;
import studio.dreamys.icarus.settings.SettingsModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reads Icarus v2's {@code <game>/<modid>/config.json} and {@code keybind.json}.
 * <p>
 * v2 keyed config.json by class/field names ({@code {"Combat": {"KillAura": {"Checkbox_Haha": true}}}}), but
 * keybind.json by display labels for the page and group (underscores shown as spaces), so both spellings are tried.
 */
final class LegacyMigration {
    private static final Logger LOG = Logger.getLogger("Icarus");

    private LegacyMigration() {
    }

    /** Applies the v2 files, if any, and renames them to {@code *.migrated}. Returns whether anything was found. */
    static boolean migrate(Path legacyDir, SettingsModel model, Keybinds keybinds) {
        Path configFile = legacyDir.resolve("config.json");
        Path keybindFile = legacyDir.resolve("keybind.json");
        JsonObject config = JsonFiles.read(configFile);
        JsonObject keys = JsonFiles.read(keybindFile);
        if (config == null && keys == null) return false;

        for (Setting<?> setting : model.getSettings()) {
            if (config != null && setting.isPersistent()) {
                JsonObject group = find(config, setting.getGroup());
                if (group != null) ProfileCodec.applyValue(setting, group.get(setting.getName()));
            }
            if (keys != null && setting.isBindable()) {
                JsonObject group = find(keys, setting.getGroup());
                if (group != null) {
                    JsonElement key = group.get(setting.getName());
                    if (key != null && key.isJsonPrimitive() && key.getAsJsonPrimitive().isNumber()) keybinds.load(setting, key.getAsInt());
                }
            }
        }

        markMigrated(configFile);
        markMigrated(keybindFile);
        LOG.info("[Icarus] Migrated v2 settings from " + legacyDir);
        return true;
    }

    private static JsonObject find(JsonObject root, GroupNode group) {
        if (group.getSubPage() == null) return null;
        String page = group.getSubPage().getPage().getName();
        JsonObject pageObject = either(root, page);
        return pageObject == null ? null : either(pageObject, group.getName());
    }

    private static JsonObject either(JsonObject parent, String name) {
        JsonObject found = ProfileCodec.object(parent, name);
        return found != null ? found : ProfileCodec.object(parent, name.replace('_', ' '));
    }

    private static void markMigrated(Path file) {
        if (!Files.isRegularFile(file)) return;
        try {
            Files.move(file, file.resolveSibling(file.getFileName() + ".migrated"));
        } catch (IOException e) {
            LOG.log(Level.WARNING, "[Icarus] Couldn't rename migrated " + file, e);
        }
    }
}
