package studio.dreamys.test.fatality;

import studio.dreamys.icarus.annotation.ILabel;
import studio.dreamys.icarus.annotation.field.DropdownOptions;
import studio.dreamys.icarus.annotation.field.IAttach;
import studio.dreamys.icarus.annotation.field.IKeybind;
import studio.dreamys.icarus.annotation.field.SliderOptions;
import studio.dreamys.icarus.annotation.type.IGroup;
import studio.dreamys.icarus.annotation.type.IPage;
import studio.dreamys.icarus.annotation.type.ISubPage;
import studio.dreamys.icarus.input.Keys;
import studio.dreamys.icarus.settings.ListModel;
import studio.dreamys.test.TestModCommon;

import java.awt.Color;

/** The RAGE tab: AIMBOT (one sub-page per weapon, shown as the weapon row) and ANTI-AIM. */
@IPage(order = 0)
public class Rage {
    @ILabel("Aimbot")
    @ISubPage(order = 0, icon = 'Y')
    public static class Aimbot_Auto {
        @ILabel("Auto")
        @IGroup(column = 0)
        public static class Weapon {
            @SliderOptions(min = 0, max = 100, onlyInt = true, units = " %")
            public static int Hitchance = 97;

            @ILabel("Min-damage")
            @SliderOptions(min = 0, max = 120, onlyInt = true, units = " dmg")
            public static int Min_damage = 73;

            @SliderOptions(min = 0, max = 100, onlyInt = true, units = " %", minText = "Auto")
            public static int Damage_accuracy = 0;

            @DropdownOptions({"Head", "Chest", "Stomach", "Arms", "Legs", "Feet"})
            public static String[] Additional_hitscan = {};

            @DropdownOptions({"Head", "Chest", "Stomach", "Arms", "Legs", "Feet"})
            public static String[] Additional_points = {};
        }

        @IGroup(column = 1)
        public static class Extra {
            @IKeybind(Keys.KEY_NONE)
            @DropdownOptions({"Off", "Slide", "Full", "Quick"})
            public static String Autostop = "Slide";

            public static boolean Predictive = false;
            public static boolean Only_when_lethal = false;
            public static boolean Autoscope = false;

            @DropdownOptions({"Off", "Default", "Prefer", "Force"})
            public static String Safepoint_mode = "Off";

            @DropdownOptions({"Head", "Chest", "Stomach", "Arms", "Legs", "Feet"})
            public static String[] Force_safepoint = {};

            public static boolean Ignore_limbs_when_target_moving = false;
            public static boolean Jumpscout = false;
            public static boolean Refine_shot = false;

            @IKeybind(Keys.KEY_NONE)
            public static boolean Silent = false;
        }

        @ILabel("Aimbot")
        @IGroup(column = 2)
        public static class Aimbot_group {
            @IKeybind(Keys.KEY_NONE)
            public static boolean Aimbot = true;

            public static boolean Autofire = true;
            public static boolean Backtrack = false;

            @IKeybind(Keys.KEY_NONE)
            public static boolean Force_safepoint = false;

            public static boolean Nospread = false;

            @IKeybind(Keys.KEY_NONE)
            public static boolean Headshot_only = false;

            public static boolean Knifebot = false;
            public static boolean Zeusbot = false;
            public static boolean Extended_backtrack = false;
        }
    }

    @ILabel("Aimbot")
    @ISubPage(order = 1, icon = 'a')
    public static class Aimbot_Scout {
        @ILabel("Scout")
        @IGroup(column = 0)
        public static class Weapon {
            @SliderOptions(min = 0, max = 100, onlyInt = true, units = " %")
            public static int Hitchance = 97;

            @ILabel("Min-damage")
            @SliderOptions(min = 0, max = 120, onlyInt = true, units = " dmg")
            public static int Min_damage = 73;

            @SliderOptions(min = 0, max = 100, onlyInt = true, units = " %", minText = "Auto")
            public static int Damage_accuracy = 0;

            @DropdownOptions({"Head", "Chest", "Stomach", "Arms", "Legs", "Feet"})
            public static String[] Additional_hitscan = {};

            @DropdownOptions({"Head", "Chest", "Stomach", "Arms", "Legs", "Feet"})
            public static String[] Additional_points = {};
        }

        @IGroup(column = 1)
        public static class Extra {
            @IKeybind(Keys.KEY_NONE)
            @DropdownOptions({"Off", "Slide", "Full", "Quick"})
            public static String Autostop = "Slide";

            public static boolean Predictive = false;
            public static boolean Only_when_lethal = false;
            public static boolean Autoscope = false;

            @DropdownOptions({"Off", "Default", "Prefer", "Force"})
            public static String Safepoint_mode = "Off";

