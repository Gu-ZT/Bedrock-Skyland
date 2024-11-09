package net.cjsah.skyland.bedrockminer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class BlockPlacer {
    public static void simpleBlockPlacement(BlockPos pos, ItemLike item) {
        Minecraft minecraftClient = Minecraft.getInstance();

        InventoryManager.switchToItem(item);
        BlockHitResult hitResult = new BlockHitResult(new Vec3(pos.getX(), pos.getY(), pos.getZ()), Direction.UP, pos, false);
        placeBlockWithoutInteractingBlock(minecraftClient, hitResult);
    }

    public static void pistonPlacement(@NotNull BlockPos pos, Direction direction) {
        Minecraft minecraftClient = Minecraft.getInstance();
        double x = pos.getX();

        switch (BreakingFlowController.getWorkingMode()) {
            case CARPET_EXTRA://carpet accurateBlockPlacement支持
                x = x + 2 + direction.get3DDataValue() * 2;
                break;
            case VANILLA://直接发包，改变服务端玩家实体视角
                Player player = minecraftClient.player;
                float pitch = direction == Direction.DOWN ? -90f : 90f;
                ClientPacketListener connection = minecraftClient.getConnection();
                if (connection == null) break;
                if (player == null) break;
                connection.send(new ServerboundMovePlayerPacket.Rot(player.getViewYRot(1.0f), pitch, player.onGround()));
                break;
        }

        Vec3 vec3d = new Vec3(x, pos.getY(), pos.getZ());

        InventoryManager.switchToItem(Blocks.PISTON);
        BlockHitResult hitResult = new BlockHitResult(vec3d, Direction.UP, pos, false);
        placeBlockWithoutInteractingBlock(minecraftClient, hitResult);
    }

    private static void placeBlockWithoutInteractingBlock(@NotNull Minecraft minecraftClient, BlockHitResult hitResult) {
        LocalPlayer player = minecraftClient.player;
        if (player == null || minecraftClient.level == null) return;
        ItemStack itemStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        minecraftClient.gameMode.startPrediction(
            minecraftClient.level,
            sequence -> new ServerboundUseItemOnPacket(InteractionHand.MAIN_HAND, hitResult, sequence)
        );
        if (!itemStack.isEmpty() && !player.getCooldowns().isOnCooldown(itemStack.getItem())) {
            UseOnContext itemUsageContext = new UseOnContext(player, InteractionHand.MAIN_HAND, hitResult);
            itemStack.useOn(itemUsageContext);
        }
    }
}
