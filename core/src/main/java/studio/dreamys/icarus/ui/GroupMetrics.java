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
    /**
     * How far below the clipped content top the first row's {@code y} goes, for themes whose rows draw a little
     * above their {@code y} (so the first row isn't clipped).
     */
    public double rowOffset;
    /** How far below the content area rows may still be drawn (themes that fade them out into the frame). */
    public double clipBelow;
    /** Vertical gap between groups stacked in one column; negative means the same as the column gap. */
    public double stackGap = -1;

    public GroupMetrics(double padTop, double padBottom, double padLeft, double padRight, double rowGap, double minHeight) {
        this.padTop = padTop;
        this.padBottom = padBottom;
        this.padLeft = padLeft;
        this.padRight = padRight;
        this.rowGap = rowGap;
        this.minHeight = minHeight;
    }
}
