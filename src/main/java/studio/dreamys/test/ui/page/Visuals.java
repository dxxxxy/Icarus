package studio.dreamys.test.ui.page;

import studio.dreamys.icarus.annotation.*;
import studio.dreamys.icarus.config.SliderWrapper;
import studio.dreamys.icarus.extra.notification.Notification;
import studio.dreamys.icarus.extra.notification.NotificationManager;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;

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
        public static AbstractMap.SimpleEntry<String, List<String>> Choice
        = new AbstractMap.SimpleEntry<>("Option 1", Arrays.asList("Option 1", "Option 2", "Option 3"));

        //TODO: color

        @ISetting
        public static AbstractMap.SimpleEntry<List<String>, List<String>> Combo
        = new AbstractMap.SimpleEntry<>(Arrays.asList("Option 1", "Option 3"), Arrays.asList("Option 1", "Option 2", "Option 3"));

        @ISetting
        public static String Field
        = "Hello World!";

        @ISetting
        public static SliderWrapper Slider
        = new SliderWrapper(1, 10, 30, false, "x");
    }
}
