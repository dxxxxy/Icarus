package studio.dreamys.icarus.theme.skeet;

import studio.dreamys.icarus.hud.Notification;
import studio.dreamys.icarus.hud.Watermark;
import studio.dreamys.icarus.render.AtlasImage;
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

import java.awt.Color;
import java.util.List;
import java.util.Random;

/**
 * "Classic": a dark, dense layout with icon tabs on the left, a thin multi-colour accent strip, noisy dark
 * background, bevelled grey borders and gradient controls. Measurements are design pixels (see {@link BaseTheme}).
 */
public class SkeetStyleTheme extends BaseTheme {
    /* palette */
    protected static final int BLACK = 0xFF0A0A0A;
    protected static final int BORDER_LIGHT = 0xFF3C3C3C;
    protected static final int BORDER_MID = 0xFF282828;
    protected static final int TAB_BG = 0xFF0C0C0C;
    protected static final int GROUP_OUTER = 0xFF0C0C0C;
    protected static final int GROUP_FILL = 0xFF171717;
    protected static final int TEXT = 0xFFCDCDCD;
    protected static final int TEXT_DIM = 0xFF999999;
    protected static final int TEXT_HINT = 0xFF727272;
    protected static final int TITLE = 0xFFCBCBCB;
    protected static final int ICON_IDLE = 0xFF5A5A5A;
    protected static final int ICON_HOVER = 0xFF8C8C8C;
    protected static final int ICON_ACTIVE = 0xFFD2D2D2;
    protected static final int STRIP_LEFT = 0xFF3BAFDE, STRIP_MID = 0xFFCA46CD, STRIP_RIGHT = 0xFFC9E33A;

    /* window geometry */
    protected static final double WIDTH = 660, HEIGHT = 560;
    protected static final double FRAME = 7;
    protected static final double TAB_COLUMN = 75;
    protected static final double TAB_HEIGHT = 64;
    protected static final double TAB_TOP = 15;
    protected static final double CONTENT_LEFT = 96, CONTENT_TOP = 27, CONTENT_RIGHT = 18, CONTENT_BOTTOM = 18;
    protected static final double FOOTER = 14;

    private final GroupMetrics groupMetrics = new GroupMetrics(18, 10, 20, 22, 4, 30);
    private final Rect contentArea = new Rect();
    private AtlasImage noise;

    public SkeetStyleTheme() {
        textRow = 13;
        labelRow = 13;
        checkboxSize = 8;
        indent = 20;
        controlWidth = 162;
        controlRightReserve = 30;
        boxHeight = 20;
        labelGap = 2;
        buttonHeight = 25;
        sliderTrackHeight = 6;
        sliderValueOverhang = 6;
        swatchWidth = 16;
        swatchHeight = 8;
        listItemHeight = 16;
        listRows = 8;
        popupItemHeight = 20;
    }

    @Override
    public String getId() {
        return "classic";
    }

    @Override
    public String getDisplayName() {
        return "Classic";
    }

    @Override
    public int getDefaultAccent() {
        return 0xFF027FD4;
    }

    @Override
    public ConfigUiStyle getConfigUiStyle() {
        return ConfigUiStyle.PAGE;
    }

    @Override
    public char getConfigIcon() {
        return 'F';
    }

    @Override
    protected void createFonts(FontAtlas atlas) {
        String family = FontFamilies.first("Verdana", "DejaVu Sans", "Tahoma");
        String icons = FontFamilies.register(SkeetStyleTheme.class, "/assets/icarus/undefeated.ttf");
        setFonts(new Font(atlas, family, java.awt.Font.PLAIN, 12),
                new Font(atlas, family, java.awt.Font.BOLD, 12),
                new Font(atlas, family, java.awt.Font.BOLD, 10),
                new Font(atlas, icons, java.awt.Font.PLAIN, 42),
                new Font(atlas, family, java.awt.Font.BOLD, 12),
                new Font(atlas, family, java.awt.Font.PLAIN, 12));
    }

