package com.newmview.wifi.fragment;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;


import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.mview.airtel.R;

import com.newmview.wifi.helper.CommonUtil;
import com.newmview.wifi.helper.CustomMarkerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Random;

/**
 * Created by functionapps on 2/25/2019.
 */

public class ScatterChartDashboard  extends Fragment{
    private View view;
    private ScatterChart scatterChart;
    private ArrayList<HashMap<String, String>> localgraphdata;
    private ArrayList<Entry> valueSet1;
    private ScatterDataSet scatterDataSet;
    private int k;
    private String chartName="";
    private Bundle args;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.scatterfragment,container,false);
        init();
        //displayGraphData();
        displayScatterGraphData();
        return view;
    }

    private void displayScatterGraphData() {
        localgraphdata=new ArrayList<>();
        localgraphdata.addAll(CommonUtil.drillrepdata);

        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList <String> labels = new ArrayList<String>();
        if(localgraphdata!=null) {
            if (localgraphdata.size() > 0) {

                for (int i = 0; i < localgraphdata.size(); i++) {
                    if (localgraphdata.get(i).get("rowcol").toLowerCase().contains("signalstrength")) {
                        entries.add(new Entry(Float.valueOf(localgraphdata.get(i).get("celldata")), k));

                        k++;
                    }
                }


                for (int i = 0; i < localgraphdata.size(); i++) {
                    if (localgraphdata.get(i).get("rowcol").toLowerCase().contains("date")) {

                        labels.add(localgraphdata.get(i).get("celldata"));

                    }

                }


                scatterDataSet = new ScatterDataSet(entries, chartName);
                chartCustomization();
                ScatterData data = new ScatterData(labels, scatterDataSet);
                scatterChart.setData(data);
                scatterChart.invalidate();
                CustomMarkerView mv = new CustomMarkerView(getActivity(), R.layout.custom_marker_view_layout, labels);
                scatterChart.setMarkerView(mv);
                scatterChart.invalidate();
            }
        }
    }


    private void displayGraphData() {
        try {


            ArrayList<String> paramlist = new ArrayList<>();
            LinkedHashSet<String> li = new LinkedHashSet<String>();
            localgraphdata=new ArrayList<>();
            localgraphdata.addAll(CommonUtil.graphdata);
            for (int i = 0; i < localgraphdata.size(); i++) {
                if(!localgraphdata.get(i).get("name").contains("date"))
                {
                    li.add(localgraphdata.get(i).get("name"));

                }
            }

            paramlist.addAll(li);
            System.out.println("params list is"+paramlist.toString());





            ArrayList<Entry> entries = new ArrayList<>();
            ArrayList <String> labels = new ArrayList<String>();
            if(localgraphdata.size()>0) {

                for (int i = 0; i < localgraphdata.size(); i++) {
                    if (!localgraphdata.get(i).get("name").toLowerCase().contains("date")) {
                        entries.add(new Entry(Float.valueOf(localgraphdata.get(i).get("value")), k));

                        k++;
                    }
                }


                for (int i = 0; i <localgraphdata.size(); i++) {
                    if (localgraphdata.get(i).get("name").toLowerCase().contains("date")) {

                        labels.add(localgraphdata.get(i).get("value"));

                    }

                }



                scatterDataSet = new ScatterDataSet(entries, chartName);
                chartCustomization();
               ScatterData data = new ScatterData(labels, scatterDataSet);
                scatterChart.setData(data);
                scatterChart.invalidate();
                CustomMarkerView mv = new CustomMarkerView(getActivity(), R.layout.custom_marker_view_layout, labels);
                scatterChart.setMarkerView(mv);
                scatterChart.invalidate();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Exception is displaying graph "+e.toString());
        }

    }

    private void chartCustomization() {
        scatterChart.setDescription("");
        scatterChart.setDoubleTapToZoomEnabled(false);
        scatterChart.animateXY(2000, 2000);
        Legend legend = scatterChart.getLegend();
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        legend.setWordWrapEnabled(true);
        legend.setTextSize(16);
        XAxis xAxis =scatterChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLimitLinesBehindData(false);
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        scatterDataSet.setColor(color);
    }



    private void init() {
        Bundle bundle=this.getArguments();
        chartName=bundle.getString("chartName");
        args=new Bundle();
        scatterChart=view.findViewById(R.id.scatterchrt);



    }








}
