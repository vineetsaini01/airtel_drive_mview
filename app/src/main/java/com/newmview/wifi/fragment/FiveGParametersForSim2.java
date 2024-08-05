package com.newmview.wifi.fragment;

import android.os.Bundle;import androidx.fragment.app.Fragment;import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mview.airtel.R;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mview.airtel.R;
import com.newmview.wifi.mView_HealthStatus;
import com.newmview.wifi.other.Utils;



public class FiveGParametersForSim2 extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View v;
    private TextView simOperator,mcc,mnc,tac,SsRsrq,SsRsrp,CsiSinr,SsSinr,pci,CsiRsrp,CsiRsrq,band,arfcn,cid,dbm;
    private TextView simOperatorVal,mccVal,mncVal,tacVal,SsRsrqVal,SsRsrpVal,CsiSinrVal,SsSinrVal,pciVal,CsiRsrpVal,CsiRsrqVal,bandVal,arfcnVal,cidVal,dbmVal;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FiveGParametersForSim2() {

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
        v=inflater.inflate(R.layout.fragment_five_g_parameters_for_sim2, container, false);
        simOperator=(TextView)v.findViewById(R.id.txt_simOpearator);
        // BY SWAPNIL 18/01/2023
        dbmVal=v.findViewById(R.id.txt_dbmVal);
        mcc=(TextView)v.findViewById(R.id.txt_mcc);
        mnc=(TextView)v.findViewById(R.id.txt_mnc);
        tac=(TextView)v.findViewById(R.id.txt_tac);
        SsRsrq=(TextView)v.findViewById(R.id.txt_SsRsrq);
        SsRsrp=(TextView)v.findViewById(R.id.txt_SsRsrp);
        CsiSinr=(TextView)v.findViewById(R.id.txt_CsiSinr);
        SsSinr=(TextView)v.findViewById(R.id.txt_SsSinr);
        pci=(TextView)v.findViewById(R.id.txt_pciNew);
        CsiRsrp=(TextView)v.findViewById(R.id.txt_CsiRsrp);
        CsiRsrq=(TextView)v.findViewById(R.id.txt_CsiRsrq);
        band=(TextView)v.findViewById(R.id.txt_band);
        arfcn=(TextView)v.findViewById(R.id.txt_arfcn);
        cid=v.findViewById(R.id.txt_cid);
        //networkType=v.findViewById(R.id.txt_networkType);
        simOperatorVal=(TextView)v.findViewById(R.id.txt_simOpearatorVal);
        mccVal=(TextView)v.findViewById(R.id.txt_mccVal);
        mncVal=(TextView)v.findViewById(R.id.txt_mncVal);
        tacVal=(TextView)v.findViewById(R.id.txt_tacVal);
        SsRsrqVal=(TextView)v.findViewById(R.id.txt_SsRsrqVal);
        SsRsrpVal=(TextView)v.findViewById(R.id.txt_SsRsrpVal);
        CsiSinrVal=(TextView)v.findViewById(R.id.txt_CsiSinrVal);
        SsSinrVal=(TextView)v.findViewById(R.id.txt_SsSinrVal);
        pciVal=(TextView)v.findViewById(R.id.txt_pciNewVal);
        CsiRsrpVal=(TextView)v.findViewById(R.id.txt_CsiRsrpVal);
        CsiRsrqVal=(TextView)v.findViewById(R.id.txt_CsiRsrqVal);
        bandVal=(TextView)v.findViewById(R.id.txt_bandVal);
        arfcnVal=(TextView)v.findViewById(R.id.txt_arfcnVal);
        cidVal=v.findViewById(R.id.txt_cidVal);
        try {
            uploadFile();
            simOperatorVal.setText(mView_HealthStatus.nrsimOperator);
            mccVal.setText(mView_HealthStatus.nrMCC);
            mncVal.setText(mView_HealthStatus.nrMNC);
            tacVal.setText(mView_HealthStatus.nrTAC);
            dbmVal.setText(mView_HealthStatus.nrDBM);
            SsRsrqVal.setText(mView_HealthStatus.nrSSRSRQ);
            SsRsrpVal.setText(mView_HealthStatus.nrSSRSRP);
            CsiSinrVal.setText(mView_HealthStatus.nrCSISINR);
            SsSinrVal.setText(mView_HealthStatus.nrSSSINR);
            pciVal.setText(mView_HealthStatus.nrPCI);
            CsiRsrpVal.setText(mView_HealthStatus.nrCSIRSRP);
            CsiRsrqVal.setText(mView_HealthStatus.nrCSIRSRQ);
            bandVal.setText(mView_HealthStatus.nrBAND);
            arfcnVal.setText(mView_HealthStatus.nrARFCN);
            cidVal.setText(mView_HealthStatus.nrcellIdentity);



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