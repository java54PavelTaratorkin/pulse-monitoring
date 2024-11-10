package telran.pulse.monitoring;

import telran.pulse.monitoring.config.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static telran.pulse.monitoring.AppConfigDefaults.*;

public class AppConfig extends AppConfigShared {
    private float jumpFactor;
    private String lastValuesTableName;
    private String jumpValuesTableName;
    private String patientIdAttribute;
    private String timestampAttribute;
    private String previousValueAttribute;
    private String valueAttribute;
    private String currentValueAttribute;
    private String eventTypeAttribute;

    private static AppConfig config = null;

    private AppConfig(Logger logger, Level loggerLevel, float jumpFactor, String lastValuesTableName, String jumpValuesTableName,
                      String patientIdAttribute, String timestampAttribute, String previousValueAttribute,
                      String valueAttribute, String currentValueAttribute, String eventTypeAttribute) {
        super(loggerLevel, logger);
        this.jumpFactor = jumpFactor;
        this.lastValuesTableName = lastValuesTableName;
        this.jumpValuesTableName = jumpValuesTableName;
        this.patientIdAttribute = patientIdAttribute;
        this.timestampAttribute = timestampAttribute;
        this.previousValueAttribute = previousValueAttribute;
        this.valueAttribute = valueAttribute;
        this.currentValueAttribute = currentValueAttribute;
        this.eventTypeAttribute = eventTypeAttribute;
    }

    public static synchronized AppConfig getConfig(Logger logger) {
        if (config == null) {
            setLogger(logger);
            Level loggerLevel = getLoggerLevelFromEnv();
            float jumpFactor = getEnvFloat(JUMP_FACTOR_ENV, DEFAULT_JUMP_FACTOR);
            String lastValuesTableName = System.getenv().getOrDefault(LAST_VALUES_TABLE_NAME_ENV, DEFAULT_LAST_VALUES_TABLE_NAME);
            String jumpValuesTableName = System.getenv().getOrDefault(JUMP_VALUES_TABLE_NAME_ENV, DEFAULT_JUMP_VALUES_TABLE_NAME);
            String patientIdAttribute = System.getenv().getOrDefault(PATIENT_ID_ATTRIBUTE_ENV, DEFAULT_PATIENT_ID_ATTRIBUTE);
            String timestampAttribute = System.getenv().getOrDefault(TIMESTAMP_ATTRIBUTE_ENV, DEFAULT_TIMESTAMP_ATTRIBUTE);
            String previousValueAttribute = System.getenv().getOrDefault(PREVIOUS_VALUE_ATTRIBUTE_ENV, DEFAULT_PREVIOUS_VALUE_ATTRIBUTE);
            String valueAttribute = System.getenv().getOrDefault(VALUE_ATTRIBUTE_ENV, DEFAULT_VALUE_ATTRIBUTE);
            String currentValueAttribute = System.getenv().getOrDefault(CURRENT_VALUE_ATTRIBUTE_ENV, DEFAULT_CURRENT_VALUE_ATTRIBUTE);
            String eventTypeAttribute = System.getenv().getOrDefault(EVENT_TYPE_ATTRIBUTE_ENV, DEFAULT_EVENT_TYPE_ATTRIBUTE);

            config = new AppConfig(logger, loggerLevel, jumpFactor, lastValuesTableName, jumpValuesTableName, 
                                   patientIdAttribute, timestampAttribute, previousValueAttribute, 
                                   valueAttribute, currentValueAttribute, eventTypeAttribute);
        }
        return config;
    }

    private static float getEnvFloat(String envName, float defaultValue) {
        String valueStr = System.getenv().getOrDefault(envName, String.valueOf(defaultValue));
        float result;
        try {
            result = Float.parseFloat(valueStr);
        } catch (Exception e) {
            logger.severe(String.format("%s - Invalid value for %s. Using default value: %.2f", valueStr, envName, defaultValue));
            result = defaultValue;
        }
        return result;
    }

    @Override
    public String toString() {
        return "[" +
                "loggerLevel=" + getLoggerLevel() +
                ", jumpFactor=" + jumpFactor +
                ", lastValuesTableName=" + lastValuesTableName +
                ", jumpValuesTableName=" + jumpValuesTableName +
                ", patientIdAttribute=" + patientIdAttribute +
                ", timestampAttribute=" + timestampAttribute +
                ", previousValueAttribute=" + previousValueAttribute +
                ", valueAttribute=" + valueAttribute +
                ", currentValueAttribute=" + currentValueAttribute +
                "]";
    }

    public float getJumpFactor() {
        return jumpFactor;
    }

    public String getLastValuesTableName() {
        return lastValuesTableName;
    }

    public String getJumpValuesTableName() {
        return jumpValuesTableName;
    }

    public String getPatientIdAttribute() {
        return patientIdAttribute;
    }

    public String getTimestampAttribute() {
        return timestampAttribute;
    }

    public String getPreviousValueAttribute() {
        return previousValueAttribute;
    }

    public String getValueAttribute() {
        return valueAttribute;
    }

    public String getCurrentValueAttribute() {
        return currentValueAttribute;
    }

    public String getEventTypeAttribute() {
        return eventTypeAttribute;
    }
}