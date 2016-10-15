package com.kokolihapihvi.orepings.client.model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ISmartItemModel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class PingModel implements ISmartItemModel {

	private final IBakedModel baseModel;
	private IBakedModel bakedModel;

	public PingModel(IBakedModel base) {
		baseModel = base;
	}

	@Override
	public IBakedModel handleItemState(ItemStack stack) {
		if (stack.getTagCompound() == null)
			return baseModel;

		String type = stack.getTagCompound().getCompoundTag("OrePing").getString("ore");

		if (bakedModel == null)
			bakedModel = changeTexture(type);

		return bakedModel;
	}

	@Override
	public List<BakedQuad> getFaceQuads(EnumFacing facing) {
		return baseModel.getFaceQuads(facing);
	}

	@Override
	public List<BakedQuad> getGeneralQuads() {
		return baseModel.getGeneralQuads();
	}

	@Override
	public boolean isAmbientOcclusion() {
		return baseModel.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return baseModel.isGui3d();
	}

	@Override
	public boolean isBuiltInRenderer() {
		return baseModel.isBuiltInRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return baseModel.getParticleTexture();
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return baseModel.getItemCameraTransforms();
	}

	// Credits to RWTema!
	private BakedQuad copyQuad(BakedQuad quad) {
		return new BakedQuad(Arrays.copyOf(quad.getVertexData(), quad.getVertexData().length), quad.getTintIndex(), quad.getFace());
	}

	private IBakedModel changeTexture(String type) {
		TextureAtlasSprite texture = ModelBakeHandler.textures.get("orepings:singleUsePing-" + type);

		SimpleBakedModel bakedModel = new SimpleBakedModel(new LinkedList(), newBlankFacingLists(), isGui3d(), isAmbientOcclusion(), texture, getItemCameraTransforms());

		for (int i = 0; i < getGeneralQuads().size(); i++) {
			BakedQuad o = getGeneralQuads().get(i);
			// Quads 56 and 57 appear to be the sides that contain the ore texture
			// Don't need to change any other quads
			// Magic numbers, horrible coding, I know :'(
			// Should work as long as the models don't change, right?
			if (i == 56 || i == 57) {
				bakedModel.getGeneralQuads().add(changeTexture(o, texture));
			} else {
				bakedModel.getGeneralQuads().add(o);
			}
		}

		for (EnumFacing facing : EnumFacing.values()) {
			for (BakedQuad o : getFaceQuads(facing)) {
				bakedModel.getFaceQuads(facing).add(changeTexture(o, texture));
			}
		}

		IBakedModel result = bakedModel;

		if (baseModel instanceof IPerspectiveAwareModel) {
			result = new PerspectiveWrapper(result, (IPerspectiveAwareModel) baseModel);
		}

		return result;
	}

	private BakedQuad changeTexture(BakedQuad quad, TextureAtlasSprite tex) {
		if (!(quad.getFace() == EnumFacing.SOUTH || quad.getFace() == EnumFacing.NORTH))
			return quad;

		quad = copyQuad(quad);

		// 4 vertexes on each quad
		for (int i = 0; i < 4; ++i) {
			int j = 7 * i;
			// get the x,y,z coordinates
			float x = Float.intBitsToFloat(quad.getVertexData()[j]);
			float y = Float.intBitsToFloat(quad.getVertexData()[j + 1]);
			float z = Float.intBitsToFloat(quad.getVertexData()[j + 2]);
			float u = 0.0F;
			float v = 0.0F;

			// move x,y,z in boundary if they are outside
			if (x < 0 || x > 1)
				x = (x + 1) % 1;
			if (y < 0 || y > 1)
				y = (y + 1) % 1;
			if (z < 0 || z > 1)
				z = (z + 1) % 1;

			// calculate the UVs based on the x,y,z and the 'face' of the quad
			switch (quad.getFace().ordinal()) {
			case 0:
				u = x * 16.0F;
				v = (1.0F - z) * 16.0F;
				break;
			case 1:
				u = x * 16.0F;
				v = z * 16.0F;
				break;
			case 2:
				u = (1.0F - x) * 16.0F;
				v = (1.0F - y) * 16.0F;
				break;
			case 3:
				u = x * 16.0F;
				v = (1.0F - y) * 16.0F;
				break;
			case 4:
				u = z * 16.0F;
				v = (1.0F - y) * 16.0F;
				break;
			case 5:
				u = (1.0F - z) * 16.0F;
				v = (1.0F - y) * 16.0F;
			}

			// set the new texture uvs
			quad.getVertexData()[j + 4] = Float.floatToRawIntBits(tex.getInterpolatedU((double) u));
			quad.getVertexData()[j + 4 + 1] = Float.floatToRawIntBits(tex.getInterpolatedV((double) v));
		}

		return quad;
	}

	// creates blank lists
	private List newBlankFacingLists() {
		Object[] list = new Object[EnumFacing.values().length];
		for (int i = 0; i < EnumFacing.values().length; ++i) {
			list[i] = Lists.newArrayList();
		}

		return ImmutableList.copyOf(list);
	}
}
