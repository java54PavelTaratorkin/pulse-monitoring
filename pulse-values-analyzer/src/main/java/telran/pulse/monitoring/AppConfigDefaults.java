package telran.pulse.monitoring;

import telran.pulse.monitoring.config.AppConfigDefaultsShared;

public interface AppConfigDefaults extends AppConfigDefaultsShared {
    // Default values for configuration
    String DEFAULT_ABNORMAL_VALUES_TABLE_NAME = "pulse_abnormal_values";
    String DEFAULT_LOGGER_NAME = "pulse-jump-analyzer";

    // Environment variable keys
    String ABNORMAL_VALUES_TABLE_NAME_ENV = "ABNORMAL_VALUES_TABLE_NAME";
    String SERVERLESS_REST_API_URL_ENV = "SERVERLESS_REST_API_URL";
}