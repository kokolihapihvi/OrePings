package com.kokolihapihvi.orepings.util;

import com.kokolihapihvi.orepings.OrePingsMod;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogHelper {
    private static final Logger logger = LogManager.getLogger(OrePingsMod.MODID);

    public static void log(Level level, String message) {
        logger.log(level, message);
    }

    public static void info(String message) {
        log(Level.INFO, message);
    }
}
