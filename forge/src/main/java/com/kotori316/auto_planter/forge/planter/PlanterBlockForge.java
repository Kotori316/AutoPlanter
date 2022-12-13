package com.kotori316.auto_planter.forge.planter;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import com.kotori316.auto_planter.AutoPlanterCommon;
import com.kotori316.auto_planter.planter.PlanterBlock;
import com.kotori316.auto_planter.planter.PlanterTile;

public sealed abstract class PlanterBlockForge extends PlanterBlock {

    PlanterBlockForge(PlanterBlockType blockType, String name) {
        super(blockType, name);
    }

    @Override
    public final InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (player.getMainHandItem().canPerformAction(ToolActions.HOE_TILL) ||
            player.getOffhandItem().canPerformAction(ToolActions.HOE_TILL)) {
            return InteractionResult.PASS;
        }
        if (worldIn.getBlockEntity(pos) instanceof PlanterTile planterTile) {
            ItemStack stack = player.getItemInHand(handIn);
            boolean notHasSapling = hit.getDirection() != Direction.UP || !PlanterTile.isPlantable(stack, true);
            if (notHasSapling) {
                if (!worldIn.isClientSide)
                    NetworkHooks.openScreen(((ServerPlayer) player), planterTile, pos);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
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

    @Override
    public final boolean isValidSpawn(BlockState state, BlockGetter world, BlockPos pos, SpawnPlacements.Type type, EntityType<?> entityType) {
        return false;
    }

    public static final class Normal extends PlanterBlockForge {

        public Normal() {
            super(PlanterBlockType.NORMAL, AutoPlanterCommon.BLOCK_NORMAL);
        }
    }

    public static final class Upgraded extends PlanterBlockForge {

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
