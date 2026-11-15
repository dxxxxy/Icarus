package studio.dreamys.icarus.platform.mc263;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.textures.FilterMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.joml.Matrix3x2f;
import studio.dreamys.icarus.platform.Renderer;
import studio.dreamys.icarus.platform.Texture;
import studio.dreamys.icarus.render.DrawList;

import java.lang.reflect.Field;

/**
 * 26.3: the GUI is retained-mode, so a draw list is submitted as GUI elements (one per texture) whose vertices are
 * built when Minecraft renders the frame. The frame's {@link GuiRenderState} is private in
 * {@link GuiGraphicsExtractor}; 26.x is unobfuscated, so it's read by name.
 */
public class ModernRenderer implements Renderer {
    private static final Field GUI_RENDER_STATE;

    static {
        try {
            GUI_RENDER_STATE = GuiGraphicsExtractor.class.getDeclaredField("guiRenderState");
            GUI_RENDER_STATE.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("GuiGraphicsExtractor.guiRenderState not found", e);
        }
    }

    private GuiGraphicsExtractor graphics;

    /** Binds the renderer to the frame's extractor; must be called before Icarus draws anything. */
    public void begin(GuiGraphicsExtractor graphics) {
        this.graphics = graphics;
    }

    @Override
    public Texture createTexture(int width, int height) {
        NativeImage image = new NativeImage(NativeImage.Format.RGBA, width, height, false);
        DynamicTexture texture = new DynamicTexture(() -> "icarus atlas", image);
        return new AtlasTexture(texture, image, width, height);
    }

    @Override
    public void upload(Texture texture, int x, int y, int width, int height, int[] argb, int offset, int stride) {
        AtlasTexture t = (AtlasTexture) texture;
        for (int row = 0; row < height; row++) {
            int base = offset + row * stride;
            for (int column = 0; column < width; column++) t.image.setPixel(x + column, y + row, argb[base + column]); //ARGB
        }
        t.texture.upload();
    }

    @Override
    public void deleteTexture(Texture texture) {
        ((AtlasTexture) texture).texture.close();
    }

    @Override
    public void render(DrawList list) {
        GuiRenderState state;
        try {
            state = (GuiRenderState) GUI_RENDER_STATE.get(graphics);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        Minecraft mc = Minecraft.getInstance();
        float inverseScale = 1f / mc.getWindow().getGuiScale();
        ScreenRectangle bounds = new ScreenRectangle(0, 0, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
        Matrix3x2f pose = new Matrix3x2f(graphics.pose());

        for (int command = 0; command < list.getCommandCount(); command++) {
            if (list.getCommandIndexCount(command) == 0) continue;
            //a fresh element per submit: the state keeps them until the frame is drawn, and several draw lists
            //(HUD, menu, toasts over the menu) are submitted in the same frame
            DrawListElement element = new DrawListElement();
            AtlasTexture texture = (AtlasTexture) list.getCommandTexture(command);
            element.set(list, list.getCommandIndexStart(command), list.getCommandIndexCount(command), inverseScale, pose, bounds,
                    TextureSetup.singleTexture(texture.texture.getTextureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST)));
            state.addGuiElement(element);
        }
    }

    /** Emits a range of the draw list's triangles; the GUI pipelines draw quads, so each triangle is a degenerate quad. */
    private static final class DrawListElement implements GuiElementRenderState {
        private DrawList list;
        private int start, count;
        private float inverseScale;
        private Matrix3x2f pose;
        private ScreenRectangle bounds;
        private TextureSetup textureSetup;

        void set(DrawList list, int start, int count, float inverseScale, Matrix3x2f pose, ScreenRectangle bounds, TextureSetup textureSetup) {
            this.list = list;
            this.start = start;
            this.count = count;
            this.inverseScale = inverseScale;
            this.pose = pose;
            this.bounds = bounds;
            this.textureSetup = textureSetup;
        }

        @Override
        public void buildVertices(VertexConsumer consumer) {
            float[] xy = list.getPositions(), uv = list.getUvs();
            int[] colors = list.getColors(), indices = list.getIndices();
            for (int i = start; i < start + count; i += 3) {
                vertex(consumer, indices[i], xy, uv, colors);
                vertex(consumer, indices[i + 1], xy, uv, colors);
                vertex(consumer, indices[i + 2], xy, uv, colors);
                vertex(consumer, indices[i + 2], xy, uv, colors);
            }
        }

        private void vertex(VertexConsumer consumer, int v, float[] xy, float[] uv, int[] colors) {
            consumer.addVertexWith2DPose(pose, xy[v * 2] * inverseScale, xy[v * 2 + 1] * inverseScale)
                    .setUv(uv[v * 2], uv[v * 2 + 1]).setColor(colors[v]);
        }

        @Override
        public RenderPipeline pipeline() {
            return RenderPipelines.GUI_TEXTURED;
        }

        @Override
        public TextureSetup textureSetup() {
            return textureSetup;
        }

        @Override
        public ScreenRectangle scissorArea() {
            return null;
        }

        @Override
        public ScreenRectangle bounds() {
            return bounds;
        }
    }

    private static final class AtlasTexture implements Texture {
        final DynamicTexture texture;
        final NativeImage image;
        final int width, height;

        AtlasTexture(DynamicTexture texture, NativeImage image, int width, int height) {
            this.texture = texture;
            this.image = image;
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
