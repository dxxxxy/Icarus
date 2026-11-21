package studio.dreamys.icarus.render;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.IcarusMod;
import studio.dreamys.icarus.IcarusOptions;
import studio.dreamys.icarus.settings.Setting;
import studio.dreamys.icarus.support.FakePlatform;
import studio.dreamys.icarus.support.Fixture;
import studio.dreamys.icarus.theme.Themes;
import studio.dreamys.icarus.ui.ConfigPanel;
import studio.dreamys.icarus.ui.Menu;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecorderOverlayTest {
    @TempDir
    Path game;

    private FakePlatform platform;
    private FakePlatform.FakeShield shield;

    @BeforeEach
    void setUp() {
        Fixture.reset();
        platform = new FakePlatform(game.toFile());
        shield = new FakePlatform.FakeShield();
        platform.shield = shield;
        Icarus.setPlatform(platform);
    }

    @AfterEach
    void tearDown() {
        Icarus.setPlatform(null);
    }

    private IcarusMod mod(IcarusOptions options) {
        return Icarus.createDetached("test", Themes.SKEET, options, Collections.singletonList(Fixture.Combat.class),
                game.resolve("config/test"), game.resolve("test"));
    }

    private Menu open(IcarusMod mod) {
        Menu menu = mod.getMenu();
        menu.opened();
        menu.skipFade();
        return menu;
    }

    @Test
    void hiddenMenuIsDrawnIntoTheShieldNotTheGame() {
        IcarusMod mod = mod(IcarusOptions.defaults());
        mod.setHiddenFromRecorders(true);
        Menu menu = open(mod);
        for (int i = 0; i < 3; i++) menu.render(0, 0, 0);

        assertEquals(0, platform.renderer.renders, "nothing drawn in game");
        assertTrue(shield.visible);
        int opaque = 0;
        for (int p : shield.pixels) if (p >>> 24 == 255) opaque++;
        assertTrue(opaque > 100_000, "the menu's pixels reached the shield (" + opaque + ")");
        assertTrue(shield.width < platform.width && shield.height < platform.height, "only the menu's bounds are sent");

        //an unchanged frame isn't rasterised or uploaded again
        RecorderOverlay overlay = Icarus.getRecorderOverlay();
        int rasterised = overlay.rasterisations, uploads = shield.uploads;
        menu.render(0, 0, 0);
        assertEquals(rasterised, overlay.rasterisations);
        assertEquals(uploads, shield.uploads);

        //moving the window by whole pixels only moves the shield
        int x = shield.x, y = shield.y;
        menu.window.x += 10;
        menu.window.y += 5;
        menu.render(0, 0, 0);
        assertEquals(rasterised, overlay.rasterisations);
        assertEquals(uploads, shield.uploads);
        assertEquals(x + 10, shield.x);
        assertEquals(y + 5, shield.y);

        menu.closed();
        assertFalse(shield.visible);
    }

    @Test
    void turningItOffDrawsInGameAgain() {
        IcarusMod mod = mod(IcarusOptions.defaults());
        mod.setHiddenFromRecorders(true);
        Menu menu = open(mod);
        menu.render(0, 0, 0);
        assertTrue(shield.visible);

        mod.setHiddenFromRecorders(false);
        assertFalse(shield.visible);
        menu.render(0, 0, 0);
        assertEquals(1, platform.renderer.renders);
        assertFalse(shield.visible);
    }

    @Test
    void aFailingShieldFallsBackToTheGame() {
        IcarusMod mod = mod(IcarusOptions.defaults());
        mod.setHiddenFromRecorders(true);
        shield.accept = false;
        Menu menu = open(mod);
        menu.render(0, 0, 0);
        assertEquals(1, platform.renderer.renders, "the failed frame is still drawn");
        menu.render(0, 0, 0);
        assertEquals(2, platform.renderer.renders);
        assertEquals(1, shield.shows, "an unavailable shield isn't asked again");
    }

    @Test
    void staleShieldIsHiddenOnTick() throws Exception {
        IcarusMod mod = mod(IcarusOptions.defaults());
        mod.setHiddenFromRecorders(true);
        open(mod).render(0, 0, 0);
        Icarus.clientTick();
        assertTrue(shield.visible, "just presented");
        Thread.sleep(RecorderOverlay.STALE_NANOS / 1_000_000 + 20);
        Icarus.clientTick();
        assertFalse(shield.visible);
    }

    @Test
    void checkboxIsOfferedOnlyWhereHidingWorks() {
        IcarusMod mod = mod(IcarusOptions.defaults());
        assertTrue(names(new ConfigPanel(mod, true, true, true)).contains("Hide from recorders"));
        assertFalse(names(new ConfigPanel(mod, true, true, false)).contains("Hide from recorders"));

        @SuppressWarnings("unchecked")
        Setting<Boolean> toggle = (Setting<Boolean>) find(new ConfigPanel(mod, false, false, true), "Hide from recorders");
        assertFalse(toggle.get());
        toggle.set(true);
        assertTrue(mod.isHiddenFromRecorders());
    }

    @Test
    void preferenceIsSavedInIcarusJson() throws Exception {
        IcarusMod mod = mod(IcarusOptions.defaults());
        mod.setHiddenFromRecorders(true);
        mod.getConfigStore().tick(false);
        String json = new String(Files.readAllBytes(game.resolve("config/test/icarus.json")), StandardCharsets.UTF_8);
        assertTrue(json.replaceAll("\\s", "").contains("\"hideFromRecorders\":true"), json);
        assertTrue(mod(IcarusOptions.defaults()).isHiddenFromRecorders());
    }

    private static List<String> names(ConfigPanel panel) {
        List<String> names = new ArrayList<>();
        for (Setting<?> s : panel.group(true).getSettings()) names.add(s.getName());
        for (Setting<?> s : panel.group(false).getSettings()) names.add(s.getName());
        return names;
    }

    private static Setting<?> find(ConfigPanel panel, String name) {
        for (Setting<?> s : panel.group(true).getSettings()) if (s.getName().equals(name)) return s;
        throw new AssertionError("no " + name);
    }
}
