package com.kotori316.autoplanter;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(name = AutoPlanter.MOD_NAME, modid = AutoPlanter.modID, version = "${version}")
public class AutoPlanter {

    public static final AutoPlanter instance;
    public static final String MOD_NAME = "AutoPlanter";
    public static final String modID = "autoplanter";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public static final BlockPlanter BLOCK_PLANTER = new BlockPlanter();

    static {
        instance = new AutoPlanter();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(instance);
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
    }

    @EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        try {
            Set<ItemDamage> set = OreDictionary.getOres("treeSapling").stream().map(ItemDamage::apply).filter(ItemDamage::isBlock).collect(Collectors.toSet());
            EnumHelper.setFailsafeFieldValue(TilePlanter.class.getDeclaredField("SAPLINGS"), null, Collections.unmodifiableSet(set));
        } catch (Exception e) {
            LOGGER.fatal("What happened?", e);
        }
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(BLOCK_PLANTER);
        TileEntity.register(modID + ":planter", TilePlanter.class);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(BLOCK_PLANTER.itemBlock);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(BLOCK_PLANTER.itemBlock, 0, new ModelResourceLocation(
                Objects.requireNonNull(BLOCK_PLANTER.getRegistryName()), "inventory"));
    }

    @Mod.InstanceFactory
    public static AutoPlanter getInstance() {
        return instance;
    }
}