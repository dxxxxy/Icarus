package studio.dreamys.icarus.ui.widget;

import studio.dreamys.icarus.render.Canvas;
import studio.dreamys.icarus.settings.Setting;
import studio.dreamys.icarus.theme.Theme;
import studio.dreamys.icarus.ui.Menu;
import studio.dreamys.icarus.ui.Rect;

/** Runs the setting's {@link Runnable} when clicked. */
public final class ButtonWidget extends Widget {
    /** Supplies a label that can change (e.g. a "Save" button showing unsaved changes). */
    public interface LabelSource {
        String label();
    }

    private final Setting<Runnable> action;
    /** The button face, set during layout. */
    public final Rect button = new Rect();
    private boolean pressed;
    private LabelSource labelSource;

    public ButtonWidget(Menu menu, Setting<Runnable> setting) {
        super(menu, setting);
        this.action = setting;
    }

    public void setLabelSource(LabelSource labelSource) {
        this.labelSource = labelSource;
    }

    @Override
    public String getLabel() {
        return labelSource != null ? labelSource.label() : super.getLabel();
    }

    public boolean isPressed() {
        return pressed;
    }

    public void run() {
        Runnable runnable = action.get();
        if (runnable != null) runnable.run();
    }

    @Override
    public boolean contains(double x, double y) {
        return button.contains(x, y);
    }

    @Override
    public boolean mousePressed(double x, double y, int button) {
        if (button != 0) return false;
        pressed = true;
        run();
        return true;
    }

    @Override
    public void mouseReleased(double x, double y, int button) {
        pressed = false;
    }

    @Override
    public double layout(Theme theme, Canvas canvas, double x, double y, double width) {
        return theme.layoutButton(canvas, this, x, y, width);
    }

    @Override
    public void paint(Theme theme, Canvas canvas) {
        theme.paintButton(canvas, this);
    }
}
