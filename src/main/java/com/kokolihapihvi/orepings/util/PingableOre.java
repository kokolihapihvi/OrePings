package com.kokolihapihvi.orepings.util;

import net.minecraft.item.ItemStack;

public class PingableOre {

    private String name;
    private int damage;
    public boolean enabled;
    public int range;

    public PingableOre(ItemStack stack) {
        name = stack.getDisplayName();
        damage = stack.getItemDamage();
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
}
