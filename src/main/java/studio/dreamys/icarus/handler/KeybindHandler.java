package studio.dreamys.icarus.handler;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.component.sub.Button;
import studio.dreamys.icarus.component.sub.Checkbox;
import studio.dreamys.icarus.component.sub.attachment.Attachment;
import studio.dreamys.icarus.component.sub.attachment.sub.Keybind;

public class KeybindHandler {
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent e) {
        if (Minecraft.getMinecraft().theWorld == null || Minecraft.getMinecraft().thePlayer == null) return;
        if (Keyboard.getEventKeyState()) { //if the key is down
            int keyCode = Keyboard.getEventKey(); //get keycode
            if (keyCode <= 0) return; //ignore invalid keycode
            if (keyCode == Icarus.getWindow().key) {
                Minecraft.getMinecraft().displayGuiScreen(Icarus.getWindow());
                return;
            }

            for (Attachment attachment : Icarus.getWindow().attachments) {
                if (attachment instanceof Keybind) {
                    if (((Keybind) attachment).getKey() == keyCode) {
                        if (attachment.child instanceof Button) {
                            ((Button) attachment.child).getRunnable().run();
                        }

                        if (attachment.child instanceof Checkbox) {
                            ((Checkbox) attachment.child).toggle();
                        }
                    }
                }
            }
        }
    }
}
