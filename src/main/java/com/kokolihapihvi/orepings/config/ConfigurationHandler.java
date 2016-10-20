package com.kokolihapihvi.orepings.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

import com.kokolihapihvi.orepings.registry.PingableOreRegistry;
import com.kokolihapihvi.orepings.util.PingableOre;

public class ConfigurationHandler {
    private static Configuration conf;

    public static int pingDuration = 400;
    public static int pingRadius = 20;

    public static void init(File configFile) {
        if(conf == null) {
            conf = new Configuration(configFile);
            conf.load();
        }
    }

    public static void loadConfig() {
        //Load all configuration variables
        for (String oreName : PingableOreRegistry.getList()) {
            loadConfig(oreName);
        }

        pingDuration = conf.getInt("pingDuration", "General", 400, 20, 100000, "Ore visibility duration in ticks");
        pingRadius = conf.getInt("pingRadius", "General", 10, 0, 1000, "Players within this radius will see pinged ores");

        if(conf.hasChanged()) {
            conf.save();
        }
    }

    public static void loadConfig(String oreName) {
        //Load configuration variables for this specific ore
        PingableOre po = PingableOreRegistry.getOre(oreName);
        po.enabled = conf.getBoolean("enabled", oreName, true, "Is the "+po.getName()+" ping enabled?");
        po.range = conf.getInt("range", oreName, 20, 5, 1000, "Range of "+po.getName()+" ping");

        if(conf.hasChanged()) {
            conf.save();
        }
    }
}
