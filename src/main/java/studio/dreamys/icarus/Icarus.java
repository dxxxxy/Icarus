package studio.dreamys.icarus;

import lombok.Getter;
import net.minecraftforge.common.MinecraftForge;
import studio.dreamys.icarus.component.Window;
import studio.dreamys.icarus.config.Config;
import studio.dreamys.icarus.handler.KeybindHandler;
import studio.dreamys.icarus.component.Component;
import studio.dreamys.icarus.util.RenderUtils;
import studio.dreamys.icarus.util.font.GlyphPageFontRenderer;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

@SuppressWarnings("unused")
public class Icarus {
    @Getter private static Window window;
    @Getter private static String modid;

    public static void init(String modid, Window window) {
        RenderUtils.loadFonts();

        Icarus.window = window;
        Icarus.modid = modid;

        MinecraftForge.EVENT_BUS.register(new KeybindHandler());

        Config.init(modid);
    }

    public static void provideTextFont(InputStream fontStream, String fontName, int fontSize, boolean bold, boolean italic, boolean boldItalic) {
        try {
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(Font.createFont(Font.TRUETYPE_FONT, fontStream));
            RenderUtils.textRenderer = GlyphPageFontRenderer.create(fontName, fontSize, bold, italic, boldItalic);
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

    public static void provideTitleFont(InputStream fontStream, String fontName, int fontSize, boolean bold, boolean italic, boolean boldItalic) {
        try {
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(Font.createFont(Font.TRUETYPE_FONT, fontStream));
            RenderUtils.titleRenderer = GlyphPageFontRenderer.create(fontName, fontSize, bold, italic, boldItalic);
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void provideComponent(Class<? extends Component> newComponent, Class<? extends Component> oldComponent) {
        Config.componentReplacements.put(oldComponent, newComponent);
    }
}
