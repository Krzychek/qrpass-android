package krzychek.qrpass.plugins.keepass2android;

import java.util.HashMap;

import keepass2android.pluginsdk.KeepassDefs;
import keepass2android.pluginsdk.PluginAccessException;
import krzychek.qrpass.SendCreds;
import android.content.Intent;

public class ActionReceiver extends
		keepass2android.pluginsdk.PluginActionBroadcastReceiver {
	@Override
	protected void openEntry(OpenEntryAction oe) {
		try {
			// TODO: use R.Strings and R.drawable(0)
			oe.addEntryAction("Use in QRPass", android.R.drawable.ic_menu_directions, null);
		} catch (PluginAccessException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void actionSelected(ActionSelectedAction actionSelected) {
		Intent i = new Intent(actionSelected.getContext(), SendCreds.class);
		
		HashMap<String, String> credentials = actionSelected.getEntryFields();
		i.putExtra("username", credentials.get(KeepassDefs.UrlField));
		i.putExtra("password", credentials.get(KeepassDefs.PasswordField));
		i.putExtra("title", credentials.get(KeepassDefs.TitleField));
		actionSelected.getContext().startActivity(i);
	}
}