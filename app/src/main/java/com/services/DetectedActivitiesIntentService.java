package com.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.newmview.wifi.other.Constants;

import java.util.ArrayList;

public class DetectedActivitiesIntentService  extends IntentService {

    protected static final String TAG = DetectedActivitiesIntentService.class.getSimpleName();

    public DetectedActivitiesIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onHandleIntent(Intent intent) {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

        // Get the list of the probable activities associated with the current state of the
        // device. Each activity is associated with a confidence level, which is an int between
        // 0 and 100.
        ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();

       for (DetectedActivity activity : detectedActivities) {
            //Toast.makeText(this,"Detected activity "+activity.getType() +" confidence "+activity.getConfidence(),Toast.LENGTH_LONG).show();
            Log.i(TAG, "Detected activity: " + activity.getType() + ", " + activity.getConfidence());
            broadcastActivity(activity);
        }

        // Get the most probable activity from the list of activities in the update
        DetectedActivity mostProbableActivity = result.getMostProbableActivity();

        // Get the confidence percentage for the most probable activity
        int confidence = mostProbableActivity.getConfidence();

        // Get the type of activity
        int activityType = mostProbableActivity.getType();
      //  mostProbableActivity.getVersionCode();
   //     List<DetectedActivity> detectedActivities = result.getProbableActivities();

        for (DetectedActivity activity : detectedActivities) {
            Log.d(TAG, "Detected activity: " + activity.getType() + ", " + activity.getConfidence());

        }
       // Log.d(TAG, "acitivty: " + getNameFromType(activityType));
        Log.i(TAG, "Detected activity: " + activityType + ", " + mostProbableActivity.getConfidence());
      //  broadcastActivity(mostProbableActivity);
/*
        if (confidence >= 50) {
            String mode = getNameFromType(activityType);

            if (activityType == DetectedActivity.ON_FOOT) {
                DetectedActivity betterActivity = walkingOrRunning(result.getProbableActivities());

                if (null != betterActivity)
                    mode = getNameFromType(betterActivity.getType());
            }

            sendNotification(mode);
        }
*/
    }

    private void broadcastActivity(DetectedActivity activity) {
        Intent intent = new Intent(Constants.BROADCAST_DETECTED_ACTIVITY);
        intent.putExtra("type", activity.getType());
        intent.putExtra("confidence", activity.getConfidence());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}