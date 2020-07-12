package com.kotori316.auto_planter.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.kotori316.auto_planter.AutoPlanter;

@Mixin(PlantBlock.class)
public class MixinCanPlantOnSapling {
    @Inject(method = "canPlantOnTop", at = @At("HEAD"), cancellable = true)
    protected void addPlanter(BlockState floor, BlockView view, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (floor.isOf(AutoPlanter.Holder.PLANTER_BLOCK)) {
            Block block = (Block) (Object) this;
            if (BlockTags.SAPLINGS.contains(block)) {
                cir.setReturnValue(Boolean.TRUE);
            }
        }
    }
}
