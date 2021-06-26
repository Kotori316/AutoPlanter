package com.kotori316.auto_planter.mixin;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.kotori316.auto_planter.AutoPlanter;

@Mixin(SaplingBlock.class)
public abstract class MixinSaplingGrow {
    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    public void growOnPlanter(BlockState state, ServerWorld worldIn, BlockPos pos, Random random, CallbackInfo ci) {
        if (worldIn.getBlockState(pos.down()).isIn(AutoPlanter.Holder.PLANTER_UPGRADED_BLOCK)) {
            if (worldIn.getLight(pos.up()) >= 9 && worldIn.isAreaLoaded(pos, 1)) {
                // Check light level only. Random check is skipped.
                BlockState newState;
                if (state.hasProperty(SaplingBlock.STAGE)) {
                    // We can use func_235896_a_ (cycle) but the next state must be 1.
                    newState = state.with(SaplingBlock.STAGE, 1);
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
    public abstract void placeTree(ServerWorld world, BlockPos pos, BlockState state, Random random);
}
