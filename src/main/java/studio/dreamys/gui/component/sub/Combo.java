package studio.dreamys.gui.component.sub;

import net.minecraft.client.gui.Gui;
import studio.dreamys.gui.component.Component;
import studio.dreamys.gui.component.Window;
import studio.dreamys.gui.util.RenderUtils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Combo extends Component {
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

    private boolean open;
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

    public void render(int mouseX, int mouseY) {
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
        if (open) {
            options.forEach((option, active) -> {
                Color color = active ? this.color : Color.WHITE;
                Gui.drawRect((int) x, (int) currentY.get().doubleValue(), (int) (x + width), (int) (currentY.get() + height), Color.DARK_GRAY.darker().darker().getRGB());
                RenderUtils.drawScaledString(option, (int) x + 4, (int) (currentY.get() + height / 3), 0.5f,  color);
                currentY.updateAndGet(v -> v + height);
            });
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (open && mouseButton == 0) {
            if (mouseX > x && mouseX < x + width && mouseY > y + height && mouseY < y + height * (options.size() + 1)) {
                double posY = mouseY - y - height;
                int index = (int) (posY / height);
                int i = 0;
                for (Map.Entry<String, Boolean> entry : options.entrySet()) {
                    if (i == index) {
                        options.put(entry.getKey(), !entry.getValue());
                    }
                    i++;
                }
            }
        }

        if (hovered(mouseX, mouseY) && mouseButton == 0) {
            toggle();
        }
    }

    private boolean hovered(double x, double y) {
        return x > this.x && x < this.x + width && y > this.y && y < this.y + height;
    }

    //display active options
    private String activeOptions() {
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

    private void toggle() {
        open = !open;
    }
}
