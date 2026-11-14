package studio.dreamys.icarus.ui.popup;

import studio.dreamys.icarus.render.Canvas;
import studio.dreamys.icarus.theme.Theme;
import studio.dreamys.icarus.ui.Rect;
import studio.dreamys.icarus.ui.widget.ColorWidget;

import java.awt.Color;

/** Saturation/value square, hue bar and alpha bar for a {@link ColorWidget}. Edits are kept in HSV. */
public final class ColorPopup extends Popup {
    private enum Drag { NONE, SV, HUE, ALPHA }

    private final ColorWidget widget;
    /** Parts, set by the theme. */
    public final Rect svSquare = new Rect();
    public final Rect hueBar = new Rect();
    public final Rect alphaBar = new Rect();

    private float hue, saturation, brightness;
    private int alpha = 255;
    private Drag drag = Drag.NONE;

    public ColorPopup(ColorWidget widget) {
        super(widget);
        this.widget = widget;
    }

    public ColorWidget getWidget() {
        return widget;
    }

    /** Reads the current colour into the HSV state (keeps the hue when the colour is grey). */
    public void syncFromColor() {
        Color color = widget.getColor();
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        if (hsb[1] > 0 && hsb[2] > 0) hue = hsb[0];
        saturation = hsb[1];
        brightness = hsb[2];
        alpha = color.getAlpha();
    }

    public float getHue() {
        return hue;
    }

    public float getSaturation() {
        return saturation;
    }

    public float getBrightness() {
        return brightness;
    }

    public int getAlpha() {
        return alpha;
    }

    /** ARGB of the fully saturated, bright current hue. */
    public int hueColor() {
        return 0xFF000000 | Color.HSBtoRGB(hue, 1, 1);
    }

    @Override
    public boolean mousePressed(double x, double y, int button) {
        if (button != 0) return false;
        if (svSquare.contains(x, y)) drag = Drag.SV;
        else if (hueBar.contains(x, y)) drag = Drag.HUE;
        else if (alphaBar.contains(x, y)) drag = Drag.ALPHA;
        else return false;
        mouseDragged(x, y);
        return true;
    }

    @Override
    public void mouseDragged(double x, double y) {
        switch (drag) {
            case SV:
                saturation = clamp((x - svSquare.x) / svSquare.w);
                brightness = 1 - clamp((y - svSquare.y) / svSquare.h);
                break;
            case HUE:
                hue = Math.min(0.9999f, clamp((y - hueBar.y) / hueBar.h));
                break;
            case ALPHA:
                alpha = Math.round(255 * clamp((x - alphaBar.x) / alphaBar.w));
                break;
            default:
                return;
        }
        int rgb = Color.HSBtoRGB(hue, saturation, brightness) & 0xFFFFFF;
        widget.setColor(new Color(alpha << 24 | rgb, true));
    }

    @Override
    public void mouseReleased(double x, double y, int button) {
        if (drag != Drag.NONE) widget.getMenu().interactionEnded();
        drag = Drag.NONE;
    }

    private static float clamp(double v) {
        return (float) Math.max(0, Math.min(1, v));
    }

    @Override
    public void layout(Theme theme, Canvas canvas) {
        theme.layoutColorPicker(canvas, this);
    }

    @Override
    public void paint(Theme theme, Canvas canvas) {
        theme.paintColorPicker(canvas, this);
    }
}
