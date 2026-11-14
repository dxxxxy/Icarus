package studio.dreamys.icarus.theme.fatality;

import studio.dreamys.icarus.hud.Notification;
import studio.dreamys.icarus.hud.Watermark;
import studio.dreamys.icarus.render.Canvas;
import studio.dreamys.icarus.render.Font;
import studio.dreamys.icarus.render.FontAtlas;
import studio.dreamys.icarus.theme.BaseTheme;
import studio.dreamys.icarus.theme.Colors;
import studio.dreamys.icarus.theme.ConfigUiStyle;
import studio.dreamys.icarus.theme.FontFamilies;
import studio.dreamys.icarus.ui.GroupMetrics;
import studio.dreamys.icarus.ui.GroupView;
import studio.dreamys.icarus.ui.Menu;
import studio.dreamys.icarus.ui.Rect;
import studio.dreamys.icarus.ui.SubTabView;
import studio.dreamys.icarus.ui.TabView;
import studio.dreamys.icarus.ui.popup.ColorPopup;
import studio.dreamys.icarus.ui.popup.DropdownPopup;
import studio.dreamys.icarus.ui.widget.ButtonWidget;
import studio.dreamys.icarus.ui.widget.CheckboxWidget;
import studio.dreamys.icarus.ui.widget.ChoiceWidget;
import studio.dreamys.icarus.ui.widget.ColorWidget;
import studio.dreamys.icarus.ui.widget.ComboWidget;
import studio.dreamys.icarus.ui.widget.FieldWidget;
import studio.dreamys.icarus.ui.widget.KeybindWidget;
import studio.dreamys.icarus.ui.widget.ListWidget;
import studio.dreamys.icarus.ui.widget.SliderWidget;
import studio.dreamys.icarus.ui.widget.Widget;

import java.awt.Color;
import java.util.List;

/**
 * "Neon": deep indigo panels, pink accent, the title and uppercase text tabs in a header band, an underlined
 * sub-tab row, three equal columns, outlined controls and pink buttons.
 */
public class FatalityStyleTheme extends BaseTheme {
    protected static final int WINDOW_BG = 0xFF140D24;
    protected static final int WINDOW_EDGE = 0xFF2E2748;
    protected static final int HEADER_TOP = 0xFF191433, HEADER_BOTTOM = 0xFF1F1941;
    protected static final int PANEL_BG = 0xFF191433;
    protected static final int PANEL_EDGE = 0xFF2E2852;
    protected static final int GROUP_FILL = 0xFF1F1944;
    protected static final int GROUP_EDGE = 0xFF37315C;
    protected static final int CONTROL_BG = 0xFF1A143F;
    protected static final int TEXT = 0xFFA29CC7;
    protected static final int TEXT_BRIGHT = 0xFFF0E8FF;
    protected static final int TAB_IDLE = 0xFF8A84B0;
    protected static final int STRIP_LEFT = 0xFF4C2BB4;

    protected static final double WIDTH = 800, HEIGHT = 650;
    protected static final double STRIP_Y = 16;
    protected static final double HEADER_Y = 18, HEADER_H = 52;
    protected static final double PANEL_X = 15, PANEL_Y = 85, PANEL_BOTTOM = 15;
    protected static final double PANEL_PAD = 16;
    protected static final double SUBTAB_H = 26;
    protected static final double FOOTER = 14;

    private final GroupMetrics groupMetrics = new GroupMetrics(19, 10, 11, 11, 7, 30);
    private final Rect panel = new Rect();
    private final Rect columns = new Rect();

    public FatalityStyleTheme() {
        textRow = 14;
        labelRow = 10;
        checkboxSize = 12;
        indent = 21;
        controlWidth = 115;
        controlRightReserve = 0;
        boxHeight = 14;
        labelGap = 0;
        buttonHeight = 14;
        sliderTrackHeight = 9;
        sliderValueOverhang = 0;
        swatchWidth = 18;
        swatchHeight = 8;
        listItemHeight = 15;
        listRows = 9;
        popupItemHeight = 15;
    }

