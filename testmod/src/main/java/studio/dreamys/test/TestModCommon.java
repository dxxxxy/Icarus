package studio.dreamys.test;

import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.IcarusMod;
import studio.dreamys.icarus.IcarusOptions;
import studio.dreamys.icarus.hud.HudContext;
import studio.dreamys.icarus.hud.HudElement;
import studio.dreamys.icarus.hud.Notification;
import studio.dreamys.icarus.hud.Watermark;
import studio.dreamys.icarus.input.Keys;
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

        if (theme == Themes.SKEET) {
            //like the original: DEL opens the menu, "Menu color" is the accent and "DPI scale" resizes the menu
            mod.setMenuKey(Keys.KEY_DELETE);
            mod.setAccent(studio.dreamys.test.skeet.Misc.Settings.Menu_color.getRGB());
            mod.onChange(studio.dreamys.test.skeet.Misc.Settings.class, "Menu_color", (java.awt.Color c) -> mod.setAccent(c.getRGB()));
            applyDpi(studio.dreamys.test.skeet.Misc.Settings.DPI_scale);
            mod.onChange(studio.dreamys.test.skeet.Misc.Settings.class, "DPI_scale", TestModCommon::applyDpi);
        }

        //HUD: a watermark in the theme's style, notifications below it (K, J and L send test ones)
        mod.addHud(new Watermark("icarus | " + modid));
        mod.notifications().setOffsetY(32);
        mod.addKeyListener(TestModCommon::key);

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

    /** "125%" is the size the skeet theme was measured at. */
    private static void applyDpi(String percent) {
        try {
            mod.setMenuScale(Integer.parseInt(percent.replace("%", "").trim()) / 125.0);
        } catch (NumberFormatException ignored) {
        }
    }

    private static void key(int keyCode) {
        if (keyCode == Keys.KEY_K) notify("Test", "Test");
        if (keyCode == Keys.KEY_J) notify("Test", "This is a very cool informational notification which is much larger in size.");
        if (keyCode == Keys.KEY_L) {
            notify("Hello", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec congue neque libero, a posuere erat pharetra sed. "
                    + "Phasellus convallis nisi vel tortor malesuada commodo eu in nunc.");
        }
    }
}
