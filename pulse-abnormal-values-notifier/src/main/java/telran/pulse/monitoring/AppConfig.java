package telran.pulse.monitoring;

import telran.pulse.monitoring.config.AppConfigShared;
import static telran.pulse.monitoring.AppConfigDefaults.*;

public class AppConfig extends AppConfigShared {
    private final String topicArn;
    private final String awsRegion;

    public AppConfig(String loggerName) {
        super(loggerName);
        logger.info("TOPIC_ARN: " + System.getenv(TOPIC_ARN_ENV));
        this.topicArn = setTopicArn();
        this.awsRegion = System.getenv().getOrDefault(REGION_ENV, DEFAULT_REGION);
    }

    private String setTopicArn() {
        String topicArn = System.getenv(TOPIC_ARN_ENV);
        if (topicArn == null || topicArn.isEmpty()) {
            logger.severe(String.format("%s environment variable is not set or empty", TOPIC_ARN_ENV));
            throw new RuntimeException(String.format("%s environment variable not set", TOPIC_ARN_ENV));
        }
        return topicArn;
    }

    public String getTopicArn() {
        return topicArn;
    }

    public String getAwsRegion() {
        return awsRegion;
    }

    @Override
    public String toString() {
        return super.toString() +
                ", topicArn=" + topicArn +
                ", awsRegion=" + awsRegion;
    }
}