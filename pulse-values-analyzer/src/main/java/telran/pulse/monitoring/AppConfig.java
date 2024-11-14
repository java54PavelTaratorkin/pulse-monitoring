package telran.pulse.monitoring;

import telran.pulse.monitoring.config.AppConfigShared;
import static telran.pulse.monitoring.AppConfigDefaults.*;

public class AppConfig extends AppConfigShared {
    private final String abnormalValuesTableName;
    private final String serverlessRestApiUri;

    public AppConfig(String loggerName) {
        super(loggerName);
        this.abnormalValuesTableName = System.getenv().getOrDefault(ABNORMAL_VALUES_TABLE_NAME_ENV,
                DEFAULT_ABNORMAL_VALUES_TABLE_NAME);
        this.serverlessRestApiUri = setServerlessRestApiRui();
    }

    private String setServerlessRestApiRui() {
        String serverlessRestApiUri = System.getenv(SERVERLESS_REST_API_URL_ENV);
        if (serverlessRestApiUri == null) {
            logger.severe(String.format("Range provider %s doesn't exist", serverlessRestApiUri));
            throw new RuntimeException(String.format("%s Environment variable not set", SERVERLESS_REST_API_URL_ENV));
        }
        return serverlessRestApiUri;
    }

    public String getAbnormalValuesTableName() {
        return abnormalValuesTableName;
    }

    public String getServerlessRestApiUri() {
        return serverlessRestApiUri;
    }

    @Override
    public String toString() {
        return super.toString() +
                ", abnormalValuesTableName=" + abnormalValuesTableName +
                ", serverlessRestApiUr=" + serverlessRestApiUri;
    }
}