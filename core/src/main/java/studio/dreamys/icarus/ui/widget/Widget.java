package studio.dreamys.icarus.ui.widget;

import studio.dreamys.icarus.render.Canvas;
import studio.dreamys.icarus.settings.Setting;
import studio.dreamys.icarus.theme.Theme;
import studio.dreamys.icarus.ui.GroupView;
import studio.dreamys.icarus.ui.Menu;
import studio.dreamys.icarus.ui.Rect;

/**
 * A control bound to a setting. Widgets hold behaviour and state only; the active {@link Theme} lays them out
 * (filling {@link #bounds} and any part rectangles) and paints them, so swapping themes never touches widget code.
 */
public abstract class Widget {
    protected final Menu menu;
    protected final Setting<?> setting;
    /** The widget's whole row, set during layout. */
    public final Rect bounds = new Rect();

    private KeybindWidget keybind;
    private GroupView group;
    private boolean hovered;
    private float hover;

    protected Widget(Menu menu, Setting<?> setting) {
        this.menu = menu;
        this.setting = setting;
    }

    public Menu getMenu() {
        return menu;
    }

    public Setting<?> getSetting() {
        return setting;
    }

    public String getLabel() {
        return setting.getLabel();
    }

    public GroupView getGroup() {
        return group;
    }

    public void setGroup(GroupView group) {
        this.group = group;
    }

    /** The keybind shown in this widget's row, or null. */
    public KeybindWidget getKeybind() {
        return keybind;
    }

    public void setKeybind(KeybindWidget keybind) {
        this.keybind = keybind;
    }

    /** Whether the mouse is over this widget (topmost under the cursor). */
    public boolean isHovered() {
        return hovered;
    }

    /** Hover animation, 0 (not hovered) to 1. */
    public float getHover() {
        return hover;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    /** Advances animations by {@code dt} seconds. */
    public void animate(float dt) {
        float target = hovered ? 1 : 0;
        float step = dt * 10;
        hover = hover < target ? Math.min(target, hover + step) : Math.max(target, hover - step);
    }

    /** Whether a point hits the widget's interactive area (by default the whole row). */
    public boolean contains(double x, double y) {
        return bounds.contains(x, y);
    }

    /** Returns true to capture the mouse until it's released (receiving {@link #mouseDragged}). */
    public boolean mousePressed(double x, double y, int button) {
        return false;
    }

    /** Called every frame while the widget has the mouse captured, before layout. */
    public void mouseDragged(double x, double y) {
    }

    public void mouseReleased(double x, double y, int button) {
    }

    /** Keys while focused. Returns whether the key was used. */
    public boolean keyPressed(int key) {
        return false;
    }

    public void charTyped(int codePoint) {
    }

    public void focusLost() {
    }

    /** Wheel over the widget; return true to stop it scrolling the group. */
    public boolean scroll(double amount) {
        return false;
    }

    /** Lays the widget out at the given row origin and width; returns the row height. */
    public abstract double layout(Theme theme, Canvas canvas, double x, double y, double width);

    public abstract void paint(Theme theme, Canvas canvas);
}
