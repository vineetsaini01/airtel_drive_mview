package com.newmview.wifi.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.newmview.wifi.MyPhoneStateListener;
import com.mview.airtel.R;
import com.newmview.wifi.mView_HealthStatus;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by functionapps on 1/24/2019.
 */

public class ChartsFragment extends Fragment {
    private View view;
    private FragmentManager fragmentManager;
    private LinearLayout layoutFor4g, layoutFor3g, layoutFor2g;
    private TextView title;
    private LineChart ecno,rscp,throughput;
    private float nodeid;
    private LineChart rxqual,rxlevel;
    private float rxquality;
    private float rxLevel;
    private LineChart rsrp_chart, snr_chart, throughput_chart;
    private float rsr;
    private float ecNo;
    private long lastTxBytes,lastRxBytes,lastTimestamp;
    ArrayList<Long> dlSpeed;
    ArrayList<Long> ulSpeed;
    ArrayList<ArrayList<Long>> throughputList;
    ArrayList<String> timeStamp;
    private int refreshFrequency=1000;
    private float dl;
    ArrayList<Entry> entries;
    ArrayList<String> labels;
    private LineDataSet dataset;
    private LineChart throughputuplink;
    private LineChart throughputuplink_chart;
    private ProgressDialog progressDialog;


    @Override
    public void onResume() {
        super.onResume();
        useHandler();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        System.out.println("on create view of charts fragment called..");
        view=inflater.inflate(R.layout.charts_new,container,false);

        init();
       handlerForGettingThroughputData();
        showProgress();
        if(mView_HealthStatus.iCurrentNetworkState==0)
        {
            dismissProgress();
            showAlert();
        }

        return view;

    }

    private void dismissProgress() {
        if(progressDialog!=null && progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }

    }

    private void showProgress() {
        progressDialog=new ProgressDialog(getActivity(),ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setMessage(Constants.LOADING);
       progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
       progressDialog.setCancelable(false);
       progressDialog.show();
    }

    public void handlerForGettingThroughputData() {
        mHandler = new Handler();
        mHandler.postDelayed(nRunnable, refreshFrequency);
    }

    private Runnable nRunnable = new Runnable() {

        @Override
        public void run() {

   try {

    getdata();
        }
    catch (Exception e)
       {
      e.printStackTrace();
          }
     finally
   {
    mHandler.postDelayed(nRunnable, refreshFrequency);
    }



        }
    };

    private void getdata() {
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
            long totalulspeed = (long) ((totalTxBytes * 8) / (1024 * gapInSecs));
            long lastul = (long) ((lastTxBytes * 8) / (1024 * gapInSecs));
            dlspeed = (long) (((totalRxBytes - lastRxBytes) * 8) / (1024 * gapInSecs));
          
            lastTxBytes = totalTxBytes;
            lastRxBytes = totalRxBytes;
            lastTimestamp = curr;
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

            String displaydate = sdf.format(curr);
            int currSize = timeStamp.size();
            if(currSize  == mView_HealthStatus.MaxPeriodicDataToSaveInDB && currSize >= 1)
            {
               dlSpeed.remove(0);
               ulSpeed.remove(0);
               timeStamp.remove(0);
            }
            dlSpeed.add(dlspeed);
            ulSpeed.add(ulspeed);
            timeStamp.add(displaydate);
            
            for(int i=0;i<dlSpeed.size();i++) {

                System.out.println("dl list values " + dlSpeed.get(i) +"  "+"ul list values "+ulSpeed.get(i) +" time stamp "+ displaydate);
//                System.out.println("throughput list dl "+throughputList.get(0).get(i)+ "throughut list ul "+throughputList.get(1).get(i));

            }

        }
    }

    Handler mHandler;

