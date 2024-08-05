package com.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;


import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.database.DB_handler;
import com.newmview.wifi.helper.RequestResponse;
import com.newmview.wifi.other.Config;

import java.util.ArrayList;
import java.util.HashMap;

public class Wifi extends BroadcastReceiver {
    private static final String TAG ="WifiService" ;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"entering selectingLoggingEvents method ");
        selectingLoggingEvents();
       // selectingLoggingEventsNewJson();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void selectingLoggingEvents()
    {
        ArrayList<HashMap<String,String>> getCompleteList=new ArrayList<>();
        DB_handler db_handler = new DB_handler(MviewApplication.ctx);
        db_handler.open();
        getCompleteList=db_handler.selectInLoggingTable();
        System.out.println(" complete list is "+getCompleteList);
        if(getCompleteList!=null)
        {
            try {
                ArrayList<String> list_count = null;
                String evt_type = null;
                for (int j = 0; j < getCompleteList.size(); ++j) {
                    HashMap<String, String> hashMap = (HashMap) getCompleteList.get(j);
                    evt_type = (String) hashMap.get("evt_type");
                    System.out.println(" Evt_type in logs model is " + evt_type);
                    String agent_output = (String) hashMap.get("agent_output");
                    System.out.println(" agent_output is logs model is " + agent_output);
                    list_count = new ArrayList<String>();
                    //list_count.add((String) hashMap.get("agent_output"));
                    // System.out.println(" Agent output  in logs model is " +list_count);
                    String date_time = (String) hashMap.get("date_time");
                    System.out.println(" Date time is is " + date_time);
                    String status1 = (String) hashMap.get("status");


                }
//                if (evt_type!=null) {
//
//                   RequestResponse.send_logging_events(evt_type, getCompleteList, Config.getDateTime(), MviewApplication.ctx);
//                 //   RequestResponse.send_logging_events_New_Json(evt_type, getCompleteList, Config.getDateTime(), MviewApplication.ctx);
//                    db_handler.updateLoggingData("completed");
//                }
                System.out.println(" Agent output  in logs model is is " + list_count);

            }
            catch (SecurityException e) {
                e.printStackTrace();
            }
        }


    }
    



}
