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
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
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

public class LineGraph extends Fragment implements View.OnClickListener , Interfaces.ChangeColorListener {

    private View view;
    private Context context;
    private FragmentManager fragmentManager;
    private LineChart lineChart;
    private ArrayList<HashMap<String, String>> localgraphdata;
    private LineDataSet lineDataSet;
    private int k;
    private String chartName = "";
    private ArrayList<String> labels;
    private LineData data;
    private LineData lineData;
    private ArrayList<LineDataSet> dataSets = new ArrayList<>();
    private List<String> legendVals = new ArrayList<>();
    private Bundle bundle;
    private ArrayList<HashMap<String, String>> columnWiseList;
    private ArrayList<ArrayList<HashMap<String, String>>> graphFinalList;
    private ArrayList<HashMap<String, String>> graphList;
    private HashMap<String, String> selectedVal;
    private boolean changeColorFlag = true;
    private ArrayList<Integer> colorsList;
    private int selectedindex = -1;
    private int finalColor;
    private Button reset_btn;
    private ArrayList<Integer> randomColorsList = new ArrayList<>();
    private TableLayout table_layout;
    private ToggleButton toggle_view_btn;
    private boolean complete_view;
    private ToggleButton toggle_landscape_btn;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.linedashboard, container, false);
        init();
        try {
            addRandomColors();
            displayAreaGraph();
            showCustomLegendsLL();
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    private void displayAreaGraph() {
        dataSets=new ArrayList<>();
        legendVals=new ArrayList<>();
        //  if(graphList!=null && graphList.size()>0)
        // legendVals= Utils.separateCommaSeparatedString(graphList.get(0).get("name"));
        lineData = new LineData();
        if (graphFinalList != null && graphFinalList.size() > 0) {
            for (int a = 0; a < graphFinalList.size(); a++) {
                legendVals.add(String.valueOf(Utils.separateCommaSeparatedString(graphList.get(a).get("name"))));
                localgraphdata = new ArrayList<>();
                localgraphdata.addAll(graphFinalList.get(a));
                ArrayList<Entry> entries = new ArrayList<>();
                labels = new ArrayList<String>();
                if (localgraphdata != null) {
                    if (localgraphdata.size() > 0) {

                        for (int i = 0; i < localgraphdata.size(); i++) {

                            entries.add(new Entry(Float.valueOf(localgraphdata.get(i).get("y")), i));

                        }
                    }


                   /* for (int i = 0; i < localgraphdata.size(); i++) {

                        labels.add(localgraphdata.get(i).get("name"));

                    }
*/


                    generateLabelsAccordingToTrending();
/*
                    for (int i = 0; i < localgraphdata.size(); i++) {


                        labels.add(localgraphdata.get(i).get("name"));

                        if ((selectedVal != null) && (selectedVal.size() > 0) && (selectedVal.get("name").equalsIgnoreCase(localgraphdata.get(i).get("name")))) {

                            selectedindex = i;
                        }

                        if((selectedVal!=null)&&(selectedVal.get("name").equalsIgnoreCase(localgraphdata.get(i).get("name"))))
                        {

                            selectedindex=i;
                        }


                    }
*/
                    colorsList = new ArrayList<>();

                    int color = Utils.getSomeRandomColor();
                    if (color != R.color.app_theme) {
                        finalColor = color;
                    } else {
                        finalColor = Utils.getSomeRandomColor();

                    }


                    lineDataSet = new LineDataSet(entries, legendVals.get(a));
                    lineDataSet.setDrawValues(false);
                    lineDataSet.setDrawCubic(true);

                  /*  if(a%2==0)
                    {
                        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                    }
                    else
                    {
                        lineDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
                    }*/
                    System.out.println("selected index " + selectedindex);
                    int redcolor = Color.rgb(255, 0, 0);
                    if (selectedindex != -1) {
                        for (int i = 0; i < localgraphdata.size(); i++) {

                            if (i == selectedindex) {


                                colorsList.add(i, redcolor);
                            } else {
                                colorsList.add(i, finalColor);
                            }
                        }
                        //  lineChart.zoomOut();
                        System.out.println("colors list " + colorsList);

                        lineChart.setScaleMinima(2f, 1f);
                        lineChart.moveViewToX(selectedindex);
                        reset_btn.setVisibility(View.VISIBLE);

                    } else {
                        reset_btn.setVisibility(View.GONE);
                    }


                    setColorForEachDataSet(a);
                    dataSets.add(lineDataSet);

                }
            }

            for (int i = 0; i < dataSets.size(); i++) {
                System.out.println("dataset size is " + dataSets.size() + "dataset is " + dataSets.get(i));
            }
            data = new LineData(labels, dataSets);
            chartCustomization();
            lineChart.setData(data);
            if(!complete_view)
                lineChart.setVisibleXRangeMaximum(15);
            lineChart.invalidate();
            CustomMarkerView mv = new CustomMarkerView(getActivity(), R.layout.custom_marker_view_layout, labels);
            lineChart.setMarkerView(mv);
            lineChart.invalidate();
            //  lineChart.setVisibleXRangeMaximum(7);
            if(!complete_view)
            {
                if(lineChart.getXValCount()>15)
                    Utils.showLongToast(getActivity(),"Kindly slide and zoom to view data!!");}
            else
            {
                Utils.showLongToast(getActivity(),"Kindly zoom to view data!!");
            }

        } else if (Constants.ERROR == 3) {
            Utils.showToast(getActivity(), Constants.SERVER_ERROR);
            Constants.ERROR= 0;
        } else {
            Toast.makeText(getActivity(), "No data is available!!", Toast.LENGTH_LONG).show();
        }
    }

    private void setColorForEachDataSet(int index) {
        int color=randomColorsList.get(index);
        //  int color = Utils.getSomeRandomColor();
        lineDataSet.setColor(color);
        lineDataSet.setLineWidth(2f);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setValueTextSize(12f);
        lineDataSet.setCircleColor(color);
        if (selectedindex != -1) {
            if (colorsList != null && colorsList.size() > 0) {
                lineDataSet.setCircleColors(colorsList);
            }
        }
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

    private void chartCustomization() {

        lineChart.setDescription("");
        lineChart.setExtraRightOffset(19);
        lineChart.setExtraLeftOffset(19);
        Legend legend = lineChart.getLegend();
        legend.setEnabled(false);
       /* legend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        legend.setWordWrapEnabled(true);
        legend.setTextSize(16);*/
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawLimitLinesBehindData(true);
        xAxis.setTextSize(11f);
        //, xAxis.setAvoidFirstLastClipping(true);
        xAxis.setLabelRotationAngle(10f);
        lineChart.setDoubleTapToZoomEnabled(false);

        //lineChart.setVisibleXRangeMinimum(graphFinalList.size() - 1);
        lineChart.animateXY(2000, 2000);

    }

    private void init() {
        lineChart = view.findViewById(R.id.linechrt);
        table_layout=view.findViewById(R.id.table_layout);
        bundle = getArguments();
        if (getArguments() != null) {
            columnWiseList = (ArrayList<HashMap<String, String>>) bundle.getSerializable("columnWiseList");
            graphFinalList = (ArrayList<ArrayList<HashMap<String, String>>>) bundle.getSerializable("graphFinalList");
            graphList = (ArrayList<HashMap<String, String>>) bundle.getSerializable("graphList");
            selectedVal = (HashMap<String, String>) bundle.getSerializable("selectedVal");


        }
        reset_btn = view.findViewById(R.id.reset_btn);
        reset_btn.setOnClickListener(this);
        toggle_view_btn=getActivity().findViewById(R.id.view_toggle);
        toggle_landscape_btn=getActivity().findViewById(R.id.view_toggle_landscape);
        if(toggle_view_btn.isChecked()|| toggle_landscape_btn.isChecked())
        {
            complete_view=true;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.reset_btn) {
            lineChart.moveViewToX(0);
            lineChart.setScaleMinima(1.0f, 1.0f);
            lineChart.fitScreen();
            lineChart.invalidate();
            reset_btn.setVisibility(View.GONE);

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

            displayAreaGraph();
            showCustomLegendsLL();
        }
    }
}
