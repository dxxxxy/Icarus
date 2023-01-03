package studio.dreamys.icarus.component.sub;

import lombok.SneakyThrows;
import studio.dreamys.icarus.component.Component;
import studio.dreamys.icarus.util.RenderUtils;

import java.awt.*;

public class Button extends Component {
    private boolean held;

    public Button(String label) {
        super(label, 80, 12);
    }

    @SneakyThrows
    public Runnable getRunnable() {
        return (Runnable) configField.get(null);
    }

    @Override
    public void render(int mouseX, int mouseY) {
        //update position
        super.render(mouseX, mouseY);

        //background
        RenderUtils.drawGradientRect(x, y, x + width, y + height, held ? Color.DARK_GRAY.darker().darker() : Color.DARK_GRAY.darker(), held ? Color.DARK_GRAY.darker() : Color.DARK_GRAY.darker().darker());

        //border
        RenderUtils.drawOutline(width, height, x, y, Color.DARK_GRAY);

        //label
        RenderUtils.drawCenteredString(label, x + width / 2, y + height / 5, Color.WHITE);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (hovered(mouseX, mouseY) && mouseButton == 0) {
            getRunnable().run();
            held = true;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        held = false;
    }
}