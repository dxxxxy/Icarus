package studio.dreamys.test.skeet;

import studio.dreamys.icarus.annotation.ILabel;
import studio.dreamys.icarus.annotation.field.DropdownOptions;
import studio.dreamys.icarus.annotation.field.IKeybind;
import studio.dreamys.icarus.annotation.field.IWarning;
import studio.dreamys.icarus.annotation.field.SliderOptions;
import studio.dreamys.icarus.annotation.type.IGroup;
import studio.dreamys.icarus.annotation.type.IPage;
import studio.dreamys.icarus.input.Keys;
import studio.dreamys.icarus.settings.KeybindMode;
import studio.dreamys.icarus.settings.Label;

/** The RAGE tab, as in the reference. */
@IPage(icon = "rage", order = 0)
public class Rage {
    @IGroup(column = 0)
    public static class Aimbot {
        @IKeybind(value = Keys.MOUSE_5, mode = KeybindMode.ON_HOTKEY)
        public static boolean Enabled = false;

        @DropdownOptions({"Cycle", "Cycle (2x)", "Near crosshair", "Highest damage", "Lowest ping", "Best K/D ratio", "Best hit chance"})
        public static String Target_selection = "Cycle";

        @DropdownOptions({"Head", "Chest", "Stomach", "Arms", "Legs", "Feet"})
        public static String[] Target_hitbox = {"Head"};

        @ILabel("Multi-point")
        @IKeybind(Keys.KEY_NONE)
        @DropdownOptions({"Head", "Chest", "Stomach", "Arms", "Legs", "Feet"})
        public static String[] Multi_point = {};

        public static boolean Prefer_safe_point = false;

        @IKeybind(Keys.KEY_NONE)
        public static Label Force_safe_point;

        public static boolean Automatic_fire = false;
        public static boolean Automatic_penetration = false;
        public static boolean Silent_aim = false;

        @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
        public static int Minimum_hit_chance = 50;

        @SliderOptions(min = 0, max = 126, onlyInt = true)
        public static int Minimum_damage = 10;

        @ILabel("Override AWP")
        public static boolean Override_AWP = false;

        public static boolean Automatic_scope = false;
        public static boolean Reduce_aim_step = false;

        @ILabel("Maximum FOV")
        @SliderOptions(min = 1, max = 180, onlyInt = true, units = "°")
        public static int Maximum_FOV = 180;

        public static boolean Log_misses_due_to_spread = true;

        @ILabel("Low FPS mitigations")
        @DropdownOptions({"Force low accuracy boost", "Disable multipoint: feet", "Disable multipoint: arms", "Disable multipoint: legs",
                "Disable hitbox: feet", "Force low multipoint", "Lower hit chance precision", "Limit targets per tick"})
        public static String[] Low_FPS_mitigations = {};
    }

    @IGroup(column = 1)
    public static class Other {
        @IWarning
        public static boolean Remove_spread = false;

        public static boolean Remove_recoil = false;

        @DropdownOptions({"Off", "Low", "Medium", "High", "Maximum"})
        public static String Accuracy_boost = "Off";

        @IKeybind(Keys.KEY_NONE)
        public static boolean Quick_stop = false;

        @ILabel("Anti-aim correction")
        public static boolean Anti_aim_correction = false;

        @ILabel("Anti-aim correction override")
        @IKeybind(Keys.KEY_NONE)
        public static Label Anti_aim_correction_override;

        public static boolean Prefer_body_aim = false;

        @IKeybind(Keys.KEY_NONE)
        public static Label Force_body_aim;

        @IKeybind(Keys.KEY_NONE)
        public static Label Duck_peek_assist;

        @IWarning
        @IKeybind(Keys.KEY_NONE)
        public static boolean Double_tap = false;

        public static boolean Increase_doubletap_speed = false;
    }
}
