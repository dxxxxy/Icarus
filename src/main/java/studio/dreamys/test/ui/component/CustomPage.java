package studio.dreamys.test.ui.component;

import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.component.Page;
import studio.dreamys.icarus.util.RenderUtils;

import java.awt.*;

public class CustomPage extends Page {
    public CustomPage(String label, char icon) {
        super(label, icon);
    }

    @Override
    public void render(int mouseX, int mouseY) {
        x = window.x + relativeX;
        y = window.y + relativeY;

        Color color = Icarus.getWindow().activePageIndex == window.pages.indexOf(this) ? window.color : Color.DARK_GRAY;

        RenderUtils.drawIcon(icon, x + 20, y + 25, color);
    }
}
