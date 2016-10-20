package com.kokolihapihvi.orepings.handlers;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraft.world.storage.loot.functions.SetNBT;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.kokolihapihvi.orepings.OrePingsMod;
import com.kokolihapihvi.orepings.config.ConfigurationHandler;
import com.kokolihapihvi.orepings.registry.ItemRegistry;
import com.kokolihapihvi.orepings.registry.PingableOreRegistry;

public class PingLootHandler {

	private void addBlankPing(LootPool pool) {
		// Add blank ping
		LootFunction[] funcs = new LootFunction[1];
		funcs[0] = new SetCount(new LootCondition[0], new RandomValueRange(1, 10));

		LootEntryItem entry = new LootEntryItem(ItemRegistry.blankPing, 18, 0, funcs, new LootCondition[0], OrePingsMod.MODID + ":" + ItemRegistry.blankPing.getItemName());
		pool.addEntry(entry);
	}

	private void addOrePings(LootPool pool) {
		// Add single use pings

		// Add one entry for each ore registered
		for (String oreDictName : PingableOreRegistry.getList()) {
			// If disabled, don't add to pool
	        if(!PingableOreRegistry.getOre(oreDictName).enabled) continue;
	        
			LootFunction[] funcs = new LootFunction[2];
			funcs[0] = new SetCount(new LootCondition[0], new RandomValueRange(1, 4));

			NBTTagCompound oreTag = new NBTTagCompound();
			oreTag.setString("ore", oreDictName);

			NBTTagCompound tag = new NBTTagCompound();
			tag.setTag("OrePing", oreTag);

			funcs[1] = new SetNBT(new LootCondition[0], tag);

			LootEntryItem entry = new LootEntryItem(ItemRegistry.singleUsePing, 1, 0, funcs, new LootCondition[0], OrePingsMod.MODID + ":" + ItemRegistry.singleUsePing.getItemName() + "_" + oreDictName);
			pool.addEntry(entry);
		}
	}

	private void addPingPool(LootTable table, int maxRolls) {
		LootPool pingpool = new LootPool(new LootEntry[0], new LootCondition[0], new RandomValueRange(0, maxRolls), new RandomValueRange(0, 0), OrePingsMod.MODID);

		addBlankPing(pingpool);
		addOrePings(pingpool);

		table.addPool(pingpool);
	}

	@SubscribeEvent
	public void onLootTableLoad(LootTableLoadEvent event) {
		// If loot tables disabled in the config, don't add entries
		if(!ConfigurationHandler.lootTables) return;
		
		LootTable table = event.getTable();

		// Add pings to various loot tables
		if (event.getName().equals(LootTableList.CHESTS_ABANDONED_MINESHAFT) ||
				event.getName().equals(LootTableList.CHESTS_DESERT_PYRAMID) ||
				event.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON) ||
				event.getName().equals(LootTableList.CHESTS_JUNGLE_TEMPLE)) {
			addPingPool(table, 2);
		} else if(event.getName().equals(LootTableList.CHESTS_STRONGHOLD_CORRIDOR) ||
				event.getName().equals(LootTableList.CHESTS_STRONGHOLD_CROSSING) ||
				event.getName().equals(LootTableList.CHESTS_STRONGHOLD_LIBRARY) ||
				event.getName().equals(LootTableList.CHESTS_VILLAGE_BLACKSMITH)) {
			addPingPool(table, 3);
		}

		return;
	}
}
