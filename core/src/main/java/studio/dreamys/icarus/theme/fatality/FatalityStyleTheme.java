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

import java.awt.Color;
import java.util.List;
import java.util.Locale;

/**
 * The fatality menu: an indigo window with the glitching "FATALITY" title and upper-case tabs in the header, an
 * underlined sub-tab row, three columns of groups, pink controls and the Config group pinned bottom right.
 * <p>
 * The reference footage shows it at exactly 1:1, so every constant is a reference pixel (= a design unit).
 */
public class FatalityStyleTheme extends BaseTheme {
    /* palette, sampled from the reference */
    protected static final int WINDOW_EDGE = 0xFF3A3345;
    protected static final int WINDOW_BG = 0xFF110B22;
    protected static final int TOPBAR_TOP = 0xFF281946, TOPBAR_BOTTOM = 0xFF221945;
    protected static final int HEADER_TOP = 0xFF231844, HEADER_BOTTOM = 0xFF181330;
    protected static final int SHADOW_TOP = 0xFF090516, SHADOW_BOTTOM = 0xFF110C29;
    protected static final int LINE_LEFT = 0xFF2E2280, LINE_RIGHT = 0xFF711654;
    protected static final int PANEL_EDGE = 0xFF393153;
    protected static final int PANEL_BG = 0xFF191337;
    protected static final int SUBTAB_BG = 0xFF1F193F;
    protected static final int DIVIDER = 0xFF373155;
    protected static final int GROUP_EDGE = 0xFF363058;
    protected static final int GROUP_TOP = 0xFF201A42, GROUP_BOTTOM = 0xFF1F1944;
    protected static final int CONTROL_BG = 0xFF1A143C;
    protected static final int CONTROL_GAP = 0xFF1C1736;
    protected static final int TEXT = 0xFFD9D4F0;
    protected static final int TEXT_DIM = 0xFFA09AC5;
    protected static final int TEXT_BRIGHT = 0xFFF0EBFF;
    protected static final int TAB_IDLE = 0xFF8C86AF;
    protected static final int TAB_HOVER = 0xFFC4BEE6;

    /* window */
    protected static final double WIDTH = 800, HEIGHT = 650;
    protected static final double LINE_Y = 17, HEADER_Y = 19, HEADER_BOTTOM_Y = 72, SHADOW_BOTTOM_Y = 87;
    protected static final double TITLE_X = 17, TITLE_CENTER = 45, TABS_X = 160, TAB_PAD = 8, UNDERLINE_Y = 55;
    protected static final double PANEL_X = 17, PANEL_Y = 87, PANEL_RIGHT = 785, PANEL_BOTTOM = 635;
    protected static final double SUBTAB_PAD = 16, SUBTAB_DIVIDER = 119;
    protected static final double GROUPS_X = 33, GROUPS_Y = 135, GROUPS_BOTTOM = 621, GROUP_W = 236, GAP = 16;

    /* rows, from the row top = the label's capital line */
    protected static final double ROW_CHECK = 21, ROW_LABEL = 21, ROW_SLIDER = 32, ROW_DROPDOWN = 39, ROW_BUTTON = 21;
    protected static final double CHECK_X = 10, LABEL_X = 32, CONTROL_X = 10, CONTROL_W = 118, CHECK = 13;
    protected static final double BOX_H = 18, SLIDER_H = 14, BUTTON_H = 15, SWATCH_W = 36, SWATCH_H = 14, RIGHT = 10;

    private final GroupMetrics groupMetrics = new GroupMetrics(15, 10, 0, 0, 0, 40);
    private final Rect panel = new Rect();
    /** The small text of keybind boxes ("NONE"). */
    private Font keybindFont;
    private final Rect columns = new Rect();
    /** Weapon row (sub-pages of the active text tab, shown as icons). */
    protected static final double WEAPONS_X = 36, WEAPONS_H = 38, WEAPONS_SHIFT = 41;
    private boolean weaponRow;
    private final java.util.Map<String, double[]> textSlots = new java.util.LinkedHashMap<>();

    /* title glitch: a random red/blue offset, re-rolled every few frames */
    private final java.util.Random glitch = new java.util.Random();
    private long glitchUntil;
    private int glitchOffset;

    public FatalityStyleTheme() {
        textRow = ROW_LABEL;
        labelRow = 12;
        checkboxSize = CHECK;
        indent = LABEL_X;
        controlWidth = CONTROL_W;
        boxHeight = BOX_H;
        buttonHeight = BUTTON_H;
        sliderTrackHeight = SLIDER_H;
        swatchWidth = SWATCH_W;
        swatchHeight = SWATCH_H;
        listItemHeight = 17;
        listRows = 14;
        popupItemHeight = 18;
        popupMaxRows = 12;
    }

    @Override
    public String getId() {
        return "fatality";
    }

    @Override
    public String getDisplayName() {
        return "Fatality";
    }

