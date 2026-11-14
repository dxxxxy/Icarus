package studio.dreamys.icarus.ui.widget;

import studio.dreamys.icarus.render.Canvas;
import studio.dreamys.icarus.settings.ListModel;
import studio.dreamys.icarus.settings.Setting;
import studio.dreamys.icarus.theme.Theme;
import studio.dreamys.icarus.ui.Menu;
import studio.dreamys.icarus.ui.Rect;

import java.util.List;

/** A scrollable list box; clicking an item selects it. */
public final class ListWidget extends Widget {
    private final Setting<String> selection;
    private final ListModel model;
    /** The list box, set during layout. */
    public final Rect box = new Rect();
    /** Row height and visible rows, set during layout. */
    public double itemHeight;
    public int visibleRows;
    private int scroll;
    private int hovered = -1;

    public ListWidget(Menu menu, Setting<String> setting, ListModel model) {
        super(menu, setting);
        this.selection = setting;
        this.model = model;
    }

    public List<String> getItems() {
        return model.getItems();
    }

    public String getSelected() {
        return selection.get();
    }

    public int getScroll() {
        return scroll;
    }

    public int getHoveredIndex() {
        return hovered;
    }

    private int indexAt(double y) {
        if (itemHeight <= 0) return -1;
        int row = (int) Math.floor((y - box.y) / itemHeight);
        int index = scroll + row;
        return row >= 0 && row < visibleRows && index < getItems().size() ? index : -1;
    }

    /** Updates the hovered row (called by the menu every frame). */
    public void hover(double x, double y) {
        hovered = box.contains(x, y) ? indexAt(y) : -1;
    }

    @Override
    public boolean contains(double x, double y) {
        return box.contains(x, y);
    }

    @Override
    public boolean mousePressed(double x, double y, int button) {
        if (button != 0) return false;
        int index = indexAt(y);
        if (index >= 0) {
            selection.set(getItems().get(index));
            menu.interactionEnded();
        }
        return false;
    }

    @Override
    public boolean scroll(double amount) {
        int max = Math.max(0, getItems().size() - visibleRows);
        if (max == 0) return false;
        scroll = Math.max(0, Math.min(max, scroll - (int) Math.signum(amount)));
        return true;
    }

    @Override
    public double layout(Theme theme, Canvas canvas, double x, double y, double width) {
        double height = theme.layoutList(canvas, this, x, y, width);
        scroll = Math.max(0, Math.min(Math.max(0, getItems().size() - visibleRows), scroll));
        return height;
    }

    @Override
    public void paint(Theme theme, Canvas canvas) {
        theme.paintList(canvas, this);
    }
}
