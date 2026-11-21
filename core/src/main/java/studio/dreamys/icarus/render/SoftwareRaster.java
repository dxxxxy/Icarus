package studio.dreamys.icarus.render;

import studio.dreamys.icarus.platform.Texture;

/**
 * Rasterises {@link DrawList}s on the CPU, the way the GPU path draws them: pixel-centre sampling, top-left fill rule,
 * nearest texture filtering, per-vertex colour × texel and source-over blending. The target is a premultiplied ARGB
 * buffer covering a rectangle of the framebuffer, so drawing into a transparent target and compositing the result
 * over something gives the same pixels as drawing onto it directly. Buffers are reused, so steady frames don't
 * allocate.
 */
public final class SoftwareRaster {
    private int[] target = new int[0];
    private int originX, originY, width, height;

    /** Starts a frame covering framebuffer pixels [x, x + w) × [y, y + h), filled with {@code premultipliedArgb}. */
    public void begin(int x, int y, int w, int h, int premultipliedArgb) {
        if (w < 0 || h < 0) throw new IllegalArgumentException("negative size");
        originX = x;
        originY = y;
        width = w;
        height = h;
        if (target.length < w * h) target = new int[w * h];
        java.util.Arrays.fill(target, 0, w * h, premultipliedArgb);
    }

    /** Premultiplied ARGB pixels, row by row ({@link #getWidth()} per row); only the first width × height are used. */
    public int[] getPixels() {
        return target;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getOriginX() {
        return originX;
    }

    public int getOriginY() {
        return originY;
    }

    /** Draws every command of {@code list} that samples the atlas texture (all Icarus drawing does). */
    public void draw(DrawList list, FontAtlas atlas) {
        Texture texture = atlas.getTexture();
        for (int c = 0; c < list.getCommandCount(); c++) {
            if (list.getCommandTexture(c) != texture) continue;
            drawCommand(list, c, atlas.pixels(), atlas.getSize(), atlas.getSize());
        }
    }

    /** Draws one command of {@code list}, sampling an ARGB texture of {@code texWidth} × {@code texHeight}. */
    public void drawCommand(DrawList list, int command, int[] texels, int texWidth, int texHeight) {
        float[] xy = list.getPositions(), uv = list.getUvs();
        int[] colors = list.getColors(), indices = list.getIndices();
        int start = list.getCommandIndexStart(command), end = start + list.getCommandIndexCount(command);
        for (int i = start; i < end; i += 3) {
            triangle(xy, uv, colors, indices[i], indices[i + 1], indices[i + 2], texels, texWidth, texHeight);
        }
    }

    private void triangle(float[] xy, float[] uv, int[] colors, int a, int b, int c, int[] texels, int texW, int texH) {
        float x0 = xy[a * 2], y0 = xy[a * 2 + 1], x1 = xy[b * 2], y1 = xy[b * 2 + 1], x2 = xy[c * 2], y2 = xy[c * 2 + 1];
        float area = (x1 - x0) * (y2 - y0) - (y1 - y0) * (x2 - x0);
        if (area == 0) return;
        int minX = Math.max(originX, (int) Math.floor(Math.min(x0, Math.min(x1, x2))));
        int maxX = Math.min(originX + width - 1, (int) Math.ceil(Math.max(x0, Math.max(x1, x2))));
        int minY = Math.max(originY, (int) Math.floor(Math.min(y0, Math.min(y1, y2))));
        int maxY = Math.min(originY + height - 1, (int) Math.ceil(Math.max(y0, Math.max(y1, y2))));
        if (minX > maxX || minY > maxY) return;
        boolean edge0 = topLeft(x1, y1, x2, y2, area), edge1 = topLeft(x2, y2, x0, y0, area), edge2 = topLeft(x0, y0, x1, y1, area);
        int ca = colors[a], cb = colors[b], cc = colors[c];
        for (int py = minY; py <= maxY; py++) {
            float sy = py + 0.5f;
            int row = (py - originY) * width - originX;
            for (int px = minX; px <= maxX; px++) {
                float sx = px + 0.5f;
                float w0 = ((x1 - sx) * (y2 - sy) - (y1 - sy) * (x2 - sx)) / area;
                float w1 = ((x2 - sx) * (y0 - sy) - (y2 - sy) * (x0 - sx)) / area;
                float w2 = 1 - w0 - w1;
                //half-open edges so shared quad diagonals don't draw twice
                if (w0 < 0 || w1 < 0 || w2 < 0) continue;
                if ((w0 == 0 && !edge0) || (w1 == 0 && !edge1) || (w2 == 0 && !edge2)) continue;
                float u = w0 * uv[a * 2] + w1 * uv[b * 2] + w2 * uv[c * 2];
                float v = w0 * uv[a * 2 + 1] + w1 * uv[b * 2 + 1] + w2 * uv[c * 2 + 1];
                int texel = texels[clamp((int) Math.floor(v * texH), texH) * texW + clamp((int) Math.floor(u * texW), texW)];
                float ma = mix(ca >>> 24, cb >>> 24, cc >>> 24, w0, w1, w2);
                float mr = mix(ca >> 16 & 255, cb >> 16 & 255, cc >> 16 & 255, w0, w1, w2);
                float mg = mix(ca >> 8 & 255, cb >> 8 & 255, cc >> 8 & 255, w0, w1, w2);
                float mb = mix(ca & 255, cb & 255, cc & 255, w0, w1, w2);
                float alpha = ma * (texel >>> 24) / (255f * 255f);
                if (alpha <= 0) continue;
                float r = mr * (texel >> 16 & 255) / 255f;
                float g = mg * (texel >> 8 & 255) / 255f;
                float bl = mb * (texel & 255) / 255f;
                int index = row + px, dst = target[index];
                int na = Math.round(255 * alpha + (dst >>> 24) * (1 - alpha));
                int nr = Math.round(r * alpha + (dst >> 16 & 255) * (1 - alpha));
                int ng = Math.round(g * alpha + (dst >> 8 & 255) * (1 - alpha));
                int nb = Math.round(bl * alpha + (dst & 255) * (1 - alpha));
                target[index] = na << 24 | nr << 16 | ng << 8 | nb;
            }
        }
    }

    /**
     * The pixel bounds {x0, y0, x1, y1} (exclusive) of everything in {@code list}, clamped to [0, maxW) × [0, maxH),
     * unioned into {@code bounds}. Start with {@link #emptyBounds(int[])}.
     */
    public static void unionBounds(DrawList list, int maxW, int maxH, int[] bounds) {
        float[] xy = list.getPositions();
        int n = list.getVertexCount();
        if (n == 0) return;
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            float x = xy[i * 2], y = xy[i * 2 + 1];
            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
        }
        int x0 = Math.max(0, (int) Math.floor(minX)), y0 = Math.max(0, (int) Math.floor(minY));
        int x1 = Math.min(maxW, (int) Math.ceil(maxX)), y1 = Math.min(maxH, (int) Math.ceil(maxY));
        if (x1 <= x0 || y1 <= y0) return;
        if (bounds[2] <= bounds[0] || bounds[3] <= bounds[1]) {
            bounds[0] = x0;
            bounds[1] = y0;
            bounds[2] = x1;
            bounds[3] = y1;
        } else {
            bounds[0] = Math.min(bounds[0], x0);
            bounds[1] = Math.min(bounds[1], y0);
            bounds[2] = Math.max(bounds[2], x1);
            bounds[3] = Math.max(bounds[3], y1);
        }
    }

    public static void emptyBounds(int[] bounds) {
        bounds[0] = bounds[1] = bounds[2] = bounds[3] = 0;
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
