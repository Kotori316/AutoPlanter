package com.kotori316.auto_planter.mixin;

import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.kotori316.auto_planter.planter.PlanterBlock;

@Mixin(HoeItem.class)
public class MixinHoeUsed {
    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    public void hoeUsed(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        var world = context.getWorld();
        var pos = context.getBlockPos();
        var state = world.getBlockState(pos);
        if (state.getBlock() instanceof PlanterBlock && !state.get(PlanterBlock.TRIGGERED)) {
            world.setBlockState(pos, state.with(PlanterBlock.TRIGGERED, Boolean.TRUE));
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }
}
