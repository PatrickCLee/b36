package tw.org.iii.brad.brad36;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileOutputStream;

public class CameraActivity extends AppCompatActivity {
    private Camera camera;
    private CameraPreview cameraPreview;
    private FrameLayout container;
    private File sdroot;
    private SensorManager sensorManager;
    private Sensor sensor;
    private MyListener myListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        myListener = new MyListener();

        camera = getCameraInstance();
//        camera.getParameters().set

        container = findViewById(R.id.preview);
        cameraPreview = new CameraPreview(this, camera);
        container.addView(cameraPreview,0); //二參數為深度
//        camera.setDisplayOrientation(90);       //讓相機預覽畫面正常呈現,預設是倒一邊

        sdroot = Environment.getExternalStorageDirectory();

//        int r = getWindowManager().getDefaultDisplay().getRotation();
//        Log.v("brad","r = " + r);       //按下at5後可以在此看到r所代表的旋轉值
//        if (r == 0){
//            camera.setDisplayOrientation(90);   //讓相機預覽畫面正常呈現,預設是倒一邊
//        }else if(r == 1){
//            camera.setDisplayOrientation(0);    //開啟螢幕旋轉時,要按照旋轉的方向去調整角度
//        }else{
//            camera.setDisplayOrientation(180);
//        }

        sensorManager.registerListener(myListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    private class MyListener implements SensorEventListener{

        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            float v = values[0];
            Log.v("brad"," o = " + v);
            try{
                camera.setDisplayOrientation((int)v);
            }catch (Exception e){

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }


    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
        sensorManager.unregisterListener(myListener);
    }

    private void releaseCamera(){
        if (camera != null){
            camera.release();        // release the camera for other applications
            camera = null;
        }
    }
    public void tackPic(View view) {
        camera.takePicture(new Camera.ShutterCallback() {
            @Override
            public void onShutter() {
                Log.v("brad","onShutter");
            }
        }, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Log.v("brad","debug1");
            }
        }, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Log.v("brad","debug2");
                savePic(data);
            }
        });
    }

    private void savePic(byte[] data){
        Log.v("brad","file : " + data.length);
        try {
            FileOutputStream fout = new FileOutputStream(
                    new File(sdroot, "iii02.jpg"));
            fout.write(data);
            fout.flush();
            fout.close();

            setResult(RESULT_OK);
            finish();
        }catch (Exception e){
            Log.v("brad", e.toString());
        }
    }
}
