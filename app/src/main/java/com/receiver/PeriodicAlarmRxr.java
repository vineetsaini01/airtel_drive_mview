package com.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.newmview.wifi.CapturedPhoneState;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.mView_HealthStatus;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;
import com.google.android.exoplayer2.util.Util;
import com.webservice.Allwebservice;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;
import static com.services.AllServices.asyncTasks;

public class PeriodicAlarmRxr extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        CapturedPhoneState obj = Utils.getCapturedData(context);
        obj.source = "all";
        System.out.println("periodic alarm receiver called "+Utils.getDateTime());
        SharedPreferences prefs = MviewApplication.ctx.getSharedPreferences(Constants.service_toggle, MODE_PRIVATE);
        Utils.stopAsyncTasks();
        if (asyncTasks != null && asyncTasks.size() > 0)
            asyncTasks.clear();
        if (prefs != null) {
            boolean service_tg = prefs.getBoolean("service_key", false);
            if (service_tg) { Allwebservice.API_sendPeriodicData(obj);
            } else {
                AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                int pendingFlags;if (Util.SDK_INT >= 23) {
                    pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
                } else {
                    pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT;
                }
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, pendingFlags);
                Calendar updateTime = Calendar.getInstance();
                updateTime.set(Calendar.SECOND, mView_HealthStatus.periodicFrequencyForAllServices);
                alarmMgr.cancel(pendingIntent);
            }
        }


    }
}

