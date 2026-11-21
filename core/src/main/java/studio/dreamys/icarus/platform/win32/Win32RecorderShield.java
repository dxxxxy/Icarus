package studio.dreamys.icarus.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import studio.dreamys.icarus.platform.RecorderShield;

import java.util.logging.Logger;

/**
 * Windows: a layered, click-through, non-activating popup owned by the game window, excluded from capture with
 * {@code SetWindowDisplayAffinity(WDA_EXCLUDEFROMCAPTURE)} (Windows 10 2004+). Its pixels are set with
 * {@code UpdateLayeredWindow}, so the compositor blends it over the game the way the menu is blended in game. Being
 * owned, it stays right above the game window and minimizes with it, without being topmost over other apps.
 * <p>
 * Only JNA 3.4 API is used (the oldest Minecraft ships) and no {@code Structure}, whose field-order rules changed in
 * JNA 4: structs are laid out by hand in {@link Memory}.
 */
public final class Win32RecorderShield implements RecorderShield {
    private static final Logger LOG = Logger.getLogger("Icarus");

    private static final int WS_POPUP = 0x80000000;
    private static final int WS_EX_TRANSPARENT = 0x20, WS_EX_TOOLWINDOW = 0x80, WS_EX_LAYERED = 0x80000, WS_EX_NOACTIVATE = 0x08000000;
    private static final int WDA_EXCLUDEFROMCAPTURE = 0x11;
    private static final int SW_HIDE = 0, SW_SHOWNOACTIVATE = 4;
    private static final int SWP_NOSIZE = 0x1, SWP_NOZORDER = 0x4, SWP_NOACTIVATE = 0x10;
    private static final int ULW_ALPHA = 2;

    @SuppressWarnings("UnusedReturnValue")
    private interface User32 extends StdCallLibrary {
        Pointer CreateWindowExW(int exStyle, WString className, WString windowName, int style, int x, int y, int width, int height,
                                Pointer parent, Pointer menu, Pointer instance, Pointer param);

        boolean DestroyWindow(Pointer hwnd);

        boolean IsWindow(Pointer hwnd);

        boolean IsWindowVisible(Pointer hwnd);

        boolean ShowWindow(Pointer hwnd, int command);

        boolean SetWindowPos(Pointer hwnd, Pointer insertAfter, int x, int y, int cx, int cy, int flags);

        boolean SetWindowDisplayAffinity(Pointer hwnd, int affinity);

        boolean UpdateLayeredWindow(Pointer hwnd, Pointer dstDc, Pointer dstPoint, Pointer size, Pointer srcDc, Pointer srcPoint,
                                    int colorKey, Pointer blend, int flags);

        boolean ClientToScreen(Pointer hwnd, Pointer point);

        boolean GetClientRect(Pointer hwnd, Pointer rect);

        boolean EnumThreadWindows(int threadId, WindowCallback callback, Pointer param);

        Pointer GetDC(Pointer hwnd);

        int ReleaseDC(Pointer hwnd, Pointer dc);
    }

    private interface WindowCallback extends StdCallLibrary.StdCallCallback {
        boolean callback(Pointer hwnd, Pointer param);
    }

    @SuppressWarnings("UnusedReturnValue")
    private interface Gdi32 extends StdCallLibrary {
        Pointer CreateCompatibleDC(Pointer dc);

        Pointer CreateDIBSection(Pointer dc, Pointer info, int usage, PointerByReference bits, Pointer section, int offset);

        Pointer SelectObject(Pointer dc, Pointer object);

        boolean DeleteObject(Pointer object);
    }

    private interface Kernel32 extends StdCallLibrary {
        int GetCurrentThreadId();
    }

    private final User32 user32;
    private final Gdi32 gdi32;
    private final Kernel32 kernel32;

    private final Memory point = new Memory(8), size = new Memory(8), origin = new Memory(8), blend = new Memory(4), rect = new Memory(16);
    private final Memory bitmapInfo = new Memory(44);
    private final PointerByReference bitsRef = new PointerByReference();
    private final WString className = new WString("STATIC"), title = new WString("Icarus");
    private final GameWindowFinder finder = new GameWindowFinder();

    private Pointer game, window, memoryDc, bitmap, bits;
    private int bitmapWidth, bitmapHeight;
    private int shownX, shownY;
    private boolean visible, failed;

