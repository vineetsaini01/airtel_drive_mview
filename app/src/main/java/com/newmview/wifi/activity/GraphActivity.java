package com.newmview.wifi.activity;

import android.app.Dialog;

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.RadioGroup;
import com.mview.airtel.R;
import com.newmview.wifi.fragment.AreaChartDashboard;
import com.newmview.wifi.fragment.BarChartNew;
import com.newmview.wifi.fragment.LineChartDashboard;
import com.newmview.wifi.fragment.ScatterChartDashboard;
import com.newmview.wifi.fragment.BarChartDashboard;
import com.newmview.wifi.helper.AsyncTasks_APIgetData;
import com.newmview.wifi.helper.CommonUtil;
import com.newmview.wifi.helper.ReportIds;
import com.newmview.wifi.interfaces.AsynctaskListener;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;

import java.util.ArrayList;
import java.util.HashMap;


public class GraphActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, AsynctaskListener {


    private Dialog dialog;
    private Context context;
    private RadioGroup trendradiogroup;

    private FragmentManager fragmentManager;
    private String chartName = "";
    private Bundle args;
    private ActionBar actionBar;
    private BarChartDashboard fragment;
    private ArrayList<String> repIdList;
    private String lat="";
    private String lon="";

    private String reqtype;
    private String RepId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        init();
        checkDataConnectivityAndSendRequest();
    }


    private void init() {
        if (getSupportActionBar() != null) {
            actionBar = getSupportActionBar();
        }
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);


        args = getIntent().getExtras();
        if (args != null) {
            lat = args.getString("lat");
            lon = args.getString("lon");
            reqtype=args.getString("reqtype");
            System.out.println("lat and long "+lat +" "+lon);
        }

        fragmentManager = getSupportFragmentManager();

    }


    private void showDialogBox(int res) {

        dialog = new Dialog(this, R.style.AlertDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(res);
        dialog.setCanceledOnTouchOutside(true);
        trendradiogroup = (RadioGroup) dialog.findViewById(R.id.radioBtn);

        if (trendradiogroup != null)
            trendradiogroup.setOnCheckedChangeListener(this);
        dialog.show();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.graph_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.trending:
                showDialogBox(R.layout.trendlayout);
                break;
            case R.id.Selectchart:
                showDialogBox(R.layout.charttypes);
                break;
            case android.R.id.home:
                onBackPressed();
                break;


        }
        return true;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {/* -------------------------- trending------------------------------------*/

          /*  case R.id.FiveminuteBtn:
                break;
            case R.id.hourlyBtn:

                break;
            case R.id.dailyBtn:
                //getDataPeriodically("daily");
                break;
            case R.id.weeklyBtn:
                break;
            case R.id.monthlyBtn:
                break;*/




            /* -------------------------- charts------------------------------------*/
            case R.id.barchart:

                BarChartNew fragment = new BarChartNew();
                loadFragment(fragment);

                break;

            case R.id.linechart:
                LineChartDashboard lineFragment = new LineChartDashboard();
                loadFragment(lineFragment);
                break;

            case R.id.scatterchart:
                ScatterChartDashboard scatterFragment = new ScatterChartDashboard();
                loadFragment(scatterFragment);
                break;

            case R.id.areachart:
                AreaChartDashboard areaFragment = new AreaChartDashboard();
                loadFragment(areaFragment);
                break;
        }
    }

    private void getDataPeriodically(String srt) {
        Intent intent = new Intent(GraphActivity.this, GraphActivity.class);
        startActivity(intent);
        intent.putExtra("reqtype","trend");
        intent.putExtra("srt", srt);


    }

    private void checkDataConnectivityAndSendRequest() {

        if (Utils.isNetworkAvailable(GraphActivity.this)) {
            if(reqtype!=null && reqtype.equalsIgnoreCase("drill")) {
                if (CommonUtil.repList.size() > 0) {
                    addData();
                }
                else
                {
                    sendInitRequest();
                }
            }
            else if(reqtype!=null && reqtype.equalsIgnoreCase("trend"))
            {

            }


        } else {
            Utils.showToast(GraphActivity.this, Constants.INTERNET_PROBLEM);
        }
    }

    private void addData() {

        ArrayList<HashMap<String, String>> localDashboardList = new ArrayList<>(CommonUtil.repList);
        for (int i = 0; i < localDashboardList.size(); i++) {
            String repId = localDashboardList.get(i).get("repId");
            if (repId != null) {
                if (repId.equals(ReportIds.signalStrengthReportId)) {
                    String repName = localDashboardList.get(i).get("repName");
                    String srt = localDashboardList.get(i).get("repType");
                    String repType = localDashboardList.get(i).get("repType");
                    sendDrillRequest(repId, repName, repType, srt);
                    break;
                }

            }
        }
    }

    private void sendInitRequest() {
            HashMap<String,String> obj=new HashMap<>();
            obj.put(CommonUtil.REQUEST_KEY,CommonUtil.INIT_REQUEST);
            obj.put(CommonUtil.USER_ID_KEY,CommonUtil.USER_ID);
            obj.put(CommonUtil.PASSWORD_KEY,CommonUtil.PASSWORD);
            CommonUtil.request=1;

        Log.d("TAG", "sendInitRequest: "+obj);

            AsyncTasks_APIgetData asyncTasks_apIgetData=new AsyncTasks_APIgetData(this,this);
            asyncTasks_apIgetData.execute(obj);


    }

    private void sendDrillRequest(String repId, String repName, String repType, String srt) {
        HashMap<String, String> obj = new HashMap<>();
        obj.put(CommonUtil.USER_ID_KEY, CommonUtil.USER_ID);
        obj.put(CommonUtil.REQUEST_KEY, CommonUtil.DRILL_REQUEST);
        obj.put("repId", repId);
        obj.put("drillType", repType);
        obj.put("drillFields", "Latitude@Longitude");
        obj.put("drillData", lat + "@" + lon);
        obj.put("srt", srt);
        obj.put("cli", "0");
        Log.d("TAG", "sendDrillRequest: "+obj);
        RepId=repId;
        AsyncTasks_APIgetData apigetData = new AsyncTasks_APIgetData(GraphActivity.this, this);
        CommonUtil.request = 2;
        apigetData.execute(obj);


    }

    private void loadFragment(Fragment fragment) {
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        String name=getLegendName(RepId);
        args.putString("chartName", name);
        actionBar.setTitle(name);
        fragment.setArguments(args);
        if (dialog != null)
            dialog.dismiss();
    }

    private String getLegendName(String repId) {
        if(repId.equals(ReportIds.signalStrengthReportId))
        {
            return "Signal Strength";
        }
        return "";

    }


    @Override
    public void onTaskCompleted() {

        if (CommonUtil.request == 1) {
            addData();
        } else if (CommonUtil.request == 2) {
            BarChartNew fragment = new BarChartNew();
            loadFragment(fragment);

        }
    }

}

