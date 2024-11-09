package net.cjsah.skyland.bedrockminer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class BreakingFlowController {
    private static ArrayList<TargetBlock> cachedTargetBlockList = new ArrayList<>();

    public static boolean isWorking() {
        return working;
    }

    private static boolean working = false;

    public static void addBlockPosToList(BlockPos pos) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        if (level.getBlockState(pos).is(Blocks.BEDROCK)) {
            String haveEnoughItems = InventoryManager.warningMessage();
            if (haveEnoughItems != null) {
                Messager.actionBar(haveEnoughItems);
                return;
            }
            if (shouldAddNewTargetBlock(pos)) {
                TargetBlock targetBlock = new TargetBlock(pos, level);
                cachedTargetBlockList.add(targetBlock);
            }
        } else {
            Messager.rawactionBar("请确保敲击的方块还是基岩！");
        }
    }

    public static void tick() {
        if (InventoryManager.warningMessage() != null) {
            return;
        }
        Minecraft minecraftClient = Minecraft.getInstance();
        Player player = minecraftClient.player;

        if (player == null || minecraftClient.gameMode == null || minecraftClient.gameMode.getPlayerMode() != GameType.SURVIVAL) {
            return;
        }

        for (int i = 0; i < cachedTargetBlockList.size(); i++) {
            TargetBlock selectedBlock = cachedTargetBlockList.get(i);
            //玩家切换世界，或离目标方块太远时，删除所有缓存的任务
            if (selectedBlock.getWorld() != Minecraft.getInstance().level) {
                cachedTargetBlockList = new ArrayList<>();
                break;
            }

            if (blockInPlayerRange(selectedBlock.getBlockPos(), player, 3.4f)) {
                TargetBlock.Status status = cachedTargetBlockList.get(i).tick();
                if (status == TargetBlock.Status.FAILED || status == TargetBlock.Status.RETRACTED) {
                    cachedTargetBlockList.remove(i);
                } else {
                    break;
                }
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static boolean blockInPlayerRange(@NotNull BlockPos blockPos, @NotNull Player player, float range) {
        return blockPos.closerToCenterThan(player.getEyePosition(), range);
    }

    public static WorkingMode getWorkingMode() {
        return WorkingMode.VANILLA;
    }

    private static boolean shouldAddNewTargetBlock(BlockPos pos) {
        for (TargetBlock targetBlock : cachedTargetBlockList) {
            if (targetBlock.getBlockPos().distManhattan(pos) == 0) {
                return false;
            }
        }
        return true;
    }

    public static void switchOnOff() {
        if (working) {
            Messager.chat("bedrockminer.toggle.off");

            working = false;
        } else {
            Messager.chat("bedrockminer.toggle.on");

            Minecraft minecraftClient = Minecraft.getInstance();
            if (!minecraftClient.isLocalServer()) {

                Messager.chat("bedrockminer.warn.multiplayer");
            }
            working = true;
        }
    }


    //测试用的。使用原版模式已经足以满足大多数需求。
    //just for test. The VANILLA mode is powerful enough.
    public enum WorkingMode {
        CARPET_EXTRA,
        VANILLA,
        MANUALLY
    }
}
