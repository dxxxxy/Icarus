package studio.dreamys.icarus.component.sub;

import lombok.Getter;
import lombok.Setter;
import studio.dreamys.icarus.component.Component;
import studio.dreamys.icarus.component.Window;
import studio.dreamys.icarus.util.Bounds;
import studio.dreamys.icarus.util.RenderUtils;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
public class Slider extends Component {
    private Window window;
    private Group group;

    private double x;
    private double y;
    private double width = 80;
    private double height = 3;
    private String label;

    private double max;
    private double min;
    private boolean onlyInt;
    private double percent;
    private double value;
    private String units;

    //relative to window, aka x,y passed in constructor
    private double relativeX;
    private double relativeY;

    //dragging stuff
    public boolean dragging;

    public Slider(String label, double min, double max, boolean onlyInt) {
        this.label = label;

        value = min;
        this.min = min;
        this.max = max;
        this.onlyInt = onlyInt;
        units = "";
    }

    public Slider(String label, double value, double min, double max, boolean onlyInt) {
        this.label = label;

        this.value = value;
        this.min = min;
        this.max = max;
        this.onlyInt = onlyInt;
        units = "";
    }

    public Slider(String label, double min, double max, boolean onlyInt, String units) {
        this.label = label;

        this.min = min;
        this.max = max;
        this.onlyInt = onlyInt;

        this.units = units;
    }

    @Override
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
        RenderUtils.drawString((onlyInt ? Integer.toString((int) value) : String.valueOf(value)) + units, x - 1 + width * percent, y + height - 1, Color.WHITE);

        update(mouseX);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (hovered(mouseX, mouseY) && mouseButton == 0) {
            dragging = true;
        }
    }

    @Override
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
            fireChange();
        }
    }

    public void setValue(double value) {
        this.value = value;
        percent = (value - min) / (max - min);
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
    public Bounds getBounds() {
        return new Bounds(width, height + 5, 7);
    }
}
