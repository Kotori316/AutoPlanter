package com.kotori316.auto_planter.planter;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

import com.kotori316.auto_planter.AutoPlanter;

public class PlanterContainer extends ScreenHandler {
    public final PlanterTile tile;
    private final int size;
    public static final String GUI_ID = AutoPlanter.AUTO_PLANTER + ":" + PlanterBlock.Normal.name + "_gui";
    public final PlayerEntity player;

    public PlanterContainer(int id, PlayerEntity player, BlockPos pos) {
        super(AutoPlanter.Holder.PLANTER_CONTAINER_TYPE, id);
        this.player = player;
        this.tile = ((PlanterTile) player.getEntityWorld().getBlockEntity(pos));
        assert tile != null;
        tile.onOpen(player);
        this.size = tile.size();

        switch (tile.blockType().rowColumn) {
            case 3:
            default:
                for (int i = 0; i < 3; ++i) {
                    for (int j = 0; j < 3; ++j) {
                        this.addSlot(new TileSlot(tile, j + i * 3, 62 + j * 18, 17 + i * 18));
                    }
                }
                break;
            case 4:
                for (int i = 0; i < 4; ++i) {
                    for (int j = 0; j < 4; ++j) {
                        this.addSlot(new TileSlot(tile, j + i * 4, 53 + j * 18, 8 + i * 18));
                    }
                }
                break;
        }

        for (int k = 0; k < 3; ++k) {
            for (int i1 = 0; i1 < 9; ++i1) {
                this.addSlot(new Slot(player.getInventory(), i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
            }
        }

        for (int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(player.getInventory(), l, 8 + l * 18, 142));
        }

    }

    @Override
    public boolean canUse(PlayerEntity playerIn) {
        return tile.canPlayerUse(playerIn);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity playerIn, int index) {
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            ItemStack copy = slotStack.copy();
            if (index < size) {
                if (!this.insertItem(slotStack, size, size + 36, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(slotStack, 0, size, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (slotStack.getCount() == copy.getCount()) {
                return ItemStack.EMPTY;
            } else {
                slot.onTakeItem(playerIn, slotStack);
                return copy;
            }
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.tile.onClose(player);
    }

    public static final class TileSlot extends Slot {

        private final int invSlot;

        public TileSlot(Inventory inventory, int invSlot, int xPosition, int yPosition) {
            super(inventory, invSlot, xPosition, yPosition);
            this.invSlot = invSlot;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return inventory.isValid(invSlot, stack);
        }
    }
}
