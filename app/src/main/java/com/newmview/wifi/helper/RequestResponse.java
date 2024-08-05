
package com.newmview.wifi.helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.newmview.wifi.MyPhoneStateListener;
import com.newmview.wifi.Telephony_params;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.listenService;
import com.newmview.wifi.mView_HealthStatus;
import com.newmview.wifi.other.Config;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.newmview.wifi.activity.MainActivity.context;

import java.util.Iterator;

public class RequestResponse {
    private static final int PERMISSIONS_REQUEST = 2;
    private static final String TAG ="RequestResponse" ;



    public static void sendOldEvents(JSONArray details,String event)
    {

        JSONObject obj = new JSONObject();
        try {
            obj.put("msg", "evt");
            obj.put("evt_type", event);
            obj.put("details",details);
            obj.put("interface", "CLI");
            obj.put("prod", Config.product);
            obj.put("msisdn", Utils.getMyContactNum(MviewApplication.ctx));
            obj.put("ver", Constants.getversionnumber(MviewApplication.ctx));
            obj.put("apn_type", Config.getApnType(MviewApplication.ctx));
            obj.put("imsi", Constants.IMSI);
            obj.put("phone_imsi", Constants.IMSI);
            obj.put("apn", Config.getApnType(MviewApplication.ctx));
            if (listenService.gps != null && listenService.gps.canGetLocation()) {
                obj.put("latitude", listenService.gps.getLatitude() + "");
                obj.put("longitude", listenService.gps.getLongitude() + "");
                obj.put("lat", listenService.gps.getLatitude() + "");
                obj.put("lon", listenService.gps.getLongitude() + "");
            } else {
                obj.put("latitude", "0");
                obj.put("longitude", "0");
                obj.put("lat", "0");
                obj.put("lon", "0");
            }
            obj.put("lacid", Config.getlacid(MviewApplication.ctx));
            obj.put("pubid", "0");
            obj.put("clickid", "0");
            obj.put("cellid", Config.getcellid(MviewApplication.ctx));
            obj.put("operatorname", mView_HealthStatus.OperatorName);
            obj.put("ip","NA");
            obj.put("port", "4444");
         obj.put("country_code",Config.getUserCountry());
            obj.put("androidsdk", Build.VERSION.SDK_INT);
            String jobjstr=obj.toString();
            System.out.println(" json is "+jobjstr);
            if (jobjstr != null && Config.isNetworkAvailable(MviewApplication.ctx)) {
                AllInOneAsyncTaskForNetwork async =
                        new AllInOneAsyncTaskForNetwork(MviewApplication.ctx,
                                AllInOneAsyncTaskForNetwork.AsyncTaskPurpose.IMUP);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,jobjstr);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void sendGagdRequest()
    {
        JSONObject obj = new JSONObject();
        try {
            obj.put("msg", "gagd");
            obj.put("ver", Constants.getversionnumber(MviewApplication.ctx));
            obj.put("prod", Config.product);
            obj.put("os_version", "22");
            obj.put("ip","NA");
            obj.put("apn_type", Config.getApnType(MviewApplication.ctx));
            obj.put("clickid", "0");
            obj.put("imsi", Constants.IMSI);
            obj.put("interface", "CLI");
            obj.put("cellid", Config.getcellid(MviewApplication.ctx));
            obj.put("phone_imsi", Constants.IMSI);
            obj.put("country_code", "IN");
            obj.put("device_info", " ");
            obj.put("androidsdk", Build.VERSION.SDK_INT);
            obj.put("port", "4444");
            obj.put("lacid", Config.getlacid(MviewApplication.ctx));
            obj.put("pubid", "0");
            obj.put("msisdn", Utils.getMyContactNum(MviewApplication.ctx));
            obj.put("apn", Config.getApnType(MviewApplication.ctx));

            if (listenService.gps != null && listenService.gps.canGetLocation()) {
                obj.put("lat", listenService.gps.getLatitude() + "");
                obj.put("lon", listenService.gps.getLongitude() + "");
                obj.put("longitude", listenService.gps.getLongitude() + "");

            } else {
                obj.put("lat", "0");
                obj.put("lon", "0");
                obj.put("longitude", "0");
            }
            obj.put("operatorname", mView_HealthStatus.OperatorName);

//            obj.put("connection_type", mView_HealthStatus.connectionType);
//            obj.put("network_type",mView_HealthStatus.prim_NetworkType);
            String jobjstr=obj.toString();
            if (jobjstr != null && Config.isNetworkAvailable(MviewApplication.ctx)) {
                AllInOneAsyncTaskForNetwork async =
                        new AllInOneAsyncTaskForNetwork(MviewApplication.ctx,
                                AllInOneAsyncTaskForNetwork.AsyncTaskPurpose.GAGD);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    Utils.appendLog("ELOG_GAGD_REQUEST_JSON:  Gagd final json going to server is:  "+jobjstr);

                    async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,jobjstr);
                }

            }

        } catch (Exception e) {
            Utils.appendLog("ELOG_GAGD_EXCEPTION: Exception in sending gagd request is "+e.getMessage());
            e.printStackTrace();
        }


    }

