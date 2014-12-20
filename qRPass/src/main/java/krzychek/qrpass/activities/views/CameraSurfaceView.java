package krzychek.qrpass.activities.views;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by krzysiek on 20.12.14.
 */
public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private Camera camera;

    public CameraSurfaceView(Context context, Camera camera) {
        super(context);
        this.camera = camera;
        camera.setDisplayOrientation(90);

        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        //noinspection deprecation
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            Log.d("CameraSurfaceView", "Error setting camera preview: " + e.getMessage());
        }


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}
}
