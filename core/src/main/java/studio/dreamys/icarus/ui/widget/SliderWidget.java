package studio.dreamys.icarus.ui.widget;

import studio.dreamys.icarus.render.Canvas;
import studio.dreamys.icarus.settings.Setting;
import studio.dreamys.icarus.theme.Theme;
import studio.dreamys.icarus.ui.Menu;
import studio.dreamys.icarus.ui.Rect;

/** A numeric slider. Dragging sets the value from the mouse position before the frame is laid out. */
public final class SliderWidget extends Widget {
    /** Extra grab margin above and below the track, in design units. */
    public static final double GRAB_MARGIN = 4;

    private final Setting<Double> value;
    private final Setting.SliderSpec spec;
    /** The track, set during layout. */
    public final Rect track = new Rect();
    private boolean dragging;
    private double cachedValue = Double.NaN;
    private String cachedText;

    public SliderWidget(Menu menu, Setting<Double> setting) {
        super(menu, setting);
        this.value = setting;
        this.spec = setting.getSlider();
    }

    public Setting.SliderSpec getSpec() {
        return spec;
    }

    public double getValue() {
        Double v = value.get();
        return v == null ? spec.getMin() : v;
    }

    /** Filled fraction of the track, 0..1. */
    public double getFraction() {
        return spec.fraction(getValue());
    }

    /** The formatted value (cached until it changes). */
    public String getValueText() {
        double v = getValue();
        if (v != cachedValue || cachedText == null) {
            cachedValue = v;
            cachedText = spec.format(v);
        }
        return cachedText;
    }

    public boolean isDragging() {
        return dragging;
    }

    @Override
    public boolean contains(double x, double y) {
        return x >= track.x && x < track.right() && y >= track.y - GRAB_MARGIN && y < track.bottom() + GRAB_MARGIN;
    }

    @Override
    public boolean mousePressed(double x, double y, int button) {
        if (button != 0) return false;
        dragging = true;
        mouseDragged(x, y);
        return true;
    }

    @Override
    public void mouseDragged(double x, double y) {
        if (!dragging || track.w <= 0) return;
        value.set(spec.valueAt((x - track.x) / track.w));
    }

    @Override
    public void mouseReleased(double x, double y, int button) {
        dragging = false;
    }

    @Override
    public double layout(Theme theme, Canvas canvas, double x, double y, double width) {
        return theme.layoutSlider(canvas, this, x, y, width);
    }

    @Override
    public void paint(Theme theme, Canvas canvas) {
        theme.paintSlider(canvas, this);
    }
}
