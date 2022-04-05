package studio.dreamys.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import studio.dreamys.gui.component.*;
import studio.dreamys.gui.component.sub.*;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ClickGUI extends GuiScreen {
    public Window window;
    public FontRenderer fr;
    public ArrayList<String> categories;
    public Checkbox checkbox;
    public Slider slider;
    public Combo combo;
    public Choice choice;
    public Field field;
    public Group group;

    public void initGui() {
        ScaledResolution res = new ScaledResolution(mc);

        double width = res.getScaledWidth_double();
        double height = res.getScaledHeight_double();

        categories = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6"));

        window = new Window(731 / 2.0, 617 / 2.0, width / 4, height / 4, new Color(20, 20, 20));
        fr = fontRendererObj;
        checkbox = new Checkbox(window, 5, 5, 60, 20, new Color(29, 122, 215), "Stonk Only");
//        slider = new Slider(window, 80, 3, 60, 40, new Color(29, 122, 215), "Distance", 10, 1, false);
//        combo = new Combo(window, 80, 10, 60, 60, new Color(29, 122, 215), "Failsafes", new ArrayList<>(Arrays.asList("warp if admin", "warp if in range", "warp if in front")));
//        choice = new Choice(window, 80, 10, 60, 80, new Color(29, 122, 215), "Mode", new ArrayList<>(Arrays.asList("nuke", "legit", "auto")));
//        field = new Field(window, 80, 10, 60, 100, "Nick");
//        group = new Group(window, 150, 200, 50, 10, "Group 1");
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

//        window.render(mouseX, mouseY);
//        group.render();
//
//        checkbox.render();
//        slider.render(mouseX, mouseY);
//
//        field.render();
//        //we render combo after because it comes first and we want to overlay over the choice
//        choice.render();
//        combo.render();
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (window.hovered(mouseX, mouseY) && mouseButton == 0) {
            window.isDragging = true;
            window.dragX = mouseX - window.x;
            window.dragY = mouseY - window.y;
        }

//        //resolve clicks for opened combo
//        if (combo.isOpened() && mouseButton == 0) {
//            if (mouseX > combo.getX() && mouseX < combo.getX() + combo.getWidth() && mouseY > combo.getY() + combo.getHeight() && mouseY < combo.getY() + combo.getHeight() * (combo.getOptions().size() + 1)) {
//                double posY = mouseY - combo.getY() - combo.getHeight();
//                int index = (int) (posY / combo.getHeight());
//                int i = 0;
//                for (Map.Entry<String, Boolean> entry : combo.getOptions().entrySet()) {
//                    if (i == index) {
//                        combo.getOptions().put(entry.getKey(), !entry.getValue());
//                    }
//                    i++;
//                }
//                return; //prevent overlaying clicks
//            }
//        }
//
//        //resolve clicks for opened choice
//        if (choice.isOpened() && mouseButton == 0) {
//            if (mouseX > choice.getX() && mouseX < choice.getX() + choice.getWidth() && mouseY > choice.getY() + choice.getHeight() && mouseY < choice.getY() + choice.getHeight() * (choice.getOptions().size() + 1)) {
//                double posY = mouseY - choice.getY() - choice.getHeight();
//                int index = (int) (posY / choice.getHeight());
//                choice.setSelected(choice.getOptions().get(index));
//                return; //prevent overlaying clicks
//            }
//        }
//
//        if (checkbox.hovered(mouseX, mouseY) && mouseButton == 0) {
//            checkbox.toggle();
//        }
//
//        if (slider.hovered(mouseX, mouseY) && mouseButton == 0) {
//            slider.isDragging = true;
//        }
//
//        if (combo.hovered(mouseX, mouseY) && mouseButton == 0) {
//            combo.open();
//        }
//
//        if (choice.hovered(mouseX, mouseY) && mouseButton == 0) {
//            choice.open();
//        }
//
//        field.setFocused(field.hovered(mouseX, mouseY) && mouseButton == 0);
    }

    public void keyTyped(char typedChar, int keyCode) throws IOException {
//        super.keyTyped(typedChar, keyCode);
//
//        field.keyTyped(typedChar, keyCode);
    }

    protected void mouseReleased(int mouseX, int mouseY, int state) {
        window.isDragging = false;
//        slider.isDragging = false;
    }
}