            @DropdownOptions({"Head", "Chest", "Stomach", "Arms", "Legs", "Feet"})
            public static String[] Force_safepoint = {};

            public static boolean Ignore_limbs_when_target_moving = false;
            public static boolean Jumpscout = false;
            public static boolean Refine_shot = false;

            @IKeybind(Keys.KEY_NONE)
            public static boolean Silent = false;
        }

        @ILabel("Aimbot")
        @IGroup(column = 2)
        public static class Aimbot_group {
            @IKeybind(Keys.KEY_NONE)
            public static boolean Aimbot = true;

            public static boolean Autofire = true;
            public static boolean Backtrack = false;

            @IKeybind(Keys.KEY_NONE)
            public static boolean Force_safepoint = false;

            public static boolean Nospread = false;

            @IKeybind(Keys.KEY_NONE)
            public static boolean Headshot_only = false;

            public static boolean Knifebot = false;
            public static boolean Zeusbot = false;
            public static boolean Extended_backtrack = false;
        }
    }

    @ILabel("Aimbot")
    @ISubPage(order = 2, icon = 'Z')
    public static class Aimbot_AWP {
        @ILabel("AWP")
        @IGroup(column = 0)
        public static class Weapon {
            @SliderOptions(min = 0, max = 100, onlyInt = true, units = " %")
            public static int Hitchance = 97;

            @ILabel("Min-damage")
            @SliderOptions(min = 0, max = 120, onlyInt = true, units = " dmg")
            public static int Min_damage = 73;

            @SliderOptions(min = 0, max = 100, onlyInt = true, units = " %", minText = "Auto")
            public static int Damage_accuracy = 0;

            @DropdownOptions({"Head", "Chest", "Stomach", "Arms", "Legs", "Feet"})
            public static String[] Additional_hitscan = {};

            @DropdownOptions({"Head", "Chest", "Stomach", "Arms", "Legs", "Feet"})
            public static String[] Additional_points = {};
        }

        @IGroup(column = 1)
        public static class Extra {
            @IKeybind(Keys.KEY_NONE)
            @DropdownOptions({"Off", "Slide", "Full", "Quick"})
            public static String Autostop = "Slide";

            public static boolean Predictive = false;
            public static boolean Only_when_lethal = false;
            public static boolean Autoscope = false;

            @DropdownOptions({"Off", "Default", "Prefer", "Force"})
            public static String Safepoint_mode = "Off";

            @DropdownOptions({"Head", "Chest", "Stomach", "Arms", "Legs", "Feet"})
            public static String[] Force_safepoint = {};

            public static boolean Ignore_limbs_when_target_moving = false;
            public static boolean Jumpscout = false;
            public static boolean Refine_shot = false;

            @IKeybind(Keys.KEY_NONE)
            public static boolean Silent = false;
        }

        @ILabel("Aimbot")
        @IGroup(column = 2)
        public static class Aimbot_group {
            @IKeybind(Keys.KEY_NONE)
            public static boolean Aimbot = true;

            public static boolean Autofire = true;
            public static boolean Backtrack = false;

            @IKeybind(Keys.KEY_NONE)
            public static boolean Force_safepoint = false;

            public static boolean Nospread = false;

            @IKeybind(Keys.KEY_NONE)
            public static boolean Headshot_only = false;

            public static boolean Knifebot = false;
            public static boolean Zeusbot = false;
            public static boolean Extended_backtrack = false;
        }
    }

    @ILabel("Aimbot")
    @ISubPage(order = 3, icon = 'A')
    public static class Aimbot_Heavy_pistol {
        @ILabel("Heavy pistol")
        @IGroup(column = 0)
        public static class Weapon {
            @SliderOptions(min = 0, max = 100, onlyInt = true, units = " %")
            public static int Hitchance = 97;

            @ILabel("Min-damage")
            @SliderOptions(min = 0, max = 120, onlyInt = true, units = " dmg")
            public static int Min_damage = 73;

            @SliderOptions(min = 0, max = 100, onlyInt = true, units = " %", minText = "Auto")
            public static int Damage_accuracy = 0;

            @DropdownOptions({"Head", "Chest", "Stomach", "Arms", "Legs", "Feet"})
            public static String[] Additional_hitscan = {};

            @DropdownOptions({"Head", "Chest", "Stomach", "Arms", "Legs", "Feet"})
            public static String[] Additional_points = {};
        }

        @IGroup(column = 1)
        public static class Extra {
            @IKeybind(Keys.KEY_NONE)
            @DropdownOptions({"Off", "Slide", "Full", "Quick"})
            public static String Autostop = "Slide";

