package studio.dreamys.icarus.platform.mc263;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import studio.dreamys.icarus.input.Keys;
import studio.dreamys.icarus.ui.MenuController;

/** The Minecraft screen showing an Icarus menu; forwards everything to the {@link MenuController}. */
public class IcarusScreen extends Screen {
    private final MenuController menu;
    private final ModernRenderer renderer;

    public IcarusScreen(MenuController menu, ModernRenderer renderer) {
        super(Component.empty());
        this.menu = menu;
        this.renderer = renderer;
    }

    @Override
    public void init() { //public: the dev jar widens Screen#init, and widening is legal for the protected original too
        menu.opened();
    }

    /** Mouse position in fractional GUI units. */
    private double mouseX() {
        return minecraft.mouseHandler.xpos() * width / minecraft.getWindow().getScreenWidth();
    }

    private double mouseY() {
        return minecraft.mouseHandler.ypos() * height / minecraft.getWindow().getScreenHeight();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        renderer.begin(graphics);
        menu.render(mouseX(), mouseY(), partialTick);
        if (menu.isFadedOut()) onClose();
    }

    /** 26.x numbers mouse buttons like SDL (left 1, middle 2, right 3); Icarus uses LWJGL's (left 0, right 1, middle 2). */
    private static int button(MouseButtonEvent event) {
        switch (event.button()) {
            case InputConstants.MOUSE_BUTTON_LEFT: return 0;
            case InputConstants.MOUSE_BUTTON_RIGHT: return 1;
            case InputConstants.MOUSE_BUTTON_MIDDLE: return 2;
            default: return event.button() - 1;
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        menu.mousePressed(event.x(), event.y(), button(event));
        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        menu.mouseReleased(event.x(), event.y(), button(event));
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        menu.mouseScrolled(mouseX, mouseY, scrollY);
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == InputConstants.KEY_ESCAPE) {
            if (!menu.keyPressed(Keys.KEY_ESCAPE)) menu.requestClose(); //fades out, then closes below
            return true;
        }
        menu.keyPressed(Keys.fromSdl(event.key()));
        return true;
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        menu.charTyped(event.codepoint());
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
