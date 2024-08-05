/*
package com.mcpsinc.mview.fragment;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.ServiceState;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mcpsinc.mview.AlarmReceiver;
import com.mcpsinc.mview.CapturedPhoneState;
import com.mcpsinc.mview.MyPhoneStateListener;
import com.mcpsinc.mview.MySpeedTest;
import com.mcpsinc.mview.R;
import com.mcpsinc.mview.SlidingTab.MyTabControl;
import com.mcpsinc.mview.TinyDB;
import com.mcpsinc.mview.activity.MainActivity;
import com.mcpsinc.mview.activity.call_sms;
import com.mcpsinc.mview.activity.mView_UploadDownloadTest;
import com.mcpsinc.mview.activity.mView_VideoTest;
import com.mcpsinc.mview.activity.mView_WebTest;
import com.mcpsinc.mview.listenService;
import com.mcpsinc.mview.mView_HealthStatus;
import com.webservice.WebService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

*/
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link home_newfragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link home_newfragment#newInstance} factory method to
 * create an instance of this fragment.
 *//*

public class home_newfragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

   // MapView mMapView;
    //public static GoogleMap googleMap;



    private OnFragmentInteractionListener mListener;

    String lat = "";
    String lon = "";

    TextView info1, info2, info3, info4;
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
    HorizontalBarChart horizontalBarChart;

    LineChart batteryLinechart;
    ImageView my_phone_detail, map_detail, call_img;
    ////
    public home_newfragment() {
        // Required empty public constructor

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        init();

        useHandler();

        MySpeedTest.urlArray = new ArrayList<String>();
        MySpeedTest.urlArray.add("http://www.google.com");
        MySpeedTest.urlArray.add("http://m.facebook.com");
        MySpeedTest.urlArray.add("http://www.twitter.com");
        MySpeedTest.urlArray.add("http://m.wikipedia.org");
        MySpeedTest.urlArray.add("http://www.yahoo.com");
        mView_HealthStatus.mySpeedTest = new MySpeedTest();

        //check if user is registered
        SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.MY_PREFS_NAME, MainActivity.context.MODE_PRIVATE);
        String restoredText = prefs.getString("userid", null);
        if (restoredText == null) {


            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    WebService.API_sendRegister( lat, lon);
                }
            }, 5000);

        }else {
            mView_HealthStatus.userid = restoredText;
            mView_HealthStatus.installedSince = prefs.getLong("installedSince", System.currentTimeMillis());

            long yourmilliseconds = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            Date resultdate = new Date(yourmilliseconds);
            String displaydate = sdf.format(resultdate);


            TinyDB tinydb = new TinyDB(MainActivity.context);
            mView_HealthStatus.timeSeriesCapturedData = new ArrayList<CapturedPhoneState.BasicPhoneState>();
            for (Object object : tinydb.getListObject("periodicData", CapturedPhoneState.BasicPhoneState.class)) {
                mView_HealthStatus.timeSeriesCapturedData.add((CapturedPhoneState.BasicPhoneState) object);
            }

            //new WebService.Async_SendPendingCallRecordsToServer().execute();

        }
        mView_HealthStatus.iTotal_Calls = prefs.getInt("totalcalls", 0);
        mView_HealthStatus.iCall_Missed = prefs.getInt("missedcalls", 0);
        mView_HealthStatus.iCall_Success = prefs.getInt("successcalls", 0);
        mView_HealthStatus.iCall_Failed = prefs.getInt("failedcalls", 0);

        mView_HealthStatus.iTotal_Calls = mView_HealthStatus.iCall_Missed + mView_HealthStatus.iCall_Success + mView_HealthStatus.iCall_Failed;

        setupAlarm();
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
        super.onDetach();
        mListener = null;
    }

    */
