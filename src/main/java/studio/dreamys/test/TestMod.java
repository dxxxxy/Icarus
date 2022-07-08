package studio.dreamys.test;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import studio.dreamys.minesense.Minesense;

@Mod(modid = "")
public class TestMod {
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        Minesense.init();
        ClientCommandHandler.instance.registerCommand(new GuiCommand());
    }
}
