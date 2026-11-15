package studio.dreamys.icarus.platform.mc1165.fabric.mixin;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.dreamys.icarus.platform.PlatformImpl;
import studio.dreamys.icarus.input.Keys;

/** Fabric has no raw key event; this mirrors Forge's KeyInputEvent: key presses while no screen is open. */
@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    @Inject(method = "keyPress", at = @At("HEAD"))
    private void icarus$keyPress(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (action == GLFW.GLFW_PRESS && window == Minecraft.getInstance().getWindow().getWindow() && Minecraft.getInstance().screen == null) {
            PlatformImpl.keyPressed(Keys.fromGlfw(key));
        } else if (action == GLFW.GLFW_RELEASE) {
            PlatformImpl.keyReleased(Keys.fromGlfw(key));
        }
    }
}
