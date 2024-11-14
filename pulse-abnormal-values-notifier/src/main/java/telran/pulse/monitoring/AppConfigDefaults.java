package telran.pulse.monitoring;

import telran.pulse.monitoring.config.AppConfigDefaultsShared;

public interface AppConfigDefaults extends AppConfigDefaultsShared {
    // Default values for configuration
    String DEFAULT_REGION = "us-east-1";
    String DEFAULT_LOGGER_NAME = "pulse-abnormal-values-notifier";

    // Environment variable keys
    String TOPIC_ARN_ENV = "TOPIC_ARN";
    String REGION_ENV = "REGION";    
}