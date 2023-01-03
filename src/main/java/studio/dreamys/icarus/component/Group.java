package studio.dreamys.icarus.component;

import lombok.Getter;
import studio.dreamys.icarus.util.RenderUtils;

import java.util.ArrayList;
import java.util.List;

public class Group extends Component {
    @Getter private List<Component> children = new ArrayList<>();

    public Group(String label, double x, double y) {
        super(label, 150, 10);

        this.label = label;
        this.x = x;
        this.y = y;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        //update position
        super.render(mouseX, mouseY);

        //outlined box + label
        RenderUtils.drawGroupWithString(width, height + 4, x, y, label);
    }

    public void addChild(Component child) {
        children.add(child);
        child.setWindow(window);
        positionChild(child);
    }

    public void positionChild(Component child) {
        child.setX(x + 12.5);
        child.setY(y + height + child.getBounds().getOffsetY());
        height += child.getBounds().getHeight() + child.getBounds().getOffsetY() + 5;
    }
}