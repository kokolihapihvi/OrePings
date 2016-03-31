package com.kokolihapihvi.orepings.registry;

import com.kokolihapihvi.orepings.util.LogHelper;
import com.kokolihapihvi.orepings.util.PingableOre;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;
import java.util.Map;

public class PingableOreRegistry {

    private static HashMap<String, PingableOre> oreDictOres = new HashMap<String, PingableOre>();

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

        oreDictOres.put(oreName, new PingableOre(stack));

        LogHelper.info("Added ping for "+oreName+" using "+stack.getItem().getItemStackDisplayName(stack));
    }

    public static void unregister(String orename) {
        if(oreDictOres.containsKey(orename)) {
            oreDictOres.remove(orename);
        }
    }

    private static void createPings() {
        String[] allOredict = OreDictionary.getOreNames();

        for (String orename : allOredict) {
            if(OreDictionary.getOres(orename).size() == 0) continue; // needs to have items registered
            if(!orename.startsWith("ore")) continue; // needs to start with "ore"
            if(!(OreDictionary.getOres(orename).get(0).getItem() instanceof ItemBlock)) continue; // needs to be a block
            if(orename.startsWith("orebush")) continue; // no pings for tinkers ore bushes :(

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
