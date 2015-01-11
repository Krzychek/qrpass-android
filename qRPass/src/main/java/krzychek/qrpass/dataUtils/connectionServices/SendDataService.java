package krzychek.qrpass.dataUtils.connectionServices;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import krzychek.qrpass.R;

public class SendDataService extends IntentService {
    static final public String DATA = "data";
    static final public String ID = "id";

    public SendDataService() {
        super("SendViaPost");
    }

    public void showToast(final String text, final int toastLength) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text, toastLength).show();
            }
        });
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String id = intent.getStringExtra(ID);
        String data = intent.getStringExtra(DATA);

        HttpURLConnection urlConnection = null;
        try {
            // start connection
            URL url = new URL(String.format("https://%s/pipe",
                    getResources().getString(R.string.serverURL)));
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("PUT");
            DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
            // send body
            String body = "qrpass_id=" + URLEncoder.encode(id, "UTF-8")
                    + "&qrpass_data=" + URLEncoder.encode(data, "UTF-8");
            out.writeBytes(body);
            // process response
            int rCode = urlConnection.getResponseCode();
            switch (rCode) {
                case 200: case 204:
                    showToast("Data send successfully", Toast.LENGTH_SHORT); break;
                default:
                    throw new IOException("Wrong response code: " + rCode);
            }
        } catch (IOException e) {
            showToast("Sending data failed", Toast.LENGTH_SHORT);
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }
}
