package studio.dreamys.icarus.ui.widget;

import studio.dreamys.icarus.render.Canvas;
import studio.dreamys.icarus.settings.Setting;
import studio.dreamys.icarus.theme.Theme;
import studio.dreamys.icarus.ui.Menu;
import studio.dreamys.icarus.ui.Rect;
import studio.dreamys.icarus.ui.popup.ColorPopup;

import java.awt.Color;

/** A colour swatch that opens a colour picker. */
public final class ColorWidget extends Widget {
    private final Setting<Color> value;
    private final ColorPopup popup;
    /** The swatch, set during layout. */
    public final Rect swatch = new Rect();

    public ColorWidget(Menu menu, Setting<Color> setting) {
        super(menu, setting);
        this.value = setting;
        this.popup = new ColorPopup(this);
    }

    public Color getColor() {
        Color color = value.get();
        return color == null ? Color.WHITE : color;
    }

    public void setColor(Color color) {
        value.set(color);
    }

    public boolean isOpen() {
        return menu.getPopup() == popup;
    }

    @Override
    public boolean contains(double x, double y) {
        return swatch.contains(x, y);
    }

    @Override
    public boolean mousePressed(double x, double y, int button) {
        if (button != 0) return false;
        if (!isOpen()) popup.syncFromColor();
        menu.togglePopup(popup);
        return false;
    }

    @Override
    public double layout(Theme theme, Canvas canvas, double x, double y, double width) {
        return theme.layoutColor(canvas, this, x, y, width);
    }

    @Override
    public void paint(Theme theme, Canvas canvas) {
        theme.paintColor(canvas, this);
    }
}
