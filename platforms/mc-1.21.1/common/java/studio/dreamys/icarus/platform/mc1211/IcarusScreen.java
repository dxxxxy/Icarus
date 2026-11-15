package studio.dreamys.icarus.platform.mc1211;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import studio.dreamys.icarus.input.Keys;
import studio.dreamys.icarus.ui.MenuController;

/** The Minecraft screen showing an Icarus menu; forwards everything to the {@link MenuController}. */
public class IcarusScreen extends Screen {
    private final MenuController menu;
    private final ModernRenderer renderer;
    private char highSurrogate;

    public IcarusScreen(MenuController menu, ModernRenderer renderer) {
        super(Component.empty());
        this.menu = menu;
        this.renderer = renderer;
    }

    @Override
    public void init() { //public: fabric-api widens Screen#init, and widening is legal for the protected original too
        menu.opened();
    }

    /** Mouse position in fractional GUI units (the int arguments are rounded). */
    private double mouseX() {
        return minecraft.mouseHandler.xpos() * width / minecraft.getWindow().getScreenWidth();
    }

    private double mouseY() {
        return minecraft.mouseHandler.ypos() * height / minecraft.getWindow().getScreenHeight();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick); //default background
        renderer.begin(graphics);
        menu.render(mouseX(), mouseY(), partialTick);
        if (menu.isFadedOut()) onClose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        menu.mousePressed(mouseX, mouseY, button);
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        menu.mouseReleased(mouseX, mouseY, button);
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        menu.mouseScrolled(mouseX, mouseY, scrollY);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if (!menu.keyPressed(Keys.KEY_ESCAPE)) menu.requestClose(); //fades out, then closes below
            return true;
        }
        menu.keyPressed(Keys.fromGlfw(keyCode));
        return true;
    }

    @Override
    public boolean charTyped(char c, int modifiers) {
        if (Character.isHighSurrogate(c)) {
            highSurrogate = c;
            return true;
        }
        if (Character.isLowSurrogate(c) && highSurrogate != 0) {
            menu.charTyped(Character.toCodePoint(highSurrogate, c));
        } else {
            menu.charTyped(c);
        }
        highSurrogate = 0;
        return true;
    }

    @Override
    public void removed() {
        menu.closed();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
