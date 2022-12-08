package com.kotori316.auto_planter;

import com.mojang.datafixers.DSL;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kotori316.auto_planter.planter.PlanterBlock;
import com.kotori316.auto_planter.planter.PlanterContainer;
import com.kotori316.auto_planter.planter.PlanterGui;
import com.kotori316.auto_planter.planter.PlanterTile;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(AutoPlanter.AUTO_PLANTER)
public final class AutoPlanter {
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String AUTO_PLANTER = "auto_planter";

    public AutoPlanter() {
        // Register ourselves for server and other game events we are interested in
        // MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("{} initialization", AUTO_PLANTER);
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = AUTO_PLANTER)
    public static final class RegistryEvents {
        @SubscribeEvent
        public static void register(RegisterEvent event) {
            event.register(Registries.BLOCK, RegistryEvents::onBlocksRegistry);
            event.register(Registries.ITEM, RegistryEvents::onItemsRegistry);
            event.register(Registries.BLOCK_ENTITY_TYPE, RegistryEvents::tiles);
            event.register(Registries.MENU, RegistryEvents::containers);
        }

        public static void onBlocksRegistry(final RegisterEvent.RegisterHelper<Block> helper) {
            // register a new block here
            helper.register(new ResourceLocation(AUTO_PLANTER, Holder.PLANTER_BLOCK.name), Holder.PLANTER_BLOCK);
            helper.register(new ResourceLocation(AUTO_PLANTER, Holder.PLANTER_UPGRADED_BLOCK.name), Holder.PLANTER_UPGRADED_BLOCK);
            MinecraftForge.EVENT_BUS.register(Holder.PLANTER_UPGRADED_BLOCK);
        }

        public static void onItemsRegistry(final RegisterEvent.RegisterHelper<Item> helper) {
//            itemRegistryEvent.getRegistry().register(Holder.CHECK_PLANTABLE_ITEM);
            helper.register(new ResourceLocation(AUTO_PLANTER, Holder.PLANTER_BLOCK.name), Holder.PLANTER_BLOCK.blockItem);
            helper.register(new ResourceLocation(AUTO_PLANTER, Holder.PLANTER_UPGRADED_BLOCK.name), Holder.PLANTER_UPGRADED_BLOCK.blockItem);
        }

        public static void tiles(RegisterEvent.RegisterHelper<BlockEntityType<?>> helper) {
            helper.register(new ResourceLocation(PlanterTile.Normal.TILE_ID), Holder.PLANTER_TILE_TILE_ENTITY_TYPE);
            helper.register(new ResourceLocation(PlanterTile.Upgraded.TILE_ID), Holder.PLANTER_UPGRADED_TILE_ENTITY_TYPE);
        }

        public static void containers(RegisterEvent.RegisterHelper<MenuType<?>> helper) {
            helper.register(new ResourceLocation(PlanterContainer.GUI_ID), Holder.PLANTER_CONTAINER_TYPE);
        }

        @SubscribeEvent
        public static void clientInit(FMLClientSetupEvent event) {
            MenuScreens.register(Holder.PLANTER_CONTAINER_TYPE, PlanterGui::new);
        }

        @SubscribeEvent
        public static void creativeTab(CreativeModeTabEvent.BuildContents event) {
            event.registerSimple(CreativeModeTabs.FUNCTIONAL_BLOCKS, Holder.PLANTER_BLOCK);
            event.registerSimple(CreativeModeTabs.FUNCTIONAL_BLOCKS, Holder.PLANTER_UPGRADED_BLOCK);
        }
    }

    public static class Holder {
        //        public static final CheckPlantableItem CHECK_PLANTABLE_ITEM = new CheckPlantableItem();
        public static final PlanterBlock PLANTER_BLOCK = new PlanterBlock.Normal();
        public static final PlanterBlock PLANTER_UPGRADED_BLOCK = new PlanterBlock.Upgraded();
        public static final BlockEntityType<PlanterTile.Normal> PLANTER_TILE_TILE_ENTITY_TYPE =
            BlockEntityType.Builder.of(PlanterTile.Normal::new, PLANTER_BLOCK).build(DSL.emptyPartType());
        public static final BlockEntityType<PlanterTile.Upgraded> PLANTER_UPGRADED_TILE_ENTITY_TYPE =
            BlockEntityType.Builder.of(PlanterTile.Upgraded::new, PLANTER_UPGRADED_BLOCK).build(DSL.emptyPartType());
        public static final MenuType<PlanterContainer> PLANTER_CONTAINER_TYPE =
            IForgeMenuType.create((id, inv, data) -> new PlanterContainer(id, inv.player, data.readBlockPos(), Holder.PLANTER_CONTAINER_TYPE));
    }
}
