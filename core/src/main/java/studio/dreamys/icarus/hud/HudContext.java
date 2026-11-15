package studio.dreamys.icarus.hud;

import studio.dreamys.icarus.theme.Theme;

/** Per-frame information for HUD elements (reused, don't keep a reference). */
public final class HudContext {
    Theme theme;
    int accent;
    double width, height;
    float partialTick;

    public Theme getTheme() {
        return theme;
    }

    /** Accent colour of the owning mod (ARGB). */
    public int getAccent() {
        return accent;
    }

    /** Screen width in design units. */
    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    /** Fraction of the current tick that has passed (for smooth motion between ticks). */
    public float getPartialTick() {
        return partialTick;
    }

    public void set(Theme theme, int accent, double width, double height, float partialTick) {
        this.theme = theme;
        this.accent = accent;
        this.width = width;
        this.height = height;
        this.partialTick = partialTick;
    }
}
