package com.dashboard.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dashboard.fragment.TableNewFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mview.airtel.R;
import com.newmview.wifi.adapter.MultiFilterAdapter;
import com.newmview.wifi.adapter.SearchAdapter;
import com.newmview.wifi.bean.CheckBoxState;
import com.newmview.wifi.database.DB_handler;
import com.newmview.wifi.fragment.AreaGraph;
import com.newmview.wifi.fragment.BarGraph;
import com.newmview.wifi.fragment.DonutGraph;
import com.newmview.wifi.fragment.Graphs_BottomsheetFragment;
import com.newmview.wifi.fragment.LineGraph;
import com.newmview.wifi.fragment.MapsFragment;
import com.newmview.wifi.fragment.MetricFragment;
import com.newmview.wifi.fragment.MultiYAxesGraph;
import com.newmview.wifi.fragment.PercentTableFragment;
import com.newmview.wifi.fragment.PieChartFragment;
import com.newmview.wifi.fragment.PiegraphWithTable;
import com.newmview.wifi.fragment.TableFragment;
import com.newmview.wifi.helper.AsyncTasks_APIgetData;
import com.newmview.wifi.helper.CommonUtil;
import com.newmview.wifi.helper.Interfaces;
import com.newmview.wifi.interfaces.AsynctaskListener;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;
import com.squareup.timessquare.CalendarPickerView;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import yuku.ambilwarna.AmbilWarnaDialog;

