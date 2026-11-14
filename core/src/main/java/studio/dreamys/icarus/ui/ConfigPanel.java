package studio.dreamys.icarus.ui;

import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.IcarusMod;
import studio.dreamys.icarus.config.ConfigStore;
import studio.dreamys.icarus.settings.GroupNode;
import studio.dreamys.icarus.settings.ListModel;
import studio.dreamys.icarus.settings.Setting;
import studio.dreamys.icarus.settings.SettingTypes;
import studio.dreamys.icarus.theme.Theme;
import studio.dreamys.icarus.theme.Themes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The built-in config UI and theme selector, made of virtual settings so they use the normal widgets:
 * <ul>
 *     <li>full (for {@code ConfigUiStyle.PAGE}): config list, name field, load/save/delete/reset/import/export</li>
 *     <li>compact (for {@code ConfigUiStyle.PINNED}): slot dropdown, load/save</li>
 * </ul>
 * Its controls aren't saved in configs.
 */
public final class ConfigPanel {
    static final List<String> SLOTS = Arrays.asList("Slot 1", "Slot 2", "Slot 3", "Slot 4", "Slot 5");

    private final IcarusMod mod;
    private final ConfigStore store;
    private final GroupNode full;
    private final GroupNode compact;
    private final ListModel profiles = new ListModel();
    private String name = "";
    private String slot = SLOTS.get(0);

    /**
     * @param configUi      include the config controls
     * @param themeSelector include a theme dropdown
     */
    public ConfigPanel(IcarusMod mod, boolean configUi, boolean themeSelector) {
        this.mod = mod;
        this.store = mod.getConfigStore();
        this.full = GroupNode.standalone(configUi ? "Configs" : "Menu", LAST_COLUMN, Integer.MAX_VALUE, false);
        this.compact = GroupNode.standalone(configUi ? "Config" : "Menu", LAST_COLUMN, Integer.MAX_VALUE, true);

        if (configUi) {
            full.addVirtual("Configs", SettingTypes.LIST, new Setting.Holder<String>() {
                @Override
                public String get() {
                    return profiles.getSelected();
                }

                @Override
                public void set(String value) {
                    profiles.select(value);
                    if (value != null) name = value;
                }
            }, null, null).setExtra(profiles);
            full.addVirtual("Name", SettingTypes.FIELD, new Setting.Holder<String>() {
                @Override
                public String get() {
                    return name;
                }

                @Override
                public void set(String value) {
                    name = value;
                }
            }, null, null);
            button(full, "Load", () -> {
                String selected = profiles.getSelected();
                if (selected != null) store.load(selected);
                refresh();
            });
            button(full, "Save", this::saveFull);
            button(full, "Delete", () -> {
                String selected = profiles.getSelected();
                if (selected != null) store.delete(selected);
                refresh();
            });
            button(full, "Reset", store::resetToDefaults);
            button(full, "Import from clipboard", () -> store.importJson(Icarus.getPlatform().getClipboard()));
            button(full, "Export to clipboard", () -> Icarus.getPlatform().setClipboard(store.exportCurrent()));

            compact.addVirtual("Slot", SettingTypes.CHOICE, new Setting.Holder<String>() {
                @Override
                public String get() {
                    return slot;
                }

                @Override
                public void set(String value) {
                    slot = value;
                }
            }, SLOTS, null);
            button(compact, "Load config", () -> store.load(slot));
            button(compact, "Save config", () -> store.saveAs(slot));
        }

        if (themeSelector) {
            Setting.Holder<String> themeHolder = new Setting.Holder<String>() {
                @Override
                public String get() {
                    return mod.getTheme().getDisplayName();
                }

                @Override
                public void set(String value) {
                    Theme theme = Themes.byDisplayName(value);
                    if (theme != null) mod.setTheme(theme);
                }
            };
            full.addVirtual("Menu theme", SettingTypes.CHOICE, themeHolder, Themes.displayNames(), null);
            compact.addVirtual("Menu theme", SettingTypes.CHOICE, themeHolder, Themes.displayNames(), null);
        }
        refresh();
    }

    /** Marker meaning "last column" for groups created in code. */
    private static final int LAST_COLUMN = Integer.MAX_VALUE;

    private static void button(GroupNode group, String label, Runnable action) {
        group.addVirtual(label, SettingTypes.BUTTON, new Setting.Holder<Runnable>() {
            @Override
            public Runnable get() {
                return action;
            }

            @Override
            public void set(Runnable value) {
            }
        }, null, null);
    }

    private void saveFull() {
        String target = name == null ? "" : name.trim();
        if (ConfigStore.isValidName(target) && !target.equals(store.getProfile())) store.saveAs(target);
        else store.save();
        refresh();
    }

    /** Re-reads the saved configs (after any file operation, and when the menu opens). */
    public void refresh() {
        List<String> names = new ArrayList<>(store.listProfiles());
        if (!names.contains(store.getProfile())) names.add(0, store.getProfile());
        profiles.setItems(names);
        profiles.select(store.getProfile());
        name = store.getProfile();
        if (SLOTS.contains(store.getProfile())) slot = store.getProfile();
    }

    /** The group for a style: full for {@code PAGE}, compact for {@code PINNED}. */
    public GroupNode group(boolean page) {
        return page ? full : compact;
    }

    /** Label for a save button: marked while there are unsaved changes. */
    public String saveLabel(String base) {
        return store.hasUnsavedChanges() ? base + " *" : base;
    }

    /** Whether a group is one of this panel's. */
    public boolean owns(GroupNode group) {
        return group == full || group == compact;
    }

    IcarusMod getMod() {
        return mod;
    }

    /** Column hint used for the panel's groups: the last column. */
    public static boolean isLastColumn(int column) {
        return column == LAST_COLUMN;
    }
}
