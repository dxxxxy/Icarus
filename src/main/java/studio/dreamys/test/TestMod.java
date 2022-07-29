package studio.dreamys.test;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import studio.dreamys.icarus.Icarus;

@Mod(modid = "icarus")
public class TestMod {
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        Icarus.init(e.getModMetadata().modId);
        ClientCommandHandler.instance.registerCommand(new GuiCommand());
    }
}
