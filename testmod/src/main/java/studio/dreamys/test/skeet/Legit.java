package studio.dreamys.test.skeet;

import studio.dreamys.icarus.annotation.ILabel;
import studio.dreamys.icarus.annotation.field.DropdownOptions;
import studio.dreamys.icarus.annotation.field.IKeybind;
import studio.dreamys.icarus.annotation.field.SliderOptions;
import studio.dreamys.icarus.annotation.type.IGroup;
import studio.dreamys.icarus.annotation.type.IPage;
import studio.dreamys.icarus.annotation.type.ISubPage;
import studio.dreamys.icarus.input.Keys;
import studio.dreamys.icarus.settings.KeybindMode;
import studio.dreamys.icarus.settings.Label;

/**
 * The LEGIT tab: one sub-page per weapon type, shown as the weapon icon row titled "Weapon type". Each weapon type
 * has its own copy of the groups; the reference shows them with these values.
 */
@ILabel("Weapon type")
@IPage(icon = "legit", order = 2)
public class Legit {
    @ISubPage(order = 0, icon = 'G')
    public static class Pistol {
        @IGroup(column = 0)
        public static class Aimbot {
            @IKeybind(value = Keys.MOUSE_5, mode = KeybindMode.ON_HOTKEY)
            public static boolean Enabled = false;

            @SliderOptions(min = 0, max = 10, step = 0.01)
            public static double Speed = 0.65;

            @ILabel("Speed (in attack)")
            @SliderOptions(min = 0, max = 10, step = 0.01)
            public static double Speed_in_attack = 0.65;

            @ILabel("Speed scale - FOV")
            @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
            public static int Speed_scale_FOV = 100;

            @ILabel("Maximum lock-on time")
            @SliderOptions(min = 0, max = 1000, onlyInt = true, units = "ms", maxText = "∞")
            public static int Maximum_lock_on_time = 1000;

            @SliderOptions(min = 0, max = 200, onlyInt = true, units = "ms")
            public static int Reaction_time = 100;

            @ILabel("Maximum FOV")
            @SliderOptions(min = 0, max = 180, step = 0.1, units = "°")
            public static double Maximum_FOV = 10.0;

            @ILabel("Recoil compensation (P/Y)")
            public static Label Recoil_compensation;

            @ILabel("")
            @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
            public static int Recoil_compensation_pitch = 0;

            @ILabel("")
            @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
            public static int Recoil_compensation_yaw = 0;

            public static boolean Quick_stop = false;
            public static boolean Aim_through_smoke = false;
            public static boolean Aim_while_blind = false;
            public static boolean Head = false;
            public static boolean Chest = true;
            public static boolean Stomach = true;
        }

        @IGroup(column = 1, order = 0, height = 372 / 1.25)
        public static class Triggerbot {
            @IKeybind(value = Keys.MOUSE_5, mode = KeybindMode.ON_HOTKEY)
            public static boolean Enabled = false;

            @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
            public static int Minimum_hit_chance = 75;

            @SliderOptions(min = 0, max = 200, onlyInt = true, units = "ms")
            public static int Reaction_time = 0;

            public static boolean Burst_fire = false;

            @SliderOptions(min = 0, max = 126, onlyInt = true)
            public static int Minimum_damage = 1;

            public static boolean Automatic_penetration = false;
            public static boolean Shoot_through_smoke = false;
            public static boolean Shoot_while_blind = false;
            public static boolean Head = true;
            public static boolean Chest = true;
            public static boolean Stomach = true;
        }

        @IGroup(column = 1, order = 1)
        public static class Other {
            @DropdownOptions({"Off", "Low", "Medium", "High", "Maximum"})
            public static String Accuracy_boost = "Off";

            public static boolean Standalone_recoil_compensation = false;
        }
    }

    @ISubPage(order = 1, icon = 'P')
    public static class SMG {
        @IGroup(column = 0)
        public static class Aimbot {
            @IKeybind(value = Keys.MOUSE_5, mode = KeybindMode.ON_HOTKEY)
            public static boolean Enabled = false;

            @SliderOptions(min = 0, max = 10, step = 0.01)
            public static double Speed = 0.65;

            @ILabel("Speed (in attack)")
            @SliderOptions(min = 0, max = 10, step = 0.01)
            public static double Speed_in_attack = 0.65;

            @ILabel("Speed scale - FOV")
            @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
            public static int Speed_scale_FOV = 100;

