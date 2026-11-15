package studio.dreamys.icarus.settings;

import studio.dreamys.icarus.annotation.type.ISubPage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A sub-tab of a page, or the implicit single sub-page of a page without sub-tabs. */
public final class SubPageNode {
    private final PageNode page;
    private final Class<?> type;
    private final String name;
    private final int order;
    private final char icon;
    final List<GroupNode> groups = new ArrayList<>();

    SubPageNode(PageNode page, Class<?> type, String name, int order) {
        this.page = page;
        this.type = type;
        this.name = name;
        this.order = order;
        ISubPage annotation = type == null ? null : type.getAnnotation(ISubPage.class);
        this.icon = annotation == null ? ' ' : annotation.icon();
    }

    /** Icon from {@code @ISubPage(icon)}, or a space. */
    public char getIcon() {
        return icon;
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
        return name == null ? page.getLabel() : Labels.of(type, name);
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
