package com.kotori316.auto_planter.fabric.planter;

import com.kotori316.auto_planter.AutoPlanterCommon;
import com.kotori316.auto_planter.planter.PlanterBlock;
import com.kotori316.auto_planter.planter.PlanterTile;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public sealed abstract class PlanterTileFabric extends PlanterTile implements ExtendedScreenHandlerFactory<BlockPos> {

    protected PlanterTileFabric(BlockPos pos, BlockState state, PlanterBlock.PlanterBlockType blockType) {
        super(pos, state, blockType);
    }

    @Override
    public PlanterContainerFabric createMenu(int id, Inventory inv, Player p) {
        return new PlanterContainerFabric(id, p, getBlockPos(), AutoPlanterCommon.accessor.planterMenuType());
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayer player) {
        return worldPosition;
    }

    public static final class Normal extends PlanterTileFabric {

        public static final String TILE_ID = AutoPlanterCommon.AUTO_PLANTER + ":" + AutoPlanterCommon.BLOCK_NORMAL + "_tile";

        public Normal(BlockPos pos, BlockState state) {
            super(pos, state, PlanterBlock.PlanterBlockType.NORMAL);
        }
    }

    public static final class Upgraded extends PlanterTileFabric {
        public static final String TILE_ID = AutoPlanterCommon.AUTO_PLANTER + ":" + AutoPlanterCommon.BLOCK_UPGRADED + "_tile";

        public Upgraded(BlockPos pos, BlockState state) {
            super(pos, state, PlanterBlock.PlanterBlockType.UPGRADED);
        }
    }
}
