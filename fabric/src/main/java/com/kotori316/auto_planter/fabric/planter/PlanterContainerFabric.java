package com.kotori316.auto_planter.fabric.planter;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import com.kotori316.auto_planter.planter.PlanterContainer;

public final class PlanterContainerFabric extends PlanterContainer<PlanterTileFabric> {
    public PlanterContainerFabric(int id, Player player, BlockPos pos, MenuType<?> type) {
        super(id, player, pos, type);
    }

    @Override
    protected Slot createSlot(PlanterTileFabric tile, int index, int x, int y) {
        return new TileSlot(tile, index, x, y);
    }

    private static final class TileSlot extends Slot {

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
