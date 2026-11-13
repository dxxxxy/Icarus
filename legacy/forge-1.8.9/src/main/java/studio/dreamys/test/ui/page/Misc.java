package studio.dreamys.test.ui.page;

import org.lwjgl.input.Keyboard;
import studio.dreamys.icarus.annotation.field.IKeybind;
import studio.dreamys.icarus.annotation.type.IGroup;
import studio.dreamys.icarus.annotation.type.IPage;

@IPage('u')
public class Misc {
    @IGroup(x = 47.5, y = 10)
    public static class Watermark {
        @IKeybind(Keyboard.KEY_G)
        public static boolean Enabled = true;

        public static String Text = "dxxxxy#0776";
    }
}