    private Win32RecorderShield() {
        user32 = (User32) Native.loadLibrary("user32", User32.class);
        gdi32 = (Gdi32) Native.loadLibrary("gdi32", Gdi32.class);
        kernel32 = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);
        origin.clear();
        blend.setByte(0, (byte) 0); //AC_SRC_OVER
        blend.setByte(1, (byte) 0);
        blend.setByte(2, (byte) 255); //SourceConstantAlpha
        blend.setByte(3, (byte) 1); //AC_SRC_ALPHA: per-pixel, premultiplied
    }

    /** The shield on Windows, or {@link RecorderShield#UNAVAILABLE} elsewhere or without JNA. */
    public static RecorderShield create() {
        if (!System.getProperty("os.name", "").startsWith("Windows")) return UNAVAILABLE;
        try {
            return new Win32RecorderShield();
        } catch (Throwable e) { //NoClassDefFoundError without JNA, UnsatisfiedLinkError without its natives
            LOG.warning("[Icarus] Can't hide the menu from recorders: " + e);
            return UNAVAILABLE;
        }
    }

    @Override
    public boolean isAvailable() {
        return !failed;
    }

    @Override
    public boolean show(int[] pixels, int stride, int width, int height, int x, int y, boolean changed) {
        if (failed) return false;
        try {
            if (width <= 0 || height <= 0) {
                hide();
                return true;
            }
            if (!ensureWindow()) return false;
            point.setInt(0, x);
            point.setInt(4, y);
            user32.ClientToScreen(game, point);
            int screenX = point.getInt(0), screenY = point.getInt(4);
            if (changed || !visible) {
                ensureBitmap(width, height);
                for (int row = 0; row < height; row++) bits.write((long) row * bitmapWidth * 4, pixels, row * stride, width);
                size.setInt(0, width);
                size.setInt(4, height);
                Pointer screenDc = user32.GetDC(null);
                boolean ok = user32.UpdateLayeredWindow(window, screenDc, point, size, memoryDc, origin, 0, blend, ULW_ALPHA);
                user32.ReleaseDC(null, screenDc);
                if (!ok) return fail("UpdateLayeredWindow failed");
            } else if (screenX != shownX || screenY != shownY) {
                user32.SetWindowPos(window, null, screenX, screenY, 0, 0, SWP_NOSIZE | SWP_NOZORDER | SWP_NOACTIVATE);
            }
            shownX = screenX;
            shownY = screenY;
            if (!visible) {
                user32.ShowWindow(window, SW_SHOWNOACTIVATE);
                visible = true;
            }
            return true;
        } catch (RuntimeException | LinkageError e) {
            return fail(e.toString());
        }
    }

    @Override
    public void hide() {
        if (!visible || window == null) return;
        visible = false;
        if (user32.IsWindow(window)) user32.ShowWindow(window, SW_HIDE);
    }

    /** Finds the game window and (re)creates the overlay owned by it; false if that's impossible. */
    private boolean ensureWindow() {
        if (window != null && user32.IsWindow(window) && user32.IsWindow(game)) return true;
        //first use, or the game recreated its window (LWJGL 2 does on a fullscreen toggle), which destroys ours too
        if (window != null && user32.IsWindow(window)) user32.DestroyWindow(window);
        window = null;
        visible = false;
        game = finder.find();
        if (game == null) return fail("no visible game window on the render thread");
        window = user32.CreateWindowExW(WS_EX_LAYERED | WS_EX_TRANSPARENT | WS_EX_TOOLWINDOW | WS_EX_NOACTIVATE,
                className, title, WS_POPUP, 0, 0, 1, 1, game, null, null, null);
        if (window == null) return fail("CreateWindowEx failed");
        if (!user32.SetWindowDisplayAffinity(window, WDA_EXCLUDEFROMCAPTURE)) {
            return fail("SetWindowDisplayAffinity(WDA_EXCLUDEFROMCAPTURE) failed; it needs Windows 10 2004 or later");
        }
        return true;
    }

    /** A top-down 32-bit DIB at least {@code width} × {@code height}, selected into the memory DC. */
    private void ensureBitmap(int width, int height) {
        if (bitmap != null && width <= bitmapWidth && height <= bitmapHeight) return;
        //grow with headroom so dragging a popup open doesn't reallocate every few frames
        int w = Math.max(width + width / 4, bitmapWidth), h = Math.max(height + height / 4, bitmapHeight);
        if (memoryDc == null) memoryDc = gdi32.CreateCompatibleDC(null);
        bitmapInfo.clear();
        bitmapInfo.setInt(0, 40); //biSize
        bitmapInfo.setInt(4, w);
        bitmapInfo.setInt(8, -h); //negative: top-down rows
        bitmapInfo.setShort(12, (short) 1); //planes
        bitmapInfo.setShort(14, (short) 32); //BI_RGB at 32 bpp: BGRA bytes, i.e. ARGB ints
        Pointer created = gdi32.CreateDIBSection(memoryDc, bitmapInfo, 0, bitsRef, null, 0);
        if (created == null) throw new IllegalStateException("CreateDIBSection failed");
        gdi32.SelectObject(memoryDc, created);
        if (bitmap != null) gdi32.DeleteObject(bitmap);
        bitmap = created;
        bits = bitsRef.getValue();
        bitmapWidth = w;
        bitmapHeight = h;
    }

    private boolean fail(String why) {
        if (!failed) LOG.warning("[Icarus] Can't hide the menu from recorders, drawing it in game: " + why);
        failed = true;
        if (window != null && user32.IsWindow(window)) user32.DestroyWindow(window);
        window = null;
        visible = false;
        return false;
    }

    /** The largest visible top-level window of the current thread that isn't ours: the game's. */
    private final class GameWindowFinder implements WindowCallback {
        private Pointer best;
        private long bestArea;

        Pointer find() {
            best = null;
            bestArea = -1;
            user32.EnumThreadWindows(kernel32.GetCurrentThreadId(), this, null);
            return best;
        }

        @Override
        public boolean callback(Pointer hwnd, Pointer param) {
            if (hwnd.equals(window) || !user32.IsWindowVisible(hwnd) || !user32.GetClientRect(hwnd, rect)) return true;
            long area = (long) (rect.getInt(8) - rect.getInt(0)) * (rect.getInt(12) - rect.getInt(4));
            if (area > bestArea) {
                best = hwnd;
                bestArea = area;
            }
            return true;
        }
    }
}
