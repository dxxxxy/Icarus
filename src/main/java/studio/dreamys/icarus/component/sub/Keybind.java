package studio.dreamys.icarus.component.sub;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.lwjgl.input.Keyboard;
import studio.dreamys.icarus.component.Attachment;
import studio.dreamys.icarus.component.Component;
import studio.dreamys.icarus.util.RenderUtils;

import java.awt.Color;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class Keybind extends Attachment {
    private Component child;

    private double x;
    private double y;

    private String keybind = "[NONE]";
    private int key;
    private boolean capturing;

    @Override
    public void render(int mouseX, int mouseY) {
        x = child.getX() + child.getGroup().getWidth() - 50;
        y = child.getY();
        RenderUtils.drawString(keybind, x, y, Color.WHITE);
    }

    @Override
    public void attachTo(Component child) {
        this.child = child;
        this.child.getGroup().addChild(this);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (hovered(mouseX, mouseY) && mouseButton == 0) {
            System.out.println("Capturing keybind");
            keybind = "[...]";
            capturing = true;
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (capturing) {
            key = keyCode;
            keybind = "["+ Keyboard.getKeyName(key) +"]";
            capturing = false;
        }
    }

    private boolean hovered(double x, double y) {
        return x > this.x && x < this.x + RenderUtils.getStringWidth(keybind) && y > this.y && y < this.y + RenderUtils.getFontHeight();
    }

    public void setKey(int key) {
        this.key = key;
        keybind = "["+ Keyboard.getKeyName(key) +"]";
    }
}
