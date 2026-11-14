package studio.dreamys.test;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("testmod")
public class TestModForge {
    public TestModForge() {
        //client setup runs in parallel; hop onto the main thread like Forge's preInit did
        FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLClientSetupEvent e) -> e.enqueueWork(() -> TestModCommon.init("testmod")));
    }
}
