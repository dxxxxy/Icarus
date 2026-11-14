package studio.dreamys.icarus.render;

import studio.dreamys.icarus.platform.Texture;

/**
 * Drawing in design units (1 unit = 1 screen pixel at Minecraft GUI scale 2), written to a {@link DrawList} in
 * framebuffer pixels. Every edge is snapped to whole pixels the same way, so shapes and text line up exactly at any
 * scale. All primitives sample the shared {@link FontAtlas}, so a frame is normally a single draw call.
 */
public final class Canvas {
    private DrawList list;
    private FontAtlas atlas;
    private double scale = 1;
    private float whiteU, whiteV;
    private int alphaMul = 255;
    private int clipDepth;

    /** Starts drawing a frame. {@code scale} is pixels per design unit; the list is reset to the framebuffer size. */
    public void begin(DrawList list, FontAtlas atlas, Texture atlasTexture, double scale, int framebufferWidth, int framebufferHeight) {
        this.list = list;
        this.atlas = atlas;
        this.scale = scale;
        this.alphaMul = 255;
        this.clipDepth = 0;
        list.reset(framebufferWidth, framebufferHeight);
        list.setTexture(atlasTexture);
        refreshWhite();
    }

    /** Prepares measuring (text widths, line heights) for a scale without starting a frame. */
    public void setup(FontAtlas atlas, double scale) {
        this.atlas = atlas;
        this.scale = scale;
    }

    private void refreshWhite() {
        whiteU = atlas.whiteU();
        whiteV = atlas.whiteV();
    }

    public DrawList getList() {
        return list;
    }

    public FontAtlas getAtlas() {
        return atlas;
    }

    /** Pixels per design unit. */
    public double getScale() {
        return scale;
    }

    /** A design coordinate in whole framebuffer pixels. */
    public int px(double design) {
        return (int) Math.round(design * scale);
    }

    /** Thickness of a 1-unit line in pixels (at least 1). */
    public int hairline() {
        return Math.max(1, (int) Math.round(scale));
    }

    /** Multiplies the alpha of everything drawn until reset (0..1), for fades. */
    public void setAlpha(double alpha) {
        alphaMul = (int) Math.round(Math.max(0, Math.min(1, alpha)) * 255);
    }

    private int a(int argb) {
        if (alphaMul == 255) return argb;
        int alpha = (argb >>> 24) * alphaMul / 255;
        return alpha << 24 | argb & 0xFFFFFF;
    }

    /* ---- clipping ---- */

    public void pushClip(double x, double y, double w, double h) {
        list.pushClip(px(x), px(y), px(x + w), px(y + h));
        clipDepth++;
    }

    public void popClip() {
        list.popClip();
        clipDepth--;
    }

    /* ---- shapes ---- */

    private void pixelRect(int x0, int y0, int x1, int y1, int tl, int tr, int br, int bl) {
        list.quad(x0, y0, x1, y1, whiteU, whiteV, whiteU, whiteV, a(tl), a(tr), a(br), a(bl));
    }

    /** End pixel of a span that is at least one pixel wide when {@code size > 0}. */
    private int end(double start, double size) {
        int s = px(start), e = px(start + size);
        return size > 0 && e <= s ? s + 1 : e;
    }

    public void rect(double x, double y, double w, double h, int color) {
        if (w <= 0 || h <= 0) return;
        pixelRect(px(x), px(y), end(x, w), end(y, h), color, color, color, color);
    }

    /** Vertical gradient. */
    public void gradientV(double x, double y, double w, double h, int top, int bottom) {
        if (w <= 0 || h <= 0) return;
        pixelRect(px(x), px(y), end(x, w), end(y, h), top, top, bottom, bottom);
    }

    /** Horizontal gradient. */
    public void gradientH(double x, double y, double w, double h, int left, int right) {
        if (w <= 0 || h <= 0) return;
        pixelRect(px(x), px(y), end(x, w), end(y, h), left, right, right, left);
    }

    /** A rectangle with a colour per corner. */
    public void gradient4(double x, double y, double w, double h, int topLeft, int topRight, int bottomRight, int bottomLeft) {
        if (w <= 0 || h <= 0) return;
        pixelRect(px(x), px(y), end(x, w), end(y, h), topLeft, topRight, bottomRight, bottomLeft);
    }

