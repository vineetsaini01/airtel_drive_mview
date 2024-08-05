package com.newmview.wifi.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.mview.airtel.R;
import com.dashboard.activity.GraphDetailsActivity;
import com.newmview.wifi.helper.CustomMarkerView;
import com.newmview.wifi.helper.Interfaces;
import com.newmview.wifi.other.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.dashboard.activity.GraphDetailsActivity.trend;


public class MultiYAxesGraph extends Fragment implements Interfaces.ChangeColorListener{
    View view;
    private CombinedChart combinedChart;
    private ArrayList<String> namesList;
    ArrayList<ArrayList<HashMap<String,String>>> dataList;
    private ArrayList<Integer> randomColorsList=new ArrayList<>();
    private TableLayout table_layout;
    private ScrollView scrollView;
    private ArrayList<String> legendVals=new ArrayList<>();
    private CombinedData combinedData;
    private ArrayList<String> labels;
    private ArrayList<BarDataSet> dataSets=new ArrayList<>();
    private ArrayList<LineDataSet> lineDS=new ArrayList<>();
    private HashMap<String, String> selectedVal;
    private int selectedindex=-1;
    LineDataSet lineDataSet=null;
    private boolean complete_view;
    private ToggleButton toggle_view_btn,toggle_landscape_btn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       
       view=inflater.inflate(R.layout.multiy_axes,container,false);
try {
    init();

    getExtras();
    if(dataList!=null)
    {
        if(dataList.size()>0)
        {

            addRandomColors();
            getLegendS();
            displayGraphData();
            showCustomLegendsLL();
        }
    }

}
catch (Exception e)
{
    e.printStackTrace();
}


