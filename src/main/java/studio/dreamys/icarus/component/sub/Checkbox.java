package studio.dreamys.icarus.component.sub;

import lombok.SneakyThrows;
import net.minecraftforge.common.MinecraftForge;
import studio.dreamys.icarus.component.Component;
import studio.dreamys.icarus.config.Config;
import studio.dreamys.icarus.event.ComponentEvent;
import studio.dreamys.icarus.util.RenderUtils;
import studio.dreamys.icarus.util.position.Bounds;

import java.awt.*;

public class Checkbox extends Component {
    public Checkbox(String label) {
        super(label, 5, 5);
    }

    @SneakyThrows
    public boolean isToggled() {
        return configField.getBoolean(null);
    }

    @SneakyThrows
    private void setToggled(boolean toggled) {
        configField.set(null, toggled);
    }

    @Override
    public void render(int mouseX, int mouseY) {
        //update position
        super.render(mouseX, mouseY);

        //toggle color
        Color color = isToggled() ? window.color : Color.DARK_GRAY;

        //background
        RenderUtils.drawGradientRect(x, y, width, height, color, color.darker().darker());

        //label
        RenderUtils.drawString(label, x + width * 2 - 1, y - height / 2 + 0.75, Color.WHITE);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (hovered(mouseX, mouseY) && mouseButton == 0) {
            toggle();
        }
    }

    @Override
    public Bounds getBounds() {
        return new Bounds(80, height);
    }

    public void toggle() {
        setToggled(!isToggled());

        Config.save();
        MinecraftForge.EVENT_BUS.post(new ComponentEvent.CheckboxEvent());
    }
}