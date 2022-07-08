package studio.dreamys.test;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import studio.dreamys.minesense.component.Page;
import studio.dreamys.minesense.component.Window;
import studio.dreamys.minesense.component.sub.*;
import studio.dreamys.minesense.util.RenderUtils;

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
        RenderUtils.loadFonts();

        double width = res.getScaledWidth_double();
        double height = res.getScaledHeight_double();

        window = new Window(731 / 2.0, 617 / 2.0, width / 4, height / 4, new java.awt.Color(29, 122, 215));

        window.addChild(new Page('t'));
        window.addChild(new Page('u'));

        Page page = (Page) window.addChild(new Page('v'));
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


//        window.addChild(new Page('v')
//                .addGroup(new Group("Visuals")
//                        .addChild(new Checkbox("Box"))
//
//                ));


//        window.addChild(new Checkbox(5, 5, 60, 20, "Checkbox")).attach(new Keybind());
//
//
//        window.addChild(new Slider(80, 3, 60, 40, "Decimal slider", 10, 1, false)).attach(new Color());
//        window.addChild(new Combo(80, 10, 60, 60, "Combo", new ArrayList<>(Arrays.asList("Option 1", "Option 2", "Option 3")))).attach(new Color());
//        window.addChild(new Choice(80, 10, 60, 80, "Choice", new ArrayList<>(Arrays.asList("Option 1", "Option 2", "Option 3"))));
//        window.addChild(new Field(80, 10, 60, 100, "Field"));
//        window.addChild(new Button(80, 12, 60, 120, "Button", () -> System.out.println("You clicked this button")));
//        window.addChild(new Slider(80, 3, 60, 145, "Int slider", 10, 1, true));
//
//        window.addChild(new Group(150, 200, 47.5, 10, "Group 1"));
//        window.addChild(new Group(150, 200, 205, 10, "Group 2"));

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
