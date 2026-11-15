package studio.dreamys.icarus.ui.popup;

import studio.dreamys.icarus.render.Canvas;
import studio.dreamys.icarus.theme.Theme;
import studio.dreamys.icarus.ui.widget.Widget;

import java.util.List;

/** The option list of a Choice (single selection, closes on pick) or Combo (multi selection, stays open). */
public final class DropdownPopup extends Popup {
    /** What the list shows and how picks are applied. */
    public interface Source {
        List<String> getOptions();

        boolean isSelected(int index);

        /** Applies a pick; returns true if the popup should close. */
        boolean choose(int index);
    }

    private final Source source;
    /** Row height and visible rows, set by the theme. */
    public double itemHeight;
    /** Space above the first option, set by the theme. */
    public double padTop;
    public int visibleRows;
    private int scroll;
    private int hovered = -1;

    public DropdownPopup(Widget owner, Source source) {
        super(owner);
        this.source = source;
    }

    public Source getSource() {
        return source;
    }

    public List<String> getOptions() {
        return source.getOptions();
    }

    /** Index of the first visible option. */
    public int getScroll() {
        return scroll;
    }

    public int getHovered() {
        return hovered;
    }

    private int indexAt(double y) {
        if (itemHeight <= 0) return -1;
        int row = (int) Math.floor((y - bounds.y - padTop) / itemHeight);
        int index = scroll + row;
        return row >= 0 && row < visibleRows && index < getOptions().size() ? index : -1;
    }

    @Override
    public void hover(double x, double y) {
        hovered = contains(x, y) ? indexAt(y) : -1;
    }

    @Override
    public boolean mousePressed(double x, double y, int button) {
        if (button != 0) return false;
        int index = indexAt(y);
        if (index >= 0 && source.choose(index)) owner.getMenu().closePopup();
        return false;
    }

    @Override
    public void scroll(double amount) {
        int max = Math.max(0, getOptions().size() - visibleRows);
        scroll = Math.max(0, Math.min(max, scroll - (int) Math.signum(amount)));
    }

    @Override
    public void layout(Theme theme, Canvas canvas) {
        theme.layoutDropdown(canvas, this);
        scroll = Math.max(0, Math.min(Math.max(0, getOptions().size() - visibleRows), scroll));
    }

    @Override
    public void paint(Theme theme, Canvas canvas) {
        theme.paintDropdown(canvas, this);
    }
}
