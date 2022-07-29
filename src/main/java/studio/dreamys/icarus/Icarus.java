package studio.dreamys.icarus;

import net.minecraftforge.common.MinecraftForge;
import studio.dreamys.icarus.config.Config;
import studio.dreamys.icarus.util.RenderUtils;

public class Icarus {
    public static Config config;

    public static void init(String modid) {
        RenderUtils.loadFonts();
        config = new Config(modid);
        MinecraftForge.EVENT_BUS.register(new Icarus());
    }
}
