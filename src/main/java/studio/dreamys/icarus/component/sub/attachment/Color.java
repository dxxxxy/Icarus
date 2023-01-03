package studio.dreamys.icarus.component.sub.attachment;

import studio.dreamys.icarus.component.Attachment;
import studio.dreamys.icarus.component.Component;
import studio.dreamys.icarus.util.RenderUtils;

//TODO: implement this class
public class Color extends Attachment {
    private Component child;
    private java.awt.Color color = new java.awt.Color(51, 192, 227);

    public void render(int mouseX, int mouseY) {
        RenderUtils.drawGradientRect(child.getX() + child.getWidth() + 115, child.getY(), child.getX() + child.getWidth() + 125, child.getY() + 5, color, color.darker().darker().darker());
    }
}
