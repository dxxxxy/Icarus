package studio.dreamys.minesense.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class RenderUtils {
    public static GlyphPageFontRenderer iconRenderer;
    public static GlyphPageFontRenderer fontRenderer;

    public static void loadFonts() {
        //register font in jvm
        try {
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(RenderUtils.class.getResourceAsStream("/assets/minesense/undefeated.ttf"))));
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        //initialize font renderer
        iconRenderer = GlyphPageFontRenderer.create("undefeated", 50, false, false, false);
        fontRenderer = GlyphPageFontRenderer.create("Verdana", 11, false, false, false);
    }

    /**
     * Modified and taken from: {@link Gui#drawGradientRect(int, int, int, int, int, int)}
     * */
    public static void drawGradientRect(double left, double top, double right, double bottom, Color startColor, Color endColor) {
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
        worldrenderer.pos(right, top, zLevel).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(left, top, zLevel).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(left, bottom, zLevel).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos(right, bottom, zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawIcon(char character, double x, double y, Color color) {
        iconRenderer.drawString(String.valueOf(character), (float) x, (float) y, color.getRGB(), false);
    }

    public static void drawIcon(char character, double x, double y, Color color, boolean shadow) {
        iconRenderer.drawString(String.valueOf(character), (float) x, (float) y, color.getRGB(), shadow);
    }

    public static void drawString(String text, double x, double y, Color color) {
        fontRenderer.drawString(text, (float) x, (float) y, color.getRGB(), false);
    }

    public static void drawString(String text, double x, double y, Color color, boolean shadow) {
        fontRenderer.drawString(text, (float) x, (float) y, color.getRGB(), shadow);
    }

    public static void drawString(String text, double x, double y, float scale, Color color) {
        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, 1);
        fontRenderer.drawString(text, (float) (x / scale), (float) (y / scale), color.getRGB(), false);
        GL11.glPopMatrix();
    }

    public static void drawString(String text, double x, double y, float scale, Color color, boolean shadow) {
        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, 1);
        fontRenderer.drawString(text, (float) (x / scale), (float) (y / scale), color.getRGB(), shadow);
        GL11.glPopMatrix();
    }

    public static void drawCenteredString(String text, double x, double y, Color color) {
       fontRenderer.drawString(text, (float) (x - getStringWidth(text) / 2), (float) (y), color.getRGB(), false);
    }

    public static void drawCenteredString(String text, double x, double y, Color color, boolean shadow) {
        fontRenderer.drawString(text, (float) (x - getStringWidth(text) / 2), (float) (y), color.getRGB(), shadow);
    }

    public static void drawCenteredString(String text, double x, double y, float scale, Color color) {
        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, 1);
        fontRenderer.drawString(text, (float) ((x  / scale) - getStringWidth(text, scale) / 2), (float) (y / scale), color.getRGB(), false);
        GL11.glPopMatrix();
    }

    public static void drawCenteredString(String text, double x, double y, float scale, Color color, boolean shadow) {
        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, 1);
        fontRenderer.drawString(text, (float) ((x  / scale) - getStringWidth(text, scale) / 2), (float) (y / scale), color.getRGB(), shadow);
        GL11.glPopMatrix();
    }

    public static float getStringWidth(String text) {
        return fontRenderer.getStringWidth(text);
    }

    public static float getStringWidth(String text, float scale) {
        return fontRenderer.getStringWidth(text) * scale;
    }

    public static void drawGroupWithString(double width, double height, double x, double y, String label) {
        GL11.glPushMatrix();
        GL11.glScalef(0.5f, 0.5f, 1f);

        x /= 0.5f;
        y /= 0.5f;
        width /= 0.5f;
        height /= 0.5f;
        double offset = 2 / 0.5f;

        drawRect(x, y, x + 1, y + height, Color.DARK_GRAY); //LEFT
        drawRect(x, y + height, x + width, y + height + 1, Color.DARK_GRAY); //BOTTOM
        drawRect(x + width, y, x + width + 1, y + height + 1, Color.DARK_GRAY); //RIGHT

        drawRect(x, y, x + width / 10, y + 1, Color.DARK_GRAY); //TOP LEFT
        drawRect(x + width / 10 + getStringWidth(label, 2) + offset, y, x + width, y + 1, Color.DARK_GRAY); //TOP RIGHT

        GL11.glPopMatrix();

        x *= 0.5f;
        y *= 0.5f;
        width *= 0.5f;

        //label
        RenderUtils.drawString(label, x + width / 10, y - 3.5,  Color.WHITE);
    }

    public static void drawOutline(double width, double height, double x, double y, Color color) {
        drawRect(x, y, x + 1, y + height, color); //LEFT
        drawRect(x, y + height, x + width, y + height + 1, color); //BOTTOM
        drawRect(x + width, y, x + width + 1, y + height + 1, color); //RIGHT
        drawRect(x, y, x + width, y + 1, color); //TOP
    }

    public static void drawOutline(double width, double height, double x, double y, float scale, Color color) {
        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, 1f);

        x /= scale;
        y /= scale;
        width /= scale;
        height /= scale;

        drawRect(x, y, x + 1, y + height, color); //LEFT
        drawRect(x, y + height, x + width, y + height + 1, color); //BOTTOM
        drawRect(x + width, y, x + width + 1, y + height + 1, color); //RIGHT
        drawRect(x, y, x + width, y + 1, color); //TOP

        GL11.glPopMatrix();
    }

    public static void drawRect(double x, double y, double width, double height, Color color) {
        Gui.drawRect((int) x, (int) y, (int) width, (int) height, color.getRGB());
    }

    public static void drawTexture(double x, double y, double width, double height) {
        Gui.drawModalRectWithCustomSizedTexture((int) x, (int) y, 0, 0, (int) width, (int) height, (int) width, (int) height);
    }
}
