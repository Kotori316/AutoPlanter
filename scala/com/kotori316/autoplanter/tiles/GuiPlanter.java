package com.kotori316.autoplanter.tiles;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import com.kotori316.autoplanter.AutoPlanter;

@SideOnly(Side.CLIENT)
public class GuiPlanter extends GuiContainer {

    private final static ResourceLocation resourceLocation = new ResourceLocation(AutoPlanter.modID, "textures/gui/planter.png");
    private final TilePlanter tilePlanter;

    public GuiPlanter(TilePlanter tilePlanter, EntityPlayer player) {
        super(new ContainerPlanter(tilePlanter, player));
        this.tilePlanter = tilePlanter;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = tilePlanter.getName();
        this.fontRenderer.drawString(I18n.format(s), this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
        this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GuiPlanter.resourceLocation);
        int k = (this.width - xSize) / 2;
        int l = (this.height - ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