            @ILabel("Maximum lock-on time")
            @SliderOptions(min = 0, max = 1000, onlyInt = true, units = "ms", maxText = "∞")
            public static int Maximum_lock_on_time = 1000;

            @SliderOptions(min = 0, max = 200, onlyInt = true, units = "ms")
            public static int Reaction_time = 100;

            @ILabel("Maximum FOV")
            @SliderOptions(min = 0, max = 180, step = 0.1, units = "°")
            public static double Maximum_FOV = 10.0;

            @ILabel("Recoil compensation (P/Y)")
            public static Label Recoil_compensation;

            @ILabel("")
            @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
            public static int Recoil_compensation_pitch = 0;

            @ILabel("")
            @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
            public static int Recoil_compensation_yaw = 0;

            public static boolean Quick_stop = false;
            public static boolean Aim_through_smoke = false;
            public static boolean Aim_while_blind = false;
            public static boolean Head = false;
            public static boolean Chest = true;
            public static boolean Stomach = true;
        }

        @IGroup(column = 1, order = 0, height = 372 / 1.25)
        public static class Triggerbot {
            @IKeybind(value = Keys.MOUSE_5, mode = KeybindMode.ON_HOTKEY)
            public static boolean Enabled = false;

            @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
            public static int Minimum_hit_chance = 75;

            @SliderOptions(min = 0, max = 200, onlyInt = true, units = "ms")
            public static int Reaction_time = 0;

            public static boolean Burst_fire = false;

            @SliderOptions(min = 0, max = 126, onlyInt = true)
            public static int Minimum_damage = 1;

            public static boolean Automatic_penetration = false;
            public static boolean Shoot_through_smoke = false;
            public static boolean Shoot_while_blind = false;
            public static boolean Head = true;
            public static boolean Chest = true;
            public static boolean Stomach = true;
        }

        @IGroup(column = 1, order = 1)
        public static class Other {
            @DropdownOptions({"Off", "Low", "Medium", "High", "Maximum"})
            public static String Accuracy_boost = "Off";

            public static boolean Standalone_recoil_compensation = false;
        }
    }

    @ISubPage(order = 2, icon = 'W')
    public static class Rifle {
        @IGroup(column = 0)
        public static class Aimbot {
            @IKeybind(value = Keys.MOUSE_5, mode = KeybindMode.ON_HOTKEY)
            public static boolean Enabled = false;

            @SliderOptions(min = 0, max = 10, step = 0.01)
            public static double Speed = 0.65;

            @ILabel("Speed (in attack)")
            @SliderOptions(min = 0, max = 10, step = 0.01)
            public static double Speed_in_attack = 0.65;

            @ILabel("Speed scale - FOV")
            @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
            public static int Speed_scale_FOV = 100;

            @ILabel("Maximum lock-on time")
            @SliderOptions(min = 0, max = 1000, onlyInt = true, units = "ms", maxText = "∞")
            public static int Maximum_lock_on_time = 1000;

            @SliderOptions(min = 0, max = 200, onlyInt = true, units = "ms")
            public static int Reaction_time = 100;

            @ILabel("Maximum FOV")
            @SliderOptions(min = 0, max = 180, step = 0.1, units = "°")
            public static double Maximum_FOV = 10.0;

            @ILabel("Recoil compensation (P/Y)")
            public static Label Recoil_compensation;

            @ILabel("")
            @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
            public static int Recoil_compensation_pitch = 0;

            @ILabel("")
            @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
            public static int Recoil_compensation_yaw = 0;

            public static boolean Quick_stop = false;
            public static boolean Aim_through_smoke = false;
            public static boolean Aim_while_blind = false;
            public static boolean Head = false;
            public static boolean Chest = true;
            public static boolean Stomach = true;
        }

        @IGroup(column = 1, order = 0, height = 372 / 1.25)
        public static class Triggerbot {
            @IKeybind(value = Keys.MOUSE_5, mode = KeybindMode.ON_HOTKEY)
            public static boolean Enabled = false;

            @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
            public static int Minimum_hit_chance = 75;

            @SliderOptions(min = 0, max = 200, onlyInt = true, units = "ms")
            public static int Reaction_time = 0;

            public static boolean Burst_fire = false;

            @SliderOptions(min = 0, max = 126, onlyInt = true)
            public static int Minimum_damage = 1;

