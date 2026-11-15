package studio.dreamys.icarus.theme;

import studio.dreamys.icarus.input.Keys;
import studio.dreamys.icarus.render.AtlasImage;
import studio.dreamys.icarus.render.Canvas;
import studio.dreamys.icarus.render.Font;
import studio.dreamys.icarus.render.FontAtlas;
import studio.dreamys.icarus.ui.GroupView;
import studio.dreamys.icarus.ui.Menu;
import studio.dreamys.icarus.ui.Rect;
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
 * Shared layout for themes that use the common "label, then control" row structure: themes set the metrics in
 * their constructor and implement painting. Fonts are created lazily from the shared atlas.
 */
public abstract class BaseTheme implements Theme {
    /** An image from the classpath as a white alpha mask for tinting (e.g. icons cut from a reference), or null. */
    protected static AtlasImage resourceImage(String path) {
        try (java.io.InputStream in = BaseTheme.class.getResourceAsStream(path)) {
            if (in == null) return null;
            java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(in);
            if (img == null) return null;
            int[] argb = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
            return new AtlasImage(argb, img.getWidth(), img.getHeight());
        } catch (java.io.IOException e) {
            return null;
        }
    }

    /** Row metrics in design units. */
    protected double textRow = 13;
    /** Height of the label line above sliders, dropdowns and fields. */
    protected double labelRow = 13;
    protected double checkboxSize = 8;
    /** Offset of labels and controls from the row's left edge (checkbox column width). */
    protected double indent = 20;
    protected double controlWidth = 162;
    protected double controlRightReserve = 30;
    protected double boxHeight = 20;
    protected double labelGap = 2;
    protected double buttonHeight = 25;
    protected double sliderTrackHeight = 6;
    protected double sliderValueOverhang = 6;
    protected double swatchWidth = 16, swatchHeight = 8;
    protected double listItemHeight = 16;
    protected int listRows = 8;
    protected double popupItemHeight = 20;
    protected int popupMaxRows = 10;
    protected double scrollbarWidth = 3;

    private FontAtlas fontsFor;
    private Font text, bold, small, icon, title, tab;

    /** Creates the theme's fonts (called once per atlas). */
    protected abstract void createFonts(FontAtlas atlas);

    protected final void setFonts(Font text, Font bold, Font small, Font icon, Font title, Font tab) {
        this.text = text;
        this.bold = bold;
        this.small = small;
        this.icon = icon;
        this.title = title;
        this.tab = tab;
    }

    private void ensureFonts(Canvas canvas) {
        if (fontsFor != canvas.getAtlas()) {
            fontsFor = canvas.getAtlas();
            createFonts(fontsFor);
        }
    }

    protected Font text(Canvas c) {
        ensureFonts(c);
        return text;
    }

    protected Font bold(Canvas c) {
        ensureFonts(c);
        return bold;
    }

    protected Font small(Canvas c) {
        ensureFonts(c);
        return small;
    }

    protected Font icon(Canvas c) {
        ensureFonts(c);
        return icon;
    }

    protected Font title(Canvas c) {
        ensureFonts(c);
        return title;
    }

    protected Font tab(Canvas c) {
        ensureFonts(c);
        return tab;
    }

    /** Top of text vertically centred on {@code centerY}. */
    protected double centeredTextY(Canvas c, Font font, double centerY) {
        return Math.round(centerY - c.lineHeight(font) / 2);
    }

    protected double controlWidth(double rowWidth) {
        return Math.max(40, Math.min(controlWidth, rowWidth - indent - controlRightReserve));
    }

    /** Places a widget's keybind label right-aligned in a row. */
    protected void layoutKeybind(Canvas c, Widget widget, double rowRight, double y, double height) {
        KeybindWidget keybind = widget.getKeybind();
        if (keybind == null) return;
        double w = c.measure(small(c), keybind.getText(this));
        keybind.bounds.set(rowRight - w, y, w, height);
        keybind.label.set(rowRight - w - 2, y, w + 4, height);
    }

    /* ---- row layout ---- */

    @Override
    public double layoutCheckbox(Canvas c, CheckboxWidget w, double x, double y, double width) {
        double h = Math.max(textRow, checkboxSize);
        w.bounds.set(x, y, width, h);
        w.box.set(x, Math.round(y + (h - checkboxSize) / 2), checkboxSize, checkboxSize);
        layoutKeybind(c, w, x + width, y, h);
        return h;
    }

