package studio.dreamys.icarus.ui;

/**
 * What a platform's menu screen forwards to Icarus. Coordinates are Minecraft GUI units; key codes are
 * {@link studio.dreamys.icarus.input.Keys} codes; mouse buttons are numbered like LWJGL/GLFW (0 left, 1 right,
 * 2 middle).
 */
public interface MenuController {
    /** The screen was opened (or resized). */
    void opened();

    /** Draws the menu. Called once per frame from the screen's render method. */
    void render(double mouseX, double mouseY, float partialTick);

    void mousePressed(double mouseX, double mouseY, int button);

    void mouseReleased(double mouseX, double mouseY, int button);

    /** {@code amount} > 0 scrolls up. */
    void mouseScrolled(double mouseX, double mouseY, double amount);

    /**
     * A key was pressed. Returns whether the menu used it; for Escape, a screen closes only if this returns false
     * (Escape first closes popups, cancels keybind capture or unfocuses a text field).
     */
    boolean keyPressed(int key);

    /** A character was typed (any Unicode code point). */
    void charTyped(int codePoint);

    /**
     * The platform's paste shortcut (Ctrl+V, Cmd+V on macOS) was pressed. Returns whether the menu used it; if not,
     * the screen forwards the press to {@link #keyPressed} as usual.
     */
    default boolean paste() {
        return false;
    }

    /**
     * Escape found nothing left to dismiss: the menu starts fading out, ignoring input meanwhile. The screen closes
     * itself once {@link #isFadedOut()} says so (checked after each {@link #render}).
     */
    default void requestClose() {
    }

    /** Whether a requested close has finished fading, so the screen can go. */
    default boolean isFadedOut() {
        return true;
    }

    /**
     * Whether a text field (or list search) has keyboard focus. Platforms that only deliver typed characters while
     * OS text input is on (26.x, SDL) start it while this is true.
     */
    default boolean isTyping() {
        return false;
    }

    /** The screen was closed. */
    void closed();
}
