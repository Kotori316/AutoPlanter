package com.kotori316.auto_planter;

import com.mojang.datafixers.DSL;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.screen.ScreenHandlerType;
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
        Registry.register(Registry.BLOCK, new Identifier(AUTO_PLANTER, PlanterBlock.name), Holder.PLANTER_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(AUTO_PLANTER, PlanterBlock.name), Holder.PLANTER_BLOCK.blockItem);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, PlanterTile.TILE_ID, Holder.PLANTER_TILE_TILE_ENTITY_TYPE);

    }

    @Override
    @Environment(EnvType.CLIENT)
    public void onInitializeClient() {
        ScreenRegistry.register(Holder.PLANTER_CONTAINER_TYPE, PlanterGui::new);
    }

    public static class Holder {
        public static final PlanterBlock PLANTER_BLOCK = new PlanterBlock();
        public static final BlockEntityType<PlanterTile> PLANTER_TILE_TILE_ENTITY_TYPE =
            BlockEntityType.Builder.create(PlanterTile::new, PLANTER_BLOCK).build(DSL.emptyPartType());
        public static final ScreenHandlerType<PlanterContainer> PLANTER_CONTAINER_TYPE = ScreenHandlerRegistry.registerExtended(new Identifier(PlanterContainer.GUI_ID),
            (i, player, buf) -> new PlanterContainer(i, player.player, buf.readBlockPos()));
    }
}
