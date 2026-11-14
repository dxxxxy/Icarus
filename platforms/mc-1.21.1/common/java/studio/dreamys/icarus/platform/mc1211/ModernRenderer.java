package studio.dreamys.icarus.platform.mc1211;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import studio.dreamys.icarus.platform.Renderer;
import studio.dreamys.icarus.platform.Texture;

/**
 * 1.21.1: immediate-mode quads through Tesselator + the position_color / position_tex_color shaders,
 * transformed by the current {@link GuiGraphics} pose.
 */
public class ModernRenderer implements Renderer {
    private static int textureCounter;

    private GuiGraphics graphics;

    /** Binds the renderer to the frame's GuiGraphics; must be called before Icarus draws anything. */
    public void begin(GuiGraphics graphics) {
        this.graphics = graphics;
        graphics.flush(); //draw whatever vanilla batched so far underneath us
    }

    private Matrix4f pose() {
        return graphics.pose().last().pose();
    }

    @Override
    public void fill(double x1, double y1, double x2, double y2, int color) {
        Matrix4f m = pose();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.addVertex(m, (float) x1, (float) y2, 0).setColor(color);
        buffer.addVertex(m, (float) x2, (float) y2, 0).setColor(color);
        buffer.addVertex(m, (float) x2, (float) y1, 0).setColor(color);
        buffer.addVertex(m, (float) x1, (float) y1, 0).setColor(color);
        BufferUploader.drawWithShader(buffer.buildOrThrow());
        RenderSystem.disableBlend();
    }

    @Override
    public void fillGradient(double x, double y, double width, double height, int topColor, int bottomColor) {
        Matrix4f m = pose();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.addVertex(m, (float) (x + width), (float) y, 0).setColor(topColor);
        buffer.addVertex(m, (float) x, (float) y, 0).setColor(topColor);
        buffer.addVertex(m, (float) x, (float) (y + height), 0).setColor(bottomColor);
        buffer.addVertex(m, (float) (x + width), (float) (y + height), 0).setColor(bottomColor);
        BufferUploader.drawWithShader(buffer.buildOrThrow());
        RenderSystem.disableBlend();
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
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("icarus", "dynamic/" + textureCounter++);
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
        RenderSystem.setShaderTexture(0, ((ModernTexture) texture).id);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.addVertex(m, (float) x, (float) (y + height), 0).setUv(u0, v1).setColor(tint);
        buffer.addVertex(m, (float) (x + width), (float) (y + height), 0).setUv(u1, v1).setColor(tint);
        buffer.addVertex(m, (float) (x + width), (float) y, 0).setUv(u1, v0).setColor(tint);
        buffer.addVertex(m, (float) x, (float) y, 0).setUv(u0, v0).setColor(tint);
        BufferUploader.drawWithShader(buffer.buildOrThrow());
        RenderSystem.disableBlend();
    }

    @Override
    public void pushMatrix() {
        graphics.pose().pushPose();
    }

    @Override
    public void scale(float x, float y) {
        graphics.pose().scale(x, y, 1);
    }

    @Override
    public void popMatrix() {
        graphics.pose().popPose();
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
