package studio.dreamys.icarus.ui.popup;

import studio.dreamys.icarus.render.Canvas;
import studio.dreamys.icarus.theme.Theme;
import studio.dreamys.icarus.ui.widget.Widget;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

/**
 * A small right-click menu (e.g. a keybind's modes, or Copy/Paste on a colour). Opens where it was clicked;
 * picking an item runs the action and closes it.
 */
public final class ContextPopup extends Popup {
    private final List<String> items;
    private final IntSupplier selected;
    private final IntConsumer action;
    /** Where it was opened (design units); the theme anchors the popup here. */
    public double anchorX, anchorY;
    /** Row height, set by the theme. */
    public double itemHeight;
    private int hovered = -1;

    /**
     * @param selected index shown as current (e.g. the active mode), or a supplier returning -1 for none
     * @param action   receives the picked index
     */
    public ContextPopup(Widget owner, List<String> items, IntSupplier selected, IntConsumer action) {
        super(owner);
        this.items = Collections.unmodifiableList(items);
        this.selected = selected;
        this.action = action;
    }

    public ContextPopup(Widget owner, IntConsumer action, String... items) {
        this(owner, Arrays.asList(items), () -> -1, action);
    }

    public List<String> getItems() {
        return items;
    }

    /** The current item, or -1. */
    public int getSelected() {
        return selected.getAsInt();
    }

    public int getHovered() {
        return hovered;
    }

    private int indexAt(double y) {
        if (itemHeight <= 0) return -1;
        int index = (int) Math.floor((y - bounds.y) / itemHeight);
        return index >= 0 && index < items.size() ? index : -1;
    }

    @Override
    public void hover(double x, double y) {
        hovered = contains(x, y) ? indexAt(y) : -1;
    }

    @Override
    public boolean mousePressed(double x, double y, int button) {
        int index = indexAt(y);
        if (index < 0) return false;
        owner.getMenu().closePopup();
        action.accept(index);
        owner.getMenu().interactionEnded();
        return false;
    }

    @Override
    public void layout(Theme theme, Canvas canvas) {
        theme.layoutContext(canvas, this);
    }

    @Override
    public void paint(Theme theme, Canvas canvas) {
        theme.paintContext(canvas, this);
    }
}
