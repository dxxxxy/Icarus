package studio.dreamys.test.skeet;

import studio.dreamys.icarus.annotation.ILabel;
import studio.dreamys.icarus.annotation.field.DropdownOptions;
import studio.dreamys.icarus.annotation.field.IAttach;
import studio.dreamys.icarus.annotation.field.IKeybind;
import studio.dreamys.icarus.annotation.field.SliderOptions;
import studio.dreamys.icarus.annotation.type.IGroup;
import studio.dreamys.icarus.annotation.type.IPage;
import studio.dreamys.icarus.input.Keys;
import studio.dreamys.icarus.settings.KeybindMode;
import studio.dreamys.icarus.settings.Label;

import java.awt.Color;

/** The VISUALS tab, as in the reference (groups are longer than they are tall and scroll). */
@IPage(icon = "visuals", order = 3)
public class Visuals {
    @ILabel("Player ESP")
    @IGroup(column = 0, order = 0, height = 420 / 1.25)
    public static class Player_ESP {
        @IKeybind(Keys.KEY_LSHIFT)
        public static Label Activation_type;

        public static boolean Teammates = false;
        public static boolean Dormant = false;

        public static boolean Bounding_box = false;
        @IAttach
        public static Color Bounding_box_color = new Color(255, 255, 255);

        public static boolean Health_bar = false;

        public static boolean Name = false;
        @IAttach
        public static Color Name_color = new Color(255, 255, 255);

        public static boolean Flags = false;
        public static boolean Weapon_text = false;

        public static boolean Weapon_icon = false;
        @IAttach
        public static Color Weapon_icon_color = new Color(255, 255, 255);

        public static boolean Ammo = false;
        @IAttach
        public static Color Ammo_color = new Color(80, 140, 220);

        public static boolean Distance = false;

        public static boolean Glow = false;
        @IAttach
        public static Color Glow_color = new Color(180, 60, 120);

        public static boolean Hit_marker = false;
        public static boolean Hit_marker_sound = false;

        public static boolean Visualize_sounds = false;
        @IAttach
        public static Color Visualize_sounds_color = new Color(232, 60, 60);

        public static boolean Line_of_sight = false;
        @IAttach
        public static Color Line_of_sight_color = new Color(224, 0, 0);

        public static boolean Money = false;

        public static boolean Skeleton = false;
        @IAttach
        public static Color Skeleton_color = new Color(240, 240, 160);

        @ILabel("Out of FOV arrow")
        public static boolean Out_of_FOV_arrow = false;
        @IAttach
        public static Color Out_of_FOV_arrow_color = new Color(255, 255, 255);
    }

    @IGroup(column = 0, order = 1)
    public static class Colored_models {
        public static boolean Player = false;
        @IAttach
        public static Color Player_color = new Color(150, 200, 60);

        public static boolean Player_behind_wall = false;
        @IAttach
        public static Color Player_behind_wall_color = new Color(60, 120, 200);

        public static boolean Teammate = false;
        @IAttach
        public static Color Teammate_color = new Color(60, 60, 60);

        public static boolean Teammate_behind_wall = false;
        @IAttach
        public static Color Teammate_behind_wall_color = new Color(255, 255, 255);

        public static boolean Local_player = false;
        @IAttach
        public static Color Local_player_color = new Color(58, 58, 58);

        public static boolean Local_player_fake = false;
        @IAttach
        public static Color Local_player_fake_color = new Color(16, 16, 16);

        public static boolean Ragdolls = false;

        public static boolean Hands = false;
        @IAttach
        public static Color Hands_color = new Color(255, 255, 255);

        public static boolean Disable_model_occlusion = false;
        public static boolean Shadow = false;
        public static boolean Props = false;
    }

    @ILabel("Other ESP")
    @IGroup(column = 1, order = 0, height = 254 / 1.25)
    public static class Other_ESP {
        public static boolean Radar = false;

        public static boolean Dropped_weapons = false;
        @IAttach
        public static Color Dropped_weapons_color = new Color(255, 255, 255);

        @ILabel("")
        @DropdownOptions({"Off", "Icon", "Text", "Glow"})
        public static String Dropped_weapons_style = "Off";

        public static boolean Dropped_weapon_ammo = false;

        public static boolean Grenades = false;
        @IAttach
        public static Color Grenades_color = new Color(255, 255, 255);

        public static boolean Inaccuracy_overlay = false;
        @IAttach
        public static Color Inaccuracy_overlay_color = new Color(32, 32, 32);

        public static boolean Recoil_overlay = false;
        public static boolean Crosshair = false;

        public static boolean Bomb = false;
        @IAttach
        public static Color Bomb_color = new Color(150, 200, 60);

        public static boolean Grenade_trajectory = false;
        @IAttach
        public static Color Grenade_trajectory_color = new Color(60, 140, 240);

        public static boolean Grenade_proximity_warning = false;
        public static boolean Spectators = false;
    }

    @IGroup(column = 1, order = 1)
    public static class Effects {
        public static boolean Remove_flashbang_effects = false;
        public static boolean Remove_smoke_grenades = false;
        public static boolean Remove_fog = false;
        public static boolean Remove_skybox = false;

        @DropdownOptions({"Off", "Remove shake", "Remove all"})
        public static String Visual_recoil_adjustment = "Off";

        @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
        public static int Transparent_walls = 100;

        @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
        public static int Transparent_props = 100;

        @DropdownOptions({"Off", "Night mode", "Full bright"})
        public static String Brightness_adjustment = "Off";

        public static boolean Remove_scope_overlay = false;
        public static boolean Instant_scope = false;
        public static boolean Disable_post_processing = false;

        @ILabel("Force third person (alive)")
        @IKeybind(value = Keys.KEY_NONE, mode = KeybindMode.TOGGLE)
        public static boolean Force_third_person_alive = false;
    }
}
