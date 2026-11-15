package studio.dreamys.icarus.ui;

import studio.dreamys.icarus.settings.PageNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A top-level tab: a settings page, or the built-in config page. */
public final class TabView {
    private final PageNode page;
    private final String label;
    private final char icon;
    final List<SubTabView> subTabs = new ArrayList<>();
    private final List<SubTabView> subTabsView = Collections.unmodifiableList(subTabs);
    /** Tab button, laid out by the theme. */
    public final Rect bounds = new Rect();

    TabView(PageNode page, String label, char icon) {
        this.page = page;
        this.label = label;
        this.icon = icon;
    }

    /** The settings page, or null for the built-in config page. */
    public PageNode getPage() {
        return page;
    }

    public String getLabel() {
        return label;
    }

    public char getIcon() {
        return icon;
    }

    public List<SubTabView> getSubTabs() {
        return subTabsView;
    }

    /** Whether the theme should show a sub-tab row. */
    public boolean hasSubTabs() {
        return page != null && page.hasSubTabs();
    }
}