            public static boolean Predictive = false;
            public static boolean Only_when_lethal = false;
            public static boolean Autoscope = false;

            @DropdownOptions({"Off", "Default", "Prefer", "Force"})
            public static String Safepoint_mode = "Off";

            @DropdownOptions({"Head", "Chest", "Stomach", "Arms", "Legs", "Feet"})
            public static String[] Force_safepoint = {};

            public static boolean Ignore_limbs_when_target_moving = false;
            public static boolean Jumpscout = false;
            public static boolean Refine_shot = false;

            @IKeybind(Keys.KEY_NONE)
            public static boolean Silent = false;
        }

        @ILabel("Aimbot")
        @IGroup(column = 2)
        public static class Aimbot_group {
            @IKeybind(Keys.KEY_NONE)
            public static boolean Aimbot = true;

            public static boolean Autofire = true;
            public static boolean Backtrack = false;

            @IKeybind(Keys.KEY_NONE)
            public static boolean Force_safepoint = false;

            public static boolean Nospread = false;

            @IKeybind(Keys.KEY_NONE)
            public static boolean Headshot_only = false;

            public static boolean Knifebot = false;
            public static boolean Zeusbot = false;
            public static boolean Extended_backtrack = false;
        }
    }

    @ILabel("Aimbot")
    @ISubPage(order = 4, icon = 'G')
    public static class Aimbot_Pistol {
        @ILabel("Pistol")
        @IGroup(column = 0)
        public static class Weapon {
            @SliderOptions(min = 0, max = 100, onlyInt = true, units = " %")
            public static int Hitchance = 97;

            @ILabel("Min-damage")
            @SliderOptions(min = 0, max = 120, onlyInt = true, units = " dmg")
            public static int Min_damage = 73;

            @SliderOptions(min = 0, max = 100, onlyInt = true, units = " %", minText = "Auto")
            public static int Damage_accuracy = 0;

            @DropdownOptions({"Head", "Chest", "Stomach", "Arms", "Legs", "Feet"})
            public static String[] Additional_hitscan = {};

            @DropdownOptions({"Head", "Chest", "Stomach", "Arms", "Legs", "Feet"})
            public static String[] Additional_points = {};
        }

        @IGroup(column = 1)
        public static class Extra {
            @IKeybind(Keys.KEY_NONE)
            @DropdownOptions({"Off", "Slide", "Full", "Quick"})
            public static String Autostop = "Slide";

            public static boolean Predictive = false;
            public static boolean Only_when_lethal = false;
            public static boolean Autoscope = false;

            @DropdownOptions({"Off", "Default", "Prefer", "Force"})
            public static String Safepoint_mode = "Off";

            @DropdownOptions({"Head", "Chest", "Stomach", "Arms", "Legs", "Feet"})
            public static String[] Force_safepoint = {};

            public static boolean Ignore_limbs_when_target_moving = false;
            public static boolean Jumpscout = false;
            public static boolean Refine_shot = false;

            @IKeybind(Keys.KEY_NONE)
            public static boolean Silent = false;
        }

        @ILabel("Aimbot")
        @IGroup(column = 2)
        public static class Aimbot_group {
            @IKeybind(Keys.KEY_NONE)
            public static boolean Aimbot = true;

            public static boolean Autofire = true;
            public static boolean Backtrack = false;

            @IKeybind(Keys.KEY_NONE)
            public static boolean Force_safepoint = false;

            public static boolean Nospread = false;

            @IKeybind(Keys.KEY_NONE)
            public static boolean Headshot_only = false;

            public static boolean Knifebot = false;
            public static boolean Zeusbot = false;
            public static boolean Extended_backtrack = false;
        }
    }

    @ILabel("Aimbot")
    @ISubPage(order = 5, icon = 'W')
    public static class Aimbot_Rifle {
        @ILabel("Rifle")
        @IGroup(column = 0)
        public static class Weapon {
            @SliderOptions(min = 0, max = 100, onlyInt = true, units = " %")
            public static int Hitchance = 97;

            @ILabel("Min-damage")
            @SliderOptions(min = 0, max = 120, onlyInt = true, units = " dmg")
            public static int Min_damage = 73;

            @SliderOptions(min = 0, max = 100, onlyInt = true, units = " %", minText = "Auto")
            public static int Damage_accuracy = 0;

            @DropdownOptions({"Head", "Chest", "Stomach", "Arms", "Legs", "Feet"})
            public static String[] Additional_hitscan = {};

            @DropdownOptions({"Head", "Chest", "Stomach", "Arms", "Legs", "Feet"})
            public static String[] Additional_points = {};
        }

