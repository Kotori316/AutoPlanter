package com.kotori316.auto_planter.forge.planter;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.items.SlotItemHandler;

import com.kotori316.auto_planter.planter.PlanterContainer;

public final class PlanterContainerForge extends PlanterContainer<PlanterTileForge> {
    public PlanterContainerForge(int id, Player player, BlockPos pos, MenuType<?> type) {
        super(id, player, pos, type);
    }

    @Override
    protected Slot createSlot(PlanterTileForge tile, int index, int x, int y) {
        return new SlotItemHandler(tile.handler, index, x, y);
    }
}
