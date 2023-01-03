package studio.dreamys.icarus.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import studio.dreamys.icarus.component.Component;
import studio.dreamys.icarus.component.sub.*;
import studio.dreamys.icarus.component.sub.attachment.sub.Keybind;

import java.util.List;

public abstract class ComponentEvent extends Event {
    public Component component;

    public ComponentEvent(Component component) {
        this.component = component;
    }

    public static class CheckboxEvent extends Event {
        public Checkbox checkbox;
        public boolean toggled;

        public CheckboxEvent(Checkbox checkbox) {
            this.checkbox = checkbox;
            toggled = checkbox.isToggled();
        }
    }

    public static class ChoiceEvent extends Event {
        public Choice choice;
        public String selected;

        public ChoiceEvent(Choice choice) {
            this.choice = choice;
            selected = choice.getSelected();
        }
    }

    public static class ComboEvent extends Event {
        public Combo combo;
        public List<String> active;

        public ComboEvent(Combo combo) {
            this.combo = combo;
            active = combo.getActive();
        }
    }

    public static class FieldEvent extends Event {
        public Field field;
        public String text;

        public FieldEvent(Field field) {
            this.field = field;
            text = field.getText();
        }
    }

    public static class KeybindEvent extends Event {
        public Keybind keybind;
        public int key;

        public KeybindEvent(Keybind keybind) {
            this.keybind = keybind;
            key = keybind.getKey();
        }
    }

    public static class SliderEvent extends Event {
        public Slider slider;
        public double value;

        public SliderEvent(Slider slider) {
            this.slider = slider;
            value = slider.getValue();
        }
    }
}