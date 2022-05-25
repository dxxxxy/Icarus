package studio.dreamys.test;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import studio.dreamys.minesense.component.Window;
import studio.dreamys.minesense.component.sub.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

public class GuiCommand extends CommandBase {
    public Window window;

    @Override
    public String getCommandName() {
        return "gui";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());

        double width = res.getScaledWidth_double();
        double height = res.getScaledHeight_double();

        window = new Window(731 / 2.0, 617 / 2.0, width / 4, height / 4, new Color(29, 122, 215));
        window.addChild(new Checkbox(5, 5, 60, 20, "Checkbox"));
        window.addChild(new Slider(80, 3, 60, 40, "Slider", 10, 1, false));
        window.addChild(new Combo(80, 10, 60, 60, "Combo", new ArrayList<>(Arrays.asList("Option 1", "Option 2", "Option 3"))));
        window.addChild(new Choice(80, 10, 60, 80, "Choice", new ArrayList<>(Arrays.asList("Option 1", "Option 2", "Option 3"))));
        window.addChild(new Field(80, 10, 60, 100, "Field"));
        window.addChild(new Button(80, 12, 60, 120, "Crash server", () -> System.out.println("yo")));
        window.addChild(new Slider(80, 3, 60, 140, "Slider", 10, 1, true));

        window.addChild(new Group(150, 200, 50, 10, "Group 1"));

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
