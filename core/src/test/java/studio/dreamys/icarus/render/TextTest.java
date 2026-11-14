package studio.dreamys.icarus.render;

import org.junit.jupiter.api.Test;
import studio.dreamys.icarus.support.FakePlatform;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextTest {
    private final FontAtlas atlas = new FontAtlas();
    private final Font font = new Font(atlas, "Dialog", java.awt.Font.PLAIN, 12);
    private final Canvas canvas = new Canvas();

    TextTest() {
        canvas.setup(atlas, 1);
    }

    @Test
    void unknownCodeTerminates() {
        assertTimeoutPreemptively(Duration.ofSeconds(3), () -> {
            canvas.measure(font, "§z");
            canvas.measure(font, "a§");
            canvas.measure(font, "§");
        });
    }

    @Test
    void codesAreNotMeasured() {
        assertEquals(canvas.measure(font, "AB"), canvas.measure(font, "§cAB"), 1e-9);
        assertEquals(canvas.measure(font, "AB"), canvas.measure(font, "§CAB"), 1e-9, "upper-case codes too");
        assertEquals(canvas.measure(font, "AB"), canvas.measure(font, "A§zB"), 1e-9, "unknown codes are skipped");
    }

    @Test
    void anyUnicodeMeasuresAndDraws() {
        String text = "café € Ж 中 😀";
        assertTrue(canvas.measure(font, text) > canvas.measure(font, "caf"));

        FakePlatform.RecordingRenderer renderer = new FakePlatform.RecordingRenderer();
        FrameRenderer frame = new FrameRenderer();
        frame.render(renderer, atlas, 1, 800, 600, () -> frame.getCanvas().text(font, text, 10, 10, 0xFFFFFFFF));
        assertEquals(1, renderer.renders);
        assertEquals(1, renderer.lastCommandCount, "all glyphs share one texture");
    }

    @Test
    void fitAddsEllipsis() {
        String fitted = canvas.fit(font, "a fairly long piece of text", 40);
        assertTrue(fitted.endsWith("…"));
        assertTrue(canvas.measure(font, fitted) <= 40);
        assertEquals("short", canvas.fit(font, "short", 1000));
    }

    @Test
    void atlasGrowsAndKeepsWhitePixel() {
        FontAtlas small = new FontAtlas(64);
        assertEquals(0xFFFFFFFF, small.pixel(1, 1));
        for (int i = 0; i < 40; i++) small.add(new int[20 * 20], 20, 20);
        assertTrue(small.getSize() > 64, "grew instead of overflowing");
        assertEquals(0xFFFFFFFF, small.pixel(1, 1));
    }

    @Test
    void scaleChangesRasterSize() {
        double at1 = font.face(1).getLineHeight();
        double at2 = font.face(2).getLineHeight();
        assertTrue(at2 > at1 * 1.5, "glyphs are rasterised at the real pixel size");
    }
}
