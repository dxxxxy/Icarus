package studio.dreamys;

import gg.essential.elementa.effects.StencilEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
//import studio.dreamys.libSkyblock.item.SBItem;

@Mod(modid = ExampleMod.MODID, version = ExampleMod.VERSION)
public class ExampleMod {
    public static final String MODID = "etsy";
    public static final String VERSION = "1.0";
    public static GuiScreen gui;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
		// some example code
        System.out.println("DIRT BLOCK >> " + Blocks.dirt.getUnlocalizedName());
        StencilEffect.enableStencil();
        ClientCommandHandler.instance.registerCommand(new ExampleCommand());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (gui != null) {
            Minecraft.getMinecraft().displayGuiScreen(gui);
            System.out.println("test");
            gui = null;
        }
    }
}
