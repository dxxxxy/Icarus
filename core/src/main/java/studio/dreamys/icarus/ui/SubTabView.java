package studio.dreamys.icarus.ui;

import studio.dreamys.icarus.settings.SubPageNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A sub-tab (or the single implicit one) with the groups shown under it. */
public final class SubTabView {
    private final SubPageNode subPage;
    private final String label;
    final List<GroupView> groups = new ArrayList<>();
    private final List<GroupView> groupsView = Collections.unmodifiableList(groups);
    /** Sub-tab button, laid out by the theme (empty when the page has no sub-tabs). */
    public final Rect bounds = new Rect();

    SubTabView(SubPageNode subPage, String label) {
        this.subPage = subPage;
        this.label = label;
    }

    /** The model sub-page, or null for the config page. */
    public SubPageNode getSubPage() {
        return subPage;
    }

    public String getLabel() {
        return label;
    }

    public List<GroupView> getGroups() {
        return groupsView;
    }
}
