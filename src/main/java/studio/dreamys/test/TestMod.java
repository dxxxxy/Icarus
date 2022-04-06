package studio.dreamys.test;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "")
public class TestMod {
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        ClientCommandHandler.instance.registerCommand(new GuiCommand());
    }
}
