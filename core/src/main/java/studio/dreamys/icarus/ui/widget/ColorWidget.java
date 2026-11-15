package studio.dreamys.icarus.ui.widget;

import studio.dreamys.icarus.render.Canvas;
import studio.dreamys.icarus.settings.Setting;
import studio.dreamys.icarus.theme.Theme;
import studio.dreamys.icarus.ui.Menu;
import studio.dreamys.icarus.ui.Rect;
import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.ui.popup.ColorPopup;
import studio.dreamys.icarus.ui.popup.ContextPopup;

import java.awt.Color;

/** A colour swatch that opens a colour picker. */
public final class ColorWidget extends Widget {
    private final Setting<Color> value;
    private final ColorPopup popup;
    private final ContextPopup context;
    /** The swatch, set during layout. */
    public final Rect swatch = new Rect();

    public ColorWidget(Menu menu, Setting<Color> setting) {
        super(menu, setting);
        this.value = setting;
        this.popup = new ColorPopup(this);
        this.context = new ContextPopup(this, index -> {
            if (index == 0) Icarus.getPlatform().setClipboard(format(getColor()));
            else {
                Color pasted = parse(Icarus.getPlatform().getClipboard());
                if (pasted != null) setColor(pasted);
            }
        }, "Copy", "Paste");
    }

    /** Clipboard form: {@code #RRGGBBAA}. */
    public static String format(Color color) {
        return String.format("#%02X%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    /** Reads {@code #RRGGBB}, {@code #RRGGBBAA} (with or without '#'); null if it isn't a colour. */
    public static Color parse(String text) {
        if (text == null) return null;
        String hex = text.trim();
        if (hex.startsWith("#")) hex = hex.substring(1);
        if (hex.length() != 6 && hex.length() != 8) return null;
        try {
            long value = Long.parseLong(hex, 16);
            if (hex.length() == 6) return new Color((int) value);
            return new Color((int) (value >> 24 & 255), (int) (value >> 16 & 255), (int) (value >> 8 & 255), (int) (value & 255));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Whether this swatch sits on the previous setting's row ({@code @IAttach}). */
    public boolean isAttached() {
        return setting.isAttached();
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
        if (button == 1) {
            menu.openContext(context, x, y);
            return false;
        }
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
