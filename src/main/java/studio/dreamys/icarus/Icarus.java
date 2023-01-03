package studio.dreamys.icarus;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import studio.dreamys.icarus.component.Window;
import studio.dreamys.icarus.config.Config;
import studio.dreamys.icarus.util.RenderUtils;

public class Icarus {
    @Getter private static Window window;
    @Getter private static String modid;

    public static void init(String modid, Window window) {
        RenderUtils.loadFonts();
        Icarus.window = window; //create window object and store it forever
        Icarus.modid = modid;
        MinecraftForge.EVENT_BUS.register(new Icarus());

        Config.init(modid);
        Config.load();
        Config.save();
        Config.generateWindow();
        Config.loadAttachments();
        Config.saveAttachments();
    }

    @SubscribeEvent
    public void key(InputEvent.KeyInputEvent e) {
        if (Minecraft.getMinecraft().theWorld == null || Minecraft.getMinecraft().thePlayer == null) return;
        if (Keyboard.getEventKeyState()) { //if the key is down
            int keyCode = Keyboard.getEventKey(); //get keycode
            if (keyCode <= 0) return; //ignore invalid keycode
            if (keyCode == window.key) {
                Minecraft.getMinecraft().displayGuiScreen(window);
            }
//            for (Component attachment : window.all) { //for every attachment
//                if (attachment instanceof Keybind) { //if it's a keybind
//                    Keybind keybind = (Keybind) attachment; //cast to keybind
//                    if (keybind.getKey() == keyCode) { //if the keys match
//                        if (keybind.getChild() instanceof Checkbox) { //if the child is a checkbox
//                            ((Checkbox) keybind.getChild()).toggle(); //toggle the checkbox
////                            config.save(); //save the config
//                            keybind.getChild().fireChange(); //fire change event
//                        }
//                        if (keybind.getChild() instanceof Button) { //if the child is a button
////                            ((Button) keybind.getChild()).getRunnable().run(); //click the button
//                        }
//                    }
//                }
//            }
        }
    }
}
