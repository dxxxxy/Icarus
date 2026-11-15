package studio.dreamys.icarus.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.IcarusMod;
import studio.dreamys.icarus.IcarusOptions;
import studio.dreamys.icarus.input.Keys;
import studio.dreamys.icarus.render.Canvas;
import studio.dreamys.icarus.settings.Setting;
import studio.dreamys.icarus.support.FakePlatform;
import studio.dreamys.icarus.support.Fixture;
import studio.dreamys.icarus.theme.skeet.SkeetStyleTheme;
import studio.dreamys.icarus.settings.KeybindMode;
import studio.dreamys.icarus.ui.popup.ContextPopup;
import studio.dreamys.icarus.ui.popup.DropdownPopup;
import studio.dreamys.icarus.ui.widget.ColorWidget;
import studio.dreamys.icarus.ui.widget.CheckboxWidget;
import studio.dreamys.icarus.ui.widget.ChoiceWidget;
import studio.dreamys.icarus.ui.widget.FieldWidget;
import studio.dreamys.icarus.ui.widget.KeybindWidget;
import studio.dreamys.icarus.ui.widget.SliderWidget;
import studio.dreamys.icarus.ui.widget.Widget;

import java.lang.management.ManagementFactory;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MenuTest {
    @TempDir
    Path game;

    private FakePlatform platform;
    private final RecordingTheme theme = new RecordingTheme();

    @BeforeEach
    void setUp() {
        Fixture.reset();
        platform = new FakePlatform(game.toFile());
        Icarus.setPlatform(platform);
    }

    @AfterEach
    void tearDown() {
        Icarus.setPlatform(null);
    }

    private IcarusMod mod(IcarusOptions options, Class<?>... pages) {
        return Icarus.createDetached("test", theme, options, Arrays.asList(pages), game.resolve("config/test"), game.resolve("test"));
    }

    /** Menu coordinates are design units; at GUI scale 2 the screen passes half of them. */
    private static void frame(Menu menu, double x, double y) {
        menu.render(x / 2, y / 2, 0);
    }

    private static void click(Menu menu, double x, double y) {
        menu.mousePressed(x / 2, y / 2, 0);
        menu.mouseReleased(x / 2, y / 2, 0);
    }

    private static <T extends Widget> T widget(Menu menu, Class<T> type, String name) {
        for (GroupView g : menu.getCurrentSubTab().getGroups()) {
            for (Widget w : g.getWidgets()) {
                if (type.isInstance(w) && w.getSetting().getName().equals(name)) return type.cast(w);
            }
        }
        throw new AssertionError("no " + type.getSimpleName() + " " + name);
    }

    @Test
    void draggingMovesEverythingInTheSameFrame() {
        Menu menu = mod(IcarusOptions.defaults(), Fixture.Combat.class).getMenu();
        frame(menu, 0, 0);
        double wx = menu.window.x, wy = menu.window.y;
        List<double[]> before = snapshot(menu);

        menu.mousePressed((wx + 300) / 2, (wy + 5) / 2, 0);
        frame(menu, wx + 300 + 40, wy + 5 + 20);

        assertEquals(wx + 40, menu.window.x, 1e-9);
        assertEquals(wy + 20, menu.window.y, 1e-9);
        List<double[]> after = snapshot(menu);
        for (int i = 0; i < before.size(); i++) {
            assertEquals(before.get(i)[0] + 40, after.get(i)[0], 1e-9, "rect " + i + " x");
            assertEquals(before.get(i)[1] + 20, after.get(i)[1], 1e-9, "rect " + i + " y");
        }
    }

    private static List<double[]> snapshot(Menu menu) {
        List<double[]> rects = new ArrayList<>();
        for (GroupView g : menu.getCurrentSubTab().getGroups()) {
            rects.add(new double[]{g.frame.x, g.frame.y});
            for (Widget w : g.getWidgets()) {
                rects.add(new double[]{w.bounds.x, w.bounds.y});
                if (w.getKeybind() != null) rects.add(new double[]{w.getKeybind().bounds.x, w.getKeybind().bounds.y});
            }
        }
        for (TabView t : menu.getTabs()) rects.add(new double[]{t.bounds.x, t.bounds.y});
        return rects;
    }

    @Test
    void groupFramesUnderWidgetsAndPopupLast() {
        Menu menu = mod(IcarusOptions.defaults(), Fixture.Combat.class).getMenu();
        frame(menu, 0, 0);
        ChoiceWidget mode = widget(menu, ChoiceWidget.class, "Mode");
        click(menu, mode.box.centerX(), mode.box.centerY());
        theme.calls.clear();
        frame(menu, 0, 0);

        List<String> calls = theme.calls;
        assertEquals("window", calls.get(0));
        assertEquals("dropdown", calls.get(calls.size() - 1), "the open popup is painted last");
        int firstGroup = calls.indexOf("group");
        int firstWidget = calls.indexOf("widget");
        assertTrue(firstGroup >= 0 && firstGroup < firstWidget, "group frame before its widgets");
    }

    @Test
    void clickOnOpenDropdownOnlyHitsTheDropdown() {
        Menu menu = mod(IcarusOptions.defaults(), Fixture.Combat.class).getMenu();
        frame(menu, 0, 0);
        ChoiceWidget mode = widget(menu, ChoiceWidget.class, "Mode");
        click(menu, mode.box.centerX(), mode.box.centerY());
        frame(menu, 0, 0);
        DropdownPopup popup = (DropdownPopup) menu.getPopup();
        double itemY = popup.bounds.y + popup.itemHeight * 2.5;

        click(menu, popup.bounds.centerX(), itemY);
        assertEquals("Option 3", Fixture.Combat.Kill_Aura.Mode);
        assertNull(menu.getPopup());
        assertNull(menu.getFocused(), "nothing under the dropdown reacted");
        assertTrue(Fixture.Combat.Kill_Aura.Enabled);
    }

    @Test
    void clickOutsidePopupOnlyClosesIt() {
        Menu menu = mod(IcarusOptions.defaults(), Fixture.Combat.class).getMenu();
        frame(menu, 0, 0);
        ChoiceWidget mode = widget(menu, ChoiceWidget.class, "Mode");
        CheckboxWidget enabled = widget(menu, CheckboxWidget.class, "Enabled");
        click(menu, mode.box.centerX(), mode.box.centerY());
        frame(menu, 0, 0);
        click(menu, enabled.box.centerX(), enabled.box.centerY());
        assertNull(menu.getPopup());
        assertTrue(Fixture.Combat.Kill_Aura.Enabled, "the click closed the popup and didn't toggle");
    }

    @Test
    void checkboxLabelIsClickable() {
        Menu menu = mod(IcarusOptions.defaults(), Fixture.Combat.class).getMenu();
        frame(menu, 0, 0);
        CheckboxWidget enabled = widget(menu, CheckboxWidget.class, "Enabled");
        click(menu, enabled.box.right() + 30, enabled.box.centerY());
        assertFalse(Fixture.Combat.Kill_Aura.Enabled);
    }

    @Test
    void keybindCaptureEscapeCancelsAndBackspaceClears() {
        IcarusMod mod = mod(IcarusOptions.defaults(), Fixture.Combat.class);
        Menu menu = mod.getMenu();
        frame(menu, 0, 0);
        KeybindWidget keybind = widget(menu, CheckboxWidget.class, "Enabled").getKeybind();
        Setting<?> setting = keybind.getSetting();

        click(menu, keybind.label.centerX(), keybind.label.centerY());
        assertTrue(keybind.isCapturing());
        assertTrue(menu.keyPressed(Keys.KEY_ESCAPE), "escape is used to cancel, not to close the menu");
        assertFalse(keybind.isCapturing());
        assertEquals(Keys.KEY_7, mod.getKeybinds().get(setting));
        assertFalse(menu.keyPressed(Keys.KEY_ESCAPE), "now escape may close the menu");

        click(menu, keybind.label.centerX(), keybind.label.centerY());
        menu.keyPressed(mod.getMenuKey());
        assertTrue(keybind.isCapturing(), "the menu key is refused");
        menu.keyPressed(Keys.KEY_BACK);
        assertEquals(Keys.KEY_NONE, mod.getKeybinds().get(setting));

        click(menu, keybind.label.centerX(), keybind.label.centerY());
        menu.keyPressed(Keys.KEY_G);
        assertEquals(Keys.KEY_G, mod.getKeybinds().get(setting));
    }

    @Test
    void menuKeyClosesTheMenuUnlessTyping() {
        IcarusMod mod = mod(IcarusOptions.defaults(), Fixture.Combat.class);
        Menu menu = mod.getMenu();
        menu.opened();
        frame(menu, 0, 0);
        menu.keyPressed(mod.getMenuKey());
        assertFalse(menu.isClosing(), "ignored while fading in: the press that opened the menu can reach it too");

        menu.skipFade();
        FieldWidget name = widget(menu, FieldWidget.class, "Name");
        click(menu, name.box.centerX(), name.box.centerY());
        assertTrue(menu.isTyping());
        menu.keyPressed(mod.getMenuKey());
        assertFalse(menu.isClosing(), "typing keeps the menu open");

        menu.focus(null);
        assertTrue(menu.keyPressed(mod.getMenuKey()));
        assertTrue(menu.isClosing(), "the menu key toggles the menu");
    }

    @Test
    void rightClickKeybindPicksAMode() {
        IcarusMod mod = mod(IcarusOptions.defaults(), Fixture.Combat.class);
        Menu menu = mod.getMenu();
        frame(menu, 0, 0);
        KeybindWidget keybind = widget(menu, CheckboxWidget.class, "Enabled").getKeybind();
        menu.mousePressed(keybind.label.centerX() / 2, keybind.label.centerY() / 2, 1);
        assertTrue(menu.getPopup() instanceof ContextPopup, "right click opens the mode menu");
        ContextPopup modes = (ContextPopup) menu.getPopup();
        frame(menu, 0, 0);
        assertEquals(KeybindMode.TOGGLE.ordinal(), modes.getSelected());
        click(menu, modes.bounds.centerX(), modes.bounds.y + modes.itemHeight * 1.5);
        assertNull(menu.getPopup());
        assertEquals(KeybindMode.ON_HOTKEY, mod.getKeybinds().getMode(keybind.getSetting()));
    }

    @Test
    void rightClickLabelKeybindOpensModes() {
        Menu menu = mod(IcarusOptions.builder().configUi(false), Fixture.Tall.class).getMenu();
        frame(menu, 0, 0);
        KeybindWidget keybind = null;
        for (Widget w : menu.getCurrentSubTab().getGroups().get(0).getWidgets()) if (w.getKeybind() != null) keybind = w.getKeybind();
        menu.mousePressed(keybind.label.centerX() / 2, keybind.label.centerY() / 2, 1);
        assertTrue(menu.getPopup() instanceof ContextPopup, "labels with a key have modes too");
    }

    @Test
    void rightClickColourCopiesAndPastes() {
        IcarusMod mod = mod(IcarusOptions.defaults(), Fixture.Combat.class);
        Menu menu = mod.getMenu();
        frame(menu, 0, 0);
        ColorWidget tint = widget(menu, ColorWidget.class, "Tint");
        menu.mousePressed(tint.swatch.centerX() / 2, tint.swatch.centerY() / 2, 1);
        ContextPopup context = (ContextPopup) menu.getPopup();
        frame(menu, 0, 0);
        click(menu, context.bounds.centerX(), context.bounds.y + context.itemHeight * 0.5);
        assertEquals("#FF0000FF", platform.clipboard);

        platform.clipboard = "#00FF0080";
        menu.mousePressed(tint.swatch.centerX() / 2, tint.swatch.centerY() / 2, 1);
        context = (ContextPopup) menu.getPopup();
        frame(menu, 0, 0);
        click(menu, context.bounds.centerX(), context.bounds.y + context.itemHeight * 1.5);
        assertEquals(new java.awt.Color(0, 255, 0, 128), Fixture.Combat.Second.Tint);
    }

    @Test
    void sliderDragWritesOnceOnRelease() {
        IcarusMod mod = mod(IcarusOptions.builder().configUi(false), Fixture.Combat.class);
        Menu menu = mod.getMenu();
        frame(menu, 0, 0);
        SliderWidget range = widget(menu, SliderWidget.class, "Range");
        double y = range.track.centerY();
        menu.mousePressed(range.track.x / 2, y / 2, 0);
        for (int i = 0; i <= 100; i++) {
            frame(menu, range.track.x + range.track.w * i / 100.0, y);
            mod.getConfigStore().tick(menu.isInteracting());
        }
        assertEquals(0, mod.getConfigStore().getWriteCount());
        menu.mouseReleased(range.track.right() / 2, y / 2, 0);
        assertEquals(100, Fixture.Combat.Kill_Aura.Range);
        assertEquals(1, mod.getConfigStore().getWriteCount());
    }

    @Test
    void subTabsSwitch() {
        Menu menu = mod(IcarusOptions.defaults(), Fixture.Visuals.class).getMenu();
        frame(menu, 0, 0);
        assertEquals("Players", menu.getCurrentSubTab().getGroups().get(0).getLabel());
        Rect second = menu.getCurrentTab().getSubTabs().get(1).bounds;
        click(menu, second.centerX(), second.centerY());
        frame(menu, 0, 0);
        assertEquals("Chams", menu.getCurrentSubTab().getGroups().get(0).getLabel());
    }

    @Test
    void twoModsAreIsolated() {
        IcarusMod a = Icarus.createDetached("a", theme, IcarusOptions.builder().configUi(false), Collections.singletonList(Fixture.Combat.class),
                game.resolve("config/a"), game.resolve("a"));
        IcarusMod b = Icarus.createDetached("b", theme, IcarusOptions.builder().configUi(false), Collections.singletonList(Fixture.Visuals.class),
                game.resolve("config/b"), game.resolve("b"));
        Setting<?> enabled = a.getModel().find(Fixture.Combat.Kill_Aura.class, "Enabled");
        a.getKeybinds().set(enabled, Keys.KEY_H);
        a.interactionEnded();
        assertEquals(1, a.getConfigStore().getWriteCount());
        assertEquals(0, b.getConfigStore().getWriteCount());
        assertSame(null, b.getModel().find(Fixture.Combat.Kill_Aura.class, "Enabled"));
        assertTrue(game.resolve("config/a/configs/default.json").toFile().exists());
        assertFalse(game.resolve("config/b/configs/default.json").toFile().exists());
    }

    @Test
    @SuppressWarnings("unchecked")
    void loadingAnEmptySlotResetsToDefaults() {
        IcarusMod mod = mod(IcarusOptions.defaults(), Fixture.Combat.class);
        Setting<?> enabled = mod.getModel().find(Fixture.Combat.Kill_Aura.class, "Enabled");
        ((Setting<Boolean>) enabled).set(false);
        assertTrue(mod.getConfigStore().load("Slot 4"), "a never-saved slot loads");
        assertTrue(Fixture.Combat.Kill_Aura.Enabled, "back to its default");
        assertEquals("Slot 4", mod.getConfigStore().getProfile());
    }

    @Test
    void showIfHidesRowsUntilTheCheckboxIsTicked() {
        Menu menu = mod(IcarusOptions.builder().configUi(false), Fixture.Tall.class).getMenu();
        frame(menu, 0, 0);
        CheckboxWidget a = widget(menu, CheckboxWidget.class, "A"), child = widget(menu, CheckboxWidget.class, "Child");
        assertTrue(child.hidden, "hidden while A is off");
        double below = widget(menu, CheckboxWidget.class, "C").bounds.y;
        click(menu, a.box.centerX(), a.box.centerY());
        frame(menu, 0, 0);
        assertFalse(child.hidden);
        assertTrue(child.bounds.y > below, "takes a row once shown");
    }

    @Test
    void groupsNeverLeaveTheirColumn() {
        Menu menu = mod(IcarusOptions.builder().configUi(false), Fixture.Tall.class).getMenu();
        frame(menu, 0, 0);
        Rect area = menu.content;
        for (GroupView g : menu.getCurrentSubTab().getGroups()) {
            assertTrue(g.frame.y + g.frame.h <= area.y + area.h + 0.01, g.getLabel() + " ends at " + (g.frame.y + g.frame.h) + ", past " + (area.y + area.h));
        }
        GroupView fixed = menu.getCurrentSubTab().getGroups().get(0), tall = menu.getCurrentSubTab().getGroups().get(1);
        assertEquals(300, fixed.frame.h, 0.01);
        assertTrue(tall.isScrollable());
    }

    @Test
    void steadyFramesAreOneDrawCallAndBarelyAllocate() {
        Menu menu = Icarus.createDetached("perf", new SkeetStyleTheme(), IcarusOptions.defaults(), Collections.singletonList(Fixture.Combat.class),
                game.resolve("config/perf"), game.resolve("perf")).getMenu();
        platform.renderer.recordQuads = false;
        for (int i = 0; i < 200; i++) frame(menu, 0, 0); //warm up: glyphs, JIT
        assertEquals(1, platform.renderer.lastCommandCount, "everything comes from one atlas texture");

        com.sun.management.ThreadMXBean bean = (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean();
        long tid = Thread.currentThread().getId();
        long before = bean.getThreadAllocatedBytes(tid);
        for (int i = 0; i < 100; i++) frame(menu, 0, 0);
        long perFrame = (bean.getThreadAllocatedBytes(tid) - before) / 100;
        assertTrue(perFrame < 2048, "allocated " + perFrame + " bytes per frame");
    }

    /** Records the order of paint calls. */
    static final class RecordingTheme extends SkeetStyleTheme {
        final List<String> calls = new ArrayList<>();

        @Override
        public void paintWindow(Canvas c, Menu menu) {
            calls.add("window");
            super.paintWindow(c, menu);
        }

        @Override
        public void paintGroup(Canvas c, Menu menu, GroupView g) {
            calls.add("group");
            super.paintGroup(c, menu, g);
        }

        @Override
        public void paintCheckbox(Canvas c, CheckboxWidget w) {
            calls.add("widget");
            super.paintCheckbox(c, w);
        }

        @Override
        public void paintChoice(Canvas c, ChoiceWidget w) {
            calls.add("widget");
            super.paintChoice(c, w);
        }

        @Override
        public void paintDropdown(Canvas c, DropdownPopup p) {
            calls.add("dropdown");
            super.paintDropdown(c, p);
        }
    }
}
