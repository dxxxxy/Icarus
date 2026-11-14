package studio.dreamys.icarus.ui;

import studio.dreamys.icarus.settings.GroupNode;
import studio.dreamys.icarus.ui.widget.Widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A group box on screen: its widgets plus the layout and scroll state the menu keeps for it. */
public final class GroupView {
    private final GroupNode group;
    private final List<Widget> widgets = new ArrayList<>();
    private final List<Widget> widgetsView = Collections.unmodifiableList(widgets);

    /** The whole box, as laid out by the theme. */
    public final Rect frame = new Rect();
    /** The scrollable, clipped area holding the rows. */
    public final Rect content = new Rect();

    double scroll;
    double contentHeight;

    GroupView(GroupNode group) {
        this.group = group;
    }

    public GroupNode getGroup() {
        return group;
    }

    public String getLabel() {
        return group.getLabel();
    }

    public List<Widget> getWidgets() {
        return widgetsView;
    }

    void add(Widget widget) {
        widgets.add(widget);
    }

    /** Scroll offset in design units (0 = top). */
    public double getScroll() {
        return scroll;
    }

    /** Height of all rows. */
    public double getContentHeight() {
        return contentHeight;
    }

    public boolean isScrollable() {
        return contentHeight > content.h + 0.5;
    }

    void scrollBy(double amount) {
        scroll = Math.max(0, Math.min(Math.max(0, contentHeight - content.h), scroll + amount));
    }
}