    @Override
    public int getDefaultAccent() {
        return 0xFFD4085C;
    }

    @Override
    public ConfigUiStyle getConfigUiStyle() {
        return ConfigUiStyle.PINNED;
    }

    @Override
    public char getConfigIcon() {
        return ' ';
    }

    @Override
    protected void createFonts(FontAtlas atlas) {
        String ui = FontFamilies.first("Segoe UI Semibold", "Segoe UI", "Tahoma", "DejaVu Sans", "Arial");
        String uiBold = FontFamilies.first("Segoe UI", "Tahoma", "DejaVu Sans", "Arial");
        String din = FontFamilies.first("Bahnschrift", "DIN", "Segoe UI", "Arial");
        String icons = FontFamilies.register(FatalityStyleTheme.class, "/assets/icarus/undefeated.ttf");
        keybindFont = new Font(atlas, din, java.awt.Font.PLAIN, 11);
        setFonts(new Font(atlas, ui, java.awt.Font.PLAIN, 11),
                new Font(atlas, uiBold, java.awt.Font.BOLD, 11),
                new Font(atlas, din, java.awt.Font.PLAIN, 15),
                new Font(atlas, icons, java.awt.Font.PLAIN, 20),
                new Font(atlas, din, java.awt.Font.BOLD, 24),
                new Font(atlas, din, java.awt.Font.PLAIN, 15));
    }

    /** Draws text with its capitals starting at {@code capTop}. */
    protected double capText(Canvas c, Font font, String text, double x, double capTop, int color) {
        return c.text(font, text, x, capsLine(c, font, capTop), color);
    }

    protected double capsLine(Canvas c, Font font, double capTop) {
        double capHeight = Math.round(font.getSize() * c.getScale() * 0.7) / c.getScale();
        return capTop - (Math.round(c.ascent(font) * c.getScale()) / c.getScale() - capHeight);
    }

    /* ---- window ---- */

    @Override
    public void layout(Menu menu, Canvas c) {
        Rect w = menu.window;
        w.set(w.x, w.y, WIDTH, HEIGHT);
        menu.dragArea.set(w.x, w.y, w.w, HEADER_BOTTOM_Y);

        double x = w.x + TABS_X;
        for (TabView tab : menu.getTabs()) {
            double tw = c.measure(tab(c), upper(tab.getLabel()));
            tab.bounds.set(x - TAB_PAD, w.y + HEADER_Y, tw + 2 * TAB_PAD, HEADER_BOTTOM_Y - HEADER_Y);
            x += tw + 15;
        }

        panel.set(w.x + PANEL_X, w.y + PANEL_Y, PANEL_RIGHT - PANEL_X + 1, PANEL_BOTTOM - PANEL_Y + 1);
        TabView current = menu.getCurrentTab();
        List<SubTabView> subs = current.getSubTabs();
        weaponRow = false;
        textSlots.clear();
        double top = w.y + (current.hasSubTabs() ? GROUPS_Y : PANEL_Y + GAP);
        if (current.hasSubTabs()) {
            //sub-pages sharing a label are one text tab; their icons make the weapon row under it
            String activeLabel = menu.getCurrentSubTab().getLabel();
            double sx = panel.x;
            for (int i = 0; i < subs.size(); ) {
                String label = subs.get(i).getLabel();
                int end = i;
                while (end + 1 < subs.size() && subs.get(end + 1).getLabel().equals(label)) end++;
                double tw = c.measure(small(c), upper(label));
                double slot = tw + 2 * SUBTAB_PAD;
                boolean active = label.equals(activeLabel);
                for (int j = i; j <= end; j++) subs.get(j).bounds.clear();
                if (active && end > i) {
                    weaponRow = true;
                    double ix = w.x + WEAPONS_X;
                    for (int j = i; j <= end; j++) {
                        double iw = c.measure(icon(c), String.valueOf(iconOf(subs.get(j))));
                        subs.get(j).bounds.set(ix, w.y + SUBTAB_DIVIDER + 1, iw + 34, WEAPONS_H);
                        ix += iw + 34;
                    }
                } else {
                    subs.get(i).bounds.set(sx, panel.y + 1, slot, SUBTAB_DIVIDER - PANEL_Y - 1);
                }
                textSlots.put(label, new double[]{sx, slot});
                sx += slot;
                i = end + 1;
            }
            //the weapon row spans the first two columns; the third keeps its place
        } else {
            for (SubTabView sub : subs) sub.bounds.clear();
        }

        columns.set(w.x + GROUPS_X, top, 3 * GROUP_W + 2 * GAP, w.y + GROUPS_BOTTOM + 1 - top);
        menu.layoutColumns(c, columns, 3, GAP, groupMetrics, weaponRow ? new double[]{WEAPONS_SHIFT, WEAPONS_SHIFT, 0} : null);
    }

