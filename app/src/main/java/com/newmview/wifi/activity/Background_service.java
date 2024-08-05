package com.newmview.wifi.activity;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import androidx.annotation.Nullable;

import android.util.Log;

import com.newmview.wifi.AlarmReceiver;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.mView_HealthStatus;
import com.newmview.wifi.other.Constants;

import java.util.Calendar;

/**
 * Created by functionapps on 11/21/2018.
 */

public class Background_service extends Service{
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String autorestart;
    private boolean name;
    private AlarmManager alarmMgr;
    private PendingIntent pendingIntent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    //The system invokes this method to perform one-time setup procedures
    // when the service is initially created (before it calls either
    // onStartCommand() or onBind()). If the service is already running, this method is not called.

    @Override
    public void onCreate() {
        super.onCreate();
        //     Toast.makeText(this, "on create of service", Toast.LENGTH_SHORT).show();
        System.out.println("in on create of autorestart ");


        //setUpAlarm();


    }

//    private void setUpAlarm() {
//        System.out.println("setup alarm in autorestart called");
//        alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//
//
//        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
//        intent.putExtra("alarm", 1);
//      pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
//                intent, 0);
//      Calendar updateTime = Calendar.getInstance();
//
//        updateTime.set(Calendar.SECOND, mView_HealthStatus.periodicRefreshFrequencyInSeconds);
//
//
//
//        sharedPreferences = getSharedPreferences(Constants.BACKGROUND_AUTORESTART, Context.MODE_PRIVATE);
//        editor = sharedPreferences.edit();
//        autorestart = sharedPreferences.getString("auto", "true");
//
//
//
//
//        System.out.println("shared_pref in back for aurtorestart" + autorestart);
//        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(), 1000 * mView_HealthStatus.periodicRefreshFrequencyInSeconds, pendingIntent);
//
//
//
//
//    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);
              //startProcess();

        return START_STICKY;
    }

    private void startProcess() {


        SharedPreferences prefs = MviewApplication.ctx.getSharedPreferences(Constants.service_toggle, MODE_PRIVATE);
        if (prefs != null) {
            boolean service_tg = prefs.getBoolean("service_key", false);
            System.out.println("service key in bg  "+service_tg);
            if(!service_tg) {
//                setUpAlarm();
            }
            else
            {
                if(pendingIntent!=null)
                    alarmMgr.cancel(pendingIntent);
            }

        }



    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();
        //create an intent that you want to start again.
        //  Toast.makeText(this, "on task removed called!!!", Toast.LENGTH_SHORT).show();
      /* Intent intent = new Intent(getApplicationContext(), Background_service.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1000, pendingIntent);
        super.onTaskRemoved(rootIntent);
*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

     /*if(mView_HealthStatus.startbackgroundservice) {
          //if button is checked and service is still not running then restart it
      if (!isMyServiceRunning(Background_service.class,context)) {
              sendBroadcast(new Intent("YouWillNeverKillMe"));
          }
        }*/
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            name=serviceClass.getName().equals(service.service.getClassName());
            if (name) {
                return true;
            }
        }
        //Toast.makeText(this, "service running or not value checking"+name +"name of service.getName   "+serviceClass.getName() , //Toast.LENGTH_SHORT).show();
        return false;
    }
    private boolean isMyServiceRunning(Class<?> serviceClass,Context context) {
        ActivityManager manager = (ActivityManager)context. getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("Service already","running");
                return true;
            }
        }
        Log.i("Service not","running");
        return false;
    }

}

