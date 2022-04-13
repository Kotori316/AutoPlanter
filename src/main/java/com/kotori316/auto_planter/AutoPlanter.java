package com.kotori316.auto_planter;

import com.mojang.datafixers.DSL;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kotori316.auto_planter.planter.PlanterBlock;
import com.kotori316.auto_planter.planter.PlanterContainer;
import com.kotori316.auto_planter.planter.PlanterGui;
import com.kotori316.auto_planter.planter.PlanterTile;

public final class AutoPlanter implements ModInitializer, ClientModInitializer {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String AUTO_PLANTER = "auto_planter";

    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, new Identifier(AUTO_PLANTER, PlanterBlock.Normal.name), Holder.PLANTER_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(AUTO_PLANTER, PlanterBlock.Upgraded.name), Holder.PLANTER_UPGRADED_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(AUTO_PLANTER, PlanterBlock.Normal.name), Holder.PLANTER_BLOCK.blockItem);
        Registry.register(Registry.ITEM, new Identifier(AUTO_PLANTER, PlanterBlock.Upgraded.name), Holder.PLANTER_UPGRADED_BLOCK.blockItem);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, PlanterTile.Normal.TILE_ID, Holder.PLANTER_TILE_TILE_ENTITY_TYPE);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, PlanterTile.Upgraded.TILE_ID, Holder.PLANTER_UPGRADED_TILE_ENTITY_TYPE);
        Registry.register(Registry.SCREEN_HANDLER, new Identifier(PlanterContainer.GUI_ID), Holder.PLANTER_CONTAINER_TYPE);
        LOGGER.debug("Registered misc in mod Auto Planter");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void onInitializeClient() {
        HandledScreens.register(Holder.PLANTER_CONTAINER_TYPE, PlanterGui::new);
    }

    public static class Holder {
        public static final PlanterBlock PLANTER_BLOCK = new PlanterBlock.Normal();
        public static final PlanterBlock PLANTER_UPGRADED_BLOCK = new PlanterBlock.Upgraded();
        public static final BlockEntityType<PlanterTile.Normal> PLANTER_TILE_TILE_ENTITY_TYPE =
            FabricBlockEntityTypeBuilder.create(PlanterTile.Normal::new, PLANTER_BLOCK).build(DSL.emptyPartType());
        public static final BlockEntityType<PlanterTile.Upgraded> PLANTER_UPGRADED_TILE_ENTITY_TYPE =
            FabricBlockEntityTypeBuilder.create(PlanterTile.Upgraded::new, PLANTER_BLOCK).build(DSL.emptyPartType());
        public static final ExtendedScreenHandlerType<PlanterContainer> PLANTER_CONTAINER_TYPE = new ExtendedScreenHandlerType<>(
            (i, player, buf) -> new PlanterContainer(i, player.player, buf.readBlockPos()));
    }
}
