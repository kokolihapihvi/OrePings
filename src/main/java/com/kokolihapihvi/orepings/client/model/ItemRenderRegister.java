package com.kokolihapihvi.orepings.client.model;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.kokolihapihvi.orepings.registry.ItemRegistry;

@SideOnly(Side.CLIENT)
public class ItemRenderRegister {

    public static void registerModels() {
        register(ItemRegistry.blankPing);
        
        //Register custom loader for dynping
        ModelLoaderRegistry.registerLoader(ModelDynPing.LoaderDynPing.instance);
        
        //Register custom model for dynping
        ModelLoader.setCustomModelResourceLocation(ItemRegistry.singleUsePing, 0, new ModelResourceLocation("ore_pings:dynping","inventory"));
    }

    private static void register(Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getUnlocalizedName().replace("item.",""),"inventory"));
    }
}
