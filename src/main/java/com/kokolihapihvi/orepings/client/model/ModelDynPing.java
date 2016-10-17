package com.kokolihapihvi.orepings.client.model;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.vecmath.Matrix4f;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelCustomData;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.ItemTextureQuadConverter;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.kokolihapihvi.orepings.OrePingsMod;

public class ModelDynPing implements IModel, IModelCustomData, IRetexturableModel {
	public static final ModelResourceLocation LOCATION = new ModelResourceLocation(new ResourceLocation("ore_pings", "dynping"), "inventory");

	// minimal Z offset to prevent depth-fighting
	private static final float NORTH_Z_BASE = 7.496f / 16f;
	private static final float SOUTH_Z_BASE = 8.504f / 16f;
	private static final float NORTH_Z_FLUID = 7.498f / 16f;
	private static final float SOUTH_Z_FLUID = 8.502f / 16f;

	public static final IModel MODEL = new ModelDynPing();

	protected final ResourceLocation baseLocation;
	protected final ResourceLocation oreMaskLocation;
	protected final ResourceLocation coverLocation;

	protected final Block ore;

	public ModelDynPing() {
		this(null, null, null, null);
	}

	public ModelDynPing(ResourceLocation baseLocation, ResourceLocation liquidLocation, ResourceLocation coverLocation, Block ore) {
		this.baseLocation = baseLocation;
		this.oreMaskLocation = liquidLocation;
		this.coverLocation = coverLocation;
		this.ore = ore;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return ImmutableList.of();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();
		if (baseLocation != null)
			builder.add(baseLocation);
		if (oreMaskLocation != null)
			builder.add(oreMaskLocation);
		if (coverLocation != null)
			builder.add(coverLocation);

		return builder.build();
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {

		ImmutableMap<TransformType, TRSRTransformation> transformMap = IPerspectiveAwareModel.MapWrapper.getTransforms(state);

		TRSRTransformation transform = state.apply(Optional.<IModelPart> absent()).or(TRSRTransformation.identity());
		TextureAtlasSprite oreSprite = null;
		ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

		if (ore != null) {
			oreSprite = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(ore.getDefaultState()).getParticleTexture();
		}

		if (baseLocation != null) {
			// build base (insidest)
			IBakedModel model = (new ItemLayerModel(ImmutableList.of(baseLocation))).bake(state, format, bakedTextureGetter);
			builder.addAll(model.getQuads(null, null, 0));
		}
		if (oreMaskLocation != null && oreSprite != null) {
            TextureAtlasSprite oreMaskSprite = bakedTextureGetter.apply(oreMaskLocation);
            // build ore layer (inside)
            builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, oreMaskSprite, oreSprite, NORTH_Z_FLUID, EnumFacing.NORTH, 0xffffffff));
            builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, oreMaskSprite, oreSprite, SOUTH_Z_FLUID, EnumFacing.SOUTH, 0xffffffff));
		}
		if (coverLocation != null) {
			// cover (the actual item around the other two)
			TextureAtlasSprite base = bakedTextureGetter.apply(coverLocation);
			builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16, 16, NORTH_Z_BASE, base, EnumFacing.NORTH, 0xffffffff));
			builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16, 16, SOUTH_Z_BASE, base, EnumFacing.SOUTH, 0xffffffff));
		}

		return new BakedDynPing(this, builder.build(), oreSprite, format, Maps.immutableEnumMap(transformMap), Maps.<String, IBakedModel> newHashMap());
	}

	@Override
	public IModelState getDefaultState() {
		return TRSRTransformation.identity();
	}

	/**
	 * Sets the liquid in the model. fluid - Name of the fluid in the
	 * FluidRegistry flipGas - If "true" the model will be flipped upside down
	 * if the liquid is a gas. If "false" it wont
	 * <p/>
	 * If the fluid can't be found, water is used
	 */
	@Override
	public ModelDynPing process(ImmutableMap<String, String> customData) {
		String oreName = customData.get("ore");
		
		List<ItemStack> ores = OreDictionary.getOres(oreName);
		Block ore = null;
		
		if(ores.size() > 0)
			ore = ((ItemBlock) ores.get(0).getItem()).block;

		if (ore == null)
			ore = this.ore;

		// create new model with correct liquid
		return new ModelDynPing(baseLocation, oreMaskLocation, coverLocation, ore);
	}

	/**
	 * Allows to use different textures for the model. There are 3 layers: base
	 * - The empty bucket/container fluid - A texture representing the liquid
	 * portion. Non-transparent = liquid cover - An overlay that's put over the
	 * liquid (optional)
	 * <p/>
	 * If no liquid is given a hardcoded variant for the bucket is used.
	 */
	@Override
	public ModelDynPing retexture(ImmutableMap<String, String> textures) {

		ResourceLocation base = baseLocation;
		ResourceLocation oreMask = oreMaskLocation;
		ResourceLocation cover = coverLocation;

		if (textures.containsKey("base"))
			base = new ResourceLocation(textures.get("base"));
		if (textures.containsKey("oreMask"))
			oreMask = new ResourceLocation(textures.get("oreMask"));
		if (textures.containsKey("cover"))
			cover = new ResourceLocation(textures.get("cover"));

		return new ModelDynPing(base, oreMask, cover, ore);
	}

	public enum LoaderDynPing implements ICustomModelLoader {
		instance;

		@Override
		public boolean accepts(ResourceLocation modelLocation) {
			return modelLocation.getResourceDomain().equals("ore_pings") && modelLocation.getResourcePath().contains("dyn_ore_ping");
		}

		@Override
		public IModel loadModel(ResourceLocation modelLocation) throws IOException {
			return MODEL;
		}

		@Override
		public void onResourceManagerReload(IResourceManager resourceManager) {
			// no need to clear cache since we create a new model instance
		}
	}

	private static final class BakedDynPingOverrideHandler extends ItemOverrideList {

		public static final BakedDynPingOverrideHandler INSTANCE = new BakedDynPingOverrideHandler();

		private BakedDynPingOverrideHandler() {
			super(ImmutableList.<ItemOverride> of());
		}

		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
			BakedDynPing model = (BakedDynPing) originalModel;

			String type = "UNKNOWN";

			if (stack.getTagCompound() != null) {
				if (stack.getTagCompound().getCompoundTag("OrePing") != null) {
					type = stack.getTagCompound().getCompoundTag("OrePing").getString("ore");
				}
			}

			String name = type;

			if (!model.cache.containsKey(name)) {
				IModel parent = model.parent.process(ImmutableMap.of("ore", name));
				Function<ResourceLocation, TextureAtlasSprite> textureGetter;
				textureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
					public TextureAtlasSprite apply(ResourceLocation location) {
						return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
					}
				};

				IBakedModel bakedModel = parent.bake(new SimpleModelState(model.transforms), model.format, textureGetter);
				model.cache.put(name, bakedModel);
				return bakedModel;
			}

			return model.cache.get(name);
		}

	}

	// the dynamic bucket is based on the empty bucket
	private static final class BakedDynPing implements IPerspectiveAwareModel {

		private final ModelDynPing parent;
		private final Map<String, IBakedModel> cache; // contains all the baked
														// models since they'll
														// never change
		private final ImmutableMap<TransformType, TRSRTransformation> transforms;
		private final ImmutableList<BakedQuad> quads;
		private final TextureAtlasSprite particle;
		private final VertexFormat format;

		public BakedDynPing(ModelDynPing parent, ImmutableList<BakedQuad> quads, TextureAtlasSprite particle, VertexFormat format, ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms, Map<String, IBakedModel> cache) {
			this.quads = quads;
			this.particle = particle;
			this.format = format;
			this.parent = parent;
			this.transforms = transforms;
			this.cache = cache;
		}

		@Override
		public ItemOverrideList getOverrides() {
			return BakedDynPingOverrideHandler.INSTANCE;
		}

		@Override
		public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
			return IPerspectiveAwareModel.MapWrapper.handlePerspective(this, transforms, cameraTransformType);
		}

		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
			if (side == null)
				return quads;
			return ImmutableList.of();
		}

		public boolean isAmbientOcclusion() {
			return true;
		}

		public boolean isGui3d() {
			return false;
		}

		public boolean isBuiltInRenderer() {
			return false;
		}

		public TextureAtlasSprite getParticleTexture() {
			return particle;
		}

		public ItemCameraTransforms getItemCameraTransforms() {
			return ItemCameraTransforms.DEFAULT;
		}
	}
}
