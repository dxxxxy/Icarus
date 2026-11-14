package studio.dreamys.icarus.hud;

import studio.dreamys.icarus.render.Canvas;

/** Something drawn on the in-game HUD by a mod (see {@code IcarusMod#addHud}). */
public interface HudElement {
    /** Draws the element. Coordinates are design units; {@code hud} gives the theme, screen size and partial tick. */
    void render(Canvas canvas, HudContext hud);

    /** Start of every client tick. */
    default void tick() {
    }
}
