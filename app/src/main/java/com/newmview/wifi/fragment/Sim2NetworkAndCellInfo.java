package com.newmview.wifi.fragment;

import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.newmview.wifi.MyPhoneStateListener;
import com.mview.airtel.R;
import com.newmview.wifi.helper.AllInOneAsyncTaskForNetwork;
import com.newmview.wifi.helper.RequestResponse;
import com.newmview.wifi.listenService;
import com.newmview.wifi.mView_HealthStatus;
import com.newmview.wifi.network.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.newmview.wifi.network.NetworkUtil.getLTEParams;
import static com.newmview.wifi.network.NetworkUtil.getSecondSimGSMNetworkParams;
import static com.newmview.wifi.network.NetworkUtil.getSecondSimThreeGNetworkParams;

public class Sim2NetworkAndCellInfo extends Fragment {
    private static final String TAG = "Sim2NetworkAndCellInfo";
    View view;

    TextView txt_operator, txt_mccval,txt_mncval,txt_lacval,txt_typeval ;
    TextView  txt_rncval,txt_cellidval,txt_pscval,txt_cellid;
    TextView  txt_rssival,txt_echoval,txt_snrval ;

    TextView txt_snr;
    TextView txt_lac;

    //labels
    TextView txt_rnc,txt_rssi,txt_psc;
    //RSSI is rxd signal strength indication

    TextView  txt_lonval,txt_latval ;
    TextView  txt_speedval,txt_gpsaccuracyval ;
    TextView  txt_heightval,txt_altitudeval, txt_groundval ;

    TextView txt_ulval, txt_dlval;
    TextView txt_dataval, txt_idleval,txt_datamode;

    TextView txt_serving;

    //TableLayout tbl_layout;

    boolean dontRefreshScreen;
    Handler mHandler;
    int refreshFrequency = 1000; //1 second

    public long lastTxBytes, lastRxBytes;
    public long lastTimestamp;

    int cellLocationArraySizeShown = 0;

    RelativeLayout r4;
    TextView txt_rsrpval, txt_arfcnval, txt_taval, txt_cqival,txt_cqi;
    TextView txt_rsrp,level,qual,ta;

    //LinearLayout l4_21, l4_22, l4_31, l4_41, l2;
    TableRow l4_21, l4_22, l4_31,l2;
    TableLayout l4_41;
    TextView timeval,lacval,nodeval,cellidval,cival,arfcnval,levelval,qualval,typeval,servcval;
    TextView uarfcn,rscp,ec,psc,type,node_val,uarfcn_val,rscp_val,ec_val,psc_val,type_val;
    TableLayout layoutFor4g;


    //TextView txt_pci;

