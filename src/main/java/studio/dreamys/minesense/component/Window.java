package studio.dreamys.minesense.component;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import studio.dreamys.minesense.util.RenderUtils;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Window extends GuiScreen {
    public double x;
    public double y;
    public double width;
    public double height;
    public Color color;
    public Page active;

    //dragging stuff
    public double dragX;
    public double dragY;
    public boolean isDragging;

    public ArrayList<Component> children = new ArrayList<>();
    public ArrayList<Component> reversedChildren;

    public Window(double width, double height, double x, double y, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    @Override
    public void initGui() {
        //reverse children to draw from end to beginning
        reversedChildren = new ArrayList<>(children);
        Collections.reverse(reversedChildren);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        //draw our very professional skeet background
        mc.getTextureManager().bindTexture(new ResourceLocation("minesense", "bg.png"));
        RenderUtils.drawTexture(x, y, width, height);

        //draw them sexy bottom strings
        RenderUtils.drawString("uid: 001", x + 3, y + height - 7, Color.WHITE);
        RenderUtils.drawCenteredString("Aphrodite", x + width / 2, y + height - 7, 0.5f, Color.WHITE.darker());
        RenderUtils.drawString("dxxxxy#0776", x + width - 3 - RenderUtils.getStringWidth("dxxxxy#0776"), y + height - 7, Color.WHITE.darker());

        update(mouseX, mouseY);

        //draw and update children
        for (Component child : reversedChildren) {
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
            if (child.open()) break; //avoid clicking underlying elements when something is open
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

    private boolean hovered(double x, double y) {
        return x > this.x && x < this.x + width && y > this.y && y < this.y + height / 25; //only 1/25 from the top is draggable
    }

    private void update(int mouseX, int mouseY) {
        if (isDragging) {
            x = mouseX - dragX;
            y = mouseY - dragY;
        }
    }

    public Component addChild(Component child) {
        child.setWindow(this);
        children.add(child);
        return child;
    }

    public void setActive(Page page) {
        active = page;
    }
}
