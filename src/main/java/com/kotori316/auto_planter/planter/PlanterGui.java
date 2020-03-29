package com.kotori316.auto_planter.planter;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PlanterGui extends AbstractContainerScreen<PlanterContainer> {
    private static final Identifier DISPENSER_GUI_TEXTURES = new Identifier("textures/gui/container/dispenser.png");

    public PlanterGui(PlanterContainer c, PlayerInventory inv, Text t) {
        super(c, inv, t);
    }

    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground();
        super.render(p_render_1_, p_render_2_, p_render_3_);
        this.drawMouseoverTooltip(p_render_1_, p_render_2_);
    }

    @Override
    protected void drawForeground(int mouseX, int mouseY) {
        String s = this.title.getString();
        this.font.draw(s, (float) (this.containerWidth / 2 - this.font.getStringWidth(s) / 2), 6.0F, 4210752);
        this.font.draw(this.playerInventory.getDisplayName().getString(), 8.0F, (float) (this.containerHeight - 96 + 2), 4210752);
    }

    @Override
    protected void drawBackground(float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        assert this.minecraft != null;
        this.minecraft.getTextureManager().bindTexture(DISPENSER_GUI_TEXTURES);
        int i = (this.width - this.containerWidth) / 2;
        int j = (this.height - this.containerHeight) / 2;
        this.blit(i, j, 0, 0, this.containerWidth, this.containerHeight);
    }

}
