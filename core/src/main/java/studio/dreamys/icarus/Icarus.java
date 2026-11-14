package studio.dreamys.icarus;

import studio.dreamys.icarus.annotation.type.IPage;
import studio.dreamys.icarus.config.ClassScanner;
import studio.dreamys.icarus.platform.Platform;
import studio.dreamys.icarus.render.FontAtlas;
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

    /** The HUD is being drawn. */
    public static void renderHud(float partialTick) {
        for (IcarusMod mod : mods) mod.renderHud(partialTick);
    }

    /** Start of a client tick. */
    public static void clientTick() {
        for (IcarusMod mod : mods) mod.tick();
    }
}
