package com.example.driverbehaviour;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    Interpreter tflite;
    SensorManager sensorManager, sensorManager1;
    Sensor gyroSensor;
    Sensor accSensor;
    DatabaseHelper1 myDb1;
    DatabaseHelper2 myDb2;
    DatabaseHelper3 myDb3;
    Button StartBtn, StopBtn;
    int t=0;
    int i=0;
    int k=0;
    float[][][] rawdata = new float[1][100][5];
    float output ;
    ArrayList<Float> obj = new ArrayList<Float>();

    //   String time;
    HashMap<String, Object> map1 = new HashMap<>();
    String date;
    SimpleDateFormat simpleDateFormat;
    FirebaseFirestore db1;
    protected SensorEventListener listener;
    private GpsTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb1 = new DatabaseHelper1(this);
        myDb2 = new DatabaseHelper2(this);
        myDb3 = new DatabaseHelper3(this);
        db1 = FirebaseFirestore.getInstance();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager1 = (SensorManager) getSystemService(SENSOR_SERVICE);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroSensor = sensorManager1.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        StartBtn = findViewById(R.id.start_main);
        StopBtn = findViewById(R.id.stop_main);
        try {
            tflite = new Interpreter(loadModelFile());
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long getDatetime() {
        Calendar calendar = Calendar.getInstance();

        long year = calendar.get(Calendar.YEAR);
        long month = calendar.get(Calendar.MONTH) + 1;
        long day = calendar.get(Calendar.DAY_OF_MONTH);
        long calcDate = year * 100 + month;
        calcDate = calcDate * 100 + day;
        long hour = calendar.get(Calendar.HOUR);
        long min = calendar.get(Calendar.MINUTE);
        long sec = calendar.get(Calendar.SECOND);
        long millisec = calendar.get(Calendar.MILLISECOND);
        long calcTime = hour * 10000000 + min * 100000 + sec * 1000 + millisec;
        long date_float = calcDate * 1000000000 + calcTime;
        return date_float;
    }

//    public static String getDatetime1() {
//        Calendar c = Calendar.getInstance();
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mms");
//        String formattedDate = df.format(c.getTime());
//        return formattedDate;
//    }
//
//    public static Float convertToFloat(double doubleValue) {
//        return (float) doubleValue;
//    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void btn_start(View view) {
        Toast.makeText(MainActivity.this,"All Sensors are started",Toast.LENGTH_LONG).show();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, SensorManager.SENSOR_DELAY_GAME);
        } else {
            listener = new SensorEventListener() {
                @Override
                public void onAccuracyChanged(Sensor arg0, int arg1) {
                }

                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {
                    Sensor sensor = sensorEvent.sensor;
                    if (t==30000){
                        i = 0 ;
                        t=0;
                        for (int q = 0; q < 100; q++) {
                            float[] gyrox = new float[100];
                            gyrox[q] = rawdata[0][q][0];
                            float[] gyroy = new float[100];
                            gyrox[q] = rawdata[0][q][1];
                            float[] gyroz = new float[100];
                            gyrox[q] = rawdata[0][q][2];
                            float[] accx = new float[100];
                            gyrox[q] = rawdata[0][q][3];
                            float[] accy = new float[100];
                            gyrox[q] = rawdata[0][q][4];
                            output = doInterference(gyrox,gyroy,gyroz,accx,accy);
                        }
                        obj.add(output);
                        System.out.println("output = "+output);

                    }
                    t++;
                    if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                        long time_float = getDatetime();
//                        time = getDatetime1();
//                        System.out.println("ACC ");
//                        System.out.println(time_float);
                        myDb2.insertData(time_float, sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
                        map1.put("time", time_float);
                        map1.put("accx", sensorEvent.values[0]);
                        map1.put("accy", sensorEvent.values[1]);
                        map1.put("accz", sensorEvent.values[2]);
                        rawdata[0][i][3] = sensorEvent.values[0] ;
                        rawdata[0][i][4] = sensorEvent.values[1] ;
//                        rawdata[0][i][0] = sensorEvent.values[0] ;
//                        rawdata[0][i][1] = sensorEvent.values[1] ;
//                        rawdata[0][i][2] = sensorEvent.values[2] ;
                        update_gps();
                        long current_time = System.currentTimeMillis();
                        String current_time_str = Long.toString(current_time);
                        simpleDateFormat = new SimpleDateFormat("MM dd yyyy ");
                        date = simpleDateFormat.format(Calendar.getInstance().getTime());
                        db1.collection(date + "All").document(current_time_str).set(map1);
                    }

                    if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                        long time_float = getDatetime();
//                        time = getDatetime1();
//                        System.out.println("GYRO ");
//                        System.out.println(time_float);
                        myDb1.insertData(time_float, sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
                        map1.put("gyrox", sensorEvent.values[0]);
                        map1.put("gyroy", sensorEvent.values[1]);
                        map1.put("gyroz", sensorEvent.values[2]);
                        rawdata[0][i][0] = sensorEvent.values[0] ;
                        rawdata[0][i][1] = sensorEvent.values[1] ;
                        rawdata[0][i][2] = sensorEvent.values[2] ;
                        i++;
                    }
                }

            };
            sensorManager.registerListener(listener, accSensor, 4000);
            sensorManager1.registerListener(listener, gyroSensor, 4000);
        }
    }
    public void update_gps(){
        long time_float = getDatetime();
        gpsTracker = new GpsTracker(MainActivity.this);
        if (gpsTracker.canGetLocation()) {
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            double altitude = gpsTracker.getAltitude();
            float speed = gpsTracker.getspeed();
            System.out.println("Your Location: " + "\n" + "Latitude: " + latitude + "\n" + "Longitude: " + longitude + "\n" + "Altitude" + altitude + "\n" + "Speed" + speed);
//            Toast.makeText(MainActivity.this, "Your Location: " + "\n" + "Latitude: " + latitude + "\n" + "Longitude: " + longitude + "\n" + "Altitude" + altitude + "\n" + "Speed" + speed, Toast.LENGTH_SHORT).show();
            map1.put("Altitude", altitude);
            map1.put("Speed", speed);
            map1.put("Latitude", latitude);
            map1.put("Longitude", longitude);
            myDb3.insertData(time_float, latitude, longitude, altitude, speed);
        } else {
            gpsTracker.showSettingsAlert();
        }
        gpsTracker.stopUsingGPS();
    }

    public void btn_stop(View view) {
        sensorManager.unregisterListener(listener, accSensor);
        sensorManager1.unregisterListener(listener, gyroSensor);
        gpsTracker.stopUsingGPS();
        Toast.makeText(MainActivity.this,"All Sensors are stopped",Toast.LENGTH_LONG).show();
//        finish();
//        System.exit(0);
    }
    public float doInterference(float[] gyrox,float[] gyroy,float[] gyroz,float[] accx,float[] accy){
        float[][][] inputVal = new float[1][100][5];
        for (int i = 0; i < 100; i++) {
            inputVal[0][i][0] = gyrox[i];
            inputVal[0][i][1] = gyroy[i];
            inputVal[0][i][2] = gyroz[i];
            inputVal[0][i][3] = accx[i];
            inputVal[0][i][4] = accy[i];
        }
        float[][] outputVal = new float[1][1];
        tflite.run(inputVal,outputVal);
        float outVal = outputVal[0][0];
        return outVal;

    }

    private MappedByteBuffer loadModelFile() throws IOException{
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startoffset = fileDescriptor.getStartOffset();
        long declaredlength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startoffset,declaredlength);

    }
    static boolean isMultipleof200 (int n)
    {
        while (n > 0)
            n = n - 200;

        if (n == 0)
            return true;

        return false;
    }

}