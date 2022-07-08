package studio.dreamys.minesense.component;

import com.google.common.collect.Lists;
import studio.dreamys.minesense.component.sub.Group;
import studio.dreamys.minesense.util.RenderUtils;

import java.awt.*;
import java.util.ArrayList;

public class Page extends Component {
    private Window window;

    private double x = 3;
    private double y = 4;
    private double width = 37.5;
    private double height = 35;

    //relative to window, aka x,y passed in constructor
    private double relativeX;
    private double relativeY;

    private char icon;
    private ArrayList<Group> groups = new ArrayList<>();
    private boolean active;

    public Page(char icon) {
        this.icon = icon;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        //update position
        x = window.x + relativeX;
        y = window.y + relativeY;

        Color color = active ? window.color : Color.DARK_GRAY;

        RenderUtils.drawOutline(width, height, x, y, color);
        RenderUtils.drawIcon(icon, x + 5, y + 5, color);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (hovered(mouseX, mouseY) && mouseButton == 0) {
            active = !active;
            window.setActive(this);
        }
    }

    private boolean hovered(double x, double y) {
        return x > this.x && x < this.x + width && y > this.y && y < this.y + height;
    }

    @Override
    public void setWindow(Window window) {
        this.window = window;

        //offsetting the page based on the last one
        if (window.children.stream().anyMatch(comp -> comp instanceof Page)) {
            Page lastPage = (Page) Lists.reverse(window.children).stream().filter(comp -> comp instanceof Page).findFirst().get();
            y += lastPage.getHeight() * window.children.stream().filter(comp -> comp instanceof Page).count() - 1;
        }

        relativeX = x;
        relativeY = y;
    }

    public Group addGroup(Group group) {
        groups.add(group);
        window.addChild(group);
        return group;
    }

    public Window getWindow() {
        return window;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }
}
