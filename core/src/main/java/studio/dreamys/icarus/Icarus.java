package studio.dreamys.icarus;

import studio.dreamys.icarus.input.Keys;
import studio.dreamys.icarus.annotation.type.IPage;
import studio.dreamys.icarus.config.ClassScanner;
import studio.dreamys.icarus.platform.Platform;
import studio.dreamys.icarus.platform.RecorderShield;
import studio.dreamys.icarus.render.FontAtlas;
import studio.dreamys.icarus.render.RecorderOverlay;
import studio.dreamys.icarus.theme.Theme;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Entry point. Create one {@link IcarusMod} per mod with {@link #create}; the platform (Minecraft version and mod
 * loader adapter) is found automatically and feeds key presses, HUD rendering and ticks to every mod.
 */
public final class Icarus {
    private static Platform platform;
    private static boolean installed;
    private static FontAtlas atlas;
    private static RecorderShield shield;
    private static final RecorderOverlay overlay = new RecorderOverlay();
    private static final List<IcarusMod> mods = new CopyOnWriteArrayList<>();

    private Icarus() {
    }

    public static IcarusMod create(String modid, Theme theme) {
        return create(modid, theme, IcarusOptions.defaults());
    }

    /**
     * Creates the Icarus instance of a mod: finds its {@code @IPage} classes in the mod's own jar, loads its config
     * from {@code config/<modid>/} (migrating Icarus v2 files) and builds its menu. Call it once, on the client
     * thread, after the game window exists.
     */
    public static IcarusMod create(String modid, Theme theme, IcarusOptions options) {
        for (IcarusMod mod : mods) {
            if (mod.getModid().equals(modid)) throw new IllegalStateException("Icarus was already created for " + modid);
        }
        Platform p = getPlatform();
        if (!installed) {
            p.install();
            installed = true;
        }
        List<Class<?>> pages = ClassScanner.findAnnotated(p.getModRoots(modid), p.getModClassLoader(modid), IPage.class);
        if (options.pagesPackage != null) pages.removeIf(type -> !type.getName().startsWith(options.pagesPackage + "."));
        Path game = p.getGameDir().toPath();
        IcarusMod mod = new IcarusMod(modid, theme, options, pages, game.resolve("config").resolve(modid), game.resolve(modid));
        mods.add(mod);
        return mod;
    }

    /** Builds an instance from explicit page classes and directories, without touching the platform (for tests). */
    public static IcarusMod createDetached(String modid, Theme theme, IcarusOptions options, List<Class<?>> pages, Path configDir, Path legacyDir) {
        return new IcarusMod(modid, theme, options, pages, configDir, legacyDir);
    }

    /** The implementation for the running Minecraft version / mod loader. */
    public static Platform getPlatform() {
        if (platform == null) {
            try {
                platform = (Platform) Class.forName(Platform.IMPLEMENTATION, true, Icarus.class.getClassLoader()).getConstructor().newInstance();
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("No Icarus platform found (" + Platform.IMPLEMENTATION + "). Make sure the Icarus build for your Minecraft version and mod loader is on the classpath.", e);
            }
        }
        return platform;
    }

    /** Supplies the platform explicitly instead of looking it up (tests, custom environments). */
    public static void setPlatform(Platform platform) {
        Icarus.platform = platform;
        if (shield != null) overlay.hide(shield);
        overlay.reset();
        shield = null;
    }

    /**
     * Whether the menu can be hidden from recorders and screen sharing here (Windows 10 2004 or later). Call it on the
     * render thread.
     */
    public static boolean canHideFromRecorders() {
        return getRecorderShield().isAvailable();
    }

    /** The platform's {@link RecorderShield} (created on first use). */
    public static RecorderShield getRecorderShield() {
        if (shield == null) {
            try {
                shield = getPlatform().createRecorderShield();
            } catch (LinkageError e) { //JNA missing (Minecraft 1.7.10 doesn't ship it)
                java.util.logging.Logger.getLogger("Icarus").warning("[Icarus] Can't hide the menu from recorders: " + e);
                shield = RecorderShield.UNAVAILABLE;
            }
        }
        return shield;
    }

    /** Draws menus into the {@link RecorderShield} while they're hidden from recorders. */
    public static RecorderOverlay getRecorderOverlay() {
        return overlay;
    }

    /** The texture atlas shared by every mod (fonts and generated images). */
    public static FontAtlas getAtlas() {
        if (atlas == null) atlas = new FontAtlas();
        return atlas;
    }

    public static List<IcarusMod> getMods() {
        return Collections.unmodifiableList(mods);
    }

    /* ---- called by the platform ---- */

    /** A key was pressed in game while no screen was open ({@link studio.dreamys.icarus.input.Keys} code). */
    public static void onKeyPressed(int key) {
        if (key <= 0 || !getPlatform().isInGame()) return;
        for (IcarusMod mod : mods) mod.onKeyPressed(key);
    }

    /** A key was released in game while no screen was open (drives "on hotkey" / "off hotkey" checkboxes). */
    public static void onKeyReleased(int key) {
        if (key <= 0 || platform == null) return;
        for (IcarusMod mod : mods) mod.onKeyReleased(key);
    }

    /** A mouse button (0 left, 1 right, 2 middle, 3/4 side) was pressed in game while no screen was open. */
    public static void onMousePressed(int button) {
        onKeyPressed(Keys.mouse(button));
    }

    public static void onMouseReleased(int button) {
        onKeyReleased(Keys.mouse(button));
    }

    /** The HUD is being drawn. */
    public static void renderHud(float partialTick) {
        for (IcarusMod mod : mods) mod.renderHud(partialTick);
    }

    /** Start of a client tick. */
    public static void clientTick() {
        for (IcarusMod mod : mods) mod.tick();
        if (shield != null) overlay.hideIfStale(shield);
    }
}