    @Override
    public String getId() {
        return "neon";
    }

    @Override
    public String getDisplayName() {
        return "Neon";
    }

    @Override
    public int getDefaultAccent() {
        return 0xFFD4065A;
    }

    @Override
    public ConfigUiStyle getConfigUiStyle() {
        return ConfigUiStyle.PINNED;
    }

    @Override
    public char getConfigIcon() {
        return 'F';
    }

    @Override
    protected void createFonts(FontAtlas atlas) {
        String text = FontFamilies.first("Segoe UI", "Tahoma", "DejaVu Sans", "Arial");
        String strong = FontFamilies.first("Segoe UI Semibold", "Segoe UI", "Tahoma", "DejaVu Sans", "Arial");
        String title = FontFamilies.first("Segoe UI Black", "Arial Black", "Segoe UI", "DejaVu Sans");
        setFonts(new Font(atlas, text, java.awt.Font.PLAIN, 12),
                new Font(atlas, strong, java.awt.Font.BOLD, 12),
                new Font(atlas, text, java.awt.Font.PLAIN, 10),
                new Font(atlas, text, java.awt.Font.PLAIN, 12),
                new Font(atlas, title, java.awt.Font.BOLD, 21),
                new Font(atlas, strong, java.awt.Font.PLAIN, 15));
    }

    /* ---- window ---- */

    @Override
    public void layout(Menu menu, Canvas c) {
        Rect w = menu.window;
        w.set(w.x, w.y, WIDTH, HEIGHT);
        menu.dragArea.set(w.x, w.y, w.w, HEADER_Y + HEADER_H);

        //title, then uppercase text tabs
        double x = w.x + 17 + c.measure(title(c), menu.getTitle()) + 32;
        List<TabView> tabs = menu.getTabs();
        for (TabView tab : tabs) {
            double tw = c.measure(tab(c), upper(tab));
            tab.bounds.set(x - 4, w.y + HEADER_Y, tw + 8, HEADER_H);
            x += tw + 18;
        }

        boolean footer = !menu.getFooterLeft().isEmpty() || !menu.getFooterCenter().isEmpty() || !menu.getFooterRight().isEmpty();
        panel.set(w.x + PANEL_X, w.y + PANEL_Y, WIDTH - 2 * PANEL_X, HEIGHT - PANEL_Y - PANEL_BOTTOM - (footer ? FOOTER : 0));

        double top = panel.y + PANEL_PAD;
        List<SubTabView> subs = menu.getCurrentTab().getSubTabs();
        if (menu.getCurrentTab().hasSubTabs()) {
            double sx = panel.x + PANEL_PAD;
            for (SubTabView sub : subs) {
                double tw = c.measure(text(c), sub.getLabel().toUpperCase(java.util.Locale.ROOT));
                sub.bounds.set(sx - 4, panel.y + 6, tw + 8, SUBTAB_H - 6);
                sx += tw + 22;
            }
            top = panel.y + SUBTAB_H + 8;
        } else {
            for (SubTabView sub : subs) sub.bounds.clear();
        }
        columns.set(panel.x + PANEL_PAD, top, panel.w - 2 * PANEL_PAD, panel.bottom() - PANEL_PAD - top);
        menu.layoutColumns(c, columns, 3, 15, groupMetrics);
    }

    private String upperCache;
    private TabView upperFor;

    private String upper(TabView tab) {
        //tabs are measured and drawn every frame; keep the upper-case label of the last one asked for
        if (tab != upperFor) {
            upperFor = tab;
            upperCache = tab.getLabel().toUpperCase(java.util.Locale.ROOT);
        }
        return upperCache;
    }

