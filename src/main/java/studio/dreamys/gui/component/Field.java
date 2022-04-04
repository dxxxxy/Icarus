package studio.dreamys.gui.component;

import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;
import studio.dreamys.gui.util.RenderUtils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

public class Field {
    private Window window;
    private double width;
    private double height;
    private double x;
    private double y;
    private java.awt.Color color;
    private String label;

    //relative to window, aka x,y passed in constructor
    private double relativeX;
    private double relativeY;

    private String text = "";
    private boolean focused;

    public Field(Window window, double width, double height, double x, double y, String label) {
        this.window = window;
        this.width = width;
        this.height = height;
        this.x = window.x + x;
        this.y = window.y + y;
        relativeX = x;
        relativeY = y;
        this.label = label;
    }

    public void render() {
        //update position
        x = window.x + relativeX;
        y = window.y + relativeY;

        //the component itself + the text written
        Gui.drawRect((int) x, (int) y, (int) (x + width), (int) (y + height), java.awt.Color.DARK_GRAY.darker().darker().getRGB());
        RenderUtils.drawScaledString(focused ? text + "_" : text, (int) x + 4, (int) (y + height / 3), 0.5f,  java.awt.Color.WHITE);
        RenderUtils.drawOutline(width, height, x, y);

        //label
        RenderUtils.drawScaledString(label, (int) x, (int) (y - height / 1.75), 0.5f,  java.awt.Color.WHITE);
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

    public boolean hovered(double x, double y) {
        return x > this.x && x < this.x + width && y > this.y && y < this.y + height;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }
}
