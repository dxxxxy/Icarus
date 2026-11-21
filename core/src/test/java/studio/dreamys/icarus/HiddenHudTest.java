package studio.dreamys.icarus;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import studio.dreamys.icarus.hud.HudContext;
import studio.dreamys.icarus.hud.HudElement;
import studio.dreamys.icarus.hud.Watermark;
import studio.dreamys.icarus.render.Canvas;
import studio.dreamys.icarus.support.FakePlatform;
import studio.dreamys.icarus.support.FakePlatform.FakeShield.FakeLayer;
import studio.dreamys.icarus.support.Fixture;
import studio.dreamys.icarus.theme.Themes;
import studio.dreamys.icarus.ui.Menu;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Everything a mod draws on the HUD follows its "hide from recorders" setting. */
class HiddenHudTest {
    @TempDir
    Path game;

    private FakePlatform platform;
    private FakePlatform.FakeShield shield;
    private final List<IcarusMod> registered = new ArrayList<>();

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
        for (IcarusMod mod : registered) Icarus.forget(mod);
        Icarus.setPlatform(null);
    }

    private IcarusMod mod(String id) {
        IcarusMod mod = Icarus.createDetached(id, Themes.SKEET, IcarusOptions.defaults(), Collections.singletonList(Fixture.Combat.class),
                game.resolve("config/" + id), game.resolve(id));
        Icarus.register(mod);
        registered.add(mod);
        return mod;
    }

    /** A box another mod draws itself, e.g. an ESP indicator. */
    private static final class Box implements HudElement {
        @Override
        public void render(Canvas canvas, HudContext hud) {
            canvas.rect(100, 100, 50, 30, 0xFFFF0000);
        }
    }

    @Test
    void watermarkAndOwnElementsGoIntoAHiddenLayer() {
        IcarusMod mod = mod("hud");
        mod.addHud(new Watermark("icarus"));
        mod.addHud(new Box());
        mod.setHiddenFromRecorders(true);
        Icarus.renderHud(0);

        assertEquals(0, platform.renderer.renders, "nothing drawn in game");
        List<FakeLayer> visible = shield.visible();
        assertEquals(1, visible.size());
        FakeLayer layer = visible.get(0);
        //the layer spans the box (top-left) to the watermark (top-right)
        assertTrue(layer.x <= 100 && layer.x + layer.width > platform.width / 2, layer.x + " + " + layer.width);
        assertTrue(layer.opaquePixels() >= 50 * 30, "box and watermark drawn");

        //not hidden: drawn in game, layers hidden
        mod.setHiddenFromRecorders(false);
        Icarus.renderHud(0);
        assertEquals(1, platform.renderer.renders);
        assertEquals(0, shield.visible().size());
    }

    @Test
    void toastsStayHiddenAfterTheMenuCloses() {
        IcarusMod mod = mod("toasts");
        mod.setHiddenFromRecorders(true);
        Menu menu = mod.getMenu();
        menu.opened();
        menu.skipFade();
        mod.toasts().send("Title", "Message");
        for (int i = 0; i < 20; i++) mod.toasts().tick();
        Icarus.renderHud(0);
        menu.render(0, 0, 0);
        menu.closed();

        Icarus.renderHud(0);
        assertEquals(0, platform.renderer.renders, "the toast isn't drawn in game");
        List<FakeLayer> visible = shield.visible();
        assertEquals(1, visible.size(), "only the HUD layer (with the toast) is left");
        assertTrue(visible.get(0).opaquePixels() > 1000);
    }

    @Test
    void anOpenMenuShowsTheHudLayerUnderIt() {
        IcarusMod mod = mod("stack");
        mod.addHud(new Box());
        mod.setHiddenFromRecorders(true);
        Menu menu = mod.getMenu();
        menu.opened();
        menu.skipFade();

        Icarus.renderHud(0); //the HUD is drawn first; the menu shows both after it draws
        assertEquals(0, shield.visible().size(), "nothing shown before the screen is drawn");
        menu.render(0, 0, 0);
        List<FakeLayer> visible = shield.visible();
        assertEquals(2, visible.size());
        assertEquals(50 * 30, visible.get(0).opaquePixels(), "the HUD layer is at the bottom");
        assertTrue(visible.get(1).opaquePixels() > 100_000, "the menu is on top");
        assertSame(visible.get(1), shield.stack.get(shield.stack.size() - 1));
    }

    @Test
    void eachModFollowsItsOwnSetting() {
        IcarusMod hidden = mod("a"), shown = mod("b");
        hidden.addHud(new Box());
        shown.addHud(new Box());
        hidden.setHiddenFromRecorders(true);
        Icarus.renderHud(0);
        assertEquals(1, platform.renderer.renders, "b's HUD is drawn in game");
        assertEquals(1, shield.visible().size(), "a's HUD is hidden");
    }
}
