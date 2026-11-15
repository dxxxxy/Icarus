package studio.dreamys.icarus.theme.skeet;

import studio.dreamys.icarus.render.AtlasImage;
import studio.dreamys.icarus.render.Canvas;

import java.util.HashMap;
import java.util.Map;

/**
 * The tiny bitmap font of keybind labels ("[DEL]", "[M5]"), transcribed from the reference: 7-pixel capitals,
 * 1-pixel strokes, 10-pixel brackets. Glyphs are drawn at whole-pixel multiples so they stay crisp at any scale.
 * '#' is a full pixel, '+' a half-covered one.
 */
final class PixelFont {
    /** Rows from the bracket top; capitals occupy rows 1-7. */
    static final int HEIGHT = 10;
    private static final Map<Character, String[]> GLYPHS = new HashMap<>();
    private static final Map<Character, AtlasImage> IMAGES = new HashMap<>();

    private static void g(char c, String... rows) {
        GLYPHS.put(c, rows);
    }

    static {
        g('[', "##+", "#", "#", "#", "#", "#", "#", "#", "#", "##+");
        g(']', "+##", "  #", "  #", "  #", "  #", "  #", "  #", "  #", "  #", "+##");
        g('-', "", "", "", "", "##");
        g('.', "", "", "", "", "", "", "", "#");
        g('/', "", "   #", "   #", "  # ", "  # ", " #  ", " #  ", "#   ");
        g('_', "", "", "", "", "", "", "", "", "####");
        g('+', "", "", "", " # ", "###", " # ");
        g('*', "", "", "", "#");
        g(' ', "", "");
        g('A', "", " ## ", "#  #", "#  #", "####", "#  #", "#  #", "#  #");
        g('B', "", "### ", "#  #", "#  #", "### ", "#  #", "#  #", "### ");
        g('C', "", " ###", "#   ", "#   ", "#   ", "#   ", "#   ", " ###");
        g('D', "", "####", "#  +#", "#   #", "#   #", "#   #", "#  +#", "####");
        g('E', "", "####", "#", "#", "####", "#", "#", "####+");
        g('F', "", "####", "#", "#", "### ", "#", "#", "#");
        g('G', "", " ###", "#   ", "#   ", "# ##", "#  #", "#  #", " ###");
        g('H', "", "#  #", "#  #", "#  #", "####", "#  #", "#  #", "#  #");
        g('I', "", "###", " # ", " # ", " # ", " # ", " # ", "###");
        g('J', "", "  #", "  #", "  #", "  #", "  #", "  #", "## ");
        g('K', "", "#  #", "# # ", "## ", "#  ", "## ", "# # ", "#  #");
        g('L', "", "#", "#", "#", "#", "#", "#", "####");
        g('M', "", "#+  +#", "##  ##", "#++++#", "# ## #", "# ++ #", "#    #", "#    #");
        g('N', "", "#   #", "##  #", "#+  #", "# # #", "#  +#", "#  ##", "#   #");
        g('O', "", " ## ", "#  #", "#  #", "#  #", "#  #", "#  #", " ## ");
        g('P', "", "### ", "#  #", "#  #", "### ", "#", "#", "#");
        g('Q', "", " ## ", "#  #", "#  #", "#  #", "#  #", "# +#", " ## #");
        g('R', "", "####", "#  #", "#  #", "###+", "# ++", "#  #+", "#   #+");
        g('S', "", " ###", "#   ", "#   ", " ## ", "   #", "   #", "### ");
        g('T', "", "#####", "  #  ", "  #  ", "  #  ", "  #  ", "  #  ", "  #  ");
        g('U', "", "#  #", "#  #", "#  #", "#  #", "#  #", "#  #", " ## ");
        g('V', "", "#   #", "#   #", "#   #", "+# #+", " # # ", " +#+ ", "  #  ");
        g('W', "", "#    #", "#    #", "# ++ #", "# ## #", "#+  +#", "##  ##", "#+  +#");
        g('X', "", "#   #", "+# #+", " +#+ ", "  #  ", " +#+ ", "+# #+", "#   #");
        g('Y', "", "#   #", "+# #+", " +#+ ", "  #  ", "  #  ", "  #  ", "  #  ");
        g('Z', "", "####", "   #", "  # ", " #  ", "#   ", "#   ", "####");
        g('0', "", " ## ", "#  #", "#  #", "#  #", "#  #", "#  #", " ## ");
        g('1', "", " # ", "## ", " # ", " # ", " # ", " # ", "###");
        g('2', "", " ## ", "#  #", "   #", "  # ", " #  ", "#   ", "####");
        g('3', "", "### ", "   #", "   #", " ## ", "   #", "   #", "### ");
        g('4', "", "  # ", " ## ", "# # ", "#  #", "####", "   #", "   #");
        g('5', "", "####", "#", "###+", "   #", "   #", "+  #", "### ");
        g('6', "", " ## ", "#   ", "#   ", "### ", "#  #", "#  #", " ## ");
        g('7', "", "####", "   #", "  # ", "  # ", " #  ", " #  ", " #  ");
        g('8', "", " ## ", "#  #", "#  #", " ## ", "#  #", "#  #", " ## ");
        g('9', "", " ## ", "#  #", "#  #", " ###", "   #", "   #", " ## ");
    }

