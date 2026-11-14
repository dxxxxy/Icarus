package studio.dreamys.icarus.platform;

import studio.dreamys.icarus.ui.MenuController;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;

/**
 * Everything Icarus needs from the running Minecraft version and mod loader. Each Icarus build ships exactly one
 * implementation, named {@link #IMPLEMENTATION}.
 */
public interface Platform {
    String IMPLEMENTATION = "studio.dreamys.icarus.platform.PlatformImpl";

    /**
     * Called once, before the first mod is created. Registers the loader hooks that feed
     * {@link studio.dreamys.icarus.Icarus#onKeyPressed(int)}, {@link studio.dreamys.icarus.Icarus#renderHud(float)}
     * and {@link studio.dreamys.icarus.Icarus#clientTick()}.
     */
    void install();

    Renderer getRenderer();

    /** Minecraft's GUI scale (GUI units to framebuffer pixels). */
    double getGuiScale();

    int getFramebufferWidth();

    int getFramebufferHeight();

    /** Whether a world is loaded and the player exists. */
    boolean isInGame();

    /** Opens a Minecraft screen showing the menu; it forwards input and rendering to {@code menu}. */
    void openScreen(MenuController menu);

    File getGameDir();

    /** The roots (directories or jars) holding the classes of the given mod. */
    Collection<Path> getModRoots(String modid);

    ClassLoader getModClassLoader(String modid);

    String getClipboard();

    void setClipboard(String text);
}
