package com.kotori316.auto_planter.forge.planter;

import com.kotori316.auto_planter.AutoPlanterCommon;
import com.kotori316.auto_planter.planter.PlanterBlock;
import com.kotori316.auto_planter.planter.PlanterTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.Result;
import net.minecraftforge.event.level.BlockEvent;

import java.util.function.BiConsumer;

public sealed abstract class PlanterBlockForge extends PlanterBlock {

    PlanterBlockForge(PlanterBlockType blockType, String name) {
        super(blockType, name);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (stack.is(ItemTags.HOES)) {
            if (!state.getValue(TRIGGERED) && !worldIn.isClientSide()) {
                worldIn.setBlockAndUpdate(pos, state.setValue(TRIGGERED, Boolean.TRUE));
            }
            return InteractionResult.SUCCESS;
        }
        if (worldIn.getBlockEntity(pos) instanceof PlanterTile planterTile) {
            boolean notHasSapling = hit.getDirection() != Direction.UP || !PlanterTile.isPlantable(stack, true);
            if (notHasSapling) {
                if (!worldIn.isClientSide())
                    ((ServerPlayer) player).openMenu(planterTile, pos);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    public final boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
        Block block = plantable.getPlant(world, pos.relative(facing)).getBlock();
        if (block instanceof SaplingBlock) {
            return true;
        }
        if (state.getValue(TRIGGERED) && PlanterTile.isPlantableCrop(block)) {
            return true;
        }
        return super.canSustainPlant(state, world, pos, facing, plantable);
    }

    @Override
    public boolean onTreeGrow(BlockState state, LevelReader level, BiConsumer<BlockPos, BlockState> placeFunction, RandomSource randomSource, BlockPos pos, TreeFeature config) {
        // No action is needed in TrunkPlacer#placeBelowTrunkBlock
        return false;
    }

    @Override
    public final boolean isFertile(BlockState state, BlockGetter world, BlockPos pos) {
        return state.getValue(TRIGGERED);
    }

    public static final class Normal extends PlanterBlockForge {

        public Normal() {
            super(PlanterBlockType.NORMAL, AutoPlanterCommon.BLOCK_NORMAL);
        }
    }

    public static final class Upgraded extends PlanterBlockForge {

        public Upgraded() {
            super(PlanterBlockType.UPGRADED, AutoPlanterCommon.BLOCK_UPGRADED);
            BlockEvent.CropGrowEvent.Pre.BUS.addListener(this::grow);
        }

        public void grow(BlockEvent.CropGrowEvent.Pre event) {
            if (event.getLevel().getBlockState(event.getPos().below()).is(this)) {
                event.setResult(Result.ALLOW);
            }
        }
    }

}
