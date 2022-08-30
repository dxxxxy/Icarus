package studio.dreamys.icarus.component.sub;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import studio.dreamys.icarus.component.Component;
import studio.dreamys.icarus.component.Window;
import studio.dreamys.icarus.util.RenderUtils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class Combo extends Component {
    private Window window;

    private double x;
    private double y;
    private double width = 80;
    private double height = 12;
    private String label;

    private boolean open;
    private HashMap<String, Boolean> options = new HashMap<>();

    //relative to window, aka x,y passed in constructor
    private double relativeX;
    private double relativeY;

    public Combo(String label, ArrayList<String> options) {
        this.label = label;

        options.forEach(option -> this.options.put(option, false));
    }

    @Override
    public void render(int mouseX, int mouseY) {
        //update position
        x = window.x + relativeX;
        y = window.y + relativeY;

        //the component itself + the active options displayed
        RenderUtils.drawRect(x, y, x + width, y + height, Color.DARK_GRAY.darker().darker());
        RenderUtils.drawString(activeOptions(), x + 4, y + height / 10,  Color.WHITE);

        //dropdown symbol
        RenderUtils.drawString("v", x + width - 8, y, Color.WHITE);

        //label
        RenderUtils.drawString(label, x, y - height / 1.5, Color.WHITE);

        //lambda stacking stuff
        AtomicReference<Double> currentY = new AtomicReference<>(y);
        currentY.updateAndGet(v -> v + height);

        //open dropdown menu
        if (open) {
            options.forEach((option, active) -> {
                Color color = active ? window.color : Color.WHITE;
                RenderUtils.drawRect(x, currentY.get(), x + width, currentY.get() + height, Color.DARK_GRAY.darker().darker());
                RenderUtils.drawString(option, x + 4, currentY.get() + height / 10,  color);
                currentY.updateAndGet(v -> v + height);
            });
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
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
            } else open = !open;
            return;
        }

        if (hovered(mouseX, mouseY) && mouseButton == 0) {
            open = !open;
        }
    }

    private boolean hovered(double x, double y) {
        return x > this.x && x < this.x + width && y > this.y && y < this.y + height;
    }

    //display active options
    private String activeOptions() {
        StringBuilder formatted = new StringBuilder();
        for (Map.Entry<String, Boolean> option : options.entrySet()) {
            if (option.getValue()) {
                formatted.append(option.getKey()).append(", ");
            }
        }

        //remove last comma
        if (formatted.length() > 0) {
            formatted.delete(formatted.length() - 2, formatted.length() - 1);
        }

        //if active string reaches the "v" dropdown symbol, replace it by three little dots
        if (x + RenderUtils.getStringWidth(formatted.toString()) > (x + width - 10)) formatted = new StringBuilder("...");

        //if none active
        if (formatted.toString().equals("")) formatted = new StringBuilder("-");

        return formatted.toString();
    }

    public String getActiveOptions() {
        StringBuilder formatted = new StringBuilder();
        for (Map.Entry<String, Boolean> option : options.entrySet()) {
            if (option.getValue()) {
                formatted.append(option.getKey()).append(",");
            }
        }

        //remove last comma
        if (formatted.length() > 0) {
            formatted.deleteCharAt(formatted.length() - 1);
        }

        return formatted.length() > 0 ? formatted.toString() : "null";
    }

    public void setActiveOptions(String activeOptions) {
        if (activeOptions.equals("null")) return;
        String[] options = activeOptions.split(",");
        for (String option : options) {
            this.options.put(option, true);
        }
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