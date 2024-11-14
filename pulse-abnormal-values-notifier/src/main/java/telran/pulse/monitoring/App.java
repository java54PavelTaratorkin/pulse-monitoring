package telran.pulse.monitoring;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.logging.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import static telran.pulse.monitoring.AppConfigDefaults.*;
import static telran.pulse.monitoring.config.AppConfigDefaultsShared.LOGGER_NAME_ENV;

public class App {
    private final AppConfig config;
    private final SnsClient snsClient;
    private final Logger logger;
    private final String topicARN;
    private final String patientIdAttribute;
    private final String valueAttribute;
    private final String timestampAttribute;
    private final String awsRegion;

    public App() {
        String loggerName = System.getenv().getOrDefault(LOGGER_NAME_ENV, DEFAULT_LOGGER_NAME);
        this.config = new AppConfig(loggerName);
        this.logger = config.getLogger();
        this.topicARN = config.getTopicArn();
        this.awsRegion = config.getAwsRegion();
        this.patientIdAttribute = config.getPatientIdAttribute();
        this.valueAttribute = config.getValueAttribute();
        this.timestampAttribute = config.getTimestampAttribute();
        this.snsClient = setUpSnsClient();
        logger.config(String.format("Environment Variables: %s", config.toString()));
    }

    public void handleRequest(DynamodbEvent event, Context context) {
        event.getRecords().forEach(r -> {
            Map<String, AttributeValue> image = r.getDynamodb().getNewImage();
            String message = getMessage(image);
            logger.info("Message to publish: " + message);
            publishMessage(message);
        });
    }

    private void publishMessage(String message) {
        PublishRequest request = PublishRequest.builder()
                .message(message)
                .topicArn(topicARN)
                .build();
        snsClient.publish(request);
        logger.info("Published message to SNS topic: " + topicARN);
    }

    private String getMessage(Map<String, AttributeValue> image) {
        return String.format("Patient %s\nAbnormal pulse value %s\nDate-Time %s",
                image.get(patientIdAttribute).getN(),
                image.get(valueAttribute).getN(),
                getDateTime(image.get(timestampAttribute).getN()));
    }

    private LocalDateTime getDateTime(String timestampStr) {
        long timestamp = Long.parseLong(timestampStr);
        Instant instant = Instant.ofEpochMilli(timestamp);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    private SnsClient setUpSnsClient() {
        SnsClient snsClient = SnsClient.builder().region(Region.of(awsRegion)).build();
        logger.info("SNS client setup complete with region: " + awsRegion);
        return snsClient;
    }
}