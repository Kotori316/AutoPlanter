package com.kotori316.auto_planter.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.kotori316.auto_planter.AutoPlanter;

@Mixin(SaplingBlock.class)
public class MixinSaplingGrow {
    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    public void growOnPlanter(BlockState state, ServerLevel world, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (world.getBlockState(pos.below()).is(AutoPlanter.Holder.PLANTER_UPGRADED_BLOCK)) {
            if (world.getMaxLocalRawBrightness(pos.above()) >= 9)  // Check light level only. Random check is skipped.
                advanceTree(world, pos, state, random);
            ci.cancel();
        }
    }

    @Shadow
    public void advanceTree(ServerLevel world, BlockPos pos, BlockState state, RandomSource random) {
    }
}
