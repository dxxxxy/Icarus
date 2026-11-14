package studio.dreamys.test.ui.page;

import studio.dreamys.icarus.annotation.field.DropdownOptions;
import studio.dreamys.icarus.annotation.field.SliderOptions;
import studio.dreamys.icarus.annotation.type.IGroup;
import studio.dreamys.icarus.annotation.type.IPage;
import studio.dreamys.icarus.annotation.type.ISubPage;

import java.awt.Color;

@IPage(value = 't', order = 1)
public class Visuals {
    @ISubPage(order = 0)
    public static class ESP {
        @IGroup(column = 0)
        public static class Players {
            public static boolean Boxes = true;
            public static Color Box_color = new Color(29, 122, 215);
            public static boolean Names = true;
            public static boolean Health = false;
            public static Color Health_color = new Color(120, 200, 60);

            @DropdownOptions({"Off", "Normal", "Flat", "Contour"})
            public static String Glow = "Off";
        }

        @IGroup(column = 1)
        public static class World {
            public static boolean Dropped_items = false;
            public static Color Item_color = new Color(255, 255, 255, 160);

            @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
            public static int Brightness = 100;
        }
    }

    @ISubPage(order = 1)
    public static class Models {
        @IGroup(column = 0)
        public static class Chams {
            public static boolean Enabled = false;
            public static Color Visible = new Color(212, 6, 90);
            public static Color Hidden = new Color(60, 40, 180, 140);

            @DropdownOptions({"Normal", "Flat", "Glow"})
            public static String Material = "Normal";
        }
    }
}
