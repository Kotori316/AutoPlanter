package com.kotori316.autoplanter;

import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

import com.kotori316.autoplanter.tiles.TilePlanter;

public class BlockPlanter extends BlockContainer {
    public static final Material DIRT = new MaterialDirt();
    public final ItemBlock itemBlock;
    public static final String unlocalizedName = "autoplanter.planter";

    public BlockPlanter() {
        super(DIRT);
        setUnlocalizedName("BlockPlanter");
        setHardness(0.6f);
        setResistance(100);
        setSoundType(SoundType.GROUND);
        setCreativeTab(CreativeTabs.DECORATIONS);
        setUnlocalizedName(unlocalizedName);
        setRegistryName(AutoPlanter.modID, "planter");
        itemBlock = new ItemBlock(this);
        itemBlock.setRegistryName(AutoPlanter.modID, "planter");
    }

    @Override
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
        Block block = plantable.getPlant(world, pos.offset(EnumFacing.UP)).getBlock();
        return block instanceof BlockSapling;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!worldIn.isRemote) {
            Optional.ofNullable((TilePlanter) worldIn.getTileEntity(pos)).ifPresent(TilePlanter::plantSapling);
        }
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TilePlanter();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!playerIn.isSneaking() && !(facing == EnumFacing.UP && TilePlanter.isValid.test(playerIn.getHeldItem(hand)))) {
            playerIn.openGui(AutoPlanter.getInstance(), 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return false;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity entity = worldIn.getTileEntity(pos);
        if (IInventory.class.isInstance(entity)) {
            IInventory inventory = (IInventory) entity;
            InventoryHelper.dropInventoryItems(worldIn, pos, inventory);
            worldIn.updateComparatorOutputLevel(pos, this);
        }
        super.breakBlock(worldIn, pos, state);
    }

    private static class MaterialDirt extends Material {
        public MaterialDirt() {
            super(MapColor.DIRT);
            setImmovableMobility();
        }
    }
}
