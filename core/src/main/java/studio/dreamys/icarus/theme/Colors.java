package studio.dreamys.icarus.theme;

/** ARGB helpers for themes. */
public final class Colors {
    private Colors() {
    }

    public static int rgb(int rgb) {
        return 0xFF000000 | rgb;
    }

    public static int withAlpha(int argb, int alpha) {
        return (alpha & 255) << 24 | argb & 0xFFFFFF;
    }

    /** Scales the RGB channels by {@code factor} (0 = black, 1 = unchanged, >1 brighter, clamped). */
    public static int shade(int argb, double factor) {
        int r = clamp((int) Math.round((argb >> 16 & 255) * factor));
        int g = clamp((int) Math.round((argb >> 8 & 255) * factor));
        int b = clamp((int) Math.round((argb & 255) * factor));
        return argb & 0xFF000000 | r << 16 | g << 8 | b;
    }

    /** Mixes two colours ({@code t} = 0 → a, 1 → b). */
    public static int mix(int a, int b, double t) {
        t = Math.max(0, Math.min(1, t));
        int aa = a >>> 24, ar = a >> 16 & 255, ag = a >> 8 & 255, ab = a & 255;
        int ba = b >>> 24, br = b >> 16 & 255, bg = b >> 8 & 255, bb = b & 255;
        return (int) Math.round(aa + (ba - aa) * t) << 24 | (int) Math.round(ar + (br - ar) * t) << 16
                | (int) Math.round(ag + (bg - ag) * t) << 8 | (int) Math.round(ab + (bb - ab) * t);
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }
}
