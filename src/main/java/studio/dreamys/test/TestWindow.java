package studio.dreamys.test;

import net.minecraft.client.Minecraft;
import studio.dreamys.icarus.component.Page;
import studio.dreamys.icarus.component.Window;
import studio.dreamys.icarus.component.sub.Button;
import studio.dreamys.icarus.component.sub.Checkbox;
import studio.dreamys.icarus.component.sub.Choice;
import studio.dreamys.icarus.component.sub.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

public class TestWindow extends Window {
    public TestWindow() {
        //x, y, width, height, accent color
        super(Minecraft.getMinecraft().displayWidth / 2.0, Minecraft.getMinecraft().displayHeight / 4.0, 731 / 2.0, 617 / 2.0, new Color(29, 122, 215));

        /*  BUILDING EXAMPLES   */

        //1. Empty pages
        addPage(new Page('t'));
        addPage(new Page('u'));

        //2. Variable and chain
        Page page = addPage(new Page('v'));
        page.addGroup(new Group("Visuals", 47.5, 10))
                .addChild(new Checkbox("haha"))
                .addChild(new Choice("hihi", new ArrayList<>(Arrays.asList("Option 1", "Option 2", "Option 3"))))
                .addChild(new Checkbox("hoho"));
        
        //3. Chaining Mania
        addPage(new Page('s'))
                .addGroup(new Group("Visuals", 47.5, 10))
                    .addChild(new Checkbox("Checkbox"), new Keybind())
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
                    .addChild(new Slider("Decimal slider", 1, 10, false))
                    .addChild(new Slider("Int slider", 10, 1, true))
        ;

        /*  ADDING COMPONENTS   */
        //add all components
        for (Page p : pages) {
            for (Group g : p.getGroups()) {
                all.add(g);
                all.addAll(g.getChildren());
            }
        }
    }
}
