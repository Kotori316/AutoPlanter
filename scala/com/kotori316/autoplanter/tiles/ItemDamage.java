package com.kotori316.autoplanter.tiles;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

public class ItemDamage implements Comparable<ItemDamage> {

    private final Item item;
    private final int meta;
    private final NBTTagCompound nbtTagCompound;
    public static final ItemDamage INVALID = new ItemDamage(Item.getItemFromBlock(Blocks.AIR), 0, null) {
        @Override
        public boolean equals(Object obj) {
            return false;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public String toString() {
            return "ItemDamage.INVALID";
        }

        @Override
        public boolean isBlock() {
            return false;
        }
    };

    public ItemDamage(@Nonnull Item item, int meta, @Nullable NBTTagCompound nbtTagCompound) {
        this.item = item;
        this.meta = meta;
        this.nbtTagCompound = nbtTagCompound;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        if (ItemDamage.class.isInstance(obj)) {
            ItemDamage that = (ItemDamage) obj;
            return this.item == that.item &&
                    (this.meta == OreDictionary.WILDCARD_VALUE || that.meta == OreDictionary.WILDCARD_VALUE || this.meta == that.meta) &&
                    Objects.equals(this.nbtTagCompound, that.nbtTagCompound);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        String nbtString = nbtTagCompound == null ? "" : " NBT : " + nbtTagCompound;
        return item.getRegistryName() + "@" + meta + nbtString;
    }

    @Override
    public int hashCode() {
        return item.hashCode();
    }

    @Override
    public int compareTo(ItemDamage that) {
        return Integer.compare(Item.getIdFromItem(this.item), Item.getIdFromItem(that.item));
    }

    public boolean isBlock() {
        return item instanceof ItemBlock;
    }

    public Block getBlock() {
        if (isBlock()) {
            return ((ItemBlock) item).getBlock();
        } else {
            return Blocks.AIR;
        }
    }

    public static ItemDamage apply(ItemStack stack) {
        if (stack.isEmpty())
            return INVALID;
        else
            return new ItemDamage(stack.getItem(), stack.getItemDamage(), stack.getTagCompound());
    }
}
