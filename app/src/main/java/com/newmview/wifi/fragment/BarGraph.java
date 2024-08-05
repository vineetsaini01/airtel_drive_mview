package com.newmview.wifi.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.mview.airtel.R;
import com.dashboard.activity.GraphDetailsActivity;
import com.newmview.wifi.helper.CustomMarkerView;
import com.newmview.wifi.helper.Interfaces;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.dashboard.activity.GraphDetailsActivity.trend;

public class BarGraph extends Fragment implements View.OnClickListener, Interfaces.ChangeColorListener{

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
    private ArrayList<BarDataSet> dataSets=new ArrayList<>();
    private ArrayList<BarEntry> entries;
    private ArrayList<String> labels;
    private BarData data;
    private ArrayList<HashMap<String, String>> columnWiseList;
    private ArrayList<ArrayList<HashMap<String, String>>> graphFinalList;
    private Bundle bundle;
    private List<String> legendVals=new ArrayList<>();
    private ArrayList<HashMap<String, String>> graphList;
    private ArrayList <Integer>colorsList;
    private int selectedindex=-1;
    private int finalColor;
    private HashMap<String, String> selectedVal;
    private Button reset_btn;
    private ArrayList<Integer> randomColorsList=new ArrayList<>();
    private float firstNonZeroValue;
    private boolean valueSet;
    private int firstNonZeroValueindex;
    private TableLayout table_layout;
    private LinearLayout legends_ll;
    private ScrollView scrollView;
    private ToggleButton toggle_view_btn;
    private boolean complete_view;
    private ToggleButton toggle_landscape_btn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.trendingcharts,container,false);
        try {
            init();

            addRandomColors();

            displayGraphData();
            chartCustomization();
            showCustomLegendsLL();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //   showCustomLegendsLayout();

        //setHasOptionsMenu(true);
        return view;
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

    private void showCustomLegendsLayout() {
        table_layout.setStretchAllColumns(true);

        for(int i=0;i<legendVals.size();i=i+2) {
            TextView legend_color=new TextView(getActivity());
            TextView legend_color1=new TextView(getActivity());
            TextView legend_Tv=new TextView(getActivity());
            TextView legend_Tv2=new TextView(getActivity());
            legend_Tv.setTextSize(10);
            legend_Tv2.setTextSize(10);
            TableRow tr = new TableRow(getActivity());
            tr.addView(legend_color);
            legend_color.setBackgroundColor(randomColorsList.get(i));

            tr.addView(legend_Tv);
            legend_Tv.setText(legendVals.get(i));
            if(legendVals.size()>1) {
                if(legendVals.size()%2==0) {
                    tr.addView(legend_color1);
                    legend_color1.setBackgroundColor(randomColorsList.get(i + 1));
                }


                tr.addView(legend_Tv2);
                legend_Tv.setText(legendVals.get(i + 1));
            }

            table_layout.addView(tr);
            table_layout.requestLayout();
        }
    }


    private void addRandomColors() {

        if(graphFinalList!=null && graphFinalList.size()>0) {
            for (int a = 0; a < graphFinalList.size(); a++) {
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


    private void displayGraphData() {
        legendVals=new ArrayList<>();
        dataSets=new ArrayList<>();
        try {
            if(graphList!=null && graphList.size()>0) {
                //legendVals = Utils.separateCommaSeparatedString(graphList.get(0).get("name"));
            }

            if(graphFinalList!=null && graphFinalList.size()>0) {
                for(int a=0;a<graphFinalList.size();a++) {
                    legendVals.add(String.valueOf(Utils.separateCommaSeparatedString(graphList.get(a).get("name"))));
                    localgraphdata=new ArrayList<>();
                    localgraphdata.addAll(graphFinalList.get(a));

                    entries = new ArrayList<>();
                    labels = new ArrayList<String>();
                    if (localgraphdata != null) {
                        if (localgraphdata.size() > 0) {

                            for (int i = 0; i < localgraphdata.size(); i++) {
                                float value= Float.parseFloat(localgraphdata.get(i).get("y"));
                                if(value!=0) {
                                    if(!valueSet) {
                                        firstNonZeroValueindex = i;
                                        firstNonZeroValue=value;
                                        valueSet=true;
                                    }
                                }

                                entries.add(new BarEntry(value, i));


                            }

                            generateLabelsAccordingToTrending();

                            colorsList=new ArrayList<>();
                            int color=Utils.getSomeRandomColor();
                            if(color!= R.color.app_theme)
                            {
                                finalColor=color;
                            }
                            else
                            {
                                finalColor=Utils.getSomeRandomColor();

                            }



                            if(legendVals!=null && legendVals.size()>0) {
                                barDataSet = new BarDataSet(entries, legendVals.get(a));
                                barDataSet.setDrawValues(false);
                            }
                            else
                            {
                                barDataSet = new BarDataSet(entries, "");
                                barDataSet.setDrawValues(false);
                            }

                            System.out.println("selected index "+selectedindex +" colors list "+colorsList);



                            int redcolor= Color.rgb(139,0,0);
                            if(selectedindex!=-1) {
                                for (int i = 0; i < localgraphdata.size(); i++) {

                                    if (i == selectedindex) {


                                        colorsList.add(i,redcolor);
                                    } else {
                                        colorsList.add(i,finalColor);
                                    }
                                }
                                barChart.setScaleMinima(2f,30f);
                               /* barChart.moveViewToX(selectedindex);
                                barChart.moveViewToY(Float.parseFloat(selectedVal.get("y")),YAxis.AxisDependency.LEFT);*/
                                System.out.println("selected value "+selectedVal.get("y") +"slected index "+selectedindex);
                                int index=selectedindex+1;
                                barChart.moveViewTo(index,Float.parseFloat(selectedVal.get("y")),YAxis.AxisDependency.LEFT);
                                reset_btn.setVisibility(View.VISIBLE);
                                System.out.println("colors list "+colorsList);

                            }
                            else
                            {
                                System.out.println("time non zro index  "+firstNonZeroValue);

                                reset_btn.setVisibility(View.GONE);
                            }

                            int ran_color=randomColorsList.get(a);
                            if(selectedindex!=-1)
                            {
                                if((colorsList!=null) && colorsList.size()>0) {
                                    barDataSet.setColors(colorsList);
                                }
                                else
                                {
                                    barDataSet.setColor(ran_color);
                                }

//barChart.zoomIn();
                                // barChart.setScaleMinima(1.5f,1f);
                            }
                            else
                            {
                                barDataSet.setColor(ran_color);
                            }

                            barDataSet.setBarSpacePercent(25f);
                            dataSets.add(barDataSet);



                        }
                    }
                }




                data = new BarData(labels, dataSets);

                barChart.setData(data);



                CustomMarkerView mv = new CustomMarkerView(getActivity(), R.layout.custom_marker_view_layout,labels);
                barChart.setMarkerView(mv);
                if(!complete_view) {
                    if (barChart.getXValCount() > 15)
                        Utils.showLongToast(getActivity(), "Kindly slide  to view data!!");
                }
                else
                {
                    Utils.showLongToast(getActivity(), "Kindly zoom  to view data!!");
                }
                //barChart.moveViewTo(firstNonZeroValueindex,firstNonZeroValue,YAxis.AxisDependency.LEFT);
            }
            else if(Constants.ERROR==3)
            {
                Utils.showToast(getActivity(),Constants.SERVER_ERROR);
                Constants.ERROR=0;
            }
            else
            {
                Toast.makeText(getActivity(), "No data is available!!", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Exception is displaying graph "+e.toString());
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void chartCustomization() {
        barChart.setDescription("");
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.animateXY(2000, 2000);
        //new added for fixing the visible barwidth
        if(!complete_view) {
            barChart.setVisibleXRangeMaximum(15);
            barChart.setVisibleXRangeMinimum(15);
        }
        barChart.setExtraLeftOffset(18);
        barChart.setExtraRightOffset(18);
        barChart.setDefaultFocusHighlightEnabled(false);
        /*barChart.getAxisLeft().setStartAtZero(false);
        barChart.getAxisRight().setStartAtZero(false);*/
        Legend legend = barChart.getLegend();
        legend.setEnabled(false);
        data.setGroupSpace(40f);
        data.setValueTextSize(12f);
       /* legend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        legend.setWordWrapEnabled(true);
        legend.setTextSize(18);*/
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawLimitLinesBehindData(true);
        xAxis.setTextSize(11f);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setLabelRotationAngle(10f);
        //new  xAxis.setAvoidFirstLastClipping(true);
        xAxis.setTextColor(getResources().getColor(R.color.black));
        YAxis yAxis=barChart.getAxisLeft();
        //  barDataSet.setDrawValues(true);
        //barDataSet.setBarSpacePercent(40f);




        //barDataSet.setBarSpacePercent(50f);

    /*    if(entries.size()==1)
            barDataSet.setBarSpacePercent(70f);*/


        // barDataSet.setValueTextSize(11f);
        barChart.invalidate();


    }

    private void generateLabelsAccordingToTrending() {

        for (int i = 0; i < localgraphdata.size(); i++) {
            String label_name=localgraphdata.get(i).get("name");
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


            if(selectedVal!=null)
            {
                System.out.println("selected value check null "+selectedVal);
                if( (selectedVal.size()>0) && (selectedVal.get("name").equalsIgnoreCase(localgraphdata.get(i).get("name")))) {

                    selectedindex = i;
                }
            }


        }

    }


    private void init() {
        args=new Bundle();
        barChart=view.findViewById(R.id.barchrt);
        table_layout=view.findViewById(R.id.table_layout);
        scrollView=view.findViewById(R.id.scrollView);
        toggle_view_btn=getActivity().findViewById(R.id.view_toggle);
        toggle_landscape_btn=getActivity().findViewById(R.id.view_toggle_landscape);
        if(toggle_view_btn.isChecked()|| toggle_landscape_btn.isChecked())
        {
            complete_view=true;
        }
        //toggle_view_btn.setOnCheckedChangeListener(this);
        scrollView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {


                // Disallow the touch request for parent scroll on touch of  child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        //legends_ll=view.findViewById(R.id.legends_ll);
        bundle=getArguments();
        if(getArguments()!=null)
        {
            columnWiseList= (ArrayList<HashMap<String, String>>) bundle.getSerializable("columnWiseList");
            graphFinalList= (ArrayList<ArrayList<HashMap<String, String>>>) bundle.getSerializable("graphFinalList");
            graphList= (ArrayList<HashMap<String, String>>) bundle.getSerializable("graphList");
            selectedVal=(HashMap<String,String>)bundle.getSerializable("selectedVal");
            System.out.println("selected val is "+selectedVal);

        }

        reset_btn=view.findViewById(R.id.reset_btn);
        reset_btn.setOnClickListener(this);



    }


    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.reset_btn:
                barChart.setScaleMinima(1.0f,1.0f);
                barChart.moveViewToX(0);

                //barChart.zoomOut();
                barChart.fitScreen();
                barChart.invalidate();
                reset_btn.setVisibility(View.GONE);
                break;

        }

    }

    @Override
    public void changeColor(int color, String legend) {
        int index=-1;
        if(randomColorsList!=null && randomColorsList.size()>0)
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
            randomColorsList.set(index,color);

            displayGraphData();
            showCustomLegendsLL();
        }
    }

}
