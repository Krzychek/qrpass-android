package krzychek.qrpass;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class SendCreds extends Activity {
	private String username;
	private String password;
	private String entryName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_creds);
		
		// get intent informations
		Intent intent = getIntent();
		username = intent.getStringExtra("username");
		password = intent.getStringExtra("password");
		entryName = intent.getStringExtra("entryName");

		Log.i("QRPass", password);

	}
}
