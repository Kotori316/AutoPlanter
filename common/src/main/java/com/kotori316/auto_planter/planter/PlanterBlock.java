package com.kotori316.auto_planter.planter;

import com.kotori316.auto_planter.AutoPlanterCommon;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Supplier;

public abstract class PlanterBlock extends BaseEntityBlock {
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
    public final BlockItem blockItem;
    final PlanterBlockType blockType;
    final String name;
    protected final MapCodec<? extends PlanterBlock> planterCodec;

    protected PlanterBlock(PlanterBlockType blockType, String name) {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).strength(0.6f, 100).sound(SoundType.GRAVEL));
        this.blockType = blockType;
        this.name = name;
        this.blockItem = new BlockItem(this, new Item.Properties());
        registerDefaultState(getStateDefinition().any().setValue(TRIGGERED, false));
        this.planterCodec = this.createCodec();
    }

    @Override
    protected final void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TRIGGERED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public abstract InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit);

    @Override
    public final BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return this.blockType.entityType.get().create(pos, state);
    }

    @Override
    public final RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public final boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public final int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(worldIn.getBlockEntity(pos));
    }

    @Override
    @SuppressWarnings("deprecation")
    public final void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
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
    public final void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
        if (!worldIn.isClientSide) {
            if (worldIn.getBlockEntity(pos) instanceof PlanterTile tile) {
                tile.plantSapling();
            }
        }
    }

    @Override
    protected MapCodec<? extends PlanterBlock> codec() {
        return planterCodec;
    }

    /**
     * Override if the concrete class has constructor with some arguments.
     *
     * @return the codec to create instance of this block
     */
    protected MapCodec<? extends PlanterBlock> createCodec() {
        return createCodec(getClass());
    }

    static MapCodec<? extends PlanterBlock> createCodec(Class<? extends PlanterBlock> clazz) {
        return simpleCodec(p -> {
            try {
                var constructor = clazz.getConstructor();
                return constructor.newInstance();
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public enum PlanterBlockType {
        NORMAL(3, () -> AutoPlanterCommon.accessor.normalType()),
        UPGRADED(4, () -> AutoPlanterCommon.accessor.upgradedType()),
        ;
        public final int storageSize;
        public final int rowColumn;
        public final Supplier<BlockEntityType<? extends PlanterTile>> entityType;

        PlanterBlockType(int rowColumn, Supplier<BlockEntityType<? extends PlanterTile>> entityType) {
            this.storageSize = rowColumn * rowColumn;
            this.rowColumn = rowColumn;
            this.entityType = entityType;
        }
    }
}
