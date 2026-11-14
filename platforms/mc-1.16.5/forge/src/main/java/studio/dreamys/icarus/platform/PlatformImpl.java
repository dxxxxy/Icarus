package studio.dreamys.icarus.platform;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import org.lwjgl.glfw.GLFW;
import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.input.Keys;
import studio.dreamys.icarus.platform.mc1165.ModernPlatform;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/** Forge 1.16.5. */
@SuppressWarnings("unused")
public class PlatformImpl extends ModernPlatform {
    @Override
    public void install() {
        MinecraftForge.EVENT_BUS.addListener((InputEvent.KeyInputEvent e) -> {
            if (e.getAction() == GLFW.GLFW_PRESS && Minecraft.getInstance().screen == null) {
                Icarus.onKeyPressed(Keys.fromGlfw(e.getKey()));
            }
        });
        MinecraftForge.EVENT_BUS.addListener((RenderGameOverlayEvent.Text e) -> {
            renderer.begin(e.getMatrixStack());
            Icarus.renderHud();
        });
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent e) -> {
            if (e.phase == TickEvent.Phase.START) Icarus.clientTick();
        });
    }

    @Override
    public Collection<Path> getModRoots(String modid) {
        ModFileInfo file = ModList.get().getModFileById(modid);
        if (file == null) throw new IllegalArgumentException("No mod with id " + modid);

        Path path = file.getFile().getFilePath();
        if (!Files.isDirectory(path)) return Collections.singletonList(path); //a mod jar

        //dev: an exploded mod spans several directories (classes, resources), listed in MOD_CLASSES as
        //"group%%path" entries; the mod file only reports the first one
        String modClasses = System.getenv("MOD_CLASSES");
        if (modClasses == null) return Collections.singletonList(path);

        List<String[]> entries = new ArrayList<>();
        String group = null;
        for (String entry : modClasses.split(File.pathSeparator)) {
            int sep = entry.indexOf("%%");
            String[] parsed = sep < 0 ? new String[]{"", entry} : new String[]{entry.substring(0, sep), entry.substring(sep + 2)};
            entries.add(parsed);
            if (Paths.get(parsed[1]).toAbsolutePath().normalize().equals(path.toAbsolutePath().normalize())) group = parsed[0];
        }
        if (group == null) return Collections.singletonList(path);

        List<Path> roots = new ArrayList<>();
        for (String[] entry : entries) {
            if (entry[0].equals(group)) roots.add(Paths.get(entry[1]));
        }
        return roots;
    }

    @Override
    public ClassLoader getModClassLoader(String modid) {
        return PlatformImpl.class.getClassLoader(); //every mod shares FML's transforming class loader
    }
}
