package studio.dreamys.icarus.platform.mc263.fabric.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.dreamys.icarus.platform.PlatformImpl;

/** Start-of-tick hook (what fabric-api's ClientTickEvents.START_CLIENT_TICK does). */
@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void icarus$tick(CallbackInfo ci) {
        PlatformImpl.clientTick();
    }
}