    @Override
    public void paintWindow(Canvas c, Menu menu) {
        Rect w = menu.window;
        int accent = menu.getAccent();
        c.rect(w.x, w.y, w.w, w.h, WINDOW_EDGE);
        c.rect(w.x + 1, w.y + 1, w.w - 2, w.h - 2, WINDOW_BG);
        c.gradientH(w.x + 1, w.y + STRIP_Y, w.w - 2, 2, STRIP_LEFT, accent);
        c.gradientV(w.x + 1, w.y + HEADER_Y, w.w - 2, HEADER_H, HEADER_TOP, HEADER_BOTTOM);

        Font titleFont = title(c);
        c.text(titleFont, menu.getTitle(), w.x + 17, centeredTextY(c, titleFont, w.y + HEADER_Y + HEADER_H / 2), TEXT_BRIGHT);

        List<TabView> tabs = menu.getTabs();
        Font tabFont = tab(c);
        for (int i = 0; i < tabs.size(); i++) {
            TabView tab = tabs.get(i);
            Rect b = tab.bounds;
            boolean active = i == menu.getTabIndex();
            boolean hovered = b.contains(menu.getMouseX(), menu.getMouseY());
            String label = upper(tab);
            double ty = centeredTextY(c, tabFont, b.centerY());
            c.text(tabFont, label, b.x + 4, ty, active ? TEXT_BRIGHT : hovered ? Colors.mix(TAB_IDLE, TEXT_BRIGHT, 0.5) : TAB_IDLE);
            if (active) c.rect(b.x + 4, ty + c.lineHeight(tabFont) + 1, b.w - 8, 2, accent);
        }

        c.rect(panel.x, panel.y, panel.w, panel.h, PANEL_EDGE);
        c.rect(panel.x + 1, panel.y + 1, panel.w - 2, panel.h - 2, PANEL_BG);

        if (menu.getCurrentTab().hasSubTabs()) {
            List<SubTabView> subs = menu.getCurrentTab().getSubTabs();
            for (int i = 0; i < subs.size(); i++) {
                SubTabView sub = subs.get(i);
                Rect b = sub.bounds;
                boolean active = i == menu.getSubTabIndex();
                String label = sub.getLabel().toUpperCase(java.util.Locale.ROOT);
                double ty = centeredTextY(c, text(c), b.centerY());
                c.text(text(c), label, b.x + 4, ty, active ? TEXT_BRIGHT : TAB_IDLE);
                if (active) c.rect(b.x, b.bottom() + 1, b.w, 2, accent);
            }
            c.hline(panel.x + 1, panel.y + SUBTAB_H + 2, panel.w - 2, PANEL_EDGE);
        }

        double fy = panel.bottom() + 2;
        if (!menu.getFooterLeft().isEmpty()) c.text(small(c), menu.getFooterLeft(), panel.x, fy, TAB_IDLE);
        if (!menu.getFooterCenter().isEmpty()) {
            double tw = c.measure(small(c), menu.getFooterCenter());
            c.text(small(c), menu.getFooterCenter(), Math.round(panel.centerX() - tw / 2), fy, TAB_IDLE);
        }
        if (!menu.getFooterRight().isEmpty()) {
            double tw = c.measure(small(c), menu.getFooterRight());
            c.text(small(c), menu.getFooterRight(), panel.right() - tw, fy, TAB_IDLE);
        }
    }

    @Override
    public void paintGroup(Canvas c, Menu menu, GroupView g) {
        Rect f = g.frame;
        Font font = bold(c);
        String title = g.getLabel();
        double titleW = c.measure(font, title);
        c.rect(f.x, f.y, f.w, f.h, GROUP_FILL);
        double gapStart = f.x + 8, gapEnd = f.x + 15 + titleW;
        c.hline(f.x, f.y, gapStart - f.x, GROUP_EDGE);
        c.hline(gapEnd, f.y, f.right() - gapEnd, GROUP_EDGE);
        c.hline(f.x, f.bottom() - 1, f.w, GROUP_EDGE);
        c.vline(f.x, f.y, f.h, GROUP_EDGE);
        c.vline(f.right() - 1, f.y, f.h, GROUP_EDGE);
        c.text(font, title, f.x + 12, Math.round(f.y - c.lineHeight(font) / 2), TEXT_BRIGHT);
    }

