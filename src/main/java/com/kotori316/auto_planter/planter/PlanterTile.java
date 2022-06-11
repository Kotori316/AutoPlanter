package com.kotori316.auto_planter.planter;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import com.kotori316.auto_planter.AutoPlanter;

public abstract class PlanterTile extends BlockEntity implements Container, ExtendedScreenHandlerFactory {
    public final NonNullList<ItemStack> inventoryContents;

    public PlanterTile(BlockEntityType<?> entityType, BlockPos pos, BlockState state) {
        super(entityType, pos, state);
        inventoryContents = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
    }

    public void plantSapling() {
        if (level != null && !level.isClientSide) {
            BlockPos upPos = getBlockPos().above();
            BlockState state = level.getBlockState(upPos);
            if (level.getFluidState(upPos).isEmpty()) {
                for (ItemStack maybeSapling : inventoryContents) {
                    if (isPlantable(maybeSapling, getBlockState().getValue(PlanterBlock.TRIGGERED))) {
                        DirectionalPlaceContext context = new DirectionalPlaceContext(level, upPos, Direction.DOWN, maybeSapling, Direction.UP);
                        if (state.canBeReplaced(context)) {
                            ((BlockItem) maybeSapling.getItem()).place(context);
                            break;
                        }
                    }
                }
            }
        }
    }

    public abstract PlanterBlock.PlanterBlockType blockType();

    @Override
    public void saveAdditional(CompoundTag tag) {
        ContainerHelper.saveAllItems(tag, inventoryContents);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ContainerHelper.loadAllItems(tag, inventoryContents);
    }

    @Override
    public int getContainerSize() {
        return blockType().storageSize;
    }

    @Override
    public boolean isEmpty() {
        return inventoryContents.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int index) {
        return index >= 0 && index < this.inventoryContents.size() ? this.inventoryContents.get(index) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack itemstack = ContainerHelper.removeItem(this.inventoryContents, index, count);
        if (!itemstack.isEmpty()) {
            this.setChanged();
        }
        return itemstack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack itemstack = this.inventoryContents.get(index);
        if (itemstack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.inventoryContents.set(index, ItemStack.EMPTY);
            return itemstack;
        }
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        this.inventoryContents.set(index, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5) <= 64;
    }

    @Override
    public void stopOpen(Player player) {
        if (level != null && !level.isClientSide) plantSapling();
    }

    @Override
    public void clearContent() {
        inventoryContents.clear();
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        return isPlantable(stack, true);
    }

    public static boolean isPlantable(ItemStack stack, boolean triggered) {
        if (stack.isEmpty()) return false;
        Item item = stack.getItem();
        if (item instanceof BlockItem) {
            Block block = ((BlockItem) item).getBlock();
            if (block.defaultBlockState().is(BlockTags.SAPLINGS)) {
                return true;
            }
            if (triggered) {
                // Seed and crops
                return block instanceof CropBlock;
            }
        }
        return false;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player p) {
        return new PlanterContainer(id, p, getBlockPos());
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBlockPos(worldPosition);
    }

    public static class Normal extends PlanterTile {

        public static final String TILE_ID = AutoPlanter.AUTO_PLANTER + ":" + PlanterBlock.Normal.name + "_tile";

        public Normal(BlockPos pos, BlockState state) {
            super(AutoPlanter.Holder.PLANTER_TILE_TILE_ENTITY_TYPE, pos, state);
        }

        @Override
        public PlanterBlock.PlanterBlockType blockType() {
            return PlanterBlock.PlanterBlockType.NORMAL;
        }
    }

    public static class Upgraded extends PlanterTile {
        public static final String TILE_ID = AutoPlanter.AUTO_PLANTER + ":" + PlanterBlock.Upgraded.name + "_tile";

        public Upgraded(BlockPos pos, BlockState state) {
            super(AutoPlanter.Holder.PLANTER_UPGRADED_TILE_ENTITY_TYPE, pos, state);
        }

        @Override
        public PlanterBlock.PlanterBlockType blockType() {
            return PlanterBlock.PlanterBlockType.UPGRADED;
        }
    }
}
