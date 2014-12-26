package krzychek.qrpass.dataUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

final public class JSONStringBuilder {
    static private final String CREDENTIALS = "credentials";
    JSONObject jsonCredentials;

	public JSONStringBuilder() {
        jsonCredentials = new JSONObject();
    };

    public void addPassEntry(Map<String,String> entry) {
        JSONObject json = new JSONObject();
        try {
            for (Map.Entry<String, String> row: entry.entrySet()) {
                json.put(row.getKey(),row.getValue());
            }
            jsonCredentials.put(CREDENTIALS, json);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getJSONString() {
        return jsonCredentials.toString();
	}
}