    private static char iconOf(SubTabView sub) {
        return sub.getSubPage() == null ? ' ' : sub.getSubPage().getIcon();
    }

    private static String upper(String label) {
        return label.toUpperCase(Locale.ROOT);
    }

    @Override
    public void paintWindow(Canvas c, Menu menu) {
        Rect w = menu.window;
        int accent = menu.getAccent();
        c.rect(w.x, w.y, w.w, w.h, WINDOW_EDGE);
        c.rect(w.x + 1, w.y + 1, w.w - 2, w.h - 2, WINDOW_BG);

        //top bar, the accent line, the header with title and tabs, and the shadow under it
        c.gradientV(w.x + 1, w.y + 1, w.w - 2, LINE_Y - 1, TOPBAR_TOP, TOPBAR_BOTTOM);
        c.gradientH(w.x + 1, w.y + LINE_Y, w.w - 2, 2, LINE_LEFT, Colors.mix(LINE_RIGHT, accent, 0.25));
        c.gradientV(w.x + 1, w.y + HEADER_Y, w.w - 2, HEADER_BOTTOM_Y - HEADER_Y, HEADER_TOP, HEADER_BOTTOM);
        c.gradientV(w.x + 1, w.y + HEADER_BOTTOM_Y, w.w - 2, SHADOW_BOTTOM_Y - HEADER_BOTTOM_Y, SHADOW_TOP, SHADOW_BOTTOM);

        paintTitle(c, menu.getTitle(), w.x + TITLE_X, w.y + TITLE_CENTER);

        Font tabFont = tab(c);
        List<TabView> tabs = menu.getTabs();
        for (int i = 0; i < tabs.size(); i++) {
            TabView tab = tabs.get(i);
            Rect b = tab.bounds;
            boolean active = i == menu.getTabIndex();
            boolean hovered = !active && b.contains(menu.getMouseX(), menu.getMouseY());
            String label = upper(tab.getLabel());
            double tw = capText(c, tabFont, label, b.x + TAB_PAD, w.y + 40, active ? TEXT_BRIGHT : hovered ? TAB_HOVER : TAB_IDLE);
            if (active) c.rect(b.x + TAB_PAD, w.y + UNDERLINE_Y, tw, 2, accent);
        }

        //panel with the sub-tab row
        c.rect(panel.x, panel.y, panel.w, panel.h, PANEL_EDGE);
        c.rect(panel.x + 1, panel.y + 1, panel.w - 2, panel.h - 2, PANEL_BG);
        if (menu.getCurrentTab().hasSubTabs()) {
            c.rect(panel.x + 1, panel.y + 1, panel.w - 2, w.y + SUBTAB_DIVIDER - panel.y - 1, SUBTAB_BG);
            c.hline(panel.x + 1, w.y + SUBTAB_DIVIDER, panel.w - 2, DIVIDER);
            List<SubTabView> subs = menu.getCurrentTab().getSubTabs();
            String activeLabel = menu.getCurrentSubTab().getLabel();
            Font font = small(c);
            for (java.util.Map.Entry<String, double[]> slot : textSlots.entrySet()) {
                double sx = slot.getValue()[0], sw = slot.getValue()[1];
                boolean active = slot.getKey().equals(activeLabel);
                boolean hovered = !active && menu.getMouseX() >= sx && menu.getMouseX() < sx + sw && menu.getMouseY() >= panel.y && menu.getMouseY() < w.y + SUBTAB_DIVIDER;
                capText(c, font, upper(slot.getKey()), sx + SUBTAB_PAD, w.y + 99, active ? TEXT_BRIGHT : hovered ? TAB_HOVER : TAB_IDLE);
                if (active) c.rect(sx, w.y + SUBTAB_DIVIDER - 4, sw, 3, accent);
            }
            if (weaponRow) {
                Font iconFont = icon(c);
                for (int i = 0; i < subs.size(); i++) {
                    if (!subs.get(i).getLabel().equals(activeLabel)) continue;
                    Rect b = subs.get(i).bounds;
                    boolean active = i == menu.getSubTabIndex();
                    boolean hovered = !active && b.contains(menu.getMouseX(), menu.getMouseY());
                    String glyph = String.valueOf(iconOf(subs.get(i)));
                    double gw = c.measure(iconFont, glyph);
                    c.text(iconFont, glyph, Math.round(b.centerX() - gw / 2), centeredTextY(c, iconFont, w.y + 143), active ? TEXT_BRIGHT : hovered ? TAB_HOVER : TAB_IDLE);
                    if (active) c.rect(b.x, w.y + 155, b.w, 3, accent);
                }
            }
        }
    }

