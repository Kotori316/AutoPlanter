package com.kotori316.auto_planter.mixin;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.kotori316.auto_planter.AutoPlanter;

@Mixin(SaplingBlock.class)
public class MixinSaplingGrow {
    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    public void growOnPlanter(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (world.getBlockState(pos.down()).isOf(AutoPlanter.Holder.PLANTER_UPGRADED_BLOCK)) {
            if (world.getLightLevel(pos.up()) >= 9)  // Check light level only. Random check is skipped.
                generate(world, pos, state, random);
            ci.cancel();
        }
    }

    @Shadow
    public void generate(ServerWorld world, BlockPos pos, BlockState state, Random random) {
    }
}
