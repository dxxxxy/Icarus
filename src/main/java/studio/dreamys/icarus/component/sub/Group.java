package studio.dreamys.icarus.component.sub;

import lombok.Getter;
import lombok.Setter;
import org.lwjgl.util.Rectangle;
import studio.dreamys.icarus.component.Attachment;
import studio.dreamys.icarus.component.Component;
import studio.dreamys.icarus.component.Window;
import studio.dreamys.icarus.util.RenderUtils;

import java.awt.*;
import java.util.ArrayList;

@Getter
@Setter
public class Group extends Component {
    private Window window;
    private Group group;

    private double x;
    private double y = 5;
    private double width = 150;
    private double height = 10;
    private String label;

    private ArrayList<Component> children = new ArrayList<>();

    //relative to window, aka x,y passed in constructor
    private double relativeX;
    private double relativeY;

    public Group(String label) {
        this.label = label;
    }

    public Group(String label, double x, double y) {
        this.label = label;
        this.x = x;
        this.y = y;
    }

    public Group(double x, double y, double width, double height, String label) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.label = label;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        //update position
        x = window.x + relativeX;
        y = window.y + relativeY;

        //the box itself
        RenderUtils.drawGroupWithString(width, height + 10, x, y, label);
    }

    @Override
    public void setWindow(Window window) {
        this.window = window;
        relativeX = x;
        relativeY = y;
    }

    public Group addChild(Component child) {
        child.setWindow(window);
        child.setGroup(this);
        children.add(child);

        positionChild(child);

        return this;
    }

    public Group addChild(Component child, Attachment... attachments) {
        child.setWindow(window);
        child.setGroup(this);
        children.add(child);

        positionChild(child);

        for (Attachment attachment : attachments) {
            attachment.attachTo(child);
            children.add(attachment);
        }

        return this;
    }

    public void positionChild(Component child) {
        child.setX(x + 12.5);
        child.setY(y + height + child.getBounds().getOffsetY());
        height += child.getBounds().getHeight() + child.getBounds().getOffsetY() + 5;
    }
}