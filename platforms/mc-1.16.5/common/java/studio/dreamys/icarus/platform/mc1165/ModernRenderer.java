package studio.dreamys.icarus.platform.mc1165;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;
import studio.dreamys.icarus.platform.Renderer;
import studio.dreamys.icarus.platform.Texture;

/**
 * 1.16.5: fixed-function quads through Tesselator (no shaders yet), transformed by the current {@link PoseStack}.
 * GL state handling mirrors vanilla's GuiComponent#fill / #fillGradient / #blit.
 */
public class ModernRenderer implements Renderer {
    private static int textureCounter;

    private PoseStack poseStack;

    /** Binds the renderer to the frame's PoseStack; must be called before Icarus draws anything. */
    public void begin(PoseStack poseStack) {
        this.poseStack = poseStack;
    }

    private Matrix4f pose() {
        return poseStack.last().pose();
    }

    @Override
    public void fill(double x1, double y1, double x2, double y2, int color) {
        Matrix4f m = pose();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_COLOR);
        color(buffer.vertex(m, (float) x1, (float) y2, 0), color).endVertex();
        color(buffer.vertex(m, (float) x2, (float) y2, 0), color).endVertex();
        color(buffer.vertex(m, (float) x2, (float) y1, 0), color).endVertex();
        color(buffer.vertex(m, (float) x1, (float) y1, 0), color).endVertex();
        buffer.end();
        BufferUploader.end(buffer);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    @Override
    public void fillGradient(double x, double y, double width, double height, int topColor, int bottomColor) {
        Matrix4f m = pose();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_COLOR);
        color(buffer.vertex(m, (float) (x + width), (float) y, 0), topColor).endVertex();
        color(buffer.vertex(m, (float) x, (float) y, 0), topColor).endVertex();
        color(buffer.vertex(m, (float) x, (float) (y + height), 0), bottomColor).endVertex();
        color(buffer.vertex(m, (float) (x + width), (float) (y + height), 0), bottomColor).endVertex();
        buffer.end();
        BufferUploader.end(buffer);
        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    @Override
    public Texture createTexture(int width, int height, int[] argb, boolean linear) {
        NativeImage image = new NativeImage(NativeImage.Format.RGBA, width, height, false);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int c = argb[y * width + x];
                //NativeImage pixels are ABGR
                image.setPixelRGBA(x, y, (c & 0xFF00FF00) | (c & 0xFF) << 16 | (c >> 16 & 0xFF));
            }
        }

        DynamicTexture texture = new DynamicTexture(image);
        ResourceLocation id = new ResourceLocation("icarus", "dynamic/" + textureCounter++);
        Minecraft.getInstance().getTextureManager().register(id, texture);

        if (linear) {
            texture.bind();
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        }

        return new ModernTexture(id, width, height);
    }

    @Override
    public void drawTexture(Texture texture, double x, double y, double width, double height, float u0, float v0, float u1, float v1, int tint) {
        Matrix4f m = pose();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableTexture();
        Minecraft.getInstance().getTextureManager().bind(((ModernTexture) texture).id);
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        color(buffer.vertex(m, (float) x, (float) (y + height), 0), tint).uv(u0, v1).endVertex();
        color(buffer.vertex(m, (float) (x + width), (float) (y + height), 0), tint).uv(u1, v1).endVertex();
        color(buffer.vertex(m, (float) (x + width), (float) y, 0), tint).uv(u1, v0).endVertex();
        color(buffer.vertex(m, (float) x, (float) y, 0), tint).uv(u0, v0).endVertex();
        buffer.end();
        BufferUploader.end(buffer);
    }

    private static VertexConsumer color(VertexConsumer vertex, int argb) {
        return vertex.color(argb >> 16 & 0xFF, argb >> 8 & 0xFF, argb & 0xFF, argb >>> 24);
    }

    @Override
    public void pushMatrix() {
        poseStack.pushPose();
    }

    @Override
    public void scale(float x, float y) {
        poseStack.scale(x, y, 1);
    }

    @Override
    public void popMatrix() {
        poseStack.popPose();
    }

    private static final class ModernTexture implements Texture {
        private final ResourceLocation id;
        private final int width, height;

        ModernTexture(ResourceLocation id, int width, int height) {
            this.id = id;
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
