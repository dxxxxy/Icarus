package studio.dreamys.icarus.component.sub;

import studio.dreamys.icarus.component.Component;
import studio.dreamys.icarus.component.Window;
import studio.dreamys.icarus.util.RenderUtils;

import java.awt.Color;

public class Checkbox extends Component {
    private Window window;

    private double x;
    private double y;
    private double width = 5;
    private double height = 5;
    private String label;

    private boolean toggled;

    //relative to window, aka x,y passed in constructor
    private double relativeX;
    private double relativeY;

    public Checkbox(String label) {
        this.label = label;
    }

    public Checkbox(double x, double y, double width, double height, String label) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.label = label;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        //update position
        x = window.x + relativeX;
        y = window.y + relativeY;

        Color color = toggled ? window.color : Color.DARK_GRAY;

        //the box itself + label next to it
        RenderUtils.drawGradientRect(x, y, x + width, y + height, color, color.darker().darker());
        RenderUtils.drawString(label, x + width * 2 - 1, y - height / 2 + 0.75,  Color.WHITE);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (hovered(mouseX, mouseY) && mouseButton == 0) {
            toggled = !toggled;
        }
    }

    private boolean hovered(double x, double y) {
        return x > this.x && x < this.x + width && y > this.y && y < this.y + height;
    }

    @Override
    public void setWindow(Window window) {
        this.window = window;
        relativeX = x;
        relativeY = y;
    }

    @Override
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

    @Override
    public String getLabel() {
        return label;
    }

    public boolean isToggled() {
        return toggled;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }
}