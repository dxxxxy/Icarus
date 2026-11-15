package studio.dreamys.test;

import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.IcarusMod;
import studio.dreamys.icarus.IcarusOptions;
import studio.dreamys.icarus.hud.HudContext;
import studio.dreamys.icarus.hud.HudElement;
import studio.dreamys.icarus.hud.Notification;
import studio.dreamys.icarus.render.Canvas;
import studio.dreamys.icarus.theme.Theme;
import studio.dreamys.icarus.theme.Themes;

/**
 * The example mod, independent of Minecraft version and mod loader: a recreation of the skeet menu
 * ({@code studio.dreamys.test.skeet}) or the fatality menu ({@code studio.dreamys.test.fatality}). The two themes
 * offer different controls, so each has its own pages; {@code ICARUS_TEST_THEME=fatality} picks the second.
 * Each platform has a tiny entrypoint (@Mod class, mixin, ...) that calls {@link #init(String)}.
 */
public class TestModCommon {
    private static IcarusMod mod;

    public static void init(String modid) {
        String themeId = System.getenv("ICARUS_TEST_THEME");
        Theme theme = Themes.byId(themeId == null ? "skeet" : themeId);
        if (theme == null) theme = Themes.SKEET;
        mod = Icarus.create(modid, theme, IcarusOptions.defaults()
                .themeSelector(false)
                .pagesPackage("studio.dreamys.test." + theme.getId()));
        //the fatality header shows the product name
        if (theme == Themes.FATALITY) mod.getMenu().setTitle("FATALITY");

        //dev aid: open the menu on the title screen so every platform can be checked without a world
        if (System.getenv("ICARUS_TEST_OPEN") != null) {
            mod.addHud(new HudElement() {
                private int ticks;

                @Override
                public void render(Canvas canvas, HudContext hud) {
                }

                @Override
                public void tick() {
                    if (++ticks == 100) mod.openMenu();
                }
            });
        }
    }

    public static void notify(String title, String message) {
        if (mod != null) mod.notifications().send(new Notification(title, message));
    }
}
