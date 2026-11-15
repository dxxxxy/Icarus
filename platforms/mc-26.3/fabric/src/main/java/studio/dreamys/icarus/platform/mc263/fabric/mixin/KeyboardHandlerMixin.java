package studio.dreamys.icarus.platform.mc263.fabric.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.dreamys.icarus.input.Keys;
import studio.dreamys.icarus.platform.PlatformImpl;

/** Fabric has no raw key event; this mirrors Forge's KeyInputEvent: key presses while no screen is open. */
@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    @Inject(method = "keyPress", at = @At("HEAD"))
    private void icarus$keyPress(long window, int action, KeyEvent event, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (action == InputConstants.PRESS && window == mc.getWindow().handle() && mc.gui.screen() == null) {
            PlatformImpl.keyPressed(Keys.fromSdl(event.key()));
        } else if (action == InputConstants.RELEASE) {
            PlatformImpl.keyReleased(Keys.fromSdl(event.key()));
        }
    }
}