    private PixelFont() {
    }

    private static String[] glyph(char c) {
        String[] rows = GLYPHS.get(Character.toUpperCase(c));
        return rows == null ? GLYPHS.get('-') : rows;
    }

    /** Advances that differ from the glyph's drawn width (the reference lets some tails overlap the spacing). */
    private static final Map<Character, Integer> ADVANCE = new HashMap<>();

    static {
        ADVANCE.put('R', 4);
        ADVANCE.put('D', 6);
        ADVANCE.put('E', 5);
    }

    /** Pixels of the bitmap (widest row). */
    private static int bitmapWidth(String[] rows) {
        int w = 0;
        for (String row : rows) w = Math.max(w, row.length());
        return Math.max(w, 2);
    }

    private static int width(String[] rows) {
        for (Map.Entry<Character, Integer> e : ADVANCE.entrySet()) if (GLYPHS.get(e.getKey()) == rows) return e.getValue();
        return bitmapWidth(rows);
    }

    private static AtlasImage image(char c) {
        char key = Character.toUpperCase(c);
        AtlasImage image = IMAGES.get(key);
        if (image == null) {
            String[] rows = glyph(key);
            int w = bitmapWidth(rows);
            int[] argb = new int[w * HEIGHT];
            for (int y = 0; y < rows.length && y < HEIGHT; y++) {
                for (int x = 0; x < rows[y].length(); x++) {
                    char p = rows[y].charAt(x);
                    int alpha = p == '#' ? 255 : p == '+' ? 110 : 0;
                    argb[y * w + x] = alpha << 24 | 0xFFFFFF;
                }
            }
            image = new AtlasImage(argb, w, HEIGHT);
            IMAGES.put(key, image);
        }
        return image;
    }

    /** Screen pixels per glyph pixel at a render scale (the reference is drawn at 1.25). */
    static int pixel(Canvas c) {
        return Math.max(1, (int) Math.round(c.getScale() / 1.25));
    }

    /** Width in design units. */
    static double measure(Canvas c, String text) {
        int px = 0;
        for (int i = 0; i < text.length(); i++) {
            px += width(glyph(text.charAt(i)));
            if (i + 1 < text.length()) px += spacing(text.charAt(i), text.charAt(i + 1));
        }
        return px * pixel(c) / c.getScale();
    }

    private static int spacing(char a, char b) {
        return 1;
    }

    /** Draws text with the bracket top at {@code y} (design units). */
    static void draw(Canvas c, String text, double x, double y, int color) {
        int unit = pixel(c);
        double scale = c.getScale();
        //snap the origin to a whole pixel so glyph pixels land on screen pixels
        double px = Math.round(x * scale), py = Math.round(y * scale);
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            AtlasImage image = image(ch);
            c.image(image, px / scale, py / scale, image.getWidth() * unit / scale, image.getHeight() * unit / scale, color);
            px += (width(glyph(ch)) + (i + 1 < text.length() ? spacing(ch, text.charAt(i + 1)) : 0)) * unit;
        }
    }
}
