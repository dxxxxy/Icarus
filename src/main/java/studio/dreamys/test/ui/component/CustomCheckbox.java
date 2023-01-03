package studio.dreamys.test.ui.component;

import studio.dreamys.icarus.component.sub.Checkbox;

public class CustomCheckbox extends Checkbox {
    public CustomCheckbox(String label) {
        super(label);

        width = 80;
        height = 80;
    }
}
