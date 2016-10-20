package com.kokolihapihvi.orepings.proxy;

import net.minecraftforge.common.MinecraftForge;

import com.kokolihapihvi.orepings.client.PingRenderer;
import com.kokolihapihvi.orepings.client.model.ItemRenderRegister;

public class ClientProxy extends CommonProxy {

	@Override
	public void registerHandlers() {
		super.registerHandlers();

		MinecraftForge.EVENT_BUS.register(new PingRenderer());

		ItemRenderRegister.registerModels();
	}
}
