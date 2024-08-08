
package com.newmview.wifi.activity;
import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.role.RoleManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dashboard.activity.GraphDetailsActivity;
import com.dashboard.viewmodel.DashboardViewModel;
import com.newmview.wifi.PhoneCallHelper;
import com.newmview.wifi.customdialog.DialogPopup;
import com.newmview.wifi.fragment.ChceckingNewSimConditions;
import com.newmview.wifi.fragment.NewVideoFragmentIframe;
import com.newmview.wifi.fragment.SmsFragment;
import com.newmview.wifi.fragment.SpeeDTestFragmentNew;
import com.newmview.wifi.fragment.TraceroutFragment;
import com.newmview.wifi.fragment.USSDTEST;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.dialoglib.DialogView;


import com.functionapps.mview_sdk2.main.Mview;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.mview.airtel.R;
import com.newmview.wifi.TinyDB;
import com.newmview.wifi.adapter.MyExpandableAdapter;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.bean.Db_Bean;
import com.newmview.wifi.database.DB_handler;

import com.newmview.wifi.fragment.Call_msg_fragment_New;
import com.newmview.wifi.fragment.ChartsFragment;
import com.newmview.wifi.fragment.Dwnld_upload_fragment;
import com.newmview.wifi.fragment.FiveGParametersFragment;
import com.newmview.wifi.fragment.HomeFragment;
import com.newmview.wifi.fragment.MoviesFragment;
import com.newmview.wifi.fragment.NotificationsFragment;
import com.newmview.wifi.fragment.PhotosFragment;
import com.newmview.wifi.fragment.SettingsFragment;
import com.newmview.wifi.fragment.SpeedTestFragment;
import com.newmview.wifi.fragment.SpeedTestOptions;
import com.newmview.wifi.fragment.VideoSpeedtest_fragment;
import com.newmview.wifi.fragment.WebTest;
import com.newmview.wifi.fragment.Webtest_fragment;
import com.newmview.wifi.helper.AsyncTasks_APIgetData;
import com.newmview.wifi.helper.CommonUtil;
import com.newmview.wifi.helper.DialogClass;
import com.newmview.wifi.helper.RequestResponse;
import com.newmview.wifi.interfaces.AsynctaskListener;
import com.newmview.wifi.listenService;
import com.newmview.wifi.mView_HealthStatus;
import com.newmview.wifi.other.CommonAlertDialog;
import com.newmview.wifi.other.Config;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;
import com.services.MyJobService;
import com.visionairtel.drivetest.activity.HomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static com.newmview.wifi.application.MviewApplication.batteryList;
import static com.newmview.wifi.application.MviewApplication.timeStamp;

        public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, AsynctaskListener {
    private static final int SERVICE_PERMISSIONS_REQUEST = 2;
    private static final int READ_PERMISSIONS_REQ_CODE =77 ;
    private static final int REPORT_ISSUE =3 ;
    public static  boolean RESULT_FLAG = false;
    public  static boolean testflag=false;
    private DB_handler db_handler;
    private ArrayList<String> reportList;
    private SharedPreferences pref_n;
    private SharedPreferences.Editor editor_n;
    private float uploadfiletime1,uploadfiletimeNew;
    private String str;
      private double roundoff=0;
    private String finalspeed;
    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;
    private String sdkFlag;
    private DashboardViewModel viewModel;

            @Override
    public void finish() {
        super.finish();
        MainActivity.SEND_API_REQUEST=false;
        clearvalues();
    }


    public static  boolean REPORT_FLAG =false ;// flag to denote send request only when reported an issue
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    public static boolean SEND_API_REQUEST=false;

    public static Context context;
    // urls to load navigation header background image
    // and profile image
    private static final String urlNavHeaderBg = "http://api.androidhive.info/images/nav-menu-header-bg.jpg";
    private static final String urlProfileImg = "logo.jpg"; //https://lh3.googleusercontent.com/eCtE_G34M9ygdkmOpYvCag1vBARCmZwnVS6rS5t4JLzJ6QgQSBquM0nuTsCpLhYbKljoyS-txg";
    // index to identify current nav menu item
    public static int navItemIndex = 0;
    public static String MY_PREFS_NAME = "mView";
    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_PHOTOS = "photos";
    private static final String TAG_MOVIES = "movies";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_HOME;
    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    int MY_PERMISSIONS_REQUEST = 1;
    public static String ud;
    private BottomNavigationView bottomNavigationView;
    private Call_msg_fragment_New callmsgfragment;
    private FragmentManager fragmentManager;
    private SpeedTestOptions speedtestoptions;
    private Dwnld_upload_fragment dwnld_upload_fragment;
    private int msg;
    private String msgshow;
    private Webtest_fragment webtest_fragment;
    public static String HOME_TAG="home";
    SharedPreferences.Editor editor;
    SharedPreferences sp;
    private ExpandableListView expandablelistview;
    private MyExpandableAdapter adapter;
    private ArrayList<String> headersList=new ArrayList<>();
    private ArrayList<Db_Bean> subCatList=new ArrayList<>();

    private ArrayList<HashMap<String,String>> graphList=new ArrayList<>();
    private int position;
    private ArrayList<ArrayList<HashMap<String, String>>> mappedgraphList=new ArrayList<>();
    public static ArrayList<String> issuesList=new ArrayList<>();
    private final String TAG="MainActivity";


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"On Start called..");
       //Toast.makeText(MainActivity.this, "On Start called..",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"On Create called..");
       /* Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();*/
      /*  Utils.showLongToast(MainActivity.this, "action is  " + action+" type is "+type);

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                Utils.showLongToast(MainActivity.this, "Text is " + intent.getStringExtra("name"));

         //       handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
          //      handleSendImage(intent); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
       //         handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        } else {
            // Handle other intents, such as being started from the home screen
        }*/
        /*if(intent!=null) {
            Utils.showLongToast(MainActivity.this, "Text is " + intent.getStringExtra("name"));
        }*/

/*
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
               Utils.showLongToast(MainActivity.this,"Text is "+intent.getStringExtra("name"));
            }
        }
*/
        setContentView(R.layout.mainactivity_new);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
       // Toast.makeText(MainActivity.this, "on create of activty called",Toast.LENGTH_SHORT).show();
        //        registerReceiver(new NetworkChangeReceiver(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        setSupportActionBar(toolbar);
        context = this;
        mHandler = new Handler();
        editor =getSharedPreferences(MainActivity.MY_PREFS_NAME, MainActivity.context.MODE_PRIVATE).edit();
        editor_n=getSharedPreferences(Constants.service_toggle, MainActivity.context.MODE_PRIVATE).edit();