            public static boolean Automatic_penetration = false;
            public static boolean Shoot_through_smoke = false;
            public static boolean Shoot_while_blind = false;
            public static boolean Head = true;
            public static boolean Chest = true;
            public static boolean Stomach = true;
        }

        @IGroup(column = 1, order = 1)
        public static class Other {
            @DropdownOptions({"Off", "Low", "Medium", "High", "Maximum"})
            public static String Accuracy_boost = "Off";

            public static boolean Standalone_recoil_compensation = false;
        }
    }

    @ISubPage(order = 3, icon = 'd')
    public static class Shotgun {
        @IGroup(column = 0)
        public static class Aimbot {
            @IKeybind(value = Keys.MOUSE_5, mode = KeybindMode.ON_HOTKEY)
            public static boolean Enabled = false;

            @SliderOptions(min = 0, max = 10, step = 0.01)
            public static double Speed = 0.65;

            @ILabel("Speed (in attack)")
            @SliderOptions(min = 0, max = 10, step = 0.01)
            public static double Speed_in_attack = 0.65;

            @ILabel("Speed scale - FOV")
            @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
            public static int Speed_scale_FOV = 100;

            @ILabel("Maximum lock-on time")
            @SliderOptions(min = 0, max = 1000, onlyInt = true, units = "ms", maxText = "∞")
            public static int Maximum_lock_on_time = 1000;

            @SliderOptions(min = 0, max = 200, onlyInt = true, units = "ms")
            public static int Reaction_time = 100;

            @ILabel("Maximum FOV")
            @SliderOptions(min = 0, max = 180, step = 0.1, units = "°")
            public static double Maximum_FOV = 10.0;

            @ILabel("Recoil compensation (P/Y)")
            public static Label Recoil_compensation;

            @ILabel("")
            @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
            public static int Recoil_compensation_pitch = 0;

            @ILabel("")
            @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
            public static int Recoil_compensation_yaw = 0;

            public static boolean Quick_stop = false;
            public static boolean Aim_through_smoke = false;
            public static boolean Aim_while_blind = false;
            public static boolean Head = false;
            public static boolean Chest = true;
            public static boolean Stomach = true;
        }

        @IGroup(column = 1, order = 0, height = 372 / 1.25)
        public static class Triggerbot {
            @IKeybind(value = Keys.MOUSE_5, mode = KeybindMode.ON_HOTKEY)
            public static boolean Enabled = false;

            @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
            public static int Minimum_hit_chance = 75;

            @SliderOptions(min = 0, max = 200, onlyInt = true, units = "ms")
            public static int Reaction_time = 0;

            public static boolean Burst_fire = false;

            @SliderOptions(min = 0, max = 126, onlyInt = true)
            public static int Minimum_damage = 1;

            public static boolean Automatic_penetration = false;
            public static boolean Shoot_through_smoke = false;
            public static boolean Shoot_while_blind = false;
            public static boolean Head = true;
            public static boolean Chest = true;
            public static boolean Stomach = true;
        }

        @IGroup(column = 1, order = 1)
        public static class Other {
            @DropdownOptions({"Off", "Low", "Medium", "High", "Maximum"})
            public static String Accuracy_boost = "Off";

            public static boolean Standalone_recoil_compensation = false;
        }
    }

    @ISubPage(order = 4, icon = 'f')
    public static class Machine_gun {
        @IGroup(column = 0)
        public static class Aimbot {
            @IKeybind(value = Keys.MOUSE_5, mode = KeybindMode.ON_HOTKEY)
            public static boolean Enabled = false;

            @SliderOptions(min = 0, max = 10, step = 0.01)
            public static double Speed = 0.65;

            @ILabel("Speed (in attack)")
            @SliderOptions(min = 0, max = 10, step = 0.01)
            public static double Speed_in_attack = 0.65;

            @ILabel("Speed scale - FOV")
            @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
            public static int Speed_scale_FOV = 100;

            @ILabel("Maximum lock-on time")
            @SliderOptions(min = 0, max = 1000, onlyInt = true, units = "ms", maxText = "∞")
            public static int Maximum_lock_on_time = 1000;

            @SliderOptions(min = 0, max = 200, onlyInt = true, units = "ms")
            public static int Reaction_time = 100;

            @ILabel("Maximum FOV")
            @SliderOptions(min = 0, max = 180, step = 0.1, units = "°")
            public static double Maximum_FOV = 10.0;

