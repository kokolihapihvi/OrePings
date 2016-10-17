package com.kokolihapihvi.orepings.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
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
		// Only run at the end of a tick
		if (event.phase.equals(TickEvent.Phase.START))
			return;

		World w = Minecraft.getMinecraft().theWorld;

		// If the world exists
		if (w == null)
			return;

		// If ticking client player
		if (!event.player.equals(Minecraft.getMinecraft().thePlayer))
			return;

		synchronized (blocks) {
			Iterator<PingBlock> it = blocks.iterator();
			while (it.hasNext()) {
				PingBlock pb = it.next();

				if (--pb.lifeTime <= 0) {
					it.remove();
					continue;
				}

				if (w != null) {
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
		if (blocks.size() == 0)
			return;

		float partialTick = event.getPartialTicks();

		EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;

		GlStateManager.pushMatrix();

		// Translate into world coordinates
		GlStateManager.translate(-(p.lastTickPosX + (p.posX - p.lastTickPosX) * partialTick), -(p.lastTickPosY + (p.posY - p.lastTickPosY) * partialTick), -(p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * partialTick));

		// Fix offset
		// GlStateManager.translate(0.5, 0.5, 0.5);

		GlStateManager.disableDepth();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_COLOR, DestFactor.ONE_MINUS_SRC_COLOR);

		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		VertexBuffer rend = Tessellator.getInstance().getBuffer();

		for (int i = 0; i < blocks.size(); i++) {
			// In case the array size changed during this loop
			if (i > blocks.size())
				break;

			PingBlock pb = blocks.get(i);

			if (pb != null) {
				// Begin drawing quads
				rend.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

				int x = pb.x;
				int y = pb.y;
				int z = pb.z;

				GlStateManager.translate(x, y, z);

				// Draw each block
				drawCube(rend, pb);

				// Finish drawing
				Tessellator.getInstance().draw();

				GlStateManager.translate(-x, -y, -z);
			}
		}

		GlStateManager.enableDepth();
		GlStateManager.disableBlend();

		GlStateManager.popMatrix();
	}

	private void drawCube(VertexBuffer rend, PingBlock pb) {
		if (pb.drawBottom)
			drawFaces(rend, pb, EnumFacing.DOWN);
		
		if (pb.drawTop)
			drawFaces(rend, pb, EnumFacing.UP);
		
		if (pb.drawNorth)
			drawFaces(rend, pb, EnumFacing.SOUTH);
		
		if (pb.drawSouth)
			drawFaces(rend, pb, EnumFacing.NORTH);
		
		if (pb.drawEast)
			drawFaces(rend, pb, EnumFacing.EAST);
		
		if (pb.drawWest)
			drawFaces(rend, pb, EnumFacing.WEST);
	}

	private void drawFaces(VertexBuffer rend, PingBlock pb, EnumFacing facing) {
		List<BakedQuad> quads = pb.model.getQuads(pb.bs, facing, 0);

		for (BakedQuad quad : quads) {
			rend.addVertexData(quad.getVertexData());
		}
	}
}