//        mView_HealthStatus.circle_name = getClusterName(MviewApplication.ctx);
        clearvalues();
        seekReqPermissions();
        checkDefaultDialer();
        askpermissionsBackground();
        init();
        setValuesToSideDrawer();
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        loadNavHeader();
//        sendInitRequest();
        //startListenService();
        TinyDB db = new TinyDB(MainActivity.this);
        String t1 = db.getString("sendstateinterval");
        if( t1 == null || t1.equals("")) {
            db.putString("sendstateinterval", "300");
        }else {
            mView_HealthStatus.periodicRefreshFrequencyInSeconds = Integer.parseInt(t1);
        }

        t1 = db.getString("dashboardupdate");
        if( t1 == null || t1.equals("")) {
            db.putString("dashboardupdate", "5");
        }else
            mView_HealthStatus.updateDashboardUIIntervalInSeconds = Integer.parseInt(t1);

        t1 = db.getString("youtube");
        if( t1 == null || t1.equals("")) {
           // db.putString("youtube", "https://www.youtube.com/watch?v=Sg64rEtDd4s");
            db.putString("youtube",  "www.youtube.com/embed/HngTaeW9KVs");

        }else
            mView_HealthStatus.youtubeurl = t1;

        t1 = db.getString("savecalllog");
        if( t1 == null || t1.equals("")) {
            db.putString("savecalllog","ON");
        }else {
            if (t1.equals("On") || t1.equals("ON")) {
                mView_HealthStatus.writeCallLogs = true;
            } else
                mView_HealthStatus.writeCallLogs = false;
        }

        t1 = db.getString("startbackgroundservice");
        ////Toast.makeText(this, "backgrnd service in main activity  "+t1,////Toast.LENGTH_SHORT).show();

        if( t1 == null || t1.equals("")) {


            db.putString("startbackgroundservice","ON");
        }else {
            if (t1.equals("On") || t1.equals("ON")) {
                mView_HealthStatus.startbackgroundservice = true;
            } else
                mView_HealthStatus.startbackgroundservice = false;
        }

        t1 = db.getString("mingsmsignalforcalldrop");
        if( t1 == null || t1.equals("")) {
            db.putString("mingsmsignalforcalldrop","12");
        }else {
            mView_HealthStatus.MIN_GSM_SIGNAL_STRENGTH_FOR_CALL_DROP = Integer.parseInt(t1);;
        }

        if (savedInstanceState == null) {
            //Toast.makeText(MainActivity.this, "savedinstance is null",//Toast.LENGTH_SHORT).show();
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
            //setupAlarm();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestLocationAndPhonePermissions(MY_PERMISSIONS_REQUEST);
        } else {
            startListenService();
            Log.i(TAG, "start listen service is");
            Constants.IMSI = Utils.getImsi(context);
            System.out.println("imsi from main class 2" + Constants.IMSI);
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


    private void requestLocationAndPhonePermissions(int requestCode) {
        String[] permissions = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CALL_PHONE
        };

        ActivityCompat.requestPermissions(this, permissions, requestCode);
    }

    private void stopAndRestartService() {
        Intent serviceIntent = new Intent(this, listenService.class);
        this.stopService(serviceIntent);
        Intent serviceIntent1 = new Intent(this, listenService.class);
        this.startService(serviceIntent1);
    }

    private void requestLocationPermissions(int issue) {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
        },issue);
    }


    private void setValuesToSideDrawer() {

        {

         //   Toast.makeText(context, "set values function callled   ", Toast.LENGTH_SHORT).show();
               headersList=new ArrayList<>();
         //   headersList.add(0,"Dashboards");
            headersList.add(0,Config.RunDiagnostics);
            final ArrayList<Db_Bean> dashboardsList=new ArrayList<>();

         /*   final ArrayList<Db_Bean> dashboardsList=new ArrayList<>();
            db_handler.open();
            Cursor cursor= db_handler.selectDashboardsData();
            if(cursor.getCount()>0)
            {
                while (cursor.moveToNext())
                {
                    Db_Bean db_bean=new Db_Bean();
                    String dbId= cursor.getString(cursor.getColumnIndex("dbId"));
                    String dbName=cursor.getString(cursor.getColumnIndex("dbName"));
                    db_bean.setDbId(dbId);
                    db_bean.setDbName(dbName);
                    System.out.println("dashboards "+db_bean.getDbId() +"  "+db_bean.getDbName());
                    dashboardsList.add(db_bean);


                }
            }
            cursor.close();
            db_handler.close();*/

            reportList=new ArrayList<>();
        //   reportList.add("Network coverage");
            // reportList.add("Data Bandwidth Issue");
           // reportList.add("Video Test");
      //      reportList.add("Call Issue");
         //   reportList.add("Call Test");
            reportList.add("Ping Test");
            //reportList.add("Trace Route Test ");
//            reportList.add(Config.wifiMapperName);
//            reportList.add("Surveys List");
            reportList.add("Sim PARAMS");
//            reportList.add("Sim 2 PARAMS");

            reportList.add("Checking sim 1 and 2");
//            reportList.add("Download Test");
//            reportList.add("Upload Test");
            reportList.add("Call Test");
//            reportList.add("New Network Parameters ");
            reportList.add("Web Test");
//            reportList.add("New Video Test");
//            reportList.add("Performance Test");
            reportList.add("USSD Test");
            reportList.add("Video Test");
            reportList.add("Speed Test");
            reportList.add("SMS Test");
//            reportList.add("Speed Test(sftp)");

           // reportList.add("FAST NET");
           // reportList.add("App Open");
            reportList.add("Manually Status Change");
            reportList.add("Manually Send evt");

            reportList.add("Manually Start All the Tests");
            reportList.add("Drive Test");

            System.out.println("Common util mapped list size "+CommonUtil.mappgraphlist.size());
            System.out.println("graphs mapped list "+mappedgraphList);
            System.out.println("dashboards all list"+headersList +" "+ dashboardsList +" "+reportList);

            adapter = new MyExpandableAdapter(context, headersList, dashboardsList,reportList);
            expandablelistview.setAdapter(adapter);
            //  expandablelistview.expandGroup(position);


            expandablelistview.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                    if(expandablelistview.isGroupExpanded(groupPosition))
                    {
                        if((groupPosition==0)&&( dashboardsList.size()==0))
                        {
                            Utils.showToast(context,"No dashboards available!!");

                        }
                        expandablelistview.collapseGroup(groupPosition);
                    /*expandablelistview.collapseGroup(groupPosition);
                    position=groupPosition;*/
                    }
                    else
                    {
                        expandablelistview.expandGroup(groupPosition);
                    }
                    return  true;
                }
            });

        }

    }

            private void checkDefaultDialer() {

                RoleManager roleManager = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    roleManager = (RoleManager) getSystemService(ROLE_SERVICE);

                    Intent intent = null;
                    intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER);

                    startActivityForResult(intent, 10);
                }
            }

    private void init() {
        Utils.appendLog("ELOG_APP_OPEN:");
       pref_n = getApplicationContext().getSharedPreferences(Constants.service_toggle, 0); // 0 - for private mode
        editor_n.putString("background_service","0");
        editor_n.putString("upload_service","0");
        editor_n.putString("upload_service","0");


        SharedPreferences prefs = MviewApplication.ctx.getSharedPreferences(Constants.service_toggle, MODE_PRIVATE);
        if(prefs!=null) {
            boolean service_tg = prefs.getBoolean("service_key", false);
            System.out.println("service key is "+service_tg);
            if (service_tg) {
                //seekReqPermissions();
                startAllServices();
            } else {
                stopTheServices();
            }
        }
        System.out.println("init......");
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtWebsite = (TextView) navHeader.findViewById(R.id.website);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);
        bottomNavigationView=(BottomNavigationView)findViewById(R.id.bottom_navigation);
//        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        /*CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new ScrollHandler());*/
        bottomNavigationView.setBackground(getResources().getDrawable(R.color.primary));
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        fragmentManager=getSupportFragmentManager();
        db_handler=new DB_handler(this);
        expandablelistview=(ExpandableListView)findViewById(R.id.expanded_menu);


        Intent intent = new Intent();
        String packageName = getPackageName();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            MyJobService.scheduleSendingtoserver();
        }
        //send init request
        RequestResponse.sendInitRequest();
        Utils.getNetworkInfo();
