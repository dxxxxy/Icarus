package studio.dreamys.icarus.component.sub;

import lombok.SneakyThrows;
import net.minecraftforge.common.MinecraftForge;
import studio.dreamys.icarus.component.Component;
import studio.dreamys.icarus.config.Config;
import studio.dreamys.icarus.event.ComponentEvent;
import studio.dreamys.icarus.util.RenderUtils;
import studio.dreamys.icarus.util.position.Bounds;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Choice extends Component {
    private List<String> options;

    public Choice(String label, List<String> options) {
        super(label, 80, 12);

        this.options = options;
    }

    @SneakyThrows
    public String getSelected() {
        return (String) configField.get(null);
    }

    @SneakyThrows
    private void setSelected(String selected) {
        configField.set(null, selected);
    }

    @Override
    public void render(int mouseX, int mouseY) {
        //update position
        super.render(mouseX, mouseY);

        //background
        RenderUtils.drawRect(x, y, x + width, y + height, Color.DARK_GRAY.darker().darker());

        //selected
        RenderUtils.drawString(getSelected(), x + 4, y + height / 10, Color.WHITE);

        //dropdown symbol
        RenderUtils.drawString("v", x + width - 8, y, Color.WHITE);

        //label
        RenderUtils.drawString(label, x, y - height / 1.5, Color.WHITE);

        //dropdown height
        AtomicReference<Double> currentY = new AtomicReference<>(y);
        currentY.updateAndGet(v -> v + height);

        //open dropdown menu
        if (open) {
            options.forEach(option -> {
                Color color = option.equals(getSelected()) ? window.color : Color.WHITE;

                //background
                RenderUtils.drawRect(x, currentY.get(), x + width, currentY.get() + height, Color.DARK_GRAY.darker().darker());

                //option
                RenderUtils.drawString(option, x + 4, currentY.get() + height / 10,  color);

                //update y
                currentY.updateAndGet(v -> v + height);
            });
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (open && mouseButton == 0) {
            if (mouseX > x && mouseX < x + width && mouseY > y + height && mouseY < y + height * (options.size() + 1)) {
                double posY = mouseY - y - height;
                int index = (int) (posY / height);

                setSelected(options.get(index));

                Config.save();
                MinecraftForge.EVENT_BUS.post(new ComponentEvent.ChoiceEvent(this));
            } else open = false;
            return;
        }

        if (hovered(mouseX, mouseY) && mouseButton == 0) {
            open = !open;
        }
    }

    @Override
    public Bounds getBounds() {
        return new Bounds(width, height, 7);
    }
}