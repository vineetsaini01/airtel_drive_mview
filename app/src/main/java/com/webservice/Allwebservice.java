package com.webservice;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellLocation;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import com.newmview.wifi.CapturedPhoneState;
import com.newmview.wifi.CommonFunctions;
import com.newmview.wifi.MyCall;
import com.newmview.wifi.MyPhoneStateListener;
import com.newmview.wifi.TinyDB;
import com.newmview.wifi.activity.MainActivity;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.listenService;
import com.newmview.wifi.mView_HealthStatus;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.NeighboringCellsInfo;
import com.newmview.wifi.other.Utils;
import com.services.AllServices;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.webservice.WebService.API_sendNeighboringCellsInfo;

public class Allwebservice{
    private static final int PERMISSIONS_REQUEST = 2;
    public static String URL = "http://198.12.250.223/mview/mview";
    //public static String URL = "";
    //htsatp://203.122.58.1818071/fapps/mview
    // http://180.151.5.13/mSignal/MobileData.aspx
    public static int api_openapp = 0;//44
    public static int api_callrecord = 1;//43
    public static int api_webtest = 2;//44
    public static int api_videotest = 3;//45
    public static int api_periodicdata = 4;
    public static int api_uploaddownloaddata = 5;
    public static String api_sendinit="initreq";
    public static String code_req="code_request";
    public static int api_reportissue=6;
    public static int api_neighboringcellsinfo=7;
    private static int rscp;
    private static int ecno;
    private static boolean yesflag=false;
    private static String query;
    public static String IMSI="";
    private static String gcmId;

