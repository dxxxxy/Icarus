package studio.dreamys.icarus.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import studio.dreamys.icarus.input.Keys;
import studio.dreamys.icarus.settings.Keybinds;
import studio.dreamys.icarus.settings.ModelBuilder;
import studio.dreamys.icarus.settings.Setting;
import studio.dreamys.icarus.settings.SettingTypes;
import studio.dreamys.icarus.settings.SettingsModel;
import studio.dreamys.icarus.support.Fixture;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigStoreTest {
    @TempDir
    Path game;

    private SettingsModel model;
    private Keybinds keybinds;

    @BeforeEach
    void reset() {
        Fixture.reset();
        build();
    }

    private void build() {
        List<Class<?>> pages = Arrays.asList(Fixture.Combat.class, Fixture.Visuals.class);
        model = new ModelBuilder(SettingTypes.builtIn()).build(pages);
        keybinds = new Keybinds(model);
    }

    private ConfigStore store(boolean profiles) {
        ConfigStore store = new ConfigStore(game.resolve("config/test"), game.resolve("test"), model, keybinds, profiles);
        for (Setting<?> s : model.getSettings()) s.setListener((setting, byUser) -> {
            if (byUser && setting.isPersistent()) store.markDirty();
        });
        keybinds.setListener((setting, byUser) -> {
            if (byUser) store.markDirty();
        });
        store.init();
        return store;
    }

    private Setting<?> setting(Class<?> group, String field) {
        return model.find(group, field);
    }

    @Test
    void keybindSurvivesRestart() {
        ConfigStore store = store(false);
        Setting<?> enabled = setting(Fixture.Combat.Kill_Aura.class, "Enabled");
        keybinds.set(enabled, Keys.KEY_A);
        store.interactionEnded();

        Fixture.reset();
        build();
        store(false);
        assertEquals(Keys.KEY_A, keybinds.get(model.find(Fixture.Combat.Kill_Aura.class, "Enabled")));
    }

    @Test
    void valuesSurviveRestartAndInitWritesNothing() {
        ConfigStore store = store(false);
        assertEquals(0, store.getWriteCount(), "init must not write");
        @SuppressWarnings("unchecked") Setting<Double> range = (Setting<Double>) setting(Fixture.Combat.Kill_Aura.class, "Range");
        range.set(75.0);
        store.interactionEnded();
        assertEquals(1, store.getWriteCount());

        Fixture.reset();
        build();
        store(false);
        assertEquals(75, Fixture.Combat.Kill_Aura.Range);
    }

    @Test
    void migratesV2Files() throws Exception {
        Path legacy = Files.createDirectories(game.resolve("test"));
        Files.write(legacy.resolve("config.json"), ("{\"Combat\":{\"Kill_Aura\":{\"Enabled\":false,\"Mode\":\"Option 3\","
                + "\"Targets\":[\"Legs\",\"Head\"],\"Name\":\"migrated\",\"Range\":20.0}}}").getBytes(StandardCharsets.UTF_8));
        //v2 keyed keybinds by display labels (spaces instead of underscores)
        Files.write(legacy.resolve("keybind.json"), "{\"Combat\":{\"Kill Aura\":{\"Enabled\":30}}}".getBytes(StandardCharsets.UTF_8));

        store(false);
        assertFalse(Fixture.Combat.Kill_Aura.Enabled);
        assertEquals("Option 3", Fixture.Combat.Kill_Aura.Mode);
        assertArrayEquals(new String[]{"Head", "Legs"}, Fixture.Combat.Kill_Aura.Targets);
        assertEquals("migrated", Fixture.Combat.Kill_Aura.Name);
        assertEquals(20, Fixture.Combat.Kill_Aura.Range);
        assertEquals(30, keybinds.get(setting(Fixture.Combat.Kill_Aura.class, "Enabled")));
        assertTrue(Files.exists(legacy.resolve("config.json.migrated")));
        assertTrue(Files.exists(legacy.resolve("keybind.json.migrated")));
        assertTrue(Files.exists(game.resolve("config/test/configs/default.json")));
    }

    @Test
    void corruptProfileIsKeptAside() throws Exception {
        Path profile = game.resolve("config/test/configs/default.json");
        Files.createDirectories(profile.getParent());
        Files.write(profile, "{ not json".getBytes(StandardCharsets.UTF_8));

        ConfigStore store = store(false);
        assertFalse(Files.exists(profile));
        assertEquals(1, Files.list(profile.getParent()).filter(p -> p.getFileName().toString().startsWith("default.json.broken-")).count());
        assertTrue(Fixture.Combat.Kill_Aura.Enabled, "defaults stay");
        assertEquals(0, store.getWriteCount());
    }

    @Test
    void emptyFileIsQuiet() throws Exception {
        Path profile = game.resolve("config/test/configs/default.json");
        Files.createDirectories(profile.getParent());
        Files.write(profile, new byte[0]);
        store(false);
        assertTrue(Files.exists(profile), "an empty file isn't treated as broken");
    }

    @Test
    void profileModeWritesOnlyOnSave() {
        ConfigStore store = store(true);
        @SuppressWarnings("unchecked") Setting<Boolean> enabled = (Setting<Boolean>) setting(Fixture.Combat.Kill_Aura.class, "Enabled");
        enabled.set(false);
        store.interactionEnded();
        store.tick(false);
        assertEquals(0, store.getWriteCount());
        assertTrue(store.hasUnsavedChanges());

        store.save();
        assertFalse(store.hasUnsavedChanges());
        assertTrue(Files.exists(game.resolve("config/test/configs/default.json")));
    }

    @Test
    void profilesLoadDeleteResetImportExport() {
        ConfigStore store = store(true);
        @SuppressWarnings("unchecked") Setting<String> name = (Setting<String>) setting(Fixture.Combat.Kill_Aura.class, "Name");
        name.set("first");
        assertTrue(store.saveAs("one"));
        name.set("second");
        assertTrue(store.saveAs("two"));
        assertEquals(Arrays.asList("one", "two"), store.listProfiles());

        assertTrue(store.load("one"));
        assertEquals("first", Fixture.Combat.Kill_Aura.Name);
        assertEquals("one", store.getProfile());

        String exported = store.exportCurrent();
        store.resetToDefaults();
        assertEquals("Hello", Fixture.Combat.Kill_Aura.Name);
        assertTrue(store.importJson(exported));
        assertEquals("first", Fixture.Combat.Kill_Aura.Name);
        assertFalse(store.importJson("not a config"));

        assertTrue(store.delete("two"));
        assertEquals(Arrays.asList("one"), store.listProfiles());
        assertFalse(store.saveAs("../escape"));
    }

    @Test
    void autoSaveWaitsForInteractionEnd() {
        ConfigStore store = store(false);
        @SuppressWarnings("unchecked") Setting<Double> smooth = (Setting<Double>) setting(Fixture.Combat.Second.class, "Smooth");
        for (int frame = 0; frame < 100; frame++) {
            smooth.set(frame / 100.0);
            store.tick(true); //mid-drag
        }
        assertEquals(0, store.getWriteCount());
        store.interactionEnded();
        assertEquals(1, store.getWriteCount());
        store.tick(false);
        assertEquals(1, store.getWriteCount(), "nothing left to write");
    }
}
