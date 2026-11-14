package studio.dreamys.icarus.config;

import com.google.gson.JsonObject;
import studio.dreamys.icarus.settings.Keybinds;
import studio.dreamys.icarus.settings.Setting;
import studio.dreamys.icarus.settings.SettingsModel;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Persists one mod's settings under {@code config/<modid>/}:
 * <ul>
 *     <li>{@code icarus.json}: UI preferences, always auto-saved</li>
 *     <li>{@code configs/<name>.json}: config profiles (settings + keybinds)</li>
 * </ul>
 * In <b>profile mode</b> (the built-in config UI is on) changes stay in memory until {@link #save()}.
 * Otherwise there's one profile ("default") that auto-saves: changes are written when an interaction ends
 * ({@link #interactionEnded()}) or on the next {@link #tick(boolean)} without an interaction in progress.
 */
public final class ConfigStore {
    private static final Logger LOG = Logger.getLogger("Icarus");
    public static final String DEFAULT_PROFILE = "default";
    private static final Pattern PROFILE_NAME = Pattern.compile("[\\p{L}\\p{N} _.\\-]{1,32}");

    private final Path dir;
    private final Path profilesDir;
    private final Path prefsFile;
    private final Path legacyDir;
    private final SettingsModel model;
    private final Keybinds keybinds;
    private final boolean profileMode;
    private final UiPrefs prefs = new UiPrefs();

    private String profile = DEFAULT_PROFILE;
    private boolean dirty;
    private boolean prefsDirty;
    private int writeCount;

    /**
     * @param dir       {@code <game>/config/<modid>}
     * @param legacyDir v2's {@code <game>/<modid>}, migrated on first start
     */
    public ConfigStore(Path dir, Path legacyDir, SettingsModel model, Keybinds keybinds, boolean profileMode) {
        this.dir = dir;
        this.profilesDir = dir.resolve("configs");
        this.prefsFile = dir.resolve("icarus.json");
        this.legacyDir = legacyDir;
        this.model = model;
        this.keybinds = keybinds;
        this.profileMode = profileMode;
    }

    /** Loads preferences and the active profile. Nothing is written unless v2 files were migrated. */
    public void init() {
        boolean fresh = !Files.exists(prefsFile) && listProfiles().isEmpty();

        JsonObject prefsJson = JsonFiles.read(prefsFile);
        if (prefsJson != null) prefs.read(prefsJson);

        if (fresh && legacyDir != null && LegacyMigration.migrate(legacyDir, model, keybinds)) {
            write(profileFile(DEFAULT_PROFILE), ProfileCodec.encode(model, keybinds));
        }

        String wanted = profileMode && prefs.getProfile() != null && isValidName(prefs.getProfile()) ? prefs.getProfile() : DEFAULT_PROFILE;
        profile = wanted;
        JsonObject json = JsonFiles.read(profileFile(profile));
        if (json != null) ProfileCodec.apply(json, model, keybinds);
        dirty = false;
    }

    public boolean isProfileMode() {
        return profileMode;
    }

    public UiPrefs getPrefs() {
        return prefs;
    }

    /** A setting or keybind was changed by the user. */
    public void markDirty() {
        dirty = true;
    }

    /** Whether there are changes not written to the active profile (shown by themes in profile mode). */
    public boolean hasUnsavedChanges() {
        return dirty;
    }

    /** Auto-save mode: writes pending changes now (end of a drag, field unfocus, toggle, menu close). */
    public void interactionEnded() {
        if (!profileMode && dirty) save();
    }

    /** Called once per client tick: writes pending auto-save changes (unless the user is mid-interaction) and preferences. */
    public void tick(boolean interacting) {
        if (!interacting) interactionEnded();
        if (prefsDirty) {
            prefsDirty = false;
            write(prefsFile, prefs.toJson());
        }
    }

    /** Menu closed: flush everything that's due. */
    public void closed() {
        tick(false);
    }

    /* ---- UI preferences ---- */

    public void setTheme(String theme) {
        if (theme.equals(prefs.getTheme())) return;
        prefs.setTheme(theme);
        prefsDirty = true;
    }

    public void setPosition(double x, double y) {
        if (x == prefs.getX() && y == prefs.getY()) return;
        prefs.setPosition(x, y);
        prefsDirty = true;
    }

    public void setPage(String page, String subPage) {
        prefs.setPage(page, subPage);
        prefsDirty = true;
    }

    /* ---- profiles ---- */

    /** The active profile's name. */
    public String getProfile() {
        return profile;
    }

    public static boolean isValidName(String name) {
        return name != null && PROFILE_NAME.matcher(name).matches() && !name.trim().isEmpty() && !name.equals(".") && !name.equals("..");
    }

    /** Saved profiles, sorted by name. */
    public List<String> listProfiles() {
        List<String> names = new ArrayList<>();
        if (!Files.isDirectory(profilesDir)) return names;
        try (DirectoryStream<Path> files = Files.newDirectoryStream(profilesDir, "*.json")) {
            for (Path file : files) {
                String fileName = file.getFileName().toString();
                names.add(fileName.substring(0, fileName.length() - ".json".length()));
            }
        } catch (IOException e) {
            LOG.log(Level.WARNING, "[Icarus] Couldn't list " + profilesDir, e);
        }
        Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
        return names;
    }

    /** Writes the current settings to the active profile. */
    public void save() {
        if (write(profileFile(profile), ProfileCodec.encode(model, keybinds))) dirty = false;
    }

    /** Writes the current settings to {@code name} and makes it the active profile. */
    public boolean saveAs(String name) {
        if (!isValidName(name)) return false;
        profile = name;
        rememberProfile();
        save();
        return true;
    }

    /** Switches to a saved profile, discarding unsaved changes. Settings it doesn't mention keep their defaults. */
    public boolean load(String name) {
        if (!isValidName(name)) return false;
        JsonObject json = JsonFiles.read(profileFile(name));
        if (json == null) return false;
        resetValues();
        ProfileCodec.apply(json, model, keybinds);
        profile = name;
        rememberProfile();
        dirty = false;
        return true;
    }

    /** Deletes a saved profile. Deleting the active one keeps the current values as unsaved changes. */
    public boolean delete(String name) {
        if (!isValidName(name)) return false;
        try {
            boolean deleted = Files.deleteIfExists(profileFile(name));
            if (deleted && name.equals(profile)) dirty = true;
            return deleted;
        } catch (IOException e) {
            LOG.log(Level.WARNING, "[Icarus] Couldn't delete profile " + name, e);
            return false;
        }
    }

    /** Resets every setting and keybind to its default (a pending change until saved). */
    public void resetToDefaults() {
        resetValues();
        dirty = true;
        interactionEnded();
    }

    /** The current settings as JSON text (for the clipboard). */
    public String exportCurrent() {
        return JsonFiles.toString(ProfileCodec.encode(model, keybinds));
    }

    /** Applies settings from JSON text (from the clipboard). Returns false if it isn't a profile. */
    public boolean importJson(String text) {
        JsonObject json = text == null ? null : JsonFiles.parse(text);
        if (json == null || (json.get("settings") == null && json.get("keybinds") == null)) return false;
        ProfileCodec.apply(json, model, keybinds);
        dirty = true;
        interactionEnded();
        return true;
    }

    private void resetValues() {
        for (Setting<?> setting : model.getSettings()) {
            if (setting.isPersistent()) setting.resetToDefault();
        }
        keybinds.resetToDefaults();
    }

    private void rememberProfile() {
        if (profileMode) {
            prefs.setProfile(profile);
            prefsDirty = true;
        }
    }

    Path profileFile(String name) {
        return profilesDir.resolve(name + ".json");
    }

    /** Number of files written so far (for tests and diagnostics). */
    public int getWriteCount() {
        return writeCount;
    }

    public Path getDirectory() {
        return dir;
    }

    private boolean write(Path file, JsonObject json) {
        try {
            JsonFiles.writeAtomic(file, json);
            writeCount++;
            return true;
        } catch (IOException e) {
            LOG.log(Level.WARNING, "[Icarus] Couldn't write " + file, e);
            return false;
        }
    }
}
