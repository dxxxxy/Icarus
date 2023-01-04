package studio.dreamys.icarus.util;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import studio.dreamys.icarus.util.font.GlyphPageFontRenderer;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;

@SuppressWarnings({"unused", "DuplicatedCode", "JavadocReference"})
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

    /**
     * Modified and taken from: {@link Gui#drawGradientRect(int, int, int, int, int, int)}
     * */
    public static void drawGradientRect(double x, double y, double width, double height, Color startColor, Color endColor) {
        double zLevel = 0;
        float f = (float)(startColor.getRGB() >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor.getRGB() >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor.getRGB() >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor.getRGB() & 255) / 255.0F;
        float f4 = (float)(endColor.getRGB() >> 24 & 255) / 255.0F;
        float f5 = (float)(endColor.getRGB() >> 16 & 255) / 255.0F;
        float f6 = (float)(endColor.getRGB() >> 8 & 255) / 255.0F;
        float f7 = (float)(endColor.getRGB() & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(x + width, y, zLevel).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(x, y, zLevel).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(x, y + height, zLevel).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos(x + width, y + height, zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
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
        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, 1);
        textRenderer.drawString(text, (float) (x / scale), (float) (y / scale), color.getRGB(), false);
        GL11.glPopMatrix();
    }

    public static void drawString(String text, double x, double y, float scale, Color color, boolean shadow) {
        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, 1);
        textRenderer.drawString(text, (float) (x / scale), (float) (y / scale), color.getRGB(), shadow);
        GL11.glPopMatrix();
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
        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, 1);
        textRenderer.drawString(text, (float) ((x  / scale) - getStringWidth(text, scale) / 2), (float) (y / scale), color.getRGB(), false);
        GL11.glPopMatrix();
    }

    public static void drawXCenterString(String text, double x, double y, float scale, Color color, boolean shadow) {
        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, 1);
        textRenderer.drawString(text, (float) ((x  / scale) - getStringWidth(text, scale) / 2), (float) (y / scale), color.getRGB(), shadow);
        GL11.glPopMatrix();
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
        GL11.glPushMatrix();
        GL11.glScalef(0.5f, 0.5f, 1f);

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

        GL11.glPopMatrix();

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
        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, 1f);

        x /= scale;
        y /= scale;
        width /= scale;
        height /= scale;

        drawRect(x, y, 1, height, color); //LEFT
        drawRect(x, y + height, width, 1, color); //BOTTOM
        drawRect(x + width, y, 1, height + 1, color); //RIGHT
        drawRect(x, y, width, 1, color); //TOP

        GL11.glPopMatrix();
    }

    public static void drawRect(double x, double y, double width, double height, Color color) {
        Gui.drawRect((int) x, (int) y, (int) (x + width), (int) (y + height), color.getRGB());
    }

    public static void drawTexture(double x, double y, double width, double height) {
        Gui.drawModalRectWithCustomSizedTexture((int) x, (int) y, 0, 0, (int) width, (int) height, (int) width, (int) height);
    }
}