/**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     *//*

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    */
/**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment home_newfragment.
     *//*

    // TODO: Rename and change types and number of parameters
    public static home_newfragment newInstance(String param1, String param2) {
        home_newfragment fragment = new home_newfragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home_newfragment, container, false);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.menu);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getActivity().onBackPressed();
                DrawerLayout drawer = getActivity().findViewById(R.id.drawer_layout);
                drawer.openDrawer(Gravity.START);
            }
        });

        // Inflate the layout for this fragment

        info1 = (TextView) v.findViewById(R.id.info1);
        info2 = (TextView) v.findViewById(R.id.info2);
        info3 = (TextView) v.findViewById(R.id.info3);
        info4 = (TextView) v.findViewById(R.id.info4);

        TextView my_phone = (TextView) v.findViewById(R.id.my_phone);
        AssetManager m = getActivity().getAssets();
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Medium.ttf");
        my_phone.setTypeface(typeface);

        Typeface typeface1 = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Regular.ttf");
        info1.setTypeface(typeface1);
        info2.setTypeface(typeface1);
        info3.setTypeface(typeface1);
        info4.setTypeface(typeface1);

//        Total_Calls = (TextView) v.findViewById(R.id.Total_Calls);
//        Call_Success = (TextView) v.findViewById(R.id.Call_Success);
//        Call_Failed = (TextView) v.findViewById(R.id.Call_Failed);
//        Call_Missed = (TextView) v.findViewById(R.id.Call_Missed);

//        mobile = (TextView) v.findViewById(R.id.mobile);
//        wifi = (TextView) v.findViewById(R.id.wifi);

        btn_upload = (Button) v.findViewById(R.id.btn_upload);
        btn_video = (Button) v.findViewById(R.id.btn_video);
        btn_web = (Button) v.findViewById(R.id.btn_web);

        call_img = (ImageView) v.findViewById(R.id.call_img);

        my_phone_detail = (ImageView) v.findViewById(R.id.my_phone_detail);
        map_detail = (ImageView) v.findViewById(R.id.map_detail);
        horizontalBarChart = (HorizontalBarChart) v.findViewById(R.id.barchart);

        childlayout = (TableLayout) v.findViewById(R.id.child_layout);
        pieChart = (PieChart) v.findViewById(R.id.chart);

        entries = new ArrayList<>();
        entries.add(new Entry(0, 0));
        entries.add(new Entry(0, 1));
        entries.add(new Entry(0, 2));
        // entries.add(new Entry(12f, 3));
        //  entries.add(new Entry(18f, 4));
        //  entries.add(new Entry(9f, 5));

        dataset = new PieDataSet(entries, "# of Calls");

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("Success");
        labels.add("Failed");
        labels.add("Missed");
        // labels.add("Apr");
        // labels.add("May");
        // labels.add("June");

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

        //for network coverage
        */
/*childlayout_nc = (TableLayout) v.findViewById(R.id.child_layout_nc);
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
        //pieChart_nc.setElevation(2.0f);

        pieChart_nc.setCenterTextSize(30);
        pieChart_nc.getLegend().setEnabled(false);
        setCustomLegendForMyCoverage();
*//*

        //////////////////////////////////////////////////
        ///////////////My Data Horizontal Bar Chart///////////////////
        //refreshDataUsageChart();

        ////////////////

        batteryLinechart = (LineChart) v.findViewById(R.id.batteryLinechart);

       */
/* mMapView = (MapView) v.findViewById(R.id.mapView);
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
        });*//*

        //createbatteryChart();
        setClickListeners();

        return v;
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
                aa = mView_HealthStatus.iCall_Success;
            else if( i == 1)
                aa = mView_HealthStatus.iCall_Failed;
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
        });

        btn_video.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                dontRefreshScreen = true;
                //Log.v("mView", " click");
                dontRefreshScreen = true;
                //Log.v("mView", " click");
                Intent intent3 = new Intent(getActivity(), mView_VideoTest.class);
                startActivityForResult(intent3,100);
            }
        });

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
            }
            return null;
        }

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

    void updateUI()
    {
//        Runtime r = Runtime.getRuntime();
//
//        try {
//            Process process = Runtime.getRuntime().exec("su");
//            DataOutputStream os = new DataOutputStream(
//                    process.getOutputStream());
//            os.writeBytes("echo -e \"AT\\r\" > /dev/smd0\n");
//            os.flush();
//            os.writeBytes("exit\n");
//            os.flush();
//        }catch(IOException e)
//        {
//            e.printStackTrace();
//        }

        info1.setText(Build.MODEL + " (" + Build.BRAND + " - " + Build.PRODUCT + " - " + android.os.Build.VERSION.SDK_INT + ")");

        String state = "";
        if(MyPhoneStateListener.lastServiceState != null)
        {
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

        if(MyPhoneStateListener.lastSignalStrength != null) {
            state = state + " - " + MyPhoneStateListener.getSignalStrength(MyPhoneStateListener.lastSignalStrength);
        }

        String roaming = "";

        if(MyPhoneStateListener.lastServiceState != null)
        {
            mView_HealthStatus.roaming = MyPhoneStateListener.lastServiceState.getRoaming();
            if( mView_HealthStatus.roaming )
                roaming = " - In Roaming";
        }

        //phonetype was showing GSM for an LTE phone and sim on Amit's phone. so not showing it now
        //info2.setText(mView_HealthStatus.OperatorName + " - " + mView_HealthStatus.phonetype + " - " + state + roaming);
        info2.setText(mView_HealthStatus.OperatorName  + " - " + state + roaming);

        String thirdLine = "";
        if(MyPhoneStateListener.lastCellLocation != null)
            thirdLine = MyPhoneStateListener.lastCellLocation.toString();

        info3.setText("Cell : " + thirdLine + " " + mView_HealthStatus.strCurrentNetworkState);
        batlevl = getBatteryLevel();

        info4.setText( chkDataConnectivity() + "Battery : " + String.format("%.0f", batlevl) + "%");

        //// My Calls ///////////////////////
//        Total_Calls.setText("Total Calls " + iTotal_Calls);
//        Call_Success.setText("Success " + iCall_Success);
//        Call_Failed.setText("Failed " + iCall_Failed);
//        Call_Missed.setText("Missed " + iCall_Missed);

        refreshCallStatusChart();
       // refreshMyCoverageChart();
        refreshDataUsageChart();
       // refreshBatteryChart();

//        wifi.setText("WiFi - " + String.format("%.2f", wifiDataInMB) + " MB");
//        mobile.setText("Mobile - " + String.format("%.2f", mobileDataInMB) + " MB");

    }//end updateUI function


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
                Date resultdate = new Date(mView_HealthStatus.timeSeriesCapturedData.get(i).captureTime);
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

       */
