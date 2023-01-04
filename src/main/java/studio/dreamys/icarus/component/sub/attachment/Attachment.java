package studio.dreamys.icarus.component.sub.attachment;

import lombok.Getter;
import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.component.Component;

public abstract class Attachment extends Component {
    @Getter protected Component child;

    protected double x, y;

    public void attachTo(Component child) {
        this.child = child; //set child
        this.child.getGroup().addChild(this); //add for render
        Icarus.getWindow().attachments.add(this); //add for config
    }
}
