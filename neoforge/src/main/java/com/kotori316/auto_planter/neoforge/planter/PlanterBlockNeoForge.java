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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.IPlantable;
import net.neoforged.neoforge.common.PlantType;
import net.neoforged.neoforge.common.ToolAction;
import net.neoforged.neoforge.common.ToolActions;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.Nullable;

public sealed abstract class PlanterBlockNeoForge extends PlanterBlock {

    PlanterBlockNeoForge(PlanterBlockType blockType, String name) {
        super(blockType, name);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (player.getMainHandItem().canPerformAction(ToolActions.HOE_TILL) ||
            player.getOffhandItem().canPerformAction(ToolActions.HOE_TILL)) {
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

    @Nullable
    @Override
    public final BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
        if (toolAction == ToolActions.HOE_TILL && state.is(this) && !state.getValue(TRIGGERED)) {
            return state.setValue(TRIGGERED, Boolean.TRUE);
        } else {
            return super.getToolModifiedState(state, context, toolAction, simulate);
        }
    }

    @Override
    public final boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
        PlantType type = plantable.getPlantType(world, pos.relative(facing));
        if (state.getValue(TRIGGERED)) {
            return type == PlantType.PLAINS || type == PlantType.CROP;
        } else {
            return type == PlantType.PLAINS;
        }
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
        public void grow(BlockEvent.CropGrowEvent.Pre event) {
            if (event.getLevel().getBlockState(event.getPos().below()).is(this))
                event.setResult(Event.Result.ALLOW);
        }
    }

}