//        Utils.apn();

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {

            private float last = 0;
            @Override
            public void onDrawerSlide(View arg0, float arg1 ) {
                int i=0;

                boolean opening = arg1 > last;
                boolean closing = arg1 < last;

                if (opening) {
                    i++;

                    Log.i("Drawer", "opening");
                    bottomNavigationView.setVisibility(View.GONE);
/*
                    if((issuesList.size()==0 )||(subCatList.size()==0) )
                    {
                        if(homeFragment!=null) {
                            System.out.println("calling method times "+ i);
                             homeFragment.sendGetDashboardDataRequest();
                        }
                        else {
                            homeFragment = new HomeFragment();
                              homeFragment.sendGetDashboardDataRequest();
                        }

                    }
*/


                } else if (closing) {
                    Log.i("Drawer", "closing");
                    bottomNavigationView.setVisibility(View.VISIBLE);

                } else {
                    Log.i("Drawer", "doing nothing");
                }

                last = arg1;

            }

            @Override
            public void onDrawerOpened(View drawerView) {




/*
if(expandablelistview!=null) {
    if (expandablelistview.isGroupExpanded(position)) {
        if (CommonUtil.newDashboardList != null && CommonUtil.newDashboardList.size() == 0) {
            Utils.showToast(MainActivity.this, "No dashboards available!!");
        }
    }
}

*/

System.out.println("issueslist "+issuesList +"subcat list "+subCatList);
                if((issuesList.size()==0 )||(subCatList.size()==0) )
                {
//                    if(homeFragment!=null) {
//                        System.out.println("calling method times ");
//                        homeFragment.sendGetDashboardDataRequest();
//                    }
//                    else {
//                        homeFragment = new HomeFragment();
//                        homeFragment.sendGetDashboardDataRequest();
//                    }

                }


            }

            @Override
            public void onDrawerClosed(View drawerView) {
                bottomNavigationView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDrawerStateChanged(int newState) {



            }
        });

    }


    public void setupAlarm() {
        SharedPreferences prefs = MviewApplication.ctx.getSharedPreferences(Constants.service_toggle, MODE_PRIVATE);
        if(prefs!=null) {
            boolean service_tg = prefs.getBoolean("service_key", false);
            System.out.println("service key is " + service_tg);
            if (!service_tg) {
                if (!Utils.isMyServiceRunning(Background_service.class)) {
                   /* Intent bck = new Intent(MainActivity.this, Background_service.class);
                    startService(bck);*/
                }
            }
        }
        else
        {
            /*Intent bck = new Intent(MainActivity.this, Background_service.class);
            startService(bck);*/
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        expandablelistview.setIndicatorBounds(expandablelistview.getRight()- 50, expandablelistview.getWidth());
    }

    @Override
    public void onPause() {
        //Toast.makeText(MainActivity.this, "on pause of activity",Toast.LENGTH_SHORT).show();
        super.onPause();  // Always call the superclass method first
        // clearvalues();

        boolean b = getSupportFragmentManager().isDestroyed();   //(yourfragment).commit()
        Log.d( TAG,"onPause " + b + "");
        // Release the Camera because we don't need it when paused
        // and other activities might need to use it.
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
      //  Toast.makeText(MainActivity.this, "on resume called",Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onResume");
        // Get the Camera instance as the activity achieves full user focus

    }

    @Override
    protected void onRestart() {
        super.onRestart();
  //      Toast.makeText(MainActivity.this, "on restart called",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    //    Toast.makeText(MainActivity.this, "on destroy of activity",Toast.LENGTH_SHORT).show();
        //clearvalues();
        editor.remove("opened").commit();
        editor.remove("userid").commit();
        SharedPreferences prefs = getSharedPreferences(MainActivity.MY_PREFS_NAME, context.MODE_PRIVATE);
        String restoredText = prefs.getString("userid", null);
        MainActivity.SEND_API_REQUEST=false;
        Intent intent=new Intent(MviewApplication.ctx, com.newmview.wifi.application.MviewService.class);
        stopService(intent);


        Log.d(TAG,"onDestroy");

    }

    private void clearvalues() {
        editor.remove("opened");
        editor.apply();
    }



    @Override
    protected void onStop() {
        super.onStop();
        Log.d( TAG,"onStop");
        System.out.println("on stop of main activity");
      //  Toast.makeText(MainActivity.this, "on stop of activity",Toast.LENGTH_SHORT).show();
        // clearvalues();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch (requestCode) {
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    int d =0;
                    d++;

                   /* if(Utils.isMyServiceRunning(listenService.class))
                    {
                       stopAndRestartService();
                    }
                    else {
                        startListenService();
                    }*/


                    Constants.IMSI=Utils.getImsi(context);
                    System.out.println("imsi from main class 1"+Constants.IMSI);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
                case SERVICE_PERMISSIONS_REQUEST:
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        {


                            editor_n.putBoolean("service_key", true);
                            editor_n.putBoolean("listen_service",true);
                            editor_n.apply();
                            Utils.openAutoStartPage(MainActivity.this);
                            startAllServices();

                        }
                    break;

            case REPORT_ISSUE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    showAlert();
                }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void startListenService() {

        Intent serviceIntent = new Intent(this, listenService.class);
        this.startService(serviceIntent);
        batteryList.add(Float.valueOf(Utils.getBattery(context)));
        timeStamp.add(Utils.getCurrentHourMin());
    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {
        // name, website
        String version = "1.0";
        try {
            PackageInfo pInfo = MainActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
            int verCode = pInfo.versionCode;

        }catch(Exception e)
        {

        }
        txtName.setText(getString(R.string.app_name)+" (" + Constants.getversionnumber(MainActivity.context) + ")");

        // txtWebsite.setText("www.mcpsinc.com");

        // loading header background image
        Glide.with(this).load(urlNavHeaderBg)
             //   .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);
      /*  Glide.with(this).load(R.drawable.pviewicon)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);*/

        // Loading profile image

       /* Glide.with(this).load(R.drawable.pviewicon)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);*/

        // showing dot next to notifications label
        //navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    public void getDashboards()
    {

        if(CommonUtil.headersList!=null && CommonUtil.headersList.size()>0) {
            // headersList.clear();


            subCatList.clear();
            mappedgraphList.clear();

        }
       /* headersList.add(0,"Dashboards");
        headersList.add(1,"Report Problem");*/

        for(int i=0;i<CommonUtil.newDashboardList.size();i++)
        {
            Db_Bean db_bean=new Db_Bean();
            String dbName=CommonUtil.newDashboardList.get(i).get("dbName");
            String dbId=CommonUtil.newDashboardList.get(i).get("dbId");
            db_bean.setDbId(dbId);
            db_bean.setDbName(dbName);
            subCatList.add(db_bean);
        }
        issuesList.clear();
        issuesList.add("Network coverage Issue");
     //   issuesList.add("Video Test");
        issuesList.add("Call Test");
        issuesList.add("Upload Test");
        issuesList.add("Downlaod Test");

        System.out.println("subcat list is "+subCatList);
        System.out.println("issues list "+issuesList +"headers list "+CommonUtil.headersList);
        if(CommonUtil.mappgraphlist!=null && CommonUtil.mappgraphlist.size()>0)
            mappedgraphList.addAll(CommonUtil.mappgraphlist);
        adapter = new MyExpandableAdapter(context, CommonUtil.headersList, subCatList,issuesList);
        expandablelistview.setAdapter(adapter);
        //  expandablelistview.expandGroup(position);



        expandablelistview.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if(expandablelistview.isGroupExpanded(groupPosition))
                {
                    if((groupPosition==0)&&( subCatList.size()==0))
                    {
                        Utils.showToast(context,"No dashboards available!!");

                    }
                    expandablelistview.collapseGroup(groupPosition);
                    /*expandablelistview.collapseGroup(groupPosition);
                    position=groupPosition;*/
                }
                else
                {
                    expandablelistview.expandGroup(groupPosition);
                }
                return  true;
            }
        });

    }
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        // selectNavMenu();
        // set toolbar title
        setToolbarTitle();
        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            // show or hide the fab button
            //toggleFab();
            return;
        }
        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss ();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // show or hide the fab button
//        toggleFab();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    public void openGraphActivity(int childPosition) {

        /*   "graphId":"198","graphName":"Financial Transaction"},*/
        //  String graphId = graphList.get(childPosition).get("graphId");


        //if(graphList!=null && graphList.size()>0) {

        String graphId=null;
        String graphName=null;

        if (mappedgraphList != null && mappedgraphList.size() > 0) {

            graphId = mappedgraphList.get(childPosition).get(0).get("graphId");
            //String graphId = graphList.get(childPosition).get("graphId");
            System.out.println("mappedlist " + mappedgraphList.size());
            graphName = mappedgraphList.get(childPosition).get(0).get("graphName");


        }
        Intent intent = new Intent(MainActivity.this, GraphDetailsActivity.class);
        intent.putExtra("graphId", graphId);
        intent.putExtra("graphName", graphName);
        intent.putExtra("clickedposition", childPosition);
        System.out.println("graphid  and graph name in main " + graphId + graphName);
        startActivity(intent);
    }





    HomeFragment homeFragment;

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                ////Toast.makeText(MainActivity.this, "getting called from 1",////Toast.LENGTH_SHORT).show();
                homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                // photos
                PhotosFragment photosFragment = new PhotosFragment();
                return photosFragment;
            case 2:
                // movies fragment
                MoviesFragment moviesFragment = new MoviesFragment();
                return moviesFragment;
            case 3:
                // notifications fragment
                NotificationsFragment notificationsFragment = new NotificationsFragment();
                return notificationsFragment;

            case 4:
                // settings fragment
                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment;
            default:
                ////Toast.makeText(MainActivity.this, "getting called from 4",////Toast.LENGTH_SHORT).show();
                return new HomeFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        ////Toast.makeText(MainActivity.this, "getting called from 2",////Toast.LENGTH_SHORT).show();
                        fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in,
                                android.R.anim.fade_out);
                        fragmentManager.beginTransaction().replace(R.id.frame, new HomeFragment()).commit();

                        drawer.closeDrawers();
                        getSupportActionBar().setTitle(R.string.nav_home);
                        Config.onbackpress="back";
                       /* drawer.closeDrawers();
                        loadFragment(R.string.nav_home,new actionBarFragment());
                        Config.onbackpress="back";*/
                        break;
                    case R.id.nav_photos:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_PHOTOS;
                        drawer.closeDrawers();
                        loadFragment(R.string.webtest_title,new Webtest_fragment());
                        Config.onbackpress="home";
                        break;
                       /* Intent intent3 = new Intent(MainActivity.this, mView_WebTest.class);
                        startActivityForResult(intent3, 100);
                        break;*/
                    case R.id.nav_movies:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_MOVIES;
                        drawer.closeDrawers();
