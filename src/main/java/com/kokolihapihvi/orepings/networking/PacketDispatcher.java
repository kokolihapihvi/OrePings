package com.kokolihapihvi.orepings.networking;

import com.kokolihapihvi.orepings.OrePingsMod;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketDispatcher {

    public static final SimpleNetworkWrapper network = new SimpleNetworkWrapper(OrePingsMod.MODID);

    public static void registerPackets() {
        network.registerMessage(PingMessage.HANDLER.class, PingMessage.class, 0, Side.CLIENT);
    }

    public static void sendTo(EntityPlayerMP player, PingMessage message) {
        network.sendTo(message, player);
    }
}
