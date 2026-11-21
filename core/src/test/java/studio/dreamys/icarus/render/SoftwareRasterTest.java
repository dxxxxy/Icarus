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

    /** The quad fast path covers the same pixels with the same colours as drawing the quad's two triangles. */
    @Test
    void quadFastPathMatchesTriangles() {
        java.util.Random random = new java.util.Random(7);
        int size = 16;
        int[] texels = new int[size * size];
        for (int i = 0; i < texels.length; i++) texels[i] = random.nextInt();
        texels[0] = 0xFFFFFFFF;
        studio.dreamys.icarus.platform.Texture texture = new FakePlatform.RecordingRenderer().createTexture(size, size);
        float[] edges = {0, 0.5f, 1, 2.25f, 3.5f, 7, 10.75f};
        SoftwareRaster fast = new SoftwareRaster(), slow = new SoftwareRaster();
        slow.quads = false;
        DrawList list = new DrawList();
        for (int n = 0; n < 600; n++) {
            float x0 = edges[random.nextInt(edges.length)] + random.nextInt(30), y0 = edges[random.nextInt(edges.length)] + random.nextInt(30);
            float x1 = x0 + edges[random.nextInt(edges.length)] + random.nextInt(20), y1 = y0 + edges[random.nextInt(edges.length)] + random.nextInt(20);
            int color = random.nextInt(), other = random.nextInt();
            if (n % 5 == 4) color |= 0xFF000000; //opaque, filled directly
            //one colour, a vertical gradient or a horizontal one (the cases the fast path takes)
            int tl = color, tr = n % 3 == 2 ? other : color, bl = n % 3 == 1 ? other : color, br = n % 3 == 0 ? color : other;
            list.reset(64, 64);
            list.setTexture(texture);
            //texel coordinates a little off the texel grid: exact ties may round either way, on the GPU too
            float u0 = (random.nextInt(size - 4) + offset(random)) / size, v0 = (random.nextInt(size - 4) + offset(random)) / size;
            float u1 = u0 + (1 + random.nextInt(3) - offset(random)) / size, v1 = v0 + (1 + random.nextInt(3) - offset(random)) / size;
            if (random.nextBoolean()) { //mirrored
                float t = u0;
                u0 = u1;
                u1 = t;
            }
            if (n % 2 == 0) u0 = v0 = u1 = v1 = 0.1f / size; //solid
            list.quad(x0, y0, x1, y1, u0, v0, u1, v1, tl, tr, br, bl);
            fast.begin(3, 2, 60, 58, 0x80102030);
            slow.begin(3, 2, 60, 58, 0x80102030);
            fast.drawCommand(list, 0, texels, size, size);
            slow.drawCommand(list, 0, texels, size, size);
            for (int i = 0; i < 60 * 58; i++) {
                int a = fast.getPixels()[i], b = slow.getPixels()[i];
                for (int shift = 0; shift < 32; shift += 8) {
                    int diff = Math.abs((a >>> shift & 255) - (b >>> shift & 255));
                    assertTrue(diff <= 1, "quad " + x0 + "," + y0 + " - " + x1 + "," + y1 + " uv " + u0 * size + "," + v0 * size + " - " + u1 * size + "," + v1 * size
                            + " colours " + Integer.toHexString(tl) + " " + Integer.toHexString(tr) + " " + Integer.toHexString(br) + " " + Integer.toHexString(bl)
                            + ": pixel " + (3 + i % 60) + "," + (2 + i / 60) + " " + Integer.toHexString(a) + " vs " + Integer.toHexString(b));
                }
            }
        }
    }

    /** A random fraction of a texel, so sample points practically never land exactly on a texel edge. */
    private static float offset(java.util.Random random) {
        return 0.005f + random.nextFloat() * 0.02f;
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
