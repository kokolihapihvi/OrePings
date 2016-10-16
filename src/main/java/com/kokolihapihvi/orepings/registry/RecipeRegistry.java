package com.kokolihapihvi.orepings.registry;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.ArrayList;

public class RecipeRegistry {

    //List of already registered recipes so we don't create multiples
    private static ArrayList<String> registeredRecipes = new ArrayList<String>();

    public static void init() {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemRegistry.blankPing, 4), "pdp", "dgd", "pdp", 'p', Items.PAPER, 'd', Items.DIAMOND, 'g', Blocks.GLASS));

        for (String oreDictName : PingableOreRegistry.getList()) {
            registerRecipe(oreDictName);
        }
    }

    public static void registerRecipe(String oreDictName) {
        //If disabled, don't create recipe
        if(!PingableOreRegistry.getOre(oreDictName).enabled) return;

        //If recipe already created, don't create multiple
        if(registeredRecipes.contains(oreDictName)) return;

        registeredRecipes.add(oreDictName);

        ItemStack itemStack = new ItemStack(ItemRegistry.singleUsePing, 4);

        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("ore", oreDictName);

        NBTTagCompound tags = new NBTTagCompound();
        tags.setTag("OrePing", tag);

        itemStack.setTagCompound(tags);

        GameRegistry.addRecipe(new ShapedOreRecipe(itemStack, "bob", "ooo", "bob", 'b', ItemRegistry.blankPing, 'o', oreDictName));
    }
}
