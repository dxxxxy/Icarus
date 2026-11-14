package studio.dreamys.icarus.hud;

/** Anchors for HUD elements. Stateless: {@link #x}/{@link #y} place a box of a given size on a screen of a given size. */
public enum Position {
    TOP_LEFT(0, 0), TOP_CENTER(1, 0), TOP_RIGHT(2, 0),
    CENTER_LEFT(0, 1), CENTER(1, 1), CENTER_RIGHT(2, 1),
    BOTTOM_LEFT(0, 2), BOTTOM_CENTER(1, 2), BOTTOM_RIGHT(2, 2);

    private final int column, row;

    Position(int column, int row) {
        this.column = column;
        this.row = row;
    }

    /** Left edge of a {@code width}-wide box on a {@code screenWidth}-wide screen, {@code margin} from the edge. */
    public double x(double width, double screenWidth, double margin) {
        switch (column) {
            case 0:
                return margin;
            case 1:
                return Math.round((screenWidth - width) / 2);
            default:
                return screenWidth - width - margin;
        }
    }

    /** Top edge of a {@code height}-tall box on a {@code screenHeight}-tall screen, {@code margin} from the edge. */
    public double y(double height, double screenHeight, double margin) {
        switch (row) {
            case 0:
                return margin;
            case 1:
                return Math.round((screenHeight - height) / 2);
            default:
                return screenHeight - height - margin;
        }
    }
}
