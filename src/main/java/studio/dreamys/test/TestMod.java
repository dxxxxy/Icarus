package studio.dreamys.test;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.event.ComponentStateChangeEvent;

@Mod(modid = "TestMod")
public class TestMod {
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        Icarus.init(e.getModMetadata().modId, new TestWindow());
        MinecraftForge.EVENT_BUS.register(this);
        System.out.println(Icarus.getConfig().getCheckbox("Visuals", "haha"));
        System.out.println(Icarus.getConfig().getCheckbox("Visuals", "Another Checkbox"));
        System.out.println(Icarus.getConfig().getCheckbox("Visuals", "Checkbox"));
    }

    @SubscribeEvent
    public void onComponentStateChange(ComponentStateChangeEvent e) {
        System.out.println(e.component);
    }
}