    @Override
    public double layoutButton(Canvas c, ButtonWidget w, double x, double y, double width) {
        w.bounds.set(x, y, width, buttonHeight);
        w.button.set(x + indent, y, controlWidth(width), buttonHeight);
        layoutKeybind(c, w, x + width, y, buttonHeight);
        return buttonHeight;
    }

    @Override
    public double layoutSlider(Canvas c, SliderWidget w, double x, double y, double width) {
        double h = labelRow + labelGap + sliderTrackHeight + sliderValueOverhang;
        w.bounds.set(x, y, width, h);
        w.track.set(x + indent, y + labelRow + labelGap, controlWidth(width), sliderTrackHeight);
        return h;
    }

    protected double labelledBox(Rect box, Widget w, double x, double y, double width) {
        double h = labelRow + labelGap + boxHeight;
        w.bounds.set(x, y, width, h);
        box.set(x + indent, y + labelRow + labelGap, controlWidth(width), boxHeight);
        return h;
    }

    @Override
    public double layoutChoice(Canvas c, ChoiceWidget w, double x, double y, double width) {
        return labelledBox(w.box, w, x, y, width);
    }

    @Override
    public double layoutCombo(Canvas c, ComboWidget w, double x, double y, double width) {
        return labelledBox(w.box, w, x, y, width);
    }

    @Override
    public double layoutField(Canvas c, FieldWidget w, double x, double y, double width) {
        return labelledBox(w.box, w, x, y, width);
    }

    @Override
    public double layoutColor(Canvas c, ColorWidget w, double x, double y, double width) {
        double h = Math.max(textRow, swatchHeight);
        w.bounds.set(x, y, width, h);
        w.swatch.set(x + width - swatchWidth, Math.round(y + (h - swatchHeight) / 2), swatchWidth, swatchHeight);
        return h;
    }

    @Override
    public void layoutAttachedColor(Canvas c, ColorWidget w, Widget row, int index) {
        Rect r = row.bounds;
        double h = Math.max(textRow, swatchHeight);
        w.bounds.set(r.x, r.y, r.w, h);
        w.swatch.set(r.right() - swatchWidth - index * (swatchWidth + 4), Math.round(r.y + (h - swatchHeight) / 2), swatchWidth, swatchHeight);
        if (index > 0) return;
        //a keybind on the same row moves left of the swatch
        KeybindWidget keybind = row.getKeybind();
        if (keybind != null) {
            double shift = swatchWidth + 6;
            keybind.bounds.x -= shift;
            keybind.label.x -= shift;
        }
    }

    @Override
    public double layoutLabel(Canvas c, LabelWidget w, double x, double y, double width) {
        w.bounds.set(x, y, width, textRow);
        layoutKeybind(c, w, x + width, y, textRow);
        return textRow;
    }

    @Override
    public void layoutContext(Canvas c, ContextPopup popup) {
        popup.itemHeight = popupItemHeight;
        double w = 0;
        for (String item : popup.getItems()) w = Math.max(w, c.measure(text(c), item));
        w += 20;
        double h = popup.getItems().size() * popupItemHeight;
        Menu menu = popup.getOwner().getMenu();
        double x = Math.min(popup.anchorX, menu.getViewWidth() - w), y = Math.min(popup.anchorY, menu.getViewHeight() - h);
        popup.bounds.set(Math.max(0, x), Math.max(0, y), w, h);
    }

    @Override
    public double layoutList(Canvas c, ListWidget w, double x, double y, double width) {
        int rows = Math.max(1, listRows);
        double h = rows * listItemHeight + 2;
        w.bounds.set(x, y, width, h);
        w.box.set(x + indent, y, controlWidth(width), h);
        w.itemHeight = listItemHeight;
        w.visibleRows = rows;
        return h;
    }

    /* ---- popups ---- */

    @Override
    public void layoutDropdown(Canvas c, DropdownPopup popup) {
        Rect box = popup.getOwner() instanceof ChoiceWidget ? ((ChoiceWidget) popup.getOwner()).box : ((ComboWidget) popup.getOwner()).box;
        int rows = Math.min(popupMaxRows, popup.getOptions().size());
        popup.itemHeight = popupItemHeight;
        popup.visibleRows = rows;
        double h = rows * popupItemHeight;
        double y = box.bottom() + 1;
        Menu menu = popup.getOwner().getMenu();
        if (y + h > menu.getViewHeight() && box.y - h - 1 >= 0) y = box.y - h - 1;
        popup.bounds.set(box.x, y, box.w, h);
    }