    @Override
    protected int scrollbarTrack() {
        return CONTROL_BG;
    }

    @Override
    protected int scrollbarThumb() {
        return GROUP_EDGE;
    }

    /* ---- widgets ---- */

    protected void label(Canvas c, String label, double x, double y, double rowHeight, int color) {
        c.text(text(c), label, x, centeredTextY(c, text(c), y + rowHeight / 2), color);
    }

    protected void smallLabel(Canvas c, String label, double x, double y, int color) {
        c.text(small(c), label, x, Math.round(y + (labelRow - c.lineHeight(small(c))) / 2), color);
    }

    protected void accentFill(Canvas c, double x, double y, double w, double h, int accent, double hover) {
        c.gradientV(x, y, w, h, Colors.shade(accent, 1.03 + 0.15 * hover), Colors.shade(accent, 0.68));
    }

    @Override
    public void paintCheckbox(Canvas c, CheckboxWidget w) {
        Rect b = w.box;
        int accent = w.getMenu().getAccent();
        if (w.isChecked()) {
            c.rect(b.x, b.y, b.w, b.h, Colors.shade(accent, 0.8));
            accentFill(c, b.x + 1, b.y + 1, b.w - 2, b.h - 2, accent, w.getHover());
        } else {
            c.rect(b.x, b.y, b.w, b.h, Colors.mix(GROUP_EDGE, TAB_IDLE, w.getHover() * 0.5));
            c.rect(b.x + 1, b.y + 1, b.w - 2, b.h - 2, CONTROL_BG);
        }
        label(c, w.getLabel(), w.bounds.x + indent, w.bounds.y, w.bounds.h, w.isChecked() ? TEXT_BRIGHT : TEXT);
    }

    @Override
    public void paintButton(Canvas c, ButtonWidget w) {
        Rect b = w.button;
        int accent = w.getMenu().getAccent();
        accentFill(c, b.x, b.y, b.w, b.h, w.isPressed() ? Colors.shade(accent, 0.85) : accent, w.getHover());
        String label = c.fit(text(c), w.getLabel(), b.w - 6);
        double tw = c.measure(text(c), label);
        c.text(text(c), label, Math.round(b.centerX() - tw / 2), centeredTextY(c, text(c), b.centerY()), TEXT_BRIGHT);
    }

    @Override
    public void paintSlider(Canvas c, SliderWidget w) {
        Rect t = w.track;
        smallLabel(c, w.getLabel(), t.x, w.bounds.y, TEXT);
        int accent = w.getMenu().getAccent();
        c.rect(t.x, t.y, t.w, t.h, GROUP_EDGE);
        c.rect(t.x + 1, t.y + 1, t.w - 2, t.h - 2, CONTROL_BG);
        accentFill(c, t.x + 1, t.y + 1, (t.w - 2) * w.getFraction(), t.h - 2, accent, w.getHover());
        Font font = small(c);
        String value = w.getValueText();
        double vw = c.measure(font, value);
        c.text(font, value, Math.round(t.centerX() - vw / 2), centeredTextY(c, font, t.centerY()), TEXT_BRIGHT);
    }

    protected void dropdown(Canvas c, Widget w, Rect b, String value, boolean open) {
        int accent = w.getMenu().getAccent();
        c.rect(b.x, b.y, b.w, b.h, Colors.mix(GROUP_EDGE, TAB_IDLE, w.getHover() * 0.4));
        c.rect(b.x + 1, b.y + 1, b.w - 2, b.h - 2, CONTROL_BG);
        double bw = 17;
        accentFill(c, b.right() - bw, b.y, bw, b.h, accent, 0);
        for (int i = 0; i < 3; i++) c.rect(b.right() - bw + 5, b.y + 4 + i * 2.5, bw - 10, 1, Colors.shade(accent, open ? 1.4 : 0.6));
        c.text(text(c), c.fit(text(c), value, b.w - bw - 10), b.x + 5, centeredTextY(c, text(c), b.centerY()), TEXT_BRIGHT);
    }

