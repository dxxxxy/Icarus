package studio.dreamys.icarus.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** All pages, sub-pages, groups and settings of one mod. */
public final class SettingsModel {
    private final List<PageNode> pages;
    private final List<Setting<?>> settings = new ArrayList<>();

    SettingsModel(List<PageNode> pages) {
        this.pages = Collections.unmodifiableList(pages);
        for (PageNode page : pages)
            for (SubPageNode sub : page.subPages)
                for (GroupNode group : sub.groups) settings.addAll(group.settings);
    }

    public List<PageNode> getPages() {
        return pages;
    }

    /** Every setting in menu order. */
    public List<Setting<?>> getSettings() {
        return Collections.unmodifiableList(settings);
    }

    /** Finds a setting by its group class and field name, or null. */
    public Setting<?> find(Class<?> group, String field) {
        for (Setting<?> setting : settings) {
            if (setting.getGroup().getType() == group && setting.getName().equals(field)) return setting;
        }
        return null;
    }
}
