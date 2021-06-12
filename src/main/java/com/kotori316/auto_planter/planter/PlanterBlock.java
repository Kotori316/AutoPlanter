package com.kotori316.auto_planter.planter;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
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

public class PlanterBlock extends BlockWithEntity {
    public static final BooleanProperty TRIGGERED = Properties.TRIGGERED;
    public static final String name = "planter";

    public final BlockItem blockItem;

    public PlanterBlock() {
        super(Block.Settings.copy(Blocks.DIRT).strength(0.6f, 100).allowsSpawning((state, world, pos, type) -> false));
        blockItem = new BlockItem(this, new Item.Settings().group(ItemGroup.DECORATIONS));
        setDefaultState(getStateManager().getDefaultState().with(TRIGGERED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
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
    public PlanterTile createBlockEntity(BlockPos pos, BlockState state) {
        return AutoPlanter.Holder.PLANTER_TILE_TILE_ENTITY_TYPE.instantiate(pos, state);
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
            if (worldIn.getBlockEntity(pos) instanceof PlanterTile tile) {
                tile.plantSapling();
            }
        }
    }
}
