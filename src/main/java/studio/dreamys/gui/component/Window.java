package studio.dreamys.gui.component;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import studio.dreamys.gui.util.RenderUtils;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Window extends GuiScreen {
    public double width;
    public double height;
    public double x;
    public double y;
    public Color color;

    //dragging stuff
    public double dragX;
    public double dragY;
    public boolean isDragging;

    public ArrayList<Component> children = new ArrayList<>();

    public Window(double width, double height, double x, double y, Color color) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.color = color;
    }

    @Override
    public void initGui() {
        Collections.reverse(children);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        //draw our professional skeet background
        mc.getTextureManager().bindTexture(new ResourceLocation("etsy", "skeet bg.png"));
        Gui.drawModalRectWithCustomSizedTexture((int) x, (int)y, 0, 0, (int) width, (int) height, (int) width, (int) height);

        //draw them sexy bottom strings
        RenderUtils.drawScaledString("uid: 001", (int) x + 3, (int) (y + height - 7), 0.5f, Color.WHITE.darker());
        RenderUtils.drawScaledCenteredString("Aphrodite", (int) (x + (width / 2)), (int) (y + height - 7), 0.5f, Color.WHITE.darker());
        RenderUtils.drawScaledString("dxxxxy#0776", (int) ((int) x + width - 3 - RenderUtils.getScaledStringWidth("dxxxxy#0776", 0.5f)), (int) (y + height - 7), 0.5f, Color.WHITE.darker());

        update(mouseX, mouseY);

        //draw and update children
        for (Component child : children) {
            child.render(mouseX, mouseY);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (hovered(mouseX, mouseY) && mouseButton == 0) {
            isDragging = true;
            dragX = mouseX - x;
            dragY = mouseY - y;
        }

        //call for children
        for (Component child : children) {
            child.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        //call for children
        for (Component child : children) {
            child.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        isDragging = false;

        //call for children
        for (Component child : children) {
            child.mouseReleased(mouseX, mouseY, state);
        }
    }

    public boolean hovered(double x, double y) {
        return x > this.x && x < this.x + width && y > this.y && y < this.y + height / 25; //only 1/25 from the top is draggable
    }

    public void update(int mouseX, int mouseY) {
        if (isDragging) {
            x = mouseX - dragX;
            y = mouseY - dragY;
        }
    }

    public void addChild(Component child) {
        children.add(child);
    }
}
