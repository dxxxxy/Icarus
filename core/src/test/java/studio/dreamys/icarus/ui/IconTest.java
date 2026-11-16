package studio.dreamys.icarus.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.IcarusOptions;
import studio.dreamys.icarus.annotation.type.IGroup;
import studio.dreamys.icarus.annotation.type.IPage;
import studio.dreamys.icarus.annotation.type.ISubPage;
import studio.dreamys.icarus.render.AtlasImage;
import studio.dreamys.icarus.render.Images;
import studio.dreamys.icarus.support.FakePlatform;
import studio.dreamys.icarus.theme.Theme;
import studio.dreamys.icarus.theme.fatality.FatalityStyleTheme;
import studio.dreamys.icarus.theme.skeet.SkeetStyleTheme;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IconTest {
    private static final String DOT = "/assets/icarustest/dot.png"; //3x2, in the test resources

    @TempDir
    Path game;

    private FakePlatform platform;

    @IPage(icon = DOT, order = 0)
    public static class Custom {
        @ISubPage(order = 0, icon = DOT)
        public static class First {
            @IGroup(column = 0)
            public static class Group {
                public static boolean A;
            }
        }

        @ISubPage(order = 1, icon = "pistol")
        public static class Second {
            @IGroup(column = 0)
            public static class Group {
                public static boolean B;
            }
        }

        @ISubPage(order = 2)
        public static class Third {
            @IGroup(column = 0)
            public static class Group {
                public static boolean C;
            }
        }
    }

    @IPage(icon = "/assets/icarustest/missing.png", order = 1)
    public static class Missing {
        @IGroup(column = 0)
        public static class Group {
            public static boolean D;
        }
    }

    /** Exposes the theme's icon lookup. */
    static final class Skeet extends SkeetStyleTheme {
        AtlasImage show(Icon icon) {
            return image(icon);
        }
    }

    static final class Fatality extends FatalityStyleTheme {
        AtlasImage show(Icon icon) {
            return image(icon);
        }
    }

    @BeforeEach
    void setUp() {
        platform = new FakePlatform(game.toFile());
        Icarus.setPlatform(platform);
    }

    @AfterEach
    void tearDown() {
        Icarus.setPlatform(null);
    }

    private Menu menu(Theme theme) {
        return Icarus.createDetached("test", theme, IcarusOptions.defaults(), Arrays.asList(Custom.class, Missing.class),
                game.resolve("config/test"), game.resolve("test")).getMenu();
    }

    @Test
    void resourceIconsLoadFromTheModsOwnJar() {
        Menu menu = menu(new Skeet());
        Icon tab = menu.getTabs().get(0).getIcon();
        assertTrue(tab.isResource());
        assertEquals(3, tab.getImage().getWidth());
        assertEquals(2, tab.getImage().getHeight());

        Icon first = menu.getTabs().get(0).getSubTabs().get(0).getIcon();
        assertNotNull(first.getImage(), "sub-tab icons load the same way");
        Icon second = menu.getTabs().get(0).getSubTabs().get(1).getIcon();
        assertFalse(second.isResource());
        assertNull(second.getImage(), "a built-in name is the theme's to draw");
        assertTrue(menu.getTabs().get(0).getSubTabs().get(2).getIcon().isEmpty());

        assertNull(menu.getTabs().get(1).getIcon().getImage(), "a missing file is no icon, not an error");
    }

    @Test
    void themesDrawResourcesAndTheirOwnBuiltins() {
        Skeet skeet = new Skeet();
        Menu menu = menu(skeet);
        TabView custom = menu.getTabs().get(0);
        assertEquals(3, skeet.show(custom.getIcon()).getWidth());
        AtlasImage pistol = skeet.show(custom.getSubTabs().get(1).getIcon());
        assertEquals(90, pistol.getWidth(), "skeet's pistol cell");
        assertNull(skeet.show(custom.getSubTabs().get(2).getIcon()));
        assertNull(skeet.show(menu.getTabs().get(1).getIcon()));

        TabView config = menu.getTabs().get(menu.getTabs().size() - 1);
        assertNull(config.getPage(), "the config tab");
        assertNotNull(skeet.show(config.getIcon()), "uses skeet's config icon");

        Fatality fatality = new Fatality();
        TabView custom2 = menu(fatality).getTabs().get(0);
        assertEquals(75, fatality.show(custom2.getSubTabs().get(1).getIcon()).getWidth(), "fatality's own pistol slot");
        assertNull(fatality.show(new Icon("sniper", null, null)), "skeet's names aren't fatality's");
    }

    @Test
    void modRootsAreSearchedWhenTheClassLoaderCantSeeTheFile() throws Exception {
        byte[] png;
        try (InputStream in = IconTest.class.getResourceAsStream(DOT)) {
            png = readAll(in);
        }
        Path dir = game.resolve("dev-resources");
        Files.createDirectories(dir.resolve("assets/other"));
        Files.write(dir.resolve("assets/other/in-dir.png"), png);
        Path jar = game.resolve("other.jar");
        try (OutputStream out = Files.newOutputStream(jar); ZipOutputStream zip = new ZipOutputStream(out)) {
            zip.putNextEntry(new ZipEntry("assets/other/in-jar.png"));
            zip.write(png);
            zip.closeEntry();
        }
        platform.modRoots.add(dir);
        platform.modRoots.add(jar);

        assertNotNull(Images.fromMod("other", IconTest.class, "/assets/other/in-dir.png"), "a directory root (dev)");
        assertNotNull(Images.fromMod("other", IconTest.class, "/assets/other/in-jar.png"), "a jar root");
        assertNull(Images.fromMod("other", IconTest.class, "/assets/other/nowhere.png"));
    }

    private static byte[] readAll(InputStream in) throws java.io.IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        for (int n; (n = in.read(buffer)) > 0; ) out.write(buffer, 0, n);
        return out.toByteArray();
    }
}
