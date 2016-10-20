package com.kokolihapihvi.orepings.registry;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.kokolihapihvi.orepings.item.BlankPingItem;
import com.kokolihapihvi.orepings.item.ItemOrePings;
import com.kokolihapihvi.orepings.item.SingleUsePingItem;

public class ItemRegistry {
    public static final ItemOrePings singleUsePing = new SingleUsePingItem();
    public static final ItemOrePings blankPing = new BlankPingItem();

    public static void init()
    {
        GameRegistry.<Item>register(blankPing);
        GameRegistry.<Item>register(singleUsePing);
    }
}
