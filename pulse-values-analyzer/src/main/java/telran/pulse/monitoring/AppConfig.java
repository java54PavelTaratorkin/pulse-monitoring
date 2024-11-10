package telran.pulse.monitoring;

import telran.pulse.monitoring.config.AppConfigShared;
import static telran.pulse.monitoring.AppConfigDefaults.*;

import java.util.logging.*;

public class AppConfig extends AppConfigShared {
    private String abnormalValuesTableName;
    private String patientIdAttribute;
    private String timestampAttribute;
    private String valueAttribute;
    private String eventTypeAttribute;
    private String serverlessRestApiUrL;

    private static AppConfig config = null;

    private AppConfig(Logger logger, Level loggerLevel, String abnormalValuesTableName, String patientIdAttribute,
            String timestampAttribute, String valueAttribute, String eventTypeAttribute,
            String serverlessRestApiUrL) {
        super(loggerLevel, logger);
        this.abnormalValuesTableName = abnormalValuesTableName;
        this.patientIdAttribute = patientIdAttribute;
        this.timestampAttribute = timestampAttribute;
        this.valueAttribute = valueAttribute;
        this.eventTypeAttribute = eventTypeAttribute;
        this.serverlessRestApiUrL = serverlessRestApiUrL;
    }

    public static synchronized AppConfig getConfig(Logger logger) {
        if (config == null) {
            setLogger(logger);
            Level loggerLevel = getLoggerLevelFromEnv();
            String abnormalValuesTableName = System.getenv().getOrDefault(ABNORMAL_VALUES_TABLE_NAME_ENV,
                    DEFAULT_ABNORMAL_VALUES_TABLE_NAME);
            String patientIdAttribute = System.getenv().getOrDefault(PATIENT_ID_ATTRIBUTE_ENV,
                    DEFAULT_PATIENT_ID_ATTRIBUTE);
            String timestampAttribute = System.getenv().getOrDefault(TIMESTAMP_ATTRIBUTE_ENV,
                    DEFAULT_TIMESTAMP_ATTRIBUTE);
            String valueAttribute = System.getenv().getOrDefault(VALUE_ATTRIBUTE_ENV, DEFAULT_VALUE_ATTRIBUTE);
            String eventTypeAttribute = System.getenv().getOrDefault(EVENT_TYPE_ATTRIBUTE_ENV,
                    DEFAULT_EVENT_TYPE_ATTRIBUTE);
            String serverlessRestApiUrL = System.getenv(SERVERLESS_REST_API_URL);

            config = new AppConfig(logger, loggerLevel, abnormalValuesTableName, patientIdAttribute,
                    timestampAttribute, valueAttribute, eventTypeAttribute, serverlessRestApiUrL);
        }
        return config;
    }

    public String getAbnormalValuesTableName() {
        return abnormalValuesTableName;
    }

    public String getPatientIdAttribute() {
        return patientIdAttribute;
    }

    public String getTimestampAttribute() {
        return timestampAttribute;
    }

    public String getValueAttribute() {
        return valueAttribute;
    }

    public String getEventTypeAttribute() {
        return eventTypeAttribute;
    }

    public String getServerlessRestApiUrl() {
        return serverlessRestApiUrL;
    }

    @Override
    public String toString() {
        return "[loggerLevel=" + getLoggerLevel() +
                ", abnormalValuesTableName=" + abnormalValuesTableName +
                ", patientIdAttribute=" + patientIdAttribute +
                ", timestampAttribute=" + timestampAttribute +
                ", valueAttribute=" + valueAttribute +
                ", eventTypeAttribute=" + eventTypeAttribute +
                ", serverlessRestApi=" + serverlessRestApiUrL + "]";
    }
}