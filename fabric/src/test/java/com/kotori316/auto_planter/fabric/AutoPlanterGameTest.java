package com.kotori316.auto_planter.fabric;

import com.google.common.base.CaseFormat;
import com.kotori316.auto_planter.planter.PlanterBlock;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class AutoPlanterGameTest implements FabricGameTest {

    @GameTestGenerator
    public static List<TestFunction> createTests() {
        Map<String, BiConsumer<GameTestHelper, PlanterBlock>> tests = Map.of(
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
                    g -> f.getValue().accept(g, b.getValue())
                ))
        ).toList();
    }

    static void placeTest(GameTestHelper helper, PlanterBlock block) {
        helper.setBlock(new BlockPos(0, 1, 0), block);
        helper.succeed();
    }

    static void placeSaplingTest1(GameTestHelper helper, PlanterBlock block) {
        var pos = new BlockPos(0, 1, 0);
        helper.setBlock(pos, Blocks.AIR);
        helper.setBlock(pos.above(), Blocks.AIR);
        var sapling = Blocks.OAK_SAPLING;
        helper.setBlock(pos, block);
        useBlock(helper, pos, helper.makeMockPlayer(GameType.CREATIVE), new ItemStack(sapling), Direction.UP);
        helper.assertBlockPresent(sapling, pos.above());
        helper.succeed();
    }

    static void placeSaplingTest2(GameTestHelper helper, PlanterBlock block) {
        var pos = new BlockPos(0, 1, 0);
        helper.setBlock(pos, Blocks.AIR);
        helper.setBlock(pos.above(), Blocks.AIR);
        var sapling = Blocks.OAK_SAPLING;
        helper.setBlock(pos, block.defaultBlockState().setValue(PlanterBlock.TRIGGERED, true));
        useBlock(helper, pos, helper.makeMockPlayer(GameType.CREATIVE), new ItemStack(sapling), Direction.UP);
        helper.assertBlockPresent(sapling, pos.above());
        helper.succeed();
    }

    static void placeSeedTest1(GameTestHelper helper, PlanterBlock block) {
        var pos = new BlockPos(0, 1, 0);
        helper.setBlock(pos, Blocks.AIR);
        helper.setBlock(pos.above(), Blocks.AIR);
        var seed = Items.WHEAT_SEEDS;
        helper.setBlock(pos, block);
        useBlock(helper, pos, helper.makeMockPlayer(GameType.CREATIVE), new ItemStack(seed), Direction.UP);
        helper.assertBlockNotPresent(Blocks.WHEAT, pos.above());
        helper.succeed();
    }

    static void placeSeedTest2(GameTestHelper helper, PlanterBlock block) {
        var pos = new BlockPos(0, 1, 0);
        helper.setBlock(pos, Blocks.AIR);
        helper.setBlock(pos.above(), Blocks.AIR);
        var seed = Items.WHEAT_SEEDS;
        helper.setBlock(pos, block.defaultBlockState().setValue(PlanterBlock.TRIGGERED, true));
        useBlock(helper, pos, helper.makeMockPlayer(GameType.CREATIVE), new ItemStack(seed), Direction.UP);
        helper.assertBlockPresent(Blocks.WHEAT, pos.above());
        helper.succeed();
    }

    /**
     * Copied from ExtendedGameTestHelper in NeoForge
     */
    private static void useBlock(GameTestHelper helper, BlockPos pos, Player player, ItemStack item, Direction direction) {
        player.setItemInHand(InteractionHand.MAIN_HAND, item);

        BlockPos blockpos = helper.absolutePos(pos);
        BlockState blockstate = helper.getLevel().getBlockState(blockpos);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(blockpos), direction, blockpos, true);
        InteractionResult interactionresult = blockstate.useWithoutItem(helper.getLevel(), player, hit);
        if (!interactionresult.consumesAction()) {
            UseOnContext useoncontext = new UseOnContext(player, InteractionHand.MAIN_HAND, hit);
            player.getItemInHand(InteractionHand.MAIN_HAND).useOn(useoncontext);
        }
    }
}
