package com.kotori316.auto_planter.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

import com.kotori316.auto_planter.AutoPlanter;

public class CheckPlantableItem extends Item {
    public CheckPlantableItem() {
        super(new Item.Properties().group(ItemGroup.TOOLS));
        setRegistryName(AutoPlanter.AUTO_PLANTER, "check_plantable");
    }
}
