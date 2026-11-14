package studio.dreamys.icarus.handler;

import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.component.sub.Button;
import studio.dreamys.icarus.component.sub.Checkbox;
import studio.dreamys.icarus.component.sub.attachment.Attachment;
import studio.dreamys.icarus.component.sub.attachment.sub.Keybind;

public class KeybindHandler {
    public static void onKeyPressed(int keyCode) {
        if (!Icarus.getPlatform().isInGame()) return;
        if (keyCode <= 0) return; //ignore invalid keycode
        if (keyCode == Icarus.getWindow().key) {
            Icarus.getPlatform().openScreen(Icarus.getWindow());
            return;
        }

        for (Attachment attachment : Icarus.getWindow().attachments) {
            if (attachment instanceof Keybind) {
                if (((Keybind) attachment).getKey() == keyCode) {
                    if (attachment.getChild() instanceof Button) {
                        ((Button) attachment.getChild()).getRunnable().run();
                    }

                    if (attachment.getChild() instanceof Checkbox) {
                        ((Checkbox) attachment.getChild()).toggle();
                    }
                }
            }
        }
    }
}
