package studio.dreamys.test;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import studio.dreamys.icarus.Icarus;

@Mod(modid = "Icarus")
public class TestMod {
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        Icarus.init(e.getModMetadata().modId, new TestWindow());
        System.out.println(Icarus.config.getCheckbox("haha"));
        System.out.println(Icarus.config.getCheckbox("Another Checkbox"));
        System.out.println(Icarus.config.getCheckbox("Checkbox"));
    }
}
