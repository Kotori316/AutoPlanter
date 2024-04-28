package com.kotori316.auto_planter.fabric.mixin;

import com.kotori316.auto_planter.MixinHelper;
import com.kotori316.auto_planter.fabric.planter.PlanterBlockFabric;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CropBlock.class)
public abstract class MixinCanPlantOnSeed {
    @SuppressWarnings("ConstantConditions")
    @Inject(method = "mayPlaceOn", at = @At("HEAD"), cancellable = true)
    protected void addPlanter(BlockState floor, BlockGetter view, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (floor.getBlock() instanceof PlanterBlockFabric) {
            if (floor.getValue(PlanterBlockFabric.TRIGGERED)) {
                Block block = MixinHelper.cast(this, Block.class);
                if (block instanceof CropBlock) {
                    cir.setReturnValue(Boolean.TRUE);
                }
            }
        }
    }
}
