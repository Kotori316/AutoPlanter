package com.kotori316.auto_planter.planter;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.SlotItemHandler;

import com.kotori316.auto_planter.AutoPlanter;

public class PlanterContainer extends Container {
    private final PlanterTile tile;
    private static final int size = PlanterTile.SIZE;
    public static final String GUI_ID = AutoPlanter.AUTO_PLANTER + ":" + PlanterBlock.name + "_gui";

    public PlanterContainer(int id, PlayerEntity player, BlockPos pos, ContainerType<?> type) {
        super(type, id);
        this.tile = ((PlanterTile) player.getEntityWorld().getTileEntity(pos));
        assert tile != null;
        assertInventorySize(tile, size);
        tile.openInventory(player);

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                this.addSlot(new SlotItemHandler(tile.handler, j + i * 3, 62 + j * 18, 17 + i * 18));
            }
        }

        for (int k = 0; k < 3; ++k) {
            for (int i1 = 0; i1 < 9; ++i1) {
                this.addSlot(new Slot(player.inventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
            }
        }

        for (int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(player.inventory, l, 8 + l * 18, 142));
        }

    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return tile.isUsableByPlayer(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index < 9) {
                if (!this.mergeItemStack(itemstack1, size, size + 36, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, size, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        this.tile.closeInventory(playerIn);
    }
}
