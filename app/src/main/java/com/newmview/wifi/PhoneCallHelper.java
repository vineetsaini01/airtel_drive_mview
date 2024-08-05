package com.newmview.wifi;

import static android.content.Context.TELEPHONY_SERVICE;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.dashboard.roomdb.gagdagent.GagdAgent;
import com.functionapps.mview_sdk2.main.Mview;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.database.DB_handler;
import com.newmview.wifi.helper.AllInOneAsyncTaskForEVT;
import com.newmview.wifi.helper.Interfaces;
import com.newmview.wifi.helper.RequestResponse;
import com.newmview.wifi.helper.SharedPreferencesHelper;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class PhoneCallHelper {

    private JSONObject jsonObject = new JSONObject();
    private Context context;
    SharedPreferencesHelper sharedPreferencesHelper;

    public PhoneCallHelper(Context context) {
        this.context = context.getApplicationContext();
        sharedPreferencesHelper = new SharedPreferencesHelper(context);

    }

    public void volteCallTest() {
        Utils.appendLog("ELOG_RUN_VOLTE_CALL_PANEL: Called");
        DB_handler db_helper = new DB_handler(MviewApplication.ctx);
        db_helper.open();

        String current_status = db_helper.current_status("VolteCall");
        String start_date = db_helper.start_date("VolteCall");
        String end_date = db_helper.end_date("VolteCall");
        Date currentdate = Utils.getcurrentdate();
        Date start_date_ = Utils.convertStringtoDate(start_date);
        Date end_date_ = Utils.convertStringtoDate(end_date);

        Log.i("TAG", "current_status is of agent is " + current_status + " and start date is " + start_date + " and end date is " + end_date);

        if (current_status == null || current_status.equalsIgnoreCase("completed")) {
            Log.d("TAG", "current status is completed");
            if (start_date_ != null && end_date_ != null &&
                    currentdate.after(start_date_) && currentdate.before(end_date_))
            {
                List<GagdAgent> initAgents = db_helper.getAgentsByNameAndStatus("VolteCall","INIT");
                Log.d("Agent list", "onClick: "+initAgents);
                if (!initAgents.isEmpty()) {
                    for (int i = 0; i < initAgents.size(); i++) {
                        GagdAgent agent = initAgents.get(i);
                        Log.d(Mview.TAG, "Going to start Volte Call test for "+agent.getUrl());

                        if (agent.getUser_type().equalsIgnoreCase("forwarder")){
                            runUSSDCode(agent.getThird_party(),agent.getId(),agent.getUrl(),agent.getRule(),agent.getUser_type(),agent.getMobile());

                        }else if (agent.getUser_type().equalsIgnoreCase("sender")){
                            startCall(agent.getUrl(),agent.getUser_type(),agent.getId(),agent.getMobile());
                        }else if (agent.getUser_type().equalsIgnoreCase("receiver")){
                            thirdParty(agent.getUser_type(),agent.getUrl(),agent.getId(),agent.getRule(),agent.getThird_party(),agent.getMobile());
                        }



                        // Update status based on event type
                        if (agent.getEventType().equalsIgnoreCase("event")) {
                            db_helper.updateAgentStatus(agent.getId(), "COMPLETED");
                        } else if (agent.getEventType().equalsIgnoreCase("schedule")) {
                            db_helper.updateAgentStatus(agent.getId(), "COMPLETED");
                        }

                        // Insert results into the table (if required)
                        // db.databaseWriteExecutor.execute(() -> insertTestResults(agent.getId(), pingResult, tracerouteResult));

                    }
                }else {
                    Utils.appendLog("ELOG_VOLTE_CALL_PANEL : No package data available in DB for Volte Call test");
                }


            }
            else {
                Utils.appendLog("ELOG_VOLTE_CALL_PANEL: Status of Volte Call TEST agent is not Active");
            }
        }

        // Reset index to start over
        db_helper.close();

    }

    private void thirdParty(String user_type, String url, String id, String rule, String third_party, String mobile) {
        Utils.appendLog("ELOG_THIRD_PARTY_RECEIVER: thirdParty called usertType is: "+user_type+" senderNum "+url);
        sharedPreferencesHelper.setUserType(user_type);
        sharedPreferencesHelper.setSenderNum(url);
        sharedPreferencesHelper.setId(id);
        sharedPreferencesHelper.setThirdPartyNum(third_party);
        sharedPreferencesHelper.setReceiverNum(mobile);

       if (rule.equalsIgnoreCase("Normal")){
            Utils.appendLog("ELOG_VOLTE_CALL: rule is Normal ");

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String date_time = now.format(dtf);
            try {
                jsonObject.put("id", id);
                jsonObject.put("accept_flag", true);
                jsonObject.put("response", "NA");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(jsonObject);
            try {
                Utils.appendLog("ELOG_VOLTE_CALL: sending ussd_Ack to server: "+jsonArray);
                RequestResponse.sendEvtToServer(jsonArray, AllInOneAsyncTaskForEVT.AsyncTaskPurpose.SDK, "ussd_ack", "1");
            } catch (Exception e) {
                DB_handler db_handler = new DB_handler(MviewApplication.ctx);
                db_handler.open();
                db_handler.insertInLoggingAgentTable("VolteCall", "ussd_ack", jsonArray.toString(), date_time, "INIT");
                db_handler.close();
            }

            return;
        }
    }

    public void startCall(String url, String userType, String id, String mobile) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(() -> {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager == null) {
                Utils.appendLog("ELOG_VOLTE_CALL: TelephonyManager is null. Cannot proceed.");
                return;
            }

            // Check permission
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                Utils.appendLog("ELOG_VOLTE_CALL: CALL_PHONE permission not granted.");
                return;
            }

            try {
                Utils.appendLog("ELOG_VOLTE_CALL: NewPhoneStateListner before starting call usertype is " + userType + " sender num is" + mobile + " id is" + id);
                sharedPreferencesHelper.setUserType(userType);
                sharedPreferencesHelper.setId(id);
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + url));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(callIntent);
                sharedPreferencesHelper.setDateTime(Utils.getDateTime());
                Utils.appendLog("ELOG_SENDER: after call start intent");
//            boolean readPhoneStatePermissionDenied = false;

//            MyPhoneStateListener myPhoneStateListener = new MyPhoneStateListener(userType,mobile,id);
//            telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
                // Listen for call state changes (if needed)

            } catch (SecurityException e) {
                Utils.appendLog("ELOG_VOLTE_CALL_EXCEPTION: is " + e.getMessage());
                e.printStackTrace();
            }
        });
    }



    public void runUSSDCode(String third_party, String id, String senderNum, String rule, String user_type, String mobile) {
        JSONObject jsonObject = new JSONObject();
        DB_handler db_handler = new DB_handler(context);
        String ussdCondition = null;
        String code = null;
        sharedPreferencesHelper.setUserType(user_type);
        sharedPreferencesHelper.setId(id);
        sharedPreferencesHelper.setSenderNum(senderNum);
        sharedPreferencesHelper.setThirdPartyNum(third_party);
        sharedPreferencesHelper.setReceiverNum(mobile);
        sharedPreferencesHelper.setRuleCondition(rule);
        Utils.appendLog("ELOG_VOLTE_CALL: set shared pref userType: "+sharedPreferencesHelper.getUserType()+" id: "+sharedPreferencesHelper.getId()+" sender num: "+sharedPreferencesHelper.getSenderNum());
        TelephonyManager manager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);

        try {
            db_handler.open();
            Utils.appendLog("ELOG_VOLTE_CALL: Sender number is "+senderNum);

            Log.d("TAG", "runUSSDCode: is "+ third_party);
            if (rule.equalsIgnoreCase(Constants.UNCONDITIONAL)){
                ussdCondition = "**21*" + third_party + "#";
                Utils.appendLog("ELOG_VOLTE_CALL: rule is Unconditional "+ussdCondition);
            }else if (rule.equalsIgnoreCase(Constants.CONDITIONAL_SWITCHOFF_UNREACHABLE)){
                ussdCondition = "**62*"+ third_party+"#";
                Utils.appendLog("ELOG_VOLTE_CALL: rule is Conditional - SwitchOFf Or Unreachable "+ussdCondition);

            }else if (rule.equalsIgnoreCase(Constants.CONDITIONAL_NO_ANSWER)){
                ussdCondition = "**61*"+third_party+"#";
                Utils.appendLog("ELOG_VOLTE_CALL: rule is Conditional - No Answer "+ussdCondition);

            }else if (rule.equalsIgnoreCase(Constants.CONDITIONAL_BUSY)){
                ussdCondition = "**67*"+third_party+"#";
                Utils.appendLog("ELOG_VOLTE_CALL: rule is Conditional - Busy "+ussdCondition);

            }
            if(ussdCondition != null){
                code = ussdCondition.trim();
                if (ussdCondition.length() > 0){
                    ussdCondition = ussdCondition.substring(0, ussdCondition.length() - 1) + Uri.encode("#");

                }

            }
            Log.d("TAG", "runUSSDCode: code is "+ code);


            Intent i = new Intent(Intent.ACTION_CALL);
            i.setData(Uri.parse("tel:" + ussdCondition));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                Utils.appendLog( "ELOG_CALL_PHONE permission not granted.");
                return;
            }

            HandlerThread handlerThread = new HandlerThread("USSDThread");
            handlerThread.start();
            Handler handler = new Handler(handlerThread.getLooper());

            if (manager == null) {
                Utils.appendLog("TelephonyManager is null.");
                return;
            }

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    manager.sendUssdRequest(code, new TelephonyManager.UssdResponseCallback() {
                        @Override
                        public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, CharSequence response) {
                            super.onReceiveUssdResponse(telephonyManager, request, response);
                            Utils.appendLog("ELOG_USSD_SUCCESS:  USSD Success Response is " + response.toString());
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            LocalDateTime now = LocalDateTime.now();
                            String date_time = now.format(dtf);
                            try {
                                jsonObject.put("id", id);
                                jsonObject.put("accept_flag", true);
                                jsonObject.put("response", response.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            JSONArray jsonArray = new JSONArray();
                            jsonArray.put(jsonObject);
                            try {
                                RequestResponse.sendEvtToServer(jsonArray, AllInOneAsyncTaskForEVT.AsyncTaskPurpose.SDK, "ussd_ack", "1");
                            } catch (Exception e) {
                                db_handler.insertInLoggingAgentTable("VolteCall", "ussd_ack", jsonArray.toString(), date_time, "INIT");
                            }
                            handlerThread.quitSafely();
                        }

                        @Override
                        public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
                            super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);
                            Utils.appendLog("ELOG_USSD_FAILED:  USSD failed code is " + failureCode);

                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            LocalDateTime now = LocalDateTime.now();
                            String date_time = now.format(dtf);
                            try {
                                jsonObject.put("id", id);
                                jsonObject.put("accept_flag", false);
                                jsonObject.put("response", "NA");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            JSONArray jsonArray = new JSONArray();
                            jsonArray.put(jsonObject);
                            if (!Utils.isNetworkAvailable(context)){
                                db_handler.insertInLoggingAgentTable("VolteCall", "ussd_ack", jsonArray.toString(), date_time, "INIT");

                            }else {
                                try {
                                    RequestResponse.sendEventToServer(jsonArray, AllInOneAsyncTaskForEVT.AsyncTaskPurpose.SDK, "ussd_ack", "1", new Interfaces.ResultListner() {
                                        @Override
                                        public void onResultObtained(boolean status) {
                                            if (!status){
                                                db_handler.insertInLoggingAgentTable("VolteCall", "ussd_ack", jsonArray.toString(), date_time, "INIT");

                                            }

                                        }
                                    });
                                } catch (Exception e) {
                                    db_handler.insertInLoggingAgentTable("VolteCall", "ussd_ack", jsonArray.toString(), date_time, "INIT");
                                }
                            }

                            handlerThread.quitSafely();
                        }
                    }, handler);
                }
            } catch (Exception e) {
                Log.e("PhoneCallHelper", "Exception in sending USSD request: " + e.getMessage());
                e.printStackTrace();
                handlerThread.quitSafely();
            }

            db_handler.close();
        } catch (Exception e) {
            Log.e("PhoneCallHelper", "Exception in runUSSDCode: " + e.getMessage());
            e.printStackTrace();
        }
    }



    public void ussdTest() {
        Utils.appendLog("ELOG_RUN_USSD_PANEL: Called");
        DB_handler db_helper = new DB_handler(MviewApplication.ctx);
        db_helper.open();

        String current_status = db_helper.current_status("UssdTest");
        String start_date = db_helper.start_date("UssdTest");
        String end_date = db_helper.end_date("UssdTest");
        Date currentdate = Utils.getcurrentdate();
        Date start_date_ = Utils.convertStringtoDate(start_date);
        Date end_date_ = Utils.convertStringtoDate(end_date);

        Log.i("TAG", "current_status is of agent is " + current_status + " and start date is " + start_date + " and end date is " + end_date);



        if (current_status == null || current_status.equalsIgnoreCase("completed")) {
            Log.d("TAG", "current status is completed");
            if (start_date_ != null && end_date_ != null &&
                    currentdate.after(start_date_) && currentdate.before(end_date_))
            {
                List<GagdAgent> initAgents = db_helper.getAgentsByNameAndStatus("UssdTest","INIT");
                Log.d("Agent list", "onClick: of ussd test "+initAgents);
                if (!initAgents.isEmpty()) {
                    for (int i = 0; i < initAgents.size(); i++) {
                        GagdAgent agent = initAgents.get(i);
                        Log.d(Mview.TAG, "Going to start USSD test for "+agent.getUrl());
                        runUssdTestPanel(agent.getId(),agent.getUrl());


                        // Update status based on event type
                        if (agent.getEventType().equalsIgnoreCase("event")) {
                            db_helper.updateAgentStatus(agent.getId(), "COMPLETED");
                        } else if (agent.getEventType().equalsIgnoreCase("schedule")) {
                            db_helper.updateAgentStatus(agent.getId(), "COMPLETED");
                        }

                        // Insert results into the table (if required)
                        // db.databaseWriteExecutor.execute(() -> insertTestResults(agent.getId(), pingResult, tracerouteResult));

                    }
                }else {
                    Utils.appendLog("ELOG_RUN_USSD_PANEL : No package data available in DB for USSD test");
                }


            }
            else {
                Utils.appendLog("ELOG_RUN_USSD_PANEL: Status of USSD TEST agent is not Active");
            }
        }

        // Reset index to start over
        db_helper.close();

    }

    public void runUssdTestPanel( String id, String ussdCondition) {
        JSONObject jsonObject = new JSONObject();
        DB_handler db_handler = new DB_handler(context);
        String  code = null;
        Utils.appendLog("ELOG_RUN_USSD_PANEL: test initiated ");
        TelephonyManager manager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);

        try {
            db_handler.open();

            if(ussdCondition != null){
               code = ussdCondition.trim();
                if (ussdCondition.length() > 0){
                    ussdCondition = ussdCondition.substring(0, ussdCondition.length() - 1) + Uri.encode("#");

                }

            }
            Utils.appendLog("runUSSDCode: code is "+ code);


            Intent i = new Intent(Intent.ACTION_CALL);
            i.setData(Uri.parse("tel:" + ussdCondition));
            jsonObject.put("request_time",Utils.getDateTime());
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                Utils.appendLog("CALL_PHONE permission is not granted.");
                return;
            }

            HandlerThread handlerThread = new HandlerThread("USSDThread");
            handlerThread.start();
            Handler handler = new Handler(handlerThread.getLooper());

            if (manager == null) {
                Utils.appendLog( "TelephonyManager instance is null.");
                return;
            }

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    manager.sendUssdRequest(code, new TelephonyManager.UssdResponseCallback() {
                        @Override
                        public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, CharSequence response) {
                            super.onReceiveUssdResponse(telephonyManager, request, response);
                            Utils.appendLog("ELOG_USSD_SUCCESS:  USSD Success Response is " + response.toString());
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            LocalDateTime now = LocalDateTime.now();
                            String date_time = now.format(dtf);
                            try {
                                jsonObject.put("id", id);
                                jsonObject.put("status","success");
                                jsonObject.put("response_time",Utils.getDateTime());
                                jsonObject.put("failureCode", "NA");
                                jsonObject.put("response", response.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            JSONArray jsonArray = new JSONArray();
                            jsonArray.put(jsonObject);
                            try {
                                RequestResponse.sendEvtToServer(jsonArray, AllInOneAsyncTaskForEVT.AsyncTaskPurpose.SDK, "ussd_report", "1");
                            } catch (Exception e) {
                                db_handler.insertInLoggingAgentTable("UssdTest", "ussd_report", jsonArray.toString(), date_time, "INIT");
                            }
                            handlerThread.quitSafely();
                        }

                        @Override
                        public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
                            super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);
                            Utils.appendLog("ELOG_USSD_FAILED:  USSD failed code is " + failureCode);

                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            LocalDateTime now = LocalDateTime.now();
                            String date_time = now.format(dtf);
                            try {
                                jsonObject.put("id", id);
                                jsonObject.put("status","failed");
                                jsonObject.put("response_time",Utils.getDateTime());
                                jsonObject.put("failureCode", failureCode);
                                jsonObject.put("response", "Connection problem or invalid MMI code");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            JSONArray jsonArray = new JSONArray();
                            jsonArray.put(jsonObject);
                            if (!Utils.isNetworkAvailable(context)){
                                db_handler.insertInLoggingAgentTable("UssdTest", "ussd_report", jsonArray.toString(), date_time, "INIT");

                            }else {
                                try {
                                    RequestResponse.sendEventToServer(jsonArray, AllInOneAsyncTaskForEVT.AsyncTaskPurpose.SDK, "ussd_report", "1", new Interfaces.ResultListner() {
                                        @Override
                                        public void onResultObtained(boolean status) {
                                            if (!status){
                                                db_handler.insertInLoggingAgentTable("UssdTest", "ussd_report", jsonArray.toString(), date_time, "INIT");

                                            }

                                        }
                                    });
                                } catch (Exception e) {
                                    db_handler.insertInLoggingAgentTable("UssdTest", "ussd_report", jsonArray.toString(), date_time, "INIT");
                                }
                            }

                            handlerThread.quitSafely();
                        }
                    }, handler);
                }
            } catch (Exception e) {
                Utils.appendLog("Exception in sending USSD request: " + e.getMessage());
                e.printStackTrace();
                handlerThread.quitSafely();
            }

            db_handler.close();
        } catch (Exception e) {
            Utils.appendLog( "Exception in runUSSDCode: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void smsTest() {
        Utils.appendLog("ELOG_RUN_SMS_PANEL: Called");
        DB_handler db_helper = new DB_handler(MviewApplication.ctx);
        db_helper.open();

        String current_status = db_helper.current_status("SmsTest");
        String start_date = db_helper.start_date("SmsTest");
        String end_date = db_helper.end_date("SmsTest");
        Date currentdate = Utils.getcurrentdate();
        Date start_date_ = Utils.convertStringtoDate(start_date);
        Date end_date_ = Utils.convertStringtoDate(end_date);

        Log.i("TAG", "current_status is of agent is " + current_status + " and start date is " + start_date + " and end date is " + end_date);



        if (current_status == null || current_status.equalsIgnoreCase("completed")) {
            Log.d("TAG", "current status is completed");
            if (start_date_ != null && end_date_ != null &&
                    currentdate.after(start_date_) && currentdate.before(end_date_))
            {
//                String text = "this is a test message with job id ";
                List<GagdAgent> initAgents = db_helper.getAgentsByNameAndStatus("SmsTest","INIT");
                Log.d("Agent list", "onClick: of SMS test "+initAgents);
                if (!initAgents.isEmpty()) {
                    for (int i = 0; i < initAgents.size(); i++) {
                        GagdAgent agent = initAgents.get(i);
                        Utils.appendLog( "Going to start SMS test for "+agent.getUrl());
                        String text = "this is a test message with job id "+agent.getId();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            sendSms(agent.getUrl(),text,agent.getId());
                        }


                        if (agent.getEventType().equalsIgnoreCase("event")) {
                            db_helper.updateAgentStatus(agent.getId(), "COMPLETED");
                        } else if (agent.getEventType().equalsIgnoreCase("schedule")) {
                            db_helper.updateAgentStatus(agent.getId(), "COMPLETED");
                        }

                    }
                }else {
                    Utils.appendLog("ELOG_RUN_SMS_PANEL : No package data available in DB for SMS test");
                }


            }
            else {
                Utils.appendLog("ELOG_RUN_SMS_PANEL: Status of SMS TEST agent is not Active");
            }
        }

        // Reset index to start over
        db_helper.close();

    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    private void sendSms(String mob, String msg, String id) {
        try {

            String SENT = "SMS_SENT";
            String DELIVERED = "SMS_DELIVERED";
            Utils.appendLog("ELOG_SEND_SMS: mobile no is: " + mob + " msg is: " + msg);
            JSONObject jsonObject = new JSONObject();
            PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, new Intent(DELIVERED), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            if (mob.isEmpty() || mob == null) {
                try {
                    jsonObject.put("number", "NA");
                    jsonObject.put("message", "Mobile number not found");
                    jsonObject.put("id", id);
                    jsonObject.put("sent_timestamp", Utils.getDateTime());
                    jsonObject.put("sent_status", "failed");
                    jsonObject.put("delivered_timestamp", Utils.getDateTime());
                    jsonObject.put("delivered_status", "failed");

                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(jsonObject);
                    Utils.appendLog("ELOG_SMS_PANEL: SMS mobile not found event Json is " + jsonArray);
                    if (!Utils.isNetworkAvailable(context)) {
                        DB_handler db_handler = new DB_handler(MviewApplication.ctx);
                        db_handler.open();
                        db_handler.insertInLoggingAgentTable("SmsTest", "sms_report", jsonArray.toString(), Utils.getDateTime(), "INIT");
                        db_handler.close();
                    } else {
                        RequestResponse.sendEvtToServer(jsonArray, AllInOneAsyncTaskForEVT.AsyncTaskPurpose.SDK, "sms_report", "1");

                    }

                    return;
                } catch (Exception e) {
                    Utils.appendLog("ELOG_EXCEPTION_SMS: mobile number is empty" + e.getStackTrace());
                }


            }
            // Register for SMS sent event
            context.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    try {
                        jsonObject.put("number", mob);
                        jsonObject.put("message", msg);
                        jsonObject.put("id", id);
                        jsonObject.put("sent_timestamp", Utils.getDateTime());


                        switch (getResultCode()) {
                            case Activity.RESULT_OK:
                                Utils.appendLog("ELOG_SEND_SMS: SMS Sent");
                                jsonObject.put("sent_status", "success");
                                break;
                            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                Utils.appendLog("ELOG_SEND_SMS: Generic failure");
                                jsonObject.put("sent_status", "generic_failure");
                                break;
                            case SmsManager.RESULT_ERROR_NO_SERVICE:
                                Utils.appendLog("ELOG_SEND_SMS: No service");
                                jsonObject.put("sent_status", "no_service");
                                break;
                            case SmsManager.RESULT_ERROR_NULL_PDU:
                                Utils.appendLog("ELOG_SEND_SMS: Null PDU");
                                jsonObject.put("sent_status", "null_pdu");
                                break;
                            case SmsManager.RESULT_ERROR_RADIO_OFF:
                                Utils.appendLog("ELOG_SEND_SMS: Radio off");
                                jsonObject.put("sent_status", "radio_off");
                                break;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new IntentFilter(SENT));

            // Register for SMS delivered event
            context.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    try {

                        jsonObject.put("delivered_timestamp", Utils.getDateTime());

                        switch (getResultCode()) {
                            case Activity.RESULT_OK:
                                Utils.appendLog("ELOG_SEND_SMS: SMS delivered");
                                jsonObject.put("delivered_status", "success");
                                break;
                            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                Utils.appendLog("ELOG_SEND_SMS: Generic failure");
                                jsonObject.put("delivered_status", "generic_failure");
                                break;
                            case SmsManager.RESULT_ERROR_NO_SERVICE:
                                Utils.appendLog("ELOG_SEND_SMS: No service");
                                jsonObject.put("delivered_status", "no_service");
                                break;
                            case SmsManager.RESULT_ERROR_NULL_PDU:
                                Utils.appendLog("ELOG_SEND_SMS: Null PDU");
                                jsonObject.put("delivered_status", "null_pdu");
                                break;
                            case SmsManager.RESULT_ERROR_RADIO_OFF:
                                Utils.appendLog("ELOG_SEND_SMS: Radio off");
                                jsonObject.put("delivered_status", "radio_off");
                                break;
                        }
                        JSONArray jsonArray = new JSONArray();
                        jsonArray.put(jsonObject);
                        Utils.appendLog("ELOG_SMS_PANEL: SMS delivered event Json is " + jsonArray);
                        if (!Utils.isNetworkAvailable(context)) {
                            DB_handler db_handler = new DB_handler(MviewApplication.ctx);
                            db_handler.open();
                            db_handler.insertInLoggingAgentTable("SmsTest", "sms_report", jsonArray.toString(), Utils.getDateTime(), "INIT");
                            db_handler.close();
                        } else {
                            RequestResponse.sendEvtToServer(jsonArray, AllInOneAsyncTaskForEVT.AsyncTaskPurpose.SDK, "sms_report", "1");

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new IntentFilter(DELIVERED));

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(mob, null, msg, sentPI, deliveredPI);
        }catch (Exception e){
            Utils.appendLog("ELOG_EXCEPTION_IN_SMS_TEST: is "+e.getStackTrace());
        }
    }
}
