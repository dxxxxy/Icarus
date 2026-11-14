package studio.dreamys.icarus;

import lombok.Getter;
import studio.dreamys.icarus.component.Component;
import studio.dreamys.icarus.component.Window;
import studio.dreamys.icarus.config.Config;
import studio.dreamys.icarus.extra.HudElement;
import studio.dreamys.icarus.handler.KeybindHandler;
import studio.dreamys.icarus.platform.Platform;
import studio.dreamys.icarus.util.RenderUtils;
import studio.dreamys.icarus.util.font.GlyphPageFontRenderer;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.IntConsumer;

@SuppressWarnings("unused")
public class Icarus {
    @Getter private static Window window;
    @Getter private static String modid;

    private static Platform platform;
    private static boolean installed;

    private static final List<HudElement> hudElements = new CopyOnWriteArrayList<>();
    private static final List<IntConsumer> keyListeners = new CopyOnWriteArrayList<>();

    public static void init(String modid, Window window) {
        RenderUtils.loadFonts();

        Icarus.window = window;
        Icarus.modid = modid;

        if (!installed) {
            getPlatform().install();
            installed = true;
        }

        Config.init(modid);
    }

    /** The implementation for the running Minecraft version / mod loader. */
    public static Platform getPlatform() {
        if (platform == null) {
            try {
                platform = (Platform) Class.forName(Platform.IMPLEMENTATION, true, Icarus.class.getClassLoader()).getConstructor().newInstance();
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("No Icarus platform found (" + Platform.IMPLEMENTATION + "). Make sure the Icarus build for your Minecraft version and mod loader is on the classpath.", e);
            }
        }
        return platform;
    }

    /** Lets a platform be supplied explicitly instead of looked up. */
    public static void setPlatform(Platform platform) {
        Icarus.platform = platform;
    }

    public static void provideTextFont(InputStream fontStream, String fontName, int fontSize, boolean bold, boolean italic, boolean boldItalic) {
        try {
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(Font.createFont(Font.TRUETYPE_FONT, fontStream));
            RenderUtils.textRenderer = GlyphPageFontRenderer.create(fontName, fontSize, bold, italic, boldItalic);
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void provideTitleFont(InputStream fontStream, String fontName, int fontSize, boolean bold, boolean italic, boolean boldItalic) {
        try {
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(Font.createFont(Font.TRUETYPE_FONT, fontStream));
            RenderUtils.titleRenderer = GlyphPageFontRenderer.create(fontName, fontSize, bold, italic, boldItalic);
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void provideIconFont(InputStream fontStream, String fontName, int fontSize, boolean bold, boolean italic, boolean boldItalic) {
        try {
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(Font.createFont(Font.TRUETYPE_FONT, fontStream));
            RenderUtils.iconRenderer = GlyphPageFontRenderer.create(fontName, fontSize, bold, italic, boldItalic);
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void provideComponent(Class<? extends Component> newComponent, Class<? extends Component> oldComponent) {
        Config.componentReplacements.put(oldComponent, newComponent);
    }

    /** Listens for key presses while in game with no screen open (same events Icarus keybinds react to). */
    public static void addKeyListener(IntConsumer listener) {
        keyListeners.add(listener);
    }

    public static void registerHud(HudElement element) {
        hudElements.add(element);
    }

    public static void unregisterHud(HudElement element) {
        hudElements.remove(element);
    }

    /* ---- called by the platform ---- */

    /** A key was pressed in game while no screen was open. {@code key} is a {@link studio.dreamys.icarus.input.Keys} code. */
    public static void onKeyPressed(int key) {
        if (window != null) KeybindHandler.onKeyPressed(key);

        if (!getPlatform().isInGame() || key <= 0) return;
        for (IntConsumer listener : keyListeners) {
            listener.accept(key);
        }
    }

    /** The in-game HUD text layer is being drawn. */
    public static void renderHud() {
        for (HudElement element : hudElements) {
            element.renderHud();
        }
    }

    /** Start of a client tick. */
    public static void clientTick() {
        for (HudElement element : hudElements) {
            element.clientTick();
        }
    }
}
