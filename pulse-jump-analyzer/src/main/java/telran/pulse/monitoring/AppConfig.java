package telran.pulse.monitoring;

import telran.pulse.monitoring.config.AppConfigShared;
import static telran.pulse.monitoring.AppConfigDefaults.*;

public class AppConfig extends AppConfigShared {
    private final float jumpFactor;
    private final String lastValuesTableName;
    private final String jumpValuesTableName;
    private final String previousValueAttribute;    
    private final String currentValueAttribute;

    public AppConfig(String loggerName) {
        super(loggerName);
        this.jumpFactor = getEnvFloat(JUMP_FACTOR_ENV, DEFAULT_JUMP_FACTOR);
        this.lastValuesTableName = System.getenv().getOrDefault(LAST_VALUES_TABLE_NAME_ENV, DEFAULT_LAST_VALUES_TABLE_NAME);
        this.jumpValuesTableName = System.getenv().getOrDefault(JUMP_VALUES_TABLE_NAME_ENV, DEFAULT_JUMP_VALUES_TABLE_NAME);
        this.previousValueAttribute = System.getenv().getOrDefault(PREVIOUS_VALUE_ATTRIBUTE_ENV, DEFAULT_PREVIOUS_VALUE_ATTRIBUTE);        
        this.currentValueAttribute = System.getenv().getOrDefault(CURRENT_VALUE_ATTRIBUTE_ENV, DEFAULT_CURRENT_VALUE_ATTRIBUTE);
    }

    private float getEnvFloat(String envName, float defaultValue) {
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
    return super.toString() +
           ", jumpFactor=" + jumpFactor +
           ", lastValuesTableName=" + lastValuesTableName +
           ", jumpValuesTableName=" + jumpValuesTableName +
           ", previousValueAttribute=" + previousValueAttribute +
           ", currentValueAttribute=" + currentValueAttribute;
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

    public String getPreviousValueAttribute() {
        return previousValueAttribute;
    }

    public String getCurrentValueAttribute() {
        return currentValueAttribute;
    }
    
}