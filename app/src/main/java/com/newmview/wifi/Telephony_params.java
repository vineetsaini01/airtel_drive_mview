package com.newmview.wifi;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityNr;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoNr;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthNr;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.functionapps.mview_sdk2.helper.CommonFunctions;
import com.functionapps.mview_sdk2.helper.mView_HealthStatus;
import com.functionapps.mview_sdk2.main.Mview;
import com.functionapps.mview_sdk2.service.ListenService;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.helper.Interfaces;
import com.newmview.wifi.helper.SharedPreferencesHelper;
import com.newmview.wifi.other.NeighboringCellsInfo;
import com.newmview.wifi.other.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Telephony_params {
    private static final String TAG = " "+Telephony_params.class.getSimpleName().toUpperCase(Locale.ROOT)+" ";
    static String network_type;
    static JSONObject finalobj;
    static JSONObject newFinalobj;
    private static String nosim = "No sim";

    public static JSONObject getsim1servingcellinfo() {


        getservingcell1info(new Interfaces.Network_list() {

            @Override
            public void onSuccess(ArrayList<HashMap<String, String>> array) {
                Utils.appendLog("ELOG_SIM1_SERVINGCELL_INFO: onSuccess: serving cell array is: " + array);

                if (array != null && !array.isEmpty()) {
                    finalobj = new JSONObject();
                    for (int i = 0; i < array.size(); i++) {
                        HashMap<String, String> hmap = new HashMap<>();
                        hmap = array.get(i);
                        String type = hmap.get("type");
                        String rsrp = hmap.get("rsrp");
                        String cqi = hmap.get("cqi");
                        String cellid = hmap.get("cellid");
                        String pci = hmap.get("pci");
                        String tac = hmap.get("tac");
                        String earfcn = hmap.get("earfcn");
                        String enb = hmap.get("enb");
                        String ta = hmap.get("ta");
                        String snr = hmap.get("snr");
                        String rsrq = hmap.get("rsrq");
                        String localcellid = hmap.get("localcellid");
                        String mnc = hmap.get("mnc");
                        String mcc = hmap.get("mcc");
                        String signalStrengthInfo = hmap.get("signal_strength_info");
                        String cellIdentityInfo = hmap.get("cell_identity_info");

                        if (type.equalsIgnoreCase("lte")){
                            try {
                                JSONObject signalStrengthJson = new JSONObject(signalStrengthInfo);
                                JSONObject cellIdentityJson = new JSONObject(cellIdentityInfo);


                                finalobj.put("type", type);

                                finalobj.put("signal_strength_info",signalStrengthJson);
                                finalobj.put("cell_identity_info",cellIdentityJson);

                                finalobj.put("network_type", hmap.get("network_type"));
                                finalobj.put("cell_identity", hmap.get("cell_identity"));
                                finalobj.put("ratType", hmap.get("ratType"));
                                finalobj.put("eUTRAN_cell_id", hmap.get("eUTRAN_cell_id"));
                                finalobj.put("eNodeB", hmap.get("eNodeB"));
                                finalobj.put("snr", hmap.get("snr"));
                                finalobj.put("earfcn", hmap.get("earfcn"));
                                finalobj.put("pci", hmap.get("pci"));
                                finalobj.put("TA", hmap.get("TA"));
                                finalobj.put("cqi", hmap.get("cqi"));
                                finalobj.put("RSRP", hmap.get("RSRP"));
                                finalobj.put("MNC_code", hmap.get("MNC_code"));
                                finalobj.put("MCC_code", hmap.get("MCC_code"));
                                finalobj.put("tac", hmap.get("tac"));
                                finalobj.put("lac_id", hmap.get("lac_id"));
                                finalobj.put("ASU", hmap.get("ASU"));
                                finalobj.put("RSSI", hmap.get("RSSI"));
                                finalobj.put("signal_quality", hmap.get("signal_quality"));
                                finalobj.put("RSRP_in_dBm", hmap.get("RSRP_in_dBm"));
                                finalobj.put("getRSRP", hmap.get("getRSRP"));

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else if (type.equalsIgnoreCase("2G")){
                            try {
                                JSONObject signalStrengthJson = new JSONObject(signalStrengthInfo);
                                JSONObject cellIdentityJson = new JSONObject(cellIdentityInfo);

                                finalobj.put("signal_strength_info",signalStrengthJson);
                                finalobj.put("cell_identity_info",cellIdentityJson);

                                finalobj.put("type", type);

                                finalobj.put("network_type", hmap.get("network_type"));
                                finalobj.put("cell_identity", hmap.get("cell_identity"));
                                finalobj.put("ratType", hmap.get("ratType"));
                                finalobj.put("ARFCN", hmap.get("ARFCN"));
                                finalobj.put("TA", hmap.get("TA"));
                                finalobj.put("RSSI", hmap.get("RSSI"));
                                finalobj.put("BSIC", hmap.get("BSIC"));

                                finalobj.put("MNC_code", hmap.get("MNC_code"));
                                finalobj.put("MCC_code", hmap.get("MCC_code"));
                                finalobj.put("lac_id", hmap.get("lac_id"));
                                finalobj.put("ASU", hmap.get("ASU"));
                                finalobj.put("BER", hmap.get("BER"));

                                finalobj.put("getRSSI", hmap.get("getRSSI"));
                                finalobj.put("signal_quality", hmap.get("signal_quality"));
                                finalobj.put("RSSI_in_dBm", hmap.get("RSSI_in_dBm"));

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else if (type.equalsIgnoreCase("3G")){
                            try {
                                JSONObject signalStrengthJson = new JSONObject(signalStrengthInfo);
                                JSONObject cellIdentityJson = new JSONObject(cellIdentityInfo);

                                finalobj.put("signal_strength_info",signalStrengthJson);
                                finalobj.put("cell_identity_info",cellIdentityJson);
                                finalobj.put("type", type);

                                finalobj.put("network_type", hmap.get("network_type"));
                                finalobj.put("cell_identity", hmap.get("cell_identity"));
                                finalobj.put("ratType", hmap.get("ratType"));
                                finalobj.put("UTRAN_cell_id", hmap.get("UTRAN_cell_id"));
                                finalobj.put("NodeB", hmap.get("NodeB"));
                                finalobj.put("Uarfcn", hmap.get("Uarfcn"));

                                finalobj.put("RSCP", hmap.get("RSCP"));
                                finalobj.put("MNC_code", hmap.get("MNC_code"));
                                finalobj.put("MCC_code", hmap.get("MCC_code"));
                                finalobj.put("PSC", hmap.get("PSC"));

                                finalobj.put("lac_id", hmap.get("lac_id"));
                                finalobj.put("ASU", hmap.get("ASU"));
                                finalobj.put("signal_quality", hmap.get("signal_quality"));
                                finalobj.put("RSCP_in_dBm", hmap.get("RSCP_in_dBm"));
                                finalobj.put("Ec_No", hmap.get("Ec_No"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }

                } else {
                    finalobj = addDefaultDataOnFailedResponse();


                }
            }

            @Override
            public void onFailure() {
                finalobj = addDefaultDataOnFailedResponse();
            }
        }, new Interfaces.NewNetwork_list() {
            @Override
            public void onSuccess(ArrayList<HashMap<String, String>> array) {

            }

            @Override
            public void onFailure() {

            }
        });

        return finalobj;
    }


    public static JSONObject getNewSim1servingcellinfo() {


        getservingcell1info(new Interfaces.Network_list() {

            @Override
            public void onSuccess(ArrayList<HashMap<String, String>> array) {

            }

            @Override
            public void onFailure() {
            }
        }, new Interfaces.NewNetwork_list() {
            @Override
            public void onSuccess(ArrayList<HashMap<String, String>> array) {

                Utils.appendLog("ELOG_NEW_JSON_SERVING_CELL_INFO is: "+array.size());

                if (array != null && !array.isEmpty()) {
                    newFinalobj = new JSONObject();
                    for (int i = 0; i < array.size(); i++) {
                        HashMap<String, String> hmap = new HashMap<>();
                        hmap = array.get(i);
                        String type = hmap.get("type");

                        if (type.equalsIgnoreCase("lte")){
                            try {
                                newFinalobj.put("4G_cellid", hmap.get("4G_cellid"));
                                newFinalobj.put("4G_cellid", hmap.get("4G_lcellid"));
                                newFinalobj.put("4G_ratType", type);
                                newFinalobj.put("4G_enb", hmap.get("4G_enb"));
                                newFinalobj.put("4G_rssnr", hmap.get("4G_rssnr"));
                                newFinalobj.put("4G_earfcn", hmap.get("4G_earfcn"));
                                newFinalobj.put("4G_rsrp", hmap.get("4G_rsrp"));
                                newFinalobj.put("4G_pci", hmap.get("4G_pci"));
                                newFinalobj.put("4G_ta", hmap.get("4G_ta"));
                                newFinalobj.put("4G_cqi", hmap.get("4G_cqi"));
                                newFinalobj.put("4G_rsrq", hmap.get("4G_rsrq"));
                                newFinalobj.put("4G_signalStrength", hmap.get("4G_rsrp"));
                                newFinalobj.put("4G_mnc", hmap.get("4G_mnc"));
                                newFinalobj.put("4G_mcc", hmap.get("4G_mcc"));
                                newFinalobj.put("4G_tac", hmap.get("4G_tac"));

                                newFinalobj.put("2G_rssi", "NA");
                                newFinalobj.put("2G_mcc", "NA");
                                newFinalobj.put("2G_mnc", "NA");
                                newFinalobj.put("2G_cellid", "NA");
                                newFinalobj.put("2G_lcellid",  "NA");
                                newFinalobj.put("2G_pci", "NA");
                                newFinalobj.put("2G_tac", "NA");
                                newFinalobj.put("2G_enb","NA");
                                newFinalobj.put("2G_ta", "NA");
                                newFinalobj.put("2G_snr", "NA");
                                newFinalobj.put("2G_cqi", "NA");
                                newFinalobj.put("2G_rsrq", "NA");
                                newFinalobj.put("2G_arfcn", "NA");

                                newFinalobj.put("3G_rsrp", "NA");
                                newFinalobj.put("3G_mcc", "NA");
                                newFinalobj.put("3G_mnc", "NA");
                                newFinalobj.put("3G_cellid", "NA");
                                newFinalobj.put("3G_lcellid", "NA");
                                newFinalobj.put("3G_psc", "NA");
                                newFinalobj.put("3G_tac", "NA");
                                newFinalobj.put("3G_enb", "NA");
                                newFinalobj.put("3G_ta", "NA");
                                newFinalobj.put("3G_snr", "NA");
                                newFinalobj.put("3G_cqi", "NA");
                                newFinalobj.put("3G_rsrq", "NA");
                                newFinalobj.put("3G_uarfcn", "NA");

                                Utils.appendLog("ELOG_NEW_SERVINGCELL_INFO_LTE_JSON is: "+newFinalobj);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else if (type.equalsIgnoreCase("3G")){
                            try {
                                newFinalobj.put("3G_cellid", hmap.get("3G_cellid"));
                                newFinalobj.put("3G_cellid", hmap.get("3G_lcellid"));
                                newFinalobj.put("3G_ratType", type);
                                newFinalobj.put("3G_enb", hmap.get("3G_enb"));
                                newFinalobj.put("3G_snr", hmap.get("3G_snr"));
                                newFinalobj.put("3G_uarfcn", hmap.get("3G_uarfcn"));
                                newFinalobj.put("3G_rsrp", hmap.get("3G_rsrp"));
                                newFinalobj.put("3G_psc", hmap.get("3G_psc"));
                                newFinalobj.put("3G_ta", hmap.get("3G_ta"));
                                newFinalobj.put("3G_cqi", hmap.get("3G_cqi"));
                                newFinalobj.put("3G_rsrq", hmap.get("3G_rsrq"));
                                newFinalobj.put("3G_signalStrength", hmap.get("3G_rsrp"));
                                newFinalobj.put("3G_mnc", hmap.get("3G_mnc"));
                                newFinalobj.put("3G_mcc", hmap.get("3G_mcc"));
                                newFinalobj.put("3G_tac", hmap.get("3G_tac"));

                                newFinalobj.put("2G_rssi", "NA");
                                newFinalobj.put("2G_mcc", "NA");
                                newFinalobj.put("2G_mnc", "NA");
                                newFinalobj.put("2G_cellid", "NA");
                                newFinalobj.put("2G_lcellid",  "NA");
                                newFinalobj.put("2G_pci", "NA");
                                newFinalobj.put("2G_tac", "NA");
                                newFinalobj.put("2G_enb","NA");
                                newFinalobj.put("2G_ta", "NA");
                                newFinalobj.put("2G_snr", "NA");
                                newFinalobj.put("2G_cqi", "NA");
                                newFinalobj.put("2G_rsrq", "NA");
                                newFinalobj.put("2G_arfcn", "NA");

                                newFinalobj.put("4G_rsrp", "NA");
                                newFinalobj.put("4G_mcc", "NA");
                                newFinalobj.put("4G_mnc", "NA");
                                newFinalobj.put("4G_cellid", "NA");
                                newFinalobj.put("4G_lcellid", "NA");
                                newFinalobj.put("4G_pci", "NA");
                                newFinalobj.put("4G_tac", "NA");
                                newFinalobj.put("4G_enb", "NA");
                                newFinalobj.put("4G_ta", "NA");
                                newFinalobj.put("4G_rssnr", "NA");
                                newFinalobj.put("4G_cqi", "NA");
                                newFinalobj.put("4G_rsrq", "NA");
                                newFinalobj.put("2G_earfcn", "NA");
                                Utils.appendLog("ELOG_NEW_SERVINGCELL_INFO_WCDMA_JSON is: "+newFinalobj);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else if (type.equalsIgnoreCase("2G")){
                            try {
                                newFinalobj.put("2G_cellid", hmap.get("2G_cellid"));
                                newFinalobj.put("2G_cellid", hmap.get("2G_cellid"));
                                newFinalobj.put("2G_ratType", type);
                                newFinalobj.put("2G_enb", hmap.get("2G_enb"));
                                newFinalobj.put("2G_snr", hmap.get("2G_snr"));
                                newFinalobj.put("2G_arfcn", hmap.get("2G_arfcn"));
                                newFinalobj.put("2G_rssi", hmap.get("2G_rssi"));
                                newFinalobj.put("2G_pci", hmap.get("2G_pci"));
                                newFinalobj.put("2G_ta", hmap.get("2G_ta"));
                                newFinalobj.put("2G_cqi", hmap.get("2G_cqi"));
                                newFinalobj.put("2G_rsrq", hmap.get("2G_rsrq"));
                                newFinalobj.put("2G_signalStrength", hmap.get("2G_rsrp"));
                                newFinalobj.put("2G_mnc", hmap.get("2G_mnc"));
                                newFinalobj.put("2G_mcc", hmap.get("2G_mcc"));
                                newFinalobj.put("2G_tac", hmap.get("2G_tac"));

                                newFinalobj.put("3G_rsrp", "NA");
                                newFinalobj.put("3G_mcc", "NA");
                                newFinalobj.put("3G_mnc", "NA");
                                newFinalobj.put("3G_cellid", "NA");
                                newFinalobj.put("3G_lcellid",  "NA");
                                newFinalobj.put("3G_psc", "NA");
                                newFinalobj.put("3G_tac", "NA");
                                newFinalobj.put("3G_enb","NA");
                                newFinalobj.put("3G_ta", "NA");
                                newFinalobj.put("3G_snr", "NA");
                                newFinalobj.put("3G_cqi", "NA");
                                newFinalobj.put("3G_rsrq", "NA");
                                newFinalobj.put("3G_urfcn", "NA");

                                newFinalobj.put("4G_rsrp", "NA");
                                newFinalobj.put("4G_mcc", "NA");
                                newFinalobj.put("4G_mnc", "NA");
                                newFinalobj.put("4G_cellid", "NA");
                                newFinalobj.put("4G_lcellid", "NA");
                                newFinalobj.put("4G_pci", "NA");
                                newFinalobj.put("4G_tac", "NA");
                                newFinalobj.put("4G_enb", "NA");
                                newFinalobj.put("4G_ta", "NA");
                                newFinalobj.put("4G_rssnr", "NA");
                                newFinalobj.put("4G_cqi", "NA");
                                newFinalobj.put("4G_rsrq", "NA");
                                newFinalobj.put("4G_earfcn", "NA");

                                Utils.appendLog("ELOG_NEW_SERVINGCELL_INFO_GSM_JSON is: "+newFinalobj);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                    }

                } else {
                    newFinalobj = addDefaultDataOnFailedResponse();
                }

            }

            @Override
            public void onFailure() {
                newFinalobj = addDefaultDataOnFailedResponse();

            }
        });

        return newFinalobj;
    }


    private static JSONObject addDefaultDataOnFailedResponse()
    {
        JSONObject defaultJsonData = new JSONObject();

        try {
            defaultJsonData.put("signal_strength_info","NA");
            defaultJsonData.put("cell_identity_info","NA");
            defaultJsonData.put("cellid", "NA");
            defaultJsonData.put("Lcellid", "NA");
            defaultJsonData.put("ratType", "NA");
            defaultJsonData.put("enb", "NA");
            defaultJsonData.put("snr", "NA");
            defaultJsonData.put("earfcn", "NA");
            defaultJsonData.put("rsrp", "NA");
            defaultJsonData.put("pci", "NA");
            defaultJsonData.put("ta", "NA");

            defaultJsonData.put("cqi", "NA");

            defaultJsonData.put("signalStrength", "NA");

            defaultJsonData.put("tac", "NA");
        } catch (Exception e) {
            Utils.appendLog(TAG+" Exception ocuured while adding default values "+e.getMessage());
            e.printStackTrace();
        }



        return defaultJsonData;
    }

    @SuppressLint("MissingPermission")
    public static int getNetworkType() {

        TelephonyManager teleMan = (TelephonyManager) MviewApplication.ctx.getSystemService(Context.TELEPHONY_SERVICE);

        return teleMan.getNetworkType();
    }

    @SuppressLint("MissingPermission")
    public static String getNetworkStringType() {


        TelephonyManager teleMan = (TelephonyManager) MviewApplication.ctx.getSystemService(Context.TELEPHONY_SERVICE);

        int networkType = teleMan.getNetworkType();
        System.out.println("network type recieved from getNetworkType is " + networkType);

        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
                network_type = "gsm";

                break;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                network_type = "gsm";

                break;
            case TelephonyManager.NETWORK_TYPE_CDMA:
                network_type = "gsm";

                break;
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                network_type = "gsm";

                break;
            case TelephonyManager.NETWORK_TYPE_IDEN:
                network_type = "gsm";

                break;

            case TelephonyManager.NETWORK_TYPE_UMTS:
                network_type = "wcdma";

                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                network_type = "wcdma";

                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                network_type = "wcdma";

                break;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                network_type = "wcdma";

                break;
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                network_type = "wcdma";

                break;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                network_type = "wcdma";

                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                network_type = "wcdma";

                break;
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                network_type = "wcdma";

                break;
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                network_type = "wcdma";

                break;
            case TelephonyManager.NETWORK_TYPE_LTE:


                network_type = check4gor5g();

                break;
            case TelephonyManager.NETWORK_TYPE_GSM:
                network_type = "gsm";

                break;
            case 19:

                network_type = "NR";
                break;
            case TelephonyManager.NETWORK_TYPE_NR:

                network_type = "NR";
                break;

            default:
                network_type = "lte";

                break;
        }
        return network_type;
    }


    private static String check4gor5g() {
        String type = "lte";
        return type;
    }


    @SuppressLint("MissingPermission")
    private static void getservingcell1info(Interfaces.Network_list network_list, Interfaces.NewNetwork_list newNetworkList) {
        TelephonyManager telMgr = (TelephonyManager) MviewApplication.ctx.getSystemService(Context.TELEPHONY_SERVICE);
        Log.d(TAG, "getcontext value: "+MviewApplication.ctx);

        List<CellInfo> cellInfo;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            cellInfo = telMgr.getAllCellInfo();
            System.out.println("cell info 1 " + cellInfo);
            Utils.appendLog( "ELOG_SIM1_SERVINGCELL_INFO: getservingcell1info: serving cell cellInfo is: "+cellInfo);


            if (cellInfo != null && cellInfo.size() > 0) {
                Log.d(TAG, "getservingcell1info: Called: "+ cellInfo);
                fetchCellsInfo1(cellInfo, network_list,newNetworkList);
            }
            else if(network_list!=null)
            {
                Log.d(TAG, "network list not null: "+ network_list);
                network_list.onFailure();
            }
        }

    }


    @SuppressLint("MissingPermission")
    private static void getservingcell2info(Interfaces.Network_list network_list) {
        TelephonyManager telMgr = (TelephonyManager) MviewApplication.ctx.getSystemService(Context.TELEPHONY_SERVICE);
        List<CellInfo> cellInfo;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            cellInfo = telMgr.getAllCellInfo();
            System.out.println("cell info 2 " + cellInfo);
            if (cellInfo != null && cellInfo.size() > 0) {
                fetchCellsInfo2(cellInfo, network_list);
            }
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static void fetchCellsInfo1(List<CellInfo> cellInfo, Interfaces.Network_list network_list, Interfaces.NewNetwork_list newNetworkList) {

        Log.d(TAG, "fetchCellsInfo1: called"+cellInfo);
        List<CellInfo> registeredCellInfo = new ArrayList<>();
       /* List<CellInfo> neighborCelllInfo = new ArrayList<>();

        NeighboringCellsInfo.neighboringCellList = new ArrayList<>();
        NeighboringCellsInfo.wcdma_neighboringCellList = new ArrayList<>();
        NeighboringCellsInfo.gsm_neighboringCellList = new ArrayList<>();
        NeighboringCellsInfo.nr_neighboringCellList = new ArrayList<>();*/

        CellInfo m;

        for (int i = 0; i < cellInfo.size(); i++) {
            m = cellInfo.get(i);
            if (m.isRegistered()) {

                registeredCellInfo.add(m);

            }
//            else {
//                neighborCelllInfo.add(m);
//            }
        }

        try {
            SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(MviewApplication.ctx);
            int rIndex = 0;
            if (registeredCellInfo != null && registeredCellInfo.size() > 0) {
                Utils.appendLog("ELOG_REGISTERED_CELL_INFO_SIZE: is "+registeredCellInfo.size());

                if (registeredCellInfo.size() == 1){
                    rIndex = 0;

                }else if (registeredCellInfo.size() == 2){
                    String subId = sharedPreferencesHelper.getSubId();
                    Utils.appendLog("ELOG_SUB_ID: is "+subId);
                    try{
                        int subIdVal = Integer.parseInt(subId);
                        if (subIdVal >0){
                            rIndex = subIdVal;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
                Utils.appendLog("ELOG_RINDEX_VALUE: is "+rIndex);
                if (registeredCellInfo.get(rIndex) != null) {
                    Log.d(TAG, "registered cell info: "+ registeredCellInfo);
                    addServingCellInfo1(registeredCellInfo.get(rIndex), network_list,newNetworkList);
                    Utils.appendLog("ELOG_SIM1_SERVINGCELL_INFO: registeredCellInfo for index "+rIndex+" is "+registeredCellInfo.get(rIndex));
                }
                else
                {
                    if(network_list!=null)
                    {
                        Log.d(TAG, "fetchCellsInfo1: network list "+ network_list);
                        network_list.onFailure();
                    }

                }

            }
            else
            {
                if(network_list!=null)
                {
                    Log.d(TAG, "fetchCellsInfo1: registered cell info null "+ network_list);
                    network_list.onFailure();
                }
            }
        }
        catch (Exception e)
        {
            Utils.appendLog(TAG+" Exception caught during fetching registered sim cell info 1 so return default values from here "+e.getMessage());
            Log.d(TAG, " Exception caught during fetching registered sim cell info 1: "+network_list);
            if(network_list!=null)
            {
                network_list.onFailure();
            }
            e.printStackTrace();
        }



    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static void fetchCellsInfo2(List<CellInfo> cellInfo, Interfaces.Network_list network_list) {

        List<CellInfo> registeredCellInfo = new ArrayList<>();
       /* List<CellInfo> neighborCelllInfo = new ArrayList<>();

        NeighboringCellsInfo.neighboringCellList = new ArrayList<>();
        NeighboringCellsInfo.wcdma_neighboringCellList = new ArrayList<>();
        NeighboringCellsInfo.gsm_neighboringCellList = new ArrayList<>();
        NeighboringCellsInfo.nr_neighboringCellList = new ArrayList<>();*/

        CellInfo m;

        for (int i = 0; i < cellInfo.size(); i++) {
            m = cellInfo.get(i);
            if (m.isRegistered()) {
                registeredCellInfo.add(m);

            }
//            else {
//                neighborCelllInfo.add(m);
//            }
        }


        if (registeredCellInfo != null && registeredCellInfo.size() > 0) {

            Log.d(TAG, "fetchCellsInfo2: registeredCellInfo size() is: "+registeredCellInfo.size());
            if (registeredCellInfo.size() > 1) {
                    addServingCellInfo2(registeredCellInfo.get(1), network_list);
            }
        }


    }


    public static void addServingCellInfo1(CellInfo m, Interfaces.Network_list network_list,Interfaces.NewNetwork_list newNetworkList) {
        Log.d(TAG, "addServingCellInfo1: "+m);
        Utils.appendLog("ELOG_SIM1_SERVINGCELL_INFO: addServingCellInfo1 Cellinfo type is "+m);
        if (m != null) {

            if (m instanceof CellInfoGsm) {

                CellInfoGsm cellInfogsm = (CellInfoGsm) m;
                CellIdentityGsm c = cellInfogsm.getCellIdentity();
                CellSignalStrengthGsm ss = cellInfogsm.getCellSignalStrength();
                getGsmCellInfoForFirstSim(ss, c, cellInfogsm, network_list);

//                getGsmNewCellInfoForSim1(ss,c,cellInfogsm,newNetworkList);

            } else if (m instanceof CellInfoLte) {

                CellInfoLte cellInfoLte = (CellInfoLte) m;
                CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
                CellSignalStrengthLte ss = cellInfoLte.getCellSignalStrength();
                getLteCellInfoForFirstSim(ss, cellIdentityLte, network_list);

//                getLteNewCellInfoForFirstSim(ss,cellIdentityLte,newNetworkList);
            } else if (m instanceof CellInfoWcdma) {

                CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) m;
                CellIdentityWcdma cellIdentityWcdma = cellInfoWcdma.getCellIdentity();
                CellSignalStrengthWcdma ss = cellInfoWcdma.getCellSignalStrength();
                getWcdmaCellInfoForFirstSim(ss, cellIdentityWcdma, network_list);

//                getWcdmaNewCellInfoForFirstSim(ss,cellIdentityWcdma,newNetworkList);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (m instanceof CellInfoNr) {

                    CellInfoNr cellInfoNr = (CellInfoNr) m;
                    CellIdentityNr cellIdentityNr = (CellIdentityNr) cellInfoNr.getCellIdentity();
                    CellSignalStrengthNr ss = (CellSignalStrengthNr) cellInfoNr.getCellSignalStrength();
                    getCellInfoForFirstSim5G(ss, cellIdentityNr, network_list);

                }
            }
        }
        else
        {
            if(network_list!=null)
            {
                network_list.onFailure();
            }
        }


    }

    private static void getGsmNewCellInfoForSim1(CellSignalStrengthGsm ss, CellIdentityGsm c, CellInfoGsm cellInfogsm, Interfaces.NewNetwork_list network_list) {
        {

            HashMap<String, String> hmap = new HashMap<>();
            hmap.put("type", "2G");


            hmap.put("2G_mcc", "" + cellInfogsm.getCellIdentity().getMcc());
            hmap.put("2G_mnc", "" + cellInfogsm.getCellIdentity().getMnc());



            int cid = c.getCid();
            if (!Utils.isMaxint(cid)) {
                com.functionapps.mview_sdk2.helper.mView_HealthStatus.Cid = String.valueOf(cid);
                hmap.put("2G_cellid", "" + cid);
                try {


                    String cellidHex = Utils.DecToHex(cid);
                    String localcellid = cellidHex.substring(cellidHex.length() - 2);
                    int cid1 = Integer.parseInt(localcellid);

                    hmap.put("2G_lcellid", "" + cid1);


                } catch (Exception e) {

                    hmap.put("2G_lcellid", "NA");
                    e.printStackTrace();
                }

            } else {
                hmap.put("2G_cellid", "NA");
                hmap.put("2G_lcellid", "NA");
            }

            int psc = c.getPsc();
            if (!Utils.isMaxint(psc)) {
                hmap.put("2G_pci", "" + psc);


            } else {
                hmap.put("2G_pci", "NA");

            }
            hmap.put("2G_tac", "NA");


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                hmap.put("2G_arfcn", "" + cellInfogsm.getCellIdentity().getArfcn());

            } else {
                hmap.put("2G_arfcn", "NA");




            }


            hmap.put("2G_enb", "" + Utils.getenb(cid));



            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {

                    if (!Utils.isMaxint(ss.getTimingAdvance())) {
                        String ta = String.valueOf(ss.getTimingAdvance());

                        hmap.put("2G_ta", ta);



                    } else {
                        hmap.put("2G_ta", "NA");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    hmap.put("2G_ta", "NA");
                }

                //System.out.println("ta is "+ta);
                //	//Toast.makeText(mContext, "ta is "+ta, //Toast.LENGTH_SHORT).show();

            } else {
                hmap.put("2G_ta", "NA");

                com.functionapps.mview_sdk2.helper.mView_HealthStatus.lteta = "0";
            }


            hmap.put("2G_snr", "NA");


            try {


                if (!Utils.isMaxint(Integer.parseInt(com.functionapps.mview_sdk2.helper.mView_HealthStatus.lteCQI))) {
                    hmap.put("2G_cqi", com.functionapps.mview_sdk2.helper.mView_HealthStatus.lteCQI);


                } else {
                    hmap.put("2G_cqi", "NA");
                }
            } catch (Exception e) {
                hmap.put("2G_cqi", "NA");
                e.printStackTrace();
            }
            int dbm = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                dbm = cellInfogsm.getCellSignalStrength().getRssi();
            } else {
                hmap.put("2G_rssi", "NA");
            }
            if (!Utils.isMaxint(dbm)) {
                hmap.put("2G_rssi", "" + dbm);
            } else {
                hmap.put("2G_rssi", "NA");



            }


            int asus = cellInfogsm.getCellSignalStrength().getAsuLevel();


            int level = cellInfogsm.getCellSignalStrength().getLevel();
            com.functionapps.mview_sdk2.helper.mView_HealthStatus.gsmSignalStrength = cellInfogsm.getCellSignalStrength().getDbm();
            System.out.println("ss in gsm " + dbm);

            if (!Utils.isMaxint(com.functionapps.mview_sdk2.helper.mView_HealthStatus.gsmSignalStrength)) {
                hmap.put("2G_rsrq", "" + com.functionapps.mview_sdk2.helper.mView_HealthStatus.gsmSignalStrength);
            } else {
                hmap.put("2G_rsrq", "NA");

            }
            com.functionapps.mview_sdk2.helper.mView_HealthStatus.lteasus = asus + "";
            com.functionapps.mview_sdk2.helper.mView_HealthStatus.ltedbm = dbm + "";
            com.functionapps.mview_sdk2.helper.mView_HealthStatus.ltelevel = level + "";


            int lac = c.getLac();


            com.functionapps.mview_sdk2.helper.mView_HealthStatus.Psc = String.valueOf(psc);
//            short cid_short = (short) cid;


            com.functionapps.mview_sdk2.helper.mView_HealthStatus.Lac = String.valueOf(lac);


            com.newmview.wifi.mView_HealthStatus.servingcell1info.add(hmap);

            if (network_list != null) {
                network_list.onSuccess(com.newmview.wifi.mView_HealthStatus.servingcell1info);
            }



        }
    }


    private static void getWcdmaNewCellInfoForFirstSim(CellSignalStrengthWcdma ss, CellIdentityWcdma cellIdentityWcdma, Interfaces.NewNetwork_list newNetworkList) {
        {

            HashMap<String, String> hmap = new HashMap<>();

            hmap.put("type", "3G");
            com.functionapps.mview_sdk2.helper.mView_HealthStatus.rscp = String.valueOf(ss.getDbm());
            if (ss.getDbm() != Integer.MAX_VALUE) {
                hmap.put("3G_rsrp", "" + ss.getDbm());

            } else {
                hmap.put("3G_rsrp", "NA");


            }
            com.functionapps.mview_sdk2.helper.mView_HealthStatus.mcc_first = cellIdentityWcdma.getMcc();


            hmap.put("3G_mcc", "" + cellIdentityWcdma.getMcc());
            com.functionapps.mview_sdk2.helper.mView_HealthStatus.mnc_first = cellIdentityWcdma.getMnc();
            hmap.put("3G_mnc", "" + cellIdentityWcdma.getMnc());

            int longCid = cellIdentityWcdma.getCid();
            if (!Utils.isMaxint(longCid)) {
                hmap.put("3G_cellid", "" + longCid);
                try {


                    String cellidHex = Utils.DecToHex(longCid);
                    String localcellid = cellidHex.substring(cellidHex.length() - 2);
                    int cid1 = Integer.parseInt(localcellid);

                    hmap.put("3G_lcellid", "" + cid1);
                } catch (Exception e) {

                    hmap.put("3G_lcellid", "NA");
                    e.printStackTrace();
                }

            } else {
                hmap.put("3G_lcellid", "NA");
                hmap.put("3G_cellid", "" + "NA");
            }


            int psc = cellIdentityWcdma.getPsc();


            if (!Utils.isMaxint(psc)) {
                hmap.put("3G_psc", "" + psc);
            } else {
                hmap.put("3G_psc", "" + "NA");
            }
            hmap.put("3G_tac", "NA");


            int uarfcn = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uarfcn = cellIdentityWcdma.getUarfcn();
            }


            if (uarfcn != Integer.MAX_VALUE) {
                hmap.put("3G_uarfcn", "" + uarfcn);
            } else {
                hmap.put("3G_uarfcn", "NA");

            }

            hmap.put("3G_snr", "NA");
            hmap.put("3G_cqi", "NA");


            hmap.put("3G_ta", "NA");

            String signalstrength = ss.toString().replace("CellSignalStrengthWcdma:", "");


            hmap.put("3G_rsrq", signalstrength);


            com.functionapps.mview_sdk2.helper.mView_HealthStatus.Wcdma_Psc = String.valueOf(psc);
            //	mView_HealthStatus.lteArfcn=String.valueOf(uarfcn);
            com.functionapps.mview_sdk2.helper.mView_HealthStatus.Uarfcn = String.valueOf(uarfcn);


            hmap.put("3G_enb", "" + Utils.getenb(longCid));



            com.newmview.wifi.mView_HealthStatus.servingcell1info.add(hmap);
            if (newNetworkList != null) {
                newNetworkList.onSuccess(com.newmview.wifi.mView_HealthStatus.servingcell1info);
            }

        }

    }

    private static void getLteNewCellInfoForFirstSim(CellSignalStrengthLte ss, CellIdentityLte cellIdentityLte, Interfaces.NewNetwork_list newNetworkList) {
        {

            Utils.appendLog("ELOG_LTE_CELL_IDENTITY: is: "+cellIdentityLte.toString());
            Utils.appendLog("ELOG_LTE_CELL_SIGNAL_STRENGTH: is: "+ss.toString());


            HashMap<String, String> hmap = new HashMap<>();
            hmap.put("type", "lte");
            int level = ss.getLevel();
            int ta = ss.getTimingAdvance();

            int cid = cellIdentityLte.getCi();
            int pci = cellIdentityLte.getPci();
            int tac = cellIdentityLte.getTac();
            int dbm = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dbm = ss.getRsrp();
                if (!Utils.isMaxint(dbm)) {
                    com.functionapps.mview_sdk2.helper.mView_HealthStatus.lteRSRP = dbm + "";
                    hmap.put("4G_rsrp", dbm + "");
                } else {
                    hmap.put("4G_rsrp", "NA");

                }
            } else {
                hmap.put("4G_rsrp", "NA");
            }


            System.out.println("signal rsrp " + dbm + "");
            int asus = ss.getAsuLevel();
            com.functionapps.mview_sdk2.helper.mView_HealthStatus.mcc_first = cellIdentityLte.getMcc();
            com.functionapps.mview_sdk2.helper.mView_HealthStatus.mnc_first = cellIdentityLte.getMnc();
            hmap.put("4G_mcc", cellIdentityLte.getMcc() + "");
            hmap.put("4G_mnc", cellIdentityLte.getMnc() + "");



//            mView_HealthStatus.lteCQI = String.valueOf(cqi);

            com.functionapps.mview_sdk2.helper.mView_HealthStatus.Cid = String.valueOf(cid);
            if (!Utils.isMaxint(cid)) {
                hmap.put("4G_cellid", String.valueOf(cid));
            } else {
                hmap.put("4G_cellid", "NA");



                /*
						getting cid which is ci in LTE
                         */
            }

            String cellidHex = Utils.DecToHex(cid);
            try {
                String localcellid = cellidHex.substring(cellidHex.length() - 2);

                int cid1 = Integer.parseInt(localcellid);
                hmap.put("4G_lcellid", cid1 + "");



            } catch (Exception e) {
                e.printStackTrace();
            }


            if (!Utils.isMaxint(pci)) {
                hmap.put("4G_pci", pci + "");
            } else {
                hmap.put("4G_pci", "NA");



            }


            if (!Utils.isMaxint(tac)) {

                hmap.put("4G_tac", tac + "");


            } else {
                hmap.put("4G_tac", "NA");



            }

            int arfcn = 0; //requires 24
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                arfcn = cellIdentityLte.getEarfcn();
            }
            if (!Utils.isMaxint(arfcn)) {
                hmap.put("4G_earfcn", arfcn + "");
            } else {
                hmap.put("4G_earfcn", "NA");



            }


            com.functionapps.mview_sdk2.helper.mView_HealthStatus.lteArfcn = String.valueOf(arfcn);


            com.functionapps.mview_sdk2.helper.mView_HealthStatus.lteasus = asus + "";

			/*mView_HealthStatus.lteRSRP = dbm + "";

			lteParams.paramslist.Rsrp=mView_HealthStatus.lteRSRP;*/

            /*Getting Level

             */

            com.functionapps.mview_sdk2.helper.mView_HealthStatus.ltelevel = level + "";


            hmap.put("4G_enb", String.valueOf(Utils.getenb(cid)));




            if (!Utils.isMaxint(ta)) {
                com.functionapps.mview_sdk2.helper.mView_HealthStatus.lteta = ta + "";

                hmap.put("4G_ta", ta + "");
            } else {
                hmap.put("4G_ta", "NA");


            }
            int snr = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                snr = ss.getRssnr();
            }
            if (!Utils.isMaxint(snr)) {
                System.out.println("Goig in if condition for snr");
                hmap.put("4G_rssnr", "" + snr);


                int cqi = getvalueofcqilte(String.valueOf(snr));

                hmap.put("4G_cqi", String.valueOf(cqi));

            } else {
                System.out.println("Goig in else condition for snr");
                hmap.put("4G_rssnr", com.functionapps.mview_sdk2.helper.mView_HealthStatus.lteSNR);
                hmap.put("4G_cqi", "NA");
            }

            int rsrq = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                rsrq = ss.getRsrq();
                if (!Utils.isMaxint(rsrq)) {
                    hmap.put("4G_rsrq", rsrq + "");
                } else {
                    hmap.put("4G_rsrq", "NA");


                }
            } else {
                hmap.put("4G_rsrq", "NA");
            }
            //	}

            com.newmview.wifi.mView_HealthStatus.servingcell1info.add(hmap);
            if (newNetworkList != null) {
                newNetworkList.onSuccess(com.newmview.wifi.mView_HealthStatus.servingcell1info);
            }
        }

    }




    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static void getGsmCellInfoForFirstSim(CellSignalStrengthGsm ss, CellIdentityGsm c, CellInfoGsm cellInfogsm, Interfaces.Network_list network_list) {


        {

            JSONObject ssGsm = new JSONObject();
            JSONObject cellIdentityGsm = new JSONObject();

            try{
                com.newmview.wifi.mView_HealthStatus.second_SimOperator = listenService.telMgr.getSimOperatorName();

                int asu = ss.getAsuLevel();
                int signalStrength = 2*asu - 113;

                ssGsm.put("asu",ss.getAsuLevel());
                ssGsm.put("signal_quality_level",ss.getLevel());
                ssGsm.put("rssi_in_dBm",ss.getDbm());
                ssGsm.put("rssi_ss",signalStrength);
                ssGsm.put("network_type", com.newmview.wifi.mView_HealthStatus.strCurrentNetworkState);
                ssGsm.put("ratType", com.newmview.wifi.mView_HealthStatus.strCurrentNetworkProtocol);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ssGsm.put("ta",ss.getTimingAdvance());
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    ssGsm.put("getRssi",ss.getRssi());
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ssGsm.put("bit_error_rate",ss.getBitErrorRate());
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    cellIdentityGsm.put("arfcn",c.getArfcn());
                    cellIdentityGsm.put("bsic",c.getBsic());

                }
                cellIdentityGsm.put("cellid",c.getCid());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    cellIdentityGsm.put("mcc",c.getMccString());
                    cellIdentityGsm.put("mnc",c.getMncString());
                    cellIdentityGsm.put("mobile_network_operator",c.getMobileNetworkOperator());


                }else {
                    cellIdentityGsm.put("mcc",c.getMcc());
                    cellIdentityGsm.put("mnc",c.getMnc());
                }

                cellIdentityGsm.put("location_area_code",c.getLac());



            }catch (Exception e){
                e.printStackTrace();
            }


            HashMap<String, String> hmap = new HashMap<>();
            hmap.put("type", "2G");
            hmap.put("network_type", com.newmview.wifi.mView_HealthStatus.strCurrentNetworkState);
            hmap.put("ratType", com.newmview.wifi.mView_HealthStatus.strCurrentNetworkProtocol);

            hmap.put("signal_strength_info",ssGsm.toString());
            hmap.put("cell_identity_info",cellIdentityGsm.toString());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                hmap.put("MCC_code",c.getMccString());
                hmap.put("MNC_code",c.getMncString());

            }else {
                hmap.put("MCC_code", "" + c.getMcc());
                hmap.put("MNC_code", "" + c.getMnc());
            }

            int cid = c.getCid();
            if (cid != 0){
                hmap.put("cell_identity", "" + cid);
            }


//            int psc = c.getPsc();
//            if (!Utils.isMaxint(psc)) {
//                hmap.put("pci", "" + psc);
//            } else {
//                hmap.put("pci", "NA");
//
//            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                hmap.put("ARFCN", "" + c.getArfcn());
            } else {
                hmap.put("ARFCN", "NA");
            }


//            hmap.put("enb", "" + Utils.getenb(cid));
            String ta;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ta = String.valueOf(ss.getTimingAdvance());
                hmap.put("TA", ta);

            }else {
                hmap.put("TA", "NA");

            }
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                try {
//
//                    if (!Utils.isMaxint(ss.getTimingAdvance())) {
//                        String ta = String.valueOf(ss.getTimingAdvance());
//
//                        hmap.put("ta", ta);
//
//                    } else {
//                        hmap.put("ta", "NA");
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    hmap.put("ta", "NA");
//                }
//
//                //System.out.println("ta is "+ta);
//                //	//Toast.makeText(mContext, "ta is "+ta, //Toast.LENGTH_SHORT).show();
//
//            } else {
//                hmap.put("ta", "NA");
//
//                mView_HealthStatus.lteta = "0";
//            }


//            hmap.put("snr", mView_HealthStatus.lteSNR);
//            try {
//
//
//                if (!Utils.isMaxint(Integer.parseInt(mView_HealthStatus.lteCQI))) {
//                    hmap.put("cqi", mView_HealthStatus.lteCQI);
//                } else {
//                    hmap.put("cqi", "NA");
//                }
//            } catch (Exception e) {
//                hmap.put("cqi", "NA");
//                e.printStackTrace();
//            }
//            int dbm = 0;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                dbm = cellInfogsm.getCellSignalStrength().getRssi();
//            } else {
//                hmap.put("rsrp", "NA");
//            }
//            if (!Utils.isMaxint(dbm)) {
//                hmap.put("rsrp", "" + dbm);
//            } else {
//                hmap.put("rsrp", "NA");
//
//            }


            int asus = ss.getAsuLevel();
            int rssi = (2*asus) - 113;

            hmap.put("ASU", String.valueOf(asus));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                hmap.put("BER", String.valueOf(ss.getBitErrorRate()));
            }else {
                hmap.put("BER", "NA");

            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                hmap.put("getRSSI", String.valueOf(ss.getRssi()));
            }else {
                hmap.put("getRSSI", "NA");

            }
            hmap.put("signal_quality", String.valueOf(ss.getLevel()));

            hmap.put("RSSI_dBm", String.valueOf(ss.getDbm()));

            hmap.put("RSSI", String.valueOf(rssi));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                hmap.put("BSIC", String.valueOf(c.getBsic()));
            }
            hmap.put("lac_id", String.valueOf(c.getLac()));


//            int level = cellInfogsm.getCellSignalStrength().getLevel();
//            mView_HealthStatus.gsmSignalStrength = cellInfogsm.getCellSignalStrength().getDbm();
//            System.out.println("ss in gsm " + dbm);
//
//            if (!Utils.isMaxint(mView_HealthStatus.gsmSignalStrength)) {
//                hmap.put("rsrq", "" + mView_HealthStatus.gsmSignalStrength);
//            } else {
//                hmap.put("rsrq", "NA");
//            }


//            mView_HealthStatus.lteasus = asus + "";
//            mView_HealthStatus.ltedbm = dbm + "";
//            mView_HealthStatus.ltelevel = level + "";


//            int lac = c.getLac();
//
//
//            mView_HealthStatus.Psc = String.valueOf(psc);
////            short cid_short = (short) cid;
//
//
//            mView_HealthStatus.Lac = String.valueOf(lac);
//
//
            mView_HealthStatus.servingcell1info.add(hmap);

            if (network_list != null) {
                network_list.onSuccess(mView_HealthStatus.servingcell1info);
            }



        }

    }

    private static ArrayList<HashMap<String, String>> getDefaultNetworkValues() {
        ArrayList<HashMap<String, String>> servingcell1info=new ArrayList<>();
        HashMap<String, String> hmap = new HashMap<>();
        hmap.put("type", "  NA");
        hmap.put("mcc", "" );
        hmap.put("mnc", "" );
        hmap.put("cellid", "NA");
        hmap.put("localcellid", "NA");
        hmap.put("pci", "NA");
        hmap.put("tac", "NA");
        hmap.put("earfcn", "NA");
        hmap.put("enb", "NA");
        hmap.put("ta", "NA");
        hmap.put("snr", com.functionapps.mview_sdk2.helper.mView_HealthStatus.lteSNR);
        hmap.put("cqi", "NA");
        hmap.put("rsrp", "NA");
        hmap.put("rsrq", "NA");

        servingcell1info.add(hmap);
        return servingcell1info;

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private static void getWcdmaCellInfoForFirstSim(CellSignalStrengthWcdma ss, CellIdentityWcdma cellIdentityWcdma, Interfaces.Network_list network_list) {

        {

            Utils.appendLog("ELOG_WCDMA_CELL_IDENTITY: is: "+cellIdentityWcdma.toString());
            Utils.appendLog("ELOG_WCDMA_CELL_SIGNAL_STRENGTH: is: "+ss.toString());

            JSONObject ssWcdma = new JSONObject();
            JSONObject cellIdentityWcdmaJson = new JSONObject();
            int cid = cellIdentityWcdma.getCid();
            int asu = ss.getAsuLevel();
            int last16Bits = cid & 0xFFFF;
            int first12Bits = (cid >> 20) & 0xFFF;
            int signalStrength = asu - 115;
            try{
                com.newmview.wifi.mView_HealthStatus.second_SimOperator = listenService.telMgr.getSimOperatorName();

                ssWcdma.put("network_type", com.newmview.wifi.mView_HealthStatus.strCurrentNetworkState);
                ssWcdma.put("ratType", com.newmview.wifi.mView_HealthStatus.strCurrentNetworkProtocol);
                ssWcdma.put("rscp_ss",signalStrength);
                ssWcdma.put("rscp_in_dBm",ss.getDbm());
                ssWcdma.put("asu",ss.getAsuLevel());
                ssWcdma.put("signal_quality_level",ss.getLevel());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    ssWcdma.put("energy_per_chip",ss.getEcNo());
                }

                cellIdentityWcdmaJson.put("cellid",cid);
                cellIdentityWcdmaJson.put("UTRAN_cell_id", last16Bits);
                cellIdentityWcdmaJson.put("NodeB", first12Bits);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    cellIdentityWcdmaJson.put("uarfcn",cellIdentityWcdma.getUarfcn());
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    cellIdentityWcdmaJson.put("mcc",cellIdentityWcdma.getMccString());
                    cellIdentityWcdmaJson.put("mnc",cellIdentityWcdma.getMncString());

                }else {
                    cellIdentityWcdmaJson.put("mcc",cellIdentityWcdma.getMcc());
                    cellIdentityWcdmaJson.put("mnc",cellIdentityWcdma.getMnc());
                }

                cellIdentityWcdmaJson.put("psc",cellIdentityWcdma.getPsc());
                cellIdentityWcdmaJson.put("lac",cellIdentityWcdma.getLac());



            }catch (Exception e){
                e.printStackTrace();
            }

            HashMap<String, String> hmap = new HashMap<>();

            hmap.put("signal_strength_info",ssWcdma.toString());
            hmap.put("cell_identity_info",cellIdentityWcdmaJson.toString());


            hmap.put("type", "3G");
            hmap.put("network_type", com.newmview.wifi.mView_HealthStatus.strCurrentNetworkState);
            hmap.put("ratType", com.newmview.wifi.mView_HealthStatus.strCurrentNetworkProtocol);

            hmap.put("cell_identity", String.valueOf(cid));
            hmap.put("UTRAN_cell_id", String.valueOf(last16Bits));
            hmap.put("NodeB", String.valueOf(first12Bits));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                hmap.put("Uarfcn", String.valueOf(cellIdentityWcdma.getUarfcn()));
            }

            int rsrp = asu - 115;
            hmap.put("ASU", String.valueOf(asu));
            hmap.put("RSCP", String.valueOf(rsrp));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                hmap.put("MCC_code", cellIdentityWcdma.getMccString());
                hmap.put("MNC_code", cellIdentityWcdma.getMncString());

            }else {
                hmap.put("MCC_code", String.valueOf(cellIdentityWcdma.getMcc()));
                hmap.put("MNC_code", String.valueOf(cellIdentityWcdma.getMnc()));

            }
            hmap.put("lac_id", String.valueOf(cellIdentityWcdma.getLac()));
            hmap.put("PSC", String.valueOf(cellIdentityWcdma.getPsc()));
            hmap.put("signal_quality", String.valueOf(ss.getLevel()));
            hmap.put("RSCP_in_dBm", String.valueOf(ss.getDbm()));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                hmap.put("Ec_No", String.valueOf(ss.getEcNo()));
            }

            mView_HealthStatus.servingcell1info.add(hmap);
            if (network_list != null) {
                network_list.onSuccess(mView_HealthStatus.servingcell1info);
            }


//            mView_HealthStatus.rscp = String.valueOf(ss.getDbm());
//            if (ss.getDbm() != Integer.MAX_VALUE) {
//                hmap.put("rsrp", "" + ss.getDbm());
//
//            } else {
//                hmap.put("rsrp", "NA");
//
//            }
            mView_HealthStatus.mcc_first = cellIdentityWcdma.getMcc();


//            hmap.put("mcc", "" + cellIdentityWcdma.getMcc());
            mView_HealthStatus.mnc_first = cellIdentityWcdma.getMnc();
//            hmap.put("mnc", "" + cellIdentityWcdma.getMnc());

//            int longCid = cellIdentityWcdma.getCid();
//            if (!Utils.isMaxint(longCid)) {
//                hmap.put("cellid", "" + longCid);
//                try {
//
//
//                    String cellidHex = Utils.DecToHex(longCid);
//                    String localcellid = cellidHex.substring(cellidHex.length() - 2);
//                    int cid1 = Integer.parseInt(localcellid);
//
//                    hmap.put("localcellid", "" + cid1);
//                } catch (Exception e) {
//
//                    hmap.put("localcellid", "NA");
//                    e.printStackTrace();
//                }
//
//            } else {
//                hmap.put("localcellid", "NA");
//                hmap.put("cellid", "" + "NA");
//            }
//
//
//            int psc = cellIdentityWcdma.getPsc();
//
//
//            if (!Utils.isMaxint(psc)) {
//                hmap.put("pci", "" + psc);
//            } else {
//                hmap.put("pci", "" + "NA");
//            }
//            hmap.put("tac", "NA");
//
//
//            int uarfcn = 0;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                uarfcn = cellIdentityWcdma.getUarfcn();
//            }
//
//
//            if (uarfcn != Integer.MAX_VALUE) {
//                hmap.put("earfcn", "" + uarfcn);
//            } else {
//                hmap.put("earfcn", "NA");
//
//            }
//
//            hmap.put("snr", "NA");
//            hmap.put("cqi", "NA");
//
//
//            hmap.put("ta", "NA");
//
//            String signalstrength = ss.toString().replace("CellSignalStrengthWcdma:", "");
//
//
//            hmap.put("rsrq", signalstrength);
//
//
//            mView_HealthStatus.Wcdma_Psc = String.valueOf(psc);
//            //	mView_HealthStatus.lteArfcn=String.valueOf(uarfcn);
//            mView_HealthStatus.Uarfcn = String.valueOf(uarfcn);
//
//
//            hmap.put("enb", "" + Utils.getenb(longCid));




        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static void getLteCellInfoForFirstSim(CellSignalStrengthLte ss, CellIdentityLte cellIdentityLte, Interfaces.Network_list network_list) {

        {

            Utils.appendLog("ELOG_LTE_CELL_IDENTITY: is: "+cellIdentityLte.toString());
            Utils.appendLog("ELOG_LTE_CELL_SIGNAL_STRENGTH: is: "+ss.toString());
            com.newmview.wifi.mView_HealthStatus.second_SimOperator = listenService.telMgr.getSimOperatorName();

            JSONObject ssLte = new JSONObject();
            JSONObject cellIdentityLteJson = new JSONObject();

            int cid = cellIdentityLte.getCi();
            int asu = ss.getAsuLevel();
            int last8Bits = cid & 0xFF;
            int first20Bits = (cid >> 12) & 0xFFFFF;
            int signaStrength = asu -140;

            try{

                ssLte.put("asu",asu);
                ssLte.put("network_type", com.newmview.wifi.mView_HealthStatus.strCurrentNetworkState);

                ssLte.put("ratType", com.newmview.wifi.mView_HealthStatus.strCurrentNetworkProtocol);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ssLte.put("cqi",ss.getCqi());
                    ssLte.put("ta",ss.getTimingAdvance());
                    ssLte.put("rsrp_ss",signaStrength);
                    ssLte.put("getRsrp",ss.getRsrp());
                    ssLte.put("rsrq",ss.getRsrq());
                    ssLte.put("rssnr",ss.getRssnr());

                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    ssLte.put("cqi_table_index",ss.getCqiTableIndex());
                }

                ssLte.put("signal_quality_level",ss.getLevel());
                ssLte.put("rsrp_in_dbm",ss.getDbm());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    ssLte.put("rssi",ss.getRssi());

                }
                cellIdentityLteJson.put("cellid",cellIdentityLte.getCi());
                cellIdentityLteJson.put("eUTRAN_cell_id", String.valueOf(last8Bits));
                cellIdentityLteJson.put("eNodeB", String.valueOf(first20Bits));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    cellIdentityLteJson.put("earfcn",cellIdentityLte.getEarfcn());
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    cellIdentityLteJson.put("bandwidth",cellIdentityLte.getBandwidth());
                    cellIdentityLteJson.put("mcc",cellIdentityLte.getMccString());
                    cellIdentityLteJson.put("mnc",cellIdentityLte.getMncString());
                    cellIdentityLteJson.put("mobile_network_operator",cellIdentityLte.getMobileNetworkOperator());


                }else {
                    cellIdentityLteJson.put("mcc",cellIdentityLte.getMcc());
                    cellIdentityLteJson.put("mnc",cellIdentityLte.getMnc());
                }

                cellIdentityLteJson.put("pci",cellIdentityLte.getPci());
                cellIdentityLteJson.put("tac",cellIdentityLte.getTac());



            }catch (Exception e){
                e.printStackTrace();
            }



            HashMap<String, String> hmap = new HashMap<>();
            hmap.put("type", "lte");
            hmap.put("network_type", com.newmview.wifi.mView_HealthStatus.strCurrentNetworkState);

            hmap.put("ratType", com.newmview.wifi.mView_HealthStatus.strCurrentNetworkProtocol);

            hmap.put("signal_strength_info",ssLte.toString());
            hmap.put("cell_identity_info",cellIdentityLteJson.toString());




            hmap.put("cell_identity", String.valueOf(cid));
            hmap.put("eUTRAN_cell_id", String.valueOf(last8Bits));
            hmap.put("eNodeB", String.valueOf(first20Bits));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                hmap.put("snr", String.valueOf(ss.getRssnr()));
                hmap.put("cqi", String.valueOf(ss.getCqi()));
                hmap.put("getRSRP", String.valueOf(ss.getRsrp()));


            }else {
                hmap.put("snr","NA");
                hmap.put("cqi", "NA");
                hmap.put("getRSRP", "NA");

            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                hmap.put("earfcn", String.valueOf(cellIdentityLte.getEarfcn()));
            }
            hmap.put("pci", String.valueOf(cellIdentityLte.getPci()));
            hmap.put("TA", String.valueOf(ss.getTimingAdvance()));

            int rsrp = asu - 140;
            hmap.put("ASU", String.valueOf(asu));

            hmap.put("RSRP", String.valueOf(rsrp));
            hmap.put("tac", String.valueOf(cellIdentityLte.getTac()));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                hmap.put("MCC_code", cellIdentityLte.getMccString());
                hmap.put("MNC_code", cellIdentityLte.getMncString());

            }else {
                hmap.put("MCC_code", String.valueOf(cellIdentityLte.getMcc()));
                hmap.put("MNC_code", String.valueOf(cellIdentityLte.getMnc()));

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                hmap.put("RSSI", String.valueOf(ss.getRssi()));
            }else {
                hmap.put("RSSI", "NA");

            }
            hmap.put("signal_quality", String.valueOf(ss.getLevel()));

            hmap.put("RSRP_in_dBm", String.valueOf(ss.getDbm()));


            mView_HealthStatus.servingcell1info.add(hmap);
            if (network_list != null) {
                network_list.onSuccess(mView_HealthStatus.servingcell1info);
            }

