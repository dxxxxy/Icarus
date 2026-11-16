package studio.dreamys.icarus.support;

import studio.dreamys.icarus.annotation.field.DropdownOptions;
import studio.dreamys.icarus.annotation.field.IKeybind;
import studio.dreamys.icarus.annotation.field.SliderOptions;
import studio.dreamys.icarus.annotation.type.IGroup;
import studio.dreamys.icarus.annotation.type.IPage;
import studio.dreamys.icarus.annotation.type.ISubPage;
import studio.dreamys.icarus.input.Keys;
import studio.dreamys.icarus.settings.ListModel;

import java.awt.Color;

/** Settings used by the core tests. {@link #reset()} restores the initial values (fields are static). */
public final class Fixture {
    private Fixture() {
    }

    @IPage(order = 0)
    public static class Combat {
        @IGroup(order = 0)
        public static class Kill_Aura {
            @IKeybind(Keys.KEY_6)
            public static Runnable Attack = () -> attacks++;

            @IKeybind(Keys.KEY_7)
            public static boolean Enabled = true;

            @DropdownOptions({"Option 1", "Option 2", "Option 3"})
            public static String Mode = "Option 1";

            @DropdownOptions({"Head", "Body", "Legs"})
            public static String[] Targets = {"Head"};

            public static String Name = "Hello";

            @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
            public static int Range = 50;
        }

        @IGroup(order = 1)
        public static class Second {
            public static Color Tint = new Color(255, 0, 0, 255);

            public static final ListModel Scripts = new ListModel("a.lua", "b.lua");

            @SliderOptions(min = 0, max = 1)
            public static double Smooth = 0.5;
        }
    }

    /** One column: a fixed-height group above a group too tall to fit. */
    @IPage(order = 2)
    public static class Tall {
        @IGroup(column = 0, order = 0, height = 300)
        public static class Fixed {
            @IKeybind(Keys.KEY_8)
            public static studio.dreamys.icarus.settings.Label Activation;

            public static boolean A, B, C;

            @studio.dreamys.icarus.annotation.field.IShowIf("A")
            public static boolean Child;
        }

        @IGroup(column = 0, order = 1)
        public static class Long {
            public static boolean Row0;
            public static boolean Row1;
            public static boolean Row2;
            public static boolean Row3;
            public static boolean Row4;
            public static boolean Row5;
            public static boolean Row6;
            public static boolean Row7;
            public static boolean Row8;
            public static boolean Row9;
            public static boolean Row10;
            public static boolean Row11;
            public static boolean Row12;
            public static boolean Row13;
            public static boolean Row14;
            public static boolean Row15;
            public static boolean Row16;
            public static boolean Row17;
            public static boolean Row18;
            public static boolean Row19;
            public static boolean Row20;
            public static boolean Row21;
            public static boolean Row22;
            public static boolean Row23;
            public static boolean Row24;
            public static boolean Row25;
            public static boolean Row26;
            public static boolean Row27;
            public static boolean Row28;
            public static boolean Row29;
            public static boolean Row30;
            public static boolean Row31;
            public static boolean Row32;
            public static boolean Row33;
            public static boolean Row34;
            public static boolean Row35;
            public static boolean Row36;
            public static boolean Row37;
            public static boolean Row38;
            public static boolean Row39;
        }
    }

    @IPage(order = 1)
    public static class Visuals {
        @ISubPage(order = 0)
        public static class Esp {
            @IGroup
            public static class Players {
                public static boolean Boxes = false;
            }
        }

        @ISubPage(order = 1)
        public static class Models {
            @IGroup
            public static class Chams {
                public static boolean Enabled = false;
            }
        }
    }

    public static int attacks;

    public static void reset() {
        attacks = 0;
        Combat.Kill_Aura.Enabled = true;
        Combat.Kill_Aura.Mode = "Option 1";
        Combat.Kill_Aura.Targets = new String[]{"Head"};
        Combat.Kill_Aura.Name = "Hello";
        Combat.Kill_Aura.Range = 50;
        Combat.Second.Tint = new Color(255, 0, 0, 255);
        Combat.Second.Scripts.select(null);
        Combat.Second.Smooth = 0.5;
        Visuals.Esp.Players.Boxes = false;
        Visuals.Models.Chams.Enabled = false;
    }
}