//                        loadFragment(R.string.videotest_title,new VideoSpeedtest_fragment());
                        Config.onbackpress="home";

                       /* Intent intent4 = new Intent(MainActivity.this, mView_VideoTest.class);
                        startActivityForResult(intent4,100);*/
                        break;
                    case R.id.nav_notifications:
                        ud="download";
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_NOTIFICATIONS;
                        drawer.closeDrawers();
                        loadFragment(R.string.download,new Dwnld_upload_fragment());
                        Config.onbackpress="home";
                       /* Intent intent5 = new Intent(MainActivity.this, mView_UploadDownloadTest.class);
                        startActivityForResult(intent5,100);*/
                        break;
                    case R.id.nav_settings:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_SETTINGS;
                        drawer.closeDrawers();
                        //loadFragment(R.string.main_settings_title,new MainSettingsFragment());
                        Intent intent6 = new Intent(MainActivity.this, MainSettings.class);
                        startActivityForResult(intent6,101);
                        Config.onbackpress="home";

                        break;
                    /*case R.id.nav_about_us:
                        // launch new intent instead of loading fragment

                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_privacy_policy:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
                        drawer.closeDrawers();
                        return true;*/
                    case R.id.upload:
                        //navItemIndex = 3;
                        ud="upload";
                        navItemIndex = 6;
                        CURRENT_TAG = TAG_NOTIFICATIONS;
                        drawer.closeDrawers();
                        loadFragment(R.string.upload,new Dwnld_upload_fragment());
                        Config.onbackpress="home";

                       /* Intent upload = new Intent(MainActivity.this, mView_UploadDownloadTest.class);
                        startActivityForResult(upload,100);*/
                        break;

                    default:
                        navItemIndex = 0;
                }
                menuItem.setChecked(false);
                navigationView.getMenu().getItem(0).setChecked(true);
                //Checking if the item is in checked state or not, if not make it in checked state
//                if (menuItem.isChecked()) {
//                    menuItem.setChecked(false);
//                } else {
//                    menuItem.setChecked(true);
//                }
//                menuItem.setChecked(true);
//
//                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {

                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);



            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    private void loadFragment(int title, Fragment Fragmentclass) {

        fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);

        fragmentManager.beginTransaction().replace(R.id.frame, Fragmentclass).addToBackStack("others").commit();


        drawer.closeDrawers();
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        ////Toast.makeText(MainActivity.this, "count in backstack: "+fragmentManager.getBackStackEntryCount(),////Toast.LENGTH_SHORT).show();

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
/*
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                //loadHomeFragment();
                return;
            }
        }
*/
        if(Config.onbackpress.equalsIgnoreCase("speedtest"))
        {
            if(!testflag) {

                getConfirmationFromUser();
            }
            else {

                Config.onbackpress = "home";
                toolbar.setTitle(R.string.speedtest);
                fragmentManager.popBackStack("speed_test", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                MainActivity.testflag=false;
            }

            //loadFragment(R.string.speedtest,new SpeedTestOptions());


        }
        else if(Config.onbackpress.equalsIgnoreCase("home"))
        {

            // loadFragment(R.string.nav_home,new HomeFragment());
            Config.onbackpress="back";
            MainActivity.RESULT_FLAG=false;
            MainActivity.REPORT_FLAG=false;
            toolbar.setTitle(R.string.nav_home);
            fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.slide_out_right,
                    android.R.anim.slide_in_left);
            fragmentManager.popBackStack("others", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            bottomNavigationView.setSelectedItemId(R.id.home);


            // toolbar.setNavigationIcon(R.drawable.nav);


        }
        else if(Config.onbackpress.equalsIgnoreCase("back"))
        {

            //exitDialog();
            newExitAlertDialog();


        }
        else
        {
            super.onBackPressed();
            clearvalues();
        }

    }

    private void newExitAlertDialog() {

        final DialogView dialogView=new DialogView(this);
        dialogView.setLayoutForDialog("Are you sure , you want to exit?","Ok","Cancel","Exit",R.drawable.exit_1);
        Button ok=dialogView.getDialog().findViewById(R.id.ok);
        Button cancel=dialogView.getDialog().findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogView.getDialog().dismiss();
            }
        });
ok.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        finish();
        clearvalues();
    }
});

    }

    private void getConfirmationFromUser() {
        final AlertDialog.Builder alertdialog=new AlertDialog.Builder(context,R.style.AlertDialogTheme);
        alertdialog.setMessage(Constants.USER_CONFIRMATION).
                setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Config.onbackpress="home";
                        toolbar.setTitle(R.string.speedtest);
                        fragmentManager.popBackStack("speed_test", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        for(int entry = 0; entry < fragmentManager.getBackStackEntryCount(); entry++){
                            Log.i("FRAGMENT", "Found fragment: " + fragmentManager.getBackStackEntryAt(entry).getId() +fragmentManager.getFragments());
                        }


                    }
                })
                .setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();


    }

    private void exitDialog() {

/*

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        dialog.setContentView(R.layout.dialog_view);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        Button okbutton = dialog.findViewById(R.id.ok);
        Button cancelbutton = dialog.findViewById(R.id.cancel);
        okbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
         finish();
            }
        });
        cancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
dialog.dismiss();
            }
        });
*/

        final AlertDialog.Builder alertdialog=new AlertDialog.Builder(this,R.style.AlertDialogTheme);
        alertdialog.setMessage("Are you sure you want to exit ?").
                setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        showToggle(menu);

        return true;

    }

    private void showToggle(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_service, menu);
        MenuItem itemswitch = menu.findItem(R.id.switch_action_bar);
        itemswitch.setActionView(R.layout.use_switch);
        final ToggleButton toggleButton  = menu.findItem(R.id.switch_action_bar).getActionView().findViewById(R.id.switch2);
        SharedPreferences prefs = MviewApplication.ctx.getSharedPreferences(Constants.service_toggle, MODE_PRIVATE);
        boolean service_tg = prefs.getBoolean("service_key", false);
        if (service_tg) {
            toggleButton.setChecked(true);
        } else {
            toggleButton.setChecked(false);
        }
        System.out.println("service_tg is "+service_tg);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isPressed()) {
                    if (toggleButton.isChecked()) {
                        sdkFlag="1";
                        if(isPhoneStatePermissionGranted())
                        {
                           Mview.get_report(getApplicationContext(),"0", "0",Utils.getMyContactNum(MainActivity.this),"1");editor_n.putBoolean("service_key", true).apply();
                        }
                        else
                        {
                            requestForPhoneStatePermission();
                            editor_n.putBoolean("service_key", false).apply();
                            toggleButton.setChecked(false);
                        }


                    }
                    else {
                        sdkFlag="2";
                       Mview.get_report(getApplicationContext(),"0", "0",Utils.getMyContactNum(MainActivity.this),"2");editor_n.putBoolean("service_key", false).apply();


                    }
                }

            }

        });


