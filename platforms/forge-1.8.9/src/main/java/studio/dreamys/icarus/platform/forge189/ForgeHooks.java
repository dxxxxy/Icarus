package studio.dreamys.icarus.platform.forge189;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import studio.dreamys.icarus.Icarus;

public class ForgeHooks {
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent e) {
        if (Keyboard.getEventKeyState()) { //if the key is down
            Icarus.onKeyPressed(Keyboard.getEventKey()); //LWJGL2 codes are Icarus' codes
        }
    }

    @SubscribeEvent
    public void onOverlayText(RenderGameOverlayEvent.Text e) {
        Icarus.renderHud(e.partialTicks);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e) {
        if (e.phase == TickEvent.Phase.START) Icarus.clientTick();
    }
}
