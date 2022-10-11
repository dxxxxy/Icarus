package studio.dreamys.icarus.util;

import net.minecraft.client.Minecraft;

public enum Placement {
    TOP_LEFT,
    TOP_CENTER,
    TOP_RIGHT,

    CENTER_LEFT,
    CENTER,
    CENTER_RIGHT,

    BOTTOM_LEFT,
    BOTTOM_CENTER,
    BOTTOM_RIGHT;

    private double x, y, width, height;

    public void init(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public double getX() {
        switch (this) {
            case TOP_LEFT:
            case BOTTOM_LEFT:
            case CENTER_LEFT:
                x = 5;
                break;
            case TOP_CENTER:
            case BOTTOM_CENTER:
            case CENTER:
                x = Minecraft.getMinecraft().displayWidth / 2.0 - width;
                break;
            case TOP_RIGHT:
            case BOTTOM_RIGHT:
            case CENTER_RIGHT:
                x = Minecraft.getMinecraft().displayWidth - width * 2 - 5;
                break;
        }

        return x / 2;
    }

    public double getY() {
        switch (this) {
            case TOP_LEFT:
            case TOP_RIGHT:
            case TOP_CENTER:
                y = 5;
                break;
            case CENTER_LEFT:
            case CENTER_RIGHT:
            case CENTER:
                y = Minecraft.getMinecraft().displayHeight / 2.0 - height;
                break;
            case BOTTOM_LEFT:
            case BOTTOM_RIGHT:
            case BOTTOM_CENTER:
                y = Minecraft.getMinecraft().displayHeight - height * 2 - 5;
                break;
        }

        return y / 2;
    }
}
