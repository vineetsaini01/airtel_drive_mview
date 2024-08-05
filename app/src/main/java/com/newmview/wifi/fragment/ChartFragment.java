package com.newmview.wifi.fragment;

import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.mview.airtel.R;
import com.newmview.wifi.mView_HealthStatus;
import com.newmview.wifi.other.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by functionapps on 1/13/2019.
 */

public class ChartFragment extends Fragment {
    private View view;
    private ActionBar actionbar;
    private LineChart rsrp_chart, snr_chart, throughput_chart;
    private LinearLayout rsrp_layout;
    private LineData data;
    private ArrayList<String> xAxis = new ArrayList<>();
    private ArrayList<LineDataSet> linedataset = new ArrayList<>();
    private ArrayList<Entry> entries = new ArrayList<>();
    private float rsrp_val;
    private String rsrp;
    private int i;
    private long resulttime;
    /*private LineDataSet dataset;*/
    private float batlevl = 0;
    private float rsr = 0f;
    private long lastTxBytes,lastRxBytes;
    private long lastTimestamp;
    private LineChart downlinkchart;
    public  ArrayList<String> uplink;
    private long totalRxBytes,totalTxBytes;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.charts, container, false);
        init();
       // data();
        totalRxBytes = TrafficStats.getTotalRxBytes();
        totalTxBytes = TrafficStats.getTotalTxBytes();

        if (lastTxBytes == 0)
            lastTxBytes = totalTxBytes;
        if (lastRxBytes == 0)
            lastRxBytes = totalRxBytes;
        useHandler();
        rsrp_chart.invalidate();
        snr_chart.invalidate();
        throughput_chart.invalidate();
        return view;

    }

    private void data() {
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

            int currSize = uplink.size();

            if(currSize  == mView_HealthStatus.MaxPeriodicDataToSaveInDB && currSize >= 1)
            {
             uplink.remove(0);
            }
            uplink.add(String.valueOf(ulspeed));

            dlspeed = (long) (((totalRxBytes - lastRxBytes) * 8) / (1024 * gapInSecs));
            long totalulspeed=(long) ((totalTxBytes  * 8) / (1024 * gapInSecs));
            long lastul=(long) ((lastTxBytes * 8) / (1024 * gapInSecs));
            System.out.println("total ulspeed "+totalulspeed + "last ulspeed "+lastul +"current ulspeed" +ulspeed);
            lastTxBytes = totalTxBytes;
            lastRxBytes = totalRxBytes;
        }
        float rsrp = lastTxBytes;


    }

    Handler mHandler;

    private void useHandler() {
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, mView_HealthStatus.updateDashboardUIIntervalInSeconds * 1000);
    }


    private boolean dontRefreshScreen;
    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            if (dontRefreshScreen == false) {
                try {
                    // updateValues();
                    refreshData();
                 //   mHandler.postDelayed(mRunnable, mView_HealthStatus.updateDashboardUIIntervalInSeconds * 1000);

                } catch (Exception e) {
                    System.out.println("Exception is " + e.toString());
                    e.printStackTrace();
                }
                finally {
                    mHandler.postDelayed(mRunnable, mView_HealthStatus.updateDashboardUIIntervalInSeconds * 1000);
                }
            }
        }
    };

    private void refreshData() {
        Toast.makeText(getActivity(), "values getting refreshed...", Toast.LENGTH_SHORT).show();
        getDataForRsrp();
        getDataForSnr();
    //    getDataForThroughput();
       /* getDataForThroughputDownlink();*/


    }

    private void getDataForThroughputDownlink() {

        {



            ArrayList<Entry> entries = new ArrayList<>();
            ArrayList<String> labels = new ArrayList<String>();
            long totalRxBytes = TrafficStats.getTotalRxBytes();
            long totalTxBytes = TrafficStats.getTotalTxBytes();




            if (mView_HealthStatus.lteparams != null) {



                for (int i = 0; i < mView_HealthStatus.lteparams.size(); i++) {
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
                        dlspeed = (long) (((totalRxBytes - lastRxBytes) * 8) / (1024 * gapInSecs));
                        lastTxBytes = totalTxBytes;
                        lastRxBytes = totalRxBytes;
                    }
                    float rsrp = lastTxBytes;
                    System.out.println("dlspeed is "+dlspeed);

                    entries.add(new Entry(dlspeed, i));
                    //entries.add(new Entry(dlspeed,i));

                    String displaydate = (mView_HealthStatus.lteparams.get(i).currenttime);
                    labels.add(displaydate);
                }
            } else {
                entries.add(new Entry(rsr, 0));
                String displaydate = (mView_HealthStatus.lteparams.get(i).currenttime);
                labels.add(displaydate);

            }


            LineDataSet dataset = new LineDataSet(entries, "Throughput Downlink");
            LineData data = new LineData(labels, dataset);
            downlinkchart.setData(data);
            chartCustomization(downlinkchart, dataset);





        }
    }

    private void getDataForThroughput()

        {


            ArrayList<Entry> entries = new ArrayList<>();
            ArrayList<String> labels = new ArrayList<String>();



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

                int currSize = uplink.size();

                if(currSize  == mView_HealthStatus.MaxPeriodicDataToSaveInDB && currSize >= 1)
                {
                    uplink.remove(0);
                }
                uplink.add(String.valueOf(ulspeed));

                dlspeed = (long) (((totalRxBytes - lastRxBytes) * 8) / (1024 * gapInSecs));
                long totalulspeed=(long) ((totalTxBytes  * 8) / (1024 * gapInSecs));
                long lastul=(long) ((lastTxBytes * 8) / (1024 * gapInSecs));
                System.out.println("total ulspeed "+totalulspeed + "last ulspeed "+lastul +"current ulspeed" +ulspeed);
                lastTxBytes = totalTxBytes;
                lastRxBytes = totalRxBytes;
            }
            float rsrp = lastTxBytes;





                if (mView_HealthStatus.lteparams != null) {



                for (int i = 0; i < mView_HealthStatus.lteparams.size(); i++)
                {

System.out.println("uplink values "+uplink.get(i));
                    entries.add(new Entry(Float.valueOf(uplink.get(i)), i));
                    //entries.add(new Entry(dlspeed,i));

                    String displaydate = (mView_HealthStatus.lteparams.get(i).currenttime);
                    labels.add(displaydate);
                }
            } else {
                entries.add(new Entry(rsr, 0));
                String displaydate = (mView_HealthStatus.lteparams.get(i).currenttime);
                labels.add(displaydate);

            }


            LineDataSet dataset = new LineDataSet(entries, "Throughput Uplink");
            LineData data = new LineData(labels, dataset);
            throughput_chart.setData(data);
            chartCustomization(throughput_chart, dataset);





    }

    private void getDataForSnr() {


        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();

        if (mView_HealthStatus.lteparams != null) {

            for (int i = 0; i < mView_HealthStatus.lteparams.size(); i++) {
                if(mView_HealthStatus.lteparams.get(i).Snr!=null) {

                    float snr = Float.parseFloat(mView_HealthStatus.lteparams.get(i).Snr);
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
                if(mView_HealthStatus.lteparams.get(i).Rsrp!=null) {
                    float rsrp = Float.parseFloat(mView_HealthStatus.lteparams.get(i).Rsrp);
                    entries.add(new Entry(rsrp, i));

                    String displaydate = (mView_HealthStatus.lteparams.get(i).currenttime);
                    labels.add(displaydate);
                }
            }
        } else {
            entries.add(new Entry(rsr, 0));
            String displaydate = (mView_HealthStatus.lteparams.get(i).currenttime);
            labels.add(displaydate);

        }


        LineDataSet dataset = new LineDataSet(entries, Constants.Rsrp);
        LineData data = new LineData(labels, dataset);
        rsrp_chart.setData(data);
        chartCustomization(rsrp_chart, dataset);


    }

    private void chartCustomization(LineChart chart, LineDataSet dataSet) {
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLimitLinesBehindData(false);
        chart.setDescription("");
       /* Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        int color2 = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        dataSet.setColor(color);*/
        chart.invalidate();


    }

    private void init() {
        //setHasOptionsMenu(true);
        rsrp_chart=(LineChart)view.findViewById(R.id.rsrp_4gChart);
        snr_chart=(LineChart)view.findViewById(R.id.snr_4gChart);
        throughput_chart=(LineChart)view.findViewById(R.id.throughput_4gChart);
        downlinkchart=(LineChart)view.findViewById(R.id.throughputDownlink_4gChart);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_new);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.charts_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
