package studio.dreamys.icarus.component.sub;

import lombok.SneakyThrows;
import net.minecraftforge.common.MinecraftForge;
import studio.dreamys.icarus.component.Component;
import studio.dreamys.icarus.config.Config;
import studio.dreamys.icarus.event.ComponentEvent;
import studio.dreamys.icarus.util.RenderUtils;
import studio.dreamys.icarus.util.position.Bounds;

import java.awt.*;
import java.util.List;

public class Choice extends Component {
    protected List<String> options;

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
        RenderUtils.drawRect(x, y, width, height, Color.DARK_GRAY.darker().darker());

        //selected
        RenderUtils.drawYCenterString(getSelected(), x + 4, y + height / 2 - 1, Color.WHITE);

        //dropdown symbol
        RenderUtils.drawYCenterString("v", x + width - 8, y + height / 2 - 1, Color.WHITE);

        //label
        RenderUtils.drawString(label, x - 1, y - height / 2 - 2, Color.WHITE);

        //open dropdown menu
        if (open) {
            options.forEach(option -> {
                Color color = option.equals(getSelected()) ? window.color : Color.WHITE;

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

                setSelected(options.get(index));

                Config.save();
                MinecraftForge.EVENT_BUS.post(new ComponentEvent.ChoiceEvent());
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