/* LineDataSet dataset = new LineDataSet(entries, "battery level %");

        LineData data = new LineData(labels, dataset);
        batteryLinechart.setData(data);
        XAxis xAxis = batteryLinechart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        batteryLinechart.setDescription("");
        dataset.setDrawFilled(true);
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
        dataset.setFillColor(Color.MAGENTA);
        batteryLinechart.invalidate();*//*

    }
    public void refreshCallStatusChart()
    {
        valueTxt_MyCalls[0].setText(mView_HealthStatus.iCall_Success+"");
        valueTxt_MyCalls[1].setText(mView_HealthStatus.iCall_Failed + "");
        valueTxt_MyCalls[2].setText(mView_HealthStatus.iCall_Missed + "");
        pieChart.setCenterText(mView_HealthStatus.iTotal_Calls + "");

        entries = new ArrayList<>();
        entries.add(new Entry(mView_HealthStatus.iCall_Success, 0));
        entries.add(new Entry(mView_HealthStatus.iCall_Failed, 1));
        entries.add(new Entry(mView_HealthStatus.iCall_Missed, 2));

        dataset = new PieDataSet(entries, "# of Calls");

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("Success");
        labels.add("Failed");
        labels.add("Missed");

        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
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
            if( mView_HealthStatus.per4g> 100)
                mView_HealthStatus.per4g = 100;
        }else if( mView_HealthStatus.iCurrentNetworkState == 3)
        {
            mView_HealthStatus.per3g = ( ((mView_HealthStatus.timein3G + elapsedTime)/(1000*60.0f) )*100) / (totalTimeInMinutes*1.0f);
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

      */
