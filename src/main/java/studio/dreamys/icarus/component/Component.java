package studio.dreamys.icarus.component;

import studio.dreamys.icarus.util.position.Bounds;

import java.lang.reflect.Field;

public class Component {
    protected Window window;
    protected Group group;

    protected double x, relativeX;
    protected double y, relativeY;
    protected double width, height;

    protected String label;

    public Field configField;

    public Component(String label, double width, double height) {
        this.label = label;
        this.width = width;
        this.height = height;
    }

    public Component() {

    }

    public void render(int mouseX, int mouseY) {
        update();
    }

    public void update() {
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

    public void setGroup(Group group) {

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

    public Group getGroup() {
        return null;
    }

    public Window getWindow() {
        return null;
    }

    public double getX() {
        return 0;
    }

    public double getY() {
        return 0;
    }

    public boolean isOpen() {
        return false;
    }

    public double getWidth() {
        return 0;
    }

    public double getHeight() {
        return 0;
    }

    public Bounds getBounds() {
        return new Bounds(width, height);
    }

    public String getLabel() {
        return "null";
    }

    public void fireChange() {

    }
}