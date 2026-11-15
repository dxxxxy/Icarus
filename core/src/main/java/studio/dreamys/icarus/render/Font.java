package studio.dreamys.icarus.render;

/**
 * A font family/style/size in design units (1 design unit = 1 screen pixel at Minecraft GUI scale 2). Text is
 * rasterised at the real pixel size for the current render scale, so it stays sharp at every scale.
 */
public final class Font {
    private final FontAtlas atlas;
    private final String family;
    private final int style;
    private final double size;

    private double lastScale = -1;
    private FontFace lastFace;
    private Font bold, italic, boldItalic;

    public Font(FontAtlas atlas, String family, int style, double size) {
        this.atlas = atlas;
        this.family = family;
        this.style = style;
        this.size = size;
    }

    public String getFamily() {
        return family;
    }

    /** {@link java.awt.Font} style bits. */
    public int getStyle() {
        return style;
    }

    /** Size in design units. */
    public double getSize() {
        return size;
    }

    /** The rasterised face for a render scale (pixels per design unit). */
    public FontFace face(double scale) {
        if (scale != lastScale) {
            lastFace = atlas.face(family, style, Math.max(1, (int) Math.round(size * scale)));
            lastScale = scale;
        }
        return lastFace;
    }

    /** This font with bold and/or italic added (for formatting codes). */
    public Font variant(boolean wantBold, boolean wantItalic) {
        if (!wantBold && !wantItalic) return this;
        if (wantBold && wantItalic) {
            if (boldItalic == null) boldItalic = new Font(atlas, family, style | java.awt.Font.BOLD | java.awt.Font.ITALIC, size);
            return boldItalic;
        }
        if (wantBold) {
            if (bold == null) bold = new Font(atlas, family, style | java.awt.Font.BOLD, size);
            return bold;
        }
        if (italic == null) italic = new Font(atlas, family, style | java.awt.Font.ITALIC, size);
        return italic;
    }
}