        public static void sendImupRequest()
    {
        Utils.appendLog("ELOG_SEND_IMUP: Sending IMUP request");
        JSONObject obj = new JSONObject();
        try {
            obj.put("msg", "imup");
            obj.put("type", "config_change");
            obj.put("interface", "CLI");
            obj.put("prod", Config.product);
            obj.put("msisdn", Utils.getMyContactNum(MviewApplication.ctx));
            obj.put("ver", Constants.getversionnumber(MviewApplication.ctx));
            obj.put("apn_type", Config.getApnType(MviewApplication.ctx));
            obj.put("imsi", Constants.IMSI);
            obj.put("phone_imsi", Constants.IMSI);
            obj.put("apn", Config.getApnType(MviewApplication.ctx));
            obj.put("circle_name",Utils.getMyCircleName(MviewApplication.ctx));
            if (listenService.gps != null && listenService.gps.canGetLocation()) {
                    obj.put("latitude", listenService.gps.getLatitude() + "");
                    obj.put("longitude", listenService.gps.getLongitude() + "");
                    obj.put("altitude", listenService.gps.getAltitude() + "");
                } else {
                    obj.put("latitude", "0");
                    obj.put("longitude", "0");
                    obj.put("altitude", "0");
                }
            obj.put("lacid", Config.getlacid(MviewApplication.ctx));
            obj.put("pubid", "0");
            obj.put("clickid", "0");
            obj.put("cellid", Config.getcellid(MviewApplication.ctx));
            obj.put("operatorname", mView_HealthStatus.OperatorName);
            obj.put("ip","NA");
            obj.put("port", "4444");
            obj.put("country_code", "IN");
            obj.put("connection_type", mView_HealthStatus.connectionType);
            obj.put("network_type",mView_HealthStatus.prim_NetworkType);
            obj.put("androidsdk", Build.VERSION.SDK_INT);
            String jobjstr=obj.toString();
            if (jobjstr != null && Config.isNetworkAvailable(MviewApplication.ctx)) {
                AllInOneAsyncTaskForNetwork async =
                        new AllInOneAsyncTaskForNetwork(MviewApplication.ctx,
                                AllInOneAsyncTaskForNetwork.AsyncTaskPurpose.IMUP);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    Utils.appendLog("ELOG_IMUP_REQUEST_JSON is: "+jobjstr);
                    async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,jobjstr);
                }

            }else {
                Utils.appendLog("ELOG_IMUP_REQUEST: Network not available or jobjstr is null");
            }

        } catch (Exception e) {
            Utils.appendLog("ELOG_IMUP_EXCEPTION: Exception in sending IMUP request is "+e.getMessage());

            e.printStackTrace();
        }


    }

    public static void sendNewImupRequest()
    {
        Utils.appendLog("ELOG_SEND_IMUP: Sending IMUP request");
        JSONObject obj = new JSONObject();
        try {
            obj.put("msg", "imup");
            obj.put("type", "config_change");
            obj.put("interface", "CLI");
            obj.put("prod", Config.product);
            obj.put("msisdn", Utils.getMyContactNum(MviewApplication.ctx));
            obj.put("ver", Constants.getversionnumber(MviewApplication.ctx));
            obj.put("apn_type", Config.getApnType(MviewApplication.ctx));
            obj.put("imsi", Constants.IMSI);
            obj.put("phone_imsi", Constants.IMSI);
            obj.put("apn", Config.getApnType(MviewApplication.ctx));
            obj.put("circle_name",Utils.getMyCircleName(MviewApplication.ctx));
            if (listenService.gps != null && listenService.gps.canGetLocation()) {
                obj.put("latitude", listenService.gps.getLatitude() + "");
                obj.put("longitude", listenService.gps.getLongitude() + "");
                obj.put("altitude", listenService.gps.getAltitude() + "");
            } else {
                obj.put("latitude", "0");
                obj.put("longitude", "0");
                obj.put("altitude", "0");
            }
            obj.put("lacid", Config.getlacid(MviewApplication.ctx));
            obj.put("pubid", "0");
            obj.put("clickid", "0");
            obj.put("cellid", Config.getcellid(MviewApplication.ctx));
            obj.put("operatorname", mView_HealthStatus.OperatorName);
            obj.put("ip","NA");
            obj.put("port", "4444");
            obj.put("country_code", "IN");
            obj.put("connection_type", mView_HealthStatus.connectionType);
            obj.put("network_type",mView_HealthStatus.prim_NetworkType);
            obj.put("androidsdk", Build.VERSION.SDK_INT);
            String jobjstr=obj.toString();
            if (jobjstr != null && Config.isNetworkAvailable(MviewApplication.ctx)) {
                AllInOneAsyncTaskForNetwork async =
                        new AllInOneAsyncTaskForNetwork(MviewApplication.ctx,
                                AllInOneAsyncTaskForNetwork.AsyncTaskPurpose.IMUPNEW);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    Utils.appendLog("ELOG_IMUP_REQUEST_JSON is: "+jobjstr);
                    async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,jobjstr);
                }

            }

        } catch (Exception e) {
            Utils.appendLog("ELOG_IMUP_EXCEPTION: Exception in sending IMUP request is "+e.getMessage());

            e.printStackTrace();
        }


    }

        public static void sendInitRequest()
        {
            Utils.appendLog("ELOG_SEND_INIT");

            JSONObject obj = new JSONObject();
            try {

                obj.put("msg", "init");
                obj.put("interface", "CLI");
                obj.put("prod", Config.product);
                obj.put("user_name", Utils.getMyUserName(MviewApplication.ctx));
                obj.put("user_type", Utils.getMyUserType(MviewApplication.ctx));
                obj.put("msisdn", Utils.getMyContactNum(MviewApplication.ctx));
                obj.put("ver", Constants.getversionnumber(MviewApplication.ctx));
//                obj.put("apn_type", Config.getApnType(MviewApplication.ctx));
                obj.put("imsi", Constants.IMSI);
//                obj.put("phone_imsi", Constants.IMSI);
                obj.put("apn", Config.getApnType(MviewApplication.ctx));
                Log.d(TAG, "circle name is "+mView_HealthStatus.circle_name);
                obj.put("circle_name",Utils.getMyCircleName(MviewApplication.ctx));
                if (listenService.gps != null && listenService.gps.canGetLocation()) {
                    obj.put("latitude", listenService.gps.getLatitude() + "");
                    obj.put("longitude", listenService.gps.getLongitude() + "");
                    obj.put("altitude", listenService.gps.getAltitude() + "");
                } else {
                    obj.put("latitude", "0");
                    obj.put("longitude", "0");
                    obj.put("altitude", "0");
                }
                obj.put("lacid", Config.getlacid(MviewApplication.ctx));
                obj.put("pubid", "0");
                obj.put("clickid", "0");
                obj.put("cellid", Config.getcellid(MviewApplication.ctx));
                obj.put("operatorname", mView_HealthStatus.OperatorName);
                obj.put("ip","NA");
                obj.put("port", "4444");
                obj.put("country_code", "IN");
                obj.put("androidsdk", Build.VERSION.SDK_INT);
                obj.put("network_type",mView_HealthStatus.strCurrentNetworkState);
                String jobjstr=obj.toString();
                if (jobjstr != null && Config.isNetworkAvailable(MviewApplication.ctx)) {
                    AllInOneAsyncTaskForNetwork async =
                            new AllInOneAsyncTaskForNetwork(MviewApplication.ctx,
                                    AllInOneAsyncTaskForNetwork.AsyncTaskPurpose.INIT);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        Utils.appendLog("ELOG_INIT_REQUEST_JSON: "+jobjstr);

                        async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,jobjstr);
                    }

                }

            } catch (Exception e) {
                Utils.appendLog("ELOG_INIT_EXCEPTION: Exception in sending init request is "+e.getMessage());

                e.printStackTrace();
            }


        }





    public static void sendEvent(JSONArray arr, AllInOneAsyncTaskForNetwork.AsyncTaskPurpose eventTypeEnum,String eventType)
    {

        JSONObject obj = new JSONObject();
        try {
            obj.put("msg", "evt");
            obj.put("evt_type",eventType);
            obj.put("interface", "CLI");
            obj.put("prod", Config.product);
            obj.put("msisdn", Utils.getMyContactNum(MviewApplication.ctx));
            obj.put("ver", Constants.getversionnumber(MviewApplication.ctx));
            obj.put("apn_type", Config.getApnType(MviewApplication.ctx));
            obj.put("imsi", Constants.IMSI);
            obj.put("phone_imsi", Constants.IMSI);
            obj.put("apn", Config.getApnType(MviewApplication.ctx));
            obj.put("circle_name", Utils.getMyCircleName(MviewApplication.ctx));
            if (listenService.gps != null && listenService.gps.canGetLocation()) {
                obj.put("latitude", listenService.gps.getLatitude() + "");
                obj.put("longitude", listenService.gps.getLongitude() + "");
                obj.put("altitude", listenService.gps.getAltitude() + "");
            } else {
                obj.put("latitude", "0");
                obj.put("longitude", "0");
                obj.put("altitude", "0");
            }
            obj.put("lacid", Config.getlacid(MviewApplication.ctx));
            obj.put("pubid", "0");
            obj.put("clickid", "0");
            obj.put("cellid", Config.getcellid(MviewApplication.ctx));
            obj.put("operatorname", " ");
            obj.put("ip","NA");
            obj.put("port", "4444");
            obj.put("country_code", Config.getUserCountry());
            obj.put("androidsdk", Build.VERSION.SDK_INT);
            obj.put("phonetype", mView_HealthStatus.phonetype);

            obj.put("date_time",Utils.getDateTime());
            obj.put("connection_type", mView_HealthStatus.connectionType);
            obj.put("network_type",mView_HealthStatus.prim_NetworkType);
            String ss="";
            if(MyPhoneStateListener.getNetworkType()==4)
            {  if(mView_HealthStatus.lteRSRP!=null)
                ss= mView_HealthStatus.lteRSRP +"dbm";

            }
            else if(MyPhoneStateListener.getNetworkType()==3) {
                if(mView_HealthStatus.rscp!=null)
                    ss= mView_HealthStatus.rscp +"dbm"; }
            else if(MyPhoneStateListener.getNetworkType()==2)
            {
                ss= MyPhoneStateListener.getRxLev()+"dbm"; }
            obj.put("signal_strength",ss);
            //  String imsi = "";
            String imei = "";
            String simOperatorName = "";


            if (listenService.telMgr != null) {
                mView_HealthStatus.OperatorName = listenService.telMgr.getNetworkOperatorName();
                //   imsi = listenService.telMgr.getSubscriberId();
//                imei = listenService.telMgr.getDeviceId();
//                simOperatorName = listenService.telMgr.getSimOperatorName();
            } else {
                TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED )
                {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{ Manifest.permission.READ_PHONE_STATE},
                            PERMISSIONS_REQUEST);
                    //do your request permission
                }else{
                    //if the permission is granted then do
                    // imsi = telMgr.getSubscriberId();
                    imei = telMgr.getDeviceId();
                }
                //IMSI=imsi;


                mView_HealthStatus.OperatorName = telMgr.getNetworkOperatorName();
                simOperatorName = telMgr.getSimOperatorName();
            }

            obj.put("operatorname", mView_HealthStatus.OperatorName);
            String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            String version = "0";

