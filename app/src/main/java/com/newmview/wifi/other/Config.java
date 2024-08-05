package com.newmview.wifi.other;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.telephony.CellInfoGsm;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

import com.newmview.wifi.activity.MainActivity;
import com.newmview.wifi.network.NetworkUtil;

import java.io.File;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by functionapps on 10/31/2018.
 */

public class Config {
    public static final int VIDEO_TEST_ID=1;
    public static final String TURN_ON_WIFI = "" ;
    public static  String PRODUCT_VERSION;
    public static String directoryname = "mview";
    public static String  RunDiagnostics="Run Diagnostics";
    public static String  wifiMapperName ="Create Wifi Coverage Map";
   /* public static File downloaded_root = new File(Environment.getExternalStorageDirectory()
            + File.separator + directoryname + File.separator + "Edit/");*/
    //by swapnil bansal  26/12/2022
    //public static File downloaded_root = new File(Environment.getExternalStorageDirectory()+"/");
    public static File downloaded_root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/");
    public static String onbackpress="back";
    public static int listSize=8;
    public static final String product="airtel_drive";
    public static int agentDefaultPeriod =120;
    // by swapnil bansal round robin part
    public  static  ArrayList<String> labels=new ArrayList<>();
              public static  ArrayList<String> floorTypesList=new ArrayList<>();
    public static ArrayList<String> getLabels()
    {
        labels=new ArrayList<>();
        labels.add("Bedroom");
        labels.add("Balcony");
        labels.add("Kitchen");
        labels.add("Bathroom");
        labels.add("Lobby");
        labels.add("Drawing Room");
        labels.add("Hall");
        labels.add("Parking");
        labels.add("Terrace");
        labels.add("Other");
        return labels;
    }
    public static ArrayList<String> getFloorTypes()
    {
        floorTypesList=new ArrayList<>();
        floorTypesList.add("Ground Floor");
        floorTypesList.add("First Floor");
        floorTypesList.add("Second Floor");
        floorTypesList.add("Third Floor");
        floorTypesList.add("Other");
        return floorTypesList;
    }
    public static String  getCurrentStatusOfInterface(String interfaceName)
    {
        String status= "NA";
        return status;
    }
    public static ArrayList<String> getTraceRouteHeaders()
    {
        ArrayList<String> columnHeaderList=new ArrayList<>();
        //columnHeaderList.add("COUNT");
        columnHeaderList.add("HOPS");
        columnHeaderList.add("TIME IN MS");

        return columnHeaderList;
    }    public static ArrayList<String> getHeatMapHeaders()
    {
        ArrayList<String> columnHeaderList=new ArrayList<>();
        columnHeaderList.add("Map Title");
        columnHeaderList.add("Site Details");
        columnHeaderList.add("Technology");
        columnHeaderList.add("Location Type");
        columnHeaderList.add("Device Id");
        columnHeaderList.add("Date Time");
        columnHeaderList.add("SSID");
        return columnHeaderList;
    }
    public static String  getDateTime()
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        //System.out.println(dtf.format(now));
        return dtf.format(now)+"";
    }
    public static String getApnType(Context context)
    {
        {
            String apn = null;
            String networkname = NetworkUtil.getConnectivityStatusString(context);

            if (networkname.equalsIgnoreCase("Wifi enabled")) {
                apn = "Wifi";
        /*    ConstantStrings.networkname = "Wifi";
            WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            ConstantStrings.cnetworkname = wifiInfo.getSSID();*/
            } else if (networkname.equalsIgnoreCase("Mobile data enabled")) {
         /*   ConstantStrings.networkname = "Mobile data";
            ConstantStrings.cnetworkname = tm.getNetworkOperatorName();*/
                apn = "Mobile Data";

            }
            return apn;
        }
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
        return "NA";
    }

    public static int getcellid(Context context) {
        int cellid = 0;

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {

            GsmCellLocation cellLocation = (GsmCellLocation) tm.getCellLocation();
            cellid = cellLocation.getCid();

        } catch (Exception e) {
            cellid = 0;
        }

        return cellid;
    }

    public static int getlacid(Context context) {
        int lacid = 0;

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {

            GsmCellLocation cellLocation = (GsmCellLocation) tm.getCellLocation();
            lacid = cellLocation.getLac();

        } catch (Exception e) {
            lacid = 0;
        }

        return lacid;

    }

    public static boolean isNetworkAvailable(Context context) {
        if(context!=null)
        {
            try {
                ConnectivityManager cm = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                // test for connection
                // txt_status.setText("Internet is working");
                // txt_status.setText("Internet Connection Not Present");
                return cm.getActiveNetworkInfo() != null
                        && cm.getActiveNetworkInfo().isAvailable()
                        && cm.getActiveNetworkInfo().isConnected();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return true;
    }

    private static  boolean getSIMStateBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {

        boolean isReady = false;

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try{

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimStateGemini = telephonyClass.getMethod(predictedMethodName, parameter);

            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimStateGemini.invoke(telephony, obParameter);

            if(ob_phone != null){
                int simState = Integer.parseInt(ob_phone.toString());
                if(simState == TelephonyManager.SIM_STATE_READY){
                    isReady = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }

        return isReady;
    }
    public static boolean isPhoneDualSim(Context context) {
        {
        /*TelephonyManager operator = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(context);

        boolean isDualSIM = telephonyInfo.isDualSIM();
return isDualSIM;*/

            String[] simStatusMethodNames = {"getSimStateGemini", "getSimState"};

            boolean first = false, second = false;

            for (String methodName: simStatusMethodNames) {
                // try with sim 0 first
                try {
                    first = getSIMStateBySlot(context, methodName, 0);
                    // no exception thrown, means method exists
                    second = getSIMStateBySlot(context, methodName, 1);
                    return first && second;
                } catch (GeminiMethodNotFoundException e) {
                    // method does not exist, nothing to do but test the next
                }
            }
            return false;

        }
    }

    public static ArrayList<String> getSubscriberInfoHeaders() {
        ArrayList<String> list=new ArrayList<>();
        list.add("Add subscriber name");
        list.add("Add subscriber id");
        return list;
    }

    private static class GeminiMethodNotFoundException extends Exception {

        private static final long serialVersionUID = -996812356902545308L;

        public GeminiMethodNotFoundException(String info) {
            super(info);
        }
    }

}
