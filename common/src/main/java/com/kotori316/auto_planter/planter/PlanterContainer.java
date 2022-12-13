package com.kotori316.auto_planter.planter;

import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import com.kotori316.auto_planter.AutoPlanterCommon;

public abstract class PlanterContainer<T extends PlanterTile> extends AbstractContainerMenu {
    public final T tile;
    private final int size;
    public static final String GUI_ID = AutoPlanterCommon.AUTO_PLANTER + ":" + AutoPlanterCommon.BLOCK_NORMAL + "_gui";

    protected PlanterContainer(int id, Player player, BlockPos pos, MenuType<?> type) {
        super(type, id);
        //noinspection unchecked
        this.tile = Objects.requireNonNull((T) player.level.getBlockEntity(pos));
        this.size = tile.blockType().storageSize;
        checkContainerSize(tile, size);
        tile.startOpen(player);

        switch (tile.blockType().rowColumn) {
            case 3:
            default:
                for (int i = 0; i < 3; ++i) {
                    for (int j = 0; j < 3; ++j) {
                        this.addSlot(createSlot(tile, j + i * 3, 62 + j * 18, 17 + i * 18));
                    }
                }
                break;
            case 4:
                for (int i = 0; i < 4; ++i) {
                    for (int j = 0; j < 4; ++j) {
                        this.addSlot(createSlot(tile, j + i * 4, 53 + j * 18, 8 + i * 18));
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

    protected abstract Slot createSlot(T tile, int index, int x, int y);

    @Override
    public boolean stillValid(Player playerIn) {
        return tile.stillValid(playerIn);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            ItemStack stack = slotStack.copy();
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

            if (slotStack.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, slotStack);
            return stack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void removed(Player playerIn) {
        super.removed(playerIn);
        this.tile.stopOpen(playerIn);
    }
}
