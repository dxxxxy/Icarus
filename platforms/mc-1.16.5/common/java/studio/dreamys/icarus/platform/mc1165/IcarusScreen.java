package studio.dreamys.icarus.platform.mc1165;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import studio.dreamys.icarus.component.Window;
import studio.dreamys.icarus.input.Keys;

/**
 * The Minecraft screen showing an Icarus {@link Window}. GLFW splits LWJGL2's keyTyped(char, key) into
 * keyPressed (key code, no char) and charTyped (char, no key code); both are forwarded as keyTyped.
 */
public class IcarusScreen extends Screen {
    private final Window window;
    private final ModernRenderer renderer;

    public IcarusScreen(Window window, ModernRenderer renderer) {
        super(new TextComponent(""));
        this.window = window;
        this.renderer = renderer;
    }

    @Override
    protected void init() {
        window.initGui();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        renderBackground(poseStack); //1.16.5's Screen#render doesn't draw the background itself
        super.render(poseStack, mouseX, mouseY, partialTick);
        renderer.begin(poseStack);
        window.drawScreen(mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        window.mouseClicked((int) mouseX, (int) mouseY, button);
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        window.mouseReleased((int) mouseX, (int) mouseY, button);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        super.keyPressed(keyCode, scanCode, modifiers); //closes on escape
        window.keyTyped('\0', Keys.fromGlfw(keyCode));
        return true;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        window.keyTyped(codePoint, Keys.KEY_NONE);
        return true;
    }
}
