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

public class App {
	private static AppConfig config;
	private static Logger logger;

	private static DynamoDbClient client = DynamoDbClient.builder().build();
	private static Builder request;

	static {
		logger = Logger.getLogger("pulse-jump-analyzer");
		config = AppConfig.getConfig(logger);
		loggerSetUp();
	}

	private static void loggerSetUp() {
		LogManager.getLogManager().reset();
		Handler handler = new ConsoleHandler();
		logger.setLevel(config.getLoggerLevel());
		handler.setLevel(Level.FINEST);
		logger.addHandler(handler);
	}

	public void handleRequest(DynamodbEvent event, Context context) {
		logger.config(String.format("Environment Variables: %s", config.toString()));
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
		logger.info(getLogMessage(map));
		String patientId = map.get(config.getPatientIdAttribute()).getN();
		Integer currentValue = Integer.parseInt(map.get(config.getValueAttribute()).getN());
		String timestamp = map.get(config.getTimestampAttribute()).getN();

		Integer lastValue = getLastValue(patientId, currentValue);

		if (isJump(currentValue, lastValue)) {
			jumpProcessing(patientId, currentValue, lastValue, timestamp);
		}
		request = PutItemRequest.builder().tableName(config.getLastValuesTableName());
		putItemToDb(getItemMap(patientId, currentValue));
	}

	private Integer getLastValue(String patientId, Integer currentValue) {
		Integer lastValue = currentValue;
		try {
			GetItemRequest request = getItemRequest(patientId);
			Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> map = client.getItem(request)
					.item();
			if (map != null) {
				lastValue = Integer.parseInt(map.get(config.getValueAttribute()).n());
			}
		} catch (Exception e) {
			logger.severe(String.format("Error retrieving last value from DB '%s': %s",
					config.getLastValuesTableName(), e.getMessage()));
		}
		return lastValue;
	}

	private GetItemRequest getItemRequest(String patientId) {
		Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> key = Map.of(
				config.getPatientIdAttribute(),
				software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder().n(patientId).build());

		return GetItemRequest.builder().tableName(config.getLastValuesTableName()).key(key).build();
	}

	private Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> getItemMap(String patientId,
			Integer currentValue) {
		return getItemMap(patientId, currentValue, null, null);
	}

	private Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> getItemMap(String patientId,
			Integer currentValue, Integer previousValue, String timestamp) {
		Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> map = new HashMap<>();

		map.put(config.getPatientIdAttribute(),
				software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder()
						.n(patientId).build());
		if (previousValue != null) {
			map.put(config.getPreviousValueAttribute(),
					software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder()
							.n(previousValue.toString()).build());
		}
		;
		map.put(getValueAttributeName(),
				software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder()
						.n(currentValue.toString()).build());
		if (timestamp != null) {
			map.put(config.getTimestampAttribute(),
					software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder()
							.n(timestamp).build());
		}

		return map;
	}

	private String getValueAttributeName() {
		return request.build().tableName().equals(config.getLastValuesTableName()) ? config.getValueAttribute()
				: config.getCurrentValueAttribute();
	}

	private void putItemToDb(Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> map) {
		try {
			client.putItem(request.item(map).build());
		} catch (Exception e) {
			logger.severe(
					String.format("Error putting item to DB '%s': %s", request.build().tableName(), e.getMessage()));
		}
	}

	private void jumpProcessing(String patientId, Integer currentValue, Integer lastValue, String timestamp) {
		request = PutItemRequest.builder().tableName(config.getJumpValuesTableName());
		putItemToDb(getItemMap(patientId, currentValue, lastValue, timestamp));
		logger.info(String.format("Jump: patientId is %s, lastValue is %d, currentValue is %d, timestamp is %s",
				patientId, lastValue, currentValue, timestamp));
	}

	private boolean isJump(Integer currentValue, Integer lastValue) {
		return (float) Math.abs(currentValue - lastValue) / lastValue > config.getJumpFactor();
	}

	private String getLogMessage(Map<String, AttributeValue> map) {
		return String.format("patientId: %s, value: %s", map.get(config.getPatientIdAttribute()).getN(),
				map.get(config.getValueAttribute()).getN());
	}

}