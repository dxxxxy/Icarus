package studio.dreamys.icarus.util;

import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.platform.Renderer;
import studio.dreamys.icarus.platform.Texture;
import studio.dreamys.icarus.util.font.GlyphPageFontRenderer;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;

@SuppressWarnings({"unused", "DuplicatedCode"})
public class RenderUtils {
    public static GlyphPageFontRenderer textRenderer;
    public static GlyphPageFontRenderer titleRenderer;
    public static GlyphPageFontRenderer iconRenderer;

    public static void loadFonts() {
        //register font in jvm
        try {
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(RenderUtils.class.getResourceAsStream("/assets/icarus/undefeated.ttf"))));
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        //initialize font renderer
        textRenderer = GlyphPageFontRenderer.create("Verdana", 11, false, false, false);
        titleRenderer = GlyphPageFontRenderer.create("Verdana", 20, true, false, false);
        iconRenderer = GlyphPageFontRenderer.create("undefeated", 50, false, false, false);
    }

    private static Renderer renderer() {
        return Icarus.getPlatform().getRenderer();
    }

    /**
     * Vertical gradient (top: startColor, bottom: endColor), like Minecraft's Gui#drawGradientRect but with exact coordinates.
     * */
    public static void drawGradientRect(double x, double y, double width, double height, Color startColor, Color endColor) {
        renderer().fillGradient(x, y, width, height, startColor.getRGB(), endColor.getRGB());
    }

    public static void drawTitle(String text, double x, double y, Color color) {
        titleRenderer.drawString(text, (float) x, (float) y, color.getRGB(), false);
    }

    public static void drawTitle(String text, double x, double y, Color color, boolean shadow) {
        titleRenderer.drawString(text, (float) x, (float) y, color.getRGB(), shadow);
    }

    public static void drawIcon(char character, double x, double y, Color color) {
        iconRenderer.drawString(String.valueOf(character), (float) x, (float) y, color.getRGB(), false);
    }

    public static void drawIcon(char character, double x, double y, Color color, boolean shadow) {
        iconRenderer.drawString(String.valueOf(character), (float) x, (float) y, color.getRGB(), shadow);
    }

    public static void drawString(String text, double x, double y, Color color) {
        textRenderer.drawString(text, (float) x, (float) y, color.getRGB(), false);
    }

    public static void drawString(String text, double x, double y, Color color, boolean shadow) {
        textRenderer.drawString(text, (float) x, (float) y, color.getRGB(), shadow);
    }

    public static void drawString(String text, double x, double y, float scale, Color color) {
        renderer().pushMatrix();
        renderer().scale(scale, scale);
        textRenderer.drawString(text, (float) (x / scale), (float) (y / scale), color.getRGB(), false);
        renderer().popMatrix();
    }

    public static void drawString(String text, double x, double y, float scale, Color color, boolean shadow) {
        renderer().pushMatrix();
        renderer().scale(scale, scale);
        textRenderer.drawString(text, (float) (x / scale), (float) (y / scale), color.getRGB(), shadow);
        renderer().popMatrix();
    }

    public static void drawXCenterString(String text, double x, double y, Color color) {
        textRenderer.drawString(text, (float) (x - getStringWidth(text) / 2), (float) (y), color.getRGB(), false);
    }

    public static void drawXYCenterString(String text, double x, double y, Color color) {
        textRenderer.drawString(text, (float) (x - getStringWidth(text) / 2), (float) (y - RenderUtils.getFontHeight() / 2), color.getRGB(), false);
    }

    public static void drawXCenterString(String text, double x, double y, Color color, boolean shadow) {
        textRenderer.drawString(text, (float) (x - getStringWidth(text) / 2), (float) (y), color.getRGB(), shadow);
    }

    public static void drawYCenterString(String text, double x, double y, Color color) {
        textRenderer.drawString(text, (float) (x), (float) (y - RenderUtils.getFontHeight() / 2), color.getRGB(), false);
    }

    public static void drawXCenterString(String text, double x, double y, float scale, Color color) {
        renderer().pushMatrix();
        renderer().scale(scale, scale);
        textRenderer.drawString(text, (float) ((x  / scale) - getStringWidth(text, scale) / 2), (float) (y / scale), color.getRGB(), false);
        renderer().popMatrix();
    }

    public static void drawXCenterString(String text, double x, double y, float scale, Color color, boolean shadow) {
        renderer().pushMatrix();
        renderer().scale(scale, scale);
        textRenderer.drawString(text, (float) ((x  / scale) - getStringWidth(text, scale) / 2), (float) (y / scale), color.getRGB(), shadow);
        renderer().popMatrix();
    }

    public static float getStringWidth(String text) {
        return textRenderer.getStringWidth(text);
    }

    public static float getStringWidth(String text, float scale) {
        return textRenderer.getStringWidth(text) * scale;
    }

    public static float getFontHeight() {
        return textRenderer.getFontHeight();
    }

    public static void drawGroupWithString(double x, double y, double width, double height, String label) {
        renderer().pushMatrix();
        renderer().scale(0.5f, 0.5f);

        x /= 0.5f;
        y /= 0.5f;
        width /= 0.5f;
        height /= 0.5f;
        double offset = 2 / 0.5f;

        drawRect(x, y, 1, height, Color.DARK_GRAY); //LEFT
        drawRect(x, y + height, width, 1, Color.DARK_GRAY); //BOTTOM
        drawRect(x + width, y, 1, height + 1, Color.DARK_GRAY); //RIGHT

        drawRect(x, y, 30, 1, Color.DARK_GRAY); //TOP LEFT
        drawRect(x + 30 + getStringWidth(label) * 2 + 4, y, width - 30 - getStringWidth(label) * 2 - 4, 1, Color.DARK_GRAY); //TOP RIGHT

        renderer().popMatrix();

        x *= 0.5f;
        y *= 0.5f;

        //label
        RenderUtils.drawString(label, x + 15, y - 3.5, Color.WHITE);
    }

    public static void drawOutline(double x, double y, double width, double height, Color color) {
        drawRect(x, y, 1, height, color); //LEFT
        drawRect(x, y + height, width, 1, color); //BOTTOM
        drawRect(x + width, y, 1, height + 1, color); //RIGHT
        drawRect(x, y, width, 1, color); //TOP
    }

    public static void drawOutline(double width, double height, double x, double y, float scale, Color color) {
        renderer().pushMatrix();
        renderer().scale(scale, scale);

        x /= scale;
        y /= scale;
        width /= scale;
        height /= scale;

        drawRect(x, y, 1, height, color); //LEFT
        drawRect(x, y + height, width, 1, color); //BOTTOM
        drawRect(x + width, y, 1, height + 1, color); //RIGHT
        drawRect(x, y, width, 1, color); //TOP

        renderer().popMatrix();
    }

    /** Same semantics as Minecraft's Gui#drawRect: coordinates are truncated to ints and the corners are sorted. */
    public static void drawRect(double x, double y, double width, double height, Color color) {
        int left = (int) x, top = (int) y, right = (int) (x + width), bottom = (int) (y + height);
        if (left < right) {
            int i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            int j = top;
            top = bottom;
            bottom = j;
        }
        renderer().fill(left, top, right, bottom, color.getRGB());
    }

    /** Draws the whole texture stretched over the given area (coordinates truncated to ints, like Gui#drawModalRectWithCustomSizedTexture). */
    public static void drawTexture(Texture texture, double x, double y, double width, double height) {
        renderer().drawTexture(texture, (int) x, (int) y, (int) width, (int) height, 0, 0, 1, 1, 0xFFFFFFFF);
    }
}
