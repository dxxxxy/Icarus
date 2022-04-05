package studio.dreamys.gui.component.sub;

import studio.dreamys.gui.component.Component;
import studio.dreamys.gui.component.Window;
import studio.dreamys.gui.util.RenderUtils;

import java.awt.Color;

public class Checkbox extends Component {
    private Window window;
    private double width;
    private double height;
    private double x;
    private double y;
    private Color color;
    private String label;

    //relative to window, aka x,y passed in constructor
    private double relativeX;
    private double relativeY;

    private boolean toggled;

    public Checkbox(Window window, double width, double height, double x, double y, Color color, String label) {
        this.window = window;
        this.width = width;
        this.height = height;
        this.x = window.x + x;
        this.y = window.y + y;
        relativeX = x;
        relativeY = y;
        this.color = color;
        this.label = label;
    }

    public void render(int mouseX, int mouseY) {
        //update position
        x = window.x + relativeX;
        y = window.y + relativeY;

        Color color = toggled ? this.color : Color.DARK_GRAY;

        //the box itself
        RenderUtils.drawGradientRect((int) x, (int) y, (int) (x + width), (int) (y + height), color.getRGB(), color.darker().darker().getRGB());

        //label
        RenderUtils.drawScaledString(label, (int) (x + width * 2), (int) y, 0.5f,  Color.WHITE);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (hovered(mouseX, mouseY) && mouseButton == 0) {
            toggle();
        }
    }

    public boolean hovered(double x, double y) {
        return x > this.x && x < this.x + width && y > this.y && y < this.y + height;
    }

    public void toggle() {
        toggled = !toggled;
    }
}
