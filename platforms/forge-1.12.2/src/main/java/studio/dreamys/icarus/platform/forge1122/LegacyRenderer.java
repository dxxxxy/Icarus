package studio.dreamys.icarus.platform.forge1122;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import studio.dreamys.icarus.platform.Renderer;
import studio.dreamys.icarus.platform.Texture;

/** Fixed-function GL through 1.8.9's GlStateManager/Tessellator, the same calls Icarus v2 made directly. */
public class LegacyRenderer implements Renderer {
    @Override
    public void fill(double x1, double y1, double x2, double y2, int color) {
        //Gui#drawRect with exact coordinates
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldrenderer = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        color(color);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(x1, y2, 0.0D).endVertex();
        worldrenderer.pos(x2, y2, 0.0D).endVertex();
        worldrenderer.pos(x2, y1, 0.0D).endVertex();
        worldrenderer.pos(x1, y1, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    @Override
    public void fillGradient(double x, double y, double width, double height, int topColor, int bottomColor) {
        float f = (float) (topColor >> 24 & 255) / 255.0F;
        float f1 = (float) (topColor >> 16 & 255) / 255.0F;
        float f2 = (float) (topColor >> 8 & 255) / 255.0F;
        float f3 = (float) (topColor & 255) / 255.0F;
        float f4 = (float) (bottomColor >> 24 & 255) / 255.0F;
        float f5 = (float) (bottomColor >> 16 & 255) / 255.0F;
        float f6 = (float) (bottomColor >> 8 & 255) / 255.0F;
        float f7 = (float) (bottomColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldrenderer = tessellator.getBuffer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(x + width, y, 0).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(x, y, 0).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(x, y + height, 0).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos(x + width, y + height, 0).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    @Override
    public Texture createTexture(int width, int height, int[] argb, boolean linear) {
        DynamicTexture texture = new DynamicTexture(width, height);
        System.arraycopy(argb, 0, texture.getTextureData(), 0, width * height);
        texture.updateDynamicTexture();

        if (linear) {
            GlStateManager.bindTexture(texture.getGlTextureId());
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        }

        return new LegacyTexture(texture, width, height);
    }

    @Override
    public void drawTexture(Texture texture, double x, double y, double width, double height, float u0, float v0, float u1, float v1, int tint) {
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture(((LegacyTexture) texture).texture.getGlTextureId());
        color(tint);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldrenderer = tessellator.getBuffer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, 0.0D).tex(u0, v1).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0D).tex(u1, v1).endVertex();
        worldrenderer.pos(x + width, y, 0.0D).tex(u1, v0).endVertex();
        worldrenderer.pos(x, y, 0.0D).tex(u0, v0).endVertex();
        tessellator.draw();
    }

    @Override
    public void pushMatrix() {
        GlStateManager.pushMatrix();
    }

    @Override
    public void scale(float x, float y) {
        GlStateManager.scale(x, y, 1);
    }

    @Override
    public void popMatrix() {
        GlStateManager.popMatrix();
    }

    private static void color(int argb) {
        GlStateManager.color((float) (argb >> 16 & 255) / 255.0F, (float) (argb >> 8 & 255) / 255.0F, (float) (argb & 255) / 255.0F, (float) (argb >> 24 & 255) / 255.0F);
    }

    private static final class LegacyTexture implements Texture {
        private final DynamicTexture texture;
        private final int width, height;

        LegacyTexture(DynamicTexture texture, int width, int height) {
            this.texture = texture;
            this.width = width;
            this.height = height;
        }

        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public int getHeight() {
            return height;
        }
    }
}
