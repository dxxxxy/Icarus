package studio.dreamys.icarus.theme;

import studio.dreamys.icarus.hud.Toast;
import studio.dreamys.icarus.hud.Watermark;
import studio.dreamys.icarus.render.Canvas;
import studio.dreamys.icarus.ui.GroupView;
import studio.dreamys.icarus.ui.Menu;
import studio.dreamys.icarus.ui.popup.ColorPopup;
import studio.dreamys.icarus.ui.popup.ContextPopup;
import studio.dreamys.icarus.ui.popup.DropdownPopup;
import studio.dreamys.icarus.ui.widget.ButtonWidget;
import studio.dreamys.icarus.ui.widget.CheckboxWidget;
import studio.dreamys.icarus.ui.widget.ChoiceWidget;
import studio.dreamys.icarus.ui.widget.ColorWidget;
import studio.dreamys.icarus.ui.widget.ComboWidget;
import studio.dreamys.icarus.ui.widget.FieldWidget;
import studio.dreamys.icarus.ui.widget.KeybindWidget;
import studio.dreamys.icarus.ui.widget.LabelWidget;
import studio.dreamys.icarus.ui.widget.ListWidget;
import studio.dreamys.icarus.ui.widget.SliderWidget;
import studio.dreamys.icarus.ui.widget.Widget;

/**
 * A complete look for the menu and HUD: window chrome, layout and every widget. Widgets and the menu only hold
 * state; everything visual comes from here, so a new style is one implementation of this interface.
 * <p>
 * All coordinates are design units (1 unit = 1 screen pixel at Minecraft GUI scale 2). Themes must be stateless
 * with respect to a particular menu (one instance can serve several mods); per-menu state lives in the menu and
 * widgets. The built-in themes are in {@link Themes}.
 */
public interface Theme {
    /** Stable id, saved in UI preferences. */
    String getId();

    /** Name shown in the theme selector. */
    String getDisplayName();

    /** Accent colour used when the mod doesn't set one (ARGB). */
    int getDefaultAccent();

    /** Where the built-in config UI goes. */
    ConfigUiStyle getConfigUiStyle();

    /**
     * Icon of the config tab for {@link ConfigUiStyle#PAGE}: a built-in icon name, or empty. A page whose
     * {@code @IPage(icon)} is this name hosts the config UI instead of getting a tab of its own.
     */
    String getConfigIcon();

    /* ---- menu ---- */

    /**
     * Lays out the window: sets {@code menu.window} size, {@code menu.dragArea}, each tab's and sub-tab's bounds,
     * and the groups of the current sub-tab (normally via {@link Menu#layoutColumns}).
     */
    void layout(Menu menu, Canvas canvas);

    /** Window background, chrome, tabs, sub-tabs and footer. */
    void paintWindow(Canvas canvas, Menu menu);

    /** A group's frame and title (before its widgets). */
    void paintGroup(Canvas canvas, Menu menu, GroupView group);

    /** Anything over a group's widgets (e.g. its scrollbar). */
    void paintGroupForeground(Canvas canvas, Menu menu, GroupView group);

    /* ---- widgets: layout returns the row height and fills the widget's part rectangles ---- */

    double layoutCheckbox(Canvas canvas, CheckboxWidget widget, double x, double y, double width);

    void paintCheckbox(Canvas canvas, CheckboxWidget widget);

    double layoutButton(Canvas canvas, ButtonWidget widget, double x, double y, double width);

    void paintButton(Canvas canvas, ButtonWidget widget);

    double layoutSlider(Canvas canvas, SliderWidget widget, double x, double y, double width);

    void paintSlider(Canvas canvas, SliderWidget widget);

    double layoutChoice(Canvas canvas, ChoiceWidget widget, double x, double y, double width);

    void paintChoice(Canvas canvas, ChoiceWidget widget);

    double layoutCombo(Canvas canvas, ComboWidget widget, double x, double y, double width);

    void paintCombo(Canvas canvas, ComboWidget widget);

    double layoutField(Canvas canvas, FieldWidget widget, double x, double y, double width);

    void paintField(Canvas canvas, FieldWidget widget);

    double layoutColor(Canvas canvas, ColorWidget widget, double x, double y, double width);

    void paintColor(Canvas canvas, ColorWidget widget);

    double layoutList(Canvas canvas, ListWidget widget, double x, double y, double width);

    void paintList(Canvas canvas, ListWidget widget);

    /**
     * A swatch shown on another widget's row ({@code @IAttach}); sets the swatch inside {@code row}'s bounds.
     * {@code index} counts the swatches already on that row (0 for the first), so several can line up.
     */
    void layoutAttachedColor(Canvas canvas, ColorWidget widget, Widget row, int index);

    double layoutLabel(Canvas canvas, LabelWidget widget, double x, double y, double width);

    void paintLabel(Canvas canvas, LabelWidget widget);

    void paintKeybind(Canvas canvas, KeybindWidget widget);

    /** Keybind label text, e.g. "[DEL]". {@code refused} is true briefly after the menu key was pressed. */
    String formatKeybind(int key, boolean capturing, boolean refused);

    /* ---- popups ---- */

    void layoutDropdown(Canvas canvas, DropdownPopup popup);

    void paintDropdown(Canvas canvas, DropdownPopup popup);

    /** Right-click menu at {@code popup.anchorX/anchorY}: sets its bounds and item height. */
    void layoutContext(Canvas canvas, ContextPopup popup);

    void paintContext(Canvas canvas, ContextPopup popup);

    void layoutColorPicker(Canvas canvas, ColorPopup popup);

    void paintColorPicker(Canvas canvas, ColorPopup popup);

    /* ---- HUD ---- */

    double watermarkHeight(Canvas canvas);

    double watermarkWidth(Canvas canvas, Watermark watermark);

    void paintWatermark(Canvas canvas, Watermark watermark, double x, double y, double width, double height, int accent);

    double toastWidth(Canvas canvas, Toast toast);

    double toastHeight(Canvas canvas, Toast toast);

    /** {@code visibility} is the slide-in/out progress, 0 (hidden) to 1 (fully shown). */
    void paintToast(Canvas canvas, Toast toast, double x, double y, double width, double height, double visibility, int accent);
}
