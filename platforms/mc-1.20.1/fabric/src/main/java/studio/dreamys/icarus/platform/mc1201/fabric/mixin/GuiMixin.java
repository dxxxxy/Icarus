package studio.dreamys.icarus.platform.mc1201.fabric.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.dreamys.icarus.platform.PlatformImpl;

/** HUD hook (what fabric-api's HudRenderCallback does), kept here so Icarus doesn't need fabric-api. */
@Mixin(Gui.class)
public class GuiMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void icarus$render(GuiGraphics graphics, float partialTick, CallbackInfo ci) {
        PlatformImpl.renderHud(graphics, partialTick);
    }
}
