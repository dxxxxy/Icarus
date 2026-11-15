package studio.dreamys.test.skeet;

import studio.dreamys.icarus.annotation.ILabel;
import studio.dreamys.icarus.annotation.type.IGroup;
import studio.dreamys.icarus.annotation.type.IPage;
import studio.dreamys.icarus.settings.Label;
import studio.dreamys.icarus.settings.ListModel;
import studio.dreamys.test.TestModCommon;

/** The config tab: Icarus' built-in Presets group goes first; this adds the Lua group next to it. */
@IPage(icon = "config", order = 7)
public class Config {
    @IGroup(column = 1)
    public static class Lua {
        public static Runnable Reload_active_scripts = () -> TestModCommon.notify("Lua", "Reloaded active scripts.");

        @ILabel("")
        public static final ListModel Scripts = new ListModel("havoc_vision", "model_changer", "dt_speed", "havoc_illusion")
                .searchable(true).height(298 / 1.25);

        @ILabel("Updated 18 days ago")
        public static Label Updated;

        public static boolean Load_on_startup = false;

        public static Runnable Load_script = () -> TestModCommon.notify("Lua", "Loaded " + Scripts.getSelected() + ".");
    }
}
