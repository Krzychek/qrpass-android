package krzychek.qrpass.plugins.keepass2android;

import android.content.Context;
import android.content.Intent;

import java.util.HashMap;
import java.util.Map;

import keepass2android.pluginsdk.PluginAccessException;
import krzychek.qrpass.dataUtils.JSONStringBuilder;
import krzychek.qrpass.ui.SendByQRActivity;

public class ActionReceiver extends
		keepass2android.pluginsdk.PluginActionBroadcastReceiver {

    @Override
    protected void openEntry(OpenEntryAction oe) {

		try {
			oe.addEntryAction("Use in QRPass", android.R.drawable.ic_menu_directions, null);
		} catch (PluginAccessException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void actionSelected(ActionSelectedAction selectedAction) {
        HashMap<String, String> data = selectedAction.getEntryFields();
        // get raw data
        String login = data.get("UserName");
        String password = data.get("Password");
        // build json
        JSONStringBuilder jsonBuilder = new JSONStringBuilder();
        Map<String, String> entryMap = new HashMap<>();
        entryMap.put("login", login);
        entryMap.put("password", password);
        jsonBuilder.addPassEntry(entryMap);
        // start qr sender
        Context context = selectedAction.getContext();
        Intent intent = new Intent(context, SendByQRActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(SendByQRActivity.STR_DATA, jsonBuilder.getJSONString());

        context.startActivity(intent);


    }
}