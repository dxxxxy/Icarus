package studio.dreamys.icarus.render;

import studio.dreamys.icarus.platform.Renderer;
import studio.dreamys.icarus.platform.Texture;

/**
 * Runs a paint pass into a {@link DrawList} and hands it to the platform renderer. New glyphs can be added to the
 * atlas while painting; if that made the atlas grow or clear, UVs painted earlier in the pass are stale, so the
 * pass is repeated once (cheap, and only happens while new text first appears).
 */
public final class FrameRenderer {
    private final DrawList list = new DrawList();
    private final Canvas canvas = new Canvas();

    public Canvas getCanvas() {
        return canvas;
    }

    public DrawList getList() {
        return list;
    }

    public void render(Renderer renderer, FontAtlas atlas, double scale, int framebufferWidth, int framebufferHeight, Runnable paint) {
        paint(renderer, atlas, scale, framebufferWidth, framebufferHeight, paint);
        if (!list.isEmpty()) renderer.render(list);
    }

    /** Runs the paint pass (syncing the atlas) without handing the list to the renderer; returns the list. */
    public DrawList paint(Renderer renderer, FontAtlas atlas, double scale, int framebufferWidth, int framebufferHeight, Runnable paint) {
        for (int attempt = 0; attempt < 2; attempt++) {
            Texture texture = atlas.sync(renderer);
            int size = atlas.getSize(), generation = atlas.getGeneration();
            canvas.begin(list, atlas, texture, scale, framebufferWidth, framebufferHeight);
            paint.run();
            if (atlas.getSize() == size && atlas.getGeneration() == generation) break;
        }
        atlas.sync(renderer);
        return list;
    }
}
