package com.newmview.wifi;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.net.TrafficStats;
import android.os.BatteryManager;
import android.os.Environment;
import android.util.Log;

import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;
import com.webservice.WebService;

import java.io.File;
import java.io.FileWriter;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;



public class AlarmReceiver extends BroadcastReceiver {

    public static String LOG_TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {


        int a =0;
        a++;
      //  Toast.makeText(context, "reached AlarmReceiver !!!!.",  Toast.LENGTH_LONG).show();
        System.out.println("reached Alarm Receiver");

        try {
            TinyDB db = new TinyDB(context);
            String t1 = db.getString("startbackgroundservice");



            if( t1 == null || t1.equals("")) {
                db.putString("startbackgroundservice","ON");
                mView_HealthStatus.startbackgroundservice = true;
            }else {
                if (t1.equals("On") || t1.equals("ON")) {
                    mView_HealthStatus.startbackgroundservice = true;
                } else
                    mView_HealthStatus.startbackgroundservice = false;
            }

//            if(mView_HealthStatus.startbackgroundservice == false)
//            {
//                AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//                Intent intent1 = new Intent(context, AlarmReceiver.class);
//
////                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
//
//                Calendar updateTime = Calendar.getInstance();
//
//                updateTime.set(Calendar.SECOND, mView_HealthStatus.periodicRefreshFrequencyInSeconds);
//
////                alarmMgr.cancel(pendingIntent);
//
//            }
//            Toast.makeText(context, "AlarmReceiver!!!!.", Toast.LENGTH_LONG).show();
           // if( intent.hasExtra("alarm"))
            {
                boolean DEBUGGABLE = (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
/*
                if(DEBUGGABLE) {
                    String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                    File file = new File(path, "mview");
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    file = new File(path, "mview" + "/" + "alarm.txt");
                    FileWriter out;
                    try {
                        out = new FileWriter(file, true);
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                        Date resultdate = new Date(System.currentTimeMillis());
                        String displaydate = sdf.format(resultdate);
                        out.append("alarm called " + displaydate + "\n");
                        if(mView_HealthStatus.startbackgroundservice == false)
                            out.append("alarm disabled now " + "\n");
                        out.flush();
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
*/

                CapturedPhoneState obj = new CapturedPhoneState();
                obj.basicPhoneState = new CapturedPhoneState().new BasicPhoneState();
                Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

                float bat = ((float)level / (float)scale) * 100.0f;
                obj.basicPhoneState.captureTime = Utils.getDateTime();
                obj.basicPhoneState.batterylevel = bat +"";
                obj.basicPhoneState.hourMin=Utils.getCurrentHourMin();

                long currentMobileTxBytes = TrafficStats.getMobileTxBytes();
                long currentMobileRxBytes = TrafficStats.getMobileRxBytes();
                long totalTxBytes = TrafficStats.getTotalTxBytes();
                long totalRxBytes = TrafficStats.getTotalRxBytes();
                double l = currentMobileTxBytes + currentMobileRxBytes;
                double mobileDataInMB = (l)/(1024*1024);
                double wifiDataInMB = ((totalTxBytes + totalRxBytes)/(1024*1024)) - mobileDataInMB;

                obj.basicPhoneState.wifiDataUsed = wifiDataInMB + "";
                obj.basicPhoneState.simDataUsed = mobileDataInMB + "";

                if(MyPhoneStateListener.lastSignalStrength != null) {
                    String s = MyPhoneStateListener.getSignalStrength(MyPhoneStateListener.lastSignalStrength);
                    obj.signalStrength = s;
                }

                if(MyPhoneStateListener.lastServiceState != null)
                    obj.roaming = MyPhoneStateListener.lastServiceState.getRoaming();

                if(MyPhoneStateListener.lastCellLocation != null) {
                    obj.cellLocation = MyPhoneStateListener.lastCellLocation.toString();
                    obj.cellLocationObj = MyPhoneStateListener.lastCellLocation;
                }

                obj.signalStrengthObj = MyPhoneStateListener.lastSignalStrength;
                if(listenService.telMgr != null) {
                    int networkType = listenService.telMgr.getNetworkType();
                    mView_HealthStatus.strCurrentNetworkState = MyPhoneStateListener.getNetworkClass(networkType,true);
                }

                obj.networkType = mView_HealthStatus.iCurrentNetworkState +"G";


                if( mView_HealthStatus.timeSeriesCapturedData == null)
                    mView_HealthStatus.timeSeriesCapturedData = new ArrayList<CapturedPhoneState.BasicPhoneState>();

                int currSize = mView_HealthStatus.timeSeriesCapturedData.size();

                if(currSize  == mView_HealthStatus.MaxPeriodicDataToSaveInDB && currSize >= 1)
                {
                    mView_HealthStatus.timeSeriesCapturedData.remove(0);
                }
                mView_HealthStatus.timeSeriesCapturedData.add(obj.basicPhoneState);

                //TinyDB tinydb = new TinyDB(context);

                db.putListObject("periodicData", mView_HealthStatus.timeSeriesCapturedData);
                /*for(int i=0;i<mView_HealthStatus.timeSeriesCapturedData.size();i++)
                {
                    Toast.makeText(context, "periodic data:  "+ mView_HealthStatus.timeSeriesCapturedData.get(i), Toast.LENGTH_SHORT).show();

                }*/
                FileWriter out;

    if(DEBUGGABLE) {

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        File file = new File(path, "mview");
        if (!file.exists()) {
            file.mkdirs();
        }
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
        if (prefs != null) {
            boolean service_tg = prefs.getBoolean("service_key", false);
            System.out.println("service key in alarm  " + service_tg);
            if (!service_tg) {
                System.out.println("service key periodc called from alarm");
                WebService.API_sendPeriodicData(obj);
            }
            else {
//                 AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//                 PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
//
//                Calendar updateTime = Calendar.getInstance();
//
//                updateTime.set(Calendar.SECOND, mView_HealthStatus.periodicRefreshFrequencyInSeconds);
//
//                alarmMgr.cancel(pendingIntent);
            }
        }

            System.out.println("Alarm Receiver called");

       /* } else {
            out.append("alarms's response not yet recived..so wait");
      //      Toast.makeText(context, "alarm recver's response yet not yes ", Toast.LENGTH_SHORT).show();
        }*/
    }




                //send failed records to server which could not be sent
                System.out.println("call api called from 2");
              //  new WebService.Async_SendPendingCallRecordsToServer().execute(db);


            }


            // TextView callState = (TextView) findViewById(R.id.callState);
            //callState.setText("Call State is: " + state + " " + number + " " + cause);


           // Log.e(LOG_TAG, "inside AlarmReceiver " + " has alarm = " + intent.hasExtra("alarm"));
        } catch (Exception e) {
    //     Toast.makeText(context, "AlarmReceiver Exception!!!!." + e.getMessage(),  Toast.LENGTH_LONG).show();
            e.printStackTrace();
            Log.e(LOG_TAG, e.toString());
        }
    }


    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }

}