    @Override
    public void paintChoice(Canvas c, ChoiceWidget w) {
        smallLabel(c, w.getLabel(), w.box.x, w.bounds.y, TEXT);
        dropdown(c, w, w.box, w.getSelected() == null ? "-" : w.getSelected(), w.isOpen());
    }

    @Override
    public void paintCombo(Canvas c, ComboWidget w) {
        smallLabel(c, w.getLabel(), w.box.x, w.bounds.y, TEXT);
        dropdown(c, w, w.box, w.getSummary(), w.isOpen());
    }

    @Override
    public void paintField(Canvas c, FieldWidget w) {
        Rect b = w.box;
        smallLabel(c, w.getLabel(), b.x, w.bounds.y, TEXT);
        c.rect(b.x, b.y, b.w, b.h, w.isFocused() ? w.getMenu().getAccent() : GROUP_EDGE);
        c.rect(b.x + 1, b.y + 1, b.w - 2, b.h - 2, CONTROL_BG);
        Font font = text(c);
        String value = w.getText();
        double tw = c.measure(font, value);
        double offset = Math.min(0, b.w - 12 - tw);
        double ty = centeredTextY(c, font, b.centerY());
        c.pushClip(b.x + 1, b.y + 1, b.w - 2, b.h - 2);
        c.text(font, value, b.x + 5 + offset, ty, TEXT_BRIGHT);
        if (w.isCaretVisible()) c.rect(b.x + 5 + offset + tw + 1, ty + 1, 1, c.lineHeight(font) - 2, TEXT_BRIGHT);
        c.popClip();
    }

    @Override
    public void paintColor(Canvas c, ColorWidget w) {
        label(c, w.getLabel(), w.bounds.x + indent, w.bounds.y, w.bounds.h, TEXT);
        Rect s = w.swatch;
        Color color = w.getColor();
        c.rect(s.x, s.y, s.w, s.h, GROUP_EDGE);
        if (color.getAlpha() < 255) paintChecker(c, s.x + 1, s.y + 1, s.w - 2, s.h - 2);
        c.rect(s.x + 1, s.y + 1, s.w - 2, s.h - 2, color.getRGB());
    }

    @Override
    public void paintList(Canvas c, ListWidget w) {
        Rect b = w.box;
        c.rect(b.x, b.y, b.w, b.h, GROUP_EDGE);
        c.rect(b.x + 1, b.y + 1, b.w - 2, b.h - 2, CONTROL_BG);
        List<String> items = w.getItems();
        int accent = w.getMenu().getAccent();
        c.pushClip(b.x + 1, b.y + 1, b.w - 2, b.h - 2);
        for (int row = 0; row < w.visibleRows; row++) {
            int index = w.getScroll() + row;
            if (index >= items.size()) break;
            String item = items.get(index);
            double y = b.y + 1 + row * w.itemHeight;
            if (index == w.getHoveredIndex()) c.rect(b.x + 1, y, b.w - 2, w.itemHeight, 0xFF241D4E);
            boolean selected = item.equals(w.getSelected());
            c.text(text(c), c.fit(text(c), item, b.w - 14), b.x + 7, centeredTextY(c, text(c), y + w.itemHeight / 2), selected ? accent : TEXT);
        }
        c.popClip();
    }

    @Override
    public void paintKeybind(Canvas c, KeybindWidget w) {
        Rect b = w.bounds;
        int color = w.isRefused() ? 0xFFE04060 : w.isCapturing() ? w.getMenu().getAccent() : TEXT;
        c.rect(b.x - 4, b.y, b.w + 8, b.h, w.isCapturing() ? w.getMenu().getAccent() : GROUP_EDGE);
        c.rect(b.x - 3, b.y + 1, b.w + 6, b.h - 2, CONTROL_BG);
        c.text(small(c), w.getText(this), b.x, centeredTextY(c, small(c), b.centerY()), color);
    }

