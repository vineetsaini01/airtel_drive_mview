package com.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.util.Util;
import com.newmview.wifi.AlarmManagerBroadcastReceiver;
import com.newmview.wifi.CapturedPhoneState;
import com.newmview.wifi.MyPhoneStateListener;
import com.newmview.wifi.TinyDB;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.listenService;
import com.newmview.wifi.mView_HealthStatus;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;
import com.receiver.PeriodicAlarmRxr;
import com.webservice.Allwebservice;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.newmview.wifi.mView_HealthStatus.firstTriggerForAllServices;

public class AllServices extends Service {
   // int time=1;
    private AlarmManager alarmMgr;
    private PendingIntent pendingIntent;
    private TelephonyManager telephonyManager;
    private MyPhoneStateListener myPhoneStateListener;
    public static List<AsyncTask<Object, Void, String>> asyncTasks = new ArrayList<AsyncTask<Object, Void, String>>();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        myPhoneStateListener = new MyPhoneStateListener(AllServices.this,telephonyManager, Constants.readPhoneStatePermissionDenied);
        telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        System.out.println("in on create of all service");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Constants.service_started = false;
        System.out.println("in on start of allservices");
        setUpAlarm();
        setUpAlarmForRoundRobinService();
        telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        //startCallingAllServices();
        return START_STICKY;
    }
    private void setUpAlarmForRoundRobinService() {
        System.out.println("setup alarm in autoRestart called");
       // Utils.appendLog(" setUpAlarmForRoundRobinService() ");
        alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        System.out.println(" alarmMgr is"+ alarmMgr);
        Intent intent = new Intent(getApplicationContext(), AlarmManagerBroadcastReceiver.class);
        intent.putExtra("alarm", 1);
        int pendingFlags;
        if (Util.SDK_INT >= 23) {
            pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        } else {
            pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        }
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, pendingFlags);
        System.out.println("  pendingIntent is"+  pendingIntent);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 5, pendingIntent);
        //Toast.makeText(context.getApplicationContext(),"Wake upseconds",Toast.LENGTH_SHORT).show();

    }
    private void setUpAlarm() {
        alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), PeriodicAlarmRxr.class);
        intent.putExtra("alarm", 1);
        int pendingFlags;

        if (Util.SDK_INT >= 23) {
            pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        } else {
            pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        }
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, pendingFlags);
//       /* Calendar updateTime = Calendar.getInstance();
//        System.out.println("update time before setting seconds.."+updateTime.getTime());
//        updateTime.set(Calendar.SECOND, mView_HealthStatus.periodicFrequencyForAllService);
//        System.out.println("update time after setting seconds.."+updateTime.getTime());*/
      // alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(), 1000 * mView_HealthStatus.periodicFrequencyForAllServices, pendingIntent);
