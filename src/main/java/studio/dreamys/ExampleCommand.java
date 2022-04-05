package studio.dreamys;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import studio.dreamys.gui.component.sub.Checkbox;
import studio.dreamys.gui.component.Window;

import java.awt.Color;

public class ExampleCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "config";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());

        double width = res.getScaledWidth_double();
        double height = res.getScaledHeight_double();


        Window window = new Window(731 / 2.0, 617 / 2.0, width / 4, height / 4, new Color(20, 20, 20));
        window.addChild(new Checkbox(window, 5, 5, 60, 20, new Color(29, 122, 215), "Stonk Only"));
        ExampleMod.gui = window;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
