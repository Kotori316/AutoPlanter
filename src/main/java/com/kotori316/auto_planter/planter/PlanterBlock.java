package com.kotori316.auto_planter.planter;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.mojang.datafixers.util.Pair;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

import com.kotori316.auto_planter.AutoPlanter;

public abstract class PlanterBlock extends BaseEntityBlock {
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

    public final BlockItem blockItem;
    public final PlanterBlockType blockType;

    public PlanterBlock(PlanterBlockType blockType, String name) {
        super(Block.Properties.of(Material.DIRT).strength(0.6f, 100).sound(SoundType.GRAVEL));
        setRegistryName(AutoPlanter.AUTO_PLANTER, name);
        this.blockType = blockType;
        blockItem = new BlockItem(this, new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS));
        blockItem.setRegistryName(AutoPlanter.AUTO_PLANTER, name);
        registerDefaultState(getStateDefinition().any().setValue(TRIGGERED, false));
        try {
            // temporal way to handle hoe activation.
            @SuppressWarnings("unchecked")
            var map = (Map<Block, Pair<Predicate<UseOnContext>, Consumer<UseOnContext>>>) ObfuscationReflectionHelper.findField(HoeItem.class, "f_41332_").get(null);
            map.put(this, Pair.of(t -> !t.getLevel().getBlockState(t.getClickedPos()).getValue(TRIGGERED), HoeItem.changeIntoState(defaultBlockState().setValue(TRIGGERED, true))));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract BlockEntityType<? extends PlanterTile> getEntityType();

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TRIGGERED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (worldIn.getBlockEntity(pos) instanceof PlanterTile planterTile) {
            ItemStack stack = player.getItemInHand(handIn);
            boolean notHasSapling = hit.getDirection() != Direction.UP || !PlanterTile.isPlantable(stack, true);
            boolean notHasHoe = !player.getMainHandItem().getToolTypes().contains(ToolType.HOE) &&
                !player.getOffhandItem().getToolTypes().contains(ToolType.HOE);
            if (notHasSapling && notHasHoe) {
                if (!worldIn.isClientSide)
                    NetworkHooks.openGui(((ServerPlayer) player), planterTile, pos);
                return InteractionResult.SUCCESS;
            }
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }

    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, Level world, BlockPos pos, Player player, ItemStack stack, ToolType toolType) {
        if (toolType.equals(ToolType.HOE) && state.is(this) && !state.getValue(TRIGGERED)) {
            return state.setValue(TRIGGERED, Boolean.TRUE);
        } else {
            return super.getToolModifiedState(state, world, pos, player, stack, toolType);
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return getEntityType().create(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
        PlantType type = plantable.getPlantType(world, pos.relative(facing));
        if (state.getValue(TRIGGERED)) {
            return type == PlantType.PLAINS || type == PlantType.CROP;
        } else
            return type == PlantType.PLAINS;
    }

    @Override
    public boolean isFertile(BlockState state, BlockGetter world, BlockPos pos) {
        return state.getValue(TRIGGERED);
    }

    @Override
    public boolean canCreatureSpawn(BlockState state, BlockGetter world, BlockPos pos, SpawnPlacements.Type type, EntityType<?> entityType) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(worldIn.getBlockEntity(pos));
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
            if (worldIn.getBlockEntity(pos) instanceof PlanterTile tile) {
                tile.plantSapling();
            }
        }
    }

    public static class Normal extends PlanterBlock {
        public static final String name = "planter";

        public Normal() {
            super(PlanterBlockType.NORMAL, name);
        }

        @Override
        protected BlockEntityType<? extends PlanterTile> getEntityType() {
            return AutoPlanter.Holder.PLANTER_TILE_TILE_ENTITY_TYPE;
        }
    }

    public static class Upgraded extends PlanterBlock {
        public static final String name = "planter_upgraded";

        public Upgraded() {
            super(PlanterBlockType.UPGRADED, name);
        }

        @Override
        protected BlockEntityType<? extends PlanterTile> getEntityType() {
            return AutoPlanter.Holder.PLANTER_UPGRADED_TILE_ENTITY_TYPE;
        }

        @SubscribeEvent
        public void grow(BlockEvent.CropGrowEvent.Pre event) {
            if (event.getWorld().getBlockState(event.getPos().below()).is(this))
                event.setResult(Event.Result.ALLOW);
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
