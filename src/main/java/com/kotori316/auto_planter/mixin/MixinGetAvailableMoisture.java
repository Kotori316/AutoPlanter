package com.kotori316.auto_planter.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.CropBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.kotori316.auto_planter.AutoPlanter;

@Mixin(CropBlock.class)
public class MixinGetAvailableMoisture {

    @Inject(method = "getAvailableMoisture", at = @At("HEAD"), cancellable = true)
    private static void planterMoisture(Block block, BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        var state = world.getBlockState(pos.down());
        if (state.isOf(AutoPlanter.Holder.PLANTER_BLOCK)) {
            cir.setReturnValue(9f);
        } else if (state.isOf(AutoPlanter.Holder.PLANTER_UPGRADED_BLOCK)) {
            cir.setReturnValue(45f);
        }
    }
}