    /** The title with its chromatic glitch: red and blue copies jump left/right every few frames. */
    protected void paintTitle(Canvas c, String title, double x, double centerY) {
        Font font = title(c);
        long now = System.nanoTime();
        if (now > glitchUntil) {
            int roll = glitch.nextInt(10);
            glitchOffset = roll < 5 ? 0 : roll < 8 ? 1 : 2;
            if (glitch.nextBoolean()) glitchOffset = -glitchOffset;
            glitchUntil = now + (40 + glitch.nextInt(90)) * 1_000_000L;
        }
        double y = centeredTextY(c, font, centerY);
        if (glitchOffset != 0) {
            c.text(font, title, x - glitchOffset, y, 0xD0FF2050);
            c.text(font, title, x + glitchOffset, y, 0xD02080FF);
        }
        c.text(font, title, x, y, TEXT_BRIGHT);
    }

    @Override
    public void paintGroup(Canvas c, Menu menu, GroupView g) {
        Rect f = g.frame;
        Font font = bold(c);
        String title = g.getLabel();
        double titleW = c.measure(font, title);
        c.gradientV(f.x + 1, f.y + 1, f.w - 2, f.h - 2, GROUP_TOP, GROUP_BOTTOM);
        double gapStart = f.x + 8, gapEnd = f.x + 12 + titleW + 3;
        c.hline(f.x, f.y, gapStart - f.x, GROUP_EDGE);
        c.hline(gapEnd, f.y, f.right() - gapEnd, GROUP_EDGE);
        c.hline(f.x, f.bottom() - 1, f.w, GROUP_EDGE);
        c.vline(f.x, f.y, f.h, GROUP_EDGE);
        c.vline(f.right() - 1, f.y, f.h, GROUP_EDGE);
        capText(c, font, title, f.x + 11, f.y - 4, TEXT_BRIGHT);
    }

    @Override
    public void paintGroupForeground(Canvas c, Menu menu, GroupView g) {
        if (!g.isScrollable()) return;
        Rect f = g.frame;
        //a small down arrow in the corner while there is more below
        if (g.getScroll() < g.getContentHeight() - g.content.h - 0.5) {
            double ax = f.right() - 10, ay = f.bottom() - 7;
            c.triangle(ax - 3, ay - 2, ax + 3, ay - 2, ax, ay + 1, TAB_IDLE);
        }
    }

    @Override
    protected int scrollbarTrack() {
        return CONTROL_BG;
    }

    @Override
    protected int scrollbarThumb() {
        return GROUP_EDGE;
    }

    /* ---- layout (row y = capitals of the row's label) ---- */

    private void keybindRow(Canvas c, Widget w, double x, double y, double width) {
        KeybindWidget keybind = w.getKeybind();
        if (keybind == null) return;
        small(c); //makes sure the fonts exist
        double kw = Math.max(36, c.measure(keybindFont, keybind.getText(this)) + 8);
        keybind.bounds.set(x + width - RIGHT - kw, y - 3, kw, 14);
        keybind.label.set(keybind.bounds);
    }

    @Override
    public double layoutCheckbox(Canvas c, CheckboxWidget w, double x, double y, double width) {
        w.bounds.set(x, y - 3, width, ROW_CHECK);
        w.box.set(x + CHECK_X, y - 2, CHECK, CHECK);
        keybindRow(c, w, x, y, width);
        return ROW_CHECK;
    }

    @Override
    public double layoutLabel(Canvas c, LabelWidget w, double x, double y, double width) {
        w.bounds.set(x, y - 3, width, ROW_LABEL);
        keybindRow(c, w, x, y, width);
        return ROW_LABEL;
    }

    @Override
    public double layoutButton(Canvas c, ButtonWidget w, double x, double y, double width) {
        w.bounds.set(x, y - 3, width, ROW_BUTTON);
        w.button.set(x + CONTROL_X, y - 3, Math.min(CONTROL_W, width - 2 * CONTROL_X), BUTTON_H);
        return ROW_BUTTON;
    }

    @Override
    public double layoutSlider(Canvas c, SliderWidget w, double x, double y, double width) {
        w.bounds.set(x, y - 3, width, ROW_SLIDER);
        w.track.set(x + CONTROL_X, y + 12, Math.min(CONTROL_W, width - 2 * CONTROL_X), SLIDER_H);
        w.minus.set(w.track.right() - 22, y - 3, 10, 12);
        w.plus.set(w.track.right() - 11, y - 3, 11, 12);
        keybindRow(c, w, x, y, width);
        return ROW_SLIDER;
    }

    private double dropdownRow(Canvas c, Rect box, Widget w, double x, double y, double width) {
        boolean labelled = !w.getLabel().isEmpty();
        double boxTop = labelled ? 12 : -3;
        w.bounds.set(x, y - 3, width, labelled ? ROW_DROPDOWN : ROW_DROPDOWN - 15);
        box.set(x + CONTROL_X, y + boxTop, Math.min(CONTROL_W, width - 2 * CONTROL_X), BOX_H);
        //a dropdown's key sits beside its box
        keybindRow(c, w, x, box.y + 5, width);
        return w.bounds.h;
    }

