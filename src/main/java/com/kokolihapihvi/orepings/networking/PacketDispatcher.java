package com.kokolihapihvi.orepings.networking;

import com.kokolihapihvi.orepings.OrePingsMod;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketDispatcher {

    public static final SimpleNetworkWrapper network = new SimpleNetworkWrapper(OrePingsMod.MODID);

    public static void registerPackets() {
        network.registerMessage(PingMessage.HANDLER.class, PingMessage.class, 0, Side.CLIENT);
    }

    public static void sendTo(EntityPlayerMP player, PingMessage message) {
        network.sendTo(message, player);
    }
}
