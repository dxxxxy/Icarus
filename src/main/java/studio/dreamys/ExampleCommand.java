package studio.dreamys;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import studio.dreamys.gui.component.sub.Checkbox;
import studio.dreamys.gui.component.Window;
import studio.dreamys.gui.component.sub.Choice;
import studio.dreamys.gui.component.sub.Combo;
import studio.dreamys.gui.component.sub.Field;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

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
        window.addChild(new Choice(window, 80, 10, 60, 80, new Color(29, 122, 215), "Mode", new ArrayList<>(Arrays.asList("nuke", "legit", "auto"))));
        window.addChild(new Combo(window, 80, 10, 60, 60, new Color(29, 122, 215), "Failsafes", new ArrayList<>(Arrays.asList("warp if admin", "warp if in range", "warp if in front"))));
        window.addChild(new Field(window, 80, 10, 60, 100, "Nick"));
        ExampleMod.gui = window;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
