package studio.dreamys.test.snapshot;

import studio.dreamys.icarus.platform.Renderer;
import studio.dreamys.icarus.platform.Texture;
import studio.dreamys.icarus.render.DrawList;
import studio.dreamys.icarus.render.SoftwareRaster;

import java.awt.image.BufferedImage;

/**
 * A software {@link Renderer}: keeps texture pixels and rasterises each {@link DrawList} into an image with core's
 * {@link SoftwareRaster}, the rasteriser the recorder-hidden menu uses too. Used to compare themes against reference
 * screenshots without Minecraft.
 */
public final class RasterRenderer implements Renderer {
    public BufferedImage image;
    private final SoftwareRaster raster = new SoftwareRaster();
    private int width, height;

    public static final class RasterTexture implements Texture {
        final int w, h;
        final int[] pixels;

        RasterTexture(int w, int h) {
            this.w = w;
            this.h = h;
            this.pixels = new int[w * h];
        }

        @Override
        public int getWidth() {
            return w;
        }

        @Override
        public int getHeight() {
            return h;
        }
    }

    /** Starts a new image filled with {@code background} (ARGB, opaque). */
    public void clear(int width, int height, int background) {
        this.width = width;
        this.height = height;
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        raster.begin(0, 0, width, height, background);
    }

    /** Copies the rendered pixels into {@link #image}. */
    public BufferedImage finish() {
        image.setRGB(0, 0, width, height, raster.getPixels(), 0, width);
        return image;
    }

    @Override
    public Texture createTexture(int width, int height) {
        return new RasterTexture(width, height);
    }

    @Override
    public void upload(Texture texture, int x, int y, int w, int h, int[] argb, int offset, int stride) {
        RasterTexture t = (RasterTexture) texture;
        for (int row = 0; row < h; row++) System.arraycopy(argb, offset + row * stride, t.pixels, (y + row) * t.w + x, w);
    }

    @Override
    public void deleteTexture(Texture texture) {
    }

    @Override
    public void render(DrawList list) {
        for (int c = 0; c < list.getCommandCount(); c++) {
            RasterTexture tex = (RasterTexture) list.getCommandTexture(c);
            raster.drawCommand(list, c, tex.pixels, tex.w, tex.h);
        }
    }
}
