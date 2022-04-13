package com.kotori316.auto_planter.mixin;

import java.util.Random;
import java.util.function.BiConsumer;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.kotori316.auto_planter.AutoPlanter;

@Mixin(TrunkPlacer.class)
public abstract class MixinTrunkPlacer {
    @Inject(method = "setToDirt", at = @At("HEAD"), cancellable = true)
    private static void cancelPlaceDirt(TestableWorld level, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos pos, TreeFeatureConfig config, CallbackInfo ci) {
        if (level.testBlockState(pos, state ->
            state.getBlock() == AutoPlanter.Holder.PLANTER_BLOCK ||
                state.getBlock() == AutoPlanter.Holder.PLANTER_UPGRADED_BLOCK)) {
            ci.cancel();
        }
    }
}
