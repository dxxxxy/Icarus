package studio.dreamys.minesense.component.sub;

import studio.dreamys.minesense.component.Attachment;
import studio.dreamys.minesense.component.Component;
import studio.dreamys.minesense.util.RenderUtils;

public class Keybind extends Attachment {
    private Component child;

    @Override
    public void render(int mouseX, int mouseY) {
//        RenderUtils.drawScaledString();
    }

    public Component attachTo(Component child) {
        this.child = child;
        this.child.getWindow().addChild(this); //add to window as component
        return child;
    }
}
