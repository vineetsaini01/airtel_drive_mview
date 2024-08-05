/*
package com.services;

import android.app.IntentService;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

public class ActivityRecognitionIntentService extends IntentService {

     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     *//*

    public ActivityRecognitionIntentService(String name) {
        super(name);
    }
    //..
    */
/**
     * Called when a new activity detection update is available.
     *//*

    @Override
    protected void onHandleIntent(Intent intent) {
        //inside ActivityRecognitionIntentService 's onHandleIntent
        Intent broadcastIntent = new Intent();

// Give it the category for all intents sent by the Intent Service
        broadcastIntent.addCategory(ActivityUtils.CATEGORY_LOCATION_SERVICES);
// Set the action and content for the broadcast intent
        broadcastIntent.setAction(ActivityUtils.ACTION_REFRESH_STATUS_LIST);

// Broadcast *locally* to other components in this app
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
        //...
        // If the intent contains an update

        if (ActivityRecognitionResult.hasResult(intent)) {
            // Get the update
            ActivityRecognitionResult result =
                    ActivityRecognitionResult.extractResult(intent);

            DetectedActivity mostProbableActivity
                    = result.getMostProbableActivity();

            // Get the confidence % (probability)
            int confidence = mostProbableActivity.getConfidence();

            // Get the type
            int activityType = mostProbableActivity.getType();
            */
/* types:
             * DetectedActivity.IN_VEHICLE
             * DetectedActivity.ON_BICYCLE
             * DetectedActivity.ON_FOOT
             * DetectedActivity.STILL
             * DetectedActivity.UNKNOWN
             * DetectedActivity.TILTING
             *//*

            // process
        }
    }
}

*/
