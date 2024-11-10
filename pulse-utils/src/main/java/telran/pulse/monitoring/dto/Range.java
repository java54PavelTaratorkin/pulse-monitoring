package telran.pulse.monitoring.dto;

import org.json.*;

public record Range(int min, int max) {

    public static Range fromJSON(String jsonString) {
        JSONObject json = new JSONObject(jsonString);
        if (!json.has("max") || !json.has("min")) {
            throw new JSONException("Missing 'max' or 'min' field in JSON response.");
        }
        return new Range(json.getInt("min"), json.getInt("max"));
    }

    public String toJSON() {
        JSONObject json = new JSONObject();
        json.put("min", min);
        json.put("max", max);
        return json.toString();
    }
}