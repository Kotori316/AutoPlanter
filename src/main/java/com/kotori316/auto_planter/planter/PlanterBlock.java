package com.kotori316.auto_planter.planter;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.fml.network.NetworkHooks;

import com.kotori316.auto_planter.AutoPlanter;

public class PlanterBlock extends ContainerBlock {
    public static final String name = "planter";

    public final BlockItem blockItem;

    public PlanterBlock() {
        super(Block.Properties.create(Material.EARTH).hardnessAndResistance(0.6f, 100).sound(SoundType.GROUND));
        setRegistryName(AutoPlanter.AUTO_PLANTER, name);
        blockItem = new BlockItem(this, new Item.Properties().group(ItemGroup.DECORATIONS));
        blockItem.setRegistryName(AutoPlanter.AUTO_PLANTER, name);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        TileEntity entity = worldIn.getTileEntity(pos);
        if (entity instanceof PlanterTile) {
            ItemStack stack = player.getHeldItem(handIn);
            if (hit.getFace() != Direction.UP || !PlanterTile.isSapling(stack)) {
                if (!worldIn.isRemote)
                    NetworkHooks.openGui(((ServerPlayerEntity) player), ((PlanterTile) entity), pos);
                return true;
            }
        }

        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return AutoPlanter.Holder.PLANTER_TILE_TILE_ENTITY_TYPE.create();
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction facing, IPlantable plantable) {
        PlantType type = plantable.getPlantType(world, pos.offset(facing));
        return type == PlantType.Plains;
    }

    @Override
    public boolean canCreatureSpawn(BlockState state, IBlockReader world, BlockPos pos, EntitySpawnPlacementRegistry.PlacementType type, @Nullable EntityType<?> entityType) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
        return Container.calcRedstone(worldIn.getTileEntity(pos));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            if (!worldIn.isRemote) {
                TileEntity entity = worldIn.getTileEntity(pos);
                if (entity instanceof PlanterTile) {
                    PlanterTile inventory = (PlanterTile) entity;
                    InventoryHelper.dropInventoryItems(worldIn, pos, inventory);
                    worldIn.updateComparatorOutputLevel(pos, state.getBlock());
                }
            }
            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
        if (!worldIn.isRemote) {
            TileEntity t = worldIn.getTileEntity(pos);
            if (t instanceof PlanterTile) {
                PlanterTile tile = (PlanterTile) t;
                tile.plantSapling();
            }
        }
    }
}
