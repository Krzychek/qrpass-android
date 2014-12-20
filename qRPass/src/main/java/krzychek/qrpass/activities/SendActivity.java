package krzychek.qrpass.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

import krzychek.qrpass.R;
import krzychek.qrpass.dataUtils.JSONStringBuilder;

/**
 * Created by krzysiek on 20.12.14.
 */
public class SendActivity extends Activity {

    public void sendTest(View view) {
        // get raw data
        EditText login = (EditText) findViewById(R.id.loginField);
        EditText pass = (EditText) findViewById(R.id.passField);
        // build json
        JSONStringBuilder jsonBuilder = new JSONStringBuilder();
        Map<String, String> entryMap = new HashMap<>();
        entryMap.put("login", login.getText().toString());
        entryMap.put("password", pass.getText().toString());
        jsonBuilder.addPassEntry(entryMap);
        // start qr sender
        Intent intent = new Intent(this, SendByQRActivity.class);
        intent.putExtra(SendByQRActivity.JSON_DATA, jsonBuilder.getJSONString());

        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }
}
