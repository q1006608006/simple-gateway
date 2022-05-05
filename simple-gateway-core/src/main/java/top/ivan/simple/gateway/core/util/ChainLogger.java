package top.ivan.simple.gateway.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ivan
 * @since 2022/04/24 17:42
 */
public class ChainLogger {
    private ChainLogger() {
    }

    public static final Logger CHAIN_LOGGER = LoggerFactory.getLogger("chain-log");

    public static void logStart() {
        logDebug("start--> ");
    }

    public static void logEnd() {
        logDebug(" <--end");
    }

    public static void logError(String message) {
        CHAIN_LOGGER.error("log-chain### error: {}", message);
    }

    @SuppressWarnings("all")
    public static void logDebug(String msg, Object... args) {
        if (CHAIN_LOGGER.isDebugEnabled()) {
            CHAIN_LOGGER.debug("log-chain### " + msg, args);
        }
    }

    public static boolean isDebugEnable() {
        return CHAIN_LOGGER.isDebugEnabled();
    }
}