    TextView txt_arfcn;
    private Menu menu;
    private View v;
    private TextView arfcn,node,cellid,lac,ci;
    private TableLayout threegparams;
    private TextView txt_ta;
    private LinearLayout cellinfo,noinfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v=getLayoutInflater().inflate(R.layout.second_cell_info,container,false);
        init();
        useHandlerForScreenRefresh();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sendRequest();
            }
        },3000);

        return v;
    }
    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first

        dontRefreshScreen = true;

    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        dontRefreshScreen = false;
        useHandlerForScreenRefresh();


    }

    private void sendRequest() {
        Log.i(TAG,"Send Request");
        try {


            JSONArray jsonArray = new JSONArray();
            Log.i(TAG,"Second cell instance "+mView_HealthStatus.second_cellInstance);
            if(mView_HealthStatus.second_cellInstance!=null) {

                if (mView_HealthStatus.second_cellInstance.equalsIgnoreCase("lte")) {
                    Log.i(TAG,"check 1");
                    jsonArray.put(NetworkUtil.getSecondaryLTENetworkParams());
                    // jsonArray.put(getThreeGParams());
                    //jsonArray.put(getGSMParams());
                } else if(mView_HealthStatus.second_cellInstance.equalsIgnoreCase("wcdma")) {
                    Log.i(TAG,"check 2");
                    jsonArray.put(getSecondSimThreeGNetworkParams());

                }  else if(mView_HealthStatus.second_cellInstance.equalsIgnoreCase("gsm")){
                    Log.i(TAG,"check 3");
                    jsonArray.put(getSecondSimGSMNetworkParams());
                } else {
                    Log.i(TAG,"check 4");
                    jsonArray.put(getLTEParams());

                }
                Log.i(TAG,"check 5");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("MCC", txt_mccval.getText() + "");
                jsonObject.put("MNC", txt_mncval.getText() + "");
                jsonObject.put("type", txt_typeval.getText() + "");
                Log.i(TAG,"check 6");
                if (listenService.gps.getLocation() != null) {
                    jsonObject.put("gps_accuracy", txt_gpsaccuracyval.getText() + "");
                    jsonObject.put("altitude", txt_altitudeval.getText() + "");
                } else {
                    jsonObject.put("gps_accuracy", "NA");
                    jsonObject.put("altitude", "NA");
                }
                Log.i(TAG,"check 7");
                jsonObject.put("uplink_speed_in_kbps", txt_ulval.getText() + "");
                jsonObject.put("downlink_speed_in_kbps", txt_dlval.getText() + "");
                jsonObject.put("data", txt_dataval.getText() + "");
                jsonObject.put("mode", txt_datamode.getText() + "");

                Log.i(TAG,"check 8");
                jsonObject.put("Network_params", jsonArray);
                JSONArray jsonArray1 = new JSONArray();
                jsonArray1.put(jsonObject);
                Log.i(TAG,"check 9");
                RequestResponse.sendEvent(jsonArray1, AllInOneAsyncTaskForNetwork.AsyncTaskPurpose.NETWORK_MONITOR_INFO_EVT_SECOND_SIM, "second_sim_network_monitor");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }




    }

    public void useHandlerForScreenRefresh() {
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, refreshFrequency);
    }

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {

            if(dontRefreshScreen == false)
            {
                updateLayout();
                mHandler.postDelayed(mRunnable, refreshFrequency);
            }
        }
    };


    private void updateLayout() {
        txt_mccval.setText(String.valueOf(mView_HealthStatus.second_Mcc));
        txt_mncval.setText(String.valueOf(mView_HealthStatus.second_Mnc));
        txt_operator.setText("DATA Operator : " + mView_HealthStatus.OperatorName);
        if (mView_HealthStatus.timeSeriesServingCellDataArray != null && mView_HealthStatus.timeSeriesServingCellDataArray.size() > 0) {
            long servetime = System.currentTimeMillis() - mView_HealthStatus.timeSeriesServingCellDataArray.get(mView_HealthStatus.timeSeriesServingCellDataArray.size() - 1).captureTime;
            servetime = servetime / 1000;
            txt_serving.setText("Serving since :" + servetime + " sec");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date resultdate = new Date(System.currentTimeMillis());
        //  Date resultdate = new Date(mView_HealthStatus.timeSeriesServingCellDataArray.get(i).captureTime);

        String displaydate = sdf.format(resultdate);
        timeval.setText(displaydate);


        long totalRxBytes = TrafficStats.getTotalRxBytes();
        long totalTxBytes = TrafficStats.getTotalTxBytes();

        if (lastTxBytes == 0)
            lastTxBytes = totalTxBytes;
        if (lastRxBytes == 0)
            lastRxBytes = totalRxBytes;

        long curr = System.currentTimeMillis();
        long gap = curr - lastTimestamp;
        float gapInSecs = (float) gap / 1000.0f;
        // if(gapInSecs == 0)
        //    gapInSecs = 1;

        long ulspeed = 0;
        long dlspeed = 0;
        if (gapInSecs >= 1) {
            float kb =
                    ulspeed = (long) (((totalTxBytes - lastTxBytes) * 8) / (1024 * gapInSecs));
            long totalulspeed=(long) ((totalTxBytes  * 8) / (1024 * gapInSecs));
            long lastul=(long) ((lastTxBytes * 8) / (1024 * gapInSecs));
            dlspeed = (long) (((totalRxBytes - lastRxBytes) * 8) / (1024 * gapInSecs));
            lastTxBytes = totalTxBytes;
            lastRxBytes = totalRxBytes;
            lastTimestamp = curr;

            txt_ulval.setText(ulspeed + " kbps");
            txt_dlval.setText(dlspeed + " kbps");
            if (ulspeed == 0 && dlspeed == 0)
                txt_idleval.setText("IDLE");
            else
                txt_idleval.setText("DATA");

        }


        if (listenService.gps != null && listenService.gps.canGetLocation()) {
            System.out.println("gsp val  "+listenService.gps +"boolean "+listenService.gps.canGetLocation());
            txt_lonval.setText(String.format("%.6f", listenService.gps.getLongitude()) + "");
            txt_latval.setText(String.format("%.6f", listenService.gps.getLatitude()) + "");
            txt_altitudeval.setText(String.format("%.2f", listenService.gps.getAltitude()) + "m");
            System.out.println("speed value"+listenService.gps.getSpeed());
            double speed = 3.6 * listenService.gps.getSpeed();


            txt_speedval.setText(String.format("%.1f", speed) + "kmph");
            if (listenService.gps.getLocation() != null)
                txt_gpsaccuracyval.setText(listenService.gps.getLocation().getAccuracy() + "m");
        }


        if(mView_HealthStatus.second_SimOperator!=null) {
            txt_operator.setText("Sim Operator : " + mView_HealthStatus.second_SimOperator);
        }
        if(mView_HealthStatus.second_cellInstance!=null)
        {
            noinfo.setVisibility(View.GONE);
            cellinfo.setVisibility(View.VISIBLE);
        if(mView_HealthStatus.second_cellInstance.equalsIgnoreCase("lte")) {
            {

                txt_ta.setVisibility(View.VISIBLE);
                txt_taval.setVisibility(View.VISIBLE);
                txt_cqival.setVisibility(View.VISIBLE);
                txt_cqi.setVisibility(View.VISIBLE);
                txt_rssi.setVisibility(View.VISIBLE);
                txt_rssival.setVisibility(View.VISIBLE);
                txt_psc.setVisibility(View.VISIBLE);
                txt_pscval.setVisibility(View.VISIBLE);
                int cid1 = mView_HealthStatus.second_Cid & 0xff;



                cellidval.setText(String.valueOf(mView_HealthStatus.second_Cid));


                txt_rssi.setText("RSSI");
                txt_rssival.setText(MyPhoneStateListener.getLTERSSI());
                txt_snrval.setText(String.valueOf(mView_HealthStatus.second_snr));
                txt_snr.setText("SINR");


                txt_rnc.setText("eNB");
                if (mView_HealthStatus.lteENB == null || Integer.parseInt(mView_HealthStatus.lteENB) == Integer.MAX_VALUE) {
                    txt_rncval.setText(" 0 ");
                } else
                    txt_rncval.setText(mView_HealthStatus.second_ENB);


                txt_rsrp.setText("RSRP");
                txt_rsrpval.setText(String.valueOf(mView_HealthStatus.second_Rsrp));


                txt_lac.setText("TAC");
                txt_lacval.setText(String.valueOf(mView_HealthStatus.second_tac));

                txt_arfcn.setText("EARFCN");
                txt_arfcnval.setText(String.valueOf(mView_HealthStatus.second_earfcn));

                txt_psc.setText("PCI");
                txt_pscval.setText(String.valueOf(mView_HealthStatus.second_pci));

                txt_taval.setText(String.valueOf(mView_HealthStatus.second_ta));
                txt_cqival.setText(String.valueOf(mView_HealthStatus.second_Cqi));


            }
        }
        else if(mView_HealthStatus.second_cellInstance.equalsIgnoreCase("wcdma"))
            {
                txt_rssi.setVisibility(View.VISIBLE);
                txt_rssival.setVisibility(View.VISIBLE);
                txt_ta.setVisibility(View.GONE);
                txt_cqival.setVisibility(View.VISIBLE);
                txt_cqi.setVisibility(View.VISIBLE);
                txt_psc.setVisibility(View.VISIBLE);
                txt_pscval.setVisibility(View.VISIBLE);
                txt_ta.setVisibility(View.GONE);
                txt_taval.setVisibility(View.GONE);
                l4_41.setVisibility(View.VISIBLE);


                txt_rnc.setText("NodeB Id");
                if(mView_HealthStatus.second_ecno_3G!=null) {
                    txt_rncval.setText(mView_HealthStatus.second_NodeBID_3G);
                }
                else
                {
                    txt_rncval.setText("0");
                }

                txt_rsrp.setText("RSCP");
                txt_rsrpval.setText(String.valueOf(mView_HealthStatus.second_rscp_3G));

                txt_arfcn.setText("Uarfcn");
                txt_arfcnval.setText(String.valueOf(mView_HealthStatus.second_uarfcn_3G));

                txt_rssi.setText("RSSI");
                txt_rssival.setText(String.valueOf(MyPhoneStateListener.getRxLev()) );


                txt_psc.setText("PSC");
                txt_pscval.setText(String.valueOf(mView_HealthStatus.second_Psc_3g));



                txt_lac.setText("LAC");
                txt_lacval.setText(String.valueOf(mView_HealthStatus.second_lac_3G));
                txt_snr.setText("Ec/no");
                 cellidval.setText(String.valueOf(mView_HealthStatus.second_cid_3G));

                    int rscp = mView_HealthStatus.second_rscp_3G;

                    int ecno = rscp - MyPhoneStateListener.getRxLev();
                    txt_snrval.setText(String.valueOf(ecno));






            }
        else if(mView_HealthStatus.second_cellInstance.equalsIgnoreCase("gsm"))
        {
            {
                txt_cqival.setVisibility(View.GONE);
                txt_cqi.setVisibility(View.GONE);

                txt_rssi.setVisibility(View.GONE);
                txt_rssival.setVisibility(View.GONE);
                txt_ta.setVisibility(View.VISIBLE);
                txt_taval.setVisibility(View.VISIBLE);

                txt_ta.setText("TA");
                txt_taval.setText(String.valueOf(mView_HealthStatus.secong_gsmTa));

                txt_psc.setVisibility(View.GONE);
                txt_pscval.setVisibility(View.GONE);


                txt_arfcn.setText("ARFCN");
                txt_arfcnval.setText(String.valueOf(mView_HealthStatus.second_arfcn));


                txt_rnc.setText("Site Id");

                txt_rsrp.setText("RXLEV");
               txt_rsrpval.setText(String.valueOf(mView_HealthStatus.second_rxLev));


                txt_snr.setText("RXQUAL");
                txt_snrval.setText(String.valueOf(mView_HealthStatus.second_rxqual));




                txt_lac.setText("LAC");
                cellidval.setText(String.valueOf(mView_HealthStatus.second_gsmCid));

                txt_lacval.setText(String.valueOf(mView_HealthStatus.second_gsmLac));


                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) txt_rsrp.getLayoutParams();
                params.weight = 1.2f;
                txt_rsrp.setLayoutParams(params);

                LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) txt_rsrpval.getLayoutParams();
                params.weight = .8f;
                txt_rsrpval.setLayoutParams(params);
                //showParams();


                //r4.setVisibility(View.GONE);
            }
        }

        }
        else
        {
            noinfo.setVisibility(View.VISIBLE);
            cellinfo.setVisibility(View.GONE);
        }
    }

    private void init() {
        layoutFor4g=(TableLayout)v.findViewById(R.id.layoutFor4gParams);
        cellinfo=(LinearLayout)v.findViewById(R.id.cellinfo);
        noinfo=(LinearLayout)v.findViewById(R.id.no_info);


        txt_operator = (TextView) v.findViewById(R.id.txt_operator);

        txt_mccval = (TextView) v.findViewById(R.id.txt_mccval);
        txt_mncval = (TextView) v.findViewById(R.id.txt_mncval);
        txt_lac = (TextView) v.findViewById(R.id.txt_lac);
        txt_lacval = (TextView) v.findViewById(R.id.txt_lacval);
        txt_typeval = (TextView) v.findViewById(R.id.txt_typeval);

        txt_rnc = (TextView) v.findViewById(R.id.txt_rnc);
        txt_rncval = (TextView) v.findViewById(R.id.txt_rncval);
        txt_cellid=(TextView)v.findViewById(R.id.txt_cellid);
        txt_cellidval = (TextView) v.findViewById(R.id.txt_cellidval);


        //labels
        //txt_echo = (TextView) v.findViewById(R.id.txt_echo);
        // txt_rnc = (TextView) findViewById(R.id.txt_rnc);
        txt_rssi = (TextView) v.findViewById(R.id.txt_rssi);
        txt_psc = (TextView) v.findViewById(R.id.txt_psc);
        txt_pscval =(TextView) v.findViewById(R.id.txt_pscval);
        txt_rssival = (TextView) v.findViewById(R.id.txt_rssival);
        //  txt_echoval = (TextView) v.findViewById(R.id.txt_echoval);
        txt_snrval = (TextView) v.findViewById(R.id.txt_snrval);
        txt_snr =  (TextView) v.findViewById(R.id.txt_snr);
        txt_lonval = (TextView) v.findViewById(R.id.txt_lonval);
        txt_latval = (TextView) v.findViewById(R.id.txt_latval);
        txt_speedval = (TextView) v.findViewById(R.id.txt_speedval);
        txt_gpsaccuracyval = (TextView) v.findViewById(R.id.txt_gpsaccuracyval);
        txt_heightval = (TextView) v.findViewById(R.id.txt_heightval);
        txt_altitudeval = (TextView) v.findViewById(R.id.txt_altitudeval);
        txt_groundval = (TextView) v.findViewById(R.id.txt_groundval);
        txt_ulval = (TextView) v.findViewById(R.id.txt_ulval);
        txt_dlval = (TextView) v.findViewById(R.id.txt_dlval);
        txt_datamode=(TextView)v.findViewById(R.id.txt_data);
        txt_dataval = (TextView) v.findViewById(R.id.txt_dataval);
        txt_idleval = (TextView) v.findViewById(R.id.txt_idleval);
        txt_serving = (TextView) v.findViewById(R.id.txt_serving);


        //tbl_layout = (TableLayout) v.findViewById(R.id.tbl_layout);

        r4 = (RelativeLayout)v.findViewById(R.id.r4);
        l4_21 = v.findViewById(R.id.l4_21);
        l4_22 = v.findViewById(R.id.l4_22);
        //l4_31 = (TableRow)v.findViewById(R.id.l4_31);
        l4_41 = (TableLayout) v.findViewById(R.id.l4_41);
        l2 = v.findViewById(R.id.l2);
        txt_rsrp = (TextView) v.findViewById(R.id.txt_rsrp);
        txt_rsrpval = (TextView) v.findViewById(R.id.txt_rsrpval);
        txt_arfcnval = (TextView) v.findViewById(R.id.txt_arfcnval);
        txt_ta=(TextView)v.findViewById(R.id.txt_ta);
        txt_taval = (TextView) v.findViewById(R.id.txt_taval);
        txt_cqi=(TextView)v.findViewById(R.id.txt_cqi);
        txt_cqival = (TextView) v.findViewById(R.id.txt_cqival);

        //txt_pci = (TextView) v.findViewById(R.id.txt_pci);
        txt_arfcn = (TextView) v.findViewById(R.id.txt_arfcn);
        //tableLayout=(TableLayout)v.findViewById(R.id.tableLayout);
        timeval=(TextView) v.findViewById(R.id.textView1val);
        lac=(TextView)v.findViewById(R.id.textView2);
        lacval=(TextView)v.findViewById(R.id.textView2val);
        node=(TextView)v.findViewById(R.id.textView3);
        nodeval=(TextView)v.findViewById(R.id.textView3val);
        cellid=(TextView)v.findViewById(R.id.textView4);
        cellidval=(TextView)v.findViewById(R.id.textView4val);
        ci=(TextView)v.findViewById(R.id.textView5);
        cival=(TextView)v.findViewById(R.id.textView5val);
        arfcn=(TextView)v.findViewById(R.id.textView6);
        arfcnval=(TextView)v.findViewById(R.id.textView6val);
        level=(TextView)v.findViewById(R.id.textView7);
        levelval=(TextView)v.findViewById(R.id.textView7val);
        qual=(TextView)v.findViewById(R.id.textView8);
        qualval=(TextView)v.findViewById(R.id.textView8val);
        typeval=(TextView)v.findViewById(R.id.textView9val);
        servcval=(TextView)v.findViewById(R.id.textView10val);
        psc=(TextView)v.findViewById(R.id.textView15);
        psc_val=(TextView)v.findViewById(R.id.textView15val);
        threegparams=(TableLayout)v.findViewById(R.id.tableLayout6);
        uarfcn=(TextView)v.findViewById(R.id.textView12);
        uarfcn_val=(TextView)v.findViewById(R.id.textView12val);
        psc=(TextView)v.findViewById(R.id.textView15);
        psc_val=(TextView)v.findViewById(R.id.textView15val);
        rscp=(TextView)v.findViewById(R.id.textView13);
        rscp_val=(TextView)v.findViewById(R.id.textView13val);
        node_val=(TextView)v.findViewById(R.id.textView11val);
        ec_val=(TextView)v.findViewById(R.id.textView14val);


    }

}
