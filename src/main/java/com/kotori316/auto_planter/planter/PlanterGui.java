package com.kotori316.auto_planter.planter;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import com.kotori316.auto_planter.AutoPlanter;

public class PlanterGui extends ContainerScreen<PlanterContainer> {
    private static final ResourceLocation LOCATION = new ResourceLocation("textures/gui/container/dispenser.png");
    private static final ResourceLocation PLANTER4_GUI_TEXTURES = new ResourceLocation(AutoPlanter.AUTO_PLANTER, "textures/gui/planter.png");

    public PlanterGui(PlanterContainer c, PlayerInventory inv, ITextComponent t) {
        super(c, inv, t);
    }

    @Override
    public void render(MatrixStack matrixStack, final int mouseX, final int mouseY, final float partialTicks) {
        this.renderBackground(matrixStack);// back ground
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY); // render tooltip
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack stack, int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(stack, mouseX, mouseY);
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    @Override
    @SuppressWarnings("deprecation")
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (getContainer().tile.blockType() == PlanterBlock.PlanterBlockType.NORMAL)
            this.getMinecraft().getTextureManager().bindTexture(LOCATION);
        else
            this.getMinecraft().getTextureManager().bindTexture(PLANTER4_GUI_TEXTURES);
        this.blit(matrixStack, guiLeft, guiTop, 0, 0, xSize, ySize);
    }
}