// Increasd  time by 30 miutes done by swapnil BANSAL - ON 29/09/2021
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + firstTriggerForAllServices, 1000 * mView_HealthStatus.newPeriodicFrequencyForAllService, pendingIntent);
//        Toast.makeText(context.getApplicationContext(),"Wake upseconds",Toast.LENGTH_SHORT).show();

    }


    private void startCallingAllServices() {
        try {
            TinyDB db = new TinyDB(MviewApplication.ctx);
            Utils.getCapturedData(this);
            CapturedPhoneState obj = new CapturedPhoneState();
            obj.basicPhoneState = new CapturedPhoneState().new BasicPhoneState();
            Intent batteryIntent = MviewApplication.ctx.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float bat = ((float) level / (float) scale) * 100.0f;
            obj.basicPhoneState.captureTime = Utils.getDateTime();
            obj.basicPhoneState.batterylevel = bat + "";
            obj.basicPhoneState.hourMin = Utils.getCurrentHourMin();
            long currentMobileTxBytes = TrafficStats.getMobileTxBytes();
            long currentMobileRxBytes = TrafficStats.getMobileRxBytes();
            long totalTxBytes = TrafficStats.getTotalTxBytes();
            long totalRxBytes = TrafficStats.getTotalRxBytes();
            double l = currentMobileTxBytes + currentMobileRxBytes;
            double mobileDataInMB = (l) / (1024 * 1024);
            double wifiDataInMB = ((totalTxBytes + totalRxBytes) / (1024 * 1024)) - mobileDataInMB;
            obj.basicPhoneState.wifiDataUsed = wifiDataInMB + "";
            obj.basicPhoneState.simDataUsed = mobileDataInMB + "";

            if (MyPhoneStateListener.lastSignalStrength != null) {
                String s = MyPhoneStateListener.getSignalStrength(MyPhoneStateListener.lastSignalStrength);
                obj.signalStrength = s;
            }

            if (MyPhoneStateListener.lastServiceState != null)
                obj.roaming = MyPhoneStateListener.lastServiceState.getRoaming();

            if (MyPhoneStateListener.lastCellLocation != null) {
                obj.cellLocation = MyPhoneStateListener.lastCellLocation.toString();
                obj.cellLocationObj = MyPhoneStateListener.lastCellLocation;
            }

            obj.signalStrengthObj = MyPhoneStateListener.lastSignalStrength;
            if (listenService.telMgr != null) {
                int networkType = listenService.telMgr.getNetworkType();
                mView_HealthStatus.strCurrentNetworkState = MyPhoneStateListener.getNetworkClass(networkType,true);
            }

            obj.networkType = mView_HealthStatus.iCurrentNetworkState + "G";


            if (mView_HealthStatus.timeSeriesCapturedData == null)
                mView_HealthStatus.timeSeriesCapturedData = new ArrayList<CapturedPhoneState.BasicPhoneState>();

            int currSize = mView_HealthStatus.timeSeriesCapturedData.size();

            if (currSize == mView_HealthStatus.MaxPeriodicDataToSaveInDB && currSize >= 1) {
                mView_HealthStatus.timeSeriesCapturedData.remove(0);
            }
            mView_HealthStatus.timeSeriesCapturedData.add(obj.basicPhoneState);


            db.putListObject("periodicData", mView_HealthStatus.timeSeriesCapturedData);
            FileWriter out;
            boolean DEBUGGABLE = (MviewApplication.ctx.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
            if (DEBUGGABLE) {
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                File file = new File(path, "mview");
                if (!file.exists()) { file.mkdirs(); }
                file = new File(path, "mview" + "/" + "alarm.txt");
                out = new FileWriter(file, true);
                // if (Constants.PERIODIC_API_RESPONSE) {
                Constants.PERIODIC_API_RESPONSE = false;
                try {

                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    Date resultdate = new Date(System.currentTimeMillis());
                    String displaydate = sdf.format(resultdate);
                    out.append("alarm called " + Utils.getDateTime() + "\n");
                    if (mView_HealthStatus.startbackgroundservice == false)
                        out.append("alarm disabled now " + "\n");
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                SharedPreferences prefs = MviewApplication.ctx.getSharedPreferences(Constants.service_toggle, MODE_PRIVATE);
                boolean service_tg = prefs.getBoolean("service_key", false);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        obj.source = "all";
                        System.out.println("service key periodc called from all services");

                        Allwebservice.API_sendPeriodicData(obj);
                    }
                }, 60000);


                //   }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        Constants.service_started = true;
        SharedPreferences.Editor editor = getSharedPreferences(Constants.service_toggle, MODE_PRIVATE).edit();
        SharedPreferences prefs = getSharedPreferences(Constants.service_toggle, MODE_PRIVATE);
        System.out.println("in on destroy of allservices");
        System.out.println("key is " + prefs.getBoolean("service_key", false));
        if (prefs.getBoolean("service_key", false)) {
            // if (!Utils.isMyServiceRunning(AllServices.class)) {
            System.out.println("in on call restart");
            //  sendBroadcast(new Intent("YouWillNeverKillMe"));
            Intent intent = new Intent(getApplicationContext(), AllServices.class);
            int pendingFlags;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
            } else {
                pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT;
            }
            PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, pendingFlags);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1000, pendingIntent);

            //  }

        } else if (mView_HealthStatus.mySpeedTest != null) {
            if (mView_HealthStatus.mySpeedTest.uploadtest != null) {
                if (Utils.checkifavailable(mView_HealthStatus.mySpeedTest.uploadtest.source)) {
                    mView_HealthStatus.mySpeedTest.uploadtest.source = null;
                }
            }
            if (mView_HealthStatus.mySpeedTest.downloadtest != null) {
                if (Utils.checkifavailable(mView_HealthStatus.mySpeedTest.downloadtest.source))
                    mView_HealthStatus.mySpeedTest.downloadtest.source = null;
            }
        }
       /* else
        {
            try {
                unregisterReceiver(new RestartReceiver());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }*/

        //if button is checked and service is still not running then restart it


    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        System.out.println("in on task removed of all services");
        Constants.service_started = true;
        Intent intent = new Intent(getApplicationContext(), AllServices.class);
        int pendingFlags;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        } else {
            pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        }
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, pendingFlags);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1000, pendingIntent);


    }//    super.onTaskRemoved(rootIntent);


}

