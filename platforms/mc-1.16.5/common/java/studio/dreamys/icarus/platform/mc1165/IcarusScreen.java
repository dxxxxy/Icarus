package studio.dreamys.icarus.platform.mc1165;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import org.lwjgl.glfw.GLFW;
import studio.dreamys.icarus.input.Keys;
import studio.dreamys.icarus.ui.MenuController;

/** The Minecraft screen showing an Icarus menu; forwards everything to the {@link MenuController}. */
public class IcarusScreen extends Screen {
    private final MenuController menu;
    private final ModernRenderer renderer;
    private char highSurrogate;

    public IcarusScreen(MenuController menu, ModernRenderer renderer) {
        super(new TextComponent(""));
        this.menu = menu;
        this.renderer = renderer;
    }

    @Override
    protected void init() {
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
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        renderBackground(poseStack); //1.16.5's Screen#render doesn't draw the background itself
        super.render(poseStack, mouseX, mouseY, partialTick);
        renderer.begin(poseStack);
        menu.render(mouseX(), mouseY(), partialTick);
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
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        menu.mouseScrolled(mouseX, mouseY, delta);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if (!menu.keyPressed(Keys.KEY_ESCAPE)) onClose();
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
