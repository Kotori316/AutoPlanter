package com.kotori316.auto_planter.mixin;
/*
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;

import com.kotori316.auto_planter.AutoPlanter;

@Mixin(SaplingBlock.class)
public abstract class MixinSaplingGrow {
    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    public void growOnPlanter(BlockState state, ServerLevel worldIn, BlockPos pos, Random random, CallbackInfo ci) {
        if (worldIn.getBlockState(pos.below()).is(AutoPlanter.Holder.PLANTER_UPGRADED_BLOCK)) {
            if (worldIn.getMaxLocalRawBrightness(pos.above()) >= 9 && worldIn.isAreaLoaded(pos, 1)) {
                // Check light level only. Random check is skipped.
                BlockState newState;
                if (state.hasProperty(SaplingBlock.STAGE)) {
                    // We can use func_235896_a_ (cycle) but the next state must be 1.
                    newState = state.setValue(SaplingBlock.STAGE, 1);
                } else {
                    // Is this a real sapling? It might be a modified sapling.
                    newState = state;
                }
                placeTree(worldIn, pos, newState, random);
            }
            ci.cancel();
        }
    }

    @Shadow
    public abstract void placeTree(ServerLevel world, BlockPos pos, BlockState state, Random random);
}
*/