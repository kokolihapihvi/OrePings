package com.kokolihapihvi.orepings.client.model;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelDynBucket;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.kokolihapihvi.orepings.registry.ItemRegistry;

@SideOnly(Side.CLIENT)
public class ItemRenderRegister {

    public static void registerModels() {
        register(ItemRegistry.blankPing);
        //register(ItemRegistry.singleUsePing);
        
        ModelLoaderRegistry.registerLoader(ModelDynPing.LoaderDynPing.instance);
        //ModelLoaderRegistry.registerLoader(ModelDynBucketTest.LoaderDynBucketTest.INSTANCE);

        //ModelLoader.registerItemVariants(ItemRegistry.singleUsePing, new ModelResourceLocation("ore_pings:dynping","inventory"));
        ModelLoader.setCustomModelResourceLocation(ItemRegistry.singleUsePing, 0, new ModelResourceLocation("ore_pings:dynping","inventory"));
        
        /*
        ModelLoader.setCustomMeshDefinition(ItemRegistry.singleUsePing, new ItemMeshDefinition()
        {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack)
            {
                return ModelDynPing.LOCATION;
            }
        });
        ModelBakery.registerItemVariants(ItemRegistry.singleUsePing, ModelDynPing.LOCATION);
        */
    }

    private static void register(Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getUnlocalizedName().replace("item.",""),"inventory"));
    }
}