    /** A dark noise tile for the content background (generated, no image files). */
    protected AtlasImage noise() {
        if (noise == null) {
            int size = 64;
            int[] pixels = new int[size * size];
            Random random = new Random(0x5EE7);
            for (int i = 0; i < pixels.length; i++) {
                int x = i % size, y = i / size;
                int v = 0x0F + random.nextInt(4) + ((x + y) % 4 == 0 ? 1 : 0);
                pixels[i] = 0xFF000000 | v << 16 | v << 8 | v;
            }
            noise = new AtlasImage(pixels, size, size);
        }
        return noise;
    }

    /* ---- window ---- */

    @Override
    public void layout(Menu menu, Canvas c) {
        Rect w = menu.window;
        w.set(w.x, w.y, WIDTH, HEIGHT);
        menu.dragArea.set(w.x, w.y, w.w, CONTENT_TOP);

        List<TabView> tabs = menu.getTabs();
        double tabX = w.x + FRAME, tabY = w.y + FRAME + 2 + TAB_TOP;
        double available = HEIGHT - 2 * FRAME - 2 - TAB_TOP * 2;
        double tabHeight = Math.min(TAB_HEIGHT, available / Math.max(1, tabs.size()));
        for (int i = 0; i < tabs.size(); i++) tabs.get(i).bounds.set(tabX, tabY + i * tabHeight, TAB_COLUMN, tabHeight);
        for (int i = 0; i < menu.getCurrentTab().getSubTabs().size(); i++) menu.getCurrentTab().getSubTabs().get(i).bounds.clear();

        boolean footer = !menu.getFooterLeft().isEmpty() || !menu.getFooterCenter().isEmpty() || !menu.getFooterRight().isEmpty();
        double top = CONTENT_TOP;
        if (menu.getCurrentTab().hasSubTabs()) {
            //sub-tabs: a text row across the top of the content area
            double x = w.x + CONTENT_LEFT;
            for (int i = 0; i < menu.getCurrentTab().getSubTabs().size(); i++) {
                String label = menu.getCurrentTab().getSubTabs().get(i).getLabel();
                double lw = c.measure(bold(c), label) + 16;
                menu.getCurrentTab().getSubTabs().get(i).bounds.set(x, w.y + CONTENT_TOP - 4, lw, 20);
                x += lw + 4;
            }
            top += 24;
        }
        contentArea.set(w.x + CONTENT_LEFT, w.y + top, WIDTH - CONTENT_LEFT - CONTENT_RIGHT,
                HEIGHT - top - CONTENT_BOTTOM - (footer ? FOOTER : 0));
        menu.layoutColumns(c, contentArea, 2, 18, groupMetrics);
    }

