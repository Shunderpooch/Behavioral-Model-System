package com.example.arun.tiltdetectionswithsensorevent;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager mSensorManager;
    Sensor mAccelerometer;
    Sensor mMagnometer;
    private TextView xText;
    final float[] mValuesMagnet      = new float[3];
    final float[] mValuesAccel       = new float[3];
    final float[] mValuesOrientation = new float[3];
    final float[] mRotationMatrix    = new float[9];
    //private TextView yText;
    //private TextView zText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnometer= mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);



        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnometer, SensorManager.SENSOR_DELAY_NORMAL);
        xText = (TextView)findViewById(R.id.textView2);
        //yText = (TextView)findViewById(R.id.textView4);
        //zText = (TextView)findViewById(R.id.textView5);

    }


    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        // Handle the events for which we registered
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                System.arraycopy(event.values, 0, mValuesAccel, 0, 3);
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                System.arraycopy(event.values, 0, mValuesMagnet, 0, 3);
                break;
        }

        SensorManager.getRotationMatrix(mRotationMatrix, null, mValuesAccel, mValuesMagnet);
        SensorManager.getOrientation(mRotationMatrix, mValuesOrientation);
        final CharSequence test;
        test = "results: " + mValuesOrientation[0]*180/Math.PI +" "+mValuesOrientation[1]*180/Math.PI+ " "+ mValuesOrientation[2]*180/Math.PI;
        xText.setText(test);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

