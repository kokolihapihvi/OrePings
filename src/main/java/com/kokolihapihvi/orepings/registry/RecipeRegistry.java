package com.kokolihapihvi.orepings.registry;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class RecipeRegistry {
    public static void init() {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemRegistry.blankPing, 4), "pdp", "dgd", "pdp", 'p', Items.paper, 'd', Items.diamond, 'g', Blocks.glass));

        for (String oreDictName : PingableOreRegistry.getList()) {
            if(!PingableOreRegistry.getOre(oreDictName).enabled) {
                continue;
            }

            ItemStack itemStack = new ItemStack(ItemRegistry.singleUsePing, 4);

            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("ore", oreDictName);

            NBTTagCompound tags = new NBTTagCompound();
            tags.setTag("OrePing", tag);

            itemStack.setTagCompound(tags);

            GameRegistry.addRecipe(new ShapedOreRecipe(itemStack, "bob", "ooo", "bob", 'b', ItemRegistry.blankPing, 'o', oreDictName));
        }
    }
}
