package com.kokolihapihvi.orepings.proxy;

import com.kokolihapihvi.orepings.client.PingRenderer;
import com.kokolihapihvi.orepings.client.model.ItemRenderRegister;
import com.kokolihapihvi.orepings.client.model.ModelBakeHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ClientProxy extends CommonProxy {

    @Override
    public void registerHandlers() {
        super.registerHandlers();

        MinecraftForge.EVENT_BUS.register(new PingRenderer());
        MinecraftForge.EVENT_BUS.register(new ModelBakeHandler());

        ItemRenderRegister.registerModels();
    }
}
