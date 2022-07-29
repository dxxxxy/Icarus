package studio.dreamys.icarus.component.sub;

import studio.dreamys.icarus.component.Component;
import studio.dreamys.icarus.component.Window;
import studio.dreamys.icarus.util.RenderUtils;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Slider extends Component {
    private Window window;
    private Group group;

    private double x;
    private double y;
    private double width = 80;
    private double height = 3;
    private double clearance = 50;

    private String label;
    private double max;
    private double min;
    private boolean onlyInt;

    //relative to window, aka x,y passed in constructor
    private double relativeX;
    private double relativeY;

    private double percent;
    private double value;

    //dragging stuff
    public boolean dragging;

    public Slider(String label, double min, double max, boolean onlyInt) {
        this.label = label;
        this.min = min;
        this.max = max;
        this.onlyInt = onlyInt;
    }

    public Slider(double width, double height, double x, double y, String label, double max, double min, boolean onlyInt) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.label = label;

        this.max = max;
        this.min = min;
        this.onlyInt = onlyInt;
    }

    public void render(int mouseX, int mouseY) {
        //update position
        x = window.x + relativeX;
        y = window.y + relativeY;

        //unfilled
        RenderUtils.drawGradientRect(x, y, x + width, y + height, Color.DARK_GRAY, Color.DARK_GRAY.darker().darker());

        //filled
        RenderUtils.drawGradientRect(x, y, x + width * percent, y + height, window.color, window.color.darker().darker());

        //label
        RenderUtils.drawString(label, x - 1, y - height * 2.5, Color.WHITE);

        //value
        RenderUtils.drawString(onlyInt ? Integer.toString((int) value) : String.valueOf(value), x - 1 + width * percent, y + height - 1, Color.WHITE);

        update(mouseX);
    }


    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (hovered(mouseX, mouseY) && mouseButton == 0) {
            dragging = true;
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        dragging = false;
    }

    private boolean hovered(double x, double y) {
        return x > this.x && x < this.x + width && y > this.y && y < this.y + height;
    }

    private double roundToPlace(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private void update(int mouseX) {
        if (dragging) {
            double value = (mouseX - x) / (width);
            if (value <= 0) value = 0;
            if (value >= 1) value = 1;

            percent = value;

            this.value = onlyInt ? Math.round(min + (max - min) * percent) : roundToPlace(min + (max - min) * percent);
        }
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
        return 15;
    }
}