    @Override
    public void paintWindow(Canvas c, Menu menu) {
        Rect w = menu.window;
        //bevelled frame: black, light, 4 × mid, light
        c.rect(w.x, w.y, w.w, w.h, BLACK);
        c.rect(w.x + 1, w.y + 1, w.w - 2, w.h - 2, BORDER_LIGHT);
        c.rect(w.x + 2, w.y + 2, w.w - 4, w.h - 4, BORDER_MID);
        c.rect(w.x + 6, w.y + 6, w.w - 12, w.h - 12, BORDER_LIGHT);
        double ix = w.x + FRAME, iy = w.y + FRAME, iw = w.w - 2 * FRAME, ih = w.h - 2 * FRAME;

        //tab column and noisy content
        c.rect(ix, iy, TAB_COLUMN, ih, TAB_BG);
        c.tile(noise(), ix + TAB_COLUMN, iy, iw - TAB_COLUMN, ih, 0xFFFFFFFF);
        c.vline(ix + TAB_COLUMN, iy, ih, 0xFF000000);
        c.vline(ix + TAB_COLUMN + 1, iy, ih, BORDER_MID);

        //accent strip (1px colour + 1px darker copy)
        double half = iw / 2;
        c.hlineGradient(ix, iy, half, STRIP_LEFT, STRIP_MID);
        c.hlineGradient(ix + half, iy, iw - half, STRIP_MID, STRIP_RIGHT);
        c.hlineGradient(ix, iy + 1, half, Colors.shade(STRIP_LEFT, 0.5), Colors.shade(STRIP_MID, 0.5));
        c.hlineGradient(ix + half, iy + 1, iw - half, Colors.shade(STRIP_MID, 0.5), Colors.shade(STRIP_RIGHT, 0.5));

        //tabs: the selected one merges into the content
        List<TabView> tabs = menu.getTabs();
        for (int i = 0; i < tabs.size(); i++) {
            TabView tab = tabs.get(i);
            Rect b = tab.bounds;
            boolean active = i == menu.getTabIndex();
            if (active) {
                c.tile(noise(), b.x, b.y, b.w + 2, b.h, 0xFFFFFFFF);
                c.hline(b.x, b.y, b.w + 1, 0xFF000000);
                c.hline(b.x, b.y + 1, b.w + 1, BORDER_MID);
                c.hline(b.x, b.bottom() - 2, b.w + 1, BORDER_MID);
                c.hline(b.x, b.bottom() - 1, b.w + 1, 0xFF000000);
            }
            boolean hovered = !active && b.contains(menu.getMouseX(), menu.getMouseY());
            String icon = iconString(tab.getIcon());
            Font iconFont = icon(c);
            double iconW = c.measure(iconFont, icon);
            c.text(iconFont, icon, Math.round(b.centerX() - iconW / 2), centeredTextY(c, iconFont, b.centerY()),
                    active ? ICON_ACTIVE : hovered ? ICON_HOVER : ICON_IDLE);
        }

        //sub-tabs
        for (int i = 0; i < menu.getCurrentTab().getSubTabs().size(); i++) {
            Rect b = menu.getCurrentTab().getSubTabs().get(i).bounds;
            if (b.isEmpty()) continue;
            boolean active = i == menu.getSubTabIndex();
            String label = menu.getCurrentTab().getSubTabs().get(i).getLabel();
            c.text(bold(c), label, b.x + 8, centeredTextY(c, bold(c), b.centerY()), active ? menu.getAccent() : TEXT_DIM);
        }

        //footer
        double fy = w.bottom() - FRAME - CONTENT_BOTTOM + 2;
        if (!menu.getFooterLeft().isEmpty()) c.text(text(c), menu.getFooterLeft(), contentArea.x, fy, TEXT_HINT);
        if (!menu.getFooterCenter().isEmpty()) {
            double tw = c.measure(text(c), menu.getFooterCenter());
            c.text(text(c), menu.getFooterCenter(), Math.round(contentArea.centerX() - tw / 2), fy, TEXT_HINT);
        }
        if (!menu.getFooterRight().isEmpty()) {
            double tw = c.measure(text(c), menu.getFooterRight());
            c.text(text(c), menu.getFooterRight(), contentArea.right() - tw, fy, TEXT_HINT);
        }
    }

    private final char[] iconChars = new char[1];
    private String lastIcon;

    private String iconString(char icon) {
        if (lastIcon == null || iconChars[0] != icon) {
            iconChars[0] = icon;
            lastIcon = String.valueOf(icon);
        }
        return lastIcon;
    }

    @Override
    public void paintGroup(Canvas c, Menu menu, GroupView g) {
        Rect f = g.frame;
        String title = g.getLabel();
        Font font = bold(c);
        double titleW = c.measure(font, title);
        double gapStart = f.x + 10, gapEnd = f.x + 14 + titleW;

        c.rect(f.x + 2, f.y + 2, f.w - 4, f.h - 4, GROUP_FILL);
        //outer and inner outline, top edge interrupted by the title
        groupEdge(c, f.x, f.y, f.w, f.h, GROUP_OUTER, gapStart, gapEnd);
        groupEdge(c, f.x + 1, f.y + 1, f.w - 2, f.h - 2, BORDER_MID, gapStart, gapEnd);
        c.text(font, title, f.x + 12, Math.round(f.y - c.lineHeight(font) / 2), TITLE);
    }

    private static void groupEdge(Canvas c, double x, double y, double w, double h, int color, double gapStart, double gapEnd) {
        c.hline(x, y, gapStart - x, color);
        c.hline(gapEnd, y, x + w - gapEnd, color);
        c.hline(x, y + h - 1, w, color);
        c.vline(x, y, h, color);
        c.vline(x + w - 1, y, h, color);
    }

    @Override
    protected int scrollbarTrack() {
        return 0xFF1D1D1D;
    }

    @Override
    protected int scrollbarThumb() {
        return 0xFF3C3C3C;
    }

