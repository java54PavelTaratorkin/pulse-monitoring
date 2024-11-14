package telran.pulse.monitoring;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
import static telran.pulse.monitoring.AppConfigDefaults.*;
import static telran.pulse.monitoring.config.AppConfigDefaultsShared.LOGGER_NAME_ENV;

import telran.pulse.monitoring.dto.Range;

public class App {
    private final DynamoDbClient client;
    private Builder request;
    private final HttpClient httpClient;
    private final AppConfig config;
    private final AppShared appShared;
    private final Logger logger;
    private final String patientIdAttribute;
    private final String valueAttribute;
    private final String timestampAttribute;
    private final String abnormalValuesTableName;
    private final String serverlessRestApiUri;
    private static Map<String, Range> cache = new HashMap<>();

    public App() {
        String loggerName = System.getenv().getOrDefault(LOGGER_NAME_ENV, DEFAULT_LOGGER_NAME);
        this.config = new AppConfig(loggerName);
        this.logger = config.getLogger();
        this.patientIdAttribute = config.getPatientIdAttribute();
        this.valueAttribute = config.getValueAttribute();
        this.timestampAttribute = config.getTimestampAttribute();
        this.abnormalValuesTableName = config.getAbnormalValuesTableName();
        this.serverlessRestApiUri = config.getServerlessRestApiUri();
        this.appShared = new AppShared(logger, config.getEventTypeAttribute());
        this.client = DynamoDbClient.builder().build();
        this.request = PutItemRequest.builder().tableName(abnormalValuesTableName);
        this.httpClient = HttpClient.newHttpClient();
        logger.config(String.format("Environment Variables: %s", config.toString()));
    }

    public void handleRequest(DynamodbEvent event, Context context) {
        appShared.processEvent(event, this::processPulseValue);
    }

    private void processPulseValue(Map<String, AttributeValue> map) {
        int value = Integer.parseInt(map.get(valueAttribute).getN());
        String patientId = map.get(patientIdAttribute).getN();
        Range range = getRange(patientId);
        logger.finer(getLogMessage(map));
        processAbnormalValue(map, value, patientId, range);
    }

    private Range getRange(String patientId) {
        Range range = cache.get(patientId);
        if (range == null) {
            range = getRangeFromProvider(patientId);
            cache.put(patientId, range);
            logger.fine(String.format("Range retrieved from provider and cached for %s: %s", patientIdAttribute,
                    patientId));
        } else {
            logger.finer(String.format("Range retrieved from cache for %s: %s", patientIdAttribute, patientId));
        }
        return range;
    }

    private void processAbnormalValue(Map<String, AttributeValue> map, int value, String patientId, Range range) {
        if (range != null && isAbnormalPulseValue(value, range)) {
            appShared.putItemToDb(client, request, getPutItemMap(map));
        } else if (range == null) {
            logger.warning(String.format("No valid range data available; skipping abnormal check for %s: %s",
                    patientIdAttribute, patientId));
        }
    }

    private boolean isAbnormalPulseValue(int value, Range range) {
        return value > range.max() || value < range.min();
    }

    private Range getRangeFromProvider(String patientId) {
        HttpRequest request = HttpRequest.newBuilder(getURI(patientId)).build();
        HttpResponse<String> response;
        try {
            response = httpClient.send(request, BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            logger.severe(
                    String.format("Error (%s) getting data from the URI '%s': %s", e.getClass().getSimpleName(),
                            request.uri().toString(), e.getMessage()));
            throw new RuntimeException(e);
        }
        String bodyJSON = response.body();
        if (response.statusCode() >= 400) {
            logger.severe(String.format("Received error status (%s) from range provider. Response body: %s",
                    response.statusCode(), bodyJSON));
            throw new RuntimeException(bodyJSON);
        }
        return Range.fromJSON(bodyJSON);
    }

    private URI getURI(String patientId) {
        String uri = String.format("%s?%s=%s", serverlessRestApiUri, patientIdAttribute, patientId);
        logger.info(String.format("Fetching threshold values from URI: %s", uri));
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            String errorMessage = uri + " wrong URI format";
            logger.severe(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    private Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> getPutItemMap(
            Map<String, AttributeValue> map) {
        Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> res = new HashMap<>();
        res.put(patientIdAttribute, buildAttribute(map, patientIdAttribute));
        res.put(timestampAttribute, buildAttribute(map, timestampAttribute));
        res.put(valueAttribute, buildAttribute(map, valueAttribute));
        return res;
    }

    private software.amazon.awssdk.services.dynamodb.model.AttributeValue buildAttribute(
            Map<String, AttributeValue> map, String key) {
        return software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder()
                .n(map.get(key).getN()).build();
    }

    private String getLogMessage(Map<String, AttributeValue> map) {
        return String.format("patientId: %s, value: %s", map.get(patientIdAttribute).getN(),
                map.get(valueAttribute).getN());
    }
}