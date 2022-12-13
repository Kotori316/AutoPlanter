package com.kotori316.auto_planter;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kotori316.auto_planter.planter.PlanterContainer;
import com.kotori316.auto_planter.planter.PlanterTile;

public final class AutoPlanterCommon {
    public static final String AUTO_PLANTER = "auto_planter";
    public static final String BLOCK_NORMAL = "planter";
    public static final String BLOCK_UPGRADED = "planter_upgraded";

    public static final Logger LOGGER = LoggerFactory.getLogger(AUTO_PLANTER);

    public static TypeAccessor accessor;

    public interface TypeAccessor {
        BlockEntityType<? extends PlanterTile> normalType();

        BlockEntityType<? extends PlanterTile> upgradedType();

        MenuType<? extends PlanterContainer<?>> planterMenuType();
    }
}
