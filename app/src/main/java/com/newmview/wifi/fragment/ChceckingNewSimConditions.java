package com.newmview.wifi.fragment;

import android.annotation.SuppressLint;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.telephony.SubscriptionInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mview.airtel.R;


import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.widget.TextView;

import com.newmview.wifi.application.MviewApplication;

import java.util.List;


public class ChceckingNewSimConditions extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View v;
    private TextView simOperator,mcc,mnc,tac,SsRsrq,SsRsrp,CsiSinr,SsSinr,pci,CsiRsrp,CsiRsrq,band,arfcn,cid,dbm;
    private TextView simOperatorVal,mccVal,mncVal,tacVal,SsRsrqVal,SsRsrpVal,CsiSinrVal,SsSinrVal,pciVal,CsiRsrpVal,CsiRsrqVal,bandVal,arfcnVal,cidVal,dbmVal,latVal,longVal;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final String TAG = "MyPhoneStateListener1";
    public ChceckingNewSimConditions() {

    }

    public static ChceckingNewSimConditions newInstance(String param1, String param2) {
        ChceckingNewSimConditions fragment = new ChceckingNewSimConditions();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_chcecking_new_sim_conditions, container, false);



       simOperator=(TextView)v.findViewById(R.id.txt_simOpearator);
//        // BY SWAPNIL 18/01/2023
//        dbmVal=v.findViewById(R.id.txt_dbmVal);
//        mcc=(TextView)v.findViewById(R.id.txt_mcc);
//        mnc=(TextView)v.findViewById(R.id.txt_mnc);
//        tac=(TextView)v.findViewById(R.id.txt_tac);
//        SsRsrq=(TextView)v.findViewById(R.id.txt_SsRsrq);
//        SsRsrp=(TextView)v.findViewById(R.id.txt_SsRsrp);
//        CsiSinr=(TextView)v.findViewById(R.id.txt_CsiSinr);
//        SsSinr=(TextView)v.findViewById(R.id.txt_SsSinr);
//        pci=(TextView)v.findViewById(R.id.txt_pciNew);
//        CsiRsrp=(TextView)v.findViewById(R.id.txt_CsiRsrp);
//        CsiRsrq=(TextView)v.findViewById(R.id.txt_CsiRsrq);
//        band=(TextView)v.findViewById(R.id.txt_band);
//        arfcn=(TextView)v.findViewById(R.id.txt_arfcn);
//        cid=v.findViewById(R.id.txt_cid);
        //networkType=v.findViewById(R.id.txt_networkType);
       simOperatorVal=(TextView)v.findViewById(R.id.txt_simOpearatorVal0);
       mccVal=(TextView)v.findViewById(R.id.txt_sim2);
        mncVal=(TextView)v.findViewById(R.id.txt_mncVal);
        isAirtelSimConnectedToNetwork(MviewApplication.ctx);
        checkDualSimForAirtel5G(MviewApplication.ctx);

//        tacVal=(TextView)v.findViewById(R.id.txt_tacVal);
//        SsRsrqVal=(TextView)v.findViewById(R.id.txt_SsRsrqVal);
//        SsRsrpVal=(TextView)v.findViewById(R.id.txt_SsRsrpVal);
//        CsiSinrVal=(TextView)v.findViewById(R.id.txt_CsiSinrVal);
//        SsSinrVal=(TextView)v.findViewById(R.id.txt_SsSinrVal);
//        pciVal=(TextView)v.findViewById(R.id.txt_pciNewVal);
//        CsiRsrpVal=(TextView)v.findViewById(R.id.txt_CsiRsrpVal);
//        CsiRsrqVal=(TextView)v.findViewById(R.id.txt_CsiRsrqVal);
//        bandVal=(TextView)v.findViewById(R.id.txt_bandVal);
//        arfcnVal=(TextView)v.findViewById(R.id.txt_arfcnVal);
//        cidVal=v.findViewById(R.id.txt_cidVal);
//        latVal=v.findViewById(R.id.txt_latVal);
//        longVal=v.findViewById(R.id.txt_longVal);
      //  try {
           // uploadFile();
