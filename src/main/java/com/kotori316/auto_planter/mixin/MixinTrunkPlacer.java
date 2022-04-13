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

import com.kotori316.auto_planter.planter.PlanterBlock;

@Mixin(TrunkPlacer.class)
public class MixinTrunkPlacer {
    @Inject(method = "setToDirt", at = @At("HEAD"), cancellable = true)
    private static void cancelPlaceDirt(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos pos, TreeFeatureConfig config, CallbackInfo ci) {
        if (world.testBlockState(pos, s -> s.getBlock() instanceof PlanterBlock)) {
            ci.cancel();
        }
    }
}
