package studio.dreamys.test.skeet;

import studio.dreamys.icarus.annotation.ILabel;
import studio.dreamys.icarus.annotation.field.DropdownOptions;
import studio.dreamys.icarus.annotation.field.SliderOptions;
import studio.dreamys.icarus.annotation.type.IGroup;
import studio.dreamys.icarus.annotation.type.IPage;
import studio.dreamys.icarus.settings.ListModel;

/** The SKINS tab, as in the reference. */
@IPage(icon = "skins", order = 5)
public class Skins {
    @IGroup(column = 0, order = 0, height = 147 / 1.25)
    public static class Knife_options {
        public static boolean Override_knife = false;

        @ILabel("")
        @DropdownOptions({"Bayonet", "Flip Knife", "Gut Knife", "Karambit", "M9 Bayonet", "Huntsman Knife", "Falchion Knife",
                "Bowie Knife", "Butterfly Knife", "Shadow Daggers", "Ursus Knife", "Navaja Knife", "Stiletto Knife", "Talon Knife"})
        public static String Knife = "Bayonet";
    }

    @IGroup(column = 0, order = 1)
    public static class Glove_options {
        public static boolean Override_gloves = false;

        @ILabel("")
        @DropdownOptions({"Bloodhound", "Sport", "Driver", "Hand Wraps", "Moto", "Specialist", "Hydra"})
        public static String Gloves = "Bloodhound";

        @ILabel("")
        public static final ListModel Glove_skin = new ListModel("Bronzed", "Charred", "Guerrilla", "Snakebite")
                .searchable(true).height(294 / 1.25);

        static {
            Glove_skin.select("Bronzed");
        }
    }

    @IGroup(column = 1)
    public static class Weapon_skin {
        public static boolean Enabled = false;

        @ILabel("StatTrak")
        public static boolean StatTrak = false;

        @SliderOptions(min = 0, max = 100, onlyInt = true, units = "%")
        public static int Quality = 100;

        @SliderOptions(min = 0, max = 1000, onlyInt = true)
        public static int Seed = 0;

        public static boolean Filter_by_weapon = false;

        @ILabel("")
        public static final ListModel Skin = new ListModel("-", "Abyss", "Acheron", "Acid Fade", "Acid Wash", "Aerial", "Afterimage",
                "Agent", "Airlock", "Akihabara Accept", "Akoben", "Akoben", "Aloha", "Aloha", "Amber Fade", "Amber Fade", "Ancient Earth",
                "Ancient Lore", "Ancient Visions", "Anodized Gunmetal", "Anodized Navy", "Antique", "Apocalypto", "Aquamarine Revenge")
                .searchable(true).height(379 / 1.25);

        static {
            Skin.select("-");
        }
    }
}
