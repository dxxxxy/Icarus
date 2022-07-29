package studio.dreamys.icarus.component.sub;

import org.lwjgl.input.Keyboard;
import studio.dreamys.icarus.component.Component;
import studio.dreamys.icarus.component.Window;
import studio.dreamys.icarus.util.RenderUtils;

import java.awt.Color;
import java.util.regex.Pattern;

public class Field extends Component {
    private Window window;
    private Group group;
    private double width = 80;
    private double height = 12;
    private double clearance = 50;
    private double x;
    private double y;
    private String label;

    //relative to window, aka x,y passed in constructor
    private double relativeX;
    private double relativeY;

    private String text = "";
    private boolean focused;

    public Field(String label) {
        this.label = label;
    }

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
        RenderUtils.drawString(focused ? text + "_" : text, x + 4, y + height / 10, Color.WHITE);

        //label
        RenderUtils.drawString(label, x, y - height / 1.5, Color.WHITE);
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

    @Override
    public double getClearance() {
        return 10;
    }
}