package com.kotori316.auto_planter.planter;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import com.kotori316.auto_planter.AutoPlanter;

public class PlanterGui extends AbstractContainerScreen<PlanterContainer> {
    private static final ResourceLocation DISPENSER_GUI_TEXTURES = new ResourceLocation("textures/gui/container/dispenser.png");
    private static final ResourceLocation PLANTER4_GUI_TEXTURES = new ResourceLocation(AutoPlanter.AUTO_PLANTER, "textures/gui/planter.png");

    public PlanterGui(PlanterContainer c, Inventory inv, Component t) {
        super(c, inv, t);
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.renderTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack matrices, int mouseX, int mouseY) {
        super.renderLabels(matrices, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        var texture = switch (getMenu().tile.blockType()) {
            case NORMAL -> DISPENSER_GUI_TEXTURES;
            case UPGRADED -> PLANTER4_GUI_TEXTURES;
        };
        RenderSystem.setShaderTexture(0, texture);
        this.blit(matrices, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

}
