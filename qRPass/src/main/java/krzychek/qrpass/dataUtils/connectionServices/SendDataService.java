package krzychek.qrpass.dataUtils.connectionServices;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import krzychek.qrpass.R;

public class SendDataService extends IntentService {
    static final public String DATA = "data";
    static final public String ID = "id";

    public SendDataService() {
        super("SendViaPost");
    }

    public void showToast(final String text, final int toastLenght) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text, toastLenght).show();
            }
        });
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String url = String.format("http://%s/pipe",
                getResources().getString(R.string.serverURL));
        String id = intent.getStringExtra(ID);
        String data = intent.getStringExtra(DATA);
        try {
            ConnectionSemaphore.getInstance().acquire();
            // set up connection
            HttpPut httpPut = new HttpPut(url);
            httpPut.setHeader("User-Agent", "Android_QRPass");
            // set request body
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("qrpass_id", id));
            params.add(new BasicNameValuePair("qrpass_data", data));
            httpPut.setEntity(new UrlEncodedFormEntity(params));
            // execute request
            HttpClient client = new DefaultHttpClient();
            client.execute(httpPut);
            showToast("Data send successfully", Toast.LENGTH_SHORT);
        } catch (Exception e) {
            showToast("Sending data failed", Toast.LENGTH_SHORT);
            e.printStackTrace();
        } finally {
            ConnectionSemaphore.getInstance().release();
        }
    }
}
