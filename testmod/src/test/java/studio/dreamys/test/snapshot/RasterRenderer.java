package studio.dreamys.test.snapshot;

import studio.dreamys.icarus.platform.Renderer;
import studio.dreamys.icarus.platform.Texture;
import studio.dreamys.icarus.render.DrawList;

import java.awt.image.BufferedImage;

/**
 * A software {@link Renderer}: keeps texture pixels and rasterises each {@link DrawList} into an image (pixel-centre
 * sampling, nearest texture filtering, per-vertex colour × texel, source-over blending), like the GPU path does.
 * Used to compare themes against reference screenshots without Minecraft.
 */
public final class RasterRenderer implements Renderer {
    public BufferedImage image;
    private int[] target;
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
        target = new int[width * height];
        java.util.Arrays.fill(target, background);
    }

    /** Copies the rendered pixels into {@link #image}. */
    public BufferedImage finish() {
        image.setRGB(0, 0, width, height, target, 0, width);
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
        float[] xy = list.getPositions(), uv = list.getUvs();
        int[] colors = list.getColors(), indices = list.getIndices();
        for (int c = 0; c < list.getCommandCount(); c++) {
            RasterTexture tex = (RasterTexture) list.getCommandTexture(c);
            int start = list.getCommandIndexStart(c), end = start + list.getCommandIndexCount(c);
            for (int i = start; i < end; i += 3) triangle(xy, uv, colors, indices[i], indices[i + 1], indices[i + 2], tex);
        }
    }

    private void triangle(float[] xy, float[] uv, int[] colors, int a, int b, int c, RasterTexture tex) {
        float x0 = xy[a * 2], y0 = xy[a * 2 + 1], x1 = xy[b * 2], y1 = xy[b * 2 + 1], x2 = xy[c * 2], y2 = xy[c * 2 + 1];
        float area = (x1 - x0) * (y2 - y0) - (y1 - y0) * (x2 - x0);
        if (area == 0) return;
        int minX = Math.max(0, (int) Math.floor(Math.min(x0, Math.min(x1, x2))));
        int maxX = Math.min(width - 1, (int) Math.ceil(Math.max(x0, Math.max(x1, x2))));
        int minY = Math.max(0, (int) Math.floor(Math.min(y0, Math.min(y1, y2))));
        int maxY = Math.min(height - 1, (int) Math.ceil(Math.max(y0, Math.max(y1, y2))));
        for (int py = minY; py <= maxY; py++) {
            float sy = py + 0.5f;
            for (int px = minX; px <= maxX; px++) {
                float sx = px + 0.5f;
                float w0 = ((x1 - sx) * (y2 - sy) - (y1 - sy) * (x2 - sx)) / area;
                float w1 = ((x2 - sx) * (y0 - sy) - (y2 - sy) * (x0 - sx)) / area;
                float w2 = 1 - w0 - w1;
                //half-open edges so shared quad diagonals don't draw twice
                if (w0 < 0 || w1 < 0 || w2 < 0) continue;
                if ((w0 == 0 && !topLeft(x1, y1, x2, y2, area)) || (w1 == 0 && !topLeft(x2, y2, x0, y0, area))
                        || (w2 == 0 && !topLeft(x0, y0, x1, y1, area))) continue;
                float u = w0 * uv[a * 2] + w1 * uv[b * 2] + w2 * uv[c * 2];
                float v = w0 * uv[a * 2 + 1] + w1 * uv[b * 2 + 1] + w2 * uv[c * 2 + 1];
                int texel = tex.pixels[clamp((int) Math.floor(v * tex.h), tex.h) * tex.w + clamp((int) Math.floor(u * tex.w), tex.w)];
                int ca = colors[a], cb = colors[b], cc = colors[c];
                float alpha = mix(ca >>> 24, cb >>> 24, cc >>> 24, w0, w1, w2) * (texel >>> 24) / (255f * 255f);
                if (alpha <= 0) continue;
                float r = mix(ca >> 16 & 255, cb >> 16 & 255, cc >> 16 & 255, w0, w1, w2) * (texel >> 16 & 255) / 255f;
                float g = mix(ca >> 8 & 255, cb >> 8 & 255, cc >> 8 & 255, w0, w1, w2) * (texel >> 8 & 255) / 255f;
                float bl = mix(ca & 255, cb & 255, cc & 255, w0, w1, w2) * (texel & 255) / 255f;
                int index = py * width + px, dst = target[index];
                int nr = Math.round(r * alpha + (dst >> 16 & 255) * (1 - alpha));
                int ng = Math.round(g * alpha + (dst >> 8 & 255) * (1 - alpha));
                int nb = Math.round(bl * alpha + (dst & 255) * (1 - alpha));
                target[index] = 0xFF000000 | nr << 16 | ng << 8 | nb;
            }
        }
    }

    private static boolean topLeft(float ax, float ay, float bx, float by, float area) {
        float dx = bx - ax, dy = by - ay;
        if (area < 0) {
            dx = -dx;
            dy = -dy;
        }
        return (dy == 0 && dx > 0) || dy < 0;
    }

    private static float mix(int a, int b, int c, float w0, float w1, float w2) {
        return a * w0 + b * w1 + c * w2;
    }

    private static int clamp(int v, int size) {
        return v < 0 ? 0 : v >= size ? size - 1 : v;
    }
}
