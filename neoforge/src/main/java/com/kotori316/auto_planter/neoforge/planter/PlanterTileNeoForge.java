package com.kotori316.auto_planter.neoforge.planter;

import com.kotori316.auto_planter.AutoPlanterCommon;
import com.kotori316.auto_planter.planter.PlanterBlock;
import com.kotori316.auto_planter.planter.PlanterTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import org.jetbrains.annotations.NotNull;

public abstract sealed class PlanterTileNeoForge extends PlanterTile {
    public final ResourceHandler<ItemResource> handler;

    PlanterTileNeoForge(BlockPos pos, BlockState state, PlanterBlock.PlanterBlockType blockType) {
        super(pos, state, blockType);
        handler = VanillaContainerWrapper.of(this.container);
    }

    @NotNull
    public ResourceHandler<ItemResource> getItemHandler(Direction ignored) {
        return this.handler;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        invalidateCapabilities();
    }

    @Override
    public PlanterContainerNeoForge createMenu(int id, Inventory inv, Player p) {
        return new PlanterContainerNeoForge(id, p, getBlockPos(), AutoPlanterCommon.accessor.planterMenuType());
    }

    public static final class Normal extends PlanterTileNeoForge {

        public static final String TILE_ID = AutoPlanterCommon.AUTO_PLANTER + ":" + AutoPlanterCommon.BLOCK_NORMAL + "_tile";

        public Normal(BlockPos pos, BlockState state) {
            super(pos, state, PlanterBlock.PlanterBlockType.NORMAL);
        }
    }

    public static final class Upgraded extends PlanterTileNeoForge {
        public static final String TILE_ID = AutoPlanterCommon.AUTO_PLANTER + ":" + AutoPlanterCommon.BLOCK_UPGRADED + "_tile";

        public Upgraded(BlockPos pos, BlockState state) {
            super(pos, state, PlanterBlock.PlanterBlockType.UPGRADED);
        }
    }
}
