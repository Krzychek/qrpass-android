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
            String body = "qrpass_id=" + URLEncoder.encode(id, "UTF-8")
                    + "&qrpass_data=" + URLEncoder.encode(data, "UTF-8");
            URL url = new URL(String.format("https://%s/pipe",
                    getResources().getString(R.string.serverURL)));
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("PUT");
            DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
            out.writeBytes(body);
            int rCode = urlConnection.getResponseCode();
            if (rCode == 200) {
                showToast("Data send successfully", Toast.LENGTH_SHORT);
            } else {
                throw new IOException("Expecting response code 200, got: " + rCode);
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
