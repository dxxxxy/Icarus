package studio.dreamys.icarus.extra;

/** Something drawn on the in-game HUD. Register with {@link studio.dreamys.icarus.Icarus#registerHud}. */
public interface HudElement {
    void renderHud();

    default void clientTick() {
    }
}
