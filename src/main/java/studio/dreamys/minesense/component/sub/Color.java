package studio.dreamys.minesense.component.sub;

import studio.dreamys.minesense.component.Attachment;
import studio.dreamys.minesense.component.Component;
import studio.dreamys.minesense.util.RenderUtils;

public class Color extends Attachment {
    private Component child;
    private java.awt.Color color = new java.awt.Color(51, 192, 227);

    public void render(int mouseX, int mouseY) {
        RenderUtils.drawGradientRect(child.getX() + child.getWidth() + 115, child.getY(), child.getX() + child.getWidth() + 125, child.getY() + 5, color, color.darker().darker().darker());
    }

    public Component attachTo(Component child) {
        this.child = child;
        this.child.getWindow().addChild(this); //add to window as component
        return child;
    }
}
