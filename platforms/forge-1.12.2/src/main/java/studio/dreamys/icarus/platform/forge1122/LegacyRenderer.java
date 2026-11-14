package studio.dreamys.icarus.platform.forge1122;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL15;
import studio.dreamys.icarus.platform.Renderer;
import studio.dreamys.icarus.platform.Texture;
import studio.dreamys.icarus.render.DrawList;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 * Draws Icarus' draw lists with client-side vertex arrays (one glDrawElements per texture), going through
 * GlStateManager for state it caches.
 */
public class LegacyRenderer implements Renderer {
    private static final int STRIDE = 20; //x, y, u, v floats + RGBA bytes

    private ByteBuffer vertices = BufferUtils.createByteBuffer(STRIDE * 4096);
    private IntBuffer indices = BufferUtils.createIntBuffer(6144);
    private IntBuffer pixels = BufferUtils.createIntBuffer(64 * 64);

    @Override
    public Texture createTexture(int width, int height) {
        int id = GL11.glGenTextures();
        GlStateManager.bindTexture(id);
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
        if (pixels.capacity() < count) pixels = BufferUtils.createIntBuffer(count);
        pixels.clear();
        for (int row = 0; row < height; row++) pixels.put(argb, offset + row * stride, width);
        pixels.flip();
        GlStateManager.bindTexture(((GlTexture) texture).id);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 4);
        GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, x, y, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixels);
    }

    @Override
    public void deleteTexture(Texture texture) {
        GlStateManager.deleteTexture(((GlTexture) texture).id);
    }

    @Override
    public void render(DrawList list) {
        int vertexCount = list.getVertexCount();
        if (vertices.capacity() < vertexCount * STRIDE) vertices = BufferUtils.createByteBuffer(vertexCount * STRIDE * 2).order(ByteOrder.nativeOrder());
        vertices.clear();
        float[] xy = list.getPositions(), uv = list.getUvs();
        int[] colors = list.getColors();
        for (int i = 0; i < vertexCount; i++) {
            int c = colors[i];
            vertices.putFloat(xy[i * 2]).putFloat(xy[i * 2 + 1]).putFloat(uv[i * 2]).putFloat(uv[i * 2 + 1])
                    .put((byte) (c >> 16)).put((byte) (c >> 8)).put((byte) c).put((byte) (c >>> 24));
        }
        int indexCount = 0;
        for (int i = 0; i < list.getCommandCount(); i++) indexCount = Math.max(indexCount, list.getCommandIndexStart(i) + list.getCommandIndexCount(i));
        if (indices.capacity() < indexCount) indices = BufferUtils.createIntBuffer(indexCount * 2);
        indices.clear();
        indices.put(list.getIndices(), 0, indexCount);

        double scale = new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
        GlStateManager.pushMatrix();
        GlStateManager.scale(1 / scale, 1 / scale, 1);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableAlpha();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.color(1, 1, 1, 1);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
        vertices.position(0);
        GL11.glVertexPointer(2, GL11.GL_FLOAT, STRIDE, vertices);
        vertices.position(8);
        GL11.glTexCoordPointer(2, GL11.GL_FLOAT, STRIDE, vertices);
        vertices.position(16);
        GL11.glColorPointer(4, true, STRIDE, vertices);

        for (int i = 0; i < list.getCommandCount(); i++) {
            int start = list.getCommandIndexStart(i), count = list.getCommandIndexCount(i);
            if (count == 0) continue;
            GlStateManager.bindTexture(((GlTexture) list.getCommandTexture(i)).id);
            indices.limit(start + count).position(start);
            GL11.glDrawElements(GL11.GL_TRIANGLES, indices);
        }

        GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
        GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.popMatrix();
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