    public static String sendPostRequest(String url1, String message) {
        int success = 0;
        String response = "";
        String errMsg = "";
//        try {
//            message = URLEncoder.encode(message, "UTF-8");
//        } catch (UnsupportedEncodingException e) {/
//            e.printStackTrace();
//        }
        InputStream in;
        try {
            // instantiate the URL object with the target URL of the resource to
            // request
            URL url = new URL(url1);

            // instantiate the HttpURLConnection with the URL object - A new
            // connection is opened every time by calling the openConnection
            // method of the protocol handler for this URL.
            // 1. This is the point where the connection is opened.
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // set connection output to true
            connection.setDoOutput(true);
            // instead of a GET, we're going to send using method="POST"
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(Constants.CONNECTION_TIMEOUT);
            OutputStream os = connection.getOutputStream();




            // instantiate OutputStreamWriter using the output stream, returned
            // from getOutputStream, that writes to this connection.
            // 2. This is the point where you'll know if the connection was
            // successfully established. If an I/O error occurs while creating
            // the output stream, you'll see an IOException.


            //   OutputStream o = connection.getOutputStream();
            if (os != null) {

                OutputStreamWriter writer = new OutputStreamWriter(os,"utf-8");

               /* BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
*/
                //BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(o, "UTF-8"));
                // write data to the connection. This is data that you are sending
                // to the server
                // 3. No. Sending the data is conducted here. We established the
                // connection with getOutputStream

                writer.write("CALLJSON=" + message);


                //o.write(postDataBytes);

                // Closes this output stream and releases any system resources
                // associated with this stream. At this point, we've sent all the
                // data. Only the outputStream is closed at this point, not the
                // actual connection

                writer.close();
                // if there is a response code AND that response code is 200 OK, do
                // stuff in the first if block
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // OK
                    System.out.println("response is ok from server");
                    in = connection.getInputStream();
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                    // otherwise, if any other status code is returned, or no status
                    // code is returned, do stuff in the else block
                } else {
                    System.out.println("response is error from server");
                    // Server returned HTTP error code.
                }
                success = 1;
            }
        } catch (MalformedURLException e) {

            errMsg = e.getMessage();
            System.out.println("exception in http response "+e.toString());
        } catch (IOException e) {

            errMsg = e.getMessage();
            System.out.println("exception in http response "+e.toString());
        } catch (Exception e) {
            errMsg = e.getMessage();
            System.out.println("exception in http response "+e.toString());
        }
        if (success == 0) {
        }
        return response;
    }//end sendPostRequest

    private static JSONObject getLTEParams() throws JSONException {
        JSONObject lteObject=new JSONObject();
        lteObject.put("pci", 0);
        lteObject.put("earfcn",0);
        lteObject.put("4G_ta", 0);
        lteObject.put("4G_CQI", 0);
        lteObject.put("4G_RSSI", 0);
        lteObject.put("RSRP", 0);
        lteObject.put("sinr", 0);
        lteObject.put("TAC",0);
        lteObject.put("enb", 0);
        lteObject.put("4G_cellid",0);
        lteObject.put("RSRQ",0);//11
        //
        lteObject.put("psc",0);
        lteObject.put("uarfcn",0);
        lteObject.put("3G_CQI",0);
        lteObject.put("3G_RSSI",0);
        lteObject.put("RSCP",0);
        lteObject.put("ecno",0);
        lteObject.put("3G_lac",0);
        lteObject.put("NodeBId",0);
        lteObject.put("3G_cellid",0);//9
        //
        lteObject.put("arfcn",0);
        lteObject.put("2G_ta",0);
        lteObject.put("rxlev",0);
        lteObject.put("rxqual",0);
        lteObject.put("2G_lac",0);
        lteObject.put("siteid",0);
        lteObject.put("2G_cellid",0);//7
        return lteObject;

    }

    private static JSONObject getGSMNetworkParams() throws  JSONException { JSONObject gsmObject=new JSONObject();
//1
        gsmObject.put("pci", 0);
        gsmObject.put("earfcn",0);
        gsmObject.put("4G_ta", 0);
        gsmObject.put("4G_CQI", 0);
        gsmObject.put("4G_RSSI", 0);
        gsmObject.put("RSRP", 0);
        gsmObject.put("sinr", 0);
        gsmObject.put("TAC",0);
        gsmObject.put("enb", 0);
        gsmObject.put("4G_cellid",0);
        gsmObject.put("RSRQ",0);

//12
        gsmObject.put("psc",0);
        gsmObject.put("uarfcn",0);
        gsmObject.put("3G_CQI",0);
        gsmObject.put("3G_RSSI",0);
        gsmObject.put("RSCP",0);
        gsmObject.put("ecno",0);
        gsmObject.put("3G_lac",0);
        gsmObject.put("NodeBId",0);
        gsmObject.put("3G_cellid",0);
        //21
        if(mView_HealthStatus.ARFCN!=null)
            gsmObject.put("arfcn",mView_HealthStatus.ARFCN);
        else
            gsmObject.put("arfcn",0);
        //22
        if(mView_HealthStatus.lteta!=null)
            gsmObject.put("2G_ta",mView_HealthStatus.lteta);
        else
            gsmObject.put("2G_ta",0);
        //23
        gsmObject.put("rxlev",MyPhoneStateListener.getRxLev());
        //24
        if(mView_HealthStatus.rxqualfor2g!=null)
            gsmObject.put("rxqual",mView_HealthStatus.rxqualfor2g);
        else
            gsmObject.put("rxqual",0);
        //25
        if(mView_HealthStatus.Lac!=null)
            gsmObject.put("2G_lac",mView_HealthStatus.Lac);
        else
            gsmObject.put("2G_lac",0);

        byte[] l_byte_array = new byte[4];
        l_byte_array = CommonFunctions.convertByteArray__p(Integer.parseInt(mView_HealthStatus.Cid));
        int l_RNC_ID = CommonFunctions.getRNCID_or_CID__p(l_byte_array, CommonFunctions.RNCID_C);
        int l_real_CID = CommonFunctions.getRNCID_or_CID__p(l_byte_array, CommonFunctions.CID_C);
        //26
        gsmObject.put("siteid",l_RNC_ID);
        //27
        gsmObject.put("2G_cellid",l_real_CID);
        //returning 27 params
        return  gsmObject;
    }

    private static JSONObject getThreeGNetworkParams() throws JSONException {

        JSONObject threeGObject=new JSONObject();
//1
        threeGObject.put("pci", 0);
        threeGObject.put("earfcn",0);
        threeGObject.put("4G_ta", 0);
        threeGObject.put("4G_CQI", 0);
        threeGObject.put("4G_RSSI", 0);
        threeGObject.put("RSRP", 0);
        threeGObject.put("sinr", 0);
        threeGObject.put("TAC",0);
        threeGObject.put("enb", 0);
        threeGObject.put("4G_cellid",0);
        threeGObject.put("RSRQ",0);
        //12
        if(mView_HealthStatus.Wcdma_Psc!=null)
            threeGObject.put("psc",mView_HealthStatus.Wcdma_Psc);
        else
            threeGObject.put("psc",0);//13
        if(mView_HealthStatus.Uarfcn!=null)
            threeGObject.put("uarfcn",mView_HealthStatus.Uarfcn);
        else threeGObject.put("uarfcn",0);
//14
        if(MyPhoneStateListener.getCQI()!=null)
            threeGObject.put("3G_CQI",MyPhoneStateListener.getCQI());
        else
            threeGObject.put("3G_CQI",0);
        //15
        threeGObject.put("3G_RSSI",MyPhoneStateListener.getRxLev());
        //16
        if (mView_HealthStatus.rscp!=null)
            threeGObject.put("RSCP",mView_HealthStatus.rscp);
        else
            threeGObject.put("RSCP",0);
        if(mView_HealthStatus.rscp!=null) {
            rscp = Integer.parseInt(mView_HealthStatus.rscp);
            ecno = rscp - MyPhoneStateListener.getRxLev();
        }

        //17

        threeGObject.put("ecno",ecno);
        //18
        if(mView_HealthStatus.Lac!=null)
            threeGObject.put("3G_lac",mView_HealthStatus.Lac);
        else
            threeGObject.put("3G_lac",0);
        //19
        if(mView_HealthStatus.nodeb_id!=null)
            threeGObject.put("NodeBId",mView_HealthStatus.nodeb_id);
        else
            threeGObject.put("NodeBId",0);
        //20
        byte[] l_byte_array = new byte[4];

        if(mView_HealthStatus.Cid!=null) {
            l_byte_array = CommonFunctions.convertByteArray__p(Integer.parseInt(mView_HealthStatus.Cid));
            int l_real_CID = CommonFunctions.getRNCID_or_CID__p(l_byte_array, CommonFunctions.CID_C);
            threeGObject.put("3G_cellid", l_real_CID);
        }
        else
        {
            threeGObject.put("3G_cellid", 0);
        }

       /* l_byte_array = CommonFunctions.convertByteArray__p(Integer.parseInt(mView_HealthStatus.Cid));
        int l_real_CID = CommonFunctions.getRNCID_or_CID__p(l_byte_array, CommonFunctions.CID_C);
        threeGObject.put("3G_cellid",l_real_CID);*/
        //21
        threeGObject.put("arfcn",0);
        threeGObject.put("2G_ta",0);
        threeGObject.put("rxlev",0);
        threeGObject.put("rxqual",0);
        threeGObject.put("2G_lac",0);
        threeGObject.put("siteid",0);
        threeGObject.put("2G_cellid",0);
//27params
        return threeGObject;
    }

    private static JSONObject getLTENetworkParams() throws JSONException {

        JSONObject lteObject=new JSONObject();
        System.out.println("periodic lte "+"pci "+mView_HealthStatus.ltePCI +"earfcn "+mView_HealthStatus.lteArfcn);
        //1
        if(mView_HealthStatus.ltePCI!=null)
            lteObject.put("pci", mView_HealthStatus.ltePCI);
        else
            lteObject.put("pci", 0);
        //2
        if(mView_HealthStatus.lteArfcn!=null)
            lteObject.put("earfcn",mView_HealthStatus.lteArfcn);
        else
            lteObject.put("earfcn",0);
        //3
        if(mView_HealthStatus.lteta!=null)
            lteObject.put("4G_ta", mView_HealthStatus.lteta);
        else
            lteObject.put("4G_ta", 0);

        //4
        if(MyPhoneStateListener.getCQI()!=null)
            lteObject.put("4G_CQI", MyPhoneStateListener.getCQI());
        else
            lteObject.put("4G_CQI",0);
        //5
        if(MyPhoneStateListener.getLTERSSI()!=null)
            lteObject.put("4G_RSSI", MyPhoneStateListener.getLTERSSI());
        else
            lteObject.put("4G_RSSI", 0);
        //6
        if(mView_HealthStatus.lteRSRP!=null)
            lteObject.put("RSRP", mView_HealthStatus.lteRSRP);
        else
            lteObject.put("RSRP",0);
        //7
        if(MyPhoneStateListener.getSNR()!=null)
            lteObject.put("sinr", MyPhoneStateListener.getSNR());
        else
            lteObject.put("sinr", 0);
        //8
        if(mView_HealthStatus.lteTAC!=null)
            lteObject.put("TAC", mView_HealthStatus.lteTAC);
        else
            lteObject.put("TAC", 0);
//9
        if(mView_HealthStatus.lteENB!=null)
            lteObject.put("enb", mView_HealthStatus.lteENB);
        else
            lteObject.put("enb", 0);
        //10
        if(mView_HealthStatus.Cid!=null) {
            int cid1 = Integer.parseInt(mView_HealthStatus.Cid) & 0xff;
            lteObject.put("4G_cellid",cid1);

        }

        else
        {
            lteObject.put("4G_cellid",0);

        }
        //11
        if(mView_HealthStatus.lteRSRQ!=null)

            lteObject.put("RSRQ", mView_HealthStatus.lteRSRQ);
        else
            lteObject.put("RSRQ", 0);
        //12
        lteObject.put("psc",0);
        lteObject.put("uarfcn",0);
        lteObject.put("3G_CQI",0);
        lteObject.put("3G_RSSI",0);
        lteObject.put("RSCP",0);
        lteObject.put("ecno",0);
        lteObject.put("3G_lac",0);
        lteObject.put("NodeBId",0);
        lteObject.put("3G_cellid",0);//20
        //21
        lteObject.put("arfcn",0);
        lteObject.put("2G_ta",0);
        lteObject.put("rxlev",0);
        lteObject.put("rxqual",0);
        lteObject.put("2G_lac",0);
        lteObject.put("siteid",0);
        lteObject.put("2G_cellid",0);//27


//returning 27 vals
        return lteObject;

    }

    public static String API_sendReportIssue(String lat, String lon,String msg,String type) {
        String res = "";
        JSONObject obj = new JSONObject();
        try {
            obj.put("datetime", Utils.getDateTime());
            obj.put("apitype", api_reportissue);
            obj.put("message",msg);
            obj.put("selected_type",type);
            obj.put("imsi",Constants.IMSI);
            obj.put("user_msisdn", Utils.getMyContactNum(MainActivity.context));

            if (lat.equals("") || lon.equals("")) {
                if (listenService.gps != null && listenService.gps.canGetLocation()) {
                    obj.put("lat", listenService.gps.getLatitude() + "");
                    obj.put("lon", listenService.gps.getLongitude() + "");
                } else {
                    obj.put("lat", 0);
                    obj.put("lon", 0);
                }
            } else {
                obj.put("lat", lat);
                obj.put("lon", lon);
            }
            obj.put("phonetype", mView_HealthStatus.phonetype);
            String imsi = "";
            String imei = "";
            String simOperatorName = "";
          /*  int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.context, Manifest.permission.READ_PHONE_STATE);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) MainActivity.context, new String[]{ Manifest.permission.READ_PHONE_STATE}, PERMISSIONS_REQUEST);

            } else {
                //TODOt
            }
*/

            if (listenService.telMgr != null) {
                mView_HealthStatus.OperatorName = listenService.telMgr.getNetworkOperatorName();
                imsi = listenService.telMgr.getSubscriberId();
                imei = listenService.telMgr.getDeviceId();
                simOperatorName = listenService.telMgr.getSimOperatorName();
            } else {
                TelephonyManager telMgr = (TelephonyManager) MainActivity.context.getSystemService(Context.TELEPHONY_SERVICE);
                if (ContextCompat.checkSelfPermission(MainActivity.context,
                        Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED )
                {
                    ActivityCompat.requestPermissions((Activity) MainActivity.context, new String[]{ Manifest.permission.READ_PHONE_STATE}, PERMISSIONS_REQUEST);
                    //do your request permission
                }else{
                    //if the permission is granted then do
                    imsi = telMgr.getSubscriberId();
                    imei = telMgr.getDeviceId();
                }
                IMSI=imsi;


                mView_HealthStatus.OperatorName = telMgr.getNetworkOperatorName();
                simOperatorName = telMgr.getSimOperatorName();
            }

            obj.put("operatorname", mView_HealthStatus.OperatorName);
            String android_id = Settings.Secure.getString(MainActivity.context.getContentResolver(), Settings.Secure.ANDROID_ID);
            String version = "0";
            try {
                PackageInfo pInfo = MainActivity.context.getPackageManager().getPackageInfo(MainActivity.context.getPackageName(), 0);
                version = pInfo.versionName;
                obj.put("deviceid", android_id);
                // obj.put("version", version);
                obj.put("version", Constants.getversionnumber(MainActivity.context));
            } catch (PackageManager.NameNotFoundException e) {

            }

            obj.put("imei", imei);

            String country = getUserCountry();
            obj.put("country", country);
            obj.put("simOperatorName", simOperatorName);

            obj.put("phonemodal", Build.MODEL);
            obj.put("phonebrand", Build.BRAND);
            obj.put("androidsdk", Build.VERSION.SDK_INT);
//18 till here
            JSONArray jsonArray = new JSONArray();

            if(MyPhoneStateListener.getNetworkType()==4)
            {
                jsonArray.put(getLTENetworkParams());
                // jsonArray.put(getThreeGParams());
                //jsonArray.put(getGSMParams());
            }
            else if(MyPhoneStateListener.getNetworkType()==3)
            {

                jsonArray.put(getThreeGNetworkParams());

            }
            else if(MyPhoneStateListener.getNetworkType()==2)
            {
                jsonArray.put(getGSMNetworkParams());
            }
            else {
                jsonArray.put(getLTEParams());

            }
            obj.put("Networkparams",jsonArray);



            String ss = "0";
            if(mView_HealthStatus.iCurrentNetworkState==4)
            {  if(mView_HealthStatus.lteRSRP!=null)
                ss= mView_HealthStatus.lteRSRP +"dbm";

            }
            else if(mView_HealthStatus.iCurrentNetworkState==3) {
                if(mView_HealthStatus.rscp!=null)
                    ss= mView_HealthStatus.rscp +"dbm";


            }
            else if(mView_HealthStatus.iCurrentNetworkState==2)
            {
                ss= MyPhoneStateListener.getRxLev()+"dbm";
            }

            obj.put("signalstrength",ss);

            System.out.println("json report issue "+obj);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Async_APIToRegister().execute(obj);
        return res;
    }

    @SuppressLint("MissingPermission")
    public static String API_sendRegister(String lat, String lon) {
        String res = "";
        JSONObject obj = new JSONObject();
        try {
            obj.put("apitype", api_openapp);
            // obj.put("imsi",Constants.)
            obj.put("imsi",Constants.IMSI);
            obj.put("user_msisdn", Utils.getMyContactNum(MainActivity.context));

            if (lat.equals("") || lat.equals("")) {
                if (listenService.gps != null && listenService.gps.canGetLocation()) {
                    obj.put("lat", listenService.gps.getLatitude() + "");
                    obj.put("lon", listenService.gps.getLongitude() + "");
                } else {
                    obj.put("lat", 0);
                    obj.put("lon", 0);
                }
            } else {
                obj.put("lat", lat);
                obj.put("lon", lon);
            }
            obj.put("phonetype", mView_HealthStatus.phonetype);
            //  String imsi = "";
            String imei = "";
            String simOperatorName = "";
          /*  int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.context, Manifest.permission.READ_PHONE_STATE);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) MainActivity.context, new String[]{ Manifest.permission.READ_PHONE_STATE}, PERMISSIONS_REQUEST);

            } else {
                //TODOt
            }
*/

            if (listenService.telMgr != null) {
                mView_HealthStatus.OperatorName = listenService.telMgr.getNetworkOperatorName();
                //   imsi = listenService.telMgr.getSubscriberId();
                imei = listenService.telMgr.getDeviceId();
                simOperatorName = listenService.telMgr.getSimOperatorName();
            } else {
                TelephonyManager telMgr = (TelephonyManager) MainActivity.context.getSystemService(Context.TELEPHONY_SERVICE);
                if (ContextCompat.checkSelfPermission(MainActivity.context,
                        Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED )
                {
                    ActivityCompat.requestPermissions((Activity) MainActivity.context, new String[]{ Manifest.permission.READ_PHONE_STATE}, PERMISSIONS_REQUEST);
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
            String android_id = Settings.Secure.getString(MainActivity.context.getContentResolver(), Settings.Secure.ANDROID_ID);
            String version = "0";
            try {
                PackageInfo pInfo = MainActivity.context.getPackageManager().getPackageInfo(MainActivity.context.getPackageName(), 0);
                version = pInfo.versionName;
                obj.put("deviceid", android_id);
                // obj.put("version", version);
                obj.put("version", Constants.getversionnumber(MainActivity.context));
            } catch (PackageManager.NameNotFoundException e) {

            }

            obj.put("imei", imei);

            String country = getUserCountry();
            obj.put("country", country);
            obj.put("simOperatorName", mView_HealthStatus.prim_carrierName);

            obj.put("phonemodal", Build.MODEL);
            obj.put("phonebrand", Build.BRAND);
            obj.put("androidsdk", Build.VERSION.SDK_INT);




            JSONArray jsonArray = new JSONArray();

            if(MyPhoneStateListener.getNetworkType()==4)
            {
                jsonArray.put(getLTENetworkParams());
                // jsonArray.put(getThreeGParams());
                //jsonArray.put(getGSMParams());
            }
            else if(MyPhoneStateListener.getNetworkType()==3)
            {

                jsonArray.put(getThreeGNetworkParams());

            }
            else if(MyPhoneStateListener.getNetworkType()==2)
            {
                jsonArray.put(getGSMNetworkParams());
            }
            else {
                jsonArray.put(getLTEParams());

            }
            obj.put("Networkparams",jsonArray);



            String ss = "0";
            if(MyPhoneStateListener.getNetworkType()==4)
            {  if(mView_HealthStatus.lteRSRP!=null)
                ss= mView_HealthStatus.lteRSRP +"dbm";

            }
            else if(MyPhoneStateListener.getNetworkType()==3) {
                if(mView_HealthStatus.rscp!=null)
                    ss= mView_HealthStatus.rscp +"dbm";


            }
            else if(MyPhoneStateListener.getNetworkType()==2)
            {
                ss= MyPhoneStateListener.getRxLev()+"dbm";
            }

            obj.put("signalstrength",ss);
            //new for another sim
            String sec_ss=getSecondSimSignalStrength();
            if(Utils.getSecondImsi(MainActivity.context)!=null) {

                obj.put("secondSimImsi", mView_HealthStatus.sec_imsi);
                obj.put("secondSimOperatorName", mView_HealthStatus.sec_carrierName);
                if(sec_ss!=null ) {
                    obj.put("secondSignalStrength", sec_ss);
                }
                else
                {
                    obj.put("secondSignalStrength",0+"dbm");
                }

            }
            else
            {
                obj.put("secondSimImsi",Constants.IMSI);
                obj.put("secondSimOperatorName", mView_HealthStatus.prim_carrierName);
                obj.put("secondSignalStrength",ss);


            }


            obj.put("gcm_id",Utils.getGcm_Id());
            System.out.println("json openapp "+obj);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Async_APIToRegister().execute(obj);
        return res;
    }

    private static String getSecondSimSignalStrength() {
        String sec_strength=null;
        if (mView_HealthStatus.second_cellInstance != null) {

            if (mView_HealthStatus.second_cellInstance.equalsIgnoreCase("lte")) {
                {

                    if (mView_HealthStatus.second_Rsrp < 0 && mView_HealthStatus.second_Rsrp > -75) {


                        sec_strength = mView_HealthStatus.second_Rsrp +"dbm";
                    } else if (mView_HealthStatus.second_Rsrp <= -75 && mView_HealthStatus.second_Rsrp > -95) {

                        sec_strength = mView_HealthStatus.second_Rsrp + "dbm";
                    } else if (mView_HealthStatus.second_Rsrp <= -95 && mView_HealthStatus.second_Rsrp > -115) {

                        sec_strength = mView_HealthStatus.second_Rsrp + "dbm";
                    }

                }


            } else if (mView_HealthStatus.second_cellInstance.equalsIgnoreCase("wcdma")) {

                if (mView_HealthStatus.second_rscp_3G < 0 && mView_HealthStatus.second_rscp_3G > -75) {

                    sec_strength = mView_HealthStatus.second_rscp_3G + "dbm";
                } else if (mView_HealthStatus.second_rscp_3G <= -75 && mView_HealthStatus.second_rscp_3G > -95) {

                    sec_strength = mView_HealthStatus.second_rscp_3G + "dbm";
                } else if (mView_HealthStatus.second_rscp_3G <= -95 && mView_HealthStatus.second_rscp_3G > -115) {

                    sec_strength = mView_HealthStatus.second_rscp_3G + "dbm";
                }


            } else if (mView_HealthStatus.second_cellInstance.equalsIgnoreCase("gsm")) {


                if (mView_HealthStatus.second_rxLev < 0 && mView_HealthStatus.second_rxLev > -75) {

                    sec_strength = mView_HealthStatus.second_rxLev + "dbm";
                } else if (mView_HealthStatus.second_rxLev <= -75 && mView_HealthStatus.second_rxLev > -95) {

                    sec_strength = mView_HealthStatus.second_rxLev + "dbm";
                } else if (mView_HealthStatus.second_rxLev <= -95 && mView_HealthStatus.second_rxLev > -115) {

                    sec_strength = mView_HealthStatus.second_rxLev + "dbm";
                }

            }
        }

        return sec_strength;
    }

    public static String API_sendCallRecord(MyCall obj) {
        String res = "";
        System.out.println("calling async of call" +obj.myLat +obj.myLon);
        new Async_APIToSendCallRecord().execute(obj);
        return res;
    }

    public static String API_sendWebTest(String src) {
        String res = "";
        AsyncTask<Object, Void, String> web= new Async_APIToSendWebTest(src).execute();
        if(AllServices.asyncTasks!=null )
            AllServices.asyncTasks.add(web);
        return res;
    }

    public static String API_sendVideoTest() {
        System.out.println("videotest ");
        String res = "";
        try {
            new Async_APIToSendVideoTest().execute();}
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("exception in videotesting is"+e.toString());
        }
        return res;
    }




    public static String API_sendPeriodicData(CapturedPhoneState obj) {
        String res = "";
        AsyncTask<Object, Void, String> periodic=new Async_APIToSendPeriodicData().execute(obj);
        if(AllServices.asyncTasks!=null )
            AllServices.asyncTasks.add(periodic);
        return res;
    }

    public static JSONObject getCallRecordJSON(MyCall obj) {
        System.out.println("in getcall json");
        JSONObject o = new JSONObject();
        JSONArray jsonArrayNew = new JSONArray();
        JSONObject callJson = new JSONObject();
        try {
            o.put("apitype", api_callrecord);
            o.put("userid", mView_HealthStatus.userid);
            o.put("version", Constants.getversionnumber(MviewApplication.ctx));
            o.put("imsi",Constants.IMSI);
            o.put("user_msisdn", Utils.getMyContactNum(MviewApplication.ctx));
            String temp = "M";
            long dura = 0;
            if (obj.isCallTaken) {
                temp = "S";
            }
            if (obj.isDroppedCall)
                temp = "F";

            dura = obj.endTimeInMS - obj.timeofcallInMS;
            callJson.put("CALLType", temp);
            callJson.put("InorOut", obj.calltype);//1 means incoming, 2 means outgoing
            if (obj.operator == null && listenService.telMgr != null) {
                obj.operator = listenService.telMgr.getNetworkOperatorName();
            }

            callJson.put("operatorname", obj.operator);

          /*  if (currentCallObject.calltype == 1)
                out.append("Incoming Phone Number " + currentCallObject.callerPhoneNumber + "\n");
            else
                out.append("Outgoing Phone Number " + currentCallObject.callerPhoneNumber + "\n");
*/
            if(obj.callerPhoneNumber!=null)
                callJson.put("Incoming", obj.callerPhoneNumber);//for outgoing as well
            else
                callJson.put("Incoming",0);
            System.out.println("calling innn "+obj.myLat +obj.myLon );

            //handle
            if (obj.myLat == null || obj.myLon == null) {
                if (listenService.gps != null && listenService.gps.canGetLocation()) {
                    obj.myLat = listenService.gps.getLatitude() + "";
                    obj.myLon = listenService.gps.getLongitude() + "";
                } else {
                    obj.myLat = "0";
                    obj.myLon = "0";
                }
            }

            callJson.put("lat", obj.myLat);
            callJson.put("lon", obj.myLon);
            callJson.put("speed", obj.speed);
            callJson.put("datetime", obj.timeofcallInMSNew);
            callJson.put("duration", dura);

            callJson.put("disconnectcause", obj.disconnectCause);
            if (obj.isRoaming)
                callJson.put("roaming", "1");
            else
                callJson.put("roaming", "0");
            callJson.put("network", MyPhoneStateListener.getNetworkType());
            //16 +27 here till here

            JSONArray jsonArray = new JSONArray();

            if(MyPhoneStateListener.getNetworkType()==4)
            {
                jsonArray.put(getLTENetworkParams());
                //jsonArray.put(getThreeGParams());
                //jsonArray.put(getGSMParams());
            }
            else if(MyPhoneStateListener.getNetworkType()==3)
            {

                jsonArray.put(getThreeGNetworkParams());

            }
            else if(MyPhoneStateListener.getNetworkType()==2)
            {
                //jsonArray.put(getLTEParams());
                //jsonArray.put(getThreeGParams());
                jsonArray.put(getGSMNetworkParams());
            }
            else {
                jsonArray.put(getLTEParams());
                //jsonArray.put(getThreeGParams());
                //jsonArray.put(getGSMParams());
            }
            callJson.put("Networkparams",jsonArray);





           /* JSONArray jsonCellLocationArray = new JSONArray();
            for (int i = 0; i < obj.cellLocationArr.size(); i++) {
                JSONObject locJson = getCellLoationJSON(obj.cellLocationArr.get(i), null);
                jsonCellLocationArray.put(locJson);
            }
            callJson.put("celllocations", jsonCellLocationArray);*/
           /* for (int i = 0; i < obj.serviceStateArr.size(); i++) {

            }
            JSONArray jsonSignalArray = new JSONArray();
            for (int ii = 0; ii < obj.signalStrengthArr.size(); ii++) {
                JSONObject signalJson = getSignalStrengthJSON(obj.signalStrengthArr.get(ii), null);
                jsonSignalArray.put(signalJson);
            }
            callJson.put("signals", jsonSignalArray);*/




            String ss = "0";
            if(MyPhoneStateListener.getNetworkType()==4)
            {  if(mView_HealthStatus.lteRSRP!=null)
                ss= mView_HealthStatus.lteRSRP +"dbm";

            }
            else if(MyPhoneStateListener.getNetworkType()==3) {
                if(mView_HealthStatus.rscp!=null)
                    ss= mView_HealthStatus.rscp +"dbm";


            }
            else if(MyPhoneStateListener.getNetworkType()==2)
            {
                ss= MyPhoneStateListener.getRxLev()+"dbm";
            }

            callJson.put("signalstrength",ss);



            jsonArrayNew.put(callJson);
            o.put("calls", jsonArrayNew);

        } catch (JSONException e) {
            e.printStackTrace();

        }
        System.out.println("call json is "+o.toString());
        return o;
    }

    static class Async_APIToRegister extends AsyncTask<Object, Void, Void> {
        @Override
        protected void onPreExecute() {
            System.out.println("registeration preexec/...");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Object... params) {
            JSONObject obj = (JSONObject) params[0];
            String arg = obj.toString();
            String res = sendPostRequest(URL, arg);
            if (!res.equals("")) {
                // SharedPreferences prefs = MainActivity.context.getSharedPreferences(MainActivity.MY_PREFS_NAME, MainActivity.context.MODE_PRIVATE);



                SharedPreferences.Editor editor = MainActivity.context.getSharedPreferences(MainActivity.MY_PREFS_NAME, MainActivity.context.MODE_PRIVATE).edit();
                System.out.println("reg flag "+MainActivity.SEND_API_REQUEST);
                if(!MainActivity.SEND_API_REQUEST) {
                    editor.putString("userid", "1");
                }
                else
                {
                    editor.remove("userid").apply();
                }

                editor.putString("installedSince", Utils.getDateTime());
                mView_HealthStatus.userid = "1";
                editor.apply();



            }
           /* if(MainActivity.SEND_API_REQUEST)
                MainActivity.SEND_API_REQUEST=false;*/

            System.out.println("registeration response"+res);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            System.out.println("registeration onpost");
            super.onPostExecute(result);
        }
    }

    static class Async_APIToSendCallRecord extends AsyncTask<Object, Void, Void> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            System.out.println("in pre of call ");
        }

        @Override
        protected Void doInBackground(Object... params) {
            MyCall obj = (MyCall) params[0];
            System.out.println("calling obj in doin "+obj.myLon +obj.myLat);
            JSONObject a = getCallRecordJSON(obj);
            String arg = a.toString();
            System.out.println("in doin of call");
            String res = sendPostRequest(URL, arg);
/*
            if (res.equals("")) {
                try {
                    //to be sent later
                    TinyDB tinydb = new TinyDB(ListenToPhoneState.mContext);
                    if (mView_HealthStatus.callRecordsTobeSentToServerArray == null) {
                        mView_HealthStatus.callRecordsTobeSentToServerArray = new ArrayList<String>();
                        for (Object object : tinydb.getListObject("CallRecords", String.class)) {
                            mView_HealthStatus.callRecordsTobeSentToServerArray.add(object.toString());
                        }
                    }

                    mView_HealthStatus.callRecordsTobeSentToServerArray.add(arg);
                    tinydb.putListObject1("CallRecords", mView_HealthStatus.callRecordsTobeSentToServerArray);
                } catch (Exception e) {
                    Log.e("mView MainAct WebSer", "Async_APIToSendCallRecord exception " + e.getMessage());
                    e.printStackTrace();
                }
            }
*/
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);
            System.out.println("in post of call ");
        }
    }

    static class Async_APIToSendPeriodicData extends AsyncTask<Object, Void, String> {
        private int rscp,ecno;
        private String source;
        private boolean periodic_exc;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Object... params) {
            CapturedPhoneState obj = (CapturedPhoneState) params[0];
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray=new JSONArray();
            this.source=obj.source;
            String res="";
            try {
                jsonObject.put("datetime", obj.basicPhoneState.captureTime);
                jsonObject.put("apitype", api_periodicdata);
                jsonObject.put("userid", mView_HealthStatus.userid);
                jsonObject.put("version", Constants.getversionnumber(MviewApplication.ctx));
                if(Constants.IMSI!=null) {
                    jsonObject.put("imsi", Constants.IMSI);
                }
                else
                {
                    jsonObject.put("imsi",Utils.getImsi(MviewApplication.ctx));
                }
                jsonObject.put("user_msisdn",Utils.getMyContactNum(MviewApplication.ctx));
                jsonObject.put("battery", obj.basicPhoneState.batterylevel);
                jsonObject.put("mobiledata", obj.basicPhoneState.simDataUsed);
                jsonObject.put("wifidata", obj.basicPhoneState.wifiDataUsed);
                jsonObject.put("type", mView_HealthStatus.strCurrentNetworkProtocol);
                jsonObject.put("network", MyPhoneStateListener.getNetworkType()); //2 means 2G, 3 means 3G, 4 means 4G
                jsonObject.put("mcc", mView_HealthStatus.mcc);
                jsonObject.put("mnc", mView_HealthStatus.mnc);
                jsonObject.put("connectiontypedetail", obj.networkType);
                if (obj.roaming) jsonObject.put("roaming", "1");
                else jsonObject.put("roaming", "0");
                /* jsonObject.put("roaming", obj.roaming);*/
                String lat = "0";
                String lon = "0";
                if (listenService.gps != null && listenService.gps.canGetLocation()) {
                    lat = listenService.gps.getLatitude() + "";
                    lon = listenService.gps.getLongitude() + "";
                }
                jsonObject.put("lat", lat);
                jsonObject.put("lon", lon);//17
                if(MyPhoneStateListener.getNetworkType()==4)
                {
                    jsonArray.put(getLTENetworkParams());
                   /* jsonArray.put(getThreeGParams());
                    jsonArray.put(getGSMParams());*/
                }
                else if(MyPhoneStateListener.getNetworkType()==3)
                {
                    //jsonArray.put(getLTEParams());
                    jsonArray.put(getThreeGNetworkParams());
                    // jsonArray.put(getGSMParams());
                }
                else if(MyPhoneStateListener.getNetworkType()==2)
                {
                    //jsonArray.put(getLTEParams());
                    //jsonArray.put(getThreeGParams());
                    jsonArray.put(getGSMNetworkParams());
                }
                else {
                    jsonArray.put(getLTEParams());
                    //jsonArray.put(getThreeGParams());
                    //jsonArray.put(getGSMParams());
                }
                jsonObject.put("Networkparams",jsonArray);//27
                String arg = jsonObject.toString();
                res = sendPostRequest(URL, arg);
                System.out.println("periodic json "+arg);
            }
            catch (JSONException e)
            {
                periodic_exc=true;
                Constants.service_started=true;
                e.printStackTrace();
            }
            catch (Exception e)

            {
                periodic_exc=true;
                Constants.service_started=true;
                e.printStackTrace();
            }
            finally {
                if(periodic_exc){
                    if(Utils.isNetworkAvailable(MviewApplication.ctx)) {
                       // AsyncTask<Object, Void, Void> periodic=new Async_SendNeighboringCellsInfo().execute(obj);
                        API_sendNeighboringCellsInfo();
                        Utils.startUploadService(MviewApplication.ctx);
                    }periodic_exc=false;}
            }
            System.out.println("res is  "+res);
            return res;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            System.out.println("result is to"+result);
          /*  SharedPreferences prefs = MviewApplication.ctx.getSharedPreferences(Constants.service_toggle, MODE_PRIVATE);
            String up_val = prefs.getString("upload_service", "0");
            if(Utils.checkifavailable(up_val)) {*/
            // if (up_val.equals("0")) {

            System.out.println("service source is "+source);

            if(Utils.checkifavailable(source))
            {
                if(source.equalsIgnoreCase("all"))
                {
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run()
                        {
                            if(Utils.isNetworkAvailable(MviewApplication.ctx)) {
                                // api 7 added by swapnil on 29/09/2021
                                API_sendNeighboringCellsInfo();
                               // AsyncTask<Object, Void, Void> periodic=new Async_SendNeighboringCellsInfo();
                                Utils.startUploadService(MviewApplication.ctx);
                            }
                        }
                    });
                }
            }

              /*  }
            }*/

            if(result.equals(""))
            {
                Constants.PERIODIC_API_RESPONSE=true;
            }
        }
    }

    static class Async_APIToSendWebTest extends AsyncTask<Object, Void, String> {

        private boolean web_exc=false;
        private String src=null;
        public Async_APIToSendWebTest(String src) {
            this.src=src;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Object... params) {

            JSONObject a = new JSONObject();
            String res = "";
            try {
                a.put("datetime", Utils.getDateTime());
                a.put("apitype", api_webtest);
                a.put("imsi",Constants.IMSI);
                a.put("user_msisdn", Utils.getMyContactNum(MviewApplication.ctx));
                a.put("userid", mView_HealthStatus.userid);
                a.put("version", Constants.getversionnumber(MviewApplication.ctx));
                a.put("connectiontype", mView_HealthStatus.connectionType);

                a.put("connectiontypedetail", mView_HealthStatus.strCurrentNetworkState);
                a.put("battery", Utils.getBattery(MviewApplication.ctx));
                String lat = "0";
                String lon = "0";

                if (listenService.gps != null && listenService.gps.canGetLocation()) {
                    lat = listenService.gps.getLatitude() + "";
                    lon = listenService.gps.getLongitude() + "";
                }
                a.put("lat", lat);
                a.put("lon", lon);
                if (mView_HealthStatus.roaming)
                    a.put("roaming", "1");
                else
                    a.put("roaming", "0");
                //   a.put("roaming", mView_HealthStatus.roaming);


                a.put("network", MyPhoneStateListener.getNetworkType());//13 till here

                //JSONArray jsonCellLocationArray = new JSONArray();
                JSONArray jsonArray = new JSONArray();

                if(MyPhoneStateListener.getNetworkType()==4)
                {
                    jsonArray.put(getLTENetworkParams());

                }
                else if(MyPhoneStateListener.getNetworkType()==3)
                {

                    jsonArray.put(getThreeGNetworkParams());

                }
                else if(MyPhoneStateListener.getNetworkType()==2)
                {
                    jsonArray.put(getGSMNetworkParams());
                }
                else {
                    jsonArray.put(getLTEParams());

                }
                a.put("Networkparams",jsonArray);



                String ss = "0";
                if(MyPhoneStateListener.getNetworkType()==4)
                {  if(mView_HealthStatus.lteRSRP!=null)
                    ss= mView_HealthStatus.lteRSRP +"dbm";

                }
                else if(MyPhoneStateListener.getNetworkType()==3) {
                    if(mView_HealthStatus.rscp!=null)
                        ss= mView_HealthStatus.rscp +"dbm";


                }
                else if(MyPhoneStateListener.getNetworkType()==2)
                {
                    ss= MyPhoneStateListener.getRxLev()+"dbm";
                }

                a.put("signalstrength",ss);


                JSONArray arr = new JSONArray();
                for (int i = 0; i < mView_HealthStatus.mySpeedTest.webtest.websiteArr.size(); i++) {
                    JSONObject webJson = new JSONObject();
                    webJson.put("url", mView_HealthStatus.mySpeedTest.webtest.websiteArr.get(i).url);
                    webJson.put("duration", mView_HealthStatus.mySpeedTest.webtest.websiteArr.get(i).timeTakenInMS);
                    webJson.put("size", mView_HealthStatus.mySpeedTest.webtest.websiteArr.get(i).bytes);
                    arr.put(webJson);
                }
                a.put("urls", arr);
                String arg = a.toString();

                res = sendPostRequest(URL, arg);
                System.out.println("webtest json is "+a.toString());
            } catch (JSONException e) {
                web_exc=true;
                e.printStackTrace();
            } catch (Exception e) {
                web_exc=true;
            }
            finally {
                if(Utils.checkifavailable(src)) {

                    if(src.equalsIgnoreCase("service"))
                    {
                        Constants.service_started=true;
                    }

                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            if(Utils.checkifavailable(src)) {

                if(src.equalsIgnoreCase("service"))
                {
                    Constants.service_started=true;
                }

            }



/*
            if(Utils.checkifavailable(src)) {
                if (src.equalsIgnoreCase("service")) {
                    CapturedPhoneState obj = Utils.getCapturedData(MviewApplication.ctx);
                    System.out.println("or obj " + obj);

                    if (obj != null) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (Utils.isNetworkAvailable(MviewApplication.ctx)) {
                                    WebService.API_sendPeriodicData(obj);

                                }
                            }
                        }, 60000);
                    }
                }
            }
*/



              /* }
           }*/
        }
    }

    static class Async_APIToSendVideoTest extends AsyncTask<Object, Void, Void> {
        @Override
        protected void onPreExecute() { super.onPreExecute();System.out.println("in pre execute of videotest api");
        }
        @Override
        protected Void doInBackground(Object... params) {

            JSONObject a = new JSONObject();
            String res = "";
            try {
                a.put("apitype", api_videotest);
                a.put("imsi",Constants.IMSI);
                a.put("user_msisdn", Utils.getMyContactNum(MainActivity.context));
                a.put("userid", mView_HealthStatus.userid);
                a.put("version", Constants.getversionnumber(MainActivity.context));
                a.put("connectiontype", mView_HealthStatus.connectionType);
                a.put("connectiontypedetail", mView_HealthStatus.strCurrentNetworkState);
                a.put("battery", mView_HealthStatus.batteryLevel);
                String lat = "0";
                String lon = "0";
                if (listenService.gps != null && listenService.gps.canGetLocation()) {
                    lat = listenService.gps.getLatitude() + "";
                    lon = listenService.gps.getLongitude() + "";
                }
                a.put("lat", lat);
                a.put("lon", lon);
                if (mView_HealthStatus.roaming)
                    a.put("roaming", "1");
                else
                    a.put("roaming", "0");
                //a.put("roaming", mView_HealthStatus.roaming);
                a.put("network", MyPhoneStateListener.getNetworkType());//12 till here
                JSONArray jsonArray = new JSONArray();

                if(MyPhoneStateListener.getNetworkType()==4)
                {
                    jsonArray.put(getLTENetworkParams());

                }
                else if(MyPhoneStateListener.getNetworkType()==3)
                {

                    jsonArray.put(getThreeGNetworkParams());

                }
                else if(MyPhoneStateListener.getNetworkType()==2)
                {

                    jsonArray.put(getGSMNetworkParams());
                }
                else {
                    jsonArray.put(getLTEParams());
                    //jsonArray.put(getThreeGParams());
                    // jsonArray.put(getGSMParams());
                }

                a.put("Networkparams",jsonArray);

               /* JSONArray jsonCellLocationArray = new JSONArray();

                if (MyPhoneStateListener.lastCellLocation != null) {
                    JSONObject locJson = getCellLoationJSON(MyPhoneStateListener.lastCellLocation, null);
                    jsonCellLocationArray.put(locJson);
                }
                a.put("celllocation", jsonCellLocationArray);*/
                String ss = "0";
               /* if (MyPhoneStateListener.lastSignalStrength != null) {
                    ss = MyPhoneStateListener.getSignalStrength(MyPhoneStateListener.lastSignalStrength);
                }
                a.put("signalstrength", ss);*/



                if(MyPhoneStateListener.getNetworkType()==4)
                {  if(mView_HealthStatus.lteRSRP!=null)
                    ss= mView_HealthStatus.lteRSRP +"dbm";

                }
                else if(MyPhoneStateListener.getNetworkType()==3) {
                    if(mView_HealthStatus.rscp!=null)
                        ss= mView_HealthStatus.rscp +"dbm";


                }
                else if(MyPhoneStateListener.getNetworkType()==2)
                {
                    ss= MyPhoneStateListener.getRxLev()+"dbm"; }
                a.put("signalstrength",ss);
                a.put("videoduration", mView_HealthStatus.mySpeedTest.video.videoDuration);
                a.put("totalBuferingTime", mView_HealthStatus.mySpeedTest.video.totalBuferingTime);
                a.put("totalPlayTime", mView_HealthStatus.mySpeedTest.video.totalPlayTime);
                a.put("noOfBuffering", mView_HealthStatus.mySpeedTest.video.noOfBuffering);
                a.put("datetime", Utils.getDateTime());
                String arg = a.toString();
                res = sendPostRequest(URL, arg);
                System.out.println("videotest json is "+a.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            System.out.println("in post execute of videotest");
        }
    }

    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name
            System.out.println("ip adress:  "+ipAddr);
            return !ipAddr.equals("");

        } catch (Exception e) {
            System.out.println("exception for ip "+e.toString());
            return false;
        }
    }

    public static class Async_SendUporDownloadtestResults extends AsyncTask<Object, Void, String> {
        int type;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("sftp upload pre");
        }

        @Override
        protected String doInBackground(Object... params) {
            System.out.println("sftp upload doin");
            JSONObject a = new JSONObject();
            String res = "";
            try {
                a.put("apitype", api_uploaddownloaddata);
                a.put("imsi",Constants.IMSI);
                a.put("user_msisdn", Utils.getMyContactNum(MviewApplication.ctx));
                a.put("userid", mView_HealthStatus.userid);
                a.put("version", Constants.getversionnumber(MviewApplication.ctx));
                a.put("connectiontype", Utils.chkDataConnectivity(MviewApplication.ctx));

                a.put("connectiontypedetail", mView_HealthStatus.strCurrentNetworkState);
                a.put("battery",Utils.getBattery(MviewApplication.ctx));
                String lat = "0";
                String lon = "0";

                if (listenService.gps != null && listenService.gps.canGetLocation()) {
                    lat = listenService.gps.getLatitude() + "";
                    lon = listenService.gps.getLongitude() + "";
                }
                a.put("lat", lat);
                a.put("lon", lon);
                if (mView_HealthStatus.roaming)
                    a.put("roaming", "1");
                else
                    a.put("roaming", "0");
                // a.put("roaming", mView_HealthStatus.roaming);
                a.put("network", MyPhoneStateListener.getNetworkType());
                JSONArray jsonArray = new JSONArray();

                if(MyPhoneStateListener.getNetworkType()==4)
                {
                    jsonArray.put(getLTENetworkParams());
                    // jsonArray.put(getThreeGParams());
                    //jsonArray.put(getGSMParams());
                }
                else if(MyPhoneStateListener.getNetworkType()==3)
                {
                    //jsonArray.put(getLTEParams());
                    jsonArray.put(getThreeGNetworkParams());
                    //jsonArray.put(getGSMParams());
                }
                else if(MyPhoneStateListener.getNetworkType()==2)
                {
                    //jsonArray.put(getLTEParams());
                    //jsonArray.put(getThreeGParams());
                    jsonArray.put(getGSMNetworkParams());
                }
                else {
                    jsonArray.put(getLTEParams());
                    //jsonArray.put(getThreeGParams());
                    //jsonArray.put(getGSMParams());
                }
                a.put("Networkparams",jsonArray);
                //  String arg = jsonObject.toString();



               /* if (MyPhoneStateListener.lastCellLocation != null) {
                    JSONObject locJson = getCellLoationJSON(MyPhoneStateListener.lastCellLocation, null);
                    jsonCellLocationArray.put(locJson);
                }
                a.put("celllocation", jsonCellLocationArray);*/
                String ss = "0";
                /*if (MyPhoneStateListener.lastSignalStrength != null) {
                    ss = MyPhoneStateListener.getSignalStrength(MyPhoneStateListener.lastSignalStrength);
                }
                a.put("signalstrength", ss);*/
                if(MyPhoneStateListener.getNetworkType()==4)
                {  if(mView_HealthStatus.lteRSRP!=null)
                    ss= mView_HealthStatus.lteRSRP +"dbm";

                }
                else if(MyPhoneStateListener.getNetworkType()==3) {
                    if(mView_HealthStatus.rscp!=null)
                        ss= mView_HealthStatus.rscp +"dbm";


                }
                else if(MyPhoneStateListener.getNetworkType()==2)
                {
                    ss= MyPhoneStateListener.getRxLev()+"dbm";
                }

                a.put("signalstrength",ss);

                int uporDown = Integer.parseInt(params[0].toString());
                this.type=uporDown;
                if (uporDown == 1) {
                    //upload
                    a.put("datetime", mView_HealthStatus.mySpeedTest.uploadtest.startTime);
                    a.put("sizeInBytes", mView_HealthStatus.mySpeedTest.uploadtest.sizeInBytes);
                    a.put("durationTakenInMS", mView_HealthStatus.mySpeedTest.uploadtest.timeTakenInMS);
                    a.put("DownloadorUpload", 1);
                } else {
                    //download
                    a.put("datetime", mView_HealthStatus.mySpeedTest.downloadtest.startTime);
                    a.put("sizeInBytes", mView_HealthStatus.mySpeedTest.downloadtest.sizeInBytes);
                    a.put("durationTakenInMS", mView_HealthStatus.mySpeedTest.downloadtest.timeTakenInMS);
                    a.put("DownloadorUpload", 2);
                }
                String arg = a.toString();
                res = sendPostRequest(URL, arg);
                System.out.println("upload/dwnld test json is "+a.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


         /*   SharedPreferences prefs = MviewApplication.ctx.getSharedPreferences(Constants.service_toggle, MODE_PRIVATE);
            boolean service_tg=prefs.getBoolean("download_key",false);
            if(service_tg)
            {*/

            if(mView_HealthStatus.mySpeedTest.uploadtest!=null) {

                if (Utils.checkifavailable(mView_HealthStatus.mySpeedTest.uploadtest.source)) {
                    System.out.println("or upload " + mView_HealthStatus.mySpeedTest.uploadtest.source);

                    if (this.type == 1) {
                        mView_HealthStatus.mySpeedTest.uploadtest.source = null;
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                if(Utils.isNetworkAvailable(MviewApplication.ctx)) {
                                    Utils.startDownloadService(MviewApplication.ctx);
                                }
                            }
                        });
              /* }
           }*/
                    }
                }
            }
            else if(mView_HealthStatus.mySpeedTest.downloadtest!=null) {
                if (Utils.checkifavailable(mView_HealthStatus.mySpeedTest.downloadtest.source)) {
                    System.out.println("or download " + mView_HealthStatus.mySpeedTest.downloadtest.source);

                    if (this.type == 2) {
                        mView_HealthStatus.mySpeedTest.downloadtest.source = null;

                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                if(Utils.isNetworkAvailable(MviewApplication.ctx)) {
                                    Utils.startWebSpeedService(MviewApplication.ctx);
                                }
                            }
                        });




                        // CapturedPhoneState obj = Utils.getCapturedData(MviewApplication.ctx);
                        //  System.out.println("or obj " + obj);
/*
            if (obj != null) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
if(Utils.isNetworkAvailable(MviewApplication.ctx)) {
    Utils.startWebSpeedService(MviewApplication.ctx);

}
                    }
                }, 60000);
            }
*/


              /* }
           }*/
                    }
                }
            }
            else {
                System.out.println("or is null");
            }


          /*  if(down_val.equals("0"))
            {*/
            //Utils.startDownloadService(MviewApplication.ctx);

            // }
        }

    }

    public static class Async_SendNeighboringCellsInfo extends AsyncTask<Object, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Object... params) {

            JSONObject a = new JSONObject();
            String res = "";
            try {
                a.put("datetime", Utils.getDateTime());
                a.put("apitype", api_neighboringcellsinfo);
                a.put("imsi",Constants.IMSI);
                a.put("user_msisdn", Utils.getMyContactNum(MainActivity.context));
                a.put("userid", mView_HealthStatus.userid);
                a.put("version", Constants.getversionnumber(MainActivity.context));
                a.put("connectiontype", mView_HealthStatus.connectionType);
                a.put("connectiontypedetail", mView_HealthStatus.strCurrentNetworkState);
                a.put("network", MyPhoneStateListener.getNetworkType());
                String lat = "0";
                String lon = "0";

                if (listenService.gps != null && listenService.gps.canGetLocation()) {
                    lat = listenService.gps.getLatitude() + "";
                    lon = listenService.gps.getLongitude() + "";
                }
                a.put("lat", lat);
                a.put("lon", lon);
                if (mView_HealthStatus.roaming)
                    a.put("roaming", "1");
                else
                    a.put("roaming", "0");//12
                System.out.println("network is "+ MyPhoneStateListener.getNetworkType());


                if(MyPhoneStateListener.getNetworkType()==4) {


                    a.put("neighboring_cells_info",getNeighboringCellsInfoForLte());

                }
                else if(MyPhoneStateListener.getNetworkType()==3)
                {
                    a.put("neighboring_cells_info",getNeighboringCellsInfoForWcdma());


                }
                else if(MyPhoneStateListener.getNetworkType()==2)
                {
                    a.put("neighboring_cells_info",getNeighboringCellsInfoForGSM());

                }
                else
                {
                    a.put("neighboring_cells_info",getNeighboringCellsInfo());//27

                }

                String arg = a.toString();
                res = sendPostRequest(URL, arg);
                System.out.println("neigboring cell info"+a.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private JSONArray getNeighboringCellsInfoForGSM() {


            {
                JSONArray jsonArray=new JSONArray();
                for(int i=0;i<NeighboringCellsInfo.gsm_neighboringCellList.size();i++)
                {
                    try {
                        JSONObject object=new JSONObject();
                        object.put("4G_PCI",0);
                        object.put("4G_EARFCN",0);
                        object.put("4G_TA",0);
                        object.put("4G_CQI",0);
                        object.put("4G_RSRQ",0);
                        object.put("4G_RSRP",0);
                        object.put("4G_TAC",0);
                        object.put("4G_SINR",0);
                        object.put("4G_CI",0);
                        object.put("4G_ENB",0);//10

                        //wcdma

                        object.put("3G_CID", 0);
                        object.put("3G_LAC", 0);
                        object.put("3G_PSC", 0);
                        object.put("3G_UARFCN", 0);
                        object.put("3G_RSCP", 0);
                        object.put("3G_RSSI", 0);
                        object.put("3G_CQI", 0);
                        object.put("3G_NODE_BID", 0);//8



                        //gsm
                        for(int j=0;j<NeighboringCellsInfo.gsm_neighboringCellList.get(0).size();j++) {
                            object.put(NeighboringCellsInfo.gsmParams.get(j),NeighboringCellsInfo.gsm_neighboringCellList.get(i).get(j));
                        }

                        jsonArray.put(object);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                return jsonArray;
            }
        }

        private JSONArray getNeighboringCellsInfoForWcdma() {
            JSONArray jsonArray=new JSONArray();
            for(int i=0;i<NeighboringCellsInfo.wcdma_neighboringCellList.size();i++)
            {
                try {
                    JSONObject object=new JSONObject();
                    object.put("4G_PCI",0);
                    object.put("4G_EARFCN",0);
                    object.put("4G_TA",0);
                    object.put("4G_CQI",0);
                    object.put("4G_RSRQ",0);
                    object.put("4G_RSRP",0);
                    object.put("4G_TAC",0);
                    object.put("4G_SINR",0);
                    object.put("4G_CI",0);
                    object.put("4G_ENB",0);//10

                    //wcdma

                    for(int j=0;j<NeighboringCellsInfo.wcdma_neighboringCellList.get(0).size();j++) {
                        object.put(NeighboringCellsInfo.wcdmaParams.get(j),NeighboringCellsInfo.wcdma_neighboringCellList.get(i).get(j));//8
                    }



                    //gsm
                    object.put("2G_LAC",0);
                    object.put("2G_CID",0);
                    object.put("2G_ARFCN",0);
                    object.put("2G_BSIC",0);
                    object.put("2G_PSC",0);
                    object.put("2G_RX_LEVEL",0);
                    object.put("2G_TA",0);
                    object.put("2G_SITE_ID",0);//8
                    jsonArray.put(object);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            return jsonArray;
        }

        private JSONArray getNeighboringCellsInfoForLte() {
            JSONArray jsonArray=new JSONArray();
            System.out.println("size of list is "+NeighboringCellsInfo.neighboringCellList.size());
            try {
                for (int i = 0; i < NeighboringCellsInfo.neighboringCellList.size(); i++) {


                    JSONObject lteObject = new JSONObject();

                    for (int j = 0; j < NeighboringCellsInfo.neighboringCellList.get(0).size(); j++) {
                        System.out.println("i is "+i +" j is "+j);
                        lteObject.put(NeighboringCellsInfo.lteParams.get(j), NeighboringCellsInfo.neighboringCellList.get(i).get(j));
                    }//10


                    lteObject.put("3G_CID", 0);
                    lteObject.put("3G_LAC", 0);
                    lteObject.put("3G_PSC", 0);
                    lteObject.put("3G_UARFCN", 0);
                    lteObject.put("3G_RSCP", 0);
                    lteObject.put("3G_RSSI", 0);
                    lteObject.put("3G_CQI", 0);
                    lteObject.put("3G_NODE_BID", 0);//8

                    //gsm
                    lteObject.put("2G_LAC", 0);
                    lteObject.put("2G_CID", 0);
                    lteObject.put("2G_ARFCN", 0);
                    lteObject.put("2G_BSIC", 0);
                    lteObject.put("2G_PSC", 0);
                    lteObject.put("2G_RX_LEVEL", 0);
                    lteObject.put("2G_TA", 0);
                    lteObject.put("2G_SITE_ID", 0);//8
                    jsonArray.put(lteObject);


                    System.out.println("json array size is "+jsonArray.length());
                }
            }

            catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println("json array size finally is "+jsonArray.length());
            return jsonArray;



        }

        private JSONArray getNeighboringCellsInfo() {

            JSONObject object=new JSONObject();
            JSONArray jsonArray=new JSONArray();

            try {

                object.put("4G_PCI",0);
                object.put("4G_EARFCN",0);
                object.put("4G_TA",0);
                object.put("4G_CQI",0);
                object.put("4G_RSRQ",0);
                object.put("4G_RSRP",0);
                object.put("4G_TAC",0);
                object.put("4G_SINR",0);
                object.put("4G_CI",0);
                object.put("4G_ENB",0);//10


                object.put("3G_CID", 0);
                object.put("3G_LAC", 0);
                object.put("3G_PSC", 0);
                object.put("3G_UARFCN", 0);
                object.put("3G_RSCP", 0);
                object.put("3G_RSSI", 0);
                object.put("3G_CQI", 0);
                object.put("3G_NODE_BID", 0);//8

                //gsm
                object.put("2G_LAC", 0);
                object.put("2G_CID", 0);
                object.put("2G_ARFCN", 0);
                object.put("2G_BSIC", 0);
                object.put("2G_PSC", 0);
                object.put("2G_RX_LEVEL", 0);
                object.put("2G_TA", 0);
                object.put("2G_SITE_ID", 0);
                object.put("signalstrength",0);//9
                jsonArray.put(object);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonArray;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

    }

    public static class Async_SendPendingCallRecordsToServer extends AsyncTask<Object, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Object... params) {
            try {
                TinyDB tinydb = (TinyDB) params[0];
                if (tinydb != null) {
                    //TinyDB tinydb = new TinyDB(MainActivity.context);

                    ArrayList<String> tempArr = new ArrayList<String>();
                    for (Object object : tinydb.getListObject("CallRecords", String.class)) {
                        String s = object.toString();
                        String res = sendPostRequest(URL, s);
                        if (res.equals("")) {
                            tempArr.add(s);
                            //mView_HealthStatus.callRecordsTobeSentToServerArray.add(s);
                        }
                    }//end for

                    if (mView_HealthStatus.callRecordsTobeSentToServerArray == null) {
                        mView_HealthStatus.callRecordsTobeSentToServerArray = tempArr;
                    } else {
                        for (int i = 0; i < tempArr.size(); i++) {
                            mView_HealthStatus.callRecordsTobeSentToServerArray.add(tempArr.get(i));
                        }
                    }
                    tinydb.remove("CallRecords");
                    tinydb.putListObject1("CallRecords", mView_HealthStatus.callRecordsTobeSentToServerArray);
                }
            } catch (Exception e) {
                Log.e("mView MainAct WebSer", "Async_SendPendingCallRecordsToServer exception " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

    }//end async

    private static JSONObject getCellLoationJSON(CellLocation obj, JSONObject jsonobj) {

        JSONObject locJson = jsonobj;

        if (jsonobj == null) {
            locJson = new JSONObject();
        }

        try {
            if (obj instanceof GsmCellLocation) {//Represents the cell location on a GSM phone.
                GsmCellLocation gcLoc = (GsmCellLocation) obj;
                locJson.put("type", "gsm");
                locJson.put("Cid", mView_HealthStatus.Cid);
                locJson.put("Lac", mView_HealthStatus.Lac);
                locJson.put("Psc", mView_HealthStatus.Psc);
                locJson.put("BaseStationId", "NA");
                locJson.put("NetworkId", "NA");
                locJson.put("SystemId", "NA");
                locJson.put("Pci", "NA");
                locJson.put("Tac", "NA");

                locJson.put("Ci", "NA");
                locJson.put("Arfcn", mView_HealthStatus.lteArfcn);
                locJson.put("Bsic", mView_HealthStatus.ltePCI);
                locJson.put("Earfcn", "NA");
                System.out.println("checking gsm in 1st "+locJson);


            } else if (obj instanceof CdmaCellLocation)
            {// /Represents the cell location on a CDMA phone.
                CdmaCellLocation ccLoc = (CdmaCellLocation) obj;
                locJson.put("type", "cdma");
                locJson.put("Cid", ccLoc.getBaseStationId());
                locJson.put("Lac", ccLoc.getNetworkId());
                locJson.put("Psc", "NA");
                locJson.put("Pci", "NA");
                locJson.put("Tac", "NA");
                locJson.put("Ci", "NA");
                locJson.put("Earfcn", "NA");
                locJson.put("Arfcn", "NA");
                locJson.put("Bsic", "NA");
                locJson.put("BaseStationId", ccLoc.getBaseStationId());
                locJson.put("NetworkId", ccLoc.getNetworkId());
                locJson.put("SystemId", ccLoc.getSystemId());
                System.out.println("checking cdma in 1st "+locJson);
            }

            /* * This method is preferred over using {@link
             * android.telephony.TelephonyManager#getCellLocation getCellLocation()}.
             * However, for older devices, <code>getAllCellInfo()</code> may return
             * null. In these cases, you should call {@link
             * android.telephony.TelephonyManager#getCellLocation getCellLocation()}
             * instead.
             */
            @SuppressLint("MissingPermizssion") List<CellInfo> cellInfo = listenService.telMgr.getAllCellInfo();
            System.out.println("cellinfooo size"+cellInfo.size()+"cellinfooo  "+cellInfo );
            for(final CellInfo m: cellInfo) {
                //     for (int i = 0; i < cellInfo.size(); i++) {

                // CellInfo m = cellInfo.get(i);
                //  System.out.println("cellinfooo "+cellInfo.get(i));
                Log.d("onCellInfoChanged", "CellInfoLte--" + m);

                if (m instanceof CellInfoLte) {
                    System.out.println("cellinfooo instance of lte");
                    locJson.put("type", "lte");
                    CellInfoLte cellInfoLte = (CellInfoLte) m;
                    CellIdentityLte c = cellInfoLte.getCellIdentity();
                    locJson.put("Pci", mView_HealthStatus.ltePCI + "");
                    locJson.put("Tac", mView_HealthStatus.lteTAC);
                    locJson.put("Ci", mView_HealthStatus.Cid);
                    locJson.put("Earfcn", mView_HealthStatus.lteArfcn);
                    locJson.put("Cid", "NA");
                    locJson.put("Lac", "NA");
                    locJson.put("Psc", "NA");
                    locJson.put("BaseStationId", "NA");
                    locJson.put("NetworkId", "NA");
                    locJson.put("SystemId", "NA");
                    locJson.put("Arfcn", "NA");
                    locJson.put("Bsic", "NA");
                    System.out.println("checking lte in last "+locJson);
                    Log.d("onCellInfoChanged", "CellInfoLte--" + m);
                } else if (m instanceof CellInfoGsm) {
                    System.out.println("cellinfooo instance of gsm");
                    CellInfoGsm cellInfogsm = (CellInfoGsm) m;
                    CellIdentityGsm c = cellInfogsm.getCellIdentity();

                    try {
                        locJson.put("type", "gsm");
                        locJson.put("Arfcn", mView_HealthStatus.lteArfcn);
                        locJson.put("Bsic", mView_HealthStatus.ltePCI);
                        locJson.put("Ci", "NA");
                        locJson.put("Earfcn", "NA");
                        locJson.put("Cid", mView_HealthStatus.Cid);
                        locJson.put("Lac", mView_HealthStatus.Cid);
                        locJson.put("Psc", mView_HealthStatus.Psc);
                        locJson.put("BaseStationId", "NA");
                        locJson.put("NetworkId", "NA");
                        locJson.put("SystemId", "NA");
                        locJson.put("Pci", "NA");
                        locJson.put("Tac", "NA");
                        System.out.println("checking gsm in last "+locJson);


                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("checking locjson "+locJson);
        return locJson;
    }

    private static JSONObject getSignalStrengthJSON(SignalStrength obj, JSONObject jsonObj) {
        JSONObject signalJson = jsonObj;

        if (signalJson == null)
            signalJson = new JSONObject();

        int getGsmBitErrorRate = 0;
        int getGsmSignalStrength = 0;
        try {
            if (obj.isGsm()) {
                getGsmBitErrorRate = obj.getGsmBitErrorRate();
                getGsmSignalStrength = obj.getGsmSignalStrength();
                signalJson.put("type", "gsm");
                signalJson.put("BitErrorRate", getGsmBitErrorRate);
                signalJson.put("SignalStrength", getGsmSignalStrength);
            } else if (obj.getCdmaDbm() > 0) {
                signalJson.put("type", "cdma");
                signalJson.put("Dbm", obj.getCdmaDbm());
                signalJson.put("Ecio", obj.getCdmaEcio());
            } else {
                //sharad2410
                signalJson.put("type", "lte");
                try {
                    Method[] methods = SignalStrength.class
                            .getMethods();
                    for (Method mthd : methods) {
                        if (mthd.getName().equals("getLteSignalStrength")) {
                            signalJson.put("rssi", mthd.invoke(obj) + "");
                        } else if (mthd.getName().equals("getLteRsrp")) {
                            signalJson.put("rsrp", mthd.invoke(obj) + "");
                        } else if (mthd.getName().equals("getLteRsrq")) {
                            signalJson.put("rsrq", mthd.invoke(obj) + "");
                        } else if (mthd.getName().equals("getLteRssnr")) {
                            signalJson.put("rssnr", mthd.invoke(obj) + "");
                        } else if (mthd.getName().equals("getLteCqi")) {
                            signalJson.put("cqi", mthd.invoke(obj) + "");
                        }
                    }//end for

                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }
        return signalJson;
    }

    public static String getUserCountry() {
        try {
            final TelephonyManager tm = (TelephonyManager) MainActivity.context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US);
            } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
        } catch (Exception e) {
        }
        return null;
    }


}
