package studio.dreamys.icarus.component.sub;

import studio.dreamys.icarus.component.Component;
import studio.dreamys.icarus.component.Window;
import studio.dreamys.icarus.util.RenderUtils;

import java.awt.Color;

public class Button extends Component {
    private Window window;

    private double x;
    private double y;
    private double width = 80;
    private double height = 12;
    private String label;

    private Runnable onClick;
    private boolean held;

    //relative to window, aka x,y passed in constructor
    private double relativeX;
    private double relativeY;

    public Button(String label, Runnable onClick) {
        this.label = label;

        this.onClick = onClick;
    }

    public Button(double x, double y, double width, double height, String label, Runnable onClick) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.label = label;

        this.onClick = onClick;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        //update position
        x = window.x + relativeX;
        y = window.y + relativeY;

        //the component itself + the text written
        RenderUtils.drawGradientRect(x, y, x + width, y + height, held ? Color.DARK_GRAY.darker().darker() : Color.DARK_GRAY.darker(), held ? Color.DARK_GRAY.darker() : Color.DARK_GRAY.darker().darker());
        RenderUtils.drawOutline(width, height, x, y, Color.DARK_GRAY);
        RenderUtils.drawCenteredString(label, x + width / 2, y + height / 5,  Color.WHITE);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (hovered(mouseX, mouseY) && mouseButton == 0) {
            onClick.run();
            held = true;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        held = false;
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
    public double getClearance() {
        return 7.5;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}