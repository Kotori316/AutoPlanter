package com.kotori316.auto_planter.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.kotori316.auto_planter.planter.PlanterBlock;

@Mixin(CropBlock.class)
public class MixinCanPlantOnSeed {
    @SuppressWarnings("ConstantConditions")
    @Inject(method = "canPlantOnTop", at = @At("HEAD"), cancellable = true)
    protected void addPlanter(BlockState floor, BlockView view, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (floor.getBlock() instanceof PlanterBlock) {
            if (floor.get(PlanterBlock.TRIGGERED)) {
                Block block = (Block) (Object) this;
                if (block instanceof CropBlock) {
                    cir.setReturnValue(Boolean.TRUE);
                }
            }
        }
    }
}
