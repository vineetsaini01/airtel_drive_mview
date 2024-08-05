package com.newmview.wifi.fragment;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mview.airtel.R;
import com.newmview.wifi.Telephony_params;
import com.newmview.wifi.listenService;
import com.newmview.wifi.mView_HealthStatus;
import com.newmview.wifi.other.Utils;

import org.json.JSONObject;


public class Sim2Params extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View v;
    private TextView simOperator, mcc, mnc, tac, SsRsrq, SsRsrp, CsiSinr, SsSinr, pci, CsiRsrp, CsiRsrq, band, arfcn, cid, dbm;
    private TextView simOperatorVal, mccVal, mncVal, tacVal, SsRsrqVal, SsRsrpVal, CsiSinrVal, SsSinrVal, pciVal, CsiRsrpVal, CsiRsrqVal, bandVal, arfcnVal, cidVal, dbmVal, latVal, longVal, enodeId;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Sim2Params() {

    }

    public static Sim2Params newInstance(String param1, String param2) {
        Sim2Params fragment = new Sim2Params();
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
        v = inflater.inflate(R.layout.fragment_five_g_parameters, container, false);

        JSONObject sim1servingobj = Telephony_params.getsim2servingcellinfo();
        Log.d("TAG", "sim1servingobj value is: " + sim1servingobj);

        simOperator = (TextView) v.findViewById(R.id.txt_simOpearator);
        // BY SWAPNIL 18/01/2023
        dbmVal = v.findViewById(R.id.txt_dbmVal);
        mcc = (TextView) v.findViewById(R.id.txt_mcc);
        mnc = (TextView) v.findViewById(R.id.txt_mnc);
        tac = (TextView) v.findViewById(R.id.txt_tac);
        SsRsrq = (TextView) v.findViewById(R.id.txt_SsRsrq);
        SsRsrp = (TextView) v.findViewById(R.id.txt_SsRsrp);
        CsiSinr = (TextView) v.findViewById(R.id.txt_CsiSinr);
        SsSinr = (TextView) v.findViewById(R.id.txt_SsSinr);
        pci = (TextView) v.findViewById(R.id.txt_pciNew);
        CsiRsrp = (TextView) v.findViewById(R.id.txt_CsiRsrp);
        CsiRsrq = (TextView) v.findViewById(R.id.txt_CsiRsrq);
        band = (TextView) v.findViewById(R.id.txt_band);
        arfcn = (TextView) v.findViewById(R.id.txt_arfcn);
        cid = v.findViewById(R.id.txt_cid);
        //networkType=v.findViewById(R.id.txt_networkType);
        simOperatorVal = (TextView) v.findViewById(R.id.txt_simOpearatorVal);
        mccVal = (TextView) v.findViewById(R.id.txt_mccVal);
        mncVal = (TextView) v.findViewById(R.id.txt_mncVal);
        tacVal = (TextView) v.findViewById(R.id.txt_tacVal);
        SsRsrqVal = (TextView) v.findViewById(R.id.txt_SsRsrqVal);
        SsRsrpVal = (TextView) v.findViewById(R.id.txt_SsRsrpVal);
        CsiSinrVal = (TextView) v.findViewById(R.id.txt_CsiSinrVal);
        SsSinrVal = (TextView) v.findViewById(R.id.txt_SsSinrVal);
        pciVal = (TextView) v.findViewById(R.id.txt_pciNewVal);
//        CsiRsrpVal=(TextView)v.findViewById(R.id.txt_CsiRsrpVal);
//        CsiRsrqVal=(TextView)v.findViewById(R.id.txt_CsiRsrqVal);
//        bandVal=(TextView)v.findViewById(R.id.txt_bandVal);
        arfcnVal = (TextView) v.findViewById(R.id.txt_arfcnVal);
        cidVal = v.findViewById(R.id.txt_cidVal);
        latVal = v.findViewById(R.id.txt_latVal);
        longVal = v.findViewById(R.id.txt_longVal);
//        enodeId=v.findViewById(R.id.txt_enode_id_val);
        try {
            uploadFile();
            simOperatorVal.setText(mView_HealthStatus.nrsimOperator);
            mccVal.setText(sim1servingobj.optString("mcc"));
            mncVal.setText(sim1servingobj.optString("mnc"));
            tacVal.setText(sim1servingobj.optString("tac"));
//            dbmVal.setText(mView_HealthStatus.nrDBM);
            SsRsrqVal.setText(mView_HealthStatus.nrSSRSRQ);
            SsRsrpVal.setText(sim1servingobj.optString("rsrp"));
            CsiSinrVal.setText(sim1servingobj.optString("snr"));
//            SsSinrVal.setText(mView_HealthStatus.nrSSSINR);
            pciVal.setText(sim1servingobj.optString("pci"));
//            CsiRsrpVal.setText(sim1servingobj.optString("rsrp"));
//            CsiRsrqVal.setText(mView_HealthStatus.nrCSIRSRQ);
//            bandVal.setText(mView_HealthStatus.nrBAND);
            arfcnVal.setText(sim1servingobj.optString("earfcn"));
            cidVal.setText(sim1servingobj.optString("cellid"));
            if (listenService.gps != null && listenService.gps.canGetLocation()) {
                latVal.setText(String.valueOf((int) listenService.gps.getLatitude()));
                longVal.setText(String.valueOf((int) listenService.gps.getLongitude()));
            }

//            enodeId.setText("NA");

        } catch (Exception e) {
            e.printStackTrace();
            Utils.appendCrashLog("Exception arised while setting values " + e.getStackTrace());
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
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.appendCrashLog("Exception arised while uploading file " + e.getStackTrace());
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