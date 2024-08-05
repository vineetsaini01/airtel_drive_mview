package com.services;

import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.telecom.Call;
import android.telecom.InCallService;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.helper.SharedPreferencesHelper;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;

@RequiresApi(api = Build.VERSION_CODES.M)
public class CallService extends InCallService {
    Call call;
    String phoneNumber = null;
    SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(MviewApplication.ctx);
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable rejectCallRunnable;

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);
        Utils.appendLog("ELOG_CALL_ADDED_METHOD: Called");
        Uri handle = call.getDetails().getHandle();
        if (handle != null) {
            phoneNumber = handle.getSchemeSpecificPart();
            Utils.appendLog( "Incoming call from: " + phoneNumber);
        }
        String senderNum = sharedPreferencesHelper.getSenderNum();
        String rule = sharedPreferencesHelper.getRuleCondition();

        if (senderNum != null) {
            if (!senderNum.contains("+91")) {
                senderNum = "+91" + senderNum;
            }

            Utils.appendLog("ELOG_CALL_ACCEPT_SERVICE: onCallAdded called: senderNum: " + senderNum + " phoneNumber: " + phoneNumber+" rule is "+rule);

            if (senderNum.equals(phoneNumber)) {

                if (!rule.equalsIgnoreCase(Constants.CONDITIONAL_NO_ANSWER)){
                    call.answer(call.getDetails().getVideoState());

                    // Schedule the call rejection after 10 seconds
                    rejectCallRunnable = new Runnable() {
                        @Override
                        public void run() {
                            Utils.appendLog("ELOG_CALL_ACCEPT_SERVICE call obj is: " + call.toString());
                            call.disconnect();
                            Utils.appendLog( "Call Disconnected after 10 seconds");
                        }
                    };
                    handler.postDelayed(rejectCallRunnable, 10000);
                }
                if (rule.equalsIgnoreCase(Constants.CONDITIONAL_BUSY)){

                    call.reject(false,null);
                    Utils.appendLog( "Call rejected");

                }
               // 10000 milliseconds = 10 seconds

            }
        }

    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        Utils.appendLog("ELOG_CALL_ACCEPT_SERVICE: onCallRemoved called ");

    }


}
