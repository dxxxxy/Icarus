package studio.dreamys.icarus.component;

import lombok.Getter;
import lombok.Setter;
import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.component.sub.Group;
import studio.dreamys.icarus.util.RenderUtils;

import java.awt.*;
import java.util.ArrayList;

@Getter
@Setter
public class Page extends Component {
    private Window window;
    private String label;

    private double x = 3;
    private double y = 4;
    private double width = 37.5;
    private double height = 35;

    private char icon;
    private ArrayList<Group> groups = new ArrayList<>();

    //relative to window, aka x,y passed in constructor
    private double relativeX;
    private double relativeY;

    public Page(String label, char icon) {
        this.label = label;
        this.icon = icon;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        //update position
        x = window.x + relativeX;
        y = window.y + relativeY;

        Color color = Icarus.getWindow().activePageIndex == window.pages.indexOf(this) ? window.color : Color.DARK_GRAY;

        RenderUtils.drawIcon(icon, x + 5, y + 5, color);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (hovered(mouseX, mouseY) && mouseButton == 0) {
            window.setActivePage(this);
        }
    }

    private boolean hovered(double x, double y) {
        return x > this.x && x < this.x + width && y > this.y && y < this.y + height;
    }

    public Group addGroup(Group group) {
        //add group to list
        groups.add(group);
        group.setPage(this);
        //pass window to group
        group.setWindow(window);
        return group;
    }

    @Override
    public void setWindow(Window window) {
        this.window = window;

        y += height * (window.pages.size() - 1);

        relativeX = x;
        relativeY = y;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
