package com.kotori316.auto_planter.planter;

import com.kotori316.auto_planter.AutoPlanterCommon;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PlanterGui extends AbstractContainerScreen<PlanterContainer<?>> {
    private static final ResourceLocation LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/dispenser.png");
    private static final ResourceLocation PLANTER4_GUI_TEXTURES = ResourceLocation.fromNamespaceAndPath(AutoPlanterCommon.AUTO_PLANTER, "textures/gui/planter.png");

    public PlanterGui(PlanterContainer c, Inventory inv, Component t) {
        super(c, inv, t);
    }

    @Override
    public void render(GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTicks) {
        this.renderBackground(graphics, mouseX, mouseY, partialTicks);// background
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY); // render tooltip
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        var texture = switch (getMenu().tile.blockType()) {
            case NORMAL -> LOCATION;
            case UPGRADED -> PLANTER4_GUI_TEXTURES;
        };
        graphics.blit(texture, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
}
