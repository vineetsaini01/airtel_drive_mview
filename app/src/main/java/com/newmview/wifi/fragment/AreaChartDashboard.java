package com.newmview.wifi.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.mview.airtel.R;
import com.newmview.wifi.helper.CommonUtil;
import com.newmview.wifi.helper.CustomMarkerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Random;

/**
 * Created by functionapps on 3/12/2019.
 */

public class AreaChartDashboard extends Fragment {
    private View view;
    private Context context;
    private FragmentManager fragmentManager;
    private LineChart lineChart;
    private ArrayList<HashMap<String,String>> localgraphdata;
    private LineDataSet lineDataSet;
    private int k;
    private String chartName="";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.linedashboard,container,false);
        init();
        displayAreaGraph();
       // displayLineChart();
        return view;
    }

    private void displayAreaGraph() {

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
                    lineDataSet = new LineDataSet(entries, chartName);
                    chartCustomization();
                    LineData data = new LineData(labels, lineDataSet);
                    lineChart.setData(data);
                    lineChart.invalidate();
                    CustomMarkerView mv = new CustomMarkerView(getActivity(), R.layout.custom_marker_view_layout, labels);

                    lineChart.setMarkerView(mv);
                    lineChart.invalidate();
                }
            }

    }


    private void displayLineChart() {
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



                lineDataSet = new LineDataSet(entries, chartName);
                chartCustomization();
                LineData data = new LineData(labels, lineDataSet);
                lineChart.setData(data);
                lineChart.invalidate();
                CustomMarkerView mv = new CustomMarkerView(getActivity(), R.layout.custom_marker_view_layout, labels);
                lineChart.setMarkerView(mv);
                lineChart.invalidate();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Exception is displaying graph "+e.toString());
        }

    }

    private void chartCustomization() {

        lineChart.setDescription("");
        Legend legend = lineChart.getLegend();
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        legend.setWordWrapEnabled(true);
        legend.setTextSize(16);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLimitLinesBehindData(false);
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        lineDataSet.setColor(color);
        lineDataSet.setLineWidth(2f);
        lineDataSet.setFillAlpha(80);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillColor(color);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setDrawValues(false);
       lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.animateXY(2000, 2000);
    }

    private void init() {
        Bundle bundle=this.getArguments();
        chartName=bundle.getString("chartName");
        lineChart=view.findViewById(R.id.linechrt);
    }
}
