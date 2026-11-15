package studio.dreamys.icarus.ui.widget;

import studio.dreamys.icarus.render.Canvas;
import studio.dreamys.icarus.settings.Setting;
import studio.dreamys.icarus.theme.Theme;
import studio.dreamys.icarus.ui.Menu;
import studio.dreamys.icarus.ui.Rect;
import studio.dreamys.icarus.ui.popup.DropdownPopup;

import java.util.ArrayList;
import java.util.List;

/** Picks any number of a fixed set of options from a dropdown that stays open. */
public final class ComboWidget extends Widget implements DropdownPopup.Source {
    private final Setting<String[]> value;
    private final DropdownPopup popup;
    /** The closed dropdown box, set during layout. */
    public final Rect box = new Rect();

    private String[] cachedFor;
    private String summary;

    public ComboWidget(Menu menu, Setting<String[]> setting) {
        super(menu, setting);
        this.value = setting;
        this.popup = new DropdownPopup(this, this);
    }

    public boolean isOpen() {
        return menu.getPopup() == popup;
    }

    /** Selected options joined with ", " in option order, or "-" (cached until the selection changes). */
    public String getSummary() {
        String[] selected = value.get();
        if (selected != cachedFor || summary == null) {
            cachedFor = selected;
            StringBuilder text = new StringBuilder();
            for (String option : getOptions()) {
                if (contains(selected, option)) {
                    if (text.length() > 0) text.append(", ");
                    text.append(option);
                }
            }
            summary = text.length() == 0 ? "-" : text.toString();
        }
        return summary;
    }

    private static boolean contains(String[] values, String option) {
        if (values == null) return false;
        for (String v : values) if (option.equals(v)) return true;
        return false;
    }

    @Override
    public List<String> getOptions() {
        return setting.getOptions();
    }

    @Override
    public boolean isSelected(int index) {
        return contains(value.get(), getOptions().get(index));
    }

    @Override
    public boolean choose(int index) {
        String option = getOptions().get(index);
        boolean selected = isSelected(index);
        List<String> next = new ArrayList<>();
        for (String o : getOptions()) {
            if (o.equals(option) ? !selected : contains(value.get(), o)) next.add(o);
        }
        value.set(next.toArray(new String[0]));
        menu.interactionEnded();
        return false;
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
        return theme.layoutCombo(canvas, this, x, y, width);
    }

    @Override
    public void paint(Theme theme, Canvas canvas) {
        theme.paintCombo(canvas, this);
    }
}
