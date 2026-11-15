package studio.dreamys.icarus.hud;

import studio.dreamys.icarus.render.Canvas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** A mod's toast stack: newest at the bottom, sliding in from the right edge. */
public final class Toasts implements HudElement {
    public static final double MARGIN = 10;
    public static final double GAP = 4;

    private final List<Toast> active = new ArrayList<>();
    private double offsetY;

    /** Queues a toast. */
    public void send(Toast toast) {
        active.add(toast);
    }

    public void send(String title, String message) {
        send(new Toast(title, message));
    }

    /** Extra space above the stack (e.g. to start below a watermark), in design units. */
    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }

    public List<Toast> getActive() {
        return active;
    }

    public boolean hasActive() {
        return !active.isEmpty();
    }

    @Override
    public void tick() {
        Iterator<Toast> it = active.iterator();
        while (it.hasNext()) {
            Toast n = it.next();
            n.age++;
            if (n.isFinished()) it.remove();
        }
    }

    @Override
    public void render(Canvas canvas, HudContext hud) {
        double y = MARGIN + offsetY;
        for (int i = 0; i < active.size(); i++) {
            Toast n = active.get(i);
            double visibility = n.visibility(hud.getPartialTick());
            double w = hud.getTheme().toastWidth(canvas, n);
            double h = hud.getTheme().toastHeight(canvas, n);
            double x = hud.getWidth() - MARGIN - w * visibility;
            hud.getTheme().paintToast(canvas, n, x, y, w, h, visibility, hud.getAccent());
            y += (h + GAP) * visibility;
        }
    }
}