//                PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
//                version = pInfo.versionName;
                obj.put("deviceid", android_id);



          //  obj.put("imei", imei);
            obj.put("phonemodal", Build.MODEL);
            obj.put("phonebrand", Build.BRAND);


            obj.put("details", arr);


            String jobjstr = obj.toString();
            Log.i(TAG,"Json to send is "+jobjstr);
            if (jobjstr != null && Config.isNetworkAvailable(MviewApplication.ctx)) {
                AllInOneAsyncTaskForNetwork async =
                        new AllInOneAsyncTaskForNetwork(MviewApplication.ctx,
                               eventTypeEnum);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,jobjstr);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void sendEvtToServer(JSONArray arr, AllInOneAsyncTaskForEVT.AsyncTaskPurpose eventTypeEnum, String eventType, String rowid)
    {
        Utils.appendLog("ELOG_SEND_EVT_SERVER: Going to send evt request to server");
        JSONObject obj = new JSONObject();
        try {

            obj.put("msg", "evt");
            obj.put("evt_type",eventType);
            obj.put("interface", "CLI");
            obj.put("prod", Config.product);
            obj.put("msisdn", Utils.getMyContactNum(MviewApplication.ctx));
            obj.put("ver", Constants.getversionnumber(MviewApplication.ctx));
            obj.put("apn_type", Config.getApnType(MviewApplication.ctx));
            obj.put("imsi", Constants.IMSI);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                obj.put("phone_imsi", " ");
            }else {
                obj.put("phone_imsi", Utils.getPhoneImsi(MviewApplication.ctx));
            }
            obj.put("apn", Config.getApnType(MviewApplication.ctx));
            obj.put("circle_name", Utils.getMyCircleName(MviewApplication.ctx));
            if (listenService.gps != null && listenService.gps.canGetLocation()) {
                obj.put("latitude", listenService.gps.getLatitude() + "");
                obj.put("longitude", listenService.gps.getLongitude() + "");
                obj.put("altitude", listenService.gps.getAltitude() + "");
            } else {
                obj.put("latitude", "0");
                obj.put("longitude", "0");
                obj.put("altitude", "0");

            }
            obj.put("lacid", Config.getlacid(MviewApplication.ctx));
            obj.put("pubid", "0");
            obj.put("clickid", "0");
            obj.put("cellid", Config.getcellid(MviewApplication.ctx));
            obj.put("operatorname", " ");

            obj.put("port", "4444");
            obj.put("country_code", Config.getUserCountry());
            obj.put("androidsdk", Build.VERSION.SDK_INT);
            obj.put("phonetype", mView_HealthStatus.phonetype);

            obj.put("date_time",Utils.getDateTime());
            obj.put("connection_type", mView_HealthStatus.connectionType);
            obj.put("network_type",mView_HealthStatus.strCurrentNetworkState);

            obj.put("operatorname", mView_HealthStatus.OperatorName);
            JSONObject sim1servingobj= Telephony_params.getsim1servingcellinfo();
            Utils.appendLog("ELOG_SIM1_SERVINGCELL_INFO: is "+sim1servingobj);


            if(sim1servingobj!=null) {
                try {
                    String csvSignalStrengthInfo = jsonObjectToCsvString(sim1servingobj.optJSONObject("signal_strength_info"));
                    String csvCellIdentityInfo = jsonObjectToCsvString(sim1servingobj.optJSONObject("cell_identity_info"));
                    obj.put("signal_strength_info",csvSignalStrengthInfo);
                    obj.put("cell_identity_info",csvCellIdentityInfo);
                    obj.put("cellid1", sim1servingobj.optString("cell_identity"));
                    obj.put("Lcellid", "NA");
                    obj.put("ratType", sim1servingobj.optString("ratType"));
                    obj.put("enb", sim1servingobj.optString("eNodeB"));
                    obj.put("snr", sim1servingobj.optString("snr"));
                    obj.put("earfcn", sim1servingobj.optString("earfcn"));
                    obj.put("pci", sim1servingobj.optString("pci"));
                    obj.put("ta", sim1servingobj.optString("ta"));
                    obj.put("cqi", sim1servingobj.optString("cqi"));
                    obj.put("signalStrength", sim1servingobj.optString("RSRP"));
                    obj.put("tac", sim1servingobj.optString("tac"));
                }catch (Exception e){
                    Utils.appendLog("ELOG_SEND_EVT_SERVINGCELL_INFO: "+e.getMessage());
                }

            }

            //  obj.put("imei", imei);
            obj.put("phonemodal", Build.MODEL);
            obj.put("phonebrand", Build.BRAND);


            obj.put("details", arr);


            String jobjstr = obj.toString();

            if (jobjstr != null && Config.isNetworkAvailable(MviewApplication.ctx)) {
                AllInOneAsyncTaskForEVT async =
                        new AllInOneAsyncTaskForEVT(MviewApplication.ctx,
                                eventTypeEnum,eventType,rowid, null);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    Utils.appendLog("ELOG_SEND_EVT_SERVER: Final JSON Going to server is: "+jobjstr);
                    async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,jobjstr);
                }

            }

        } catch (Exception e) {
            Utils.appendLog("ELOG_SEND_EVT_SERVER: Exception"+e.getMessage());
            e.printStackTrace();
        }


    }

    public static String jsonObjectToCsvString(JSONObject jsonObject) {
        try{
            if (jsonObject != null){
                StringBuilder csvStringBuilder = new StringBuilder();
                Iterator<String> keys = jsonObject.keys();

                while (keys.hasNext()) {
                    String key = keys.next();
                    String value = jsonObject.optString(key);
                    csvStringBuilder.append(key).append("=").append(value);

                    if (keys.hasNext()) {
                        csvStringBuilder.append(", ");
                    }
                }
                return csvStringBuilder.toString();

            }
        }catch (Exception e){
            Utils.appendLog("ELOG_EXCEPTION_CSV_STRING: is "+e.getStackTrace());
        }

        return "NA";
    }

    public static void sendEventToServer(JSONArray arr, AllInOneAsyncTaskForEVT.AsyncTaskPurpose eventTypeEnum, String eventType, String rowid, Interfaces.ResultListner resultListner)
    {
        Utils.appendLog("ELOG_SEND_EVT_SERVER: Going to send evt request to server");
        JSONObject obj = new JSONObject();
        try {

            obj.put("msg", "evt");
            obj.put("evt_type",eventType);
            obj.put("interface", "CLI");
            obj.put("prod", Config.product);
            obj.put("msisdn", Utils.getMyContactNum(MviewApplication.ctx));
            obj.put("ver", Constants.getversionnumber(MviewApplication.ctx));
            obj.put("apn_type", Config.getApnType(MviewApplication.ctx));
            obj.put("imsi", Constants.IMSI);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                obj.put("phone_imsi", " ");
            }else {
                obj.put("phone_imsi", Utils.getPhoneImsi(MviewApplication.ctx));
            }
            obj.put("apn", Config.getApnType(MviewApplication.ctx));
            obj.put("circle_name", Utils.getMyCircleName(MviewApplication.ctx));
            if (listenService.gps != null && listenService.gps.canGetLocation()) {
                obj.put("latitude", listenService.gps.getLatitude() + "");
                obj.put("longitude", listenService.gps.getLongitude() + "");
                obj.put("lat", listenService.gps.getLatitude() + "");
                obj.put("lon", listenService.gps.getLongitude() + "");
                obj.put("altitude", listenService.gps.getAltitude() + "");
            } else {
                obj.put("latitude", "0");
                obj.put("longitude", "0");
                obj.put("altitude", "0");
                obj.put("lat", "0");
                obj.put("lon", "0");
            }
            obj.put("lacid", Config.getlacid(MviewApplication.ctx));
            obj.put("pubid", "0");
            obj.put("clickid", "0");
            obj.put("cellid", Config.getcellid(MviewApplication.ctx));
            obj.put("operatorname", " ");

            obj.put("port", "4444");
            obj.put("country_code", Config.getUserCountry());
            obj.put("androidsdk", Build.VERSION.SDK_INT);
            obj.put("phonetype", mView_HealthStatus.phonetype);

            obj.put("date_time",Utils.getDateTime());
            obj.put("connection_type", mView_HealthStatus.connectionType);
            obj.put("network_type",mView_HealthStatus.strCurrentNetworkState);

            obj.put("operatorname", mView_HealthStatus.OperatorName);
            JSONObject sim1servingobj= Telephony_params.getsim1servingcellinfo();
            Utils.appendLog("ELOG_SIM1_SERVINGCELL_INFO: is "+sim1servingobj);

            if(sim1servingobj!=null) {
                try {
                    String csvSignalStrengthInfo = jsonObjectToCsvString(sim1servingobj.optJSONObject("signal_strength_info"));
                    String csvCellIdentityInfo = jsonObjectToCsvString(sim1servingobj.optJSONObject("cell_identity_info"));

                    obj.put("signal_strength_info",csvSignalStrengthInfo);
                    obj.put("cell_identity_info",csvCellIdentityInfo);
                    obj.put("cellid1", sim1servingobj.optString("cellid"));
                    obj.put("Lcellid", sim1servingobj.optString("Lcellid"));
                    obj.put("ratType", sim1servingobj.optString("ratType"));
                    obj.put("enb", sim1servingobj.optString("enb"));
                    obj.put("snr", sim1servingobj.optString("snr"));
                    obj.put("earfcn", sim1servingobj.optString("earfcn"));
                    obj.put("pci", sim1servingobj.optString("pci"));
                    obj.put("ta", sim1servingobj.optString("ta"));
                    obj.put("cqi", sim1servingobj.optString("cqi"));
                    obj.put("signalStrength", sim1servingobj.optString("signalStrength"));
                    obj.put("tac", sim1servingobj.optString("tac"));
                }catch (Exception e){
                    Utils.appendLog("ELOG_SEND_EVT_SERVINGCELL_INFO: "+e.getMessage());
                }

            }

            //  obj.put("imei", imei);
            obj.put("phonemodal", Build.MODEL);
            obj.put("phonebrand", Build.BRAND);


            obj.put("details", arr);


            String jobjstr = obj.toString();

            if (jobjstr != null && Config.isNetworkAvailable(MviewApplication.ctx)) {
                AllInOneAsyncTaskForEVT async =
                        new AllInOneAsyncTaskForEVT(MviewApplication.ctx,
                                eventTypeEnum,eventType,rowid,resultListner);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    Utils.appendLog("ELOG_SEND_EVT_SERVER: Final JSON Going to server is: "+jobjstr);
                    async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,jobjstr);
                }

            }

        } catch (Exception e) {
            resultListner.onResultObtained(false);

            Utils.appendLog("ELOG_SEND_EVT_SERVER: Exception"+e.getMessage());
            e.printStackTrace();
        }


    }


    public static void sendClientSocketSpeedEvent(JSONArray arr, AllInOneAsyncTaskForNetwork.AsyncTaskPurpose eventTypeEnum,String eventType)
    {
        System.out.println(" entering new speed test  for computing json");
        JSONObject obj = new JSONObject();
        try {
            obj.put("msg", "evt");
            obj.put("evt_type",eventType);
            obj.put("interface", "CLI");
            obj.put("prod", Config.product);
            obj.put("msisdn", Utils.getMyContactNum(MviewApplication.ctx));
            obj.put("ver", Constants.getversionnumber(MviewApplication.ctx));
            obj.put("apn_type", Config.getApnType(MviewApplication.ctx));
            obj.put("imsi", Constants.IMSI);
            obj.put("phone_imsi", Constants.IMSI);
            obj.put("apn", Config.getApnType(MviewApplication.ctx));
            if (listenService.gps != null && listenService.gps.canGetLocation()) {
                obj.put("latitude", listenService.gps.getLatitude() + "");
                obj.put("longitude", listenService.gps.getLongitude() + "");
            } else {
                obj.put("latitude", "0");
                obj.put("longitude", "0");
            }
            obj.put("lacid", Config.getlacid(MviewApplication.ctx));
            obj.put("pubid", "0");
            obj.put("clickid", "0");
            obj.put("cellid", Config.getcellid(MviewApplication.ctx));
            obj.put("operatorname", mView_HealthStatus.OperatorName);
            obj.put("ip","NA");
            obj.put("port", "4444");
            obj.put("country_code", Config.getUserCountry());
            obj.put("androidsdk", Build.VERSION.SDK_INT);
            obj.put("phonetype", mView_HealthStatus.phonetype);

            obj.put("date_time",Utils.getDateTime());
            obj.put("connection_type", mView_HealthStatus.connectionType);
            obj.put("network_type",mView_HealthStatus.prim_NetworkType);


            //  obj.put("imei", imei);
            obj.put("phonemodal", Build.MODEL);
            obj.put("phonebrand", Build.BRAND);


            obj.put("details", arr);


            String jobjstr=obj.toString();
            Log.i(TAG,"Json to send is "+jobjstr);
            if (jobjstr != null && Config.isNetworkAvailable(MviewApplication.ctx)) {
                AllInOneAsyncTaskForNetwork async =
                        new AllInOneAsyncTaskForNetwork(MviewApplication.ctx,
                                eventTypeEnum);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,jobjstr);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    // BY SWAPNIL BANSAL 30/03/2023
    public static void sendNewVideoTestEvent(JSONArray arr, AllInOneAsyncTaskForNetwork.AsyncTaskPurpose eventTypeEnum,String eventType)
    {
        System.out.println(" entering new json for video");

        JSONObject obj = new JSONObject();
        try {
            obj.put("msg", "evt");
            obj.put("evt_type",eventType);
            obj.put("interface", "CLI");
            obj.put("prod", Config.product);
            obj.put("msisdn", Utils.getMyContactNum(MviewApplication.ctx));
            obj.put("ver", Constants.getversionnumber(MviewApplication.ctx));
            obj.put("apn_type", Config.getApnType(MviewApplication.ctx));
            obj.put("imsi", Constants.IMSI);
            obj.put("phone_imsi", Constants.IMSI);
            obj.put("apn", Config.getApnType(MviewApplication.ctx));
            if (listenService.gps != null && listenService.gps.canGetLocation()) {
                obj.put("latitude", listenService.gps.getLatitude() + "");
                obj.put("longitude", listenService.gps.getLongitude() + "");
            } else {
                obj.put("latitude", "0");
                obj.put("longitude", "0");
            }
            obj.put("lacid", Config.getlacid(MviewApplication.ctx));
            obj.put("pubid", "0");
            obj.put("clickid", "0");
            obj.put("cellid", Config.getcellid(MviewApplication.ctx));
            obj.put("operatorname", " ");
            obj.put("ip","NA");
            obj.put("port", "4444");
            obj.put("country_code", Config.getUserCountry());
            obj.put("androidsdk", Build.VERSION.SDK_INT);
            obj.put("phonetype", mView_HealthStatus.phonetype);
            obj.put("date_time",Utils.getDateTime());
            obj.put("connection_type", mView_HealthStatus.connectionType);
            obj.put("network_type",mView_HealthStatus.prim_NetworkType);


            obj.put("phonemodal", Build.MODEL);
            obj.put("phonebrand", Build.BRAND);


            obj.put("details", arr);


            String jobjstr=obj.toString();
            System.out.println("Video json to send is "+jobjstr);
            if (jobjstr != null && Config.isNetworkAvailable(MviewApplication.ctx)) {
                AllInOneAsyncTaskForNetwork async = new AllInOneAsyncTaskForNetwork(MviewApplication.ctx, eventTypeEnum);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,jobjstr);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void sendNewWebTestEvent(JSONArray arr, AllInOneAsyncTaskForNetwork.AsyncTaskPurpose eventTypeEnum,String eventType)
    {
 System.out.println(" entering sendnewwebtestevent json");
        JSONObject obj = new JSONObject();
        try {
            obj.put("msg", "evt");
            obj.put("evt_type",eventType);
            obj.put("interface", "CLI");
            obj.put("prod", Config.product);
            obj.put("msisdn", Utils.getMyContactNum(MviewApplication.ctx));
            obj.put("ver", Constants.getversionnumber(MviewApplication.ctx));
            obj.put("apn_type", Config.getApnType(MviewApplication.ctx));
            obj.put("imsi", Constants.IMSI);
            obj.put("phone_imsi", Constants.IMSI);
            obj.put("apn", Config.getApnType(MviewApplication.ctx));
            if (listenService.gps != null && listenService.gps.canGetLocation()) {
                obj.put("latitude", listenService.gps.getLatitude() + "");
                obj.put("longitude", listenService.gps.getLongitude() + "");
            } else {
                obj.put("latitude", "0");
                obj.put("longitude", "0");
            }
            obj.put("lacid", Config.getlacid(MviewApplication.ctx));
            obj.put("pubid", "0");
            obj.put("clickid", "0");
            obj.put("cellid", Config.getcellid(MviewApplication.ctx));
            obj.put("operatorname", mView_HealthStatus.OperatorName);
            obj.put("ip","NA");
            obj.put("port", "4444");
            obj.put("country_code", Config.getUserCountry());
            obj.put("androidsdk", Build.VERSION.SDK_INT);
            obj.put("phonetype", mView_HealthStatus.phonetype);

            obj.put("date_time",Utils.getDateTime());
            obj.put("connection_type", mView_HealthStatus.connectionType);
            obj.put("network_type",mView_HealthStatus.prim_NetworkType);


            //  obj.put("imei", imei);
            obj.put("phonemodal", Build.MODEL);
            obj.put("phonebrand", Build.BRAND);


            obj.put("details", arr);


            String jobjstr=obj.toString();
            Log.i(TAG,"Json to send is "+jobjstr);
            if (jobjstr != null && Config.isNetworkAvailable(MviewApplication.ctx)) {
                AllInOneAsyncTaskForNetwork async =
                        new AllInOneAsyncTaskForNetwork(MviewApplication.ctx,
                                eventTypeEnum);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,jobjstr);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public static void sendUpdateDataRequest(Context context) {

        //  RequestResponse.context = context;
        Utils.appendLog("ELOG_UPDATE_DATA: Send UpdateData Request");
        JSONObject obj = new JSONObject();
        try {
            obj.put("msg", "ud");//update_data
            obj.put("os_version", "30");
            obj.put("device_info", " ");
            obj.put("interface", "CLI");
            obj.put("prod", Config.product);
            obj.put("msisdn",Utils.getMyContactNum(MviewApplication.ctx));
            obj.put("ver", Constants.getversionnumber(MviewApplication.ctx));
            obj.put("imsi", Utils.getImsi(MviewApplication.ctx));
            obj.put("phone_imsi", Utils.getImsi(MviewApplication.ctx));
            obj.put("latitude", "0");
            obj.put("longitude", "0");
            obj.put("lat", "0");
            obj.put("lon", "0");
            obj.put("lacid", "0");
            obj.put("pubid", "0");
            obj.put("clickid", "0");
            obj.put("cellid", "0");
            obj.put("apn", " ");
            obj.put("apn_type", "NA");
            obj.put("operatorname", " ");
            obj.put("ip", "NA");
            obj.put("port", "9999");
            obj.put("country_code", "IN");
            obj.put("androidsdk", Build.VERSION.SDK_INT);
            String jobjstr=obj.toString();
            System.out.println("sendUpdateDataRequest  Request  json is " + obj.toString());
           // Utils.appendLog("sendUpdateDataRequest  Request  json is " + obj.toString());
            if(jobjstr!=null && Config.isNetworkAvailable(MviewApplication.ctx))
            {
                AllInOneAsyncTaskForNetwork async = new AllInOneAsyncTaskForNetwork(MviewApplication.ctx, AllInOneAsyncTaskForNetwork.AsyncTaskPurpose.UD);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    Utils.appendLog("ELOG_UPDATE_DATA_JSON: is " +jobjstr);

                    async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,jobjstr);
                }

            }


        } catch (Exception e) {
            Utils.appendLog("ELOG_UPDATE_DATA_EXCEPTION: is " +e.getMessage());

            e.printStackTrace();
            System.out.println("the error in  sendUpdateDataRequest() is " + e.getMessage());

        }


    }

}

