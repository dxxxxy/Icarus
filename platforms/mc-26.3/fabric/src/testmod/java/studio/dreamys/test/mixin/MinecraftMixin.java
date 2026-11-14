package studio.dreamys.test.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.dreamys.test.TestModCommon;

/** Fabric client entrypoints run before the game window exists; initialise once Minecraft is constructed. */
@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void testmod$init(GameConfig gameConfig, CallbackInfo ci) {
        TestModCommon.init("testmod");
    }
}
