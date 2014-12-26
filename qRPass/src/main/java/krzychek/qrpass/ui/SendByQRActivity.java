package krzychek.qrpass.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.Window;

import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.io.IOException;

import krzychek.qrpass.R;
import krzychek.qrpass.dataUtils.EncryptUtil;
import krzychek.qrpass.dataUtils.connectionServices.SendViaPost;

public class SendByQRActivity extends Activity implements Camera.PreviewCallback, Camera.AutoFocusCallback, SurfaceHolder.Callback {
    public static final String STR_DATA = "STR_DATA";
    private Camera camera;
    private ImageScanner scanner;
    private Handler handler;
    private boolean processing = false;
    private boolean previewing = false;
    private SurfaceView cameraSurface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set up basic activity spec
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.qr_scan_activity);
        scanner = new ImageScanner();
        cameraInitialize();
        camera.setPreviewCallback(this);

        // start auto-focus handler
        handler = new Handler();
    }

    private void cameraInitialize() {
        camera = Camera.open();
        camera.setDisplayOrientation(90);
        cameraSurface = (SurfaceView) findViewById(R.id.CameraPreview);
        SurfaceHolder previewHolder = cameraSurface.getHolder();
        previewHolder.addCallback(this);
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

    private void startPreview() {
        if (!previewing) {
            previewing = true;
            // Set preview surface size
            Camera.Size size = camera.getParameters().getPreviewSize();
            double ratio = size.width/(double) size.height;
            ViewGroup.LayoutParams layoutParams = cameraSurface.getLayoutParams();
            layoutParams.height = (int) (cameraSurface.getWidth() * ratio);
            cameraSurface.setLayoutParams(layoutParams);
            camera.startPreview();
            camera.autoFocus(this);
        }
    }

    // Preview callback method
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

    // Auto focus
    private Runnable doFocus = new Runnable() {
        @Override
        public void run() {
            if(!isFinishing())
                camera.autoFocus(SendByQRActivity.this);
        }
    };

    public void onAutoFocus(boolean success, Camera camera) {
        handler.postDelayed(doFocus, 1000);
    }

    // Surface holder methods
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(cameraSurface.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
        startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
