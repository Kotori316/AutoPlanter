package com.kotori316.auto_planter.fabric.planter;

import com.kotori316.auto_planter.AutoPlanterCommon;
import com.kotori316.auto_planter.planter.PlanterBlock;
import com.kotori316.auto_planter.planter.PlanterTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public sealed abstract class PlanterBlockFabric extends PlanterBlock {
    protected PlanterBlockFabric(PlanterBlock.PlanterBlockType blockType, String name) {
        super(blockType, name);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (worldIn.getBlockEntity(pos) instanceof PlanterTile planterTile) {
            boolean notHasSapling = hit.getDirection() != Direction.UP || !PlanterTile.isPlantable(stack, true);
            boolean notHasHoe = !(player.getMainHandItem().getItem() instanceof HoeItem) &&
                                !(player.getOffhandItem().getItem() instanceof HoeItem);
            if (notHasSapling && notHasHoe) {
                if (!worldIn.isClientSide) {
                    player.openMenu(planterTile);
                }
                return ItemInteractionResult.SUCCESS;
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
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
