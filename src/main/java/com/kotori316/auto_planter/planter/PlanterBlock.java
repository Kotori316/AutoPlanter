package com.kotori316.auto_planter.planter;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import com.kotori316.auto_planter.AutoPlanter;

public abstract class PlanterBlock extends BlockWithEntity {
    public static final BooleanProperty TRIGGERED = Properties.TRIGGERED;

    public final BlockItem blockItem;
    public final PlanterBlockType blockType;

    public PlanterBlock(PlanterBlockType blockType) {
        super(Block.Settings.copy(Blocks.DIRT).strength(0.6f, 100).allowsSpawning((state, world, pos, type) -> false));
        this.blockType = blockType;
        blockItem = new BlockItem(this, new Item.Settings().group(ItemGroup.DECORATIONS));
        setDefaultState(getStateManager().getDefaultState().with(TRIGGERED, false));
    }

    protected abstract BlockEntityType<? extends PlanterTile> getEntityType();

    @Override
    protected final void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(TRIGGERED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResult onUse(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockHitResult hit) {
        if (worldIn.getBlockEntity(pos) instanceof PlanterTile planterTile) {
            ItemStack stack = player.getStackInHand(handIn);
            boolean notHasSapling = hit.getSide() != Direction.UP || !PlanterTile.isPlantable(stack, true);
            boolean notHasHoe = !(player.getMainHandStack().getItem() instanceof HoeItem) &&
                !(player.getOffHandStack().getItem() instanceof HoeItem);
            if (notHasSapling && notHasHoe) {
                if (!worldIn.isClient) {
                    player.openHandledScreen(planterTile);
                }
                return ActionResult.SUCCESS;
            }
        }

        return super.onUse(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public final PlanterTile createBlockEntity(BlockPos pos, BlockState state) {
        return getEntityType().instantiate(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onStateReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.isOf(newState.getBlock())) {
            if (!worldIn.isClient) {
                if (worldIn.getBlockEntity(pos) instanceof PlanterTile inventory) {
                    ItemScatterer.spawn(worldIn, pos, inventory);
                    worldIn.updateComparators(pos, state.getBlock());
                }
            }
            super.onStateReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborUpdate(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborUpdate(state, worldIn, pos, blockIn, fromPos, isMoving);
        if (!worldIn.isClient) {
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
        NORMAL(9), UPGRADED(16);
        public final int storageSize;
        public final int rowColumn;

        PlanterBlockType(int storageSize) {
            this.storageSize = storageSize;
            this.rowColumn = (int) Math.sqrt(storageSize);
        }
    }
}
