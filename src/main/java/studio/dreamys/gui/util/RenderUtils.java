package studio.dreamys.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class RenderUtils {
    public static final int FONT_HEIGHT = Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT;

    /**
     * Taken from: {@link Gui#drawGradientRect(int, int, int, int, int, int)}
     * */
    public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        double zLevel = 0;
        float f = (float)(startColor >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor & 255) / 255.0F;
        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
        float f5 = (float)(endColor >> 16 & 255) / 255.0F;
        float f6 = (float)(endColor >> 8 & 255) / 255.0F;
        float f7 = (float)(endColor & 255) / 255.0F;
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

    public static void drawScaledString(String text, int x, int y, float scale, Color color) {
        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, 1);
        Minecraft.getMinecraft().fontRendererObj.drawString(text, (int) (x / scale), (int) (y / scale), color.getRGB());
        GL11.glPopMatrix();
    }

    public static void drawScaledCenteredString(String text, int x, int y, float scale, Color color) {
        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, 1);
        Minecraft.getMinecraft().fontRendererObj.drawString(text, (int) (x  / scale) - Minecraft.getMinecraft().fontRendererObj.getStringWidth(text) / 2, (int) (y / scale), color.getRGB());
        GL11.glPopMatrix();
    }

    public static float getScaledStringWidth(String text, float scale) {
        return Minecraft.getMinecraft().fontRendererObj.getStringWidth(text) * scale;
    }

    public static float getScaledStringHeight(String text, float scale) {
        return Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT  * scale;
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

        drawRect(x, y, x + width / 9 - offset, y + 1, Color.DARK_GRAY); //TOP LEFT
        drawRect(x + width / 9 + offset * 5 + getScaledStringWidth(label, 0.5f), y, x + width, y + 1, Color.DARK_GRAY); //TOP RIGHT

        GL11.glPopMatrix();

        x *= 0.5f;
        y *= 0.5f;
        width *= 0.5f;

        //label
        RenderUtils.drawScaledString(label, (int) (x + width / 9), (int) y - 1, 0.5f,  Color.WHITE);
    }

    public static void drawOutline(double width, double height, double x, double y, Color color) {
        GL11.glPushMatrix();
        GL11.glScalef(0.5f, 0.5f, 1f);

        x /= 0.5f;
        y /= 0.5f;
        width /= 0.5f;
        height /= 0.5f;

        drawRect(x, y, x + 1, y + height, color); //LEFT
        drawRect(x, y + height, x + width, y + height + 1, color); //BOTTOM
        drawRect(x + width, y, x + width + 1, y + height + 1, color); //RIGHT
        drawRect(x, y, x + width, y + 1, color); //TOP

        GL11.glPopMatrix();
    }

    public static void drawRect(double width, double height, double x, double y, Color color) {
        Gui.drawRect((int) width, (int) height, (int) x, (int) y, color.getRGB());
    }
}