/*  valueTxt_MyCoverage[0].setText(String.format("%.2f",mView_HealthStatus.per4g)+"%");
        valueTxt_MyCoverage[1].setText(String.format("%.2f",mView_HealthStatus.per3g)+"%");
        valueTxt_MyCoverage[2].setText( String.format("%.2f",mView_HealthStatus.per2g)+"%");
        valueTxt_MyCoverage[3].setText(String.format("%.2f",mView_HealthStatus.perNS)+"%");

        entries_nc = new ArrayList<>();
        entries_nc.add(new Entry(mView_HealthStatus.per4g, 0));
        entries_nc.add(new Entry(mView_HealthStatus.per3g, 1));
        entries_nc.add(new Entry(mView_HealthStatus.per2g, 2));
        entries_nc.add(new Entry(mView_HealthStatus.perNS, 3));

        dataset_nc = new PieDataSet(entries_nc, "# of Calls");

        ArrayList<String> labels1 = new ArrayList<String>();
        labels1.add("4G");
        labels1.add("3G");
        labels1.add("2G");
        labels1.add("NS");

        dataset_nc.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data_nc = new PieData(labels1, dataset_nc);
        pieChart_nc.setData(data_nc);
        pieChart_nc.invalidate();
*//*
    }//end refreshMyCoverageChart

    public void refreshDataUsageChart()
    {
        long currentMobileTxBytes = TrafficStats.getMobileTxBytes();
        long currentMobileRxBytes = TrafficStats.getMobileRxBytes();
        long totalTxBytes = TrafficStats.getTotalTxBytes();
        long totalRxBytes = TrafficStats.getTotalRxBytes();
        float l = currentMobileTxBytes + currentMobileRxBytes;
        float mobileDataInMB = (l)/(1024*1024);
        float wifiDataInMB = ((totalTxBytes + totalRxBytes)/(1024*1024)) - mobileDataInMB;

        //bug reported
        if(wifiDataInMB < 0 )
            wifiDataInMB = 0;

        float total = wifiDataInMB + mobileDataInMB;
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(wifiDataInMB, 0));
        entries.add(new BarEntry(mobileDataInMB, 1));
        entries.add(new BarEntry(total, 2));

        BarDataSet dataset = new BarDataSet(entries, "My Data Usage(MB)");
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
        ArrayList<String> labels = new ArrayList<String> ();
        labels.add("WiFi");
        labels.add("Mobile");
        labels.add("Total");

        BarData data = new BarData(labels, dataset);
        horizontalBarChart.setData(data);

        horizontalBarChart.setDescription("");

        horizontalBarChart.invalidate();
        horizontalBarChart.setClickable(false);
    }//refreshDataUsageChart

    //public static GPSTracker gps;
    void init()
    {

        if (listenService.gps != null && listenService.gps.canGetLocation()) {

            lat = listenService.gps.getLatitude() + "";
            lon = listenService.gps.getLongitude() + "";

            if (lat.equals("0.0") || lon.equals("0.0"))
            {
                Toast.makeText(this.getActivity(), "Turn On GPS,to get Latitude and Longitude Values.", Toast.LENGTH_LONG).show();
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
        //    return;

        AlarmManager alarmMgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        intent.putExtra("alarm",1);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0,
                intent, 0);

//        Calendar time = Calendar.getInstance();
//        time.setTimeInMillis(System.currentTimeMillis());
//        // Set Alarm for next 10 seconds
//        time.add(Calendar.SECOND, 30);

        Calendar updateTime = Calendar.getInstance();

        updateTime.set(Calendar.SECOND, mView_HealthStatus.periodicRefreshFrequencyInSeconds);

        alarmMgr.cancel(pendingIntent);

        if(mView_HealthStatus.startbackgroundservice)
            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(), 1000 * mView_HealthStatus.periodicRefreshFrequencyInSeconds, pendingIntent);

        //alarmMgr.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), pendingIntent);
    }

    String chkDataConnectivity() {
        String s = "";
        final ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isConnectedOrConnecting ()) {
            s = "WiFi";
            WifiManager wm = (WifiManager)getActivity().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wm.getConnectionInfo();
            int linkSpeed = wifiInfo.getLinkSpeed();
            String ss = wifiInfo.getSSID();
            int rs = wifiInfo.getRssi();
            s = "WiFi (Link " + linkSpeed + "Mbps, " + ss + ", " + rs +"dBm)\n";
            mView_HealthStatus.connectionType = "WiFi";
            mView_HealthStatus.connectionTypeIdentifier = 1;
        } else if (mobile.isConnectedOrConnecting ()) {

            s = "Mobile data"; //getNetworkTypeString (listenService.telMgr.getNetworkType());
            s += "\n";
            mView_HealthStatus.connectionType = "Mobile data";
            mView_HealthStatus.connectionTypeIdentifier = 2;
        } else {
            s = "Offline";
            mView_HealthStatus.connectionTypeIdentifier = 0;
            //Toast.makeText(this, "No Network ", Toast.LENGTH_LONG).show();
        }
        return s;
    }

    public float getBatteryLevel() {
        Intent batteryIntent = getActivity().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if(level == -1 || scale == -1) {
            return 50.0f;
        }

        float bat = ((float)level / (float)scale) * 100.0f;
        mView_HealthStatus.batteryLevel = bat + "";
        return bat;
    }

*/
/*
    public void updateGoogleMap()
    {
        try {
            if (ContextCompat.checkSelfPermission(MainActivity.context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
*//*

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                        //updateGoogleMap();
                    }//public void run() {
                });
            }
        }else if (requestCode == 101) {
            if (resultCode == getActivity().RESULT_OK) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Your code to run in GUI thread here
                        setupAlarm();
                    }//public void run() {
                });
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first

        // Release the Camera because we don't need it when paused
        // and other activities might need to use it.
        dontRefreshScreen = true;
   //     mMapView.onPause();
        Log.e("mView MainAct Frag", "onPause" );
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        // Get the Camera instance as the activity achieves full user focus
        dontRefreshScreen = false;
        mHandler.postDelayed(mRunnable, mView_HealthStatus.updateDashboardUIIntervalInSeconds*1000);
       // mMapView.onResume();
        Log.e("mView MainAct Frag", "onResume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       // mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
      //  mMapView.onLowMemory();
    }

}
*/
