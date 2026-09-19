package com.kotori316.auto_planter.fabric.planter;

import com.kotori316.auto_planter.AutoPlanterCommon;
import com.kotori316.auto_planter.planter.PlanterBlock;
import com.kotori316.auto_planter.planter.PlanterTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public sealed abstract class PlanterBlockFabric extends PlanterBlock {
    protected PlanterBlockFabric(PlanterBlock.PlanterBlockType blockType, String name) {
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
                if (!worldIn.isClientSide()) {
                    player.openMenu(planterTile);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    public static final class Normal extends PlanterBlockFabric {

        public Normal() {
            super(PlanterBlockType.NORMAL, AutoPlanterCommon.BLOCK_NORMAL);
        }
    }

    public static final class Upgraded extends PlanterBlockFabric {

        public Upgraded() {
            super(PlanterBlockType.UPGRADED, AutoPlanterCommon.BLOCK_UPGRADED);
        }

    }

}
