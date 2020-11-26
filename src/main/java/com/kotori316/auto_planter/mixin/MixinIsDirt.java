package com.kotori316.auto_planter.mixin;

import net.minecraft.block.Block;
import net.minecraft.world.gen.feature.Feature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.kotori316.auto_planter.AutoPlanter;

@Mixin(Feature.class)
public class MixinIsDirt {
    @Inject(method = "isSoil(Lnet/minecraft/block/Block;)Z", at = @At("HEAD"), cancellable = true)
    private static void planterIsDirt(Block block, CallbackInfoReturnable<Boolean> cir) {
        if (block.is(AutoPlanter.Holder.PLANTER_BLOCK))
            cir.setReturnValue(Boolean.TRUE);
    }
}
