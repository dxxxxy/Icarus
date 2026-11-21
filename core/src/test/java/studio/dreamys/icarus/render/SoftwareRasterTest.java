package studio.dreamys.icarus.render;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.IcarusMod;
import studio.dreamys.icarus.IcarusOptions;
import studio.dreamys.icarus.support.FakePlatform;
import studio.dreamys.icarus.support.Fixture;
import studio.dreamys.icarus.theme.Theme;
import studio.dreamys.icarus.theme.Themes;
import studio.dreamys.icarus.ui.Menu;

import java.nio.file.Path;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SoftwareRasterTest {
    @TempDir
    Path game;

    @AfterEach
    void tearDown() {
        Icarus.setPlatform(null);
    }

    /** A menu drawn into a transparent buffer and composited over a background equals the menu drawn onto it. */
    @Test
    void transparentRasterCompositesLikeDirectDrawing() {
        for (Theme theme : new Theme[]{Themes.SKEET, Themes.FATALITY}) {
            FakePlatform platform = new FakePlatform(game.toFile());
            platform.width = 1000;
            platform.height = 800;
            Icarus.setPlatform(platform);
            SoftwareRaster direct = new SoftwareRaster(), transparent = new SoftwareRaster(), cropped = new SoftwareRaster();
            int w = platform.width, h = platform.height;
            int[] bounds = new int[4];
            platform.renderer.onRender = list -> {
                direct.begin(0, 0, w, h, 0xFF000000);
                int[] bg = direct.getPixels();
                for (int i = 0; i < w * h; i++) bg[i] = background(i % w, i / w);
                direct.draw(list, Icarus.getAtlas());
                transparent.begin(0, 0, w, h, 0);
                transparent.draw(list, Icarus.getAtlas());
                SoftwareRaster.emptyBounds(bounds);
                SoftwareRaster.unionBounds(list, w, h, bounds);
                cropped.begin(bounds[0], bounds[1], bounds[2] - bounds[0], bounds[3] - bounds[1], 0);
                cropped.draw(list, Icarus.getAtlas());
            };
            IcarusMod mod = Icarus.createDetached("raster-" + theme.getId(), theme, IcarusOptions.defaults(),
                    Collections.singletonList(Fixture.Combat.class), game.resolve("config/" + theme.getId()), game.resolve("legacy"));
            Menu menu = mod.getMenu();
            menu.opened();
            menu.skipFade();
            menu.render(0, 0, 0);
            menu.render(0, 0, 0);

            int drawn = 0;
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int src = transparent.getPixels()[y * w + x];
                    if (src >>> 24 != 0) drawn++;
                    int expected = direct.getPixels()[y * w + x], actual = over(src, background(x, y));
                    for (int shift = 0; shift < 24; shift += 8) {
                        int diff = Math.abs((expected >> shift & 255) - (actual >> shift & 255));
                        assertTrue(diff <= 1, theme.getId() + " pixel " + x + "," + y + " differs by " + diff);
                    }
                }
            }
            assertTrue(drawn > 100_000, "the menu was drawn (" + drawn + " px)");

            //a raster covering just the bounds holds the same pixels
            assertTrue(bounds[2] > bounds[0] && bounds[3] > bounds[1]);
            for (int y = bounds[1]; y < bounds[3]; y++) {
                for (int x = bounds[0]; x < bounds[2]; x++) {
                    assertEquals(transparent.getPixels()[y * w + x], cropped.getPixels()[(y - bounds[1]) * cropped.getWidth() + x - bounds[0]]);
                }
            }
            //and nothing lies outside them
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (x >= bounds[0] && x < bounds[2] && y >= bounds[1] && y < bounds[3]) continue;
                    assertEquals(0, transparent.getPixels()[y * w + x] >>> 24);
                }
            }
        }
    }

    @Test
    void boundsUnionAndClamp() {
        DrawList a = new DrawList(), b = new DrawList();
        a.reset(100, 100);
        b.reset(100, 100);
        studio.dreamys.icarus.platform.Texture texture = new FakePlatform.RecordingRenderer().createTexture(4, 4);
        a.setTexture(texture);
        b.setTexture(texture);
        a.quad(10.5f, 20, 30, 40.2f, 0, 0, 0, 0, -1, -1, -1, -1);
        b.quad(-5, 50, 120, 60, 0, 0, 0, 0, -1, -1, -1, -1);
        int[] bounds = new int[4];
        SoftwareRaster.emptyBounds(bounds);
        SoftwareRaster.unionBounds(a, 100, 100, bounds);
        assertEquals("[10, 20, 30, 41]", java.util.Arrays.toString(bounds));
        SoftwareRaster.unionBounds(b, 90, 100, bounds);
        assertEquals("[0, 20, 90, 60]", java.util.Arrays.toString(bounds));
        SoftwareRaster.unionBounds(new DrawList(), 90, 100, bounds);
        assertEquals("[0, 20, 90, 60]", java.util.Arrays.toString(bounds));
    }

    private static int background(int x, int y) {
        return 0xFF000000 | (x * 7 & 255) << 16 | (y * 3 & 255) << 8 | ((x ^ y) & 255);
    }

    /** Premultiplied {@code src} over opaque {@code dst}, the way the compositor blends a layered window. */
    private static int over(int src, int dst) {
        float inv = 1 - (src >>> 24) / 255f;
        int r = Math.round((src >> 16 & 255) + (dst >> 16 & 255) * inv);
        int g = Math.round((src >> 8 & 255) + (dst >> 8 & 255) * inv);
        int b = Math.round((src & 255) + (dst & 255) * inv);
        return 0xFF000000 | r << 16 | g << 8 | b;
    }
}
