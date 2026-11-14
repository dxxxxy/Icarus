package studio.dreamys.icarus.platform.forge1122;

import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import studio.dreamys.icarus.input.Keys;
import studio.dreamys.icarus.ui.MenuController;

import java.io.IOException;

/** The Minecraft screen showing an Icarus menu; forwards everything to the {@link MenuController}. */
public class IcarusScreen extends GuiScreen {
    private final MenuController menu;

    public IcarusScreen(MenuController menu) {
        this.menu = menu;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        menu.opened();
    }

    /** Mouse position in (fractional) GUI units. */
    private double mouseX() {
        return Mouse.getX() * (double) width / mc.displayWidth;
    }

    private double mouseY() {
        return height - Mouse.getY() * (double) height / mc.displayHeight - 1;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        menu.render(mouseX(), mouseY(), partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        menu.mousePressed(mouseX(), mouseY(), mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        menu.mouseReleased(mouseX(), mouseY(), state);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) menu.mouseScrolled(mouseX(), mouseY(), Math.signum(wheel));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keys.KEY_ESCAPE) {
            if (!menu.keyPressed(keyCode)) super.keyTyped(typedChar, keyCode); //closes
            return;
        }
        menu.keyPressed(keyCode);
        if (typedChar != 0 && !Character.isISOControl(typedChar)) menu.charTyped(typedChar);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        menu.closed();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
