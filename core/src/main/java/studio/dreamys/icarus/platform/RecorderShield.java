package studio.dreamys.icarus.platform;

/**
 * A window over the game that recorders and screen sharing can't see. While "hide from recorders" is on, the menu is
 * drawn into it instead of into the game. All methods are called on the render thread (the thread that owns the game
 * window).
 */
public interface RecorderShield {
    /** Whether this system can hide a window from capture (false once it turned out it can't). */
    boolean isAvailable();

    /**
     * Shows premultiplied ARGB {@code pixels} ({@code stride} per row) of {@code width} × {@code height} with its
     * top-left corner at framebuffer pixel ({@code x}, {@code y}) of the game window. When {@code changed} is false
     * the pixels are the same as last time and only the position may differ. Returns false if the window couldn't be
     * shown hidden from capture; the menu is then drawn in game again.
     */
    boolean show(int[] pixels, int stride, int width, int height, int x, int y, boolean changed);

    /** Hides the window (it may be shown again). */
    void hide();

    /** Used where nothing can be hidden from capture. */
    RecorderShield UNAVAILABLE = new RecorderShield() {
        @Override
        public boolean isAvailable() {
            return false;
        }

        @Override
        public boolean show(int[] pixels, int stride, int width, int height, int x, int y, boolean changed) {
            return false;
        }

        @Override
        public void hide() {
        }
    };
}
