package com.kokolihapihvi.orepings;

import com.kokolihapihvi.orepings.config.ConfigurationHandler;
import com.kokolihapihvi.orepings.proxy.CommonProxy;
import com.kokolihapihvi.orepings.registry.ItemRegistry;
import com.kokolihapihvi.orepings.registry.PingableOreRegistry;
import com.kokolihapihvi.orepings.registry.RecipeRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = OrePingsMod.MODID, name = OrePingsMod.NAME, version = OrePingsMod.VERSION)
public class OrePingsMod
{
    public static final String NAME = "Ore Pings";
    public static final String MODID = "OrePings";
    public static final String VERSION = "1.7.10-1.0.1";

    @SidedProxy(serverSide = "com.kokolihapihvi.orepings.proxy.ServerProxy", clientSide = "com.kokolihapihvi.orepings.proxy.ClientProxy")
    public static CommonProxy proxy;

    @Mod.Instance(value=OrePingsMod.MODID)
    public static OrePingsMod instance;

    public static CreativeTabs creativeTab;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        creativeTab = new CreativeTabs(MODID) {
            @Override
            @SideOnly(Side.CLIENT)
            public Item getTabIconItem() {
                return ItemRegistry.blankPing;
            }
        };

        ItemRegistry.init();

        proxy.registerHandlers();

        ConfigurationHandler.init(event.getSuggestedConfigurationFile());

        //Register PingOreRegistry for OreRegisterEvents
        MinecraftForge.EVENT_BUS.register(new PingableOreRegistry());
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        PingableOreRegistry.init();

        //Load the config after the registers pings register and before recipes
        ConfigurationHandler.loadConfig();

        RecipeRegistry.init();
    }
}