    /* ---- widgets ---- */

    protected void label(Canvas c, String label, double x, double rowY, int color) {
        c.text(text(c), label, x, Math.round(rowY + (textRow - c.lineHeight(text(c))) / 2), color);
    }

    @Override
    public void paintCheckbox(Canvas c, CheckboxWidget w) {
        Rect b = w.box;
        c.rect(b.x, b.y, b.w, b.h, BLACK);
        int accent = w.getMenu().getAccent();
        double hover = w.getHover();
        if (w.isChecked()) {
            c.gradientV(b.x + 1, b.y + 1, b.w - 2, b.h - 2, Colors.shade(accent, 1 + 0.15 * hover), Colors.shade(accent, 0.62));
        } else {
            c.gradientV(b.x + 1, b.y + 1, b.w - 2, b.h - 2, Colors.mix(0xFF4D4D4D, 0xFF5A5A5A, hover), 0xFF333333);
        }
        label(c, w.getLabel(), w.bounds.x + indent, w.bounds.y, TEXT);
    }

    @Override
    public void paintButton(Canvas c, ButtonWidget w) {
        Rect b = w.button;
        c.rect(b.x, b.y, b.w, b.h, BLACK);
        c.outline(b.x + 1, b.y + 1, b.w - 2, b.h - 2, 0xFF323232);
        int top = Colors.mix(0xFF212121, 0xFF292929, w.getHover()), bottom = 0xFF1E1E1E;
        if (w.isPressed()) {
            int t = top;
            top = bottom;
            bottom = t;
        }
        c.gradientV(b.x + 2, b.y + 2, b.w - 4, b.h - 4, top, bottom);
        String label = c.fit(text(c), w.getLabel(), b.w - 8);
        double tw = c.measure(text(c), label);
        c.text(text(c), label, Math.round(b.centerX() - tw / 2), centeredTextY(c, text(c), b.centerY()), TEXT);
    }

    @Override
    public void paintSlider(Canvas c, SliderWidget w) {
        Rect t = w.track;
        label(c, w.getLabel(), t.x, w.bounds.y, TEXT);
        int accent = w.getMenu().getAccent();
        c.rect(t.x, t.y, t.w, t.h, BLACK);
        c.gradientV(t.x + 1, t.y + 1, t.w - 2, t.h - 2, 0xFF444444, 0xFF343434);
        double fill = (t.w - 2) * w.getFraction();
        c.gradientV(t.x + 1, t.y + 1, fill, t.h - 2, Colors.shade(accent, 1.15), Colors.shade(accent, 0.7));

        Font font = small(c);
        String value = w.getValueText();
        double vw = c.measure(font, value);
        double vx = Math.round(Math.max(t.x - vw / 2, Math.min(t.right() - vw / 2, t.x + 1 + fill - vw / 2)));
        c.textOutline(font, value, vx, t.bottom() - c.lineHeight(font) / 2, TEXT, 0xFF000000);
    }

    protected void dropdownBox(Canvas c, Rect b, double hover, boolean open) {
        c.rect(b.x, b.y, b.w, b.h, BLACK);
        c.gradientV(b.x + 1, b.y + 1, b.w - 2, b.h - 2, Colors.mix(0xFF1F1F1F, 0xFF262626, hover), Colors.mix(0xFF232323, 0xFF2A2A2A, hover));
        double ax = b.right() - 10, ay = b.centerY();
        if (open) c.triangle(ax - 3, ay + 1.5, ax + 3, ay + 1.5, ax, ay - 1.5, TEXT_DIM);
        else c.triangle(ax - 3, ay - 1.5, ax + 3, ay - 1.5, ax, ay + 1.5, TEXT_DIM);
    }

    @Override
    public void paintChoice(Canvas c, ChoiceWidget w) {
        label(c, w.getLabel(), w.box.x, w.bounds.y, TEXT);
        dropdownBox(c, w.box, w.getHover(), w.isOpen());
        String value = w.getSelected() == null ? "-" : w.getSelected();
        c.text(text(c), c.fit(text(c), value, w.box.w - 26), w.box.x + 10, centeredTextY(c, text(c), w.box.centerY()), TEXT_DIM);
    }

