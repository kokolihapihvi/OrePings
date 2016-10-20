package com.kokolihapihvi.orepings.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.kokolihapihvi.orepings.registry.ItemRegistry;

public class PingableOre {

    private String name;
    private int damage;
    public boolean enabled;
    public int range;
    public String oreDictName;

    public PingableOre(ItemStack stack, String oreName) {
        name = stack.getDisplayName();
        damage = stack.getItemDamage();
        oreDictName = oreName;
    }

    public int getDamage() {
        return damage;
    }

    public String getName() {
        return name;
    }

    public boolean is(ItemStack stack) {
        if(stack.getDisplayName().equals(name) &&
                stack.getItemDamage() == damage
                ) return true;

        return false;
    }

    public ItemStack getStack() {
        ItemStack stack = new ItemStack(ItemRegistry.singleUsePing);

        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("ore", oreDictName);

        NBTTagCompound tags = new NBTTagCompound();
        tags.setTag("OrePing", tag);

        return stack;
    }
}