//            simOperatorVal.setText(mView_HealthStatus.nrsimOperator);
//            mccVal.setText(mView_HealthStatus.nrMCC);
//            mncVal.setText(mView_HealthStatus.nrMNC);
//            tacVal.setText(mView_HealthStatus.nrTAC);
//            dbmVal.setText(mView_HealthStatus.nrDBM);
//            SsRsrqVal.setText(mView_HealthStatus.nrSSRSRQ);
//            SsRsrpVal.setText(mView_HealthStatus.nrSSRSRP);
//            CsiSinrVal.setText(mView_HealthStatus.nrCSISINR);
//            SsSinrVal.setText(mView_HealthStatus.nrSSSINR);
//            pciVal.setText(mView_HealthStatus.nrPCI);
//            CsiRsrpVal.setText(mView_HealthStatus.nrCSIRSRP);
//            CsiRsrqVal.setText(mView_HealthStatus.nrCSIRSRQ);
//            bandVal.setText(mView_HealthStatus.nrBAND);
//            arfcnVal.setText(mView_HealthStatus.nrARFCN);
//            cidVal.setText(mView_HealthStatus.nrcellIdentity);
//            latVal.setText(mView_HealthStatus.latValue);
//            longVal.setText(mView_HealthStatus.longValue);


        //}


        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public int checkDualSimForAirtel5G(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        SubscriptionManager subscriptionManager = SubscriptionManager.from(context);
        @SuppressLint("MissingPermission")
        List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();

        if (subscriptionInfoList != null && subscriptionInfoList.size() >= 2) {
            SubscriptionInfo subInfo0 = subscriptionInfoList.get(0);
            String operatorName0 = subInfo0.getCarrierName().toString();
            SubscriptionInfo subInfo1 = subscriptionInfoList.get(1);
            String operatorName1 = subInfo1.getCarrierName().toString();
            simOperatorVal.setText(operatorName0);
            mccVal.setText(operatorName1);
            Log.d(TAG, "checkDualSimFor vodafone: "+operatorName0);
            if (isAirtel(operatorName0)) {
                @SuppressLint("MissingPermission")
                int networkType0 = telephonyManager.getNetworkType();
                if (networkType0 == TelephonyManager.NETWORK_TYPE_NR) {
                    Log.i(TAG, "Connected to 5G network (NR)");
//                    simOperatorVal.setText("Idea sim and 5g)");
                    return 0;
                } else {
                    Log.i(TAG, "Not connected to a 5G network");
//                    simOperatorVal.setText("Idea sim but not 5g");
                    return 1;
                }
            } else if (isAirtel(operatorName1)) {
                @SuppressLint("MissingPermission")
                int networkType1 = telephonyManager.getNetworkType();
                Log.i(TAG, "SIM slot 1 is Idea" + networkType1);
                if (networkType1 == TelephonyManager.NETWORK_TYPE_NR) {
                    Log.i(TAG, "Connected to 5G network (NR)");
//                    mccVal.setText("Idea sim and 5g");
                    return 0;
                } else {
//                    mccVal.setText("Idea sim but not 5g");
                    Log.i(TAG, "Not connected to a 5G network");
                    return 1;
                }
            } else {
                Log.i(TAG, "Idea not found in either slot");
//                mncVal.setText("Idea not found in either slot");
                return 2;
            }
        } else if (subscriptionInfoList != null && subscriptionInfoList.size() == 1) {
            SubscriptionInfo subInfo = subscriptionInfoList.get(0);
            String operatorName = subInfo.getCarrierName().toString();
            simOperatorVal.setText(operatorName);
            Log.d(TAG, "checkDualSimFor vodafone: "+operatorName);
            if (isAirtel(operatorName)) {
                @SuppressLint("MissingPermission")
                int networkType = telephonyManager.getNetworkType();
                if (networkType == TelephonyManager.NETWORK_TYPE_NR) {
                    Log.i(TAG, "Connected to 5G network (NR)");
//                    simOperatorVal.setText("Idea sim and 5g");
                    return 0;
                } else {
//                    simOperatorVal.setText("Idea sim but not 5g");
                    Log.i(TAG, "Not connected to a 5G network");
                    return 1;
                }
            } else {
                Log.i(TAG, "Idea not found in the single slot");
//                simOperatorVal.setText("Idea not found in the single slot");
                return 2;
            }
        } else {
            Log.i(TAG, "Less than two active subscriptions");
            return 3;
        }
    }

    public void isAirtelSimConnectedToNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            String operatorName = telephonyManager.getNetworkOperatorName();
            if (isAirtel(operatorName)) {
                NetworkInfo mobileNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (mobileNetworkInfo != null && mobileNetworkInfo.isConnected()) {
                    Log.i(TAG, "Airtel SIM is connected to mobile data");}
                NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (wifiNetworkInfo != null && wifiNetworkInfo.isConnected()) {
                    Log.i(TAG, "Airtel SIM is connected to Wi-Fi");};}}
        Log.i(TAG, "Airtel SIM not available");
    }


    private static boolean isAirtel(String operatorName) {
        return operatorName.toLowerCase().contains("idea");
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println("on activity created called");

        //  cidVal.setText((CharSequence) mView_HealthStatus.nrcellIdentity);
        //networkTypeVal.setText(mView_HealthStatus.nrNetworkType);
    }


}