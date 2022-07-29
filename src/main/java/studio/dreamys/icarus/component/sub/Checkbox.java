package studio.dreamys.icarus.component.sub;

import studio.dreamys.icarus.component.Component;
import studio.dreamys.icarus.component.Window;
import studio.dreamys.icarus.util.RenderUtils;

import java.awt.Color;

public class Checkbox extends Component {
    private Window window;
    private Group group;
    private double width = 5;
    private double height = 5;
    private double x;
    private double y;
    private String label;

    //relative to window, aka x,y passed in constructor
    private double relativeX;
    private double relativeY;

    private boolean toggled;

    public Checkbox(String label) {
        this.label = label;
    }

    public Checkbox(double width, double height, double x, double y, String label) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.label = label;
    }

    public void render(int mouseX, int mouseY) {
        //update position
        x = window.x + relativeX;
        y = window.y + relativeY;

        Color color = toggled ? window.color : Color.DARK_GRAY;

        //the box itself + label next to it
        RenderUtils.drawGradientRect(x, y, x + width, y + height, color, color.darker().darker());
        RenderUtils.drawString(label, x + width * 2 - 1, y - height / 2 + 0.75,  Color.WHITE);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (hovered(mouseX, mouseY) && mouseButton == 0) {
            toggled = !toggled;
        }
    }

    private boolean hovered(double x, double y) {
        return x > this.x && x < this.x + width && y > this.y && y < this.y + height;
    }

    public void setWindow(Window window) {
        this.window = window;
        relativeX = x;
        relativeY = y;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Window getWindow() {
        return window;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public void setX(double x) {
        relativeX = x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public void setY(double y) {
        relativeY = y;
    }
}