    @Override
    public void paintCombo(Canvas c, ComboWidget w) {
        label(c, w.getLabel(), w.box.x, w.box.y - labelGap - labelRow, TEXT);
        dropdownBox(c, w.box, w.getHover(), w.isOpen());
        c.text(text(c), c.fit(text(c), w.getSummary(), w.box.w - 26), w.box.x + 10, centeredTextY(c, text(c), w.box.centerY()), TEXT_DIM);
    }

    @Override
    public void paintField(Canvas c, FieldWidget w) {
        Rect b = w.box;
        label(c, w.getLabel(), b.x, b.y - labelGap - labelRow, TEXT);
        c.rect(b.x, b.y, b.w, b.h, BLACK);
        c.rect(b.x + 1, b.y + 1, b.w - 2, b.h - 2, w.isFocused() ? 0xFF262626 : Colors.mix(0xFF1F1F1F, 0xFF242424, w.getHover()));
        Font font = text(c);
        String value = w.getText();
        double inner = b.w - 16;
        double tw = c.measure(font, value);
        double offset = Math.min(0, inner - tw - 2);
        double ty = centeredTextY(c, font, b.centerY());
        c.pushClip(b.x + 1, b.y + 1, b.w - 2, b.h - 2);
        c.text(font, value, b.x + 8 + offset, ty, TEXT);
        if (w.isCaretVisible()) c.rect(b.x + 8 + offset + tw + 1, ty + 1, 1, c.lineHeight(font) - 2, TEXT);
        c.popClip();
    }

    @Override
    public void paintColor(Canvas c, ColorWidget w) {
        label(c, w.getLabel(), w.bounds.x + indent, w.bounds.y, TEXT);
        Rect s = w.swatch;
        c.rect(s.x, s.y, s.w, s.h, BLACK);
        Color color = w.getColor();
        if (color.getAlpha() < 255) paintChecker(c, s.x + 1, s.y + 1, s.w - 2, s.h - 2);
        int argb = color.getRGB();
        c.gradientV(s.x + 1, s.y + 1, s.w - 2, s.h - 2, argb, Colors.shade(argb, 0.8));
    }

    @Override
    public void paintList(Canvas c, ListWidget w) {
        Rect b = w.box;
        c.rect(b.x, b.y, b.w, b.h, BLACK);
        c.rect(b.x + 1, b.y + 1, b.w - 2, b.h - 2, 0xFF1B1B1B);
        List<String> items = w.getItems();
        Font font = text(c);
        c.pushClip(b.x + 1, b.y + 1, b.w - 2, b.h - 2);
        for (int row = 0; row < w.visibleRows; row++) {
            int index = w.getScroll() + row;
            if (index >= items.size()) break;
            String item = items.get(index);
            double y = b.y + 1 + row * w.itemHeight;
            if (index == w.getHoveredIndex()) c.rect(b.x + 1, y, b.w - 2, w.itemHeight, 0xFF232323);
            boolean selected = item.equals(w.getSelected());
            c.text(selected ? bold(c) : font, c.fit(font, item, b.w - 16), b.x + 8, centeredTextY(c, font, y + w.itemHeight / 2),
                    selected ? w.getMenu().getAccent() : TEXT);
        }
        c.popClip();
        if (items.size() > w.visibleRows) {
            double thumbH = Math.max(10, b.h * w.visibleRows / items.size());
            double thumbY = b.y + (b.h - thumbH) * w.getScroll() / (items.size() - w.visibleRows);
            c.rect(b.right() - 4, thumbY, 3, thumbH, 0xFF3C3C3C);
        }
    }

    @Override
    public void paintKeybind(Canvas c, KeybindWidget w) {
        int color = w.isRefused() ? 0xFFD04040 : w.isCapturing() ? w.getMenu().getAccent() : TEXT_HINT;
        c.text(small(c), w.getText(this), w.bounds.x, centeredTextY(c, small(c), w.bounds.centerY()), color);
    }

    @Override
    public String formatKeybind(int key, boolean capturing, boolean refused) {
        if (refused) return "[in use]";
        if (capturing) return "[...]";
        if (key <= 0) return "[-]";
        return "[" + keyName(key) + "]";
    }

    /* ---- popups ---- */

