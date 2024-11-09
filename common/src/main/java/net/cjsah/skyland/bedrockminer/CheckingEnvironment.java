package net.cjsah.skyland.bedrockminer;


import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static net.minecraft.world.level.block.Block.canSupportCenter;

public class CheckingEnvironment {

    public static @Nullable BlockPos findNearbyFlatBlockToPlaceRedstoneTorch(ClientLevel world, @NotNull BlockPos blockPos) {
        if ((canSupportCenter(world, blockPos.east(), Direction.UP) && (world.getBlockState(blockPos.east().above()).canBeReplaced()) || world.getBlockState(blockPos.east().above()).is(Blocks.REDSTONE_TORCH))) {
            return blockPos.east();
        } else if ((canSupportCenter(world, blockPos.west(), Direction.UP) && (world.getBlockState(blockPos.west().above()).canBeReplaced()) || world.getBlockState(blockPos.west().above()).is(Blocks.REDSTONE_TORCH))) {
            return blockPos.west();
        } else if ((canSupportCenter(world, blockPos.north(), Direction.UP) && (world.getBlockState(blockPos.north().above()).canBeReplaced()) || world.getBlockState(blockPos.north().above()).is(Blocks.REDSTONE_TORCH))) {
            return blockPos.north();
        } else if ((canSupportCenter(world, blockPos.south(), Direction.UP) && (world.getBlockState(blockPos.south().above()).canBeReplaced()) || world.getBlockState(blockPos.south().above()).is(Blocks.REDSTONE_TORCH))) {
            return blockPos.south();
        }
        return null;
    }

    public static @Nullable BlockPos findPossibleSlimeBlockPos(ClientLevel world, BlockPos blockPos) {
        for (Direction direction : Direction.values()) {
            BlockPos newBlockPos = blockPos.relative(direction);
            if (!world.getBlockState(newBlockPos).canBeReplaced()) {
                continue;
            }
            if (CheckingEnvironment.isBlocked(newBlockPos)) {
                continue;
            }
            return newBlockPos;
        }
        return null;
    }

    public static boolean has2BlocksOfPlaceToPlacePiston(@NotNull ClientLevel world, @NotNull BlockPos blockPos) {
        if (world.getBlockState(blockPos.above()).getDestroySpeed(world, blockPos.above()) == 0) {
            BlockBreaker.breakBlock(blockPos.above());
        }
        if (isBlocked(blockPos.above())) {
            return false;
        }
        return world.getBlockState(blockPos.above().above()).canBeReplaced();
    }

    public static @NotNull ArrayList<BlockPos> findNearbyRedstoneTorch(@NotNull ClientLevel world, @NotNull BlockPos pistonBlockPos) {
        ArrayList<BlockPos> list = new ArrayList<>();
        if (world.getBlockState(pistonBlockPos.east()).is(Blocks.REDSTONE_TORCH)) {
            list.add(pistonBlockPos.east());
        }
        if (world.getBlockState(pistonBlockPos.west()).is(Blocks.REDSTONE_TORCH)) {
            list.add(pistonBlockPos.west());
        }
        if (world.getBlockState(pistonBlockPos.south()).is(Blocks.REDSTONE_TORCH)) {
            list.add(pistonBlockPos.south());
        }
        if (world.getBlockState(pistonBlockPos.north()).is(Blocks.REDSTONE_TORCH)) {
            list.add(pistonBlockPos.north());
        }
        return list;
    }

    public static boolean isBlocked(BlockPos blockPos) {
        if (Minecraft.getInstance().player == null) return false;
        BlockPlaceContext context = new BlockPlaceContext(Minecraft.getInstance().player,
            InteractionHand.MAIN_HAND,
            Blocks.PISTON.asItem().getDefaultInstance(),
            new BlockHitResult(blockPos.getCenter(), Direction.UP, blockPos, false));
        return !Blocks.PISTON.asItem().useOn(context).consumesAction();
    }
}
