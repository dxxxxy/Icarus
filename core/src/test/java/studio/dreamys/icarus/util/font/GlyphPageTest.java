package studio.dreamys.icarus.util.font;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GlyphPageTest {
    @Test
    void generatesAtlasWithoutAGraphicsContext() {
        char[] chars = new char[256];
        for (int i = 0; i < chars.length; i++) chars[i] = (char) i;

        GlyphPage page = new GlyphPage(new Font("Dialog", Font.PLAIN, 11), true, true);
        page.generateGlyphPage(chars);
        page.setupTexture(); //no-op now: the texture is uploaded lazily on the render thread

        assertTrue(page.getMaxFontHeight() > 0);
        assertTrue(page.getWidth('W') > 8, "glyph widths include 8px of padding");
    }
}
