package studio.dreamys.icarus.platform;

import studio.dreamys.icarus.component.Window;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;

/**
 * Everything Icarus needs from the game. Each Minecraft version / mod loader ships exactly one
 * implementation named {@value #IMPLEMENTATION}, which {@link studio.dreamys.icarus.Icarus#getPlatform()}
 * looks up reflectively.
 */
public interface Platform {
    String IMPLEMENTATION = "studio.dreamys.icarus.platform.PlatformImpl";

    /**
     * Called once from {@link studio.dreamys.icarus.Icarus#init}. Registers the loader hooks that feed
     * {@link studio.dreamys.icarus.Icarus#onKeyPressed(int)}, {@link studio.dreamys.icarus.Icarus#renderHud()}
     * and {@link studio.dreamys.icarus.Icarus#clientTick()}.
     */
    void install();

    Renderer getRenderer();

    /** Framebuffer width in real pixels (not GUI-scaled). */
    int getDisplayWidth();

    /** Framebuffer height in real pixels (not GUI-scaled). */
    int getDisplayHeight();

    /** Whether a world and player are loaded. */
    boolean isInGame();

    void openScreen(Window window);

    /** The game directory (.minecraft or the dev run directory). */
    File getGameDir();

    /** Directories and/or jars that make up the given mod, searched for {@code @IPage} classes. */
    Collection<Path> getModRoots(String modid);

    /** The class loader the given mod's classes are loaded with. */
    ClassLoader getModClassLoader(String modid);
}
