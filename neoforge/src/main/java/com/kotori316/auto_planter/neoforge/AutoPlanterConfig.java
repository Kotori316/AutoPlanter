package com.kotori316.auto_planter.neoforge;

import net.neoforged.neoforge.common.ModConfigSpec;

@SuppressWarnings("ClassCanBeRecord") // Should not keep the builder instance passed to constructor
public final class AutoPlanterConfig {
    static final ModConfigSpec SPEC_INSTANCE;
    public static final AutoPlanterConfig INSTANCE;

    static {
        var pair = new ModConfigSpec.Builder().configure(AutoPlanterConfig::new);
        SPEC_INSTANCE = pair.getRight();
        INSTANCE = pair.getLeft();
    }

    public final ModConfigSpec.BooleanValue allowSugarcane;

    private AutoPlanterConfig(ModConfigSpec.Builder builder) {
        this.allowSugarcane = builder
            .comment("Allow AutoPlanter to place Sugarcane in Farmland mode")
            .translation("config.auto_planter.allow_sugarcane")
            .define("allowSugarcane", true);
    }
}
