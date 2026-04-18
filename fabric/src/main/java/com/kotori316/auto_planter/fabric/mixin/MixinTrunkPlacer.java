package com.kotori316.auto_planter.fabric.mixin;

import com.kotori316.auto_planter.fabric.AutoPlanter;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;

@Mixin(TrunkPlacer.class)
public abstract class MixinTrunkPlacer {
    @Inject(method = "placeBelowTrunkBlock", at = @At("HEAD"), cancellable = true)
    private static void cancelPlaceDirt(WorldGenLevel level, BiConsumer<BlockPos, BlockState> trunkSetter, RandomSource random, BlockPos pos, TreeConfiguration config, CallbackInfo ci) {
        if (level.isStateAtPosition(pos, state ->
            state.getBlock() == AutoPlanter.Holder.PLANTER_BLOCK ||
            state.getBlock() == AutoPlanter.Holder.PLANTER_UPGRADED_BLOCK)) {
            ci.cancel();
        }
    }
}
