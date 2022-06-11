package com.kotori316.auto_planter.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.kotori316.auto_planter.planter.PlanterBlock;

@Mixin(CropBlock.class)
public class MixinCanPlantOnSeed {
    @SuppressWarnings("ConstantConditions")
    @Inject(method = "mayPlaceOn", at = @At("HEAD"), cancellable = true)
    protected void addPlanter(BlockState floor, BlockGetter view, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (floor.getBlock() instanceof PlanterBlock) {
            if (floor.getValue(PlanterBlock.TRIGGERED)) {
                Block block = (Block) (Object) this;
                if (block instanceof CropBlock) {
                    cir.setReturnValue(Boolean.TRUE);
                }
            }
        }
    }
}
