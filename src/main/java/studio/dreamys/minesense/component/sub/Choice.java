package studio.dreamys.minesense.component.sub;

import studio.dreamys.minesense.component.Component;
import studio.dreamys.minesense.component.Window;
import studio.dreamys.minesense.util.RenderUtils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class Choice extends Component {
    private Window window;
    private double width;
    private double height;
    private double x;
    private double y;
    private String label;

    //relative to window, aka x,y passed in constructor
    private double relativeX;
    private double relativeY;

    private boolean open;
    private ArrayList<String> options;
    private String selected;

    public Choice(double width, double height, double x, double y, String label, ArrayList<String> options) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.label = label;

        this.options = options;
        selected = this.options.get(0);
    }

    public void render(int mouseX, int mouseY) {
        //update position
        x = window.x + relativeX;
        y = window.y + relativeY;

        //the component itself + the chosen option
        RenderUtils.drawRect(x, y, x + width, y + height, Color.DARK_GRAY.darker().darker());
        RenderUtils.drawScaledString(selected, x + 4, y + height / 3, 0.5f,  Color.WHITE);

        //dropdown symbol
        RenderUtils.drawScaledString("v", x + width - 8, y + height / 3.5, 0.5f,  Color.WHITE);

        //label
        RenderUtils.drawScaledString(label, x, y - height / 1.75, 0.5f,  Color.WHITE);

        //lambda stacking stuff
        AtomicReference<Double> currentY = new AtomicReference<>(y);
        currentY.updateAndGet(v -> v + height);

        //open dropdown menu
        if (open) {
            options.forEach(option -> {
                Color color = option.equals(selected) ? window.color : Color.WHITE;
                RenderUtils.drawRect(x, currentY.get(), x + width, currentY.get() + height, Color.DARK_GRAY.darker().darker());
                RenderUtils.drawScaledString(option, x + 4, currentY.get() + height / 3, 0.5f,  color);
                currentY.updateAndGet(v -> v + height);
            });
        }
    }


    @SuppressWarnings("ConstantConditions")
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (open && mouseButton == 0) {
            if (mouseX > x && mouseX < x + width && mouseY > y + height && mouseY < y + height * (options.size() + 1)) {
                double posY = mouseY - y - height;
                int index = (int) (posY / height);
                selected = options.get(index);
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

    public boolean open() {
        return open;
    }

    public void setWindow(Window window) {
        this.window = window;
        relativeX = x;
        relativeY = y;
    }
}