package studio.dreamys.icarus.ui.popup;

import studio.dreamys.icarus.render.Canvas;
import studio.dreamys.icarus.theme.Theme;
import studio.dreamys.icarus.ui.Rect;
import studio.dreamys.icarus.ui.widget.Widget;

/**
 * A floating panel opened by a widget (dropdown list, colour picker). At most one is open per menu; it's drawn
 * after everything else and gets mouse input first.
 */
public abstract class Popup {
    protected final Widget owner;
    /** Laid out by the theme. */
    public final Rect bounds = new Rect();
    private float open;

    protected Popup(Widget owner) {
        this.owner = owner;
    }

    public Widget getOwner() {
        return owner;
    }

    /** Opening animation, 0..1. */
    public float getOpen() {
        return open;
    }

    public void resetAnimation() {
        open = 0;
    }

    public void animate(float dt) {
        open = Math.min(1, open + dt * 12);
    }

    public boolean contains(double x, double y) {
        return bounds.contains(x, y);
    }

    /** Returns true to capture the mouse until release. */
    public boolean mousePressed(double x, double y, int button) {
        return false;
    }

    public void mouseDragged(double x, double y) {
    }

    public void mouseReleased(double x, double y, int button) {
    }

    public void scroll(double amount) {
    }

    /** Updates hover state from the mouse position (before painting). */
    public void hover(double x, double y) {
    }

    public abstract void layout(Theme theme, Canvas canvas);

    public abstract void paint(Theme theme, Canvas canvas);
}