    /** A border of {@link #hairline()} pixels drawn inside the rectangle. */
    public void outline(double x, double y, double w, double h, int color) {
        if (w <= 0 || h <= 0) return;
        int x0 = px(x), y0 = px(y), x1 = end(x, w), y1 = end(y, h), t = hairline();
        if (x1 - x0 <= 2 * t || y1 - y0 <= 2 * t) {
            pixelRect(x0, y0, x1, y1, color, color, color, color);
            return;
        }
        pixelRect(x0, y0, x1, y0 + t, color, color, color, color);
        pixelRect(x0, y1 - t, x1, y1, color, color, color, color);
        pixelRect(x0, y0 + t, x0 + t, y1 - t, color, color, color, color);
        pixelRect(x1 - t, y0 + t, x1, y1 - t, color, color, color, color);
    }

    /** A horizontal line of {@link #hairline()} pixels whose top edge is at {@code y}. */
    public void hline(double x, double y, double w, int color) {
        if (w <= 0) return;
        int y0 = px(y);
        pixelRect(px(x), y0, end(x, w), y0 + hairline(), color, color, color, color);
    }

    /** A vertical line of {@link #hairline()} pixels whose left edge is at {@code x}. */
    public void vline(double x, double y, double h, int color) {
        if (h <= 0) return;
        int x0 = px(x);
        pixelRect(x0, px(y), x0 + hairline(), end(y, h), color, color, color, color);
    }

    /** A horizontal gradient line (e.g. an accent strip). */
    public void hlineGradient(double x, double y, double w, int left, int right) {
        if (w <= 0) return;
        int y0 = px(y);
        pixelRect(px(x), y0, end(x, w), y0 + hairline(), left, right, right, left);
    }

    public void triangle(double x0, double y0, double x1, double y1, double x2, double y2, int color) {
        list.triangle((float) (x0 * scale), (float) (y0 * scale), (float) (x1 * scale), (float) (y1 * scale),
                (float) (x2 * scale), (float) (y2 * scale), whiteU, whiteV, a(color));
    }

    /* ---- images ---- */

    /** Draws an atlas image stretched over the rectangle, tinted. */
    public void image(AtlasImage image, double x, double y, double w, double h, int tint) {
        if (!image.ensure(atlas)) return;
        refreshWhite();
        float s = atlas.getSize();
        list.quad(px(x), px(y), end(x, w), end(y, h), image.getX() / s, image.getY() / s,
                (image.getX() + image.getWidth()) / s, (image.getY() + image.getHeight()) / s, a(tint), a(tint), a(tint), a(tint));
    }

    /** Tiles an atlas image over the rectangle at one image pixel per screen pixel (e.g. a noise background). */
    public void tile(AtlasImage image, double x, double y, double w, double h, int tint) {
        if (w <= 0 || h <= 0 || !image.ensure(atlas)) return;
        refreshWhite();
        int x0 = px(x), y0 = px(y), x1 = end(x, w), y1 = end(y, h);
        list.pushClip(x0, y0, x1, y1);
        float s = atlas.getSize();
        float u0 = image.getX() / s, v0 = image.getY() / s;
        float u1 = (image.getX() + image.getWidth()) / s, v1 = (image.getY() + image.getHeight()) / s;
        int c = a(tint);
        for (int ty = y0; ty < y1; ty += image.getHeight()) {
            for (int tx = x0; tx < x1; tx += image.getWidth()) {
                list.quad(tx, ty, tx + image.getWidth(), ty + image.getHeight(), u0, v0, u1, v1, c, c, c, c);
            }
        }
        list.popClip();
    }

    /* ---- text ---- */

    /** Line height of a font in design units. */
    public double lineHeight(Font font) {
        return font.face(scale).getLineHeight() / scale;
    }

    /** Distance from the top of a line to the baseline, in design units. */
    public double ascent(Font font) {
        return font.face(scale).getAscent() / scale;
    }

