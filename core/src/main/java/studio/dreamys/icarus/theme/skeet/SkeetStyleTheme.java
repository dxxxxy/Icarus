package studio.dreamys.icarus.theme.skeet;

import studio.dreamys.icarus.hud.Toast;
import studio.dreamys.icarus.hud.Watermark;
import studio.dreamys.icarus.render.AtlasImage;
import studio.dreamys.icarus.render.Canvas;
import studio.dreamys.icarus.render.Font;
import studio.dreamys.icarus.render.FontAtlas;
import studio.dreamys.icarus.settings.Setting;
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

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The classic dark menu: icon tabs on the left, a thin multi-colour strip, a striped background, bevelled frame
 * and gradient controls.
 * <p>
 * Every measurement was taken from the reference footage, where the menu is drawn at 125%; constants are written
 * in those reference pixels and converted with {@link #P} so they can be checked against it directly. The frame
 * bevel is a fixed number of screen pixels at every scale, as in the original.
 */
public class SkeetStyleTheme extends BaseTheme {
    /* palette (reference values; near-black ones corrected for the video's lifted blacks) */
    protected static final int BLACK = 0xFF0C0C0C;
    protected static final int BEVEL_LIGHT = 0xFF3C3C3C;
    protected static final int BEVEL_MID = 0xFF282828;
    protected static final int TAB_BG = 0xFF0C0C0C;
    protected static final int DIVIDER_DARK = 0xFF000000;
    protected static final int DIVIDER_LIGHT = 0xFF2C2C2C;
    protected static final int STRIPE_A = 0xFF0C0C0C, STRIPE_B = 0xFF0F0F0F;
    protected static final int GROUP_EDGE = 0xFF282828;
    protected static final int GROUP_FILL = 0xFF171717;
    protected static final int TEXT = 0xFFCDCDCD;
    protected static final int TEXT_WARNING = 0xFFB6B665;
    protected static final int TEXT_VALUE = 0xFF9A9A9A;
    protected static final int TEXT_KEYBIND = 0xFF727272;
    protected static final int ICON_IDLE = 0xFF5A5A5A;
    protected static final int ICON_HOVER = 0xFF8C8C8C;
    protected static final int ICON_ACTIVE = 0xFFD2D2D2;
    protected static final int STRIP_LEFT = 0xFF37B1DA, STRIP_MID = 0xFFC93ACD, STRIP_RIGHT = 0xFFC8E33A;

    /** Reference pixels (the footage is at 125%) to design units. */
    protected static double P(double referencePixels) {
        return referencePixels / 1.25;
    }

    /* window, in reference pixels from the window's top-left */
    protected static final double WIDTH = P(826), HEIGHT = P(701);
    /** Frame bevel in screen pixels: black, light, 3 × mid, light, black. */
    protected static final int FRAME_PX = 7;
    protected static final double TAB_RIGHT = 98, TAB_TOP = 20, TAB_PITCH = 80, TAB_ICON_X = 52, TAB_ICON_Y = 59;
    protected static final double CONTENT_X = 119, CONTENT_Y = 30, CONTENT_RIGHT = 802, CONTENT_BOTTOM = 677, COLUMN_GAP = 19;

    /* rows, in reference pixels from the row top (= the label's line top; capitals start 3 below) */
    protected static final double GROUP_PAD_TOP = 24, GROUP_PAD_BOTTOM = 10;
    protected static final double CHECKBOX_X = 28, LABEL_X = 53, CONTROL_W = 206, SLIDER_W = 208;
    protected static final double ROW_CHECKBOX = 21, ROW_LABEL = 20, ROW_SLIDER = 36, ROW_DROPDOWN = 49, ROW_BUTTON = 40;
    protected static final double BOX_H = 25, BUTTON_H = 32, TRACK_H = 10, CHECK = 10;
    /** Sub-tabs: a full-width header group with an icon per sub-tab (the "Weapon type" row). */
    protected static final double SUBTAB_H = 57, SUBTAB_GAP = 23, SUBTAB_X = 6, SUBTAB_PITCH = 82, SUBTAB_SLOT_H = 40, WEAPON_ICON = 31;
    /** Where a weapon bitmap's cell starts, left of and above its slot (the bitmaps are 90x44 cells). */
    protected static final double WEAPON_CELL_X = 4, WEAPON_CELL_Y = 3;
    protected static final int TEXT_DISABLED = 0xFF5C5C5C;
    protected static final double KEYBIND_RIGHT = 33, SWATCH_RIGHT = 31, SWATCH_W = 21, SWATCH_H = 12;

    private final GroupMetrics groupMetrics = new GroupMetrics(P(GROUP_PAD_TOP), P(GROUP_PAD_BOTTOM), 0, 0, 0, P(40));
    private final Rect contentArea = new Rect();
    private final Rect subTabArea = new Rect();
    private AtlasImage stripes;
    private final Map<String, AtlasImage> icons = new HashMap<>();

    public SkeetStyleTheme() {
        textRow = P(ROW_LABEL);
        labelRow = P(16);
        checkboxSize = P(CHECK);
        indent = P(LABEL_X);
        controlWidth = P(CONTROL_W);
        boxHeight = P(BOX_H);
        buttonHeight = P(BUTTON_H);
        sliderTrackHeight = P(TRACK_H);
        swatchWidth = P(SWATCH_W);
        swatchHeight = P(SWATCH_H);
        listItemHeight = P(20);
        listRows = 7;
        popupItemHeight = P(25);
        popupMaxRows = 12;
    }

    @Override
    public String getId() {
        return "skeet";
    }

    @Override
    public String getDisplayName() {
        return "Skeet";
    }

    @Override
    public int getDefaultAccent() {
        return 0xFF10A8F6;
    }

    @Override
    public ConfigUiStyle getConfigUiStyle() {
        return ConfigUiStyle.PAGE;
    }

    @Override
    public char getConfigIcon() {
        return ' ';
    }

    @Override
    protected void createFonts(FontAtlas atlas) {
        String family = FontFamilies.first("Verdana", "DejaVu Sans", "Tahoma");
        Font text = new Font(atlas, family, java.awt.Font.PLAIN, P(11));
        Font bold = new Font(atlas, family, java.awt.Font.BOLD, P(11));
        String icons = FontFamilies.register(SkeetStyleTheme.class, "/assets/icarus/undefeated.ttf");
        setFonts(text, bold, bold, new Font(atlas, icons, java.awt.Font.PLAIN, P(WEAPON_ICON)), bold, text);
    }

    /* ---- resources ---- */

    /** Background: 1-pixel vertical stripes, tiled at screen resolution. */
    protected AtlasImage stripes() {
        if (stripes == null) {
            int[] pixels = new int[4];
            pixels[0] = STRIPE_A;
            pixels[1] = STRIPE_B;
            pixels[2] = STRIPE_A;
            pixels[3] = STRIPE_B;
            stripes = new AtlasImage(pixels, 2, 2);
        }
        return stripes;
    }

    /** A tab icon from the theme's resources (white with alpha), or null. */
    protected AtlasImage icon(String name) {
        if (name == null || name.isEmpty()) return null;
        if (icons.containsKey(name)) return icons.get(name);
        AtlasImage image = null;
        try (InputStream in = SkeetStyleTheme.class.getResourceAsStream("/assets/icarus/skeet/" + name + ".png")) {
            if (in != null) {
                BufferedImage img = ImageIO.read(in);
                int[] argb = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
                image = new AtlasImage(argb, img.getWidth(), img.getHeight());
            }
        } catch (Exception ignored) {
        }
        icons.put(name, image);
        return image;
    }

    /* ---- window ---- */

    @Override
    public void layout(Menu menu, Canvas c) {
        Rect w = menu.window;
        w.set(w.x, w.y, WIDTH, HEIGHT);
        menu.dragArea.set(w.x, w.y, w.w, P(CONTENT_Y));

        List<TabView> tabs = menu.getTabs();
        for (int i = 0; i < tabs.size(); i++) {
            tabs.get(i).bounds.set(w.x + px(c, FRAME_PX), w.y + P(TAB_TOP + TAB_PITCH * i), P(TAB_RIGHT) - px(c, FRAME_PX), P(TAB_PITCH));
            for (int j = 0; j < tabs.get(i).getSubTabs().size(); j++) tabs.get(i).getSubTabs().get(j).bounds.clear();
        }

        double top = CONTENT_Y;
        subTabArea.clear();
        TabView current = menu.getCurrentTab();
        if (current.hasSubTabs()) {
            subTabArea.set(w.x + P(CONTENT_X), w.y + P(CONTENT_Y), P(CONTENT_RIGHT - CONTENT_X), P(SUBTAB_H));
            for (int j = 0; j < current.getSubTabs().size(); j++) {
                current.getSubTabs().get(j).bounds.set(subTabArea.x + P(SUBTAB_X + SUBTAB_PITCH * j), subTabArea.y + P(10), P(SUBTAB_PITCH), P(SUBTAB_SLOT_H));
            }
            top += SUBTAB_H + SUBTAB_GAP;
        }
        contentArea.set(w.x + P(CONTENT_X), w.y + P(top), P(CONTENT_RIGHT - CONTENT_X), P(CONTENT_BOTTOM) - P(top));
        menu.layoutColumns(c, contentArea, 2, P(COLUMN_GAP), groupMetrics);
    }

    /** {@code pixels} screen pixels in design units. */
    protected static double px(Canvas c, double pixels) {
        return pixels / c.getScale();
    }

    @Override
    public void paintWindow(Canvas c, Menu menu) {
        Rect w = menu.window;
        double p = px(c, 1);

        //bevel: black, light, 3 × mid, light, black (screen pixels)
        c.rect(w.x, w.y, w.w, w.h, BLACK);
        c.rect(w.x + p, w.y + p, w.w - 2 * p, w.h - 2 * p, BEVEL_LIGHT);
        c.rect(w.x + 2 * p, w.y + 2 * p, w.w - 4 * p, w.h - 4 * p, BEVEL_MID);
        c.rect(w.x + 5 * p, w.y + 5 * p, w.w - 10 * p, w.h - 10 * p, BEVEL_LIGHT);
        c.rect(w.x + 6 * p, w.y + 6 * p, w.w - 12 * p, w.h - 12 * p, BLACK);
        double ix = w.x + FRAME_PX * p, iy = w.y + FRAME_PX * p, iw = w.w - 2 * FRAME_PX * p, ih = w.h - 2 * FRAME_PX * p;

        //tab column and striped content
        double tabRight = w.x + P(TAB_RIGHT);
        c.rect(ix, iy, tabRight - ix, ih, TAB_BG);
        c.tile(stripes(), tabRight + 2 * p, iy, ix + iw - tabRight - 2 * p, ih, 0xFFFFFFFF);

        List<TabView> tabs = menu.getTabs();
        int active = menu.getTabIndex();
        Rect activeBounds = active >= 0 && active < tabs.size() ? tabs.get(active).bounds : null;

        //divider, interrupted by the active tab
        if (activeBounds == null) {
            c.vline(tabRight, iy, ih, DIVIDER_DARK);
            c.vline(tabRight + p, iy, ih, DIVIDER_LIGHT);
        } else {
            double top = activeBounds.y - 2 * p, bottom = activeBounds.bottom() + 2 * p;
            c.vline(tabRight, iy, top - iy, DIVIDER_DARK);
            c.vline(tabRight + p, iy, top - iy, DIVIDER_LIGHT);
            c.vline(tabRight, bottom, iy + ih - bottom, DIVIDER_DARK);
            c.vline(tabRight + p, bottom, iy + ih - bottom, DIVIDER_LIGHT);
            //the active tab opens into the content: striped, with lines above and below
            c.tile(stripes(), ix, activeBounds.y, tabRight + 2 * p - ix, activeBounds.h, 0xFFFFFFFF);
            c.hline(ix, activeBounds.y - 2 * p, tabRight + p - ix, DIVIDER_DARK);
            c.hline(ix, activeBounds.y - p, tabRight + 2 * p - ix, DIVIDER_LIGHT);
            c.hline(ix, activeBounds.bottom(), tabRight + 2 * p - ix, DIVIDER_LIGHT);
            c.hline(ix, activeBounds.bottom() + p, tabRight + p - ix, DIVIDER_DARK);
        }

        //accent strip: one bright row and a half-bright one under the top bevel
        double half = iw / 2;
        c.hlineGradient(ix, iy, half, STRIP_LEFT, STRIP_MID);
        c.hlineGradient(ix + half, iy, iw - half, STRIP_MID, STRIP_RIGHT);
        c.hlineGradient(ix, iy + p, half, Colors.shade(STRIP_LEFT, 0.53), Colors.shade(STRIP_MID, 0.53));
        c.hlineGradient(ix + half, iy + p, iw - half, Colors.shade(STRIP_MID, 0.53), Colors.shade(STRIP_RIGHT, 0.53));

        //tab icons
        for (int i = 0; i < tabs.size(); i++) {
            TabView tab = tabs.get(i);
            Rect b = tab.bounds;
            boolean isActive = i == active;
            boolean hovered = !isActive && b.contains(menu.getMouseX(), menu.getMouseY());
            int color = isActive ? ICON_ACTIVE : hovered ? ICON_HOVER : ICON_IDLE;
            double cx = w.x + P(TAB_ICON_X), cy = w.y + P(TAB_ICON_Y + TAB_PITCH * i);
            String name = tab.getPage() == null ? "config" : tab.getPage().getIconName();
            AtlasImage image = icon(name);
            if (image != null) {
                //icons are reference-sized bitmaps: draw them at their pixel size for the reference scale
                double iwd = P(image.getWidth()), ihd = P(image.getHeight());
                c.image(image, snap(c, cx - iwd / 2), snap(c, cy - ihd / 2), iwd, ihd, color);
            } else if (tab.getPage() != null) {
                String glyph = String.valueOf(tab.getIcon());
                Font iconFont = icon(c);
                double gw = c.measure(iconFont, glyph);
                c.text(iconFont, glyph, Math.round(cx - gw / 2), centeredTextY(c, iconFont, cy), color);
            }
        }

        if (!subTabArea.isEmpty()) paintSubTabs(c, menu);
    }

    /** The sub-tab row: a group box titled with the page's label, holding one icon per sub-tab. */
    protected void paintSubTabs(Canvas c, Menu menu) {
        Rect f = subTabArea;
        double p = px(c, 1);
        String title = menu.getCurrentTab().getLabel();
        Font font = bold(c);
        double titleW = c.measure(font, title);
        double gapStart = f.x + P(7), gapEnd = f.x + P(14) + titleW + P(3);
        c.rect(f.x + 2 * p, f.y + 2 * p, f.w - 4 * p, f.h - 4 * p, GROUP_FILL);
        groupEdge(c, f.x, f.y, f.w, f.h, BLACK, gapStart, gapEnd, p);
        groupEdge(c, f.x + p, f.y + p, f.w - 2 * p, f.h - 2 * p, GROUP_EDGE, gapStart, gapEnd, p);
        c.text(font, title, f.x + P(14), capsTop(c, font, f.y - P(4)), TEXT);

        Font iconFont = icon(c);
        java.util.List<studio.dreamys.icarus.ui.SubTabView> subs = menu.getCurrentTab().getSubTabs();
        for (int j = 0; j < subs.size(); j++) {
            Rect b = subs.get(j).bounds;
            boolean active = j == menu.getSubTabIndex();
            boolean hovered = !active && b.contains(menu.getMouseX(), menu.getMouseY());
            studio.dreamys.icarus.settings.SubPageNode node = subs.get(j).getSubPage();
            char icon = node == null ? ' ' : node.getIcon();
            String text = icon == ' ' ? subs.get(j).getLabel() : String.valueOf(icon);
            int color = active ? ICON_ACTIVE : hovered ? ICON_HOVER : ICON_IDLE;
            //skeet's own weapon bitmaps (cut from the reference, named by code point) replace the font glyphs it has
            AtlasImage bitmap = icon == ' ' ? null : icon("weapon_" + (int) icon);
            if (bitmap != null) {
                c.image(bitmap, snap(c, b.x - P(WEAPON_CELL_X)), snap(c, b.y - P(WEAPON_CELL_Y)), P(bitmap.getWidth()), P(bitmap.getHeight()), color);
                continue;
            }
            Font f2 = icon == ' ' ? bold(c) : iconFont;
            double tw = c.measure(f2, text);
            c.text(f2, text, snap(c, b.x + (b.w - tw) / 2), centeredTextY(c, f2, b.centerY() + (icon == ' ' ? 0 : P(3))), color);
        }
    }

    /** Rounds a design coordinate to a whole screen pixel. */
    protected static double snap(Canvas c, double design) {
        return Math.round(design * c.getScale()) / c.getScale();
    }

    @Override
    public void paintGroup(Canvas c, Menu menu, GroupView g) {
        Rect f = g.frame;
        double p = px(c, 1);
        String title = g.getLabel();
        Font font = bold(c);
        double titleW = c.measure(font, title);
        double gapStart = f.x + P(7), gapEnd = f.x + P(15) + titleW + P(3);

        c.rect(f.x + 2 * p, f.y + 2 * p, f.w - 4 * p, f.h - 4 * p, GROUP_FILL);
        groupEdge(c, f.x, f.y, f.w, f.h, BLACK, gapStart, gapEnd, p);
        groupEdge(c, f.x + p, f.y + p, f.w - 2 * p, f.h - 2 * p, GROUP_EDGE, gapStart, gapEnd, p);
        //title centred on the top edge (capitals 4 reference pixels above it)
        c.text(font, title, f.x + P(14), capsTop(c, font, f.y - P(4)), TEXT);
    }

    private static void groupEdge(Canvas c, double x, double y, double w, double h, int color, double gapStart, double gapEnd, double p) {
        c.hline(x, y, gapStart - x, color);
        c.hline(gapEnd, y, x + w - gapEnd, color);
        c.hline(x, y + h - p, w, color);
        c.vline(x, y, h, color);
        c.vline(x + w - p, y, h, color);
    }

    /** Line top that puts a font's capitals at {@code capTop}. */
    protected double capsTop(Canvas c, Font font, double capTop) {
        double capHeight = Math.round(font.getSize() * c.getScale() * 0.727) / c.getScale();
        return capTop - (Math.round(c.ascent(font) * c.getScale()) / c.getScale() - capHeight) + P(1);
    }

    @Override
    public void paintGroupForeground(Canvas c, Menu menu, GroupView g) {
        if (!g.isScrollable()) return;
        Rect f = g.frame, content = g.content;
        double p = px(c, 1);
        //scroll bar inside the right edge, and a fade + arrow when there is more below
        double trackX = f.right() - P(10), trackW = P(5);
        double thumbH = Math.max(P(14), content.h * content.h / g.getContentHeight());
        double thumbY = content.y + (content.h - thumbH) * g.getScroll() / (g.getContentHeight() - content.h);
        c.rect(trackX, content.y, trackW, content.h, 0xFF202020);
        c.rect(trackX, thumbY, trackW, thumbH, 0xFF404040);
        if (g.getScroll() < g.getContentHeight() - content.h - 0.5) {
            double fadeH = P(20);
            c.gradientV(f.x + 2 * p, f.bottom() - 2 * p - fadeH, trackX - f.x - 2 * p, fadeH, GROUP_FILL & 0x00FFFFFF, GROUP_FILL);
            double ax = trackX - P(9), ay = f.bottom() - P(9);
            c.triangle(ax - P(3), ay - P(2), ax + P(3), ay - P(2), ax, ay + P(1), TEXT_KEYBIND);
        }
    }

    @Override
    protected int scrollbarTrack() {
        return 0xFF202020;
    }

    @Override
    protected int scrollbarThumb() {
        return 0xFF404040;
    }

    /* ---- row layout (row y = the label's line top) ---- */

    private void keybindRow(Canvas c, Widget w, double x, double y, double width) {
        KeybindWidget keybind = w.getKeybind();
        if (keybind == null) return;
        double kw = PixelFont.measure(c, keybind.getText(this));
        double right = x + width - P(KEYBIND_RIGHT);
        keybind.bounds.set(right - kw, y + P(5), kw, P(PixelFont.HEIGHT));
        keybind.label.set(right - kw - P(4), y, kw + P(8), P(ROW_CHECKBOX));
    }

    @Override
    public double layoutCheckbox(Canvas c, CheckboxWidget w, double x, double y, double width) {
        w.bounds.set(x, y, width, P(ROW_CHECKBOX));
        w.box.set(x + P(CHECKBOX_X), y + P(4), P(CHECK), P(CHECK));
        keybindRow(c, w, x, y, width);
        return P(ROW_CHECKBOX);
    }

    @Override
    public double layoutLabel(Canvas c, LabelWidget w, double x, double y, double width) {
        w.bounds.set(x, y, width, P(ROW_LABEL));
        keybindRow(c, w, x, y, width);
        return P(ROW_LABEL);
    }

    @Override
    public double layoutButton(Canvas c, ButtonWidget w, double x, double y, double width) {
        w.bounds.set(x, y, width, P(ROW_BUTTON));
        w.button.set(x + P(LABEL_X), y + P(1), controlWidth(c, width, CONTROL_W), P(BUTTON_H));
        keybindRow(c, w, x, y + P(6), width);
        return P(ROW_BUTTON);
    }

    /** Control width, shrunk if the group is narrow. */
    protected double controlWidth(Canvas c, double rowWidth, double reference) {
        return Math.max(P(60), Math.min(P(reference), rowWidth - P(LABEL_X) - P(KEYBIND_RIGHT)));
    }

    @Override
    public double layoutSlider(Canvas c, SliderWidget w, double x, double y, double width) {
        boolean labelled = !w.getLabel().isEmpty();
        double trackTop = labelled ? P(20) : P(2);
        w.bounds.set(x, y, width, labelled ? P(ROW_SLIDER) : P(18));
        w.track.set(x + P(LABEL_X), y + trackTop, controlWidth(c, width, SLIDER_W), P(TRACK_H));
        //the -/+ markers beside fine-grained tracks step the value
        Setting.SliderSpec spec = w.getSpec();
        if ((spec.getMax() - spec.getMin()) / spec.getStep() > w.track.w * c.getScale()) {
            w.minus.set(w.track.x - P(9), w.track.y - P(2), P(8), w.track.h + P(4));
            w.plus.set(w.track.right() + P(1), w.track.y - P(2), P(8), w.track.h + P(4));
        } else {
            w.minus.clear();
            w.plus.clear();
        }
        if (labelled) keybindRow(c, w, x, y, width);
        return w.bounds.h;
    }

    private double dropdownRow(Rect box, Widget w, double x, double y, double width, Canvas c) {
        boolean labelled = !w.getLabel().isEmpty();
        double boxTop = labelled ? P(19) : P(1);
        w.bounds.set(x, y, width, boxTop + P(ROW_DROPDOWN - 19));
        box.set(x + P(LABEL_X), y + boxTop, controlWidth(c, width, CONTROL_W), P(BOX_H));
        keybindRow(c, w, x, y, width);
        return w.bounds.h;
    }

    @Override
    public double layoutChoice(Canvas c, ChoiceWidget w, double x, double y, double width) {
        return dropdownRow(w.box, w, x, y, width, c);
    }

    @Override
    public double layoutCombo(Canvas c, ComboWidget w, double x, double y, double width) {
        return dropdownRow(w.box, w, x, y, width, c);
    }

    @Override
    public double layoutField(Canvas c, FieldWidget w, double x, double y, double width) {
        boolean labelled = !w.getLabel().isEmpty();
        double boxTop = labelled ? P(19) : P(3);
        w.bounds.set(x, y, width, boxTop + P(30));
        w.box.set(x + P(LABEL_X), y + boxTop, controlWidth(c, width, CONTROL_W), P(22));
        return w.bounds.h;
    }

    @Override
    public double layoutColor(Canvas c, ColorWidget w, double x, double y, double width) {
        w.bounds.set(x, y, width, P(ROW_LABEL));
        w.swatch.set(x + width - P(SWATCH_RIGHT) - P(SWATCH_W), y + P(3), P(SWATCH_W), P(SWATCH_H));
        return P(ROW_LABEL);
    }

    @Override
    public void layoutAttachedColor(Canvas c, ColorWidget w, Widget row, int index) {
        Rect r = row.bounds;
        w.bounds.set(r.x, r.y, r.w, P(ROW_LABEL));
        w.swatch.set(r.right() - P(SWATCH_RIGHT) - P(SWATCH_W) - index * P(SWATCH_W + 6), r.y + P(3), P(SWATCH_W), P(SWATCH_H));
        KeybindWidget keybind = row.getKeybind();
        if (keybind != null && index == 0) {
            double shift = P(SWATCH_W + 6);
            keybind.bounds.x -= shift;
            keybind.label.x -= shift;
        }
    }

    @Override
    public double layoutList(Canvas c, ListWidget w, double x, double y, double width) {
        double cw = controlWidth(c, width, CONTROL_W);
        double top = P(1);
        double searchH = w.isSearchable() ? P(LIST_SEARCH_H) : 0;
        if (w.isSearchable()) w.search.set(x + P(LABEL_X), y + top, cw, searchH);
        else w.search.clear();
        double boxH = w.getModel().getHeight() > 0 ? w.getModel().getHeight() : P(156);
        w.box.set(x + P(LABEL_X), y + top + searchH, cw, boxH);
        w.itemHeight = P(LIST_ITEM_H);
        w.visibleRows = Math.max(1, (int) Math.ceil(boxH / w.itemHeight));
        w.bounds.set(x, y, width, top + searchH + boxH + P(5));
        keybindRow(c, w, x, y - P(5), width);
        return w.bounds.h;
    }

    /* ---- widgets ---- */

    protected void label(Canvas c, Widget w, double x, double rowY) {
        String label = w.getLabel();
        if (label.isEmpty()) return;
        int color = w.getSetting().isDisabled() ? TEXT_DISABLED : w.getSetting().isWarning() ? TEXT_WARNING : TEXT;
        c.text(text(c), label, x, rowY + P(1), color);
    }

    /** A 1-pixel black border around a vertical gradient. */
    protected void bevelBox(Canvas c, Rect b, int top, int bottom) {
        double p = px(c, 1);
        c.rect(b.x, b.y, b.w, b.h, BLACK);
        c.gradientV(b.x + p, b.y + p, b.w - 2 * p, b.h - 2 * p, top, bottom);
    }

    @Override
    public void paintCheckbox(Canvas c, CheckboxWidget w) {
        Rect b = w.box;
        int accent = w.getMenu().getAccent();
        double hover = w.getHover();
        if (w.isChecked()) bevelBox(c, b, Colors.shade(accent, 0.88), Colors.shade(accent, 0.66));
        else if (w.getSetting().isDisabled()) bevelBox(c, b, 0xFF2A2A2A, 0xFF222222);
        else bevelBox(c, b, Colors.mix(0xFF4B4B4B, 0xFF575757, hover), Colors.mix(0xFF333333, 0xFF3D3D3D, hover));
        label(c, w, w.bounds.x + P(LABEL_X), w.bounds.y);
    }

    @Override
    public void paintLabel(Canvas c, LabelWidget w) {
        label(c, w, w.bounds.x + P(LABEL_X), w.bounds.y);
    }

    @Override
    public void paintButton(Canvas c, ButtonWidget w) {
        Rect b = w.button;
        double p = px(c, 1);
        c.rect(b.x, b.y, b.w, b.h, BLACK);
        c.outline(b.x + p, b.y + p, b.w - 2 * p, b.h - 2 * p, 0xFF323232);
        double hover = w.getHover();
        int top = Colors.mix(0xFF202020, 0xFF282828, hover), bottom = Colors.mix(0xFF1E1E1E, 0xFF242424, hover);
        if (w.isPressed()) {
            top = 0xFF181818;
            bottom = 0xFF1C1C1C;
        }
        c.gradientV(b.x + 2 * p, b.y + 2 * p, b.w - 4 * p, b.h - 4 * p, top, bottom);
        Font font = text(c);
        String label = c.fit(font, w.getLabel(), b.w - P(12));
        double tw = c.measure(font, label);
        c.text(font, label, snap(c, b.centerX() - tw / 2), capsTop(c, font, b.y + P(12)), TEXT);
    }

    @Override
    public void paintSlider(Canvas c, SliderWidget w) {
        Rect t = w.track;
        label(c, w, t.x, w.bounds.y);
        int accent = w.getMenu().getAccent();
        double p = px(c, 1);
        c.rect(t.x, t.y, t.w, t.h, BLACK);
        double hover = w.getHover();
        c.gradientV(t.x + p, t.y + p, t.w - 2 * p, t.h - 2 * p, Colors.mix(0xFF343434, 0xFF3C3C3C, hover), Colors.mix(0xFF444444, 0xFF4C4C4C, hover));
        double fill = (t.w - 2 * p) * w.getFraction();
        if (fill > 0) c.gradientV(t.x + p, t.y + p, fill, t.h - 2 * p, Colors.shade(accent, 0.88), Colors.shade(accent, 0.66));

        //value: bold, outlined, centred on the end of the fill and on the track's bottom edge
        Font font = bold(c);
        String value = w.getValueText();
        double vw = c.measure(font, value);
        double vx = snap(c, t.x + p + fill - vw / 2);
        c.textOutline(font, value, vx, capsTop(c, font, t.bottom() - P(5)), TEXT, 0xFF000000);

        //-/+ beside tracks too fine to set exactly with the mouse (more steps than pixels), where the value can still move
        Setting.SliderSpec spec = w.getSpec();
        if ((spec.getMax() - spec.getMin()) / spec.getStep() > t.w * c.getScale()) {
            double my = t.y + P(2);
            if (w.getFraction() > 0) PixelFont.draw(c, "-", t.x - P(6), my - P(4), TEXT_KEYBIND);
            if (w.getFraction() < 1) PixelFont.draw(c, "+", t.right() + P(3), my - P(3), TEXT_KEYBIND);
        }
    }

    protected void dropdownBox(Canvas c, Rect b, double hover, boolean open) {
        if (open) hover = 1;
        int top = Colors.mix(0xFF1F1F1F, 0xFF262626, hover), bottom = Colors.mix(0xFF232323, 0xFF2D2D2D, hover);
        bevelBox(c, b, top, bottom);
        //5×3 arrow, 9 reference pixels from the right edge
        double ax = b.right() - P(9), ay = b.y + P(10);
        double p = px(c, 1);
        //the arrow keeps pointing down while the list is open
        c.rect(ax - 2 * p, ay, 5 * p, p, TEXT_VALUE);
        c.rect(ax - p, ay + p, 3 * p, p, TEXT_VALUE);
        c.rect(ax, ay + 2 * p, p, p, TEXT_VALUE);
    }

    @Override
    public void paintChoice(Canvas c, ChoiceWidget w) {
        label(c, w, w.box.x, w.bounds.y);
        dropdownBox(c, w.box, w.getHover(), w.isOpen());
        String value = w.getSelected() == null ? "-" : w.getSelected();
        Font font = text(c);
        c.text(font, c.fit(font, value, w.box.w - P(30)), w.box.x + P(10), capsTop(c, font, w.box.y + P(8)), TEXT_VALUE);
    }

    @Override
    public void paintCombo(Canvas c, ComboWidget w) {
        label(c, w, w.box.x, w.bounds.y);
        dropdownBox(c, w.box, w.getHover(), w.isOpen());
        Font font = text(c);
        c.text(font, c.fit(font, w.getSummary(), w.box.w - P(30)), w.box.x + P(10), capsTop(c, font, w.box.y + P(8)), TEXT_VALUE);
    }

    @Override
    public void paintField(Canvas c, FieldWidget w) {
        Rect b = w.box;
        label(c, w, b.x, w.bounds.y);
        double p = px(c, 1);
        c.rect(b.x, b.y, b.w, b.h, BLACK);
        c.rect(b.x + p, b.y + p, b.w - 2 * p, b.h - 2 * p, w.isFocused() ? 0xFF232323 : Colors.mix(0xFF1D1D1D, 0xFF222222, w.getHover()));
        Font font = text(c);
        String value = w.getText();
        double tw = c.measure(font, value);
        double offset = Math.min(0, b.w - P(22) - tw);
        double ty = capsTop(c, font, b.y + P(6));
        c.pushClip(b.x + p, b.y + p, b.w - 2 * p, b.h - 2 * p);
        c.text(font, value, b.x + P(7) + offset, ty, TEXT);
        if (!w.isFocused() || w.isCaretVisible()) c.text(font, "_", b.x + P(7) + offset + tw, ty, TEXT);
        c.popClip();
    }

    @Override
    public void paintColor(Canvas c, ColorWidget w) {
        if (!w.isAttached()) label(c, w, w.bounds.x + P(LABEL_X), w.bounds.y);
        Rect s = w.swatch;
        double p = px(c, 1);
        c.rect(s.x, s.y, s.w, s.h, BLACK);
        Color color = w.getColor();
        if (color.getAlpha() < 255) paintChecker(c, s.x + p, s.y + p, s.w - 2 * p, s.h - 2 * p);
        int argb = color.getRGB();
        c.gradientV(s.x + p, s.y + p, s.w - 2 * p, s.h - 2 * p, argb, Colors.shade(argb, 0.85));
    }

    @Override
    public void paintList(Canvas c, ListWidget w) {
        Rect b = w.box;
        double p = px(c, 1);
        boolean search = w.isSearchable();
        if (search) {
            //a text field (black, grey and dark rings) sitting on top of the list
            Rect s = w.search;
            c.rect(s.x, s.y, s.w, s.h, BLACK);
            c.rect(s.x + p, s.y + p, s.w - 2 * p, s.h - p, 0xFF2F2F2F);
            c.rect(s.x + 2 * p, s.y + 2 * p, s.w - 4 * p, s.h - 3 * p, 0xFF111111);
            c.rect(s.x + 3 * p, s.y + 3 * p, s.w - 6 * p, s.h - 5 * p, w.isSearchFocused() ? 0xFF1D1D1D : 0xFF181818);
            Font sf = text(c);
            double qw = c.measure(sf, w.getQuery());
            double ty = capsTop(c, sf, s.y + P(8));
            c.text(sf, w.getQuery(), s.x + P(7), ty, TEXT);
            c.text(sf, "_", s.x + P(7) + qw, ty, TEXT);
        }
        //the list: black sides and bottom (and top without a search field), dark grey inside
        c.rect(b.x, b.y - (search ? 0 : p), b.w, b.h + (search ? 0 : p), BLACK);
        c.rect(b.x + p, b.y, b.w - 2 * p, b.h - p, 0xFF222222);
        List<String> items = w.getItems();
        boolean scrolls = items.size() > w.visibleRows;
        double textW = b.w - P(20) - (scrolls ? P(6) : 0);
        Font font = text(c);
        c.pushClip(b.x + p, b.y, b.w - 2 * p, b.h - p);
        for (int row = 0; row < w.visibleRows; row++) {
            int index = w.getScroll() + row;
            if (index >= items.size()) break;
            String item = items.get(index);
            double y = b.y + row * w.itemHeight;
            boolean selected = item.equals(w.getSelected());
            boolean hovered = index == w.getHoveredIndex();
            if (hovered || selected) c.rect(b.x + p, y, b.w - 2 * p, w.itemHeight - 2 * p, 0xFF191919);
            Font f = selected || hovered ? bold(c) : font;
            c.text(f, c.fit(f, item, textW), b.x + P(10), capsTop(c, f, y + P(LIST_TEXT_Y)), selected ? w.getMenu().getAccent() : TEXT);
        }
        c.popClip();
        if (scrolls) {
            //scrollbar track along the right edge, a small thumb, and the "more below" arrow
            double tx = b.right() - p - P(6);
            c.rect(tx, b.y, P(6), b.h - p, 0xFF2D2D2D);
            double thumbH = Math.max(P(20), (b.h - p) * w.visibleRows / items.size());
            double thumbY = b.y + (b.h - p - thumbH) * w.getScroll() / (items.size() - w.visibleRows);
            c.rect(tx + p, snap(c, thumbY), P(4), thumbH, 0xFF404040);
            if (w.getScroll() + w.visibleRows < items.size()) {
                double ax = snap(c, b.right() - P(17)), ay = snap(c, b.bottom() - P(9));
                c.rect(ax - P(2), ay, P(5), p, BLACK);
                c.triangle(ax - P(2), ay + p, ax + P(3), ay + p, ax + P(0.5), ay + p + P(2.5), 0xFFCCCCCC);
            }
        }
    }

    @Override
    public void paintKeybind(Canvas c, KeybindWidget w) {
        int color = w.isRefused() ? 0xFFD04040 : w.isCapturing() ? TEXT : TEXT_KEYBIND;
        PixelFont.draw(c, w.getText(this), w.bounds.x, w.bounds.y, color);
    }

    private static final Map<String, String> SHORT_KEYS = new HashMap<>();

    static {
        SHORT_KEYS.put("DELETE", "DEL");
        SHORT_KEYS.put("INSERT", "INS");
        SHORT_KEYS.put("LSHIFT", "SHF");
        SHORT_KEYS.put("RSHIFT", "SHF");
        SHORT_KEYS.put("LCONTROL", "CTR");
        SHORT_KEYS.put("RCONTROL", "CTR");
        SHORT_KEYS.put("LMENU", "ALT");
        SHORT_KEYS.put("RMENU", "ALT");
        SHORT_KEYS.put("SPACE", "SPC");
        SHORT_KEYS.put("RETURN", "RET");
        SHORT_KEYS.put("ESCAPE", "ESC");
        SHORT_KEYS.put("CAPITAL", "CAP");
        SHORT_KEYS.put("BACK", "BCK");
        SHORT_KEYS.put("PRIOR", "PGU");
        SHORT_KEYS.put("NEXT", "PGD");
        SHORT_KEYS.put("HOME", "HOM");
        SHORT_KEYS.put("END", "END");
        SHORT_KEYS.put("TAB", "TAB");
        SHORT_KEYS.put("GRAVE", "~");
    }

    @Override
    public String formatKeybind(int key, boolean capturing, boolean refused) {
        if (refused) return "[-]";
        if (capturing) return "[...]";
        if (key <= 0) return "[-]";
        String name = keyName(key);
        String shortName = SHORT_KEYS.get(name);
        if (shortName != null) name = shortName;
        else if (name.startsWith("NUMPAD")) name = "N" + name.substring(6);
        return "[" + name + "]";
    }

    /* ---- popups ---- */

    @Override
    public void layoutDropdown(Canvas c, DropdownPopup popup) {
        Rect box = popup.getOwner() instanceof ChoiceWidget ? ((ChoiceWidget) popup.getOwner()).box : ((ComboWidget) popup.getOwner()).box;
        int rows = Math.min(popupMaxRows, popup.getOptions().size());
        double p = px(c, 1);
        popup.itemHeight = popupItemHeight;
        popup.visibleRows = rows;
        double h = rows * popupItemHeight + 2 * p;
        double y = box.bottom() + p;
        Menu menu = popup.getOwner().getMenu();
        if (y + h > menu.getViewHeight() && box.y - h - p >= 0) y = box.y - h - p;
        popup.bounds.set(box.x, y, box.w, h);
    }

    @Override
    public void paintDropdown(Canvas c, DropdownPopup p) {
        Rect b = p.bounds;
        double px = px(c, 1);
        c.rect(b.x, b.y, b.w, b.h, BLACK);
        c.rect(b.x + px, b.y + px, b.w - 2 * px, b.h - 2 * px, 0xFF232323);
        List<String> options = p.getOptions();
        int accent = p.getOwner().getMenu().getAccent();
        for (int row = 0; row < p.visibleRows; row++) {
            int index = p.getScroll() + row;
            if (index >= options.size()) break;
            double y = b.y + px + row * p.itemHeight;
            boolean hovered = index == p.getHovered();
            boolean selected = p.getSource().isSelected(index);
            if (hovered) c.rect(b.x + px, y, b.w - 2 * px, p.itemHeight, 0xFF1A1A1A);
            Font font = selected || hovered ? bold(c) : text(c);
            c.text(font, c.fit(font, options.get(index), b.w - P(22)), b.x + P(11), capsTop(c, font, y + P(7)), selected ? accent : TEXT);
        }
    }

    @Override
    public void layoutContext(Canvas c, ContextPopup popup) {
        double p = px(c, 1);
        popup.itemHeight = P(20);
        double w = 0;
        for (String item : popup.getItems()) w = Math.max(w, c.measure(bold(c), item));
        w = Math.max(P(70), w + P(22));
        double h = popup.getItems().size() * popup.itemHeight + 2 * p;
        Menu menu = popup.getOwner().getMenu();
        double x = Math.min(popup.anchorX, menu.getViewWidth() - w), y = Math.min(popup.anchorY, menu.getViewHeight() - h);
        popup.bounds.set(Math.max(0, x), Math.max(0, y), w, h);
    }

    @Override
    public void paintContext(Canvas c, ContextPopup popup) {
        Rect b = popup.bounds;
        double p = px(c, 1);
        c.rect(b.x, b.y, b.w, b.h, BLACK);
        c.rect(b.x + p, b.y + p, b.w - 2 * p, b.h - 2 * p, 0xFF232323);
        int accent = popup.getOwner().getMenu().getAccent();
        List<String> items = popup.getItems();
        for (int i = 0; i < items.size(); i++) {
            double y = b.y + p + i * popup.itemHeight;
            boolean hovered = i == popup.getHovered(), selected = i == popup.getSelected();
            if (hovered) c.rect(b.x + p, y, b.w - 2 * p, popup.itemHeight, 0xFF1A1A1A);
            Font font = selected || hovered ? bold(c) : text(c);
            c.text(font, items.get(i), b.x + P(10), capsTop(c, font, y + P(6)), selected ? accent : TEXT);
        }
    }

    /**
     * Colour picker (reference pixels): black and grey rings, 2px padding, then the SV square and the hue bar (red at
     * the top, running backwards) side by side and the alpha bar below, each in a dark inset with 3px between them.
     */
    /** Lists (reference pixels): search field height, row pitch, text offset in a row. */
    protected static final double LIST_SEARCH_H = 24, LIST_ITEM_H = 23, LIST_TEXT_Y = 6;
    protected static final double PICKER_SV = 188, PICKER_HUE_W = 19, PICKER_ALPHA_H = 13, PICKER_GAP = 3;

    @Override
    public void layoutColorPicker(Canvas c, ColorPopup popup) {
        Rect swatch = popup.getWidget().swatch;
        double edge = P(4); //black ring, grey ring, 2px padding
        double w = 2 * edge + P(1 + PICKER_SV + 1 + PICKER_GAP + 1 + PICKER_HUE_W + 1);
        double h = 2 * edge + P(1 + PICKER_SV + 1 + PICKER_GAP + 1 + PICKER_ALPHA_H + 1);
        Menu menu = popup.getOwner().getMenu();
        double x = swatch.x - P(2), y = swatch.bottom() + P(2);
        if (x + w > menu.getViewWidth()) x = menu.getViewWidth() - w;
        if (y + h > menu.getViewHeight()) y = swatch.y - h - P(2);
        popup.bounds.set(x, Math.max(0, y), w, h);
        Rect b = popup.bounds;
        popup.svSquare.set(b.x + edge + P(1), b.y + edge + P(1), P(PICKER_SV), P(PICKER_SV));
        popup.hueBar.set(popup.svSquare.right() + P(1 + PICKER_GAP + 1), popup.svSquare.y, P(PICKER_HUE_W), P(PICKER_SV));
        popup.alphaBar.set(popup.svSquare.x, popup.svSquare.bottom() + P(1 + PICKER_GAP + 1), P(PICKER_SV), P(PICKER_ALPHA_H));
        popup.hueReversed = true;
    }

    @Override
    public void paintColorPicker(Canvas c, ColorPopup p) {
        Rect b = p.bounds;
        double px = px(c, 1);
        c.rect(b.x, b.y, b.w, b.h, BLACK);
        c.rect(b.x + px, b.y + px, b.w - 2 * px, b.h - 2 * px, 0xFF393939);
        c.rect(b.x + 2 * px, b.y + 2 * px, b.w - 4 * px, b.h - 4 * px, 0xFF262626);

        Rect sv = p.svSquare;
        c.rect(sv.x - px, sv.y - px, sv.w + 2 * px, sv.h + 2 * px, 0xFF0D0D0D);
        c.gradientH(sv.x, sv.y, sv.w, sv.h, 0xFFFFFFFF, p.hueColor());
        c.gradientV(sv.x, sv.y, sv.w, sv.h, 0x00000000, 0xFF000000);
        double mx = snap(c, sv.x + p.getSaturation() * (sv.w - px)), my = snap(c, sv.y + (1 - p.getBrightness()) * (sv.h - px));
        c.outline(mx - 2 * px, my - 2 * px, 5 * px, 5 * px, BLACK);
        c.outline(mx - px, my - px, 3 * px, 3 * px, 0xFFE6E6E6);

        //hue: red at the top, then magenta, blue, cyan, green, yellow, red again
        Rect hue = p.hueBar;
        c.rect(hue.x - px, hue.y - px, hue.w + 2 * px, hue.h + 2 * px, 0xFF0D0D0D);
        for (int i = 0; i < 6; i++) {
            int top = 0xFF000000 | Color.HSBtoRGB(1 - i / 6f, 1, 1), bottom = 0xFF000000 | Color.HSBtoRGB(1 - (i + 1) / 6f, 1, 1);
            double y0 = snap(c, hue.y + hue.h * i / 6), y1 = snap(c, hue.y + hue.h * (i + 1) / 6);
            c.gradientV(hue.x, y0, hue.w, y1 - y0, top, bottom);
        }
        double hy = snap(c, hue.y + (1 - p.getHue()) * (hue.h - 2 * px));
        c.rect(hue.x, hy, hue.w, 2 * px, 0xFFE6E6E6);

        //alpha: filled with the colour up to its alpha, like a slider, with a small handle at the end
        Rect alpha = p.alphaBar;
        c.rect(alpha.x - px, alpha.y - px, alpha.w + 2 * px, alpha.h + 2 * px, 0xFF0D0D0D);
        c.rect(alpha.x, alpha.y, alpha.w, alpha.h, 0xFF262626);
        int rgb = p.getWidget().getColor().getRGB() | 0xFF000000;
        double fill = snap(c, alpha.w * p.getAlpha() / 255.0);
        c.rect(alpha.x, alpha.y, fill, alpha.h, rgb);
        double hx = Math.min(alpha.right() - 3 * px, alpha.x + fill - 2 * px);
        c.rect(hx, alpha.y, 3 * px, alpha.h, 0xFFB4B4B4);
    }

    /* ---- HUD ---- */

    @Override
    public double watermarkHeight(Canvas c) {
        return P(30);
    }

    @Override
    public double watermarkWidth(Canvas c, Watermark watermark) {
        return Math.round(c.measure(text(c), watermark.getText()) + P(30));
    }

    protected void hudFrame(Canvas c, double x, double y, double w, double h) {
        double p = px(c, 1);
        c.rect(x, y, w, h, BLACK);
        c.rect(x + p, y + p, w - 2 * p, h - 2 * p, BEVEL_LIGHT);
        c.rect(x + 2 * p, y + 2 * p, w - 4 * p, h - 4 * p, BEVEL_MID);
        c.rect(x + 5 * p, y + 5 * p, w - 10 * p, h - 10 * p, BEVEL_LIGHT);
        c.rect(x + 6 * p, y + 6 * p, w - 12 * p, h - 12 * p, BLACK);
        double ix = x + 7 * p, iy = y + 7 * p, iw = w - 14 * p, ih = h - 14 * p;
        c.tile(stripes(), ix, iy, iw, ih, 0xFFFFFFFF);
        double half = iw / 2;
        c.hlineGradient(ix, iy, half, STRIP_LEFT, STRIP_MID);
        c.hlineGradient(ix + half, iy, iw - half, STRIP_MID, STRIP_RIGHT);
        c.hlineGradient(ix, iy + p, half, Colors.shade(STRIP_LEFT, 0.53), Colors.shade(STRIP_MID, 0.53));
        c.hlineGradient(ix + half, iy + p, iw - half, Colors.shade(STRIP_MID, 0.53), Colors.shade(STRIP_RIGHT, 0.53));
    }

    @Override
    public void paintWatermark(Canvas c, Watermark watermark, double x, double y, double w, double h, int accent) {
        hudFrame(c, x, y, w, h);
        c.text(text(c), watermark.getText(), x + P(15), capsTop(c, text(c), y + P(13)), TEXT);
    }

    @Override
    public double toastWidth(Canvas c, Toast n) {
        return P(280);
    }

    @Override
    public double toastHeight(Canvas c, Toast n) {
        return P(24) + P(20) + n.lines(c, text(c), P(280 - 36)).size() * P(17) + P(12);
    }

    @Override
    public void paintToast(Canvas c, Toast n, double x, double y, double w, double h, double visibility, int accent) {
        c.setAlpha(visibility);
        hudFrame(c, x, y, w, h);
        double ty = y + P(18);
        c.text(bold(c), n.getTitle(), x + P(18), capsTop(c, bold(c), ty), TEXT);
        ty += P(20);
        for (String line : n.lines(c, text(c), w - P(36))) {
            c.text(text(c), line, x + P(18), capsTop(c, text(c), ty), TEXT_VALUE);
            ty += P(17);
        }
        c.setAlpha(1);
    }
}
