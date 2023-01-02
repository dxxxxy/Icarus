package studio.dreamys.test.ui.page;

import studio.dreamys.icarus.annotation.IGroup;
import studio.dreamys.icarus.annotation.IPage;
import studio.dreamys.icarus.annotation.ISetting;
import studio.dreamys.icarus.component.wrapper.WChoice;
import studio.dreamys.icarus.component.wrapper.WSlider;
import studio.dreamys.icarus.extra.notification.Notification;
import studio.dreamys.icarus.extra.notification.NotificationManager;

import java.util.Arrays;
import java.util.HashMap;

@IPage(icon = 'v')
public class Visuals {
    @IGroup(x = 47.5, y = 10)
    public static class ESP {
        @ISetting
        public static Runnable Button
        = () -> NotificationManager.send(new Notification("Button", "You pressed the button!"));

        @ISetting
        public static boolean Checkbox
        = true;

        @ISetting
        public static WChoice Choice
        = new WChoice("Option 1", Arrays.asList("Option 1", "Option 2", "Option 3"));

        //TODO: color

        @ISetting
        public static HashMap<String, Boolean> Combo
        = new HashMap<String, Boolean>() {{
            put("Option 1", true);
            put("Option 2", false);
            put("Option 3", true);
        }};

        @ISetting
        public static String Field
        = "Hello World!";

        @ISetting
        public static WSlider Slider
        = new WSlider(1, 10, 30, false, "x");
    }
}
