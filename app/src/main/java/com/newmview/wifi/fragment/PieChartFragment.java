package com.newmview.wifi.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.mview.airtel.R;
import com.dashboard.activity.GraphDetailsActivity;
import com.newmview.wifi.helper.CustomMarkerView;
import com.newmview.wifi.helper.Interfaces;
import com.newmview.wifi.other.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class PieChartFragment extends Fragment implements Interfaces.ChangeColorListener{
    View view;
    private PieChart pieChart;
    private ArrayList<Entry> entries;
    private ArrayList<String> labels;
    private Bundle bundle;
    private ArrayList<HashMap<String, String>> pieDataColumns;
    private ArrayList<HashMap<String,String>> pieDataNamesList;
    private PieDataSet pieDataSet;
    private PieData data;
    private int[] colors;
    private ArrayList<Integer> randomColorsList;
    private ArrayList<String> legendVals;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.piechartonly,container,false);
        init();
        getRandomColors();
        displayPieData();
        return view;
    }

    private void getRandomColors() {

        randomColorsList=new ArrayList<>();
        if(pieDataColumns!=null && pieDataColumns.size()>0) {
            colors = new int[pieDataColumns.size()];
            for (int j = 0; j < pieDataColumns.size(); j++) {
                int color=Utils.getSomeRandomColor();
                colors[j] = color;
                randomColorsList.add(color);
            }
            // pieDataSet.setColors(colors);
            if(getActivity()!=null)
            {
                if(getActivity() instanceof GraphDetailsActivity)
                {
                    ((GraphDetailsActivity)getActivity()).sendColorsList(randomColorsList);
                }
            }

        }
    }

    private void init() {
       pieChart=view.findViewById(R.id.piechart);
        bundle=getArguments();
        if(bundle!=null)
        {
            pieDataColumns=(ArrayList<HashMap<String, String>>) bundle.getSerializable("pieDataColums");
            pieDataNamesList=(ArrayList<HashMap<String, String>>) bundle.getSerializable("pieDataNamesList");

        }

    }

    private void displayPieData() {


            try {
                //List<String> legendVals= Utils.separateCommaSeparatedString(CommonUtil.columnWiseList.get(0).get("name"));
                legendVals=new ArrayList<>();
                entries = new ArrayList<>();
                labels = new ArrayList<String>();
                System.out.println("pieDtaCol "+pieDataColumns);
                if (pieDataColumns != null && pieDataColumns.size() > 0) {

                    for (int i = 0; i < pieDataColumns.size(); i++) {
                        String name=pieDataColumns.get(i).get("name");
                        legendVals.add(name);
                        entries.add((new Entry(Float.valueOf(pieDataColumns.get(i).get("y")), i)));
                        labels.add(name);
                    }
                    pieDataSet = new PieDataSet(entries, "");
                    pieDataSet.setSliceSpace(2);
                    setColors();

                    pieDataSet.setSelectionShift(5);
                    data = new PieData(labels, pieDataSet);
                    chartCustomization();
                    pieChart.setData(data);
                    pieChart.invalidate();
                    CustomMarkerView mv = new CustomMarkerView(getActivity(), R.layout.custom_marker_view_layout, labels);
                    pieChart.setMarkerView(mv);
                    pieChart.invalidate();


                }
                //   }



            }




            catch (Exception e)
            {
                e.printStackTrace();
                System.out.println("Exception is displaying graph "+e.toString());
            }

        }


    private void chartCustomization() {
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(0f);
        Legend legend =pieChart.getLegend();
        legend.setWordWrapEnabled(true);
        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.setDescription("");
        pieChart.setDrawSliceText(true);
        pieChart.animateXY(1000,1000);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setHoleColorTransparent(true);
        pieChart.setHoleRadius(0);

        pieChart.setTransparentCircleRadius(0);
      //  pieChart.setHovered(true);
       // pieChart.setRotationEnabled(true);
        //pieChart.setRotationAngle(0);
        //pieChart.setDragDecelerationEnabled(true);
      //

       // pieChart.getLegend().setEnabled(false);
    }

    private void setColors() {

        colors = new int[entries.size()];
        for (int j = 0; j < entries.size(); j++) {
            colors[j]=   Utils.getSomeRandomColor();
        }
        pieDataSet.setColors(colors);
    }


    @Override
    public void changeColor(int color, String legend) {
        int index=-1;
        if(colors!=null && colors.length>0)
        {
            for(int i=0;i<legendVals.size();i++)
            {


                String val=legendVals.get(i);
                if(Utils.checkifavailable(val))
                {
                    if(Utils.checkifavailable(legend))
                    {
                        if(val.equalsIgnoreCase(legend))
                        {
                            index=i;
                            break;
                        }
                    }
                }
            }

            if(index>-1) {
                if(randomColorsList!=null && randomColorsList.size()>0) {
                    randomColorsList.set(index, color);
                    System.out.println("random list "+randomColorsList);
                    colors = new int[randomColorsList.size()];
                    for (int i = 0; i < randomColorsList.size(); i++) {
                        colors[i] = randomColorsList.get(i);
                    }


                    displayPieData();
                }

            }
        }
    }
}
