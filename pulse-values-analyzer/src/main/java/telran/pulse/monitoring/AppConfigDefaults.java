package telran.pulse.monitoring;

import telran.pulse.monitoring.config.AppConfigDefaultsShared;

public interface AppConfigDefaults extends AppConfigDefaultsShared {
    // Default values for configuration
    String DEFAULT_ABNORMAL_VALUES_TABLE_NAME = "pulse_abnormal_values";
    String DEFAULT_PATIENT_ID_ATTRIBUTE = "patientId";
    String DEFAULT_TIMESTAMP_ATTRIBUTE = "timestamp";
    String DEFAULT_VALUE_ATTRIBUTE = "value";
    String DEFAULT_EVENT_TYPE_ATTRIBUTE = "INSERT";
    int DEFAULT_MIN_THRESHOLD_PULSE_VALUE = 60;
    int DEFAULT_MAX_THRESHOLD_PULSE_VALUE = 120;

    // Environment variable keys
    String ABNORMAL_VALUES_TABLE_NAME_ENV = "ABNORMAL_VALUES_TABLE_NAME";
    String PATIENT_ID_ATTRIBUTE_ENV = "PATIENT_ID_ATTRIBUTE";
    String TIMESTAMP_ATTRIBUTE_ENV = "TIMESTAMP_ATTRIBUTE";
    String VALUE_ATTRIBUTE_ENV = "VALUE_ATTRIBUTE";
    String EVENT_TYPE_ATTRIBUTE_ENV = "EVENT_TYPE_ATTRIBUTE";
    String MIN_THRESHOLD_PULSE_VALUE_ENV = "MIN_THRESHOLD_PULSE_VALUE";
    String MAX_THRESHOLD_PULSE_VALUE_ENV = "MAX_THRESHOLD_PULSE_VALUE";
    String SERVERLESS_REST_API_URL = "SERVERLESS_REST_API_URL";
}