package com.kotori316.auto_planter.mixin;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.kotori316.auto_planter.AutoPlanter;

@Mixin(TrunkPlacer.class)
public abstract class MixinTrunkPlacer {
    @Inject(method = "canGenerate", at = @At("HEAD"), cancellable = true)
    private static void canGenerate(TestableWorld level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (level.testBlockState(pos, state ->
            state.getBlock() == AutoPlanter.Holder.PLANTER_BLOCK ||
                state.getBlock() == AutoPlanter.Holder.PLANTER_UPGRADED_BLOCK)) {
            cir.setReturnValue(Boolean.TRUE);
        }
    }
}
