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

/** The MISC tab, as in the reference (no sub-tabs). */
@IPage(order = 2)
public class Misc {
    @IGroup(column = 0)
    public static class Movement {
            public static boolean Bhop = false;

            @DropdownOptions({"None", "Legit", "Rage", "Directional"})
            public static String Autostrafer = "None";

            @ILabel("Peek Assist")
            @IKeybind(Keys.KEY_NONE)
            public static boolean Peek_Assist = false;

            public static boolean Infinite_duck_stamina = false;

            @IKeybind(Keys.KEY_NONE)
            public static boolean Slide = false;

            @IKeybind(Keys.KEY_NONE)
            public static boolean Fake_duck = false;

            @ILabel("Bypass sv_pure")
            public static boolean Bypass_sv_pure = false;

    }

    @IGroup(column = 1)
    public static class Buy_bot {
            @DropdownOptions({"None", "Auto", "Scout", "AWP"})
            public static String Primary = "None";

            @DropdownOptions({"None", "Heavy pistol", "Dual berettas", "Five-seven"})
            public static String Secondary = "None";

            @DropdownOptions({"None", "Kevlar", "Kevlar + Helmet"})
            public static String Armor = "None";

            public static boolean Defuser = false;

            public static boolean Taser = false;

            public static boolean Grenades = false;

    }

    @IGroup(column = 2)
    public static class Lua {
        @ILabel("")
        public static final ListModel Scripts = new ListModel("slowwalk.lua").height(137);

        static {
            Scripts.select("slowwalk.lua");
        }

        public static Runnable Load = () -> TestModCommon.notify("Lua", "Loaded " + Scripts.getSelected() + ".");

        @ILabel("")
        public static final ListModel Loaded = new ListModel().height(137);

        public static Runnable Unload = () -> TestModCommon.notify("Lua", "Unloaded.");

        public static Runnable Reload = () -> TestModCommon.notify("Lua", "Reloaded.");
    }
}
