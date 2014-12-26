package krzychek.qrpass.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import krzychek.qrpass.R;
import krzychek.qrpass.dataUtils.EncryptUtil;
import krzychek.qrpass.dataUtils.connectionServices.SendViaPost;
import krzychek.qrpass.ui.views.CameraSurfaceView;

public class SendByQRActivity extends Activity {
    public static final String STR_DATA = "STR_DATA";
    private Camera camera;
    private ImageScanner scanner;
    private CameraSurfaceView cameraSurface;
    private Handler handler;
    private boolean processing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set up basic activity spec
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.qr_scan_activity);
        processing = false;
        // initialize scanner
        scanner = new ImageScanner();
        // initialize camera and preview
        camera = Camera.open();
        cameraSurface = new CameraSurfaceView(this, camera);
        FrameLayout cameraPreview = (FrameLayout) findViewById(R.id.CameraPreview);
        cameraPreview.addView(cameraSurface);
        // set camera callback for scanning
        camera.setPreviewCallback(getPreviewCb());
        camera.startPreview();
        camera.autoFocus(autoFocusCB);
        // start auto-focus handler
        handler = new Handler();
        cameraSurface.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                camera.autoFocus(autoFocusCB);
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        releaseCamera();
        handler.removeCallbacks(doFocus);
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
            if(!isFinishing())
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
                if(!processing) {
                    processing = true;
                    Camera.Parameters parameters = camera.getParameters();
                    Camera.Size size = parameters.getPreviewSize();

                    Image image = new Image(size.width, size.height, "Y800");
                    image.setData(imageData);

                    if (scanner.scanImage(image) != 0) {
                        SymbolSet symbolSet = scanner.getResults();
                        for (Symbol sym : symbolSet) {
                            String[] qrData = sym.getData().split("\n", 5);
                            if (qrData.length == 4) {
                                // get data from QRCode
                                String id = qrData[0];
                                String salt = qrData[1];
                                String iv = qrData[2];
                                String passPhrase = qrData[3];
                                // encrypt input data
                                String inData = getIntent().getStringExtra(STR_DATA);
                                EncryptUtil encryptUtil = new EncryptUtil(passPhrase, salt, iv);
                                String outData = encryptUtil.encrypt(inData);
                                // start service
                                Intent intent = new Intent(getApplicationContext(), SendViaPost.class);
                                intent.putExtra(SendViaPost.ID, id);
                                intent.putExtra(SendViaPost.DATA, outData);
                                startService(intent);
                                // destroy activity
                                finish();
                                return;
                            }
                        }
                    }
                    processing = false;
                }
            }
        };
    }
}
