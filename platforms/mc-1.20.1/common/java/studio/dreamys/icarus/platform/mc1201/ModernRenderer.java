package studio.dreamys.icarus.platform.mc1201;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.system.MemoryUtil;
import studio.dreamys.icarus.platform.Renderer;
import studio.dreamys.icarus.platform.Texture;
import studio.dreamys.icarus.render.DrawList;

import java.nio.IntBuffer;

/** 1.20.1: draw lists as triangles through the position_tex_color shader, in the frame's GuiGraphics pass. */
public class ModernRenderer implements Renderer {
    private GuiGraphics graphics;
    private IntBuffer pixels = MemoryUtil.memAllocInt(64 * 64);

    /** Binds the renderer to the frame's GuiGraphics; must be called before Icarus draws anything. */
    public void begin(GuiGraphics graphics) {
        this.graphics = graphics;
    }

    @Override
    public Texture createTexture(int width, int height) {
        int id = TextureUtil.generateTextureId();
        GlStateManager._bindTexture(id);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, (IntBuffer) null);
        return new GlTexture(id, width, height);
    }

    @Override
    public void upload(Texture texture, int x, int y, int width, int height, int[] argb, int offset, int stride) {
        int count = width * height;
        if (pixels.capacity() < count) {
            MemoryUtil.memFree(pixels);
            pixels = MemoryUtil.memAllocInt(count);
        }
        pixels.clear();
        for (int row = 0; row < height; row++) pixels.put(argb, offset + row * stride, width);
        pixels.flip();
        GlStateManager._bindTexture(((GlTexture) texture).id);
        GlStateManager._pixelStore(GL11.GL_UNPACK_ROW_LENGTH, 0);
        GlStateManager._pixelStore(GL11.GL_UNPACK_SKIP_PIXELS, 0);
        GlStateManager._pixelStore(GL11.GL_UNPACK_SKIP_ROWS, 0);
        GlStateManager._pixelStore(GL11.GL_UNPACK_ALIGNMENT, 4);
        GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, x, y, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixels);
    }

    @Override
    public void deleteTexture(Texture texture) {
        TextureUtil.releaseTextureId(((GlTexture) texture).id);
    }

    @Override
    public void render(DrawList list) {
        graphics.flush(); //draw whatever vanilla batched so far underneath us
        float scale = (float) Minecraft.getInstance().getWindow().getGuiScale();
        graphics.pose().pushPose();
        graphics.pose().scale(1 / scale, 1 / scale, 1);
        Matrix4f pose = graphics.pose().last().pose();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        float[] xy = list.getPositions(), uv = list.getUvs();
        int[] colors = list.getColors(), indices = list.getIndices();
        for (int command = 0; command < list.getCommandCount(); command++) {
            int start = list.getCommandIndexStart(command), count = list.getCommandIndexCount(command);
            if (count == 0) continue;
            RenderSystem.setShaderTexture(0, ((GlTexture) list.getCommandTexture(command)).id);
            BufferBuilder buffer = Tesselator.getInstance().getBuilder();
            buffer.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_TEX_COLOR);
            for (int i = start; i < start + count; i++) {
                int v = indices[i], c = colors[v];
                buffer.vertex(pose, xy[v * 2], xy[v * 2 + 1], 0).uv(uv[v * 2], uv[v * 2 + 1]).color(c >> 16 & 255, c >> 8 & 255, c & 255, c >>> 24).endVertex();
            }
            BufferUploader.drawWithShader(buffer.end());
        }

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        graphics.pose().popPose();
    }

    private static final class GlTexture implements Texture {
        final int id, width, height;

        GlTexture(int id, int width, int height) {
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
