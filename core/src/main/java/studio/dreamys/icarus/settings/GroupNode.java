package studio.dreamys.icarus.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A group box holding settings. */
public final class GroupNode {
    private final SubPageNode subPage;
    private final Class<?> type;
    private final String name;
    private final int column;
    private final int order;
    private final boolean pinBottom;
    final List<Setting<?>> settings = new ArrayList<>();

    GroupNode(SubPageNode subPage, Class<?> type, String name, int column, int order, boolean pinBottom) {
        this.subPage = subPage;
        this.type = type;
        this.name = name;
        this.column = column;
        this.order = order;
        this.pinBottom = pinBottom;
    }

    /** The sub-page holding this group, or null for a free-standing group created in code. */
    public SubPageNode getSubPage() {
        return subPage;
    }

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

    /** Preferred column, or -1 for automatic. */
    public int getColumn() {
        return column;
    }

    public int getOrder() {
        return order;
    }

    public boolean isPinBottom() {
        return pinBottom;
    }

    public List<Setting<?>> getSettings() {
        return Collections.unmodifiableList(settings);
    }

    /** Readable path for messages, e.g. "Combat/KillAura". */
    public String getPath() {
        if (subPage == null) return name;
        StringBuilder path = new StringBuilder(subPage.getPage().getName());
        if (!subPage.isImplicit()) path.append('/').append(subPage.getName());
        return path.append('/').append(name).toString();
    }

    /** Adds a virtual setting backed by {@code holder} (used for the built-in config UI and similar). */
    public <T> Setting<T> addVirtual(String name, SettingType<T> type, Setting.Holder<T> holder, List<String> options, Setting.SliderSpec slider) {
        Setting<T> setting = new Setting<>(this, name, type, holder, null, false, options, slider, 64, -1);
        setting.captureDefault();
        settings.add(setting);
        return setting;
    }

    /** Creates a free-standing group (not part of the annotated model). */
    public static GroupNode standalone(String name, int column, int order, boolean pinBottom) {
        return new GroupNode(null, null, name, column, order, pinBottom);
    }
}
