package com.kotori316.auto_planter.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.kotori316.auto_planter.AutoPlanter;
import com.kotori316.auto_planter.planter.PlanterBlock;

@Mixin(HoeItem.class)
public class MixinHoeUsed {
    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    public void hoeUsed(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        World world1 = context.getWorld();
        BlockPos pos1 = context.getBlockPos();
        BlockState state1 = world1.getBlockState(pos1);
        if (state1.isOf(AutoPlanter.Holder.PLANTER_BLOCK) && !state1.get(PlanterBlock.TRIGGERED)) {
            world1.setBlockState(pos1, state1.with(PlanterBlock.TRIGGERED, Boolean.TRUE));
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }
}
