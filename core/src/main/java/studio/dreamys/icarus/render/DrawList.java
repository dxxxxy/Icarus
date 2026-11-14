package studio.dreamys.icarus.render;

import studio.dreamys.icarus.platform.Texture;

/**
 * One frame of Icarus drawing: indexed triangles in framebuffer pixels with per-vertex colour and UVs, split into
 * commands only when the texture changes. Clipping is done on the CPU while primitives are added (axis-aligned
 * quads are cut exactly, triangles are kept or dropped), so platforms never need a scissor. Arrays grow as needed
 * and are reused across frames, so steady-state frames don't allocate.
 */
public final class DrawList {
    private float[] xy = new float[2 * 1024];
    private float[] uv = new float[2 * 1024];
    private int[] colors = new int[1024];
    private int vertexCount;

    private int[] indices = new int[1536];
    private int indexCount;

    private Texture[] cmdTexture = new Texture[8];
    private int[] cmdStart = new int[8];
    private int[] cmdCount = new int[8];
    private int commandCount;
    private Texture texture;

    private float[] clips = new float[4 * 16];
    private int clipDepth;
    private float clipX0, clipY0, clipX1, clipY1;

    public DrawList() {
        reset(Float.MAX_VALUE, Float.MAX_VALUE);
    }

    /** Starts a new frame; {@code width}/{@code height} bound the default clip (the framebuffer). */
    public void reset(float width, float height) {
        vertexCount = 0;
        indexCount = 0;
        commandCount = 0;
        texture = null;
        clipDepth = 0;
        clipX0 = 0;
        clipY0 = 0;
        clipX1 = width;
        clipY1 = height;
    }

    /* ---- state ---- */

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    /** Intersects the clip rectangle (pixels) with the current one. */
    public void pushClip(float x0, float y0, float x1, float y1) {
        if (clipDepth * 4 + 4 > clips.length) clips = grow(clips, clips.length * 2);
        clips[clipDepth * 4] = clipX0;
        clips[clipDepth * 4 + 1] = clipY0;
        clips[clipDepth * 4 + 2] = clipX1;
        clips[clipDepth * 4 + 3] = clipY1;
        clipDepth++;
        clipX0 = Math.max(clipX0, x0);
        clipY0 = Math.max(clipY0, y0);
        clipX1 = Math.min(clipX1, x1);
        clipY1 = Math.min(clipY1, y1);
    }

    public void popClip() {
        if (clipDepth == 0) throw new IllegalStateException("popClip without pushClip");
        clipDepth--;
        clipX0 = clips[clipDepth * 4];
        clipY0 = clips[clipDepth * 4 + 1];
        clipX1 = clips[clipDepth * 4 + 2];
        clipY1 = clips[clipDepth * 4 + 3];
    }

    public boolean isClippedOut(float x0, float y0, float x1, float y1) {
        return x1 <= clipX0 || x0 >= clipX1 || y1 <= clipY0 || y0 >= clipY1 || clipX1 <= clipX0 || clipY1 <= clipY0;
    }

    /* ---- primitives ---- */

    /**
     * An axis-aligned quad with a colour per corner (top-left, top-right, bottom-right, bottom-left), cut to the clip
     * rectangle with UVs and colours interpolated.
     */
    public void quad(float x0, float y0, float x1, float y1, float u0, float v0, float u1, float v1,
                     int topLeft, int topRight, int bottomRight, int bottomLeft) {
        if (x1 < x0 || y1 < y0 || isClippedOut(x0, y0, x1, y1)) return;

        float cx0 = Math.max(x0, clipX0), cy0 = Math.max(y0, clipY0);
        float cx1 = Math.min(x1, clipX1), cy1 = Math.min(y1, clipY1);
        if (cx0 != x0 || cy0 != y0 || cx1 != x1 || cy1 != y1) {
            float w = x1 - x0, h = y1 - y0;
            float fx0 = (cx0 - x0) / w, fx1 = (cx1 - x0) / w, fy0 = (cy0 - y0) / h, fy1 = (cy1 - y0) / h;
            float nu0 = u0 + (u1 - u0) * fx0, nu1 = u0 + (u1 - u0) * fx1;
            float nv0 = v0 + (v1 - v0) * fy0, nv1 = v0 + (v1 - v0) * fy1;
            int tl = bilerp(topLeft, topRight, bottomRight, bottomLeft, fx0, fy0);
            int tr = bilerp(topLeft, topRight, bottomRight, bottomLeft, fx1, fy0);
            int br = bilerp(topLeft, topRight, bottomRight, bottomLeft, fx1, fy1);
            int bl = bilerp(topLeft, topRight, bottomRight, bottomLeft, fx0, fy1);
            x0 = cx0; y0 = cy0; x1 = cx1; y1 = cy1;
            u0 = nu0; u1 = nu1; v0 = nv0; v1 = nv1;
            topLeft = tl; topRight = tr; bottomRight = br; bottomLeft = bl;
        }

        ensureCommand();
        ensureVertices(4);
        ensureIndices(6);
        int base = vertexCount;
        vertex(x0, y0, u0, v0, topLeft);
        vertex(x1, y0, u1, v0, topRight);
        vertex(x1, y1, u1, v1, bottomRight);
        vertex(x0, y1, u0, v1, bottomLeft);
        index(base, base + 1, base + 2);
        index(base, base + 2, base + 3);
    }

