/**
 * 
 */
package krzychek.qrpass.plugins.keepass2android;

import android.util.Log;

import java.util.ArrayList;

import keepass2android.pluginsdk.PluginAccessBroadcastReceiver;
import keepass2android.pluginsdk.Strings;

public class AccessReceiver extends PluginAccessBroadcastReceiver {

	@Override
	public ArrayList<String> getScopes() {
		Log.e("qrpass", "method getScopes in AccessReceiver class");
		ArrayList<String> scopes = new ArrayList<>();
		scopes.add(Strings.SCOPE_CURRENT_ENTRY);
		scopes.add(Strings.SCOPE_QUERY_CREDENTIALS);
		return scopes;
	}
}