        @IGroup(column = 1)
        public static class Extra {
            @IKeybind(Keys.KEY_NONE)
            @DropdownOptions({"Off", "Slide", "Full", "Quick"})
            public static String Autostop = "Slide";

            public static boolean Predictive = false;
            public static boolean Only_when_lethal = false;
            public static boolean Autoscope = false;

            @DropdownOptions({"Off", "Default", "Prefer", "Force"})
            public static String Safepoint_mode = "Off";

            @DropdownOptions({"Head", "Chest", "Stomach", "Arms", "Legs", "Feet"})
            public static String[] Force_safepoint = {};

            public static boolean Ignore_limbs_when_target_moving = false;
            public static boolean Jumpscout = false;
            public static boolean Refine_shot = false;

            @IKeybind(Keys.KEY_NONE)
            public static boolean Silent = false;
        }

        @ILabel("Aimbot")
        @IGroup(column = 2)
        public static class Aimbot_group {
            @IKeybind(Keys.KEY_NONE)
            public static boolean Aimbot = true;

            public static boolean Autofire = true;
            public static boolean Backtrack = false;

            @IKeybind(Keys.KEY_NONE)
            public static boolean Force_safepoint = false;

            public static boolean Nospread = false;

            @IKeybind(Keys.KEY_NONE)
            public static boolean Headshot_only = false;

            public static boolean Knifebot = false;
            public static boolean Zeusbot = false;
            public static boolean Extended_backtrack = false;
        }
    }

    @ILabel("Anti-aim")
    @ISubPage(order = 6)
    public static class Anti_aim {
        @IGroup(column = 0, order = 0)
        public static class Standing {
            @DropdownOptions({"None", "Down", "Up", "Zero"})
            public static String Pitch = "Down";

            @DropdownOptions({"None", "Backwards", "Forwards", "Spin"})
            public static String Yaw = "Backwards";

            public static boolean Yaw_add = false;
            public static boolean At_fov_target = false;
            public static boolean Freestand = false;

            @DropdownOptions({"None", "Static", "Jitter", "Random"})
            public static String Fake_type = "None";

            public static boolean Freestand_fake = false;

            @DropdownOptions({"Always on", "On enemy visible", "On peek", "Off"})
            public static String Fake_lag = "On enemy visible";

            @SliderOptions(min = 0, max = 14, onlyInt = true)
            public static int Base_amount = 14;

            @SliderOptions(min = 0, max = 14, onlyInt = true)
            public static int Limit = 14;

            public static boolean Spin = false;
            public static boolean Jitter = false;
        }

        @IGroup(column = 1, order = 0, height = 234)
        public static class Moving {
            @DropdownOptions({"None", "Down", "Up", "Zero"})
            public static String Pitch = "None";

            @DropdownOptions({"None", "Backwards", "Forwards", "Spin"})
            public static String Yaw = "None";

            public static boolean Yaw_add = false;
            public static boolean At_fov_target = false;
            public static boolean Freestand = false;

            @DropdownOptions({"None", "Static", "Jitter", "Random"})
            public static String Fake_type = "None";

            public static boolean Freestand_fake = false;

            @DropdownOptions({"Always on", "On enemy visible", "On peek", "Off"})
            public static String Fake_lag = "On enemy visible";
        }

        @IGroup(column = 1, order = 1)
        public static class Air {
            @DropdownOptions({"None", "Down", "Up", "Zero"})
            public static String Pitch = "None";

            @DropdownOptions({"None", "Backwards", "Forwards", "Spin"})
            public static String Yaw = "None";

            public static boolean Yaw_add = false;
            public static boolean At_fov_target = false;
            public static boolean Freestand = false;

            @DropdownOptions({"None", "Static", "Jitter", "Random"})
            public static String Fake_type = "None";

            public static boolean Freestand_fake = false;

            @DropdownOptions({"Always on", "On enemy visible", "On peek", "Off"})
            public static String Fake_lag = "On enemy visible";
        }

        @IGroup(column = 2)
        public static class General {
            @ILabel("Anti-aim")
            @IKeybind(Keys.KEY_NONE)
            public static boolean Anti_aim = true;

            @DropdownOptions({"On shot", "On peek", "On land"})
            public static String[] Additional_fakelag = {};

            public static boolean Antiaim_override = false;

            @IKeybind(Keys.KEY_NONE)
            public static boolean Back = false;

            @IKeybind(Keys.KEY_NONE)
            public static boolean Left = false;

            @IKeybind(Keys.KEY_NONE)
            public static boolean Right = false;

            @ILabel("Shot Antiaim")
            @DropdownOptions({"Auto", "Off", "On"})
            public static String Shot_Antiaim = "Auto";
        }
    }
}
