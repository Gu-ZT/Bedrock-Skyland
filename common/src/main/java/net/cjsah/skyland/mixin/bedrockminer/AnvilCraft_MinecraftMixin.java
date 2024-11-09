package net.cjsah.skyland.mixin.bedrockminer;

import net.cjsah.skyland.bedrockminer.BreakingFlowController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;


@Mixin(Minecraft.class)
public abstract class AnvilCraft_MinecraftMixin {
    @Shadow
    @Nullable
    public ClientLevel level;
    @Shadow
    @Nullable
    public LocalPlayer player;
    @Shadow
    @Nullable
    public HitResult hitResult;

    @Inject(method = "startUseItem", at = @At(value = "HEAD"))
    private void onInitComplete(CallbackInfo ci) {
        if (this.hitResult == null) return;
        if (this.hitResult.getType() != HitResult.Type.BLOCK) return;
        if (this.level == null) return;
        if (this.player == null) return;
        BlockHitResult blockHitResult = (BlockHitResult) this.hitResult;
        if (this.level.getBlockState(blockHitResult.getBlockPos()).is(Blocks.BEDROCK) && this.player.getMainHandItem().isEmpty()) {
            BreakingFlowController.switchOnOff();
        }
    }


    @Inject(method = "continueAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;swing(Lnet/minecraft/world/InteractionHand;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void inject(boolean bl, CallbackInfo ci, BlockHitResult blockHitResult, BlockPos blockPos, Direction direction) {
        if (this.level == null) return;
        if (this.level.getBlockState(blockPos).is(Blocks.BEDROCK) && BreakingFlowController.isWorking()) {
            BreakingFlowController.addBlockPosToList(blockPos);
        }
    }
}

