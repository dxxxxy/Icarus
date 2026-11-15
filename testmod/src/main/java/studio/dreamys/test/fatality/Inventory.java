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

/** The INVENTORY tab, as in the reference (no sub-tabs). */
@IPage(order = 3)
public class Inventory {
    @IGroup(column = 0)
    public static class Weapons {
        @ILabel("")
        public static final ListModel Weapon = new ListModel("Sporty Gloves", "Driver Gloves", "Handwraps", "Motorcycle Gloves",
                "Specialist Gloves", "Hydra Gloves", "Bayonet", "Flip Knife", "Gut Knife", "Karambit", "M9 Bayonet", "Huntsman Knife",
                "Falchion Knife", "Bowie Knife", "Butterfly Knife", "Shadow Daggers", "Ursus Knife", "Navaja Knife", "Stiletto Knife",
                "Talon Knife", "Paracord Knife", "Survival Knife", "Nomad Knife", "Skeleton Knife", "Classic Knife", "AK-47", "AUG", "AWP")
                .searchable(true).height(493);

        static {
            Weapon.select("Karambit");
        }
    }

    @ILabel("Paint kit")
    @IGroup(column = 1)
    public static class Paint_kit {
        @ILabel("")
        public static final ListModel Kit = new ListModel("Arctic Camo", "Arctic Wolf", "Arctic Wolf", "Arid", "Aristocrat", "Armor Core",
                "Army Mesh", "Army Recon", "Army Sheen", "Ash Wood", "Asiimov", "Asiimov", "Asiimov", "Asiimov", "Asiimov", "Assault",
                "Asterion", "Atheris", "Atlas").searchable(true).height(311);

        static {
            Kit.select("Asiimov");
        }

        @SliderOptions(min = 0, max = 1000, onlyInt = true, units = "")
        public static int Seed = 0;

        @SliderOptions(min = 0, max = 100, onlyInt = true, units = " %")
        public static int Wear = 0;

        @SliderOptions(min = -1, max = 10000, onlyInt = true, units = " kills")
        public static int Stattrak = -1;

        @ILabel("CT")
        public static boolean CT = true;

        public static boolean T = true;

        public static Runnable Add = () -> TestModCommon.notify("Inventory", "Added " + Kit.getSelected() + ".");

        public static Runnable Save = () -> TestModCommon.notify("Inventory", "Saved.");
    }

    @ILabel("Inventory")
    @IGroup(column = 2)
    public static class Inventory_items {
        @ILabel("")
        public static final ListModel Items = new ListModel().searchable(true).height(356);

        public static Runnable Remove = () -> TestModCommon.notify("Inventory", "Removed.");
    }
}
