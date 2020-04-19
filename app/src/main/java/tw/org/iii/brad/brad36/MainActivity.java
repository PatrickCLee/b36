package tw.org.iii.brad.brad36;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;

import java.io.File;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private Vibrator vibrator;
    private SwitchCompat switchCompat;
    private CameraManager cameraManager;
    private File sdroot, downloadDir;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    123);
        } else {
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        init();
    }

    private void init(){
        img = findViewById(R.id.img);
        sdroot = Environment.getExternalStorageDirectory();
        downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        cameraManager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        switchCompat = findViewById(R.id.switchLight);
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        onFlashLight();
                    }else{
                        setOnLight();
                    }
                }else{
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        offFlashLight();
                    }else {
                        setOffLight();
                    }
                }
            }
        });
    }

    private Camera camera;

    private void setOnLight(){      //舊版方式
        camera = Camera.open();     //預設後鏡頭
        Camera.Parameters p = camera.getParameters();
        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(p);
        camera.startPreview();
    }

    private void setOffLight(){
        camera.stopPreview();
        camera.release();
    }

    private void onFlashLight(){
        try {
            cameraManager.setTorchMode("0", true);//分前後鏡頭,0是後鏡頭
        } catch (Exception e) {
            Log.v("brad",e.toString());
        }
    }

    private void offFlashLight(){
        try {
            cameraManager.setTorchMode("0", false);//分前後鏡頭,0是後鏡頭
        } catch (Exception e) {
            Log.v("brad",e.toString());
        }
    }

    public void vibrate(View view) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            vibrator.vibrate(
                    VibrationEffect.createOneShot(1*1000,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        }else{
            vibrator.vibrate(1*1000);
        }
    }


    public void sos(View view) {
        int dot = 200;      // Length of a Morse Code "dot" in milliseconds
        int dash = 500;     // Length of a Morse Code "dash" in milliseconds
        int short_gap = 200;    // Length of Gap Between dots/dashes
        int medium_gap = 500;   // Length of Gap Between Letters
        int long_gap = 1000;    // Length of Gap Between Words
        long[] pattern = {
                0,  // Start immediately
                dot, short_gap, dot, short_gap, dot,    // s
                medium_gap,
                dash, short_gap, dash, short_gap, dash, // o
                medium_gap,
                dot, short_gap, dot, short_gap, dot,    // s
                long_gap
        };
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            vibrator.vibrate(pattern, -1);  // Only perform this pattern one time (-1 means "do not repeat")
        }else{
            vibrator.vibrate(pattern, 0); // The "0" means to repeat the pattern starting at the beginning and -1 means do not stop
        }
    }

    public void at3(View view) {
//        Uri uri = Uri.fromFile(new File(sdroot, "iii01.jpg"));
        Uri uri = FileProvider.getUriForFile(this,
                getPackageName() + ".fileprovider",
                new File(downloadDir, "iii01.jpg"));

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, 123);
    }

    public void at4(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 124);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==123 && resultCode == RESULT_OK){
//            Bitmap bmp = BitmapFactory.decodeFile(sdroot.getAbsolutePath()+"/iii01.jpg");
//            img.setImageBitmap(bmp);

            Uri uri = FileProvider.getUriForFile(this,
                    getPackageName() + ".fileprovider",
                    new File(downloadDir, "iii01.jpg"));
            img.setImageURI(uri);
        }else if(requestCode == 124 && resultCode == RESULT_OK){
            Bundle bundle = data.getExtras();
            Set<String> keys = bundle.keySet();
            for (String key : keys){
                Log.v("brad",key);
                Object obj = bundle.get(key);
                Log.v("brad",obj.getClass().getName());
            }

            Bitmap bmp = (Bitmap)bundle.get("data");
            img.setImageBitmap(bmp);
        }else if(requestCode == 125 && resultCode == RESULT_OK){
            Bitmap bmp = BitmapFactory.decodeFile(sdroot.getAbsolutePath()+"/iii02.jpg");
            img.setImageBitmap(bmp);
        }
    }

    public void at5(View view) {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivityForResult(intent,125);
    }
}
