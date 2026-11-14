package studio.dreamys.icarus.platform;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import studio.dreamys.icarus.component.Window;
import studio.dreamys.icarus.platform.forge1710.ForgeHooks;
import studio.dreamys.icarus.platform.forge1710.IcarusScreen;
import studio.dreamys.icarus.platform.forge1710.LegacyRenderer;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

/** Forge 1.7.10. */
@SuppressWarnings("unused")
public class PlatformImpl implements Platform {
    private final Renderer renderer = new LegacyRenderer();

    @Override
    public void install() {
        ForgeHooks hooks = new ForgeHooks();
        MinecraftForge.EVENT_BUS.register(hooks); //render overlay events
        FMLCommonHandler.instance().bus().register(hooks); //input and tick events
    }

    @Override
    public Renderer getRenderer() {
        return renderer;
    }

    @Override
    public int getDisplayWidth() {
        return Minecraft.getMinecraft().displayWidth;
    }

    @Override
    public int getDisplayHeight() {
        return Minecraft.getMinecraft().displayHeight;
    }

    @Override
    public boolean isInGame() {
        return Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().thePlayer != null;
    }

    @Override
    public void openScreen(Window window) {
        Minecraft.getMinecraft().displayGuiScreen(new IcarusScreen(window));
    }

    @Override
    public File getGameDir() {
        return Minecraft.getMinecraft().mcDataDir;
    }

    @Override
    public Collection<Path> getModRoots(String modid) {
        return Collections.singletonList(container(modid).getSource().toPath());
    }

    @Override
    public ClassLoader getModClassLoader(String modid) {
        return container(modid).getMod().getClass().getClassLoader();
    }

    private static ModContainer container(String modid) {
        ModContainer container = Loader.instance().getIndexedModList().get(modid);
        if (container == null) throw new IllegalArgumentException("No mod with id " + modid);
        return container;
    }
}
