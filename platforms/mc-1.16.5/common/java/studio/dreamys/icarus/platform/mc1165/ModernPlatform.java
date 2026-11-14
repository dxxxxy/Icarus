package studio.dreamys.icarus.platform.mc1165;

import net.minecraft.client.Minecraft;
import studio.dreamys.icarus.component.Window;
import studio.dreamys.icarus.platform.Platform;
import studio.dreamys.icarus.platform.Renderer;

import java.io.File;

/** The loader-independent half of the 1.16.5 platforms. */
public abstract class ModernPlatform implements Platform {
    protected final ModernRenderer renderer = new ModernRenderer();

    @Override
    public Renderer getRenderer() {
        return renderer;
    }

    @Override
    public int getDisplayWidth() {
        return Minecraft.getInstance().getWindow().getWidth();
    }

    @Override
    public int getDisplayHeight() {
        return Minecraft.getInstance().getWindow().getHeight();
    }

    @Override
    public boolean isInGame() {
        return Minecraft.getInstance().level != null && Minecraft.getInstance().player != null;
    }

    @Override
    public void openScreen(Window window) {
        Minecraft.getInstance().setScreen(new IcarusScreen(window, renderer));
    }

    @Override
    public File getGameDir() {
        return Minecraft.getInstance().gameDirectory;
    }
}
