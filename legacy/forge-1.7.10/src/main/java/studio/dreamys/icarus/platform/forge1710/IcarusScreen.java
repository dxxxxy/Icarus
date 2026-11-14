package studio.dreamys.icarus.platform.forge1710;

import net.minecraft.client.gui.GuiScreen;
import studio.dreamys.icarus.component.Window;

/** The Minecraft screen showing an Icarus {@link Window}. */
public class IcarusScreen extends GuiScreen {
    private final Window window;

    public IcarusScreen(Window window) {
        this.window = window;
    }

    @Override
    public void initGui() {
        window.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        window.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        window.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode); //closes on escape
        window.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (state != -1) window.mouseReleased(mouseX, mouseY, state); //older versions also reported plain movement as -1
    }
}
