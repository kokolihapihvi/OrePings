package com.kokolihapihvi.orepings.proxy;

import net.minecraftforge.common.MinecraftForge;

import com.kokolihapihvi.orepings.handlers.PingLootHandler;
import com.kokolihapihvi.orepings.networking.PacketDispatcher;

public class CommonProxy {
    public void registerHandlers() {
        PacketDispatcher.registerPackets();
        
        MinecraftForge.EVENT_BUS.register(new PingLootHandler());
    }
}
