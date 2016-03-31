package com.kokolihapihvi.orepings.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Iterator;

@SideOnly(Side.CLIENT)
public class PingRenderer {

    private static ArrayList<PingBlock> blocks = new ArrayList<PingBlock>();

    public static void AddPing(PingBlock pb) {
        synchronized (blocks) {
            blocks.add(pb);
        }
    }

    @SubscribeEvent
    public void tickLifetimes(TickEvent.PlayerTickEvent event) {
        //Only run at the end of a tick
        if(event.phase.equals(TickEvent.Phase.START)) return;

        World w = Minecraft.getMinecraft().theWorld;

        //If the world exists
        if(w == null) return;

        //If ticking client player
        if(!event.player.equals(Minecraft.getMinecraft().thePlayer)) return;

        synchronized (blocks) {
            Iterator<PingBlock> it = blocks.iterator();
            while (it.hasNext()) {
                PingBlock pb = it.next();

                if (--pb.lifeTime <= 0) {
                    it.remove();
                    continue;
                }

                if(w != null) {
                    if (w.getBlock(pb.x, pb.y, pb.z).equals(Blocks.air)) {
                        it.remove();
                        continue;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void drawThings(RenderWorldLastEvent event) {
        if(blocks.size() == 0) return;

        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;

        GL11.glPushMatrix();

        //Translate into world coordinates
        GL11.glTranslated(-player.posX + 0.5, -player.posY + 0.5, -player.posZ + 0.5);

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR);

        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);

        for (int i = 0; i < blocks.size(); i++) {
            //In case the array size changed during this loop
            if(i > blocks.size()) break;

            PingBlock pb = blocks.get(i);

            if(pb != null) {
                drawCube(pb);
            }
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);

        GL11.glPopMatrix();
    }

    private void drawCube(PingBlock pb) {
        int x = pb.x;
        int y = pb.y;
        int z = pb.z;

        Tessellator tess = Tessellator.instance;

        float minu = pb.icon.getMinU();
        float maxu = pb.icon.getMaxU();

        float minv = pb.icon.getMinV();
        float maxv = pb.icon.getMaxV();

        double size = 1;
        size *= 0.5;

        tess.startDrawingQuads();

        //X faces
        if(pb.drawWest) {
            tess.addVertexWithUV(x - size, y - size, z - size, minu, maxv);
            tess.addVertexWithUV(x - size, y - size, z + size, maxu, maxv);
            tess.addVertexWithUV(x - size, y + size, z + size, maxu, minv);
            tess.addVertexWithUV(x - size, y + size, z - size, minu, minv);
        }

        if(pb.drawEast) {
            tess.addVertexWithUV(x + size, y + size, z - size, maxu, minv);
            tess.addVertexWithUV(x + size, y + size, z + size, minu, minv);
            tess.addVertexWithUV(x + size, y - size, z + size, minu, maxv);
            tess.addVertexWithUV(x + size, y - size, z - size, maxu, maxv);
        }

        //Z faces
        if(pb.drawNorth) {
            tess.addVertexWithUV(x - size, y - size, z + size, minu, maxv);
            tess.addVertexWithUV(x + size, y - size, z + size, maxu, maxv);
            tess.addVertexWithUV(x + size, y + size, z + size, maxu, minv);
            tess.addVertexWithUV(x - size, y + size, z + size, minu, minv);
        }

        if(pb.drawSouth) {
            tess.addVertexWithUV(x - size, y + size, z - size, maxu, minv);
            tess.addVertexWithUV(x + size, y + size, z - size, minu, minv);
            tess.addVertexWithUV(x + size, y - size, z - size, minu, maxv);
            tess.addVertexWithUV(x - size, y - size, z - size, maxu, maxv);
        }

        if(pb.drawBottom) {
            tess.addVertexWithUV(x - size, y - size, z - size, minu, minv);
            tess.addVertexWithUV(x + size, y - size, z - size, maxu, minv);
            tess.addVertexWithUV(x + size, y - size, z + size, maxu, maxv);
            tess.addVertexWithUV(x - size, y - size, z + size, minu, maxv);
        }

        if(pb.drawTop) {
            tess.addVertexWithUV(x - size, y + size, z + size, minu, maxv);
            tess.addVertexWithUV(x + size, y + size, z + size, maxu, maxv);
            tess.addVertexWithUV(x + size, y + size, z - size, maxu, minv);
            tess.addVertexWithUV(x - size, y + size, z - size, minu, minv);
        }

        tess.draw();
    }
}
