package studio.dreamys.icarus.component.sub;

import lombok.SneakyThrows;
import net.minecraftforge.common.MinecraftForge;
import studio.dreamys.icarus.component.Component;
import studio.dreamys.icarus.config.Config;
import studio.dreamys.icarus.event.ComponentEvent;
import studio.dreamys.icarus.util.RenderUtils;
import studio.dreamys.icarus.util.position.Bounds;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Combo extends Component {
    private List<String> options;

    public Combo(String label, List<String> options) {
        super(label, 80, 12);

        this.options = options;
    }

    @SneakyThrows
    public List<String> getActive() {
        return new ArrayList<>(Arrays.asList((String[]) configField.get(null)));
    }

    @SneakyThrows
    public void setActive(List<String> active) {
        configField.set(null, active.toArray(new String[0]));
    }

    @Override
    public void render(int mouseX, int mouseY) {
        //update position
        super.render(mouseX, mouseY);

        //background
        RenderUtils.drawRect(x, y, x + width, y + height, Color.DARK_GRAY.darker().darker());

        //active options
        RenderUtils.drawString(activeOptions(), x + 4, y + height / 10,  Color.WHITE);

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
                boolean active = getActive().contains(option);

                Color color = active ? window.color : Color.WHITE;

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
                int i = 0;

                List<String> active = getActive();

                for (String option : options) {
                    if (i == index) {
                        if (active.contains(option)) active.remove(option);
                        else active.add(option);
                        break;
                    }
                    i++;
                }

                //set and save
                setActive(active);
                Config.save();
                MinecraftForge.EVENT_BUS.post(new ComponentEvent.ComboEvent(this));
            } else open = false;
            return;
        }

        if (hovered(mouseX, mouseY) && mouseButton == 0) {
            open = !open;
        }
    }

    //display active options
    private String activeOptions() {
        StringBuilder formatted = new StringBuilder();
        for (String option : getActive()) {
            formatted.append(option).append(", ");
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

    @Override
    public Bounds getBounds() {
        return new Bounds(width, height, 7);
    }
}