package studio.dreamys.gui.component.sub;

import studio.dreamys.gui.component.Component;
import studio.dreamys.gui.component.Window;
import studio.dreamys.gui.util.RenderUtils;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Slider extends Component {
    private Window window;
    private double width;
    private double height;
    private double x;
    private double y;
    private Color color;
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

    public Slider(Window window, double width, double height, double x, double y, Color color, String label, double max, double min, boolean onlyInt) {
        this.window = window;
        this.width = width;
        this.height = height;
        this.x = window.x + x;
        this.y = window.y + y;
        relativeX = x;
        relativeY = y;
        this.color = color;
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
        RenderUtils.drawGradientRect((int) x, (int) y, (int) (x + width), (int) (y + height), Color.DARK_GRAY.getRGB(), Color.DARK_GRAY.darker().darker().getRGB());

        //filled
        RenderUtils.drawGradientRect((int) x, (int) y, (int) (x + width * percent), (int) (y + height), color.getRGB(), color.darker().darker().getRGB());

        //label
        RenderUtils.drawScaledString(label, (int) x, (int) (y - height * 2), 0.5f,  Color.WHITE);

        //value
        RenderUtils.drawScaledString(String.valueOf(value), (int) (x + width * percent), (int) (y + height), 0.5f,  Color.WHITE);

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
}
