package telran.pulse.monitoring;

import java.util.Map;
import java.util.logging.Logger;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

public class AppShared {

    public void putItemToDb(DynamoDbClient client, PutItemRequest.Builder request,
                            Map<String, AttributeValue> map, Logger logger) {
        logger.info(String.format("Putting item to table %s: %s", request.build().tableName(), map));
        try {
            client.putItem(request.item(map).build());
            logger.info(String.format("Successfully put item to table %s", request.build().tableName()));
        } catch (Exception e) {
            logger.severe(String.format("Error putting item to DB '%s': %s", request.build().tableName(), e.getMessage()));
        }
    }
}