package krzychek.qrpass.dataUtils.connectionServices;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by krzysiek on 21.12.14.
 */
public class SendViaPost extends IntentService {
    private static final String URL = "http://192.168.229.130/qrpass/functions/android_app.php";
    final public static String DATA = "data";
    final public static String ID = "id";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public SendViaPost() {
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

    ;

    @Override
    protected void onHandleIntent(Intent intent) {
        String id = intent.getStringExtra(ID);
        String data = intent.getStringExtra(DATA);
        try {
            ConnectionSemaphore.getInstance().acquire();
            // set up connection
            HttpPost post = new HttpPost(URL);
            post.setHeader("User-Agent", "Android_QRPass");
            post.setHeader("Accept","*/*");
            // set post data
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("qrpass_id", id));
            params.add(new BasicNameValuePair("qrpass_data", data));
            post.setEntity(new UrlEncodedFormEntity(params));
            // execute POST
            HttpClient client = new DefaultHttpClient();
            client.execute(post);
            showToast("Data send successfully", Toast.LENGTH_SHORT);
        } catch (Exception e) {
            showToast("Sending data failed", Toast.LENGTH_SHORT);
            e.printStackTrace();
        } finally {
            ConnectionSemaphore.getInstance().release();
        }
    }
}