    @Override
    public void paintDropdown(Canvas c, DropdownPopup p) {
        Rect b = p.bounds;
        c.rect(b.x, b.y, b.w, b.h, BLACK);
        c.rect(b.x + 1, b.y + 1, b.w - 2, b.h - 2, 0xFF191919);
        List<String> options = p.getOptions();
        int accent = p.getOwner().getMenu().getAccent();
        for (int row = 0; row < p.visibleRows; row++) {
            int index = p.getScroll() + row;
            if (index >= options.size()) break;
            double y = b.y + row * p.itemHeight;
            if (index == p.getHovered()) c.rect(b.x + 1, y + (row == 0 ? 1 : 0), b.w - 2, p.itemHeight - (row == 0 ? 1 : 0), 0xFF232323);
            boolean selected = p.getSource().isSelected(index);
            Font font = selected ? bold(c) : text(c);
            c.text(font, c.fit(font, options.get(index), b.w - 20), b.x + 10, centeredTextY(c, font, y + p.itemHeight / 2), selected ? accent : TEXT_DIM);
        }
    }

    @Override
    public void paintColorPicker(Canvas c, ColorPopup p) {
        Rect b = p.bounds;
        c.rect(b.x, b.y, b.w, b.h, BLACK);
        c.rect(b.x + 1, b.y + 1, b.w - 2, b.h - 2, BORDER_MID);
        c.rect(b.x + 2, b.y + 2, b.w - 4, b.h - 4, GROUP_FILL);
        paintPickerParts(c, p, BLACK);
    }

    /* ---- HUD ---- */

    @Override
    public double watermarkHeight(Canvas c) {
        return 4 + 2 + 3 + textRow + 3 + 4;
    }

    @Override
    public double watermarkWidth(Canvas c, Watermark watermark) {
        return Math.round(c.measure(text(c), watermark.getText()) + 2 * 4 + 12);
    }

    protected void hudFrame(Canvas c, double x, double y, double w, double h) {
        c.rect(x, y, w, h, BLACK);
        c.rect(x + 1, y + 1, w - 2, h - 2, BORDER_LIGHT);
        c.rect(x + 2, y + 2, w - 4, h - 4, BORDER_MID);
        c.rect(x + 3, y + 3, w - 6, h - 6, BORDER_LIGHT);
        c.tile(noise(), x + 4, y + 4, w - 8, h - 8, 0xFFFFFFFF);
        double half = (w - 8) / 2;
        c.hlineGradient(x + 4, y + 4, half, STRIP_LEFT, STRIP_MID);
        c.hlineGradient(x + 4 + half, y + 4, w - 8 - half, STRIP_MID, STRIP_RIGHT);
        c.hlineGradient(x + 4, y + 5, half, Colors.shade(STRIP_LEFT, 0.5), Colors.shade(STRIP_MID, 0.5));
        c.hlineGradient(x + 4 + half, y + 5, w - 8 - half, Colors.shade(STRIP_MID, 0.5), Colors.shade(STRIP_RIGHT, 0.5));
    }

    @Override
    public void paintWatermark(Canvas c, Watermark watermark, double x, double y, double w, double h, int accent) {
        hudFrame(c, x, y, w, h);
        c.text(text(c), watermark.getText(), x + 10, Math.round(y + 4 + 2 + 3), TEXT);
    }

    @Override
    public double notificationWidth(Canvas c, Notification n) {
        return 220;
    }

    @Override
    public double notificationHeight(Canvas c, Notification n) {
        return 4 + 2 + 6 + textRow + 4 + n.lines(c, text(c), 220 - 28).size() * textRow + 8 + 4;
    }

    @Override
    public void paintNotification(Canvas c, Notification n, double x, double y, double w, double h, double visibility, int accent) {
        c.setAlpha(visibility);
        hudFrame(c, x, y, w, h);
        double ty = y + 4 + 2 + 6;
        c.text(bold(c), n.getTitle(), x + 14, ty, TEXT);
        ty += textRow + 4;
        for (String line : n.lines(c, text(c), w - 28)) {
            c.text(text(c), line, x + 14, ty, TEXT_DIM);
            ty += textRow;
        }
        c.setAlpha(1);
    }
}
