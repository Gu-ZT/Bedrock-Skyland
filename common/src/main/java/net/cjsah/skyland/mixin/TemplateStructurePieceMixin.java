package net.cjsah.skyland.mixin;

import net.cjsah.skyland.util.ClassMatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.structures.EndCityPieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(TemplateStructurePiece.class)
public abstract class TemplateStructurePieceMixin {
    @Unique
    private static final List<ClassMatcher> skyland$passedStructures;

    static {
        skyland$passedStructures = List.of(
                new ClassMatcher(EndCityPieces.EndCityPiece.class, "ship"::equals)
        );
    }

    @Shadow @Final protected String templateName;

    @Redirect(method = "postProcess", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;placeInWorld(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;Lnet/minecraft/util/RandomSource;I)Z"))
    private boolean placeInWord(StructureTemplate instance, ServerLevelAccessor accessor, BlockPos pos, BlockPos pos2, StructurePlaceSettings settings, RandomSource randomSource, int val) {
        for (ClassMatcher matcher : skyland$passedStructures) {
            if (matcher.match(this.getClass(), this.templateName)) {
                System.out.println(this.templateName);
                return instance.placeInWorld(accessor, pos, pos2, settings, randomSource, val);
            }
        }
        return false;
    }
}
