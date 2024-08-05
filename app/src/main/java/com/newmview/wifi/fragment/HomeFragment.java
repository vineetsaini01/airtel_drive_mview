
package com.newmview.wifi.fragment;
import android.app.AlertDialog;
import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import android.telephony.CarrierConfigManager;
import android.telephony.ServiceState;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
//import android.widget.toast;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.newmview.wifi.CapturedPhoneState;
import com.newmview.wifi.MyPhoneStateListener;
import com.newmview.wifi.MySpeedTest;
import com.mview.airtel.R;
import com.newmview.wifi.SlidingTab.MyTabControl;
import com.newmview.wifi.Telephony_params;
import com.newmview.wifi.TinyDB;
//import com.mcpsinc.mview.activity.AboutUsActivity;
//import com.mcpsinc.mview.activity.Activity2;
import com.newmview.wifi.activity.Background_service;
import com.newmview.wifi.activity.GraphActivity;
import com.newmview.wifi.activity.MainActivity;
import com.newmview.wifi.activity.call_sms;
import com.newmview.wifi.activity.mView_UploadDownloadTest;
import com.newmview.wifi.activity.mView_VideoTest;
import com.newmview.wifi.activity.mView_WebTest;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.customview.HalfGauge;
import com.newmview.wifi.database.DB_handler;
import com.newmview.wifi.helper.AllInOneAsyncTaskForNetwork;
import com.newmview.wifi.helper.AsyncTasks_APIgetData;
import com.newmview.wifi.helper.CommonUtil;
import com.newmview.wifi.helper.RequestResponse;
import com.newmview.wifi.helper.SharedPreferencesHelper;
import com.newmview.wifi.interfaces.AsynctaskListener;
import com.newmview.wifi.listenService;
import com.newmview.wifi.mView_HealthStatus;
import com.newmview.wifi.other.Config;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.SimInfoUtility;
import com.newmview.wifi.other.Utils;
import com.newmview.wifi.other.WifiConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import static com.newmview.wifi.activity.MainActivity.context;
import static com.newmview.wifi.activity.MainActivity.issuesList;
import static com.newmview.wifi.network.NetworkUtil.getSecondSimSignalStrength;
import static com.newmview.wifi.other.Constants.mview;
import static com.newmview.wifi.other.Utils.checkForGps;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements View.OnClickListener, AsynctaskListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG ="HomeFragment" ;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    MapView mMapView;
    private String statusInit;
    public static GoogleMap googleMap;
