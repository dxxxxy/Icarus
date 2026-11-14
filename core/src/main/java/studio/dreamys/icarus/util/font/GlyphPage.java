package studio.dreamys.icarus.util.font;

import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.platform.Texture;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class GlyphPage {
    private int imgSize;
    private int maxFontHeight = -1;
    private Font font;
    private boolean antiAliasing;
    private boolean fractionalMetrics;
    private HashMap<Character, Glyph> glyphCharacterMap = new HashMap<>();

    private BufferedImage bufferedImage;
    private Texture loadedTexture;

    public GlyphPage(Font font, boolean antiAliasing, boolean fractionalMetrics) {
        this.font = font;
        this.antiAliasing = antiAliasing;
        this.fractionalMetrics = fractionalMetrics;
    }

    public void generateGlyphPage(char[] chars) {
        // Calculate glyphPageSize
        double maxWidth = -1;
        double maxHeight = -1;

        AffineTransform affineTransform = new AffineTransform();
        FontRenderContext fontRenderContext = new FontRenderContext(affineTransform, antiAliasing, fractionalMetrics);

        for (char ch : chars) {
            Rectangle2D bounds = font.getStringBounds(Character.toString(ch), fontRenderContext);

            if (maxWidth < bounds.getWidth()) maxWidth = bounds.getWidth();
            if (maxHeight < bounds.getHeight()) maxHeight = bounds.getHeight();
        }

        // Leave some additional space

        maxWidth += 2;
        maxHeight += 2;

        imgSize = (int) Math.ceil(Math.max(
                Math.ceil(Math.sqrt(maxWidth * maxWidth * chars.length) / maxWidth),
                Math.ceil(Math.sqrt(maxHeight * maxHeight * chars.length) / maxHeight))
                * Math.max(maxWidth, maxHeight)) + 1;

        bufferedImage = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = (Graphics2D) bufferedImage.getGraphics();

        g.setFont(font);
        // Set Color to Transparent
        g.setColor(new Color(255, 255, 255, 0));
        // Set the image background to transparent
        g.fillRect(0, 0, imgSize, imgSize);

        g.setColor(Color.white);

        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, fractionalMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAliasing ? RenderingHints.VALUE_ANTIALIAS_OFF : RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antiAliasing ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        FontMetrics fontMetrics = g.getFontMetrics();

        int currentCharHeight = 0;
        int posX = 0;
        int posY = 1;

        for (char ch : chars) {
            Glyph glyph = new Glyph();

            Rectangle2D bounds = fontMetrics.getStringBounds(Character.toString(ch), g);

            glyph.width = bounds.getBounds().width + 8; // Leave some additional space
            glyph.height = bounds.getBounds().height;

            if (posX + glyph.width >= imgSize) {
                posX = 0;
                posY += currentCharHeight;
                currentCharHeight = 0;
            }

            glyph.x = posX;
            glyph.y = posY;

            if (glyph.height > maxFontHeight) maxFontHeight = glyph.height;

            if (glyph.height > currentCharHeight) currentCharHeight = glyph.height;

            g.drawString(Character.toString(ch), posX + 2, posY + fontMetrics.getAscent());

            posX += glyph.width;

            glyphCharacterMap.put(ch, glyph);

        }
    }

    /** Nothing is uploaded here anymore: fonts are created during mod init, which may not be on the render thread. */
    public void setupTexture() {
    }

    /** Uploads the glyph atlas on first use (always from the render thread). */
    private Texture getTexture() {
        if (loadedTexture == null) {
            int[] argb = bufferedImage.getRGB(0, 0, imgSize, imgSize, null, 0, imgSize);
            loadedTexture = Icarus.getPlatform().getRenderer().createTexture(imgSize, imgSize, argb, true);
        }
        return loadedTexture;
    }

    public float drawChar(char ch, float x, float y, int color) {
        Glyph glyph = glyphCharacterMap.get(ch);

        if (glyph == null) throw new IllegalArgumentException("'" + ch + "' wasn't found");

        float pageX = glyph.x / (float) imgSize;
        float pageY = glyph.y / (float) imgSize;

        float pageWidth = glyph.width / (float) imgSize;
        float pageHeight = glyph.height / (float) imgSize;

        float width = glyph.width;
        float height = glyph.height;

        Icarus.getPlatform().getRenderer().drawTexture(getTexture(), x, y, width, height, pageX, pageY, pageX + pageWidth, pageY + pageHeight, color);

        return width - 8;
    }

    public float getWidth(char ch) {
        return glyphCharacterMap.get(ch).width;
    }

    public int getMaxFontHeight() {
        return maxFontHeight;
    }

    static class Glyph {
        private int x;
        private int y;
        private int width;
        private int height;

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }
}
