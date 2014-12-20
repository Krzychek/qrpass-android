package krzychek.qrpass.dataUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

final public class JSONStringBuilder {
    JSONArray jsonCredentials = new JSONArray();

	public JSONStringBuilder() {};

    public void addPassEntry(Map<String,String> entry) {
        JSONObject json = new JSONObject();
        try {
            for (Map.Entry<String, String> row: entry.entrySet()) {
                json.put(row.getKey(),row.getValue());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonCredentials.put(json);
    }

    public String getJSONString() {
		JSONObject json = new JSONObject();
        try {
            json.put("credentials", jsonCredentials);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
	}
}
