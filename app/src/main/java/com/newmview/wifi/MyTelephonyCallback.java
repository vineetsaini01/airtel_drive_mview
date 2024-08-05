package com.newmview.wifi;

import android.os.Build;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONObject;

@RequiresApi(api = Build.VERSION_CODES.S)
public class MyTelephonyCallback implements TelephonyCallback.CallStateListener {
    @Override
    public void onCallStateChanged(int i) {

        System.out.println("call status " + i);
        switch (i) {
            case TelephonyManager.CALL_STATE_IDLE:
                Log.i("LOG_TAG", "onCallStateChanged: CALL_STATE_IDLE");
                break;

            case TelephonyManager.CALL_STATE_RINGING:
                Log.i("LOG_TAG", "onCallStateChanged: CALL_STATE_RINGING");
                break;

            case TelephonyManager.CALL_STATE_OFFHOOK:

                //	Toast.makeText(mContext, "increasing call success", Toast.LENGTH_SHORT).show();
                Log.i("LOG_TAG", "onCallStateChanged: CALL_STATE_OFFHOOK");
                break;

            default:

                Log.i("LOG_TAG", "UNKNOWN_STATE: " + i);
                break;
        }

    }


    private void callstate(){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result","");

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
