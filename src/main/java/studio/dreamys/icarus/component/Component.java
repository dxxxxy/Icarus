package studio.dreamys.icarus.component;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import studio.dreamys.icarus.component.sub.*;
import studio.dreamys.icarus.event.ComponentEvent;
import studio.dreamys.icarus.util.Bounds;

//keep this not abstract to avoid some useless empty methods
public class Component {
    public void render(int mouseX, int mouseY) {

    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {

    }

    public void keyTyped(char typedChar, int keyCode) {

    }

    public void mouseReleased(int mouseX, int mouseY, int state) {

    }

    public void setWindow(Window window) {

    }

    public void setGroup(Group group) {

    }

    public void setX(double x) {

    }

    public void setY(double y) {

    }

    public Group getGroup() {
        return null;
    }

    public Window getWindow() {
        return null;
    }

    public double getX() {
        return 0;
    }

    public double getY() {
        return 0;
    }

    public boolean isOpen() {
        return false;
    }

    public double getWidth() {
        return 0;
    }

    public double getHeight() {
        return 0;
    }

    public Bounds getBounds() {
        return null;
    }

    public String getLabel() {
        return "null";
    }

    public void fireChange() {
        Event event = null;
        if (this instanceof Checkbox) event = new ComponentEvent.CheckboxEvent((Checkbox) this);
        if (this instanceof Choice) event = new ComponentEvent.ChoiceEvent((Choice) this);
        if (this instanceof Combo) event = new ComponentEvent.ComboEvent((Combo) this);
        if (this instanceof Field) event = new ComponentEvent.FieldEvent((Field) this);
        if (this instanceof Keybind) event = new ComponentEvent.KeybindEvent((Keybind) this);
        if (this instanceof Slider) event = new ComponentEvent.SliderEvent((Slider) this);
        if (event != null) MinecraftForge.EVENT_BUS.post(event);
    }
}