package net.cjsah.skyland.bedrockminer;

import dev.dubhe.anvilcraft.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class InventoryManager {
    public static void switchToItem(ItemLike item) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;
        if (minecraft.gameMode == null) return;
        Inventory inventory = minecraft.player.getInventory();

        int i = inventory.findSlotMatchingItem(new ItemStack(item));

        if ("amethyst_pickaxe".equals(item.toString())) {
            i = getEfficientTool(inventory);
        }

        if (i != -1) {
            if (Inventory.isHotbarSlot(i)) {
                inventory.selected = i;
            } else {
                minecraft.gameMode.handlePickItem(i);
            }
            ClientPacketListener connection = minecraft.getConnection();
            if (connection == null) return;
            connection.send(new ServerboundSetCarriedItemPacket(inventory.selected));
        }
    }

    private static int getEfficientTool(Inventory playerInventory) {
        for (int i = 0; i < playerInventory.items.size(); ++i) {
            if (getBlockBreakingSpeed(Blocks.PISTON.defaultBlockState(), i) > 45f) {
                return i;
            }
        }
        return -1;
    }

    public static boolean canInstantlyMinePiston() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return false;
        Inventory inventory = minecraft.player.getInventory();

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (getBlockBreakingSpeed(Blocks.PISTON.defaultBlockState(), i) > 45f) {
                return true;
            }
        }
        return false;
    }

    private static float getBlockBreakingSpeed(BlockState block, int slot) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) return 0.0f;
        ItemStack stack = player.getInventory().getItem(slot);
        return stack.is(ModItems.AMETHYST_PICKAXE.get()) && (block.is(Blocks.PISTON) || block.is(Blocks.STICKY_PISTON) || block.is(Blocks.PISTON_HEAD)) ? 50.0f : 0.0f;
    }

    public static int getInventoryItemCount(ItemLike item) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return 0;
        Inventory playerInventory = minecraft.player.getInventory();
        return playerInventory.countItem(item.asItem());
    }

    public static @Nullable String warningMessage() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.gameMode == null) return "";
        if (!"survival".equals(minecraft.gameMode.getPlayerMode().getName())) {
            return "bedrockminer.fail.missing.survival";
        }

        if (InventoryManager.getInventoryItemCount(Blocks.PISTON) < 2) {
            return "bedrockminer.fail.missing.piston";
        }

        if (InventoryManager.getInventoryItemCount(Blocks.REDSTONE_TORCH) < 1) {
            return "bedrockminer.fail.missing.redstonetorch";
        }

        if (!InventoryManager.canInstantlyMinePiston()) {
            return "bedrockminer.fail.missing.instantmine";
        }
        return null;
    }

}
