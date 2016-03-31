package com.kokolihapihvi.orepings;

import com.kokolihapihvi.orepings.config.ConfigurationHandler;
import com.kokolihapihvi.orepings.proxy.CommonProxy;
import com.kokolihapihvi.orepings.registry.ItemRegistry;
import com.kokolihapihvi.orepings.registry.PingableOreRegistry;
import com.kokolihapihvi.orepings.registry.RecipeRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = OrePingsMod.MODID, name = OrePingsMod.NAME, version = OrePingsMod.VERSION)
public class OrePingsMod
{
    public static final String NAME = "Ore Pings";
    public static final String MODID = "OrePings";
    public static final String VERSION = "1.7.10-1.0.0";

    @SidedProxy(serverSide = "com.kokolihapihvi.orepings.proxy.ServerProxy", clientSide = "com.kokolihapihvi.orepings.proxy.ClientProxy")
    public static CommonProxy proxy;

    @Mod.Instance(value=OrePingsMod.MODID)
    public static OrePingsMod instance;

    public static CreativeTabs creativeTab;

    @Mod.EventHandler
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
    }
    
    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        PingableOreRegistry.init();

        //Load the config after the registers pings register and before recipes
        ConfigurationHandler.loadConfig();

        RecipeRegistry.init();
    }
}