    @Override
    protected void layoutKeybind(Canvas c, Widget widget, double rowRight, double y, double height) {
        KeybindWidget keybind = widget.getKeybind();
        if (keybind == null) return;
        double w = c.measure(small(c), keybind.getText(this));
        double boxH = Math.min(height, 14);
        double by = Math.round(y + (height - boxH) / 2);
        keybind.bounds.set(rowRight - w - 4, by, w, boxH);
        keybind.label.set(rowRight - w - 8, by, w + 8, boxH);
    }

    @Override
    public String formatKeybind(int key, boolean capturing, boolean refused) {
        if (refused) return "IN USE";
        if (capturing) return "...";
        if (key <= 0) return "NONE";
        return keyName(key);
    }

    /* ---- popups ---- */

    @Override
    public void paintDropdown(Canvas c, DropdownPopup p) {
        Rect b = p.bounds;
        int accent = p.getOwner().getMenu().getAccent();
        c.rect(b.x, b.y, b.w, b.h, GROUP_EDGE);
        c.rect(b.x + 1, b.y + 1, b.w - 2, b.h - 2, 0xFF1C1645);
        List<String> options = p.getOptions();
        for (int row = 0; row < p.visibleRows; row++) {
            int index = p.getScroll() + row;
            if (index >= options.size()) break;
            double y = b.y + row * p.itemHeight;
            if (index == p.getHovered()) c.rect(b.x + 1, y + 1, b.w - 2, p.itemHeight - 1, 0xFF262057);
            boolean selected = p.getSource().isSelected(index);
            c.text(text(c), c.fit(text(c), options.get(index), b.w - 10), b.x + 5, centeredTextY(c, text(c), y + p.itemHeight / 2), selected ? accent : TEXT);
        }
    }

    @Override
    public void paintColorPicker(Canvas c, ColorPopup p) {
        Rect b = p.bounds;
        c.rect(b.x, b.y, b.w, b.h, GROUP_EDGE);
        c.rect(b.x + 1, b.y + 1, b.w - 2, b.h - 2, GROUP_FILL);
        paintPickerParts(c, p, GROUP_EDGE);
    }

    /* ---- HUD ---- */

    @Override
    public double watermarkHeight(Canvas c) {
        return 3 + 4 + textRow + 4 + 1;
    }

    @Override
    public double watermarkWidth(Canvas c, Watermark watermark) {
        return Math.round(c.measure(text(c), watermark.getText()) + 20);
    }

    @Override
    public void paintWatermark(Canvas c, Watermark watermark, double x, double y, double w, double h, int accent) {
        c.rect(x, y, w, h, WINDOW_EDGE);
        c.gradientV(x + 1, y + 1, w - 2, h - 2, HEADER_TOP, HEADER_BOTTOM);
        c.gradientH(x + 1, y + 1, w - 2, 2, STRIP_LEFT, accent);
        c.text(text(c), watermark.getText(), x + 10, centeredTextY(c, text(c), y + 3 + (h - 3) / 2), TEXT_BRIGHT);
    }

    @Override
    public double notificationWidth(Canvas c, Notification n) {
        return 220;
    }

    @Override
    public double notificationHeight(Canvas c, Notification n) {
        return 3 + 7 + textRow + 3 + n.lines(c, text(c), 220 - 24).size() * textRow + 8;
    }

    @Override
    public void paintNotification(Canvas c, Notification n, double x, double y, double w, double h, double visibility, int accent) {
        c.setAlpha(visibility);
        c.rect(x, y, w, h, WINDOW_EDGE);
        c.gradientV(x + 1, y + 1, w - 2, h - 2, HEADER_TOP, HEADER_BOTTOM);
        c.gradientH(x + 1, y + 1, w - 2, 2, STRIP_LEFT, accent);
        double ty = y + 3 + 7;
        c.text(bold(c), n.getTitle(), x + 12, ty, TEXT_BRIGHT);
        ty += textRow + 3;
        for (String line : n.lines(c, text(c), w - 24)) {
            c.text(text(c), line, x + 12, ty, TEXT);
            ty += textRow;
        }
        c.setAlpha(1);
    }
}
