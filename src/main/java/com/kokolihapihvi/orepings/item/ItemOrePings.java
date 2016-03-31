package com.kokolihapihvi.orepings.item;

import com.kokolihapihvi.orepings.OrePingsMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemOrePings extends Item {

    private String itemName;

    public ItemOrePings() {
        super();

        setCreativeTab(OrePingsMod.creativeTab);
        setMaxStackSize(1);
    }

    public ItemOrePings(String itemName) {
        this();

        this.itemName = itemName;
    }

    public String getItemName() {
        return itemName;
    }

    @Override
    public String getUnlocalizedName() {
        return String.format("item.%s:%s", OrePingsMod.MODID.toLowerCase(), this.itemName);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return String.format("item.%s:%s", OrePingsMod.MODID.toLowerCase(), this.itemName);
    }
}
