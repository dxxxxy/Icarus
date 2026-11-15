package studio.dreamys.test.snapshot;

import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.IcarusMod;
import studio.dreamys.icarus.IcarusOptions;
import studio.dreamys.icarus.platform.Platform;
import studio.dreamys.icarus.platform.Renderer;
import studio.dreamys.icarus.theme.Theme;
import studio.dreamys.icarus.theme.Themes;
import studio.dreamys.icarus.ui.Menu;
import studio.dreamys.icarus.ui.MenuController;

import javax.imageio.ImageIO;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Renders a theme's menu without Minecraft, for pixel comparison with the reference footage.
 * <p>
 * {@code Snapshot <theme> <scale> <tab> <out.png> [mouseX mouseY] [click x y]...}: renders the testmod pages of that
 * theme at {@code scale} screen pixels per design unit (the skeet reference is 1.25, fatality 1), with the window
 * at the top-left (1 pixel margin), on tab index {@code tab}.
 */
public final class Snapshot {
    public static void main(String[] args) throws Exception {
        String themeId = args[0];
        double scale = Double.parseDouble(args[1]);
        int tab = Integer.parseInt(args[2]);
        File out = new File(args[3]);
        double mouseX = args.length > 5 ? Double.parseDouble(args[4]) : -100, mouseY = args.length > 5 ? Double.parseDouble(args[5]) : -100;

        Theme theme = Themes.byId(themeId);
        RasterRenderer renderer = new RasterRenderer();
        Path game = Files.createTempDirectory("icarus-snapshot");
        Headless platform = new Headless(renderer, scale * 2, game.toFile());
        Icarus.setPlatform(platform);

        String pkg = "studio.dreamys.test." + themeId;
        List<Class<?>> pages = new ArrayList<>();
        for (String name : PAGES.get(themeId)) {
            try {
                pages.add(Class.forName(pkg + "." + name));
            } catch (ClassNotFoundException ignored) {
                //not written yet
            }
        }
        IcarusMod mod = Icarus.createDetached("snapshot", theme, IcarusOptions.defaults().themeSelector(false), pages,
                game.resolve("config"), game.resolve("legacy"));
        Menu menu = mod.getMenu();
        if (themeId.equals("fatality")) menu.setTitle("FATALITY");
        menu.opened();

        //first frame positions the window; then pin it near the origin
        renderer.clear(platform.width, platform.height, 0xFF404040);
        menu.render(0, 0, 0);
        menu.window.x = 1 / scale;
        menu.window.y = 1 / scale;
        menu.selectTab(tab);

        //optional clicks (design units), e.g. to open a dropdown
        for (int i = 6; i + 1 < args.length; i += 2) {
            double cx = Double.parseDouble(args[i]), cy = Double.parseDouble(args[i + 1]);
            menu.render(cx * scale / platform.guiScale, cy * scale / platform.guiScale, 0);
            menu.mousePressed(cx * scale / platform.guiScale, cy * scale / platform.guiScale, args[i].startsWith("-") ? 1 : 0);
            menu.mouseReleased(cx * scale / platform.guiScale, cy * scale / platform.guiScale, 0);
        }

        double gx = mouseX * scale / platform.guiScale, gy = mouseY * scale / platform.guiScale;
        for (int i = 0; i < 3; i++) {
            renderer.clear(platform.width, platform.height, 0xFF404040);
            menu.render(gx, gy, 0);
        }
        int w = (int) Math.ceil((menu.window.w + 2 / scale) * scale) + 1, h = (int) Math.ceil((menu.window.h + 2 / scale) * scale) + 1;
        ImageIO.write(renderer.finish().getSubimage(0, 0, Math.min(w, platform.width), Math.min(h, platform.height)), "png", out);
        System.out.println("wrote " + out + " window " + menu.window.w * scale + "x" + menu.window.h * scale);
    }

    private static final java.util.Map<String, String[]> PAGES = new java.util.HashMap<>();

    static {
        PAGES.put("skeet", new String[]{"Rage", "AntiAim", "Legit", "Visuals", "Misc", "Skins", "Players", "Config"});
        PAGES.put("fatality", new String[]{"Rage", "Visuals", "Misc", "Inventory", "Legit"});
    }

    static final class Headless implements Platform {
        final Renderer renderer;
        final double guiScale;
        final File gameDir;
        final int width = 1400, height = 1100;
        String clipboard = "";

        Headless(Renderer renderer, double guiScale, File gameDir) {
            this.renderer = renderer;
            this.guiScale = guiScale;
            this.gameDir = gameDir;
        }

        @Override
        public void install() {
        }

        @Override
        public Renderer getRenderer() {
            return renderer;
        }

        @Override
        public double getGuiScale() {
            return guiScale;
        }

        @Override
        public int getFramebufferWidth() {
            return width;
        }

        @Override
        public int getFramebufferHeight() {
            return height;
        }

        @Override
        public boolean isInGame() {
            return false;
        }

        @Override
        public void openScreen(MenuController menu) {
        }

        @Override
        public File getGameDir() {
            return gameDir;
        }

        @Override
        public Collection<Path> getModRoots(String modid) {
            return Collections.emptyList();
        }

        @Override
        public ClassLoader getModClassLoader(String modid) {
            return Snapshot.class.getClassLoader();
        }

        @Override
        public String getClipboard() {
            return clipboard;
        }

        @Override
        public void setClipboard(String text) {
            clipboard = text;
        }
    }
}
