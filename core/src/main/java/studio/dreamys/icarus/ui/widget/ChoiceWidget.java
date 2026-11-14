package studio.dreamys.icarus.ui.widget;

import studio.dreamys.icarus.render.Canvas;
import studio.dreamys.icarus.settings.Setting;
import studio.dreamys.icarus.theme.Theme;
import studio.dreamys.icarus.ui.Menu;
import studio.dreamys.icarus.ui.Rect;
import studio.dreamys.icarus.ui.popup.DropdownPopup;

import java.util.List;

/** Picks one of a fixed set of options from a dropdown. */
public final class ChoiceWidget extends Widget implements DropdownPopup.Source {
    private final Setting<String> value;
    private final DropdownPopup popup;
    /** The closed dropdown box, set during layout. */
    public final Rect box = new Rect();

    public ChoiceWidget(Menu menu, Setting<String> setting) {
        super(menu, setting);
        this.value = setting;
        this.popup = new DropdownPopup(this, this);
    }

    public String getSelected() {
        return value.get();
    }

    public boolean isOpen() {
        return menu.getPopup() == popup;
    }

    @Override
    public List<String> getOptions() {
        return setting.getOptions();
    }

    @Override
    public boolean isSelected(int index) {
        return getOptions().get(index).equals(value.get());
    }

    @Override
    public boolean choose(int index) {
        value.set(getOptions().get(index));
        menu.interactionEnded();
        return true;
    }

    @Override
    public boolean contains(double x, double y) {
        return box.contains(x, y);
    }

    @Override
    public boolean mousePressed(double x, double y, int button) {
        if (button != 0) return false;
        menu.togglePopup(popup);
        return false;
    }

    @Override
    public double layout(Theme theme, Canvas canvas, double x, double y, double width) {
        return theme.layoutChoice(canvas, this, x, y, width);
    }

    @Override
    public void paint(Theme theme, Canvas canvas) {
        theme.paintChoice(canvas, this);
    }
}
