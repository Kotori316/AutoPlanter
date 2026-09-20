package com.kotori316.auto_planter.neoforge;

import com.google.common.base.CaseFormat;
import com.kotori316.auto_planter.AutoPlanterCommon;
import com.kotori316.auto_planter.planter.PlanterBlock;
import com.kotori316.auto_planter.planter.PlanterTile;
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
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
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
        Identifier getTestFunctionName() {
            return Identifier.fromNamespaceAndPath(AutoPlanterCommon.AUTO_PLANTER, name);
        }

        AutoPlanterGameTestInstance createTestInstance(Holder<TestEnvironmentDefinition<?>> definition) {
            return new AutoPlanterGameTestInstance(definition, this);
        }
    }

    private static class AutoPlanterGameTestInstance extends GameTestInstance {
        private final TestFunction testFunction;

        public AutoPlanterGameTestInstance(Holder<TestEnvironmentDefinition<?>> definition, TestFunction testFunction) {
            super(new TestData<>(definition, Identifier.parse(testFunction.structureName()), testFunction.maxTicks(), testFunction.setupTicks(), testFunction.required()));
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

    @EventBusSubscriber(modid = AutoPlanterCommon.AUTO_PLANTER)
    public static final class Register {
        @SubscribeEvent
        public static void registerGameTest(RegisterGameTestsEvent event) {
            var environmentDefinition = event.registerEnvironment(Identifier.fromNamespaceAndPath(AutoPlanterCommon.AUTO_PLANTER, "test"));
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
            "placeSeedTest2", AutoPlanterGameTest::placeSeedTest2,
            "canPlaceSapling", AutoPlanterGameTest::canPlaceSapling,
            "placeSaplingItemTest", AutoPlanterGameTest::placeSaplingItemTest,
            "hopperInsertTest", AutoPlanterGameTest::hopperInsertTest,
            "hoeTriggersTest", AutoPlanterGameTest::hoeTriggersTest
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

    static void canPlaceSapling(ExtendedGameTestHelper helper, PlanterBlock planterBlock) {
        var state = planterBlock.canSustainPlant(
            planterBlock.defaultBlockState(), EmptyBlockGetter.INSTANCE, BlockPos.ZERO, Direction.UP, Blocks.OAK_SAPLING.defaultBlockState()
        );
        helper.assertTrue(state.isTrue(), "Must canPlaceSapling be true");
        helper.succeed();
    }

    static void hopperInsertTest(ExtendedGameTestHelper helper, PlanterBlock block) {
        var planterPos = new BlockPos(0, 1, 0);
        var hopperPos = planterPos.above();

        helper.setBlock(planterPos, Blocks.AIR);
        helper.setBlock(hopperPos, Blocks.AIR);
        helper.setBlock(hopperPos.above(), Blocks.AIR);

        helper.setBlock(planterPos, block);
        // FACING=DOWN by default — hopper ejects into the block below
        helper.setBlock(hopperPos, Blocks.HOPPER.defaultBlockState());

        var hopperTile = helper.getBlockEntity(hopperPos, HopperBlockEntity.class);
        hopperTile.setItem(0, new ItemStack(Items.OAK_SAPLING));

        helper.succeedWhen(() -> {
            var planterTile = helper.getBlockEntity(planterPos, PlanterTile.class);
            helper.assertTrue(
                planterTile.getContainer().countItem(Blocks.OAK_SAPLING.asItem()) > 0,
                "Hopper must insert sapling into planter"
            );
        });
    }

    static void hoeTriggersTest(ExtendedGameTestHelper helper, PlanterBlock block) {
        var pos = new BlockPos(0, 1, 0);
        helper.setBlock(pos, block.defaultBlockState().setValue(PlanterBlock.TRIGGERED, false));
        useBlock(helper, pos, helper.makeMockPlayer(GameType.CREATIVE), new ItemStack(Items.WOODEN_HOE), Direction.UP);
        helper.assertBlockProperty(pos, PlanterBlock.TRIGGERED, true);
        helper.succeed();
    }

    /**
     * Mirrors the real ServerPlayerGameMode.useItemOn order: blockState.useItemOn first,
     * then useWithoutItem on TryEmptyHandInteraction, then itemStack.useOn.
     * ExtendedGameTestHelper#useBlock skips useItemOn entirely (useWithoutItem → item.useOn),
     * so it cannot reach PlanterBlockNeoForge#useItemOn where hoe detection lives.
     */
    private static void useBlock(GameTestHelper helper, BlockPos pos, Player player, ItemStack item, Direction direction) {
        player.setItemInHand(InteractionHand.MAIN_HAND, item);
        BlockPos blockpos = helper.absolutePos(pos);
        BlockState blockstate = helper.getLevel().getBlockState(blockpos);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(blockpos), direction, blockpos, true);
        InteractionResult blockResult = blockstate.useItemOn(item, helper.getLevel(), player, InteractionHand.MAIN_HAND, hit);
        if (blockResult == InteractionResult.TRY_WITH_EMPTY_HAND) {
            InteractionResult useResult = blockstate.useWithoutItem(helper.getLevel(), player, hit);
            if (!useResult.consumesAction()) {
                player.getItemInHand(InteractionHand.MAIN_HAND).useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit));
            }
        } else if (!blockResult.consumesAction()) {
            player.getItemInHand(InteractionHand.MAIN_HAND).useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit));
        }
    }

    static void placeSaplingItemTest(ExtendedGameTestHelper helper, PlanterBlock block) {
        var pos = new BlockPos(0, 1, 0);
        helper.setBlock(pos, Blocks.AIR);
        helper.setBlock(pos.above(), Blocks.AIR);
        var sapling = Blocks.OAK_SAPLING;
        helper.setBlock(pos, block.defaultBlockState().setValue(PlanterBlock.TRIGGERED, false));
        var tile = helper.getBlockEntity(pos, PlanterTile.class);
        tile.getContainer().setItem(0, new ItemStack(sapling));
        tile.plantSapling();

        helper.assertBlockPresent(sapling, pos.above());
        helper.assertBlockState(pos, tile.getBlockState());
        helper.assertTrue(tile.getContainer().countItem(sapling.asItem()) == 0, "Must be empty");
        helper.succeed();
    }
}
