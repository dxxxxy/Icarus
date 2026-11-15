package studio.dreamys.test.fatality;

import studio.dreamys.icarus.annotation.ILabel;
import studio.dreamys.icarus.annotation.field.DropdownOptions;
import studio.dreamys.icarus.annotation.field.IAttach;
import studio.dreamys.icarus.annotation.field.IKeybind;
import studio.dreamys.icarus.annotation.field.IShowIf;
import studio.dreamys.icarus.annotation.field.SliderOptions;
import studio.dreamys.icarus.annotation.type.IGroup;
import studio.dreamys.icarus.annotation.type.IPage;
import studio.dreamys.icarus.annotation.type.ISubPage;
import studio.dreamys.icarus.input.Keys;
import studio.dreamys.icarus.settings.ListModel;
import studio.dreamys.test.TestModCommon;

import java.awt.Color;

/** The VISUALS tab: ESP, MODELS and MISC, as first shown in the reference. */
@IPage(order = 1)
public class Visuals {
    @ISubPage(order = 0)
    public static class ESP {
        @IGroup(column = 0)
        public static class Player {
            public static boolean Esp = false;
            @IAttach
            public static Color Esp_color = new Color(28, 22, 65);

            public static boolean Box = false;
            @IAttach
            public static Color Box_color = new Color(255, 255, 255);

            public static boolean Skeleton = false;
            @IAttach
            public static Color Skeleton_color = new Color(255, 255, 255);

            public static boolean History_skeleton = false;
            @IAttach
            public static Color History_skeleton_color = new Color(255, 255, 255);

            public static boolean Name = false;
            @IAttach
            public static Color Name_color = new Color(255, 255, 255);

            public static boolean Weapon = false;
            @IAttach
            public static Color Weapon_color = new Color(255, 255, 255);

            @IShowIf("Weapon")
            public static boolean Weapon_icons = false;

            public static boolean Health = false;
            @IAttach
            public static Color Health_color = new Color(140, 0, 0);
            @IAttach
            public static Color Health_color2 = new Color(150, 200, 30);

            public static boolean Armor = false;
            @IAttach
            public static Color Armor_color = new Color(255, 255, 255);

            public static boolean Ammo = false;
            @IAttach
            public static Color Ammo_color = new Color(255, 255, 255);

            public static boolean Pingspike = false;
            @IAttach
            public static Color Pingspike_color = new Color(255, 255, 255);

            @DropdownOptions({"Money", "Armor", "Scoped", "Hit", "Defuser", "Bomb", "LC", "Fake duck"})
            public static String[] Info = {};

            public static boolean Sound = false;
            @IAttach
            public static Color Sound_color = new Color(255, 255, 255);

            public static boolean Glow = false;

            public static boolean Team_glow = false;
            @IAttach
            public static Color Team_glow_color = new Color(255, 255, 255);

            public static boolean Enemy_glow = false;
            @IAttach
            public static Color Enemy_glow_color = new Color(255, 255, 255);

            public static boolean Local_glow = false;
            @IAttach
            public static Color Local_glow_color = new Color(255, 255, 255);

        }

        @IGroup(column = 1)
        public static class World {
            @DropdownOptions({"Weapons", "Grenades", "Bomb"})
            public static String[] Esp = {};
            @IAttach
            public static Color Esp_color = new Color(255, 255, 255);

            @DropdownOptions({"Weapons", "Grenades", "Bomb"})
            public static String[] Glow = {};
            @IAttach
            public static Color Glow_color = new Color(255, 255, 255);

        }
    }

    @ISubPage(order = 1)
    public static class Models {
        @IGroup(column = 0, order = 0, height = 234)
        public static class General {
            @IKeybind(Keys.KEY_NONE)
            public static boolean Chams = true;

            public static boolean Skip_occlusion = true;

            public static boolean Custom_bloom_scale = true;

            @SliderOptions(min = 0, max = 100, onlyInt = true, units = "")
            public static int Scale = 0;

            public static boolean Custom_model_brightness = false;

        }

        @IGroup(column = 1, order = 0, height = 234)
        public static class Enemy {
            @DropdownOptions({"Off", "Normal", "Flat", "Contour"})
            public static String Player = "Off";
            @IAttach
            public static Color Player_color = new Color(255, 255, 255);

            public static boolean Hidden = false;
            @IAttach
            public static Color Hidden_color = new Color(255, 255, 255);

            public static boolean History = false;
            @IAttach
            public static Color History_color = new Color(255, 255, 255);

            public static boolean Shot_record = false;
            @IAttach
            public static Color Shot_record_color = new Color(255, 255, 255);

            @DropdownOptions({"Off", "Normal", "Flat", "Contour"})
            public static String Ragdoll = "Off";
            @IAttach
            public static Color Ragdoll_color = new Color(255, 255, 255);

        }

        @IGroup(column = 0, order = 1)
        public static class Team {
            @DropdownOptions({"Off", "Normal", "Flat", "Contour"})
            public static String Player = "Off";
            @IAttach
            public static Color Player_color = new Color(255, 255, 255);

