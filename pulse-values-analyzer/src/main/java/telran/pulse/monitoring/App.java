package telran.pulse.monitoring;

import java.net.URI;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.*;
import java.util.logging.*;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;
import software.amazon.awssdk.services.dynamodb.*;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest.Builder;

import telran.pulse.monitoring.dto.Range;

public class App extends AppShared{
    private static AppConfig config;
    private static Logger logger;

    private static DynamoDbClient client = DynamoDbClient.builder().build();
    private static Builder request;
    private static HttpClient httpClient;

    static {
        logger = Logger.getLogger("pulse-value-analyzer");
        config = AppConfig.getConfig(logger);
        httpClient = HttpClient.newHttpClient();
        logger.config(String.format("Environment Variables: %s", config.toString()));
    }

    public void handleRequest(DynamodbEvent event, Context context) {        
        request = PutItemRequest.builder().tableName(config.getAbnormalValuesTableName());
        event.getRecords().forEach(r -> {
            Map<String, AttributeValue> map = r.getDynamodb().getNewImage();
            if (map == null) {
                logger.warning("No new image found");
            } else if (config.getEventTypeAttribute().equals(r.getEventName())) {
                processPulseValue(map);
            } else {
                logger.warning(String.format("The event isn't %s but %s",
                        config.getEventTypeAttribute(), r.getEventName()));
            }
        });
    }

    private void processPulseValue(Map<String, AttributeValue> map) {
        int value = Integer.parseInt(map.get(config.getValueAttribute()).getN());
        String patientId = map.get(config.getPatientIdAttribute()).getN();
        Range range = getMaxMinThresholdPulseValue(patientId);
        logger.finer(getLogMessage(map));
        processAbnormalValue(map, value, patientId, range);
    }

    private void processAbnormalValue(Map<String, AttributeValue> map, int value, String patientId, Range range) {
        if (range != null && isAbnormalPulseValue(value, range)) {
            putItemToDb(client, request, getPutItemMap(map), logger);
        } else if (range == null) {
            logger.warning("No valid range data available; skipping abnormal check for patientId: " + patientId);
        }
    }

    private boolean isAbnormalPulseValue(int value, Range range) {
        return value > range.max() || value < range.min();
    }

    private Range getMaxMinThresholdPulseValue(String patientId) {
        String uri = String.format("%s?patientId=%s", config.getServerlessRestApiUrl(), patientId);
        logger.info("Fetching threshold values from URL: " + uri);
        return fetchRangeFromURI(uri);
    }

    private Range fetchRangeFromURI(String uri) {
        Range range = null;
        try {            
            HttpRequest request = HttpRequest.newBuilder(new URI(uri)).build();
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

            if (response.statusCode() < 400) { 
                range = Range.fromJSON(response.body());
            } else {
                throw new Exception(String.format("Error fetching range data. Received status %d from range provider is \'%s\'.",
                                response.statusCode(), response.body()));
            }
        } catch (Exception e) {
            logger.severe(String.format("Error getting data from the resource '%s': %s", uri, e.getMessage()));
        }
        return range;
    }

    private Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> getPutItemMap(
            Map<String, AttributeValue> map) {
        Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> res = new HashMap<>();
        res.put(config.getPatientIdAttribute(), buildAttribute(map, config.getPatientIdAttribute()));
        res.put(config.getTimestampAttribute(), buildAttribute(map, config.getTimestampAttribute()));
        res.put(config.getValueAttribute(), buildAttribute(map, config.getValueAttribute()));        
        return res;
    }

    private software.amazon.awssdk.services.dynamodb.model.AttributeValue buildAttribute(
            Map<String, AttributeValue> map, String key) {
        return software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder()
                .n(map.get(key).getN()).build();
    }

    private String getLogMessage(Map<String, AttributeValue> map) {
        return String.format("patientId: %s, value: %s", map.get(config.getPatientIdAttribute()).getN(),
                map.get(config.getValueAttribute()).getN());
    }
}