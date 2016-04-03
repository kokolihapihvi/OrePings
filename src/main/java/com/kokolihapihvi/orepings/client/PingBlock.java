package com.kokolihapihvi.orepings.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PingBlock {
    public final int x,y,z;
    public final boolean drawTop, drawBottom, drawNorth, drawSouth, drawEast, drawWest;
    public final BlockPos pos;
    public int lifeTime;
    public String oreDictName;
    public TextureAtlasSprite tas;

    public PingBlock(int lifetime, int x, int y, int z, Block block, World world, String oreName) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pos = new BlockPos(x,y,z);
        this.lifeTime = lifetime;
        oreDictName = oreName;

        tas = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelFromBlockState(world.getBlockState(pos), world, pos).getParticleTexture();

        //Y+
        drawTop = !world.getBlockState(new BlockPos(x,y+1,z)).getBlock().equals(block);

        //Y-
        drawBottom = !world.getBlockState(new BlockPos(x,y-1,z)).getBlock().equals(block);

        //Z+
        drawNorth = !world.getBlockState(new BlockPos(x,y,z+1)).getBlock().equals(block);

        //Z-
        drawSouth = !world.getBlockState(new BlockPos(x,y,z-1)).getBlock().equals(block);

        //X+
        drawEast = !world.getBlockState(new BlockPos(x+1,y,z)).getBlock().equals(block);

        //X-
        drawWest = !world.getBlockState(new BlockPos(x-1,y,z)).getBlock().equals(block);
    }
}
