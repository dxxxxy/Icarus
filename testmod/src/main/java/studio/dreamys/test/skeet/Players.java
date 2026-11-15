package studio.dreamys.test.skeet;

import studio.dreamys.icarus.annotation.ILabel;
import studio.dreamys.icarus.annotation.field.DropdownOptions;
import studio.dreamys.icarus.annotation.field.IDisabled;
import studio.dreamys.icarus.annotation.field.SliderOptions;
import studio.dreamys.icarus.annotation.type.IGroup;
import studio.dreamys.icarus.annotation.type.IPage;
import studio.dreamys.icarus.settings.ListModel;
import studio.dreamys.test.TestModCommon;

/** The PLAYERS tab, as in the reference. */
@IPage(icon = "players", order = 6)
public class Players {
    @ILabel("Players")
    @IGroup(column = 0)
    public static class Players_list {
        @ILabel("")
        public static final ListModel Players = new ListModel("Ulric", "Marvin", "Niles", "Bob", "Ferris").height(530 / 1.25);

        static {
            Players.select("Ulric");
        }

        public static Runnable Reset_all = () -> TestModCommon.notify("Players", "Reset every player.");
    }

    @IGroup(column = 1)
    public static class Adjustments {
        public static boolean Add_to_whitelist = false;

        @IDisabled
        @ILabel("Allow shared ESP updates")
        public static boolean Allow_shared_ESP_updates = false;

        public static boolean Disable_visuals = false;
        public static boolean High_priority = false;

        @DropdownOptions({"-", "Default", "Sideways", "Backwards"})
        public static String Force_yaw = "-";

        @SliderOptions(min = -180, max = 180, onlyInt = true, units = "°")
        public static int Force_yaw_value = 0;

        public static boolean Force_pitch = false;

        @SliderOptions(min = -89, max = 89, onlyInt = true, units = "°")
        public static int Force_pitch_value = 0;

        public static boolean Force_body_yaw = false;

        @ILabel("")
        @SliderOptions(min = -60, max = 60, onlyInt = true, units = "°")
        public static int Force_body_yaw_value = 0;

        @ILabel("Override anti-aim correction")
        @DropdownOptions({"-", "On", "Off"})
        public static String Override_anti_aim_correction = "-";

        @DropdownOptions({"-", "On", "Off", "Force"})
        public static String Override_prefer_body_aim = "-";

        @DropdownOptions({"-", "Low", "Medium", "High", "Maximum"})
        public static String Override_accuracy_boost = "-";

        @DropdownOptions({"-", "On", "Off"})
        public static String Override_safe_point = "-";

        public static Runnable Apply_to_all = () -> TestModCommon.notify("Players", "Applied to every player.");
    }
}
