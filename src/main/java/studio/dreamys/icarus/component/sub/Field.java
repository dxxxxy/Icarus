package studio.dreamys.icarus.component.sub;

import lombok.Getter;
import lombok.Setter;
import org.lwjgl.input.Keyboard;
import studio.dreamys.icarus.component.Component;
import studio.dreamys.icarus.component.Window;
import studio.dreamys.icarus.util.RenderUtils;

import java.awt.Color;
import java.util.regex.Pattern;

@Getter
@Setter
public class Field extends Component {
    private Window window;
    private Group group;

    private double x;
    private double y;
    private double width = 80;
    private double height = 12;
    private String label;

    private String text = "";
    private boolean focused;

    //relative to window, aka x,y passed in constructor
    private double relativeX;
    private double relativeY;

    public Field(String label) {
        this.label = label;
    }

    public Field(String label, String text) {
        this.label = label;
        this.text = text;
    }

    @Override
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

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        focused = hovered(mouseX, mouseY) && mouseButton == 0;
    }

    @Override
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

    public String getText() {
        return text.equals("") ? "null" : text;
    }

    @Override
    public void setWindow(Window window) {
        this.window = window;
        relativeX = x;
        relativeY = y;
    }

    @Override
    public void setX(double x) {
        relativeX = x;
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