    /** A triangle; kept whole if its bounds are inside the clip, dropped otherwise. */
    public void triangle(float x0, float y0, float x1, float y1, float x2, float y2, float u, float v, int color) {
        float minX = Math.min(x0, Math.min(x1, x2)), maxX = Math.max(x0, Math.max(x1, x2));
        float minY = Math.min(y0, Math.min(y1, y2)), maxY = Math.max(y0, Math.max(y1, y2));
        if (minX < clipX0 || minY < clipY0 || maxX > clipX1 || maxY > clipY1) return;
        ensureCommand();
        ensureVertices(3);
        ensureIndices(3);
        int base = vertexCount;
        vertex(x0, y0, u, v, color);
        vertex(x1, y1, u, v, color);
        vertex(x2, y2, u, v, color);
        index(base, base + 1, base + 2);
    }

    private void vertex(float x, float y, float u, float v, int color) {
        int i = vertexCount++;
        xy[i * 2] = x;
        xy[i * 2 + 1] = y;
        uv[i * 2] = u;
        uv[i * 2 + 1] = v;
        colors[i] = color;
    }

    private void index(int a, int b, int c) {
        indices[indexCount++] = a;
        indices[indexCount++] = b;
        indices[indexCount++] = c;
        cmdCount[commandCount - 1] += 3;
    }

    private void ensureCommand() {
        if (texture == null) throw new IllegalStateException("No texture set");
        if (commandCount > 0 && cmdTexture[commandCount - 1] == texture) return;
        if (commandCount > 0 && cmdCount[commandCount - 1] == 0) {
            cmdTexture[commandCount - 1] = texture;
            return;
        }
        if (commandCount == cmdTexture.length) {
            Texture[] t = new Texture[commandCount * 2];
            System.arraycopy(cmdTexture, 0, t, 0, commandCount);
            cmdTexture = t;
            cmdStart = grow(cmdStart, commandCount * 2);
            cmdCount = grow(cmdCount, commandCount * 2);
        }
        cmdTexture[commandCount] = texture;
        cmdStart[commandCount] = indexCount;
        cmdCount[commandCount] = 0;
        commandCount++;
    }

    private void ensureVertices(int n) {
        if (vertexCount + n <= colors.length) return;
        int capacity = Math.max(colors.length * 2, vertexCount + n);
        xy = grow(xy, capacity * 2);
        uv = grow(uv, capacity * 2);
        colors = grow(colors, capacity);
    }

    private void ensureIndices(int n) {
        if (indexCount + n > indices.length) indices = grow(indices, Math.max(indices.length * 2, indexCount + n));
    }

    private static float[] grow(float[] a, int size) {
        float[] b = new float[size];
        System.arraycopy(a, 0, b, 0, a.length);
        return b;
    }

    private static int[] grow(int[] a, int size) {
        int[] b = new int[size];
        System.arraycopy(a, 0, b, 0, a.length);
        return b;
    }

    /** Bilinear interpolation of four ARGB corner colours (fx, fy in 0..1). */
    static int bilerp(int tl, int tr, int br, int bl, float fx, float fy) {
        if (tl == tr && tr == br && br == bl) return tl;
        return lerpColor(lerpColor(tl, tr, fx), lerpColor(bl, br, fx), fy);
    }

    public static int lerpColor(int a, int b, float t) {
        if (a == b) return a;
        int aa = a >>> 24, ar = a >> 16 & 255, ag = a >> 8 & 255, ab = a & 255;
        int ba = b >>> 24, br = b >> 16 & 255, bg = b >> 8 & 255, bb = b & 255;
        return Math.round(aa + (ba - aa) * t) << 24 | Math.round(ar + (br - ar) * t) << 16
                | Math.round(ag + (bg - ag) * t) << 8 | Math.round(ab + (bb - ab) * t);
    }

    /* ---- read access for renderers ---- */

    public boolean isEmpty() {
        return indexCount == 0;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    /** Interleaved x, y pixel coordinates (2 floats per vertex). */
    public float[] getPositions() {
        return xy;
    }

    /** Interleaved u, v texture coordinates (2 floats per vertex). */
    public float[] getUvs() {
        return uv;
    }

    /** ARGB colour per vertex. */
    public int[] getColors() {
        return colors;
    }

    public int[] getIndices() {
        return indices;
    }

    public int getCommandCount() {
        return commandCount;
    }

    public Texture getCommandTexture(int command) {
        return cmdTexture[command];
    }

    public int getCommandIndexStart(int command) {
        return cmdStart[command];
    }

    public int getCommandIndexCount(int command) {
        return cmdCount[command];
    }
}
