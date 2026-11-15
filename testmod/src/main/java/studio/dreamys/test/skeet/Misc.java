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
import studio.dreamys.test.TestModCommon;

import java.awt.Color;

/** The MISC tab, as in the reference. */
@IPage(icon = "misc", order = 4)
public class Misc {
    @IGroup(column = 0, order = 0)
    public static class Miscellaneous {
        @ILabel("Override FOV")
        @SliderOptions(min = 1, max = 135, onlyInt = true, units = "°")
        public static int Override_FOV = 90;

        @ILabel("Override zoom FOV")
        @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
        public static int Override_zoom_FOV = 100;

        public static boolean Knifebot = false;
        public static boolean Zeusbot = false;
        public static boolean Automatic_weapons = false;
        public static boolean Reveal_competitive_ranks = false;
        public static boolean Reveal_Overwatch_players = false;

        @ILabel("Auto-accept matchmaking")
        public static boolean Auto_accept_matchmaking = true;

        public static boolean Clan_tag_spammer = false;
        public static boolean Log_weapon_purchases = false;
        public static boolean Log_damage_dealt = false;

        @IKeybind(Keys.KEY_NONE)
        public static boolean Automatic_grenade_release = false;

        @IKeybind(Keys.KEY_NONE)
        public static boolean Ping_spike = false;

        @IKeybind(Keys.KEY_NONE)
        public static Label Free_look;

        public static boolean Persistent_kill_feed = false;

        @IKeybind(Keys.KEY_R)
        public static Label Last_second_defuse;

        public static Runnable Steal_player_name = () -> TestModCommon.notify("Steal player name", "Stole a name.");

        @ILabel("Dump MM wins")
        public static Runnable Dump_MM_wins = () -> TestModCommon.notify("Dump MM wins", "Nothing to dump.");
    }

    @IGroup(column = 1, order = 0, height = 314 / 1.25)
    public static class Movement {
        public static boolean Standalone_quick_stop = false;

        @IWarning
        public static boolean Infinite_duck = false;

        public static boolean Easy_strafe = false;
        public static boolean Bunny_hop = false;
        public static boolean Air_strafe = false;

        @DropdownOptions({"Off", "Normal", "Spam"})
        public static String Air_duck = "Off";

        @IKeybind(value = Keys.KEY_NONE, mode = KeybindMode.ON_HOTKEY)
        public static boolean Blockbot = false;

        @IKeybind(value = Keys.MOUSE_5, mode = KeybindMode.ON_HOTKEY)
        public static boolean Jump_at_edge = false;

        public static boolean Fast_walk = false;
    }

    @IGroup(column = 1, order = 1)
    public static class Settings {
        @IKeybind(Keys.KEY_DELETE)
        public static Label Menu_key;

        public static Color Menu_color = new Color(16, 168, 246);

        @ILabel("DPI scale")
        @DropdownOptions({"100%", "125%", "150%", "175%", "200%"})
        public static String DPI_scale = "125%";

        @ILabel("Anti-untrusted")
        public static boolean Anti_untrusted = true;

        @ILabel("Hide from OBS")
        public static boolean Hide_from_OBS = false;

        @ILabel("Low FPS warning")
        public static boolean Low_FPS_warning = false;

        public static boolean Lock_menu_layout = false;
        public static boolean Allow_unsafe_scripts = true;

        public static Runnable Reset_menu_layout = () -> TestModCommon.notify("Menu", "Layout reset.");

        public static Runnable Unload = () -> TestModCommon.notify("Menu", "Unload is disabled in the test mod.");
    }
}
