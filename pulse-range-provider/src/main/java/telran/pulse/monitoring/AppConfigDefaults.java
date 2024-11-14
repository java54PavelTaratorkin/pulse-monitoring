package telran.pulse.monitoring;

import telran.pulse.monitoring.config.AppConfigDefaultsShared;

public interface AppConfigDefaults extends AppConfigDefaultsShared {
    // Default values for configuration
    String DEFAULT_CONTENT_TYPE = "application/json";
    String DEFAULT_LOGGER_NAME = "pulse-range-provider";
    String DEFAULT_CONTENT_TYPE_ATTRIBUTE = "Content-Type";

    // Environment variable keys
    String CONTENT_TYPE_ENV = "application/json";
    String CONTENT_TYPE_ATTRIBUTE_ENV = "CONTENT_TYPE_ATTRIBUTE";
}