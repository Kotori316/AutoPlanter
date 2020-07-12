package com.kotori316.auto_planter;

import com.mojang.datafixers.DSL;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
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
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String AUTO_PLANTER = "auto_planter";

    public AutoPlanter() {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = AUTO_PLANTER)
    public static final class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
//            LOGGER.info("HELLO from Register Block");
            blockRegistryEvent.getRegistry().register(Holder.PLANTER_BLOCK);
        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
//            itemRegistryEvent.getRegistry().register(Holder.CHECK_PLANTABLE_ITEM);
            itemRegistryEvent.getRegistry().register(Holder.PLANTER_BLOCK.blockItem);
        }

        @SubscribeEvent
        public static void tiles(RegistryEvent.Register<TileEntityType<?>> e) {
            e.getRegistry().register(Holder.PLANTER_TILE_TILE_ENTITY_TYPE.setRegistryName(PlanterTile.TILE_ID));
        }

        @SubscribeEvent
        public static void containers(RegistryEvent.Register<ContainerType<?>> e) {
            e.getRegistry().register(Holder.PLANTER_CONTAINER_TYPE.setRegistryName(PlanterContainer.GUI_ID));
        }

        @SubscribeEvent
        public static void clientInit(FMLClientSetupEvent event) {
            ScreenManager.registerFactory(Holder.PLANTER_CONTAINER_TYPE, PlanterGui::new);
        }
    }

    public static class Holder {
//        public static final CheckPlantableItem CHECK_PLANTABLE_ITEM = new CheckPlantableItem();
        public static final PlanterBlock PLANTER_BLOCK = new PlanterBlock();
        public static final TileEntityType<PlanterTile> PLANTER_TILE_TILE_ENTITY_TYPE =
            TileEntityType.Builder.create(PlanterTile::new, PLANTER_BLOCK).build(DSL.emptyPartType());
        public static final ContainerType<PlanterContainer> PLANTER_CONTAINER_TYPE =
            IForgeContainerType.create((id, inv, data) -> new PlanterContainer(id, inv.player, data.readBlockPos(), Holder.PLANTER_CONTAINER_TYPE));
    }
}
