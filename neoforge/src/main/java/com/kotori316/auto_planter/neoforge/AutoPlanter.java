package com.kotori316.auto_planter.neoforge;

import com.kotori316.auto_planter.AutoPlanterCommon;
import com.kotori316.auto_planter.neoforge.planter.PlanterBlockNeoForge;
import com.kotori316.auto_planter.neoforge.planter.PlanterContainerNeoForge;
import com.kotori316.auto_planter.neoforge.planter.PlanterTileNeoForge;
import com.kotori316.auto_planter.planter.PlanterGui;
import com.mojang.datafixers.DSL;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

@Mod(AutoPlanterCommon.AUTO_PLANTER)
public final class AutoPlanter {
    public static final Logger LOGGER = AutoPlanterCommon.LOGGER;

    public AutoPlanter() {
        LOGGER.info("{} initialization", AutoPlanterCommon.AUTO_PLANTER);
    }

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = AutoPlanterCommon.AUTO_PLANTER)
    public static final class RegistryEvents {
        @SubscribeEvent
        public static void register(RegisterEvent event) {
            event.register(Registries.BLOCK, RegistryEvents::onBlocksRegistry);
            event.register(Registries.ITEM, RegistryEvents::onItemsRegistry);
            event.register(Registries.BLOCK_ENTITY_TYPE, RegistryEvents::tiles);
            event.register(Registries.MENU, RegistryEvents::containers);
        }

        public static void onBlocksRegistry(final RegisterEvent.RegisterHelper<Block> helper) {
            helper.register(ResourceLocation.fromNamespaceAndPath(AutoPlanterCommon.AUTO_PLANTER, AutoPlanterCommon.BLOCK_NORMAL), Holder.PLANTER_BLOCK);
            helper.register(ResourceLocation.fromNamespaceAndPath(AutoPlanterCommon.AUTO_PLANTER, AutoPlanterCommon.BLOCK_UPGRADED), Holder.PLANTER_UPGRADED_BLOCK);
            NeoForge.EVENT_BUS.register(Holder.PLANTER_UPGRADED_BLOCK);
        }

        public static void onItemsRegistry(final RegisterEvent.RegisterHelper<Item> helper) {
            helper.register(ResourceLocation.fromNamespaceAndPath(AutoPlanterCommon.AUTO_PLANTER, AutoPlanterCommon.BLOCK_NORMAL), Holder.PLANTER_BLOCK.blockItem);
            helper.register(ResourceLocation.fromNamespaceAndPath(AutoPlanterCommon.AUTO_PLANTER, AutoPlanterCommon.BLOCK_UPGRADED), Holder.PLANTER_UPGRADED_BLOCK.blockItem);
        }

        public static void tiles(RegisterEvent.RegisterHelper<BlockEntityType<?>> helper) {
            helper.register(ResourceLocation.parse(PlanterTileNeoForge.Normal.TILE_ID), Holder.PLANTER_TILE_TILE_ENTITY_TYPE);
            helper.register(ResourceLocation.parse(PlanterTileNeoForge.Upgraded.TILE_ID), Holder.PLANTER_UPGRADED_TILE_ENTITY_TYPE);
        }

        public static void containers(RegisterEvent.RegisterHelper<MenuType<?>> helper) {
            helper.register(ResourceLocation.parse(PlanterContainerNeoForge.GUI_ID), Holder.PLANTER_CONTAINER_TYPE);
        }

        @SubscribeEvent
        public static void clientInit(FMLClientSetupEvent event) {
        }

        @SubscribeEvent
        public static void creativeTab(BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey().equals(CreativeModeTabs.FUNCTIONAL_BLOCKS)) {
                event.accept(Holder.PLANTER_BLOCK);
                event.accept(Holder.PLANTER_UPGRADED_BLOCK);
            }
        }

        @SubscribeEvent
        public static void registerCapabilities(RegisterCapabilitiesEvent event) {
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, Holder.PLANTER_TILE_TILE_ENTITY_TYPE, PlanterTileNeoForge::getItemHandler);
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, Holder.PLANTER_UPGRADED_TILE_ENTITY_TYPE, PlanterTileNeoForge::getItemHandler);
        }

        @SubscribeEvent
        public static void registerMenu(RegisterMenuScreensEvent event) {
            event.register(Holder.PLANTER_CONTAINER_TYPE, PlanterGui::new);
        }
    }

    public static class Holder implements AutoPlanterCommon.TypeAccessor {
        public static final PlanterBlockNeoForge PLANTER_BLOCK = new PlanterBlockNeoForge.Normal();
        public static final PlanterBlockNeoForge PLANTER_UPGRADED_BLOCK = new PlanterBlockNeoForge.Upgraded();
        public static final BlockEntityType<PlanterTileNeoForge.Normal> PLANTER_TILE_TILE_ENTITY_TYPE =
            BlockEntityType.Builder.of(PlanterTileNeoForge.Normal::new, PLANTER_BLOCK).build(DSL.emptyPartType());
        public static final BlockEntityType<PlanterTileNeoForge.Upgraded> PLANTER_UPGRADED_TILE_ENTITY_TYPE =
            BlockEntityType.Builder.of(PlanterTileNeoForge.Upgraded::new, PLANTER_UPGRADED_BLOCK).build(DSL.emptyPartType());
        public static final MenuType<PlanterContainerNeoForge> PLANTER_CONTAINER_TYPE =
            IMenuTypeExtension.create((id, inv, data) -> new PlanterContainerNeoForge(id, inv.player, data.readBlockPos(), Holder.PLANTER_CONTAINER_TYPE));

        @Override
        public BlockEntityType<PlanterTileNeoForge.Normal> normalType() {
            return PLANTER_TILE_TILE_ENTITY_TYPE;
        }

        @Override
        public BlockEntityType<PlanterTileNeoForge.Upgraded> upgradedType() {
            return PLANTER_UPGRADED_TILE_ENTITY_TYPE;
        }

        @Override
        public MenuType<PlanterContainerNeoForge> planterMenuType() {
            return PLANTER_CONTAINER_TYPE;
        }

        static {
            AutoPlanterCommon.accessor = new Holder();
        }
    }
}