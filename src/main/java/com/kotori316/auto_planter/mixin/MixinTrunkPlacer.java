package com.kotori316.auto_planter.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.kotori316.auto_planter.AutoPlanter;

@Mixin(TrunkPlacer.class)
public abstract class MixinTrunkPlacer {
    @Inject(method = "isDirt", at = @At("HEAD"), cancellable = true)
    private static void isDirt(LevelSimulatedReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (level.isStateAtPosition(pos, state ->
            state.getBlock() == AutoPlanter.Holder.PLANTER_BLOCK ||
                state.getBlock() == AutoPlanter.Holder.PLANTER_UPGRADED_BLOCK)) {
            cir.setReturnValue(Boolean.TRUE);
        }
    }
}
