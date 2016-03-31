package com.kokolihapihvi.orepings.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class PingBlock {
    public final int x,y,z;
    public int lifeTime;
    public final boolean drawTop, drawBottom, drawNorth, drawSouth, drawEast, drawWest;

    public final IIcon icon;

    public PingBlock(int lifetime, int x, int y, int z, Block block, World world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.lifeTime = lifetime;

        //Y+
        drawTop = !world.getBlock(x,y+1,z).equals(block);

        //Y-
        drawBottom = !world.getBlock(x,y-1,z).equals(block);

        //Z+
        drawNorth = !world.getBlock(x,y,z+1).equals(block);

        //Z-
        drawSouth = !world.getBlock(x,y,z-1).equals(block);

        //X+
        drawEast = !world.getBlock(x+1,y,z).equals(block);

        //X-
        drawWest = !world.getBlock(x-1,y,z).equals(block);

        icon = block.getIcon(world, x, y, z, 0);
    }
}