    @Override
    public double layoutChoice(Canvas c, ChoiceWidget w, double x, double y, double width) {
        return dropdownRow(c, w.box, w, x, y, width);
    }

    @Override
    public double layoutCombo(Canvas c, ComboWidget w, double x, double y, double width) {
        return dropdownRow(c, w.box, w, x, y, width);
    }

    @Override
    public double layoutField(Canvas c, FieldWidget w, double x, double y, double width) {
        return dropdownRow(c, w.box, w, x, y, width);
    }

    @Override
    public double layoutColor(Canvas c, ColorWidget w, double x, double y, double width) {
        w.bounds.set(x, y - 3, width, ROW_LABEL);
        w.swatch.set(x + width - RIGHT - SWATCH_W, y - 2, SWATCH_W, SWATCH_H);
        return ROW_LABEL;
    }

    @Override
    public void layoutAttachedColor(Canvas c, ColorWidget w, Widget row, int index) {
        Rect r = row.bounds;
        //next to a dropdown the swatch lines up with the box, otherwise with the row's text
        Rect anchor = row instanceof ChoiceWidget ? ((ChoiceWidget) row).box : row instanceof ComboWidget ? ((ComboWidget) row).box : null;
        double y = anchor != null ? anchor.y + (anchor.h - SWATCH_H) / 2 : r.y + 1;
        w.bounds.set(r.x, r.y, r.w, r.h);
        w.swatch.set(r.right() - RIGHT - SWATCH_W - index * (SWATCH_W + 3), y, SWATCH_W, SWATCH_H);
    }

    @Override
    public double layoutList(Canvas c, ListWidget w, double x, double y, double width) {
        double h = w.getModel().getHeight() > 0 ? w.getModel().getHeight() : listRows * listItemHeight + 2;
        double searchH = w.isSearchable() ? 17 : 0;
        if (w.isSearchable()) w.search.set(x + CONTROL_X, y - 3, width - 2 * CONTROL_X, searchH);
        else w.search.clear();
        w.bounds.set(x, y - 3, width, h + 8);
        w.box.set(x + CONTROL_X, y - 3 + searchH, width - 2 * CONTROL_X, h - searchH);
        w.itemHeight = listItemHeight;
        w.visibleRows = Math.max(1, (int) Math.floor((h - 2) / listItemHeight));
        return w.bounds.h;
    }

    /* ---- widgets ---- */

    protected void label(Canvas c, Widget w, double x, double capTop, int color) {
        if (w.getLabel().isEmpty()) return;
        capText(c, text(c), w.getLabel(), x, capTop, w.getSetting().isDisabled() ? TAB_IDLE : color);
    }

    /** Pink control fill: a lighter outline, a dark gap, then the accent gradient. */
    protected void accentBox(Canvas c, double x, double y, double w, double h, int accent, double hover) {
        c.rect(x, y, w, h, Colors.shade(accent, 0.72));
        c.rect(x + 1, y + 1, w - 2, h - 2, Colors.shade(accent, 0.42));
        c.gradientV(x + 2, y + 2, w - 4, h - 4, Colors.shade(accent, 1.0 + 0.1 * hover), Colors.shade(accent, 0.62));
    }

    @Override
    public void paintCheckbox(Canvas c, CheckboxWidget w) {
        Rect b = w.box;
        int accent = w.getMenu().getAccent();
        if (w.isChecked()) accentBox(c, b.x, b.y, b.w, b.h, accent, w.getHover());
        else {
            c.rect(b.x, b.y, b.w, b.h, Colors.mix(GROUP_EDGE, accent, 0.35 * w.getHover()));
            c.rect(b.x + 1, b.y + 1, b.w - 2, b.h - 2, CONTROL_BG);
        }
        label(c, w, w.bounds.x + LABEL_X, w.bounds.y + 3, TEXT);
    }

    @Override
    public void paintLabel(Canvas c, LabelWidget w) {
        label(c, w, w.bounds.x + CHECK_X, w.bounds.y + 3, TEXT);
    }

    @Override
    public void paintButton(Canvas c, ButtonWidget w) {
        Rect b = w.button;
        int accent = w.getMenu().getAccent();
        accentBox(c, b.x, b.y, b.w, b.h, w.isPressed() ? Colors.shade(accent, 0.85) : accent, w.getHover());
        Font font = text(c);
        String label = c.fit(font, w.getLabel(), b.w - 6);
        double tw = c.measure(font, label);
        capText(c, font, label, Math.round(b.centerX() - tw / 2), b.y + 4, TEXT_BRIGHT);
    }

