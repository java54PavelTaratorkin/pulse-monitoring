package telran.pulse.monitoring;

import java.util.*;
import java.util.logging.*;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import org.json.JSONObject;
import telran.pulse.monitoring.dto.Range;
import static telran.pulse.monitoring.AppConfigDefaults.*;
import static telran.pulse.monitoring.config.AppConfigDefaultsShared.LOGGER_NAME_ENV;

public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final String JSON_ERROR_KEY = "error";    
    private final AppConfig config;
    private final Logger logger;
    private final String patientIdAttribute;
    private final String contentType;
    private final String contentTypeAttribute;

    private static final Map<String, Range> ranges = Map.of(
            "1", new Range(60, 150),
            "2", new Range(70, 160),
            "3", new Range(50, 250),
            "4", new Range(50, 250),
            "5", new Range(50, 250)
    );

    public App() {
        String loggerName = System.getenv().getOrDefault(LOGGER_NAME_ENV, DEFAULT_LOGGER_NAME);
        this.config = new AppConfig(loggerName);        
        this.logger = config.getLogger();
        this.patientIdAttribute= config.getPatientIdAttribute();
        this.contentType = config.getContentType();
        this.contentTypeAttribute = config.getContentTypeAttribute();
        logger.config(String.format("Environment Variables: %s", config.toString()));
    }
    
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put(contentTypeAttribute, contentType);
        Map<String, String> mapParameters = input.getQueryStringParameters();
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent().withHeaders(headers);

        try {
            if (mapParameters == null || !mapParameters.containsKey(patientIdAttribute)) {
                throw new IllegalArgumentException(String.format("no %s parameter", patientIdAttribute));
            }
            String patientIdStr = mapParameters.get(patientIdAttribute);
            Range range = ranges.get(patientIdStr);
            if (range == null) {
                throw new IllegalStateException(patientIdStr + " not found in ranges");
            }
            logger.info(String.format("Retrieved range for %s \'%s\'': %s", patientIdAttribute, patientIdStr, range));
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
        jsonObj.put(JSON_ERROR_KEY, message);
        logger.fine("Generated error JSON: " + jsonObj.toString());
        return jsonObj.toString();
    }
}