package studio.dreamys.icarus.ui;

/** A mutable rectangle in design units. Containment is half-open: {@code [x, x + w) × [y, y + h)}. */
public final class Rect {
    public double x, y, w, h;

    public Rect set(double x, double y, double w, double h) {
        this.x = x;
        this.y = y;
        this.w = Math.max(0, w);
        this.h = Math.max(0, h);
        return this;
    }

    public Rect set(Rect other) {
        return set(other.x, other.y, other.w, other.h);
    }

    public Rect clear() {
        return set(0, 0, 0, 0);
    }

    public boolean contains(double px, double py) {
        return w > 0 && h > 0 && px >= x && px < x + w && py >= y && py < y + h;
    }

    public boolean isEmpty() {
        return w <= 0 || h <= 0;
    }

    public double right() {
        return x + w;
    }

    public double bottom() {
        return y + h;
    }

    public double centerX() {
        return x + w / 2;
    }

    public double centerY() {
        return y + h / 2;
    }

    @Override
    public String toString() {
        return "Rect[" + x + ", " + y + ", " + w + "x" + h + "]";
    }
}
