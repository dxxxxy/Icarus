package studio.dreamys.gui.component.sub;

import studio.dreamys.gui.component.Window;
import studio.dreamys.gui.util.RenderUtils;

import java.awt.Color;

public class Group {
    private Window window;
    private double width;
    private double height;
    private double x;
    private double y;
    private String label;

    //relative to window, aka x,y passed in constructor
    private double relativeX;
    private double relativeY;

    public Group(Window window, double width, double height, double x, double y, String label) {
        this.window = window;
        this.width = width;
        this.height = height;
        this.x = window.x + x;
        this.y = window.y + y;
        relativeX = x;
        relativeY = y;
        this.label = label;
    }

    public void render() {
        //update position
        x = window.x + relativeX;
        y = window.y + relativeY;

        //the box itself
        RenderUtils.drawGroupWithString(width, height, x, y, label);
    }
}