//            int dbm = 0;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                dbm = ss.getRsrp();
//                if (!Utils.isMaxint(dbm)) {
//                    mView_HealthStatus.lteRSRP = dbm + "";
//                    hmap.put("rsrp", dbm + "");
//                } else {
//                    hmap.put("rsrp", "NA");
//                }
//            } else {
//                hmap.put("rsrp", "NA");
//            }


//            System.out.println("signal rsrp " + dbm + "");
//            int asus = ss.getAsuLevel();
//            mView_HealthStatus.mcc_first = cellIdentityLte.getMcc();
//            mView_HealthStatus.mnc_first = cellIdentityLte.getMnc();
//            hmap.put("mcc", cellIdentityLte.getMcc() + "");
//            hmap.put("mnc", cellIdentityLte.getMnc() + "");
////            mView_HealthStatus.lteCQI = String.valueOf(cqi);
//
//            mView_HealthStatus.Cid = String.valueOf(cid);
//            if (!Utils.isMaxint(cid)) {
//                hmap.put("cellid", String.valueOf(cid));
//            } else {
//                hmap.put("cellid", "NA");
//
//
//                /*
//						getting cid which is ci in LTE
//                         */
//            }
//
//            String cellidHex = Utils.DecToHex(cid);
//            try {
//                String localcellid = cellidHex.substring(cellidHex.length() - 2);
//
//                int cid1 = Integer.parseInt(localcellid);
//                hmap.put("localcellid", cid1 + "");
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//
//            if (!Utils.isMaxint(pci)) {
//                hmap.put("pci", pci + "");
//            } else {
//                hmap.put("pci", "NA");
//
//            }
//
//
//            if (!Utils.isMaxint(tac)) {
//
//                hmap.put("tac", tac + "");
//
//
//            } else {
//                hmap.put("tac", "NA");
//
//            }
//
//            int arfcn = 0; //requires 24
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                arfcn = cellIdentityLte.getEarfcn();
//            }
//            if (!Utils.isMaxint(arfcn)) {
//                hmap.put("earfcn", arfcn + "");
//            } else {
//                hmap.put("earfcn", "NA");
//
//            }
//
//
//            mView_HealthStatus.lteArfcn = String.valueOf(arfcn);
//
//
//            mView_HealthStatus.lteasus = asus + "";
//
//			/*mView_HealthStatus.lteRSRP = dbm + "";
//
//			lteParams.paramslist.Rsrp=mView_HealthStatus.lteRSRP;*/
//
//            /*Getting Level
//
//             */
//
//            mView_HealthStatus.ltelevel = level + "";


