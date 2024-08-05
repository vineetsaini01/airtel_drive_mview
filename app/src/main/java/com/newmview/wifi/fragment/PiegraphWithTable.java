package com.newmview.wifi.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class PiegraphWithTable extends Fragment implements Interfaces.ChangeColorListener{
    private View view;
    private PieChart pieGraph;
    private TableLayout tableLayout;
    private ArrayList<HashMap<String, String>> localgraphdata;
    private ArrayList<Entry> entries;
    private ArrayList<String> labels;
    private ArrayList<PieDataSet> dataSets=new ArrayList<>();
    private PieDataSet pieDataSet;
    private PieData data;
    private double single_dataitem_percent;
    private double total_data=0;
    TableLayout db_tblLayout;
    Context context ;
    private int deviceWidth;
    private int deviceHeight;
    private ArrayList<ArrayList<HashMap<String, String>>> graphFinalList;

    private Bundle bundle;

    
    private ArrayList<String> colnames;
   
    private Spinner goToSpinner, rowsSpinner;
    private ArrayList<HashMap<String, String>> percentData;
    private TableRow tableRow,firstRow;
    private Integer totalCount;
    private ArrayList<Integer> rowsList;
    private EditText goToEt;
    private String upperIndex;
    private String lowerIndex = "1";
    private boolean tableFlag = false;
    private ArrayList<HashMap<String, String>> tablelist;
    private ArrayList<HashMap<String, String>> pieDataColumns;
    private ArrayList<HashMap<String,String>> pieDataNamesList;
    private int[] colors;
    private TableRow.LayoutParams lp;
    private ArrayList<Integer> randomColorsList=new ArrayList<>();
    private ArrayList<String> legendVals;

    public void onCreate(Bundle icici){
        super.onCreate(icici);
        try {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            deviceWidth = displayMetrics.widthPixels;
            deviceHeight = displayMetrics.heightPixels;
            Log.e("Width, Height:: ", deviceWidth + ", " + deviceHeight);
            Log.e("Width/2, Height/2:: ", deviceWidth/2 + ", " + deviceHeight/2);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.pie_new,container,false);
        init();
addSomeRandomColors();
        displayGraphData();
        displayTableData();

        return view;
    }

    private void addSomeRandomColors() {
        if (pieDataColumns != null)
        {
            if(pieDataColumns.size()>0) {
                colors = new int[pieDataColumns.size()];
                for (int j = 0; j < pieDataColumns.size(); j++) {
                    int color = Utils.getSomeRandomColor();
                    colors[j] = color;
                    randomColorsList.add(color);

                }

                System.out.println("randomcolors " + randomColorsList);
                if (getActivity() != null) {
                    if (getActivity() instanceof GraphDetailsActivity) {
                        ((GraphDetailsActivity) getActivity()).sendColorsList(randomColorsList);
                    }
                }
            }
    }

    }

    private void displayTableData() {
        try {
legendVals=new ArrayList<>();
tableLayout.removeAllViews();
            //tableLayout.setStretchAllColumns(true);
            tableLayout.setPadding(15, 2, 2, 2);
            tableLayout.setGravity(Gravity.CENTER);
            ViewGroup.LayoutParams layoutParams1 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (percentData != null && percentData.size() > 0) {

                View viewDivider;




                for (int j = 0; j < percentData.size(); j++) {


                    System.out.println("in j loop"+j );


                    if(j==0)
                    {
                        firstRow = new TableRow(getActivity());
                        int k=0;
                        for(int i=0;i<=colnames.size();i++) {
                            TextView tv=new TextView(getActivity());
                            tv.setPadding(15, 8, 1, 8);
                            tv.setGravity(Gravity.START);
                            firstRow.setBackgroundColor(getResources().getColor(R.color.lightgrey));

                            if(i==0)
                            {
                                firstRow.addView(tv);
                            }
                            else {
                                tv.setText(colnames.get(k));


                                firstRow.addView(tv);
                                k++;
                            }
                            //tableRow.requestLayout();

                            // tableLayout.requestLayout();
                        }

                    }


                    tableRow = new TableRow(getActivity());
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
                    tableRow.setLayoutParams(lp);



                    TextView tv_name = new TextView(getActivity());
                    View viewDiv = new View(getActivity());
                    int dividerHeight = (int) (getResources().getDisplayMetrics().density * 1);
                    int width=(int) (getResources().getDisplayMetrics().density*2);
                    int height= (int) (getResources().getDisplayMetrics().density * 0.2);

                    viewDiv.setBackgroundColor(getResources().getColor(R.color.lightgrey));

                    // ViewGroup.LayoutParams layoutParams1 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    TextView tv_val = new TextView(getActivity());

                 /*  TableRow.LayoutParams paramsExample = new TableRow.LayoutParams(2,2);
                    */
                   // tv_leg.setBackground(getResources().getDrawable(R.drawable.square));
                    if(j>0)
                    {
                        TextView tv_leg = new TextView(getActivity());
if(j<colors.length) {
    tv_leg.setBackgroundColor(colors[j - 1]);
}
else
{
    tv_leg.setBackgroundColor(colors[colors.length-1]);
}
                        tableRow.addView(tv_leg);
                        TableRow.LayoutParams params = new TableRow.LayoutParams(getResources().getDimensionPixelSize(R.dimen.text_view_width),15);
                        params.setMargins(10, 10, 10, 10);
                        tv_leg.setLayoutParams(params);
                      //  tv_leg.setGravity(Gravity.CENTER);
                    }
else
                    {
                        TextView tv_leg = new TextView(getActivity());
                        tableRow.addView(tv_leg);
                    }

                    tableRow.addView(tv_name);
//tableRow.addView(viewDiv);

                    tableRow.addView(tv_val);
                    viewDiv.setLayoutParams(new TableRow.LayoutParams(1,ViewGroup.LayoutParams.MATCH_PARENT));
legendVals.add(tablelist.get(j).get("y"));
                    tv_name.setText(tablelist.get(j).get("y"));
                    tv_name.setPadding(15, 8, 1, 8);
                   // tv_name.setGravity(Gravity.START);
                    /*tv_leg.setPadding(5,10,5,10);*/

                    tv_name.setTextColor(getResources().getColor(R.color.textColor));
                    tv_val.setPadding(10, 8, 1, 8);
                   // tv_val.setGravity(Gravity.START);
                    tv_val.setText(String.format("%s %s", percentData.get(j).get("y"), percentData.get(j).get("percent")));

                    viewDivider = new View(getActivity());
RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dividerHeight);
layoutParams.setMarginEnd(2);
                    viewDivider.setLayoutParams(layoutParams);
                    viewDivider.setBackgroundColor(getResources().getColor(R.color.lightgrey));
                    tableLayout.addView(viewDivider);
                    /*if(tableRow.getParent() != null) {
                        ((ViewGroup)tableRow.getParent()).removeView(tv);
                    }*/

                    if(j==0)
                    {
                        tableLayout.addView(firstRow);
                    }
                   /* tableRow.requestLayout();*/
                    tableLayout.addView(tableRow);
                    tableLayout.requestLayout();
        
        
        
                }

                viewDivider = new View(getActivity());
                int dividerHeight = (int) (getResources().getDisplayMetrics().density * 2); // 1dp to pixels
                viewDivider.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dividerHeight));
                viewDivider.setBackgroundColor(getResources().getColor(R.color.app_theme));

            } else if (Constants.ERROR == 3) {
                Utils.showToast(getActivity(), Constants.SERVER_ERROR);
                Constants.ERROR=0;
            }
        } catch (Exception e) {
            e.printStackTrace();

            System.out.println("exception on creating table is " + e.toString());
        }
    }


    private void init() {

        pieGraph=view.findViewById(R.id.piechrt);
        tableLayout=view.findViewById(R.id.tableLayout);
        bundle=getArguments();
        if (Utils.checkContext(getActivity())) {
            goToEt = view.findViewById(R.id.goToEt);
            rowsSpinner = view.findViewById(R.id.rowsSpinner);
            // rowsSpinner.setOnItemSelectedListener(this);
            if (bundle != null) {
                tablelist = (ArrayList<HashMap<String, String>>) bundle.getSerializable("tablelist");
                colnames = (ArrayList<String>) bundle.getSerializable("colnameslist");
                percentData = (ArrayList<HashMap<String, String>>) bundle.getSerializable("percentDatalist");
                totalCount = Integer.valueOf(bundle.getString("totalCount"));
                pieDataColumns=(ArrayList<HashMap<String, String>>) bundle.getSerializable("pieDataColums");
                pieDataNamesList=(ArrayList<HashMap<String, String>>) bundle.getSerializable("pieDataNamesList");

                System.out.println("table list " + tablelist);
                System.out.println("table col names " + colnames);
                System.out.println("table percent data " + percentData);
                rowsList = new ArrayList<>();
                int count = totalCount / 10;
                int remainder = totalCount % 10;
                if (totalCount <= 100) {
                    for (int i = 1; i < count; i++) {
                        rowsList.add(10 * i);
                    }
                    rowsList.add((10 * count) + remainder);
                }


            }

        }

    }

    private void displayGraphData() {
        try {
            //List<String> legendVals= Utils.separateCommaSeparatedString(CommonUtil.columnWiseList.get(0).get("name"));

            entries = new ArrayList<>();
            labels = new ArrayList<String>();

            if (pieDataColumns != null && pieDataColumns.size() > 0) {
                for (int i = 0; i < pieDataColumns.size(); i++) {
                    entries.add((new Entry(Float.valueOf(pieDataColumns.get(i).get("y")), i)));
                    labels.add("");
                }
                pieDataSet = new PieDataSet(entries, "");
                setColors();
                pieDataSet.setSliceSpace(6);
                pieDataSet.setSelectionShift(10);
                data = new PieData(labels, pieDataSet);
                chartCustomization();
                pieGraph.setData(data);
                pieGraph.invalidate();
                CustomMarkerView mv = new CustomMarkerView(getActivity(), R.layout.custom_marker_view_layout, labels);
                pieGraph.setMarkerView(mv);
                pieGraph.invalidate();


            }
            //   }



        }




        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Exception is displaying graph "+e.toString());
        }

    }




    private void setColors() {


        pieDataSet.setColors(colors);
    }

    private void calculatePercentageForDataItems() {


        //pieDataSet.setColors(colors);
    }

    private void chartCustomization() {
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(0f);
        Legend legend =pieGraph.getLegend();
        legend.setWordWrapEnabled(true);
        pieGraph.setData(data);
       pieGraph.setUsePercentValues(true);
       pieGraph.setDescription("");
       pieGraph.setDrawSliceText(false);
       pieGraph.animateXY(2000,2000);
       pieGraph.setHighlightPerTapEnabled(true);
       pieGraph.setHoleColorTransparent(true);
       pieGraph.setHoleRadius(15);
       pieGraph.setTransparentCircleRadius(0);
       pieGraph.setRotationEnabled(true);
       pieGraph.setRotationAngle(0);
       pieGraph.setDragDecelerationEnabled(true);
       pieGraph.getLegend().setEnabled(false);
    }

    @Override
    public void changeColor(int color, String legend) {
        int index=-1;
        if(colors!=null && colors.length>0)
        {
            for(int i=1;i<legendVals.size();i++)
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

            if(index>0) {
              if(randomColorsList!=null && randomColorsList.size()>0) {
                  randomColorsList.set(index-1, color);
                  System.out.println("random list "+randomColorsList);
               colors = new int[randomColorsList.size()];
                  for (int i = 0; i < randomColorsList.size(); i++) {
                      colors[i] = randomColorsList.get(i);
                  }


                  displayGraphData();
                  displayTableData();
              }

            }
        }
    }
    }

