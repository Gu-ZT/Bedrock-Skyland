package net.cjsah.skyland.mixin.bedrockminer;

import net.cjsah.skyland.bedrockminer.BreakingFlowController;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public class AnvilCraft_MultiPlayerGameModeMixin {
    @Inject(at = @At("HEAD"), method = "tick")
    private void init(CallbackInfo info) {
        if (BreakingFlowController.isWorking()) {
            BreakingFlowController.tick();
        }
    }
}
