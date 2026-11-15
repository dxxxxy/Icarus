package studio.dreamys.icarus;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import studio.dreamys.icarus.input.Keys;
import studio.dreamys.icarus.settings.KeybindMode;
import studio.dreamys.icarus.settings.Setting;
import studio.dreamys.icarus.support.FakePlatform;
import studio.dreamys.icarus.support.Fixture;
import studio.dreamys.icarus.theme.Themes;

import java.nio.file.Path;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KeybindModeTest {
    @TempDir
    Path game;

    private IcarusMod mod;
    private Setting<?> enabled;

    @BeforeEach
    void setUp() {
        Fixture.reset();
        Icarus.setPlatform(new FakePlatform(game.toFile()));
        mod = Icarus.createDetached("test", Themes.SKEET, IcarusOptions.builder().configUi(false),
                Collections.<Class<?>>singletonList(Fixture.Combat.class), game.resolve("config"), game.resolve("legacy"));
        for (Setting<?> s : mod.getModel().getSettings()) if (s.getName().equals("Enabled")) enabled = s;
    }

    @AfterEach
    void tearDown() {
        Icarus.setPlatform(null);
    }

    @Test
    void toggleFlipsOnEachPress() {
        assertEquals(KeybindMode.TOGGLE, mod.getKeybinds().getMode(enabled));
        mod.onKeyPressed(Keys.KEY_7);
        assertFalse(Fixture.Combat.Kill_Aura.Enabled);
        mod.onKeyReleased(Keys.KEY_7);
        assertFalse(Fixture.Combat.Kill_Aura.Enabled);
        mod.onKeyPressed(Keys.KEY_7);
        assertTrue(Fixture.Combat.Kill_Aura.Enabled);
    }

    @Test
    void onHotkeyFollowsTheKey() {
        mod.getKeybinds().setMode(enabled, KeybindMode.ON_HOTKEY);
        assertFalse(Fixture.Combat.Kill_Aura.Enabled, "picking the mode puts it at rest (off)");
        mod.onKeyPressed(Keys.KEY_7);
        assertTrue(Fixture.Combat.Kill_Aura.Enabled);
        mod.onKeyReleased(Keys.KEY_7);
        assertFalse(Fixture.Combat.Kill_Aura.Enabled);
    }

    @Test
    void offHotkeyAndAlwaysOn() {
        mod.getKeybinds().setMode(enabled, KeybindMode.OFF_HOTKEY);
        assertTrue(Fixture.Combat.Kill_Aura.Enabled);
        mod.onKeyPressed(Keys.KEY_7);
        assertFalse(Fixture.Combat.Kill_Aura.Enabled);
        mod.onKeyReleased(Keys.KEY_7);
        assertTrue(Fixture.Combat.Kill_Aura.Enabled);

        mod.getKeybinds().setMode(enabled, KeybindMode.ALWAYS_ON);
        mod.onKeyPressed(Keys.KEY_7);
        mod.onKeyReleased(Keys.KEY_7);
        assertTrue(Fixture.Combat.Kill_Aura.Enabled);
    }

    @Test
    void mouseButtonsAreKeys() {
        mod.getKeybinds().set(enabled, Keys.MOUSE_5);
        mod.getKeybinds().setMode(enabled, KeybindMode.ON_HOTKEY);
        mod.onKeyPressed(Keys.mouse(4));
        assertTrue(Fixture.Combat.Kill_Aura.Enabled);
        assertEquals("M5", Keys.getKeyName(Keys.MOUSE_5));
    }
}
