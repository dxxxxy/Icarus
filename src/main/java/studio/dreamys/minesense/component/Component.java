package studio.dreamys.minesense.component;

import studio.dreamys.minesense.component.sub.Group;

public class Component {
    public void render(int mouseX, int mouseY) {

    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {

    }

    public void keyTyped(char typedChar, int keyCode) {

    }

    public void mouseReleased(int mouseX, int mouseY, int state) {

    }

    public void setWindow(Window window) {

    }

    public void setGroup(Group group) {

    }

    public Window getWindow() {
        return null;
    }

    public boolean open() {
        return false;
    }

    public Component attach(Attachment attachment) {
        return attachment.attachTo(this);
    }

    public double getX() {
        return 0;
    }

    public void setX(double x) {

    }

    public void setY(double y) {

    }

    public double getY() {
        return 0;
    }

    public double getWidth() {
        return 0;
    }

    public double getHeight() {
        return 0;
    }

    public double getClearance() {
        return 5;
    }
}
