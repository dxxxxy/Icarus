package studio.dreamys.icarus.platform;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModFileInfo;
import org.lwjgl.glfw.GLFW;
import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.input.Keys;
import studio.dreamys.icarus.platform.mc1201.ModernPlatform;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

/** Forge 1.20.1. */
@SuppressWarnings("unused")
public class PlatformImpl extends ModernPlatform {
    @Override
    public void install() {
        MinecraftForge.EVENT_BUS.addListener((InputEvent.Key e) -> {
            if (e.getAction() == GLFW.GLFW_PRESS && Minecraft.getInstance().screen == null) {
                Icarus.onKeyPressed(Keys.fromGlfw(e.getKey()));
            }
        });
        MinecraftForge.EVENT_BUS.addListener((RenderGuiEvent.Post e) -> {
            renderer.begin(e.getGuiGraphics());
            Icarus.renderHud();
        });
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent e) -> {
            if (e.phase == TickEvent.Phase.START) Icarus.clientTick();
        });
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
