package telran.pulse.monitoring.config;

import java.util.logging.*;

import static telran.pulse.monitoring.config.AppConfigDefaultsShared.*;

public class AppConfigShared {
    protected Level loggerLevel;
    protected static Logger logger;

    protected AppConfigShared(Level loggerLevel, Logger logger) {
        this.loggerLevel = loggerLevel;
        setLogger(logger);
    }

    protected static void setLogger(Logger logger) {
        AppConfigShared.logger = logger;
        LogManager.getLogManager().reset();
        Handler handler = new ConsoleHandler();
        logger.setLevel(getLoggerLevelFromEnv());
        handler.setLevel(Level.FINEST);
        logger.addHandler(handler);
    }

    protected static Level getLoggerLevelFromEnv() {
        String loggerLevelStr = System.getenv().getOrDefault(LOGGER_LEVEL_ENV_VARIABLE, DEFAULT_LOGGER_LEVEL);
        Level level;
        try {
            level = Level.parse(loggerLevelStr.toUpperCase());
        } catch (Exception e) {
            logger.severe(loggerLevelStr + " - Invalid logging level. Using default level: " + DEFAULT_LOGGER_LEVEL);
            level = Level.parse(DEFAULT_LOGGER_LEVEL);
        }
        return level;
    }

    public Level getLoggerLevel() {
        return loggerLevel;
    }
}