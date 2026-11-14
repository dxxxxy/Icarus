package studio.dreamys.test.ui.page;

import studio.dreamys.icarus.annotation.field.FieldOptions;
import studio.dreamys.icarus.annotation.field.IKeybind;
import studio.dreamys.icarus.annotation.type.IGroup;
import studio.dreamys.icarus.annotation.type.IPage;
import studio.dreamys.icarus.input.Keys;
import studio.dreamys.icarus.settings.ListModel;

@IPage(value = 'u', order = 2)
public class Misc {
    @IGroup(column = 0)
    public static class Watermark {
        @IKeybind(Keys.KEY_G)
        public static boolean Enabled = true;

        @FieldOptions(maxLength = 48)
        public static String Text = "dxxxxyware | uid 001";
    }

    @IGroup(column = 1)
    public static class Scripts {
        public static final ListModel Loaded = new ListModel("slowwalk.lua", "clantag.lua", "logger.lua");

        public static Runnable Reload = () -> System.out.println("Reloading " + Loaded.getSelected());
    }
}
