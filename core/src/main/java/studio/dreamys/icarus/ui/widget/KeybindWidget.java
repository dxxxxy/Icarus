package studio.dreamys.icarus.ui.widget;

import studio.dreamys.icarus.input.Keys;
import studio.dreamys.icarus.render.Canvas;
import studio.dreamys.icarus.settings.KeybindMode;
import studio.dreamys.icarus.settings.Keybinds;
import studio.dreamys.icarus.settings.SettingTypes;
import studio.dreamys.icarus.settings.Setting;
import studio.dreamys.icarus.theme.Theme;
import studio.dreamys.icarus.ui.Menu;
import studio.dreamys.icarus.ui.Rect;
import studio.dreamys.icarus.ui.popup.ContextPopup;

import java.util.ArrayList;
import java.util.List;

/**
 * The key bound to a button or checkbox, shown in its row. Click to capture: the next key binds it,
 * Escape cancels, Backspace/Delete clears, and the menu key is refused.
 */
public final class KeybindWidget extends Widget {
    private final Keybinds keybinds;
    private final Widget owner;
    private final ContextPopup modes;
    /** The clickable label, set during layout. */
    public final Rect label = new Rect();
    private boolean capturing;
    private long refusedAt;

    private int cachedKey = Integer.MIN_VALUE;
    private boolean cachedCapturing;
    private boolean cachedRefused;
    private Theme cachedTheme;
    private String cachedText;

    public KeybindWidget(Menu menu, Setting<?> setting, Keybinds keybinds, Widget owner) {
        super(menu, setting);
        this.keybinds = keybinds;
        this.owner = owner;
        List<String> labels = new ArrayList<>();
        for (KeybindMode mode : KeybindMode.values()) labels.add(mode.getLabel());
        this.modes = new ContextPopup(this, labels, () -> getMode().ordinal(), index -> keybinds.setMode(setting, KeybindMode.values()[index]));
    }

    /** Whether the binding has modes the user can pick (checkboxes). */
    public boolean hasModes() {
        return setting.getType() == SettingTypes.CHECKBOX;
    }

    public KeybindMode getMode() {
        KeybindMode mode = keybinds.getMode(setting);
        return mode == null ? KeybindMode.TOGGLE : mode;
    }

    public Widget getOwner() {
        return owner;
    }

    public int getKey() {
        return keybinds.get(setting);
    }

    public boolean isCapturing() {
        return capturing;
    }

    /** Whether the last key was refused (it's the menu key) within the last second. */
    public boolean isRefused() {
        return System.nanoTime() - refusedAt < 1_000_000_000L;
    }

    /** The label text, formatted by the theme and cached until something changes. */
    public String getText(Theme theme) {
        int key = getKey();
        boolean refused = isRefused();
        if (key != cachedKey || capturing != cachedCapturing || refused != cachedRefused || theme != cachedTheme || cachedText == null) {
            cachedKey = key;
            cachedCapturing = capturing;
            cachedRefused = refused;
            cachedTheme = theme;
            cachedText = theme.formatKeybind(key, capturing, refused);
        }
        return cachedText;
    }

    @Override
    public boolean contains(double x, double y) {
        return label.contains(x, y);
    }

    @Override
    public boolean mousePressed(double x, double y, int button) {
        if (button == 1) {
            if (hasModes()) menu.openContext(modes, x, y);
            return false;
        }
        if (button != 0) return false;
        capturing = true;
        menu.focus(this);
        return false;
    }

    @Override
    public boolean keyPressed(int key) {
        if (!capturing) return false;
        if (key == Keys.KEY_ESCAPE) {
            //cancel, keeping the old binding
        } else if (key == Keys.KEY_BACK || key == Keys.KEY_DELETE) {
            keybinds.set(setting, Keys.KEY_NONE);
        } else if (key == menu.getMod().getMenuKey()) {
            refusedAt = System.nanoTime();
            return true;
        } else if (key > 0) {
            keybinds.set(setting, key);
        }
        menu.focus(null);
        return true;
    }

    /** While capturing: binds a mouse button (right, middle or side). */
    public boolean bindMouse(int button) {
        if (!capturing || button < 1) return false;
        keybinds.set(setting, Keys.mouse(button));
        menu.focus(null);
        return true;
    }

    @Override
    public void focusLost() {
        capturing = false;
        menu.interactionEnded();
    }

    @Override
    public double layout(Theme theme, Canvas canvas, double x, double y, double width) {
        return 0; //laid out by its owner's row
    }

    @Override
    public void paint(Theme theme, Canvas canvas) {
        theme.paintKeybind(canvas, this);
    }
}