    @Override
    public void paintSlider(Canvas c, SliderWidget w) {
        Rect t = w.track;
        int accent = w.getMenu().getAccent();
        label(c, w, t.x, w.bounds.y + 3, TEXT);

        //"-  +" to the right of the label
        Font font = text(c);
        double py = w.bounds.y + 3;
        capText(c, font, "-", t.right() - 19, py, TEXT_DIM);
        capText(c, font, "+", t.right() - 8, py, TEXT_DIM);

        //value box: pink outline, dark inside, the filled part in pink, the value centred
        c.rect(t.x, t.y, t.w, t.h, Colors.mix(GROUP_EDGE, accent, 0.45));
        c.rect(t.x + 1, t.y + 1, t.w - 2, t.h - 2, Colors.mix(0xFF19133B, accent, 0.18));
        c.rect(t.x + 2, t.y + 2, t.w - 4, t.h - 4, 0xFF19133B);
        double fill = (t.w - 4) * w.getFraction();
        if (fill > 0) c.gradientV(t.x + 2, t.y + 2, fill, t.h - 4, Colors.shade(accent, 0.9 + 0.1 * w.getHover()), Colors.shade(accent, 0.6));
        String value = w.getValueText();
        double vw = c.measure(font, value);
        capText(c, font, value, Math.round(t.centerX() - vw / 2), t.y + 3, TEXT_DIM);
    }

    protected void dropdown(Canvas c, Widget w, Rect b, String value, boolean open) {
        int accent = w.getMenu().getAccent();
        c.rect(b.x, b.y, b.w, b.h, open ? accent : Colors.mix(GROUP_EDGE, accent, 0.3 * w.getHover()));
        c.gradientV(b.x + 1, b.y + 1, b.w - 2, b.h - 2, CONTROL_BG, 0xFF17113C);
        //the pink button with three lines
        double bw = 18, bx = b.right() - bw - 1, by = b.y + 1, bh = b.h - 2;
        accentBox(c, bx, by, bw, bh, accent, w.getHover() + (open ? 1 : 0));
        for (int i = 0; i < 3; i++) c.rect(bx + 5, by + 4 + i * 3, bw - 10, 1, Colors.shade(accent, 0.55));
        Font font = text(c);
        capText(c, font, c.fit(font, value, b.w - bw - 12), b.x + 6, b.y + 5, TEXT_DIM);
    }

    @Override
    public void paintChoice(Canvas c, ChoiceWidget w) {
        label(c, w, w.box.x, w.bounds.y + 3, TEXT);
        dropdown(c, w, w.box, w.getSelected() == null ? "-" : w.getSelected(), w.isOpen());
    }

    @Override
    public void paintCombo(Canvas c, ComboWidget w) {
        label(c, w, w.box.x, w.bounds.y + 3, TEXT);
        String summary = w.getSummary();
        dropdown(c, w, w.box, summary.isEmpty() ? "-" : summary, w.isOpen());
    }

    @Override
    public void paintField(Canvas c, FieldWidget w) {
        Rect b = w.box;
        label(c, w, b.x, w.bounds.y + 3, TEXT);
        c.rect(b.x, b.y, b.w, b.h, w.isFocused() ? w.getMenu().getAccent() : GROUP_EDGE);
        c.rect(b.x + 1, b.y + 1, b.w - 2, b.h - 2, CONTROL_BG);
        Font font = text(c);
        String value = w.getText();
        double tw = c.measure(font, value);
        double offset = Math.min(0, b.w - 12 - tw);
        double ty = capsLine(c, font, b.y + 5);
        c.pushClip(b.x + 1, b.y + 1, b.w - 2, b.h - 2);
        c.text(font, value, b.x + 6 + offset, ty, TEXT);
        if (w.isCaretVisible()) c.rect(b.x + 6 + offset + tw + 1, b.y + 3, 1, b.h - 6, TEXT);
        c.popClip();
    }

    @Override
    public void paintColor(Canvas c, ColorWidget w) {
        if (!w.isAttached()) label(c, w, w.bounds.x + LABEL_X, w.bounds.y + 3, TEXT);
        Rect s = w.swatch;
        Color color = w.getColor();
        c.rect(s.x, s.y, s.w, s.h, GROUP_EDGE);
        c.rect(s.x + 1, s.y + 1, s.w - 2, s.h - 2, CONTROL_GAP);
        if (color.getAlpha() < 255) paintChecker(c, s.x + 2, s.y + 2, s.w - 4, s.h - 4);
        c.rect(s.x + 2, s.y + 2, s.w - 4, s.h - 4, color.getRGB());
    }

