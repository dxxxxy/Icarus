package studio.dreamys.icarus.platform;

import net.minecraft.client.Minecraft;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforgespi.language.IModFileInfo;
import com.mojang.blaze3d.platform.InputConstants;
import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.input.Keys;
import studio.dreamys.icarus.platform.mc263.ModernPlatform;

import java.nio.file.Path;
import java.util.Collection;

/** NeoForge 26.3. */
@SuppressWarnings("unused")
public class PlatformImpl extends ModernPlatform {
    @Override
    public void install() {
        NeoForge.EVENT_BUS.addListener((InputEvent.Key e) -> {
            if (e.getAction() == InputConstants.PRESS && Minecraft.getInstance().gui.screen() == null) {
                Icarus.onKeyPressed(Keys.fromSdl(e.getKey()));
            }
        });
        NeoForge.EVENT_BUS.addListener((RenderGuiEvent.Post e) -> {
            renderer.begin(e.getGuiGraphics());
            Icarus.renderHud();
        });
        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Pre e) -> Icarus.clientTick());
    }

    @Override
    public Collection<Path> getModRoots(String modid) {
        IModFileInfo file = ModList.get().getModFileById(modid);
        if (file == null) throw new IllegalArgumentException("No mod with id " + modid);
        return file.getFile().getContents().getContentRoots(); //jar files or, in dev, class/resource directories
    }

    @Override
    public ClassLoader getModClassLoader(String modid) {
        return PlatformImpl.class.getClassLoader(); //every game-layer mod shares FML's transforming class loader
    }
}
