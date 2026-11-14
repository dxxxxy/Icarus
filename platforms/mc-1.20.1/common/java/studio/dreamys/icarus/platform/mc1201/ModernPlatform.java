package studio.dreamys.icarus.platform.mc1201;

import net.minecraft.client.Minecraft;
import studio.dreamys.icarus.platform.Platform;
import studio.dreamys.icarus.platform.Renderer;
import studio.dreamys.icarus.ui.MenuController;

import java.io.File;

/** The loader-independent half of the 1.20.1 platforms. */
public abstract class ModernPlatform implements Platform {
    protected final ModernRenderer renderer = new ModernRenderer();

    @Override
    public Renderer getRenderer() {
        return renderer;
    }

    @Override
    public double getGuiScale() {
        return Minecraft.getInstance().getWindow().getGuiScale();
    }

    @Override
    public int getFramebufferWidth() {
        return Minecraft.getInstance().getWindow().getWidth();
    }

    @Override
    public int getFramebufferHeight() {
        return Minecraft.getInstance().getWindow().getHeight();
    }

    @Override
    public boolean isInGame() {
        return Minecraft.getInstance().level != null && Minecraft.getInstance().player != null;
    }

    @Override
    public void openScreen(MenuController menu) {
        Minecraft.getInstance().setScreen(new IcarusScreen(menu, renderer));
    }

    @Override
    public File getGameDir() {
        return Minecraft.getInstance().gameDirectory;
    }

    @Override
    public String getClipboard() {
        return Minecraft.getInstance().keyboardHandler.getClipboard();
    }

    @Override
    public void setClipboard(String text) {
        Minecraft.getInstance().keyboardHandler.setClipboard(text);
    }
}
