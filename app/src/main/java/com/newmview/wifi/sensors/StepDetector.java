package com.newmview.wifi.sensors;



import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.newmview.wifi.other.Utils;

public class StepDetector implements SensorEventListener {
    private static final String TAG = "StepDetector";
    private final SensorManager sensorManager;
    private final Context context;
    private Sensor sSensor;
    private long steps = 0;
    private StepDetectorListener stepDetectorListener;
    public interface StepDetectorListener {
        void onStepDetected(long steps);
    }
    public StepDetector(Context context)
    {
        this.context=context;
        sensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        sSensor= sensorManager .getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
   /*     if(!HasGotSensorCaps()){
            showToast(context,"Required sensor not supported on this device!");
            return;
        }*/

    }




    public void start() {
        Utils.showToast(context,"Listener Registered..");
        sensorManager.registerListener(this, sSensor,
                SensorManager.SENSOR_DELAY_FASTEST);
    }
    public void setListener(StepDetectorListener stepDetectorListener)
    {
        this.stepDetectorListener=stepDetectorListener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
   //     float[] values = event.values;
       // Utils.showToast(context,"Event detected ");
        if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            steps++;
            Utils.showToast(context,"Step detected "+steps);
            if(stepDetectorListener!=null)
                stepDetectorListener.onStepDetected(steps);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }
    public  long getStepsCount()
    {
        return steps;
    }
}