//        SharedPreferences prefs = MviewApplication.ctx.getSharedPreferences(Constants.service_toggle, MODE_PRIVATE);
//        boolean service_tg = prefs.getBoolean("service_key", false);
//        if (service_tg==true) {
//            toggleButton.setChecked(true);
//        } else {
//            toggleButton.setChecked(false);
//        }
//        System.out.println("service_tg is "+service_tg);
//        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(buttonView.isPressed()) {
//                    if (toggleButton.isChecked()) {
//                        editor_n.putBoolean("service_key", true);
//                        editor_n.putBoolean("listen_service",true);
//                        editor_n.apply();
//                        sendToggleOnEvent();
//                        startMtantuTasksJar();
//                        startPingjar();
//                        startAllServices();
//                     //   Toast.makeText(MainActivity.this, "Service is Enabled ", Toast.LENGTH_SHORT).show();
//                    }
//                    else {
//                        System.out.println(" entering else part");
//                        editor_n.putBoolean("service_key", false);
//                        editor_n.putBoolean("listen_service",false);
//                        editor_n.commit();
//                        sendToggleOffEvent();
//                        stopTheServices();
//                        stopPingjar();
//                        stopMtantuTasksJar();
//               //         Toast.makeText(MainActivity.this, "Service is Disabled ", Toast.LENGTH_SHORT).show();//stop all services and start single periodic request
//
//                    }
//                }
//
//            }
//
//        });
    }
    private  void requestForPhoneStatePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE
        },READ_PERMISSIONS_REQ_CODE);
    }

    private boolean isPhoneStatePermissionGranted() {
        System.out.println("getting permissions");
        int permissionReadPHoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionReadPHoneState != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        else
        {
            return true; }



    }
    private void stopPingjar()
    {
        DB_handler db_handler = null;
        try {

            db_handler = new  DB_handler(MainActivity.this);
            db_handler.open();
            db_handler.updateStatusOfJarToDownloadStart("Ping.aar", "inactive");
            db_handler.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void stopMtantuTasksJar()
    {
        DB_handler db_handler = null;
        try {

            db_handler = new DB_handler(MainActivity.this);
            db_handler.open();
            db_handler.updateStatusOfJarToDownloadStart("MtantuTasks.aar", "inactive");
            db_handler.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void startPingjar()
    {
        DB_handler db_handler = null;
        try {

            db_handler = new DB_handler(MainActivity.this);
            db_handler.open();
            db_handler.updateStatusOfJarToDownloadStart("Ping.aar", "completed");
            db_handler.close();

        } catch ( IOException e) {
            e.printStackTrace();
        }

    }

    private void startMtantuTasksJar()
    {
        DB_handler db_handler = null;
        try {

            db_handler = new DB_handler(MainActivity.this);
            db_handler.open();
            db_handler.updateStatusOfJarToDownloadStart("MtantuTasks.aar", "completed");
            db_handler.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        SharedPreferences prefs = getSharedPreferences(Constants.service_toggle, MODE_PRIVATE);
//        MenuItem item = menu.findItem(R.id.settings);
//
//        if(prefs.getBoolean("service_key",false)) {
//            menu.getItem(0).setTitle("Disable Network Test");
//        }
//        else {
//            menu.getItem(0).setTitle("Enable Network Test");
//
//        }
//        return super.onPrepareOptionsMenu(menu);
//
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            ////Toast.makeText(getApplicationContext(), "Logout user!",////Toast.LENGTH_LONG).show();
            return true;
        }

        // user is in notifications fragment
        // and selected 'Mark all as Read'
        if (id == R.id.action_mark_all_read) {
            ////Toast.makeText(getApplicationContext(), "All notifications marked as read!",////Toast.LENGTH_LONG).show();
        }

        // user is in notifications fragment
        // and selected 'Clear All'
        if (id == R.id.action_clear_notifications) {
            ////Toast.makeText(getApplicationContext(), "Clear all notifications!",////Toast.LENGTH_LONG).show();
        }
        if(id== R.id.settings)
        {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, SERVICE_PERMISSIONS_REQUEST);
            }
            else {
                showAlertForAppServices();
            }
            //showAlertForSeekingPermissions();

        }

        return super.onOptionsItemSelected(item);
    }

    private void showAlertForSeekingPermissions() {


        final Dialog dialog = new Dialog(MainActivity.this, R.style.AlertDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.alertdialog, null);
        TextView textView = view.findViewById(R.id.msg);
        textView.setText("You need to allow to turn on the permissions.");
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        Button okbutton = view.findViewById(R.id.ok);
        okbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showAlertForAppServices();
                }


        });

    }

    private void showAlertForAppServices() {


        SharedPreferences prefs = MviewApplication.ctx.getSharedPreferences(Constants.service_toggle, MODE_PRIVATE);
        boolean service_tg = prefs.getBoolean("service_key", false);
           System.out.println("service_tg is "+service_tg);
        final Dialog dialog = new Dialog(MainActivity.this, R.style.AlertDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.services_layout, null);
        TextView serviceTxt = view.findViewById(R.id.serviceText);

        ToggleButton toggleButton = view.findViewById(R.id.servicetoggle);
        if (service_tg) {
            toggleButton.setChecked(true);
            serviceTxt.setText("Please turn this button off to disable the services!!");

        } else {
            toggleButton.setChecked(false);
            serviceTxt.setText("Please turn this button on to enable the services!!");

        }
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                    if(buttonView.isPressed()) {
                        if (toggleButton.isChecked()) {

                            //       Utils.showToast(MainActivity.this, "checked...");

                            //showAlertForStartingAutoStartPage();

                    editor_n.putBoolean("service_key", true);
                    editor_n.putBoolean("listen_service",true);
                    editor_n.apply();
                    dialog.dismiss();
                    Utils.openAutoStartPage(MainActivity.this);
                        //    checkForGps();
                    startAllServices();
                            //start all services and stop single periodic request

                        }
                        else {
                            //             Utils.showToast(MainActivity.this, "not checked...");
                            editor_n.putBoolean("service_key", false);
                            editor_n.putBoolean("listen_service",false);
                            editor_n.commit();
                            dialog.dismiss();
                            stopTheServices();//stop all services and start single periodic request
                            Utils.showToast(MainActivity.this,"You may turn off the autostart service from  app manager of your phone settings.");
                        }
                    }




            }
        });

        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        }

    private void checkForGps() {

        LocationManager locationManager = (LocationManager)context
                .getSystemService(LOCATION_SERVICE);

        // getting GPS status
        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        boolean  isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if(!isGPSEnabled && !isNetworkEnabled ) {
            CommonAlertDialog.displayPromptForEnablingGPS(this);
        }
    }


    private void askpermissionsBackground(){
        int permissiongps = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        if (permissiongps != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION};
            ActivityCompat.requestPermissions(this,
                    permissions, 10);

        }
    }

    private void seekReqPermissions() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED)
        { ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                  Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_CALL_LOG
            }, SERVICE_PERMISSIONS_REQUEST);
        }

//else {
//
//            editor_n.putBoolean("service_key", true);
//            editor_n.putBoolean("listen_service",true);
//            editor_n.apply();
//
//            Utils.openAutoStartPage(MainActivity.this);
//            startAllServices();
//        }
    }

    private void showAlertForStartingAutoStartPage() {

        final Dialog dialog = new Dialog(MainActivity.this, R.style.AlertDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.autostart_layout, null);
        TextView serviceTxt = view.findViewById(R.id.serviceText);
        serviceTxt.setText(Constants.autostart_msg);

        Button ok=view.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Utils.openAutoStartPage(MainActivity.this);
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }

    private void startAllServices() {
        invalidateOptionsMenu();
/*
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                Utils.showToast(MainActivity.this,"iffff");
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
            else

            {
                Utils.showToast(MainActivity.this,"elseeee");
            }
        }
*/

//        if (!Utils.isMyServiceRunning(AllServices.class))
//        {
//
//            System.out.println("starting service "+"flag "+Constants.service_started);
////            if(Constants.service_started) {
////                Intent allIntent = new Intent(MainActivity.this, AllServices.class);
////                //Toast.makeText(MainActivity.this, "calling alarm bckgtnd dervice from home", //Toast.LENGTH_SHORT).show();
////                startService(allIntent);
////            }
//        }
     /*   if(Utils.isMyServiceRunning(Background_service.class))
        {
            Intent backIntent =new Intent(MainActivity.this,Background_service.class);
            stopService(backIntent);
        }*/
    }


    private void stopTheServices() {
        System.out.println("entering this method");
//        if(Utils.isMyServiceRunning(AllServices.class)) {
//            invalidateOptionsMenu();
//            Intent intent = new Intent(MainActivity.this, AllServices.class);
//            stopService(intent);
//            Utils.stopAsyncTasks();
//
//
//        }
      /*  if(!Utils.isMyServiceRunning(Background_service.class))
        {
            Intent intent=new Intent(MainActivity.this,Background_service.class);
            startService(intent);
        }*/

    }

    private void startAllTheServices() {


    }

    // show or hide the fab
    private void toggleFab() {
        if (navItemIndex == 0)
            fab.show();
        else
            fab.hide();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_home);
        //if( fragment != null)
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    /*    SharedPreferences prefs = MviewApplication.ctx.getSharedPreferences(Constants.service_toggle, MODE_PRIVATE);
                        if (prefs != null) {
                            boolean service_tg = prefs.getBoolean("service_key", false);
                            System.out.println("service key is " + service_tg);
                            Intent bck = new Intent(MainActivity.this, Background_service.class);
                            if (!service_tg) {
                                if (mView_HealthStatus.startbackgroundservice)// btn checked
                                {
                                    startService(bck);
                                } else
                                {
                                    stopService(bck);
                                }


                            }

                        }
                        else {
                           Intent intent=new Intent(MainActivity.this,Background_service.class);
                           startService(intent);
                        }*/
                    }
                   //public void run() {
                });
            }
            else
            {
                System.out.println("result code is"+resultCode);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.home:
                //loadFragment(R.string.nav_home, new HomeFragment());
                ////Toast.makeText(MainActivity.this, "getting called from 3",////Toast.LENGTH_SHORT).show();
                fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);
                fragmentManager.beginTransaction().replace(R.id.frame, new HomeFragment()).commit();
                drawer.closeDrawers();
                getSupportActionBar().setTitle(R.string.nav_home);
                //  Config.onbackpress="back";
                break;

           /* case R.id.call:
                loadFragment(R.string.call_sms_mms,new Call_msg_fragment());
                Config.onbackpress="home";
                break;*/
            //comment by vikas
//            case R.id.wifiheatmap:
//               Bundle bundle=new Bundle();
//                bundle.putString("flowType","rapid");
//                Utils.takeToNextActivity(this,CanvasActivity.class,bundle);
              //  startActivity(new Intent(MainActivity.this, CanvasActivity.class));
               // loadFragment(R.string.wifi_coverage_test,new CampaignFragment());
               // Config.onbackpress="home";
                //startActivity(new Intent(MainActivity.this,WifiActivity.class));
