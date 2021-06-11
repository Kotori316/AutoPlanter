package com.kotori316.auto_planter.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.Feature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.kotori316.auto_planter.AutoPlanter;

@Mixin(Feature.class)
public class MixinIsDirt {
    @Inject(method = "isSoil(Lnet/minecraft/block/BlockState;)Z", at = @At("HEAD"), cancellable = true)
    private static void planterIsDirt(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (state.isOf(AutoPlanter.Holder.PLANTER_BLOCK))
            cir.setReturnValue(Boolean.TRUE);
    }
}
