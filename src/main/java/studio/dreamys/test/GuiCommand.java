package studio.dreamys.test;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import studio.dreamys.icarus.component.Page;
import studio.dreamys.icarus.component.Window;
import studio.dreamys.icarus.component.sub.*;
import studio.dreamys.icarus.util.RenderUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class GuiCommand extends CommandBase {
    public Window window;

    @Override
    public String getCommandName() {
        return "icarusgui";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        RenderUtils.loadFonts();

        double width = res.getScaledWidth_double();
        double height = res.getScaledHeight_double();

        window = new Window(731 / 2.0, 617 / 2.0, width / 4, height / 4, new java.awt.Color(29, 122, 215));

        window.addPage(new Page('t'));
        window.addPage(new Page('u'));
        window.addPage(new Page('v'));

        Page page = window.addPage(new Page('s'));
        page.addGroup(new Group("Visuals", 47.5, 10))
                .addChild(new Checkbox("Checkbox"))
                .addChild(new Choice("Choice", new ArrayList<>(Arrays.asList("Option 1", "Option 2", "Option 3"))))
                .addChild(new Checkbox("Another Checkbox"))
                .addChild(new Combo("Combo", new ArrayList<>(Arrays.asList("Option 1", "Option 2", "Option 3"))))
                .addChild(new Button("Button", () -> {
                    System.out.println("Runnable");
                }))
                .addChild(new Button("Another Button", () -> {
                    System.out.println("Another Runnable");
                }))
                .addChild(new Choice("Choice", new ArrayList<>(Arrays.asList("Option 1", "Option 2", "Option 3"))))
                .addChild(new Choice("Another Choice", new ArrayList<>(Arrays.asList("Option 1", "Option 2", "Option 3"))))
                .addChild(new Combo("Combo", new ArrayList<>(Arrays.asList("Option 1", "Option 2", "Option 3"))))
                .addChild(new Combo("Another Combo", new ArrayList<>(Arrays.asList("Option 1", "Option 2", "Option 3"))))
                .addChild(new Field("Field"))
                .addChild(new Slider("Decimal slider", 10, 1, false))
                .addChild(new Slider("Int slider", 10, 1, true))
        ;

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        Minecraft.getMinecraft().displayGuiScreen(window);
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
