package studio.dreamys.icarus.hud;

import studio.dreamys.icarus.render.Canvas;

/** A text box in a corner of the HUD, drawn in the theme's style. Add it with {@code IcarusMod#addHud}. */
public final class Watermark implements HudElement {
    public static final double MARGIN = 10;

    private String text;
    private Position position;
    private boolean enabled = true;

    public Watermark(String text) {
        this(text, Position.TOP_RIGHT);
    }

    public Watermark(String text, Position position) {
        this.text = text;
        this.position = position;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /** Height of the watermark with the given context's theme (for placing things below it). */
    public double height(Canvas canvas, HudContext hud) {
        return hud.getTheme().watermarkHeight(canvas);
    }

    @Override
    public void render(Canvas canvas, HudContext hud) {
        if (!enabled || text == null) return;
        double h = hud.getTheme().watermarkHeight(canvas);
        double w = hud.getTheme().watermarkWidth(canvas, this);
        double x = position.x(w, hud.getWidth(), MARGIN);
        double y = position.y(h, hud.getHeight(), MARGIN);
        hud.getTheme().paintWatermark(canvas, this, x, y, w, h, hud.getAccent());
    }
}
