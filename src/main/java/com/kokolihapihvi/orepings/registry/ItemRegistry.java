package com.kokolihapihvi.orepings.registry;

import com.kokolihapihvi.orepings.item.BlankPingItem;
import com.kokolihapihvi.orepings.item.ItemOrePings;
import com.kokolihapihvi.orepings.item.SingleUsePingItem;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemRegistry {
    public static final ItemOrePings singleUsePing = new SingleUsePingItem();
    public static final ItemOrePings blankPing = new BlankPingItem();

    public static void init()
    {
        GameRegistry.registerItem(blankPing, blankPing.getItemName());
        GameRegistry.registerItem(singleUsePing, singleUsePing.getItemName());
    }
}
