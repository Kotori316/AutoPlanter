package com.kotori316.auto_planter.mixin;

import java.util.function.BiConsumer;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.kotori316.auto_planter.AutoPlanter;

@Mixin(TrunkPlacer.class)
public abstract class MixinTrunkPlacer {
    @Inject(method = "setDirtAt", at = @At("HEAD"), cancellable = true)
    private static void cancelPlaceDirt(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> replacer, RandomSource random, BlockPos pos, TreeConfiguration config, CallbackInfo ci) {
        if (level.isStateAtPosition(pos, state ->
            state.getBlock() == AutoPlanter.Holder.PLANTER_BLOCK ||
                state.getBlock() == AutoPlanter.Holder.PLANTER_UPGRADED_BLOCK)) {
            ci.cancel();
        }
    }
}