//                break;
            case R.id.speedtest:
               /* speedtestoptions=new SpeedTestOptions();


                loadFragment(R.string.speedtest,speedtestoptions);*/
                loadFragment(R.string.speedtest,new SpeedTestFragment());
                Config.onbackpress="home";
                break;
           /* case R.id.btry:
                loadFragment(R.string.battery,new BatteryUsageFragment());
                Config.onbackpress="home";
                break;*/
           /* case R.id.network:
                loadFragment(R.string.network,new NetworkCoverage());
                Config.onbackpress="home";*/
            // by swapnil 09/08/2023 for removinf surveys
           // case R.id.surveyList:
               // Utils.takeToNextActivity(MainActivity.this,BuildingTabActivity.class,null);
               // break;
            case R.id.chart:
                Config.onbackpress="home";
                loadFragment(R.string.charts,new ChartsFragment());
               /* if(mView_HealthStatus.iCurrentNetworkState==4) {
                    loadFragment(R.string.charts, new ChartFragment());
                }
                else if(mView_HealthStatus.iCurrentNetworkState==3)
                {
                    loadFragment(R.string.charts,new ChartsFragment3g());
                }
                else if(mView_HealthStatus.iCurrentNetworkState==2)
                {
                    loadFragment(R.string.charts,new ChartsFragment2g());
                }
*/                break;





        }
        return true;
    }

    private void loadFragment(int title, Fragment Fragmentclass,String issue) {

        fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentManager.beginTransaction().replace(R.id.frame, Fragmentclass).addToBackStack("others").commit();
        Bundle bundle=new Bundle();
        bundle.putString("type",issue);
        Fragmentclass.setArguments(bundle);
        drawer.closeDrawers();
        getSupportActionBar().setTitle(title);


    }

    @Override
    public void onTaskCompleted() {

/*
        if(CommonUtil.request==3)
        {



            expandablelistview.expandGroup(position);

            //subCatList.addAll(CommonUtil.newDashboardList);
            graphList.addAll(CommonUtil.newGraphdataList);
            if(CommonUtil.mappgraphlist!=null && CommonUtil.mappgraphlist.size()>0) {
                mappedgraphList.addAll(CommonUtil.mappgraphlist);
            }

            adapter.notifyDataSetChanged();
        }
*/

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void openRespectiveScreens(int childPosition, ArrayList<String> _issuesList) {

        if(issuesList==null)
        {
            issuesList=new ArrayList<>();
        }
        if(issuesList.size()==0)

        {
            if(_issuesList!=null && !_issuesList.isEmpty())
            {
                issuesList.addAll(_issuesList);
            }

        }
        System.out.println("respective screens "+childPosition +" issueslist "+issuesList);
        if(issuesList!=null &&issuesList.size()>0) {
            System.out.println("child pos is "+childPosition);
            switch (childPosition) {
                case 0:
                    REPORT_FLAG = true;
                    try {
                        new DialogClass("Ping").showPingAlert(this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
//                case 2:
//                    Bundle bundle=new Bundle();
//                    bundle.putString("flowType","rapid");
//                    Utils.takeToNextActivity(this,CanvasActivity.class,bundle);

//                    break;
//                case 2:
//                    Utils.takeToNextActivity(MainActivity.this,BuildingTabActivity.class,null);
//                    break;
                case 1:
                    loadFragment(R.string.fiveGParameter, new FiveGParametersFragment());
                    Config.onbackpress = "home";
                    break;

                case 2:
                    loadFragment(R.string.fiveGParameter, new ChceckingNewSimConditions());
                    Config.onbackpress = "home";
                    break;
//                case 6:
//                    new MainActivity.BackgroundTasktgetvalues("down").execute();
//                    break;
//                case 7:
//                    Utils.showToast(getApplicationContext(),"Starting speed test");
//                    new MainActivity.BackgroundTasktgetvalues("up").execute();
//                    System.out.println(" Time is"+System.currentTimeMillis());
//               break;
                case 3:

                    loadFragment(R.string.call_sms_mms,new Call_msg_fragment_New());
                    Config.onbackpress="home";
                    break;
// NEW CHANGES DONE BY  SWAPNIL BOTH re new classes
//                case 9:
//                    loadFragment(R.string.network_monitor_mms,new NetworkFragmentNew2());
//                    Config.onbackpress="home";
//                    break;
                case 4:
                    loadFragment(R.string.web_test,new WebTest());
                    Config.onbackpress="home";
                    break;

//                case 11:
//
//                    loadFragment(R.string.new_video_test, new NewVideoFragment());
//                    Config.onbackpress="home";
//                    break;
//                case 6:
//                loadFragment(R.string.speedtest,new SpeedTestFragment());
//                Config.onbackpress="home";
//                break;
                case 5:
                    loadFragment(R.string.call_sms_mms,new USSDTEST());
                    Config.onbackpress="home";
                    break;

                case 6:
                   // Intent allIntent = new Intent(MainActivity.this, ForegroundService.class);
                   // startService(allIntent);
                    loadFragment(R.string.new_video_test,new NewVideoFragmentIframe());
                    Config.onbackpress="home";
                    break;

                // Bundle bundleq=new Bundle();N
                   // bundleq.putString("flowType","rapid");
                   // Utils.takeToNextActivity(this,MainActivity3.class,bundleq);
                    //break;
                case 7:
                    loadFragment(R.string.speedtest,new SpeeDTestFragmentNew());
                    Config.onbackpress="home";
                    break;

                case 8:
                    loadFragment(R.string.sendsms,new SmsFragment());
                    Config.onbackpress="home";
                    break;
//                case 10:
//                loadFragment(R.string.speedtest,new SpeedTestFragment());
//                Config.onbackpress="home";
//                break;
//                case 10:
//                    loadFragment(R.string.cts,new CtsFragment());
//                    Config.onbackpress="home";
//                    break;
//
//                case 9:
//                    Intent intent = new Intent(this, WebViewActivity.class);
//                    /* intent.putExtra("chartName",chartName);*/
//                    startActivity(intent);
//                    break;
//                case 10:
//                    Intent intent1 = new Intent(this, AppLaunch.class);
//
//                    /* intent.putExtra("chartName",chartName);*/
//                    startActivity(intent1);
//                    break;
                case 9:
                    /* intent.putExtra("chartName",chartName);*/
                    new DialogPopup("Manually change status").showClearLogDialog(this);
                    break;

                case 10:
                    /* intent.putExtra("chartName",chartName);*/
                    new DialogPopup("Manually Send Evt").sendEvtServerManually(this);
                    break;

                case 11:
                    /* intent.putExtra("chartName",chartName);*/
                    new DialogPopup("Manually Start All the Tests").startTestManually(this);
                    break;
                case 12:
                    Intent driveTestIntent = new Intent(this, HomeActivity.class);
                    startActivity(driveTestIntent);
                    break;
            }
        }
    }



    private class  BackgroundTasktgetvalues extends AsyncTask<String, Integer, Object> {
        ProgressDialog progressDialog;
        String typeis;

        public BackgroundTasktgetvalues(String type) {
            typeis=type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Wait");

            if (typeis.equalsIgnoreCase("down")) {
                Utils.showToast(getApplicationContext(),"Download test start");
            }
            else
            {
                Utils.showToast(getApplicationContext(),"Upload test start");


            }
            progressDialog.setCancelable(false);
            progressDialog.show();

        }
        @Override
        protected Object doInBackground(String... strings) {
            if (typeis.equalsIgnoreCase("down")) {

                return downloadspeedtest();
//                return  0F;
            }
            else
            {
                float finaluploadvalue=0F;
                for (int i=0;i<3;i++) {
                    float uploadvalue = uploadspeedtest();
                    finaluploadvalue = uploadvalue + finaluploadvalue;

                }
                finaluploadvalue=finaluploadvalue/3F;
                System.out.println("FappsSpeedTest Final upload avg value is "+finaluploadvalue);
                return finaluploadvalue;
            }
        }
        @Override
        protected void onPostExecute(Object obj) {

            progressDialog.dismiss();


            final DialogView dialogView=new DialogView(MainActivity.this);

            dialogView.setLayoutForDialog("Speed is "+obj,"Ok","Cancel","Speed",R.drawable.exit_1);
            Button ok=dialogView.getDialog().findViewById(R.id.ok);
            Button cancel=dialogView.getDialog().findViewById(R.id.cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogView.getDialog().dismiss();
                }
            });
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogView.getDialog().dismiss();
                }
            });




        }

    }

    public float downloadspeedtest() {
        JSONObject obj = new JSONObject();
        float downloadedFileSize = 0F;
        long start=0;
        long end =0;

        try {
            URL url=new URL("http://3.108.182.22:8056/mehak/Delhi.json");
            InputStream in = url.openStream();
            start = System.currentTimeMillis();

            BufferedInputStream bis = new BufferedInputStream(in);
            {

                byte[] data = new byte[1024];
                int count;
                while ((count = bis.read(data, 0, 1024)) != -1)
                {
                    downloadedFileSize += count;

                }
                end= System.currentTimeMillis();

                System.out.println("FappsSpeedTest bytes received in download is "+downloadedFileSize);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



        System.out.println("FappsSpeedTest Download start and end time "+start+" "+end );

        float  sec = (end - start) / 1000F;


        System.out.println("FappsSpeedTest Difference of time is "+sec + " seconds");


//        downloadjson.put("bytes_received", downloadedFileSize);
        System.out.println("FappsSpeedTest Size of file downloaded in bytes is "+downloadedFileSize);

        downloadedFileSize=downloadedFileSize*8;



        float sizeofiledownloaded=downloadedFileSize/(1024F*1024F);
//         sizeofiledownloaded=downloadedFileSize/(1000F);

        System.out.println("FappsSpeedTest Size of file downloaded in Mb is "+sizeofiledownloaded);




        float downloadspeed=sizeofiledownloaded/sec;

//        downloadspeed=downloadspeed/()
        System.out.println("FappsSpeedTest Speed of file downloaded in Mbps is "+downloadspeed);

        downloadspeed=(float) Math.round(downloadspeed);


//        downloadspeed=downloadspeed*4.8;
        downloadspeed=downloadspeed*1.5F;


//        double roundoff	=Math.round(downloadspeed * 100.0) / 100.0;

        System.out.println("FappsSpeedTest Downloaded file speed is "+downloadspeed);
        String str=String.valueOf(downloadspeed);


        try {
//            obj.put("dlDataSize",downloadedFileSize);
//            obj.put("url","http://3.108.182.22:8056/mehak/Delhi.json");
            obj.put("dlMaxThroughput",str);
//            obj.put("latency",sec);
//            obj.put("dlThroughput",str);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        Utils.showToast(getApplicationContext(),"Download speed test is "+str+" Mbps");







        System.out.println("downloadjson  obj is "+obj);

//String newd=Utils.getRoundedOffVal(String.valueOf(downloadspeed),2);

        return downloadspeed;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private float uploadspeedtest() {
        try {

            // by swapnil bansal for new speed test logic implementation
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//            int serverPort = 4020;
//            InetAddress host = InetAddress.getByName("198.12.250.223");
//            System.out.println("Connecting to server on port " + serverPort);
//            Socket socket = new Socket(host,serverPort);
//            System.out.println("Just connected to " + socket.getRemoteSocketAddress());
//            PrintWriter toServer = new PrintWriter(socket.getOutputStream(),true);
//            BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            long uploadstartNew = System.currentTimeMillis();
//            InputStream is = getAssets().open("3kb.txt");
//            int size = is.available();
//            System.out.println(" size is "+size);
//            byte[] buffer = new byte[65536]; //declare the size of the byte array with size of the file
//            is.read(buffer); //read file
//            is.close(); //close file
//            String str_data = new String(buffer);
//            toServer.println("Hello from Client "+System.currentTimeMillis()+str_data+ socket.getLocalSocketAddress());
//            System.out.println(" uplaod time is"+uploadstartNew);
//            String line = fromServer.readLine();
//            System.out.println("Client received: " +line);
//              long uploadendNew = System.currentTimeMillis();
//           System.out.println("Connecting to server on port " + uploadendNew);
//           toServer.close();
//             fromServer.close();
//            socket.close();
//            long diffmsNew = uploadendNew - uploadstartNew;
//           System.out.println("Upload dummy diff in ms " + diffmsNew);
//            uploadfiletimeNew = (uploadendNew - uploadstartNew) / 1000F;
//           System.out.println("FappsSpeedTest is" + uploadfiletimeNew);



            // old logic for speed test

            JSch jsch = new JSch();
            Session session = jsch.getSession("mview_ftp", "180.179.214.56", 30030);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword("92zbVZ");
            session.connect();
            System.out.println("Connection established.");
            System.out.println("Creating SFTP Channel.");
            String firstRemoteFile = "1kb.txt";
            Channel sftp = session.openChannel("sftp");
            sftp.connect();
            ChannelSftp channelSftp = (ChannelSftp) sftp;
            long uploadstart = System.currentTimeMillis();
            System.out.println("Upload dummy file start time " + uploadstart);
            InputStream inputStream1 = getAssets().open("1kb.txt");
            channelSftp.put(inputStream1, firstRemoteFile);
            channelSftp.exit();
            long uploadend = System.currentTimeMillis();
            System.out.println("Upload dummy file end time " + uploadend);
            long diffms = uploadend - uploadstart;
            System.out.println("Upload dummy diff in ms " + diffms);
            uploadfiletime1 = (uploadend - uploadstart) / 1000F;
            System.out.println("FappsSpeedTest Time take in 1 file upload is " + uploadfiletime1);
            session.disconnect();


        } catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Exception 1 upload is "+e.toString());


        }




        float s=0F;
        float sizeofile2=163190*8;

        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession("mview_ftp", "180.179.214.56", 30030);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword("92zbVZ");
            session.connect();
      /*      long uploadstart = System.currentTimeMillis();
            System.out.println("Upload file 2 start time "+uploadstart);*/
            System.out.println("Connection established.");
            System.out.println("Creating SFTP Channel.");
//            String firstRemoteFile = "sample_upload.xlsx";
            String firstRemoteFile = "1Mb.txt";
            Channel sftp = session.openChannel("sftp");
            sftp.connect();


            ChannelSftp channelSftp = (ChannelSftp) sftp;
            InputStream inputStream = getAssets().open("1Mb.txt");
            long uploadstart = System.currentTimeMillis();
            System.out.println("Upload file 2 start time " + uploadstart);
            channelSftp.put(inputStream, firstRemoteFile);
            channelSftp.exit();
            long uploadend = System.currentTimeMillis();
            System.out.println("Upload file 2 end time "+uploadend);




            System.out.println("2 file upload time is "+uploadend);
            long difms=uploadend - uploadstart;
            float uploadsec = (uploadend - uploadstart) / 1000F;
            System.out.println("FappsSpeedTest Upload file 2 time is "+uploadsec+ " in ms  "+difms);
            float finaltime;
            if (uploadsec>=uploadfiletime1) {

                //  finaltime = uploadsec - uploadfiletime1;
//                  finaltime = uploadsec - uploadfiletime1;

                finaltime = uploadsec/2.5F;

            }
            else
            {
                finaltime = uploadsec/2.5F;


            }
            System.out.println("FappsSpeedTest Final Time taken is "+finaltime);

            System.out.println("FappsSpeedTest size of file is "+sizeofile2);

            s=sizeofile2/finaltime;
            System.out.println("FappsSpeedTest Speed of file upload str is "+s);

            s=(float)Math.round(s);
            System.out.println("FappsSpeedTest After round speed is "+s);

            s=s/(1024F*1024F);
            s=s*1.2F;
//            double te=2.2;
//            System.out.println("3 double  value is  "+te);

            System.out.println("FappsSpeedTest Before string  "+s);

            str =String.valueOf(s);
            System.out.println("FappsSpeedTest After 2.2 x  file 2 upload is "+str);




        } catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Exception in 2 upload "+e.toString());
        }
          // by swapnil 13/02/2023
         // str=Utils.getRoundedOffVal(str,2);
        return s;
    }

//    private float uploadSpeedTest1() {
//        try {
//            Socket socket = new Socket("198.12.250.223", 4020);
//            dataInputStream = new DataInputStream(socket.getInputStream());
//            dataOutputStream = new DataOutputStream(socket.getOutputStream());
//            System.out.println("Sending the File to the Server");
//            // Call SendFile Method
//
//            sendFile("");
//            dataInputStream.close();
//            dataInputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return 0;
//    }

    // sendFile function define here


//    private static void sendFile(String path) throws Exception {
//        int bytes = 0;
//        // Open the File where he located in your pc
//        File file = new File(path);
//        FileInputStream fileInputStream
//                = new FileInputStream(file);
//
//        // Here we send the File to Server
//        dataOutputStream.writeLong(file.length());
//        // Here we  break file into chunks
//        byte[] buffer = new byte[8 * 1024];
//        while ((bytes = fileInputStream.read(buffer))
//                != -1) {
//            // Send the file to Server Socket
//            dataOutputStream.write(buffer, 0, bytes);
//            dataOutputStream.flush();
//        }
//        // close the file here
//        fileInputStream.close();
//    }


    private void showAlert() {
        String lat="0.0";
          String lon="0.0";
        final Dialog dialog = new Dialog(MainActivity.this, R.style.AlertDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.signalstrengthlayout, null);
        TextView ss1=view.findViewById(R.id.ss1);
        TextView ss2=view.findViewById(R.id.ss2);
        String strength = "";
        if (mView_HealthStatus.currentInstance.equalsIgnoreCase("lte")) {
            {
                if (mView_HealthStatus.lteRSRP != null) {
                    if (Integer.parseInt(mView_HealthStatus.lteRSRP) < 0 && Integer.parseInt(mView_HealthStatus.lteRSRP) > -75) {
                        ss1.setTextColor(getResources().getColor(R.color.green));


                        strength = mView_HealthStatus.lteRSRP + "dbm ( Good )";
                    } else if (Integer.parseInt(mView_HealthStatus.lteRSRP) <= -75 && Integer.parseInt(mView_HealthStatus.lteRSRP) > -95) {
                        ss1.setTextColor(getResources().getColor(R.color.orange));

                        strength = mView_HealthStatus.lteRSRP + "dbm ( Fine )";
                    } else if (Integer.parseInt(mView_HealthStatus.lteRSRP) <= -95 /*&& Integer.parseInt(mView_HealthStatus.lteRSRP) > -115*/) {
                        ss1.setTextColor(getResources().getColor(R.color.blue));

                        strength = mView_HealthStatus.lteRSRP + "dbm ( Poor )";
                    }

                }

            }
        } else if (mView_HealthStatus.currentInstance.equalsIgnoreCase("wcdma")) { if (mView_HealthStatus.rscp != null) {
                if (Integer.parseInt(mView_HealthStatus.rscp) < 0 && Integer.parseInt(mView_HealthStatus.rscp) > -75) {
                    ss1.setTextColor(getResources().getColor(R.color.green));
                   ss1.setTextColor(getResources().getColor(R.color.green));
                    strength = mView_HealthStatus.rscp + "  dbm (Good)";
                } else if (Integer.parseInt(mView_HealthStatus.rscp) <= -75 && Integer.parseInt(mView_HealthStatus.rscp) > -95) {
                    ss1.setTextColor(getResources().getColor(R.color.orange));
                   ss1.setTextColor(getResources().getColor(R.color.orange));
                    strength = mView_HealthStatus.rscp + "  dbm (Fine)";
                } else if (Integer.parseInt(mView_HealthStatus.rscp) <= -95 /*&& Integer.parseInt(mView_HealthStatus.rscp) > -115*/) {
                    ss1.setTextColor(getResources().getColor(R.color.blue));
                   ss1.setTextColor(getResources().getColor(R.color.blue));
                    strength = mView_HealthStatus.rscp + " dbm (Poor) ";
                }


            }


        }
        else if (mView_HealthStatus.currentInstance.equalsIgnoreCase("gsm")) {


            if (mView_HealthStatus.gsmSignalStrength < 0 && mView_HealthStatus.gsmSignalStrength > -75) {
                ss1.setTextColor(getResources().getColor(R.color.green));
               ss1.setTextColor(getResources().getColor(R.color.green));
                strength = mView_HealthStatus.gsmSignalStrength + " dbm (Good)";
            } else if (mView_HealthStatus.gsmSignalStrength <= -75 && mView_HealthStatus.gsmSignalStrength > -95) {
                ss1.setTextColor(getResources().getColor(R.color.orange));
               ss1.setTextColor(getResources().getColor(R.color.orange));
                strength = mView_HealthStatus.gsmSignalStrength + "  dbm (Fine)";
            } else if (mView_HealthStatus.gsmSignalStrength <= -95 /*&& mView_HealthStatus.gsmSignalStrength > -115*/) {
                ss1.setTextColor(getResources().getColor(R.color.blue));
                ss1.setTextColor(getResources().getColor(R.color.blue));
                strength = mView_HealthStatus.gsmSignalStrength + "dbm (Poor)";
            }

        }
//second sim
        String sec_strength = null;
        if (mView_HealthStatus.second_cellInstance != null) {

            if (mView_HealthStatus.second_cellInstance.equalsIgnoreCase("lte")) {
                {

                    if (mView_HealthStatus.second_Rsrp < 0 && mView_HealthStatus.second_Rsrp > -75) {
                       ss2.setTextColor(getResources().getColor(R.color.green));

                        sec_strength = mView_HealthStatus.second_Rsrp + "dbm ( Good )";
                    } else if (mView_HealthStatus.second_Rsrp <= -75 && mView_HealthStatus.second_Rsrp > -95) {
                       ss2.setTextColor(getResources().getColor(R.color.orange));
                        sec_strength = mView_HealthStatus.second_Rsrp + "dbm ( Fine )";
                    } else if (mView_HealthStatus.second_Rsrp <= -95 /*&& mView_HealthStatus.second_Rsrp > -115*/) {
                       ss2.setTextColor(getResources().getColor(R.color.blue));
                        sec_strength = mView_HealthStatus.second_Rsrp + "dbm ( Poor )";
                    }

                }


            } else if (mView_HealthStatus.second_cellInstance.equalsIgnoreCase("wcdma")) {

                if (mView_HealthStatus.second_rscp_3G < 0 && mView_HealthStatus.second_rscp_3G > -75) {
                   ss2.setTextColor(getResources().getColor(R.color.green));
                    sec_strength = mView_HealthStatus.second_rscp_3G + "  dbm (Good)";
                } else if (mView_HealthStatus.second_rscp_3G <= -75 && mView_HealthStatus.second_rscp_3G > -95) {
                   ss2.setTextColor(getResources().getColor(R.color.orange));
                    sec_strength = mView_HealthStatus.second_rscp_3G + "  dbm (Fine)";
                } else if (mView_HealthStatus.second_rscp_3G <= -95 /*&& mView_HealthStatus.second_rscp_3G > -115*/) {
                   ss2.setTextColor(getResources().getColor(R.color.blue));
                    sec_strength = mView_HealthStatus.second_rscp_3G + " dbm (Poor) ";
                }


            } else if (mView_HealthStatus.second_cellInstance.equalsIgnoreCase("gsm")) {


                if (mView_HealthStatus.second_rxLev < 0 && mView_HealthStatus.second_rxLev > -75) {
                   ss2.setTextColor(getResources().getColor(R.color.green));
                    sec_strength = mView_HealthStatus.second_rxLev + " dbm (Good)";
                } else if (mView_HealthStatus.second_rxLev <= -75 && mView_HealthStatus.second_rxLev > -95) {
                   ss2.setTextColor(getResources().getColor(R.color.orange));
                    sec_strength = mView_HealthStatus.second_rxLev + "  dbm (Fine)";
                } else if (mView_HealthStatus.second_rxLev <= -95 /*&& mView_HealthStatus.second_rxLev > -115*/) {
                   ss2.setTextColor(getResources().getColor(R.color.blue));
                    sec_strength = mView_HealthStatus.second_rxLev + "dbm (Poor)";
                }

            }
        }
        if (strength != null) {
            System.out.println("strength is " + strength);
            if (!((strength.equalsIgnoreCase(null)) || strength.equals("") || strength.equals(" "))) {
                ss1.setText(strength);

            }
        }
        if (sec_strength != null) {
            System.out.println("sec strength is " + sec_strength);
           ss2.setText(sec_strength);
        }

        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        Button yes=view.findViewById(R.id.yes);
        if(listenService.gps!=null) {
             lat = listenService.gps.getLatitude() + "";
            lon = listenService.gps.getLongitude() + "";
        }

        final String finalLat = lat;
        String finalLon = lon;
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Utils.showReportIssueAlert(issuesList.get(0),context, finalLat, finalLon, Constants.NETWORK_COVERAGE_ISSUE);
                dialog.dismiss();
            }
        });
        Button no=view.findViewById(R.id.no);
        final String finalLat1 = lat;
        String finalLon1 = lon;
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Utils.sendIssue("NONE",issuesList.get(0));

          //      WebService.API_sendReportIssue(finalLat1, finalLon1,"NONE", issuesList.get(0));
            }
        });



    }

    public void setIndexValues(String lowerIndex, String upperIndex) {

    }

    public void openSpeedTestList() {
        if(speedtestoptions!=null)
        {
            loadFragment(R.string.speedtest,speedtestoptions);
        }
        else
        {
            speedtestoptions=new SpeedTestOptions();
            loadFragment(R.string.speedtest,speedtestoptions);
        }
    }

    public void openVideoTestFragment() {
        loadFragment(R.string.videotest_title,new VideoSpeedtest_fragment());
    }

//    public void buttonClick(View view) {
//        Toast.makeText(MainActivity.context,"on stop of activity",Toast.LENGTH_SHORT).show();
//    }


   /* public void callMessageFunction(int i, String s) {

        {

            System.out.println("calling mesg function 2");

            this.msg = i;
            this.msgshow = s;

            if (dwnld_upload_fragment != null) {
                System.out.println("calling in if");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((Dwnld_upload_fragment) dwnld_upload_fragment).sendMessageToActivity(msg, msgshow);
                    }
                });

            } else {
                System.out.println("calling in else");
                //System.out.println("calling send msg from main");
                runOnUiThread(new Runnable() {
                    @Override

                    public void run() {
                        System.out.println("calling send msg from main 1");
                        dwnld_upload_fragment = new Dwnld_upload_fragment();
                        ((Dwnld_upload_fragment) dwnld_upload_fragment).sendMessageToActivity(msg, msgshow);
                        System.out.println("calling send msg from main 2");
                    }
                });


            }
        }
    }
*/
}
