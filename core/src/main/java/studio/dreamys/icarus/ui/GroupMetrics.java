package studio.dreamys.icarus.ui;

/** Spacing a theme uses inside group boxes, in design units. */
public final class GroupMetrics {
    /** Space from the group's top edge to its first row (includes the title). */
    public double padTop;
    public double padBottom;
    public double padLeft;
    public double padRight;
    /** Vertical gap between rows. */
    public double rowGap;
    /** Minimum height of a group (title only). */
    public double minHeight;

    public GroupMetrics(double padTop, double padBottom, double padLeft, double padRight, double rowGap, double minHeight) {
        this.padTop = padTop;
        this.padBottom = padBottom;
        this.padLeft = padLeft;
        this.padRight = padRight;
        this.rowGap = rowGap;
        this.minHeight = minHeight;
    }
}
