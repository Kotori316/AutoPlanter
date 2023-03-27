package com.kotori316.auto_planter.planter;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import com.kotori316.auto_planter.AutoPlanterCommon;


public class PlanterGui extends AbstractContainerScreen<PlanterContainer<?>> {
    private static final ResourceLocation LOCATION = new ResourceLocation("textures/gui/container/dispenser.png");
    private static final ResourceLocation PLANTER4_GUI_TEXTURES = new ResourceLocation(AutoPlanterCommon.AUTO_PLANTER, "textures/gui/planter.png");

    public PlanterGui(PlanterContainer c, Inventory inv, Component t) {
        super(c, inv, t);
    }

    @Override
    public void render(PoseStack matrixStack, final int mouseX, final int mouseY, final float partialTicks) {
        this.renderBackground(matrixStack);// background
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY); // render tooltip
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY) {
        super.renderLabels(stack, mouseX, mouseY);
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        switch (getMenu().tile.blockType()) {
            case NORMAL -> RenderSystem.setShaderTexture(0, LOCATION);
            case UPGRADED -> RenderSystem.setShaderTexture(0, PLANTER4_GUI_TEXTURES);
        }
        blit(matrixStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
}