public class GraphDetailsActivity  extends AppCompatActivity implements AsynctaskListener, View.OnClickListener,
        AdapterView.OnItemSelectedListener, Filterable, SearchView.OnQueryTextListener, SearchView.OnCloseListener,AdapterView.OnTouchListener, CompoundButton.OnCheckedChangeListener  {

    public static String column;
    private static String multipleTableColumns;
    private static String multipleMetricColumns;
    private String filter=null;

    private static String status;
    private static boolean successFlag=false;

    private static String statusval;
    private  String filterval;
    private static String label;
    private String graphId;
    private ActionBar actionBar;
    private Dialog dialog;
    private View rootView;


    private RadioGroup trendradiogroup;
    private FragmentManager fragmentManager;
    private Bundle args;
    private String name;
    private CalendarPickerView calendar;
    private long recordsFiltered,recordsTotal;
    private LayoutInflater inflater;
    private RadioButton radioButton;
    private static String mindate;
    private static String maxdate;
    public static String subgrp;
    public static String group=null;
    public static String ctype="area";
    public static String graphname,graphid;
    RadioGroup.LayoutParams rprms;
    public static String trend;
    private Spinner grouptv,subgrouptv,trendtv;
    private AreaGraph areaGraph;
    private Spinner trendspinner;
    private LinearLayout checkboxll;
    private Spinner columntv;
    private String col=null;
    private static String multipleColumn;
    private boolean multiplefields=false;
    private ArrayList<String> colum=new ArrayList();
    private BarGraph barGraph;
    final ArrayList<HashMap<String,String>> locallist=new ArrayList<>();
    private PiegraphWithTable piegraph;
    private TableFragment tablefragment;
    private TableNewFragment tableNewfragment;

    private int dbposition;
    private LinearLayout linearLayout;
    public static int position;
    private Graphs_BottomsheetFragment graphs_bottomsheetFragment;
    private LineGraph linegraph;
    ArrayList<HashMap<String,String>> relativeList=new ArrayList<>();
    ArrayList <String> groupingList=new ArrayList<>();
    ArrayList<ArrayList<HashMap<String, String>>> subgroupingList=new ArrayList<>();
    ArrayList<HashMap<String,String>> trendList=new ArrayList<>();
    ArrayList<HashMap<String,String>> matrixList=new ArrayList<>();
    ArrayList<HashMap<String,Object>> filterList=new ArrayList<>();
    ArrayList<ArrayList<HashMap<String, String>>> filterData=new ArrayList<>();
    ArrayList<HashMap<String,String>> trendingList=new ArrayList<>();
    ArrayList <HashMap<String,String> >graphTypes=new ArrayList<>();
    ArrayList <HashMap<String,String>>columnList = new ArrayList<>();
    private ArrayList<HashMap<String, String>> columnWiseList;
    private ArrayList<ArrayList<HashMap<String, String>>> graphFinalList;
    private ArrayList<HashMap<String, String>> chartTitleList;
    private int i;
    private int j;
    private boolean firsttime=false;
    ArrayList<HashMap<String,String>> datalist=new ArrayList<>();
    ArrayList<HashMap<String,String>> filteredSearchList;
    private ArrayList<HashMap<String,String>> commondatalist;

    private ArrayList<HashMap<String, String>> graphList;
    private PiegraphWithTable pieGraphWithTable;
    private int ind;
    private ArrayList<Integer> indexList=new ArrayList<>();
    private String monthNum;
    private ArrayList<HashMap<String, String>> tabledatalist;
    private ArrayList<String> colNamesList;
    private ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>> grpWithSubgrpsList;
    private ArrayList<String> sublist;
    private ArrayList<String> subList;
    private static String  filterType;
    private ArrayList<HashMap<String, String>> tablePercentDataList;
    private String totalCount;
    private boolean tableFlag;
    private String lowerIndex;
    private  String upperIndex;
    private PercentTableFragment percentTableFragment;
    private ArrayList<HashMap<String, String>> pieDataNamesList;
    private ArrayList<HashMap<String,String>> pieDataColumns;
    private ArrayList<ArrayList<HashMap<String, String>>> tableAllColumnsDataList;
    private ArrayList<String> drillXAxisList=new ArrayList<>();

    private PieChartFragment pieFragment;
    private LinearLayout ll;
    private AutoCompleteTextView search;
    private FilterValuesFilter filterValuesFilter;
    private ArrayList<HashMap<String, String>> finalSearchList;
    private SearchView searchView;
    private RecyclerView searchRecycler;
    private SearchAdapter searchAdapter;
    private HashMap<String, String> selectedVal;
    private String button;
    private LinearLayout topBarLl;
    private ArrayList<HashMap<String,String>> noneSublist;
    private LinearLayout linear;
    private String lerankRange,geRankRange;
    private String lowerbtwn,upperbtwn;
    private Button resetFilter;
    private boolean lowerFlag=false;
    private boolean upperFlag=false;
    private boolean btwnFlag=false;


    private Menu menu;
    private TextView textView;
    private LinearLayout filterLayout;
    private DB_handler db_handler;
    private ArrayList<String> filterNamesList;
    private ArrayList<ArrayList<HashMap<String,String>>> allTablesData;
    private ArrayList<ArrayList<HashMap<String, String>>> tablesData2=new ArrayList<>();
    private ArrayList<String> newFilterNamesList;
    private ArrayList<Object> newwTables=new ArrayList<>();
    private HashMap<Integer,ArrayList<HashMap<String,String>>> tableHp=new HashMap<>();
    private ArrayList<ArrayList<HashMap<String, String>>> newlyTablesList=new ArrayList<ArrayList<HashMap<String, String>>>();
    private int finalI2;
    private ArrayList<ArrayList<HashMap<String,String>>> finalList=new ArrayList<>();
    private ArrayList<HashMap<String, String>> selectedList;
    private ArrayList<HashMap<String, String>> newestlist=new ArrayList<HashMap<String, String>>();
    private ArrayList<ArrayList<HashMap<String, String>>> finalListOfAllCheckBoxes;
    private ArrayList<ArrayList<String>> totalTableNewList;

    private boolean trendSpinnerTouched=false;
    private boolean groupingSpinnerTouched=false;
    private boolean subGroupingTouched=false;
    private ImageView circle_IV,selectChart,selectFilter,selectType,selectdaterange,selectGraphType,selectColumn;
    private ImageView view_img,trend_img;
    private ImageView shareIv;
    private ArrayList<String> trendNames;
    private static ArrayList<Date> selected=new ArrayList<>();
    private ArrayList<HashMap<String, String>> filterDetails=new ArrayList<>();
    private MultiFilterAdapter rvAdapter;
    private ArrayList<HashMap<String, String>> clickedList;
    private RecyclerView rvTest;

    private HashMap<String, String> params;
    private HashMap<String,String> hp=new HashMap<>();
    private ArrayList<String> checkedValues;
    private MetricFragment metricFragment;
    private DonutGraph donutGraph;
    private FloatingActionButton floatingActionButton;
    private int DefaultColor;
    private ArrayList<String> legendsList;
    private Interfaces.ChangeColorListener changeColorListener;
    private ArrayList<Integer> colorsList;
    private  ArrayList<String> finalFilterValues=new ArrayList<>();
    private ArrayList<HashMap<String,String>> filterAndValuesList=new ArrayList<>();

    private LinearLayout bottom_ll;
    private ImageView selectFilter_landscape;
    private ImageView selectGraph_landscape;
    private String selectedGraphName;
    private String start;
    private String length;
    private long initialIndex;
    private String pageNum;
    private ArrayList<Date> selectedDates;
    private boolean trendhourlyFlag;
    private ScrollView scrollView;
    private MapsFragment mapsFragment;
    private ArrayList<String> multiYNameList;
    private ArrayList<ArrayList<HashMap<String, String>>> mainDataMultiYList;
    private ToggleButton toggle_view_btn;
    private ToggleButton view_toggle_landscape;

    @Override
    protected void onStop()
    {
        super.onStop();

    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);

        if((fragment instanceof AreaGraph)|| (fragment instanceof LineGraph) || (fragment instanceof BarGraph)||
                (fragment instanceof PiegraphWithTable) || (fragment instanceof DonutGraph) ||
                (fragment instanceof MetricFragment) ||
               // (fragment instanceof PieChartFragment) ||
                (fragment instanceof MultiYAxesGraph))
        {
            changeColorListener=(Interfaces.ChangeColorListener)fragment;
        }

    }

    @Override
    protected void onStart() {
        // Utils.showToast(this,"onstart callled");
        super.onStart();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int orientation = getResources().getConfiguration().orientation;
        if(orientation==Configuration.ORIENTATION_LANDSCAPE)
        {
            selectFilter_landscape.setVisibility(View.VISIBLE);
            selectGraph_landscape.setVisibility(View.VISIBLE);
            bottom_ll.setVisibility(View.GONE);
            resetFilter.setVisibility(View.GONE);
            if(Utils.checkifavailable(ctype)) {

   /* if(toggle_view_btn.isChecked())
    {
        view_toggle_landscape.setChecked(true);
    }
    else {
        view_toggle_landscape.setChecked(false);
    }*/

                if (ctype.equalsIgnoreCase("bar") || ctype.equalsIgnoreCase("line") ||
                        ctype.equalsIgnoreCase("area") ||
                        ctype.equalsIgnoreCase("Multi_Y_Axis_Bar")) {


                    toggle_view_btn.setVisibility(View.GONE);
                    view_toggle_landscape.setVisibility(View.VISIBLE);
                } else {
                    toggle_view_btn.setVisibility(View.GONE);
                    view_toggle_landscape.setVisibility(View.GONE);
                }
            }
            //  scrollView.setEnabled(false);
        }
        else
        {
            selectFilter_landscape.setVisibility(View.GONE);
            selectGraph_landscape.setVisibility(View.GONE);
            bottom_ll.setVisibility(View.VISIBLE);
            setVisibilityOfResetFilterButton();

            if(Utils.checkifavailable(ctype)) {
               /* if(view_toggle_landscape.isChecked())
                {
                    toggle_view_btn.setChecked(true);
                }

                else {
                    toggle_view_btn.setChecked(false);
                }*/
                if (ctype.equalsIgnoreCase("bar") || ctype.equalsIgnoreCase("line") || ctype.equalsIgnoreCase("area") ||
                        ctype.equalsIgnoreCase("Multi_Y_Axis_Bar")) {
                    toggle_view_btn.setVisibility(View.VISIBLE);
                    view_toggle_landscape.setVisibility(View.GONE);
                } else {
                    toggle_view_btn.setVisibility(View.GONE);
                    view_toggle_landscape.setVisibility(View.GONE);
                }
            }


            //   scrollView.setEnabled(false);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //   Utils.showToast(this,"on create  callled");
        setContentView(R.layout.graph_details);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        init();





        if (Utils.isNetworkAvailable(GraphDetailsActivity.this)) {

            // if (graphId != null) {
            sendGraphDetailsRequest(graphId);
            // }
        } else {
            Utils.showToast(GraphDetailsActivity.this, Constants.NO_INTERNET);
        }


    }

    private void setItemsForSpinner() {

        subgrouptv.setVisibility(View.INVISIBLE);


        if(CommonUtil.trend!=null && CommonUtil.trend.size()>0) {
            ArrayAdapter<String> spinneritems = new ArrayAdapter<>(this, R.layout.spinner_item, CommonUtil.trend);
            spinneritems.setDropDownViewResource(R.layout.dialog_singlechoice);
            trendspinner.setAdapter(spinneritems);
            trendspinner.setVisibility(View.VISIBLE);
        }
        if(groupingList!=null && groupingList.size()>0) {
            ArrayAdapter<String> grpspinner = new ArrayAdapter<>(this, R.layout.spinner_item, groupingList);
            grpspinner.setDropDownViewResource(R.layout.dialog_singlechoice);
            grouptv.setAdapter(grpspinner);
            grouptv.setVisibility(View.VISIBLE);
        }



    }

    private void init() {
        args=new Bundle();
        // scrollView = (ScrollView)findViewById(R.id.scrollview);
        //scrollView.setEnabled(false);
        if (getSupportActionBar() != null) {
            actionBar = getSupportActionBar();
        }
        db_handler=new DB_handler(this);
        bottom_ll=findViewById(R.id.bottom_ll);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        grouptv=findViewById(R.id.grouping);
        trendspinner=findViewById(R.id.trending);
        subgrouptv=findViewById(R.id.subgrouping);
        searchView=findViewById(R.id.search);
        searchRecycler=findViewById(R.id.searchList);
        topBarLl=findViewById(R.id.topbarll);
        resetFilter=findViewById(R.id.resetfilter);
        filterLayout=findViewById(R.id.filterLayout);
        toggle_view_btn=findViewById(R.id.view_toggle);
        toggle_view_btn.setOnCheckedChangeListener(this);

        view_toggle_landscape=findViewById(R.id.view_toggle_landscape);
        view_toggle_landscape.setOnCheckedChangeListener(this);
        resetFilter.setOnClickListener(this);
        selectGraphType=findViewById(R.id.Selectchart);
        selectdaterange=findViewById(R.id.select_date);
        selectChart=findViewById(R.id.select_graph);
        view_img=findViewById(R.id.view);
        trend_img=findViewById(R.id.trend);
        shareIv=findViewById(R.id.shareIv);
        floatingActionButton=findViewById(R.id.floatingActionButton);
        selectFilter=findViewById(R.id.select_filteroptions);
        selectColumn=findViewById(R.id.select_column);
        selectFilter_landscape=findViewById(R.id.select_filteroptions1);
        selectGraph_landscape=findViewById(R.id.select_graph1);
        selectColumn.setOnClickListener(this);
        selectFilter.setOnClickListener(this);
        selectGraphType.setOnClickListener(this);
        selectdaterange.setOnClickListener(this);
        selectChart.setOnClickListener(this);
        resetFilter.setOnClickListener(this);
        view_img.setOnClickListener(this);
        trend_img.setOnClickListener(this);
        shareIv.setOnClickListener(this);
        selectGraph_landscape.setOnClickListener(this);
        selectFilter_landscape.setOnClickListener(this);
        floatingActionButton.setOnClickListener(this);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        searchRecycler.setLayoutManager(linearLayoutManager);
        searchRecycler.setHasFixedSize(true);
        graphId = getIntent().getStringExtra("graphId");
        name=getIntent().getStringExtra("graphName");
        GraphDetailsActivity.graphid=graphId;
        GraphDetailsActivity.graphname=name;
        if(Utils.checkifavailable(name))
        {
            actionBar.setTitle(name);
            selectedGraphName=name;
        }
        else
        {
            actionBar.setTitle(getResources().getString(R.string.app_name));
        }
        dbposition=getIntent().getIntExtra("clickedposition",0);

        fragmentManager=getSupportFragmentManager();
        System.out.println("graphid in  details activity " + graphId +"dbpoition "+dbposition);
        SpinnerInteractionListener listener = new SpinnerInteractionListener();
        grouptv.setOnItemSelectedListener(listener);
        trendspinner.setOnItemSelectedListener(listener);

        subgrouptv.setOnItemSelectedListener(listener);
        trendspinner.setOnTouchListener(listener);
        subgrouptv.setOnTouchListener(listener);
        grouptv.setOnTouchListener(listener);


        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -8);
        Date newdate=calendar.getTime();


        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");


        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.DAY_OF_YEAR, -1);
        Date newdate1=calendar1.getTime();


        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");


        if(selected.isEmpty()) {
            mindate = df.format(newdate);
            maxdate = df1.format(newdate1);
            selected.add(0, newdate);
            selected.add(1, newdate1);

        }

    }

    public void sendGraphDetailsRequest(String graphId) {

        HashMap<String, String> obj = new HashMap<>();
        obj.put(CommonUtil.REQUEST_KEY, CommonUtil.GRAPHDETAILS_REQUEST);
        obj.put(CommonUtil.USER_ID_KEY, CommonUtil.USER_ID_NEW);
        obj.put("graphId", graphId);
        AsyncTasks_APIgetData asyncTasks_apIgetData = new AsyncTasks_APIgetData(GraphDetailsActivity.this, this);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
        {
            asyncTasks_apIgetData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,obj);
        }
        else {
            asyncTasks_apIgetData.execute(obj);
        }
        CommonUtil.request = 4;
        GraphDetailsActivity.successFlag=false;
    }



    public void sendGetGraphDataRequest() {
        i++;
        //   Utils.showToast(this,"sending get graph data");
        //  Toast.makeText(this, "sending getgraph req "+ i +" time", Toast.LENGTH_SHORT).show();

        HashMap<String, String> obj = new HashMap<>();
        obj.put(CommonUtil.REQUEST_KEY, CommonUtil.GRAPHDATA_REQUEST);
        obj.put(CommonUtil.USER_ID_KEY, CommonUtil.USER_ID_NEW);
        obj.put("graphId", GraphDetailsActivity.graphid);
        obj.put("graphName",GraphDetailsActivity.graphname);
        obj.put("ctype",GraphDetailsActivity.ctype);
        obj.put("frequency",GraphDetailsActivity.trend);
        obj.put("xaxisType",GraphDetailsActivity.group);
        if(Utils.checkifavailable(GraphDetailsActivity.group))
        {
            if(GraphDetailsActivity.group.equalsIgnoreCase("nonTemporal"))
            {
                if(!Utils.checkifavailable(GraphDetailsActivity.subgrp))
                    GraphDetailsActivity.subgrp="date";
            }
        }

        obj.put("xaxis",GraphDetailsActivity.subgrp);
        if(multiplefields)
        {
            obj.put("fields",GraphDetailsActivity.multipleColumn);
        }
        else if(Utils.checkifavailable(ctype))
        {
            if(GraphDetailsActivity.ctype.equalsIgnoreCase("table")||
                    GraphDetailsActivity.ctype.equalsIgnoreCase("metric")||ctype.equalsIgnoreCase("tablewithbucket") ||
                    ctype.equalsIgnoreCase("tablewithdynamicbucket"))
            {

                obj.put("fields",GraphDetailsActivity.multipleMetricColumns);
            }
            else
            {
                obj.put("fields",GraphDetailsActivity.column);
            }
        }

        else
        {
            obj.put("fields",GraphDetailsActivity.column);
        }

        obj.put("drillFilter","");
        obj.put("mindate",GraphDetailsActivity.mindate);
        obj.put("maxdate",GraphDetailsActivity.maxdate);
        obj.put("relative","0");
        obj.put("click","0");
        if(matrixList!=null && matrixList.size()>0) {
            obj.put("matrix", matrixList.get(0).get("name"));
        }
        obj.put("rqFor","");
        System.out.println("response qlistfilterlist "+ filterList);

        if(filterList!=null && filterList.size()>0) {

            if(filterAndValuesList!=null) {
                if(filterAndValuesList.size()>0) {
                    for (int i = 0; i < filterAndValuesList.size(); i++) {
                        obj.put(filterAndValuesList.get(i).get("name"), filterAndValuesList.get(i).get("val"));
                    }
                }
            }

            //obj.put(filter,filterval);
            System.out.println("response qfilter "+filterAndValuesList);

        }
        if(successFlag)
        {
            if(GraphDetailsActivity.statusval!=null)
                obj.put(GraphDetailsActivity.status,GraphDetailsActivity.statusval);
            System.out.println("response qstatus flag is "+GraphDetailsActivity.statusval);
        }
        if(tableFlag)
        {
            obj.put("lowerIndex",lowerIndex);
            obj.put("upperIndex",upperIndex);
        }
        if(lowerFlag)
        {
            obj.put("rankRange","le@"+lerankRange);
        }
        else if(upperFlag)
        {
            obj.put("rankRange","ge@"+geRankRange);
        }
        else if (btwnFlag)
        {
            obj.put("rankRange","@"+lowerbtwn+"@"+upperbtwn);
        }

        if(ctype.equalsIgnoreCase("table"))
        {
            if(start!=null && length!=null) {
                obj.put("start", start);
                obj.put("length", length);
            }
        }

        Log.d("TAG", "sendGetGraphDataRequest: "+obj);

        AsyncTasks_APIgetData asyncTasks_apIgetData = new AsyncTasks_APIgetData(GraphDetailsActivity.this, this);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
        {
            asyncTasks_apIgetData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,obj);
        }
        else {
            asyncTasks_apIgetData.execute(obj);
        }
        CommonUtil.request = 5;
        tableFlag=false;
        lowerFlag=false;
        upperFlag=false;
        btwnFlag=false;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();

        this.menu=menu;
        // getMenuInflater().inflate(R.menu.graph_newmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {


            case R.id.trending:
                showDialogBox("Trend");
                break;
            case R.id.Selectchart:
                showDialogBox("Graph Type");
                break;
            case R.id.groupingType:
                showDialogBox("Grouping Type");
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.select_date:
                showCalendarDialog();
                break;
            case R.id.select_graph:
                openGraphsBottomsheetdialog();
                break;
            case R.id.select_column:
                showDialogBox("Column");
                break;
            case R.id.select_filteroptions:
                showDialogBox("Filter");


        }
        return true;
    }

    public void captureScreen(View v, String filename) {
        Log.d("TAG", "captureScreen: called");
        View v1 = v.getRootView();
        v1.setDrawingCacheEnabled(true);
        Bitmap bm = Bitmap.createBitmap(v1.getDrawingCache());
        v1.setDrawingCacheEnabled(false);
        String extr = Environment.getExternalStorageDirectory().toString();
        System.out.println("PATH"+ " "+extr);//PATH /storage/emulated/0
        File mFolder = new File(extr);

        if (!mFolder.exists()) {
            mFolder.mkdir();
            System.out.println("created...");
        }

        String strF = mFolder.getAbsolutePath();
        System.out.println(strF);///storage/emulated/0

        File f = new File(strF + "/" + filename + ".png");
        System.out.println("complete name"+" "+f.getAbsolutePath());///storage/emulated/0/Graph.png

        String strMyImagePath = f.getAbsolutePath();
        // save on internal storage to avoid asking for write_external storage permisiion
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(strMyImagePath);
            bm.compress(Bitmap.CompressFormat.PNG, 70, fos);
            fos.flush();
            fos.close();
         /*   MediaStore.Images.Media.insertImage(this.getContentResolver(),
                    strMyImagePath, filename, "screenShot");
*/          Uri uri = Uri.fromFile(f);//Convert file path into Uri for sharing
            String path=uri.getPath();
            Intent intent=new Intent(GraphDetailsActivity.this, EditImageActivity.class);
            intent.putExtra("imgUri",path);

            startActivity(intent);
            /*Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
            intent.putExtra(android.content.Intent.EXTRA_TEXT, "");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(intent, "Share Screenshot"));*/
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (Exception e) {

            e.printStackTrace();
        }
        //   rootView.destroyDrawingCache();
    }

    private void openGraphsBottomsheetdialog() {
        graphs_bottomsheetFragment=new Graphs_BottomsheetFragment();
        graphs_bottomsheetFragment.show(fragmentManager,graphs_bottomsheetFragment.getTag());
        args.putInt("dbposition",dbposition);
        graphs_bottomsheetFragment.setArguments(args);
        graphs_bottomsheetFragment.setCancelable(false);

    }

    private void showCalendarDialog() {
        dialog=new Dialog(this,R.style.AlertDialogTheme);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //  View view=inflater.inflate(R.layout.calendar_layout,null,false);



        dialog.setContentView(R.layout.calendar_layout);
        dialog.setCanceledOnTouchOutside(true);

        Calendar minDate=Utils.setDate(0,2010,1);
        Calendar maxDate=Utils.setDate(11,2050,31);
        calendar = (CalendarPickerView) dialog.findViewById(R.id.calendar_view);
        Button okbutton=dialog.findViewById(R.id.ok);
        Button cancelbutton=dialog.findViewById(R.id.cancel);
        Button select=dialog.findViewById(R.id.select);
        final Date today = new Date();
        System.out.println("selected date "+selected);
        // calendar.init(today, nextYear.getTime())
        calendar.init(minDate.getTime(),maxDate.getTime())

                .inMode(CalendarPickerView.SelectionMode.RANGE) .withSelectedDates(selected);



        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMonthAndYearPicker();
            }
        });
        okbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDates = (ArrayList<Date>)calendar
                        .getSelectedDates();
                selected.clear();

                SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");

                String maxdate=df1.format(selectedDates.get(selectedDates.size()-1));
                String mindate = df1.format(selectedDates.get(0));
                System.out.println("min date"+mindate);
                System.out.println("max date"+maxdate);

                GraphDetailsActivity.maxdate=maxdate;
                GraphDetailsActivity.mindate=mindate;

                selected.add(0,(selectedDates.get(0)));
                selected.add(1,selectedDates.get(selectedDates.size()-1));

                sendGetGraphDataRequest();
                System.out.println("calling from "+1);
             /* if(columnList!=null && columnList.size()>0) {
                  sendGetGraphDataRequest();
                  System.out.println("calling from "+1);
              }
              else
              {
                  Utils.showToast(GraphDetailsActivity.this,"No data available!!");
              }*/
                dialog.dismiss();
            }
        });
        cancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void setMonthAndYearPicker() {



        final Calendar today = Calendar.getInstance();

        final MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(GraphDetailsActivity.this, new MonthPickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(int selectedMonth, int selectedYear) {

                today.set(Calendar.MONTH, selectedMonth);
                today.set(Calendar.YEAR, selectedYear);
                today.set(Calendar.DATE,today.get(Calendar.DATE));

                calendar.selectDate(today.getTime());


            }
        }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));
        builder.setActivatedMonth( today.get(Calendar.MONTH))
                .setYearRange(2010, 2050)
                .setActivatedYear( today.get(Calendar.YEAR))
                .build()
                .show();




    }


    private void showDialogBox(String title) {

        System.out.println("title is"+title);
        dialog = new Dialog(this, R.style.AlertDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.TOP;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        trendradiogroup = (RadioGroup) dialog.findViewById(R.id.radioBtn);
        switch (title) {


            case "Legends":
                showDataOnBasisOfLegends();
                break;
            case "Grouping":

                if(groupingList!=null && groupingList.size()>0)
                {
                    initializeRadioButtonDialog("Grouping");
                    //      openGroupingDialog();
                    addViewsToGroupingDialog(groupingList,GraphDetailsActivity.group);
                    applyClickListenerToRadioGroup(trendradiogroup,"grouping");
                }
                else
                {
                    Utils.showToast(this,"No Options available");
                }
                break;
            case "sub":
                initializeRadioButtonDialog("Sub-Grouping");
                addViewsToGroupingDialog(subList, GraphDetailsActivity.subgrp);
                applyClickListenerToRadioGroup(trendradiogroup,"sub");
                break;

            case "Trend":

                if(trendingList!=null && trendingList.size()>0) {

                    //item.setVisible(true);

                    initializeRadioButtonDialog("Trend");
                    addViewstoRadioGroup(trendingList, "name",GraphDetailsActivity.trend);
                    applyClickListenerToRadioGroup(trendradiogroup,"trend");
                }
                else
                {
                    Utils.showToast(this,"No trending available!!");
                    // item.setVisible(false);
                }

                break;
            case "Graph Type":
                MenuItem item = menu.findItem(R.id.Selectchart);
                if (graphTypes != null && graphTypes.size() > 0) {
                    //item.setVisible(true);

                    initializeRadioButtonDialog("Graph Type");
                    addViewstoRadioGroup(graphTypes, "name", GraphDetailsActivity.ctype);
                    applyClickListenerToRadioGroup(trendradiogroup,"graph");
                }
                else
                {
                    Utils.showToast(this,"No Graph types available!!");
                    // item.setVisible(false);
                }

                break;

            case "Column":
                if (columnList != null && columnList.size() > 0) {
                    String frstCol=columnList.get(0).get("columnType");
                    if(Utils.checkifavailable(frstCol))
                    {
                        if (frstCol.equalsIgnoreCase("singleselect")) {
                            initializeRadioButtonDialog("Column");
                            addViewstoRadioGroup(columnList,"id", GraphDetailsActivity.column);
                            applyClickListenerToRadioGroup(trendradiogroup,"column");
                        } else if (frstCol.equalsIgnoreCase("multiselect")) {
                            dialog.setContentView(R.layout.checkboxdialoglayout);
                            checkboxll = (LinearLayout) dialog.findViewById(R.id.cblayout);
                            linearLayout = (LinearLayout) dialog.findViewById(R.id.linear_layout);
                            checkboxll.removeAllViews();
                            addViewsToCheckboxGroup();
                        }
                    }


               /* if (this.columnList.get(0).get("columnType").equalsIgnoreCase("singleselect")) {
                    addViewstoRadioGroup(columnList,"id");
                    applyClickListenerToRadioGroup(trendradiogroup,"column");

                } else if (this.columnList.get(0).get("columnType").equalsIgnoreCase("multiselect")) {
                    addViewsToCheckboxGroup();
                }*/
                }
                else
                {
                    Utils.showToast(this,"No columns available!!");
                }
                break;
            case "Filter":
                MenuItem item1 = menu.findItem(R.id.select_filteroptions);
                System.out.println("filterlist size "+filterList.size());


                if (filterList != null && filterList.size() > 0) {
                    initializeRadioButtonDialog("Filter");

                    addViewstoRadioGroup2(filterList, "label");
                    trendradiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                        private int index;


                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            datalist.clear();

                            for (int i = 0; i < group.getChildCount(); i++) {
                                RadioButton radioButton = (RadioButton) group.getChildAt(i);

                                if (radioButton.getId() == checkedId) {
                                    String text = String.valueOf(radioButton.getText());
                                    if (Utils.checkifavailable(text)) {
                                        if (!text.equalsIgnoreCase("success"))
                                            filter = text;
                                        GraphDetailsActivity.label = text;


                                        System.out.println("calling from " + 2);
                                        group.removeAllViews();


                                        for (int j = 0; j < filterList.size(); j++) {
                                            String label = (String) filterList.get(i).get("label");
                                            if (label.equalsIgnoreCase(text)) {
                                                index = i;
                                                ind = i;
                                                indexList.add(i);

                                                break;

                                            }
                                        }


                       /* [{filterType=singleselect, data=[{name=BBPS, id=BBPS}, {name=PAYGOV, id=PAYGOV}, {name=Other, id=Other}], label=Transaction},

                        {filterType=multiselect, data=[{name=Fail, id=f}, {name=Success, id=s}], label=Status}]*/
                                        System.out.println("filter data is " + filterData.get(index));
                                        datalist.addAll(filterData.get(index));

                                        System.out.println("data list is" + datalist);
                                        dialog.dismiss();
                                        showDialogBox("filterval");


                                    }
                                }
                            }

                        }
                    });

                }
                else
                {

                    Utils.showToast(this,"No filter option available!!");
                    //item1.setVisible(false);
                }


                break;
            case "filterval":

                if(Utils.checkifavailable(label)) {
                    if (GraphDetailsActivity.label.equalsIgnoreCase("status")) {

                        dialog.setContentView(R.layout.checkboxdialoglayoutwithsearch);
                        textView = dialog.findViewById(R.id.tvTitle);

                        textView.setText(title);

                        final ArrayList<CheckBoxState> statelist = new ArrayList<>();
                        final ArrayList<String> statusVals = new ArrayList<>();
                        // if(checkboxll==null)
                        checkboxll = (LinearLayout) dialog.findViewById(R.id.cblayout);

                        checkboxll.removeAllViews();
                        System.out.println("data list size " + datalist.size() + "   " + datalist);


                        search = (AutoCompleteTextView) dialog.findViewById(R.id.searchoptions);
                        addCheckBoxesDynamicallyForStatus(datalist, statusVals);
                        addSearchFunctionalityInStatus(datalist, statusVals);
                    }
                    else if(Utils.checkifavailable(filterType))
                    {
                        if(filterType.equalsIgnoreCase("Range")||
                                (filterType.equalsIgnoreCase("rank_range")))
                        {
                            showRankRangeDialog();


                        }
                        else if( filterType.equalsIgnoreCase("multiselect"))
                        {
                            showMultiSelectFilterValues("Filter Value");
                        }
                        else
                        {
                            addRestValues();
                        }
                    }
                }



                else {

                    //
                }

                //  dialog.dismiss();


                break;
        }


        dialog.show();


    }

    private void addRestValues() {

        addViewstoRadioGroup(datalist, "name", filter);
        trendradiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                for (int i = 0; i < trendradiogroup.getChildCount(); i++) {
                    RadioButton radioButton = (RadioButton) trendradiogroup.getChildAt(i);

                    if (radioButton.getId() == checkedId) {
                        String text = String.valueOf(radioButton.getText());
                        if (!GraphDetailsActivity.label.equalsIgnoreCase("success")) {


                            filterval = text;
                            System.out.println("response qfilter 1"+filterval);
                        } else {

                            if ((GraphDetailsActivity.status != null)) {
                                GraphDetailsActivity.statusval = text;
                            }


                        }
                        sendGetGraphDataRequest();
                        dialog.dismiss();

                    }
                }
            }
        });
    }

    private void showDataOnBasisOfLegends() {

        if(Utils.checkifavailable(ctype))
        {
            if(ctype.equalsIgnoreCase("tablewithpie"))
            {
                //addLegendsForPieWithTable();
                addLegendsForGraph(tabledatalist,"y",1);
            }
            else if(ctype.equalsIgnoreCase("donut")||(ctype.equalsIgnoreCase("pie")))
            {
                addLegendsForGraph(pieDataColumns,"name",0);
            }
            else if(ctype.equalsIgnoreCase("metric"))
            {
                addLegendsForGraph(tabledatalist,"name",0);
            }
            else if(ctype.equalsIgnoreCase("Multi_Y_Axis_Bar"))
            {
                if(multiYNameList!=null) {
                    if(multiYNameList.size()>0) {
                        legendsList=new ArrayList<>();
                        legendsList.addAll(multiYNameList);
                    }
                }
            }

            else
            {

                if(graphFinalList!=null) {
                    if (graphFinalList.size() > 0) {
                        legendsList = new ArrayList<>();
                        if (graphList != null) {
                            if (graphList.size() > 0) {
                                for (int a = 0; a < graphFinalList.size(); a++) {


                                    legendsList.add(String.valueOf(Utils.separateCommaSeparatedString(graphList.get(a).get("name"))));
                                }
                            }
                        }
                    }
                }


            }
        }


        if(legendsList!=null)
        {
            if(legendsList.size()>0)
            {
                initializeRadioButtonDialog("Select a value");
                addViewsToGroupingDialog(legendsList, "");
                applyClickListenerToRadioGroup(trendradiogroup,"color");
            }
            else
            {
                Utils.showToast(this,"No data available!");
            }
        }
        else
        {
            Utils.showToast(this,"No data available!");
        }


    }

    private void addLegendsForGraph(ArrayList<HashMap<String, String>> datalist, String key,int index) {

        if(datalist!=null) {
            if (datalist.size() > 0) {
                legendsList = new ArrayList<>();


                for (int a = index; a < datalist.size(); a++) {


                    legendsList.add(datalist.get(a).get(key));
                }


            }
        }

    }

    private void addLegendsForDonut() {
        if(pieDataColumns!=null) {
            if (pieDataColumns.size() > 0) {
                legendsList = new ArrayList<>();


                for (int a = 0; a < pieDataColumns.size(); a++) {


                    legendsList.add(pieDataColumns.get(a).get("name"));
                }


            }
        }

    }

    private void addLegendsForPieWithTable() {

        if(tabledatalist!=null) {
            if (tabledatalist.size() > 0) {
                legendsList = new ArrayList<>();


                for (int a = 1; a < tabledatalist.size(); a++) {


                    legendsList.add(tabledatalist.get(a).get("y"));
                }


            }
        }

    }