    private void useHandler() {
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable,  1000);
    }



    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {

                try {
                    dismissProgress();
                   getParamsData(MyPhoneStateListener.getNetworkType());


                } catch (Exception e) {
                    System.out.println("Exception is " + e.toString());
                    e.printStackTrace();
                }
                finally {
                    mHandler.postDelayed(mRunnable, mView_HealthStatus.updateDashboardUIIntervalInSeconds * 1000);
                }

        }
    };


    private void getParamsData(int ntwrk) {
        title.setText(String.format("%sG", String.valueOf(ntwrk)));
        switch (ntwrk)
        {
            case 4:
                display4GCharts();
                break;
            case 3:
                display3GCharts();
                break;
            case 2:
                display2GCharts();
                break;

        }

    }

    private void showAlert() {
        AlertDialog.Builder alert=new AlertDialog.Builder(getActivity(),R.style.AlertDialogTheme);
        alert.setMessage(Constants.NO_NTWRK);
        alert.setTitle("Charts");
        alert.setCancelable(true);
        alert.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int id) {
                        dialog.cancel();
                    }
                });
        alert.show();
    }

    private void display2GCharts() {
        layoutFor2g.setVisibility(View.VISIBLE);
        layoutFor3g.setVisibility(View.GONE);
        layoutFor4g.setVisibility(View.GONE);
        getDataForRxLevel();
        getDataForRxQual();
    }

    private void display3GCharts() {
        layoutFor3g.setVisibility(View.VISIBLE);
        layoutFor4g.setVisibility(View.GONE);
        layoutFor2g.setVisibility(View.GONE);
        getDataForRscp();
        getDataForEcno();
        getDataForDownlinkThroughput(throughput);
        getDataForUplinkThroughput(throughputuplink);
    }

    private void display4GCharts() {
        layoutFor4g.setVisibility(View.VISIBLE);
        layoutFor3g.setVisibility(View.GONE);
        layoutFor2g.setVisibility(View.GONE);
        getDataForRsrp();
        getDataForSnr();
        getDataForDownlinkThroughput(throughput_chart);
        getDataForUplinkThroughput(throughputuplink_chart);
    }

    private void getDataForUplinkThroughput(LineChart lineChart) {
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList <String> labels = new ArrayList<String>();
        System.out.println("timestamp size "+timeStamp.size() );
        for (int i = 0; i < timeStamp.size(); i++) {
            long ulspeed=ulSpeed.get(i);
            entries.add(new Entry(ulspeed, i));
            String displaydate = timeStamp.get(i);
            labels.add(displaydate);

        }
        dataset = new LineDataSet(entries, "Uplink(in kbps)");

        LineData data = new LineData(labels, dataset);
        lineChart.setData(data);
        chartCustomization(lineChart, dataset);


    }
    //-------------------------------------3G----------------------------------------------------

    private void getDataForEcno()
    {


        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();

        if (mView_HealthStatus.lteparams != null) {

            for (int i = 0; i < mView_HealthStatus.lteparams.size(); i++) {
                float ecno = Float.parseFloat(mView_HealthStatus.lteparams.get(i).Ecno);
                entries.add(new Entry(ecno, i));

                String displaydate = (mView_HealthStatus.lteparams.get(i).currenttime);
                labels.add(displaydate);
            }
        } else {
            entries.add(new Entry(ecNo, 0));
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Date resultdate = new Date(System.currentTimeMillis());
            String displaydate = sdf.format(resultdate);
            labels.add(displaydate);

        }


        LineDataSet dataset = new LineDataSet(entries, "EcNo");
        LineData data = new LineData(labels, dataset);
        ecno.setData(data);
        chartCustomization(ecno, dataset);


    }
    private void getDataForRscp()

    {


        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();

        if (mView_HealthStatus.lteparams != null) {

            for (int i = 0; i < mView_HealthStatus.lteparams.size(); i++) {
                float rscp = Float.parseFloat(mView_HealthStatus.lteparams.get(i).Rscp);
                entries.add(new Entry(rscp, i));

                String displaydate = (mView_HealthStatus.lteparams.get(i).currenttime);
                labels.add(displaydate);
            }
        } else {
            entries.add(new Entry(nodeid, 0));
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Date resultdate = new Date(System.currentTimeMillis());
            String displaydate = sdf.format(resultdate);
            labels.add(displaydate);

        }


        LineDataSet dataset = new LineDataSet(entries, "Rscp");
        LineData data = new LineData(labels, dataset);
        rscp.setData(data);
        chartCustomization(rscp, dataset);


    }




    private void getDataForDownlinkThroughput(LineChart lineChart) {
        entries = new ArrayList<>();
        labels = new ArrayList<String>();
        System.out.println("timestamp size "+timeStamp.size() );
            for (int i = 0; i < timeStamp.size(); i++) {

                long dlspeed = dlSpeed.get(i);
                entries.add(new Entry(dlspeed, i));
                String displaydate = timeStamp.get(i);
                labels.add(displaydate);
            }

        dataset = new LineDataSet(entries, "Downlink(in kbps)");

        LineData data = new LineData(labels, dataset);
        lineChart.setData(data);
        chartCustomization(lineChart, dataset);


    }
    //-----------------------------------------------4G------------------------------------------------

    private void getDataForSnr() {


        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();

        if (mView_HealthStatus.lteparams != null) {

            for (int i = 0; i < mView_HealthStatus.lteparams.size(); i++) {
String snrString=mView_HealthStatus.lteparams.get(i).Snr;
if(Utils.checkifavailable(snrString))//check added by Sonal on 08-02-2021
{
    float snr = Float.parseFloat(snrString);
    entries.add(new Entry(snr, i));

    String displaydate = (mView_HealthStatus.lteparams.get(i).currenttime);
    labels.add(displaydate);
}

            }
        } else {
            entries.add(new Entry(rsr, 0));
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Date resultdate = new Date(System.currentTimeMillis());
            String displaydate = sdf.format(resultdate);
            labels.add(displaydate);

        }


        LineDataSet dataset = new LineDataSet(entries, Constants.Snr);
        System.out.println("dataset val of snr" + dataset);
        LineData data = new LineData(labels, dataset);
        snr_chart.setData(data);
        chartCustomization(snr_chart, dataset);


    }


    private void getDataForRsrp() {


        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();

        if (mView_HealthStatus.lteparams != null) {

            for (int i = 0; i < mView_HealthStatus.lteparams.size(); i++) {
                System.out.println("rsrp in chart "+mView_HealthStatus.lteparams.get(i).Rsrp +"size is "+mView_HealthStatus.lteparams.size());
                float rsrp = Float.parseFloat(mView_HealthStatus.lteparams.get(i).Rsrp);
                entries.add(new Entry(rsrp, i));

                String displaydate = (mView_HealthStatus.lteparams.get(i).currenttime);
                labels.add(displaydate);
            }
        } else {
            entries.add(new Entry(rsr, 0));
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Date resultdate = new Date(System.currentTimeMillis());
            String displaydate = sdf.format(resultdate);
            labels.add(displaydate);
        }


        LineDataSet dataset = new LineDataSet(entries, Constants.Rsrp);

        LineData data = new LineData(labels, dataset);
        rsrp_chart.setData(data);
        chartCustomization(rsrp_chart, dataset);


    }


    //--------------------------------------2g--------------------------------------------------------------/
    private void getDataForRxLevel() {
        {


            ArrayList<Entry> entries = new ArrayList<>();
            ArrayList<String> labels = new ArrayList<String>();

            if (mView_HealthStatus.lteparams != null) {

                for (int i = 0; i < mView_HealthStatus.lteparams.size(); i++) {

                    float rxlevel = Float.parseFloat(mView_HealthStatus.lteparams.get(i).RxLevel);
                    entries.add(new Entry(rxlevel, i));

                    String displaydate = (mView_HealthStatus.lteparams.get(i).currenttime);
                    labels.add(displaydate);
                }
            } else {
                entries.add(new Entry(rxLevel, 0));
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                Date resultdate = new Date(System.currentTimeMillis());
                String displaydate = sdf.format(resultdate);
                labels.add(displaydate);

            }


            LineDataSet dataset = new LineDataSet(entries, "RxLevel");
            LineData data = new LineData(labels, dataset);
            rxlevel.setData(data);
            chartCustomization(rxlevel, dataset);


        }
    }

    private void getDataForRxQual()

    {


        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();

        if (mView_HealthStatus.lteparams != null) {

            for (int i = 0; i < mView_HealthStatus.lteparams.size(); i++) {
                float rxqual = Float.parseFloat(mView_HealthStatus.lteparams.get(i).RXQual);
                entries.add(new Entry(rxqual, i));

                String displaydate = (mView_HealthStatus.lteparams.get(i).currenttime);
                labels.add(displaydate);
            }
        } else {
            entries.add(new Entry(rxquality, 0));
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Date resultdate = new Date(System.currentTimeMillis());
            String displaydate = sdf.format(resultdate);
            labels.add(displaydate);

        }


        LineDataSet dataset = new LineDataSet(entries, "RxQual");
        LineData data = new LineData(labels, dataset);
        rxqual.setData(data);
        chartCustomization(rxqual, dataset);


    }



    private void chartCustomization(LineChart chart, LineDataSet dataSet) {
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLimitLinesBehindData(false);
        chart.setDescription("");
        chart.invalidate();
        dataSet.setColor(getResources().getColor(R.color.textColor));
        dataSet.setValueTextSize(12f);
       /* Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        int color2 = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        dataSet.setColor(color);*/

    }


    private void init() {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_new);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("charts fragmnet navigate");
                //  Toast.makeText(getActivity(), "chartsfragmnet", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            }
        });
        layoutFor4g=(LinearLayout)view.findViewById(R.id.charts_4g);
        layoutFor3g=(LinearLayout)view.findViewById(R.id.charts_3g);
        layoutFor2g=(LinearLayout)view.findViewById(R.id.charts_2g);
        title=(TextView)view.findViewById(R.id.title);
        rscp=(LineChart)view.findViewById(R.id.rscp_3gChart);
        ecno=(LineChart)view.findViewById(R.id.ecno_3gChart);
        throughput=(LineChart)view.findViewById(R.id.throughput_3gChart);
        throughputuplink=(LineChart)view.findViewById(R.id.throughputUplink_3gChart);
        rsrp_chart=(LineChart)view.findViewById(R.id.rsrp_4gChart);
        snr_chart=(LineChart)view.findViewById(R.id.snr_4gChart);
        throughput_chart=(LineChart)view.findViewById(R.id.throughput_4gChart);
        throughputuplink_chart=(LineChart)view.findViewById(R.id.throughputuplink_4gChart);
        rxqual=(LineChart)view.findViewById(R.id.rxqual_2gChart);
        rxlevel=(LineChart)view.findViewById(R.id.rxlevel_2gChart);
        dlSpeed=new ArrayList<>();
        ulSpeed=new ArrayList<>();
        timeStamp=new ArrayList<>();
        throughputList=new ArrayList<>();

        }

    @Override
    public void onPause() {
        super.onPause();
        try
        {
            mHandler.removeCallbacks(mRunnable);
        }
        catch (Exception e)
        {
e.printStackTrace();
        }
    }
}
