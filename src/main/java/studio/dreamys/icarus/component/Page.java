package studio.dreamys.icarus.component;

import lombok.Getter;
import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.util.RenderUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Page extends Component {
    @Getter private List<Group> groups = new ArrayList<>();

    private char icon;

    public Page(String label, char icon) {
        super(label, 37.5, 35);

        this.icon = icon;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        //update position
        super.render(mouseX, mouseY);

        Color color = Icarus.getWindow().activePageIndex == window.pages.indexOf(this) ? window.color : Color.DARK_GRAY;

        RenderUtils.drawIcon(icon, x + 5, y + 5, color);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (hovered(mouseX, mouseY) && mouseButton == 0) {
            window.setActivePage(this);
        }
    }

    public void addGroup(Group group) {
        groups.add(group);
        group.setWindow(window);
    }

    @Override
    public void setWindow(Window window) {
        this.window = window;

        y += height * (window.pages.size() - 1);

        relativeX = x;
        relativeY = y;
    }
}
