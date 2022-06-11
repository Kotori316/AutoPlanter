package com.kotori316.auto_planter.planter;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import com.kotori316.auto_planter.AutoPlanter;

public abstract class PlanterBlock extends BaseEntityBlock {
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

    public final BlockItem blockItem;
    public final PlanterBlockType blockType;

    public PlanterBlock(PlanterBlockType blockType) {
        super(Block.Properties.copy(Blocks.DIRT).strength(0.6f, 100).isValidSpawn((state, world, pos, type) -> false));
        this.blockType = blockType;
        blockItem = new BlockItem(this, new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS));
        registerDefaultState(getStateDefinition().any().setValue(TRIGGERED, false));
    }

    protected abstract BlockEntityType<? extends PlanterTile> getEntityType();

    @Override
    protected final void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TRIGGERED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (worldIn.getBlockEntity(pos) instanceof PlanterTile planterTile) {
            ItemStack stack = player.getItemInHand(handIn);
            boolean notHasSapling = hit.getDirection() != Direction.UP || !PlanterTile.isPlantable(stack, true);
            boolean notHasHoe = !(player.getMainHandItem().getItem() instanceof HoeItem) &&
                                !(player.getOffhandItem().getItem() instanceof HoeItem);
            if (notHasSapling && notHasHoe) {
                if (!worldIn.isClientSide) {
                    player.openMenu(planterTile);
                }
                return InteractionResult.SUCCESS;
            }
        }

        return super.use(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public final PlanterTile newBlockEntity(BlockPos pos, BlockState state) {
        return getEntityType().create(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(world.getBlockEntity(pos));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (!worldIn.isClientSide) {
                if (worldIn.getBlockEntity(pos) instanceof PlanterTile inventory) {
                    Containers.dropContents(worldIn, pos, inventory);
                    worldIn.updateNeighbourForOutputSignal(pos, state.getBlock());
                }
            }
            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
        if (!worldIn.isClientSide) {
            worldIn.getBlockEntity(pos, getEntityType())
                .ifPresent(PlanterTile::plantSapling);
        }
    }

    public static class Normal extends PlanterBlock {
        public static final String name = "planter";

        public Normal() {
            super(PlanterBlockType.NORMAL);
        }

        @Override
        protected BlockEntityType<? extends PlanterTile> getEntityType() {
            return AutoPlanter.Holder.PLANTER_TILE_TILE_ENTITY_TYPE;
        }
    }

    public static class Upgraded extends PlanterBlock {
        public static final String name = "planter_upgraded";

        public Upgraded() {
            super(PlanterBlockType.UPGRADED);
        }

        @Override
        protected BlockEntityType<? extends PlanterTile> getEntityType() {
            return AutoPlanter.Holder.PLANTER_UPGRADED_TILE_ENTITY_TYPE;
        }
    }

    public enum PlanterBlockType {
        NORMAL(3), UPGRADED(4);
        public final int storageSize;
        public final int rowColumn;

        PlanterBlockType(int rowColumn) {
            this.storageSize = rowColumn * rowColumn;
            this.rowColumn = rowColumn;
        }
    }
}
