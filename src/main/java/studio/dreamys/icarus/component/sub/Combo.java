package studio.dreamys.icarus.component.sub;

import lombok.SneakyThrows;
import net.minecraftforge.common.MinecraftForge;
import studio.dreamys.icarus.component.Component;
import studio.dreamys.icarus.config.Config;
import studio.dreamys.icarus.event.ComponentEvent;
import studio.dreamys.icarus.util.RenderUtils;
import studio.dreamys.icarus.util.position.Bounds;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Combo extends Component {
    protected List<String> options;

    public Combo(String label, List<String> options) {
        super(label, 80, 12);

        this.options = options;
    }

    @SneakyThrows
    public List<String> getActive() {
        return new ArrayList<>(Arrays.asList((String[]) configField.get(null)));
    }

    @SneakyThrows
    private void setActive(List<String> active) {
        configField.set(null, active.toArray(new String[0]));
    }

    @Override
    public void render(int mouseX, int mouseY) {
        //update position
        super.render(mouseX, mouseY);

        //background
        RenderUtils.drawRect(x, y, width, height, Color.DARK_GRAY.darker().darker());

        //selected
        RenderUtils.drawYCenterString(activeOptions(), x + 4, y + height / 2 - 1, Color.WHITE);

        //dropdown symbol
        RenderUtils.drawYCenterString("v", x + width - 8, y + height / 2 - 1, Color.WHITE);

        //label
        RenderUtils.drawString(label, x - 1, y - height / 2 - 2, Color.WHITE);

        //open dropdown menu
        if (open) {
            options.forEach(option -> {
                Color color = getActive().contains(option) ? window.color : Color.WHITE;

                //background
                RenderUtils.drawRect(x, y + height * (options.indexOf(option) + 1), width, height, Color.DARK_GRAY.darker().darker());

                //option
                RenderUtils.drawYCenterString(option, x + 4, y + height * (options.indexOf(option) + 1) + height / 2 - 1, color);
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

                setActive(active);

                Config.save();
                MinecraftForge.EVENT_BUS.post(new ComponentEvent.ComboEvent());
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
}