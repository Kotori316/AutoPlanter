package com.kotori316.auto_planter.planter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

import com.kotori316.auto_planter.AutoPlanter;

public class PlanterTile extends TileEntity implements IInventory, INamedContainerProvider {
    public static final String TILE_ID = AutoPlanter.AUTO_PLANTER + ":" + PlanterBlock.name + "_tile";
    public static final int SIZE = 9;
    public final NonNullList<ItemStack> inventoryContents;
    public final IItemHandlerModifiable handler = new InvWrapper(this);
    private final LazyOptional<IItemHandlerModifiable> handlerLazyOptional = LazyOptional.of(() -> handler);

    public PlanterTile() {
        super(AutoPlanter.Holder.PLANTER_TILE_TILE_ENTITY_TYPE);
        inventoryContents = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
    }

    public void plantSapling() {
        if (world != null && !world.isRemote) {
            BlockPos upPos = getPos().up();
            BlockState state = world.getBlockState(upPos);
            if (world.getFluidState(upPos).isEmpty()) { // Water removes sapling immediately.
                for (ItemStack maybeSapling : inventoryContents) {
                    if (isPlantable(maybeSapling, getBlockState().get(PlanterBlock.TRIGGERED))) {
                        DirectionalPlaceContext context = new DirectionalPlaceContext(world, upPos, Direction.DOWN, maybeSapling, Direction.UP);
                        if (state.isReplaceable(context)) {
                            ((BlockItem) maybeSapling.getItem()).tryPlace(context);
                        }
                    }
                }
            }
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        ItemStackHelper.saveAllItems(compound, inventoryContents);
        return super.write(compound);
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        ItemStackHelper.loadAllItems(compound, inventoryContents);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return handlerLazyOptional.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void remove() {
        super.remove();
        handlerLazyOptional.invalidate();
    }

    @Override
    public int getSizeInventory() {
        return SIZE;
    }

    @Override
    public boolean isEmpty() {
        return inventoryContents.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return index >= 0 && index < this.inventoryContents.size() ? this.inventoryContents.get(index) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack itemstack = ItemStackHelper.getAndSplit(this.inventoryContents, index, count);
        if (!itemstack.isEmpty()) {
            this.markDirty();
        }
        return itemstack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack itemstack = this.inventoryContents.get(index);
        if (itemstack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.inventoryContents.set(index, ItemStack.EMPTY);
            return itemstack;
        }
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.inventoryContents.set(index, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
        this.markDirty();
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        return player.getDistanceSq(getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5) <= 64;
    }

    @Override
    public void closeInventory(PlayerEntity player) {
        if (world != null && !world.isRemote) plantSapling();
    }

    @Override
    public void clear() {
        inventoryContents.clear();
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return isPlantable(stack, true);
    }

    public static boolean isPlantable(ItemStack stack, boolean triggered) {
        if (stack.isEmpty()) return false;
        Item item = stack.getItem();
        if (item instanceof BlockItem) {
            Block block = ((BlockItem) item).getBlock();
            if (BlockTags.SAPLINGS.contains(block)) {
                return true;
            }
            if (triggered) {
                // Seed and crops
                return block instanceof CropsBlock;
            }
        }
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(AutoPlanter.Holder.PLANTER_BLOCK.getTranslationKey());
    }

    @Override
    public Container createMenu(int id, PlayerInventory inv, PlayerEntity p) {
        return new PlanterContainer(id, p, getPos(), AutoPlanter.Holder.PLANTER_CONTAINER_TYPE);
    }

}
