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
import com.github.mikephil.charting.components.YAxis;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.dashboard.activity.GraphDetailsActivity.trend;

public class AreaGraph  extends Fragment implements View.OnClickListener , Interfaces.ChangeColorListener{


    private View view;
    private Context context;
    private FragmentManager fragmentManager;
    private LineChart lineChart;
    private ArrayList<HashMap<String,String>> localgraphdata;
    private LineDataSet lineDataSet;
    private int k;
    private String chartName="";
    private ArrayList<String> labels;
    private LineData data;
    private LineData lineData;
    private ArrayList<LineDataSet> dataSets=new ArrayList<>();
    private List<String> legendVals=new ArrayList<>();
    private Bundle bundle;
    private ArrayList<HashMap<String, String>> columnWiseList;
    private ArrayList<ArrayList<HashMap<String, String>>> graphFinalList;
    private ArrayList<HashMap<String, String>> graphList;
    private HashMap<String, String> selectedVal;
    private boolean changeColorFlag=true;
    private ArrayList <Integer>colorsList;
    private ArrayList<Integer> randomColorsList=new ArrayList<>();
    private int selectedindex=-1;
    private int finalColor;
    private Button reset_btn;
    private XAxis xAxis;
    private Button zoomIn,zoomOut;
    private TableLayout table_layout;
    private TextView year_Tv;
    private ToggleButton toggle_view_btn;
    private boolean complete_view;
    private ToggleButton toggle_landscape_btn;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.linedashboard,container,false);

        try {
            init();
            addRandomColors();
            displayAreaGraph();
            showCustomLegendsLL();
        }
        catch (Exception e)
        {
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

        legendVals=new ArrayList<>();
        dataSets=new ArrayList<>();
        lineData=new LineData();

        if(graphFinalList!=null && graphFinalList.size()>0) {
            int maxSize=0;
            ArrayList<Integer> datasetSizeList=new ArrayList<>();
            //legendVals = Util.separateCommaSeparatedString(graphList.get(0).get("name"));
            for(int a=0;a<graphFinalList.size();a++) {
                datasetSizeList.add(graphFinalList.get(a).size());
            }
            maxSize= Collections.max(datasetSizeList);
           /* AsyncTaskForGraph asyncTask=new AsyncTaskForGraph();
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);*/
            for (int a = 0; a < graphFinalList.size(); a++) {
                String name=graphList.get(a).get("name");
                if(Utils.checkifavailable(name))
                {
                    String legend= String.valueOf(Utils.separateCommaSeparatedString(name));

                    if(Utils.checkifavailable(legend))
                    {
                        legend = legend.replaceAll("\\[", "").replaceAll("\\]","");

                        legendVals.add(legend);

                    }

                }


                //legendVals.add(String.valueOf(Utils.separateCommaSeparatedString(graphList.get(a).get("name"))));
                //   legendVals= Utils.separateCommaSeparatedString(graphList.get(a).get("name"));
                System.out.println("legend val "+legendVals +a);
                localgraphdata = new ArrayList<>();
                localgraphdata.addAll(graphFinalList.get(a));
                ArrayList<Entry> entries = new ArrayList<>();
                labels = new ArrayList<String>();
//                if(graphFinalList.get(a).size()==maxSize) {
//                    labels=new ArrayList<>();
//                    generateLabelsAccordingToTrending();
//                }
                if (localgraphdata != null) {
                    if (localgraphdata.size() > 0) {
                        System.out.println("localgraphdata size "+localgraphdata.size());
                        entries.addAll(generateEntriesDataSet());
                        System.out.println("localgraph data enteries size "+entries.size());
                        generateLabelsAccordingToTrending();
                        System.out.println("localgraph data labels size "+labels.size());
                    }


                    colorsList=new ArrayList<>();
                    int color=Utils.getSomeRandomColor();
                    if(color!=R.color.app_theme)
                    {
                        finalColor=color;
                    }
                    else
                    {
                        finalColor=Utils.getSomeRandomColor();

                    }


                    lineDataSet = new LineDataSet(entries, legendVals.get(a));
                    lineDataSet.setDrawValues(false);
                    lineDataSet.setDrawCubic(true);


                    System.out.println("selected index "+selectedindex);
                    int redcolor= Color.rgb(255,0,0);
                    if(selectedindex!=-1) {
                        for (int i = 0; i < localgraphdata.size(); i++) {

                            if (i == selectedindex) {


                                colorsList.add(i,redcolor);
                            } else {
                                colorsList.add(i,finalColor);
                            }
                        }
                        System.out.println("default vals "+ lineChart.getScaleX() +"   "+lineChart.getScaleY());
                        lineChart.setScaleMinima(2f,1f);
                        lineChart.moveViewToX(selectedindex);
                        reset_btn.setVisibility(View.VISIBLE);
                        System.out.println("colors list "+colorsList);

                    }
                    else
                    {
                        reset_btn.setVisibility(View.GONE);
                    }



                    setColorForEachDataSet(a);
                    dataSets.add(lineDataSet);

                }
            }


            data=new LineData(labels,dataSets);


            lineChart.setData(data);
            chartCustomization();
            if(!complete_view)
                lineChart.setVisibleXRangeMaximum(15);

            //lineChart.setVisibleXRangeMinimum(15);
            lineChart.invalidate();
            CustomMarkerView mv = new CustomMarkerView(getActivity(), R.layout.custom_marker_view_layout, labels);
            lineChart.setMarkerView(mv);
            lineChart.invalidate();
            //  lineChart.setVisibleXRangeMaximum(7);


            if(!complete_view) {
                if (lineChart.getXValCount() > 15)
                    Utils.showLongToast(getActivity(), "Kindly slide and zoom to view data!!");
            }
            else
            {
                Utils.showLongToast(getActivity(), "Kindly zoom to view data!!");
            }

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

    private ArrayList<Entry> generateEntriesDataSet() {
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < localgraphdata.size(); i++) {
            String value=localgraphdata.get(i).get("y");
            float entry = 0f;
            if(Utils.checkifavailable(value))
            {
                entry=Float.valueOf(value);
            }

            entries.add(new Entry(entry, i));

        }

        return  entries;
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

    private void setColorForEachDataSet(int index) {
        int color=randomColorsList.get(index);
        lineDataSet.setColor(color);
        randomColorsList.add(color);
        lineDataSet.setLineWidth(2f);
        lineDataSet.setFillAlpha(80);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillColor(color);

        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setCircleColor(color);
        lineDataSet.setValueTextSize(12f);
        if(selectedindex!=-1) {
            if(colorsList!=null && colorsList.size()>0) {
                lineDataSet.setCircleColors(colorsList);
            }
        }
    }


    private void chartCustomization() {
        lineDataSet.setDrawValues(false);
        lineChart.setDescription("");

        lineChart.animateXY(2000, 2000);

        //lineChart.setVisibleXRangeMinimum(15);

        lineChart.setExtraRightOffset(19);
        lineChart.setExtraLeftOffset(19);

        Legend legend = lineChart.getLegend();
        legend.setEnabled(false);
       /* legend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        legend.setWordWrapEnabled(true);
        legend.setTextSize(7);*/
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawLimitLinesBehindData(true);
        xAxis.setTextSize(11f);

        xAxis.setLabelRotationAngle(10f);

        YAxis yAxis=lineChart.getAxisLeft();

        //xAxis.setAvoidFirstLastClipping(true);






        //   lineDataSet.se

    }

    private void init() {
        lineChart=view.findViewById(R.id.linechrt);
        table_layout=view.findViewById(R.id.table_layout);
        zoomIn=view.findViewById(R.id.zoomin);
        zoomOut=view.findViewById(R.id.zoomOut);
        year_Tv=view.findViewById(R.id.year);
        toggle_view_btn=getActivity().findViewById(R.id.view_toggle);
        toggle_landscape_btn=getActivity().findViewById(R.id.view_toggle_landscape);
        if(toggle_view_btn.isChecked() || toggle_landscape_btn.isChecked())
        {
            complete_view=true;
        }
        bundle=getArguments();
        if(getArguments()!=null)
        {
            columnWiseList= (ArrayList<HashMap<String, String>>) bundle.getSerializable("columnWiseList");
            graphFinalList= (ArrayList<ArrayList<HashMap<String, String>>>) bundle.getSerializable("graphFinalList");
            graphList= (ArrayList<HashMap<String, String>>) bundle.getSerializable("graphList");
            selectedVal=(HashMap<String,String>)bundle.getSerializable("selectedVal");


        }
        reset_btn=view.findViewById(R.id.reset_btn);
        reset_btn.setOnClickListener(this);
        zoomIn.setOnClickListener(this);
        zoomOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.reset_btn)
        {
            lineChart.moveViewToX(0);
            lineChart.setScaleMinima(1.0f,1.0f);
            lineChart.fitScreen();
            lineChart.invalidate();
            reset_btn.setVisibility(View.GONE);

        }
        else  if(v.getId()==R.id.zoomin)
        {
            //   lineChart.zoom(0,0.1f,2,0);
            lineChart.zoomIn();
        }
        else  if(v.getId()==R.id.zoomOut)
        {
            lineChart.zoomOut();
        }
    }


    @Override
    public void changeColor(int color,String legend) {
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

            displayAreaGraph();
            showCustomLegendsLL();
        }
    }

}
