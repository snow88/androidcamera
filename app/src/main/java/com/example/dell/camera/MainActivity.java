package com.example.dell.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.dell.camera.views.CameraView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Camera.PictureCallback{
    Button btnpreview, btnphoto;
    FrameLayout flcontainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnphoto = findViewById(R.id.btnphoto);
        btnpreview = findViewById(R.id.btnpreview);          //for enabling/disabling preview
        flcontainer = findViewById(R.id.flcontainer);

        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (cameraPermission == PackageManager.PERMISSION_DENIED || storagePermission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    10);
        }

        else {
            Camera c = Camera.open();
            if (c!= null)
                Log.d("CAMERA: ", "access obtained");
            List<Camera.Size> availsizes = c.getParameters().getSupportedPictureSizes();
            for (Camera.Size cs : availsizes)
                Log.d("pic size: ", cs.width + "x" + cs.height);

            availsizes = c.getParameters().getSupportedVideoSizes();
            for (Camera.Size cs : availsizes)
                Log.d(" vid size: ", cs.width + "x" + cs.height);

            availsizes = c.getParameters().getSupportedPreviewSizes();
            for (Camera.Size cs : availsizes)
                Log.d(" prev size: ", cs.width + "x" + cs.height);

            final CameraView cameraView = new CameraView(this, c, getWindowManager().getDefaultDisplay());

            flcontainer.addView(cameraView);

            btnpreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean prevstate = cameraView.togglepreview();
                    Toast.makeText(MainActivity.this,
                            "prev state: " + (prevstate ? "enabled": "disabled"), Toast.LENGTH_SHORT).show();
                }
            });

            btnphoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cameraView.takephoto(MainActivity.this);
                }
            });
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        File dcmdirec = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File f = new File(dcmdirec, "mypfolder");
        if(!f.exists())
            f.mkdir();
        String pname = "Photo" + System.currentTimeMillis() + ".jpeg";
        File pf = new File(f, pname);
        try {
            FileOutputStream fo = new FileOutputStream(pf);
            fo.write(data);
            Toast.makeText(MainActivity.this, "pic taken", Toast.LENGTH_SHORT).show();
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            camera.startPreview();
        }
    }
}
