package com.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.receiver.PeriodicAlarmRxr;

import java.util.Calendar;

public class Periodic_Background_service extends Service {
    private AlarmManager alarmMgr;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setUpAlarm();
        return START_STICKY;
    }

    private void setUpAlarm() {
        System.out.println("setup alarm in new service called");
        alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), PeriodicAlarmRxr.class);
        intent.putExtra("alarm", 1);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        Calendar updateTime = Calendar.getInstance();
        updateTime.set(Calendar.SECOND, 30);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(), 6000, pendingIntent);


    }

}
