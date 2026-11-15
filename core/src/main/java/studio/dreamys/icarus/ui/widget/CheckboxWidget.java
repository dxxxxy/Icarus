package studio.dreamys.icarus.ui.widget;

import studio.dreamys.icarus.render.Canvas;
import studio.dreamys.icarus.settings.Setting;
import studio.dreamys.icarus.theme.Theme;
import studio.dreamys.icarus.ui.Menu;
import studio.dreamys.icarus.ui.Rect;

/** A boolean toggle. The whole row (box and label) is clickable. */
public final class CheckboxWidget extends Widget {
    private final Setting<Boolean> value;
    /** The box, set during layout. */
    public final Rect box = new Rect();

    public CheckboxWidget(Menu menu, Setting<Boolean> setting) {
        super(menu, setting);
        this.value = setting;
    }

    public boolean isChecked() {
        Boolean checked = value.get();
        return checked != null && checked;
    }

    public void toggle() {
        value.set(!isChecked());
    }

    @Override
    public boolean mousePressed(double x, double y, int button) {
        if (button != 0) return false;
        toggle();
        return true;
    }

    @Override
    public double layout(Theme theme, Canvas canvas, double x, double y, double width) {
        return theme.layoutCheckbox(canvas, this, x, y, width);
    }

    @Override
    public void paint(Theme theme, Canvas canvas) {
        theme.paintCheckbox(canvas, this);
    }
}
