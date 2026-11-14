package studio.dreamys.icarus.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A menu page (tab). Always has at least one sub-page; pages without {@code @ISubPage} get one implicit sub-page. */
public final class PageNode {
    private final Class<?> type;
    private final String name;
    private final char icon;
    private final int order;
    final List<SubPageNode> subPages = new ArrayList<>();

    PageNode(Class<?> type, String name, char icon, int order) {
        this.type = type;
        this.name = name;
        this.icon = icon;
        this.order = order;
    }

    /** The annotated class, or null for a page created in code. */
    public Class<?> getType() {
        return type;
    }

    /** Config key: the class's simple name. */
    public String getName() {
        return name;
    }

    public String getLabel() {
        return name.replace('_', ' ');
    }

    public char getIcon() {
        return icon;
    }

    public int getOrder() {
        return order;
    }

    public List<SubPageNode> getSubPages() {
        return Collections.unmodifiableList(subPages);
    }

    /** Whether this page shows a sub-tab row (it declared {@code @ISubPage} classes). */
    public boolean hasSubTabs() {
        return !(subPages.size() == 1 && subPages.get(0).isImplicit());
    }
}
