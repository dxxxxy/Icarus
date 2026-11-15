package studio.dreamys.test;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "testmod") //1.12.2 requires lowercase mod ids
public class TestMod {
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        TestModCommon.init(e.getModMetadata().modId);
    }
}
