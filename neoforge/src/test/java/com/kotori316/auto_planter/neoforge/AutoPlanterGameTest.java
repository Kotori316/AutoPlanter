package com.kotori316.auto_planter.neoforge;

import com.google.common.base.CaseFormat;
import com.kotori316.auto_planter.AutoPlanterCommon;
import com.kotori316.auto_planter.planter.PlanterBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;
import net.neoforged.testframework.gametest.GameTest;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public final class AutoPlanterGameTest {
    private record TestFunction(
        String name,
        String structureName,
        int maxTicks,
        int setupTicks,
        boolean required,
        Consumer<GameTestHelper> testFunction
    ) {
        ResourceLocation getTestFunctionName() {
            return ResourceLocation.fromNamespaceAndPath(AutoPlanterCommon.AUTO_PLANTER, name);
        }

        AutoPlanterGameTestInstance createTestInstance(Holder<TestEnvironmentDefinition> definition) {
            return new AutoPlanterGameTestInstance(definition, this);
        }
    }

    private static class AutoPlanterGameTestInstance extends GameTestInstance {
        private final TestFunction testFunction;

        public AutoPlanterGameTestInstance(Holder<TestEnvironmentDefinition> definition, TestFunction testFunction) {
            super(new TestData<>(definition, ResourceLocation.parse(testFunction.structureName()), testFunction.maxTicks(), testFunction.setupTicks(), testFunction.required()));
            this.testFunction = testFunction;
        }

        @Override
        public void run(GameTestHelper helper) {
            this.testFunction.testFunction.accept(helper);
        }

        @Override
        public MapCodec<? extends GameTestInstance> codec() {
            return MapCodec.unit(this);
        }

        @Override
        protected MutableComponent typeDescription() {
            return Component.empty();
        }
    }

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = AutoPlanterCommon.AUTO_PLANTER)
    public static final class Register {
        @SubscribeEvent
        public static void registerGameTest(RegisterGameTestsEvent event) {
            var environmentDefinition = event.registerEnvironment(ResourceLocation.fromNamespaceAndPath(AutoPlanterCommon.AUTO_PLANTER, "test"));
            var tests = createTests();
            for (TestFunction test : tests) {
                event.registerTest(test.getTestFunctionName(), test.createTestInstance(environmentDefinition));
            }
        }
    }

    @GameTest(template = "trail_ruins/tower/one_room_1")
    public static void dummyTest(GameTestHelper helper) {
        helper.succeed();
    }

    private static List<TestFunction> createTests() {
        Map<String, BiConsumer<ExtendedGameTestHelper, PlanterBlock>> tests = Map.of(
            "placeTest", AutoPlanterGameTest::placeTest,
            "placeSaplingTest1", AutoPlanterGameTest::placeSaplingTest1,
            "placeSaplingTest2", AutoPlanterGameTest::placeSaplingTest2,
            "placeSeedTest1", AutoPlanterGameTest::placeSeedTest1,
            "placeSeedTest2", AutoPlanterGameTest::placeSeedTest2
        );
        var blocks = Stream.of(Map.entry("Normal", AutoPlanter.Holder.PLANTER_BLOCK), Map.entry("Advanced", AutoPlanter.Holder.PLANTER_BLOCK));

        return blocks.flatMap(
            b -> tests.entrySet().stream()
                .map(f -> new TestFunction(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, f.getKey() + b.getKey()), "minecraft:trail_ruins/tower/one_room_1", 100, 0, true,
                    g -> f.getValue().accept(new ExtendedGameTestHelper(g.testInfo), b.getValue())
                ))
        ).toList();
    }

    static void placeTest(ExtendedGameTestHelper helper, PlanterBlock block) {
        helper.catchException(() -> helper.setBlock(new BlockPos(0, 1, 0), block));
        helper.succeed();
    }

    static void placeSaplingTest1(ExtendedGameTestHelper helper, PlanterBlock block) {
        var pos = new BlockPos(0, 1, 0);
        helper.setBlock(pos, Blocks.AIR);
        helper.setBlock(pos.above(), Blocks.AIR);
        var sapling = Blocks.OAK_SAPLING;
        helper.setBlock(pos, block);
        helper.useBlock(pos, helper.makeMockPlayer(GameType.CREATIVE), new ItemStack(sapling), Direction.UP);
        helper.assertBlockPresent(sapling, pos.above());
        helper.succeed();
    }

    static void placeSaplingTest2(ExtendedGameTestHelper helper, PlanterBlock block) {
        var pos = new BlockPos(0, 1, 0);
        helper.setBlock(pos, Blocks.AIR);
        helper.setBlock(pos.above(), Blocks.AIR);
        var sapling = Blocks.OAK_SAPLING;
        helper.setBlock(pos, block.defaultBlockState().setValue(PlanterBlock.TRIGGERED, true));
        helper.useBlock(pos, helper.makeMockPlayer(GameType.CREATIVE), new ItemStack(sapling), Direction.UP);
        helper.assertBlockPresent(sapling, pos.above());
        helper.succeed();
    }

    static void placeSeedTest1(ExtendedGameTestHelper helper, PlanterBlock block) {
        var pos = new BlockPos(0, 1, 0);
        helper.setBlock(pos, Blocks.AIR);
        helper.setBlock(pos.above(), Blocks.AIR);
        var seed = Items.WHEAT_SEEDS;
        helper.setBlock(pos, block);
        helper.useBlock(pos, helper.makeMockPlayer(GameType.CREATIVE), new ItemStack(seed), Direction.UP);
        helper.assertBlockNotPresent(Blocks.WHEAT, pos.above());
        helper.succeed();
    }

    static void placeSeedTest2(ExtendedGameTestHelper helper, PlanterBlock block) {
        var pos = new BlockPos(0, 1, 0);
        helper.setBlock(pos, Blocks.AIR);
        helper.setBlock(pos.above(), Blocks.AIR);
        var seed = Items.WHEAT_SEEDS;
        helper.setBlock(pos, block.defaultBlockState().setValue(PlanterBlock.TRIGGERED, true));
        helper.useBlock(pos, helper.makeMockPlayer(GameType.CREATIVE), new ItemStack(seed), Direction.UP);
        helper.assertBlockPresent(Blocks.WHEAT, pos.above());
        helper.succeed();
    }
}
