package telran.pulse.monitoring.config;

import java.util.logging.*;

import static telran.pulse.monitoring.config.AppConfigDefaultsShared.*;

public class AppConfigBasicShared {
    protected final Level loggerLevel;
    protected final Logger logger;
    protected final String patientIdAttribute;

    public AppConfigBasicShared(String loggerName) {
        this.loggerLevel = getLoggerLevelFromEnv();
        this.logger = initializeLogger(loggerName);
        this.patientIdAttribute = System.getenv().getOrDefault(PATIENT_ID_ATTRIBUTE_ENV, DEFAULT_PATIENT_ID_ATTRIBUTE);
    }

    private Logger initializeLogger(String loggerName) {
        Logger logger = Logger.getLogger(loggerName);
        LogManager.getLogManager().reset();
        Handler handler = new ConsoleHandler();
        logger.setLevel(loggerLevel);
        handler.setLevel(Level.FINEST);
        logger.addHandler(handler);
        return logger;
    }

    private static Level getLoggerLevelFromEnv() {
        String loggerLevelStr = System.getenv().getOrDefault(LOGGER_LEVEL_ENV_VARIABLE, DEFAULT_LOGGER_LEVEL);
        try {
            return Level.parse(loggerLevelStr.toUpperCase());
        } catch (Exception e) {
            return Level.parse(DEFAULT_LOGGER_LEVEL);
        }
    }

    public Logger getLogger() {
        return logger;
    }

    public Level getLoggerLevel() {
        return loggerLevel;
    }

    public String getPatientIdAttribute() {
        return patientIdAttribute;
    }

    @Override
    public String toString() {
        return "loggerLevel=" + loggerLevel +
               ", loggerName=" + logger.getName();
    }
}