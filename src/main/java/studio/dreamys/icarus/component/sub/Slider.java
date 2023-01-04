package studio.dreamys.icarus.component.sub;

import lombok.SneakyThrows;
import net.minecraftforge.common.MinecraftForge;
import studio.dreamys.icarus.component.Component;
import studio.dreamys.icarus.config.Config;
import studio.dreamys.icarus.event.ComponentEvent;
import studio.dreamys.icarus.util.RenderUtils;
import studio.dreamys.icarus.util.position.Bounds;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Slider extends Component {
    private double max;
    private double min;
    private boolean onlyInt;
    private String units;

    private double percent;

    private boolean dragging;

    public Slider(String label, double min, double max, boolean onlyInt, String units) {
        super(label, 80, 3);

        this.min = min;
        this.max = max;
        this.onlyInt = onlyInt;
        this.units = units;
    }

    @SneakyThrows
    public double getValue() {
        double value =  configField.getDouble(null);
        percent = (value - min) / (max - min);
        return value;
    }

    @SneakyThrows
    private void setValue(double value) {
        configField.set(null, value);
    }

    @Override
    public void render(int mouseX, int mouseY) {
        //update position
        super.render(mouseX, mouseY);

        //unfilled background
        RenderUtils.drawGradientRect(x, y, width, height, Color.DARK_GRAY, Color.DARK_GRAY.darker().darker());

        //filled background
        RenderUtils.drawGradientRect(x, y, width * percent, height, window.color, window.color.darker().darker());

        //label
        RenderUtils.drawString(label, x - 1, y - height * 2 - 2, Color.WHITE);

        //value
        RenderUtils.drawXCenterString((onlyInt ? Integer.toString((int) getValue()) : String.valueOf(getValue())) + units,  x + width * percent, y + height - 1, Color.WHITE);

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

    @Override
    public Bounds getBounds() {
        return new Bounds(width, height + 5, 7);
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

            //set and save
            setValue(onlyInt ? Math.round(min + (max - min) * percent) : roundToPlace(min + (max - min) * percent));
            Config.save();
            MinecraftForge.EVENT_BUS.post(new ComponentEvent.SliderEvent());
        }
    }
}