package studio.dreamys.icarus.component.sub.attachment;

import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.component.Component;

public abstract class Attachment extends Component {
    public Component child;

    protected double x, y;

    public void attachTo(Component child) {
        this.child = child;
        this.child.group.addChild(this);
        Icarus.getWindow().attachments.add(this);
    }
}
