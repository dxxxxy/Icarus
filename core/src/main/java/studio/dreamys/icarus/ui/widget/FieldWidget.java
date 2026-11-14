package studio.dreamys.icarus.ui.widget;

import studio.dreamys.icarus.input.Keys;
import studio.dreamys.icarus.render.Canvas;
import studio.dreamys.icarus.settings.Setting;
import studio.dreamys.icarus.theme.Theme;
import studio.dreamys.icarus.ui.Menu;
import studio.dreamys.icarus.ui.Rect;

/**
 * A single-line text field. Click to focus; typing appends any printable character up to the setting's maximum
 * length, Backspace deletes, Enter or Escape unfocuses. The text is saved when the field loses focus.
 */
public final class FieldWidget extends Widget {
    private final Setting<String> value;
    /** The text box, set during layout. */
    public final Rect box = new Rect();
    private long focusedAt;

    public FieldWidget(Menu menu, Setting<String> setting) {
        super(menu, setting);
        this.value = setting;
    }

    public String getText() {
        String text = value.get();
        return text == null ? "" : text;
    }

    public boolean isFocused() {
        return menu.getFocused() == this;
    }

    /** Whether the caret is in the visible half of its blink cycle. */
    public boolean isCaretVisible() {
        return isFocused() && ((System.nanoTime() - focusedAt) / 500_000_000L) % 2 == 0;
    }

    @Override
    public boolean contains(double x, double y) {
        return box.contains(x, y);
    }

    @Override
    public boolean mousePressed(double x, double y, int button) {
        if (button != 0) return false;
        menu.focus(this);
        focusedAt = System.nanoTime();
        return false;
    }

    @Override
    public boolean keyPressed(int key) {
        if (key == Keys.KEY_BACK) {
            String text = getText();
            if (!text.isEmpty()) {
                int cut = text.offsetByCodePoints(text.length(), -1);
                value.set(text.substring(0, cut));
            }
            focusedAt = System.nanoTime();
            return true;
        }
        if (key == Keys.KEY_RETURN || key == Keys.KEY_NUMPADENTER || key == Keys.KEY_ESCAPE) {
            menu.focus(null);
            return true;
        }
        return true; //swallow everything else while typing (so it can't trigger keybinds)
    }

    @Override
    public void charTyped(int codePoint) {
        if (Character.isISOControl(codePoint) || !Character.isDefined(codePoint)) return;
        String text = getText();
        if (text.codePointCount(0, text.length()) >= setting.getMaxLength()) return;
        value.set(text + new String(Character.toChars(codePoint)));
        focusedAt = System.nanoTime();
    }

    @Override
    public void focusLost() {
        menu.interactionEnded();
    }

    @Override
    public double layout(Theme theme, Canvas canvas, double x, double y, double width) {
        return theme.layoutField(canvas, this, x, y, width);
    }

    @Override
    public void paint(Theme theme, Canvas canvas) {
        theme.paintField(canvas, this);
    }
}
