package studio.dreamys.icarus.component.sub;

import org.lwjgl.input.Keyboard;
import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.component.Attachment;
import studio.dreamys.icarus.component.Component;
import studio.dreamys.icarus.util.RenderUtils;

import java.awt.Color;

public class Keybind extends Attachment {
    private Component child;

    private double x;
    private double y;

    private String keybind;
    private int key;
    private boolean capturing;

    @Override
    public void render(int mouseX, int mouseY) {
        x = child.getX();
        y = child.getY();
        RenderUtils.drawString(keybind, x + child.getGroup().getWidth() - 150, y, Color.WHITE);
    }

    public int getKey() {
        return key;
    }

    @Override
    public Component attachTo(Component child) {
        this.child = child;
        this.child.getGroup().addChild(this);
        Icarus.window.attachments.add(this);
        return child;
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (hovered(mouseX, mouseY) && mouseButton == 0) {
            capturing = !capturing;
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
}
