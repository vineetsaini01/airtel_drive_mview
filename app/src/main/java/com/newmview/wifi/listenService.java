package com.newmview.wifi;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.newmview.wifi.application.MviewApplication;
import com.google.android.exoplayer2.util.Util;
import com.gpsTracker.GPSTracker;
import com.newmview.wifi.activity.MainActivity;
import com.newmview.wifi.database.DB_handler;
import com.newmview.wifi.other.Constants;
import com.receiver.AlarmRxr;

import java.util.Calendar;
import java.util.Random;

public class listenService extends Service {
    private static final int NOTIFICATION_ID = 99;
    public static GPSTracker gps;
    public static TelephonyManager telMgr;
    MyPhoneStateListener myPhoneStateListener = null;
    private TelephonyManager telephonyManager;
    private PendingIntent pendingIntent;
    private AlarmManager alarmMgr;
    private NewPhoneStateListner newPhoneStateListener;

    public TelephonyManager getInstance() {
        telMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        return telMgr;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Toast.makeText(this, "listen service created...", Toast.LENGTH_SHORT).show();
        telMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        gps = new GPSTracker(getApplicationContext());
        mView_HealthStatus.OperatorName = telMgr.getNetworkOperatorName();
        mView_HealthStatus.simOperatorName = telMgr.getSimOperatorName();
        //System.out.println("imsi in service class " + telMgr.getSubscriberId() + "operator " + telMgr.getNetworkOperatorName() + telMgr.getSimOperatorName());

        int cc = telMgr.getPhoneType();
        if (cc == telMgr.PHONE_TYPE_GSM) {
            mView_HealthStatus.phonetype = "GSM";
        } else if (cc == telMgr.PHONE_TYPE_CDMA) {
            mView_HealthStatus.phonetype = "CDMA";
        } else if (cc == telMgr.PHONE_TYPE_NONE) {
        } else {
            mView_HealthStatus.phonetype = "LTE";
        }


        TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = tel.getNetworkOperator();
        if (TextUtils.isEmpty(networkOperator) == false) {
            mView_HealthStatus.mcc = Integer.parseInt(networkOperator.substring(0, 3));
            mView_HealthStatus.mnc = Integer.parseInt(networkOperator.substring(3));
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Constants.readPhoneStatePermissionDenied=true;
        }
        else
        {
            Constants.readPhoneStatePermissionDenied=false;
        }
        // by swapnil 18/08/2022
        TelephonyManager teleMan = (TelephonyManager) MviewApplication.ctx.getSystemService(Context.TELEPHONY_SERVICE);
        if (ContextCompat.checkSelfPermission(MviewApplication.ctx, android.Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            int networkType = teleMan.getNetworkType();
//
            // int networkType = telMgr.getNetworkType();
            mView_HealthStatus.strCurrentNetworkState = MyPhoneStateListener.getNetworkClass(networkType, Constants.readPhoneStatePermissionDenied);
            SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);

        }


    /*MyPhoneStateListener MyListener = new MyPhoneStateListener(listenService.this,telMgr);
        telMgr.listen(MyListener,
    	        PhoneStateListener.LISTEN_CALL_STATE
    	        | PhoneStateListener.LISTEN_CELL_INFO // Requires API 17
    	        | PhoneStateListener.LISTEN_CELL_LOCATION
    	        | PhoneStateListener.LISTEN_DATA_ACTIVITY
    	        | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
    	        | PhoneStateListener.LISTEN_SERVICE_STATE
    	        | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
    	        | PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR
    	        | PhoneStateListener.LISTEN_MESSAGE_WAITING_INDICATOR);*/
    }

    
 /* @Override 
  public void onStart(Intent i, int startid) 
  { 
	  
  } */

    boolean runthread = true;
    int periodicRefreshFrequencyInSeconds = 300;

    private void setUpAlarm() {
        //  Utils.showToast(this,"alarm called in on start of lsiten service");
        alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmRxr.class);
        intent.putExtra("alarm", 1);
        // by swapnil 23/12/2022
        int pendingFlags;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        } else {
            pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        }
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, pendingFlags);
        // PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
       // pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        Calendar updateTime = Calendar.getInstance();
        updateTime.set(Calendar.SECOND, mView_HealthStatus.periodicFrequencyForAllServices);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(), mView_HealthStatus.periodicFrequencyForAllServices * 1000, pendingIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Toast.makeText(this, "listen service started.. and myphonestate listener object is  "+myPhoneStateListener, Toast.LENGTH_SHORT).show();
        //   runAsForeground();
        setUpAlarm();
        System.out.println("in on start of listen service..");
        if (myPhoneStateListener == null) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {

                myPhoneStateListener = new MyPhoneStateListener(listenService.this, telMgr, Constants.readPhoneStatePermissionDenied);
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    telMgr.listen(myPhoneStateListener,
                            PhoneStateListener.LISTEN_CALL_STATE
                                    | PhoneStateListener.LISTEN_CELL_INFO // Requires API 17
                                    | PhoneStateListener.LISTEN_CELL_LOCATION
                                    | PhoneStateListener.LISTEN_DATA_ACTIVITY
                                    | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
                                    | PhoneStateListener.LISTEN_SERVICE_STATE
                                    | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                                    | PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR
                                    | PhoneStateListener.LISTEN_MESSAGE_WAITING_INDICATOR
                                    | PhoneStateListener.LISTEN_ACTIVE_DATA_SUBSCRIPTION_ID_CHANGE);
                } else {
                    telMgr.listen(myPhoneStateListener,
                            PhoneStateListener.LISTEN_CALL_STATE
                                    // Requires API 17
                                    | PhoneStateListener.LISTEN_DATA_ACTIVITY
                                    | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
                                    | PhoneStateListener.LISTEN_SERVICE_STATE
                                    | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                                    | PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR
                                    | PhoneStateListener.LISTEN_MESSAGE_WAITING_INDICATOR
                                    | PhoneStateListener.LISTEN_ACTIVE_DATA_SUBSCRIPTION_ID_CHANGE);
                }
            }
        }


