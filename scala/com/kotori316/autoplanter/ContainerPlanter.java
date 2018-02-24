package com.kotori316.autoplanter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerPlanter extends Container {
    private final TilePlanter tilePlanter;

    public ContainerPlanter(TilePlanter planter, EntityPlayer player) {
        this.tilePlanter = planter;
        int onebox = 18;
        for (int h = 0; h < 3; ++h)
            for (int v = 0; v < 3; ++v)
                addSlotToContainer(new LimitableSlot(planter, h * 3 + v, 62 + onebox * v, 17 + onebox * h));

        for (int h = 0; h < 3; ++h)
            for (int v = 0; v < 9; ++v)
                addSlotToContainer(new Slot(player.inventory, 9 + h * 9 + v, 8 + onebox * v, 84 + onebox * h));

        for (int v = 0; v < 9; ++v)
            addSlotToContainer(new Slot(player.inventory, v, 8 + onebox * v, 142));
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return tilePlanter.isUsableByPlayer(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        Slot from = getSlot(index);
        if (from != null && from.getHasStack()) {
            ItemStack current = from.getStack();
            int originalSize = current.getCount();
            int originalSlot = 9;
            if (index < originalSlot) {
                if (!this.mergeItemStack(current, originalSlot, originalSlot + 36, true)) {
                    return ItemStack.EMPTY;
                }
            } else /*if (tilePlanter.isItemValidForSlot(0, current)) */ {
                if (!this.mergeItemStack(current, 0, originalSlot, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (current.getCount() == 0)
                from.putStack(ItemStack.EMPTY);
            else
                from.onSlotChanged();

            if (current.getCount() == originalSize)
                return ItemStack.EMPTY;

            from.onTake(playerIn, current);
        }
        return ItemStack.EMPTY;
    }

    private static class LimitableSlot extends Slot {
        public LimitableSlot(TilePlanter planter, int index, int xP, int yP) {
            super(planter, index, xP, yP);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return inventory.isItemValidForSlot(getSlotIndex(), stack);
        }
    }
}
