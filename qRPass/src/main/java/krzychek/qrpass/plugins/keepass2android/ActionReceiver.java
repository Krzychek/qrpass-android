package krzychek.qrpass.plugins.keepass2android;

import keepass2android.pluginsdk.PluginAccessException;

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

	}
}