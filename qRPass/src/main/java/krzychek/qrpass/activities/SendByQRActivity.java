package krzychek.qrpass.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;

import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import krzychek.qrpass.activities.views.CameraSurfaceView;
import krzychek.qrpass.dataUtils.ConnectionManager;
import krzychek.qrpass.dataUtils.EncryptUtil;

/**
 * Created by krzysiek on 20.12.14.
 */
public class SendByQRActivity extends Activity {
    public static final String STR_DATA = "STR_DATA";
    private Camera camera;
    private ImageScanner scanner;
    private CameraSurfaceView cameraSurface;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set up basic activity spec
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        camera = Camera.open();
        cameraSurface = new CameraSurfaceView(this, camera);
        setContentView(cameraSurface);

        camera.setPreviewCallback(getPreviewCb());
        camera.startPreview();
        camera.autoFocus(autoFocusCB);

        handler = new Handler();
        cameraSurface.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                camera.autoFocus(autoFocusCB);
            }
        });

        scanner = new ImageScanner();

    }
    
    @Override
    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
    }

    Runnable doFocus = new Runnable() {
        @Override
        public void run() {
            handler.removeCallbacks(this);
            camera.autoFocus(autoFocusCB);
        }
    };

    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() { //TODO getter
        public void onAutoFocus(boolean success, Camera camera) {
            handler.postDelayed(doFocus, 1000);
        }
    };


    private Camera.PreviewCallback getPreviewCb() {
        return new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] imageData, Camera camera) {
                Camera.Parameters parameters = camera.getParameters();
                Camera.Size size = parameters.getPreviewSize();

                Image image = new Image(size.width, size.height, "Y800");
                image.setData(imageData);

                if(scanner.scanImage(image) != 0) {

                    SymbolSet symbolSet = scanner.getResults();
                    for (Symbol sym : symbolSet) {
                        String[] qrData = sym.getData().split("\n",5);
                        if (qrData.length == 4) {
                            String id = qrData[0];
                            String salt = qrData[1];
                            String iv = qrData[2];
                            String passPhrase = qrData[3];

                            Intent intent = getIntent();
                            String data = intent.getStringExtra(STR_DATA);
                            EncryptUtil encryptUtil = new EncryptUtil(passPhrase,salt,iv);
                            ConnectionManager.sendViaPost(id,encryptUtil.encrypt(data));
                        }
                    }
                }
            }
        };
    }
}