        return view;
    }

    private void getLegendS() {
        if(namesList!=null) {
            legendVals=new ArrayList<>();
            legendVals.addAll(namesList);
        }
    }

    private void init() {

        combinedChart=view.findViewById(R.id.combinedChart);
        table_layout=view.findViewById(R.id.table_layout);
        scrollView=view.findViewById(R.id.scrollView);
        toggle_view_btn=getActivity().findViewById(R.id.view_toggle);
        toggle_landscape_btn=getActivity().findViewById(R.id.view_toggle_landscape);
        if(toggle_view_btn.isChecked() || toggle_landscape_btn.isChecked())
        {
            complete_view=true;
        }
        scrollView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {


                // Disallow the touch request for parent scroll on touch of  child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    private void displayGraphData() {
getXAxisValues();
        combinedData=new CombinedData(labels);

        combinedData.setData(generateBarChartData());
        combinedData.setData(generatelineDataSetData());

        CustomMarkerView mv = new CustomMarkerView(getActivity(), R.layout.custom_marker_view_layout,labels);
        combinedChart.setMarkerView(mv);
        combinedChart.setData(combinedData);
        chartCustomization();
        combinedChart.invalidate();
        if(!complete_view) {
            if (combinedChart.getXValCount() > 15)
                Utils.showToast(getActivity(), "Kindly slide  to view data!!");
        }
        else
        {
            Utils.showToast(getActivity(), "Kindly zoom  to view data!!");
        }
    }

    private BarData generateBarChartData() {
        System.out.println("size of labels "+labels.size());
        return new BarData(labels, getBarDataSet());
    }

    private List<BarDataSet> getBarDataSet() {
       BarDataSet barDataSet=null;
     ArrayList<HashMap<String,String>> barList=  dataList.get(0);
       ArrayList<BarEntry> entries=new ArrayList<>();
       if(barList!=null) {
           if(barList.size()>0) {
               for (int i = 0; i < barList.size(); i++) {
                   String y = barList.get(i).get("y");
                   if (Utils.checkifavailable(y)) {
                       float value = Float.parseFloat(y);
                       entries.add(new BarEntry(value, i));
System.out.println("value of bar "+value);

                   }
               }

               barDataSet = new BarDataSet(entries, legendVals.get(0));
               barDataSet.setColor(randomColorsList.get(0));
               barDataSet.setDrawValues(false);

               dataSets.add(barDataSet);

           }
       }
       System.out.println("size of datset "+dataSets);
     return dataSets ;
    }

    private LineData generatelineDataSetData() {
LineData lineData=new LineData(labels, getLineDataSet());;

        return lineData;
    }

    private List<LineDataSet> getLineDataSet() {


        if(dataList.size()>1) {
            for (int j = 1; j <= 2; j++)
            {
            ArrayList<HashMap<String, String>> barList = dataList.get(j);
            ArrayList<Entry> entries = new ArrayList<>();
            if (barList != null) {
                if (barList.size() > 0) {
                    for (int i = 0; i < barList.size(); i++) {
                        String y = barList.get(i).get("y");
                        if (Utils.checkifavailable(y)) {
                            float value = Float.parseFloat(y);
                            entries.add(new Entry(value, i));

System.out.println("value of line "+value);

                        }
                    }
                    System.out.println("linedata SET SIZE of eneteries "+entries +"legnds "+legendVals);
                    lineDataSet = new LineDataSet(entries, legendVals.get(j));
                    lineDataSet.setCircleColor(randomColorsList.get(j));
                    lineDataSet.setColor(randomColorsList.get(j));
                    lineDataSet.setLineWidth(2f);
                    lineDataSet.setDrawCircleHole(false);
                    lineDataSet.setValueTextSize(9f);

                    lineDS.add(lineDataSet);
                    if(lineDataSet!=null) {
                        if (j == 1) {

                            lineDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
                        } else {
                            lineDataSet.enableDashedLine(2, 3, 0);
                            lineDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
                        }
                    }

                    if(legendVals.size()==2)
                    {
                        break;
                    }
                }
            }

        }

        }
        return lineDS ;
    }


    private void chartCustomization() {

        combinedChart.setDescription("");
        combinedChart.setDoubleTapToZoomEnabled(false);
        combinedChart.animateXY(2000, 2000);
if(!complete_view) {
    combinedChart.setVisibleXRangeMaximum(15);
    combinedChart.setVisibleXRangeMinimum(15);
}
        combinedChart.setExtraLeftOffset(18);
        combinedChart.setExtraRightOffset(18);
        combinedChart.setDefaultFocusHighlightEnabled(false);
        Legend legend = combinedChart.getLegend();
        legend.setEnabled(false);
        XAxis xAxis = combinedChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawLimitLinesBehindData(true);
        xAxis.setTextSize(11f);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setLabelRotationAngle(10f);
        xAxis.setTextColor(getResources().getColor(R.color.black));

    }
    private void generateLabelsAccordingToTrending(String label_name) {



            System.out.println("label name is "+label_name +" trend is"+trend  +"groupiing type "+GraphDetailsActivity.group);
            if(Utils.checkifavailable(label_name)) {

                if (Utils.checkifavailable(GraphDetailsActivity.group)) {
                    if(GraphDetailsActivity.group.equalsIgnoreCase("temporal")) {
                        if (Utils.checkifavailable(trend)) {
                            if (trend.equalsIgnoreCase("hourly")||
                                    trend.equalsIgnoreCase("1 minute") ||
                                    trend.equalsIgnoreCase("5 minute")||
                                    trend.equalsIgnoreCase("10 minute") ||
                                    trend.equalsIgnoreCase("15 minute") ||
                                    trend.equalsIgnoreCase("30 minute")) {
                                String label_without_seconds=Utils.parseDateTimeFormatWithoutSeconds(label_name);
                                labels.add(label_without_seconds);
                                System.out.println("label on adding is "+label_without_seconds);

                            }
                            else if(trend.equalsIgnoreCase("daily"))
                            {
                                String label_without_seconds=Utils.parseDateTimeFormatWithoutSeconds2(label_name);
                                labels.add(label_without_seconds);
                                System.out.println("label on adding 1 is "+label_without_seconds);
                            }
                            else if(trend.equalsIgnoreCase("weekly")||trend.equalsIgnoreCase("monthly") ||
                                    trend.equalsIgnoreCase("yearly"))
                            {
                                if(label_name.contains("(")) {
                                    label_name = label_name.substring(0, label_name.indexOf("("));
                                    labels.add(label_name);
                                }
                                else
                                {
                                    labels.add(label_name);
                                }
                                System.out.println("label on adding 2 is "+label_name);
                            }
                            else
                            {
                                labels.add(label_name);
                                System.out.println("label on adding 3 is "+label_name);
                            }
                        } else {
                            labels.add(label_name);
                            System.out.println("label on adding 4 is "+label_name);
                        }
                    }
                    else
                    {
                        labels.add(label_name);
                        System.out.println("label on adding 5 is "+label_name);
                    }
                }
                else
                {
                    labels.add(label_name);
                }
            }


           /* if(selectedVal!=null)
            {
                System.out.println("selected value check null "+selectedVal);
                if( (selectedVal.size()>0) && (selectedVal.get("name").equalsIgnoreCase(localgraphdata.get(i).get("name")))) {

                    selectedindex = i;
                }
            }


*/

    }

    private void getXAxisValues()
    {


    ArrayList<HashMap<String,String>> paramsList;
        labels=new ArrayList<>();
    paramsList=dataList.get(0);
    if(paramsList!=null)
    {
        if(paramsList.size()>0)
        {
            for(int j=0;j<paramsList.size();j++)
            {
                String xAxisName=paramsList.get(j).get("name");

                generateLabelsAccordingToTrending(xAxisName);
            }
        }
    
}


    }
    private void showCustomLegendsLL() {
        // table_layout.setStretchAllColumns(true);

        table_layout.removeAllViews();
        for(int i=0;i<legendVals.size();i++) {
            TextView legend_color=new TextView(getActivity());
            TableRow.LayoutParams layoutParams=new TableRow.LayoutParams(50, 50);
            layoutParams.topMargin=5;
            layoutParams.rightMargin=5;
            TableRow.LayoutParams layoutParams_tv=new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 50);
            legend_color.setLayoutParams(layoutParams);
            TextView legend_Tv=new TextView(getActivity());
            legend_Tv.setTextSize(13);
            legend_Tv.setTextColor(getActivity().getResources().getColor(R.color.black));
            legend_Tv.setLayoutParams(layoutParams_tv);
            TableRow tr = new TableRow(getActivity());

            tr.addView(legend_color);

            legend_color.setBackgroundColor(randomColorsList.get(i));

            tr.addView(legend_Tv);
            legend_Tv.setText(legendVals.get(i));

            table_layout.addView(tr);
            table_layout.requestLayout();
        }
    }

    private void addRandomColors() {

        if(dataList!=null && dataList.size()>0) {
            for (int a = 0; a < dataList.size(); a++) {
                randomColorsList.add(Utils.getSomeRandomColor());
            }
            if(getActivity()!=null)
            {
                if(getActivity() instanceof GraphDetailsActivity)
                {
                    ((GraphDetailsActivity)getActivity()).sendColorsList(randomColorsList);
                }
            }
        }
    }


    private void getExtras() {
        Bundle bundle=getArguments();
        if(bundle!=null) {
            namesList = (ArrayList<String>) bundle.getSerializable("namesList");
            dataList= (ArrayList<ArrayList<HashMap<String, String>>>) bundle.getSerializable("dataList");

        }
    }

    @Override
    public void changeColor(int color, String legend) {
        int index = -1;
        if (randomColorsList != null && randomColorsList.size() > 0) {
            for (int i = 0; i < legendVals.size(); i++) {


                String val = legendVals.get(i);
                if (Utils.checkifavailable(val)) {
                    if (Utils.checkifavailable(legend)) {
                        if (val.equalsIgnoreCase(legend)) {
                            index = i;
                            break;
                        }
                    }
                }
            }
            randomColorsList.set(index, color);

            getLegendS();
            displayGraphData();
            showCustomLegendsLL();
        }
    }
    }