    /** Width of text in design units (formatting codes aren't counted). */
    public double measure(Font font, CharSequence text) {
        if (text == null) return 0;
        boolean bold = false, italic = false;
        float pen = 0;
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            if (c == Formatting.PREFIX) {
                if (i + 1 < length) {
                    int code = Formatting.code(text.charAt(++i));
                    if (code >= 0 && code < 16 || code == Formatting.RESET) {
                        bold = false;
                        italic = false;
                    } else if (code == Formatting.BOLD) {
                        bold = true;
                    } else if (code == Formatting.ITALIC) {
                        italic = true;
                    }
                }
                continue;
            }
            int codePoint = c;
            if (Character.isHighSurrogate(c) && i + 1 < length && Character.isLowSurrogate(text.charAt(i + 1))) {
                codePoint = Character.toCodePoint(c, text.charAt(++i));
            }
            pen += font.variant(bold, italic).face(scale).glyph(codePoint).advance;
        }
        return pen / scale;
    }

    /**
     * Draws text with its line top at {@code y}. Supports {@code §} formatting codes; colour codes keep the base
     * colour's alpha. Returns the width in design units.
     */
    public double text(Font font, CharSequence text, double x, double y, int color) {
        if (text == null) return 0;
        refreshWhite();
        FontFace baseFace = font.face(scale);
        float start = px(x);
        float pen = start;
        int baseline = px(y) + Math.round(baseFace.getAscent());
        int current = a(color);
        int alpha = current & 0xFF000000;
        boolean bold = false, italic = false, underline = false, strike = false;
        float s = atlas.getSize();
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            if (c == Formatting.PREFIX) {
                if (i + 1 < length) {
                    int code = Formatting.code(text.charAt(++i));
                    if (code >= 0 && code < 16) {
                        current = alpha | Formatting.rgb(code);
                        bold = italic = underline = strike = false;
                    } else if (code == Formatting.RESET) {
                        current = a(color);
                        bold = italic = underline = strike = false;
                    } else if (code == Formatting.BOLD) {
                        bold = true;
                    } else if (code == Formatting.ITALIC) {
                        italic = true;
                    } else if (code == Formatting.UNDERLINE) {
                        underline = true;
                    } else if (code == Formatting.STRIKETHROUGH) {
                        strike = true;
                    }
                }
                continue;
            }
            int codePoint = c;
            if (Character.isHighSurrogate(c) && i + 1 < length && Character.isLowSurrogate(text.charAt(i + 1))) {
                codePoint = Character.toCodePoint(c, text.charAt(++i));
            }
            FontFace face = font.variant(bold, italic).face(scale);
            FontFace.Glyph glyph = face.glyph(codePoint);
            if (glyph.hasImage()) {
                int gx = Math.round(pen) + glyph.offsetX, gy = baseline + glyph.offsetY;
                list.quad(gx, gy, gx + glyph.width, gy + glyph.height,
                        glyph.x / s, glyph.y / s, (glyph.x + glyph.width) / s, (glyph.y + glyph.height) / s,
                        current, current, current, current);
            }
            float next = pen + glyph.advance;
            int t = hairline();
            if (underline) {
                int uy = baseline + Math.max(1, Math.round(face.getDescent() / 2));
                list.quad(Math.round(pen), uy, Math.round(next), uy + t, whiteU, whiteV, whiteU, whiteV, current, current, current, current);
            }
            if (strike) {
                int sy = baseline - Math.round(face.getAscent() * 0.3f);
                list.quad(Math.round(pen), sy, Math.round(next), sy + t, whiteU, whiteV, whiteU, whiteV, current, current, current, current);
            }
            pen = next;
        }
        return (pen - start) / scale;
    }

    /** Text with a 1-pixel drop shadow (down-right). */
    public double textShadow(Font font, CharSequence text, double x, double y, int color, int shadow) {
        double offset = 1 / scale;
        text(font, text, x + offset, y + offset, shadow);
        return text(font, text, x, y, color);
    }

    /** Text with a 1-pixel outline on all sides. */
    public double textOutline(Font font, CharSequence text, double x, double y, int color, int outline) {
        double o = 1 / scale;
        text(font, text, x - o, y, outline);
        text(font, text, x + o, y, outline);
        text(font, text, x, y - o, outline);
        text(font, text, x, y + o, outline);
        return text(font, text, x, y, color);
    }

    /** {@code text}, shortened with "…" so it fits {@code maxWidth}. Returns the same instance when it fits. */
    public String fit(Font font, String text, double maxWidth) {
        if (text == null || measure(font, text) <= maxWidth) return text;
        String ellipsis = "…";
        double room = maxWidth - measure(font, ellipsis);
        int end = text.length();
        while (end > 0 && measure(font, text.substring(0, end)) > room) end--;
        if (end > 0 && Character.isHighSurrogate(text.charAt(end - 1))) end--;
        return text.substring(0, end) + ellipsis;
    }

    /** Depth of {@link #pushClip} nesting (for checks). */
    public int getClipDepth() {
        return clipDepth;
    }
}
