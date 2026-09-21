package com.kotori316.auto_planter.planter;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

final class PlanterItem extends BlockItem {
    PlanterItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> componentAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, componentAdder, flag);
        componentAdder.accept(Component.translatable("tooltip.auto_planter.planter_item"));
    }
}
