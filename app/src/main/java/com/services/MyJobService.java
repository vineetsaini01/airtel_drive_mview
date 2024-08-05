package com.services;


import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.functionapps.mview_sdk2.main.Mview;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.database.DB_handler;
import com.newmview.wifi.other.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MyJobService
{
    public static final String TAG_MY_IMUPWORK = "imup";
    public static final String TAG_MY_CTSWORK = "work";
    public static final String TAG_ONE_TIME_WORK = "one_time_work";



    public static final String TAG_MY_EVTWORK = "evtctswork";


    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void scheduleWork() {


        try {
            Utils.appendLog("ELOG_IMUP_WORKER_START: Imup schedular called");

            PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(MyWorker.class, 15, TimeUnit.MINUTES)
                    .build();

//            WorkManager.getInstance().enqueue(workRequest);
            WorkManager.getInstance().enqueueUniquePeriodicWork(TAG_MY_IMUPWORK, ExistingPeriodicWorkPolicy.KEEP, workRequest);


            WorkManager.getInstance(MviewApplication.ctx)
                    .getWorkInfoByIdLiveData(workRequest.getId())
                    .observeForever(new Observer<WorkInfo>()  {
                        @Override
                        public void onChanged(WorkInfo workInfo) {
                            if (workInfo != null) {
                                Utils.appendLog("ELOG_IMUP_ONCHANGED WorkInfo of IMUP: " + workInfo.toString());

                            }
                        }
                    });

        }
        catch (Exception e)
        {
            Utils.appendLog("ELOG_IMUP_WORKER_EXCEPTION: Exception in imup schedular: "+e.getMessage());
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void scheduleSendingtoserver() {


        try {
            Utils.appendLog("EVT_SEND_SERVER: Send to server schedular Instance called");
            androidx.work.Constraints constraints = new Constraints.Builder()
                    .setRequiresDeviceIdle(true) // or setRequiresBatteryNotLow(true)
                    .build();

            PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(EVT_Schedular.class, 15, TimeUnit.MINUTES)
                    .build();

//            WorkManager.getInstance().enqueue(workRequest);
            WorkManager.getInstance().enqueueUniquePeriodicWork(TAG_MY_EVTWORK, ExistingPeriodicWorkPolicy.KEEP, workRequest);


            WorkManager.getInstance(MviewApplication.ctx)
                    .getWorkInfoByIdLiveData(workRequest.getId())
                    .observeForever(new Observer<WorkInfo>()  {
                        @Override
                        public void onChanged(WorkInfo workInfo) {
                            if (workInfo != null) {
                                Utils.appendLog("ELOG_EVT_ONCHANGED WorkInfo of EVT: " + workInfo.toString());

                            }
                        }
                    });

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void scheduleTest() {

//        DB_handler db_helper = new DB_handler(MviewApplication.ctx);
//        db_helper.open();
//
//        ArrayList<HashMap<String,String>> agents_data_list = new ArrayList<>();
//        agents_data_list = db_helper.getAgentData();
//        Log.d(Mview.TAG,"Size of agents data list is "+agents_data_list.size());
//
//        for (int i = 0; i < agents_data_list.size(); i++) {
//
//            HashMap<String,String>hmap=agents_data_list.get(i);
//            String name=hmap.get("file_name");
//            String period=hmap.get("period");
//            String num_iterations=hmap.get("no_iterations");
//            int no_iter=Integer.parseInt(num_iterations);
//
//            int periodis = 900;
//            try {
//                periodis = Integer.parseInt(period);
//            } catch (Exception e) {
//                e.printStackTrace();
//                Utils.appendLog("exception catched is " + e.toString());
//            }
//
//            Data.Builder data = new Data.Builder();
//            data.putString("name", name);


            Utils.appendLog("ELOG_TEST_WORKER_START: called to start MyTest worker ");
            Log.d("TAG", "scheduleTest: called to start MyTest worker ");
            PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(MyTest.class, 15, TimeUnit.MINUTES)
                    .build();

            WorkManager.getInstance().enqueueUniquePeriodicWork(TAG_MY_CTSWORK, ExistingPeriodicWorkPolicy.KEEP, workRequest);
            //      WorkManager.getInstance().enqueue(workRequest);
            // Observe the work status

            try {
                WorkManager.getInstance(MviewApplication.ctx).getWorkInfoByIdLiveData(workRequest.getId())
                        .observeForever(new Observer<WorkInfo>() {
                            @Override
                            public void onChanged(WorkInfo workInfo) {
                                if (workInfo != null) {
                                    Utils.appendLog("ELOG_TEST_ONCHANGED WorkInfo of TEST: " + workInfo.toString());

                                }
                            }
                        });
            } catch (Exception e) {
                Utils.appendLog("ELOG_TEST_WORKER_EXCEPTION: Exception in test schedular: " + e.getMessage());

                e.printStackTrace();
            }
//        }
//        db_helper.close();
    }

    public static void scheduleOneTimeTest() {
        Utils.appendLog("ELOG_ONE_TIME_WORKER_START: called to start MyTest one-time worker ");
        Log.d("TAG", "scheduleOneTimeTest: called to start MyTest one-time worker ");

        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(MyTest.class)
                .build();

        WorkManager.getInstance().enqueueUniqueWork(TAG_ONE_TIME_WORK, ExistingWorkPolicy.KEEP, oneTimeWorkRequest);

        try {
            WorkManager.getInstance(MviewApplication.ctx).getWorkInfoByIdLiveData(oneTimeWorkRequest.getId())
                    .observeForever(new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(WorkInfo workInfo) {
                            if (workInfo != null) {
                                Utils.appendLog("ELOG_ONE_TIME_ONCHANGED WorkInfo of TEST: " + workInfo.toString());
                            }
                        }
                    });
        } catch (Exception e) {
            Utils.appendLog("ELOG_ONE_TIME_WORKER_EXCEPTION: Exception in one-time test scheduler: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Function to find the thread with the lowest priority
    private static String findLowestPriorityThread(Map<String, Integer> threadPriorities) {
        String lowestPriorityThread = null;
        int lowestPriority = Integer.MAX_VALUE;

        for (Map.Entry<String, Integer> entry : threadPriorities.entrySet()) {
            String threadName = entry.getKey();
            int priority = entry.getValue();
            if (priority < lowestPriority) {
                lowestPriority = priority;
                lowestPriorityThread = threadName;
            }
        }
        Log.d("TAG", "findLowestPriorityThread: "+ lowestPriorityThread);
        return lowestPriorityThread;
    }
}
