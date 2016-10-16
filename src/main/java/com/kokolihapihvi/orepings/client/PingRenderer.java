package com.kokolihapihvi.orepings.client;

import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

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
                    if (w.getBlockState(pb.pos).getBlock().equals(Blocks.AIR)) {
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

        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

        GL11.glPushMatrix();

        //Translate into world coordinates
        GL11.glTranslated(-player.posX + 0.5, -player.posY + 0.5, -player.posZ + 0.5);

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR);

        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

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

        VertexBuffer rend = Tessellator.getInstance().getBuffer();

        float minu = pb.tas.getMinU();
        float maxu = pb.tas.getMaxU();

        float minv = pb.tas.getMinV();
        float maxv = pb.tas.getMaxV();

        double size = 1;
        size *= 0.5;

        rend.begin(GL11.GL_QUADS, POSITION_TEX);

        //X faces
        if(pb.drawWest) {
            rend.pos(x - size, y - size, z - size).tex(minu, maxv).endVertex();
            rend.pos(x - size, y - size, z + size).tex(maxu, maxv).endVertex();
            rend.pos(x - size, y + size, z + size).tex(maxu, minv).endVertex();
            rend.pos(x - size, y + size, z - size).tex(minu, minv).endVertex();
        }

        if(pb.drawEast) {
            rend.pos(x + size, y + size, z - size).tex(maxu, minv).endVertex();
            rend.pos(x + size, y + size, z + size).tex(minu, minv).endVertex();
            rend.pos(x + size, y - size, z + size).tex(minu, maxv).endVertex();
            rend.pos(x + size, y - size, z - size).tex(maxu, maxv).endVertex();
        }

        //Z faces
        if(pb.drawNorth) {
            rend.pos(x - size, y - size, z + size).tex(minu, maxv).endVertex();
            rend.pos(x + size, y - size, z + size).tex(maxu, maxv).endVertex();
            rend.pos(x + size, y + size, z + size).tex(maxu, minv).endVertex();
            rend.pos(x - size, y + size, z + size).tex(minu, minv).endVertex();
        }

        if(pb.drawSouth) {
            rend.pos(x - size, y + size, z - size).tex(maxu, minv).endVertex();
            rend.pos(x + size, y + size, z - size).tex(minu, minv).endVertex();
            rend.pos(x + size, y - size, z - size).tex(minu, maxv).endVertex();
            rend.pos(x - size, y - size, z - size).tex(maxu, maxv).endVertex();
        }

        if(pb.drawBottom) {
            rend.pos(x - size, y - size, z - size).tex(minu, minv).endVertex();
            rend.pos(x + size, y - size, z - size).tex(maxu, minv).endVertex();
            rend.pos(x + size, y - size, z + size).tex(maxu, maxv).endVertex();
            rend.pos(x - size, y - size, z + size).tex(minu, maxv).endVertex();
        }

        if(pb.drawTop) {
            rend.pos(x - size, y + size, z + size).tex(minu, maxv).endVertex();
            rend.pos(x + size, y + size, z + size).tex(maxu, maxv).endVertex();
            rend.pos(x + size, y + size, z - size).tex(maxu, minv).endVertex();
            rend.pos(x - size, y + size, z - size).tex(minu, minv).endVertex();
        }

        Tessellator.getInstance().draw();
    }
}
