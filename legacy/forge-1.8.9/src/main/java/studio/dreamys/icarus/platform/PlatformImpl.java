package studio.dreamys.icarus.platform;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import studio.dreamys.icarus.component.Window;
import studio.dreamys.icarus.platform.forge189.ForgeHooks;
import studio.dreamys.icarus.platform.forge189.IcarusScreen;
import studio.dreamys.icarus.platform.forge189.LegacyRenderer;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

/** Forge 1.8.9. */
@SuppressWarnings("unused")
public class PlatformImpl implements Platform {
    private final Renderer renderer = new LegacyRenderer();

    @Override
    public void install() {
        MinecraftForge.EVENT_BUS.register(new ForgeHooks());
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