    @Override
    public void layoutColorPicker(Canvas c, ColorPopup popup) {
        Rect swatch = popup.getWidget().swatch;
        double pad = 6, square = 150, bar = 10;
        double w = pad + square + pad + bar + pad, h = pad + square + pad + bar + pad;
        Menu menu = popup.getOwner().getMenu();
        double x = swatch.right() + 6;
        if (x + w > menu.getViewWidth()) x = swatch.x - w - 6;
        double y = Math.min(swatch.y, menu.getViewHeight() - h);
        popup.bounds.set(x, Math.max(0, y), w, h);
        popup.svSquare.set(popup.bounds.x + pad, popup.bounds.y + pad, square, square);
        popup.hueBar.set(popup.svSquare.right() + pad, popup.svSquare.y, bar, square);
        popup.alphaBar.set(popup.svSquare.x, popup.svSquare.bottom() + pad, square, bar);
    }

    /** Draws the colour picker's gradients (shared by themes; frames are theme-specific). */
    protected void paintPickerParts(Canvas c, ColorPopup p, int border) {
        Rect sv = p.svSquare;
        //saturation: white → hue horizontally, then value: transparent → black vertically
        c.gradientH(sv.x, sv.y, sv.w, sv.h, 0xFFFFFFFF, p.hueColor());
        c.gradientV(sv.x, sv.y, sv.w, sv.h, 0x00000000, 0xFF000000);
        c.outline(sv.x - 1, sv.y - 1, sv.w + 2, sv.h + 2, border);
        double mx = sv.x + p.getSaturation() * sv.w, my = sv.y + (1 - p.getBrightness()) * sv.h;
        c.outline(mx - 2, my - 2, 5, 5, 0xFFFFFFFF);

        Rect hue = p.hueBar;
        int segments = 6;
        for (int i = 0; i < segments; i++) {
            int top = 0xFF000000 | java.awt.Color.HSBtoRGB(i / (float) segments, 1, 1);
            int bottom = 0xFF000000 | java.awt.Color.HSBtoRGB((i + 1) / (float) segments, 1, 1);
            c.gradientV(hue.x, hue.y + hue.h * i / segments, hue.w, hue.h / segments, top, bottom);
        }
        c.outline(hue.x - 1, hue.y - 1, hue.w + 2, hue.h + 2, border);
        double hy = hue.y + p.getHue() * hue.h;
        c.rect(hue.x - 1, hy - 1, hue.w + 2, 2, 0xFFFFFFFF);

        Rect alpha = p.alphaBar;
        paintChecker(c, alpha.x, alpha.y, alpha.w, alpha.h);
        int rgb = p.getOwner() instanceof ColorWidget ? ((ColorWidget) p.getOwner()).getColor().getRGB() & 0xFFFFFF : 0xFFFFFF;
        c.gradientH(alpha.x, alpha.y, alpha.w, alpha.h, rgb, 0xFF000000 | rgb);
        c.outline(alpha.x - 1, alpha.y - 1, alpha.w + 2, alpha.h + 2, border);
        double ax = alpha.x + p.getAlpha() / 255.0 * alpha.w;
        c.rect(ax - 1, alpha.y - 1, 2, alpha.h + 2, 0xFFFFFFFF);
    }

    /** A grey checkerboard (behind translucent colours). */
    protected void paintChecker(Canvas c, double x, double y, double w, double h) {
        c.rect(x, y, w, h, 0xFFBFBFBF);
        double cell = 4;
        c.pushClip(x, y, w, h);
        for (double cy = 0; cy < h; cy += cell) {
            for (double cx = ((int) (cy / cell) % 2) * cell; cx < w; cx += cell * 2) c.rect(x + cx, y + cy, cell, cell, 0xFFFFFFFF);
        }
        c.popClip();
    }

    /* ---- keybinds ---- */

    protected String keyName(int key) {
        String name = Keys.getKeyName(key);
        return name == null ? "#" + key : name;
    }

    /* ---- scroll bars ---- */

    @Override
    public void paintGroupForeground(Canvas c, Menu menu, GroupView g) {
        if (!g.isScrollable()) return;
        Rect content = g.content;
        double trackX = content.right() + 2;
        double thumbH = Math.max(12, content.h * content.h / g.getContentHeight());
        double thumbY = content.y + (content.h - thumbH) * g.getScroll() / (g.getContentHeight() - content.h);
        c.rect(trackX, content.y, scrollbarWidth, content.h, scrollbarTrack());
        c.rect(trackX, thumbY, scrollbarWidth, thumbH, scrollbarThumb());
    }

    protected abstract int scrollbarTrack();

    protected abstract int scrollbarThumb();
}
