package studio.dreamys.icarus.render;

/**
 * Minecraft-style {@code §} formatting codes, shared by text drawing and measuring so both always agree.
 * A {@code §} and the character after it are never drawn; unknown codes are ignored.
 */
public final class Formatting {
    public static final char PREFIX = '§';

    public static final int BOLD = 17, STRIKETHROUGH = 18, UNDERLINE = 19, ITALIC = 20, RESET = 21, OBFUSCATED = 16;
    public static final int NONE = -1;

    private static final int[] PALETTE = {
            0x000000, 0x0000AA, 0x00AA00, 0x00AAAA, 0xAA0000, 0xAA00AA, 0xFFAA00, 0xAAAAAA,
            0x555555, 0x5555FF, 0x55FF55, 0x55FFFF, 0xFF5555, 0xFF55FF, 0xFFFF55, 0xFFFFFF
    };

    private Formatting() {
    }

    /**
     * The meaning of the character after {@code §} (case-insensitive): 0–15 colours, {@link #OBFUSCATED},
     * {@link #BOLD}, {@link #STRIKETHROUGH}, {@link #UNDERLINE}, {@link #ITALIC}, {@link #RESET}, or {@link #NONE}.
     */
    public static int code(char c) {
        if (c >= '0' && c <= '9') return c - '0';
        if (c >= 'a' && c <= 'f') return c - 'a' + 10;
        if (c >= 'A' && c <= 'F') return c - 'A' + 10;
        switch (Character.toLowerCase(c)) {
            case 'k':
                return OBFUSCATED;
            case 'l':
                return BOLD;
            case 'm':
                return STRIKETHROUGH;
            case 'n':
                return UNDERLINE;
            case 'o':
                return ITALIC;
            case 'r':
                return RESET;
            default:
                return NONE;
        }
    }

    /** RGB of a colour code (0–15). */
    public static int rgb(int colorCode) {
        return PALETTE[colorCode];
    }

    /** The text without formatting codes. */
    public static String strip(String text) {
        if (text.indexOf(PREFIX) < 0) return text;
        StringBuilder out = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == PREFIX) {
                i++;
                continue;
            }
            out.append(c);
        }
        return out.toString();
    }
}
