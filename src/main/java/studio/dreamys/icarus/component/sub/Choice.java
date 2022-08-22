package studio.dreamys.icarus.component.sub;

import studio.dreamys.icarus.component.Component;
import studio.dreamys.icarus.component.Window;
import studio.dreamys.icarus.util.RenderUtils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class Choice extends Component {
    private Window window;

    private double x;
    private double y;
    private double width = 80;
    private double height = 12;
    private String label;

    private boolean open;
    private ArrayList<String> options;
    private String selected;

    //relative to window, aka x,y passed in constructor
    private double relativeX;
    private double relativeY;

    public Choice(String label, ArrayList<String> options) {
        this.label = label;

        this.options = options;
        selected = this.options.get(0);
    }

    public Choice(double x, double y, double width, double height, String label, ArrayList<String> options) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.label = label;

        this.options = options;
        selected = this.options.get(0);
    }

    @Override
    public void render(int mouseX, int mouseY) {
        //update position
        x = window.x + relativeX;
        y = window.y + relativeY;

        //the component itself + the chosen option
        RenderUtils.drawRect(x, y, x + width, y + height, Color.DARK_GRAY.darker().darker());
        RenderUtils.drawString(selected, x + 4, y + height / 10, Color.WHITE);

        //dropdown symbol
        RenderUtils.drawString("v", x + width - 8, y, Color.WHITE);

        //label
        RenderUtils.drawString(label, x, y - height / 1.5, Color.WHITE);

        //lambda stacking stuff
        AtomicReference<Double> currentY = new AtomicReference<>(y);
        currentY.updateAndGet(v -> v + height);

        //open dropdown menu
        if (open) {
            options.forEach(option -> {
                Color color = option.equals(selected) ? window.color : Color.WHITE;
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

    @Override
    public boolean open() {
        return open;
    }

    @Override
    public void setWindow(Window window) {
        this.window = window;
        relativeX = x;
        relativeY = y;
    }

    @Override
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

    @Override
    public String getLabel() {
        return label;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }
}