package studio.dreamys.icarus.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import studio.dreamys.icarus.component.Component;

public class ComponentStateChangeEvent extends Event {
    public final Component component;

    public ComponentStateChangeEvent(Component component) {
        this.component = component;
    }
}
