package com.kotori316.auto_planter.planter;

import java.util.Objects;

import it.unimi.dsi.fastutil.ints.IntIntPair;
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
        this.tile = cast(player.level.getBlockEntity(pos));
        var blockType = tile.blockType();
        this.size = blockType.storageSize;
        checkContainerSize(tile, size);
        tile.startOpen(player);

        var startXY = switch (blockType) {
            case NORMAL -> IntIntPair.of(62, 17);
            case UPGRADED -> IntIntPair.of(53, 8);
        };
        for (int i = 0; i < blockType.rowColumn; ++i) {
            for (int j = 0; j < blockType.rowColumn; ++j) {
                this.addSlot(createSlot(tile, j + i * blockType.rowColumn, startXY.leftInt() + j * 18, startXY.rightInt() + i * 18));
            }
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

    @SuppressWarnings("unchecked")
    private static <T extends PlanterTile> T cast(Object o) {
        return (T) Objects.requireNonNull(o);
    }
}
