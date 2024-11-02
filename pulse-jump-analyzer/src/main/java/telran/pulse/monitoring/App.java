package telran.pulse.monitoring;

import java.util.*;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;

public class App {
	public void handleRequest(DynamodbEvent event, Context context) {
		event.getRecords().forEach(r -> {
			Map<String, AttributeValue> map = r.getDynamodb().getNewImage();
			System.out.printf("patientId=%s, timestamp=%s, pulseValue=%s\n",
					map.get("patientId").getN(), map.get("timestamp").getN(),
					map.get("value").getN());
		});
	}
}
