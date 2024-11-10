package telran.pulse.monitoring;

import telran.pulse.monitoring.config.*;
import java.util.logging.*;
import static telran.pulse.monitoring.AppConfigDefaults.*;

public class AppConfig extends AppConfigShared {
    private String contentType;
    private static AppConfig config = null;

    private AppConfig(Logger logger, Level loggerLevel, String contentType) {
        super(loggerLevel, logger);
        this.contentType = contentType;
    }

    public static synchronized AppConfig getConfig(Logger logger) {
        if (config == null) {
            setLogger(logger);
            Level loggerLevel = getLoggerLevelFromEnv();
            String contentType = System.getenv().getOrDefault("CONTENT_TYPE", DEFAULT_CONTENT_TYPE);

            config = new AppConfig(logger, loggerLevel, contentType);
        }
        return config;
    }

    public String getContentType() {
        return contentType;
    }

    @Override
    public String toString() {
        return "[" +
                "loggerLevel=" + getLoggerLevel() +
                ", contentType=" + contentType +
                "]";
    }
}