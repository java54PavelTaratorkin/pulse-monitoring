package telran.pulse.monitoring;
import telran.pulse.monitoring.config.*;

public interface AppConfigDefaults extends AppConfigDefaultsShared {
    // Default values for configuration
    float DEFAULT_JUMP_FACTOR = 0.2f;
    String DEFAULT_LAST_VALUES_TABLE_NAME = "pulse_last_values";
    String DEFAULT_JUMP_VALUES_TABLE_NAME = "pulse_jump_values";
    String DEFAULT_PREVIOUS_VALUE_ATTRIBUTE = "previousValue";    
    String DEFAULT_CURRENT_VALUE_ATTRIBUTE = "currentValue";
    String DEFAULT_LOGGER_NAME = "pulse-jump-analyzer";

    // Environment variable keys
    String JUMP_FACTOR_ENV = "JUMP_FACTOR";
    String LAST_VALUES_TABLE_NAME_ENV = "LAST_VALUES_TABLE_NAME";
    String JUMP_VALUES_TABLE_NAME_ENV = "JUMP_VALUES_TABLE_NAME";
    String PREVIOUS_VALUE_ATTRIBUTE_ENV = "PREVIOUS_VALUE_ATTRIBUTE";    
    String CURRENT_VALUE_ATTRIBUTE_ENV = "CURRENT_VALUE_ATTRIBUTE";    
}