/*
    private void openGroupingDialog() {




            String grpname = grouptv.getSelectedItem().toString();
            StatsActivity.group=grpname;


            //  Toast.makeText(this, "slecetd item si" +grpname, Toast.LENGTH_SHORT).show();
            System.out.println("sub grouping "+subgroupingList +"grouping list "+groupingList);
            subList=new ArrayList<>();
            if (!grpname.equalsIgnoreCase("none")) {
                if (checkGroupAvailablity(grpWithSubgrpsList, grpname) != null) {
                    subList = new ArrayList<>(checkGroupAvailablity(grpWithSubgrpsList, grpname));

                    System.out.println("new logic sublist size " + subList.size() + subList + "      " + checkGroupAvailablity(grpWithSubgrpsList, grpname));
                }
            }
            else {
                noneSublist = new ArrayList<>();
                try
                {
                    for (int i = 0; i < grpWithSubgrpsList.size(); i++) {

                        System.out.println("none sub before" + grpWithSubgrpsList + "sizee " + grpWithSubgrpsList.size());
*/
/*
[{none=[{xaxis=Category, option=button}]},
                        {none=[{xaxis=Application, option=button}]}, {none=[{xaxis=Service, option=button}]}]
*//*

                        if (grpWithSubgrpsList.get(i).containsKey("none")) {

                            HashMap<String, String> xaxis = new HashMap<>();
                            xaxis.put("xaxis", grpWithSubgrpsList.get(i).get(grpname).get(0).get("xaxis"));
                            xaxis.put("option", grpWithSubgrpsList.get(i).get(grpname).get(1).get("option"));

                            if (xaxis != null) {
                                noneSublist.add(xaxis);
                            }

                        }
                    }
                }
                catch (ArrayIndexOutOfBoundsException ae)
                {
                    ae.printStackTrace();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                System.out.println("none sub list " + noneSublist );
            }
            System.out.println("subList is " +subList );

            if(subList!=null && subList.size()>0)
            {

                if(filterLayout!=null && filterLayout.getChildCount()>0)
                {
                    filterLayout.removeAllViews();
                }
                StatsActivity.subgrp=subList.get(0);
                System.out.println("new logic setting adapter");
                if(grpname.equalsIgnoreCase("Temporal"))
                {
                    searchView.setVisibility(View.GONE);
                    subgrouptv.setVisibility(View.INVISIBLE);
                    if(groupingSpinnerTouched) {
                        sendGetGraphDataRequest();
                        groupingSpinnerTouched=false;
                    }
                }
                else if(grpname.equalsIgnoreCase("Demography") || (grpname.equalsIgnoreCase("Geography"))) {
                    subgrouptv.setVisibility(View.VISIBLE);
                    searchView.setVisibility(View.VISIBLE);


                    ArrayAdapter<String> subgrpspinner = new ArrayAdapter<>(StatsActivity.this, R.layout.spinner_item, subList);
                    subgrpspinner.setDropDownViewResource(R.layout.dialog_singlechoice);
                    subgrouptv.setAdapter(subgrpspinner);
                    if(groupingSpinnerTouched) {
                        sendGetGraphDataRequest();
                        groupingSpinnerTouched=false;
                    }
                }

            }
            else if(noneSublist!=null && noneSublist.size()>0)
            {
                searchView.setVisibility(View.GONE);
                grouptv.setVisibility(View.GONE);
                subgrouptv.setVisibility(View.GONE);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.rightMargin = 8;
                linear = new LinearLayout(StatsActivity.this);
                linear.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                linear.setOrientation(LinearLayout.HORIZONTAL);
                linear.setId(View.generateViewId());
                System.out.println("ids are"+linear.getId());
                // topBarLl.addView(linear);

                for(int i=0;i<noneSublist.size();i++) {
                    if(noneSublist.get(i).get("option").equalsIgnoreCase("button")) {


                        final String subgrp=noneSublist.get(i).get("xaxis");
                        Button myButton = new Button(StatsActivity.this);
                        myButton.setText(subgrp);
                        myButton.setBackgroundResource(R.drawable.btn_bg);
                        myButton.setTextColor(getResources().getColor(R.color.white));
                        myButton.setId(i);
                        myButton.setLayoutParams(params);
                        myButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                StatsActivity.subgrp = subgrp;
                                sendGetGraphDataRequest();
                            }
                        });
                        filterLayout.addView(myButton);
                        // topBarLl.addView(myButton,params);
                    }





                    // }
                }
            }


            */
