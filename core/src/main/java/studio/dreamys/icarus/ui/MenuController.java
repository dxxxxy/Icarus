package studio.dreamys.icarus.ui;

/**
 * What a platform's menu screen forwards to Icarus. Coordinates are Minecraft GUI units; key codes are
 * {@link studio.dreamys.icarus.input.Keys} codes.
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

    /** The screen was closed. */
    void closed();
}
