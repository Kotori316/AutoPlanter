package com.kotori316.auto_planter.fabric.mixin;

import com.kotori316.auto_planter.MixinHelper;
import com.kotori316.auto_planter.fabric.planter.PlanterBlockFabric;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VegetationBlock.class)
public abstract class MixinCanPlantOnSapling {
    @Inject(method = "mayPlaceOn", at = @At("HEAD"), cancellable = true)
    protected void addPlanter(BlockState floor, BlockGetter view, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (floor.getBlock() instanceof PlanterBlockFabric) {
            Block block = MixinHelper.cast(this, Block.class);
            if (block.defaultBlockState().is(BlockItemTags.SAPLINGS.block())) {
                cir.setReturnValue(Boolean.TRUE);
            }
        }
    }
}