//            hmap.put("enb", String.valueOf(Utils.getenb(cid)));
//
//
//            if (!Utils.isMaxint(ta)) {
//                mView_HealthStatus.lteta = ta + "";
//
//                hmap.put("ta", ta + "");
//            } else {
//                hmap.put("ta", "NA");
//            }
//            int snr = 0;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//            {
//                snr = ss.getRssnr();
//            }
//            if (!Utils.isMaxint(snr)) {
//                System.out.println("Goig in if condition for snr");
//                hmap.put("snr", "" + snr);
//                int cqi = getvalueofcqilte(String.valueOf(snr));
//
//                hmap.put("cqi", String.valueOf(cqi));
//
//            } else {
//                System.out.println("Goig in else condition for snr");
//                hmap.put("snr", mView_HealthStatus.lteSNR);
//                hmap.put("cqi", "NA");
//
//            }
//
//
//
//            int rsrq = 0;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                rsrq = ss.getRsrq();
//                if (!Utils.isMaxint(rsrq)) {
//                    hmap.put("rsrq", rsrq + "");
//                } else {
//                    hmap.put("rsrq", "NA");
//                }
//            } else {
//                hmap.put("rsrq", "NA");
//            }



            //	}


        }
    }

    private static int getvalueofcqinr(String lte) {
        System.out.println("STRING SNR IS " + lte);
        int lteSNR = 0;
        try {
            float f1 = Float.parseFloat(lte);
            lteSNR = (int) f1;
            System.out.println("STRING SNR IS " + lte + " " + " integer is " + lteSNR);

        } catch (Exception e) {
            System.out.println("exception while converting snr " + e.toString());

            e.printStackTrace();
        }

        int cqi;
        if (lteSNR < 32 && lteSNR >= 28.2) {
            cqi = 15;
        } else if (lteSNR < 28.2 && lteSNR >= 23.6) {
            cqi = 14;
        } else if (lteSNR < 23.6 && lteSNR >= 21.4) {
            cqi = 13;
        } else if (lteSNR < 21.4 && lteSNR >= 18.8) {
            cqi = 12;
        } else if (lteSNR < 18.8 && lteSNR >= 15.8) {
            cqi = 11;
        } else if (lteSNR < 15.8 && lteSNR >= 13.6) {
            cqi = 10;
        } else if (lteSNR < 13.6 && lteSNR >= 11.7) {
            cqi = 9;
        } else if (lteSNR < 11.7 && lteSNR >= 9.8) {

            cqi = 8;


        } else if (lteSNR < 9.8 && lteSNR >= 8.1) {
            cqi = 7;
        } else if (lteSNR < 8.1 && lteSNR >= 5.3) {
            cqi = 6;
        } else if (lteSNR < 5.3 && lteSNR >= 3.9) {
            cqi = 5;
        } else if (lteSNR < 3.9 && lteSNR >= -1.4) {
            cqi = 4;
        } else if (lteSNR < -1.4 && lteSNR >= -5.8) {
            cqi = 3;
        } else if (lteSNR < -5.8 && lteSNR >= -6.3) {
            cqi = 2;
        } else if (lteSNR < -6.3) {
            cqi = 1;
        } else {
            cqi = -1;
        }


        return cqi;
    }

    private static int getvalueofcqilte(String lte) {
        System.out.println("STRING SNR IS " + lte);
        int lteSNR = 0;
        try {
            float f1 = Float.parseFloat(lte);
            lteSNR = (int) f1;
            System.out.println("STRING SNR IS " + lte + " " + " integer is " + lteSNR);

        } catch (Exception e) {
            System.out.println("exception while converting snr " + e.toString());

            e.printStackTrace();
        }

        int cqi;
        if (lteSNR < 19.8290 && lteSNR >= 17.8410) {
            cqi = 15;
        } else if (lteSNR < 17.8410 && lteSNR >= 15.8880) {
            cqi = 14;
        } else if (lteSNR < 15.8880 && lteSNR >= 14.1730) {
            cqi = 13;
        } else if (lteSNR < 14.1730 && lteSNR >= 12.2890) {
            cqi = 12;
        } else if (lteSNR < 12.2890 && lteSNR >= 10.3660) {
            cqi = 11;
        } else if (lteSNR < 10.3660 && lteSNR >= 8.5730) {
            cqi = 10;
        } else if (lteSNR < 8.5730 && lteSNR >= 6.5250) {
            cqi = 9;
        } else if (lteSNR < 6.250 && lteSNR >= 4.6940) {

            cqi = 8;


        } else if (lteSNR < 4.6940 && lteSNR >= 2.6990) {
            cqi = 7;
        } else if (lteSNR < 2.6990 && lteSNR >= 0.7610) {
            cqi = 6;
        } else if (lteSNR < 0.7610 && lteSNR >= -1.2530) {
            cqi = 5;
        } else if (lteSNR < -1.2530 && lteSNR >= -3.1800) {
            cqi = 4;
        } else if (lteSNR < -3.1800 && lteSNR >= -5.1470) {
            cqi = 3;
        } else if (lteSNR < -5.1470 && lteSNR >= -6.9360) {
            cqi = 2;
        } else if (lteSNR < -6.9360) {
            cqi = 1;
        } else {
            cqi = -1;
        }


        return cqi;
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static void getCellInfoForFirstSim5G(CellSignalStrengthNr ss, CellIdentityNr cellIdentityNr, Interfaces.Network_list network_list) {

        HashMap<String, String> hmap = new HashMap<>();
        hmap.put("type", "nr");
        int SsRsrp = ss.getSsRsrp();
        if (!Utils.isMaxint(SsRsrp)) {
            hmap.put("rsrp", SsRsrp + "");
        } else {
            hmap.put("rsrp", "NA");
        }

        String mcc = cellIdentityNr.getMccString();
        String mnc = cellIdentityNr.getMncString();
        hmap.put("mcc", "" + mcc);
        hmap.put("mnc", "" + mnc);
        long cid = cellIdentityNr.getNci();

        if (!Utils.isMaxint((int) cid)) {
            hmap.put("cellid", String.valueOf(cid));
            try {

                String cellidHex = Utils.DecToHex((int) cid);
                String localcellid = cellidHex.substring(cellidHex.length() - 2);
                int cid1 = Integer.parseInt(localcellid);

                hmap.put("localcellid", cid1 + "");
            } catch (Exception e) {
                e.printStackTrace();
                hmap.put("localcellid", " NA");

            }


            try {
                hmap.put("enb", String.valueOf(Utils.getenb((int) cid)));
            } catch (Exception e) {
                e.printStackTrace();
                hmap.put("enb", "NA");

            }
        } else {
            hmap.put("enb", "NA");
            hmap.put("localcellid", " NA");
            hmap.put("cellid", "NA");

        }

        int pci = cellIdentityNr.getPci();
        if (!Utils.isMaxint(pci)) {
            hmap.put("pci", "" + pci);
        } else {
            hmap.put("pci", "NA");


        }

        int tac = cellIdentityNr.getTac();

        if (!Utils.isMaxint(tac)) {
            hmap.put("tac", "" + tac);
        } else {
            hmap.put("tac", "NA");

        }

        int arfcn = cellIdentityNr.getNrarfcn();

        if (!Utils.isMaxint(arfcn)) {
            hmap.put("earfcn", arfcn + "");
        } else {
            hmap.put("earfcn", "NA");

        }


        hmap.put("ta", "NA");


//        int CsiSinr = ss.getCsiSinr();
        int CsiSinr = ss.getSsSinr();

        if (!Utils.isMaxint(CsiSinr)) {
            hmap.put("snr", "" + CsiSinr);

            hmap.put("cqi", "" + getvalueofcqinr(String.valueOf(CsiSinr)));
        } else {
            hmap.put("snr", "NA");
            hmap.put("cqi", "" + "NA");
        }


//        int CsiRsrp = ss.getCsiRsrp();
        int CsiRsrp = ss.getSsRsrp();
        if (!Utils.isMaxint(CsiRsrp)) {
            hmap.put("rsrp", CsiRsrp + "");
        } else {
            hmap.put("rsrp", "NA");

        }
//        int CsiRsrq = ss.getCsiRsrq();
        int CsiRsrq = ss.getSsRsrq();
        if (!Utils.isMaxint(CsiRsrq)) {

            hmap.put("rsrq", "" + CsiRsrq);
        } else {
            hmap.put("rsrq", "NA");
        }


       /* int[] Band = new int[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            Band = MyPhoneStateListener.cellIdentityNr.getBands();
        }
        hmap.put("bands",""+ Band);*/


        /* */


//
       /*
        int dbm = ss.getDbm();
       */
        com.functionapps.mview_sdk2.helper.mView_HealthStatus.servingcell1info.add(hmap);
        if (network_list != null) {
            network_list.onSuccess(com.functionapps.mview_sdk2.helper.mView_HealthStatus.servingcell1info);
        }


    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static void getGsmCellInfoForSecondSim(CellSignalStrengthGsm ss, CellIdentityGsm c, CellInfoGsm cellInfogsm, Interfaces.Network_list network_list) {

        HashMap<String, String> hmap = new HashMap<>();
        hmap.put("type", "gsm");

        int dbm = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            dbm = cellInfogsm.getCellSignalStrength().getRssi();
        }
        hmap.put("rsrq", "" + dbm);


        System.out.println("ss in gsm " + dbm);


        int mcc = c.getMcc();
        int mnc = c.getMnc();
        hmap.put("mcc", c.getMcc() + "");
        hmap.put("mnc", c.getMnc() + "");

        if (!Utils.isMaxint(com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_Cqi)) {
            hmap.put("cqi", "" + com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_Cqi);
        } else {
            hmap.put("cqi", "NA");

        }


        int snr = com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_snr;

        hmap.put("snr", "" + snr);
        if (mcc != Integer.MAX_VALUE) {
            com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_Mcc = mcc;
        }
        if (mnc != Integer.MAX_VALUE) {
            com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_Mnc = mnc;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int ta = ss.getTimingAdvance();
            if (ta != Integer.MAX_VALUE) {

                com.functionapps.mview_sdk2.helper.mView_HealthStatus.secong_gsmTa = ta;
                hmap.put("ta", "" + ta);

            } else {
                hmap.put("ta", "NA");

            }
        } else {
            hmap.put("ta", "NA");

        }


        int cid = c.getCid();
        if (cid != Integer.MAX_VALUE)
        {
            com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_Cid = cid;
            hmap.put("cellid", String.valueOf(cid));

            try

            {
                String cellidHex = Utils.DecToHex(cid);
                String localcellid = cellidHex.substring(cellidHex.length() - 2);
                int cid1 = Integer.parseInt(localcellid);
                hmap.put("localcellid", cid1 + "");

            }
            catch (Exception e)
            {
                e.toString();
                hmap.put("localcellid", "NA");

            }

        } else {
            hmap.put("cellid", "NA");
            hmap.put("localcellid", "NA");


        }
        hmap.put("enb", String.valueOf(Utils.getenb(cid)));

        int lac = c.getLac();
        int psc = c.getPsc();


        if (psc != Integer.MAX_VALUE) {
            com.functionapps.mview_sdk2.helper.mView_HealthStatus.secong_gsmPsc = psc;
            hmap.put("pci", psc + "");

        } else {
            hmap.put("pci", "NA");

        }
        System.out.println("cid is " + cid);
        if (cid != Integer.MAX_VALUE) {
            com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_gsmCid = cid;

        }

        if (Build.VERSION.SDK_INT >= 24) {
            int arfcn = c.getArfcn();
            if (arfcn != Integer.MAX_VALUE) {
                com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_arfcn = arfcn;
                hmap.put("earfcn", "" + arfcn);

            } else {
                hmap.put("earfcn", "NA");

            }

        } else {
            hmap.put("earfcn", "NA");

        }
        hmap.put("tac", "NA");
        com.functionapps.mview_sdk2.helper.mView_HealthStatus.servingcell2info.add(hmap);
        if (network_list != null) {
            network_list.onSuccess(com.functionapps.mview_sdk2.helper.mView_HealthStatus.servingcell2info);
        }

    }

    private static void addServingCellInfo2(CellInfo m, Interfaces.Network_list network_list) {
        if (m != null) {


            if (m instanceof CellInfoGsm) {
                CellInfoGsm cellInfogsm = (CellInfoGsm) m;
                CellIdentityGsm c = cellInfogsm.getCellIdentity();
                CellSignalStrengthGsm ss = cellInfogsm.getCellSignalStrength();


                getGsmCellInfoForSecondSim(ss, c, cellInfogsm, network_list);
            } else if (m instanceof CellInfoLte) {

                CellInfoLte cellInfoLte = (CellInfoLte) m;
                CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
                CellSignalStrengthLte ss = cellInfoLte.getCellSignalStrength();
                getLteCellInfoForAnotherSim(ss, cellIdentityLte, network_list);

            } else if (m instanceof CellInfoWcdma) {

                CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) m;
                CellIdentityWcdma cellIdentityWcdma = cellInfoWcdma.getCellIdentity();
                CellSignalStrengthWcdma ss = cellInfoWcdma.getCellSignalStrength();
                getWcdmaCellInfoForAnotherSim(ss, cellIdentityWcdma, network_list);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (m instanceof CellInfoNr) {
                    CellInfoNr cellInfoNr = (CellInfoNr) m;
                    CellIdentityNr cellIdentityNr = (CellIdentityNr) cellInfoNr.getCellIdentity();
                    CellSignalStrengthNr ss = (CellSignalStrengthNr) cellInfoNr.getCellSignalStrength();

                    getCellInfoForSecondSim5G(ss, cellIdentityNr, network_list);


                }
            }
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static void getLteCellInfoForAnotherSim(CellSignalStrengthLte ss, CellIdentityLte cellIdentityLte, Interfaces.Network_list network_list) {

        {


            HashMap<String, String> hmap = new HashMap<>();
            hmap.put("type", "lte");

            System.out.println("lte cell info 2 " + cellIdentityLte);
            com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_cellInstance = "lte";
            int level = ss.getLevel();

            int ta = ss.getTimingAdvance();
            if (ta != Integer.MAX_VALUE) {
                hmap.put("ta", "" + ta);

            } else {
                hmap.put("ta", "NA");

            }
            int cqi = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                cqi = ss.getCqi();
            }

            if (cqi != Integer.MAX_VALUE || cqi != 0)
            {
                hmap.put("cqi", String.valueOf(cqi));
            } else {
                hmap.put("cqi", "NA");

            }

            int cid = cellIdentityLte.getCi();
            int pci = cellIdentityLte.getPci();
            hmap.put("pci", pci + "");

            int snr = com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_snr;

            hmap.put("snr", "" + snr);
            int rsrq = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                rsrq = ss.getRsrq();
            }
            hmap.put("rsrq", rsrq + "");
            int tac = cellIdentityLte.getTac();
            int mcc = cellIdentityLte.getMcc();
            int mnc = cellIdentityLte.getMnc();
            hmap.put("mcc", cellIdentityLte.getMcc() + "");
            hmap.put("mnc", cellIdentityLte.getMnc() + "");
            hmap.put("enb", String.valueOf(Utils.getenb(cid)));

            int dbm = ss.getDbm();


            int rsrp = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                rsrp = ss.getRsrp();
                if (rsrp != Integer.MAX_VALUE)
                {
                    com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_Rsrp = rsrp;
                    hmap.put("rsrp", "" + rsrp);
                }

            } else {
                hmap.put("rsrp", "NA");
            }

            int aba = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                aba = ss.getRsrq();
            }

            if (aba != Integer.MAX_VALUE) {
                com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_rsrq = "" + aba;
                hmap.put("rsrq", "" + aba);

            } else {
                hmap.put("rsrq", "NA");
                com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_rsrq = "NA";


            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                snr = ss.getRssnr();

            }


            com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_Mcc = mcc;
            com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_Mnc = mnc;

            if (snr != Integer.MAX_VALUE) {
                com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_snr = snr;
                if (cqi != Integer.MAX_VALUE) {
                }

                hmap.put("snr", "" + snr);

            } else {
                hmap.put("snr", "NA");

            }


            if (cid != Integer.MAX_VALUE) {
                com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_Cid = cid;

                hmap.put("cellid", String.valueOf(cid));

                try {
                    String cellidHex = Utils.DecToHex(cid);
                    String localcellid = cellidHex.substring(cellidHex.length() - 2);
                    int cid1 = Integer.parseInt(localcellid);

                    hmap.put("localcellid", cid1 + "");
                }
                catch (Exception e)
                {
                    hmap.put("localcellid",  "NA");

                    e.toString();
                }
            } else {
                hmap.put("cellid", "NA");
                hmap.put("localcellid", "NA");


            }
            if (pci != Integer.MAX_VALUE) {
                com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_pci = pci;
            }

            if (tac != Integer.MAX_VALUE) {

                com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_tac = tac;
            }
            if (tac != Integer.MAX_VALUE) {
                hmap.put("tac", tac + "");


            } else {
                hmap.put("tac", "NA");

            }

            if (Build.VERSION.SDK_INT >= 24) {
                int arfcn = cellIdentityLte.getEarfcn();
                com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_earfcn = arfcn;
                hmap.put("earfcn", arfcn + "");


            }

            com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_Cqi = cqi;

            com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_ENB = String.valueOf(Utils.getenb(cid));
            if (ta != Integer.MAX_VALUE) {
                com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_ta = ta;
            }
            com.functionapps.mview_sdk2.helper.mView_HealthStatus.servingcell2info.add(hmap);
            if (network_list != null) {
                network_list.onSuccess(com.functionapps.mview_sdk2.helper.mView_HealthStatus.servingcell2info);
            }

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private static void getWcdmaCellInfoForAnotherSim(CellSignalStrengthWcdma ss, CellIdentityWcdma cellIdentityWcdma, Interfaces.Network_list network_list) {

        {
            HashMap<String, String> hmap = new HashMap<>();
            hmap.put("type", "3G");
            hmap.put("ta", "NA");
            hmap.put("rsrq", com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_rsrq);
            int rscp = ss.getDbm();
            int uarfcn = 0;
            int lac = cellIdentityWcdma.getLac();

            int cid = cellIdentityWcdma.getCid();


            hmap.put("cellid", String.valueOf(cid));

            try {


                String cellidHex = Utils.DecToHex(cid);
                String localcellid = cellidHex.substring(cellidHex.length() - 2);
                int cid1 = Integer.parseInt(localcellid);

                hmap.put("localcellid", cid1 + "");
            } catch (Exception e) {
                e.printStackTrace();
                hmap.put("localcellid", " NA");

            }
            int psc = cellIdentityWcdma.getPsc();
            int mcc = cellIdentityWcdma.getMcc();
            int mnc = cellIdentityWcdma.getMnc();
            hmap.put("mcc", "" + mcc);
            hmap.put("mnc", "" + mnc);
            com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_rsrq = " ";
            int cqi = com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_Cqi;

            String snr = "NA";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                snr = getSNR();

            }
            hmap.put("snr", "" + snr);
            if (cqi != Integer.MAX_VALUE || cqi != 0) {
                hmap.put("cqi", String.valueOf(cqi));
            } else {
                hmap.put("cqi", "NA");

            }

            hmap.put("pci", "" + psc);


            if (rscp != Integer.MAX_VALUE) {

                hmap.put("rsrp", rscp + "");


            } else {
                hmap.put("rsrp", "NA");

            }
            hmap.put("tac", "NA");


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uarfcn = cellIdentityWcdma.getUarfcn();
                hmap.put("earfcn", uarfcn + "");

            } else {
                hmap.put("earfcn", "NA");

            }

            if (rscp != Integer.MAX_VALUE) {
                com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_rscp_3G = rscp;
            }
            if (uarfcn != Integer.MAX_VALUE) {
                com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_uarfcn_3G = uarfcn;
            }
            if (lac != Integer.MAX_VALUE) {
                com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_lac_3G = lac;
            }
            if (cid != Integer.MAX_VALUE) {
                com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_cid_3G = cid;
            }
            if (psc != Integer.MAX_VALUE) {
                com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_Psc_3g = psc;
            }
            if (mcc != Integer.MAX_VALUE) {
                com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_Mcc = mcc;
            }
            if (mnc != Integer.MAX_VALUE) {
                com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_Mnc = mnc;
            }


            hmap.put("rsrq", "NA");

            com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_NodeBID_3G = String.valueOf(Utils.getenb(cid));
            com.functionapps.mview_sdk2.helper.mView_HealthStatus.servingcell2info.add(hmap);

            if (network_list != null) {
                network_list.onSuccess(com.functionapps.mview_sdk2.helper.mView_HealthStatus.servingcell2info);
            }

        }
    }

    @SuppressLint("WrongConstant")
    private static void getCellInfoForSecondSim5G(CellSignalStrengthNr ss, CellIdentityNr cellIdentityNr, Interfaces.Network_list network_list) {
        HashMap<String, String> hmap = new HashMap<>();
        hmap.put("type", "nr");

        hmap.put("ta", "NA");
        String mcc = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mcc = cellIdentityNr.getMccString();
            hmap.put("mcc", "" + mcc);

        } else {
            hmap.put("mcc", "NA");
        }

        String mnc = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mnc = cellIdentityNr.getMncString();
            hmap.put("mnc", "" + mnc);

        } else {
            hmap.put("mnc", "NA");
        }

        int CsiSinr = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            CsiSinr = ss.getCsiSinr();
            CsiSinr = ss.getSsSinr();


            if (!Utils.isMaxint(CsiSinr)) {
                hmap.put("snr", "" + CsiSinr);
                hmap.put("cqi", "" + getvalueofcqinr(String.valueOf(CsiSinr)));
            } else {
                hmap.put("snr", "NA");
                hmap.put("cqi", "NA");


            }
        } else {
            hmap.put("snr", "NA");
            hmap.put("cqi", "NA");

        }


        long cid = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            cid = MyPhoneStateListener.cellIdentityNr.getNci();

            if (!Utils.isMaxint((int) cid)) {
                hmap.put("cellid", String.valueOf(cid));

                try {


                    String cellidHex = Utils.DecToHex((int) cid);
                    String localcellid = cellidHex.substring(cellidHex.length() - 2);
                    int cid1 = Integer.parseInt(localcellid);

                    hmap.put("localcellid", cid1 + "");
                } catch (Exception e) {
                    e.printStackTrace();
                    hmap.put("localcellid", " NA");

                }


                try {


                    hmap.put("enb", String.valueOf(Utils.getenb((int) cid)));
                } catch (Exception e) {
                    e.printStackTrace();
                    hmap.put("enb", "NA");

                }
            } else {
                hmap.put("enb", "NA");
                hmap.put("cellid", "NA");
                hmap.put("localcellid", " NA");

            }
        } else {
            hmap.put("enb", "NA");
            hmap.put("cellid", "NA");
            hmap.put("localcellid", " NA");
        }


        int SsRsrp = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            SsRsrp = ss.getCsiRsrp();
            SsRsrp = ss.getSsRsrp();
            if (!Utils.isMaxint(SsRsrp)) {
                hmap.put("rsrp", SsRsrp + "");
            } else {
                hmap.put("rsrp", "NA");

            }
        } else {
            hmap.put("rsrp", "NA");

        }


        int CsiRsrq = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            CsiRsrq = ss.getCsiRsrq();
            CsiRsrq = ss.getSsRsrq();
            if (!Utils.isMaxint(CsiRsrq)) {

                hmap.put("rsrq", "" + CsiRsrq);
            } else {
                hmap.put("rsrq", "NA");
            }
        } else {

            hmap.put("rsrq", "NA");
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            int SsSinr = ss.getSsSinr();
            if (!Utils.isMaxint(SsSinr)) {
                hmap.put("snr", "" + SsSinr);
                hmap.put("cqi", "" + getvalueofcqinr(String.valueOf(SsSinr)));

            } else {
                hmap.put("snr", "NA");
                hmap.put("cqi", "NA");


            }
        } else {
            hmap.put("snr", "NA");
            hmap.put("cqi", "NA");


        }


        int pci = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            pci = MyPhoneStateListener.cellIdentityNr.getPci();
            if (!Utils.isMaxint(pci)) {
                hmap.put("pci", "" + pci);
            } else {
                hmap.put("pci", "NA");

            }
        } else {
            hmap.put("pci", "NA");

        }


        int arfcn = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arfcn = cellIdentityNr.getNrarfcn();
            if (!Utils.isMaxint(arfcn)) {
                hmap.put("earfcn", arfcn + "");
            } else {
                hmap.put("earfcn", "NA");

            }
        } else {
            hmap.put("earfcn", "NA");

        }


        int tac = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tac = cellIdentityNr.getTac();
            if (!Utils.isMaxint(tac)) {
                hmap.put("tac", "" + tac);
            } else {
                hmap.put("tac", "NA");

            }
        } else {
            hmap.put("tac", "NA");

        }


        com.functionapps.mview_sdk2.helper.mView_HealthStatus.servingcell2info.add(hmap);

        if (network_list != null) {
            network_list.onSuccess(com.functionapps.mview_sdk2.helper.mView_HealthStatus.servingcell2info);
        }


    }

    @SuppressLint("MissingPermission")
    public static String getSNR() {

            /*
             /*
             * @see #NETWORK_TYPE_UNKNOWN
             * @see #NETWORK_TYPE_GPRS
             * @see #NETWORK_TYPE_EDGE
             * @see #NETWORK_TYPE_UMTS
             * @see #NETWORK_TYPE_HSDPA
             * @see #NETWORK_TYPE_HSUPA
             * @see #NETWORK_TYPE_HSPA
             * @see #NETWORK_TYPE_CDMA
             * @see #NETWORK_TYPE_EVDO_0
             * @see #NETWORK_TYPE_EVDO_A
             * @see #NETWORK_TYPE_EVDO_B
             * @see #NETWORK_TYPE_1xRTT
             * @see #NETWORK_TYPE_IDEN
             * @see #NETWORK_TYPE_LTE
             * @see #NETWORK_TYPE_EHRPD
             * @see #NETWORK_TYPE_HSPAP
             * @see #NETWORK_TYPE_NR
             */


        try {
            int NetworkType = Integer.parseInt(network_type);
            if (NetworkType == 0 || NetworkType == 1 || NetworkType == 2) {
                return "0";
            }
            if (NetworkType == 3 || NetworkType == 8 || NetworkType == 9 || NetworkType == 10 || NetworkType == 15) {
                return "0";
            }
            if (NetworkType == 4 || NetworkType == 7) {
                return "0";
            }
            if (NetworkType == 5 || NetworkType == 6 || NetworkType == 12 || NetworkType == 14) {
                if (com.functionapps.mview_sdk2.helper.mView_HealthStatus.EVDOSNR <= -1 || com.functionapps.mview_sdk2.helper.mView_HealthStatus.EVDOSNR >= 50) {
                    return "0";
                }
                return String.valueOf(com.functionapps.mview_sdk2.helper.mView_HealthStatus.EVDOSNR);
            } else if (NetworkType != 13) {
                return "0";
            } else {
                if (Integer.parseInt(com.functionapps.mview_sdk2.helper.mView_HealthStatus.lteSNR) == -99.0d || Integer.parseInt(com.functionapps.mview_sdk2.helper.mView_HealthStatus.lteSNR) < -50.0d || Integer.parseInt(com.functionapps.mview_sdk2.helper.mView_HealthStatus.lteSNR) >= 80.0d) {
                    return "0";
                }
                return com.functionapps.mview_sdk2.helper.mView_HealthStatus.lteSNR; //String.valueOf(mView_HealthStatus.LTESNR);
            }
        } catch (Exception e) {
            return "0";
        }


    }


    public static JSONObject getsim2servingcellinfo() {
        final JSONObject[] finalobj = {new JSONObject()};
        getservingcell2info(new Interfaces.Network_list() {
            @Override
            public void onSuccess(ArrayList<HashMap<String, String>> array) {
                Log.d(TAG, "sim 2 serving cell array: "+array);
                if (mView_HealthStatus.second_cellInstance != null) {

                    for (int i = 0; i < array.size(); i++) {
                        HashMap<String, String> hmap = new HashMap<>();

                        hmap = array.get(i);


                        String type = hmap.get("type");
                        String rsrp = hmap.get("rsrp");
                        String rsrq = hmap.get("rsrq");
                        String mcc = hmap.get("mcc");
                        String mnc = hmap.get("mnc");
                        String cqi = hmap.get("cqi");
                        String cellid = hmap.get("cellid");
                        String pci = hmap.get("pci");
                        String tac = hmap.get("tac");
                        String earfcn = hmap.get("earfcn");
                        String enb = hmap.get("enb");
                        String ta = hmap.get("ta");
                        String snr = hmap.get("snr");
                        String localcellid = hmap.get("localcellid");

                        try {

                            finalobj[0].put("cellid", cellid);
                            finalobj[0].put("Lcellid", localcellid);

                            finalobj[0].put("ratType", type);

//                    finalobj.put("NodeBId", " ");
                            finalobj[0].put("enb", enb);

                            finalobj[0].put("mcc", mcc);

                            finalobj[0].put("mnc", mnc);
                            finalobj[0].put("snr", snr);

                            finalobj[0].put("earfcn", earfcn);

                            finalobj[0].put("rsrp", rsrp);


                            finalobj[0].put("pci", pci);


                            finalobj[0].put("ta", ta);
                            finalobj[0].put("cqi", cqi);

                            finalobj[0].put("signalStrength", rsrp);


                            finalobj[0].put("tac", tac);

                        }  catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                } else {
                    try {
                        finalobj[0].put("cellid", nosim);
                        finalobj[0].put("Lcellid", nosim);
                        finalobj[0].put("ratType", nosim);
//                finalobj.put("NodeBId", nosim);
                        finalobj[0].put("enb", nosim);
                        finalobj[0].put("snr", nosim);
                        finalobj[0].put("earfcn", nosim);
                        finalobj[0].put("rsrp", nosim);
                        finalobj[0].put("pci", nosim);
                        finalobj[0].put("ta", nosim);
                        finalobj[0].put("cqi", nosim);
                        finalobj[0].put("signalStrength", nosim);
                        finalobj[0].put("tac", nosim);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure() {
                Log.d(TAG, "onFailure: ");
                finalobj[0] =addDefaultDataOnFailedResponse();
            }
        });


        return finalobj[0];
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void fetchNeighbour1(Interfaces.Neighbour_cell_List network_list) {
        List<CellInfo> neighborCelllInfo = new ArrayList<>();

//        NeighboringCellsInfo.neighboringCellList = new ArrayList<>();
//        NeighboringCellsInfo.wcdma_neighboringCellList = new ArrayList<>();
//        NeighboringCellsInfo.gsm_neighboringCellList = new ArrayList<>();
//        NeighboringCellsInfo.nr_neighboringCellList = new ArrayList<>();

        CellInfo m;
        TelephonyManager telMgr = (TelephonyManager) MviewApplication.ctx.getSystemService(Context.TELEPHONY_SERVICE);
        List<CellInfo> cellInfo;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (ActivityCompat.checkSelfPermission(MviewApplication.ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            cellInfo = telMgr.getAllCellInfo();
            Log.i(TAG, "fetchNeighbour1 cellinfo object: "+cellInfo);

            for (int i = 0; i < cellInfo.size(); i++) {
                m = cellInfo.get(i);
                if (m.isRegistered()) {


                } else {
                    neighborCelllInfo.add(m);
                }
            }
            Log.i(TAG, " Neighbouring cells info size: "+neighborCelllInfo.size());

            Utils.appendLog(TAG + " Neighbouring cells info size "+neighborCelllInfo.size());
            if (neighborCelllInfo.size() > 0) {//size check to be written for greater than 0..fixed by Sonal on 17-01-2024
                for (int i = 0; i < neighborCelllInfo.size(); i++) {
                    Log.i(Mview.TAG,"Neighbour cell with index: "+ neighborCelllInfo.get(i));
                    Log.i(Mview.TAG,"Network list: "+ network_list.toString());


                    if (neighborCelllInfo.get(i) != null) {//h
                        Utils.appendLog(TAG + " Neighbouring cells found ");
                        addNeighbourcellInfo1(neighborCelllInfo.get(i), network_list);
                    } else {
                        Utils.appendLog(TAG + " Neighbouring cells info is null ");
                        if (network_list != null) {
                            network_list.onFailure();
                        }
                    }
                }
            }
            else {
                Utils.appendLog(TAG+" NO neighbouring cells found ");
                if(network_list!=null)
                {
                    network_list.onFailure();
                }
                Log.i(Mview.TAG,"no neighbours here");
            }
        }
    }





    private static void addNeighbourcellInfo1(CellInfo cellInfo, Interfaces.Neighbour_cell_List list) {



        if (cellInfo instanceof CellInfoGsm)
        {
            Log.i(Mview.TAG,"neighbour is of gsm");
            CellInfoGsm cellInfogsm = (CellInfoGsm) cellInfo;
            CellIdentityGsm c = cellInfogsm.getCellIdentity();
            CellSignalStrengthGsm ss = cellInfogsm.getCellSignalStrength();
            getNeighboringCellsInfoForGSM(ss, c, cellInfo.isRegistered(),list);


        } else if (cellInfo instanceof CellInfoLte) {

            Log.i(Mview.TAG,"neighbour is of lte");

            CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;


            CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();


            CellSignalStrengthLte ss = cellInfoLte.getCellSignalStrength();

            getNeighboringCellsInfoForLte(ss, cellIdentityLte, cellInfo.isRegistered(),list);
        } else if  (cellInfo  instanceof CellInfoWcdma) {
            Log.i(Mview.TAG,"neighbour is of wcdma");

            CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfo;
            CellIdentityWcdma cellIdentityWcdma = cellInfoWcdma.getCellIdentity();
            CellSignalStrengthWcdma ss = cellInfoWcdma.getCellSignalStrength();
            getNeighboringCellsInfoForWcdma(ss, cellIdentityWcdma, cellInfo.isRegistered(),list);


        }


        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (cellInfo   instanceof CellInfoNr)
            {
                Log.i(Mview.TAG,"neighbour is of nr");

                CellInfoNr cellInfoNr = (CellInfoNr) cellInfo;
                CellIdentityNr cellIdentityNr = (CellIdentityNr) cellInfoNr.getCellIdentity();
                CellSignalStrengthNr ss = (CellSignalStrengthNr) cellInfoNr.getCellSignalStrength();

                getNeighboringCellsInfoForNR(ss, cellIdentityNr, cellInfo.isRegistered(),list);


            }
        }







    }


    public static JSONObject getsim1neighbourcellinfo() {

        final  JSONObject obj = new JSONObject();

        fetchNeighbour1(new Interfaces.Neighbour_cell_List()
        {
            @Override
            public void onSuccess(ArrayList<HashMap<String, String>>  list,String type)
            {
                JSONArray finalobj = new JSONArray();

                if (list!=null&&list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {

                        try {
                            obj.put("type", type);

                            if (type != null && (type.equalsIgnoreCase("LTE")||type.equalsIgnoreCase("nr"))) {


                                obj.put("cellId", list.get(i).get("4G_CI"));
                                obj.put("pci", list.get(i).get("4G_PCI"));
                                obj.put("channel", list.get(i).get("4G_EARFCN"));
                                obj.put("ta", list.get(i).get("4G_TA"));
                                obj.put("cqi", list.get(i).get("4G_CQI"));
                                obj.put("rsrq", list.get(i).get("4G_RSRQ"));
                                obj.put("rsrp", list.get(i).get("4G_RSRP"));
                                obj.put("tac", list.get(i).get("4G_TAC"));
                                obj.put("sinr", list.get(i).get("4G_SINR"));
                                obj.put("enb", list.get(i).get("4G_ENB"));
                                obj.put("mcc", list.get(i).get("4G_MCC"));
                                obj.put("mnc", list.get(i).get("4G_MNC"));
                                obj.put("isRegistered", list.get(i).get("isregistered"));


                            } else if (type.equalsIgnoreCase("Wcdma")) {
                                obj.put("mcc", list.get(i).get("3G_MCC"));
                                obj.put("mnc", list.get(i).get("3G_MNC"));
                                obj.put("cellId", list.get(i).get("3G_CID"));
                                obj.put("pci", list.get(i).get("3G_PSC"));
                                obj.put("cqi", list.get(i).get("3G_CQI"));
                                obj.put("isRegistered", list.get(i).get("isRegistered"));
                                obj.put("rsrq", list.get(i).get("signalstrength"));


                            } else if (type.equalsIgnoreCase("Gsm")) {
                                obj.put("cellId", list.get(i).get("G_CID"));


                            } else {
                                obj.put("cellId", "NA");

                            }
                            finalobj.put(obj);


                        } catch (Exception jsonException) {
                            jsonException.printStackTrace();
                        }
                    }


                }
            }

            @Override
            public void onFailure() {

            }
        });
        return obj;

    }

    private static JSONObject addDefaultParamOnFailedResponse()
    {
        JSONObject defaultJsonData = new JSONObject();

        try {
            defaultJsonData.put("cellid1", "NA");
            defaultJsonData.put("pci", "NA");
            defaultJsonData.put("channel", "NA");
            defaultJsonData.put("ta", "NA");
            defaultJsonData.put("cqi", "NA");
            defaultJsonData.put("rsrq", "NA");
            defaultJsonData.put("rsrp","NA");
            defaultJsonData.put("tac", "NA");
            defaultJsonData.put("sinr", "NA");
            defaultJsonData.put("enb", "NA");
            defaultJsonData.put("mcc","NA");
            defaultJsonData.put("mnc", "NA");
            defaultJsonData.put("isRegistered", "NA");
        } catch (Exception e) {
            Utils.appendLog(TAG+" Exception ocuured while adding default values "+e.getMessage());
            e.printStackTrace();
        }

        return defaultJsonData;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static void getNeighboringCellsInfoForGSM(CellSignalStrengthGsm ss, CellIdentityGsm cellIdentityGsm, boolean isRegistered, Interfaces.Neighbour_cell_List list) {

		/*CellInfoGsm:{mRegistered=NO mTimeStampType=oem_ril mTimeStamp=15712607914940ns mCellConnectionStatus=0
			CellIdentityGsm:{ mLac=1014 mCid=48161 mArfcn=661 mBsic=0x15 mMcc=404 mMnc=11
		mAlphaLong=404 11 mAlphaShort=404 11} CellSignalStrengthGsm: ss=8 ber=99 mTa=2147483647},*/
        try {
//            if (NeighboringCellsInfo.gsm_neighboringCellList != null) {
            HashMap<String, String> hp = new HashMap<>();
            NeighboringCellsInfo.gsmParams = new ArrayList<>();
            NeighboringCellsInfo.gsmParams.add("2G_LAC");


            hp.put("2G_LAC", ""+cellIdentityGsm.getLac());


            NeighboringCellsInfo.gsmParams.add("2G_CID");
            hp.put("2G_CID", ""+cellIdentityGsm.getCid());

            NeighboringCellsInfo.gsmParams.add("2G_ARFCN");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                hp.put("2G_ARFCN",""+ cellIdentityGsm.getArfcn());
            } else {
                hp.put("2G_ARFCN", "0");
            }

            NeighboringCellsInfo.gsmParams.add("2G_BSIC");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                hp.put("2G_BSIC", ""+cellIdentityGsm.getBsic());
            } else {
                hp.put("2G_BSIC", "0");
            }


            NeighboringCellsInfo.gsmParams.add("2G_PSC");
            hp.put("2G_PSC", ""+cellIdentityGsm.getPsc());

            NeighboringCellsInfo.gsmParams.add("2G_RX_LEVEL");
            hp.put("2G_RX_LEVEL", ""+ss.getDbm());
            NeighboringCellsInfo.gsm_neighnor_ss = ss.getDbm();

            NeighboringCellsInfo.gsmParams.add("2G_TA");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                hp.put("2G_TA", ""+ss.getTimingAdvance());
            } else {
                hp.put("2G_TA", "0");
            }
            byte[] l_byte_array = com.functionapps.mview_sdk2.helper.CommonFunctions.convertByteArray__p(cellIdentityGsm.getCid());
            int l_RNC_ID = com.functionapps.mview_sdk2.helper.CommonFunctions.getRNCID_or_CID__p(l_byte_array, CommonFunctions.RNCID_C);
            NeighboringCellsInfo.gsmParams.add("2G_SITE_ID");
//                hp.put(7, l_RNC_ID);
            NeighboringCellsInfo.gsmParams.add("signalstrength");
            hp.put("signalstrength", ""+ss.getDbm());

//                NeighboringCellsInfo.gsm_neighboringCellList.add(hp);
            NeighboringCellsInfo.neighboringCellList1.add(hp);

            if (list!=null)
            {
                list.onSuccess(NeighboringCellsInfo.neighboringCellList1,"gsm");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Utils.appendLog(TAG+" Exception occured in getNeighboringCellsInfoForGSM "+e.getMessage());
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private static void getNeighboringCellsInfoForWcdma(CellSignalStrengthWcdma ss, CellIdentityWcdma cellIdentityWcdma, boolean isRegistered, Interfaces.Neighbour_cell_List list) {

		/*CellInfoWcdma:{mRegistered=NO mTimeStampType=oem_ril mTimeStamp=10537808770612ns mCellConnectionStatus=0 CellIdentityWcdma:
		{ mLac=2147483647 mCid=2147483647 mPsc=260
		mUarfcn=10757 mMcc=null mMnc=null mAlphaLong= mAlphaShort=} CellSignalStrengthWcdma: ss=6 ber=99}*/
        try {
            NeighboringCellsInfo.neighboringCellList1=new ArrayList<>();
            HashMap<String ,String>hashMap=new HashMap<>();

            NeighboringCellsInfo.wcdma_neighborCells = new HashMap<>();
            NeighboringCellsInfo.wcdmaParams = new ArrayList<>();


            NeighboringCellsInfo.wcdmaParams.add("3G_CID");
            if (Utils.isMaxint(cellIdentityWcdma.getCid()))

            {
                NeighboringCellsInfo.wcdma_neighborCells.put(0, 0);
                hashMap.put("3G_CID","NA");

            }
            else {
                NeighboringCellsInfo.wcdma_neighborCells.put(0, cellIdentityWcdma.getCid());
                hashMap.put("3G_CID","" + cellIdentityWcdma.getCid());

            }

            NeighboringCellsInfo.wcdmaParams.add("3G_LAC");
            NeighboringCellsInfo.wcdma_neighborCells.put(1,  cellIdentityWcdma.getLac());
            hashMap.put("3G_LAC","" + cellIdentityWcdma.getLac());



            NeighboringCellsInfo.wcdmaParams.add("3G_PSC");
            NeighboringCellsInfo.wcdma_neighborCells.put(2, cellIdentityWcdma.getPsc());
            hashMap.put("3G_PSC","" + cellIdentityWcdma.getPsc());

            NeighboringCellsInfo.wcdmaParams.add("3G_UARFCN");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                NeighboringCellsInfo.wcdma_neighborCells.put(3,  cellIdentityWcdma.getUarfcn());
                hashMap.put("3G_UARFCN","" + cellIdentityWcdma.getUarfcn());

            } else {
                NeighboringCellsInfo.wcdma_neighborCells.put(3, 0);
                hashMap.put("3G_UARFCN","NA");


            }

            NeighboringCellsInfo.wcdmaParams.add("3G_RSCP");
            NeighboringCellsInfo.wcdma_neighborCells.put(4, ss.getDbm());
            hashMap.put("3G_RSCP",""+ss.getDbm());

            NeighboringCellsInfo.threeG_neighbor_ss = ss.getDbm();


            NeighboringCellsInfo.wcdmaParams.add("3G_RSSI");
            NeighboringCellsInfo.wcdma_neighborCells.put(5, 0);
            hashMap.put("3G_RSSI","0");


            NeighboringCellsInfo.wcdmaParams.add("3G_CQI");
            NeighboringCellsInfo.wcdma_neighborCells.put(6, 0);
            hashMap.put("3G_CQI","0");


            NeighboringCellsInfo.wcdmaParams.add("3G_NODE_BID");
            NeighboringCellsInfo.wcdma_neighborCells.put(7,  Utils.getenb(cellIdentityWcdma.getCid()));
            hashMap.put("3G_CQI","" + Utils.getenb(cellIdentityWcdma.getCid()));

            NeighboringCellsInfo.wcdmaParams.add("signalstrength");

//
            String signalstrength = ss.toString().replace("CellSignalStrengthWcdma:", "");
            String[] signal = signalstrength.toString().split(" ");
            NeighboringCellsInfo.wcdma_neighborCells.put(8, Integer.valueOf(signalstrength));
            hashMap.put("signalstrength","" + signalstrength);
//
//
//                NeighboringCellsInfo.wcdma_neighboringCellList.add(NeighboringCellsInfo.wcdma_neighborCells);
            NeighboringCellsInfo.wcdmaParams.add("isRegistered");
            NeighboringCellsInfo.wcdma_neighborCells.put(9, Integer.valueOf("" +isRegistered));
            hashMap.put("isRegistered","" + isRegistered);

            NeighboringCellsInfo.wcdmaParams.add("3G_MCC");

            if (cellIdentityWcdma.getMcc() != 0) {

                if (cellIdentityWcdma.getMcc() != Integer.MAX_VALUE) {

                    NeighboringCellsInfo.wcdma_neighborCells.put(11, cellIdentityWcdma.getMcc());//mcc
                    hashMap.put("3G_MCC","" + cellIdentityWcdma.getMcc());

                } else {
                    NeighboringCellsInfo.wcdma_neighborCells.put(11, com.functionapps.mview_sdk2.helper.mView_HealthStatus.prim_mcc);//mcc
                    hashMap.put("3G_MCC","" + com.functionapps.mview_sdk2.helper.mView_HealthStatus.prim_mcc);


                }
            } else {
                NeighboringCellsInfo.lte_neighborCells.put(11,  com.functionapps.mview_sdk2.helper.mView_HealthStatus.prim_mcc);//mcc


            }
            NeighboringCellsInfo.wcdmaParams.add("3G_MNC");

            if (cellIdentityWcdma.getMnc() != 0) {
                if (cellIdentityWcdma.getMnc() != Integer.MAX_VALUE) {
                    NeighboringCellsInfo.wcdma_neighborCells.put(12,  cellIdentityWcdma.getMnc());//mnc
                    hashMap.put("3G_MNC","" + cellIdentityWcdma.getMnc());

                } else {
                    NeighboringCellsInfo.wcdma_neighborCells.put(12, com.functionapps.mview_sdk2.helper.mView_HealthStatus.mnc);//mnc
                    hashMap.put("3G_MNC","" + com.functionapps.mview_sdk2.helper.mView_HealthStatus.mnc);


                }

            } else {
                NeighboringCellsInfo.wcdma_neighborCells.put(12, com.functionapps.mview_sdk2.helper.mView_HealthStatus.mnc);//mnc
                hashMap.put("3G_MNC","" + com.functionapps.mview_sdk2.helper.mView_HealthStatus.mnc);


            }


            NeighboringCellsInfo.wcdma_neighboringCellList.add(NeighboringCellsInfo.wcdma_neighborCells);
            NeighboringCellsInfo.neighboringCellList1.add(hashMap);

            if (list!=null)
            {
                list.onSuccess(NeighboringCellsInfo.neighboringCellList1,"wcdma");
            }


        } catch (Exception e) {
            e.printStackTrace();
            Utils.appendLog(TAG+" Exception occured in getNeighboringCellsInfoForWcdma "+e.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private static void getNeighboringCellsInfoForNR(CellSignalStrengthNr ss, CellIdentityNr cellIdentitynr, boolean isRegistered, Interfaces.Neighbour_cell_List list) {

        try {

            NeighboringCellsInfo.neighboringCellList1=new ArrayList<>();
            HashMap<String,String>hashmap=new HashMap<>();
            NeighboringCellsInfo.nr_neighborCells = new HashMap<>();
            NeighboringCellsInfo.nrParams = new ArrayList<>();

            NeighboringCellsInfo.nrParams.add("4G_PCI");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (cellIdentitynr.getPci() != Integer.MAX_VALUE) {

                    NeighboringCellsInfo.nr_neighborCells.put(0, "" + cellIdentitynr.getPci());//pci
                    hashmap.put("4G_PCI","" + cellIdentitynr.getPci());
                } else {
                    NeighboringCellsInfo.nr_neighborCells.put(0, "NA");//pci
                    hashmap.put("4G_PCI","NA");

                }
            }
            else
            {
                NeighboringCellsInfo.nr_neighborCells.put(0, "NA");//pci
                hashmap.put("4G_PCI","NA");

            }



            NeighboringCellsInfo.nrParams.add("4G_EARFCN");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (cellIdentitynr.getNrarfcn() != Integer.MAX_VALUE) {

                        NeighboringCellsInfo.nr_neighborCells.put(1, "" + cellIdentitynr.getNrarfcn());
                        hashmap.put("4G_EARFCN","" + cellIdentitynr.getNrarfcn());

                    } else {
                        NeighboringCellsInfo.nr_neighborCells.put(1, "NA");
                        hashmap.put("4G_EARFCN","NA");



                    }
                }
            } else {
                NeighboringCellsInfo.nr_neighborCells.put(1, "NA");
                hashmap.put("4G_EARFCN","NA");

            }

            NeighboringCellsInfo.nrParams.add("4G_TA");

            NeighboringCellsInfo.lte_neighborCells.put(2, 0);//ta

            hashmap.put("4G_TA","NA");








            NeighboringCellsInfo.nrParams.add("4G_RSRQ");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Log.i(Mview.TAG, "rsrq is " + ss.getSsRsrq());
                    hashmap.put("4G_RSRQ",""+ss.getSsRsrq());

                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (ss.getSsRsrq() != Integer.MAX_VALUE) {

                        NeighboringCellsInfo.nr_neighborCells.put(4, "" + ss.getSsRsrq());//rsrq
                        hashmap.put("4G_RSRQ",""+ss.getSsRsrq());

                    } else {
                        NeighboringCellsInfo.nr_neighborCells.put(4, "NA");//cqi
                        hashmap.put("4G_RSRQ","NA");


                    }
                }
            } else {
                NeighboringCellsInfo.nr_neighborCells.put(4, "NA");//rsrq
                hashmap.put("4G_RSRQ","NA");


            }


            NeighboringCellsInfo.nrParams.add("4G_RSRP");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Log.i(Mview.TAG, "rsrp is " + ss.getSsRsrp());
                    hashmap.put("4G_RSRP",""+ss.getSsRsrp());

                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (ss.getSsRsrp() != Integer.MAX_VALUE) {

                        NeighboringCellsInfo.nr_neighborCells.put(5, "" + ss.getSsRsrp());//rsrp
                        hashmap.put("4G_RSRP",""+ss.getSsRsrp());

                    } else {
                        NeighboringCellsInfo.nr_neighborCells.put(5, "NA");//rsrq
                        hashmap.put("4G_RSRP","NA");


                    }
                }
            } else {
                NeighboringCellsInfo.nr_neighborCells.put(5, "NA");//rsrp
                hashmap.put("4G_RSRP","NA");

//                    NeighboringCellsInfo.nr_neighborCells = 0;
            }


            NeighboringCellsInfo.nrParams.add("4G_TAC");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (cellIdentitynr.getTac() != Integer.MAX_VALUE) {

                    NeighboringCellsInfo.nr_neighborCells.put(6, "" + cellIdentitynr.getTac());//tac
                    hashmap.put("4G_TAC", "" + cellIdentitynr.getTac());


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        Log.i(Mview.TAG, "tac is " + cellIdentitynr.getTac());
                    }
                } else {
                    hashmap.put("4G_TAC", "NA");
                    NeighboringCellsInfo.nr_neighborCells.put(6, "NA");//tac

                }
            }
            else {
                NeighboringCellsInfo.nr_neighborCells.put(6, "NA");//tac
                hashmap.put("4G_TAC", "NA");

            }
            NeighboringCellsInfo.nrParams.add("4G_SINR");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (ss.getCsiSinr() != Integer.MAX_VALUE) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            NeighboringCellsInfo.nr_neighborCells.put(7, "" + ss.getCsiSinr());//rssnr
                            NeighboringCellsInfo.nrParams.add("4G_CQI");
                            hashmap.put("4G_CQI",""+getvalueofcqinr(String.valueOf(ss.getCsiSinr())) );

                            hashmap.put("4G_SINR",""+ss.getCsiSinr() );

                            NeighboringCellsInfo.nr_neighborCells.put(3, ""+getvalueofcqinr(String.valueOf(ss.getCsiSinr())));//cqi
                        }
                    } else {
                        NeighboringCellsInfo.nr_neighborCells.put(7, "NA");//rssnr
                        NeighboringCellsInfo.nrParams.add("4G_CQI");
                        hashmap.put("4G_SINR","NA");

                        hashmap.put("4G_CQI","NA");

                        NeighboringCellsInfo.nr_neighborCells.put(3, "NA");

                    }
                }
            } else {
                NeighboringCellsInfo.nr_neighborCells.put(7, "NA");//rssnr
                NeighboringCellsInfo.nrParams.add("4G_CQI");
                hashmap.put("4G_SINR","NA");

                hashmap.put("4G_CQI","NA");
                NeighboringCellsInfo.nr_neighborCells.put(3, "NA");


            }
               /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        Log.i(Mview.TAG, "sinr is " + ss.getCsiSinr());
                    }
                } else {

                }
