package com.kotori316.auto_planter.fabric;

import com.mojang.datafixers.DSL;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.slf4j.Logger;

import com.kotori316.auto_planter.AutoPlanterCommon;
import com.kotori316.auto_planter.fabric.planter.PlanterBlockFabric;
import com.kotori316.auto_planter.fabric.planter.PlanterContainerFabric;
import com.kotori316.auto_planter.fabric.planter.PlanterTileFabric;
import com.kotori316.auto_planter.planter.PlanterContainer;
import com.kotori316.auto_planter.planter.PlanterGui;
import com.kotori316.auto_planter.planter.PlanterTile;

public final class AutoPlanter implements ModInitializer, ClientModInitializer {
    private static final Logger LOGGER = AutoPlanterCommon.LOGGER;

    @Override
    public void onInitialize() {
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(AutoPlanterCommon.AUTO_PLANTER, AutoPlanterCommon.BLOCK_NORMAL), Holder.PLANTER_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(AutoPlanterCommon.AUTO_PLANTER, AutoPlanterCommon.BLOCK_UPGRADED), Holder.PLANTER_UPGRADED_BLOCK);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(AutoPlanterCommon.AUTO_PLANTER, AutoPlanterCommon.BLOCK_NORMAL), Holder.PLANTER_BLOCK.blockItem);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(AutoPlanterCommon.AUTO_PLANTER, AutoPlanterCommon.BLOCK_UPGRADED), Holder.PLANTER_UPGRADED_BLOCK.blockItem);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, PlanterTileFabric.Normal.TILE_ID, Holder.PLANTER_TILE_TILE_ENTITY_TYPE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, PlanterTileFabric.Upgraded.TILE_ID, Holder.PLANTER_UPGRADED_TILE_ENTITY_TYPE);
        Registry.register(BuiltInRegistries.MENU, new ResourceLocation(PlanterContainer.GUI_ID), Holder.PLANTER_CONTAINER_TYPE);
        LOGGER.debug("Registered misc in mod Auto Planter");
        AutoPlanterCommon.accessor = new Holder();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void onInitializeClient() {
        MenuScreens.register(Holder.PLANTER_CONTAINER_TYPE, PlanterGui::new);
    }

    public static class Holder implements AutoPlanterCommon.TypeAccessor {
        public static final PlanterBlockFabric PLANTER_BLOCK = new PlanterBlockFabric.Normal();
        public static final PlanterBlockFabric PLANTER_UPGRADED_BLOCK = new PlanterBlockFabric.Upgraded();
        public static final BlockEntityType<PlanterTileFabric.Normal> PLANTER_TILE_TILE_ENTITY_TYPE =
            FabricBlockEntityTypeBuilder.create(PlanterTileFabric.Normal::new, PLANTER_BLOCK).build(DSL.emptyPartType());
        public static final BlockEntityType<PlanterTileFabric.Upgraded> PLANTER_UPGRADED_TILE_ENTITY_TYPE =
            FabricBlockEntityTypeBuilder.create(PlanterTileFabric.Upgraded::new, PLANTER_BLOCK).build(DSL.emptyPartType());
        public static final ExtendedScreenHandlerType<PlanterContainerFabric> PLANTER_CONTAINER_TYPE = new ExtendedScreenHandlerType<>(
            (i, player, buf) -> new PlanterContainerFabric(i, player.player, buf.readBlockPos(), Holder.PLANTER_CONTAINER_TYPE));

        static {
            ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                .register(entries -> {
                    entries.accept(Holder.PLANTER_BLOCK);
                    entries.accept(Holder.PLANTER_UPGRADED_BLOCK);
                });
        }

        @Override
        public BlockEntityType<? extends PlanterTile> normalType() {
            return PLANTER_TILE_TILE_ENTITY_TYPE;
        }

        @Override
        public BlockEntityType<? extends PlanterTile> upgradedType() {
            return PLANTER_UPGRADED_TILE_ENTITY_TYPE;
        }

        @Override
        public MenuType<? extends PlanterContainer<?>> planterMenuType() {
            return PLANTER_CONTAINER_TYPE;
        }
    }
}
