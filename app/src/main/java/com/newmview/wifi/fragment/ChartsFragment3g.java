package com.newmview.wifi.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.mview.airtel.R;
import com.newmview.wifi.mView_HealthStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * Created by functionapps on 1/23/2019.
 */

public class ChartsFragment3g extends Fragment {
    private View view;
    private LineChart ecno,rscp,throughput;
    private float nodeid;
    private float ecNo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.chartsfor3g,container,false);
        init();
        useHandler();
        return view;
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

                    refreshData();
                    mHandler.postDelayed(mRunnable, mView_HealthStatus.updateDashboardUIIntervalInSeconds * 1000);
                } catch (Exception e) {
                    System.out.println("Exception is " + e.toString());
                    e.printStackTrace();
                }
            }
        }
    };

    private void refreshData() {
        //Toast.makeText(getActivity(), "values getting refereshed...", Toast.LENGTH_SHORT).show();

        getDataForRscp();
        getDataForEcno();
        getDataForThroughput();
    }

    private void getDataForThroughput() {
    }
private void getDataForEcno()
{
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

    private void chartCustomization(LineChart chart, LineDataSet dataSet) {
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLimitLinesBehindData(false);
        chart.setDescription("");
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        int color2 = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        dataSet.setColor(color);
        chart.invalidate();
    }

    private void init() {
        rscp=(LineChart)view.findViewById(R.id.rscp_3gChart);
        ecno=(LineChart)view.findViewById(R.id.ecno_3gChart);
        throughput=(LineChart)view.findViewById(R.id.throughput_3gChart);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_new);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }
}
