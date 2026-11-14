package studio.dreamys.test;

import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.IcarusMod;
import studio.dreamys.icarus.hud.HudContext;
import studio.dreamys.icarus.hud.HudElement;
import studio.dreamys.icarus.hud.Notification;
import studio.dreamys.icarus.hud.Watermark;
import studio.dreamys.icarus.input.Keys;
import studio.dreamys.icarus.render.Canvas;
import studio.dreamys.icarus.theme.Theme;
import studio.dreamys.icarus.theme.Themes;
import studio.dreamys.test.ui.page.Combat;
import studio.dreamys.test.ui.page.Misc;

/**
 * The example mod, independent of Minecraft version and mod loader.
 * Each platform has a tiny entrypoint (@Mod class, mixin, ...) that calls {@link #init(String)}.
 */
public class TestModCommon {
    private static IcarusMod mod;

    public static void init(String modid) {
        mod = Icarus.create(modid, Themes.CLASSIC);
        mod.setFooter("uid: 001", modid, "dxxxxy#0776");

        mod.onChange(Combat.KillAura.class, "Checkbox_Haha", (Boolean value) -> System.out.println("KillAura toggled: " + value));

        Watermark watermark = new Watermark(Misc.Watermark.Text);
        watermark.setEnabled(Misc.Watermark.Enabled);
        mod.addHud(watermark);
        mod.onChange(Misc.Watermark.class, "Enabled", (Boolean value) -> watermark.setEnabled(value));
        mod.onChange(Misc.Watermark.class, "Text", (String value) -> watermark.setText(value));
        mod.notifications().setOffsetY(32); //start below the watermark

        mod.addKeyListener(TestModCommon::key);

        //dev aids: open the menu on the title screen (and pick a theme) so every platform can be checked without a world
        Theme theme = Themes.byId(System.getenv("ICARUS_TEST_THEME"));
        if (theme != null) mod.setTheme(theme);
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
        mod.notifications().send(new Notification(title, message));
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
