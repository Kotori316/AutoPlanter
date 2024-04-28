package com.kotori316.auto_planter.fabric.mixin;

import com.kotori316.auto_planter.fabric.AutoPlanter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(CropBlock.class)
public abstract class MixinGetAvailableMoisture {

    @Inject(method = "getGrowthSpeed", at = @At("HEAD"), cancellable = true)
    private static void planterMoisture(Block block, BlockGetter world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        var state = world.getBlockState(pos.below());
        if (state.is(AutoPlanter.Holder.PLANTER_BLOCK)) {
            cir.setReturnValue(9f);
        } else if (state.is(AutoPlanter.Holder.PLANTER_UPGRADED_BLOCK)) {
            cir.setReturnValue(45f);
        }
    }
}
