package studio.dreamys.test.ui.page;

import org.lwjgl.input.Keyboard;
import studio.dreamys.icarus.annotation.field.DropdownOptions;
import studio.dreamys.icarus.annotation.field.IKeybind;
import studio.dreamys.icarus.annotation.field.SliderOptions;
import studio.dreamys.icarus.annotation.type.IGroup;
import studio.dreamys.icarus.annotation.type.IPage;
import studio.dreamys.icarus.extra.notification.Notification;
import studio.dreamys.icarus.extra.notification.NotificationManager;

@IPage('v')
public class Visuals {
    @IGroup(x = 47.5, y = 10)
    public static class ESP {
        public static Runnable Button = () -> {
            NotificationManager.send(new Notification("Button", "You pressed the button!"));
            System.out.println("Button pressed!");
        };

        public static boolean Checkbox = true;

        @DropdownOptions({"Option 1", "Option 2", "Option 3"})
        public static String Choice = "Option 1";

        @DropdownOptions({"Option 1", "Option 2", "Option 3"})
        public static String[] Combo = {"Option 1", "Option 3"};

        public static String Field = "Hello World!";

        @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
        public static double Slider = 1;
    }

    @IGroup(x = 200, y = 10)
    public static class Chams {
        @IKeybind(Keyboard.KEY_ESCAPE)
        public static Runnable Button = () -> {
            NotificationManager.send(new Notification("Button", "You pressed the button!"));
            System.out.println("Button pressed!");
        };

        public static boolean Checkbox = true;

        @DropdownOptions({"Option 1", "Option 2", "Option 3"})
        public static String Choice = "Option 1";

        @DropdownOptions({"Option 1", "Option 2", "Option 3"})
        public static String[] Combo = {"Option 1", "Option 3"};

        public static String Field = "Hello World!";

        @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
        public static double Slider = 1;
    }
}
