package com.newmview.wifi.fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mview.airtel.R;
import com.newmview.wifi.Telephony_params;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.database.DB_handler;
import com.newmview.wifi.helper.RequestResponse;
import com.newmview.wifi.helper.SharedPreferencesHelper;
import com.newmview.wifi.listenService;
import com.newmview.wifi.mView_HealthStatus;
import com.newmview.wifi.other.Utils;

import org.json.JSONObject;


public class FiveGParametersFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View v;
    private TextView simOperatorVal,mccVal,mncVal,tacVal,SsRsrpVal,pciVal,CsiRsrpVal,CsiRsrqVal,bandVal,arfcnVal,
            cidVal,dbmVal,latVal,longVal,enodeId,apnVal,snrVal,networkVal,utranVal,asuVal,signalVal;

    private TextView simOperatorVal2,mccVal2,mncVal2,tacVal2,SsRsrpVal2,pciVal2,CsiRsrpVal2,CsiRsrqVal2,bandVal2,arfcnVal2,
            cidVal2,dbmVal2,latVal2,longVal2,enodeId2,apnVal2,snrVal2,networkVal2,utranVal2,asuVal2,signalVal2;
    private LinearLayout llSim1Params,llSim2Params;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(MviewApplication.ctx);

    public FiveGParametersFragment() {

    }

    public static FiveGParametersFragment newInstance(String param1, String param2) {
        FiveGParametersFragment fragment = new FiveGParametersFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_five_g_parameters, container, false);

        JSONObject sim1servingobj= Telephony_params.getsim1servingcellinfo();
        Utils.appendLog("ELOG_SIM1_SERVINGCELL_INFO_SIDE_MENU: Sim 1 serving cell info is "+sim1servingobj);
        // BY SWAPNIL 18/01/2023
        llSim1Params=v.findViewById(R.id.llSim1Params);
        llSim2Params=v.findViewById(R.id.llSim2Params);
        dbmVal=v.findViewById(R.id.txt_dbmVal);
        snrVal =v.findViewById(R.id.txt_snrVal);
        apnVal = v.findViewById(R.id.txt_apnVal);
        //networkType=v.findViewById(R.id.txt_networkType);
        simOperatorVal=(TextView)v.findViewById(R.id.txt_simOpearatorVal);
        mccVal=(TextView)v.findViewById(R.id.txt_mccVal);
        mncVal=(TextView)v.findViewById(R.id.txt_mncVal);
        tacVal=(TextView)v.findViewById(R.id.txt_tacVal);
        SsRsrpVal=(TextView)v.findViewById(R.id.txt_SsRsrpVal);
        pciVal=(TextView)v.findViewById(R.id.txt_pciNewVal);
        CsiRsrpVal=(TextView)v.findViewById(R.id.txt_CsiRsrpVal);
        CsiRsrqVal=(TextView)v.findViewById(R.id.txt_CsiRsrqVal);
        bandVal=(TextView)v.findViewById(R.id.txt_bandVal);
        arfcnVal=(TextView)v.findViewById(R.id.txt_arfcnVal);
        cidVal=v.findViewById(R.id.txt_cidVal);
        latVal=v.findViewById(R.id.txt_latVal);
        longVal=v.findViewById(R.id.txt_longVal);
        enodeId=v.findViewById(R.id.txt_enode_id_val);
        networkVal=v.findViewById(R.id.txt_network_type);
        utranVal=v.findViewById(R.id.txt_utranVal);
        asuVal=v.findViewById(R.id.txt_asuVal);
        signalVal=v.findViewById(R.id.txt_signalVal);

        dbmVal2=v.findViewById(R.id.txt_dbmVal2);
        snrVal2 =v.findViewById(R.id.txt_snrVal2);
        apnVal2 = v.findViewById(R.id.txt_apnVal2);
        //networkType=v.findViewById(R.id.txt_networkType);
        simOperatorVal2=(TextView)v.findViewById(R.id.txt_simOpearatorVal2);
        mccVal2=(TextView)v.findViewById(R.id.txt_mccVal2);
        mncVal2=(TextView)v.findViewById(R.id.txt_mncVal2);
        tacVal2=(TextView)v.findViewById(R.id.txt_tacVal2);
        SsRsrpVal2=(TextView)v.findViewById(R.id.txt_SsRsrpVal2);
        pciVal2=(TextView)v.findViewById(R.id.txt_pciNewVal2);
        CsiRsrpVal2=(TextView)v.findViewById(R.id.txt_CsiRsrpVal2);
        CsiRsrqVal2=(TextView)v.findViewById(R.id.txt_CsiRsrqVal2);
        bandVal2=(TextView)v.findViewById(R.id.txt_bandVal2);
        arfcnVal2=(TextView)v.findViewById(R.id.txt_arfcnVal2);
        cidVal2=v.findViewById(R.id.txt_cidVal2);
        latVal2=v.findViewById(R.id.txt_latVal2);
        longVal2=v.findViewById(R.id.txt_longVal2);
        enodeId2=v.findViewById(R.id.txt_enode_id_val2);
        networkVal2=v.findViewById(R.id.txt_network_type2);
        utranVal2=v.findViewById(R.id.txt_utranVal2);
        asuVal2=v.findViewById(R.id.txt_asuVal2);
        signalVal2=v.findViewById(R.id.txt_signalVal2);


        try {
            uploadFile();
            String subId = sharedPreferencesHelper.getSubId();

            Utils.appendLog("ELOG_SUB_ID:  sub id 1 is: "+subId);

            if (subId.equals("0")){
                llSim1Params.setVisibility(View.VISIBLE);
                String type = sim1servingobj.optString("type");
                simOperatorVal.setText(mView_HealthStatus.second_SimOperator);
                if (type.equalsIgnoreCase("lte")){
                    pciVal.setText(sim1servingobj.optString("pci"));
                    CsiRsrpVal.setText(sim1servingobj.optString("ratType"));
                    bandVal.setText(sim1servingobj.optString("RSRP"));
                    arfcnVal.setText(sim1servingobj.optString("earfcn"));
                    cidVal.setText(sim1servingobj.optString("cell_identity"));
                    enodeId.setText(sim1servingobj.optString("cqi"));
                    mccVal.setText(sim1servingobj.optString("MCC_code"));
                    mncVal.setText(sim1servingobj.optString("MNC_code"));
                    tacVal.setText(sim1servingobj.optString("tac"));
                    snrVal.setText(sim1servingobj.optString("snr"));
                    CsiRsrqVal.setText(sim1servingobj.optString("eNodeB"));
                    SsRsrpVal.setText(sim1servingobj.optString("getRSRP"));
                    utranVal.setText(sim1servingobj.optString("eUTRAN_cell_id"));
                    networkVal.setText(sim1servingobj.optString("network_type"));
                    asuVal.setText(sim1servingobj.optString("ASU"));
                    signalVal.setText(sim1servingobj.optString("signal_quality"));
                    dbmVal.setText(sim1servingobj.optString("lac_id"));



                    apnVal.setText(" ");
                }else if (type.equalsIgnoreCase("3G")){
//                pciVal.setText(sim1servingobj.optString("pci"));
                    CsiRsrpVal.setText(sim1servingobj.optString("ratType"));
                    bandVal.setText(sim1servingobj.optString("RSCP"));
                    arfcnVal.setText(sim1servingobj.optString("Uarfcn"));
                    cidVal.setText(sim1servingobj.optString("cell_identity"));
//                enodeId.setText(sim1servingobj.optString("cqi"));
                    mccVal.setText(sim1servingobj.optString("MCC_code"));
                    mncVal.setText(sim1servingobj.optString("MNC_code"));
//                tacVal.setText(sim1servingobj.optString("tac"));
//                snrVal.setText(sim1servingobj.optString("snr"));
                    CsiRsrqVal.setText(sim1servingobj.optString("NodeB"));
                    SsRsrpVal.setText(sim1servingobj.optString("RSCP_in_dBm"));
                    utranVal.setText(sim1servingobj.optString("UTRAN_cell_id"));
                    networkVal.setText(sim1servingobj.optString("network_type"));
                    asuVal.setText(sim1servingobj.optString("ASU"));
                    signalVal.setText(sim1servingobj.optString("signal_quality"));
                    dbmVal.setText(sim1servingobj.optString("lac_id"));

                    apnVal.setText(" ");
                }else if (type.equalsIgnoreCase("2G")){
//                pciVal.setText(sim1servingobj.optString("pci"));
                    CsiRsrpVal.setText(sim1servingobj.optString("ratType"));
                    bandVal.setText(sim1servingobj.optString("RSSI"));
                    arfcnVal.setText(sim1servingobj.optString("ARFCN"));
                    cidVal.setText(sim1servingobj.optString("cell_identity"));
//                enodeId.setText(sim1servingobj.optString("cqi"));
                    mccVal.setText(sim1servingobj.optString("MCC_code"));
                    mncVal.setText(sim1servingobj.optString("MNC_code"));
//                tacVal.setText(sim1servingobj.optString("tac"));
//                snrVal.setText(sim1servingobj.optString("snr"));
                    CsiRsrqVal.setText(sim1servingobj.optString("eNodeB"));
                    SsRsrpVal.setText(sim1servingobj.optString("getRSSI"));
//                utranVal.setText(sim1servingobj.optString("UTRAN_cell_id"));
                    networkVal.setText(sim1servingobj.optString("network_type"));
                    asuVal.setText(sim1servingobj.optString("ASU"));
                    signalVal.setText(sim1servingobj.optString("signal_quality"));
                    dbmVal.setText(sim1servingobj.optString("lac_id"));

                    apnVal.setText(" ");
                }

                if (listenService.gps != null && listenService.gps.canGetLocation()){
                    latVal.setText(String.valueOf((int) listenService.gps.getLatitude()));
                    longVal.setText(String.valueOf((int) listenService.gps.getLongitude()));
                }
            }else if (subId.equals("1")){
                llSim2Params.setVisibility(View.VISIBLE);

                String type = sim1servingobj.optString("type");
                simOperatorVal2.setText(mView_HealthStatus.second_SimOperator);
                if (type.equalsIgnoreCase("lte")){
                    pciVal2.setText(sim1servingobj.optString("pci"));
                    CsiRsrpVal2.setText(sim1servingobj.optString("ratType"));
                    bandVal2.setText(sim1servingobj.optString("RSRP"));
                    arfcnVal2.setText(sim1servingobj.optString("earfcn"));
                    cidVal2.setText(sim1servingobj.optString("cell_identity"));
                    enodeId2.setText(sim1servingobj.optString("cqi"));
                    mccVal2.setText(sim1servingobj.optString("MCC_code"));
                    mncVal2.setText(sim1servingobj.optString("MNC_code"));
                    tacVal2.setText(sim1servingobj.optString("tac"));
                    snrVal2.setText(sim1servingobj.optString("snr"));
                    CsiRsrqVal2.setText(sim1servingobj.optString("eNodeB"));
                    SsRsrpVal2.setText(sim1servingobj.optString("getRSRP"));
                    utranVal2.setText(sim1servingobj.optString("eUTRAN_cell_id"));
                    networkVal2.setText(sim1servingobj.optString("network_type"));
                    asuVal2.setText(sim1servingobj.optString("ASU"));
                    signalVal2.setText(sim1servingobj.optString("signal_quality"));
                    dbmVal2.setText(sim1servingobj.optString("lac_id"));
                    apnVal2.setText(" ");


                }else if (type.equalsIgnoreCase("3G")){
//                pciVal.setText(sim1servingobj.optString("pci"));
                    CsiRsrpVal2.setText(sim1servingobj.optString("ratType"));
                    bandVal2.setText(sim1servingobj.optString("RSCP"));
                    arfcnVal2.setText(sim1servingobj.optString("Uarfcn"));
                    cidVal2.setText(sim1servingobj.optString("cell_identity"));
//                enodeId.setText(sim1servingobj.optString("cqi"));
                    mccVal2.setText(sim1servingobj.optString("MCC_code"));
                    mncVal2.setText(sim1servingobj.optString("MNC_code"));
//                tacVal.setText(sim1servingobj.optString("tac"));
//                snrVal.setText(sim1servingobj.optString("snr"));
                    CsiRsrqVal2.setText(sim1servingobj.optString("NodeB"));
                    SsRsrpVal2.setText(sim1servingobj.optString("RSCP_in_dBm"));
                    utranVal2.setText(sim1servingobj.optString("UTRAN_cell_id"));
                    networkVal2.setText(sim1servingobj.optString("network_type"));
                    asuVal2.setText(sim1servingobj.optString("ASU"));
                    signalVal2.setText(sim1servingobj.optString("signal_quality"));
                    dbmVal2.setText(sim1servingobj.optString("lac_id"));

                    apnVal2.setText(" ");
                }else if (type.equalsIgnoreCase("2G")){
//                pciVal.setText(sim1servingobj.optString("pci"));
                    CsiRsrpVal2.setText(sim1servingobj.optString("ratType"));
                    bandVal2.setText(sim1servingobj.optString("RSSI"));
                    arfcnVal2.setText(sim1servingobj.optString("ARFCN"));
                    cidVal2.setText(sim1servingobj.optString("cell_identity"));
//                enodeId.setText(sim1servingobj.optString("cqi"));
                    mccVal2.setText(sim1servingobj.optString("MCC_code"));
                    mncVal2.setText(sim1servingobj.optString("MNC_code"));
//                tacVal.setText(sim1servingobj.optString("tac"));
//                snrVal.setText(sim1servingobj.optString("snr"));
                    CsiRsrqVal2.setText(sim1servingobj.optString("eNodeB"));
                    SsRsrpVal2.setText(sim1servingobj.optString("getRSSI"));
//                utranVal.setText(sim1servingobj.optString("UTRAN_cell_id"));
                    networkVal2.setText(sim1servingobj.optString("network_type"));
                    asuVal2.setText(sim1servingobj.optString("ASU"));
                    signalVal2.setText(sim1servingobj.optString("signal_quality"));
                    dbmVal2.setText(sim1servingobj.optString("lac_id"));

                    apnVal2.setText(" ");
                }

                if (listenService.gps != null && listenService.gps.canGetLocation()){
                    latVal2.setText(String.valueOf((int) listenService.gps.getLatitude()));
                    longVal2.setText(String.valueOf((int) listenService.gps.getLongitude()));
                }
            }



//            enodeId.setText("NA");

        }
        catch (Exception e)
        {
            e.printStackTrace();
            Utils.appendCrashLog("Exception arised while setting values "+e.getStackTrace());
        }

        // networkTypeVal=v.findViewById(R.id.txt_networkTypeVal);

        return v;
    }












    private void uploadFile() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                try {
                   // new UploadHelper(Config.getLogsPath(MviewApplication.ctx),getActivity());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Utils.appendCrashLog("Exception arised while uploading file "+e.getStackTrace());
                }

            }
        });

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