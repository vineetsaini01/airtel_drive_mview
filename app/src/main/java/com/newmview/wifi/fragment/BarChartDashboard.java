package com.newmview.wifi.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.RadioGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
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

public class BarChartDashboard extends Fragment  {
    private View view;
    private ProgressDialog progressDialog;
    private Dialog dialog;
    private Context context;
    private BarChart barChart;
    private ArrayList<String> xaxisvals;
    private ArrayList<BarDataSet> dataSet=new ArrayList<>();
    private RadioGroup trendradiogroup;
    private ArrayList<HashMap<String, String>> localgraphdata;
    private ArrayList<BarEntry> valueSet1;
    private BarDataSet barDataSet;
    private int k;
    private FragmentManager fragmentManager;
    private String chartName="";
    private Bundle args;
    private ArrayList<Integer> randomColorsList=new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.trendingcharts,container,false);
        init();
        displayGraphData();
        //setHasOptionsMenu(true);
        return view;
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





            ArrayList<BarEntry> entries = new ArrayList<>();
            ArrayList <String> labels = new ArrayList<String>();
            if(localgraphdata.size()>0) {

                for (int i = 0; i < localgraphdata.size(); i++) {
                    if (!localgraphdata.get(i).get("name").toLowerCase().contains("date")) {
                        entries.add(new BarEntry(Float.valueOf(localgraphdata.get(i).get("value")), k));

                        k++;
                    }
                }


                    for (int i = 0; i <localgraphdata.size(); i++) {
                        if (localgraphdata.get(i).get("name").toLowerCase().contains("date")) {

                            labels.add(localgraphdata.get(i).get("value"));

                        }

                    }



                barDataSet = new BarDataSet(entries, chartName);
                chartCustomization();
                BarData data = new BarData(labels, barDataSet);
                barChart.setData(data);
                barChart.invalidate();
                CustomMarkerView mv = new CustomMarkerView(getActivity(), R.layout.custom_marker_view_layout, labels);
                barChart.setMarkerView(mv);
                barChart.invalidate();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Exception is displaying graph "+e.toString());
        }

    }

    private void chartCustomization() {
        barChart.setDescription("");
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.animateXY(2000, 2000);
        Legend legend = barChart.getLegend();
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        legend.setWordWrapEnabled(true);
        legend.setTextSize(16);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLimitLinesBehindData(false);
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        barDataSet.setColor(color);
    }



    private void init() {
        Bundle bundle=this.getArguments();
         chartName=bundle.getString("chartName");
        args=new Bundle();
        barChart=view.findViewById(R.id.barchrt);



    }





}