            public static boolean Hidden = false;
            @IAttach
            public static Color Hidden_color = new Color(255, 255, 255);

            @DropdownOptions({"Off", "Normal", "Flat", "Contour"})
            public static String Ragdoll = "Off";
            @IAttach
            public static Color Ragdoll_color = new Color(255, 255, 255);

        }

        @IGroup(column = 1, order = 1)
        public static class Local {
            @DropdownOptions({"Off", "Normal", "Flat", "Contour"})
            public static String Type = "Off";
            @IAttach
            public static Color Type_color = new Color(255, 255, 255);

            @DropdownOptions({"Off", "Normal", "Flat", "Contour"})
            public static String Fake_type = "Off";
            @IAttach
            public static Color Fake_type_color = new Color(255, 255, 255);

            @DropdownOptions({"Off", "Normal", "Flat", "Contour"})
            public static String Arms = "Off";
            @IAttach
            public static Color Arms_color = new Color(255, 255, 255);

            public static boolean Interpolate = false;

            public static boolean Pulsate = false;

            @ILabel("Agent T")
            @DropdownOptions({"None", "Ground Rebel", "Phoenix", "Elite Crew"})
            public static String Agent_T = "None";

            @ILabel("Agent CT")
            @DropdownOptions({"None", "SAS", "FBI", "SEAL"})
            public static String Agent_CT = "None";

        }
    }

    @ISubPage(order = 2)
    public static class Misc {
        @IGroup(column = 0)
        public static class Local {
            @IKeybind(Keys.KEY_V)
            public static boolean Thirdperson = true;

            public static boolean Force_when_dead = true;

            public static boolean Disable_on_grenade = false;

            @SliderOptions(min = 30, max = 300, onlyInt = true, units = "")
            public static int Distance = 180;

            @SliderOptions(min = 0, max = 100, onlyInt = true, units = " %")
            public static int Transparency_in_scope = 50;

            public static boolean Fov_changer = true;

            @SliderOptions(min = 60, max = 140, onlyInt = true, units = "")
            public static int Fov = 97;

            public static boolean Remove_zoom = true;

            public static boolean Viewmodel_fov_changer = false;

            public static boolean Remove_visual_punch = true;

            @DropdownOptions({"Off", "Static", "Dynamic"})
            public static String Remove_scope = "Static";

            public static boolean Remove_blur = true;

            public static boolean Remove_smoke = true;

            public static boolean Remove_flash = true;

            public static boolean Penetration_crosshair = true;

            public static boolean Force_crosshair = true;

            @DropdownOptions({"Off", "Static", "Rainbow", "Rainbow ( rotating )"})
            public static String Spread_circle = "Rainbow ( rotating )";

            @ILabel("OOF arrows")
            public static boolean OOF_arrows = false;
            @IAttach
            public static Color OOF_arrows_color = new Color(255, 255, 255);

            public static boolean Grenade_prediction = false;
            @IAttach
            public static Color Grenade_prediction_color = new Color(255, 255, 255);
            @IAttach
            public static Color Grenade_prediction_color2 = new Color(255, 255, 255);

        }

        @IGroup(column = 1)
        public static class Various {
            public static boolean Darkmode = false;

            @ILabel("Prop Transparency")
            @SliderOptions(min = 0, max = 100, onlyInt = true, units = " %")
            public static int Prop_Transparency = 0;

            @DropdownOptions({"None", "Cross", "Damage"})
            public static String Hitmarker = "None";

            @DropdownOptions({"None", "Arena", "Bubble", "Bell"})
            public static String Hitsound = "None";

            @DropdownOptions({"DT", "ST", "PING / X", "FAKE", "LC"})
            public static String[] Indicators = {"DT", "ST", "PING / X"};

            @ILabel("Show Impacts")
            public static boolean Show_Impacts = false;

            public static boolean Radar = false;

            @ILabel("Auto-accept")
            public static boolean Auto_accept = false;

            public static boolean Reveal_ranks = false;

            public static boolean Hurt_log = false;

            public static boolean Buy_log = false;

            public static boolean Preserve_deathnotices = false;

            public static boolean Clan_tag = false;

            @SliderOptions(min = 0, max = 500, onlyInt = true, units = " %")
            public static int Ragdoll_thrust = 100;

            public static boolean Party_mode = false;

        }

        @IGroup(column = 2)
        public static class Beams {
            public static boolean Enabled = false;

            @SliderOptions(min = 1, max = 30, onlyInt = true, units = "")
            public static int Width = 8;

            public static boolean Team = false;
            @IAttach
            public static Color Team_color = new Color(255, 255, 255);

            public static boolean Enemy = false;
            @IAttach
            public static Color Enemy_color = new Color(255, 255, 255);

            public static boolean Local = false;
            @IAttach
            public static Color Local_color = new Color(255, 255, 255);

        }
    }
}
