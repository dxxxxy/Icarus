package studio.dreamys.icarus.render;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * A font at one real pixel size. Glyphs are rasterised with AWT the first time a code point is used and packed
 * into the shared {@link FontAtlas}; afterwards lookups and measuring don't allocate.
 */
public final class FontFace {
    private final FontAtlas atlas;
    private final Font font;
    private final FontRenderContext frc;
    private final float ascent, descent, lineHeight;
    private final IntMap<Glyph> glyphs = new IntMap<>();

    FontFace(FontAtlas atlas, String family, int style, int pixelSize) {
        this.atlas = atlas;
        this.font = new Font(family, style, 1).deriveFont((float) pixelSize);
        this.frc = new FontRenderContext(new AffineTransform(), true, true);
        LineMetrics metrics = font.getLineMetrics("Hg", frc);
        this.ascent = metrics.getAscent();
        this.descent = metrics.getDescent();
        this.lineHeight = (float) Math.ceil(metrics.getAscent() + metrics.getDescent());
    }

    /** Pixels from the top of a line to the baseline. */
    public float getAscent() {
        return ascent;
    }

    public float getDescent() {
        return descent;
    }

    public float getLineHeight() {
        return lineHeight;
    }

    /** Whether the font has a real glyph for the code point (otherwise its missing-glyph box is drawn). */
    public boolean canDisplay(int codePoint) {
        return font.canDisplay(codePoint);
    }

    /** The glyph for a code point, rasterising it on first use. */
    public Glyph glyph(int codePoint) {
        Glyph glyph = glyphs.get(codePoint);
        if (glyph == null || glyph.generation != atlas.getGeneration()) {
            glyph = rasterise(codePoint);
            glyphs.put(codePoint, glyph);
        }
        return glyph;
    }

    void clearGlyphs() {
        glyphs.clear();
    }

    private Glyph rasterise(int codePoint) {
        GlyphVector vector = font.createGlyphVector(frc, new String(Character.toChars(codePoint)));
        float advance = (float) vector.getGlyphMetrics(0).getAdvanceX();
        Rectangle bounds = vector.getPixelBounds(frc, 0, 0);

        Glyph glyph = new Glyph();
        glyph.advance = advance;
        glyph.generation = atlas.getGeneration();
        if (bounds.width <= 0 || bounds.height <= 0) return glyph; //whitespace

        int w = bounds.width + 2, h = bounds.height + 2;
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setColor(java.awt.Color.WHITE);
        g.drawGlyphVector(vector, 1 - bounds.x, 1 - bounds.y);
        g.dispose();

        int[] argb = image.getRGB(0, 0, w, h, null, 0, w);
        for (int i = 0; i < argb.length; i++) argb[i] = (argb[i] & 0xFF000000) | 0x00FFFFFF; //white, coverage in alpha

        int generation = atlas.getGeneration();
        int[] at = atlas.add(argb, w, h);
        if (atlas.getGeneration() != generation) {
            //the atlas was cleared to make room; this glyph made it into the new generation
            glyph.generation = atlas.getGeneration();
        }
        if (at == null) return glyph;
        glyph.x = at[0];
        glyph.y = at[1];
        glyph.width = w;
        glyph.height = h;
        glyph.offsetX = bounds.x - 1;
        glyph.offsetY = bounds.y - 1;
        return glyph;
    }

    /** A rasterised glyph: atlas region plus placement relative to the pen position on the baseline. */
    public static final class Glyph {
        int x, y, width, height;
        int offsetX, offsetY;
        float advance;
        int generation;

        public float getAdvance() {
            return advance;
        }

        public boolean hasImage() {
            return width > 0;
        }
    }
}
