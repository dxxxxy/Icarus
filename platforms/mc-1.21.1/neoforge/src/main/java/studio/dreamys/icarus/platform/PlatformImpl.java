package studio.dreamys.icarus.platform;

import net.minecraft.client.Minecraft;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforgespi.language.IModFileInfo;
import org.lwjgl.glfw.GLFW;
import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.input.Keys;
import studio.dreamys.icarus.platform.mc1211.ModernPlatform;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

/** NeoForge 1.21.1. */
@SuppressWarnings("unused")
public class PlatformImpl extends ModernPlatform {
    @Override
    public void install() {
        NeoForge.EVENT_BUS.addListener((InputEvent.Key e) -> {
            if (e.getAction() == GLFW.GLFW_PRESS && Minecraft.getInstance().screen == null) {
                Icarus.onKeyPressed(Keys.fromGlfw(e.getKey()));
            } else if (e.getAction() == GLFW.GLFW_RELEASE) {
                Icarus.onKeyReleased(Keys.fromGlfw(e.getKey()));
            }
        });
        NeoForge.EVENT_BUS.addListener((RenderGuiEvent.Post e) -> {
            renderer.begin(e.getGuiGraphics());
            Icarus.renderHud(e.getPartialTick().getGameTimeDeltaPartialTick(false));
        });
        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Pre e) -> Icarus.clientTick());
    }

    @Override
    public Collection<Path> getModRoots(String modid) {
        IModFileInfo file = ModList.get().getModFileById(modid);
        if (file == null) throw new IllegalArgumentException("No mod with id " + modid);
        return Collections.singletonList(file.getFile().getSecureJar().getRootPath());
    }

    @Override
    public ClassLoader getModClassLoader(String modid) {
        return PlatformImpl.class.getClassLoader(); //every game-layer mod shares FML's transforming class loader
    }
}
