package studio.dreamys.icarus.component.sub;

import studio.dreamys.icarus.component.Component;
import studio.dreamys.icarus.util.RenderUtils;
import studio.dreamys.icarus.util.position.Bounds;

import java.awt.*;

public class Checkbox extends Component<Boolean> {
    public Checkbox(String label) {
        //build component
        super(label, 5, 5);
    }

    @Override
    public void render(int mouseX, int mouseY) {
        //update position
        super.render(mouseX, mouseY);

        //toggle color
        Color color = get() ? window.color : Color.DARK_GRAY;

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
        set(!get());
    }
}