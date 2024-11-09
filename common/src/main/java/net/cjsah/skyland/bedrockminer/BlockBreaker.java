package net.cjsah.skyland.bedrockminer;


import dev.dubhe.anvilcraft.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class BlockBreaker {
    public static void breakBlock(BlockPos pos) {
        InventoryManager.switchToItem(ModItems.AMETHYST_PICKAXE.get());
        if (Minecraft.getInstance().gameMode == null) return;
        Minecraft.getInstance().gameMode.startDestroyBlock(pos, Direction.UP);
    }
}