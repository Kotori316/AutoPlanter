package com.kotori316.auto_planter.forge.planter;

import com.kotori316.auto_planter.AutoPlanterCommon;
import com.kotori316.auto_planter.forge.PacketHandler;
import com.kotori316.auto_planter.planter.PlanterBlock;
import com.kotori316.auto_planter.planter.PlanterMessage;
import com.kotori316.auto_planter.planter.PlanterTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract sealed class PlanterTileForge extends PlanterTile {
    public final IItemHandlerModifiable handler;
    private final LazyOptional<IItemHandlerModifiable> handlerLazyOptional;

    PlanterTileForge(BlockPos pos, BlockState state, PlanterBlock.PlanterBlockType blockType) {
        super(pos, state, blockType);
        handler = new InvWrapper(this.container);
        handlerLazyOptional = LazyOptional.of(() -> handler);
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

    @Override
    protected void onInventoryOpen(ServerPlayer player) {
        super.onInventoryOpen(player);
        PacketHandler.sendToClientPlayer(createMessage(), player);
    }

    @Override
    protected void onInventoryUpdate() {
        super.onInventoryUpdate();
        PacketHandler.sendToClientNear(createMessage(), getBlockPos(), getLevel().dimension(), 8);
    }

    private PlanterMessage createMessage() {
        CompoundTag tag;
        try (var reporter = new ProblemReporter.ScopedCollector(this.problemPath(), AutoPlanterCommon.LOGGER)) {
            var out = TagValueOutput.createWithContext(reporter, this.getLevel().registryAccess());
            this.saveAdditional(out);
            tag = out.buildResult();
        }
        return new PlanterMessage(getBlockPos(), getLevel().dimension(), tag);
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
