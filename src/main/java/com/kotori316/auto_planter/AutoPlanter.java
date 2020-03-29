package com.kotori316.auto_planter;

import com.mojang.datafixers.DSL;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.container.ContainerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kotori316.auto_planter.items.CheckPlantableItem;
import com.kotori316.auto_planter.planter.PlanterBlock;
import com.kotori316.auto_planter.planter.PlanterContainer;
import com.kotori316.auto_planter.planter.PlanterGui;
import com.kotori316.auto_planter.planter.PlanterTile;

// The value here should match an entry in the META-INF/mods.toml file
//@Mod(AutoPlanter.AUTO_PLANTER)
public final class AutoPlanter implements ModInitializer, ClientModInitializer {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String AUTO_PLANTER = "auto_planter";

    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, new Identifier(AUTO_PLANTER, PlanterBlock.name), Holder.PLANTER_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(AUTO_PLANTER, PlanterBlock.name), Holder.PLANTER_BLOCK.blockItem);
        Registry.register(Registry.ITEM, new Identifier(AUTO_PLANTER, "check_plantable"), Holder.CHECK_PLANTABLE_ITEM);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, PlanterTile.TILE_ID, Holder.PLANTER_TILE_TILE_ENTITY_TYPE);
//        Registry.register(Registry.CONTAINER, PlanterContainer.GUI_ID, Holder.PLANTER_CONTAINER_TYPE);

        ContainerProviderRegistry.INSTANCE.registerFactory(new Identifier(PlanterContainer.GUI_ID), (syncId, identifier, player, buf) ->
            new PlanterContainer(syncId, player, buf.readBlockPos(), Holder.PLANTER_CONTAINER_TYPE));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void onInitializeClient() {
        ScreenProviderRegistry.INSTANCE.<PlanterContainer>registerFactory(new Identifier(PlanterContainer.GUI_ID), c ->
            new PlanterGui(c, c.player.inventory, Holder.PLANTER_BLOCK.getName()));
    }

    public static class Holder {
        public static final CheckPlantableItem CHECK_PLANTABLE_ITEM = new CheckPlantableItem();
        public static final PlanterBlock PLANTER_BLOCK = new PlanterBlock();
        public static final BlockEntityType<PlanterTile> PLANTER_TILE_TILE_ENTITY_TYPE =
            BlockEntityType.Builder.create(PlanterTile::new, PLANTER_BLOCK).build(DSL.nilType());
        public static final ContainerType<PlanterContainer> PLANTER_CONTAINER_TYPE = null;
    }
}
