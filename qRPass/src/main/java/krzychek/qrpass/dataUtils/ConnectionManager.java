package krzychek.qrpass.dataUtils;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public final class ConnectionManager {

    private static final String URL = "http://192.168.229.130/qrpass/functions/android_app.php";

	private ConnectionManager() {}
	
	public static void sendViaPost(final String id,final String data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("ConnectionManager", "sendViaPost thread started");
                try {
                    // set up connection
                    HttpPost post = new HttpPost(URL);
                    post.setHeader("User-Agent", "Android_QRPass");
                    post.setHeader("Accept","*/*");
                    // set post data
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("qrpass_id", id));
                    params.add(new BasicNameValuePair("qrpass_data", data));
                    post.setEntity(new UrlEncodedFormEntity(params));
                    // execute POST
                    HttpClient client = new DefaultHttpClient();
                    client.execute(post);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
	}
}