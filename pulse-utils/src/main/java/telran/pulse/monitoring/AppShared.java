package telran.pulse.monitoring;

import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

public class AppShared {
    private final Logger logger;
    private final String eventTypeAttribute;

    public AppShared(Logger logger, String eventTypeAttribute) {
        this.logger = logger;
        this.eventTypeAttribute = eventTypeAttribute;
    }
 
    protected void putItemToDb(DynamoDbClient client, PutItemRequest.Builder request,
                            Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> map) {
        logger.info(String.format("Putting item to table %s: %s", request.build().tableName(), map));
        try {
            client.putItem(request.item(map).build());
            logger.info(String.format("Successfully put item to table %s", request.build().tableName()));
        } catch (Exception e) {
            logger.severe(String.format("Error putting item to DB '%s': %s", request.build().tableName(), e.getMessage()));
        }
    }

    protected void processEvent(DynamodbEvent event, Consumer<Map<String, AttributeValue>> processor) {
        event.getRecords().forEach(r -> {
            Map<String, AttributeValue> map = r.getDynamodb().getNewImage();
            if (map == null) {
                logger.warning("No new image found");
            } else if (eventTypeAttribute.equals(r.getEventName())) {
                processor.accept(map);
            } else {
                logger.warning(String.format("The event isn't %s but %s", eventTypeAttribute, r.getEventName()));
            }
        });
    }
}