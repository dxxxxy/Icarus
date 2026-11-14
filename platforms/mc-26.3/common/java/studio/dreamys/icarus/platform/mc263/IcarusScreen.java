package studio.dreamys.icarus.platform.mc263;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import studio.dreamys.icarus.component.Window;
import studio.dreamys.icarus.input.Keys;

/**
 * The Minecraft screen showing an Icarus {@link Window}. SDL splits LWJGL2's keyTyped(char, key) into
 * keyPressed (scancode, no char) and charTyped (code point, no key code); both are forwarded as keyTyped.
 */
public class IcarusScreen extends Screen {
    private final Window window;
    private final ModernRenderer renderer;

    public IcarusScreen(Window window, ModernRenderer renderer) {
        super(Component.empty());
        this.window = window;
        this.renderer = renderer;
    }

    @Override
    public void init() { //public: the dev jar widens Screen#init, and widening is legal for the protected original too
        window.initGui();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        renderer.begin(graphics);
        window.drawScreen(mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        window.mouseClicked((int) event.x(), (int) event.y(), event.button());
        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        window.mouseReleased((int) event.x(), (int) event.y(), event.button());
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        super.keyPressed(event); //closes on escape
        window.keyTyped('\0', Keys.fromSdl(event.key()));
        return true;
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        int codePoint = event.codepoint();
        if (Character.isBmpCodePoint(codePoint)) window.keyTyped((char) codePoint, Keys.KEY_NONE);
        return true;
    }
}
