package com.kotori316.auto_planter.neoforge.planter;

import com.kotori316.auto_planter.planter.PlanterContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public final class PlanterContainerNeoForge extends PlanterContainer<PlanterTileNeoForge> {
    public PlanterContainerNeoForge(int id, Player player, BlockPos pos, MenuType<?> type) {
        super(id, player, pos, type);
    }

    @Override
    protected Slot createSlot(PlanterTileNeoForge tile, int index, int x, int y) {
        return new ResourceHandlerSlot(tile.handler, (i, resource, amount) -> tile.getContainer().setItem(i, resource.toStack(amount)), index, x, y);
    }
}
