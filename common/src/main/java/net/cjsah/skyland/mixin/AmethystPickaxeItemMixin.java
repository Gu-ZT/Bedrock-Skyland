package net.cjsah.skyland.mixin;

import dev.dubhe.anvilcraft.item.AmethystPickaxeItem;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AmethystPickaxeItem.class)
abstract class AmethystPickaxeItemMixin extends DiggerItem {
    public AmethystPickaxeItemMixin(float f, float g, Tier tier, TagKey<Block> tagKey, Properties properties) {
        super(f, g, tier, tagKey, properties);
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack arg, @NotNull BlockState arg2) {
        if (arg2.is(Blocks.PISTON) || arg2.is(Blocks.STICKY_PISTON) || arg2.is(Blocks.PISTON_HEAD)) return 1000.0f;
        return super.getDestroySpeed(arg, arg2);
    }
}
