package com.kokolihapihvi.orepings.client.model;

import com.kokolihapihvi.orepings.registry.ItemRegistry;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemRenderRegister {

    public static void registerModels() {
        register(ItemRegistry.blankPing);
        register(ItemRegistry.singleUsePing);
    }

    private static void register(Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getUnlocalizedName().replace("item.",""),"inventory"));
    }
}
