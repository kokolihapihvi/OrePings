package com.kokolihapihvi.orepings.proxy;

import com.kokolihapihvi.orepings.networking.PacketDispatcher;

public class CommonProxy {
    public void registerHandlers() {
        PacketDispatcher.registerPackets();
    }
}
