package com.newmview.wifi;

import static android.content.Context.CARRIER_CONFIG_SERVICE;
import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.TELEPHONY_SERVICE;
import static android.content.Context.TELEPHONY_SUBSCRIPTION_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityNr;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoNr;
import android.telephony.CellInfoWcdma;
import android.telephony.CellLocation;
import android.telephony.CellSignalStrength;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthNr;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.aykuttasil.callrecord.service.CallRecordService;
import com.functionapps.mview_sdk2.helper.Neighbour_cells_info;
import com.functionapps.mview_sdk2.helper.Network_Params;
import com.newmview.wifi.activity.Background_service;
import com.newmview.wifi.activity.MainActivity;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.database.DB_handler;
import com.newmview.wifi.helper.AllInOneAsyncTaskForEVT;
import com.newmview.wifi.helper.AllInOneAsyncTaskForNetwork;
import com.newmview.wifi.helper.Call_State_Helper;
import com.newmview.wifi.helper.Interfaces;
import com.newmview.wifi.helper.RequestResponse;
import com.newmview.wifi.helper.SharedPreferencesHelper;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.NeighboringCellsInfo;
import com.newmview.wifi.other.Utils;
import com.services.AllServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MyPhoneStateListener extends PhoneStateListener {
    private static final String TAG = "MyPhoneStateListener";
    public static String LOG_TAG = "mViewPhoneStateListener";
    public static Context mContext;
    static String ss;
    String userType = null;
    String offhook_time = "NA";
    String receiver_offhook_time = "NA";
    String receiver_ringing_time = "NA";
    String thirdparty_offhook_time = "NA";
    String  call_initiate_time = "NA";


    private final SharedPreferences.Editor editor;
    private final SharedPreferences prefs;
    private SubscriptionManager mSubMgr;
    private final boolean mReadPhoneState;
    ArrayList<MyCall> myCallArray;
    boolean incomingCallStatus;
    boolean outgoingCallStatus;
    MyCall currentCallObject;
    ArrayList<RecordedCellLocation> last5CellLocationArr;
    ArrayList<RecordedServiceState> last5CellServiceStateArr;
    boolean bCallDropDanger = false;
    int currLocationIndex;
    int currServiceStateIndex;
    int maxLocationsToRecord = 5;
    int maxServiceStatesToRecord = 5;
    public static CellLocation lastCellLocation;
    public static ServiceState lastServiceState;
    public static SignalStrength lastSignalStrength;
    int currentSignalStrength;
    CurrentCellServing currentCellServing;
    private TelephonyManager telMgr;
    private long resulttime;
    private LteParams.Paramslist obj;
    private LteParams lteParams;
    public MyPhoneStateListener myPhoneStateListener = null;
    private String type;
    private TelephonyManager telephonyManager;
    private ArrayList<String> finalOp_Nameslte;
    private ArrayList<String> finalOpNames;
    private CellIdentityLte cellIdentityLte;
    private CellInfoLte cellInfoLte;
    // BY SWAPNIL 11/01/2023
    private CellInfoNr cellInfoNr;
    static CellIdentityNr cellIdentityNr;
    private ArrayList<String> finalOp_Nameswcdma;
    private ArrayList<String> finalOp_Namesgsm;
    private ArrayList<String> finalOp_Names;
    private Boolean offHookState = false;

    private int rsrq;


    public MyPhoneStateListener(Context context, TelephonyManager tel, boolean readPhoneStatePermissionDenied) {
        mContext = context;
        telMgr = tel;
        mReadPhoneState = readPhoneStatePermissionDenied;
        myCallArray = new ArrayList<MyCall>();
        currLocationIndex = -1;
        currServiceStateIndex = -1;
        last5CellLocationArr = new ArrayList<RecordedCellLocation>();
        last5CellServiceStateArr = new ArrayList<RecordedServiceState>();
        last5CellServiceStateArr.ensureCapacity(maxServiceStatesToRecord);
        last5CellLocationArr.ensureCapacity(maxLocationsToRecord);
        prefs = mContext.getSharedPreferences(MainActivity.MY_PREFS_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();

    }

    @SuppressLint("MissingPermission")
    public static int getNetworkType() {
       /* if (mContext != null)
        {
*/
        TelephonyManager teleMan = (TelephonyManager) MviewApplication.ctx.getSystemService(TELEPHONY_SERVICE);
      /*  if (teleMan.getNetworkType() != 18) {
           System.out.println("Netwrk Type"+ teleMan.getNetworkType() + "");
            // return tm.getNetworkType();
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            System.out.println("Netwrk Type "+ "voice " + teleMan.getVoiceNetworkType() + "");
        }

        else {
            System.out.println("Netwrk Type "+ "else " + "-1 ");
        }*/

        @SuppressLint("MissingPermission") int networkType = teleMan.getNetworkType();
        System.out.println("network type " + networkType);
        //check with Sir once
        if (networkType == 18) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return checkNetworkTypeAccToVoice(teleMan.getVoiceNetworkType());
            } else {
                mView_HealthStatus.onlyCurrentNetworkState = "NS";
                mView_HealthStatus.iCurrentNetworkState = 0;
                return mView_HealthStatus.iCurrentNetworkState;
            }
        } else {
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    mView_HealthStatus.onlyCurrentNetworkState = "2G";
                    mView_HealthStatus.iCurrentNetworkState = 2;
                    break;
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    mView_HealthStatus.onlyCurrentNetworkState = "2G";
                    mView_HealthStatus.iCurrentNetworkState = 2;
                    break;
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    mView_HealthStatus.onlyCurrentNetworkState = "2G";
                    mView_HealthStatus.iCurrentNetworkState = 2;
                    break;
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    mView_HealthStatus.onlyCurrentNetworkState = "2G";
                    mView_HealthStatus.iCurrentNetworkState = 2;
                    break;
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    mView_HealthStatus.onlyCurrentNetworkState = "2G";
                    mView_HealthStatus.iCurrentNetworkState = 2;
                    break;

                case TelephonyManager.NETWORK_TYPE_UMTS:
                    mView_HealthStatus.onlyCurrentNetworkState = "3G";
                    mView_HealthStatus.iCurrentNetworkState = 3;
                    break;
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    mView_HealthStatus.onlyCurrentNetworkState = "3G";
                    mView_HealthStatus.iCurrentNetworkState = 3;
                    break;
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    mView_HealthStatus.onlyCurrentNetworkState = "3G";
                    mView_HealthStatus.iCurrentNetworkState = 3;
                    break;
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    mView_HealthStatus.onlyCurrentNetworkState = "3G";
                    mView_HealthStatus.iCurrentNetworkState = 3;
                    break;
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    mView_HealthStatus.onlyCurrentNetworkState = "3G";
                    mView_HealthStatus.iCurrentNetworkState = 3;
                    break;
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    mView_HealthStatus.onlyCurrentNetworkState = "3G";
                    mView_HealthStatus.iCurrentNetworkState = 3;
                    break;
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    mView_HealthStatus.onlyCurrentNetworkState = "3G";
                    mView_HealthStatus.iCurrentNetworkState = 3;
                    break;
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                    mView_HealthStatus.onlyCurrentNetworkState = "3G";
                    mView_HealthStatus.iCurrentNetworkState = 3;
                    break;
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    mView_HealthStatus.onlyCurrentNetworkState = "3G";
                    mView_HealthStatus.iCurrentNetworkState = 3;
                    break;
                case TelephonyManager.NETWORK_TYPE_LTE:

                    mView_HealthStatus.onlyCurrentNetworkState = "4G";
                    mView_HealthStatus.iCurrentNetworkState = 4;
                    break;
                case TelephonyManager.NETWORK_TYPE_GSM:
                    mView_HealthStatus.onlyCurrentNetworkState = "2G";
                    mView_HealthStatus.iCurrentNetworkState = 2;
                    break;
                // by swapnil 18
                case TelephonyManager.NETWORK_TYPE_NR:
                    mView_HealthStatus.onlyCurrentNetworkState = "5G";
                    mView_HealthStatus.iCurrentNetworkState = 5;
                    break;
                default:

                    mView_HealthStatus.onlyCurrentNetworkState = "NS";
                    mView_HealthStatus.iCurrentNetworkState = 0;
                    break;
            }
            return mView_HealthStatus.iCurrentNetworkState;
        }
        //return mView_HealthStatus.iCurrentNetworkState;
    /*}
        return mView_HealthStatus.iCurrentNetworkState;*/

    }

    private static String checkStringNetworkTypeAccToVoice(int voiceNetworkType) {

        String proto = "";
        String proto1 = "";
        Log.i(TAG, "Network type acc to voice " + voiceNetworkType);
        Utils.appendLog("ELOG_NETWORK_TYPE: Network type acc to voice"+voiceNetworkType);

        switch (voiceNetworkType) {

            case TelephonyManager.NETWORK_TYPE_GPRS:
                proto = "2G (GPRS)";
                proto1 = "GPRS";
                mView_HealthStatus.iCurrentNetworkState = 2;
                break;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                proto = "2G (EDGE)";
                proto1 = "EDGE";
                mView_HealthStatus.iCurrentNetworkState = 2;
                break;
            case TelephonyManager.NETWORK_TYPE_CDMA:
                proto = "2G (CDMA)";
                proto1 = "CDMA";
                mView_HealthStatus.iCurrentNetworkState = 2;
                break;
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                proto = "2G (1xRTT)";
                proto1 = "1xRTT";
                mView_HealthStatus.iCurrentNetworkState = 2;
                break;
            case TelephonyManager.NETWORK_TYPE_IDEN:
                proto = "2G (IDEN)";
                proto1 = "IDEN";
                mView_HealthStatus.iCurrentNetworkState = 2;
                break;

            case TelephonyManager.NETWORK_TYPE_UMTS:
                proto = "3G (UMTS)";
                proto1 = "UMTS";
                mView_HealthStatus.iCurrentNetworkState = 3;
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                proto = "3G (EVDO_0)";
                proto1 = "EVDO_0";
                mView_HealthStatus.iCurrentNetworkState = 3;
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                proto = "3G (EVDO_A)";
                proto1 = "EVDO_A";
                mView_HealthStatus.iCurrentNetworkState = 3;
                break;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                proto = "3G (HSDPA)";
                proto1 = "HSDPA";
                mView_HealthStatus.iCurrentNetworkState = 3;
                break;
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                proto = "3G (HSUPA)";
                proto1 = "HSUPA";
                mView_HealthStatus.iCurrentNetworkState = 3;
                break;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                proto = "3G (HSPA)";
                proto1 = "HSPA";
                mView_HealthStatus.iCurrentNetworkState = 3;
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                proto = "3G (EVDO_B)";
                proto1 = "EVDO_B";
                mView_HealthStatus.iCurrentNetworkState = 3;
                break;
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                proto = "3G (EHRPD)";
                proto1 = "EHRPD";
                mView_HealthStatus.iCurrentNetworkState = 3;
                break;
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                proto = "3G (HSPAP)";
                proto1 = "HSPAP";
                mView_HealthStatus.iCurrentNetworkState = 3;
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                proto = "4G(LTE)";
                proto1 = "LTE";
                mView_HealthStatus.iCurrentNetworkState = 4;
                break;
            case TelephonyManager.NETWORK_TYPE_GSM:
                proto = "2G (GSM)";
                proto1 = "GSM";
                mView_HealthStatus.iCurrentNetworkState = 2;

                break;
            default:
                proto = "NS";
                proto1 = "NS";
                mView_HealthStatus.iCurrentNetworkState = 0;
        }
        Utils.appendLog("ELOG_NETWORK_TYPE: Returning value of current state network " + mView_HealthStatus.iCurrentNetworkState + " network is " + mView_HealthStatus.onlyCurrentNetworkState);
        mView_HealthStatus.strCurrentNetworkState = proto;
        mView_HealthStatus.strCurrentNetworkProtocol = proto1;
        return proto;
    }

    private static int checkNetworkTypeAccToVoice(int voiceNetworkType) {
        Log.i(TAG, "Network type acc to voice " + voiceNetworkType);
        switch (voiceNetworkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:

                mView_HealthStatus.onlyCurrentNetworkState = "2G";
                mView_HealthStatus.iCurrentNetworkState = 2;
                break;
            case TelephonyManager.NETWORK_TYPE_EDGE:

                mView_HealthStatus.onlyCurrentNetworkState = "2G";
                mView_HealthStatus.iCurrentNetworkState = 2;
                break;
            case TelephonyManager.NETWORK_TYPE_CDMA:

                mView_HealthStatus.onlyCurrentNetworkState = "2G";
                mView_HealthStatus.iCurrentNetworkState = 2;
                break;
            case TelephonyManager.NETWORK_TYPE_1xRTT:

                mView_HealthStatus.onlyCurrentNetworkState = "2G";
                mView_HealthStatus.iCurrentNetworkState = 2;
                break;
            case TelephonyManager.NETWORK_TYPE_IDEN:

                mView_HealthStatus.onlyCurrentNetworkState = "2G";
                mView_HealthStatus.iCurrentNetworkState = 2;
                break;

            case TelephonyManager.NETWORK_TYPE_UMTS:
                mView_HealthStatus.onlyCurrentNetworkState = "3G";
                mView_HealthStatus.iCurrentNetworkState = 3;
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                mView_HealthStatus.onlyCurrentNetworkState = "3G";
                mView_HealthStatus.iCurrentNetworkState = 3;
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                mView_HealthStatus.onlyCurrentNetworkState = "3G";
                mView_HealthStatus.iCurrentNetworkState = 3;
                break;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                mView_HealthStatus.onlyCurrentNetworkState = "3G";
                mView_HealthStatus.iCurrentNetworkState = 3;
                break;
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                mView_HealthStatus.onlyCurrentNetworkState = "3G";
                mView_HealthStatus.iCurrentNetworkState = 3;
                break;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                mView_HealthStatus.onlyCurrentNetworkState = "3G";
                mView_HealthStatus.iCurrentNetworkState = 3;
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                mView_HealthStatus.onlyCurrentNetworkState = "3G";
                mView_HealthStatus.iCurrentNetworkState = 3;
                break;
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                mView_HealthStatus.onlyCurrentNetworkState = "3G";
                mView_HealthStatus.iCurrentNetworkState = 3;
                break;
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                mView_HealthStatus.onlyCurrentNetworkState = "3G";
                mView_HealthStatus.iCurrentNetworkState = 3;
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:

                mView_HealthStatus.onlyCurrentNetworkState = "4G";
                mView_HealthStatus.iCurrentNetworkState = 4;
                break;

            case TelephonyManager.NETWORK_TYPE_NR:

                mView_HealthStatus.onlyCurrentNetworkState = "5G";
                mView_HealthStatus.iCurrentNetworkState = 5;
                break;

            default:

                mView_HealthStatus.onlyCurrentNetworkState = "NS";
                mView_HealthStatus.iCurrentNetworkState = 0;
                break;
        }
        Log.i(TAG, "Returning value of current state network " + mView_HealthStatus.iCurrentNetworkState + " network is " + mView_HealthStatus.onlyCurrentNetworkState);
        //  mView_HealthStatus.onlyCurrentNetworkState=  mView_HealthStatus.iCurrentNetworkState;
        mView_HealthStatus.strCurrentNetworkProtocol = mView_HealthStatus.strCurrentNetworkState;
        return mView_HealthStatus.iCurrentNetworkState;

    }

    public static String getSignalStrengthForSec() {
        String sec_strength = null;
        if (mView_HealthStatus.second_cellInstance != null) {

            if (mView_HealthStatus.second_cellInstance.equalsIgnoreCase("lte")) {
                {
                    if (Integer.parseInt(mView_HealthStatus.lteRSRP) < 0 && Integer.parseInt(mView_HealthStatus.lteRSRP) > -75) {
                        sec_strength = mView_HealthStatus.second_Rsrp + "dbm ( Good )";
                    } else if (Integer.parseInt(mView_HealthStatus.lteRSRP) <= -75 && Integer.parseInt(mView_HealthStatus.lteRSRP) > -95) {
                        sec_strength = mView_HealthStatus.second_Rsrp + "dbm ( Fine )";
                    } else if (Integer.parseInt(mView_HealthStatus.lteRSRP) <= -95 && Integer.parseInt(mView_HealthStatus.lteRSRP) > -115) {
                        sec_strength = mView_HealthStatus.second_Rsrp + "dbm ( Poor )";
                    }
                }


            } else if (mView_HealthStatus.second_cellInstance.equalsIgnoreCase("wcdma")) {
                if (mView_HealthStatus.second_rscp_3G < 0 && mView_HealthStatus.second_rscp_3G > -75) {
                    sec_strength = mView_HealthStatus.second_rscp_3G + "  dbm (Good)";
                } else if (mView_HealthStatus.second_rscp_3G <= -75 && mView_HealthStatus.second_rscp_3G > -95) {
                    sec_strength = mView_HealthStatus.second_rscp_3G + "  dbm (Fine)";
                } else if (mView_HealthStatus.second_rscp_3G <= -95 && mView_HealthStatus.second_rscp_3G > -115) {
                    sec_strength = mView_HealthStatus.second_rscp_3G + " dbm (Poor) ";
                }


            } else if (mView_HealthStatus.second_cellInstance.equalsIgnoreCase("gsm")) {
                if (mView_HealthStatus.second_rxLev < 0 && mView_HealthStatus.second_rxLev > -75) {
                    sec_strength = mView_HealthStatus.second_rxLev + " dbm (Good)";
                } else if (mView_HealthStatus.second_rxLev <= -75 && mView_HealthStatus.second_rxLev > -95) {
                    sec_strength = mView_HealthStatus.second_rxLev + "  dbm (Fine)";
                } else if (mView_HealthStatus.second_rxLev <= -95 && mView_HealthStatus.second_rxLev > -115) {
                    sec_strength = mView_HealthStatus.second_rxLev + "dbm (Poor)";
                }
            }
        }
        return sec_strength;

    }

    public static String getSignalStrengthForPrim() {
        String strength = null;
        if (mView_HealthStatus.currentInstance.equalsIgnoreCase("lte")) {
            {
                if (mView_HealthStatus.lteRSRP != null) {
                    if (Integer.parseInt(mView_HealthStatus.lteRSRP) < 0 && Integer.parseInt(mView_HealthStatus.lteRSRP) > -75) {
                        strength = mView_HealthStatus.lteRSRP + "dbm ( Good )";
                    } else if (Integer.parseInt(mView_HealthStatus.lteRSRP) <= -75 && Integer.parseInt(mView_HealthStatus.lteRSRP) > -95) {
                        strength = mView_HealthStatus.lteRSRP + "dbm ( Fine )";
                    } else if (Integer.parseInt(mView_HealthStatus.lteRSRP) <= -95 && Integer.parseInt(mView_HealthStatus.lteRSRP) > -115) {
                        strength = mView_HealthStatus.lteRSRP + "dbm ( Poor )";
                    }
                }
            }
        } else if (mView_HealthStatus.currentInstance.equalsIgnoreCase("wcdma")) {
            if (mView_HealthStatus.rscp != null) {
                if (Integer.parseInt(mView_HealthStatus.rscp) < 0 && Integer.parseInt(mView_HealthStatus.rscp) > -75) {
                    strength = mView_HealthStatus.rscp + "  dbm (Good)";
                } else if (Integer.parseInt(mView_HealthStatus.rscp) <= -75 && Integer.parseInt(mView_HealthStatus.rscp) > -95) {
                    strength = mView_HealthStatus.rscp + "  dbm (Fine)";
                } else if (Integer.parseInt(mView_HealthStatus.rscp) <= -95 && Integer.parseInt(mView_HealthStatus.rscp) > -115) {
                    strength = mView_HealthStatus.rscp + " dbm (Poor) ";
                }
            }
        } else if (mView_HealthStatus.currentInstance.equalsIgnoreCase("gsm")) {
            if (mView_HealthStatus.gsmSignalStrength < 0 && mView_HealthStatus.gsmSignalStrength > -75) {
                strength = mView_HealthStatus.gsmSignalStrength + " dbm (Good)";
            } else if (mView_HealthStatus.gsmSignalStrength <= -75 && mView_HealthStatus.gsmSignalStrength > -95) {
                strength = mView_HealthStatus.gsmSignalStrength + "  dbm (Fine)";
            } else if (mView_HealthStatus.gsmSignalStrength <= -95 && mView_HealthStatus.gsmSignalStrength > -115) {
                strength = mView_HealthStatus.gsmSignalStrength + "dbm (Poor)";
            }

        }


        return strength;
    }

    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCellInfoChanged(List<CellInfo> cellInfo) {


        super.onCellInfoChanged(cellInfo);
        //  Utils.showToast(mContext,"cell info changed called");


        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        long curr = System.currentTimeMillis();

        String displaydate = sdf.format(curr);
        //System.out.println("current time in celllinfo"+displaydate);
        try {
            if (telMgr != null) {
                if (mContext != null) {


                      /*  cellInfo = listenService.telMgr.getAllCellInfo();
                        if ((cellInfo != null) && cellInfo.size() > 0) {
                            fetchCellsInfo(cellInfo);
                        }
*/

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        //    Log.i(LOG_TAG, "onCellInfoChanged: " + cellInfo);
       /* if (cellInfo != null) {
            for (CellInfo m : cellInfo) {
                if (m instanceof CellInfoLte) {
                    CellInfoLte cellInfoLte = (CellInfoLte) m;
                    cellInfoLte.getCellIdentity().getPci();
                    //	Log.d("onCellInfoChanged", "CellInfoLte--" + m);
                }
            }
        }*/
    }

    public void writeLogToFile() {
        call_checks();
        String log = "";
        boolean b = mView_HealthStatus.writeCallLogs; //isExternalStorageWritable();
        if (b) {
            System.out.println("in call if condition");
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
            File file = new File(path, "mview");
            if (!file.exists()) {
                file.mkdirs();
            }
            //  file = new File(path, "mview" + "/" + "calllog.txt");
            Utils.deletefileFromFileManager(path + "/mview" + "/" + "calllog.txt");
            FileWriter out;
//            try {
//                out = new FileWriter(file, true);
//                ///////////
//                if (currentCallObject.calltype == 1)
//                    out.append("Incoming Phone Number " + currentCallObject.callerPhoneNumber + "\n");
//                else
//                    out.append("Outgoing Phone Number " + currentCallObject.callerPhoneNumber + "\n");
//
//                out.append("Call Start " + currentCallObject.timeofCall.toString() + "\n");
//                out.append("Call End " + currentCallObject.endTime.toString() + "\n");
//                String callTaken = "No";
//                if (currentCallObject.isCallTaken)
//                    callTaken = "Yes";
//                out.append("Call Answered = " + callTaken + "\n");
//                out.append(currentCallObject.myLat + " ");
//                out.append(currentCallObject.myLon + "\n");
//                out.append("Call dropped = " + currentCallObject.isDroppedCall + "\n");
//
//                if (currentCallObject.cellLocationArr.size() == 0) {
//                    if (last5CellLocationArr.size() == 0) {
//                        if (lastCellLocation != null) {
//                            out.append("##Last Cell Location\n");
//                            if (lastCellLocation instanceof GsmCellLocation) {
//                                GsmCellLocation gcLoc = (GsmCellLocation) lastCellLocation;
//                                out.append(gcLoc.toString() + "\n");
//                            }
//                        }
//                    } else {
//                        out.append("##Total Cell Locations Captured = " + last5CellLocationArr.size() + "\n");
//                        for (int i = 0; i < last5CellLocationArr.size(); i++) {
//                            if (last5CellLocationArr.get(i).loc instanceof GsmCellLocation) {
//                                GsmCellLocation gcLoc = (GsmCellLocation) last5CellLocationArr.get(i).loc;
//                                out.append(gcLoc.toString() + "\n" + last5CellLocationArr.get(i).myLat + " " + last5CellLocationArr.get(i).myLon + " " + last5CellLocationArr.get(i).dt + "\n");
//                            }
//                        }
//                    }
//                }
//                for (int i = 0; i < currentCallObject.cellLocationArr.size(); i++) {
//                    if (i == 0)
//                        out.append("##Cell Location Info\n");
//
//                    if (currentCallObject.cellLocationArr.get(i) instanceof GsmCellLocation) {
//                        GsmCellLocation gcLoc = (GsmCellLocation) currentCallObject.cellLocationArr.get(i);
//                        out.append(gcLoc.toString() + "\n");
//                        /*Log.i(LOG_TAG,
//                                "onCellLocationChanged: GsmCellLocation "
//										+ gcLoc.toString());
//						Log.i(LOG_TAG, "onCellLocationChanged: GsmCellLocation getCid "
//								+ gcLoc.getCid());
//						Log.i(LOG_TAG, "onCellLocationChanged: GsmCellLocation getLac "
//								+ gcLoc.getLac());
//						Log.i(LOG_TAG, "onCellLocationChanged: GsmCellLocation getPsc"
//								+ gcLoc.getPsc()); // Requires min API 9*/
//                    }
//
//                }
//                if (last5CellServiceStateArr.size() > 0) {
//                    out.append("@@Total Service State captured = " + last5CellServiceStateArr.size() + "\n");
//                    for (int i = 0; i < last5CellServiceStateArr.size(); i++) {
//                        String state = "";
//                        switch (last5CellServiceStateArr.get(i).service.getState()) {
//                            case ServiceState.STATE_IN_SERVICE:
//                                state = "STATE_IN_SERVICE";
//                                break;
//                            case ServiceState.STATE_OUT_OF_SERVICE:
//                                state = "STATE_OUT_OF_SERVICE";
//                                break;
//                            case ServiceState.STATE_EMERGENCY_ONLY:
//                                state = "STATE_EMERGENCY_ONLY";
//
//                                break;
//                            case ServiceState.STATE_POWER_OFF:
//                                state = "STATE_POWER_OFF";
//
//                                break;
//                        }
//                        out.append("-> " + state + " " + last5CellServiceStateArr.get(i).service.toString() + "\n" + last5CellServiceStateArr.get(i).myLat + " " + last5CellServiceStateArr.get(i).myLon + " " + last5CellServiceStateArr.get(i).dt + "\n");
//                    }
//                }
//
//                for (int i = 0; i < currentCallObject.serviceStateArr.size(); i++) {
//                    if (i == 0)
//                        out.append("@@Service State Info\n");
//
//                    String state = "";
//                    switch (currentCallObject.serviceStateArr.get(i).getState()) {
//                        case ServiceState.STATE_IN_SERVICE:
//                            state = "STATE_IN_SERVICE";
//                            break;
//                        case ServiceState.STATE_OUT_OF_SERVICE:
//                            state = "STATE_OUT_OF_SERVICE";
//
//                            break;
//                        case ServiceState.STATE_EMERGENCY_ONLY:
//                            state = "STATE_EMERGENCY_ONLY";
//                            break;
//                        case ServiceState.STATE_POWER_OFF:
//                            state = "STATE_POWER_OFF";
//                            break;
//                    }
//                    out.append("-> " + state + " " + currentCallObject.serviceStateArr.get(i).toString() + "\n");
//                }
//                out.append("##Total Signal Strength Info captured = " + currentCallObject.signalStrengthArr.size() + "\n");
//                for (int ii = 0; ii < currentCallObject.signalStrengthArr.size(); ii++) {
//                    int getGsmBitErrorRate = 0;
//                    int getGsmSignalStrength = 0;
//                    if (currentCallObject.signalStrengthArr.get(ii).isGsm()) {
//                        getGsmBitErrorRate = currentCallObject.signalStrengthArr.get(ii).getGsmBitErrorRate();
//                        getGsmSignalStrength = currentCallObject.signalStrengthArr.get(ii).getGsmSignalStrength();
//                    }
//
//                    out.append(getGsmSignalStrength + " " + currentCallObject.signalStrengthArr.get(ii).toString() + "\n");
//                }
//
//                //JSONArray jarr = WebService.getCallRecordJSON(currentCallObject);
//                //String s = jarr.toString();
//                //out.append("______________________________________________"+"\n");
//                //out.append(s);
//                out.append("______________________________________________" + "\n");
//                //////////////
//                //out.append(log);
//                out.flush();
//                out.close();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }

        }

        try {
            //System.out.println("about to call web api ");
            //Toast.makeText(mContext, "about to call web api", Toast.LENGTH_SHORT).show();
            JSONObject callJson = new JSONObject();
            System.out.println("calling curr call obj value " + currentCallObject.myLon + " " + currentCallObject.myLat);
            String temp = "M";
            long dura = 0;
            if (currentCallObject.isCallTaken) {
                temp = "S";
            }
            if (currentCallObject.isDroppedCall)
                temp = "F";

            dura = currentCallObject.endTimeInMS - currentCallObject.timeofcallInMS;
            callJson.put("CALLType", temp);
            mView_HealthStatus.CALLType = temp;
            callJson.put("InorOut", currentCallObject.calltype);//1 means incoming, 2 means outgoing
            if (currentCallObject.operator == null && listenService.telMgr != null) {
                currentCallObject.operator = listenService.telMgr.getNetworkOperatorName();
            }
            mView_HealthStatus.inComingOutgoing = String.valueOf(currentCallObject.calltype);
            callJson.put("operatorname", currentCallObject.operator);
            if (currentCallObject.callerPhoneNumber != null)
                callJson.put("Incoming", currentCallObject.callerPhoneNumber);//for outgoing as well
            else
                callJson.put("Incoming", 0);
            mView_HealthStatus.CALLERPHONENUMBER = currentCallObject.callerPhoneNumber;
            callJson.put("speed", currentCallObject.speed);
            mView_HealthStatus.SPEED = String.valueOf(currentCallObject.speed);
            callJson.put("datetime", currentCallObject.timeofcallInMSNew);
            callJson.put("duration", dura);
            mView_HealthStatus.CALLERDURATION = String.valueOf(dura);
            mView_HealthStatus.timeofcallinms = currentCallObject.timeofcallInMSNew;
            callJson.put("disconnectcause", currentCallObject.disconnectCause);
            if (currentCallObject.isRoaming)
                callJson.put("roaming", "1");
            else
                callJson.put("roaming", "0");
            mView_HealthStatus.ROAMING = String.valueOf(currentCallObject.isRoaming);

            JSONArray detailsArray = new JSONArray();
            detailsArray.put(callJson);


            RequestResponse.sendEvent(detailsArray, AllInOneAsyncTaskForNetwork.AsyncTaskPurpose.CALL_EVT, "call_evt");
            //  String a = WebService.API_sendCallRecord(currentCallObject);
            //    new WebService.Async_SendNeighboringCellsInfo().execute();

            editor.putInt("totalcalls", mView_HealthStatus.iTotal_Calls);
            //Toast.makeText(mContext, "adding SUCCESS calls as in shared preferences as " +mView_HealthStatus.iCall_Success, Toast.LENGTH_SHORT).show();
            editor.putInt("missedcalls", mView_HealthStatus.iCall_Missed);
            editor.putInt("successcalls", mView_HealthStatus.iCall_Success);
            editor.putInt("failedcalls", mView_HealthStatus.iCall_Failed);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void call_checks() {
        //System.out.println("called call_checks");
        for (int i = 0; i < last5CellServiceStateArr.size(); i++) {
            String state = "";
            switch (last5CellServiceStateArr.get(i).service.getState()) {
                case ServiceState.STATE_IN_SERVICE:
                    state = "STATE_IN_SERVICE";
                    break;
                case ServiceState.STATE_OUT_OF_SERVICE:
                    state = "STATE_OUT_OF_SERVICE";
                    if (i == currServiceStateIndex) {
                        if (currentCallObject.isCallTaken) {
                            mView_HealthStatus.iCall_Success--;
                            editor.putInt("successcalls", mView_HealthStatus.iCall_Success).apply();
                            //			Toast.makeText(mContext, "decreasing call success", Toast.LENGTH_SHORT).show();
                        } else {
                            mView_HealthStatus.iCall_Missed--;
                            editor.putInt("missedcalls", mView_HealthStatus.iCall_Missed).apply();
                        }
                        //Toast.makeText(mContext, "missed calls in outofservice "+mView_HealthStatus.iCall_Missed, //Toast.LENGTH_SHORT).show();

                        mView_HealthStatus.iCall_Failed++;
                        editor.putInt("failedcalls", mView_HealthStatus.iCall_Failed).apply();
                        currentCallObject.isDroppedCall = true;
                        currentCallObject.disconnectCause = "STATE_OUT_OF_SERVICE";
                    }

                    break;
                case ServiceState.STATE_EMERGENCY_ONLY:
                    state = "STATE_EMERGENCY_ONLY";
                    if (i == currServiceStateIndex) {
                        mView_HealthStatus.iCall_Failed++;
                        editor.putInt("failedcalls", mView_HealthStatus.iCall_Failed).apply();
                        if (currentCallObject.isCallTaken) {
                            mView_HealthStatus.iCall_Success--;
                            editor.putInt("successcalls", mView_HealthStatus.iCall_Success).apply();
                            //	Toast.makeText(mContext, "decreasing call success", Toast.LENGTH_SHORT).show();
                        } else {
                            mView_HealthStatus.iCall_Missed--;
                            editor.putInt("missedcalls", mView_HealthStatus.iCall_Missed).apply();
                        }
                        //Toast.makeText(mContext, "missed calls in emergency "+mView_HealthStatus.iCall_Missed, //Toast.LENGTH_SHORT).show();
                        currentCallObject.isDroppedCall = true;
                        currentCallObject.disconnectCause = "STATE_EMERGENCY_ONLY";
                    }
                    break;
                case ServiceState.STATE_POWER_OFF:
                    state = "STATE_POWER_OFF";
                    if (i == currServiceStateIndex) {
                        mView_HealthStatus.iCall_Failed++;
                        editor.putInt("failedcalls", mView_HealthStatus.iCall_Failed).apply();

                        if (currentCallObject.isCallTaken) {
                            mView_HealthStatus.iCall_Success--;
                            editor.putInt("successcalls", mView_HealthStatus.iCall_Success).apply();
                            //	Toast.makeText(mContext, "decreasing call success", Toast.LENGTH_SHORT).show();
                        } else {
                            mView_HealthStatus.iCall_Missed--;
                            editor.putInt("missedcalls", mView_HealthStatus.iCall_Missed).apply();
                        }
                        //Toast.makeText(mContext, "missed calls in poweroff "+mView_HealthStatus.iCall_Missed, //Toast.LENGTH_SHORT).show();
                        currentCallObject.isDroppedCall = true;
                        currentCallObject.disconnectCause = "STATE_POWER_OFF";
                    }
                    break;
            }
            //out.append("-> " + state + " " + last5CellServiceStateArr.get(i).service.toString()+ "\n" + last5CellServiceStateArr.get(i).myLat + " " + last5CellServiceStateArr.get(i).myLon + " " + last5CellServiceStateArr.get(i).dt + "\n");
        }//end for loop
    }//end function

    @Override
    public void onDataConnectionStateChanged(int state, int networkType) {
        super.onDataConnectionStateChanged(state, networkType);
        int oldstate = -1;
        if (mView_HealthStatus.iCurrentNetworkState != -1) {
            if (mView_HealthStatus.starTimeInCurrentState == 0) {
                mView_HealthStatus.starTimeInCurrentState = System.currentTimeMillis();
            }
            if (mView_HealthStatus.iCurrentNetworkState == 5) {
                mView_HealthStatus.timein5G += (System.currentTimeMillis() - mView_HealthStatus.starTimeInCurrentState);
            } else if (mView_HealthStatus.iCurrentNetworkState == 4) {
                mView_HealthStatus.timein4G += (System.currentTimeMillis() - mView_HealthStatus.starTimeInCurrentState);
            } else if (mView_HealthStatus.iCurrentNetworkState == 3) {

                mView_HealthStatus.timein3G += (System.currentTimeMillis() - mView_HealthStatus.starTimeInCurrentState);
            } else if (mView_HealthStatus.iCurrentNetworkState == 2) {
                mView_HealthStatus.timein2G += (System.currentTimeMillis() - mView_HealthStatus.starTimeInCurrentState);
            } else if (mView_HealthStatus.iCurrentNetworkState == 0) {
                mView_HealthStatus.timeinNS += (System.currentTimeMillis() - mView_HealthStatus.starTimeInCurrentState);
            }
            oldstate = mView_HealthStatus.iCurrentNetworkState;
        }
        mView_HealthStatus.starTimeInCurrentState = System.currentTimeMillis();
        String proto = "";
        mView_HealthStatus.iCurrentNetworkState = 2;
        mView_HealthStatus.onlyCurrentNetworkState = "3G";
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
                proto = "2G (GPRS)";
                mView_HealthStatus.onlyCurrentNetworkState = "2G";
                break;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                proto = "2G (EDGE)";
                mView_HealthStatus.onlyCurrentNetworkState = "2G";
                break;
            case TelephonyManager.NETWORK_TYPE_CDMA:
                proto = "2G (CDMA)";
                mView_HealthStatus.onlyCurrentNetworkState = "2G";
                break;
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                proto = "2G (1xRTT)";
                mView_HealthStatus.onlyCurrentNetworkState = "2G";
                break;
            case TelephonyManager.NETWORK_TYPE_IDEN:
                proto = "2G (IDEN)";
                mView_HealthStatus.onlyCurrentNetworkState = "2G";
                break;

            case TelephonyManager.NETWORK_TYPE_UMTS:
                proto = "3G (UMTS)";
                mView_HealthStatus.iCurrentNetworkState = 3;
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                proto = "3G (EVDO_0)";
                mView_HealthStatus.iCurrentNetworkState = 3;
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                proto = "3G (EVDO_A)";
                mView_HealthStatus.iCurrentNetworkState = 3;
                break;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                proto = "3G (HSDPA)";
                mView_HealthStatus.iCurrentNetworkState = 3;
                break;
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                proto = "3G (HSUPA)";
                mView_HealthStatus.iCurrentNetworkState = 3;
                break;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                proto = "3G (HSPA)";
                mView_HealthStatus.iCurrentNetworkState = 3;
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                proto = "3G (EVDO_B)";
                mView_HealthStatus.iCurrentNetworkState = 3;
                break;
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                proto = "3G (EHRPD)";
                mView_HealthStatus.iCurrentNetworkState = 3;
                break;
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                proto = "3G (HSPAP)";
                mView_HealthStatus.iCurrentNetworkState = 3;
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                proto = "4G";
                mView_HealthStatus.onlyCurrentNetworkState = "4G";
                mView_HealthStatus.iCurrentNetworkState = 4;

            case TelephonyManager.NETWORK_TYPE_NR:
                proto = "5G";
                mView_HealthStatus.onlyCurrentNetworkState = "5G";
                mView_HealthStatus.iCurrentNetworkState = 5;
                break;


            default:
                proto = "NS";
                mView_HealthStatus.onlyCurrentNetworkState = "NS";
                mView_HealthStatus.iCurrentNetworkState = 0;
                break;
        }
        mView_HealthStatus.strCurrentNetworkState = proto;
        if (oldstate == -1) {
            if (mView_HealthStatus.iCurrentNetworkState == 5) {
                mView_HealthStatus.timein5G += (System.currentTimeMillis() - mView_HealthStatus.starTimeInCurrentState) + 100;
            } else if (mView_HealthStatus.iCurrentNetworkState == 4) {
                mView_HealthStatus.timein4G += (System.currentTimeMillis() - mView_HealthStatus.starTimeInCurrentState) + 100;
            } else if (mView_HealthStatus.iCurrentNetworkState == 3) {
                mView_HealthStatus.timein3G += (System.currentTimeMillis() - mView_HealthStatus.starTimeInCurrentState) + 100;
            } else if (mView_HealthStatus.iCurrentNetworkState == 2) {
                mView_HealthStatus.timein2G += (System.currentTimeMillis() - mView_HealthStatus.starTimeInCurrentState) + 100;
            } else if (mView_HealthStatus.iCurrentNetworkState == 0) {
                mView_HealthStatus.timein3G = 0;
                mView_HealthStatus.timeinNS += (System.currentTimeMillis() - mView_HealthStatus.starTimeInCurrentState) + 100;
            }
        }
    }

    //  CODE ############################
    public static String getLTERSSI() {
        try {
//            if (this.NetworkType != 13) {
//                return "-";
//            }
            if (Integer.parseInt(mView_HealthStatus.lteRSSI) == 99 || Integer.parseInt(mView_HealthStatus.lteRSSI) == -1) {
                return "0";

            }
            if (Integer.parseInt(mView_HealthStatus.lteRSSI) >= 0) {
                return String.valueOf(Integer.parseInt(mView_HealthStatus.lteRSSI) - 94);
            }
            return String.valueOf((-1 * Integer.parseInt(mView_HealthStatus.lteRSSI)) - 94);
        } catch (Exception e) {
            return "0";
        }
    }

    public static int NetworkType;

    public static String getSignalStrength() {

        if (mView_HealthStatus.iCurrentNetworkState == 4) {
            if (mView_HealthStatus.lteRSRP != null)
                ss = mView_HealthStatus.lteRSRP + "dbm";

        } else if (mView_HealthStatus.iCurrentNetworkState == 3) {
            if (mView_HealthStatus.rscp != null)
                ss = mView_HealthStatus.rscp + "dbm";


        } else if (mView_HealthStatus.iCurrentNetworkState == 2) {
            ss = MyPhoneStateListener.getRxLev() + "dbm";
        }
        return ss;
    }

    public static int getRxLev() {
        //  if (ContextCompat.checkSelfPermission(MviewApplication.ctx, Manifest.permission.READ_PHONE_STATE)
        //  != PackageManager.PERMISSION_GRANTED) {
        // Permission is not granted, request it
        // ActivityCompat.requestPermissions((Activity) MviewApplication.ctx, new String[]{Manifest.permission.READ_PHONE_STATE}, 22);

        // Return a placeholder value indicating that the permission is not yet granted
        // You can choose a different value that makes sense in your context
        //  return -1;
        // }
        if (listenService.telMgr != null) {
            NetworkType = listenService.telMgr.getNetworkType();
            try {
                if (NetworkType == 1 || NetworkType == 2) {
                    if (mView_HealthStatus.RxLev == 99 || mView_HealthStatus.RxLev == 9999) {
                        return -200;
                    }
                    if (mView_HealthStatus.RxLev >= 0) {
                        return (mView_HealthStatus.RxLev * 2) - 113;
                    }
                    return mView_HealthStatus.RxLev;
                } else if (NetworkType == 0) {
//                if (!MainActivity.discardnetworktypenull.booleanValue()) {
//                    return -200;
//                }
                    if (mView_HealthStatus.iCurrentNetworkState == 2) {//this.nettech.equals("2G")) {
                        if (mView_HealthStatus.RxLev == 99 || mView_HealthStatus.RxLev == 9999) {
                            return -200;
                        }
                        if (mView_HealthStatus.RxLev >= 0) {
                            return (mView_HealthStatus.RxLev * 2) - 113;
                        }
                        return mView_HealthStatus.RxLev;
                    } else if (mView_HealthStatus.iCurrentNetworkState == 3) { //this.nettech.equals("3G")) {
                        if (mView_HealthStatus.CDMARSSI > -120 && mView_HealthStatus.CDMARSSI <= -32) {
                            return mView_HealthStatus.CDMARSSI;
                        }
                        if (mView_HealthStatus.RxLev == 99 || mView_HealthStatus.RxLev == 9999) {
                            return -200;
                        }
                        if (mView_HealthStatus.RxLev >= 0) {
                            return (mView_HealthStatus.RxLev * 2) - 113;

                        }
                        return mView_HealthStatus.RxLev;
                    } else if (mView_HealthStatus.iCurrentNetworkState == 4) { //this.nettech.equals("4G")) {
                        if (Integer.parseInt(mView_HealthStatus.lteRSRP) < -2 && Integer.parseInt(mView_HealthStatus.lteRSRP) > -200) {
                            return Integer.parseInt(mView_HealthStatus.lteRSRP);
                        }
                        if (mView_HealthStatus.RxLev == 99 || mView_HealthStatus.RxLev == 9999) {
                            return -200;
                        }
                        return mView_HealthStatus.RxLev - 140;
                    } else if (mView_HealthStatus.tddphone.booleanValue()) {
                        return mView_HealthStatus.CDMARSSI;
                    } else {
                        return -200;
                    }
                } else if (NetworkType == 3 || NetworkType == 8 || NetworkType == 9 || NetworkType == 10 || NetworkType == 15) {
                    if (mView_HealthStatus.CDMARSSI > -120 && mView_HealthStatus.CDMARSSI <= -32) {
                        System.out.println("signal val 2" + mView_HealthStatus.CDMARSSI);
                        return mView_HealthStatus.CDMARSSI;
                    }
                    if (mView_HealthStatus.RxLev == 99 || mView_HealthStatus.RxLev == 9999) {
                        System.out.println("signal val 3" + mView_HealthStatus.CDMARSSI);
                        return -200;
                    }
                    if (mView_HealthStatus.RxLev >= 0) {
                        System.out.println("signal val 4" + mView_HealthStatus.CDMARSSI);
                        return (mView_HealthStatus.RxLev * 2) - 113;
                    }
                    return mView_HealthStatus.RxLev;
                } else if (NetworkType == 4 || NetworkType == 7) {
                    if (mView_HealthStatus.CDMARSSI != -1 && mView_HealthStatus.CDMARSSI != -120) {
                        return mView_HealthStatus.CDMARSSI;
                    }
                    if (mView_HealthStatus.RxLev == 99 || mView_HealthStatus.RxLev == 9999) {
                        return -200;
                    }
                    return (mView_HealthStatus.RxLev * 2) - 113;
                } else if (NetworkType == 5 || NetworkType == 6 || NetworkType == 12 || NetworkType == 14) {
                    if (mView_HealthStatus.EVDORSSI != -1 && mView_HealthStatus.EVDORSSI != -120) {
                        return mView_HealthStatus.EVDORSSI;
                    }
                    if (mView_HealthStatus.RxLev == 99 || mView_HealthStatus.RxLev == 9999) {
                        return -200;
                    }
                    return (mView_HealthStatus.RxLev * 2) - 113;
                } else if (NetworkType != 13) {
                    return -200;
                } else {
                    if (Integer.parseInt(mView_HealthStatus.lteRSRP) < -2 && Integer.parseInt(mView_HealthStatus.lteRSRP) > -200) {
                        return Integer.parseInt(mView_HealthStatus.lteRSRP);
                    }
                    if (mView_HealthStatus.RxLev == 99 || mView_HealthStatus.RxLev == 9999) {
                        return -200;
                    }
                    return mView_HealthStatus.RxLev - 140;
                }
            } catch (Exception e) {
                return -200;
            }
        } else {
            return -200;
        }
    }

    public static String getCQI() {
        if (listenService.telMgr != null) {
            NetworkType = listenService.telMgr.getNetworkType();
            try {
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
                    return "0";
                }
                if (NetworkType != 13) {
                    return "0";
                }
                if (Integer.parseInt(mView_HealthStatus.lteCQI) == -1 || Integer.parseInt(mView_HealthStatus.lteCQI) >= 100
                        || Integer.parseInt(mView_HealthStatus.lteCQI) < 0) {
                    return "0";
                }
                return mView_HealthStatus.lteCQI; //String.valueOf(this.LTECQI);
            } catch (Exception e) {
                return "0";
            }
        }
        return "0";
    }

    public static String getSNR() {
        if (listenService.telMgr != null) {
            System.out.println(" entering snr of phone");
            NetworkType = listenService.telMgr.getNetworkType();
            try {
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
                    if (mView_HealthStatus.EVDOSNR <= -1 || mView_HealthStatus.EVDOSNR >= 50) {
                        return "0";
                    }
                    return String.valueOf(mView_HealthStatus.EVDOSNR);
                } else if (NetworkType != 13) {
                    return "0";
                } else {
                    if (Integer.parseInt(mView_HealthStatus.lteSNR) == -99.0d || Integer.parseInt(mView_HealthStatus.lteSNR) < -50.0d || Integer.parseInt(mView_HealthStatus.lteSNR) >= 80.0d) {
                        return "0";
                    }
                    return mView_HealthStatus.lteSNR; //String.valueOf(mView_HealthStatus.LTESNR);
                }
            } catch (Exception e) {
                return "0";
            }

        }
        return "0";
    }

    @SuppressLint({"MissingPermission"})
//    public static String getSNR1() {
//        if (listenService.telMgr != null) {
//            NetworkType = listenService.telMgr.getNetworkType();
//            try {
//                if (NetworkType != 0 && NetworkType != 1 && NetworkType != 2) {
//                    if (NetworkType != 3 && NetworkType != 8 && NetworkType != 9 && NetworkType != 10 && NetworkType != 15) {
//                        if (NetworkType != 4 && NetworkType != 7) {
//                            if (NetworkType != 5 && NetworkType != 6 && NetworkType != 12 && NetworkType != 14) {
//                                if (NetworkType != 13) {
//                                    return "0";
//                                } else {
//                                    return (double)Integer.parseInt(mView_HealthStatus.lteSNR) != -99.0D && (double)Integer.parseInt(mView_HealthStatus.lteSNR) >= -50.0D && (double)Integer.parseInt(mView_HealthStatus.lteSNR) < 80.0D ? mView_HealthStatus.lteSNR : "0";
//                                }
//                            } else {
//                                return mView_HealthStatus.EVDOSNR > -1 && mView_HealthStatus.EVDOSNR < 50 ? String.valueOf(mView_HealthStatus.EVDOSNR) : "0";
//                            }
//                        } else {
//                            return "0";
//                        }
//                    } else {
//                        return "0";
//                    }
//                } else {
//                    return "0";
//                }
//            } catch (Exception var1) {
//                return "0";
//            }
//        } else {
//            return "0";
//        }
//    }


    public void decodesignalstrength(SignalStrength signalStrength) {
        String SignalString = signalStrength.toString().replace("SignalStrength:", "");

        String[] signal = signalStrength.toString().split(" ");
        System.out.println("4g signal " + Arrays.toString(signal));
        //System.out.println("mview signal  "+ Arrays.toString(signal));
		/*[SignalStrength:, 99, 0, -120, -160, -120, -160, -1, 99, 2147483647, 2147483647, 2147483647,
		2147483647, 0, 2147483647, 8, 23, -95,
				gsm|lte, use_rsrp_and_rssnr_for_lte_level,
		, [-128,, -118,, -108,, -98], [-115,, -105,, -95,, -85], 0, 3, 0, 0, 0, 0]*/

        System.out.println("cdma dbm " + signalStrength.getCdmaDbm() + "evdo dbm " + signalStrength.getEvdoDbm());

        if (mView_HealthStatus.phonetype.equals("CDMA") || mView_HealthStatus.phonetype.equals("")) {
            try {
                mView_HealthStatus.CDMARSSI = Integer.parseInt(signal[3]);
            } catch (Exception e) {
            }
            try {
                mView_HealthStatus.CDMAECIO = Integer.parseInt(signal[4]) / 10;
            } catch (Exception e2) {
            }
            try {
                mView_HealthStatus.EVDORSSI = Integer.parseInt(signal[5]);
            } catch (Exception e3) {
            }
            try {
                mView_HealthStatus.EVDOECIO = Integer.parseInt(signal[6]) / 10;
            } catch (Exception e4) {
            }
            try {
                mView_HealthStatus.EVDOSNR = Integer.parseInt(signal[7]);
                System.out.println(" snr value is " + mView_HealthStatus.EVDOSNR);
            } catch (Exception e5) {
            }
        } else {
            mView_HealthStatus.RxLev = signalStrength.getGsmSignalStrength();
            mView_HealthStatus.BER = signalStrength.getGsmBitErrorRate();
            mView_HealthStatus.CDMARSSI = signalStrength.getCdmaDbm();
            mView_HealthStatus.CDMAECIO = signalStrength.getCdmaEcio();
            mView_HealthStatus.EVDORSSI = signalStrength.getEvdoDbm();
            mView_HealthStatus.EVDOECIO = signalStrength.getEvdoEcio() / 10;
            mView_HealthStatus.evdoecio = signalStrength.getEvdoEcio();
            mView_HealthStatus.EVDOSNR = signalStrength.getEvdoSnr();
            System.out.println(" snr value is " + mView_HealthStatus.EVDOSNR);
            //lteParams.paramslist.Ecno=String.valueOf(mView_HealthStatus.EVDOECIO);

        }
        try {
            if (signalStrength.toString().contains("gw")) {
                mView_HealthStatus.tddphone = Boolean.valueOf(true);
                try {
                    mView_HealthStatus.CDMARSSI = Integer.parseInt(signal[3]);
                } catch (Exception e6) {
                }
                try {
                    mView_HealthStatus.CDMAECIO = Integer.parseInt(signal[4]);
                } catch (Exception e7) {
                }
                try {
                    mView_HealthStatus.lteRSSI = signal[10];
                    System.out.println("signal vals rssi from 1" + signal[10]);
                } catch (Exception e8) {
                }
                try {
					/*mView_HealthStatus.lteRSRP = signal[11];
					if (mView_HealthStatus.iCurrentNetworkState == 4) {
						lteParams.paramslist.Rsrp = signal[11];
					}*/
                    //System.out.println("rsrp.. 11 "+mView_HealthStatus.lteRSRP);
                } catch (Exception e9) {
                }
                try {
                    mView_HealthStatus.lteRSRQ = signal[12];
                } catch (Exception e10) {
                }
                try {
                    if (signal[13].equals("INVALID_SNR")) {
                        mView_HealthStatus.lteSNR = -99.0d + "";
                    } else if (Integer.parseInt(signal[13]) < 10000) {
                        mView_HealthStatus.lteSNR = signal[13];
                    } else {
                        mView_HealthStatus.lteSNR = -99.0d + "";
                    }
                    lteParams.paramslist.Snr = mView_HealthStatus.lteSNR;
                    //System.out.println("SNR val "+lteParams.paramslist.Snr);

                } catch (Exception e11) {
                }
                try {
                    mView_HealthStatus.lteCQI = signal[14];
                    //System.out.println("mview signal 14  "+signal[14]);
                    return;
                } catch (Exception e12) {
                    return;
                }
            }

            try {
                System.out.println("signal values " + Arrays.toString(signal));
                mView_HealthStatus.lteRSSI = signal[8];
                System.out.println("signal val rssi " + signal[8]);
            } catch (Exception e13) {
            }
            try {
                //commented by Sonal on 02-08-2021 due to irrelevant values
                //  mView_HealthStatus.lteRSRP = signal[9];

                // lteParams.paramslist.Rsrp = signal[9];
                // System.out.println("signal rsrp " + signal[9]);
                //System.out.println("rsrp.. 9"+mView_HealthStatus.lteRSRP);
            } catch (Exception e14) {
            }
            try {
                mView_HealthStatus.lteRSRQ = signal[10];
            } catch (Exception e15) {
            }
            try {
                if (signal[11].equals("INVALID_SNR")) {
                    mView_HealthStatus.lteSNR = -99.0d + "";
                    System.out.println(" snr is is " + mView_HealthStatus.lteSNR);
                } else if (Integer.parseInt(signal[11]) < 10000) {
                    mView_HealthStatus.lteSNR = (Double.parseDouble(signal[11]) / 10.0d) + "";
                    System.out.println(" snr is is " + mView_HealthStatus.lteSNR);
                } else {
                    mView_HealthStatus.lteSNR = -99.0d + "";
                    System.out.println(" snr is is " + mView_HealthStatus.lteSNR);
                }
                if (mView_HealthStatus.iCurrentNetworkState == 4) {
                    lteParams.paramslist.Snr = mView_HealthStatus.lteSNR;
                    System.out.println("snr on getting " + mView_HealthStatus.lteSNR);
                }
            } catch (Exception e16) {
            }
            System.out.println("4G rssi " + signal[8] + " rsrp " + signal[9] + " snr " + mView_HealthStatus.lteSNR);
            try {

                mView_HealthStatus.lteCQI = signal[12];
                //System.out.println("mview signal 12  "+signal[12]);
            } catch (Exception e17) {
            }
        } catch (Exception e18) {
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        System.out.println(" entering signal strength");
////Toast.makeText(mContext, "signal strength changed called", //Toast.LENGTH_SHORT).show();
        Utils.isMyServiceRunning(Background_service.class);
//        Utils.isMyServiceRunning(AllServices.class);
        Utils.isMyServiceRunning(CallRecordService.class);
        Utils.isMyServiceRunning(listenService.class);

        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
        long curr = System.currentTimeMillis();

        String displaydate1 = sdf1.format(curr);
        //System.out.println("current time in signal strength"+displaydate1);


        boolean callInProgress = false;
        if (incomingCallStatus && currentCallObject != null) {
            currentCallObject.signalStrengthArr.add(signalStrength);
            callInProgress = true;
        }
        lastSignalStrength = signalStrength;
        int networkType = -1;
        if (!mReadPhoneState) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            networkType = telMgr.getNetworkType();
            mView_HealthStatus.strCurrentNetworkState = MyPhoneStateListener.getNetworkClass(networkType, mReadPhoneState);
        }


        //  Log.i(LOG_TAG, "onSignalStrengthsChanged: " + signalStrength);
        if (signalStrength.isGsm()) {
            currentSignalStrength = signalStrength.getGsmSignalStrength();
            if (callInProgress) {
                if (currentSignalStrength < mView_HealthStatus.MIN_GSM_SIGNAL_STRENGTH_FOR_CALL_DROP) {
                    //sharad
                    bCallDropDanger = true;
                } else {
                    bCallDropDanger = false;
                }
            }
         /*   Log.i(LOG_TAG, "onSignalStrengthsChanged: getGsmBitErrorRate "
                    + signalStrength.getGsmBitErrorRate());
            Log.i(LOG_TAG, "onSignalStrengthsChanged: getGsmSignalStrength "
*/                  /*  + signalStrength.getGsmSignalStrength());*/
        } else if (signalStrength.getCdmaDbm() > 0) {
          /*  Log.i(LOG_TAG, "onSignalStrengthsChanged: getCdmaDbm "
                    + signalStrength.getCdmaDbm());
            Log.i(LOG_TAG, "onSignalStrengthsChanged: getCdmaEcio "
                    + signalStrength.getCdmaEcio());*/
        } else {
         /*   Log.i(LOG_TAG, "onSignalStrengthsChanged: getEvdoDbm "
                    + signalStrength.getEvdoDbm());
            Log.i(LOG_TAG, "onSignalStrengthsChanged: getEvdoEcio "
                    + signalStrength.getEvdoEcio());
            Log.i(LOG_TAG, "onSignalStrengthsChanged: getEvdoSnr "
                    + signalStrength.getEvdoSnr());*/
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        resulttime = System.currentTimeMillis();
        String displaydate = sdf.format(resulttime);
        lteParams = new LteParams();

        lteParams.paramslist = new LteParams().new Paramslist();


        decodesignalstrength(signalStrength);
        lteParams.paramslist.currenttime = displaydate;
        lteParams.paramslist.RxLevel = String.valueOf(getRxLev());
//	//Toast.makeText(mContext, "calling signalstrength changed", //Toast.LENGTH_SHORT).show();
        int currSize = mView_HealthStatus.lteparams.size();

        if (currSize == mView_HealthStatus.MaxPeriodicDataToSaveInDB && currSize >= 1) {
            mView_HealthStatus.lteparams.remove(0);
        }

        mView_HealthStatus.lteparams.add(lteParams.paramslist);
        for (int i = 0; i < mView_HealthStatus.lteparams.size(); i++) {
            ////System.out.println("captured data listener " + mView_HealthStatus.lteparams.get(i));
            //System.out.println("current time listener" + mView_HealthStatus.lteparams.get(i).currenttime + " rsrp" +
            //	mView_HealthStatus.lteparams.get(i).Rsrp);
        }


        mView_HealthStatus.rxqualfor2g = signalStrength.getGsmBitErrorRate() + "";
        lteParams.paramslist.RXQual = String.valueOf(signalStrength.getGsmBitErrorRate());

        int rxl = MyPhoneStateListener.getRxLev();
        if (currentCellServing != null) {

            currentCellServing.level = rxl + "";
        }


        try {
            if (telMgr != null) {

                final List<CellInfo> cellInfo = telMgr.getAllCellInfo();
                System.out.println("cell info 1 " + cellInfo);
                if (cellInfo != null && cellInfo.size() > 0) {
                    fetchCellsInfo(cellInfo);
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (true)
            return;

        // Reflection code starts from here
        try {
            Method[] methods = SignalStrength.class
                    .getMethods();
            for (Method mthd : methods) {
                if (mthd.getName().equals("getLteSignalStrength")) {
                    //mView_HealthStatus.lteRSSI = mthd.invoke(signalStrength) + "";
                } else if (mthd.getName().equals("getLteRsrp")) {
                    //mView_HealthStatus.lteRSRP = mthd.invoke(signalStrength) + "";
                } else if (mthd.getName().equals("getLteRsrq")) {
                    //mView_HealthStatus.lteRSRQ = mthd.invoke(signalStrength) + "";
                } else if (mthd.getName().equals("getLteRssnr")) {
                    // mView_HealthStatus.lteSNR = mthd.invoke(signalStrength) + "";
                } else if (mthd.getName().equals("getLteCqi")) {
                    mView_HealthStatus.lteCQI = mthd.invoke(signalStrength) + "";
                } else if (mthd.getName().equals("getLteCqi")) {
                    mView_HealthStatus.lteSINR = mthd.invoke(signalStrength) + "";
                }

                if (mthd.getName().equals("getLteSignalStrength")
                        || mthd.getName().equals("getLteRsrp")
                        || mthd.getName().equals("getLteRsrq")
                        || mthd.getName().equals("getLteRssnr")
                        || mthd.getName().equals("getLteCqi")) {

                    Log.i(LOG_TAG,
                            "onSignalStrengthsChanged LTE: " + mthd.getName() + " "
                                    + mthd.invoke(signalStrength));
                }


            }//end for
//
//            List<CellInfo> cellInfo = telMgr.getAllCellInfo();
//            if (cellInfo != null)
//                for (CellInfo m : cellInfo) {
//                    if (m instanceof CellInfoLte) {
//                        CellInfoLte cellInfoLte = (CellInfoLte) m;
//                        CellIdentityLte clte = cellInfoLte.getCellIdentity();
//
//                        int pci = clte.getPci();
//                        int tac = clte.getTac();
//                        currentCellServing.ltePCI = pci + "";
//                        if (tac != Integer.MAX_VALUE) {
//                            currentCellServing.lteTAC = tac + "";
//                            mView_HealthStatus.lteTAC = tac + "";
//                        }
//
//                        mView_HealthStatus.ltePCI = pci + "";
//
//                        int cid = clte.getCi();
//                        mView_HealthStatus.Cid = String.valueOf(cid);
//                        if (android.os.Build.VERSION.SDK_INT >= 24) {
//                            int arfcn = cellInfoLte.getCellIdentity().getEarfcn(); //requires 24
//                            currentCellServing.arfcn = arfcn + "";
//                            mView_HealthStatus.lteArfcn = arfcn + "";
//
//                        }
//                        CellSignalStrengthLte ss = cellInfoLte.getCellSignalStrength();
//                        int asus = ss.getAsuLevel();
//                        mView_HealthStatus.lteasus = asus + "";
//                        int dbm = ss.getDbm();
//                        mView_HealthStatus.ltedbm = dbm + "";
//                        int level = ss.getLevel();
//                        mView_HealthStatus.ltelevel = level + "";
//                        int ta = ss.getTimingAdvance();
//                        mView_HealthStatus.lteta = ta + "";
//
//                        Log.d("onCellInfoChanged", "CellInfoLte--" + m);
//                    }
//                }
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

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void fetchCellsInfo(List<CellInfo> cellInfo) {


        final List<CellInfo> registeredCellInfo = new ArrayList<>();
        final List<CellInfo> neighborCelllInfo = new ArrayList<>();

        //   System.out.println("cell info size " + cellInfo.size());
        //   System.out.println("cell info is" + cellInfo);
        NeighboringCellsInfo.neighboringCellList = new ArrayList<>();
        NeighboringCellsInfo.wcdma_neighboringCellList = new ArrayList<>();
        NeighboringCellsInfo.gsm_neighboringCellList = new ArrayList<>();
        CellInfo m;
        for (int i = 0; i < cellInfo.size(); i++) {
            m = cellInfo.get(i);
            System.out.println("cell info " + m);
            if (m.isRegistered()) {
                registeredCellInfo.add(m);
            } else {
                neighborCelllInfo.add(m);
            }
        }
        //  System.out.println("new cell registered " + registeredCellInfo);
        //   System.out.println("new cell non reg " + neighborCelllInfo);
        if (registeredCellInfo != null && registeredCellInfo.size() > 0) {
            if (registeredCellInfo.get(0) != null)
                System.out.println("ddServingCellInfo1");
            addServingCellInfo1(registeredCellInfo.get(0));
            if (registeredCellInfo.size() > 1) {
                if (registeredCellInfo.get(1) != null)
                    addServingCellInfo2(registeredCellInfo.get(1));
            }
        }
        if (neighborCelllInfo != null && neighborCelllInfo.size() > 0) {
            addNeighboringCellsInfo(neighborCelllInfo);
        }


    }


    //    @RequiresApi(api = Build.VERSION_CODES.M)
//    public static void checkSimSlotConnections(Context context) {
//
//        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//
//        if (telephonyManager.getPhoneCount() < 2) {
//            Log.e(TAG, "Dual SIM not supported");
//            return;
//        }
//
//        SubscriptionManager subscriptionManager = SubscriptionManager.from(context);
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
//            List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
//
//            if (subscriptionInfoList != null) {
//                for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
//                    int subscriptionId = subscriptionInfo.getSubscriptionId();
//                    int slotIndex = subscriptionInfo.getSimSlotIndex();
//
//                    // Check mobile data connection
//                    if (isMobileDataConnected(context) )
//                    {
//                        Log.i(TAG, "SIM slot " + slotIndex + " has mobile data connection");
//                          int getIndex= slotIndex;
//                        String carrierName = subscriptionInfo.getCarrierName().toString();
//                        if (isAirtelCarrier(carrierName)) {
//                            if index slot is  1 -
//
//                            Log.i(TAG, "Airtel name is" is associated with Airt");
//                        } else {
//                            Log.i(TAG, "SIM slot " + storedSlotIndex + " is not associated with Airtel");
//                        }
//                         // checkSimisAirtelorNot(getIndex);
//                        Log.i(TAG, "GetIndex  is" +getIndex);
//                    } else {
//                        Log.i(TAG, "SIM slot " + slotIndex + " does not have mobile data connection");
//                    }
//
//                    // Check Wi-Fi connection
//                    if (isWiFiConnected(context)) {
//                        Log.i(TAG, "SIM slot " + slotIndex + " has Wi-Fi connection");
//                    } else {
//                        Log.i(TAG, "SIM slot " + slotIndex + " does not have Wi-Fi connection");
//                    }
//                }
//            }
//        }
//    }
//
//    @SuppressLint("MissingPermission")
//    private static boolean isMobileDataConnected(Context context) {
//        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//
//            return telephonyManager.getNetworkType() != TelephonyManager.NETWORK_TYPE_UNKNOWN;
//        } else {
//
//            return false;
//        }
//    }
//
//    private static boolean isAirtelCarrier(String carrierName) {
//        return carrierName.toLowerCase().contains("airtel");
//    }
//
//    private static boolean isWiFiConnected(Context context) {
//        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//        return wifiInfo != null && wifiInfo.isConnected();
//    }
    private void addNeighboringCellsInfo(List<CellInfo> cellInfo) {
        for (int i = 0; i < cellInfo.size(); i++) {
            if (cellInfo.get(i) instanceof CellInfoGsm) {
                CellInfoGsm cellInfogsm = (CellInfoGsm) cellInfo.get(i);
                CellIdentityGsm c = cellInfogsm.getCellIdentity();
                CellSignalStrengthGsm ss = cellInfogsm.getCellSignalStrength();
                getNeighboringCellsInfoForGSM(ss, c);
            } else if (cellInfo.get(i) instanceof CellInfoLte) {

                cellInfoLte = (CellInfoLte) cellInfo.get(i);
                cellIdentityLte = cellInfoLte.getCellIdentity();
                CellSignalStrengthLte ss = cellInfoLte.getCellSignalStrength();
                getNeighboringCellsInfoForLte(ss, cellIdentityLte);
            } else if (cellInfo.get(i) instanceof CellInfoWcdma) {
                CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfo.get(i);
                CellIdentityWcdma cellIdentityWcdma = cellInfoWcdma.getCellIdentity();
                CellSignalStrengthWcdma ss = cellInfoWcdma.getCellSignalStrength();
                getNeighboringCellsInfoForWcdma(ss, cellIdentityWcdma);

            }

        }
    }

    private void addServingCellInfo2(CellInfo m) {
        if (m != null) {


            if (m instanceof CellInfoGsm) {
                CellInfoGsm cellInfogsm = (CellInfoGsm) m;
                CellIdentityGsm c = cellInfogsm.getCellIdentity();
                CellSignalStrengthGsm ss = cellInfogsm.getCellSignalStrength();
                String[] info = m.toString().split(" ");

                String[] ber;
                ber = info[17].split("ber=");
                if (ber[1] != null) {
                    mView_HealthStatus.second_rxqual = Integer.parseInt(ber[1]);
                }


                getGsmCellInfoForSecondSim(ss, c, cellInfogsm);
            } else if (m instanceof CellInfoLte) {

                cellInfoLte = (CellInfoLte) m;
                cellIdentityLte = cellInfoLte.getCellIdentity();
                CellSignalStrengthLte ss = cellInfoLte.getCellSignalStrength();
                getLteCellInfoForAnotherSim(ss);

            } else if (m instanceof CellInfoWcdma) {

                CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) m;
                CellIdentityWcdma cellIdentityWcdma = cellInfoWcdma.getCellIdentity();
                CellSignalStrengthWcdma ss = cellInfoWcdma.getCellSignalStrength();
                getWcdmaCellInfoForAnotherSim(ss, cellIdentityWcdma);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (m instanceof CellInfoNr) {
                    System.out.println(" entering cell info");
                    mView_HealthStatus.currentInstance = "nr";
                    cellInfoNr = (CellInfoNr) m;
                    cellIdentityNr = (CellIdentityNr) cellInfoNr.getCellIdentity();
                    CellSignalStrengthNr ss = (CellSignalStrengthNr) cellInfoNr.getCellSignalStrength();
                    getCellInfoForSecondSim5G(ss);

                }
            }
        }


    }

    private void getWcdmaCellInfoForAnotherSim(CellSignalStrengthWcdma ss, CellIdentityWcdma cellIdentityWcdma) {

        {
            mView_HealthStatus.second_SimOperator = listenService.telMgr.getSimOperatorName();

            mView_HealthStatus.second_cellInstance = "wcdma";
            int rscp = ss.getDbm();
            int uarfcn = 0;
            int lac = cellIdentityWcdma.getLac();
            int cid = cellIdentityWcdma.getCid();
            int psc = cellIdentityWcdma.getPsc();
            int mcc = cellIdentityWcdma.getMcc();
            int mnc = cellIdentityWcdma.getMnc();

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                uarfcn = cellIdentityWcdma.getUarfcn();
            }

            if (rscp != Integer.MAX_VALUE) {
                mView_HealthStatus.second_rscp_3G = rscp;
            }
            if (uarfcn != Integer.MAX_VALUE) {
                mView_HealthStatus.second_uarfcn_3G = uarfcn;
            }
            if (lac != Integer.MAX_VALUE) {
                mView_HealthStatus.second_lac_3G = lac;
            }
            if (cid != Integer.MAX_VALUE) {
                mView_HealthStatus.second_cid_3G = cid;
            }
            if (psc != Integer.MAX_VALUE) {
                mView_HealthStatus.second_Psc_3g = psc;
            }
            if (mcc != Integer.MAX_VALUE) {
                mView_HealthStatus.second_Mcc = mcc;
            }
            if (mnc != Integer.MAX_VALUE) {
                mView_HealthStatus.second_Mnc = mnc;
            }


            String ecno = String.valueOf(mView_HealthStatus.second_rscp_3G - getRxLev());
            if (ecno != null) {
                mView_HealthStatus.second_ecno_3G = ecno;
            } else {
                mView_HealthStatus.second_ecno_3G = "0";
            }


            mView_HealthStatus.second_NodeBID_3G = String.valueOf(getenb(cid));

        }
    }

    private void getLteCellInfoForSecondSim(CellSignalStrengthLte ss) {
    }

    private void getGsmCellInfoForSecondSim(CellSignalStrengthGsm ss, CellIdentityGsm c, CellInfoGsm cellInfogsm) {
        mView_HealthStatus.second_cellInstance = "gsm";
        mView_HealthStatus.second_SimOperator = listenService.telMgr.getSimOperatorName();

        int dbm = cellInfogsm.getCellSignalStrength().getDbm();

        System.out.println("ss in gsm " + dbm);
        int mcc = c.getMcc();
        int mnc = c.getMnc();


        if (mcc != Integer.MAX_VALUE) {
            mView_HealthStatus.second_Mcc = mcc;
        }
        if (mnc != Integer.MAX_VALUE) {
            mView_HealthStatus.second_Mnc = mnc;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int ta = ss.getTimingAdvance();
            if (ta != Integer.MAX_VALUE) {

                mView_HealthStatus.secong_gsmTa = ta;
            }
        }


        mView_HealthStatus.second_rxLev = dbm;


        int cid = c.getCid();
        int lac = c.getLac();
        int psc = c.getPsc();


        if (psc != Integer.MAX_VALUE) {
            mView_HealthStatus.secong_gsmPsc = psc;
        }
        System.out.println("cid is " + cid);
        if (cid != Integer.MAX_VALUE) {
            mView_HealthStatus.second_gsmCid = cid;

        }
        if (lac != Integer.MAX_VALUE) {
            mView_HealthStatus.second_gsmLac = lac;
        }


//                            if (c.getMcc() < 2147483647) {
        if (Build.VERSION.SDK_INT >= 24) {
            int arfcn = c.getArfcn();
            if (arfcn != Integer.MAX_VALUE) {
                mView_HealthStatus.second_arfcn = arfcn;
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void addServingCellInfo1(CellInfo m) {
        if (m != null) {

            if (m instanceof CellInfoGsm) {
                mView_HealthStatus.currentInstance = "gsm";
                CellInfoGsm cellInfogsm = (CellInfoGsm) m;
                CellIdentityGsm c = cellInfogsm.getCellIdentity();
                CellSignalStrengthGsm ss = cellInfogsm.getCellSignalStrength();
                getGsmCellInfoForFirstSim(ss, c, cellInfogsm);
            } else if (m instanceof CellInfoLte) {
                // Utils.appendCrashLog(" entering lte ");
                System.out.println(" entering lte " + getSNR());
                mView_HealthStatus.currentInstance = "lte";
                cellInfoLte = (CellInfoLte) m;
                cellIdentityLte = cellInfoLte.getCellIdentity();
                CellSignalStrengthLte ss = cellInfoLte.getCellSignalStrength();
                Log.d(TAG, "getCellSignalStrength: " + ss);
                // checkSimSlotConnections(MviewApplication.ctx);
                int final_value = checkDualSimForAirtel5Ghere(MviewApplication.ctx);
                System.out.println(" final value is  " + final_value);
                if (final_value == 0) {
                    mView_HealthStatus.currentInstance = "nr";
                    cellInfoNr = (CellInfoNr) m;
                    cellIdentityNr = (CellIdentityNr) cellInfoNr.getCellIdentity();
                    CellSignalStrengthNr ss1 = (CellSignalStrengthNr) cellInfoNr.getCellSignalStrength();

                    getCellInfoForFirstSim5G(ss1);
                }
                getLteCellInfoForFirstSim(ss);
                try {
                    getothernetworkparams();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (m instanceof CellInfoWcdma) {
                mView_HealthStatus.currentInstance = "wcdma";
                CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) m;
                CellIdentityWcdma cellIdentityWcdma = cellInfoWcdma.getCellIdentity();
                CellSignalStrengthWcdma ss = cellInfoWcdma.getCellSignalStrength();
                getWcdmaCellInfoForFirstSim(ss, cellIdentityWcdma);

            }
            // by swapnil 01/11/2023
            else if (m instanceof CellInfoNr) {
                System.out.println(" entering cell info");
                mView_HealthStatus.currentInstance = "nr";
                cellInfoNr = (CellInfoNr) m;
                cellIdentityNr = (CellIdentityNr) cellInfoNr.getCellIdentity();
                CellSignalStrengthNr ss = (CellSignalStrengthNr) cellInfoNr.getCellSignalStrength();
                getCellInfoForFirstSim5G(ss);

                //  getLteCellInfoForFirstSim(ss);
            }
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static int checkDualSimForAirtel5Ghere(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        SubscriptionManager subscriptionManager = SubscriptionManager.from(context);

        @SuppressLint("MissingPermission")
        List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();

        if (subscriptionInfoList == null || subscriptionInfoList.isEmpty()) {
            Log.i(TAG, "No active subscriptions found");
            return 2; // No active subscriptions found
        }

        boolean foundAirtel = false;
        for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
            String operatorName = subscriptionInfo.getCarrierName().toString();
            if (isAirtel(operatorName)) {
                foundAirtel = true;
                int subscriptionId = subscriptionInfo.getSubscriptionId();
                @SuppressLint("MissingPermission")
                TelephonyManager subscriptionTelephonyManager = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    subscriptionTelephonyManager = telephonyManager.createForSubscriptionId(subscriptionId);
                }
                if (ActivityCompat.checkSelfPermission(MviewApplication.ctx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return 0;
                }
                int networkType = subscriptionTelephonyManager.getNetworkType();
                if (networkType == TelephonyManager.NETWORK_TYPE_NR) {
                    Log.i(TAG, "Connected to 5G network (NR)");
                    return 0;
                }
            }
        }

        if (foundAirtel) {
            Log.i(TAG, "Not connected to a 5G network");
            return 1;
        } else {
            Log.i(TAG, "Airtel not found in any slot");
            return 2;
        }
    }


    private static boolean isAirtel(String operatorName) {
        // Check if the carrier name contains "airtel" (case-insensitive)
        return operatorName.toLowerCase().contains("airtel");
    }

    // by swapnil bansal 30/03/2023
    private static ArrayList<HashMap<String, String>> getothernetworkparams() throws JSONException {
        ArrayList<HashMap<String, String>> list = new ArrayList();
        JSONArray array = Network_Params.getnetwork_params();
        JSONArray finalarray = new JSONArray();
        JSONObject obj = new JSONObject();
        Log.i(TAG, "Network params are " + array);
        int rxl;
        JSONArray finalneighbourarray;
        int i;
        JSONObject finalneighbourobj;
        if (array != null && array.length() > 0) {
            try {
                for (rxl = 0; rxl < array.length(); ++rxl) {
                    JSONObject networkobject = new JSONObject();
                    JSONObject object1 = array.optJSONObject(rxl);
                    networkobject.put("mcc", object1.optString("MCC"));
                    Log.i(TAG, "mcc is " + object1.optString("MCC"));
                    networkobject.put("mnc", object1.optString("MNC"));
                    finalneighbourarray = object1.optJSONArray("Network_params");

                    for (i = 0; i < finalneighbourarray.length(); ++i) {
                        finalneighbourobj = finalneighbourarray.optJSONObject(i);
                        String signalStrength = finalneighbourobj.optString("4G_RSSI");
                        obj.put("rsrq", finalneighbourobj.optString("RSRQ"));
                        networkobject.put("signalStrength", signalStrength);
                        networkobject.put("cellId", finalneighbourobj.optString("4G_cellid"));
                        HashMap<String, String> hashMap = new HashMap();
                        hashMap.put("tac", finalneighbourobj.optString("TAC"));
                        hashMap.put("rsrq1", finalneighbourobj.optString("RSRQ"));
                        hashMap.put("cellid1", "" + com.functionapps.mview_sdk2.helper.mView_HealthStatus.Cid);
                        list.add(hashMap);
                    }

                    networkobject.put("cellType", object1.optString("type"));
                    networkobject.put("isRegistered", " ");
                    finalarray.put(networkobject);
                }
            } catch (JSONException var12) {
                var12.printStackTrace();
            }
        }

        if (obj != null) {
            obj.put("testCompletionTime", com.functionapps.mview_sdk2.helper.Utils.getDateTime());
        }

        if (com.functionapps.mview_sdk2.helper.mView_HealthStatus.Cid != null && Integer.parseInt(com.functionapps.mview_sdk2.helper.mView_HealthStatus.Cid) != 2147483647) {
            if (obj != null) {
                obj.put("cellId", com.functionapps.mview_sdk2.helper.mView_HealthStatus.Cid);
            }
        } else if (obj != null) {
            obj.put("cellId", "0");
        }

        if (com.functionapps.mview_sdk2.helper.mView_HealthStatus.currentInstance.equalsIgnoreCase("lte")) {
            if (com.functionapps.mview_sdk2.helper.mView_HealthStatus.Cid != null) {
                rxl = Integer.parseInt(com.functionapps.mview_sdk2.helper.mView_HealthStatus.Cid) & 255;
                if (obj != null) {
                    obj.put("arfcn", rxl);
                }
            } else {
                obj.put("arfcn", "0");
            }

            if (com.functionapps.mview_sdk2.helper.mView_HealthStatus.Cid != null) {
                rxl = Integer.parseInt(com.functionapps.mview_sdk2.helper.mView_HealthStatus.Cid) & 255;
                if (obj != null) {
                    obj.put("cqi", rxl);
                }
            } else if (obj != null) {
                obj.put("cqi", "0");
            }

            if (obj != null) {
                obj.put("signalStrength", com.functionapps.mview_sdk2.helper.MyPhoneStateListener.getLTERSSI());
            }

            if (com.functionapps.mview_sdk2.helper.mView_HealthStatus.lteSNR != null) {
                if (obj != null) {
                    obj.put("snr", com.functionapps.mview_sdk2.helper.mView_HealthStatus.lteSNR);
                }
            } else if (obj != null) {
                obj.put("snr", "0");
            }

            if (com.functionapps.mview_sdk2.helper.Utils.checkIfNumeric(com.functionapps.mview_sdk2.helper.mView_HealthStatus.lteRSRP)) {
                if (com.functionapps.mview_sdk2.helper.mView_HealthStatus.lteRSRP != null && Integer.parseInt(com.functionapps.mview_sdk2.helper.mView_HealthStatus.lteRSRP) != 2147483647) {
                    if (obj != null) {
                        obj.put("rsrp", com.functionapps.mview_sdk2.helper.mView_HealthStatus.lteRSRP);
                    }
                } else if (obj != null) {
                    obj.put("rsrp", "0");
                }
            } else if (obj != null) {
                obj.put("rsrp", "0");
            }

            if (com.functionapps.mview_sdk2.helper.mView_HealthStatus.lteTAC != null && Integer.parseInt(com.functionapps.mview_sdk2.helper.mView_HealthStatus.lteTAC) != 2147483647) {
                if (obj != null) {
                    obj.put("tac", com.functionapps.mview_sdk2.helper.mView_HealthStatus.lteTAC);
                }
            } else if (obj != null) {
                obj.put("tac", "0");
            }

            if (com.functionapps.mview_sdk2.helper.mView_HealthStatus.lteta != null && Integer.parseInt(com.functionapps.mview_sdk2.helper.mView_HealthStatus.lteta) != 2147483647) {
                if (obj != null) {
                    obj.put("ta", com.functionapps.mview_sdk2.helper.mView_HealthStatus.lteta);
                }
            } else if (obj != null) {
                obj.put("ta", "0");
            }

            if (com.functionapps.mview_sdk2.helper.mView_HealthStatus.ltePCI != null && Integer.parseInt(com.functionapps.mview_sdk2.helper.mView_HealthStatus.ltePCI) != 2147483647) {
                if (obj != null) {
                    obj.put("pci", com.functionapps.mview_sdk2.helper.mView_HealthStatus.ltePCI);
                }
            } else if (obj != null) {
                obj.put("pci", "0");
            }

            com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_rsrq = " ";
            HashMap hmap;
            if (com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_cellInstance != null) {
                hmap = new HashMap();
                hmap.put("rsrp2", "" + com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_Rsrp);
                hmap.put("cellid2", "" + com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_Cid);
                hmap.put("rsrq2", "" + com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_rsrq);
                list.add(hmap);
            } else {
                hmap = new HashMap();
                hmap.put("rsrp2", "NA");
                hmap.put("cellid2", "NA");
                hmap.put("rsrq2", "NA");
                list.add(hmap);
            }
        } else {
            HashMap hmap;
            if (com.functionapps.mview_sdk2.helper.mView_HealthStatus.currentInstance.equalsIgnoreCase("wcdma")) {
                if (com.functionapps.mview_sdk2.helper.mView_HealthStatus.Uarfcn != null && Integer.parseInt(com.functionapps.mview_sdk2.helper.mView_HealthStatus.Uarfcn) != 2147483647) {
                    if (obj != null) {
                        obj.put("arfcn", com.functionapps.mview_sdk2.helper.mView_HealthStatus.Uarfcn);
                    }
                } else if (obj != null) {
                    obj.put("arfcn", " 0 ");
                }

                rxl = com.functionapps.mview_sdk2.helper.MyPhoneStateListener.getRxLev();
                if (obj != null) {
                    obj.put("signalStrength", String.valueOf(rxl));
                }

                if (com.functionapps.mview_sdk2.helper.mView_HealthStatus.rscp != null) {
                    int rscp = Integer.valueOf(com.functionapps.mview_sdk2.helper.mView_HealthStatus.rscp);
                    int ecno = rscp - rxl;
                    System.out.println(" ecno is " + ecno);
                    if (obj != null) {
                        obj.put("snr", String.valueOf(ecno));
                    }
                } else if (obj != null) {
                    obj.put("snr", "0");
                }

                if (com.functionapps.mview_sdk2.helper.mView_HealthStatus.lteRSRP != null && Integer.parseInt(com.functionapps.mview_sdk2.helper.mView_HealthStatus.lteRSRP) != 2147483647) {
                    if (obj != null) {
                        obj.put("rsrp", com.functionapps.mview_sdk2.helper.mView_HealthStatus.lteRSRP);
                    }
                } else if (obj != null) {
                    obj.put("rsrp", "0");
                }

                if (com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_cellInstance != null && com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_cellInstance.equalsIgnoreCase("wcdma")) {
                    hmap = new HashMap();
                    hmap.put("rsrp2", "" + com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_rscp_3G);
                    hmap.put("cellid2", "" + com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_Cid);
                    hmap.put("rsrp1", "" + com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_Rsrp);
                    hmap.put("cellid1", com.functionapps.mview_sdk2.helper.mView_HealthStatus.Cid);
                    list.add(hmap);
                } else {
                    hmap = new HashMap();
                    hmap.put("rsrp2", "NA");
                    hmap.put("cellid2", "NA");
                    hmap.put("rsrp1", "" + com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_Rsrp);
                    hmap.put("cellid1", com.functionapps.mview_sdk2.helper.mView_HealthStatus.Cid);
                    list.add(hmap);
                }
            } else if (com.functionapps.mview_sdk2.helper.mView_HealthStatus.currentInstance.equalsIgnoreCase("gsm")) {
                if (com.functionapps.mview_sdk2.helper.mView_HealthStatus.lteta != null && Integer.parseInt(com.functionapps.mview_sdk2.helper.mView_HealthStatus.lteta) != 2147483647) {
                    if (obj != null) {
                        obj.put("ta", com.functionapps.mview_sdk2.helper.mView_HealthStatus.lteta);
                    }

                    if (com.functionapps.mview_sdk2.helper.mView_HealthStatus.ARFCN != null && Integer.parseInt(com.functionapps.mview_sdk2.helper.mView_HealthStatus.ARFCN) != 2147483647) {
                        if (obj != null) {
                            obj.put("arfcn", com.functionapps.mview_sdk2.helper.mView_HealthStatus.ARFCN);
                        }
                    } else if (obj != null) {
                        obj.put("arfcn", " 0 ");
                    }

                    rxl = com.functionapps.mview_sdk2.helper.MyPhoneStateListener.getRxLev();
                    if (obj != null) {
                        obj.put("rsrp", rxl);
                    }

                    if (com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_cellInstance != null && com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_cellInstance.equalsIgnoreCase("gsm")) {
                        hmap = new HashMap();
                        hmap.put("rsrp2", "" + com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_rxLev);
                        hmap.put("cellid2", "" + com.functionapps.mview_sdk2.helper.mView_HealthStatus.second_Cid);
                        hmap.put("rsrp1", "" + rxl);
                        hmap.put("cellid1", com.functionapps.mview_sdk2.helper.mView_HealthStatus.Cid);
                        list.add(hmap);
                    } else {
                        hmap = new HashMap();
                        hmap.put("rsrp2", "NA");
                        hmap.put("cellid2", "NA");
                        hmap.put("rsrp1", "" + rxl);
                        hmap.put("cellid1", com.functionapps.mview_sdk2.helper.mView_HealthStatus.Cid);
                        list.add(hmap);
                    }
                } else if (obj != null) {
                    obj.put("ta", " 0 ");
                }
            }
        }

        JSONObject neighbourcellmainobj = Neighbour_cells_info.sendRequest();
        Log.i(TAG, "neigh cell info is " + neighbourcellmainobj);
        JSONArray neighbourcellarray = neighbourcellmainobj.optJSONArray("neighbourCellInformation");
        String type = neighbourcellmainobj.optString("type");
        finalneighbourarray = new JSONArray();

        for (i = 0; i < neighbourcellarray.length(); ++i) {
            finalneighbourobj = new JSONObject();
            JSONObject jsonObject = neighbourcellarray.optJSONObject(i);
            finalneighbourobj.put("mcc", neighbourcellmainobj.optString("MCC"));
            finalneighbourobj.put("mnc", neighbourcellmainobj.optString("MNC"));
            String signalstrength = jsonObject.optString("signalstrength");
            finalneighbourobj.put("type", type);
            finalneighbourobj.put("signalStrength", signalstrength);
            if (type.equalsIgnoreCase("LTE")) {
                finalneighbourobj.put("cellId", finalneighbourobj.optString("4G_CI"));
            } else if (type.equalsIgnoreCase("Wcdma")) {
                finalneighbourobj.put("cellId", finalneighbourobj.optString("3G_CID"));
            } else if (type.equalsIgnoreCase("Gsm")) {
                finalneighbourobj.put("cellId", finalneighbourobj.optString("G_CID"));
            } else {
                finalneighbourobj.put("cellId", "NA");
            }

            finalneighbourobj.put("isRegistered", " ");
            finalneighbourarray.put(finalneighbourobj);
        }

        if (obj != null) {
            obj.put("neighbourCellInformation", finalneighbourarray);
        }

        if (com.functionapps.mview_sdk2.helper.Utils.checkifavailable(com.functionapps.mview_sdk2.helper.mView_HealthStatus.currentInstance)) {
            if (obj != null) {
                obj.put("ratType", com.functionapps.mview_sdk2.helper.mView_HealthStatus.currentInstance);
            }
        } else if (obj != null) {
            obj.put("ratType", "NA");
        }

        return list;
    }

    private void getGsmCellInfoForFirstSim(CellSignalStrengthGsm ss, CellIdentityGsm c, CellInfoGsm cellInfogsm) {


        {
            String simOperator = listenService.telMgr.getSimOperatorName();
            int asus = cellInfogsm.getCellSignalStrength().getAsuLevel();
            int dbm = cellInfogsm.getCellSignalStrength().getDbm();
            int level = cellInfogsm.getCellSignalStrength().getLevel();
            mView_HealthStatus.gsmSignalStrength = cellInfogsm.getCellSignalStrength().getDbm();
            System.out.println("ss in gsm " + dbm);
            mView_HealthStatus.nrsimOperator = simOperator;


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String ta = String.valueOf(ss.getTimingAdvance());
                //System.out.println("ta is "+ta);
                //	//Toast.makeText(mContext, "ta is "+ta, //Toast.LENGTH_SHORT).show();
                mView_HealthStatus.lteta = ta;
            } else {
                mView_HealthStatus.lteta = "0";
            }

            mView_HealthStatus.lteasus = asus + "";
            mView_HealthStatus.ltedbm = dbm + "";
            mView_HealthStatus.ltelevel = level + "";


            int cid = c.getCid();
            int lac = c.getLac();
            int psc = c.getPsc();


            mView_HealthStatus.Psc = String.valueOf(psc);
            mView_HealthStatus.Cid = String.valueOf(cid);
            mView_HealthStatus.Lac = String.valueOf(lac);


//                            if (c.getMcc() < 2147483647) {
            if (Build.VERSION.SDK_INT >= 24) {
                int arfcn = c.getArfcn();
                int bsic = c.getBsic();
                //System.out.println("arfcn.. cell   " + c.getArfcn());
                ///
                mView_HealthStatus.ARFCN = arfcn + "";
                //just in case for LTE if it comes here
                if (mView_HealthStatus.iCurrentNetworkState != 4) {
                    mView_HealthStatus.ltePCI = bsic + "";
                    currentCellServing.arfcn = arfcn + "";
                    //System.out.println("arfcn.. cell info  " + c.getArfcn());
                }
            } else {
                mView_HealthStatus.ltePCI = "0";
                mView_HealthStatus.lteArfcn = "0";
            }
        }
    }

    private void getWcdmaCellInfoForFirstSim(CellSignalStrengthWcdma ss, CellIdentityWcdma cellIdentityWcdma) {

        {
            String simOperator = listenService.telMgr.getSimOperatorName();

            mView_HealthStatus.mcc_first = cellIdentityWcdma.getMcc();
            mView_HealthStatus.mnc_first = cellIdentityWcdma.getMnc();
            mView_HealthStatus.currentInstance = "wcdma";
            mView_HealthStatus.nrsimOperator = simOperator;


            mView_HealthStatus.rscp = String.valueOf(ss.getDbm());
            lteParams.paramslist.Rscp = mView_HealthStatus.rscp;
            System.out.println("wcdma asu is " + ss.getAsuLevel());
            if (mView_HealthStatus.rscp != null) {

                String ecno = String.valueOf(Integer.parseInt(mView_HealthStatus.rscp) - getRxLev());
                lteParams.paramslist.Ecno = ecno;
            }
            String signalstrength = ss.toString().replace("CellSignalStrengthWcdma:", "");
            String[] signal = signalstrength.toString().split(" ");

            int psc = cellIdentityWcdma.getPsc();
            int uarfcn = 0;
            //if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uarfcn = cellIdentityWcdma.getUarfcn();
            }
            //}
            //	int Ec=cellIdentityWcdma.get

            mView_HealthStatus.Wcdma_Psc = String.valueOf(psc);
            //	mView_HealthStatus.lteArfcn=String.valueOf(uarfcn);
            mView_HealthStatus.Uarfcn = String.valueOf(uarfcn);

            int longCid = cellIdentityWcdma.getCid();

            mView_HealthStatus.nodeb_id = String.valueOf(getenb(longCid));
            lteParams.paramslist.NodeBid = mView_HealthStatus.nodeb_id;
//						mView_HealthStatus.lteENB=String.valueOf(getenb(longCid));
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getLteCellInfoForFirstSim(CellSignalStrengthLte ss) {

        {
//            List<CellInfo> cellInfos = telMgr.getAllCellInfo();
            int snr = 0;
            //if (Build.VERSION.SDK_INT >= 26) {
            snr = ss.getRssnr();
        }

//        if (!Utils.isMaxint(snr)) {
//            hmap.put("snr", "" + snr);
//        } else {
//            hmap.put("snr", "" + getSNR());
//        }

//                for (int i = 0; i < cellInfos.size(); i++) {
//                    if (cellInfos.get(i).isRegistered()) {
//                        if (cellInfos.get(i) instanceof CellInfoLte) { CellInfoLte cellInfoLte = (CellInfoLte) cellInfos.get(i);
//                            CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
//                            if(Build.VERSION.SDK_INT >= 26){
//                                String snr=String.valueOf(cellSignalStrengthLte.getRssnr());
//                                System.out.println(" snr is "+snr);
//                            }
//                        }
//                    }
//                }
//            @SuppressLint("SoonBlockedPrivateApi")
//            Method getLteRssnr = null;
//            try {
//                getLteRssnr = lastSignalStrength.getClass().getDeclaredMethod("getLteRssnr");
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            }
//            getLteRssnr.setAccessible(true);
//            int lte_SINR = 0;
//            try {
//                lte_SINR = (int) getLteRssnr.invoke(lastSignalStrength);
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            }
//            System.out.println(" lteSINR IS "+lte_SINR);

//            double snr = 0;
//            try {
//                snr = (double) SignalStrength.class.getMethod("getLteRssnr").invoke(signalStrength)/10D;
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            }sinr

//           int a=lastSignalStrength.getEvdoSnr();
//            System.out.println(" a is"+a);
//            String snr1=String.valueOf(ss.getRssnr());
//            System.out.println("Signal Strength is " +a);
//            try {
//                double snr = (double) SignalStrength.class.getMethod("getLteRssnr").invoke(lastSignalStrength)/10D;
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            }
        //int networkType = TelephonyDisplayInfo.getNetworkType() ;
        //System.out.println(" network type getLteCellInfoForFirstSim is"+networkType);

        // by swapnil bansal 30/03/2023
        String simOperator = listenService.telMgr.getSimOperatorName();

        int rscp = ss.getDbm();
        int rxl = MyPhoneStateListener.getRxLev();
        int ecno = rscp - rxl;
        System.out.println(" ecno is " + ecno);
        String newSinr = String.valueOf(ecno);
        System.out.println(" New sinr is  " + newSinr);
        mView_HealthStatus.newSinr = newSinr;
        System.out.println("lte cell info 1 " + cellIdentityLte);
        mView_HealthStatus.currentInstance = "lte";
        //int[] band=cellIdentityLte.getBands();
        //System.out.println("bands are"+Arrays.toString(band));
        int level = ss.getLevel();
        int ta = ss.getTimingAdvance();
        int rsrq = ss.getRsrq();
        mView_HealthStatus.lteRSRQ = rsrq + "";

//            int cqi = 0;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                cqi = ss.getCqi();
//            }
        int cqi = ss.getCqi();
        int cid = cellIdentityLte.getCi();
        // by swapnil bansal 30/03/2023
        rxl = Integer.parseInt(String.valueOf(cid)) & 255;
        System.out.println(" rxl is cqi is " + rxl);
        mView_HealthStatus.newCQI = String.valueOf(rxl);
        int pci = cellIdentityLte.getPci();
        int tac = cellIdentityLte.getTac();
        System.out.println(" tac in 4g is " + tac);
        int dbm = ss.getDbm();
        if (dbm != Integer.MAX_VALUE) {
            mView_HealthStatus.lteRSRP = dbm + "";
            lteParams.paramslist.Rsrp = dbm + "";
        }
        System.out.println("signal rsrp " + dbm + "");
        //   System.out.println("info while fetching lte "+ss.getDbm());
        int asus = ss.getAsuLevel();
        mView_HealthStatus.mcc_first = cellIdentityLte.getMcc();
        System.out.println(" mcc_first is" + mView_HealthStatus.mcc_first);
        mView_HealthStatus.mnc_first = cellIdentityLte.getMnc();
        mView_HealthStatus.lteCQI = String.valueOf(cqi);
        mView_HealthStatus.Cid = String.valueOf(cid);
        // by swapnil bansal 30/03/2023


                        /*
						getting cid which is ci in LTE
                         */
        if (currentCellServing != null) {
            currentCellServing.ci = String.valueOf(cid);
                        /*
                        getting PCI
                        * */
            currentCellServing.ltePCI = pci + "";
        }

        mView_HealthStatus.ltePCI = pci + "";


                        /*
                        getting tac
                         */
        if (tac != Integer.MAX_VALUE) {
            if (currentCellServing != null) {
                currentCellServing.lteTAC = tac + "";
                mView_HealthStatus.lteTAC = tac + "";
            }
        }

                      /*
                      Getting earfcn which is arfcn in gsm
                       */
        if (Build.VERSION.SDK_INT >= 24) {
            int arfcn = cellIdentityLte.getEarfcn(); //requires 24

            currentCellServing.arfcn = arfcn + "";
            mView_HealthStatus.lteArfcn = String.valueOf(arfcn);

        }
        Log.d(TAG, "getLteCellInfoForFirstSim: ");
        mView_HealthStatus.nrsimOperator = simOperator;

        mView_HealthStatus.lteasus = asus + "";

			/*mView_HealthStatus.lteRSRP = dbm + "";

			lteParams.paramslist.Rsrp=mView_HealthStatus.lteRSRP;*/

        /*Getting Level

         */

        mView_HealthStatus.ltelevel = level + "";


        mView_HealthStatus.lteENB = String.valueOf(getenb(cid));
        currentCellServing.lteENB = String.valueOf(getenb(cid));
        //System.out.println("enb..." + getenb(cid));

        /*Getting TA

         */
        mView_HealthStatus.lteta = ta + "";

        //	}
        // }
    }

    // by swapnil bansal 16/01/2022
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void getCellInfoForFirstSim5G(CellSignalStrengthNr ss) {

        System.out.print(" entering 5g params class");
        String simOperator = listenService.telMgr.getSimOperatorName();
        String mcc = cellIdentityNr.getMccString();
        String mnc = cellIdentityNr.getMncString();
        int tac = cellIdentityNr.getTac();
        int SsRsrp = ss.getSsRsrp();
        int SsRsrq = ss.getSsRsrq();
        int CsiSinr = ss.getCsiSinr();
        int SsSinr = ss.getSsSinr();
        int pci = cellIdentityNr.getPci();
        String lat = "";
        String lon = "";
        if (listenService.gps.canGetLocation()) {
            lat = listenService.gps.getLatitude() + "";
            lon = listenService.gps.getLongitude() + "";
        }
        mView_HealthStatus.longValue = lon;
        mView_HealthStatus.latValue = lat;
        int CsiRsrp = ss.getCsiRsrp();
        int CsiRsrq = ss.getCsiRsrq();
        System.out.print(" my csirsrp is " + ss.getCsiRsrp());
        System.out.print(" my csirsrq is " + ss.getCsiRsrq());

        int[] Band = cellIdentityNr.getBands();
        for (int i = 0; i < Band.length; i++) {
            int value = Band[i];
            System.out.println("Element at index " + i + ": " + value);
        }

        System.out.println(" band is " + Arrays.toString(Band));
        int arfcn = cellIdentityNr.getNrarfcn();

        // by swapnil 18/01/2022
        long cid = cellIdentityNr.getNci();
        int dbm = ss.getDbm();
        mView_HealthStatus.nrcellIdentity = String.valueOf(cid);
        mView_HealthStatus.nrDBM = String.valueOf(dbm);
        Utils.returnNAIfMaxValue(tac);
        // mView_HealthStatus.nrNetworkType=String.valueOf(networkType);
        mView_HealthStatus.nrcellIdentity = String.valueOf(cid);
        mView_HealthStatus.nrsimOperator = simOperator;
        mView_HealthStatus.nrMCC = mcc;
        mView_HealthStatus.nrMNC = mnc;
        mView_HealthStatus.nrTAC = Utils.returnNAIfMaxValue(tac);

        // BY SWAPNIL BANSAL 20/01/2023
        mView_HealthStatus.nrSSRSRP = Utils.returnNAIfMaxValue(SsRsrp);
        mView_HealthStatus.nrSSRSRQ = Utils.returnNAIfMaxValue(SsRsrq);
        mView_HealthStatus.nrCSISINR = Utils.returnNAIfMaxValue(CsiSinr);
        mView_HealthStatus.nrSSSINR = Utils.returnNAIfMaxValue(SsSinr);
        mView_HealthStatus.nrPCI = Utils.returnNAIfMaxValue(pci);
        mView_HealthStatus.nrCSIRSRP = Utils.returnNAIfMaxValue(CsiRsrp);
        mView_HealthStatus.nrCSIRSRQ = Utils.returnNAIfMaxValue(CsiRsrq);

        mView_HealthStatus.nrBAND = Arrays.toString(Band);
        mView_HealthStatus.nrARFCN = Utils.returnNAIfMaxValue(arfcn);
        //  Utils.appendCrashLog(" tac is"+tac);
        // Utils.appendCrashLog(" mcc is"+mcc);
        // Utils.appendCrashLog(" mnc is"+mnc);
        // Utils.appendCrashLog(" pci is"+pci);

        System.out.println(" tac is" + tac);
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void get5GParams() {
        CellInfoNr cellInfoNr = null;
        CellSignalStrengthNr ss = (CellSignalStrengthNr) cellInfoNr.getCellSignalStrength();
        CellIdentityNr cellIdentityNr = null;

        String mcc = cellIdentityNr.getMccString();
        String mnc = cellIdentityNr.getMncString();
        int SsRsrp = ss.getSsRsrp();
        int SsRsrq = ss.getSsRsrq();
        int CsiSinr = ss.getCsiSinr();
        int SsSinr = ss.getSsSinr();
        int CsiRsrp = ss.getCsiRsrp();
        int CsiRsrq = ss.getCsiRsrq();
        int pci = cellIdentityNr.getPci();
        long cid = cellIdentityNr.getNci();
        int tac = cellIdentityNr.getTac();
        String lat = "";
        String lon = "";
        if (listenService.gps.canGetLocation()) {
            lat = listenService.gps.getLatitude() + "";
            lon = listenService.gps.getLongitude() + "";
        }
        String simOperator = listenService.telMgr.getSimOperatorName();


    }


//    private void get5GParams() {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            @SuppressLint("MissingPermission")
//            List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();
//            if (cellInfoList != null) {
//                for (CellInfo cellInfo : cellInfoList) {
//                    if (cellInfo instanceof CellInfoNr) {
//                        CellInfoNr cellInfoNr = (CellInfoNr) cellInfo;
//                        CellSignalStrengthNr ss = (CellSignalStrengthNr) cellInfoNr.getCellSignalStrength();
//                        CellIdentityNr cellIdentityNr = (CellIdentityNr) cellInfoNr.getCellIdentity();
//                        String mcc = cellIdentityNr.getMccString();
//                        String mnc = cellIdentityNr.getMncString();
//                        int SsRsrp = ss.getSsRsrp();
//                        int SsRsrq = ss.getSsRsrq();
//                        int CsiSinr = ss.getCsiSinr();
//                        int SsSinr = ss.getSsSinr();
//                        int CsiRsrp = ss.getCsiRsrp();
//                        int CsiRsrq = ss.getCsiRsrq();
//                        int pci = cellIdentityNr.getPci();
//                        long cid = cellIdentityNr.getNci();
//                        int tac = cellIdentityNr.getTac();
//                        String lat = "";
//                        String lon = "";
//                        if (listenService.gps.canGetLocation()) {
//                            lat = String.valueOf(listenService.gps.getLatitude());
//                            lon = String.valueOf(listenService.gps.getLongitude());}
//                        String simOperator = listenService.telMgr.getSimOperatorName();
//                    }
//                }
//            }
//        }
//    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void getCellInfoForSecondSim5G(CellSignalStrengthNr ss) {

        System.out.print(" entering 5g params class");

        String simOperator = listenService.telMgr.getSimOperatorName();
        String mcc = cellIdentityNr.getMccString();
        String mnc = cellIdentityNr.getMncString();
        int tac = cellIdentityNr.getTac();
        int SsRsrp = ss.getSsRsrp();
        int SsRsrq = ss.getSsRsrq();
        int CsiSinr = ss.getCsiSinr();
        int SsSinr = ss.getSsSinr();
        int pci = cellIdentityNr.getPci();
        int CsiRsrp = ss.getCsiRsrp();
        int CsiRsrq = ss.getCsiRsrq();
        System.out.print(" my csirsrp is " + ss.getCsiRsrp());
        System.out.print(" my csirsrq is " + ss.getCsiRsrq());

        int[] Band = cellIdentityNr.getBands();
        for (int i = 0; i < Band.length; i++) {
            int value = Band[i];
            System.out.println("Element at index " + i + ": " + value);
        }

        System.out.println(" band is " + Arrays.toString(Band));
        int arfcn = cellIdentityNr.getNrarfcn();

        // by swapnil 18/01/2022
        long cid = cellIdentityNr.getNci();
        int dbm = ss.getDbm();
        mView_HealthStatus.nrcellIdentity = String.valueOf(cid);
        mView_HealthStatus.nrDBM = String.valueOf(dbm);
        Utils.returnNAIfMaxValue(tac);
        // mView_HealthStatus.nrNetworkType=String.valueOf(networkType);
        mView_HealthStatus.nrcellIdentity = String.valueOf(cid);
        mView_HealthStatus.nrsimOperator = simOperator;
        mView_HealthStatus.nrMCC = mcc;
        mView_HealthStatus.nrMNC = mnc;
        mView_HealthStatus.nrTAC = Utils.returnNAIfMaxValue(tac);

        // BY SWAPNIL BANSAL 20/01/2023
        mView_HealthStatus.nrSSRSRP = Utils.returnNAIfMaxValue(SsRsrp);
        mView_HealthStatus.nrSSRSRQ = Utils.returnNAIfMaxValue(SsRsrq);
        mView_HealthStatus.nrCSISINR = Utils.returnNAIfMaxValue(CsiSinr);
        mView_HealthStatus.nrSSSINR = Utils.returnNAIfMaxValue(SsSinr);
        mView_HealthStatus.nrPCI = Utils.returnNAIfMaxValue(pci);
        mView_HealthStatus.nrCSIRSRP = Utils.returnNAIfMaxValue(CsiRsrp);
        mView_HealthStatus.nrCSIRSRQ = Utils.returnNAIfMaxValue(CsiRsrq);

        mView_HealthStatus.nrBAND = Arrays.toString(Band);
        mView_HealthStatus.nrARFCN = Utils.returnNAIfMaxValue(arfcn);
        //  Utils.appendCrashLog(" tac is"+tac);
        // Utils.appendCrashLog(" mcc is"+mcc);
        // Utils.appendCrashLog(" mnc is"+mnc);
        // Utils.appendCrashLog(" pci is"+pci);

        System.out.println(" tac is" + tac);
    }

    private void getLteCellInfoForAnotherSim(CellSignalStrengthLte ss) {

        {
            System.out.println("lte cell info 2 " + cellIdentityLte);
            mView_HealthStatus.second_SimOperator = listenService.telMgr.getSimOperatorName();
            mView_HealthStatus.second_cellInstance = "lte";
            int level = ss.getLevel();
            int ta = ss.getTimingAdvance();
            int cqi = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                cqi = ss.getCqi();
            }
            int cid = cellIdentityLte.getCi();
            int pci = cellIdentityLte.getPci();
            int tac = cellIdentityLte.getTac();
            int mcc = cellIdentityLte.getMcc();
            int mnc = cellIdentityLte.getMnc();

            int snr = 0;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                snr = ss.getRssnr();
                System.out.println(" snr is is " + snr);

            }

            int rsrp = ss.getDbm();


            mView_HealthStatus.second_Mcc = mcc;
            mView_HealthStatus.second_Mnc = mnc;
            if (rsrp != Integer.MAX_VALUE) {
                mView_HealthStatus.second_Rsrp = rsrp;
            }
            if (snr != Integer.MAX_VALUE) {
                mView_HealthStatus.second_snr = snr;
                System.out.println(" snr is is " + mView_HealthStatus.second_snr);
            }

            if (cqi != Integer.MAX_VALUE) {
                mView_HealthStatus.second_Cqi = cqi;
            }

            if (cid != Integer.MAX_VALUE) {
                mView_HealthStatus.second_Cid = cid;
            }

            if (pci != Integer.MAX_VALUE) {
                mView_HealthStatus.second_pci = pci;
            }

            if (tac != Integer.MAX_VALUE) {

                mView_HealthStatus.second_tac = tac;
            }

            if (Build.VERSION.SDK_INT >= 24) {
                int arfcn = cellIdentityLte.getEarfcn();
                mView_HealthStatus.second_earfcn = arfcn;

            }

            mView_HealthStatus.second_ENB = String.valueOf(getenb(cid));
            if (ta != Integer.MAX_VALUE) {
                mView_HealthStatus.second_ta = ta;
            }

        }
    }

    private void getCellInfoForAnotherSim() {
    }

    private void getNeighboringCellsInfoForGSM(CellSignalStrengthGsm ss, CellIdentityGsm cellIdentityGsm) {

		/*CellInfoGsm:{mRegistered=NO mTimeStampType=oem_ril mTimeStamp=15712607914940ns mCellConnectionStatus=0
			CellIdentityGsm:{ mLac=1014 mCid=48161 mArfcn=661 mBsic=0x15 mMcc=404 mMnc=11
		mAlphaLong=404 11 mAlphaShort=404 11} CellSignalStrengthGsm: ss=8 ber=99 mTa=2147483647},*/
        try {
            if (NeighboringCellsInfo.gsm_neighboringCellList != null) {
                HashMap<Integer, Integer> hp = new HashMap<>();
                NeighboringCellsInfo.gsmParams = new ArrayList<>();
                NeighboringCellsInfo.gsmParams.add("2G_LAC");
                hp.put(0, cellIdentityGsm.getLac());


                NeighboringCellsInfo.gsmParams.add("2G_CID");
                hp.put(1, cellIdentityGsm.getCid());

                NeighboringCellsInfo.gsmParams.add("2G_ARFCN");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    hp.put(2, cellIdentityGsm.getArfcn());
                } else {
                    hp.put(2, 0);
                }

                NeighboringCellsInfo.gsmParams.add("2G_BSIC");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    hp.put(3, cellIdentityGsm.getBsic());
                } else {
                    hp.put(3, 0);
                }


                NeighboringCellsInfo.gsmParams.add("2G_PSC");
                hp.put(4, cellIdentityGsm.getPsc());

                NeighboringCellsInfo.gsmParams.add("2G_RX_LEVEL");
                hp.put(5, ss.getDbm());
                NeighboringCellsInfo.gsm_neighnor_ss = ss.getDbm();

                NeighboringCellsInfo.gsmParams.add("2G_TA");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    hp.put(6, ss.getTimingAdvance());
                } else {
                    hp.put(6, 0);
                }
                byte[] l_byte_array = CommonFunctions.convertByteArray__p(cellIdentityGsm.getCid());
                int l_RNC_ID = CommonFunctions.getRNCID_or_CID__p(l_byte_array, CommonFunctions.RNCID_C);
                NeighboringCellsInfo.gsmParams.add("2G_SITE_ID");
                hp.put(7, l_RNC_ID);
                NeighboringCellsInfo.gsmParams.add("signalstrength");
                hp.put(8, ss.getDbm());

                NeighboringCellsInfo.gsm_neighboringCellList.add(hp);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void getNeighboringCellsInfoForWcdma(CellSignalStrengthWcdma ss, CellIdentityWcdma cellIdentityWcdma) {

		/*CellInfoWcdma:{mRegistered=NO mTimeStampType=oem_ril mTimeStamp=10537808770612ns mCellConnectionStatus=0 CellIdentityWcdma:
		{ mLac=2147483647 mCid=2147483647 mPsc=260
		mUarfcn=10757 mMcc=null mMnc=null mAlphaLong= mAlphaShort=} CellSignalStrengthWcdma: ss=6 ber=99}*/
        try {
            if (NeighboringCellsInfo.wcdma_neighboringCellList != null) {
                NeighboringCellsInfo.wcdma_neighborCells = new HashMap<>();
                NeighboringCellsInfo.wcdmaParams = new ArrayList<>();


                NeighboringCellsInfo.wcdmaParams.add("3G_CID");
                NeighboringCellsInfo.wcdma_neighborCells.put(0, cellIdentityWcdma.getCid());


                NeighboringCellsInfo.wcdmaParams.add("3G_LAC");
                NeighboringCellsInfo.wcdma_neighborCells.put(1, cellIdentityWcdma.getLac());


                NeighboringCellsInfo.wcdmaParams.add("3G_PSC");
                NeighboringCellsInfo.wcdma_neighborCells.put(2, cellIdentityWcdma.getPsc());

                NeighboringCellsInfo.wcdmaParams.add("3G_UARFCN");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    NeighboringCellsInfo.wcdma_neighborCells.put(3, cellIdentityWcdma.getUarfcn());
                } else {
                    NeighboringCellsInfo.wcdma_neighborCells.put(3, 0);

                }

                NeighboringCellsInfo.wcdmaParams.add("3G_RSCP");
                NeighboringCellsInfo.wcdma_neighborCells.put(4, ss.getDbm());
                NeighboringCellsInfo.threeG_neighbor_ss = ss.getDbm();


                NeighboringCellsInfo.wcdmaParams.add("3G_RSSI");
                NeighboringCellsInfo.wcdma_neighborCells.put(5, 0);

                NeighboringCellsInfo.wcdmaParams.add("3G_CQI");
                NeighboringCellsInfo.wcdma_neighborCells.put(6, 0);

                NeighboringCellsInfo.wcdmaParams.add("3G_NODE_BID");
                NeighboringCellsInfo.wcdma_neighborCells.put(7, getenb(cellIdentityWcdma.getCid()));

                NeighboringCellsInfo.wcdmaParams.add("signalstrength");
                NeighboringCellsInfo.wcdma_neighborCells.put(8, ss.getDbm());
                NeighboringCellsInfo.wcdma_neighboringCellList.add(NeighboringCellsInfo.wcdma_neighborCells);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getNeighboringCellsInfoForLte(CellSignalStrengthLte ss, CellIdentityLte cellIdentityLte) {
        try {

            if (NeighboringCellsInfo.neighboringCellList != null) {
                NeighboringCellsInfo.lte_neighborCells = new HashMap<>();
                NeighboringCellsInfo.lteParams = new ArrayList<>();
								/*NeighboringCellsInfo.lte_neighborCells.put("mcc",cellIdentityLte.getMcc());
								NeighboringCellsInfo.lte_neighborCells.put("mnc",cellIdentityLte.getMnc());*/
                NeighboringCellsInfo.lteParams.add("4G_PCI");
                NeighboringCellsInfo.lte_neighborCells.put(0, cellIdentityLte.getPci());//pci

                NeighboringCellsInfo.lteParams.add("4G_EARFCN");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    NeighboringCellsInfo.lte_neighborCells.put(1, cellIdentityLte.getEarfcn());
                } else {
                    NeighboringCellsInfo.lte_neighborCells.put(1, 0);
                }
                NeighboringCellsInfo.lteParams.add("4G_TA");
                NeighboringCellsInfo.lte_neighborCells.put(2, ss.getTimingAdvance());//ta

                NeighboringCellsInfo.lteParams.add("4G_CQI");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NeighboringCellsInfo.lte_neighborCells.put(3, ss.getCqi());//cqi
                } else {
                    NeighboringCellsInfo.lte_neighborCells.put(3, 0);//cqi
                }

                NeighboringCellsInfo.lteParams.add("4G_RSRQ");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NeighboringCellsInfo.lte_neighborCells.put(4, ss.getRsrq());//rsrq
                } else {
                    NeighboringCellsInfo.lte_neighborCells.put(4, 0);//rsrq
                }
                NeighboringCellsInfo.lteParams.add("4G_RSRP");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NeighboringCellsInfo.lte_neighborCells.put(5, ss.getRsrp());//rsrp
                    NeighboringCellsInfo.lte_neighbor_ss = ss.getRsrp();
                } else {
                    NeighboringCellsInfo.lte_neighborCells.put(5, 0);//rsrp
                    NeighboringCellsInfo.lte_neighbor_ss = 0;
                }

                NeighboringCellsInfo.lteParams.add("4G_TAC");
                NeighboringCellsInfo.lte_neighborCells.put(6, cellIdentityLte.getTac());//tac

                NeighboringCellsInfo.lteParams.add("4G_SINR");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NeighboringCellsInfo.lte_neighborCells.put(7, ss.getRssnr());//rssnr
                } else {
                    NeighboringCellsInfo.lte_neighborCells.put(7, 0);//rssnr

                }
                NeighboringCellsInfo.lteParams.add("4G_CI");
                NeighboringCellsInfo.lte_neighborCells.put(8, cellIdentityLte.getCi());//cid

                NeighboringCellsInfo.lteParams.add("4G_ENB");

                NeighboringCellsInfo.lte_neighborCells.put(9, getenb(cellIdentityLte.getCi()));//cid
                NeighboringCellsInfo.lteParams.add("signalstrength");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NeighboringCellsInfo.lte_neighborCells.put(10, ss.getRsrp());//rsrp

                } else {
                    NeighboringCellsInfo.lte_neighborCells.put(10, 0);//rsrp

                }

                NeighboringCellsInfo.neighboringCellList.add(NeighboringCellsInfo.lte_neighborCells);
                System.out.println("size of lte list " + NeighboringCellsInfo.neighboringCellList.size());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getenb(int cid) {
           /*
                        Getting enb
                         */
        int eNB = 0;
        if (cid != Integer.MAX_VALUE) {
            String cellidHex = DecToHex(cid);
            if (cellidHex != null) {
                //System.out.println("cellidhex..  "+cellidHex);//66
                if (cellidHex.length() > 2) {

                    String eNBHex = cellidHex.substring(0, cellidHex.length() - 2);//last 2 digits represent local cellid
                    //System.out.println("enBhex" + eNBHex);

                    try {
                        eNB = HexToDec(eNBHex);
                    } catch (Exception e) {
                        e.printStackTrace();
                        //System.out.println("exception here  " + e.toString());
                    }
                }


            }
        }
        return eNB;
    }

    // @Override
    public void onDataActivity(int direction) {
        super.onDataActivity(direction);
        switch (direction) {
            case TelephonyManager.DATA_ACTIVITY_NONE:
                Log.i(LOG_TAG, "onDataActivity: DATA_ACTIVITY_NONE");
                break;
            case TelephonyManager.DATA_ACTIVITY_IN:
                Log.i(LOG_TAG, "onDataActivity: DATA_ACTIVITY_IN");
                break;
            case TelephonyManager.DATA_ACTIVITY_OUT:
                Log.i(LOG_TAG, "onDataActivity: DATA_ACTIVITY_OUT");
                break;
            case TelephonyManager.DATA_ACTIVITY_INOUT:
                Log.i(LOG_TAG, "onDataActivity: DATA_ACTIVITY_INOUT");
                break;
            case TelephonyManager.DATA_ACTIVITY_DORMANT:
                Log.i(LOG_TAG, "onDataActivity: DATA_ACTIVITY_DORMANT");
                break;
            default:
                Log.w(LOG_TAG, "onDataActivity: UNKNOWN " + direction);
                break;
        }
    }

    @Override
    public void onServiceStateChanged(ServiceState serviceState) {
        super.onServiceStateChanged(serviceState);


        if (incomingCallStatus && currentCallObject != null) {
            currentCallObject.serviceStateArr.add(serviceState);
        }
        lastServiceState = serviceState;

        String lat = "";
        String lon = "";
        if (listenService.gps.canGetLocation()) {
            lat = listenService.gps.getLatitude() + "";
            lon = listenService.gps.getLongitude() + "";
        }

        if (listenService.telMgr != null) {
            mView_HealthStatus.simOperatorName = listenService.telMgr.getSimOperatorName();
            mView_HealthStatus.OperatorName = listenService.telMgr.getNetworkOperatorName();
        }

        int ind = currServiceStateIndex + 1;
        currServiceStateIndex = (ind) % maxServiceStatesToRecord;
        //currServiceStateIndex++;

        RecordedServiceState r = new RecordedServiceState(lat, lon, serviceState, new Date());
        int sz = last5CellServiceStateArr.size();
        try {
            RecordedServiceState r1 = last5CellServiceStateArr.get(currServiceStateIndex);
            last5CellServiceStateArr.set(currServiceStateIndex, r);
        } catch (Exception e) {
            last5CellServiceStateArr.add(currServiceStateIndex, r);
        }

        //last5CellServiceStateArr.add(currServiceStateIndex, r);
        //currServiceStateIndex++;
        mView_HealthStatus.carrier_selection = serviceState.getIsManualSelection();

        Log.i(LOG_TAG, "onServiceStateChanged: " + serviceState.toString());
        Log.i(LOG_TAG, "onServiceStateChanged: getOperatorAlphaLong "
                + serviceState.getOperatorAlphaLong());
        Log.i(LOG_TAG, "onServiceStateChanged: getOperatorAlphaShort "
                + serviceState.getOperatorAlphaShort());
        Log.i(LOG_TAG, "onServiceStateChanged: getOperatorNumeric "
                + serviceState.getOperatorNumeric());
        Log.i(LOG_TAG, "onServiceStateChanged: getIsManualSelection "
                + serviceState.getIsManualSelection());
        Log.i(LOG_TAG,
                "onServiceStateChanged: getRoaming "
                        + serviceState.getRoaming());
        mView_HealthStatus.roaming = serviceState.getRoaming();
        switch (serviceState.getState()) {
            case ServiceState.STATE_IN_SERVICE:
                Log.i(LOG_TAG, "onServiceStateChanged: STATE_IN_SERVICE");
                break;
            case ServiceState.STATE_OUT_OF_SERVICE:
                Log.i(LOG_TAG, "onServiceStateChanged: STATE_OUT_OF_SERVICE");
                break;
            case ServiceState.STATE_EMERGENCY_ONLY:
                Log.i(LOG_TAG, "onServiceStateChanged: STATE_EMERGENCY_ONLY");
                break;
            case ServiceState.STATE_POWER_OFF:
                Log.i(LOG_TAG, "onServiceStateChanged: STATE_POWER_OFF");
                break;
        }
    }
//
//    @Override
//    public void onCallStateChanged(int state, String incomingNumber) {
//        super.onCallStateChanged(state, incomingNumber);
//
//        System.out.println("calling incoming num1 " + incomingNumber + "call status " + state);
//        switch (state) {
//            case TelephonyManager.CALL_STATE_IDLE:
//                callStateIdle(state, incomingNumber);
//                Log.i(LOG_TAG, "onCallStateChanged: CALL_STATE_IDLE");
//                break;
//
//            case TelephonyManager.CALL_STATE_RINGING:
//                callStateRinging(state, incomingNumber);
//                Log.i(LOG_TAG, "onCallStateChanged: CALL_STATE_RINGING");
//                break;
//
//            case TelephonyManager.CALL_STATE_OFFHOOK:
//                callStateOffhook(state, incomingNumber);
//                //	Toast.makeText(mContext, "increasing call success", Toast.LENGTH_SHORT).show();
//                Log.i(LOG_TAG, "onCallStateChanged: CALL_STATE_OFFHOOK");
//                break;
//
//            default:
//                incomingCallStatus = false;
//                outgoingCallStatus = false;
//                Log.i(LOG_TAG, "UNKNOWN_STATE: " + state);
//                break;
//        }
//    }
@RequiresApi(Build.VERSION_CODES.N)
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
                userType = "receiver";
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

    @RequiresApi(Build.VERSION_CODES.N)
    private void callStateToServer(int state, String phoneNumber, String dateTime){
        JSONObject jsonObject = new JSONObject();
        DB_handler db_handler = new DB_handler(MviewApplication.ctx);
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(MviewApplication.ctx);
        String userType = sharedPreferencesHelper.getUserType();
        String condition = sharedPreferencesHelper.getRuleCondition();
        String id = sharedPreferencesHelper.getId();
        String senderNum = sharedPreferencesHelper.getSenderNum();
        String receiverNum = sharedPreferencesHelper.getReceiverNum();
        String thirdParty = sharedPreferencesHelper.getThirdPartyNum();
        String callInitiateTime = sharedPreferencesHelper.getDateTime();
        String proto1 = mView_HealthStatus.strCurrentNetworkProtocol;
        String callTech;

        int activeSubscriptionId = SubscriptionManager.getDefaultDataSubscriptionId();
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) mContext.getSystemService(CARRIER_CONFIG_SERVICE);
        PersistableBundle carrierConfig = carrierConfigManager.getConfigForSubId(activeSubscriptionId);
        boolean isVolteAvailable = carrierConfig != null && carrierConfig.getBoolean(CarrierConfigManager.KEY_CARRIER_VOLTE_AVAILABLE_BOOL, false);
        if ((proto1.equalsIgnoreCase("LTE") || proto1.equalsIgnoreCase("4G")) && isVolteAvailable){
            callTech = "VOLTE";
        }else {
            callTech = "CS";
        }
        Utils.appendLog("ELOG_VOLTE_CALL: Call technology: "+isVolteAvailable+" network type is: "+proto1+" call technology is: "+callTech );


        Utils.appendLog( "ELOG_SENDER: get shared pref saved values userType: "+userType+" Id is :"+id+" state: "+state+" sender number: "+senderNum);

        if (userType != null) {
                if (senderNum != null) {
                    if (!senderNum.contains("+91")) {
                        senderNum = "+91" + senderNum;
                    }
                }


                Utils.appendLog("ELOG_RECEIVER: sender number is " + senderNum + " phoneNumber is " + phoneNumber);

                if (userType.equalsIgnoreCase("receiver") && senderNum.equals(phoneNumber)) {

                    Utils.appendLog("ELOG_RECEIVER: sender number matched with phone number ");

                    if (state == 0) {
                        long callDuration = Utils.calculateCallDuration(dateTime, receiver_offhook_time);

                        Utils.appendLog("ELOG_RECEIVER: call state is IDLE ");
                        try {
                            jsonObject.put("id", id);
                            jsonObject.put("user_type", userType);
                            jsonObject.put("b_num", receiverNum);
                            if (thirdParty != null) {
                                jsonObject.put("c_num", thirdParty);
                            }
                            jsonObject.put("called_technology", callTech);

//                            jsonObject.put("cst", " ");
                            jsonObject.put("speech_quality", " ");
                            jsonObject.put("codec", " ");

                            jsonObject.put("b_call_state_ringing_time", receiver_ringing_time);
                            jsonObject.put("b_call_answer_time", receiver_offhook_time);
                            jsonObject.put("b_call_disconnect_time", dateTime);

                            jsonObject.put("duration", callDuration + "sec");

                            JSONArray jsonArray = new JSONArray();
                            jsonArray.put(jsonObject);
                            Utils.appendLog("ELOG_RECEIVER: json going to server is " + jsonArray);
                            if (!Utils.isNetworkAvailable(MviewApplication.ctx)) {
                                Utils.appendLog("ELOG_VOLTE_CALL: No Internet While Sending Request, Data is Inserted in Table");
                                db_handler.open();
                                db_handler.insertInLoggingAgentTable("VolteCall", "call_report", jsonArray.toString(), Utils.getDateTime(), "INIT");
                                db_handler.close();
                            }else {
                                RequestResponse.sendEventToServer(jsonArray, AllInOneAsyncTaskForEVT.AsyncTaskPurpose.SDK, "call_report", "1", new Interfaces.ResultListner() {
                                    @Override
                                    public void onResultObtained(boolean status) {
                                        if (!status) {
                                            db_handler.open();
                                            db_handler.insertInLoggingAgentTable("VolteCall", "call_report", jsonArray.toString(), Utils.getDateTime(), "INIT");
                                            db_handler.close();
                                        }
                                    }
                                });
                            }


                            sharedPreferencesHelper.clearUserValue();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    } else if (state == 1) {
                        try {
                            Utils.appendLog("ELOG_RECEIVER: call state is RINGING");
                            receiver_ringing_time = dateTime;

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    } else if (state == 2) {
                        try {
                            Utils.appendLog("ELOG_RECEIVER: call state is OFFHOOK ");
                            receiver_offhook_time = dateTime;

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                } else if (userType.equalsIgnoreCase("sender") || userType.equalsIgnoreCase("caller")) {
                    if (state == 0) {
                        Utils.appendLog("ELOG_SENDER: offHook flag is " + offHookState);
                        try {
                            if (offHookState) {
                                long cst_time = Utils.calculateCallDuration(offhook_time, callInitiateTime);
                                jsonObject.put("id", id);
                                jsonObject.put("user_type", userType);
                                jsonObject.put("a_num", senderNum);
                                jsonObject.put("calling_technology", callTech);

                                jsonObject.put("cst", cst_time+"sec");
                                jsonObject.put("speech_quality", " ");
                                jsonObject.put("codec", " ");
                                jsonObject.put("a_call_initiate_time",callInitiateTime );
                                jsonObject.put("a_call_answer_time", offhook_time);
                                jsonObject.put("a_call_disconnect_time", dateTime);
                                long callDuration = Utils.calculateCallDuration(dateTime, offhook_time);
                                jsonObject.put("duration", callDuration + "sec");

                                JSONArray jsonArray = new JSONArray();

                                jsonArray.put(jsonObject);
                                Utils.appendLog("ELOG_SENDER: callStateToServer sender: " + jsonArray);

                                if (!Utils.isNetworkAvailable(MviewApplication.ctx)) {
                                    Utils.appendLog("ELOG_VOLTE_CALL: No Internet While Sending Request, Data is Inserted in Table");
                                    db_handler.open();
                                    db_handler.insertInLoggingAgentTable("VolteCall", "call_report", jsonArray.toString(), Utils.getDateTime(), "INIT");
                                    db_handler.close();
                                }else {
                                    RequestResponse.sendEventToServer(jsonArray, AllInOneAsyncTaskForEVT.AsyncTaskPurpose.SDK, "call_report", "1", new Interfaces.ResultListner() {
                                        @Override
                                        public void onResultObtained(boolean status) {
                                            if (!status) {
                                                db_handler.open();
                                                db_handler.insertInLoggingAgentTable("VolteCall", "call_report", jsonArray.toString(), Utils.getDateTime(), "INIT");
                                                db_handler.close();
                                            }
                                        }
                                    });
                                }


                                sharedPreferencesHelper.clearUserValue();
                                offHookState = false;

                            } else {
                                call_initiate_time = dateTime;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else if (state == 2) {
                        Utils.appendLog("ELOG_SENDER: call state is OFFHOOK ");

                        offHookState = true;
                        offhook_time = dateTime;
                    }
                } else if (userType.equalsIgnoreCase("forwarder") && senderNum.equals(phoneNumber)) {

                    Utils.appendLog("ELOG_FORWARDER: sender number matched with phone number ");

                    if (state == 0) {
                        long callDuration = Utils.calculateCallDuration(dateTime, receiver_offhook_time);

                        Utils.appendLog("ELOG_FORWARDER: call state is IDLE ");
                        try {
                            jsonObject.put("id", id);
                            jsonObject.put("user_type", userType);
                            jsonObject.put("b_num", receiverNum);
                            if (thirdParty != null) {
                                jsonObject.put("c_num", thirdParty);
                            }
                            jsonObject.put("called_technology", callTech);

//                            jsonObject.put("cst", " ");
                            jsonObject.put("speech_quality", " ");
                            jsonObject.put("codec", " ");

                            jsonObject.put("b_call_state_ringing_time", receiver_ringing_time);
                            jsonObject.put("b_call_answer_time", "NA");
                            jsonObject.put("b_call_disconnect_time", dateTime);

                            jsonObject.put("duration", callDuration + "sec");

                            JSONArray jsonArray = new JSONArray();
                            jsonArray.put(jsonObject);
                            Utils.appendLog("ELOG_FORWARDER: json going to server is " + jsonArray);

                            if (!Utils.isNetworkAvailable(MviewApplication.ctx)) {
                                Utils.appendLog("ELOG_VOLTE_CALL: No Internet While Sending Request, Data is Inserted in Table");
                                db_handler.open();
                                db_handler.insertInLoggingAgentTable("VolteCall", "call_report", jsonArray.toString(), Utils.getDateTime(), "INIT");
                                db_handler.close();
                                resetCondition(condition);
                            }else {
                                RequestResponse.sendEventToServer(jsonArray, AllInOneAsyncTaskForEVT.AsyncTaskPurpose.SDK, "call_report", "1", new Interfaces.ResultListner() {
                                    @Override
                                    public void onResultObtained(boolean status) {
                                        if (!status) {
                                            db_handler.open();
                                            db_handler.insertInLoggingAgentTable("VolteCall", "call_report", jsonArray.toString(), Utils.getDateTime(), "INIT");
                                            db_handler.close();
                                        }
                                    }
                                });
                                resetCondition(condition);
                            }


                            sharedPreferencesHelper.clearUserValue();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    } else if (state == 1) {
                        try {
                            Utils.appendLog("ELOG_FORWARDER: call state is RINGING");
                            receiver_ringing_time = dateTime;

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }

                }

        }

    }

    private void resetCondition(String rule) {
        try {
            String resetCode = null;
            // Reset USSD condition code here
            if (rule.equalsIgnoreCase(Constants.UNCONDITIONAL)){
                resetCode = "##21#";
                Utils.appendLog("ELOG_VOLTE_CALL: rule is Unconditional "+resetCode);
            }else if (rule.equalsIgnoreCase(Constants.CONDITIONAL_SWITCHOFF_UNREACHABLE)){
                resetCode = "##62#";
                Utils.appendLog("ELOG_VOLTE_CALL: rule is Conditional - SwitchOFf Or Unreachable "+resetCode);

            }else if (rule.equalsIgnoreCase(Constants.CONDITIONAL_NO_ANSWER)){
                resetCode = "##61#";
                Utils.appendLog("ELOG_VOLTE_CALL: rule is Conditional - No Answer "+resetCode);

            }else if (rule.equalsIgnoreCase(Constants.CONDITIONAL_BUSY)){
                resetCode = "##67#";
                Utils.appendLog("ELOG_VOLTE_CALL: rule is Conditional - Busy "+resetCode);

            }// Example code for resetting all call forwarding
            Intent resetIntent = new Intent(Intent.ACTION_CALL);
            resetIntent.setData(Uri.parse("tel:" + Uri.encode(resetCode)));
            resetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (ActivityCompat.checkSelfPermission(MviewApplication.ctx, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                Utils.appendLog("ELOG_CALL_PHONE permission not granted for reset.");
                return;
            }
            MviewApplication.ctx.startActivity(resetIntent);
            Utils.appendLog("ELOG_CONDITION_RESET: completed");
        } catch (Exception e) {
            Utils.appendLog("Exception in resetting USSD condition: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void callStateIdle(int state, String incomingNumber) {
        if (Utils.isMyServiceRunning(CallRecordService.class)) {
            // by swapnil bansal  24/08/2023
            //Call_State_Helper.stopCallService(MviewApplication.ctx);
        }
        Call_State_Helper.call_state = state;

        mView_HealthStatus.iTotal_Calls = prefs.getInt("totalcalls", 0);
        System.out.println("total calls is " + mView_HealthStatus.iTotal_Calls);
        mView_HealthStatus.iCall_Missed = prefs.getInt("missedcalls", 0);
        mView_HealthStatus.iCall_Success = prefs.getInt("successcalls", 0);
        mView_HealthStatus.iCall_Failed = prefs.getInt("failedcalls", 0);
        if (incomingCallStatus == true) {
            Log.d(TAG, "onCallStateChanged: incoming call ringing");
            //ringing
            //	Toast.makeText(mContext, "", Toast.LENGTH_SHORT).show();

            if (currentCallObject.isCallTaken == false) {

                mView_HealthStatus.iCall_Missed++;
						/*mView_HealthStatus.iTotal_Calls++;
						editor.putInt("totalcalls",mView_HealthStatus.iTotal_Calls).apply();*/
                //Toast.makeText(mContext, "missed calls in oncall "+mView_HealthStatus.iCall_Missed, //Toast.LENGTH_SHORT).show();
            } else if (bCallDropDanger) {
                currentCallObject.isDroppedCall = true;
                mView_HealthStatus.iCall_Failed++;
                editor.putInt("failedcalls", mView_HealthStatus.iCall_Failed).apply();
                currentCallObject.disconnectCause = "Signal Drop";
            }

            if (currentCallObject.cellLocationArr.size() == 0)
                currentCallObject.cellLocationArr.add(lastCellLocation);


            if (currentCallObject.signalStrengthArr.size() == 0)
                currentCallObject.signalStrengthArr.add(lastSignalStrength);

            currentCallObject.endTime = new Date();
            currentCallObject.endTimeInMS = System.currentTimeMillis();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //System.out.println("calling calllog");

                    writeLogToFile();
                }
            }, 3000);
        }
        if (outgoingCallStatus == true) {
            System.out.println("calling outgoing ");
					/*if(currentCallObject.isCallTaken==false)
					{
						mView_HealthStatus.iNoAnswered_Calls++;
						editor.putInt("noansweredcalls",mView_HealthStatus.iNoAnswered_Calls).apply();
						currentCallObject.iscallNotAnswered=true;

					}*/

            if (currentCallObject.cellLocationArr.size() == 0)
                currentCallObject.cellLocationArr.add(lastCellLocation);

            if (currentCallObject.signalStrengthArr.size() == 0)
                currentCallObject.signalStrengthArr.add(lastSignalStrength);

            currentCallObject.endTime = new Date();
            currentCallObject.endTimeInMS = System.currentTimeMillis();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    System.out.println("calling calllog");
                    writeLogToFile();
                }
            }, 3000);
        }
        incomingCallStatus = false;
        outgoingCallStatus = false;
        //currentCallObject = null;
    }

    private void callStateRinging(int state, String incomingNumber) {
        Call_State_Helper.call_state = state;

        System.out.println("calling incoming number 2 " + incomingNumber);
        if (incomingCallStatus == false) {
//                String phoneNumberToForward = "9810997777";
//                String url = "tel:"+"**21*"+ phoneNumberToForward+Uri.encode("#");
//                Intent intent1 = (new Intent(Intent.ACTION_CALL, Uri.parse(url)));
//                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                MainActivity.context.startActivity(intent1);

            //set it to false as phone is ringing so signal are fine
            bCallDropDanger = false;
            System.out.println("calling function ");
            currentCallObject = createCallObject(incomingNumber);
            currentCallObject.calltype = 1;
            //increment the count as call is coming/going
					/*mView_HealthStatus.iTotal_Calls++;
                    editor.putInt("totalcalls", mView_HealthStatus.iTotal_Calls);
                    editor.apply();*/
            incomingCallStatus = true;
            //currentCallObject = new MyIncomingCall();
//				currentCallObject.timeofCall = new Date();
//				currentCallObject.timeofcallInMS = System.currentTimeMillis();
//				currentCallObject.operator = listenService.telMgr.getNetworkOperatorName();
//				currentCallObject.isRoaming = lastServiceState.getRoaming();
            myCallArray.add(currentCallObject);

            if (listenService.gps.canGetLocation()) {
                currentCallObject.myLat = listenService.gps.getLatitude() + "";
                currentCallObject.myLon = listenService.gps.getLongitude() + "";
            }
                /*last5CellLocationArr.clear();
                last5CellServiceStateArr.clear();
				currLocationIndex = 0;
				currServiceStateIndex = 0;*/
        }
    }

    private void callStateOffhook(int state, String incomingNumber) {
        Call_State_Helper.call_state = state;
        if (incomingCallStatus) {
            currentCallObject.isCallTaken = true;
            mView_HealthStatus.iCall_Answered++;
            editor.putInt("answeredcalls", mView_HealthStatus.iCall_Answered).apply();
            //	mView_HealthStatus.iTotal_Calls++;
        } else {
            outgoingCallStatus = true;
            mView_HealthStatus.iOutgoing_Success++;
            editor.putInt("outgoingsuccess", mView_HealthStatus.iOutgoing_Success).apply();
            System.out.println("calling function");
            currentCallObject = createCallObject(incomingNumber);
            if (listenService.gps != null && listenService.gps.canGetLocation()) {
                currentCallObject.myLat = listenService.gps.getLatitude() + "";
                currentCallObject.myLon = listenService.gps.getLongitude() + "";
            }
            currentCallObject.calltype = 2;
            //	mView_HealthStatus.iTotal_Calls++;
        }

    }

    public MyCall createCallObject(String incomingNumber) {

        MyCall obj = new MyCall();
        obj.callerPhoneNumber = incomingNumber;
        obj.timeofCall = new Date();
        obj.timeofcallInMS = System.currentTimeMillis();
        obj.timeofcallInMSNew = Utils.getDateTime();
        obj.operator = listenService.telMgr.getNetworkOperatorName();
        obj.isRoaming = lastServiceState.getRoaming();
        obj.speed = listenService.gps.getSpeed();

        myCallArray.add(obj);

        if (listenService.gps.canGetLocation()) {
            obj.myLat = listenService.gps.getLatitude() + "";
            obj.myLon = listenService.gps.getLongitude() + "";
        }
        System.out.println("calling incoming num " + incomingNumber + Utils.getDateTime() + " " + "lat " + obj.myLat + "long  " + obj.myLon);
        return obj;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onCellLocationChanged(CellLocation location) {

        super.onCellLocationChanged(location);
        try {
            ArrayList<CellLocation> list = new ArrayList<>();
            list.add(location);
            if (incomingCallStatus && currentCallObject != null) {
                currentCallObject.cellLocationArr.add(location);
            }
            lastCellLocation = location;
            String lat = "";
            String lon = "";
            if (listenService.gps.canGetLocation()) {
                lat = listenService.gps.getLatitude() + "";
                lon = listenService.gps.getLongitude() + "";
            }

            int ind = (currLocationIndex + 1) % maxLocationsToRecord;
            currLocationIndex = ind;
            RecordedCellLocation r = new RecordedCellLocation(lat, lon, location, new Date());

            try {
                RecordedCellLocation r1 = last5CellLocationArr.get(currLocationIndex);
                last5CellLocationArr.set(currLocationIndex, r);
            } catch (Exception e) {
                last5CellLocationArr.add(currLocationIndex, r);
            }
            //System.out.println("context  "+ context);
            if (mView_HealthStatus.timeSeriesServingCellDataArray != null) {
                //System.out.println("size of array  "+mView_HealthStatus.timeSeriesServingCellDataArray.size());
            } else {
                //System.out.println("array null ");
            }

            if (mView_HealthStatus.timeSeriesServingCellDataArray == null /*&& MainActivity.context != null*/) {
                mView_HealthStatus.timeSeriesServingCellDataArray = new ArrayList<CurrentCellServing>();
                //System.out.println("array value"+mView_HealthStatus.timeSeriesServingCellDataArray);


            }
            if (mView_HealthStatus.lteparams == null) {
                ArrayList<LteParams.Paramslist> lteparams = new ArrayList<>();

            }

            if (mView_HealthStatus.timeSeriesServingCellDataArray != null && mView_HealthStatus.timeSeriesServingCellDataArray.size() > 0) {
                currentCellServing.serveTime = ((System.currentTimeMillis() - currentCellServing.captureTime) / 1000) + "";
            }

            currentCellServing = new CurrentCellServing();
            if (mView_HealthStatus.timeSeriesServingCellDataArray != null)
                mView_HealthStatus.timeSeriesServingCellDataArray.add(currentCellServing);
            currentCellServing.captureTime = System.currentTimeMillis();
            currentCellServing.lat = listenService.gps.getLatitude() + "";
            currentCellServing.lon = listenService.gps.getLongitude() + "";
            int networkType = -1;
            if (!mReadPhoneState) {
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                networkType = listenService.telMgr.getNetworkType();
                mView_HealthStatus.strCurrentNetworkState = MyPhoneStateListener.getNetworkClass(networkType, mReadPhoneState);
            }

            if (mView_HealthStatus.iCurrentNetworkState != 0)
                currentCellServing.networkType = mView_HealthStatus.iCurrentNetworkState + "G";
            else
                currentCellServing.networkType = "NS";


            if (location instanceof GsmCellLocation) {


                GsmCellLocation gcLoc = (GsmCellLocation) location;
                mView_HealthStatus.cellLocationType = "GSM";
                mView_HealthStatus.Lac = gcLoc.getLac() + "";

                //hack
                if (mView_HealthStatus.iCurrentNetworkState == 4)
                    mView_HealthStatus.lteTAC = gcLoc.getLac() + "";

                mView_HealthStatus.Psc = gcLoc.getPsc() + "";
                mView_HealthStatus.Cid = gcLoc.getCid() + "";


                currentCellServing.LAC = mView_HealthStatus.Lac;
                currentCellServing.cellId = mView_HealthStatus.Cid;
                currentCellServing.ci = mView_HealthStatus.Psc;


                mView_HealthStatus.lteENB = String.valueOf(getenb(gcLoc.getCid()));
                currentCellServing.lteENB = String.valueOf(getenb(gcLoc.getCid()));


            } else if (location instanceof CdmaCellLocation) {
                CdmaCellLocation ccLoc = (CdmaCellLocation) location;

            } else {
            }


            try {
                if (listenService.telMgr != null) {
                    List<CellInfo> cellInfo = listenService.telMgr.getAllCellInfo();
                    System.out.println("cell info 2 " + cellInfo);
                    if ((cellInfo != null) && cellInfo.size() > 0) {
                        fetchCellsInfo(cellInfo);
                    }
                }
            }


		/*{
			List<CellInfo> cellInfo = listenService.telMgr.getAllCellInfo();

			NeighboringCellsInfo.neighboringCellList = new ArrayList<>();
			NeighboringCellsInfo.wcdma_neighboringCellList = new ArrayList<>();
			NeighboringCellsInfo.gsm_neighboringCellList = new ArrayList<>();
			ArrayList<String> op_nameslte=new ArrayList<>();
			ArrayList<String> op_nameswcdma=new ArrayList<>();
			ArrayList<String> op_namesgsm=new ArrayList<>();




				if (cellInfo != null)
			{

				for(int c=0;c<cellInfo.size();c++) {
					CellInfo cellInfo1 = cellInfo.get(c);
					if (cellInfo1 != null)
					{

						if (cellInfo1.isRegistered()) {
							if (cellInfo1 instanceof CellInfoLte) {

								String[] info = cellInfo1.toString().split(" ");
								String alphaName = info[12] + info[13];

								op_nameslte.add(alphaName);
							} else if (cellInfo1 instanceof CellInfoWcdma) {
								String[] info = cellInfo1.toString().split(" ");
								String alphaName = info[11] + info[12];

								op_nameswcdma.add(alphaName);
							} else if (cellInfo1 instanceof CellInfoGsm) {

							}
						}
					}
				}





				for (int i = 0; i < cellInfo.size(); i++) {
				CellInfo m = cellInfo.get(i);
				System.out.println("cell info index " + i);

				if (m instanceof CellInfoLte)
				{

					cellInfoLte = (CellInfoLte) m;
					cellIdentityLte = cellInfoLte.getCellIdentity();
					CellSignalStrengthLte ss = cellInfoLte.getCellSignalStrength();
					if (m.isRegistered()) {
						String[] info = m.toString().split(" ");
						String alphaName = info[12] + info[13];
						if (finalOp_Nameslte != null && finalOp_Nameslte.size() > 0) {

							for (int j = 0; j < finalOp_Nameslte.size(); j++)
							{


								if (finalOp_Nameslte.get(j).equalsIgnoreCase(alphaName))
								{
									getLteCellInfoForFirstSim(ss);

								}
								else
								{
									getLteCellInfoForAnotherSim(ss);

								}

							}
						}
					}else {
							*//*{mRegistered=YES mTimeStampType=oem_ril mTimeStamp=73643362238166ns CellIdentityLte:{ mMcc=405 mMnc=872 mCi=119577 mPci=261 mTac=7}
							CellSignalStrengthLte: ss=31 rsrp=-80 rsrq=-12 rssnr=2147483647 cqi=2147483647 ta=2147483647}*//*
						getNeighboringCellsInfoForLte(ss, cellIdentityLte);


					}

				}

				else if (m instanceof CellInfoGsm) {

					//	Utils.showToast(context,"instance of gsm ");

					CellInfoGsm cellInfogsm = (CellInfoGsm) m;
					CellIdentityGsm c = cellInfogsm.getCellIdentity();
					CellSignalStrengthGsm ss = cellInfogsm.getCellSignalStrength();
					if (m.isRegistered()) {


						try {
							int asus = cellInfogsm.getCellSignalStrength().getAsuLevel();
							int dbm = cellInfogsm.getCellSignalStrength().getDbm();
							int level = cellInfogsm.getCellSignalStrength().getLevel();
							int cc = cellInfogsm.getCellSignalStrength().describeContents();
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
								String ta = String.valueOf(ss.getTimingAdvance());
								//System.out.println("ta is " + ta);
								//	//Toast.makeText(mContext, "ta is "+ta, //Toast.LENGTH_SHORT).show();
								mView_HealthStatus.lteta = ta;
							}
							mView_HealthStatus.lteasus = asus + "";
							mView_HealthStatus.ltedbm = dbm + "";
							mView_HealthStatus.ltelevel = level + "";
							//mView_HealthStatus.gsmSignalStrength=cellInfogsm.getCellSignalStrength().getDbm();


							int cid = c.getCid();
							int lac = c.getLac();
							int psc = c.getPsc();


							mView_HealthStatus.Psc = String.valueOf(psc);
							mView_HealthStatus.Cid = String.valueOf(cid);
							mView_HealthStatus.Lac = String.valueOf(lac);


//                            if (c.getMcc() < 2147483647) {
							///////check!!!!
							*//*if (Build.VERSION.SDK_INT >= 24) {
								int arfcn = c.getArfcn();
								int bsic = c.getBsic();
								mView_HealthStatus.lteArfcn = arfcn + "";
								//just in case for LTE if it comes here
								if (mView_HealthStatus.iCurrentNetworkState != 4)
									mView_HealthStatus.ltePCI = bsic + "";
								currentCellServing.arfcn = arfcn + "";
							}*//*


						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						getNeighboringCellsInfoForGSM(ss, c);
					}

				} else if (m instanceof CellInfoWcdma) {

					mView_HealthStatus.currentInstance = "wcdma";
					CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) m;
					CellIdentityWcdma cellIdentityWcdma = cellInfoWcdma.getCellIdentity();
					CellSignalStrengthWcdma ss = cellInfoWcdma.getCellSignalStrength();
					if (m.isRegistered()) {

					} else {
						getNeighboringCellsInfoForWcdma(ss, cellIdentityWcdma);
					}
				}


			}
		}

		}*/ catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCallForwardingIndicatorChanged(boolean cfi) {
        super.onCallForwardingIndicatorChanged(cfi);
        Log.i(LOG_TAG, "onCallForwardingIndicatorChanged: " + cfi);
    }

    @Override
    public void onMessageWaitingIndicatorChanged(boolean mwi) {
        super.onMessageWaitingIndicatorChanged(mwi);
        Log.i(LOG_TAG, "onMessageWaitingIndicatorChanged: " + mwi);
    }

    public void onCarrierNetworkChange(boolean active) {

    }

    @Override
    public void onActiveDataSubscriptionIdChanged(int subId) {
        super.onActiveDataSubscriptionIdChanged(subId);
//        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(MviewApplication.ctx);
//        sharedPreferencesHelper.setSubId(String.valueOf(subId));
//        Utils.appendLog("ELOG_SUBSCRIPTION_ID_MYPHONESTATE: is "+subId);
    }

    public static String getSignalStrength(SignalStrength signalStrength) {
        String ss = "";

        if (signalStrength.isGsm()) {
            Log.i(LOG_TAG, "onSignalStrengthsChanged: getGsmBitErrorRate "
                    + signalStrength.getGsmBitErrorRate());
            Log.i(LOG_TAG, "onSignalStrengthsChanged: getGsmSignalStrength "
                    + signalStrength.getGsmSignalStrength());
            ss = signalStrength.getGsmSignalStrength() + "dBm";
        } else if (signalStrength.getCdmaDbm() > 0) {
            Log.i(LOG_TAG, "onSignalStrengthsChanged: getCdmaDbm "
                    + signalStrength.getCdmaDbm());
            Log.i(LOG_TAG, "onSignalStrengthsChanged: getCdmaEcio "
                    + signalStrength.getCdmaEcio());
            ss = signalStrength.getCdmaDbm() + "dBm";
        } else {
            Log.i(LOG_TAG, "onSignalStrengthsChanged: getEvdoDbm "
                    + signalStrength.getEvdoDbm());
            Log.i(LOG_TAG, "onSignalStrengthsChanged: getEvdoEcio "
                    + signalStrength.getEvdoEcio());
            Log.i(LOG_TAG, "onSignalStrengthsChanged: getEvdoSnr "
                    + signalStrength.getEvdoSnr());
        }

        // Reflection code starts from here
        try {
            Method[] methods = SignalStrength.class
                    .getMethods();
            for (Method mthd : methods) {
                if (mthd.getName().equals("getLteSignalStrength")
                /* || mthd.getName().equals("getLteRsrp")
                 || mthd.getName().equals("getLteRsrq")
				 || mthd.getName().equals("getLteRssnr")
				 || mthd.getName().equals("getLteCqi")*/
                ) {
                    Log.i(LOG_TAG,
                            "onSignalStrengthsChanged LTE: " + mthd.getName() + " "
                                    + mthd.invoke(signalStrength));
                    String ss1 = mthd.invoke(signalStrength) + "";
                    if (!ss1.equals("99"))
                        ss = "LTE " + ss1 + "dBm";

                }//end if
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
        return ss;
    }

    public static String getNetworkClass(int networkType,boolean readPhoneStateDenied) {

        String proto = "";
        String proto1 = "";

        TelephonyManager teleMan = (TelephonyManager) MviewApplication.ctx.getSystemService(TELEPHONY_SERVICE);

        System.out.println("network type " + networkType);
        Utils.appendLog("ELOG_NETWORK_TYPE: network type is " + networkType);

        //check with Sir once
        if (networkType == 18) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Utils.appendLog("ELOG_NETWORK_TYPE: version greater than equal to 24");
                //if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                if(!readPhoneStateDenied) {
                    return checkStringNetworkTypeAccToVoice(teleMan.getVoiceNetworkType()) + "";
                }
               // }

            } else {

                mView_HealthStatus.iCurrentNetworkState = 0;
                //return mView_HealthStatus.iCurrentNetworkState; }
            }
        } else {

            Utils.appendLog("ELOG_NETWORK_TYPE: network type if not equal to 18 in else case " + networkType);

            switch (networkType) {

                case TelephonyManager.NETWORK_TYPE_GPRS:
                    proto = "2G (GPRS)";
                    proto1 = "GPRS";
                    mView_HealthStatus.iCurrentNetworkState = 2;
                    break;
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    proto = "2G (EDGE)";
                    proto1 = "EDGE";
                    mView_HealthStatus.iCurrentNetworkState = 2;
                    break;
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    proto = "2G (CDMA)";
                    proto1 = "CDMA";
                    mView_HealthStatus.iCurrentNetworkState = 2;
                    break;
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    proto = "2G (1xRTT)";
                    proto1 = "1xRTT";
                    mView_HealthStatus.iCurrentNetworkState = 2;
                    break;
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    proto = "2G (IDEN)";
                    proto1 = "IDEN";
                    mView_HealthStatus.iCurrentNetworkState = 2;
                    break;

                case TelephonyManager.NETWORK_TYPE_UMTS:
                    proto = "3G (UMTS)";
                    proto1 = "UMTS";
                    mView_HealthStatus.iCurrentNetworkState = 3;
                    break;
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    proto = "3G (EVDO_0)";
                    proto1 = "EVDO_0";
                    mView_HealthStatus.iCurrentNetworkState = 3;
                    break;
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    proto = "3G (EVDO_A)";
                    proto1 = "EVDO_A";
                    mView_HealthStatus.iCurrentNetworkState = 3;
                    break;
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    proto = "3G (HSDPA)";
                    proto1 = "HSDPA";
                    mView_HealthStatus.iCurrentNetworkState = 3;
                    break;
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    proto = "3G (HSUPA)";
                    proto1 = "HSUPA";
                    mView_HealthStatus.iCurrentNetworkState = 3;
                    break;
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    proto = "3G (HSPA)";
                    proto1 = "HSPA";
                    mView_HealthStatus.iCurrentNetworkState = 3;
                    break;
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    proto = "3G (EVDO_B)";
                    proto1 = "EVDO_B";
                    mView_HealthStatus.iCurrentNetworkState = 3;
                    break;
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                    proto = "3G (EHRPD)";
                    proto1 = "EHRPD";
                    mView_HealthStatus.iCurrentNetworkState = 3;
                    break;
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    proto = "3G (HSPAP)";
                    proto1 = "HSPAP";
                    mView_HealthStatus.iCurrentNetworkState = 3;
                    break;
                case TelephonyManager.NETWORK_TYPE_LTE:
                    proto = "4G(LTE)";
                    proto1 = "LTE";
                    mView_HealthStatus.iCurrentNetworkState = 4;
                    break;

                default:
                    proto = "NS";
                    proto1 = "NS";
                    mView_HealthStatus.iCurrentNetworkState = 0;
            }

            //	Toast.makeText(mContext, "current ntwrk state "+mView_HealthStatus.iCurrentNetworkState +networkType, Toast.LENGTH_SHORT).show();
            mView_HealthStatus.strCurrentNetworkState = proto;
            mView_HealthStatus.strCurrentNetworkProtocol = proto1;
            return proto;



        }
        return proto;
    }

    public class RecordedCellLocation {
	public String myLat;
	public String myLon;
	public CellLocation loc;
	public Date dt;

	public RecordedCellLocation(String lat, String lon, CellLocation loc1, Date dt1) {
		myLat = lat;
		myLon = lon;
		loc = loc1;
		dt = dt1;
	}
}

    public class RecordedServiceState {
	public String myLat;
	public String myLon;
	public ServiceState service;
	public Date dt;

	public RecordedServiceState(String lat, String lon, ServiceState ser, Date dt1) {
		myLat = lat;
		myLon = lon;
		service = ser;
		dt = dt1;
	}
}

	public String DecToHex(int dec) {
//        return String.format("%x", dec);
		return Integer.toHexString(dec);

	}

	// hex -> decimal
	public int HexToDec(String hex)  {
        return Integer.parseInt(hex, 16);

	}
}
