package telran.pulse.monitoring;

public interface AppConfigDefaults {
    // Default values for configuration
    String DEFAULT_LOGGING_LEVEL = "INFO";
    int DEFAULT_LOWER_THRESHOLD = 150;
    int DEFAULT_UPPER_THRESHOLD = 190;
    String DEFAULT_TABLE_NAME = "pulse_abnormal_values";

    // Environment variable keys
    String LOGGING_LEVEL = "LOGGING_LEVEL";
    String LOWER_THRESHOLD = "LOWER_THRESHOLD";
    String UPPER_THRESHOLD = "UPPER_THRESHOLD";
    String TABLE_NAME = "TABLE_NAME";
}