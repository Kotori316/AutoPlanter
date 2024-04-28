package com.kotori316.auto_planter.fabric.mixin;

import com.kotori316.auto_planter.fabric.planter.PlanterBlockFabric;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HoeItem.class)
public abstract class MixinHoeUsed {
    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    public void hoeUsed(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        var world = context.getLevel();
        var pos = context.getClickedPos();
        var state = world.getBlockState(pos);
        if (state.getBlock() instanceof PlanterBlockFabric && !state.getValue(PlanterBlockFabric.TRIGGERED)) {
            world.setBlockAndUpdate(pos, state.setValue(PlanterBlockFabric.TRIGGERED, Boolean.TRUE));
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
