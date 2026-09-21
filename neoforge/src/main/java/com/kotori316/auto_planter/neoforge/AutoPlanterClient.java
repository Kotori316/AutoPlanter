package com.kotori316.auto_planter.neoforge;

import com.kotori316.auto_planter.AutoPlanterCommon;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = AutoPlanterCommon.AUTO_PLANTER, dist = Dist.CLIENT)
public final class AutoPlanterClient {
    public AutoPlanterClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
