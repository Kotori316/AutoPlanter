package com.kotori316.auto_planter.fabric.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.kotori316.auto_planter.fabric.planter.PlanterBlockFabric;

@Mixin(BushBlock.class)
public class MixinCanPlantOnSapling {
    @Inject(method = "mayPlaceOn", at = @At("HEAD"), cancellable = true)
    protected void addPlanter(BlockState floor, BlockGetter view, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (floor.getBlock() instanceof PlanterBlockFabric) {
            Block block = (Block) (Object) this;
            if (block.defaultBlockState().is(BlockTags.SAPLINGS)) {
                cir.setReturnValue(Boolean.TRUE);
            }
        }
    }
}
