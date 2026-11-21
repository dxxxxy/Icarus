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
    private float alphaCutoff;

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

    /** Drops fragments whose alpha is below {@code cutoff}, like a shader that discards them (0 by default). */
    public void setAlphaCutoff(float cutoff) {
        alphaCutoff = cutoff;
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
        for (int i = start; i < end; ) {
            if (quads && i + 6 <= end && quad(xy, uv, colors, indices, i, texels, texWidth, texHeight)) {
                i += 6;
                continue;
            }
            triangle(xy, uv, colors, indices[i], indices[i + 1], indices[i + 2], texels, texWidth, texHeight);
            i += 3;
        }
    }

    /** Whether axis-aligned quads take the fast path (tests compare it with the triangle path). */
    boolean quads = true;
    private int[] columnTexels = new int[256];
    private float[] columnColors = new float[4 * 256];

    /**
     * Draws the two triangles at {@code i} as one rectangle if they're a {@link DrawList#quad} with axis-aligned UVs
     * and one colour or a gradient along one axis (where the triangles' interpolation is linear in x or y alone).
     * It covers the same pixels as the triangles (pixel centres in [x0, x1) × [y0, y1)), but works texel columns and
     * colours out once per row or column, skips the edge tests and fills opaque solid rows directly. Returns false
     * (drawing nothing) for anything else.
     */
    private boolean quad(float[] xy, float[] uv, int[] colors, int[] indices, int i, int[] texels, int texW, int texH) {
        int tl = indices[i], bl = indices[i + 1], br = indices[i + 2], tr = tl + 1;
        //DrawList.quad emits (tl, bl, br), (tl, br, tr) with tl, tr, br, bl = base .. base + 3
        if (bl != tl + 3 || br != tl + 2 || indices[i + 3] != tl || indices[i + 4] != br || indices[i + 5] != tr) return false;
        int cTl = colors[tl], cTr = colors[tr], cBr = colors[br], cBl = colors[bl];
        boolean vertical = cTl == cTr && cBl == cBr, horizontal = cTl == cBl && cTr == cBr;
        if (!vertical && !horizontal) return false;
        if (vertical && horizontal) vertical = horizontal = false; //one colour
        float x0 = xy[tl * 2], y0 = xy[tl * 2 + 1], x1 = xy[br * 2], y1 = xy[br * 2 + 1];
        if (xy[tr * 2] != x1 || xy[tr * 2 + 1] != y0 || xy[bl * 2] != x0 || xy[bl * 2 + 1] != y1) return false;
        float u0 = uv[tl * 2], v0 = uv[tl * 2 + 1], u1 = uv[br * 2], v1 = uv[br * 2 + 1];
        if (uv[tr * 2] != u1 || uv[tr * 2 + 1] != v0 || uv[bl * 2] != u0 || uv[bl * 2 + 1] != v1) return false;
        if (!(x1 > x0) || !(y1 > y0)) return true; //empty: the triangles would draw nothing either

        //pixel centres inside the rectangle; left/top edges inclusive like the triangles' top-left rule
        int minX = Math.max(originX, ceil(x0 - 0.5f)), maxX = Math.min(originX + width, ceil(x1 - 0.5f));
        int minY = Math.max(originY, ceil(y0 - 0.5f)), maxY = Math.min(originY + height, ceil(y1 - 0.5f));
        if (minX >= maxX || minY >= maxY) return true;
        int columns = maxX - minX;
        if (columnTexels.length < columns) columnTexels = new int[Math.max(columns, columnTexels.length * 2)];
        float du = (u1 - u0) / (x1 - x0), dv = (v1 - v0) / (y1 - y0);
        for (int px = minX; px < maxX; px++) columnTexels[px - minX] = clamp(floor((u0 + (px + 0.5f - x0) * du) * texW), texW);
        if (horizontal) {
            if (columnColors.length < columns * 4) columnColors = new float[Math.max(columns * 4, columnColors.length * 2)];
            for (int px = minX; px < maxX; px++) lerp(cTl, cTr, (px + 0.5f - x0) / (x1 - x0), columnColors, (px - minX) * 4);
        }
        boolean solid = u0 == u1 && v0 == v1; //every pixel samples the same texel

        float ca = cTl >>> 24, cr = cTl >> 16 & 255, cg = cTl >> 8 & 255, cb = cTl & 255;
        for (int py = minY; py < maxY; py++) {
            if (vertical) {
                float t = (py + 0.5f - y0) / (y1 - y0);
                ca = (cTl >>> 24) + ((cBl >>> 24) - (cTl >>> 24)) * t;
                cr = (cTl >> 16 & 255) + ((cBl >> 16 & 255) - (cTl >> 16 & 255)) * t;
                cg = (cTl >> 8 & 255) + ((cBl >> 8 & 255) - (cTl >> 8 & 255)) * t;
                cb = (cTl & 255) + ((cBl & 255) - (cTl & 255)) * t;
            }
            int texRow = clamp(floor((v0 + (py + 0.5f - y0) * dv) * texH), texH) * texW;
            int row = (py - originY) * width - originX;
            if (solid && !horizontal) {
                int texel = texels[texRow + columnTexels[0]];
                float alpha = ca * (texel >>> 24) / (255f * 255f);
                if (alpha <= 0 || alpha < alphaCutoff) continue;
                float r = cr * (texel >> 16 & 255) / 255f, g = cg * (texel >> 8 & 255) / 255f, b = cb * (texel & 255) / 255f;
                if (alpha >= 1) {
                    java.util.Arrays.fill(target, row + minX, row + maxX, 0xFF000000 | Math.round(r) << 16 | Math.round(g) << 8 | Math.round(b));
                } else {
                    for (int px = minX; px < maxX; px++) blend(row + px, alpha, r, g, b);
                }
                continue;
            }
            for (int px = minX; px < maxX; px++) {
                if (horizontal) {
                    int at = (px - minX) * 4;
                    ca = columnColors[at];
                    cr = columnColors[at + 1];
                    cg = columnColors[at + 2];
                    cb = columnColors[at + 3];
                }
                int texel = texels[texRow + columnTexels[px - minX]];
                float alpha = ca * (texel >>> 24) / (255f * 255f);
                if (alpha <= 0 || alpha < alphaCutoff) continue;
                blend(row + px, alpha, cr * (texel >> 16 & 255) / 255f, cg * (texel >> 8 & 255) / 255f, cb * (texel & 255) / 255f);
            }
        }
        return true;
    }

    /** Source-over of a straight colour (0-255 channels) with {@code alpha} onto the premultiplied target pixel. */
    private void blend(int index, float alpha, float r, float g, float b) {
        int dst = target[index];
        float keep = 1 - alpha;
        int na = Math.round(255 * alpha + (dst >>> 24) * keep);
        int nr = Math.round(r * alpha + (dst >> 16 & 255) * keep);
        int ng = Math.round(g * alpha + (dst >> 8 & 255) * keep);
        int nb = Math.round(b * alpha + (dst & 255) * keep);
        target[index] = na << 24 | nr << 16 | ng << 8 | nb;
    }

    private static void lerp(int from, int to, float t, float[] out, int at) {
        out[at] = (from >>> 24) + ((to >>> 24) - (from >>> 24)) * t;
        out[at + 1] = (from >> 16 & 255) + ((to >> 16 & 255) - (from >> 16 & 255)) * t;
        out[at + 2] = (from >> 8 & 255) + ((to >> 8 & 255) - (from >> 8 & 255)) * t;
        out[at + 3] = (from & 255) + ((to & 255) - (from & 255)) * t;
    }

    /** {@code Math.floor} for floats in int range, without the round trip through double. */
    private static int floor(float x) {
        int i = (int) x;
        return x < i ? i - 1 : i;
    }

    private static int ceil(float x) {
        int i = (int) x;
        return x > i ? i + 1 : i;
    }

    /**
     * One triangle. Coverage uses exact integer edge functions on vertices snapped to 1/256 pixel, like GPUs do, so
     * a pixel centre on an edge shared by two triangles (a quad's diagonal) is drawn exactly once.
     */
    private void triangle(float[] xy, float[] uv, int[] colors, int a, int b, int c, int[] texels, int texW, int texH) {
        float x0 = xy[a * 2], y0 = xy[a * 2 + 1], x1 = xy[b * 2], y1 = xy[b * 2 + 1], x2 = xy[c * 2], y2 = xy[c * 2 + 1];
        long fx0 = fixed(x0), fy0 = fixed(y0), fx1 = fixed(x1), fy1 = fixed(y1), fx2 = fixed(x2), fy2 = fixed(y2);
        long area = (fx1 - fx0) * (fy2 - fy0) - (fy1 - fy0) * (fx2 - fx0);
        if (area == 0) return;
        int minX = Math.max(originX, floor(Math.min(x0, Math.min(x1, x2))));
        int maxX = Math.min(originX + width - 1, ceil(Math.max(x0, Math.max(x1, x2))));
        int minY = Math.max(originY, floor(Math.min(y0, Math.min(y1, y2))));
        int maxY = Math.min(originY + height - 1, ceil(Math.max(y0, Math.max(y1, y2))));
        if (minX > maxX || minY > maxY) return;
        long sign = area > 0 ? 1 : -1;
        boolean edge0 = topLeft(fx1, fy1, fx2, fy2, sign), edge1 = topLeft(fx2, fy2, fx0, fy0, sign), edge2 = topLeft(fx0, fy0, fx1, fy1, sign);
        float inverseArea = 1f / area;
        int ca = colors[a], cb = colors[b], cc = colors[c];
        for (int py = minY; py <= maxY; py++) {
            long sy = (long) py * SUBPIXELS + SUBPIXELS / 2;
            int row = (py - originY) * width - originX;
            for (int px = minX; px <= maxX; px++) {
                long sx = (long) px * SUBPIXELS + SUBPIXELS / 2;
                long e0 = ((fx1 - sx) * (fy2 - sy) - (fy1 - sy) * (fx2 - sx)) * sign;
                long e1 = ((fx2 - sx) * (fy0 - sy) - (fy2 - sy) * (fx0 - sx)) * sign;
                long e2 = area * sign - e0 - e1;
                //half-open edges so shared edges don't draw twice
                if (e0 < 0 || e1 < 0 || e2 < 0) continue;
                if ((e0 == 0 && !edge0) || (e1 == 0 && !edge1) || (e2 == 0 && !edge2)) continue;
                float w0 = e0 * sign * inverseArea, w1 = e1 * sign * inverseArea, w2 = 1 - w0 - w1;
                float u = w0 * uv[a * 2] + w1 * uv[b * 2] + w2 * uv[c * 2];
                float v = w0 * uv[a * 2 + 1] + w1 * uv[b * 2 + 1] + w2 * uv[c * 2 + 1];
                int texel = texels[clamp(floor(v * texH), texH) * texW + clamp(floor(u * texW), texW)];
                float ma = mix(ca >>> 24, cb >>> 24, cc >>> 24, w0, w1, w2);
                float mr = mix(ca >> 16 & 255, cb >> 16 & 255, cc >> 16 & 255, w0, w1, w2);
                float mg = mix(ca >> 8 & 255, cb >> 8 & 255, cc >> 8 & 255, w0, w1, w2);
                float mb = mix(ca & 255, cb & 255, cc & 255, w0, w1, w2);
                float alpha = ma * (texel >>> 24) / (255f * 255f);
                if (alpha <= 0 || alpha < alphaCutoff) continue;
                float r = mr * (texel >> 16 & 255) / 255f;
                float g = mg * (texel >> 8 & 255) / 255f;
                float bl = mb * (texel & 255) / 255f;
                blend(row + px, alpha, r, g, bl);
            }
        }
    }

    /** Sub-pixel precision of triangle vertices. */
    private static final int SUBPIXELS = 256;

    private static long fixed(float v) {
        return Math.round((double) v * SUBPIXELS);
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

    /** Whether the edge a to b of a triangle wound by {@code sign} is a top or left edge (owns centres on it). */
    private static boolean topLeft(long ax, long ay, long bx, long by, long sign) {
        long dx = (bx - ax) * sign, dy = (by - ay) * sign;
        return (dy == 0 && dx > 0) || dy < 0;
    }

    private static float mix(int a, int b, int c, float w0, float w1, float w2) {
        return a * w0 + b * w1 + c * w2;
    }

    private static int clamp(int v, int size) {
        return v < 0 ? 0 : v >= size ? size - 1 : v;
    }
}
