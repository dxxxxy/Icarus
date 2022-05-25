package studio.dreamys.minesense.component.sub;

import org.lwjgl.input.Keyboard;
import studio.dreamys.minesense.component.Component;
import studio.dreamys.minesense.component.Window;
import studio.dreamys.minesense.util.RenderUtils;

import java.awt.Color;
import java.util.regex.Pattern;

public class Field extends Component {
    private Window window;
    private double width;
    private double height;
    private double x;
    private double y;
    private String label;

    //relative to window, aka x,y passed in constructor
    private double relativeX;
    private double relativeY;

    private String text = "";
    private boolean focused;

    public Field(double width, double height, double x, double y, String label) {
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

        //the component itself + the text written
        RenderUtils.drawRect(x, y, x + width, y + height, Color.DARK_GRAY.darker().darker());
        RenderUtils.drawOutline(width, height, x, y, Color.DARK_GRAY);
        RenderUtils.drawScaledString(focused ? text + "_" : text, x + 4, y + height / 3, 0.5f,  Color.WHITE);

        //label
        RenderUtils.drawScaledString(label, x, y - height / 1.75, 0.5f, Color.WHITE);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        focused = hovered(mouseX, mouseY) && mouseButton == 0;
    }

    public void keyTyped(char typedChar, int keyCode) {
        if (focused) {
            if (keyCode == Keyboard.KEY_BACK) {
                if (text.length() > 0) {
                    text = text.substring(0, text.length() - 1);
                }
            }
            else if (Pattern.compile("[a-zA-Z0-9\\t\\n ./<>?;:\"'`!@#$%^&*()\\[\\]{}_+=~|\\\\-]").matcher(String.valueOf(typedChar)).find()) text += typedChar;
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