package com.kokolihapihvi.orepings.proxy;

import com.kokolihapihvi.orepings.client.PingRenderer;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {

    @Override
    public void registerHandlers() {
        super.registerHandlers();

        PingRenderer pr = new PingRenderer();

        MinecraftForge.EVENT_BUS.register(pr);
        FMLCommonHandler.instance().bus().register(pr);
    }
}