    @Override
    public void paintList(Canvas c, ListWidget w) {
        Rect b = w.box;
        double sh = w.search.h;
        c.rect(b.x, b.y - sh, b.w, b.h + sh, GROUP_EDGE);
        c.rect(b.x + 1, b.y + 1 - sh, b.w - 2, b.h - 2 + sh, CONTROL_BG);
        if (w.isSearchable()) {
            Rect s = w.search;
            c.rect(s.x + 1, s.y + 1, s.w - 2, s.h - 2, w.isSearchFocused() ? 0xFF231C4C : 0xFF1F1946);
            String q = w.getQuery();
            boolean empty = q.isEmpty() && !w.isSearchFocused();
            capText(c, text(c), empty ? "Search..." : q + (w.isSearchFocused() ? "_" : ""), s.x + 6, s.y + 5, empty ? 0xFF4E4874 : TEXT);
        }
        List<String> items = w.getItems();
        int accent = w.getMenu().getAccent();
        Font font = text(c);
        c.pushClip(b.x + 1, b.y + 1, b.w - 2, b.h - 2);
        for (int row = 0; row < w.visibleRows; row++) {
            int index = w.getScroll() + row;
            if (index >= items.size()) break;
            String item = items.get(index);
            double y = b.y + 1 + row * w.itemHeight;
            if (index == w.getHoveredIndex()) c.rect(b.x + 1, y, b.w - 2, w.itemHeight, 0xFF241D4E);
            boolean selected = item.equals(w.getSelected());
            capText(c, font, c.fit(font, item, b.w - 14), b.x + 6, y + 5, selected ? accent : TEXT_DIM);
        }
        c.popClip();
    }

