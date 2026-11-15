package studio.dreamys.icarus.hud;

import studio.dreamys.icarus.render.Canvas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** A mod's notification stack: newest at the bottom, sliding in from the right edge. */
public final class Notifications implements HudElement {
    public static final double MARGIN = 10;
    public static final double GAP = 4;

    private final List<Notification> active = new ArrayList<>();
    private double offsetY;

    /** Queues a notification. */
    public void send(Notification notification) {
        active.add(notification);
    }

    public void send(String title, String message) {
        send(new Notification(title, message));
    }

    /** Extra space above the stack (e.g. to start below a watermark), in design units. */
    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }

    public List<Notification> getActive() {
        return active;
    }

    public boolean hasActive() {
        return !active.isEmpty();
    }

    @Override
    public void tick() {
        Iterator<Notification> it = active.iterator();
        while (it.hasNext()) {
            Notification n = it.next();
            n.age++;
            if (n.isFinished()) it.remove();
        }
    }

    @Override
    public void render(Canvas canvas, HudContext hud) {
        double y = MARGIN + offsetY;
        for (int i = 0; i < active.size(); i++) {
            Notification n = active.get(i);
            double visibility = n.visibility(hud.getPartialTick());
            double w = hud.getTheme().notificationWidth(canvas, n);
            double h = hud.getTheme().notificationHeight(canvas, n);
            double x = hud.getWidth() - MARGIN - w * visibility;
            hud.getTheme().paintNotification(canvas, n, x, y, w, h, visibility, hud.getAccent());
            y += (h + GAP) * visibility;
        }
    }
}
