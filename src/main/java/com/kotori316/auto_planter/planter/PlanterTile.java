package com.kotori316.auto_planter.planter;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import com.kotori316.auto_planter.AutoPlanter;

public class PlanterTile extends BlockEntity implements Inventory, ExtendedScreenHandlerFactory {
    public static final String TILE_ID = AutoPlanter.AUTO_PLANTER + ":" + PlanterBlock.name + "_tile";
    public static final int SIZE = 9;
    public final DefaultedList<ItemStack> inventoryContents;

    public PlanterTile() {
        super(AutoPlanter.Holder.PLANTER_TILE_TILE_ENTITY_TYPE);
        inventoryContents = DefaultedList.ofSize(size(), ItemStack.EMPTY);
    }

    public void plantSapling() {
        if (world != null && !world.isClient) {
            BlockPos upPos = getPos().up();
            BlockState state = world.getBlockState(upPos);
            if (world.getFluidState(upPos).isEmpty()) {
                for (ItemStack maybeSapling : inventoryContents) {
                    if (isSapling(maybeSapling)) {
                        AutomaticItemPlacementContext context = new AutomaticItemPlacementContext(world, upPos, Direction.DOWN, maybeSapling, Direction.UP);
                        if (state.canReplace(context)) {
                            ((BlockItem) maybeSapling.getItem()).place(context);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        Inventories.toTag(tag, inventoryContents);
        return super.toTag(tag);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        Inventories.fromTag(tag, inventoryContents);
    }

    @Override
    public int size() {
        return SIZE;
    }

    @Override
    public boolean isEmpty() {
        return inventoryContents.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int index) {
        return index >= 0 && index < this.inventoryContents.size() ? this.inventoryContents.get(index) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int index, int count) {
        ItemStack itemstack = Inventories.splitStack(this.inventoryContents, index, count);
        if (!itemstack.isEmpty()) {
            this.markDirty();
        }
        return itemstack;
    }

    @Override
    public ItemStack removeStack(int index) {
        ItemStack itemstack = this.inventoryContents.get(index);
        if (itemstack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.inventoryContents.set(index, ItemStack.EMPTY);
            return itemstack;
        }
    }

    @Override
    public void setStack(int index, ItemStack stack) {
        this.inventoryContents.set(index, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
        this.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return player.squaredDistanceTo(getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5) <= 64;
    }

    @Override
    public void onClose(PlayerEntity player) {
        if (world != null && !world.isClient) plantSapling();
    }

    @Override
    public void clear() {
        inventoryContents.clear();
    }

    @Override
    public boolean isValid(int index, ItemStack stack) {
        return isSapling(stack);
    }

    public static boolean isSapling(ItemStack stack) {
        if (stack.isEmpty()) return false;
        Item item = stack.getItem();
        if (item instanceof BlockItem) {
            Block block = ((BlockItem) item).getBlock();
            return BlockTags.SAPLINGS.contains(block);
        }
        return false;
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText(AutoPlanter.Holder.PLANTER_BLOCK.getTranslationKey());
    }

    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inv, PlayerEntity p) {
        return new PlanterContainer(id, p, getPos());
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }
}
