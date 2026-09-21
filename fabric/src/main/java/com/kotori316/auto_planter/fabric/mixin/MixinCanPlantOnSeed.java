package com.kotori316.auto_planter.fabric.mixin;

import com.kotori316.auto_planter.MixinHelper;
import com.kotori316.auto_planter.fabric.planter.PlanterBlockFabric;
import com.kotori316.auto_planter.planter.PlanterTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.PitcherCropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({CropBlock.class, PitcherCropBlock.class})
public abstract class MixinCanPlantOnSeed {
    @SuppressWarnings("ConstantConditions")
    @Inject(method = "mayPlaceOn", at = @At("HEAD"), cancellable = true)
    protected void addPlanter(BlockState state, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (state.getBlock() instanceof PlanterBlockFabric) {
            if (state.getValue(PlanterBlockFabric.TRIGGERED)) {
                Block block = MixinHelper.cast(this, Block.class);
                if (PlanterTile.isPlantableCrop(block)) {
                    cir.setReturnValue(Boolean.TRUE);
                }
            }
        }
    }
}
