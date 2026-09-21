package com.kotori316.auto_planter.neoforge.mixin.compat;

import com.kotori316.auto_planter.neoforge.planter.PlanterBlockNeoForge;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Soft-dependency mixin for Fish of Thieves' PineappleCropBlock.
 * NeoForge usually covers this via {@code canSustainPlant}, but this mixin is a fallback
 * if the plant overrides {@code mayPlaceOn}/{@code canSurvive} without consulting that hook.
 * Safe when the mod is absent: {@link Pseudo}, separate required=false mixin config, require=0.
 * <p>
 * Extends {@link VegetationBlock} so the Mixin AP can remap the obfuscated {@code mayPlaceOn} override.
 */
@Pseudo
@Mixin(targets = "com.stevekung.fishofthieves.block.PineappleCropBlock", remap = false)
public abstract class MixinFishOfThievesPineapple extends VegetationBlock {
    private MixinFishOfThievesPineapple(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Inject(method = "mayPlaceOn", at = @At("HEAD"), cancellable = true, remap = true, require = 0)
    private void addPlanter(BlockState floor, BlockGetter view, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (floor.getBlock() instanceof PlanterBlockNeoForge && floor.getValue(PlanterBlockNeoForge.TRIGGERED)) {
            cir.setReturnValue(Boolean.TRUE);
        }
    }
}
