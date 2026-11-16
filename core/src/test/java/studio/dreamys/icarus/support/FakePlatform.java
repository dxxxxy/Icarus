package studio.dreamys.icarus.support;

import studio.dreamys.icarus.platform.Platform;
import studio.dreamys.icarus.platform.Renderer;
import studio.dreamys.icarus.platform.Texture;
import studio.dreamys.icarus.render.DrawList;
import studio.dreamys.icarus.ui.MenuController;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** A headless platform that records what Icarus draws. */
public final class FakePlatform implements Platform {
    public final RecordingRenderer renderer = new RecordingRenderer();
    public final File gameDir;
    public double guiScale = 2;
    public int width = 1920, height = 1080;
    public MenuController opened;
    public String clipboard = "";
    /** What {@link #getModRoots} reports for every mod id. */
    public final List<Path> modRoots = new ArrayList<>();

    public FakePlatform(File gameDir) {
        this.gameDir = gameDir;
    }

    @Override
    public void install() {
    }

    @Override
    public Renderer getRenderer() {
        return renderer;
    }

    @Override
    public double getGuiScale() {
        return guiScale;
    }

    @Override
    public int getFramebufferWidth() {
        return width;
    }

    @Override
    public int getFramebufferHeight() {
        return height;
    }

    @Override
    public boolean isInGame() {
        return true;
    }

    @Override
    public void openScreen(MenuController menu) {
        opened = menu;
    }

    @Override
    public File getGameDir() {
        return gameDir;
    }

    @Override
    public Collection<Path> getModRoots(String modid) {
        return modRoots;
    }

    @Override
    public ClassLoader getModClassLoader(String modid) {
        return FakePlatform.class.getClassLoader();
    }

    @Override
    public String getClipboard() {
        return clipboard;
    }

    @Override
    public void setClipboard(String text) {
        clipboard = text;
    }

    /** Records draw lists (as quads) and texture uploads. */
    public static final class RecordingRenderer implements Renderer {
        public int renders;
        public int lastCommandCount;
        public int lastIndexCount;
        /** Quads of the last render: x0, y0, x1, y1 per quad (from each pair of triangles). */
        public final List<float[]> lastQuads = new ArrayList<>();
        public boolean recordQuads = true;
        public int uploads;

        @Override
        public Texture createTexture(int width, int height) {
            return new Texture() {
                @Override
                public int getWidth() {
                    return width;
                }

                @Override
                public int getHeight() {
                    return height;
                }
            };
        }

        @Override
        public void upload(Texture texture, int x, int y, int width, int height, int[] argb, int offset, int stride) {
            uploads++;
        }

        @Override
        public void deleteTexture(Texture texture) {
        }

        @Override
        public void render(DrawList list) {
            renders++;
            lastCommandCount = list.getCommandCount();
            lastIndexCount = 0;
            for (int i = 0; i < list.getCommandCount(); i++) lastIndexCount += list.getCommandIndexCount(i);
            if (!recordQuads) return;
            lastQuads.clear();
            float[] xy = list.getPositions();
            int[] indices = list.getIndices();
            for (int i = 0; i + 5 < lastIndexCount; i += 6) {
                int a = indices[i], c = indices[i + 2];
                lastQuads.add(new float[]{xy[a * 2], xy[a * 2 + 1], xy[c * 2], xy[c * 2 + 1]});
            }
        }
    }
}
