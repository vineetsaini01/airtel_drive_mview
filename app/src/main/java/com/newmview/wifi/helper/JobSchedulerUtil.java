package com.newmview.wifi.helper;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.services.ImupService;
import com.services.WifiService;

public class JobSchedulerUtil {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    // schedule the start of the service every 10 - 30 seconds
    public static void scheduleImupJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, ImupService.class);
        ComponentName serviceComponent1 = new ComponentName(context, WifiService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        JobInfo.Builder builder1 = new JobInfo.Builder(0, serviceComponent1);
        builder.setMinimumLatency(10000); // wait at least
        builder.setOverrideDeadline(10 * 1000); // maximum delay
        builder1.setMinimumLatency(10000); // wait at least
        builder1.setOverrideDeadline(10 * 1000);
        //builder.setPeriodic()
        //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
        //builder.setRequiresDeviceIdle(true); // device should be idle
        //builder.setRequiresCharging(false); // we don't care if the device is charging or not
        JobScheduler jobScheduler = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            jobScheduler = context.getSystemService(JobScheduler.class);
            jobScheduler.schedule(builder.build());
            jobScheduler.schedule(builder1.build());
        }

    }
}
