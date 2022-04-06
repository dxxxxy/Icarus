package studio.dreamys;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import studio.dreamys.gui.component.Window;
import studio.dreamys.gui.component.sub.Checkbox;
import studio.dreamys.gui.component.sub.Choice;
import studio.dreamys.gui.component.sub.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

public class ExampleCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "minesense";
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

        Window window = new Window(731 / 2.0, 617 / 2.0, width / 4, height / 4, new Color(20, 20, 20));
        window.addChild(new Checkbox(window, 5, 5, 60, 20, new Color(29, 122, 215), "Checkbox"));
        window.addChild(new Slider(window, 80, 3, 60, 40, new Color(29, 122, 215), "Slider", 10, 1, false));
        window.addChild(new Combo(window, 80, 10, 60, 60, new Color(29, 122, 215), "Combo", new ArrayList<>(Arrays.asList("Option 1", "Option 2", "Option 3"))));
        window.addChild(new Choice(window, 80, 10, 60, 80, new Color(29, 122, 215), "Choice", new ArrayList<>(Arrays.asList("Option 1", "Option 2", "Option 3"))));
        window.addChild(new Field(window, 80, 10, 60, 100, "Field"));
        window.addChild(new Group(window, 150, 200, 50, 10, "Group"));

        new Thread(() -> {
            try {
                Thread.sleep(20);
                Minecraft.getMinecraft().displayGuiScreen(window);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
