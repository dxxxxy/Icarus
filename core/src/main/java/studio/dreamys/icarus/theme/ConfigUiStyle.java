package studio.dreamys.icarus.theme;

/** How a theme presents the built-in config UI. */
public enum ConfigUiStyle {
    /** A dedicated last tab with a config list, name field and load/save/delete/reset/import/export buttons. */
    PAGE,
    /** A compact "Config" group pinned to the bottom of the last column of every page: slot dropdown + load/save. */
    PINNED
}
