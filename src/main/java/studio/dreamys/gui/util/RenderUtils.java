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
}
