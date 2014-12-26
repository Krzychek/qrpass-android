package krzychek.qrpass.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

import krzychek.qrpass.R;
import krzychek.qrpass.dataUtils.JSONStringBuilder;

public class SendActivity extends Activity {

    public void sendTest(View view) {
        // get raw data
        EditText login = (EditText) findViewById(R.id.loginField);
        EditText pass = (EditText) findViewById(R.id.passField);
        // build json
        JSONStringBuilder jsonBuilder = new JSONStringBuilder();
        Map<String, String> entryMap = new HashMap<>();
        entryMap.put("userName", login.getText().toString());
        entryMap.put("password", pass.getText().toString());
        jsonBuilder.addPassEntry(entryMap);
        // start qr sender
        Intent intent = new Intent(this, SendByQRActivity.class);
        intent.putExtra(SendByQRActivity.STR_DATA, jsonBuilder.getJSONString());

        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }
}
