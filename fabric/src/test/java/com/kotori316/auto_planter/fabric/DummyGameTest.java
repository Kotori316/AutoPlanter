package com.kotori316.auto_planter.fabric;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

public final class DummyGameTest {
    @GameTest
    public void dummyTest(GameTestHelper helper) {
        helper.succeed();
    }
}