            @ILabel("Recoil compensation (P/Y)")
            public static Label Recoil_compensation;

            @ILabel("")
            @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
            public static int Recoil_compensation_pitch = 0;

            @ILabel("")
            @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
            public static int Recoil_compensation_yaw = 0;

            public static boolean Quick_stop = false;
            public static boolean Aim_through_smoke = false;
            public static boolean Aim_while_blind = false;
            public static boolean Head = false;
            public static boolean Chest = true;
            public static boolean Stomach = true;
        }

        @IGroup(column = 1, order = 0, height = 372 / 1.25)
        public static class Triggerbot {
            @IKeybind(value = Keys.MOUSE_5, mode = KeybindMode.ON_HOTKEY)
            public static boolean Enabled = false;

            @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
            public static int Minimum_hit_chance = 75;

            @SliderOptions(min = 0, max = 200, onlyInt = true, units = "ms")
            public static int Reaction_time = 0;

            public static boolean Burst_fire = false;

            @SliderOptions(min = 0, max = 126, onlyInt = true)
            public static int Minimum_damage = 1;

            public static boolean Automatic_penetration = false;
            public static boolean Shoot_through_smoke = false;
            public static boolean Shoot_while_blind = false;
            public static boolean Head = true;
            public static boolean Chest = true;
            public static boolean Stomach = true;
        }

        @IGroup(column = 1, order = 1)
        public static class Other {
            @DropdownOptions({"Off", "Low", "Medium", "High", "Maximum"})
            public static String Accuracy_boost = "Off";

            public static boolean Standalone_recoil_compensation = false;
        }
    }

    @ISubPage(order = 5, icon = 'a')
    public static class Sniper {
        @IGroup(column = 0)
        public static class Aimbot {
            @IKeybind(value = Keys.MOUSE_5, mode = KeybindMode.ON_HOTKEY)
            public static boolean Enabled = false;

            @SliderOptions(min = 0, max = 10, step = 0.01)
            public static double Speed = 0.65;

            @ILabel("Speed (in attack)")
            @SliderOptions(min = 0, max = 10, step = 0.01)
            public static double Speed_in_attack = 0.65;

            @ILabel("Speed scale - FOV")
            @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
            public static int Speed_scale_FOV = 100;

            @ILabel("Maximum lock-on time")
            @SliderOptions(min = 0, max = 1000, onlyInt = true, units = "ms", maxText = "∞")
            public static int Maximum_lock_on_time = 1000;

            @SliderOptions(min = 0, max = 200, onlyInt = true, units = "ms")
            public static int Reaction_time = 100;

            @ILabel("Maximum FOV")
            @SliderOptions(min = 0, max = 180, step = 0.1, units = "°")
            public static double Maximum_FOV = 10.0;

            @ILabel("Recoil compensation (P/Y)")
            public static Label Recoil_compensation;

            @ILabel("")
            @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
            public static int Recoil_compensation_pitch = 0;

            @ILabel("")
            @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
            public static int Recoil_compensation_yaw = 0;

            public static boolean Quick_stop = false;
            public static boolean Aim_through_smoke = false;
            public static boolean Aim_while_blind = false;
            public static boolean Head = false;
            public static boolean Chest = true;
            public static boolean Stomach = true;
        }

        @IGroup(column = 1, order = 0, height = 372 / 1.25)
        public static class Triggerbot {
            @IKeybind(value = Keys.MOUSE_5, mode = KeybindMode.ON_HOTKEY)
            public static boolean Enabled = false;

            @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
            public static int Minimum_hit_chance = 75;

            @SliderOptions(min = 0, max = 200, onlyInt = true, units = "ms")
            public static int Reaction_time = 0;

            public static boolean Burst_fire = false;

            @SliderOptions(min = 0, max = 126, onlyInt = true)
            public static int Minimum_damage = 1;

            public static boolean Automatic_penetration = false;
            public static boolean Shoot_through_smoke = false;
            public static boolean Shoot_while_blind = false;
            public static boolean Head = true;
            public static boolean Chest = true;
            public static boolean Stomach = true;
        }

        @IGroup(column = 1, order = 1)
        public static class Other {
            @DropdownOptions({"Off", "Low", "Medium", "High", "Maximum"})
            public static String Accuracy_boost = "Off";

            public static boolean Standalone_recoil_compensation = false;
        }
    }
}
