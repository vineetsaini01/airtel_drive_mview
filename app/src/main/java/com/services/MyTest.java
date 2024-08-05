package com.services;


import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION;
import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.ForegroundInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.functionapps.mview_sdk2.main.Mview;
import com.newmview.wifi.AppLauncher;
import com.newmview.wifi.PhoneCallHelper;
import com.mview.airtel.R;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.fragment.CtsTest;
import com.newmview.wifi.fragment.TraceroutFragment;
import com.newmview.wifi.helper.DialogClass;
import com.newmview.wifi.other.Utils;

import java.util.Locale;

public class MyTest extends Worker {
    private static final String TAG = " "+MyTest.class.getSimpleName().toUpperCase(Locale.ROOT)+" ";
    private NotificationManager notificationManager;
    private static final String CHANNEL_ID = "1";
    private static final String CHANNEL_NAME = "Voda app";
    public MyTest(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        MviewApplication.ctx=context;
        notificationManager = (NotificationManager)
                context.getSystemService(NOTIFICATION_SERVICE);
    }

    @NonNull
    @Override
    public Result doWork() {

        String name = getInputData().getString("name");

        Utils.appendLog("ELOG_DO_WORK_TEST: do work called of MYTest Worker to start test ");
        String progress = "Performing test";
        try {
            Log.d(TAG,"Calling start process from MYTest WorkManager");
            setForegroundAsync(createForegroundInfo(progress));
            new PhoneCallHelper(MviewApplication.ctx).volteCallTest();
            new PhoneCallHelper(MviewApplication.ctx).ussdTest();
            new PhoneCallHelper(MviewApplication.ctx).smsTest();

            new AppLauncher(MviewApplication.ctx).appOpenTest();

            CtsTest.startBigFtpTest(MviewApplication.ctx);
            CtsTest.startFTPTest(MviewApplication.ctx);
            new CtsTest().startUrlBlock();
            CtsTest.startPingTest("Ping");
            CtsTest.startTraceroute("Traceroute");
            CtsTest.startWebTest(MviewApplication.ctx);
            CtsTest.startSpeedTest();
            CtsTest.startVideoTest();
            new CtsTest().startLongWebTest(MviewApplication.ctx);

        }
        catch (Exception e)
        {
            Utils.appendLog(TAG +" Exception occured while Starting tests from MYTEST worker.."+" "+e.getMessage());
            e.printStackTrace();

        }
        return Result.success();

    }
    @NonNull
    private ForegroundInfo createForegroundInfo(@NonNull String progress) {
        // Build a notification...
//        Notification notification = NotificationHelper.createNotification(Mview.fapps_ctx,"Thanks app","App is running!");
        // This PendingIntent can be used to cancel the worker
        PendingIntent intent = WorkManager.getInstance(MviewApplication.ctx)
                .createCancelPendingIntent(getId());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
        Notification notification = new NotificationCompat.Builder(MviewApplication.ctx, "1")
                .setContentTitle("Airtel app")
                .setTicker("Airtel app")
                .setSmallIcon(R.drawable.airtel_icon)
                .setOngoing(true)
                // Add the cancel action to the notification which can
                // be used to cancel the worker
                .addAction(android.R.drawable.ic_delete, "Cancel", intent)
                .build();

        return new ForegroundInfo(1, notification,
                FOREGROUND_SERVICE_TYPE_LOCATION | FOREGROUND_SERVICE_TYPE_PHONE_CALL);
    }

    private void createChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager =
                    (NotificationManager) MviewApplication.ctx.getSystemService(Context.NOTIFICATION_SERVICE);

            // Create the NotificationChannel with unique ID, name, and importance level
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the channel's settings if necessary (e.g., sound, vibration, etc.)

            // Register the channel with the system
            notificationManager.createNotificationChannel(channel);
        }
    }
}
