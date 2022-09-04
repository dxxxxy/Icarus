package studio.dreamys.icarus.component.sub;

import lombok.Getter;
import lombok.Setter;
import net.minecraftforge.common.MinecraftForge;
import studio.dreamys.icarus.component.Component;
import studio.dreamys.icarus.component.Window;
import studio.dreamys.icarus.event.ComponentStateChangeEvent;
import studio.dreamys.icarus.util.RenderUtils;

import java.awt.Color;

@Getter
@Setter
public class Checkbox extends Component {
    private Window window;
    private Group group;

    private double x;
    private double y;
    private double width = 5;
    private double height = 5;
    private String label;

    private boolean toggled;

    //relative to window, aka x,y passed in constructor
    private double relativeX;
    private double relativeY;

    public Checkbox(String label) {
        this.label = label;
    }

    public Checkbox(String label, boolean toggled) {
        this.label = label;

        this.toggled = toggled;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        //update position
        x = window.x + relativeX;
        y = window.y + relativeY;

        Color color = toggled ? window.color : Color.DARK_GRAY;

        //the box itself + label next to it
        RenderUtils.drawGradientRect(x, y, x + width, y + height, color, color.darker().darker());
        RenderUtils.drawString(label, x + width * 2 - 1, y - height / 2 + 0.75,  Color.WHITE);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (hovered(mouseX, mouseY) && mouseButton == 0) {
            toggled = !toggled;
            MinecraftForge.EVENT_BUS.post(new ComponentStateChangeEvent(this));
        }
    }

    private boolean hovered(double x, double y) {
        return x > this.x && x < this.x + width && y > this.y && y < this.y + height;
    }

    public void toggle() {
        toggled = !toggled;
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
}