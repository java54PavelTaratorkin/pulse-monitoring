package telran.pulse.monitoring.config;

import static telran.pulse.monitoring.config.AppConfigDefaultsShared.*;

public class AppConfigShared extends AppConfigBasicShared {
    protected final String eventTypeAttribute;
    protected final String timestampAttribute;
    protected final String valueAttribute;

    public AppConfigShared(String loggerName) {
        super(loggerName);
        this.eventTypeAttribute = System.getenv().getOrDefault(EVENT_TYPE_ATTRIBUTE_ENV, DEFAULT_EVENT_TYPE_ATTRIBUTE);
        this.timestampAttribute = System.getenv().getOrDefault(TIMESTAMP_ATTRIBUTE_ENV, DEFAULT_TIMESTAMP_ATTRIBUTE);
        this.valueAttribute = System.getenv().getOrDefault(VALUE_ATTRIBUTE_ENV, DEFAULT_VALUE_ATTRIBUTE);
    }

    public String getEventTypeAttribute() {
        return eventTypeAttribute;
    }

    public String getTimestampAttribute() {
        return timestampAttribute;
    }

    public String getValueAttribute() {
        return valueAttribute;
    }

    @Override
    public String toString() {
        return super.toString() +
                ", timestampAttribute=" + timestampAttribute +
                ", valueAttribute=" + valueAttribute +
                ", eventTypeAttribute=" + eventTypeAttribute;
    }
}