*/

            NeighboringCellsInfo.nrParams.add("4G_CI");


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (cellIdentitynr.getNci() != Integer.MAX_VALUE) {
                    NeighboringCellsInfo.nr_neighborCells.put(8, "" + cellIdentitynr.getNci());//cid
                    hashmap.put("4G_CI","" + cellIdentitynr.getNci());

                } else {
                    NeighboringCellsInfo.nr_neighborCells.put(8, "NA");//cid
                    hashmap.put("4G_CI","NA");


                }
            }
            else
            {
                NeighboringCellsInfo.nr_neighborCells.put(8, "NA");//cid
                hashmap.put("4G_CI","NA");


            }



            NeighboringCellsInfo.nrParams.add("4G_ENB");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NeighboringCellsInfo.nr_neighborCells.put(9, "" + Utils.getenb((int) cellIdentitynr.getNci()));//cid
                hashmap.put("4G_ENB", "" + Utils.getenb((int) cellIdentitynr.getNci()));

            }
            else
            {
                NeighboringCellsInfo.nr_neighborCells.put(9,"NA");
                hashmap.put("4G_ENB","NA");

            }


//                NeighboringCellsInfo.nr_neighboringCellList.add("signalstrength");

/*

               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (ss.getCsiRsrp() != Integer.MAX_VALUE) {
                        NeighboringCellsInfo.nr_neighborCells.put(10, "" + ss.getCsiRsrp());//rsrp
                    } else {
                        NeighboringCellsInfo.nr_neighborCells.put(10, "NA");//rsrp

                    }
                } else {
                    NeighboringCellsInfo.nr_neighborCells.put(10, "NA");//rsrp

                }
*/

            NeighboringCellsInfo.nrParams.add("4G_MCC");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (cellIdentitynr.getMccString() != null) {


                    NeighboringCellsInfo.lte_neighborCells.put(11, Integer.valueOf(cellIdentitynr.getMccString()));//mcc
                    hashmap.put("4G_MCC",""+cellIdentitynr.getMccString());


                } else {
                    NeighboringCellsInfo.lte_neighborCells.put(11, com.functionapps.mview_sdk2.helper.mView_HealthStatus.prim_mcc);//mcc
                    hashmap.put("4G_MCC",""+ com.functionapps.mview_sdk2.helper.mView_HealthStatus.prim_mcc);



                }
            }
            NeighboringCellsInfo. nrParams.add("4G_MNC");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (cellIdentitynr.getMncString() !=  null) {
                    NeighboringCellsInfo.nr_neighborCells.put(12, "" + cellIdentitynr.getMncString());//mnc
                    hashmap.put("4G_MNC","" + cellIdentitynr.getMncString());



                } else {
                    hashmap.put("4G_MNC","" + com.functionapps.mview_sdk2.helper.mView_HealthStatus.mnc);
                    NeighboringCellsInfo.nr_neighborCells.put(12, "" + com.functionapps.mview_sdk2.helper.mView_HealthStatus.mnc);//mnc

                }
            }
            NeighboringCellsInfo.lte_neighborCells.put(11,com.functionapps.mview_sdk2.helper.mView_HealthStatus.prim_mnc);//mcc
            hashmap.put("4G_MNC","" + com.functionapps.mview_sdk2.helper.mView_HealthStatus.mnc);




            NeighboringCellsInfo.nrParams.add("4G_Network");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

                if (ListenService.telMgr != null) {
//                        NeighboringCellsInfo.nr_neighborCells.put(13, telMgr.getSimOperatorName());//mnc
//                        hashmap.put("4G_Network","" + mView_HealthStatus.mnc);

                }
                else
                {
                    NeighboringCellsInfo.nr_neighborCells.put(13, "NA");//operator

                }
            }
            else {
                NeighboringCellsInfo.nr_neighborCells.put(13, "NA");//operator

            }


            NeighboringCellsInfo.nrParams.add("isregistered");
            hashmap.put("isregistered","" + isRegistered);

            NeighboringCellsInfo.nr_neighborCells.put(14, "" + isRegistered);//isregisterd


            NeighboringCellsInfo.neighboringCellList1.add(hashmap);
            NeighboringCellsInfo.nr_neighboringCellList.add(NeighboringCellsInfo.nr_neighborCells);
            Log.i(Mview.TAG, "Neighbour cell list " + NeighboringCellsInfo.neighboringCellList);

            if (list!=null)
            {
                list.onSuccess(NeighboringCellsInfo.neighboringCellList1,"nr");
            }

        } catch (Exception e) {
            Utils.appendLog(TAG+" Exception occured in getNeighboringCellsInfoForNR "+e.getMessage());
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static void getNeighboringCellsInfoForLte(CellSignalStrengthLte ss, CellIdentityLte cellIdentityLte, boolean isRegistered, Interfaces.Neighbour_cell_List list) {
        try {

            HashMap <String,String >hashMap= new HashMap<>();
            NeighboringCellsInfo.neighboringCellList1=new ArrayList<>();

            NeighboringCellsInfo.lte_neighborCells = new HashMap<>();
            NeighboringCellsInfo.lteParams = new ArrayList<>();

            NeighboringCellsInfo.lteParams.add("4G_PCI");

            if (cellIdentityLte.getPci() != Integer.MAX_VALUE) {
                Log.i(Mview.TAG, "PCI is " + cellIdentityLte.getPci());
                NeighboringCellsInfo.lte_neighborCells.put(0,cellIdentityLte.getPci());//


                hashMap.put("4G_PCI", "" + cellIdentityLte.getPci());

            } else {
                NeighboringCellsInfo.lte_neighborCells.put(0, Integer.valueOf("NA"));//pci
                hashMap.put("4G_PCI", "NA");
            }


            NeighboringCellsInfo.lteParams.add("4G_EARFCN");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (cellIdentityLte.getEarfcn() != Integer.MAX_VALUE) {

                    NeighboringCellsInfo.lte_neighborCells.put(1, cellIdentityLte.getEarfcn());
                    hashMap.put("4G_EARFCN", "" + cellIdentityLte.getEarfcn());
                }
                else
                {
//                        NeighboringCellsInfo.lte_neighborCells.put(1, "NA");
                    hashMap.put("4G_EARFCN", "NA");

                }
            } else {
                NeighboringCellsInfo.lte_neighborCells.put(1, Integer.valueOf("NA"));
                hashMap.put("4G_EARFCN", "NA");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Log.i(Mview.TAG, "Earfcn is " + cellIdentityLte.getEarfcn());
            }


            NeighboringCellsInfo.lteParams.add("4G_TA");
            if (ss.getTimingAdvance() != Integer.MAX_VALUE) {

                NeighboringCellsInfo.lte_neighborCells.put(2,  ss.getTimingAdvance());//ta
                hashMap.put("4G_TA", "" + ss.getTimingAdvance());



            } else {
                NeighboringCellsInfo.lte_neighborCells.put(2, Integer.valueOf("NA"));//ta
                hashMap.put("4G_TA", "NA" );



            }
            Log.i(Mview.TAG, "TA is " + ss.getTimingAdvance());


            NeighboringCellsInfo.lteParams.add("4G_CQI");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (ss.getCqi() != Integer.MAX_VALUE) {

                    NeighboringCellsInfo.lte_neighborCells.put(3,  ss.getCqi());//cqi
                    hashMap.put("4G_CQI","" + ss.getCqi() );

                } else {
                    NeighboringCellsInfo.lte_neighborCells.put(3, Integer.valueOf("NA"));//ta
                    hashMap.put("4G_CQI","NA"  );

                }
            } else {
                NeighboringCellsInfo.lte_neighborCells.put(3, Integer.valueOf("NA"));//cqi
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.i(Mview.TAG, "CQI is " + ss.getCqi());
            }


            NeighboringCellsInfo.lteParams.add("4G_RSRQ");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.i(Mview.TAG, "rsrq is " + ss.getRsrq());

                if (ss.getRsrq() != Integer.MAX_VALUE) {

                    NeighboringCellsInfo.lte_neighborCells.put(4,ss.getRsrq());//rsrq
                    hashMap.put("4G_RSRQ","" + ss.getRsrq());
                } else {
                    NeighboringCellsInfo.lte_neighborCells.put(4, Integer.valueOf("NA"));//cqi
                    hashMap.put("4G_RSRQ","NA" );

                }
            } else {
                NeighboringCellsInfo.lte_neighborCells.put(4, Integer.valueOf("NA"));//rsrq
                hashMap.put("4G_RSRQ","NA" );
            }


            NeighboringCellsInfo.lteParams.add("4G_RSRP");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.i(Mview.TAG, "rsrp is " + ss.getRsrp());

                if (ss.getRsrp() != Integer.MAX_VALUE) {

                    NeighboringCellsInfo.lte_neighborCells.put(5,  ss.getRsrp());//rsrp
                    hashMap.put("4G_RSRP","" + ss.getRsrp() );


                } else {
                    NeighboringCellsInfo.lte_neighborCells.put(5, Integer.valueOf("NA"));//rsrq
                    hashMap.put("4G_RSRP","" +  "NA");

                }
                NeighboringCellsInfo.lte_neighbor_ss = ss.getRsrp();
            } else {
                NeighboringCellsInfo.lte_neighborCells.put(5,Integer.valueOf("NA"));//rsrp
                NeighboringCellsInfo.lte_neighbor_ss = 0;
            }


            NeighboringCellsInfo.lteParams.add("4G_TAC");

            if (cellIdentityLte.getTac() != Integer.MAX_VALUE) {

                NeighboringCellsInfo.lte_neighborCells.put(6,  cellIdentityLte.getTac());//tac

                hashMap.put("4G_TAC", "" + cellIdentityLte.getTac());
                Log.i(Mview.TAG, "tac is " + cellIdentityLte.getTac());
            } else {
                NeighboringCellsInfo.lte_neighborCells.put(6, Integer.valueOf("NA"));//tac
                hashMap.put("4G_TAC", "NA");

            }

            NeighboringCellsInfo.lteParams.add("4G_SINR");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (ss.getRssnr() != Integer.MAX_VALUE) {
                    NeighboringCellsInfo.lte_neighborCells.put(7, ss.getRssnr());//rssnr

                    hashMap.put("4G_SINR", "" + ss.getRssnr());
                } else {
                    NeighboringCellsInfo.lte_neighborCells.put(7, Integer.valueOf("NA"));//rssnr
                    hashMap.put("4G_SINR", "NA");

                }
            } else {
                NeighboringCellsInfo.lte_neighborCells.put(7, Integer.valueOf("NA"));//rssnr
                hashMap.put("4G_SINR", "NA");

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.i(Mview.TAG, "sinr is " + ss.getRssnr());
            } else {

            }


            NeighboringCellsInfo.lteParams.add("4G_CI");


            if (cellIdentityLte.getCi() != Integer.MAX_VALUE) {
                NeighboringCellsInfo.lte_neighborCells.put(8,  cellIdentityLte.getCi());//cid
                hashMap.put("4G_CI","" + cellIdentityLte.getCi());
            } else {
                NeighboringCellsInfo.lte_neighborCells.put(8, Integer.valueOf("NA"));//cid
                hashMap.put("4G_CI","" + "NA");

            }


            NeighboringCellsInfo.lteParams.add("4G_ENB");

            NeighboringCellsInfo.lte_neighborCells.put(9,  Utils.getenb(cellIdentityLte.getCi()));//cid
            hashMap.put("4G_ENB",""+ Utils.getenb(cellIdentityLte.getCi()));

            NeighboringCellsInfo.lteParams.add("signalstrength");


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (ss.getRsrp() != Integer.MAX_VALUE) {
                    NeighboringCellsInfo.lte_neighborCells.put(10,ss.getRsrp());//rsrp
                    hashMap.put("signalstrength", "" + ss.getRsrp());
                } else {
                    NeighboringCellsInfo.lte_neighborCells.put(10, Integer.valueOf("NA"));//rsrp
                    hashMap.put("signalstrength", "NA");

                }
            } else {
                NeighboringCellsInfo.lte_neighborCells.put(10,0);//rsrp
                hashMap.put("signalstrength", "NA");



            }


            NeighboringCellsInfo.lteParams.add("4G_MCC");

            if (cellIdentityLte.getMcc() != 0) {

                if (cellIdentityLte.getMcc() != Integer.MAX_VALUE) {

                    NeighboringCellsInfo.lte_neighborCells.put(11, cellIdentityLte.getMcc());//mcc
                    hashMap.put("4G_MCC", "" + cellIdentityLte.getMcc());

                } else {
                    NeighboringCellsInfo.lte_neighborCells.put(11, com.functionapps.mview_sdk2.helper.mView_HealthStatus.prim_mcc);//mcc
                    hashMap.put("4G_MCC","" + com.functionapps.mview_sdk2.helper.mView_HealthStatus.prim_mcc);


                }
            } else {
                NeighboringCellsInfo.lte_neighborCells.put(11,  com.functionapps.mview_sdk2.helper.mView_HealthStatus.prim_mcc);//mcc
                hashMap.put("4G_MCC","" + com.functionapps.mview_sdk2.helper.mView_HealthStatus.prim_mcc);



            }
            NeighboringCellsInfo.lteParams.add("4G_MNC");

            if (cellIdentityLte.getMnc() != 0) {
                if (cellIdentityLte.getMnc() != Integer.MAX_VALUE) {
                    NeighboringCellsInfo.lte_neighborCells.put(12,  cellIdentityLte.getMnc());//mnc
                    hashMap.put("4G_MNC","" + cellIdentityLte.getMnc());

                } else {
                    NeighboringCellsInfo.lte_neighborCells.put(12,  com.functionapps.mview_sdk2.helper.mView_HealthStatus.mnc);//mnc
                    hashMap.put("4G_MNC", "" + com.functionapps.mview_sdk2.helper.mView_HealthStatus.mnc);


                }

            } else {
                NeighboringCellsInfo.lte_neighborCells.put(12,  com.functionapps.mview_sdk2.helper.mView_HealthStatus.mnc);//mnc
                hashMap.put("4G_MNC", "" + mView_HealthStatus.mnc);


            }


            NeighboringCellsInfo.lteParams.add("4G_Network");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

                NeighboringCellsInfo.lte_neighborCells.put(13, Integer.valueOf(cellIdentityLte.getMobileNetworkOperator()));//mnc
                hashMap.put("4G_Network", cellIdentityLte.getMobileNetworkOperator());

            } else {
                NeighboringCellsInfo.lte_neighborCells.put(13, Integer.valueOf("NA"));//operator
                hashMap.put("4G_Network", "");


            }


            NeighboringCellsInfo.lteParams.add("isregistered");
            NeighboringCellsInfo.lte_neighborCells.put(14, Integer.valueOf("" + isRegistered));//isregisterd
            hashMap.put("isregistered", ""+isRegistered);


            NeighboringCellsInfo.neighboringCellList1.add(hashMap);

            NeighboringCellsInfo.neighboringCellList.add(NeighboringCellsInfo.lte_neighborCells);
            Log.i(Mview.TAG, "Neighbour cell list " + NeighboringCellsInfo.neighboringCellList);

            if (list!=null)
            {
                list.onSuccess(NeighboringCellsInfo.neighboringCellList1,"lte");
            }


        } catch (Exception e) {
            e.printStackTrace();
            Utils.appendLog(TAG+" Exception occured in getNeighboringCellsInfoForLte "+e.getMessage());
        }
    }


}
