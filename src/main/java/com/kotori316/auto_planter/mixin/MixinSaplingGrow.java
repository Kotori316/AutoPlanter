package com.kotori316.auto_planter.mixin;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
                advanceTree(worldIn, pos, newState, random);
                // AutoPlanter.LOGGER.debug("Tree was grown in #growOnPlanter. {}, {}", state, pos);
            }
            ci.cancel();
        }
    }

    @Shadow
    public abstract void advanceTree(ServerLevel world, BlockPos pos, BlockState state, Random random);
}
