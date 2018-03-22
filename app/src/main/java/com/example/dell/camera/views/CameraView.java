package com.example.dell.camera.views;

import android.content.Context;
import android.hardware.Camera;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by Dell on 03-02-2018.
 */

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
    Context context;
    Camera camera;
    Display display;
    boolean isprevenable = false;

    public CameraView(Context context, Camera camera, Display display) {
        super(context);
        this.camera = camera;
        this.context = context;
        this.display = display;
        this.getHolder().addCallback(this);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            Camera.Parameters cparam= camera.getParameters();
            cparam.setPreviewSize(1920, 1080);     //to prevent app from crashing, only device-supported preview and picture sizes are used
            cparam.setPictureSize(4160, 3120);
            cparam.setColorEffect(Camera.Parameters.EFFECT_NEGATIVE);      //negative effect added to camera
            cparam.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            camera.setParameters(cparam);
            camera.setPreviewDisplay(this.getHolder());

            switch(display.getRotation()) {
                case Surface.ROTATION_90:
                    camera.setDisplayOrientation(0);
                    break;
                case Surface.ROTATION_180:
                    camera.setDisplayOrientation(270);
                    break;
                case Surface.ROTATION_270:
                    camera.setDisplayOrientation(180);
                    break;
                case Surface.ROTATION_0:
                    camera.setDisplayOrientation(90);
                    break;
            }

            camera.startPreview();
            isprevenable = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        isprevenable = false;
        camera.release();
    }

    public boolean togglepreview() {
        if (isprevenable == false) {
            camera.startPreview();
            isprevenable = true;
        }
        else {
            camera.stopPreview();
            isprevenable = false;
        }
        return isprevenable;
    }

    public void takephoto(Camera.PictureCallback pictureCallback) {
        if (camera!=null && isprevenable)
            camera.takePicture(null, null, pictureCallback);
    }
}
