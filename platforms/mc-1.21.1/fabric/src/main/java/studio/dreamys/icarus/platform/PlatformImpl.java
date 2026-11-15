package studio.dreamys.icarus.platform;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.gui.GuiGraphics;
import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.platform.mc1211.ModernPlatform;

import java.nio.file.Path;
import java.util.Collection;

/**
 * Fabric 1.21.1. Hooks are mixins (studio.dreamys.icarus.platform.mc1211.fabric.mixin) rather than fabric-api
 * events, so Icarus works without fabric-api; they only forward once {@link #install()} ran.
 */
@SuppressWarnings("unused")
public class PlatformImpl extends ModernPlatform {
    private static PlatformImpl installed;

    @Override
    public void install() {
        installed = this;
    }

    /** Called by GuiMixin at the end of the HUD render. */
    public static void renderHud(GuiGraphics graphics, float partialTick) {
        if (installed == null) return;
        installed.renderer.begin(graphics);
        Icarus.renderHud(partialTick);
    }

    /** Called by MinecraftMixin at the start of every client tick. */
    public static void clientTick() {
        if (installed != null) Icarus.clientTick();
    }

    /** Called by KeyboardHandlerMixin for key presses while no screen is open. */
    public static void keyPressed(int key) {
        if (installed != null) Icarus.onKeyPressed(key);
    }

    /** Called by KeyboardHandlerMixin for key releases. */
    public static void keyReleased(int key) {
        if (installed != null) Icarus.onKeyReleased(key);
    }

    @Override
    public Collection<Path> getModRoots(String modid) {
        return container(modid).getRootPaths();
    }

    @Override
    public ClassLoader getModClassLoader(String modid) {
        return PlatformImpl.class.getClassLoader(); //Knot loads every mod with the same class loader
    }

    private static ModContainer container(String modid) {
        return FabricLoader.getInstance().getModContainer(modid).orElseThrow(() -> new IllegalArgumentException("No mod with id " + modid));
    }
}
