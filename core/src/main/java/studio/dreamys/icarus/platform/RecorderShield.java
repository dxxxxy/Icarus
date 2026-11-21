package studio.dreamys.icarus.platform;

/**
 * Windows over the game that recorders and screen sharing can't see. While "hide from recorders" is on, what Icarus
 * draws (the menu, the toasts, the HUD) goes into these layers instead of into the game. All methods are called on
 * the render thread (the thread that owns the game window).
 */
public interface RecorderShield {
    /** Whether this system can hide windows from capture (false once it turned out it can't). */
    boolean isAvailable();

    /** A new layer, initially hidden; null if layers can't be made. */
    Layer createLayer();

    /** One window of the shield. */
    interface Layer {
        /**
         * Shows premultiplied ARGB {@code pixels} ({@code stride} per row) of {@code width} × {@code height} with its
         * top-left corner at framebuffer pixel ({@code x}, {@code y}) of the game window. When {@code changed} is
         * false the pixels are the same as last time and only the position may differ. Returns false if the layer
         * couldn't be shown hidden from capture; everything is then drawn in game again.
         */
        boolean show(int[] pixels, int stride, int width, int height, int x, int y, boolean changed);

        /** Hides the layer (it may be shown again). */
        void hide();

        /** Puts this layer right under {@code above} (layers drawn later are on top). */
        void placeUnder(Layer above);
    }

    /** Used where nothing can be hidden from capture. */
    RecorderShield UNAVAILABLE = new RecorderShield() {
        @Override
        public boolean isAvailable() {
            return false;
        }

        @Override
        public Layer createLayer() {
            return null;
        }
    };
}
