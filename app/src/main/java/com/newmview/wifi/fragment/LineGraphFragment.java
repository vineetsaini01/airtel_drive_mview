package com.newmview.wifi.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.mview.airtel.R;
import com.newmview.wifi.helper.CustomMarkerView;

import java.util.ArrayList;
import java.util.HashMap;

public class LineGraphFragment  extends Fragment {
    private View view;
    private ArrayList<HashMap<String,String>> localgraphdata;
    private LineDataSet lineDataSet;
    private LineChart lineChart;
    private String graphName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.linedashboard, container, false);
        init();
        displayLineGraph();
        return view;

    }

    private void init() {

        Bundle bundle=this.getArguments();
        graphName=bundle.getString("graphName");
        lineChart=view.findViewById(R.id.linechrt);
    }

    private void displayLineGraph() {
        localgraphdata=new ArrayList<>();



        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList <String> labels = new ArrayList<String>();

        lineDataSet = new LineDataSet(entries, graphName);
        // chartCustomization();
        LineData data = new LineData(labels, lineDataSet);
        lineChart.setData(data);
        lineChart.invalidate();
        CustomMarkerView mv = new CustomMarkerView(getActivity(), R.layout.custom_marker_view_layout, labels);
        lineChart.setMarkerView(mv);
        lineChart.invalidate();
/*
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
                lineDataSet = new LineDataSet(entries, graphName);
               // chartCustomization();
                LineData data = new LineData(labels, lineDataSet);
                lineChart.setData(data);
                lineChart.invalidate();
                CustomMarkerView mv = new CustomMarkerView(getActivity(), R.layout.custom_marker_view_layout);
                lineChart.setMarkerView(mv);
                lineChart.invalidate();
            }
        }
*/
    }

}
