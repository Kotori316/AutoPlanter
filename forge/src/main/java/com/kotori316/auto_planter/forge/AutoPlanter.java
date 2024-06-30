package com.kotori316.auto_planter.forge;

import com.kotori316.auto_planter.AutoPlanterCommon;
import com.kotori316.auto_planter.forge.planter.PlanterBlockForge;
import com.kotori316.auto_planter.forge.planter.PlanterContainerForge;
import com.kotori316.auto_planter.forge.planter.PlanterTileForge;
import com.kotori316.auto_planter.planter.PlanterGui;
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
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegisterEvent;
import org.slf4j.Logger;

@Mod(AutoPlanterCommon.AUTO_PLANTER)
public final class AutoPlanter {
    public static final Logger LOGGER = AutoPlanterCommon.LOGGER;

    public AutoPlanter() {
        LOGGER.info("{} initialization", AutoPlanterCommon.AUTO_PLANTER);
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = AutoPlanterCommon.AUTO_PLANTER)
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
            MinecraftForge.EVENT_BUS.register(Holder.PLANTER_UPGRADED_BLOCK);
        }

        public static void onItemsRegistry(final RegisterEvent.RegisterHelper<Item> helper) {
            helper.register(ResourceLocation.fromNamespaceAndPath(AutoPlanterCommon.AUTO_PLANTER, AutoPlanterCommon.BLOCK_NORMAL), Holder.PLANTER_BLOCK.blockItem);
            helper.register(ResourceLocation.fromNamespaceAndPath(AutoPlanterCommon.AUTO_PLANTER, AutoPlanterCommon.BLOCK_UPGRADED), Holder.PLANTER_UPGRADED_BLOCK.blockItem);
        }

        public static void tiles(RegisterEvent.RegisterHelper<BlockEntityType<?>> helper) {
            helper.register(ResourceLocation.parse(PlanterTileForge.Normal.TILE_ID), Holder.PLANTER_TILE_TILE_ENTITY_TYPE);
            helper.register(ResourceLocation.parse(PlanterTileForge.Upgraded.TILE_ID), Holder.PLANTER_UPGRADED_TILE_ENTITY_TYPE);
        }

        public static void containers(RegisterEvent.RegisterHelper<MenuType<?>> helper) {
            helper.register(ResourceLocation.parse(PlanterContainerForge.GUI_ID), Holder.PLANTER_CONTAINER_TYPE);
        }

        @SubscribeEvent
        public static void clientInit(FMLClientSetupEvent event) {
            MenuScreens.register(Holder.PLANTER_CONTAINER_TYPE, PlanterGui::new);
        }

        @SubscribeEvent
        public static void creativeTab(BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey().equals(CreativeModeTabs.FUNCTIONAL_BLOCKS)) {
                event.accept(Holder.PLANTER_BLOCK);
                event.accept(Holder.PLANTER_UPGRADED_BLOCK);
            }
        }
    }

    public static class Holder implements AutoPlanterCommon.TypeAccessor {
        public static final PlanterBlockForge PLANTER_BLOCK = new PlanterBlockForge.Normal();
        public static final PlanterBlockForge PLANTER_UPGRADED_BLOCK = new PlanterBlockForge.Upgraded();
        public static final BlockEntityType<PlanterTileForge.Normal> PLANTER_TILE_TILE_ENTITY_TYPE =
                BlockEntityType.Builder.of(PlanterTileForge.Normal::new, PLANTER_BLOCK).build(DSL.emptyPartType());
        public static final BlockEntityType<PlanterTileForge.Upgraded> PLANTER_UPGRADED_TILE_ENTITY_TYPE =
                BlockEntityType.Builder.of(PlanterTileForge.Upgraded::new, PLANTER_UPGRADED_BLOCK).build(DSL.emptyPartType());
        public static final MenuType<PlanterContainerForge> PLANTER_CONTAINER_TYPE =
                IForgeMenuType.create((id, inv, data) -> new PlanterContainerForge(id, inv.player, data.readBlockPos(), Holder.PLANTER_CONTAINER_TYPE));

        @Override
        public BlockEntityType<PlanterTileForge.Normal> normalType() {
            return PLANTER_TILE_TILE_ENTITY_TYPE;
        }

        @Override
        public BlockEntityType<PlanterTileForge.Upgraded> upgradedType() {
            return PLANTER_UPGRADED_TILE_ENTITY_TYPE;
        }

        @Override
        public MenuType<PlanterContainerForge> planterMenuType() {
            return PLANTER_CONTAINER_TYPE;
        }

        static {
            AutoPlanterCommon.accessor = new Holder();
        }
    }
}