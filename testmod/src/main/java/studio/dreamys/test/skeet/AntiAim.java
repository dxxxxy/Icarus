package studio.dreamys.test.skeet;

import studio.dreamys.icarus.annotation.ILabel;
import studio.dreamys.icarus.annotation.field.DropdownOptions;
import studio.dreamys.icarus.annotation.field.IKeybind;
import studio.dreamys.icarus.annotation.field.IWarning;
import studio.dreamys.icarus.annotation.field.SliderOptions;
import studio.dreamys.icarus.annotation.type.IGroup;
import studio.dreamys.icarus.annotation.type.IPage;
import studio.dreamys.icarus.input.Keys;

/** The ANTI-AIM tab, as in the reference. */
@IPage(icon = "antiaim", order = 1)
public class AntiAim {
    @ILabel("Anti-aimbot angles")
    @IGroup(column = 0)
    public static class Anti_aimbot_angles {
        public static boolean Enabled = false;

        @DropdownOptions({"Off", "Default", "Up", "Down", "Minimal", "Random"})
        public static String Pitch = "Off";

        @DropdownOptions({"Local view", "At targets"})
        public static String Yaw_base = "Local view";

        @DropdownOptions({"Off", "180", "Spin", "Static", "180 Z", "Crosshair"})
        public static String Yaw = "Off";

        @DropdownOptions({"Off", "Opposite", "Jitter", "Static"})
        public static String Body_yaw = "Off";

        @DropdownOptions({"Off", "Sway", "Opposite", "Eye yaw"})
        public static String Lower_body_yaw_target = "Off";

        @DropdownOptions({"Off", "On"})
        public static String Edge_yaw = "Off";

        @IKeybind(Keys.KEY_NONE)
        @DropdownOptions({"Default", "Edge", "Manual"})
        public static String[] Freestanding = {};
    }

    @IGroup(column = 1, order = 0, height = 388 / 1.25)
    public static class Fake_lag {
        @IKeybind(Keys.KEY_NONE)
        public static boolean Enabled = false;

        public static boolean Customize_triggers = false;

        @DropdownOptions({"Dynamic", "Maximum", "Fluctuate"})
        public static String Amount = "Dynamic";

        @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
        public static int Variance = 0;

        @SliderOptions(min = 1, max = 14, onlyInt = true)
        public static int Limit = 14;

        public static boolean Fake_lag_while_shooting = false;
        public static boolean Reset_on_bunny_hop = false;
    }

    @IGroup(column = 1, order = 1)
    public static class Other {
        @IKeybind(Keys.KEY_CAPITAL)
        public static boolean Slow_motion = false;

        @DropdownOptions({"Favor high speed", "Favor anti-aim"})
        public static String Slow_motion_type = "Favor high speed";

        @DropdownOptions({"Off", "Always slide", "Never slide"})
        public static String Leg_movement = "Off";

        @IWarning
        @ILabel("On shot anti-aim")
        @IKeybind(Keys.KEY_NONE)
        public static boolean On_shot_anti_aim = false;
    }
}
