package studio.dreamys.icarus.platform.mc263;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.textures.FilterMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2fStack;
import studio.dreamys.icarus.platform.Renderer;
import studio.dreamys.icarus.platform.Texture;

/**
 * 26.3: the GUI is retained-mode. Drawing means submitting elements to the frame's {@link GuiGraphicsExtractor},
 * whose public fill/blit calls only take int coordinates. Icarus draws at fractional positions, so every element
 * is submitted as a unit (or texel-sized) quad and placed exactly by translating and scaling the pose.
 */
public class ModernRenderer implements Renderer {
    private static int textureCounter;

    private GuiGraphicsExtractor graphics;

    /** Binds the renderer to the frame's extractor; must be called before Icarus draws anything. */
    public void begin(GuiGraphicsExtractor graphics) {
        this.graphics = graphics;
    }

    private Matrix3x2fStack pose() {
        return graphics.pose();
    }

    /** Pushes a pose mapping the unit square onto the given rectangle. */
    private void pushRect(double x1, double y1, double x2, double y2) {
        pose().pushMatrix();
        pose().translate((float) Math.min(x1, x2), (float) Math.min(y1, y2));
        pose().scale((float) Math.abs(x2 - x1), (float) Math.abs(y2 - y1));
    }

    @Override
    public void fill(double x1, double y1, double x2, double y2, int color) {
        if (x1 == x2 || y1 == y2) return;
        pushRect(x1, y1, x2, y2);
        graphics.fill(0, 0, 1, 1, color);
        pose().popMatrix();
    }

    @Override
    public void fillGradient(double x, double y, double width, double height, int topColor, int bottomColor) {
        if (width == 0 || height == 0) return;
        pushRect(x, y, x + width, y + height);
        graphics.fillGradient(0, 0, 1, 1, topColor, bottomColor);
        pose().popMatrix();
    }

    @Override
    public Texture createTexture(int width, int height, int[] argb, boolean linear) {
        NativeImage image = new NativeImage(NativeImage.Format.RGBA, width, height, false);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image.setPixel(x, y, argb[y * width + x]); //26.x NativeImage#setPixel takes ARGB
            }
        }

        Identifier id = Identifier.fromNamespaceAndPath("icarus", "dynamic/" + textureCounter++);
        DynamicTexture texture = new DynamicTexture(id::toString, image) {
            {
                if (linear) sampler = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR);
            }
        };
        Minecraft.getInstance().getTextureManager().register(id, texture);

        return new ModernTexture(id, width, height);
    }

    @Override
    public void drawTexture(Texture texture, double x, double y, double width, double height, float u0, float v0, float u1, float v1, int tint) {
        if (width == 0 || height == 0) return;
        ModernTexture t = (ModernTexture) texture;

        //source region in texels; the blit maps it onto a regionW x regionH quad, which the pose then stretches
        float u = u0 * t.width, v = v0 * t.height;
        int regionW = Math.max(1, Math.round((u1 - u0) * t.width));
        int regionH = Math.max(1, Math.round((v1 - v0) * t.height));

        pose().pushMatrix();
        pose().translate((float) x, (float) y);
        pose().scale((float) (width / regionW), (float) (height / regionH));
        graphics.blit(RenderPipelines.GUI_TEXTURED, t.id, 0, 0, u, v, regionW, regionH, regionW, regionH, t.width, t.height, tint);
        pose().popMatrix();
    }

    @Override
    public void pushMatrix() {
        pose().pushMatrix();
    }

    @Override
    public void scale(float x, float y) {
        pose().scale(x, y);
    }

    @Override
    public void popMatrix() {
        pose().popMatrix();
    }

    private static final class ModernTexture implements Texture {
        private final Identifier id;
        private final int width, height;

        ModernTexture(Identifier id, int width, int height) {
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
