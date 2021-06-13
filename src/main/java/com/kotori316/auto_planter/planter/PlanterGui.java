package com.kotori316.auto_planter.planter;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import com.kotori316.auto_planter.AutoPlanter;

public class PlanterGui extends HandledScreen<PlanterContainer> {
    private static final Identifier DISPENSER_GUI_TEXTURES = new Identifier("textures/gui/container/dispenser.png");
    private static final Identifier PLANTER4_GUI_TEXTURES = new Identifier(AutoPlanter.AUTO_PLANTER, "textures/gui/planter.png");

    public PlanterGui(PlanterContainer c, PlayerInventory inv, Text t) {
        super(c, inv, t);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        super.drawForeground(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        var texture = switch (getScreenHandler().tile.blockType()) {
            case NORMAL -> DISPENSER_GUI_TEXTURES;
            case UPGRADED -> PLANTER4_GUI_TEXTURES;
        };
        RenderSystem.setShaderTexture(0, texture);
        this.drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

}
