package com.kotori316.auto_planter.forge.planter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

import com.kotori316.auto_planter.AutoPlanterCommon;
import com.kotori316.auto_planter.planter.PlanterBlock;
import com.kotori316.auto_planter.planter.PlanterTile;

public abstract sealed class PlanterTileForge extends PlanterTile {
    public final IItemHandlerModifiable handler = new InvWrapper(this);
    private final LazyOptional<IItemHandlerModifiable> handlerLazyOptional = LazyOptional.of(() -> handler);

    PlanterTileForge(BlockPos pos, BlockState state, PlanterBlock.PlanterBlockType blockType) {
        super(pos, state, blockType);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER)
            return handlerLazyOptional.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        handlerLazyOptional.invalidate();
    }

    @Override
    public PlanterContainerForge createMenu(int id, Inventory inv, Player p) {
        return new PlanterContainerForge(id, p, getBlockPos(), AutoPlanterCommon.accessor.planterMenuType());
    }

    public static final class Normal extends PlanterTileForge {

        public static final String TILE_ID = AutoPlanterCommon.AUTO_PLANTER + ":" + AutoPlanterCommon.BLOCK_NORMAL + "_tile";

        public Normal(BlockPos pos, BlockState state) {
            super(pos, state, PlanterBlock.PlanterBlockType.NORMAL);
        }
    }

    public static final class Upgraded extends PlanterTileForge {
        public static final String TILE_ID = AutoPlanterCommon.AUTO_PLANTER + ":" + AutoPlanterCommon.BLOCK_UPGRADED + "_tile";

        public Upgraded(BlockPos pos, BlockState state) {
            super(pos, state, PlanterBlock.PlanterBlockType.UPGRADED);
        }
    }
}
