package studio.dreamys.icarus.component.sub;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import studio.dreamys.icarus.component.Attachment;
import studio.dreamys.icarus.component.Component;
import studio.dreamys.icarus.component.Window;
import studio.dreamys.icarus.util.RenderUtils;

import java.util.ArrayList;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class Group extends Component {
    private Window window;

    private double x;
    private double y;
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
        child.setX(x + 12.5);
        child.setY(y + height + (children.size() == 1 ? 0 : child.getClearance()));
        height += child.getHeight() + (children.size() == 1 ? 0 : child.getClearance());
        return this;
    }

    public Group addChild(Component child, Attachment... attachments) {
        child.setWindow(window);
        child.setGroup(this);
        children.add(child);
        child.setX(x + 12.5);
        child.setY(y + height + (children.size() == 1 ? 0 : child.getClearance()));
        height += child.getHeight() + (children.size() == 1 ? 0 : child.getClearance());
        for (Attachment attachment : attachments) {
            attachment.attachTo(child);
        }
        return this;
    }
}