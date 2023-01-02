package studio.dreamys.test.ui.page;

import studio.dreamys.icarus.annotation.IGroup;
import studio.dreamys.icarus.annotation.IPage;
import studio.dreamys.icarus.component.wrapper.WChoice;
import studio.dreamys.icarus.component.wrapper.WSlider;
import studio.dreamys.icarus.extra.notification.Notification;
import studio.dreamys.icarus.extra.notification.NotificationManager;

import java.util.Arrays;
import java.util.HashMap;

@IPage(icon = 'u')
public class Misc {
    @IGroup(x = 47.5, y = 10)
    public static class Crasher {
        public static Runnable Button
                = () -> NotificationManager.send(new Notification("Button", "You pressed the button!"));

        public static boolean Checkbox
                = true;

        public static WChoice Choice
                = new WChoice("Option 1", Arrays.asList("Option 1", "Option 2", "Option 3"));

        public static HashMap<String, Boolean> Combo
                = new HashMap<String, Boolean>() {{
            put("Option 1", true);
            put("Option 2", false);
            put("Option 3", true);
        }};

        public static String Field
                = "Hello World!";

        public static WSlider Slider
                = new WSlider(1, 10, 30, false, "x");
    }
}