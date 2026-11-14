package studio.dreamys.icarus.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A sub-tab of a page, or the implicit single sub-page of a page without sub-tabs. */
public final class SubPageNode {
    private final PageNode page;
    private final Class<?> type;
    private final String name;
    private final int order;
    final List<GroupNode> groups = new ArrayList<>();

    SubPageNode(PageNode page, Class<?> type, String name, int order) {
        this.page = page;
        this.type = type;
        this.name = name;
        this.order = order;
    }

    public PageNode getPage() {
        return page;
    }

    public Class<?> getType() {
        return type;
    }

    /** Config key, or null for the implicit sub-page. */
    public String getName() {
        return name;
    }

    public String getLabel() {
        return name == null ? page.getLabel() : name.replace('_', ' ');
    }

    public int getOrder() {
        return order;
    }

    public boolean isImplicit() {
        return name == null;
    }

    public List<GroupNode> getGroups() {
        return Collections.unmodifiableList(groups);
    }
}
