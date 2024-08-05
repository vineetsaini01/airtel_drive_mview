package com.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.util.Util;
import com.receiver.ImupServiceRxr;
import com.receiver.Wifi;

import java.util.Calendar;

public class WifiService extends IntentService {
    private static final int WIFI_SERVICE_TIMER=120;
    private static final String TAG ="WifiService" ;
    private AlarmManager alarmMgr;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public  WifiService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        setUpAlarm();
    }

    private void setUpAlarm() {
        Log.i(TAG,"Wifi service called...");
        alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), Wifi.class);
        intent.putExtra("alarm", 1);
        int pendingFlags;if (Util.SDK_INT >= 23) {
            pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        } else {
            pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        }
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, pendingFlags);
        // PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0)
       // PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        Calendar updateTime = Calendar.getInstance();
        updateTime.set(Calendar.SECOND, 30);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(),WIFI_SERVICE_TIMER*1000 , pendingIntent);


    }

}
