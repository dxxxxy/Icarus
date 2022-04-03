package studio.dreamys.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import studio.dreamys.gui.util.RenderUtils;

import java.awt.Color;

public class Window {
    public double width;
    public double height;
    public double x;
    public double y;
    public Color color;

    //dragging stuff
    public double dragX;
    public double dragY;
    public boolean isDragging;

    public Window(double width, double height, double x, double y, Color color) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public void render(int mouseX, int mouseY) {
        //draw our professional skeet background
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("etsy", "skeet bg.png"));
        Gui.drawModalRectWithCustomSizedTexture((int) x, (int)y, 0, 0, (int) width, (int) height, (int) width, (int) height);

        //draw them sexy bottom strings
        RenderUtils.drawScaledString("uid: 001", (int) x + 3, (int) (y + height - 7), 0.5f, Color.WHITE.darker());
        RenderUtils.drawScaledCenteredString("Aphrodite", (int) (x + (width / 2)), (int) (y + height - 7), 0.5f, Color.WHITE.darker());
        RenderUtils.drawScaledString("dxxxxy#0776", (int) ((int) x + width - 3 - RenderUtils.getScaledStringWidth("dxxxxy#0776", 0.5f)), (int) (y + height - 7), 0.5f, Color.WHITE.darker());

        update(mouseX, mouseY);
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

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Color getColor() {
        return color;
    }

    public double getDragX() {
        return dragX;
    }

    public double getDragY() {
        return dragY;
    }

    public boolean isDragging() {
        return isDragging;
    }
}
