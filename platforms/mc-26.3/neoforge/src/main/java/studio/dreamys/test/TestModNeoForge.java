package studio.dreamys.test;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(value = "testmod", dist = Dist.CLIENT)
public class TestModNeoForge {
    public TestModNeoForge(IEventBus modBus) {
        //client setup runs in parallel; hop onto the main thread like Forge's preInit did
        modBus.addListener((FMLClientSetupEvent e) -> e.enqueueWork(() -> TestModCommon.init("testmod")));
    }
}
