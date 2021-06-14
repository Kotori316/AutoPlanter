package com.kotori316.auto_planter.planter;

import java.util.Objects;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.SlotItemHandler;

import com.kotori316.auto_planter.AutoPlanter;

public class PlanterContainer extends Container {
    public final PlanterTile tile;
    private final int size;
    public static final String GUI_ID = AutoPlanter.AUTO_PLANTER + ":" + PlanterBlock.Normal.name + "_gui";

    public PlanterContainer(int id, PlayerEntity player, BlockPos pos, ContainerType<?> type) {
        super(type, id);
        this.tile = Objects.requireNonNull((PlanterTile) player.getEntityWorld().getTileEntity(pos));
        this.size = tile.blockType().storageSize;
        assertInventorySize(tile, size);
        tile.openInventory(player);


        switch (tile.blockType().rowColumn) {
            case 3:
            default:
                for (int i = 0; i < 3; ++i) {
                    for (int j = 0; j < 3; ++j) {
                        this.addSlot(new SlotItemHandler(tile.handler, j + i * 3, 62 + j * 18, 17 + i * 18));
                    }
                }
                break;
            case 4:
                for (int i = 0; i < 4; ++i) {
                    for (int j = 0; j < 4; ++j) {
                        this.addSlot(new SlotItemHandler(tile.handler, j + i * 4, 53 + j * 18, 8 + i * 18));
                    }
                }
                break;
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
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();
            if (index < size) {
                if (!this.mergeItemStack(slotStack, size, size + 36, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(slotStack, 0, size, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (slotStack.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, slotStack);
        }

        return stack;
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        this.tile.closeInventory(playerIn);
    }
}