/*    if(grpname.equalsIgnoreCase("Temporal"))
                {

                   // StatsActivity.subgrp=subgroupingList.get(0).get(0).get("xaxis");
                    subgrouptv.setVisibility(View.INVISIBLE);
                    // if(!firsttime) {
                    sendGetGraphDataRequest();
                    firsttime=false;
                    //  }
                    System.out.println("calling from "+5);


                }

                else if (grpname.equalsIgnoreCase("Demography")) {


                    subgrouptv.setVisibility(View.VISIBLE);


                  //  StatsActivity.subgrp=subgroupingList.get(1).get(0).get("xaxis");




                }
                else if(grpname.equalsIgnoreCase("Geography"))
                {
                    subgrouptv.setVisibility(View.VISIBLE);


                }


*//*






    }
*/

    private void showMultiSelectFilterValues(String title) {
        dialog.setContentView(R.layout.checkboxdialoglayoutwithsearch);
        textView=dialog.findViewById(R.id.tvTitle);

        textView.setText(title);

        final ArrayList<CheckBoxState> statelist=new ArrayList<>();
        final ArrayList<String> statusVals=new ArrayList<>();
        // if(checkboxll==null)
        //ll=(LinearLayout)dialog.findViewById(R.id.searchlayout);
        checkboxll=(LinearLayout)dialog.findViewById(R.id.cblayout);
        linearLayout=(LinearLayout)dialog.findViewById(R.id.linear_layout);
        search=(AutoCompleteTextView)dialog.findViewById(R.id.searchoptions);
        /* addMultipleFiltersLayout(datalist,statusVals);*/

        addCheckBoxesDynamically(datalist,statusVals);
        addSearchFunctionality(datalist,statusVals);


    }

    private void addMultipleFiltersLayout(ArrayList<HashMap<String, Object>> datalist, String key) {

        {

            db_handler.open();

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.rightMargin = 8;
            linear = new LinearLayout(this);
            linear.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linear.setOrientation(LinearLayout.HORIZONTAL);
            linear.setId(View.generateViewId());
            System.out.println("ids are"+linear.getId());
            filterLayout.addView(linear);
            ArrayList<String> filterValues=new ArrayList<>();






            for(int i=0;i<datalist.size();i++) {
                Spinner spinner=new Spinner(this);
                String filterValue= (String) datalist.get(i).get(key);
                filterValues.add(filterValue);
                spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,filterValues));
                linear.addView(spinner);

            }



        }


    }

    private void showRankRangeDialog() {

        lowerbtwn = null;
        upperbtwn=null;
        lerankRange=null;
        geRankRange=null;
        dialog.setContentView(R.layout.range);
        RadioGroup rangeRg=dialog.findViewById(R.id.rangrRadioGrp);
        RadioButton greaterThanRb = dialog.findViewById(R.id.greaterthanrb);
        RadioButton lessThanRb=dialog.findViewById(R.id.lessthanrb);
        RadioButton btwnRb=dialog.findViewById(R.id.betweenrb);

        final EditText greaterThanEt=dialog.findViewById(R.id.greaterthanet);
        final EditText lessThanEt=dialog.findViewById(R.id.lessthanet);
        final EditText btwnet1=dialog.findViewById(R.id.btwn1);
        final EditText btwnet2=dialog.findViewById(R.id.btwn2);

        greaterThanRb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //  Utils.showToast(GraphDetailsActivity.this,"clicked!!!");
                if(Utils.checkifavailable(greaterThanEt.getText().toString()))
                {


                    geRankRange=greaterThanEt.getText().toString();
                    dialog.dismiss();
                    lowerFlag=true;
                    upperFlag=false;
                    btwnFlag=false;

                    sendGetGraphDataRequest();

                }
            }
        });
        lessThanRb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(Utils.checkifavailable(lessThanEt.getText().toString()))
                {
                    lerankRange=lessThanEt.getText().toString();

                    lowerFlag=false;
                    upperFlag=true;
                    btwnFlag=false;
                    dialog.dismiss();
                    sendGetGraphDataRequest();
                }
            }
        });

        btwnRb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lowerbtwn=btwnet1.getText().toString();
                upperbtwn=btwnet2.getText().toString();
                if((Utils.checkifavailable(lowerbtwn))&& (Utils.checkifavailable(upperbtwn)))
                {


                    dialog.dismiss();

                    lowerFlag=false;
                    upperFlag=false;

                    btwnFlag=true;
                    sendGetGraphDataRequest();
                }
            }
        });



    }

    private void initializeRadioButtonDialog(String title) {
        dialog.setContentView(R.layout.dialoglayout);
        trendradiogroup = (RadioGroup) dialog.findViewById(R.id.radioBtn);
        trendradiogroup.removeAllViews();
        textView=dialog.findViewById(R.id.tvTitle);
        textView.setText(title);

    }
    private void addViewsToGroupingDialog(ArrayList<String> groupingList, String selected_btn) {
        trendradiogroup.removeAllViews();

        System.out.println("grouping list is "+groupingList);
        for (int i = 0; i < groupingList.size(); i++) {
            RadioButton radioButton = new RadioButton(this);

            String groupingName=groupingList.get(i);
            if(Utils.checkifavailable(groupingName)) {

                rprms = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);

                radioButton.setText(groupingName);
                trendradiogroup.addView(radioButton, rprms);
                if(Utils.checkifavailable(selected_btn))
                {
                    if(selected_btn.equalsIgnoreCase(groupingName))
                    {
                        radioButton.setChecked(true);
                    }
                }
            }




        }


    }

    private void applyClickListenerToRadioGroup(final RadioGroup trendradiogroup, final String type) {
        System.out.println("radio type is "+type);
        trendradiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < trendradiogroup.getChildCount(); i++) {
                    RadioButton radioButton = (RadioButton) trendradiogroup.getChildAt(i);

                    if (radioButton.getId() == checkedId) {
                        String text = String.valueOf(radioButton.getText());
                        if(type.equalsIgnoreCase("graph"))
                        {


                            if(Utils.checkifavailable(ctype)) {
                                String previousCtype=ctype;
                                GraphDetailsActivity.ctype = text;
                                if (previousCtype.equalsIgnoreCase("area") || previousCtype.equalsIgnoreCase("bar") ||
                                        previousCtype.equalsIgnoreCase("line")) {
                                    if (ctype.equalsIgnoreCase("area") || ctype.equalsIgnoreCase("bar") ||
                                            ctype.equalsIgnoreCase("line")) {

                                        showGraphicalData(selectedVal);
                                    }
                                    else
                                    {
                                        sendGetGraphDataRequest();
                                    }
                                } else {


                                    sendGetGraphDataRequest();
                                }
                            }
                            dialog.dismiss();
                        }
                        else if(type.equalsIgnoreCase("trend"))
                        {
                            System.out.println("selected date range is "+selectedDates);
                            if(selectedDates!=null)
                            {
                                if(selectedDates.size()>1)

                                {
                                    if(Utils.checkifavailable(text))
                                    {
                                        if(text.equalsIgnoreCase("1 minute") || text.equalsIgnoreCase("5 Minute") ||
                                                text.equalsIgnoreCase("10 Minute")|| text.equalsIgnoreCase("15 Minute") ||
                                                text.equalsIgnoreCase("30 Minute"))
                                        {
                                            Utils.showLongToast(GraphDetailsActivity.this,"Large Date Range For "+text +"!!!");
                                            dialog.dismiss();
                                        }
                                        else {
                                            GraphDetailsActivity.trend=text;
                                            sendGetGraphDataRequest();
                                            dialog.dismiss();
                                        }
                                    }
                                }
                                else
                                {
                                    if(Utils.checkifavailable(text))
                                    {
                                        if(text.equalsIgnoreCase("1 minute") || text.equalsIgnoreCase("5 Minute") ||
                                                text.equalsIgnoreCase("10 Minute")|| text.equalsIgnoreCase("15 Minute") ||
                                                text.equalsIgnoreCase("30 Minute"))
                                        {

                                            GraphDetailsActivity.trend=text;
                                            sendGetGraphDataRequest();
                                            dialog.dismiss();

                                        }
                                        else {
                                            Utils.showLongToast(GraphDetailsActivity.this,"Insufficient Data Range For "+text +"!!!");
                                            dialog.dismiss();
                                        }
                                    }
                                }
                            }

                        }
                        else if(type.equalsIgnoreCase("grouping"))
                        {
                            GraphDetailsActivity.group=text;
                            openSubGroupingDialog(text);
                        }
                        else if(type.equalsIgnoreCase("sub"))
                        {
                            GraphDetailsActivity.subgrp=text;
                            sendGetGraphDataRequest();
                            dialog.dismiss();

                        }
                        else if(type.equalsIgnoreCase("color")) {

                            showColorPickerDialog(text,i);
                        }
                        else
                        {
                            GraphDetailsActivity.column=text;
                            sendGetGraphDataRequest();
                            dialog.dismiss();

                        }


                        System.out.println("calling from " + 2);

                    }
                }

            }
        });


    }

    private void openSubGroupingDialog(String grpname) {
        System.out.println("text selected is "+grpname);

        subList=new ArrayList<>();
        if (!grpname.equalsIgnoreCase("none")) {
            if (checkGroupAvailablity(grpWithSubgrpsList, grpname) != null) {
                subList = new ArrayList<>(checkGroupAvailablity(grpWithSubgrpsList, grpname));

                System.out.println("new logic sublist size " + subList.size() + subList + "      " + checkGroupAvailablity(grpWithSubgrpsList, grpname));
            }
        }
        else {
            noneSublist = new ArrayList<>();
            try {
                for (int i = 0; i < grpWithSubgrpsList.size(); i++) {

                    System.out.println("none sub before" + grpWithSubgrpsList + "sizee " + grpWithSubgrpsList.size());
/*
[{none=[{xaxis=Category, option=button}]},
                        {none=[{xaxis=Application, option=button}]}, {none=[{xaxis=Service, option=button}]}]
*/
                    if (grpWithSubgrpsList.get(i).containsKey("none")) {

                        HashMap<String, String> xaxis = new HashMap<>();
                        xaxis.put("xaxis", grpWithSubgrpsList.get(i).get(grpname).get(0).get("xaxis"));
                        xaxis.put("option", grpWithSubgrpsList.get(i).get(grpname).get(1).get("option"));

                        if (xaxis != null) {
                            noneSublist.add(xaxis);
                        }

                    }
                }
            } catch (ArrayIndexOutOfBoundsException ae) {
                ae.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("none sub list " + noneSublist);
        }

        if(subList!=null && subList.size()>0)
        {

            if(filterLayout!=null && filterLayout.getChildCount()>0)
            {
                filterLayout.removeAllViews();
            }
            //GraphDetailsActivity.subgrp=subList.get(0);
            System.out.println("new logic setting adapter");
            if(grpname.equalsIgnoreCase("Temporal"))
            {
                searchView.setVisibility(View.GONE);
                subgrouptv.setVisibility(View.INVISIBLE);
                if(subList!=null && subList.size()>0) {
                    GraphDetailsActivity.subgrp = subList.get(0);
                }
                sendGetGraphDataRequest();

            }
            else if(grpname.equalsIgnoreCase("Demography") || (grpname.equalsIgnoreCase("Geography"))) {
                subgrouptv.setVisibility(View.VISIBLE);
                searchView.setVisibility(View.VISIBLE);
                dialog.dismiss();
                showDialogBox("sub");


            }

            else if(grpname.equalsIgnoreCase("nontemporal"))
            {
                GraphDetailsActivity.ctype="tablewithpie" ;
                if(subList!=null && subList.size()>0) {
                    GraphDetailsActivity.subgrp = subList.get(0);
                }
                sendGetGraphDataRequest();



            }


        }
        else if(noneSublist!=null && noneSublist.size()>0)
        {
            searchView.setVisibility(View.GONE);
            grouptv.setVisibility(View.GONE);
            subgrouptv.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.rightMargin = 8;
            linear = new LinearLayout(GraphDetailsActivity.this);
            linear.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linear.setOrientation(LinearLayout.HORIZONTAL);
            linear.setId(View.generateViewId());
            System.out.println("ids are"+linear.getId());
            // topBarLl.addView(linear);

            for(int i=0;i<noneSublist.size();i++) {
                if(noneSublist.get(i).get("option").equalsIgnoreCase("button")) {


                    final String subgrp=noneSublist.get(i).get("xaxis");
                    Button myButton = new Button(GraphDetailsActivity.this);
                    myButton.setText(subgrp);
                    myButton.setBackgroundResource(R.drawable.btn_bg);
                    myButton.setTextColor(getResources().getColor(R.color.white));
                    myButton.setId(i);
                    myButton.setLayoutParams(params);
                    myButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            GraphDetailsActivity.subgrp = subgrp;
                            sendGetGraphDataRequest();
                        }
                    });
                    filterLayout.addView(myButton);
                    // topBarLl.addView(myButton,params);
                }





                // }
            }
        }


    }

    private void setAdapterToList(ArrayList<HashMap<String, String>> clickedList, String filterName, int finalI,
                                  HashMap<String, String> paramslist) {


        final ArrayList<String> statusVals=new ArrayList<>();

        dialog = new Dialog(GraphDetailsActivity.this, R.style.AlertDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        dialog.setContentView(R.layout.listwithsearchview);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
        search=(AutoCompleteTextView)dialog.findViewById(R.id.searchoptions);
        rvTest = (RecyclerView) dialog.findViewById(R.id.listView);
        rvTest.setHasFixedSize(true);
        rvTest.setLayoutManager(new LinearLayoutManager(GraphDetailsActivity.this));
        HashMap<String,String> paramHp=new HashMap<>();
       /* if(params!=null && params.size()>0) {
            ArrayList<String> keySet = new ArrayList<>(params.keySet());
            if (keySet != null && keySet.size() > 0) {
                for (int i = 0; i < keySet.size(); i++) {
                    System.out.println("key set and filtername "+keySet +filterName);
                    if (keySet.get(i).equalsIgnoreCase(filterName)) {
                        hp.put(filterName, params.get(filterName));
                    }
                }
            }
        }
        else
        {*/
        hp.put(filterName,"");
        // }
        checkedValues=new ArrayList<>();

        if(paramslist !=null && paramslist.size()>0) {
            System.out.println("checked values before sending "+ paramslist.values());
            checkedValues.addAll(paramslist.values());
        }
        rvAdapter = new MultiFilterAdapter(GraphDetailsActivity.this,
                clickedList,filterNamesList,allTablesData,filterLayout,filterName,hp,finalI,
                new ArrayList[filterNamesList.size()],checkedValues);
        rvTest.setAdapter(rvAdapter);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                rvAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });




    }

    private void addSpinnersForMultipleFilters() {
        {
            allTablesData=new ArrayList<>();
            filterNamesList=new ArrayList<>();
            db_handler.open();
            System.out.println("filterDetails Are "+filterDetails);
            for(int i=0;i<filterDetails.size();i++)
            {
                filterNamesList.add(filterDetails.get(i).get("name"));
                System.out.println("filterNames Are "+filterNamesList);
            }

            //   params=new ArrayList<>();
            for(int j=0;j<filterNamesList.size();j++)
            {
                final ArrayList<HashMap<String, String>> filterValues=new ArrayList<>();
                Cursor cursor= db_handler.getAllDataForMultiFilterTable(filterNamesList.get(j).toUpperCase());





                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        HashMap<String,String> filterData=new HashMap<>();
                        String prevId = cursor.getString(0);
                        String id=cursor.getString(1);
                        String name=cursor.getString(2);
                        filterData.put("prevId",prevId);
                        filterData.put("id",id);
                        filterData.put("name",name);
                        filterData.put("state","f");

                        filterValues.add(filterData);

                    }
                }
                allTablesData.add(filterValues);



            }
            for(int i=0;i<filterNamesList.size();i++)
            {

                final ArrayList<HashMap<String, String>> filterValues=new ArrayList<>();
                final Spinner spinner=new Spinner(this);
                Button myButton = new Button(this);
                myButton.setText(filterNamesList.get(i));
                myButton.setBackgroundResource(R.drawable.btn_bg);
                myButton.setTextColor(getResources().getColor(R.color.white));
                myButton.setId(i);


                final int finalI = i;
                final int finalI1 = i;
                // linear.addView(myButton);
                filterLayout.addView(myButton);



                final int finalI2 = i;
                myButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickedList=new ArrayList<>();

                        clickedList.addAll(allTablesData.get(finalI));
                        setAdapterToList(clickedList,filterNamesList.get(finalI),finalI,params);


                       /* showSubOptionsForMultiFilter(allTablesData.get(finalI1));
                        addSearchFunctionalityForMultipleFilters(allTablesData.get(finalI1));*/



                    }
                });


            }

            db_handler.close();









        }




    }

    private void addSearchFunctionalityInStatus(final ArrayList<HashMap<String, String>> datalist, final ArrayList<String> statusVals) {
        {

            search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if(s==null || s.length()==0)
                    {
                        addCheckBoxesDynamicallyForStatus(datalist,statusVals);
                    }
                    else
                    {
                        filteredSearchList=new ArrayList<>();
                        for(int i=0;i<datalist.size();i++) {

                            if (datalist.get(i).get("name").toUpperCase().contains(s.toString().toUpperCase()))
                            // if (String.valueOf(s).equalsIgnoreCase(datalist.get(i).get("name")))
                            {

                                filteredSearchList.add(datalist.get(i));
                                addCheckBoxesDynamically(filteredSearchList,statusVals);
                            }

                        }


                    }


                    // getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


        }


    }

    private void addCheckBoxesDynamicallyForStatus(final ArrayList<HashMap<String, String>> datalist, final ArrayList<String> statusVals) {

        for (int i = 0; i < datalist.size(); i++) {
            System.out.println("data list in for loop"+datalist.get(i).get("state"));

            final CheckBox cb = new CheckBox(GraphDetailsActivity.this);

            cb.setText(datalist.get(i).get("name"));
            cb.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            checkboxll.addView(cb);
            System.out.println("count is "+checkboxll.getChildCount());

            if(datalist.get(i).get("state").equalsIgnoreCase("f"))
            {
                cb.setChecked(false);

            }
            else if(datalist.get(i).get("state").equalsIgnoreCase("t"))
            {
                cb.setChecked(true);
                statusVals.add(datalist.get(i).get("id"));
            }
            final int finalI = i;

            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(!cb.isChecked()) {

                        //  if (statusVals.size()>1) {

                        cb.setChecked(false);
                        filterData.get(ind).get(finalI).put("state", "f");
                        datalist.get(finalI).put("state", "f");
                        statusVals.remove(datalist.get(finalI).get("id"));



                    }
                    else
                    {

                        cb.setChecked(true);
                        datalist.get(finalI).put("state", "t");
                        filterData.get(ind).get(finalI).put("state", "t");
                        statusVals.add(datalist.get(finalI).get("id"));






                    }
                    HashSet<String> hashSet = new HashSet<String>(statusVals);
                    statusVals.clear();

                    statusVals.addAll(hashSet);
                    if(statusVals.size()>0) {

                        GraphDetailsActivity.statusval = TextUtils.join(",", statusVals);
                    }
                    sendGetGraphDataRequest();
                    dialog.dismiss();
                    for(int i=0;i<datalist.size();i++)
                    {
                        System.out.println("checkbox vals "+datalist.get(i).get("state"));
                    }


                    /*System.out.println("status vals are "+statusVals +datalist.get());*/


                }
            });
        }



    }

    private void addSearchFunctionality(final ArrayList<HashMap<String, String>> datalist, final ArrayList<String> statusVals) {

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s==null || s.length()==0)
                {
                    addCheckBoxesDynamically(datalist,statusVals);
                }
                else
                {
                    filteredSearchList=new ArrayList<>();
                    for(int i=0;i<datalist.size();i++) {
                        if (String.valueOf(s).equalsIgnoreCase(datalist.get(i).get("name")))
                        {

                            filteredSearchList.add(datalist.get(i));
                            addCheckBoxesDynamically(filteredSearchList,statusVals);
                        }

                    }


                }


                // getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void addCheckBoxesDynamically(final ArrayList<HashMap<String, String>> datalist, final ArrayList<String> statusVals) {
        checkboxll.removeAllViews();
        finalFilterValues.clear();
        for (int i = 0; i < datalist.size(); i++) {
            System.out.println("data list in for loop"+datalist.get(i).get("state"));

            final CheckBox cb = new CheckBox(GraphDetailsActivity.this);

            cb.setText(datalist.get(i).get("name"));
            cb.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            checkboxll.addView(cb);
            System.out.println("count is "+checkboxll.getChildCount());
            final int finalI1 = i;


            if(datalist.get(i).get("state").equalsIgnoreCase("f"))
            {
                cb.setChecked(false);

            }
            else if(datalist.get(i).get("state").equalsIgnoreCase("t"))
            {
                cb.setChecked(true);
                statusVals.add(datalist.get(i).get("id"));
            }
            final int finalI = i;

            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(!cb.isChecked()) {
                        cb.setChecked(false);
                        filterData.get(ind).get(finalI).put("state", "f");
                        datalist.get(finalI).put("state", "f");
                        statusVals.remove(datalist.get(finalI).get("id"));
                        finalFilterValues.remove(datalist.get(finalI).get("id"));
                    }
                    else
                    {

                        cb.setChecked(true);
                        datalist.get(finalI).put("state", "t");

                        statusVals.add(datalist.get(finalI).get("id"));






                    }
                    HashSet<String> hashSet = new HashSet<String>(statusVals);
                    statusVals.clear();

                    statusVals.addAll(hashSet);
                    finalFilterValues.addAll(statusVals);
                    HashMap<String,String> filterHp=new HashMap<>();
                    if(finalFilterValues.size()>0) {


                        filterval = TextUtils.join(",", finalFilterValues);


                        System.out.println("response qfilter 2"+filterval);
                    }
                    else
                    {

                        filterval=null;
                        System.out.println("response qfilter 3"+filterval);
                    }
                    if(filterAndValuesList.size()>0)
                    {
                        for(int i=0;i<filterAndValuesList.size();i++)
                        {
                            if(Utils.checkifavailable(filter)) {
                                if (filterAndValuesList.get(i).get("name").equalsIgnoreCase(filter)) {
                                    filterAndValuesList.remove(i);
                                    break;
                                }
                            }
                        }
                    }
                    if(Utils.checkifavailable(filterval)) {
                        filterHp.put("name", filter);
                        filterHp.put("val", filterval);
                        filterAndValuesList.add(filterHp);
                    }
                    System.out.println("status values are "+filterAndValuesList);

                    //  GraphDetailsActivity.statusval = TextUtils.join(",",statusVals) ;
                    sendGetGraphDataRequest();
                    //dialog.dismiss();
                    for(int i=0;i<datalist.size();i++)
                    {
                        System.out.println("checkbox vals "+datalist.get(i).get("state"));
                    }


                    /*System.out.println("status vals are "+statusVals +datalist.get());*/


                }
            });
        }


    }

    private void addViewstoRadioGroup2(ArrayList<HashMap<String, Object>> List, String key) {
        rprms = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < List.size(); i++) {
            RadioButton radioButton = new RadioButton(this);
            String lab=String.valueOf( List.get(i).get(key));
/*            if(lab.contains("#")) {
                String[] sepLabel = lab.split("#");
                ArrayList<String> sepList = new ArrayList<>(Arrays.asList(sepLabel));
                for (String j : sepList) {
                    RadioButton radioButton1 = new RadioButton(this);
                    System.out.println("filter item is "+j);

                    radioButton1.setText(j);
                    if(trendradiogroup!=null)
                        trendradiogroup.addView(radioButton1, rprms);
                }

            }

            else {*/
            System.out.println("trendradio group is "+trendradiogroup);
            String label_key= String.valueOf(List.get(i).get(key));
            if(Utils.checkifavailable(label))
            {
                if(Utils.checkifavailable(label_key))
                {
                    if(label.equalsIgnoreCase(label_key))
                    {
                        radioButton.setChecked(true);
                    }
                }
            }
            radioButton.setText(label_key);

            if(trendradiogroup!=null)
                trendradiogroup.addView(radioButton, rprms);
            // }




        }
    }

    private void addViewsToCheckboxGroup() {
        colum.clear();


        if(locallist!=null && locallist.size()>0) {
            for (int i = 0; i < locallist.size(); i++) {
                final CheckBox cb = new CheckBox(GraphDetailsActivity.this);
                final HashMap<String, String> hashMap = new HashMap<>();
                cb.setText(locallist.get(i).get("id"));
                cb.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                final int finalI = i;
                if (locallist.get(finalI).get("state").equalsIgnoreCase("f")) {
                    cb.setChecked(false);
                } else {

                    cb.setChecked(true);
                    colum.add(locallist.get(finalI).get("id"));

                }

                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            cb.setChecked(true);

                            String col = locallist.get(finalI).get("id");
                            System.out.println("state getting local item "+col);
                            hashMap.put("state", "t");
                            if(!colum.contains(col)) {

                                colum.add(col);
                                GraphDetailsActivity.multipleColumn = TextUtils.join(",", colum);
                            }



                        } else {

                            System.out.println("column size "+colum.size() + colum );
                            if(colum.size()>1) {
                                cb.setChecked(false);
                                hashMap.put("state", "f");

                                colum.remove(locallist.get(finalI).get("id"));
                            }
                            else
                            {
                                cb.setChecked(true);
                                hashMap.put("state", "t");
                            }
                            System.out.println("column list before clearing is  "+colum);

                            HashSet<String> hashSet = new HashSet<String>(colum);
                            colum.clear();

                            colum.addAll(hashSet);
                            System.out.println("column list after clearing is  "+colum);

                            GraphDetailsActivity.multipleColumn = TextUtils.join(",", colum);


                            //   GraphDetailsActivity.multipleColumn.
                        }

                        multiplefields = true;
                        System.out.println("state multiple fields " + GraphDetailsActivity.multipleColumn +"and column array list is "+colum);
                        sendGetGraphDataRequest();
                        System.out.println("calling from "+3);

                    }
                });
                if (colum.size() > 0)
                    GraphDetailsActivity.column = colum.get(colum.size() - 1);

                checkboxll.addView(cb);


            }
        }
    }

    private void setTrending(String trend) {
        GraphDetailsActivity.trend=trend;
        // if(!firsttime) {
        System.out.println("trendspinner flag "+trendSpinnerTouched);
        if(trendSpinnerTouched) {
            sendGetGraphDataRequest();
            trendSpinnerTouched=false;
        }
        // }
        System.out.println("calling from "+4);
//        dialog.dismiss();
    }

    private void addViewstoRadioGroup(ArrayList<HashMap<String, String>> list, String key, String selected_btn) {

        for (int i = 0; i < list.size(); i++) {
            RadioButton radioButton = new RadioButton(this);

            /*   radioButton.setHighlightColor(getResources().getColor(R.color.app_theme));*/
            System.out.println("name " + selected_btn);
            rprms = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            if(Utils.checkifavailable(GraphDetailsActivity.group)) {

                if (GraphDetailsActivity.group.equalsIgnoreCase("Temporal")) {
                    if (!(list.get(i).get(key).contains("tablewithpercent"))) {



                        radioButton.setText(list.get(i).get(key));
                        trendradiogroup.addView(radioButton, rprms);
                        if(Utils.checkifavailable(selected_btn))
                        {
                            if(list.get(i).get(key).equalsIgnoreCase(selected_btn))
                                radioButton.setChecked(true);

                        }
                    }
                } else {
                    radioButton.setText(list.get(i).get(key));
                    trendradiogroup.addView(radioButton, rprms);
                    if(Utils.checkifavailable(selected_btn))
                    {
                        if(list.get(i).get(key).equalsIgnoreCase(selected_btn))
                            radioButton.setChecked(true);
                    }
                }
            }
            else
            {
                radioButton.setText(list.get(i).get(key));
                trendradiogroup.addView(radioButton, rprms);
                if(Utils.checkifavailable(selected_btn))
                {
                    if(list.get(i).get(key).equalsIgnoreCase(selected_btn))
                        radioButton.setChecked(true);
                }
                System.out.println("name of graph in else" + list.get(i).get(key));
            }



        }
    }


    private void loadFragment(Fragment fragment, HashMap<String, String> selectedValue) {

        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        args.putString("graphName", name);
        System.out.println("chart title is "+chartTitleList);
        //actionBar.setTitle(chartTitleList.get(0).get("name"));
        Bundle bundle=new Bundle();

        if(GraphDetailsActivity.ctype.equalsIgnoreCase("tablewithpercent")||
                GraphDetailsActivity.ctype.equalsIgnoreCase("tablewithpie") ||
                GraphDetailsActivity.ctype.equalsIgnoreCase("metric"))
        {
            bundle.putSerializable("tablelist",tabledatalist);
            bundle.putSerializable("colnameslist",colNamesList);
            bundle.putSerializable("percentDatalist",this.tablePercentDataList);
            bundle.putString("totalCount",this.totalCount);
            bundle.putString("lowerIndex",this.lowerIndex);
            bundle.putString("upperIndex",this.upperIndex);
            bundle.putSerializable("pieDataColums",this.pieDataColumns);
            bundle.putSerializable("pieDataNamesList",this.pieDataNamesList);
            bundle.putSerializable("tableAllColumnsDataList",this.tableAllColumnsDataList);
            bundle.putSerializable("selectedVal",selectedValue);
            System.out.println("table in graphdetails "+this.tableAllColumnsDataList);


        }else if(ctype.equalsIgnoreCase("table")||
                ctype.equalsIgnoreCase("tablewithbucket")
                || ctype.equalsIgnoreCase("tablewithdynamicbucket"))
        {

            bundle.putLong("initialIndex",initialIndex);
            bundle.putString("pageNum",pageNum);
            bundle.putLong("totalCount",recordsTotal);
            bundle.putSerializable("tableAllColumnsDataList",this.tableAllColumnsDataList);
            bundle.putString("start",start);
            bundle.putString("length",length);

        }
        else if(GraphDetailsActivity.ctype.equalsIgnoreCase("pie"))
        {
            bundle.putSerializable("pieDataColums",this.pieDataColumns);
            bundle.putSerializable("pieDataNamesList",this.pieDataNamesList);
        }

        else if(GraphDetailsActivity.ctype.equalsIgnoreCase("donut"))
        {
            bundle.putSerializable("donutDataColums",this.pieDataColumns);
            bundle.putSerializable("donutDataNamesList",this.pieDataNamesList);
        }
        else  if(GraphDetailsActivity.ctype.equalsIgnoreCase("Multi_Y_Axis_Bar"))
        {
            bundle.putSerializable("namesList",multiYNameList);
            bundle.putSerializable("dataList",mainDataMultiYList);
            bundle.putSerializable("legendsList",legendsList);
        }
        else
        {
            bundle.putSerializable("columnWiseList",columnWiseList);
            bundle.putSerializable("graphFinalList",graphFinalList);
            bundle.putSerializable("columnsList",columnList);
            bundle.putSerializable("graphList",graphList);
            bundle.putSerializable("selectedVal",selectedValue);
            System.out.println("selected val sending 2 "+selectedValue);
            setListenerToSeacrhView();


        }

        bundle.putString("trend",this.trend);
        bundle.putString("group",group);
        bundle.putString("columnName",column);
        bundle.putString("subgroup",subgrp);
        bundle.putString("ctype",ctype);

        fragment.setArguments(bundle);
      /*  if (dialog != null)
            dialog.dismiss();*/

    }

    private void setListenerToSeacrhView() {

        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchRecycler.setVisibility(View.VISIBLE);
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {

            case R.id.floatingActionButton:
                showDialogBox("Legends");

                break;
            case R.id.Selectchart:
                showDialogBox("Graph Type");
                break;
            case R.id.groupingType:
                showDialogBox("Grouping Type");
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.select_date:

                showCalendarDialog();


                break;
            case R.id.select_graph:
                openGraphsBottomsheetdialog();
                break;
            case R.id.select_column:
                showDialogBox("Column");
                break;
            case R.id.select_graph1:
                showDialogBox("Graph Type");
                break;
            case R.id.select_filteroptions:
                showDialogBox("Filter");
                break;
            case R.id.select_filteroptions1:
                showDialogBox("Filter");
                break;
            case R.id.trend:
                showDialogBox("Trend");
                break;
            case R.id.view:
                showDialogBox("Grouping");
                break;
            case R.id.resetfilter:
                resetFilter();
                break;
            case R.id.shareIv:
                rootView = getWindow().getDecorView().findViewById(android.R.id.content);
                String filename = "Graph";
                captureScreen( rootView, Utils.getRandomString());
        }

    }

    private void resetFilter() {

        if(datalist.size()>0) {
            for (int i = 0; i < datalist.size(); i++) {
                if (datalist.get(i).get("state").equalsIgnoreCase("t")) {
                    datalist.get(i).put("state", "f");
                    //filterData.get(ind).get(i).put("state", "f");

                }
            }
            if(filterData!=null)
            {
                if(filterData.size()>0)
                {
                    for(int i=0;i<filterData.size();i++)
                    {
                        for(int j=0;j<filterData.get(i).size();j++)
                        {
                            String filter=filterData.get(i).get(j).get("state");
                            if(Utils.checkifavailable(filter)) {
                                if (filter.equalsIgnoreCase("t")) {
                                    filterData.get(i).get(j).put("state", "f");
                                }
                            }
                        }
                    }
                }
            }

            filterval = null;
            if (filterAndValuesList.size() > 0)
                filterAndValuesList.clear();
            System.out.println("response qfilter 4" + filterval);
            sendGetGraphDataRequest();
        }

    }

    private void showColorPickerDialog(final String text, int index) {

        if(colorsList!=null)
        {
            if(colorsList.size()>0)
            {
                DefaultColor= colorsList.get(index);
            }
        }
        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(GraphDetailsActivity.this, DefaultColor, false, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog ambilWarnaDialog, int color) {

                //DefaultColor = color;
                if(changeColorListener!=null)
                    changeColorListener.changeColor(color,text);
                if(dialog!=null) {
                    if(dialog.isShowing())
                        dialog.dismiss();
                }

            }

            @Override
            public void onCancel(AmbilWarnaDialog ambilWarnaDialog) {


            }
        });
        ambilWarnaDialog.show();

    }



            /* case R.id.grouping:
                showDialogBox("Grouping Type");
                break;
            case R.id.trending:
                showDialogBox("Trend");
                break;
            case R.id.subgrouping:
                showDialogBox("Sub Grouping");
                break;
            case R.id.column:
                showDialogBox("Column");
                break;*/






    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getId())
        {
            case R.id.grouping:
                String grpname = grouptv.getSelectedItem().toString();
                GraphDetailsActivity.group=grpname;
/*
                if(GraphDetailsActivity.group!=null) {
                    if (GraphDetailsActivity.group.equalsIgnoreCase("Temporal")) {
                        searchView.setVisibility(View.GONE);

                       // searchRecycler.setVisibility(View.GONE);
                    } else {
                        searchView.setVisibility(View.VISIBLE);
                      //  searchRecycler.setVisibility(View.VISIBLE);
                    }
                }
*/
                //  Toast.makeText(this, "slecetd item si" +grpname, Toast.LENGTH_SHORT).show();
                System.out.println("sub grouping "+subgroupingList +"grouping list "+groupingList);
                subList=new ArrayList<>();
                if (!grpname.equalsIgnoreCase("none")) {
                    if (checkGroupAvailablity(grpWithSubgrpsList, grpname) != null) {
                        subList = new ArrayList<>(checkGroupAvailablity(grpWithSubgrpsList, grpname));

                        System.out.println("new logic sublist size " + subList.size() + subList + "      " + checkGroupAvailablity(grpWithSubgrpsList, grpname));
                    }
                }
                else {
                    noneSublist = new ArrayList<>();
                    try
                    {
                        for (int i = 0; i < grpWithSubgrpsList.size(); i++) {

                            System.out.println("none sub before" + grpWithSubgrpsList + "sizee " + grpWithSubgrpsList.size());
/*
[{none=[{xaxis=Category, option=button}]},
                        {none=[{xaxis=Application, option=button}]}, {none=[{xaxis=Service, option=button}]}]
*/
                            if (grpWithSubgrpsList.get(i).containsKey("none")) {

                                HashMap<String, String> xaxis = new HashMap<>();
                                xaxis.put("xaxis", grpWithSubgrpsList.get(i).get(grpname).get(0).get("xaxis"));
                                xaxis.put("option", grpWithSubgrpsList.get(i).get(grpname).get(1).get("option"));

                                if (xaxis != null) {
                                    noneSublist.add(xaxis);
                                }

                            }
                        }
                    }
                    catch (ArrayIndexOutOfBoundsException ae)
                    {
                        ae.printStackTrace();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    System.out.println("none sub list " + noneSublist );
                }
                System.out.println("subList is " +subList );

                if(subList!=null && subList.size()>0)
                {

                    if(filterLayout!=null && filterLayout.getChildCount()>0)
                    {
                        filterLayout.removeAllViews();
                    }
                    GraphDetailsActivity.subgrp=subList.get(0);
                    System.out.println("new logic setting adapter");
                    if(grpname.equalsIgnoreCase("Temporal"))
                    {
                        searchView.setVisibility(View.GONE);
                        subgrouptv.setVisibility(View.INVISIBLE);
                        if(groupingSpinnerTouched) {
                            sendGetGraphDataRequest();
                            groupingSpinnerTouched=false;
                        }
                    }
                    else if(grpname.equalsIgnoreCase("Demography") || (grpname.equalsIgnoreCase("Geography"))) {
                        subgrouptv.setVisibility(View.VISIBLE);
                        searchView.setVisibility(View.VISIBLE);


                        ArrayAdapter<String> subgrpspinner = new ArrayAdapter<>(this, R.layout.spinner_item, subList);
                        subgrpspinner.setDropDownViewResource(R.layout.dialog_singlechoice);
                        subgrouptv.setAdapter(subgrpspinner);
                    }

                }
                else if(noneSublist!=null && noneSublist.size()>0)
                {
                    searchView.setVisibility(View.GONE);
                    grouptv.setVisibility(View.GONE);
                    subgrouptv.setVisibility(View.GONE);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.rightMargin = 8;
                    linear = new LinearLayout(this);
                    linear.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    linear.setOrientation(LinearLayout.HORIZONTAL);
                    linear.setId(View.generateViewId());
                    System.out.println("ids are"+linear.getId());
                    // topBarLl.addView(linear);

                    for(int i=0;i<noneSublist.size();i++) {
                        if(noneSublist.get(i).get("option").equalsIgnoreCase("button")) {


                            final String subgrp=noneSublist.get(i).get("xaxis");
                            Button myButton = new Button(this);
                            myButton.setText(subgrp);
                            myButton.setBackgroundResource(R.drawable.btn_bg);
                            myButton.setTextColor(getResources().getColor(R.color.white));
                            myButton.setId(i);
                            myButton.setLayoutParams(params);
                            myButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    GraphDetailsActivity.subgrp = subgrp;
                                    sendGetGraphDataRequest();
                                }
                            });
                            filterLayout.addView(myButton);
                            // topBarLl.addView(myButton,params);
                        }





                        // }
                    }
                }


            /*    if(grpname.equalsIgnoreCase("Temporal"))
                {

                   // GraphDetailsActivity.subgrp=subgroupingList.get(0).get(0).get("xaxis");
                    subgrouptv.setVisibility(View.INVISIBLE);
                    // if(!firsttime) {
                    sendGetGraphDataRequest();
                    firsttime=false;
                    //  }
                    System.out.println("calling from "+5);


                }

                else if (grpname.equalsIgnoreCase("Demography")) {


                    subgrouptv.setVisibility(View.VISIBLE);


                  //  GraphDetailsActivity.subgrp=subgroupingList.get(1).get(0).get("xaxis");




                }
                else if(grpname.equalsIgnoreCase("Geography"))
                {
                    subgrouptv.setVisibility(View.VISIBLE);


                }


*/

                break;
            case R.id.trending:
                String text = trendspinner.getSelectedItem().toString();

                setTrending(text);
                break;
            case R.id.subgrouping:

                GraphDetailsActivity.subgrp =  subgrouptv.getSelectedItem().toString();
                if(subGroupingTouched) {
                    sendGetGraphDataRequest();
                    subGroupingTouched=false;
                }
                System.out.println("calling from "+6);
                break;


        }

    }

    private ArrayList<String> checkGroupAvailablity(ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>> grpWithSubgrpsList, String grpname) {
        ArrayList<String> sublist = null;
        for(int i=0;i<grpWithSubgrpsList.size();i++)
        {
            System.out.println("new logic sub grps all "+grpWithSubgrpsList);
            sublist=new ArrayList<>();
            if(grpWithSubgrpsList.get(i).containsKey(grpname))
            {
                System.out.println("new logic sub groups for "+grpWithSubgrpsList.get(i).get(grpname));
                for(int j=0;j<grpWithSubgrpsList.get(i).get(grpname).size();j++) {
                    sublist.add(grpWithSubgrpsList.get(i).get(grpname).get(j).get("xaxis"));
                    System.out.println("new logic subgrp names" + grpWithSubgrpsList.get(i).get(grpname).get(j).get("xaxis"));

                }
                break;
            }
        }
        return sublist;


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void getResponseOfGraphDetailsRequest(ArrayList<HashMap<String, String>> localgraphTypes,
                                                 ArrayList<HashMap<String, String>> columnList, ArrayList<HashMap<String, String>> relativeList,
                                                 ArrayList<String> groupingList, ArrayList<ArrayList<HashMap<String, String>>> subgroupingList,
                                                 ArrayList<HashMap<String, String>> trendList, ArrayList<HashMap<String, String>> matrixList,
                                                 ArrayList<HashMap<String, Object>> filterList,
                                                 ArrayList<ArrayList<HashMap<String, String>>> filterData, ArrayList<HashMap<String,
            ArrayList<HashMap<String, String>>>> groupWithSubgrpList)

    {
        firsttime=true;
        this.graphTypes=localgraphTypes;
        this.columnList=columnList;
        this.relativeList=relativeList;
        this.groupingList=groupingList;
        this.subgroupingList=subgroupingList;
        this.trendingList=trendList;
        this.matrixList=matrixList;
        this.filterList=filterList;
        this.filterData=filterData;
        this.filterNamesList=filterNamesList;
        this.grpWithSubgrpsList=groupWithSubgrpList;
        if(Utils.checkifavailable(selectedGraphName))
        {
            actionBar.setTitle(selectedGraphName);
        }
        else
        {
            actionBar.setTitle(getResources().getString(R.string.app_name));
        }
        if(columnList!=null && columnList.size()>0)
        {
            ArrayList<String> colnamelist=new ArrayList<>();
            for(int i=0;i<columnList.size();i++)
            {
                colnamelist.add(i,columnList.get(i).get("id"));


            }
            GraphDetailsActivity.multipleMetricColumns = TextUtils.join(",", colnamelist);
            // GraphDetailsActivity.multipleTableColumns = TextUtils.join(",", colnamelist);

        }
        if(filterAndValuesList!=null && filterAndValuesList.size()>0)
        {
            resetFilter.setVisibility(View.VISIBLE);
        }
        else
        {
            resetFilter.setVisibility(View.GONE);
        }


        if(filterList!=null && filterList.size()>0)
            filterType=(String)filterList.get(0).get("filterType");
        if(filterData!=null && filterData.size()>0) {

            for (int j = 0; j < filterData.size(); j++) {
                for (int k = 0; k < filterData.get(j).size(); k++) {
                    if (filterData.get(j).get(k).get("id").equalsIgnoreCase("s")) {
                        filterData.get(j).get(k).put("state", "t");
                    } else {
                        filterData.get(j).get(k).put("state", "f");
                    }
                }
            }
        }
        if(filterList!=null && filterList.size()>0) {
            for (int i = 0; i < filterList.size(); i++) {
                String label = (String) filterList.get(i).get("label");
                if (label.equalsIgnoreCase("status")) {
                    successFlag = true;
                    GraphDetailsActivity.status = label;
                    //  GraphDetailsActivity.statusval = "s";
                    break;
                }

            }
        }






        if (graphTypes != null && graphTypes.size() > 0) {
            String graphtype = graphTypes.get(0).get("name");
            System.out.println("graph type " + graphtype);
            if(subgroupingList!=null && subgroupingList.size()>0) {
                GraphDetailsActivity.subgrp = subgroupingList.get(0).get(0).get("xaxis");
            }
            else
                GraphDetailsActivity.subgrp="";
            if(groupingList!=null && groupingList.size()>0) {
                GraphDetailsActivity.group = groupingList.get(0);
            }
            else
            {
                GraphDetailsActivity.group=null;
            }
            if(trendList!=null && trendList.size()>0) {
                for(int i=0;i<trendList.size();i++)
                {
                    String trend=trendList.get(i).get("name");
                    if(Utils.checkifavailable(trend))
                    {
                        if(trend.equalsIgnoreCase("hourly"))
                        {
                            if(!trendhourlyFlag) {
                                GraphDetailsActivity.trend = trend;
                                trendhourlyFlag = true;
                                break;
                            }
                        }
                    }
                }
                if(!trendhourlyFlag) {

                    GraphDetailsActivity.trend = trendList.get(0).get("name");
                }
            }
            else
            {
                GraphDetailsActivity.trend=null;
            }
            if(columnList!=null && columnList.size()>0)
                GraphDetailsActivity.column=columnList.get(0).get("id");
            else
                GraphDetailsActivity.column=null;
            if(graphTypes!=null && graphTypes.size()>0) {
                GraphDetailsActivity.ctype = graphTypes.get(0).get("name");
            }
            System.out.println("sub grp init is "+GraphDetailsActivity.subgrp);


            setItemsForSpinner();
            addSpinnersForMultipleFilters();

            //added by vikas for showing table data multiselect
            if(Utils.checkifavailable(ctype)) {
                if(ctype.equalsIgnoreCase("table")||
                        ctype.equalsIgnoreCase("tablewithbucket")||
                        ctype.equalsIgnoreCase("tablewithdynamicbucket"))
                {
                    if(this.columnList!=null )
                    {
                        System.out.println("state chngd from 1");
                        if(this.columnList.size()>0) {
                            List<String> result = Arrays.asList(this.columnList.get(0).get("id").split("\\s*,\\s*"));
                            this.columnList.clear();
                            for (int i = 0; i < result.size(); i++) {
                                HashMap<String, String> hp = new HashMap<>();
                                hp.put("id", result.get(i));
                                hp.put("name", result.get(i));
                                hp.put("columnType", "multiselect");
                                hp.put("state","t");
                                this.columnList.add(i, hp);
                            }
                        }


                    }
                }

            }
            i=0;
            j++;
            System.out.println("method called "+ j);
            sendGetGraphDataRequest();
            System.out.println("calling from "+0);
        }

    }







    public void sendResponseOfGetGraphDetails(ArrayList<ArrayList<HashMap<String, String>>> graphFinalList,
                                              ArrayList<HashMap<String, String>> graphList, ArrayList<HashMap<String, String>> repIdList,
                                              ArrayList<HashMap<String, String>> yTitleList, ArrayList<HashMap<String, String>> chartTitleList,
                                              ArrayList<HashMap<String, String>> xTitleList, ArrayList<HashMap<String, String>> xaxisTypeList,
                                              ArrayList<HashMap<String, String>> xaxisList, ArrayList<HashMap<String, String>> sourceList,
                                              ArrayList<HashMap<String, String>> columnWiseList,
                                              ArrayList<HashMap<String, String>> linkrepdetailsList, String button) {

        this.graphFinalList = graphFinalList;
        this.chartTitleList = chartTitleList;
        this.columnWiseList = columnWiseList;
        this.graphList = graphList;
        this.button=button;
        System.out.println("graph List is "+graphList);


        setValuesToSearchViewAdapter(graphFinalList);

        if (graphs_bottomsheetFragment != null && graphs_bottomsheetFragment.isVisible())
            graphs_bottomsheetFragment.dismiss();

        locallist.clear();
        if (columnList != null && columnList.size() > 0) {
            for (int a = 0; a < columnList.size(); a++) {

                HashMap<String, String> hp = new HashMap<>();
                hp.put("id", columnList.get(a).get("id"));
                // System.out.println("state graphlist size is " + graphList.size() + graphList);
                if (graphList != null && graphList.size() > 0) {

                    int index = Utils.getIndex(graphList, "name", columnList.get(a).get("id"));
                    if (index != -1) {
                        hp.put("state", "t");

                    } else
                        hp.put("state", "f");
                } else {
                    hp.put("state", "f");
                }
                System.out.println("state is " + hp.get("state"));


                locallist.add(hp);

                System.out.println("state local list is " + locallist);

            }
        }
        if (Utils.checkifavailable(group))
        {
            if(selectedVal!=null ) {
                selectedVal.clear();
            }
            //   MenuItem item = menu.findItem(R.id.Selectchart);
            if (GraphDetailsActivity.group.equalsIgnoreCase("Geography") &&
                    (GraphDetailsActivity.subgrp.equalsIgnoreCase("state"))) {
                // if(GraphDetailsActivity.subgrp.equalsIgnoreCase("state")) {
                //  item.setVisible(false);
                //this.invalidateOptionsMenu();
                selectChart.setVisibility(View.GONE);

                mapsFragment = new MapsFragment();
                loadFragment(mapsFragment, selectedVal);


                //  }

            } else {
                // item.setVisible(true);
                selectChart.setVisibility(View.VISIBLE);
               /* item.setVisible(false);
                this.invalidateOptionsMenu();*/
                showGraphicalData(selectedVal);


            }
        }
        else
        {
            if(selectedVal!=null ) {
                selectedVal.clear();
            }
            showGraphicalData(selectedVal);
        }



    }

    private void setValuesToSearchViewAdapter(ArrayList<ArrayList<HashMap<String, String>>> graphFinalList) {

        searchAdapter=new SearchAdapter(this,graphFinalList);
        searchRecycler.setAdapter(searchAdapter);
        searchAdapter.notifyDataSetChanged();
        // Utils.showToast(this,"setting adapter");


    }

    private void showGraphicalData(HashMap<String, String> selectedValue) {
        if (Utils.checkifavailable(ctype))
        {
            setVisibilityOfResetFilterButton();
            if (GraphDetailsActivity.ctype.equalsIgnoreCase("area")) {
                areaGraph = new AreaGraph();
                loadFragment(areaGraph, selectedValue);
                checkGroupingAndSetVisibilityOfSearchView(group);
                toggle_view_btn.setVisibility(View.VISIBLE);

            } else if (GraphDetailsActivity.ctype.equalsIgnoreCase("bar")) {

                barGraph = new BarGraph();
                loadFragment(barGraph, selectedValue);
                checkGroupingAndSetVisibilityOfSearchView(group);
                toggle_view_btn.setVisibility(View.VISIBLE);
            } else if (GraphDetailsActivity.ctype.equalsIgnoreCase("line")) {
                linegraph = new LineGraph();
                loadFragment(linegraph, selectedValue);
                checkGroupingAndSetVisibilityOfSearchView(group);
                toggle_view_btn.setVisibility(View.VISIBLE);
            } else if (GraphDetailsActivity.ctype.equalsIgnoreCase("tablewithpercent") ||
                    GraphDetailsActivity.ctype.equalsIgnoreCase("tablewithpie")) {
                if (GraphDetailsActivity.ctype.equalsIgnoreCase("tablewithpercent")) {
                    checkGroupingAndSetVisibilityOfSearchView(group);


                } else {
                    searchView.setVisibility(View.GONE);
                }
                tablefragment = new TableFragment();
                loadFragment(tablefragment, selectedValue);
                toggle_view_btn.setVisibility(View.GONE);


            } else if (GraphDetailsActivity.ctype.equalsIgnoreCase("pie")) {
                searchView.setVisibility(View.GONE);
                pieFragment = new PieChartFragment();
                //loadFragment(pieFragment, selectedValue);
                toggle_view_btn.setVisibility(View.GONE);
            } else if (GraphDetailsActivity.ctype.equalsIgnoreCase("donut")) {
                searchView.setVisibility(View.GONE);
                donutGraph = new DonutGraph();
                loadFragment(donutGraph, selectedValue);
                toggle_view_btn.setVisibility(View.GONE);

            } else if (GraphDetailsActivity.ctype.equalsIgnoreCase("gmap")) {
                if (!GraphDetailsActivity.group.equalsIgnoreCase("Temporal")) {
                    searchView.setVisibility(View.VISIBLE);
                }
                mapsFragment = new MapsFragment();
                loadFragment(mapsFragment, selectedVal);
                toggle_view_btn.setVisibility(View.GONE);

            } else if (GraphDetailsActivity.ctype.equalsIgnoreCase("metric")) {
                metricFragment = new MetricFragment();
                loadFragment(metricFragment, selectedVal);
                toggle_view_btn.setVisibility(View.GONE);
            } else if (GraphDetailsActivity.ctype.equalsIgnoreCase("table")) {
                tableNewfragment = new TableNewFragment();
                loadFragment(tableNewfragment, selectedVal);
                toggle_view_btn.setVisibility(View.GONE);
            }
            else if(GraphDetailsActivity.ctype.equalsIgnoreCase("Multi_Y_Axis_Bar"))
            {
                MultiYAxesGraph multiYAxesGraph=new MultiYAxesGraph();
                loadFragment(multiYAxesGraph,null);
                toggle_view_btn.setVisibility(View.VISIBLE);
            }
            else {
                Utils.showToast(this, "Unable to load the graph!!");
                toggle_view_btn.setVisibility(View.GONE);
            }

       /* else if(GraphDetailsActivity.ctype.equalsIgnoreCase("table"))
        {
            tablefragment=new TableFragment();
            loadFragment(tablefragment);
        }*/

           /* else if(GraphDetailsActivity.ctype.equalsIgnoreCase("tablewithpie"))
            {
               piegraph=new PiegraphWithTable();
                loadFragment(piegraph);
            }
            else if(GraphDetailsActivity.ctype.equalsIgnoreCase("tablewithpercent"))
            {
                tablefragment=new TableFragment();
                loadFragment(tablefragment);
            }
*/
        }
        else
        {
            Utils.showToast(this, "Unable to load the graph!!");
        }

    }

    private void checkGroupingAndSetVisibilityOfSearchView(String group) {

        if(Utils.checkifavailable(group)) {
            if (!GraphDetailsActivity.group.equalsIgnoreCase("Temporal")) {
                searchView.setVisibility(View.VISIBLE);
            }
            else
            {
                searchView.setVisibility(View.GONE);
            }
        }
        else
        {
            searchView.setVisibility(View.GONE);
        }
    }


    public void sendLatLongResponse(String json) {
        if(mapsFragment==null)
        {
            mapsFragment=new MapsFragment();

        }

//mapsFragment.LatLongJSONResponseFunction(json);

    }

    public void sendResponseOfGetGraphDetails(ArrayList<HashMap<String, String>> datalist, ArrayList<String> colNamesList) {
        this.tabledatalist=datalist;
        this.colNamesList=colNamesList;

        if(selectedVal!=null ) {
            selectedVal.clear();
        }
        System.out.println("multiple colnames "+colNamesList +"comma "+GraphDetailsActivity.multipleTableColumns);
        showGraphicalData(selectedVal);

    }

    public void sendGraphRequest(String graphId, String graphname) {

        filterval=null;
        System.out.println("response qfilter 5"+filterval);
        filter=null;
        this.selectedGraphName=graphname;
        graphs_bottomsheetFragment.dismiss();
        if (filterAndValuesList.size() > 0)
            filterAndValuesList.clear();
        sendGraphDetailsRequest(graphId);
    }

    public void sendResponseOfTableWithPercent(ArrayList<String> colNamesList, ArrayList<HashMap<String, String>> tableDataList, ArrayList<HashMap<String, String>> tablePercentDataList, String totalcount, ArrayList<ArrayList<HashMap<String, String>>> tableAllColumsDataList) {
        this.colNamesList=colNamesList;
        this.tabledatalist=tableDataList;
        this.tablePercentDataList=tablePercentDataList;
        this.totalCount=totalcount;
        this.tableAllColumnsDataList=tableAllColumsDataList;
        if(selectedVal!=null ) {
            selectedVal.clear();
        }
        showGraphicalData(selectedVal);
    }

    public void setIndexValues(String lowerIndex, String upperIndex, boolean tableFlag) {
        this.tableFlag=tableFlag;
        this.lowerIndex=lowerIndex;
        this.upperIndex=upperIndex;
        sendGetGraphDataRequest();
        System.out.println("calling from 9 "+"upper index is " +upperIndex +"table flag "+tableFlag);
        //     Utils.showToast(this,"index function called");
    }

    public void sendResponseOfTableWithPie(ArrayList<String> colNamesList, ArrayList<HashMap<String, String>> tableDataList, ArrayList<HashMap<String, String>> tablePercentDataList, String totalcount, ArrayList<HashMap<String, String>> pieDataNamesList, ArrayList<HashMap<String, String>> pieDataColumns) {
        this.colNamesList=colNamesList;
        this.tabledatalist=tableDataList;
        this.tablePercentDataList=tablePercentDataList;
        this.totalCount=totalcount;
        this.pieDataColumns=pieDataColumns;
        this.pieDataNamesList=pieDataNamesList;
        if(selectedVal!=null ) {
            selectedVal.clear();
        }

        showGraphicalData(this.selectedVal);
        setVisibilityOfResetFilterButton();

    }

    private void setVisibilityOfResetFilterButton() {

        if(filterAndValuesList!=null && filterAndValuesList.size()>0)
        {
            resetFilter.setVisibility(View.VISIBLE);
        }
        else
        {
            resetFilter.setVisibility(View.GONE);
        }
    }

    public void sendPieGraphResponse(ArrayList<HashMap<String, String>> pieDataColumns, ArrayList<HashMap<String, String>> pieDataNamesList) {
        this.pieDataColumns=pieDataColumns;
        this.pieDataNamesList=pieDataNamesList;
        if(selectedVal!=null ) {
            selectedVal.clear();
        }
        showGraphicalData(this.selectedVal);
    }

    @Override
    public Filter getFilter() {
        if(filterValuesFilter==null)
            filterValuesFilter = new FilterValuesFilter();
        return filterValuesFilter;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(Utils.checkifavailable(ctype)) {
            if (GraphDetailsActivity.ctype.equalsIgnoreCase("tablewithpercent")) {

            } else {

                if (newText.length() > 0) {
                    searchRecycler.setVisibility(View.VISIBLE);
                } else {
                    searchRecycler.setVisibility(View.GONE);
                }


                searchAdapter.getFilter().filter(newText);
            }
        }

        return false;
    }

    @Override
    public boolean onClose() {
        searchRecycler.setVisibility(View.GONE);
        return false;
    }

    public void sendSelectedOption(HashMap<String, String> hp) {
        this.selectedVal=hp;
        searchRecycler.setVisibility(View.GONE);
        showGraphicalData(this.selectedVal);

    }


    public void sendSelectedState(HashMap<String, String> selectedVal) {
        this.selectedVal=selectedVal;
        mapsFragment = new MapsFragment();
        loadFragment(mapsFragment, selectedVal);
        searchRecycler.setVisibility(View.GONE);


        System.out.println("selected val sending "+selectedVal);




    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Utils.showToast(GraphDetailsActivity.this,"touched!!!!");
        switch (v.getId())
        {

            case R.id.trending:

                trendSpinnerTouched=true;
                break;
            case R.id.grouping:
                groupingSpinnerTouched=true;
                break;
            case R.id.subgrouping:
                subGroupingTouched=true;

        }
        return true;
    }

    public void sendFilterParams(HashMap<String, String> params) {
        this.params=params;
        System.out.println("keyvalue set is "+params.values());
        sendGetGraphDataRequest();
    }

    public void updateData(ArrayList<HashMap<String, String>> newlyTablesList, String s, int i, HashMap<String, String> paramslist,
                           ArrayList<ArrayList<HashMap<String, String>>> tablesList) {
        /*clickedList=new ArrayList<>();
        clickedList.addAll(newlyTablesList);*/
        this.allTablesData=tablesList;

        setAdapterToList(newlyTablesList,s, i, paramslist);

       /* rvAdapter = new MultiFilterAdapter(GraphDetailsActivity.this,
                clickedList,filterNamesList,allTablesData,filterLayout);
        rvTest.setAdapter(rvAdapter);
*/
        //rvAdapter.notifyDataSetChanged();
        Utils.showToast(this,"notified!!");

    }


    public void sendResponseOfMetricGraph(ArrayList<HashMap<String, String>> datalist, ArrayList<String> colNamesList) {

        this.tabledatalist=datalist;
        this.colNamesList=colNamesList;
        if(selectedVal!=null ) {
            selectedVal.clear();
        }
        showGraphicalData(selectedVal);
    }

    public void sendColorsList(ArrayList<Integer> randomColorsList) {
        colorsList=randomColorsList;
    }



    public void sendResponseOfMultiYAxis(ArrayList<String> nameList, ArrayList<ArrayList<HashMap<String, String>>> mainDataList) {



        multiYNameList=nameList;
        mainDataMultiYList=mainDataList;
        showGraphicalData(selectedVal);


    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(compoundButton.getId()==R.id.view_toggle)
        {
            if(toggle_view_btn.isChecked())
                view_toggle_landscape.setChecked(true);
            else
                view_toggle_landscape.setChecked(false);
        }
        else if(compoundButton.getId()==R.id.view_toggle_landscape)
        {
            if(view_toggle_landscape.isChecked())
                toggle_view_btn.setChecked(true);
            else
                toggle_view_btn.setChecked(false);
        }
        showGraphicalData(selectedVal);


    }

    @Override
    public void onTaskCompleted() {

    }


    private class FilterValuesFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            {
                FilterResults results = new FilterResults();

                if (constraint == null || constraint.length() == 0) {
                    results.values = datalist;
                    results.count = datalist.size();
                } else {


                    filteredSearchList=new ArrayList<>();


                    for (int i = 0; i < datalist.size(); i++) {


                        if (datalist.get(i).get("name").toUpperCase().contains(constraint.toString().toUpperCase())) {
                            // if `contains` == true then add it
                            // to our filtered list
                            filteredSearchList.add(datalist.get(i));
                            System.out.println("filtered name is "+name);
                        }
                    }

                    // Finally set the filtered values and size/count
                    results.values = filteredSearchList;
                    results.count =filteredSearchList.size();
                }
                System.out.println("result count  " + results.count + "result values " + results.values);

                return results;
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            finalSearchList = (ArrayList<HashMap<String, String>>) results.values;
            //notifyDataSetChanged();

        }
    }

    private class SpinnerInteractionListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId())
            {

                case R.id.trending:

                    trendSpinnerTouched=true;
                    break;
                case R.id.grouping:
                    groupingSpinnerTouched=true;
                    break;
                case R.id.subgrouping:
                    subGroupingTouched=true;

            }


            return false;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {



            switch (parent.getId()) {
                case R.id.grouping:
                    String grpname = grouptv.getSelectedItem().toString();
                    GraphDetailsActivity.group = grpname;


                    //  Toast.makeText(this, "slecetd item si" +grpname, Toast.LENGTH_SHORT).show();
                    System.out.println("sub grouping " + subgroupingList + "grouping list " + groupingList);
                    subList = new ArrayList<>();
                    if (Utils.checkifavailable(grpname))
                    {
                        if (!grpname.equalsIgnoreCase("none")) {
                            if (checkGroupAvailablity(grpWithSubgrpsList, grpname) != null) {
                                subList = new ArrayList<>(checkGroupAvailablity(grpWithSubgrpsList, grpname));

                                System.out.println("new logic sublist size " + subList.size() + subList + "      " + checkGroupAvailablity(grpWithSubgrpsList, grpname));
                            }
                        } else {
                            noneSublist = new ArrayList<>();
                            try {
                                for (int i = 0; i < grpWithSubgrpsList.size(); i++) {

                                    System.out.println("none sub before" + grpWithSubgrpsList + "sizee " + grpWithSubgrpsList.size());
/*
[{none=[{xaxis=Category, option=button}]},
                        {none=[{xaxis=Application, option=button}]}, {none=[{xaxis=Service, option=button}]}]
*/
                                    if (grpWithSubgrpsList.get(i).containsKey("none")) {

                                        HashMap<String, String> xaxis = new HashMap<>();
                                        xaxis.put("xaxis", grpWithSubgrpsList.get(i).get(grpname).get(0).get("xaxis"));
                                        xaxis.put("option", grpWithSubgrpsList.get(i).get(grpname).get(1).get("option"));

                                        if (xaxis != null) {
                                            noneSublist.add(xaxis);
                                        }

                                    }
                                }
                            } catch (ArrayIndexOutOfBoundsException ae) {
                                ae.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            System.out.println("none sub list " + noneSublist);
                        }
                        System.out.println("subList is " + subList);

                        if (subList != null && subList.size() > 0) {

                            if (filterLayout != null && filterLayout.getChildCount() > 0) {
                                filterLayout.removeAllViews();
                            }
                            GraphDetailsActivity.subgrp = subList.get(0);
                            System.out.println("new logic setting adapter");
                            if (grpname.equalsIgnoreCase("Temporal")) {
                                searchView.setVisibility(View.GONE);
                                subgrouptv.setVisibility(View.INVISIBLE);
                                if (groupingSpinnerTouched) {
                                    sendGetGraphDataRequest();
                                    groupingSpinnerTouched = false;
                                }
                            } else if (grpname.equalsIgnoreCase("Demography") ||
                                    (grpname.equalsIgnoreCase("Geography"))) {
                                subgrouptv.setVisibility(View.VISIBLE);
                                searchView.setVisibility(View.VISIBLE);


                                ArrayAdapter<String> subgrpspinner = new ArrayAdapter<>(GraphDetailsActivity.this, R.layout.spinner_item, subList);
                                subgrpspinner.setDropDownViewResource(R.layout.dialog_singlechoice);
                                subgrouptv.setAdapter(subgrpspinner);
                                if (groupingSpinnerTouched) {
                                    sendGetGraphDataRequest();
                                    groupingSpinnerTouched = false;
                                }
                            }

                        } else if (noneSublist != null && noneSublist.size() > 0) {
                            searchView.setVisibility(View.GONE);
                            grouptv.setVisibility(View.GONE);
                            subgrouptv.setVisibility(View.GONE);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.rightMargin = 8;
                            linear = new LinearLayout(GraphDetailsActivity.this);
                            linear.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            linear.setOrientation(LinearLayout.HORIZONTAL);
                            linear.setId(View.generateViewId());
                            System.out.println("ids are" + linear.getId());
                            // topBarLl.addView(linear);

                            for (int i = 0; i < noneSublist.size(); i++) {
                                if (noneSublist.get(i).get("option").equalsIgnoreCase("button")) {


                                    final String subgrp = noneSublist.get(i).get("xaxis");
                                    Button myButton = new Button(GraphDetailsActivity.this);
                                    myButton.setText(subgrp);
                                    myButton.setBackgroundResource(R.drawable.btn_bg);
                                    myButton.setTextColor(getResources().getColor(R.color.white));
                                    myButton.setId(i);
                                    myButton.setLayoutParams(params);
                                    myButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            GraphDetailsActivity.subgrp = subgrp;
                                            sendGetGraphDataRequest();
                                        }
                                    });
                                    filterLayout.addView(myButton);
                                    // topBarLl.addView(myButton,params);
                                }


                                // }
                            }
                        }


            /*    if(grpname.equalsIgnoreCase("Temporal"))
                {

                   // GraphDetailsActivity.subgrp=subgroupingList.get(0).get(0).get("xaxis");
                    subgrouptv.setVisibility(View.INVISIBLE);
                    // if(!firsttime) {
                    sendGetGraphDataRequest();
                    firsttime=false;
                    //  }
                    System.out.println("calling from "+5);


                }

                else if (grpname.equalsIgnoreCase("Demography")) {


                    subgrouptv.setVisibility(View.VISIBLE);


                  //  GraphDetailsActivity.subgrp=subgroupingList.get(1).get(0).get("xaxis");




                }
                else if(grpname.equalsIgnoreCase("Geography"))
                {
                    subgrouptv.setVisibility(View.VISIBLE);


                }


*/

                    }

                    break;
                case R.id.trending:
                    String text = trendspinner.getSelectedItem().toString();

                    setTrending(text);
                    break;
                case R.id.subgrouping:

                    GraphDetailsActivity.subgrp =  subgrouptv.getSelectedItem().toString();
                    if(subGroupingTouched) {
                        sendGetGraphDataRequest();
                        subGroupingTouched=false;
                    }
                    System.out.println("calling from "+6);
                    break;


            }




        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    public void setPaginationValues(String start, String length, long initialIndex, String pageNum) {
        this.start=start;
        this.length=length;
        this.initialIndex=initialIndex;
        this.pageNum=pageNum;
        sendGetGraphDataRequest();

    }

    public void showAllTabularColumns(ArrayList<ArrayList<HashMap<String, String>>> mainList) {
        resetTableList();


       /* System.out.println("main list after calling "+mainList);
        tableAllColumnsDataList.addAll(mainList);
        System.out.println("tble list now "+mainList);*/
        showGraphicalData(selectedVal);

    }

    private void resetTableList() {
        System.out.println("state chngd from 2");
        if(tableAllColumnsDataList!=null)
        {
            if(tableAllColumnsDataList.size()>0)
            {
                for(int i=0;i<tableAllColumnsDataList.get(0).size();i++)
                {
                    tableAllColumnsDataList.get(0).get(i).put("state","t");
                }
            }
        }

    }
    public void sendSelectedParams(HashMap<String, String> hp) {
        this.trend=hp.get("trend");
        group=hp.get("group");
        subgrp=hp.get("sub");
        ctype=hp.get("ctype");

    }

    public void sendResponseOfTable(ArrayList<ArrayList<String>> totalTableNewList,
                                    long recordsFiltered, long recordsTotal) {
        this.totalTableNewList=totalTableNewList;
        tableAllColumnsDataList=new ArrayList<>();
        System.out.println("column list for table new "+columnList);
        for(int i=0;i<totalTableNewList.size();i++)
        {
            ArrayList<HashMap<String,String>> localList=new ArrayList<>();
            for(int j=0;j<totalTableNewList.get(i).size();j++)

            {
                if (j >= columnList.size()) {
                    System.err.println("Index out of bounds: columnList size is " + columnList.size() + " but tried to access index " + j);
                    continue; // Skip this iteration if the index is out of bounds
                }

                String value = totalTableNewList.get(i).get(j);
                HashMap<String, String> datamap = new HashMap<>();

                String columnName = columnList.get(j).get("name");
                if (columnName == null) {
                    System.err.println("Column name at index " + j + " is null");
                    continue; // Skip this iteration if the column name is null
                }
                datamap.put("name", columnList.get(j).get("name"));
                datamap.put("value",value );
                datamap.put("state", "t");
                System.out.println("key and value " + ": " + columnList.get(j).get("name") + " : " + value);
                localList.add(datamap);
            }
            tableAllColumnsDataList.add(localList);
        }
        System.out.println("drillx axis list "+drillXAxisList);
        if(drillXAxisList!=null)
        {
            if(this.tableAllColumnsDataList!=null)
            {
                if(drillXAxisList.size()>0 && this.tableAllColumnsDataList.size()>0)
                {
                    for(int i=0;i<tableAllColumnsDataList.size();i++)
                    {
                        for(int j=0;j<tableAllColumnsDataList.get(i).size();j++)
                        {
                            System.out.println("name for drill check "+tableAllColumnsDataList.get(i).get(j).get("name"));
                            if(drillXAxisList.contains(tableAllColumnsDataList.get(i).get(j).get("name")))
                            {
                                tableAllColumnsDataList.get(i).get(j).put("drill","y");
                            }
                            else
                            {
                                tableAllColumnsDataList.get(i).get(j).put("drill","n");
                            }
                        }


                    }
                }
            }
        }

        this.recordsFiltered=recordsFiltered;
        this.recordsTotal=recordsTotal;
        showGraphicalData(selectedVal);
    }
}

