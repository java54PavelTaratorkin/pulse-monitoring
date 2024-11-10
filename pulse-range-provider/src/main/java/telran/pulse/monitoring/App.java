package telran.pulse.monitoring;

import java.util.*;
import java.util.logging.*;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import org.json.JSONObject;
import telran.pulse.monitoring.dto.Range;

public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static AppConfig config;
    private static Logger logger;

    static {
        logger = Logger.getLogger("pulse-range-provider");
        config = AppConfig.getConfig(logger);
        logger.config(String.format("Environment Variables: %s", config.toString()));
    }

    private static final Map<String, Range> ranges = Map.of(
            "1", new Range(60, 150),
            "2", new Range(70, 160),
            "3", new Range(50, 250),
            "4", new Range(50, 250),
            "5", new Range(50, 250));

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", config.getContentType());
        Map<String, String> mapParameters = input.getQueryStringParameters();
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent().withHeaders(headers);

        try {
            if (mapParameters == null || !mapParameters.containsKey("patientId")) {
                throw new IllegalArgumentException("no patientId parameter");
            }
            String patientIdStr = mapParameters.get("patientId");
            Range range = ranges.get(patientIdStr);
            if (range == null) {
                throw new IllegalStateException(patientIdStr + " not found in ranges");
            }
            logger.info(String.format("Retrieved range for patientId %s: %s", patientIdStr, range));
            response
                    .withStatusCode(200)
                    .withBody(range.toJSON());
        } catch (IllegalArgumentException e) {
            logger.warning("Bad request: " + e.getMessage());
            response.withBody(getErrorJSON(e.getMessage())).withStatusCode(400);
        } catch (IllegalStateException e) {
            logger.warning("Data not found: " + e.getMessage());
            response.withBody(getErrorJSON(e.getMessage())).withStatusCode(404);
        }

        return response;
    }

    private String getErrorJSON(String message) {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("error", message);
        logger.fine("Generated error JSON: " + jsonObj.toString());
        return jsonObj.toString();
    }
}