package com.kokolihapihvi.orepings.registry;

import com.kokolihapihvi.orepings.config.ConfigurationHandler;
import com.kokolihapihvi.orepings.util.LogHelper;
import com.kokolihapihvi.orepings.util.PingableOre;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;
import java.util.Map;

public class PingableOreRegistry {

    private static HashMap<String, PingableOre> oreDictOres = new HashMap<String, PingableOre>();

    //Subscribe to OreRegisterEvents
    @SubscribeEvent
    public void oreRegister(OreDictionary.OreRegisterEvent event) {
        if(shouldRegister(event.Name)) {
            createPing(event.Name, event.Ore);
        }
    }

    public static void init() {
        createPings();
    }

    public static void register(String oreName, ItemStack stack) {
        for (Map.Entry<String, PingableOre> oreEntry : oreDictOres.entrySet()) {
            if(oreEntry.getValue().is(stack)) {
                LogHelper.info("Skipping "+oreName+" as it is already registered as "+oreEntry.getValue().getName());
                return;
            }
        }

        //Put it into our list
        oreDictOres.put(oreName, new PingableOre(stack, oreName));

        //Load/Create config for it
        ConfigurationHandler.loadConfig(oreName);

        //Create recipe
        RecipeRegistry.registerRecipe(oreName);

        LogHelper.info("Added ping for "+oreName+" using "+stack.getItem().getItemStackDisplayName(stack));
    }

    public static boolean shouldRegister(String orename) {
        if(OreDictionary.getOres(orename).size() == 0) return false; // needs to have items registered
        if(!orename.startsWith("ore")) return false; // needs to start with "ore"
        if(!(OreDictionary.getOres(orename).get(0).getItem() instanceof ItemBlock)) return false; // needs to be a block
        if(!Character.isUpperCase(orename.charAt(3))) return false; // only ores that are formatted like oreA...

        return true;
    }

    private static void createPings() {
        String[] allOredict = OreDictionary.getOreNames();

        for (String orename : allOredict) {
            //Check if this ore should be registered
            if(!shouldRegister(orename)) continue;

            ItemStack stack = OreDictionary.getOres(orename).get(0);

            createPing(orename, stack);
        }
    }

    private static void createPing(String oreName, ItemStack stack) {
        if(hasOre(oreName)) return;

        register(oreName, stack);
    }

    public static boolean hasOre(String oreName) {
        return oreDictOres.containsKey(oreName);
    }

    public static String[] getList() {
        return oreDictOres.keySet().toArray(new String[]{});
    }

    public static PingableOre getOre(String type) {
        return oreDictOres.get(type);
    }
}
