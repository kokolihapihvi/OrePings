package com.kokolihapihvi.orepings.proxy;

import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;

import com.kokolihapihvi.orepings.client.PingRenderer;
import com.kokolihapihvi.orepings.client.model.ItemRenderRegister;
import com.kokolihapihvi.orepings.client.model.ModelDynPing;

public class ClientProxy extends CommonProxy {

	@Override
	public void registerHandlers() {
		super.registerHandlers();

		MinecraftForge.EVENT_BUS.register(new PingRenderer());

		ItemRenderRegister.registerModels();
	}
}
