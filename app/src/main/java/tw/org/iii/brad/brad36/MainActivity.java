package tw.org.iii.brad.brad36;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

public class MainActivity extends AppCompatActivity {
    private Vibrator vibrator;
    private SwitchCompat switchCompat;
    private CameraManager cameraManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init(){
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
}