// private boolean opened=true;
    private OnFragmentInteractionListener mListener;
    String lat = "";
    String lon = "";
    TextView info1, info2, info3, info4,btryTv,ssidNameVal,wifiSSVal,linkSpeedVal,bssidVal,manufacturerVal,frequencyVal,channelVal,wifiStandardVal;
    TextView modelNameTv,sdkVersionTv,androidVersionTv,batteryTv;
    TextView Total_Calls, Call_Success, Call_Failed,Call_Missed;
    TextView mobile, wifi, roaming;
    Button btn_upload, btn_web, btn_video;
    boolean dontRefreshScreen;
    PieChart pieChart, pieChart_nc;
    PieDataSet dataset, dataset_nc;
    TextView[] valueTxt_MyCalls;
    TextView[] valueTxt_MyCoverage;
    ArrayList<Entry> entries;
    ArrayList<Entry> entries_nc;
    TableLayout childlayout, childlayout_nc;
    HorizontalBarChart horizontalBarChart,horizontalBarChartMB;
    LineChart batteryLinechart;
    ImageView my_phone_detail, map_detail, call_img;
    private String restoredText;
    private Button button;
    private SharedPreferences.Editor editor;
    private  String missedcalls;
    private TextView info2part2;
    private View v;
    private FragmentManager fragmentManager;
    private ArrayList<String> repIdList;
    private ArrayList<HashMap<String,String>> localDashboardList;
    private String srt;
    private String latitude="";
    private String longitude="";
    private Button open;
    private LayoutInflater inflater;
    private PieChart outgoingPiechart;
    private TableLayout childLayout1;
    private TextView[] valueTxt_OutgoingCalls;
    private TextView imsi_tv,imsi1_tv;
    private LinearLayout secondary_sim_ll,nofirstSimLL,primInfoll,primsecondsimll;
    private ArrayList<String> imsi2Info;
    private TextView no_secondary_sim;
    private TableLayout tablelayout;
    private TextView prim_simOpVal,prim_simroaming, prim_simss,prim_simimsi;
    private TextView sec_simOpVal,sec_simroaming, sec_simss,sec_simimsi;
    private TextView prim_carrierVal,sec_carrierVal;
    private TextView sec_head;
    private TextView prim_head;
    private LinearLayout no_sec_simLL;
    private TableLayout sec_table;
    private LinearLayout mainSecLL;
    private TextView  prim_network,sec_network;
    private float wifiDataInMB;
    private float total;
    private float mobileDataInMB;
    String status1 = null;
    private SharedPreferences.Editor editor1;
    private SharedPreferences preferences;
    private HalfGauge linkSpeedGauge,signalStrengthGauge;
    private TableLayout wifiDetailsTL;

    public HomeFragment() {
        // Required empty public constructor
    }

  //  public void buttonClick(View view){
      //  Toast.makeText(MainActivity.context,"on stop of activity",Toast.LENGTH_SHORT).show();
    // }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        System.out.println("on activity created called");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            CarrierConfigManager carrierConfigManager= (CarrierConfigManager) context.getSystemService(Context.CARRIER_CONFIG_SERVICE);
            //System.out.println("carrier is "+carrierConfigManager.getCo);
        }
         // Toast.makeText(getActivity(), "onActivity of home called", Toast.LENGTH_SHORT).show();
        initValues();
        MySpeedTest.urlArray = new ArrayList<String>();
       MySpeedTest.urlArray.add("http://www.google.com");
       MySpeedTest.urlArray.add("http://www.facebook.com");
        MySpeedTest.urlArray.add("http://www.gmail.com");
        MySpeedTest.urlArray.add("http://www.wikipedia.org");
        MySpeedTest.urlArray.add("http://www.yahoo.com");
        mView_HealthStatus.mySpeedTest = new MySpeedTest();
        useHandler();
        //check if user is registered
        SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.MY_PREFS_NAME, context.MODE_PRIVATE);
        restoredText = prefs.getString("userid", null);
        String opened_val=prefs.getString("opened",null);
        System.out.println("registeration restored text is"+restoredText+" and opened val"+opened_val);
        editor = context.getSharedPreferences(MainActivity.MY_PREFS_NAME, context.MODE_PRIVATE).edit();
        System.out.println("user is is "+restoredText);
        //  Toast.makeText(getActivity(), "restored text "+restoredText  +"opened val "+opened_val, Toast.LENGTH_SHORT).show();
       if (restoredText == null) {
           Log.d(TAG, "onActivityCreated: appopened");
                if(editor!=null) {
                    editor.putString("opened", "yes");
                    editor.apply();
                }
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {


                                JSONObject detailsObj = new JSONObject();

                                detailsObj.put("total_outgoing_calls", mView_HealthStatus.iTotal_OutgoingCalls);
                                detailsObj.put("outgoing_success_calls", mView_HealthStatus.iOutgoing_Success);
                                detailsObj.put("outgoing_not_answered_calls", mView_HealthStatus.iNoAnswered_Calls);
                                detailsObj.put("total_incoming_calls", mView_HealthStatus.iTotal_Calls);
                                detailsObj.put("incoming_answered_calls", mView_HealthStatus.iCall_Answered);
                                detailsObj.put("missed_calls", mView_HealthStatus.iCall_Missed);
                                detailsObj.put("4G_coverage", mView_HealthStatus.per4g);
                                detailsObj.put("3G_coverage", mView_HealthStatus.per3g);
                                detailsObj.put("2G_coverage", mView_HealthStatus.per2g);
                                detailsObj.put("wifiData", wifiDataInMB);
                                detailsObj.put("mobileData", mobileDataInMB);
                                detailsObj.put("totalData", total);



                                String ss = "0";
                                if (MyPhoneStateListener.getNetworkType() == 4) {
                                    if (mView_HealthStatus.lteRSRP != null)
                                        ss = mView_HealthStatus.lteRSRP + "dbm";

                                } else if (MyPhoneStateListener.getNetworkType() == 3) {
                                    if (mView_HealthStatus.rscp != null)
                                        ss = mView_HealthStatus.rscp + "dbm";


                                } else if (MyPhoneStateListener.getNetworkType() == 2) {
                                    ss = MyPhoneStateListener.getRxLev() + "dbm";
                                }
                                if( mView_HealthStatus.prim_imsi==null)
                                    mView_HealthStatus.prim_imsi="NA";
                                detailsObj.put("firstSimImsi", mView_HealthStatus.prim_imsi);
                                detailsObj.put("firstSimSignalStrength", ss);
                                detailsObj.put("firstSimOperatorName",mView_HealthStatus.prim_carrierName);
                                detailsObj.put("firstSimCarrierMode",mView_HealthStatus.prim_carrierMode);
                                //new for another sim
                                String sec_ss = getSecondSimSignalStrength();

                                if (Utils.getSecondImsi(MainActivity.context) != null) {
                                    System.out.println("second sim details not null");
                                    detailsObj.put("secondSimImsi", mView_HealthStatus.sec_imsi);
                                    detailsObj.put("secondSimOperatorName", mView_HealthStatus.sec_carrierName);
                                    if (sec_ss != null) {
                                        detailsObj.put("secondSimSignalStrength", sec_ss);
                                    } else {
                                        detailsObj.put("secondSimSignalStrength", 0 + "dbm");
                                    }
                                    detailsObj.put("secondSimCarrierMode",mView_HealthStatus.sec_carrierMode);

                                } else {
                                    System.out.println("second sim details  null");
                                    detailsObj.put("secondSimImsi", "NA");
                                    detailsObj.put("secondSimOperatorName", "NA");
                                    detailsObj.put("secondSimSignalStrength", "NA");
                                    detailsObj.put("secondSimCarrierMode","NA");

                                }


                                detailsObj.put("gcm_id", Utils.getGcm_Id());
                                JSONArray detailsArray = new JSONArray();
                                detailsArray.put(detailsObj);
                                RequestResponse.sendEvent(detailsArray, AllInOneAsyncTaskForNetwork.AsyncTaskPurpose.APP_OPEN,"app_opened");
                              //  RequestResponse.sendRequestOnOpeningTheApp(batlevl, total, wifiDataInMB, mobileDataInMB);
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            //    WebService.API_sendRegister(lat, lon);//api type 0 request method called
                        //    new WebService.Async_SendNeighboringCellsInfo().execute();
                            //  opened=false;

                        }
                    }, 7000);



                if(MainActivity.SEND_API_REQUEST)
                {
                    Bundle bundle=getArguments();
                    String type=null;
                    if(bundle!=null) {
                        type = bundle.getString("type");
                    }
                    else
                    {
                        type="";
                    }
                    //  WebService.API_sendRegister(lat,lon);

                    if(MainActivity.REPORT_FLAG)
                    {
                        //   Utils.showToast(getActivity(),"sending rep request!!!! ");
                       // Utils.showReportIssueAlert(type,context,lat,lon,Constants.NETWORK_COVERAGE_ISSUE);

                    }
                }
                else {
//                    sendGetDashboardDataRequest();
                    // sendInitRequest();
                }


        }
        else {

            mView_HealthStatus.userid = restoredText;
            mView_HealthStatus.installedSince = prefs.getString("installedSince", ""+Utils.getDateTime());
            long yourmilliseconds = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            Date resultdate = new Date(yourmilliseconds);
            String displaydate = sdf.format(resultdate);
            TinyDB tinydb = new TinyDB(context);
            mView_HealthStatus.timeSeriesCapturedData = new ArrayList<CapturedPhoneState.BasicPhoneState>();
            ArrayList<Object> listObject = tinydb.getListObject("periodicData", CapturedPhoneState.BasicPhoneState.class);
            for (int i = 0; i < listObject.size(); i++) {
                Object object = listObject.get(i);
                mView_HealthStatus.timeSeriesCapturedData.add((CapturedPhoneState.BasicPhoneState) object); }
            //new WebService.Async_SendPendingCallRecordsToServer().execute();
        }
        //toast.makeText(getActivity(), "missed calls in onactivitycreated "+mView_HealthStatus.iCall_Missed, //toast.LENGTH_SHORT).show();
        mView_HealthStatus.iTotal_Calls = prefs.getInt("totalcalls", 0);
        mView_HealthStatus.iCall_Missed = prefs.getInt("missedcalls", 0);
        mView_HealthStatus.iCall_Success = prefs.getInt("successcalls", 0);
        mView_HealthStatus.iCall_Failed = prefs.getInt("failedcalls", 0);
        mView_HealthStatus.iCall_Answered=prefs.getInt("answeredcalls",0);
        mView_HealthStatus.iNoAnswered_Calls=prefs.getInt("noansweredcalls",0);
        mView_HealthStatus.iOutgoing_Success=prefs.getInt("outgoingsuccess",0);
        mView_HealthStatus.iTotal_OutgoingCalls=prefs.getInt("totaloutgoing",0);
        mView_HealthStatus.iTotal_Calls=mView_HealthStatus.iCall_Missed+mView_HealthStatus.iCall_Answered;
        mView_HealthStatus.iTotal_OutgoingCalls=mView_HealthStatus.iNoAnswered_Calls+mView_HealthStatus.iOutgoing_Success;
        // mView_HealthStatus.iTotal_Calls = mView_HealthStatus.iCall_Missed + mView_HealthStatus.iCall_Success + mView_HealthStatus.iCall_Failed;
        // setupAlarm();
    }
    private void parseInitData(String result) {
        try {
            JSONObject object = new JSONObject(result);

            /************ Nework, Project and Report Array **************/
            //  JSONArray networkArray = object.getJSONArray("Network");
            //JSONArray projectArray = object.getJSONArray("Project");
            JSONArray reportArray = object.getJSONArray("Report");

            /************ Nework, Project and Report Array **************/
            //  JSONArray netProjArray = object.getJSONArray("Network_Project_Map");
            //JSONArray projRepArray = object.getJSONArray("Project_Report_Map");
            JSONArray dashboardArray = object.getJSONArray("Dashboard");
            JSONArray dbChartArray = object.getJSONArray("DbChart");
            //     JSONArray graphdataArray = object.getJSONArray("GraphData");


            if(CommonUtil.repList!=null)
            {
                CommonUtil.repList.clear();
            }
            for(int i = 0; i < reportArray.length(); i++)
            {
                JSONObject obj = reportArray.getJSONObject(i);
                HashMap<String,String> hashMap=new HashMap<>();

                String repId=  obj.optString("repId");
                String repType=  obj.optString("repType");
                String repName =obj.optString("repName");
                String minAcnt= String.valueOf(obj.optInt("min_acnt"));
                String max_acnt= String.valueOf(obj.optInt("max_acnt"));
                String trendUpdateStatus= obj.getString("trendUpdateStatus");



                hashMap.put("repId",repId);
                hashMap.put("repType",repType);
                hashMap.put("repName",repName);
                hashMap.put("min_acnt",minAcnt);
                hashMap.put("max_acnt",max_acnt);
                hashMap.put("trendUpdateStatus",trendUpdateStatus);
                CommonUtil.repList.add(hashMap);

            }




            ///for dashboards
            if(CommonUtil.dashboardList!=null)
            {
                CommonUtil.dashboardList.clear();
            }

            for(int i=0;i<dashboardArray.length();i++)
            {
                JSONObject obj=dashboardArray.getJSONObject(i);
                HashMap<String,String> hashMap=new HashMap<>();
                hashMap.put("dbId",obj.optString("dbId"));
                hashMap.put("dbName",obj.optString("dbName"));
                CommonUtil.dashboardList.add(hashMap);


            }
            System.out.println("response of dashboards" + CommonUtil.dashboardList);




            /////
            for (int iDbChart = 0; iDbChart < dbChartArray.length(); iDbChart++) {
                HashMap<String,String> nameHashMap=new HashMap<>();

                JSONObject dbChartObj = dbChartArray.getJSONObject(0);
                nameHashMap.put("name",dbChartObj.optString("name"));
                CommonUtil.listDbChart.add(nameHashMap);

                JSONArray nameArray=dbChartObj.getJSONArray(CommonUtil.listDbChart.get(iDbChart).get("name"));
                for (int iName = 0; iName < nameArray.length(); iName++) {
                    HashMap<String,String> hash=new HashMap<>();
                    JSONObject dashboardNameObj = nameArray.getJSONObject(iName);
                    hash.put("userDbId", String.valueOf(dashboardNameObj.optInt("userDbId")));
                    hash.put("repId",String.valueOf(dashboardNameObj.optInt("repId")));
                    hash.put("dbId",String.valueOf(dashboardNameObj.optInt("dbId")));
                    hash.put("srt",dashboardNameObj.optString("srt"));
                    hash.put("chartName",dashboardNameObj.optString("chartName"));
                    hash.put("dbType",dashboardNameObj.optString("dbType"));
                    CommonUtil.list_dashboardName.add(hash);
                    System.out.println("response hash "+CommonUtil.list_dashboardName.get(iName));

                }


            }

        }
        catch (JSONException e)
        {
            Constants.ERROR=3;
            System.out.println("response json exception in parse init "+e.toString());
        }
        catch (Exception e)
        {
            System.out.println("response exception in parse init "+e.toString());
        }
    }


    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        //////toast.makeText(getActivity(), "on detach called,", ////toast.LENGTH_SHORT).show();
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.info2part2:
                //PviewWebService.API_init();
                // WebService.API_getData();
                System.out.println("lat and long service " + listenService.gps + "  " + listenService.gps.canGetLocation());
                if (listenService.gps != null && listenService.gps.canGetLocation()) {
                    System.out.println("lat and long service " + "  " + listenService.gps.getLatitude());
                    lat = listenService.gps.getLatitude() + "";
                    latitude = String.valueOf((double) Math.round((Double.parseDouble(lat)) * 1000) / 1000);
                    System.out.println("latitude " + latitude + "only internal double" + (Double.parseDouble(lat) + "  " + Math.round((Double.parseDouble(lat) * 1000d) / 1000d)));
                    lon = listenService.gps.getLongitude() + "";
                    longitude = String.valueOf((double) Math.round((Double.parseDouble(lon)) * 1000) / 1000);
                }
                Intent intent = new Intent(getActivity(), GraphActivity.class);
                intent.putExtra("lat", latitude);
                intent.putExtra("lon", longitude);
                intent.putExtra("reqtype", "drill");
                System.out.println("lat and long " + latitude + " " + longitude + "in home");
                /* intent.putExtra("chartName",chartName);*/
                startActivity(intent);
                break;
        }
    }

    /** Open another app.
     * @param context current Context, like Activity, App, or Service
     * @param packageName the full package name of the app to open
     * @return true if likely successful, false if unsuccessful
     */
    public  boolean openApp(Context context, String packageName) {
        //     Toast.makeText(context, "open the app ", Toast.LENGTH_SHORT).show();
        PackageManager manager = context.getPackageManager();
        try {
            Intent i = manager.getLaunchIntentForPackage(packageName);

            if (i == null) {
                return false;
                //throw new ActivityNotFoundException();
            }
            i.putExtra("text","send this");
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(i);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    private void openGraphActivity(String dbName,String chartName) {
        System.out.println("response dbname "+dbName);
        switch (dbName)
        {
            case "Bar":
               /* Bundle bundle=new Bundle();
                bundle.putString("chartName",chartName);
                BarChartDashboard fragment=new BarChartDashboard();
                fragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.frame,fragment).commit();*/
                Intent intent=new Intent(getActivity(), GraphActivity.class);
                intent.putExtra("chartName",chartName);
                startActivity(intent);

                Config.onbackpress="home";
                break;

        }


    }

    @Override
    public void onTaskCompleted() {
        //  useHandler();
        if(CommonUtil.request==1) {
            ArrayList<HashMap<String, String>> localRepList = new ArrayList<>();

            repIdList = new ArrayList<>();
            localRepList.addAll(CommonUtil.repList);

            System.out.println("response list is " + localRepList);


            for (int i = 0; i < localRepList.size(); i++) {
                String repId = localRepList.get(i).get("repId");
                String repType = localRepList.get(i).get("repType");
                String repName = localRepList.get(i).get("repName");
                String minaccnt = localRepList.get(i).get("min_acnt");
                String maxacnt = localRepList.get(i).get("max_acnt");
                String trendUpdateStatus = localRepList.get(i).get("trendUpdateStatus");
                repIdList.add(repId);

            }
            System.out.println("response of repids " + repIdList);
        }
        else if(CommonUtil.request==2)
        {
            String dbId="";
            String dbType="";
            String chartName="";
            String srt="";
            ArrayList<HashMap<String,String>> dbTypeList=new ArrayList<>();
            dbTypeList.addAll(CommonUtil.list_dashboardName);

           /* for(int i=0;i<dbTypeList.size();i++)
            {*/
            //dbId=localDashboardList.get(i).get("dbId");
            if(dbTypeList!=null) {
                if(dbTypeList.size()>0) {
                    dbType = dbTypeList.get(0).get("dbType");
                    chartName = dbTypeList.get(0).get("chartName");
                    srt=dbTypeList.get(0).get("srt");
                }
            }

            // }
            if(dbType!=null) {
                openGraphActivity(dbType,chartName);
            }
        }

        /*else if(CommonUtil.request==3)
        {
            ((MainActivity)context).getDashboards();
        }
*/




    }

    private void getDashboardData(String dbId, String dbName) {


        HashMap<String,String> obj=new HashMap<>();

        obj.put(CommonUtil.USER_ID_KEY,CommonUtil.USER_ID);
        obj.put(CommonUtil.REQUEST_KEY,CommonUtil.DASHBOARD_REQ);
        obj.put(CommonUtil.DBID_KEY,dbId);
        obj.put(CommonUtil.DBNAME_KEY,dbName);


        AsyncTasks_APIgetData apigetData = new AsyncTasks_APIgetData(context,this);
        CommonUtil.request=2;
        apigetData.execute(obj);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicatedz
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //  Toast.makeText(getActivity(), "on create callled", Toast.LENGTH_SHORT).show();
        //  System.out.println("imsi for second num is "+Utils.getSim2IMSI(context));

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        ////toast.makeText(getActivity(), "o create of frgmnet called", ////toast.LENGTH_SHORT).show();
       /* editor = MainActivity.context.getSharedPreferences(MainActivity.MY_PREFS_NAME, MainActivity.context.MODE_PRIVATE).edit();
       editor.remove("opened");
        editor.apply();*/
        ///   opened=true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Toast.makeText(getActivity(), "on create view callled", Toast.LENGTH_SHORT).show();
        //  System.out.println("oncreateview called"));
        v = inflater.inflate(R.layout.home_newfragment, container, false);
        init();
        //sendGetDashboardDataRequest();



        createViewForIncomingcCalls();
        createViewForOutgoingCalls();
        initializeNtwrkCoverageChart();////for network coverage



        //////////////////////////////////////////////////
        ///////////////My Data Horizontal Bar Chart///////////////////
        //refreshDataUsageChart();

        ////////////////

        batteryLinechart = (LineChart) v.findViewById(R.id.batteryLinechart);

        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                updateGoogleMap();

                // For dropping a marker at a point on the Map

            }
        });
        //createbatteryChart();
        setClickListeners();

        DB_handler dbhandler= null;
        try {
            dbhandler = new DB_handler(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbhandler.open();
        Cursor c = dbhandler.selectInitStatusData();
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                status1 = c.getString(c.getColumnIndex("status"));
                System.out.println(" status one is "+status1);
            } }
      //  appopenfirstime();
       // updateApp();
       if (appopenfirstime()) {
           updateApp();
        }


        return v;
    }

    private void createViewForOutgoingCalls() {
//onCallStateChanged(MyPhoneStateListener)
        {

            entries = new ArrayList<>();
            entries.add(new Entry(0, 0));
            entries.add(new Entry(0, 1));
            entries.add(new Entry(0, 2));

            dataset = new PieDataSet(entries, "# of Calls");

            ArrayList<String> labels = new ArrayList<String>();
            labels.add("Total");
            labels.add("Success");
            labels.add("Not Answered");

            dataset.setColors(ColorTemplate.COLORFUL_COLORS);
            PieData data = new PieData(labels, dataset); // initialize Piedata<br />
            outgoingPiechart.setData(data);
            outgoingPiechart.setDescription("");
            try {
                if(android.os.Build.VERSION.SDK_INT > 19)
                    outgoingPiechart.setElevation(2.0f);
            }catch(Exception e)
            {

            }

            outgoingPiechart.setCenterTextSize(30);
            outgoingPiechart.getLegend().setEnabled(false);
            setCustomLegendForMyOutgoingCalls();

        }
    }

    private void setCustomLegendForMyOutgoingCalls() {

        {

            valueTxt_OutgoingCalls = new TextView[3];
            Legend l = outgoingPiechart.getLegend();
            int colorcodes[] = l.getColors();
            Context context = getActivity();

            for (int i = 0; i < l.getColors().length - 1; i++) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                TableRow tr = (TableRow)inflater.inflate(R.layout.table_row_legend, childLayout1, false);

                childLayout1.addView(tr);

                LinearLayout linearLayoutColorContainer=(LinearLayout) tr.getChildAt(0);
                LinearLayout linearLayoutColor= (LinearLayout)linearLayoutColorContainer.getChildAt(0);
                TextView tvLabel = (TextView) tr.getChildAt(1);
                TextView tvAmt = (TextView) tr.getChildAt(2);
                valueTxt_OutgoingCalls[i] = tvAmt;
                linearLayoutColor.setBackgroundColor(colorcodes[i]);
                tvLabel.setText(l.getLabel(i));
                int aa = (int)(entries.get(i).getVal());
                if( i == 0)
                    aa = mView_HealthStatus.iTotal_OutgoingCalls;
                else if( i == 1)
                    aa = mView_HealthStatus.iOutgoing_Success;
                else
                    aa = mView_HealthStatus.iNoAnswered_Calls;

                tvAmt.setText(aa + "");
            }

            outgoingPiechart.setCenterText(mView_HealthStatus.iTotal_Calls+"");

            outgoingPiechart.getLegend().setWordWrapEnabled(true);
            outgoingPiechart.getLegend().setEnabled(false);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void createViewForIncomingcCalls() {
//onCallStateChanged
        entries = new ArrayList<>();
        entries.add(new Entry(0, 0));
        entries.add(new Entry(0, 1));
        entries.add(new Entry(0, 2));

        dataset = new PieDataSet(entries, "# of Calls");

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("Total");
        labels.add("Answered");
        labels.add("Missed");

        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data = new PieData(labels, dataset); // initialize Piedata<br />
        pieChart.setData(data);
        pieChart.setDescription("");

        try {
            if(android.os.Build.VERSION.SDK_INT > 19)
                pieChart.setElevation(2.0f);
        }catch(Exception e)
        {

        }

        pieChart.setCenterTextSize(30);
        pieChart.getLegend().setEnabled(false);
        setCustomLegendForMyCalls();
    }

    private void initializeNtwrkCoverageChart() {

        childlayout_nc = (TableLayout) v.findViewById(R.id.child_layout_nc);
        pieChart_nc = (PieChart) v.findViewById(R.id.chart_nc);
        entries_nc = new ArrayList<>();
        entries_nc.add(new Entry(10, 0));
        entries_nc.add(new Entry(20, 1));
        entries_nc.add(new Entry(30, 2));
        entries_nc.add(new Entry(40, 3));

        dataset_nc = new PieDataSet(entries_nc, "Network Coverage");

        ArrayList<String> labels_nc = new ArrayList<String>();
        labels_nc.add("4G");
        labels_nc.add("3G");
        labels_nc.add("2G");
        labels_nc.add("NS");

        int[] colors = new int[labels_nc.size()];
        for (int i = 0; i < labels_nc.size(); i++) {
            colors[i]=ColorTemplate.JOYFUL_COLORS[i];

        }
        dataset_nc.setColors(colors);
        //dataset_nc.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data_nc = new PieData(labels_nc, dataset_nc);
        pieChart_nc.setData(data_nc);
        pieChart_nc.setDescription("");
        pieChart_nc.highlightValues(null);
        pieChart_nc.setDescriptionTextSize(12f);
        //pieChart_nc.setElevation(2.0f);

        pieChart_nc.setCenterTextSize(30);
        pieChart_nc.getLegend().setEnabled(false);
        setCustomLegendForMyCoverage();

    }

    private void init() {

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.menu);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getActivity().onBackPressed();
                DrawerLayout drawer = getActivity().findViewById(R.id.drawer_layout);
                drawer.openDrawer(Gravity.LEFT);
            }
        });


       // button=v.findViewById(R.id.button_click);



        secondary_sim_ll=v.findViewById(R.id.secondary_sim_layout);
        imsi_tv = (TextView) v.findViewById(R.id.imsi2);
        imsi1_tv=(TextView) v.findViewById(R.id.imsi1);
        no_secondary_sim=(TextView)v.findViewById(R.id.nosecondsim);


        info1 = (TextView) v.findViewById(R.id.info1);
        info2 = (TextView) v.findViewById(R.id.info2);
        info3 = (TextView) v.findViewById(R.id.info3);
        info4 = (TextView) v.findViewById(R.id.info4);
        ssidNameVal=v.findViewById(R.id.ssidNameVal);
        wifiSSVal=v.findViewById(R.id.wifiSSVal);
        channelVal=v.findViewById(R.id.channelVal);
        frequencyVal=v.findViewById(R.id.frequencyVal);
        manufacturerVal=v.findViewById(R.id.manufacturerVal);
        wifiStandardVal=v.findViewById(R.id.wifiStandardVal);
        bssidVal=v.findViewById(R.id.bssidVal);
        linkSpeedVal=v.findViewById(R.id.linkSpeedVal);
        modelNameTv=v.findViewById(R.id.modelNameTv);
        androidVersionTv=v.findViewById(R.id.androidVersionTv);
        sdkVersionTv=v.findViewById(R.id.sdkVersionTv);
        batteryTv=v.findViewById(R.id.batteryTv);



        signalStrengthGauge=v.findViewById(R.id.ssGauge);
        linkSpeedGauge=v.findViewById(R.id.linkSpeedGauge);
        wifiDetailsTL=v.findViewById(R.id.wifiDetailsTL);
        setBasicValuesToGauge();
        btryTv=v.findViewById(R.id.btryTv);
        info2part2=(TextView)v.findViewById(R.id.info2part2);
        info2part2.setOnClickListener(this);

        TextView my_phone = (TextView) v.findViewById(R.id.my_phone);
        AssetManager m = getActivity().getAssets();
     /*   Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Medium.ttf");
        my_phone.setTypeface(typeface);

        Typeface typeface1 = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Regular.ttf");
        info1.setTypeface(typeface1);
        info2.setTypeface(typeface1);
        info3.setTypeface(typeface1);
        info4.setTypeface(typeface1);
        imsi_tv.setTypeface(typeface1);
        imsi1_tv.setTypeface(typeface1);
*/

        btn_upload = (Button) v.findViewById(R.id.btn_upload);
        btn_video = (Button) v.findViewById(R.id.btn_video);
        btn_web = (Button) v.findViewById(R.id.btn_web);
        call_img = (ImageView) v.findViewById(R.id.call_img);
        my_phone_detail = (ImageView) v.findViewById(R.id.my_phone_detail);
        map_detail = (ImageView) v.findViewById(R.id.map_detail);
        horizontalBarChart = (HorizontalBarChart) v.findViewById(R.id.barchart);
        horizontalBarChartMB = (HorizontalBarChart) v.findViewById(R.id.barchartMb);
        childlayout = (TableLayout) v.findViewById(R.id.child_layout);
        pieChart = (PieChart) v.findViewById(R.id.chart);
        outgoingPiechart=(PieChart)v.findViewById(R.id.outgoingchart);
        childLayout1=(TableLayout)v.findViewById(R.id.child_layout1);
        tablelayout=(TableLayout)v.findViewById(R.id.prim_celltable);
        sec_table=(TableLayout)v.findViewById(R.id.sec_celltable);
        mainSecLL=v.findViewById(R.id.mainSecLL);
        no_sec_simLL=(LinearLayout)v.findViewById(R.id.no_secSimLL);
        prim_simOpVal=(TextView)v.findViewById(R.id.txt_prim_OpVal);
        nofirstSimLL = (LinearLayout) v.findViewById(R.id.no_firstSimLL);
        primInfoll =(LinearLayout) v.findViewById(R.id.primInfoll);
        primsecondsimll = (LinearLayout) v.findViewById(R.id.primsecondsimll);
        prim_simroaming=(TextView)v.findViewById(R.id.txt_prim_RoamingVal);
        prim_simss=(TextView)v.findViewById(R.id.txt_prim_ssval);
        prim_simimsi=(TextView)v.findViewById(R.id.txt_prim_ImsiVal);
        prim_carrierVal=(TextView)v.findViewById(R.id.txt_prim_modeval);
      //  prim_head=(TextView)v.findViewById(R.id.primary_sim);
        prim_network=(TextView)v.findViewById(R.id.txt_prim_networkVal);
     //   sec_head=(TextView)v.findViewById(R.id.sec_sim);
        sec_simOpVal=(TextView)v.findViewById(R.id.txt_sec_OpVal);
        sec_simroaming=(TextView)v.findViewById(R.id.txt_sec_RoamingVal);
        sec_simss=(TextView)v.findViewById(R.id.txt_sec_ssval);
        sec_simimsi=(TextView)v.findViewById(R.id.txt_sec_ImsiVal);
        sec_carrierVal=(TextView)v.findViewById(R.id.txt_sec_modeval);
        sec_network=(TextView)v.findViewById(R.id.txt_sec_networkVal);
        fragmentManager=getActivity().getSupportFragmentManager();
        imsi2Info=Utils.getSecondImsi(context);
        //System.out.println("default sim "+ Utils.getDefaultSim(context));
        if(Utils.getSecondImsi(context)!=null)
        {
            imsi_tv.setVisibility(View.VISIBLE);
            no_secondary_sim.setVisibility(View.GONE);

        }
        else
        {
            imsi_tv.setVisibility(View.GONE);
            no_secondary_sim.setVisibility(View.VISIBLE);
        }


      /*  open=(Button)v.findViewById(R.id.open);
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pkgname = "com.functionapps.peacock";
                boolean res=openApp(context, pkgname);
                System.out.println("result is "+String.valueOf(res));
            }
        });*/

    }

    private void setBasicValuesToGauge() {
      
            signalStrengthGauge.setMaxValue(0);
            signalStrengthGauge.setMinValue(-90);
            signalStrengthGauge.setMaxValueTextColor(Color.GREEN);
            signalStrengthGauge.setMinValueTextColor(Color.RED);
           linkSpeedGauge.setMaxValue(1000);
            linkSpeedGauge.setMinValue(0);
            linkSpeedGauge.setMaxValueTextColor(Color.GREEN);
            linkSpeedGauge.setMinValueTextColor(Color.RED);
        
    }

    public void setCustomLegendForMyCalls(){

        valueTxt_MyCalls = new TextView[3];
        Legend l = pieChart.getLegend();
        int colorcodes[] = l.getColors();
        Context context = getActivity();

        for (int i = 0; i < l.getColors().length - 1; i++) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            TableRow tr = (TableRow)inflater.inflate(R.layout.table_row_legend, childlayout, false);

            childlayout.addView(tr);

            LinearLayout linearLayoutColorContainer=(LinearLayout) tr.getChildAt(0);
            LinearLayout linearLayoutColor= (LinearLayout)linearLayoutColorContainer.getChildAt(0);
            TextView tvLabel = (TextView) tr.getChildAt(1);
            TextView tvAmt = (TextView) tr.getChildAt(2);
            valueTxt_MyCalls[i] = tvAmt;
            linearLayoutColor.setBackgroundColor(colorcodes[i]);
            tvLabel.setText(l.getLabel(i));
            int aa = (int)(entries.get(i).getVal());
            if( i == 0)
                aa = mView_HealthStatus.iTotal_Calls;
            else if( i == 1)
                aa = mView_HealthStatus.iCall_Answered;
            else
                aa = mView_HealthStatus.iCall_Missed;

            tvAmt.setText(aa + "");
        }

        pieChart.setCenterText(mView_HealthStatus.iTotal_Calls+"");
        pieChart.getLegend().setWordWrapEnabled(true);
        pieChart.getLegend().setEnabled(false);

    }

    public void setCustomLegendForMyCoverage(){

        valueTxt_MyCoverage = new TextView[4];
        Legend l = pieChart_nc.getLegend();
        int colorcodes[] = l.getColors();
        int sz = l.getColors().length;
        Context context = getActivity();

        for (int i = 0; i < l.getColors().length -1 ; i++) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            TableRow tr = (TableRow)inflater.inflate(R.layout.table_row_legend, childlayout, false);

            childlayout_nc.addView(tr);

            LinearLayout linearLayoutColorContainer=(LinearLayout) tr.getChildAt(0);
            LinearLayout linearLayoutColor= (LinearLayout)linearLayoutColorContainer.getChildAt(0);
            TextView tvLabel = (TextView) tr.getChildAt(1);
            TextView tvAmt = (TextView) tr.getChildAt(2);
            valueTxt_MyCoverage[i] = tvAmt;
            linearLayoutColor.setBackgroundColor(colorcodes[i]);
            tvLabel.setText(l.getLabel(i));
            int aa = (int)(entries_nc.get(i).getVal());

            tvAmt.setText(aa+"");
        }

        pieChart_nc.setCenterText(mView_HealthStatus.strCurrentNetworkState);

        pieChart_nc.getLegend().setWordWrapEnabled(true);
        pieChart_nc.getLegend().setEnabled(false);


    }

    Handler mHandler;
    public void useHandler() {
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, mView_HealthStatus.updateDashboardUIIntervalInSeconds*1000);
    }

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            //Log.e("Handlers", "Calls");
            System.out.println("calling  update ui" +dontRefreshScreen);
            if(dontRefreshScreen == false)
            {
                try {
                    updateUI();
                    mHandler.postDelayed(mRunnable, mView_HealthStatus.updateDashboardUIIntervalInSeconds*1000);
                }catch(Exception e)
                {
                    Log.e("mView MainAct Frag" , e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    };

    void setClickListeners()
    {
        my_phone_detail.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                Log.v("mView", " click");
                dontRefreshScreen = true;
                Intent intent3 = new Intent(getActivity(), MyTabControl.class);//ViewMyPhoneDetail.class);
                intent3.putExtra( "view", 1 );
                startActivityForResult(intent3, 100);
            }
        });

        call_img.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                Log.v("mView", " click");
                dontRefreshScreen = true;
                Intent intent3 = new Intent(getActivity(), call_sms.class);
                startActivityForResult(intent3, 100);
            }
        });

        map_detail.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                Log.v("mView", " click");
                dontRefreshScreen = true;
                Intent intent3 = new Intent(getActivity(), MyTabControl.class); //ViewMyMap.class);
                intent3.putExtra( "view", 2 );
                startActivityForResult(intent3, 100);
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                Log.v("mView", " click");
                dontRefreshScreen = true;
                Intent intent3 = new Intent(getActivity(), mView_UploadDownloadTest.class);
                startActivityForResult(intent3,100);
            }
        });



        btn_web.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                dontRefreshScreen = true;
                Log.v("mView", " click");
                Intent intent3 = new Intent(getActivity(), mView_WebTest.class);
                startActivityForResult(intent3, 100);
            }
        }
        );

        btn_video.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                dontRefreshScreen = true;
                //Log.v("mView", " click");
                dontRefreshScreen = true;
                //Log.v("mView", " click");
                Intent intent3 = new Intent(getActivity(), mView_VideoTest.class);
                startActivityForResult(intent3, 100);
            }
        });

     //   button.setOnClickListener(new View.OnClickListener() {
       //     @Override
       //     public void onClick(View v) {
       //         openActivity2();

               // showAlertDialog("HELLO",Constants.NO_INTERNET);



         //   }
     //   });




        //new read().start();
    }

    public class read implements Runnable {
        private Thread mBlinker;
        private ArrayList<String> output = new ArrayList<String>();
        public String getResponce() {
            if (output.size() != 0) {
                String ans = output.get(0);
                output.remove(0);
                return ans;
            }return null; }
            public void start() {
            mBlinker = new Thread(this);
            mBlinker.start();
        }
        public void stop() {
            mBlinker = null;
        }
        private DataInputStream dis;
        private DataOutputStream dos;
        @Override
        public void run() {
            System.out.println("START READ");
            try {
                Runtime r = Runtime.getRuntime();
                //Process process = r.exec(new String[] {"su", "AT+CSQ", "AT+CREG" });
                Process process = r.exec("su"); //echo -e \"AT+CSQ\r\"");
                process = r.exec(" *#0011#");
                // process = r.exec(" echo -e \"AT\\r\" ");

                dos = new DataOutputStream(process.getOutputStream());

                dos.writeBytes("cat /dev/smd0\n");
                dos.flush();

                dis = new DataInputStream(process.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (mBlinker != null) {
                try {
                    int av = dis.available();
                    if (av != 0) {
                        byte[] b = new byte[av];
                        dis.read(b);
                        output.add(new String(b));
                        System.out.println(new String(b) + "Recieved form modem");
                    }
                    else
                    {
                        Thread.sleep(100);
                    }
                } catch (IOException e) {
                    if (e.getMessage() != null)
                        System.out.println(e.getMessage());
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            try {
                dos.writeBytes("exit\n");
                dos.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("STOP READ");
        }
    }
    float batlevl=0;

   // public void openActivity2(){
    //    Intent intent=new Intent(getActivity(), Activity2.class);
    //    startActivity(intent);
    //}
    void updateUI() {
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(MviewApplication.ctx);
        SimInfoUtility.getSimInfo(context);
//        if((mView_HealthStatus.subSize==1)||(mView_HealthStatus.subSize==0))
//        {
//           no_sec_simLL.setVisibility(View.VISIBLE);
//            sec_table.setVisibility(View.GONE);
//        }
//        else
//        {
//            no_sec_simLL.setVisibility(View.GONE);
//            sec_table.setVisibility(View.VISIBLE);
//        }


        String subId1 = sharedPreferencesHelper.getSubId1();
        String subId2 = sharedPreferencesHelper.getSubId2();
//        JSONObject sim1servingobj= Telephony_params.getsim1servingcellinfo();
        Log.d(TAG, "updateUI: sub id's "+subId1+" id 2 is "+subId2);
        if (subId1.equals("SIM1")){
            tablelayout.setVisibility(View.VISIBLE);
            no_sec_simLL.setVisibility(View.VISIBLE);
            sec_table.setVisibility(View.GONE);
            prim_simimsi.setText(mView_HealthStatus.prim_imsi);
            nofirstSimLL.setVisibility(View.GONE);
            prim_simOpVal.setText(mView_HealthStatus.prim_carrierName);
            primInfoll.setVisibility(View.VISIBLE);
            primsecondsimll.setVisibility(View.GONE);

        }
        if (subId2.equals("SIM2") || subId1.equals("SIM2")){
            JSONObject sim1servingobj= Telephony_params.getsim2servingcellinfo();
            Log.d(TAG, "updateUI: ss value is dbm");
            no_sec_simLL.setVisibility(View.GONE);
            sec_table.setVisibility(View.VISIBLE);
//            prim_simOpVal.setVisibility(View.GONE);
            sec_simimsi.setText(mView_HealthStatus.prim_imsi);
            sec_simss.setText(sim1servingobj.optString("signalStrength"));
            tablelayout.setVisibility(View.GONE);
            primsecondsimll.setVisibility(View.VISIBLE);
//            sec_simOpVal.setText(mView_HealthStatus.prim_carrierName);
            nofirstSimLL.setVisibility(View.VISIBLE);
            primInfoll.setVisibility(View.GONE);

        }
        if (subId1.equals("SIM1") && subId2.equals("SIM2")){
            tablelayout.setVisibility(View.VISIBLE);
            sec_table.setVisibility(View.VISIBLE);
            no_sec_simLL.setVisibility(View.GONE);
            primInfoll.setVisibility(View.VISIBLE);
            nofirstSimLL.setVisibility(View.GONE);
            primsecondsimll.setVisibility(View.VISIBLE);
//            prim_simOpVal.setText(mView_HealthStatus.prim_carrierName);
        }

        batlevl = getBatteryLevel();
        Log.d(TAG, " ");

      //  btryTv.setText("Battery : " + String.format("%.0f", batlevl) + "%");
       info1.setText(Build.MODEL + /*"\n"+ " (" + Build.BRAND + " - " + Build.PRODUCT +  ")" +*/
               "\n"+"Sdk Version - " + android.os.Build.VERSION.SDK_INT+
                "\n"+"Android Version - " + Build.VERSION.RELEASE
       +"\n"+"Battery : " + String.format("%.0f", batlevl) + "%");
       modelNameTv.setText(Build.MODEL);
       sdkVersionTv.setText(android.os.Build.VERSION.SDK_INT+"");
       androidVersionTv.setText(Build.VERSION.RELEASE+"");
       batteryTv.setText( String.format("%.0f", batlevl) + "%");
        String state = "";
        if (MyPhoneStateListener.lastServiceState != null) {
            switch (MyPhoneStateListener.lastServiceState.getState()) {
                case ServiceState.STATE_IN_SERVICE:
                    state = "IN_SERVICE";
                    break;
                case ServiceState.STATE_OUT_OF_SERVICE:
                    state = "OUT_OF_SERVICE";
                    break;
                case ServiceState.STATE_EMERGENCY_ONLY:
                    state = "EMERGENCY_ONLY";
                    break;
                case ServiceState.STATE_POWER_OFF:
                    state = "POWER_OFF";
                    break;
            }
        }
        // if(MyPhoneStateListener.lastSignalStrength != null) {
        //  state = state + " - " + MyPhoneStateListener.getSignalStrength(MyPhoneStateListener.lastSignalStrength);
        String strength = "";
        System.out.println("current instance "+mView_HealthStatus.currentInstance +" Rsrp "+mView_HealthStatus.lteRSRP);
        if (mView_HealthStatus.currentInstance.equalsIgnoreCase("lte")) {
            Log.i(TAG,"Check 1");
            {
                if (mView_HealthStatus.lteRSRP != null) {
                    Log.i(TAG,"Check 2");
                    if(Utils.checkIfNumeric(mView_HealthStatus.lteRSRP)) {
                        Log.i(TAG,"Check 3");
                        if (Integer.parseInt(mView_HealthStatus.lteRSRP) < 0 && Integer.parseInt(mView_HealthStatus.lteRSRP) > -75) {
                            Log.i(TAG,"Check 4");
                            info2part2.setTextColor(getResources().getColor(R.color.green));
                            prim_simss.setTextColor(getResources().getColor(R.color.green));
                            strength = mView_HealthStatus.lteRSRP + "dbm ( Good )";
                        } else if (Integer.parseInt(mView_HealthStatus.lteRSRP) <= -75 && Integer.parseInt(mView_HealthStatus.lteRSRP) > -95) {
                            Log.i(TAG,"Check 5");
                            info2part2.setTextColor(getResources().getColor(R.color.orange));
                            prim_simss.setTextColor(getResources().getColor(R.color.orange));
                            strength = mView_HealthStatus.lteRSRP + "dbm ( Fine )";
                        } else if (Integer.parseInt(mView_HealthStatus.lteRSRP) <= -95 /*&& Integer.parseInt(mView_HealthStatus.lteRSRP) > -115*/) {
                            Log.i(TAG,"Check 6");
                            info2part2.setTextColor(getResources().getColor(R.color.blue));
                            prim_simss.setTextColor(getResources().getColor(R.color.blue));
                            strength = mView_HealthStatus.lteRSRP + "dbm ( Poor )";
                        }
                    }

                }

            }
        } else if (mView_HealthStatus.currentInstance.equalsIgnoreCase("wcdma")) {
            if (mView_HealthStatus.rscp != null) {
                if(Utils.checkIfNumeric(mView_HealthStatus.rscp ))
                { if (Integer.parseInt(mView_HealthStatus.rscp) < 0 && Integer.parseInt(mView_HealthStatus.rscp) > -75) {
                        info2part2.setTextColor(getResources().getColor(R.color.green));
                        prim_simss.setTextColor(getResources().getColor(R.color.green));
                        strength = mView_HealthStatus.rscp + "  dbm (Good)";
                    } else if (Integer.parseInt(mView_HealthStatus.rscp) <= -75 && Integer.parseInt(mView_HealthStatus.rscp) > -95) {
                        info2part2.setTextColor(getResources().getColor(R.color.orange));
                        prim_simss.setTextColor(getResources().getColor(R.color.orange));
                        strength = mView_HealthStatus.rscp + "  dbm (Fine)";
                    } else if (Integer.parseInt(mView_HealthStatus.rscp) <= -95 /*&& Integer.parseInt(mView_HealthStatus.rscp) > -115*/) {
                        info2part2.setTextColor(getResources().getColor(R.color.blue));
                        prim_simss.setTextColor(getResources().getColor(R.color.blue));
                        strength = mView_HealthStatus.rscp + " dbm (Poor) ";
                    }
                }

            }


        } else if (mView_HealthStatus.currentInstance.equalsIgnoreCase("gsm")) {
            Log.i(TAG,"Gsm check 1");
            if (mView_HealthStatus.gsmSignalStrength < 0 && mView_HealthStatus.gsmSignalStrength > -75) {
                Log.i(TAG,"Gsm check 2");
                    info2part2.setTextColor(getResources().getColor(R.color.green));
                    prim_simss.setTextColor(getResources().getColor(R.color.green));
                    strength = mView_HealthStatus.gsmSignalStrength + " dbm (Good)";
                } else if (mView_HealthStatus.gsmSignalStrength <= -75 && mView_HealthStatus.gsmSignalStrength > -95) {
                Log.i(TAG,"Gsm check 3");
                    info2part2.setTextColor(getResources().getColor(R.color.orange));
                    prim_simss.setTextColor(getResources().getColor(R.color.orange));
                    strength = mView_HealthStatus.gsmSignalStrength + "  dbm (Fine)";
                } else if (mView_HealthStatus.gsmSignalStrength <= -95 /*&& mView_HealthStatus.gsmSignalStrength > -115*/) {
                Log.i(TAG,"Gsm check 4");
                    info2part2.setTextColor(getResources().getColor(R.color.blue));
                    prim_simss.setTextColor(getResources().getColor(R.color.blue));
                    strength = mView_HealthStatus.gsmSignalStrength + "dbm (Poor)";
                }


        }
//second sim
        String sec_strength = null;
        if (mView_HealthStatus.second_cellInstance != null) {
            if (mView_HealthStatus.second_cellInstance.equalsIgnoreCase("lte")) {
                {
                    if (mView_HealthStatus.second_Rsrp < 0 && mView_HealthStatus.second_Rsrp > -75) {
                        sec_simss.setTextColor(getResources().getColor(R.color.green));
                        sec_strength = mView_HealthStatus.second_Rsrp + "dbm ( Good )";
                    } else if (mView_HealthStatus.second_Rsrp <= -75 && mView_HealthStatus.second_Rsrp > -95) {
                        sec_simss.setTextColor(getResources().getColor(R.color.orange));
                        sec_strength = mView_HealthStatus.second_Rsrp + "dbm ( Fine )";
                    } else if (mView_HealthStatus.second_Rsrp <= -95 /*&& mView_HealthStatus.second_Rsrp > -115*/) {
                        sec_simss.setTextColor(getResources().getColor(R.color.blue));
                        sec_strength = mView_HealthStatus.second_Rsrp + "dbm ( Poor )";
                    }

                }


            } else if (mView_HealthStatus.second_cellInstance.equalsIgnoreCase("wcdma")) {
                if (mView_HealthStatus.second_rscp_3G < 0 && mView_HealthStatus.second_rscp_3G > -75) {
                    sec_simss.setTextColor(getResources().getColor(R.color.green));
                    sec_strength = mView_HealthStatus.second_rscp_3G + "  dbm (Good)";
                } else if (mView_HealthStatus.second_rscp_3G <= -75 && mView_HealthStatus.second_rscp_3G > -95) {
                    sec_simss.setTextColor(getResources().getColor(R.color.orange));
                    sec_strength = mView_HealthStatus.second_rscp_3G + "  dbm (Fine)";
                } else if (mView_HealthStatus.second_rscp_3G <= -95 /*&& mView_HealthStatus.second_rscp_3G > -115*/) {
                    sec_simss.setTextColor(getResources().getColor(R.color.blue));
                    sec_strength = mView_HealthStatus.second_rscp_3G + " dbm (Poor) ";
                }


            } else if (mView_HealthStatus.second_cellInstance.equalsIgnoreCase("gsm")) {
                if (mView_HealthStatus.second_rxLev < 0 && mView_HealthStatus.second_rxLev > -75) {
                    sec_simss.setTextColor(getResources().getColor(R.color.green));
                    sec_strength = mView_HealthStatus.second_rxLev + " dbm (Good)";
                } else if (mView_HealthStatus.second_rxLev <= -75 && mView_HealthStatus.second_rxLev > -95) {
                    sec_simss.setTextColor(getResources().getColor(R.color.orange));
                    sec_strength = mView_HealthStatus.second_rxLev + "  dbm (Fine)";
                } else if (mView_HealthStatus.second_rxLev <= -95 /*&& mView_HealthStatus.second_rxLev > -115*/) {
                    sec_simss.setTextColor(getResources().getColor(R.color.blue));
                    sec_strength = mView_HealthStatus.second_rxLev + "dbm (Poor)";
                }

            }
        }
        //   }
        String roaming = "";
        if (MyPhoneStateListener.lastServiceState != null) {
            mView_HealthStatus.roaming = MyPhoneStateListener.lastServiceState.getRoaming();
            if (mView_HealthStatus.roaming)
                roaming = " - In Roaming";
        }
        if (strength != null) {
            System.out.println("strength is " + strength);
            if (!((strength.equalsIgnoreCase(null)) || strength.equals("") || strength.equals(" "))) {
                prim_simss.setText(strength);
                info2part2.setText(strength);
            }
            else {
                JSONObject sim1servingobj= Telephony_params.getsim1servingcellinfo();
                prim_simss.setText(sim1servingobj.optString("signalStrength"));
                info2part2.setText(strength);
            }
        }
        if (sec_strength != null) {
            System.out.println("sec strength is " + sec_strength);
            sec_simss.setText(sec_strength);
        }

        //phonetype was showing GSM for an LTE phone and sim on Amit's phone. so not showing it now
        //info2.setText(mView_HealthStatus.OperatorName + " - " + mView_HealthStatus.phonetype + " - " + state + roaming);
        String carrier = "";

        if (mView_HealthStatus.carrier_selection) {
            carrier = "Manual Mode";
        } else {
            carrier = "Automatic Mode";
        }
        info2.setText(mView_HealthStatus.OperatorName + " - " + state + "\n" + roaming + "\n" + "( " + carrier + " )");
        String thirdLine = "";
        if (MyPhoneStateListener.lastCellLocation != null)
            thirdLine = MyPhoneStateListener.lastCellLocation.toString();

        info3.setText("Cell : " + thirdLine + " " + mView_HealthStatus.strCurrentNetworkState);
        batlevl = getBatteryLevel();
        Utils.appendLog("ELOG_BATTERY_LEVEL: is  "+String.format("%.0f", batlevl) + "%");

        info4.setText(chkDataConnectivity());
        btryTv.setText("Battery : " + String.format("%.0f", batlevl) + "%");
        ;
        prim_simroaming.setText(mView_HealthStatus.prim_getDataRoaming);
        String prim_carrier = null;
        if (mView_HealthStatus.prim_carrierMode == 1) {
            prim_carrier = "Manual Mode";

        } else if (mView_HealthStatus.prim_carrierMode == 0) {
            prim_carrier = "Automatic Mode";
        }
        String sec_carrier = null;
        if (mView_HealthStatus.sec_carrierMode == 1) {
            sec_carrier = "Manual Mode";

        } else if (mView_HealthStatus.sec_carrierMode == 0) {
            sec_carrier = "Automatic Mode";
        }
      //  prim_head.setText("SIM 1" + "(" + "Slot " + mView_HealthStatus.prim_Slot + ")");
       /* if (mView_HealthStatus.subSize > 1) {
            sec_head.setText("SIM 2" + "(" + "Slot " + mView_HealthStatus.sec_slot + ")");
        } else
        {
            sec_head.setText("SIM 2");*/
    //}
        if(mView_HealthStatus.prim_NetworkType!=null) {
           prim_network.setText(mView_HealthStatus.prim_NetworkType);
        }


        if(prim_carrier!=null) {
            prim_carrierVal.setText(prim_carrier);
        }

        sec_simOpVal.setText(mView_HealthStatus.sec_carrierName);
        sec_simroaming.setText(mView_HealthStatus.sec_getDataRoaming);
        if(sec_carrier!=null) {
            sec_carrierVal.setText(sec_carrier);
        }
        if(mView_HealthStatus.sec_NetworkType!=null) {
            sec_network.setText(mView_HealthStatus.sec_NetworkType);
        }
     /*  imsi1_tv.setText("Sim Operator - "+mView_HealthStatus.prim_carrierName+"\n"+"Roaming - "+mView_HealthStatus.prim_getDataRoaming +"\n"+"IMSI - "+mView_HealthStatus.prim_imsi
       );
*/
         // imsi1_tv.setText(" SIM OPERATOR NAME - "+mView_HealthStatus.simOperatorName  + " - " + state + "\n"+ roaming +"\n"+"( " +carrier +" )");

        if(imsi2Info!=null)
        {
            String roaming1="";
            String imsi="";
            if(imsi2Info.size()>1) {
                if (imsi2Info.get(1).equals("1")) {
                    roaming1 = "IN ROAMING ";
                }
            }

            try {
if(imsi2Info.size()>0)
                {
                    imsi_tv.setText("\n" + "IMSI - " + imsi2Info.get(0) + "\n" + "\n" + "OPERATOR - " + imsi2Info.get(1) + "\n" + "\n" + roaming1);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }


        }
        refreshCallStatusChart();
        refreshOutgoingCallsChart();
        refreshMyCoverageChart();
        refreshDataUsageChart();
        refreshBatteryChart();
    }//end updateUI function
    private void refreshOutgoingCallsChart() {
        mView_HealthStatus.iTotal_OutgoingCalls=mView_HealthStatus.iOutgoing_Success+mView_HealthStatus.iNoAnswered_Calls;
        editor.putInt("totaloutgoingcalls",mView_HealthStatus.iTotal_OutgoingCalls);
        valueTxt_OutgoingCalls[0].setText(mView_HealthStatus.iTotal_OutgoingCalls+"");
        valueTxt_OutgoingCalls[1].setText(mView_HealthStatus.iOutgoing_Success + "");
        valueTxt_OutgoingCalls[2].setText(mView_HealthStatus.iNoAnswered_Calls + "");
        outgoingPiechart.setCenterText(mView_HealthStatus.iTotal_OutgoingCalls + "");
        entries = new ArrayList<>();
    //    entries.add(new Entry(mView_HealthStatus.iTotal_OutgoingCalls, 0));
        entries.add(new Entry(mView_HealthStatus.iOutgoing_Success, 0));
        entries.add(new Entry(mView_HealthStatus.iNoAnswered_Calls, 1));
        dataset = new PieDataSet(entries, "# of Calls");
        ArrayList<String> labels = new ArrayList<String>();
      //  labels.add("Total");
        labels.add("Success");
        labels.add("Not answered");
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data = new PieData(labels, dataset);
        data.setValueTextSize(12f);
        outgoingPiechart.setData(data);
        outgoingPiechart.invalidate();
    }

    private void showAlertDialog(String title,String message) {
        AlertDialog.Builder alert=new AlertDialog.Builder(getActivity(),R.style.AlertDialogTheme);
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setCancelable(true);
        alert.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int id) {
                        dialog.cancel();
                    }
                });
        alert.show();
    }

    private void refreshBatteryChart()
    {
        // creating list of entry
        ArrayList<Entry>entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();

        if( mView_HealthStatus.timeSeriesCapturedData != null) {

            for (int i = 0; i < mView_HealthStatus.timeSeriesCapturedData.size(); i++) {
                float bat = Float.parseFloat(mView_HealthStatus.timeSeriesCapturedData.get(i).batterylevel);
                entries.add(new Entry(bat, i));

                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                Date resultdate = new Date(mView_HealthStatus.timeSeriesCapturedData.get(i).captureTimeN);
                String displaydate = sdf.format(resultdate);
                labels.add(displaydate);
            }
        }else {
            entries.add(new Entry(batlevl, 0));
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date resultdate = new Date(System.currentTimeMillis());
            String displaydate = sdf.format(resultdate);
            labels.add(displaydate);
        }

        LineDataSet dataset = new LineDataSet(entries, "battery level %");

        LineData data = new LineData(labels, dataset);
        batteryLinechart.setData(data);
        XAxis xAxis = batteryLinechart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        batteryLinechart.setDescription("");
        dataset.setDrawFilled(true);
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
        dataset.setFillColor(Color.MAGENTA);
        batteryLinechart.invalidate();
    }
    public void refreshCallStatusChart()
    {
        mView_HealthStatus.iTotal_Calls=mView_HealthStatus.iCall_Missed+mView_HealthStatus.iCall_Answered;
        editor.putInt("totalcalls",mView_HealthStatus.iTotal_Calls).apply();
        valueTxt_MyCalls[0].setText(mView_HealthStatus.iTotal_Calls+"");
        valueTxt_MyCalls[1].setText(mView_HealthStatus.iCall_Answered + "");
        valueTxt_MyCalls[2].setText(mView_HealthStatus.iCall_Missed + "");
        pieChart.setCenterText(mView_HealthStatus.iTotal_Calls + "");
        entries = new ArrayList<>();
      //  entries.add(new Entry(mView_HealthStatus.iTotal_Calls, 0));
        entries.add(new Entry(mView_HealthStatus.iCall_Answered, 0));
        entries.add(new Entry(mView_HealthStatus.iCall_Missed, 1));

        dataset = new PieDataSet(entries, "# of Calls");

        ArrayList<String> labels = new ArrayList<String>();
        //labels.add("Total");
        labels.add("Answered");
        labels.add("Missed");

        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
        dataset.setValueTextSize(12f);
        PieData data = new PieData(labels, dataset);
        pieChart.setData(data);
        pieChart.invalidate();
    }//end refreshCallStatusChart

    public void refreshMyCoverageChart()
    {
        pieChart_nc.setCenterText(mView_HealthStatus.onlyCurrentNetworkState);

        long elapsedTime =  (System.currentTimeMillis() - mView_HealthStatus.starTimeInCurrentState);
        long totalTime = mView_HealthStatus.timein4G + mView_HealthStatus.timein3G + mView_HealthStatus.timein2G + mView_HealthStatus.timeinNS +elapsedTime;
        float totalTimeInMinutes = totalTime / (1000*60.0f);
        if( totalTimeInMinutes == 0.0f)
            totalTimeInMinutes = 0.1f;

        mView_HealthStatus.per4g = (float)(mView_HealthStatus.timein4G*100) / ((totalTimeInMinutes*1.0f)*(1000*60));
        mView_HealthStatus.per3g = (float)(mView_HealthStatus.timein3G*100) / ((totalTimeInMinutes*1.0f)*(1000*60));
        mView_HealthStatus.per2g = (float)(mView_HealthStatus.timein2G*100) / ((totalTimeInMinutes*1.0f)*(1000*60));
        mView_HealthStatus.perNS = (float)(mView_HealthStatus.timeinNS*100) / ((totalTimeInMinutes*1.0f)*(1000*60));
        if( mView_HealthStatus.iCurrentNetworkState == 4)
        {

            mView_HealthStatus.per4g = ( ((mView_HealthStatus.timein4G + elapsedTime)/(1000*60.0f) )*100) / (totalTimeInMinutes*1.0f);
            //  Toast.makeText(getActivity(), "time is 4g "+mView_HealthStatus.per4g , Toast.LENGTH_SHORT).show();
            if( mView_HealthStatus.per4g> 100)
                mView_HealthStatus.per4g = 100;
        }else if( mView_HealthStatus.iCurrentNetworkState == 3)
        {


            mView_HealthStatus.per3g = ( ((mView_HealthStatus.timein3G + elapsedTime)/(1000*60.0f) )*100) / (totalTimeInMinutes*1.0f);
            //      Toast.makeText(getActivity(), "time is 3g "+mView_HealthStatus.per4g, Toast.LENGTH_SHORT).show();
            if( mView_HealthStatus.per3g> 100)
                mView_HealthStatus.per3g = 100;
        }else if( mView_HealthStatus.iCurrentNetworkState == 2)
        {
            mView_HealthStatus.per2g = ( ((mView_HealthStatus.timein2G + elapsedTime)/(1000*60.0f) )*100) / (totalTimeInMinutes*1.0f);
            if( mView_HealthStatus.per2g> 100)
                mView_HealthStatus.per2g = 100;
        }else if( mView_HealthStatus.iCurrentNetworkState == 0)
        {

            mView_HealthStatus.perNS = ( ((mView_HealthStatus.timeinNS + elapsedTime)/(1000*60.0f) )*100) / (totalTimeInMinutes*1.0f);
            if( mView_HealthStatus.perNS> 100)
                mView_HealthStatus.perNS = 100;
        }

        valueTxt_MyCoverage[0].setText(String.format("%.2f",mView_HealthStatus.per4g)+"%");
        valueTxt_MyCoverage[1].setText(String.format("%.2f",mView_HealthStatus.per3g)+"%");
        valueTxt_MyCoverage[2].setText( String.format("%.2f",mView_HealthStatus.per2g)+"%");
        valueTxt_MyCoverage[3].setText(String.format("%.2f",mView_HealthStatus.perNS)+"%");

        entries_nc = new ArrayList<>();

        if(mView_HealthStatus.per4g!=0)
        entries_nc.add(new Entry(mView_HealthStatus.per4g, 0));
        if(mView_HealthStatus.per3g!=0)
        entries_nc.add(new Entry(mView_HealthStatus.per3g, 1));
        if(mView_HealthStatus.per2g!=0)
        entries_nc.add(new Entry(mView_HealthStatus.per2g, 2));
        if(mView_HealthStatus.perNS!=0)
        entries_nc.add(new Entry(mView_HealthStatus.perNS, 3));

        dataset_nc = new PieDataSet(entries_nc, "# of Calls");

        ArrayList<String> labels1 = new ArrayList<String>();
        labels1.add("4G");
        labels1.add("3G");
        labels1.add("2G");
        labels1.add("NS");

        dataset_nc.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data_nc = new PieData(labels1, dataset_nc);
        data_nc.setValueTextSize(12f);
        pieChart_nc.setData(data_nc);
        pieChart_nc.invalidate();
    }//end refreshMyCoverageChart

    public void refreshDataUsageChart()
    {
        //////toast.makeText(getActivity(), "refreh data chrt called", ////toast.LENGTH_SHORT).show();
        long currentMobileTxBytes = TrafficStats.getMobileTxBytes();// Return number of bytes transmitted across mobile networks since device
        // boot.
        long currentMobileRxBytes = TrafficStats.getMobileRxBytes();
        long totalTxBytes = TrafficStats.getTotalTxBytes();
        long totalRxBytes = TrafficStats.getTotalRxBytes();
        float l = currentMobileTxBytes + currentMobileRxBytes;
         mobileDataInMB = (l)/(1024*1024);
        System.out.println("mobile data "+l +"");
         wifiDataInMB = ((totalTxBytes + totalRxBytes)/(1024*1024)) - mobileDataInMB;
        //bug reported
        if(wifiDataInMB < 0 )
            wifiDataInMB = 0;
         total = wifiDataInMB + mobileDataInMB;
        ArrayList<BarEntry> entries = new ArrayList<>();
        float wifiPercent= ((float)(wifiDataInMB *100 )/total);
        float mobilePercent= ((float)(mobileDataInMB *100 )/total);
        entries.add(new BarEntry((float) wifiPercent, 0));
        entries.add(new BarEntry((float) mobilePercent, 1));
    //    entries.add(new BarEntry(total, 2));
        setValuesToDataUsageGraph(wifiPercent,mobilePercent,"My Data Usage(Percentage)",horizontalBarChart);
        setValuesToDataUsageGraph(wifiDataInMB,mobileDataInMB,"My Data Usage(MB)",horizontalBarChartMB);



    }//refreshDataUsageChart

    private void setValuesToDataUsageGraph(float wifiVal, float mobileVal, String legendTitle, HorizontalBarChart horizontalBarChart) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(wifiVal, 0));
        entries.add(new BarEntry(mobileVal, 1));
        BarDataSet dataset = new BarDataSet(entries, legendTitle);
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
        ArrayList<String> labels = new ArrayList<String> ();
        labels.add("WiFi");
        labels.add("Mobile");
        //    labels.add("Total");
        BarData data = new BarData(labels, dataset);
        horizontalBarChart.setData(data);
        data.setValueTextSize(10f);
        horizontalBarChart.setDescription("");
        horizontalBarChart.invalidate();
        horizontalBarChart.setClickable(false);
        dataset.setValueFormatter(new LargeValueFormatter());
    }

    //public static GPSTracker gps;
    void initValues()
    {
        Utils.printTelephonyManagerMethodNamesForThisDevice(context);
       /* Utils.getStateInfo(context);*/
        if (listenService.gps != null && listenService.gps.canGetLocation()) {
            lat = listenService.gps.getLatitude() + "";
            lon = listenService.gps.getLongitude() + "";
            if (lat.equals("0.0") || lon.equals("0.0"))
            {
                //////toast.makeText(this.getActivity(), "Turn On GPS,to get Latitude and Longitude Values.", ////toast.LENGTH_LONG).show();
            }
            Double d = Double.parseDouble(lat);
            lat = String.format("%.4f", d);
            Double z = Double.parseDouble(lon);
            lon = String.format("%.4f", z);
        } else
        {
            if(listenService.gps != null)
                listenService.gps.showSettingsAlert();
        }

    }//end Init
    public void setupAlarm() {


        //if(true)
        //    return;\
        Intent bck=new Intent(getActivity(),Background_service.class);
        getActivity().startService(bck);
        // //toast.makeText(getActivity(), "calling alarm bckgtnd dervice from home", //toast.LENGTH_SHORT).show();

    }
    String chkDataConnectivity() {
        String s = "";
        if(getActivity()!=null) {
            final ConnectivityManager connMgr = (ConnectivityManager)
                    Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
            final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifi.isConnectedOrConnecting()) {
                s = "WiFi";
                WifiManager wm = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wm.getConnectionInfo();
                int linkSpeed = wifiInfo.getLinkSpeed();
                String ss = wifiInfo.getSSID();
                int rs = wifiInfo.getRssi();
               /* int wifiStandard=-1;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                     wifiStandard=wifiInfo.getWifiStandard();
                }*/
                ssidNameVal.setText(ss);
                wifiSSVal.setText(rs+"dBM");
                int freq=wifiInfo.getFrequency();
                int channel=WifiConfig.convertFrequencyToChannel(freq);

               double freqBand= WifiConfig.getFreqBw(freq);
                channelVal.setText(channel+"");
                frequencyVal.setText(wifiInfo.getFrequency()+"MHz"+" ("+freqBand+"GHz)" );
                linkSpeedVal.setText(linkSpeed+"Mbps");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                   wifiStandardVal.setText(wifiInfo.getWifiStandard()+"");
                }
                bssidVal.setText(wifiInfo.getBSSID());

              /*  if(wifiStandard!=-1) {
                    s = ss + "\n" + "Link " + rs + "Mbps" + "\n" +"Signal "+ rs + "dBm" + "\n" + wifiStandard + "\n"
                            + "Ch-"+WifiConfig.convertFrequencyToChannel(wifiInfo.getFrequency())+"\n"+"BSSID "+wifiInfo.getBSSID()+"\n"+
                            "Freq "+wifiInfo.getFrequency()+" MHz";
                }
                else
                {
                    s = ss + "\n" + "Link:" + rs + "Mbps" + "\n" +"Signal:"+ rs + "dBm" + "\n"
                            + "Channel:"+WifiConfig.convertFrequencyToChannel(wifiInfo.getFrequency()) +"\n"+"BSSID "+wifiInfo.getBSSID()+"\n"+
                            "Freq "+wifiInfo.getFrequency()+" MHz";
                }*/
         //       s = "WiFi (Link " + linkSpeed + "Mbps, " + ss + ", " + rs + "dBm)\n";
                mView_HealthStatus.connectionType = "WiFi";
                mView_HealthStatus.connectionTypeIdentifier = 1;
                signalStrengthGauge.setValue(wifiInfo.getRssi());
                linkSpeedGauge.setValue(wifiInfo.getLinkSpeed());
            } else if (mobile.isConnectedOrConnecting()) {

                s = "Mobile data"; //getNetworkTypeString (listenService.telMgr.getNetworkType());
                s += "\n";
                mView_HealthStatus.connectionType = "Mobile data";
                mView_HealthStatus.connectionTypeIdentifier = 2;
                ssidNameVal.setText("");
                wifiSSVal.setText("");
                channelVal.setText("");
                frequencyVal.setText("");
                linkSpeedVal.setText("");
                wifiStandardVal.setText("");
                bssidVal.setText("");
                signalStrengthGauge.setValue(0);
                linkSpeedGauge.setValue(0);
            } else {
                s = "Offline";
                mView_HealthStatus.connectionTypeIdentifier = 0;
                ssidNameVal.setText("");
                wifiSSVal.setText("");
                channelVal.setText("");
                frequencyVal.setText("");
                linkSpeedVal.setText("");
                wifiStandardVal.setText("");
                bssidVal.setText("");
                signalStrengthGauge.setValue(0);
                linkSpeedGauge.setValue(0);
                ////////toast.makeText(this, "No Network ", ////toast.LENGTH_LONG).show();
            }
            return s;
        }
        return s;
    }
    public float getBatteryLevel() {
        float bat=0f;
        if(getActivity()!=null) {
            Intent batteryIntent = getActivity().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            if(batteryIntent!=null) {
                int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

                // Error checking that probably isn't needed but I added just in case.
                if (level == -1 || scale == -1) {
                    return 50.0f;
                }

                 bat = ((float) level / (float) scale) * 100.0f;
                mView_HealthStatus.batteryLevel = bat + "";
                return bat;
            }
        }
        return bat;
    }
    public void updateGoogleMap()
    {
        try {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
                LatLng sydney = new LatLng(-34, 151);
                googleMap.addMarker(new MarkerOptions().position(sydney).title("My Location").snippet("mView MapView"));

                // For zooming automatically to the location of the marker
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                        new LatLng(listenService.gps.getLatitude(), listenService.gps.getLongitude()), 15);
                //CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                //googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                googleMap.animateCamera(cameraUpdate);
            }
        }catch(Exception e)
        {

        }
    }

    private boolean appopenfirstime() {
      //  preferences = getPreferences(MODE_PRIVATE);
        preferences = getActivity().getPreferences(context.MODE_PRIVATE);
        boolean app1time = preferences.getBoolean("Firstime", false);
        System.out.println(" app1time " +app1time);
        if (!app1time) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("Firstime", true);
            editor.apply();
            updateApp();

        }
        return app1time;
    }

    public void updateApp() {
        System.out.println(" entering updateApp() ");

        if (status1 != null && status1.equals("0")) { }
        else if (status1 != null && status1.equals("1")) {
            android.app.AlertDialog.Builder updatealert = new android.app.AlertDialog.Builder(context);
            updatealert.setTitle(Html.fromHtml("<font color='#FF0000'>Update</font>"));
            updatealert.setMessage("Please update to continue mView Services ");
            updatealert.setPositiveButton(Html.fromHtml("<font color='#FF0000'>NOW</font>"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(mview));
                    startActivity(i);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                           try {
                                DB_handler dbhandler=new DB_handler(context);
                                dbhandler.open();
                                dbhandler.updateInitData("0");
                                dbhandler.close();

                            } catch (Exception e) {
                                e.printStackTrace();
                           }
                        }
                    }).run();
                    //finish();

                }
            });
            updatealert.setNegativeButton(Html.fromHtml("<font color='#FF0000'>LATER</font>"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();

        } else if (status1 != null && status1.equals("2")) {

            final android.app.AlertDialog.Builder forceupdatealert = new android.app.AlertDialog.Builder(context);
            forceupdatealert.setTitle(Html.fromHtml("<font color='#FF0000'>Update</font>"));
            forceupdatealert.setCancelable(false);
            forceupdatealert.setMessage("Please update to continue mView Services");
            forceupdatealert.setPositiveButton("<font color='#FF0000'>NOW</font>", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(mview));
                    startActivity(i);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                DB_handler dbhandler=new DB_handler(context);
                                dbhandler.open();
                                dbhandler.updateInitData("0");
                                dbhandler.close();

                           } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).run();
                  //  finish();


                }
            });

            forceupdatealert.setNegativeButton("<font color='#FF0000'>LATER</font>", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    android.os.Process
                            .killProcess(android.os.Process
                                    .myPid());
                   // finish();
                }
            }).show();

        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode,resultCode,data);
        // //toast.makeText(getActivity(), "alarm from settings", //toast.LENGTH_SHORT).show();
        // In fragment class callback
        dontRefreshScreen = false;
        mHandler.postDelayed(mRunnable, mView_HealthStatus.updateDashboardUIIntervalInSeconds*1000);
        if (requestCode == 100) {
            // Make sure the request was successful
            if (resultCode == getActivity().RESULT_OK) {
                // dontRefreshScreen = false;
                //mHandler.postDelayed(mRunnable, mView_HealthStatus.updateDashboardUIIntervalInSeconds*1000);

                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Your code to run in GUI thread here
                        updateGoogleMap();
                    }//public void run() {
                });
            }
        } /*else if (requestCode == 101) {
            if (resultCode == MainActivity.RESULT_OK) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Your code to run in GUI thread here
                        //toast.makeText(getActivity(), "alarm from settings", //toast.LENGTH_SHORT).show();
                        setupAlarm();
                    }//public void run() {
                });
            }
        }*/
    }
    @Override
    public void onPause() {
        //////toast.makeText(getActivity(), "on pause called", ////toast.LENGTH_SHORT).show();
        super.onPause();  // Always call the superclass method first
        System.out.println("callinf onpause "+dontRefreshScreen);
        // Release the Camera because we don't need it when paused
        // and other activities might need to use it.
        dontRefreshScreen = true;
        mMapView.onPause();
        Log.e("mView MainAct Frag", "onPause" );
    }

    @Override
    public void onResume() {
        //////toast.makeText(getActivity(), "on resume called", ////toast.LENGTH_SHORT).show();
        super.onResume();  // Always call the superclass method first
        // Get the Camera instance as the activity achieves full user focus
        System.out.println("on resume called" +dontRefreshScreen);
        checkForGps(getActivity());
        dontRefreshScreen = false;
        if(mHandler!=null)
            mHandler.postDelayed(mRunnable, 0);
        mMapView.onResume();
        Log.e("mView MainAct Frag", "onResume");
    }

    @Override
    public void onDestroy() {
        //////toast.makeText(getActivity(), "on destroy called...", ////toast.LENGTH_SHORT).show();
        super.onDestroy();
        if(mMapView!=null) {
            mMapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onStop() {
        //////toast.makeText(getActivity(), "on stop called", ////toast.LENGTH_SHORT).show();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        //////toast.makeText(getActivity(), "on destroy view callled", ////toast.LENGTH_SHORT).show();
        super.onDestroyView();
    }

}
