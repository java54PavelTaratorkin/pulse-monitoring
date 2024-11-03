package telran.pulse.monitoring;

import java.util.Map;
import java.util.logging.*;

import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.lambda.runtime.*;
import com.amazonaws.services.lambda.runtime.events.*;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;

public class App {
    private Logger logger;
    private DynamoDB dynamo;
    private Table table;
    private AppConfig config;

    public App() {
        this.logger = Logger.getLogger("PulseValuesAnalyzer");
        this.config = AppConfig.getConfig(logger);
        this.dynamo = new DynamoDB(AmazonDynamoDBClientBuilder.defaultClient());
        this.table = dynamo.getTable(config.getTableName());
        setLogger();
        logger.info(config.toString());
    }

    private void setLogger() {
        LogManager.getLogManager().reset();
        logger.setLevel(config.getLoggerLevel());
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(logger.getLevel());
        logger.addHandler(consoleHandler);
    }

    public void handleRequest(DynamodbEvent event, Context context) {
        event.getRecords().forEach(r -> {
            Map<String, AttributeValue> map = r.getDynamodb().getNewImage();
            if ("INSERT".equals(r.getEventName()) && map != null) {
                String patientId = map.get("patientId").getN();
                String timestamp = map.get("timestamp").getN();
                int value = Integer.parseInt(map.get("value").getN());

                logger.finer(String.format("Received: patientId=%s, timestamp=%s, value=%d", patientId, timestamp, value));

                if (value < config.getLowerThreshold() || value > config.getUpperThreshold()) {
                    logger.info(String.format("Abnormal value detected: patientId=%s, value=%d", patientId, value));
                    putAbnormalValue(patientId, timestamp, value);
                }
            }
        });
    }

    private void putAbnormalValue(String patientId, String timestamp, int value) {
        try {
            table.putItem(new Item()
                    .withPrimaryKey("patientId", Long.parseLong(patientId))
                    .withNumber("timestamp", Long.parseLong(timestamp))
                    .withNumber("value", value));
        } catch (Exception e) {
            logger.severe(String.format("Table %s not found: %s", config.getTableName(), e.getMessage()));
        }
    }
}