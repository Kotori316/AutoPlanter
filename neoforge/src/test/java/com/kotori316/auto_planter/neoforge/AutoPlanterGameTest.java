package com.kotori316.auto_planter.neoforge;

import com.google.common.base.CaseFormat;
import com.kotori316.auto_planter.AutoPlanterCommon;
import com.kotori316.auto_planter.planter.PlanterBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

@PrefixGameTestTemplate(value = false)
public final class AutoPlanterGameTest {
    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = AutoPlanterCommon.AUTO_PLANTER)
    public static final class Register {
        @SubscribeEvent
        public static void registerGameTest(RegisterGameTestsEvent event) {
            event.register(AutoPlanterGameTest.class);
        }
    }

    @GameTest(templateNamespace = "minecraft", template = "trail_ruins/tower/one_room_1")
    public static void dummyTest(GameTestHelper helper) {
        helper.succeed();
    }

    @GameTestGenerator
    public static List<TestFunction> createTests() {
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
                .map(f -> new TestFunction("defaultBatch", CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, f.getKey() + b.getKey()), "minecraft:trail_ruins/tower/one_room_1", 100, 0, true,
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
