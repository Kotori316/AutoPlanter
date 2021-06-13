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
    // Use cache because float boxing always creates new instance.
    private static final Float AUTO_PLANTER_MOISTURE = 9f;
    private static final Float UPGRADED_PLANTER_MOISTURE = 9f;

    @Inject(method = "getAvailableMoisture", at = @At("HEAD"), cancellable = true)
    private static void planterMoisture(Block block, BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        var state = world.getBlockState(pos.down());
        if (state.isOf(AutoPlanter.Holder.PLANTER_BLOCK)) {
            cir.setReturnValue(AUTO_PLANTER_MOISTURE);
        } else if (state.isOf(AutoPlanter.Holder.PLANTER_UPGRADED_BLOCK)) {
            cir.setReturnValue(UPGRADED_PLANTER_MOISTURE);
        }
    }
}
