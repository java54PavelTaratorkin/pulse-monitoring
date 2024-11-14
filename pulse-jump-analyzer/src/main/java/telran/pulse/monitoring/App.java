package telran.pulse.monitoring;

import java.util.*;
import java.util.logging.*;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;
import software.amazon.awssdk.services.dynamodb.*;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest.Builder;
import static telran.pulse.monitoring.AppConfigDefaults.*;
import static telran.pulse.monitoring.config.AppConfigDefaultsShared.LOGGER_NAME_ENV;

public class App {
    private final DynamoDbClient client;
    private Builder request;
    private final AppConfig config;
    private final AppShared appShared;
    private final Logger logger;
    private final String patientIdAttribute;
    private final String valueAttribute;
    private final String timestampAttribute;
    private final String lastValuesTableName;
    private final String previousValueAttribute;
    private final String currentValueAttribute;
    private final String jumpValuesTableName;

	public App() {
		String loggerName = System.getenv().getOrDefault(LOGGER_NAME_ENV, DEFAULT_LOGGER_NAME);
		this.config = new AppConfig(loggerName);
		this.logger = config.getLogger();
		this.patientIdAttribute = config.getPatientIdAttribute();
		this.valueAttribute = config.getValueAttribute();
		this.timestampAttribute = config.getTimestampAttribute();
		this.lastValuesTableName = config.getLastValuesTableName();
		this.previousValueAttribute = config.getPreviousValueAttribute();
		this.currentValueAttribute = config.getCurrentValueAttribute();
		this.jumpValuesTableName = config.getJumpValuesTableName();
		logger.config(String.format("Environment Variables: %s", config.toString()));
		this.appShared = new AppShared(logger, config.getEventTypeAttribute());
		this.client = DynamoDbClient.builder().build();
	}

	public void handleRequest(DynamodbEvent event, Context context) {
		appShared.processEvent(event, this::processPulseValue);
	}

	private void processPulseValue(Map<String, AttributeValue> map) {
		logger.info(getLogMessage(map));
		String patientId = map.get(patientIdAttribute).getN();
		Integer currentValue = Integer.parseInt(map.get(valueAttribute).getN());
		String timestamp = map.get(timestampAttribute).getN();

		Integer lastValue = getLastValue(patientId, currentValue);

		if (isJump(currentValue, lastValue)) {
			jumpProcessing(patientId, currentValue, lastValue, timestamp);
		}
		request = PutItemRequest.builder().tableName(lastValuesTableName);
		appShared.putItemToDb(client, request, getItemMap(patientId, currentValue));
	}

	private Integer getLastValue(String patientId, Integer currentValue) {
		Integer lastValue = currentValue;
		try {
			GetItemRequest request = getItemRequest(patientId);
			Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> map = client.getItem(request)
					.item();
			if (map != null) {
				lastValue = Integer.parseInt(map.get(valueAttribute).n());
			}
		} catch (Exception e) {
			logger.severe(String.format("Error retrieving last value from DB '%s': %s",
					lastValuesTableName, e.getMessage()));
		}
		return lastValue;
	}

	private GetItemRequest getItemRequest(String patientId) {
		Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> key = Map.of(
				patientIdAttribute,
				software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder().n(patientId).build());

		return GetItemRequest.builder().tableName(lastValuesTableName).key(key).build();
	}

	private Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> getItemMap(String patientId,
			Integer currentValue) {
		return getItemMap(patientId, currentValue, null, null);
	}

	private Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> getItemMap(String patientId,
			Integer currentValue, Integer previousValue, String timestamp) {
		Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> map = new HashMap<>();

		map.put(patientIdAttribute,
				software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder()
						.n(patientId).build());
		if (previousValue != null) {
			map.put(previousValueAttribute,
					software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder()
							.n(previousValue.toString()).build());
		}
		map.put(getValueAttributeName(),
				software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder()
						.n(currentValue.toString()).build());
		if (timestamp != null) {
			map.put(timestampAttribute,
					software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder()
							.n(timestamp).build());
		}

		return map;
	}

	private String getValueAttributeName() {
		return request.build().tableName().equals(lastValuesTableName) ? valueAttribute	: currentValueAttribute;
	}

	private void jumpProcessing(String patientId, Integer currentValue, Integer lastValue, String timestamp) {
		request = PutItemRequest.builder().tableName(jumpValuesTableName);
		appShared.putItemToDb(client, request, getItemMap(patientId, currentValue, lastValue, timestamp));
		logger.info(String.format("Jump: patientId is %s, lastValue is %d, currentValue is %d, timestamp is %s",
				patientId, lastValue, currentValue, timestamp));
	}

	private boolean isJump(Integer currentValue, Integer lastValue) {
		return (float) Math.abs(currentValue - lastValue) / lastValue > config.getJumpFactor();
	}

	private String getLogMessage(Map<String, AttributeValue> map) {
		return String.format("patientId: %s, value: %s", map.get(patientIdAttribute).getN(),
				map.get(valueAttribute).getN());
	}
}