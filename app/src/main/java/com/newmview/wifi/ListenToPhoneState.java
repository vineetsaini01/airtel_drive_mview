package com.newmview.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.TrafficStats;
import android.os.BatteryManager;
import android.os.Environment;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.newmview.wifi.other.Utils;
import com.webservice.WebService;

import java.io.File;
import java.io.FileWriter;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//import android.telephony.CellInfo;

public class ListenToPhoneState extends BroadcastReceiver {
	
	public static String LOG_TAG = "ListenToPhoneState";
	public static Context mContext;
	TelephonyManager telephonyManager;
	public static MyPhoneStateListener phoneListener;
	@Override
	public void onReceive(Context context, Intent intent) {
	    try {
		//	Toast.makeText(context, "listen to phone state called", Toast.LENGTH_SHORT).show();
			this.mContext = context;
			MyPhoneStateListener.mContext = context;
	       // telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	        /*phoneListener = new MyPhoneStateListener();
	        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

	        telephonyManager.listen(Cell_details_info.MyListener,
	    	        PhoneStateListener.LISTEN_CALL_STATE
	    	        //| PhoneStateListener.LISTEN_CELL_INFO // Requires API 17
	    	        | PhoneStateListener.LISTEN_CELL_LOCATION
	    	        | PhoneStateListener.LISTEN_DATA_ACTIVITY
	    	        | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
	    	        | PhoneStateListener.LISTEN_SERVICE_STATE
	    	        | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
	    	        | PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR
	    	        | PhoneStateListener.LISTEN_MESSAGE_WAITING_INDICATOR);*/
	        String cause = "NOTHING";
	        String msg1 =" NA";
	        if (intent.hasExtra(TelecomManager.EXTRA_CALL_DISCONNECT_CAUSE)) {
                cause = intent.getExtras().getString(TelecomManager.EXTRA_CALL_DISCONNECT_CAUSE);
            }
	        if (intent.hasExtra(TelecomManager.EXTRA_CALL_DISCONNECT_MESSAGE)) {
                msg1 = intent.getExtras().getString(TelecomManager.EXTRA_CALL_DISCONNECT_MESSAGE);
            }

			if( intent.hasExtra("alarm"))
			{

				String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
				File file = new File(path, "mview" );
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
					out.flush();
					out.close();
				}catch(Exception e)
				{

				}
				CapturedPhoneState obj = new CapturedPhoneState();
				obj.basicPhoneState = new CapturedPhoneState().new BasicPhoneState();
				Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
				int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
				int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

				float bat = ((float)level / (float)scale) * 100.0f;
				obj.basicPhoneState.captureTime = Utils.getDateTime();
				obj.basicPhoneState.captureTimeN=System.currentTimeMillis();
				obj.basicPhoneState.batterylevel = bat +"";

				long currentMobileTxBytes = TrafficStats.getMobileTxBytes();
				long currentMobileRxBytes = TrafficStats.getMobileRxBytes();
				long totalTxBytes = TrafficStats.getTotalTxBytes();
				long totalRxBytes = TrafficStats.getTotalRxBytes();
				double l = currentMobileTxBytes + currentMobileRxBytes;
				double mobileDataInMB = (l)/(1024*1024);
				double wifiDataInMB = ((totalTxBytes + totalRxBytes)/(1024*1024)) - mobileDataInMB;

				obj.basicPhoneState.wifiDataUsed = wifiDataInMB + "";
				obj.basicPhoneState.simDataUsed = mobileDataInMB + "";

				String s = MyPhoneStateListener.getSignalStrength(MyPhoneStateListener.lastSignalStrength);
				obj.signalStrength = s;
				obj.roaming = MyPhoneStateListener.lastServiceState.getRoaming();

				obj.cellLocation = MyPhoneStateListener.lastCellLocation.toString();
				obj.cellLocationObj = MyPhoneStateListener.lastCellLocation;

				obj.signalStrengthObj = MyPhoneStateListener.lastSignalStrength;
				int networkType = listenService.telMgr.getNetworkType();
				mView_HealthStatus.strCurrentNetworkState = MyPhoneStateListener.getNetworkClass(networkType,true);

				obj.networkType = mView_HealthStatus.iCurrentNetworkState +"G";


				if( mView_HealthStatus.timeSeriesCapturedData == null)
					mView_HealthStatus.timeSeriesCapturedData = new ArrayList<CapturedPhoneState.BasicPhoneState>();

				int currSize = mView_HealthStatus.timeSeriesCapturedData.size();

				if(currSize  == mView_HealthStatus.MaxPeriodicDataToSaveInDB )
				{
					mView_HealthStatus.timeSeriesCapturedData.remove(0);
				}
				mView_HealthStatus.timeSeriesCapturedData.add(obj.basicPhoneState);

				TinyDB tinydb = new TinyDB(context);

				tinydb.putListObject("periodicData", mView_HealthStatus.timeSeriesCapturedData);

				WebService.API_sendPeriodicData(obj);

				//send failed records to server which could not be sent
				System.out.println("call api called from 1");
			//	new WebService.Async_SendPendingCallRecordsToServer().execute(tinydb);

			}


           // TextView callState = (TextView) findViewById(R.id.callState);
            //callState.setText("Call State is: " + state + " " + number + " " + cause);
            
	        Log.e(LOG_TAG, "inside on receive " + cause + " " + msg1 + " has alarm = " + intent.hasExtra("alarm"));
	        // inside on receive NOTHING  NA has alarm = false
	    } catch (Exception e) {
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
	/*@Override
	public void onDestroy() {
		telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_NONE);
	}*/
	

}