package com.kotori316.auto_planter.planter;

import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.kotori316.auto_planter.AutoPlanter;

public class PlanterContainer extends AbstractContainerMenu {
    @NotNull
    public final PlanterTile tile;
    private final int size;
    public static final String GUI_ID = AutoPlanter.AUTO_PLANTER + ":" + PlanterBlock.Normal.name + "_gui";
    public final Player player;

    public PlanterContainer(int id, Player player, BlockPos pos) {
        super(AutoPlanter.Holder.PLANTER_CONTAINER_TYPE, id);
        this.player = player;
        this.tile = Objects.requireNonNull((PlanterTile) player.getCommandSenderWorld().getBlockEntity(pos));
        tile.startOpen(player);
        this.size = tile.getContainerSize();

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
    public boolean stillValid(Player playerIn) {
        return tile.stillValid(playerIn);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            ItemStack copy = slotStack.copy();
            if (index < size) {
                if (!this.moveItemStackTo(slotStack, size, size + 36, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotStack, 0, size, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == copy.getCount()) {
                return ItemStack.EMPTY;
            } else {
                slot.onTake(playerIn, slotStack);
                return copy;
            }
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.tile.stopOpen(player);
    }

    public static final class TileSlot extends Slot {

        private final int invSlot;

        public TileSlot(Container inventory, int invSlot, int xPosition, int yPosition) {
            super(inventory, invSlot, xPosition, yPosition);
            this.invSlot = invSlot;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return container.canPlaceItem(invSlot, stack);
        }
    }
}
