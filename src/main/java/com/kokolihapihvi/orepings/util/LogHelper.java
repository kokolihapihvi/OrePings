package com.kokolihapihvi.orepings.util;

import com.kokolihapihvi.orepings.OrePingsMod;
import cpw.mods.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

/**
 * Created by Jeesus on 13.3.2016.
 */
public class LogHelper {
    public static void log(Level level, String message) {
        FMLLog.log(OrePingsMod.NAME, level, message);
    }

    public static void info(String message) {
        log(Level.INFO, message);
    }
}
