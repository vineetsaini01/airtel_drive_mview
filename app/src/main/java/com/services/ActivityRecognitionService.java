package com.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.internal.ApiKey;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.SleepSegmentRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.newmview.wifi.other.Constants;

public class ActivityRecognitionService extends Service {
    private static final String TAG = ActivityRecognitionService.class.getSimpleName();

    private Intent mIntentService;
    private PendingIntent mPendingIntent;
    private ActivityRecognitionClient mActivityRecognitionClient;

    IBinder mBinder = new ActivityRecognitionService.LocalBinder();

    public class LocalBinder extends Binder {
        public ActivityRecognitionService getServerInstance() {
            return ActivityRecognitionService.this;
        }
    }

    public ActivityRecognitionService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
      //  Utils.showToast(this,"Start service");
        // by swapnil bansal 26/12/2022
        mActivityRecognitionClient = new ActivityRecognitionClient() {
            @NonNull
            @Override
            public ApiKey<Api.ApiOptions.NoOptions> getApiKey() {
                return null;
            }

            @NonNull
            @Override
            public Task<Void> requestActivityUpdates(long l, @NonNull PendingIntent pendingIntent) {
                return null;
            }

            @NonNull
            @Override
            public Task<Void> removeActivityUpdates(@NonNull PendingIntent pendingIntent) {
                return null;
            }

            @NonNull
            @Override
            public Task<Void> requestActivityTransitionUpdates(@NonNull ActivityTransitionRequest activityTransitionRequest, @NonNull PendingIntent pendingIntent) {
                return null;
            }

            @NonNull
            @Override
            public Task<Void> removeActivityTransitionUpdates(@NonNull PendingIntent pendingIntent) {
                return null;
            }

            @NonNull
            @Override
            public Task<Void> requestSleepSegmentUpdates(@NonNull PendingIntent pendingIntent, @NonNull SleepSegmentRequest sleepSegmentRequest) {
                return null;
            }

            @NonNull
            @Override
            public Task<Void> removeSleepSegmentUpdates(@NonNull PendingIntent pendingIntent) {
                return null;
            }
        };
        mIntentService = new Intent(this, DetectedActivitiesIntentService.class);
        int pendingFlags;
        if (Util.SDK_INT >= 23) {
            pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        } else {
            pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        }
        mPendingIntent = PendingIntent.getService(this, 1, mIntentService,pendingFlags);
       // mPendingIntent = PendingIntent.getService(this, 1, mIntentService, PendingIntent.FLAG_UPDATE_CURRENT,);
        requestActivityUpdatesButtonHandler();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public void requestActivityUpdatesButtonHandler() {
        Task<Void> task = mActivityRecognitionClient.requestActivityUpdates(
                Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
                mPendingIntent);

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
               /* Toast.makeText(getApplicationContext(),
                        "Successfully requested activity updates",
                        Toast.LENGTH_SHORT)
                        .show();*/
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               /* Toast.makeText(getApplicationContext(),
                        "Requesting activity updates failed to start",
                        Toast.LENGTH_SHORT)
                        .show();*/
            }
        });
       /* ActivityTransitionRequest request = buildTransitionRequest();
        Task<Void> task1 = mActivityRecognitionClient.requestActivityTransitionUpdates(
                request,
                mPendingIntent);
        task1.addOnSuccessListener(
                new OnSuccessListener() {
                    @Override
                    public void onSuccess(Void result) {
                        // Handle success...
                        Toast.makeText(getApplicationContext(),
                                "Successfully requested transition activity updates",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });
        task1.addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // Handle failure...
                        Toast.makeText(getApplicationContext(),
                                "Requesting activity transition updates failed to start",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });*/

    }

    public void removeActivityUpdatesButtonHandler() {
        Task<Void> task = mActivityRecognitionClient.removeActivityUpdates(
                mPendingIntent);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
              /*  Toast.makeText(getApplicationContext(),
                        "Removed activity updates successfully!",
                        Toast.LENGTH_SHORT)
                        .show();*/
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               /* Toast.makeText(getApplicationContext(), "Failed to remove activity updates!",
                        Toast.LENGTH_SHORT).show();*/
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeActivityUpdatesButtonHandler();
    }
}
