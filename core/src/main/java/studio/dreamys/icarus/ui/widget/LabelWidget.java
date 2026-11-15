package studio.dreamys.icarus.ui.widget;

import studio.dreamys.icarus.render.Canvas;
import studio.dreamys.icarus.settings.Label;
import studio.dreamys.icarus.settings.Setting;
import studio.dreamys.icarus.theme.Theme;
import studio.dreamys.icarus.ui.Menu;

/** A text-only row (a {@link Label} field), usually carrying a keybind. */
public final class LabelWidget extends Widget {
    public LabelWidget(Menu menu, Setting<Label> setting) {
        super(menu, setting);
    }

    @Override
    public double layout(Theme theme, Canvas canvas, double x, double y, double width) {
        return theme.layoutLabel(canvas, this, x, y, width);
    }

    @Override
    public void paint(Theme theme, Canvas canvas) {
        theme.paintLabel(canvas, this);
    }
}
