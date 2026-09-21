package com.kotori316.auto_planter.fabric.mixin;

import com.kotori316.auto_planter.fabric.planter.PlanterBlockFabric;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SugarCaneBlock.class)
public abstract class MixinCanPlantOnSugarCane {
    @Inject(method = "canSurvive", at = @At("HEAD"), cancellable = true)
    private void addPlanter(BlockState state, LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockState floor = level.getBlockState(pos.below());
        if (floor.getBlock() instanceof PlanterBlockFabric && floor.getValue(PlanterBlockFabric.TRIGGERED)) {
            cir.setReturnValue(Boolean.TRUE);
        }
    }
}
