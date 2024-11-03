package telran.pulse.monitoring;

import static telran.pulse.monitoring.AppConfigDefaults.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AppConfig {
    private Level loggerLevel;
    private int lowerThreshold;
    private int upperThreshold;
    private String tableName;

    private static Logger logger;
    private static AppConfig config = null;

    private AppConfig(Level loggerLevel, int lowerThreshold, int upperThreshold, String tableName) {
        this.loggerLevel = loggerLevel;
        this.lowerThreshold = lowerThreshold;
        this.upperThreshold = upperThreshold;
        this.tableName = tableName;
    }

    public static synchronized AppConfig getConfig(Logger logger) {
        if (config == null) {
            AppConfig.logger = logger;
            String tableName = System.getenv().getOrDefault(TABLE_NAME, DEFAULT_TABLE_NAME);
            Level loggerLevel = getLoggerValue();
            int lowerThreshold = getThresholdValue(LOWER_THRESHOLD, DEFAULT_LOWER_THRESHOLD);
            int upperThreshold = getThresholdValue(UPPER_THRESHOLD, DEFAULT_UPPER_THRESHOLD);
            config = new AppConfig(loggerLevel, lowerThreshold, upperThreshold, tableName);
        }
        return config;
    }

    private static Level getLoggerValue() {
        String loggerLevelStr = System.getenv().getOrDefault(LOGGING_LEVEL, DEFAULT_LOGGING_LEVEL);
        Level level;
        try {
            level = Level.parse(loggerLevelStr.toUpperCase());
        } catch (Exception e) {
            logger.severe(loggerLevelStr + " - Wrong Logging Level. Using default level: " + DEFAULT_LOGGING_LEVEL);
            level = Level.parse(DEFAULT_LOGGING_LEVEL);
        }
        return level;
    }

    private static int getThresholdValue(String envName, int defaultValue) {
        String valueStr = System.getenv().getOrDefault(envName, String.valueOf(defaultValue));
        int value;
        try {
            value = Integer.parseInt(valueStr);
        } catch (Exception e) {
            logger.severe(String.format("%s - wrong value for %s. Using default value: %d", valueStr, envName, defaultValue));
            value = defaultValue;
        }
        return value;
    }


    @Override
    public String toString() {
        return "PulseValuesAnalyzerConfigValues [loggerLevel=" + loggerLevel + ", lowerThreshold=" + lowerThreshold
                + ", upperThreshold=" + upperThreshold + ", tableName=" + tableName + "]";
    }

    public Level getLoggerLevel() {
        return loggerLevel;
    }

    public int getLowerThreshold() {
        return lowerThreshold;
    }

    public int getUpperThreshold() {
        return upperThreshold;
    }

    public String getTableName() {
        return tableName;
    }
}