//      TinyDB db = new TinyDB(this);
//      String t1 = db.getString("sendstateinterval");
//      if( t1 == null || t1.equals("")) {
//
//      }else {
//          periodicRefreshFrequencyInSeconds = Integer.parseInt(t1);
//      }

//      Runnable r = new Runnable() {
//          public void run() {
//
//              //for (int i = 0; i < 3; i++) {
//               while(runthread)    {
//                  long endTime = System.currentTimeMillis() + periodicRefreshFrequencyInSeconds * 1000;
//
//                  while (System.currentTimeMillis() < endTime) {
//                      synchronized (this) {
//                          try {
//                              wait(endTime - System.currentTimeMillis());
//                          } catch (Exception e) {
//                          }
//                      }
//
//
//                  }//end internal while
//                   setupAlarm();
//                   Message msg = new Message();
//                   msg.arg1=1;
//                   handler.sendMessage(msg);
//
//              }//end while
//              stopSelf();
//          }
//      };
//
//      Thread t = new Thread(r);
//      t.start();
        return START_STICKY;
    }

//    private void runAsForeground() {
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//        Notification notification = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.google_icon).setContentText("notifyy").setContentIntent(pendingIntent).build();
//        startForeground(NOTIFICATION_ID, notification);
//    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.arg1 == 1) {
                //Print Toast or open dialog
                Toast.makeText(listenService.this, " Service Running",
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

//    public void setupAlarm() {
//        AlarmManager alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(this, AlarmReceiver.class);
//        intent.putExtra("alarm", 1);
//        Random r = new Random();
//        int ID = r.nextInt();
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, ID, intent, 0);
//        //Calendar time = Calendar.getInstance();
////        time.setTimeInMillis(System.currentTimeMillis());
////        // Set Alarm for next 10 seconds
////        time.add(Calendar.SECOND, 30);
//        Calendar updateTime = Calendar.getInstance();
//        updateTime.set(Calendar.SECOND, mView_HealthStatus.periodicRefreshFrequencyInSeconds);
//        alarmMgr.cancel(pendingIntent);
//        alarmMgr.set(AlarmManager.RTC_WAKEUP, 1000, pendingIntent);
//        //alarmMgr.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), pendingIntent);
//    }

    @Override
    public IBinder onBind(Intent arg0) {// TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //  if(mView_HealthStatus.startbackgroundservice) {
        //if button is checked and service is still not running then restart it
        SharedPreferences.Editor editor = getSharedPreferences(Constants.service_toggle, MODE_PRIVATE).edit();
        SharedPreferences prefs = getSharedPreferences(Constants.service_toggle, MODE_PRIVATE);
        System.out.println("in on destroy of allservices");
        System.out.println("listen key is " + prefs.getBoolean("listen_service", false));
        if (prefs.getBoolean("listen_service", false)) {
            System.out.println("listen service restart from on destroy.");
            System.out.println("in on destroy of listen service..");
            sendBroadcast(new Intent("YouWillNeverKillMe"));
        }//   }
        //Toast.makeText(this, "MyService destroyed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        //stopSelf();
        SharedPreferences.Editor editor = getSharedPreferences(Constants.service_toggle, MODE_PRIVATE).edit();
        SharedPreferences prefs = getSharedPreferences(Constants.service_toggle, MODE_PRIVATE);
        System.out.println("listen key is " + prefs.getBoolean("listen_service", false));
        if (prefs.getBoolean("listen_service", false)) {
            System.out.println("listen service restart.");
            Intent restartservice = new Intent(getApplicationContext(), listenService.class);

            int pendingFlags;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
            } else {
                pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT;
            }
            PendingIntent pendingIntent = PendingIntent.getService(this, 1, restartservice, pendingFlags);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1000, pendingIntent);
        } else {
            System.out.println("listen key is " + prefs.getBoolean("listen_service", false) + "stop slef");
            stopSelf();
        }// Toast.makeText(this, "MyService Stopped", Toast.LENGTH_LONG).show();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("Service already", "running");
                return true;
            }
        }
        Log.i("Service not", "running");
        return false;
    }

    @Override
    public void onLowMemory() {
        // Toast.makeText(this, "on low memory", Toast.LENGTH_SHORT).show();
        super.onLowMemory();
    }
}
