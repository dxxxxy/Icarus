package studio.dreamys.icarus.component;

import lombok.Getter;
import studio.dreamys.icarus.util.position.Bounds;

import java.lang.reflect.Field;

public abstract class Component {
    public Window window;
    public Group group;

    @Getter protected double x, y;
    @Getter protected double width, height;
    protected double relativeX, relativeY;

    @Getter protected String label;
    @Getter protected boolean open;

    public Field configField;

    public Component(String label, double width, double height) {
        this.label = label.replaceAll("_", " ");
        this.width = width;
        this.height = height;
    }

    public Component() {

    }

    public void render(int mouseX, int mouseY) {
        x = window.x + relativeX;
        y = window.y + relativeY;
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {

    }

    public void keyTyped(char typedChar, int keyCode) {

    }

    public void mouseReleased(int mouseX, int mouseY, int state) {

    }

    public void setWindow(Window window) {
        this.window = window;
        relativeX = x;
        relativeY = y;
    }

    public void setX(double x) {
        relativeX = x;
    }

    public void setY(double y) {
        relativeY = y;
    }

    public boolean hovered(double x, double y) {
        return x > this.x && x < this.x + width && y > this.y && y < this.y + height;
    }

    public Bounds getBounds() {
        return new Bounds(width, height);
    }
}