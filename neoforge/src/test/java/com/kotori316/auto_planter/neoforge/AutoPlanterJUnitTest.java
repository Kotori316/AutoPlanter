package com.kotori316.auto_planter.neoforge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.util.TriState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class AutoPlanterJUnitTest {
    @Test
    void dummy() {
        assertTrue(true);
    }

    @Test
    void canPlaceSapling() {
        var planterBlock = AutoPlanter.Holder.PLANTER_BLOCK;
        var state = planterBlock.canSustainPlant(
            planterBlock.defaultBlockState(), EmptyBlockGetter.INSTANCE, BlockPos.ZERO, Direction.UP, Blocks.OAK_SAPLING.defaultBlockState()
        );
        assertEquals(TriState.TRUE, state);
    }
}
