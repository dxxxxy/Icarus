package studio.dreamys.test.ui.page;

import studio.dreamys.icarus.annotation.field.DropdownOptions;
import studio.dreamys.icarus.annotation.field.IKeybind;
import studio.dreamys.icarus.annotation.field.SliderOptions;
import studio.dreamys.icarus.annotation.type.IGroup;
import studio.dreamys.icarus.annotation.type.IPage;
import studio.dreamys.icarus.input.Keys;
import studio.dreamys.test.TestModCommon;

@IPage(value = 's', order = 0)
public class Combat {
    @IGroup(column = 0)
    public static class KillAura {
        @IKeybind(Keys.KEY_6)
        public static Runnable Button = () -> {
            TestModCommon.notify("Button", "You pressed the button!");
            System.out.println("Button pressed!");
        };

        @IKeybind(Keys.KEY_7)
        public static boolean Checkbox_Haha = true;

        @DropdownOptions({"Option 1", "Option 2", "Option 3"})
        public static String Choice = "Option 1";

        @DropdownOptions({"Option 1", "Option 2", "Option 3"})
        public static String[] Combo = {"Option 1", "Option 3"};

        public static String Field = "Hello World!";

        @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
        public static double Slider = 1;
    }

    @IGroup(column = 1)
    public static class Accuracy {
        @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
        public static int Hit_chance = 60;

        @SliderOptions(min = 0, max = 10)
        public static double Smoothing = 2.5;

        public static boolean Auto_fire = false;

        @DropdownOptions({"Head", "Chest", "Stomach", "Arms", "Legs"})
        public static String[] Hitboxes = {"Head", "Chest"};
    }
}
