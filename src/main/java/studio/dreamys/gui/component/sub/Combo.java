package studio.dreamys.gui.component.sub;

import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;
import studio.dreamys.gui.component.Window;
import studio.dreamys.gui.util.RenderUtils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Combo {
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

    private boolean opened;
    private HashMap<String, Boolean> options = new HashMap<>();

    public Combo(Window window, double width, double height, double x, double y, Color color, String label, ArrayList<String> options) {
        this.window = window;
        this.width = width;
        this.height = height;
        this.x = window.x + x;
        this.y = window.y + y;
        relativeX = x;
        relativeY = y;
        this.color = color;
        this.label = label;

        options.forEach(option -> {
            this.options.put(option, false);
        });
    }

    public void render() {
        //update position
        x = window.x + relativeX;
        y = window.y + relativeY;

        //the component itself + the active options displayed
        Gui.drawRect((int) x, (int) y, (int) (x + width), (int) (y + height), Color.DARK_GRAY.darker().darker().getRGB());
        RenderUtils.drawScaledString(activeOptions(), (int) x + 4, (int) (y + height / 3), 0.5f,  Color.WHITE);

        //dropdown symbol
        RenderUtils.drawScaledString("v", (int) (x + width - 8), (int) (y + height / 3.5), 0.5f,  Color.WHITE);

        //label
        RenderUtils.drawScaledString(label, (int) x, (int) (y - height / 1.75), 0.5f,  Color.WHITE);

        //lambda stacking stuff
        AtomicReference<Double> currentY = new AtomicReference<>(y);
        currentY.updateAndGet(v -> v + (int) (height));

        //open dropdown menu
        if (opened) {
            options.forEach((option, active) -> {
                Color color = active ? this.color : Color.WHITE;
                Gui.drawRect((int) x, (int) currentY.get().doubleValue(), (int) (x + width), (int) (currentY.get() + height), Color.DARK_GRAY.darker().darker().getRGB());
                RenderUtils.drawScaledString(option, (int) x + 4, (int) (currentY.get() + height / 3), 0.5f,  color);
                currentY.updateAndGet(v -> v + height);
            });
        }
    }

    public boolean hovered(double x, double y) {
        return x > this.x && x < this.x + width && y > this.y && y < this.y + height;
    }

    //display active options
    public String activeOptions() {
        String formatted = "";
        for (Map.Entry<String, Boolean> option : options.entrySet()) {
            if (option.getValue()) {
                formatted += option.getKey() + ", ";
            }
        }

        //if active string reaches the "v" dropdown symbol, replace it by three little dots
        if (x + RenderUtils.getScaledStringWidth(formatted, 0.5f) > (x + width - 6)) formatted = "...";

        //if none active
        if (formatted.equals("")) formatted = "None";

        return formatted;
    }

    public void open() {
        opened = !opened;
    }

    public Window getWindow() {
        return window;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Color getColor() {
        return color;
    }

    public String getLabel() {
        return label;
    }

    public double getRelativeX() {
        return relativeX;
    }

    public double getRelativeY() {
        return relativeY;
    }

    public boolean isOpened() {
        return opened;
    }

    public HashMap<String, Boolean> getOptions() {
        return options;
    }
}
