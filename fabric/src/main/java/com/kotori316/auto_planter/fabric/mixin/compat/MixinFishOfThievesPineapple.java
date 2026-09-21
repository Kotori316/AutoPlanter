package com.kotori316.auto_planter.fabric.mixin.compat;

import com.kotori316.auto_planter.fabric.planter.PlanterBlockFabric;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Soft-dependency mixin for Fish of Thieves' PineappleCropBlock.
 * Safe when the mod is absent: {@link Pseudo}, separate required=false mixin config, require=0.
 * <p>
 * The target class is absent at compile time, so Mixin's refmap can't resolve the inherited
 * {@code mayPlaceOn} (VegetationBlock) to its runtime name. Bypassed by targeting the
 * intermediary name directly with remap=false. method_9695 = VegetationBlock#mayPlaceOn
 * for MC 1.21.11; re-check this mapping when bumping the Minecraft version.
 */
@Pseudo
@Mixin(targets = "com.stevekung.fishofthieves.block.PineappleCropBlock", remap = false)
public abstract class MixinFishOfThievesPineapple {
    @Inject(method = "method_9695", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void addPlanter(BlockState floor, BlockGetter view, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (floor.getBlock() instanceof PlanterBlockFabric && floor.getValue(PlanterBlockFabric.TRIGGERED)) {
            cir.setReturnValue(Boolean.TRUE);
        }
    }
}
