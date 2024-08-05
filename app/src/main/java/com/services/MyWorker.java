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
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.ForegroundInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.functionapps.mview_sdk2.main.Mview;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.database.DB_handler;
import com.newmview.wifi.helper.RequestResponse;
import com.newmview.wifi.other.Utils;

public class MyWorker extends Worker {

    private NotificationManager notificationManager;
    private static final String CHANNEL_ID = "1";
    private static final String CHANNEL_NAME = "Voda app";

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        MviewApplication.ctx=context;
        notificationManager = (NotificationManager)
                context.getSystemService(NOTIFICATION_SERVICE);
    }

    @NonNull
    @Override
    public Result doWork() {

        try {

            Utils.appendLog("ELOG_DO_WORK_IMUP: do work of IMUP called to send imup request ");

//            Context context = getApplicationContext();

            String progress = "Connecting server";
            setForegroundAsync(createForegroundInfo(progress));
            try {
                RequestResponse.sendImupRequest();
//                send_report_to_server();
//                cancelNotification(MviewApplication.ctx,1);

            }
            catch (Exception e)
            {
                Utils.appendLog("ELOG_IMUP_WORKER_EXCEPTION: there is exception while sending imup "+e.toString());
            }

            return Result.success();
        }
        catch (Exception e)
        {

            Utils.appendLog("ELOG_IMUP_WORKER_EXCEPTION: MyWorker Work execution failed at IMUP"+ e);
            return Result.failure();
        }

    }
    private void send_report_to_server() {

        DB_handler db_helper = new DB_handler(MviewApplication.ctx);
        db_helper.open();
        Log.d("TAG", "send_report_to_server: Called");

        db_helper.send_evt_to_server();

        db_helper.close();
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
                .setSmallIcon(android.R.color.transparent)
                // Set the notification to be ongoing, which helps to ensure it's not dismissed
                .setOngoing(false)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                // Add the cancel action to the notification which can
                // be used to cancel the worker
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

    private void cancelNotification(Context context, int notificationId) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(notificationId);
    }

}
