package studio.dreamys.icarus.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import studio.dreamys.icarus.component.Component;
import studio.dreamys.icarus.component.sub.*;

import java.util.HashMap;

public abstract class ComponentEvent extends Event {
    public Component component;

    public ComponentEvent(Component component) {
        this.component = component;
    }

    public static class CheckboxEvent extends ComponentEvent {
        public boolean toggled;

        public CheckboxEvent(Checkbox checkbox) {
            super(checkbox);
            toggled = checkbox.isToggled();
        }
    }

    public static class ChoiceEvent extends ComponentEvent {
        public String selected;

        public ChoiceEvent(Choice choice) {
            super(choice);
            selected = choice.getSelected();
        }
    }

    public static class ComboEvent extends ComponentEvent {
        public HashMap<String, Boolean> activeOptions;

        public ComboEvent(Combo combo) {
            super(combo);
            activeOptions = combo.getOptions();
        }
    }

    public static class FieldEvent extends ComponentEvent {
        public String text;

        public FieldEvent(Field field) {
            super(field);
            text = field.getText();
        }
    }

    public static class KeybindEvent extends ComponentEvent {
        public int key;

        public KeybindEvent(Keybind keybind) {
            super(keybind);
            key = keybind.getKey();
        }
    }

    public static class SliderEvent extends ComponentEvent {
        public double value;

        public SliderEvent(Slider slider) {
            super(slider);
            value = slider.getValue();
        }
    }
}