package com.kotori316.autoplanter;

import javax.annotation.Nullable;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import com.kotori316.autoplanter.tiles.ContainerPlanter;
import com.kotori316.autoplanter.tiles.GuiPlanter;
import com.kotori316.autoplanter.tiles.TilePlanter;

public class GuiHandler implements IGuiHandler {

    public static final int PLANTER_GUIID = 0;

    @Nullable
    @Override
    public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        switch (ID) {
            case PLANTER_GUIID:
                return new ContainerPlanter(((TilePlanter) world.getTileEntity(pos)), player);
            default:
                return null;
        }
    }

    @Nullable
    @Override
    @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
    public GuiContainer getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        switch (ID) {
            case PLANTER_GUIID:
                return new GuiPlanter(((TilePlanter) world.getTileEntity(pos)), player);
            default:
                return null;
        }
    }
}
