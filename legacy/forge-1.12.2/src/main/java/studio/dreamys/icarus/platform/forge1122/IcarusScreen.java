package studio.dreamys.icarus.platform.forge1122;

import net.minecraft.client.gui.GuiScreen;
import studio.dreamys.icarus.component.Window;

import java.io.IOException;

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
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        window.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode); //closes on escape
        window.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        window.mouseReleased(mouseX, mouseY, state);
    }
}
