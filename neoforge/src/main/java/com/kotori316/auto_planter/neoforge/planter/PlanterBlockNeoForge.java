package com.kotori316.auto_planter.neoforge.planter;

import com.kotori316.auto_planter.AutoPlanterCommon;
import com.kotori316.auto_planter.planter.PlanterBlock;
import com.kotori316.auto_planter.planter.PlanterTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;
import org.jetbrains.annotations.Nullable;

public sealed abstract class PlanterBlockNeoForge extends PlanterBlock {

    PlanterBlockNeoForge(PlanterBlockType blockType, String name) {
        super(blockType, name);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (player.getMainHandItem().canPerformAction(ItemAbilities.HOE_TILL) ||
            player.getOffhandItem().canPerformAction(ItemAbilities.HOE_TILL)) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
        if (worldIn.getBlockEntity(pos) instanceof PlanterTile planterTile) {
            boolean notHasSapling = hit.getDirection() != Direction.UP || !PlanterTile.isPlantable(stack, true);
            if (notHasSapling) {
                if (!worldIn.isClientSide) {
                    player.openMenu(planterTile, pos);
                }
                return ItemInteractionResult.SUCCESS;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        if (itemAbility == ItemAbilities.HOE_TILL && state.is(this) && !state.getValue(TRIGGERED)) {
            return state.setValue(TRIGGERED, Boolean.TRUE);
        } else {
            return super.getToolModifiedState(state, context, itemAbility, simulate);
        }
    }

    @Override
    public TriState canSustainPlant(BlockState state, BlockGetter level, BlockPos soilPosition, Direction facing, BlockState plant) {
        var block = plant.getBlock();
        if (block instanceof SaplingBlock) {
            return TriState.TRUE;
        }
        if (block instanceof CropBlock) {
            if (state.getValue(TRIGGERED)) {
                return TriState.TRUE;
            }
        }
        return super.canSustainPlant(state, level, soilPosition, facing, plant);
    }

    @Override
    public final boolean isFertile(BlockState state, BlockGetter world, BlockPos pos) {
        return state.getValue(TRIGGERED);
    }

    public static final class Normal extends PlanterBlockNeoForge {

        public Normal() {
            super(PlanterBlockType.NORMAL, AutoPlanterCommon.BLOCK_NORMAL);
        }
    }

    public static final class Upgraded extends PlanterBlockNeoForge {

        public Upgraded() {
            super(PlanterBlockType.UPGRADED, AutoPlanterCommon.BLOCK_UPGRADED);
        }

        @SubscribeEvent
        public void grow(CropGrowEvent.Pre event) {
            if (event.getLevel().getBlockState(event.getPos().below()).is(this))
                event.setResult(CropGrowEvent.Pre.Result.GROW);
        }
    }

}
