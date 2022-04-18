package studio.dreamys.minesense.component.sub;

import net.minecraft.client.gui.Gui;
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

    public Button(Window window, double width, double height, double x, double y, String label, Runnable onClick) {
        this.window = window;
        this.width = width;
        this.height = height;
        this.x = window.x + x;
        this.y = window.y + y;
        relativeX = x;
        relativeY = y;
        this.label = label;

        this.onClick = onClick;
    }

    public void render(int mouseX, int mouseY) {
        //update position
        x = window.x + relativeX;
        y = window.y + relativeY;

        //the component itself + the text written
        Gui.drawRect((int) x, (int) y, (int) (x + width), (int) (y + height), Color.DARK_GRAY.darker().darker().getRGB());
        RenderUtils.drawScaledCenteredString(label, (int) ((int) x + width / 2), (int) (y + height / 3), 0.5f,  Color.WHITE);
//        RenderUtils.drawOutline(width, height, x, y, Color.DARK_GRAY);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (hovered(mouseX, mouseY) && mouseButton == 0) {
            onClick.run();
        }
    }

    private boolean hovered(double x, double y) {
        return x > this.x && x < this.x + width && y > this.y && y < this.y + height;
    }
}
