package studio.dreamys.minesense.component.sub;

import studio.dreamys.minesense.component.Component;
import studio.dreamys.minesense.component.Window;
import studio.dreamys.minesense.util.RenderUtils;

import java.awt.Color;

public class Button extends Component {
    private Window window;
    private double width;
    private double height;
    private double x;
    private double y;
    private String label;

    //relative to window, aka x,y passed in constructor
    private double relativeX;
    private double relativeY;

    private Runnable onClick;

    public Button(double width, double height, double x, double y, String label, Runnable onClick) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.label = label;

        this.onClick = onClick;
    }

    public void render(int mouseX, int mouseY) {
        //update position
        x = window.x + relativeX;
        y = window.y + relativeY;

        //the component itself + the text written
        RenderUtils.drawGradientRect(x, y, x + width, y + height, Color.DARK_GRAY.darker(), Color.DARK_GRAY.darker().darker());
        RenderUtils.drawOutline(width, height, x, y, Color.DARK_GRAY);
        RenderUtils.drawScaledCenteredString(label, x + width / 2, y + height / 3, 0.5f,  Color.WHITE);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (hovered(mouseX, mouseY) && mouseButton == 0) {
            onClick.run();
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
}