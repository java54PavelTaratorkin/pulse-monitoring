package telran.pulse.monitoring;


import static telran.pulse.monitoring.AppConfigDefaults.*;
import telran.pulse.monitoring.config.AppConfigBasicShared;

public class AppConfig extends AppConfigBasicShared {
    private final String contentType;
    private final String contentTypeAttribute;

    public AppConfig(String loggerName) {
        super(loggerName);
        this.contentType = System.getenv().getOrDefault(CONTENT_TYPE_ENV, DEFAULT_CONTENT_TYPE);;
        this.contentTypeAttribute = System.getenv().getOrDefault(CONTENT_TYPE_ATTRIBUTE_ENV, DEFAULT_CONTENT_TYPE_ATTRIBUTE);
    }

    public String getContentTypeAttribute() {
        return contentTypeAttribute;
    }

    public String getContentType() {
        return contentType;
    }

    @Override
    public String toString() {
        return super.toString() + 
        ", contentTypeAttribute=" + getContentTypeAttribute() +
        ", contentType=" + getContentType();
    }
}