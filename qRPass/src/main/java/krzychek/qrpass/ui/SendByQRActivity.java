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
import krzychek.qrpass.dataUtils.connectionServices.SendDataService;

public class SendByQRActivity extends Activity implements Camera.PreviewCallback, Camera.AutoFocusCallback, SurfaceHolder.Callback {
    public static final String DATA_TO_SEND = "DATA_TO_SEND";
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
        startCamera();
        // start auto-focus handler
        handler = new Handler();
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseCamera();
        handler.removeCallbacks(doFocus);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCamera();
        if (cameraSurface.isShown()) {
            startPreview();
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
                    String[] qrData = sym.getData().split("\n");
                    if (qrData.length == 2) {
                        // get data from QRCode
                        String id = qrData[0];
                        String key = qrData[1];
                        // encrypt input data
                        String inData = getIntent().getStringExtra(DATA_TO_SEND);
                        EncryptUtil encryptUtil = new EncryptUtil(key);
                        String outData = encryptUtil.encrypt(inData);
                        // start service
                        Intent intent = new Intent(getApplicationContext(), SendDataService.class);
                        intent.putExtra(SendDataService.ID, id);
                        intent.putExtra(SendDataService.DATA, outData);
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

    // Auto focus related
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
        startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}

    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
            previewing = false;
        }
    }

    private void startCamera() {
        if (camera == null) {
            camera = Camera.open();
            camera.setDisplayOrientation(90);
            cameraSurface = (SurfaceView) findViewById(R.id.CameraPreview);
            SurfaceHolder previewHolder = cameraSurface.getHolder();
            previewHolder.addCallback(this);
            camera.setPreviewCallback(this);
        }
    }

    private void startPreview() {
        if (!previewing) {
            previewing = true;
            try {
                camera.setPreviewDisplay(cameraSurface.getHolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
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
}