    @Override
    public void paintKeybind(Canvas c, KeybindWidget w) {
        Rect b = w.bounds;
        boolean active = w.isCapturing();
        c.rect(b.x, b.y, b.w, b.h, active ? w.getMenu().getAccent() : GROUP_EDGE);
        c.rect(b.x + 1, b.y + 1, b.w - 2, b.h - 2, 0xFF1C1641);
        small(c);
        Font font = keybindFont;
        String text = w.getText(this);
        double tw = c.measure(font, text);
        int color = w.isRefused() ? 0xFFE04060 : active ? TEXT_BRIGHT : TEXT_DIM;
        capText(c, font, text, Math.round(b.centerX() - tw / 2), b.y + 4, color);
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
    public void layoutDropdown(Canvas c, DropdownPopup popup) {
        Rect box = popup.getOwner() instanceof ChoiceWidget ? ((ChoiceWidget) popup.getOwner()).box : ((ComboWidget) popup.getOwner()).box;
        int rows = Math.min(popupMaxRows, popup.getOptions().size());
        popup.itemHeight = popupItemHeight;
        popup.visibleRows = rows;
        double h = rows * popupItemHeight + 2;
        double y = box.bottom() - 1;
        Menu menu = popup.getOwner().getMenu();
        if (y + h > menu.getViewHeight() && box.y - h >= 0) y = box.y - h + 1;
        popup.bounds.set(box.x, y, box.w - 19, h);
    }

    @Override
    public void paintDropdown(Canvas c, DropdownPopup p) {
        Rect b = p.bounds;
        //the options roll open over a few frames
        double reveal = Math.min(1, p.getOpen());
        double shown = b.h * (1 - Math.pow(1 - reveal, 3));
        int accent = p.getOwner().getMenu().getAccent();
        c.pushClip(b.x, b.y, b.w, shown);
        c.rect(b.x, b.y, b.w, b.h, GROUP_EDGE);
        c.rect(b.x + 1, b.y + 1, b.w - 2, b.h - 2, 0xFF1C1645);
        List<String> options = p.getOptions();
        Font font = text(c);
        for (int row = 0; row < p.visibleRows; row++) {
            int index = p.getScroll() + row;
            if (index >= options.size()) break;
            double y = b.y + 1 + row * p.itemHeight;
            boolean hovered = index == p.getHovered();
            if (hovered) c.rect(b.x + 1, y, b.w - 2, p.itemHeight, 0xFF262057);
            boolean selected = p.getSource().isSelected(index);
            capText(c, font, c.fit(font, options.get(index), b.w - 10), b.x + 6, y + 5, selected ? accent : hovered ? TEXT : TEXT_DIM);
        }
        c.popClip();
    }

    @Override
    public void layoutContext(Canvas c, ContextPopup popup) {
        popup.itemHeight = popupItemHeight;
        double w = 0;
        for (String item : popup.getItems()) w = Math.max(w, c.measure(text(c), item));
        w += 16;
        double h = popup.getItems().size() * popupItemHeight + 2;
        Menu menu = popup.getOwner().getMenu();
        popup.bounds.set(Math.max(0, Math.min(popup.anchorX, menu.getViewWidth() - w)), Math.max(0, Math.min(popup.anchorY, menu.getViewHeight() - h)), w, h);
    }

    @Override
    public void paintContext(Canvas c, ContextPopup p) {
        Rect b = p.bounds;
        int accent = p.getOwner().getMenu().getAccent();
        c.rect(b.x, b.y, b.w, b.h, GROUP_EDGE);
        c.rect(b.x + 1, b.y + 1, b.w - 2, b.h - 2, 0xFF1C1645);
        Font font = text(c);
        for (int i = 0; i < p.getItems().size(); i++) {
            double y = b.y + 1 + i * p.itemHeight;
            if (i == p.getHovered()) c.rect(b.x + 1, y, b.w - 2, p.itemHeight, 0xFF262057);
            capText(c, font, p.getItems().get(i), b.x + 8, y + 5, i == p.getSelected() ? accent : TEXT);
        }
    }

    @Override
    public void layoutColorPicker(Canvas c, ColorPopup popup) {
        Rect swatch = popup.getWidget().swatch;
        double pad = 6, square = 110, bar = 8;
        double w = pad + square + pad, h = pad + square + pad + bar + pad + bar + pad;
        Menu menu = popup.getOwner().getMenu();
        double x = Math.min(swatch.right() - w, menu.getViewWidth() - w);
        double y = swatch.bottom() + 2;
        if (y + h > menu.getViewHeight()) y = swatch.y - h - 2;
        popup.bounds.set(Math.max(0, x), Math.max(0, y), w, h);
        popup.svSquare.set(popup.bounds.x + pad, popup.bounds.y + pad, square, square);
        popup.hueBar.set(popup.svSquare.x, popup.svSquare.bottom() + pad, square, bar);
        popup.alphaBar.set(popup.svSquare.x, popup.hueBar.bottom() + pad, square, bar);
    }

    @Override
    public void paintColorPicker(Canvas c, ColorPopup p) {
        Rect b = p.bounds;
        c.rect(b.x, b.y, b.w, b.h, GROUP_EDGE);
        c.rect(b.x + 1, b.y + 1, b.w - 2, b.h - 2, 0xFF1C1645);
        Rect sv = p.svSquare;
        c.gradientH(sv.x, sv.y, sv.w, sv.h, 0xFFFFFFFF, p.hueColor());
        c.gradientV(sv.x, sv.y, sv.w, sv.h, 0x00000000, 0xFF000000);
        double mx = sv.x + p.getSaturation() * sv.w, my = sv.y + (1 - p.getBrightness()) * sv.h;
        c.outline(mx - 2, my - 2, 5, 5, 0xFFFFFFFF);
        //horizontal hue bar
        Rect hue = p.hueBar;
        for (int i = 0; i < 6; i++) {
            int left = 0xFF000000 | Color.HSBtoRGB(i / 6f, 1, 1), right = 0xFF000000 | Color.HSBtoRGB((i + 1) / 6f, 1, 1);
            c.gradientH(hue.x + hue.w * i / 6, hue.y, hue.w / 6, hue.h, left, right);
        }
        c.rect(hue.x + p.getHue() * hue.w - 1, hue.y - 1, 2, hue.h + 2, 0xFFFFFFFF);
        Rect alpha = p.alphaBar;
        paintChecker(c, alpha.x, alpha.y, alpha.w, alpha.h);
        int rgb = p.getWidget().getColor().getRGB() & 0xFFFFFF;
        c.gradientH(alpha.x, alpha.y, alpha.w, alpha.h, rgb, 0xFF000000 | rgb);
        c.rect(alpha.x + p.getAlpha() / 255.0 * alpha.w - 1, alpha.y - 1, 2, alpha.h + 2, 0xFFFFFFFF);
    }

    /* ---- HUD ---- */

    @Override
    public double watermarkHeight(Canvas c) {
        return 24;
    }

    @Override
    public double watermarkWidth(Canvas c, Watermark watermark) {
        return Math.round(c.measure(text(c), watermark.getText()) + 20);
    }

    @Override
    public void paintWatermark(Canvas c, Watermark watermark, double x, double y, double w, double h, int accent) {
        c.rect(x, y, w, h, WINDOW_EDGE);
        c.gradientV(x + 1, y + 1, w - 2, h - 2, HEADER_TOP, HEADER_BOTTOM);
        c.gradientH(x + 1, y + 1, w - 2, 2, LINE_LEFT, accent);
        capText(c, text(c), watermark.getText(), x + 10, y + 10, TEXT_BRIGHT);
    }

    @Override
    public double notificationWidth(Canvas c, Notification n) {
        return 220;
    }

    @Override
    public double notificationHeight(Canvas c, Notification n) {
        return 28 + n.lines(c, text(c), 196).size() * 15 + 6;
    }

    @Override
    public void paintNotification(Canvas c, Notification n, double x, double y, double w, double h, double visibility, int accent) {
        c.setAlpha(visibility);
        c.rect(x, y, w, h, WINDOW_EDGE);
        c.gradientV(x + 1, y + 1, w - 2, h - 2, HEADER_TOP, HEADER_BOTTOM);
        c.gradientH(x + 1, y + 1, w - 2, 2, LINE_LEFT, accent);
        capText(c, bold(c), n.getTitle(), x + 12, y + 11, TEXT_BRIGHT);
        double ty = y + 28;
        for (String line : n.lines(c, text(c), w - 24)) {
            capText(c, text(c), line, x + 12, ty, TEXT_DIM);
            ty += 15;
        }
        c.setAlpha(1);
    }
}
