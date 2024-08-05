package com.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.exoplayer2.util.Util;
import com.receiver.ImupServiceRxr;

import java.util.Calendar;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
/*public class ImupService extends JobService {
    private static final String TAG = "ImupService";


    @Override
    public boolean onStartJob(JobParameters params) {

  //    RequestResponse.sendImupRequest();
    //  Log.i(TAG,Utils.getDateTime() +" Sending imup request");
   //     JobSchedulerUtil.scheduleImupJob(getApplicationContext()); // reschedule the job
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }


}*/
public class ImupService extends IntentService
{
    private static final int IMUP_TIMER=120;
    private static final String TAG ="ImupService" ;
    private AlarmManager alarmMgr;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    public ImupService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
//        setUpAlarm();//commented by vikas
       /* Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                RequestResponse.sendImupRequest();
            }
        },10*60*60 );*/
    }
    private void setUpAlarm() {
       Log.i(TAG,"Imup service alarm manager called...");
        alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), ImupServiceRxr.class);
        intent.putExtra("alarm", 1);
        int pendingFlags;
        if (Util.SDK_INT >= 23) {
            pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        } else {
            pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        }
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, pendingFlags);
       // PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        Calendar updateTime = Calendar.getInstance();
        updateTime.set(Calendar.SECOND, 30);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(),IMUP_TIMER*1000 , pendingIntent);


    }

}
