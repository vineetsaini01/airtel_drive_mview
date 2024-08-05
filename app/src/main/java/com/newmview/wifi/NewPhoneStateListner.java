package com.newmview.wifi;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.database.DB_handler;
import com.newmview.wifi.helper.AllInOneAsyncTaskForEVT;
import com.newmview.wifi.helper.Interfaces;
import com.newmview.wifi.helper.RequestResponse;
import com.newmview.wifi.other.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

public class NewPhoneStateListner extends PhoneStateListener {
    private String userType;
    private String senderNumber;
    private String id;
    private Boolean offHookState = false;
    public static String LOG_TAG = "newPhoneStateListener";


    public NewPhoneStateListner(String userType,String senderNumber,String id){
        Utils.appendLog("ELOG_SENDER: NewPhoneStateListner called usertype is "+userType+" sender num is"+senderNumber+" id is"+ id);
        this.userType = userType;
        this.senderNumber = senderNumber;
        this.id = id;

    }
    //usertype,sender number,id
    @Override
    public void onCallStateChanged(int state, String phoneNumber) {
        super.onCallStateChanged(state, phoneNumber);

        Utils.appendLog("ELOG_SENDER: calling incoming num is " + phoneNumber + "call status " + state);
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:

                callStateToServer(state, phoneNumber, Utils.getDateTime());
                Utils.appendLog( "ELOG_SENDER: onCallStateChanged: new CALL_STATE_IDLE");
                break;

            case TelephonyManager.CALL_STATE_RINGING:
                callStateToServer(state, phoneNumber, Utils.getDateTime());
                Utils.appendLog( "ELOG_SENDER: onCallStateChanged: new CALL_STATE_RINGING");
                break;

            case TelephonyManager.CALL_STATE_OFFHOOK:
                callStateToServer(state, phoneNumber, Utils.getDateTime());
                //	Toast.makeText(mContext, "increasing call success", Toast.LENGTH_SHORT).show();
                Utils.appendLog( "ELOG_SENDER: onCallStateChanged: new CALL_STATE_OFFHOOK");
                break;

            default:
                Log.i(LOG_TAG, "UNKNOWN_STATE: " + state);
                break;
        }
    }

    private void callStateToServer(int state, String phoneNumber, String dateTime){
        Utils.appendLog( "ELOG_SENDER: callStateToServer: called "+userType+" sender" +senderNumber+" state "+state);
        JSONObject jsonObject = new JSONObject();
        DB_handler db_handler = new DB_handler(MviewApplication.ctx);
        if (userType != null){
            if (userType.equalsIgnoreCase("sender")){
                if (state == 0){
                    Utils.appendLog("ELOG_SENDER: offHook flag is "+offHookState);
                    try {
                        jsonObject.put("call_state_idle_time",dateTime);
                        jsonObject.put("id",id);
                        jsonObject.put("user_type",userType);
                        JSONArray jsonArray = new JSONArray();
                        jsonArray.put(jsonObject);
                        if (offHookState){
                            Utils.appendLog( "ELOG_SENDER: callStateToServer sender: "+jsonArray);

                            RequestResponse.sendEventToServer(jsonArray, AllInOneAsyncTaskForEVT.AsyncTaskPurpose.SDK,"call_report","1", new Interfaces.ResultListner() {
                                @Override
                                public void onResultObtained(boolean status) {
                                    if (!status){
                                        db_handler.open();
                                        db_handler.insertInLoggingAgentTable("VolteCall", "call_report", jsonArray.toString(), dateTime, "INIT");
                                        db_handler.close();
                                    }
                                }
                            });
                            offHookState =false;

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }



                }else if (state == 2){
                    offHookState = true;
                    try {
                        jsonObject.put("call_state_offhook_time",dateTime);

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        }


    }
}
