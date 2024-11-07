package telran.pulse.monitoring;

public interface AppConfigDefaults {
    // Default values for configuration
    String DEFAULT_LOGGER_LEVEL = "INFO";
    float DEFAULT_JUMP_FACTOR = 0.2f;
    String DEFAULT_LAST_VALUES_TABLE_NAME = "pulse_last_values";
    String DEFAULT_JUMP_VALUES_TABLE_NAME = "pulse_jump_values";
    String DEFAULT_PATIENT_ID_ATTRIBUTE = "patientId";
    String DEFAULT_TIMESTAMP_ATTRIBUTE = "timestamp";
    String DEFAULT_PREVIOUS_VALUE_ATTRIBUTE = "previousValue";
    String DEFAULT_VALUE_ATTRIBUTE = "value";
    String DEFAULT_CURRENT_VALUE_ATTRIBUTE = "currentValue";
    String DEFAULT_EVENT_TYPE_ATTRIBUTE = "INSERT";

    // Environment variable keys
    String LOGGER_LEVEL_ENV_VARIABLE = "LOGGER_LEVEL";
    String JUMP_FACTOR_ENV = "JUMP_FACTOR";
    String LAST_VALUES_TABLE_NAME_ENV = "LAST_VALUES_TABLE_NAME";
    String JUMP_VALUES_TABLE_NAME_ENV = "JUMP_VALUES_TABLE_NAME";
    String PATIENT_ID_ATTRIBUTE_ENV = "PATIENT_ID_ATTRIBUTE";
    String TIMESTAMP_ATTRIBUTE_ENV = "TIMESTAMP_ATTRIBUTE";
    String PREVIOUS_VALUE_ATTRIBUTE_ENV = "PREVIOUS_VALUE_ATTRIBUTE";
    String VALUE_ATTRIBUTE_ENV = "VALUE_ATTRIBUTE";
    String CURRENT_VALUE_ATTRIBUTE_ENV = "CURRENT_VALUE_ATTRIBUTE";
    String EVENT_TYPE_ATTRIBUTE_ENV = "INSERT";
}