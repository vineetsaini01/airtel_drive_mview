package com.newmview.wifi.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;


import com.google.android.gms.location.DetectedActivity;
import com.google.gson.Gson;
import com.mview.airtel.BuildConfig;
import com.mview.airtel.R;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.bean.HistoryModel;
import com.newmview.wifi.bean.MapModel;
import com.newmview.wifi.bean.WifiHeatMapPoints;
import com.newmview.wifi.bean.WifiModel;
import com.newmview.wifi.canvas.CanvasDrawElements;
import com.newmview.wifi.customdialog.MyAlertDialog;
import com.newmview.wifi.enumtypes.AlertType;
import com.newmview.wifi.fragment.Dwnld_upload_fragment;
import com.newmview.wifi.fragment.VideoSpeedtest_fragment;
import com.newmview.wifi.helper.CoordsBuilder;
import com.newmview.wifi.helper.DirectionPath;
import com.newmview.wifi.other.CommonAlertDialog;
import com.newmview.wifi.other.DialogManager;
import com.newmview.wifi.sensors.Compass;
import com.newmview.wifi.database.DB_handler;
import com.mview.airtel.databinding.ActivityWifiHeatmapBinding;
import com.newmview.wifi.helper.Interfaces;
import com.newmview.wifi.other.Config;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;
import com.newmview.wifi.sensors.StepDetector;
import com.newmview.wifi.viewmodel.DialogViewModel;
import com.newmview.wifi.viewmodel.HistoryViewModel;
import com.newmview.wifi.viewmodel.MainViewModelFactory;
import com.newmview.wifi.viewmodel.MapVM;
import com.newmview.wifi.viewmodel.WifiViewModel;
import com.services.ActivityRecognitionService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.newmview.wifi.other.Config.downloaded_root;
import static com.newmview.wifi.other.Utils.checkForGps;
import static com.newmview.wifi.other.WifiConfig.getColorForLinkSpeed;
import static com.newmview.wifi.other.WifiConfig.getColorForWifiSignalStrength;


public class CreateWalkMapActivity extends AppCompatActivity implements View.OnClickListener, StepDetector.StepDetectorListener, MyAlertDialog.AlertDialogInterface {
    private static final String TAG = "WifiHeatMapActivity";
    private static  String MODE = "MANUAL";
    private static  boolean SIGNAL_STRENGTH_FLAG;
    
    private Bitmap bitmap;
    private Bitmap bmp;
    private ImageView iv;
    private String msg = "DD";
    private String path,flatType,opening,openingType,componentType;
    private Bitmap bmOverlay;
    private DB_handler db_handler;
    private ActivityWifiHeatmapBinding heatmapBinding;
    private final Handler handler = new Handler();
    private final long delay = 5;//seconds
    private Runnable runnable;
    private WifiManager mWifiManager;
    private WifiViewModel viewModel;
    private ArrayList<Object> mScanResults;
    private boolean saved;
    private Map<Float, Integer> colors=new ArrayMap<>();
    private String filepath;
    private String connectedSSID;
    private int pos;
    private String mapId;
    private int wifiCoordsX;
    private int wifiCoordsY;
    private List<MapModel> mapList;
    private Compass compass;
    private float currentAzimuth;
    private ShowAlertRxr showAlertRxr;
    private RedrawRxr redrawRxr;
    private StepDetector stepDetector;
    private long steps;
    private boolean newStepDetected;
    private ImageButton imgBtn;
    public static Activity cwmap;
    private String flowType;
    private VideoSpeedtest_fragment videospeedtest_fragment;
    private FragmentManager fragmentManager;
    private Dwnld_upload_fragment dwnld_upload_fragment;
    private DialogViewModel alertDialogViewModel;
    private BroadcastReceiver broadcastReceiver;
    public static boolean WALKING;
    private String pointsString,name;
    private boolean walkWarningIgnored;
    private List<WifiHeatMapPoints> list;
    private String walkMapPath;
    private int ss;
    private MapVM mapModel;
    private HistoryViewModel labelsHistoryVM;
    private ArrayList<HistoryModel> labelsHistory;
    private AlertType alertType;
    private DialogViewModel historyAlertVM;
    private String label;
    private String cardDirect;
    private String lastDirect;
    private ArrayList<String> directionList=new ArrayList<>();
    private ArrayList<Float> azimuthList=new ArrayList<Float>();
    private boolean run;
    private int count;
    private String newDire;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        implementMenuButton(menu);

        return true;
    }
    private void initializeHistoryVM() {
        Bundle bundle=new Bundle();
        bundle.putString("tag","label_type");
        labelsHistoryVM=new ViewModelProvider(this,new MainViewModelFactory(bundle)).get(HistoryViewModel.class);
        labelsHistoryVM.getHistoryObservable().observe(CreateWalkMapActivity.this, new Observer<ArrayList<HistoryModel>>() {
            @Override
            public void onChanged(ArrayList<HistoryModel> labelsHistory) {
                CreateWalkMapActivity.this.labelsHistory=labelsHistory;
            }
        });
    }


    private void implementMenuButton(Menu menu) {
        getMenuInflater().inflate(R.menu.more_options_menu, menu);
        MenuItem moreOptions = menu.findItem(R.id.more_options);
        moreOptions.setActionView(R.layout.more_options_layout);
        ImageView moreMenu = menu.findItem(R.id.more_options).getActionView().findViewById(R.id.menu);
        //      ImageView moreMenu = menu.findItem(R.id.more_options).getActionView().findViewById(R.id.menu);
        moreMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    CommonAlertDialog.showPopup(v, CreateWalkMapActivity.this, R.menu.walk_map_more_menu, new Interfaces.onMenuButtonClickListener() {
                        @Override
                        public void onMenuButtonClicked(MenuItem v) {
                            performMenuItemsClickOperation(v);
                        }
                    });
                }


            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      /*  StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());*/
        heatmapBinding = DataBindingUtil.setContentView(this, R.layout.activity_wifi_heatmap);
        getBundleExtras();
        init();
        initializeDialogVM();
        initializeHistoryVM();
       // cwmap=this;
        mapModel=new ViewModelProvider(this,new MainViewModelFactory()).get(MapVM.class);
        Utils.showAlert(false, Constants.CREATE_WALKMAP, CreateWalkMapActivity.this, null);
 registerRxrs();

        startTracking();
        setTimer();
    }


    private void registerRxrs() {
        if(showAlertRxr==null) {
            showAlertRxr = new ShowAlertRxr();
            LocalBroadcastManager.getInstance(this).registerReceiver(showAlertRxr, new IntentFilter(FinalWifiHeatMapActivity.ACTIVITY_FINISHED));
        }
        if(redrawRxr==null) {
            redrawRxr = new RedrawRxr();
            LocalBroadcastManager.getInstance(this).registerReceiver(redrawRxr,
                    new IntentFilter(FinalWifiHeatMapActivity.REDRAW_WALKMAP));
        }
        registerRxrForActivityRecognition();
    }

    private void registerRxrForActivityRecognition()
    {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.BROADCAST_DETECTED_ACTIVITY)) {
                    int type = intent.getIntExtra("type", -1);
                    int confidence = intent.getIntExtra("confidence", 0);
                //   Utils.showToast(CreateWalkMapActivity.this,"Event rxd at "+Utils.getDateTime() +"  ");
                    Log.i(TAG,"Event rxd at "+Utils.getDateTime() +" Confidence "+confidence);
                    if(confidence>40) {
                        handleUserActivity(type, confidence);
                    }

                }
            }
        };
    }
    private void handleUserActivity(int type, int confidence) {


        switch (type) {
            case DetectedActivity.IN_VEHICLE: {
                Utils.showToast(CreateWalkMapActivity.this,"Moving");
                WALKING=true;
               // Utils.showToast(CreateWalkMapActivity.this,"In Vehicle");
                break;
            }
            case DetectedActivity.ON_BICYCLE: {
                Utils.showToast(CreateWalkMapActivity.this,"Moving");
                WALKING=true;
                break;
            }
            case DetectedActivity.ON_FOOT: {
                Utils.showToast(CreateWalkMapActivity.this,"Moving");
                WALKING=true;
                break;
            }
            case DetectedActivity.RUNNING: {
                Utils.showToast(CreateWalkMapActivity.this,"Moving");
                WALKING=true;
                break;
            }
            case DetectedActivity.STILL: {
               Utils.showToast(CreateWalkMapActivity.this,"Not Moving");
                WALKING=false;
                break;
            }
            case DetectedActivity.TILTING: {
               Utils.showToast(CreateWalkMapActivity.this,"Not Moving");
                WALKING=false;
                break;
            }
            case DetectedActivity.WALKING: {
                Utils.showToast(CreateWalkMapActivity.this,"Moving");
                WALKING=true;
                break;
            }
            case DetectedActivity.UNKNOWN: {
               // Utils.showToast(CreateWalkMapActivity.this,"Unknown");
              WALKING=false;
                break;
            }
        }
        heatmapBinding.displayIv.setWalkingFlag(WALKING);

      //  Log.e(TAG, "User activity: " +  + ", Confidence: " + confidence);


    }
    private void startTracking() {
        Intent intent = new Intent(CreateWalkMapActivity.this, ActivityRecognitionService.class);
        startService(intent);
    }

    private void stopTracking() {
        Intent intent = new Intent(CreateWalkMapActivity.this, ActivityRecognitionService.class);
        stopService(intent);
    }
    private void initializeDialogVM() {
        alertDialogViewModel = DialogManager.initializeViewModel(this);
        historyAlertVM=DialogManager.initializeViewModel(this);
        DialogManager.initializeViewModel(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(showAlertRxr!=null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(showAlertRxr);
            showAlertRxr=null;
        }
        if(redrawRxr!=null)
        {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(redrawRxr);
            redrawRxr=null;
        }
        stopTracking();
        WALKING=false;

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
      /*  if(showAlertRxr==null) {
            showAlertRxr = new ShowAlertRxr();
            LocalBroadcastManager.getInstance(this).registerReceiver(showAlertRxr, new IntentFilter(FinalWifiHeatMapActivity.ACTIVITY_FINISHED));
        }*/
        ss=0;
        checkForGps(CreateWalkMapActivity.this);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 90);
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 87);
        } else {
            refreshData();

        }
        compass.start();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY));
       // stepDetector.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        compass.stop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    //   stepDetector.stop();
    }

    private void init() {

        db_handler = new DB_handler(MviewApplication.ctx);
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        viewModel = new ViewModelProvider(this,
                new MainViewModelFactory(CreateWalkMapActivity.this, mWifiManager)).get(WifiViewModel.class);
        heatmapBinding.setViewModel(new WifiModel());
        bitmap = setMapToImage();
        fragmentManager=getSupportFragmentManager();
        setToolbar();
        setUpCompass();
     /*   if(hasGotSensorCaps())
        {
            setUpStepDetector();
        }*/
        getExtras();
        //Log.i(TAG,"Coord x "+wifiCoordsX +"coordy "+wifiCoordsY);
        if (bitmap != null) {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(bitmap, new Matrix(), null);
            heatmapBinding.displayIv.setImageBitmap(bitmap);

        }
        heatmapBinding.displayIv.setVisibility(View.VISIBLE);
        db_handler.open();
        mapList= db_handler.readMapDataForMapId(mapId);
        if(mapList!=null) {
            if(mapList.size()>0) {
                wifiCoordsY = (int) Float.parseFloat(mapList.get(0).getWifiY());
                wifiCoordsX = (int) Float.parseFloat(mapList.get(0).getWifiX());
            }
        }
        db_handler.close();
if(mapList.size()>0) {
    heatmapBinding.displayIv.setWifiCoords(mapList.get(0).getWifiX(), mapList.get(0).getWifiY());
}
        int windowwidth = getWindowManager().getDefaultDisplay().getWidth();
        int windowheight = getWindowManager().getDefaultDisplay().getHeight();
        CanvasDrawElements canvasDrawElements = new CanvasDrawElements(CreateWalkMapActivity.this);
        //  heatmapBinding.previewIv.callTodraw(true);
        setClickListeners();
        viewModel.getWifiDetailsObservable().observe(CreateWalkMapActivity.this, new Observer<List<WifiModel>>() {
            @Override
            public void onChanged(List<WifiModel> scanResults) {
                setConnectedWifiDetails(scanResults);
                if(Utils.checkifavailable(connectedSSID))
                {

                }
            }
        });

    }

    private void setUpStepDetector() {
        stepDetector=new StepDetector(this);
        stepDetector.setListener(this);

    }
    // Method to check that the running device has the required capability to
    // perform step detection.
    public boolean hasGotSensorCaps()
    {
        PackageManager pm = getPackageManager();

        // Require at least Android KitKat

        int currentApiVersion = Build.VERSION.SDK_INT;

        // Check that the device supports the step counter and detector sensors

        return currentApiVersion >= 19
                && pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR);
    }
    private void setUpCompass() {
        compass = new Compass(this);
        Compass.CompassListener cl = new Compass.CompassListener() {
            @Override
            public void onNewAzimuth(float azimuth) {

                adjustArrow(azimuth);
            }
        };
        compass.setListener(cl);
    }
    private void adjustArrow(float azimuth) {
       /* Log.d(TAG, "will set rotation from " + currentAzimuth + " to "
                + azimuth);
*/
        Animation animator = new RotateAnimation(-currentAzimuth, -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        currentAzimuth = azimuth;
        String display = (int) currentAzimuth + "";


       /* if (currentAzimuth == 0 || currentAzimuth == 360)
            cardDirect = "N";*/

        if (currentAzimuth >= 0 && currentAzimuth <=23) {//changed by Sonal\// Log.i(TAG,"Degree check 1 ");
            cardDirect = "N";
            newDire="NN";
        }
        else if (currentAzimuth > 23 && currentAzimuth <=67) {
           // Log.i(TAG,"Degree check 1 "+ currentAzimuth);
            cardDirect = "NE";
            newDire="NE";

        }
        else if (currentAzimuth >68 && currentAzimuth <=112) {
           // Log.i(TAG,"Degree check 2 "+currentAzimuth);
            cardDirect = "E";
            newDire="E";
        }
        else if (currentAzimuth > 112 && currentAzimuth <=157) {
           // Log.i(TAG,"Degree check 3 "+currentAzimuth);
            cardDirect = "SE";
            newDire="SE";

        }
        else if (currentAzimuth >157 && currentAzimuth<=202) {
           // Log.i(TAG,"Degree check 4 "+currentAzimuth);
            cardDirect = "S";
            newDire="SS";

        }
        else if (currentAzimuth >202 && currentAzimuth <= 247) {
           // Log.i(TAG,"Degree check 5 "+currentAzimuth);
            cardDirect = "SW";
            newDire="SW";

        }
        else if (currentAzimuth >247 && currentAzimuth<=292) {
           // Log.i(TAG,"Degree check 6 "+currentAzimuth);
            cardDirect = "W";
            newDire="WW";

        }
        else if (currentAzimuth > 292 && currentAzimuth <= 337) {
           // Log.i(TAG,"Degree check 7 "+currentAzimuth);
            cardDirect = "NW";
            newDire="NW";


        }
        else if(currentAzimuth>337 && currentAzimuth<=360) {
           // Log.i(TAG,"Degree check 8 "+currentAzimuth);
            cardDirect = "N";
            newDire="NN";
        }
        else {
           // Log.i(TAG,"Degree check 9 "+currentAzimuth);
            cardDirect = "Unknown";
        }
/*
        if (currentAzimuth == 0 || currentAzimuth == 360)
            newDire = "NN";
        else if (currentAzimuth > 0 && currentAzimuth < 90)
            newDire = "NE";
        else if (currentAzimuth == 90)
            newDire = "EE";
        else if (currentAzimuth > 90 && currentAzimuth < 180)
            newDire = "SE";
        else if (currentAzimuth == 180)
            newDire = "SS";
        else if (currentAzimuth > 180 && currentAzimuth < 270)
            newDire = "SW";
        else if (currentAzimuth == 270)
            newDire = "WW";
        else if (currentAzimuth > 270 && currentAzimuth < 360)
            newDire = "NW";
        else
            newDire = "Unknown";*/
       /* if(directionList.size()>0) {
            String lastdir = directionList.get(directionList.size()-1);
            if(cardDirect.equalsIgnoreCase(lastdir))
            {

            }
            else
            {
                directionList.add(cardDirect);
            }
        }
        else
        {*/
        azimuthList.add(azimuth);
            directionList.add(newDire);
    //    }

      //  degreeView.setText(display + "°" + " " + cardDirect);
        heatmapBinding.directionTv.setText(display + "°" + " " + cardDirect);

        animator.setDuration(3000);
        animator.setRepeatCount(0);
        animator.setFillAfter(true);

        heatmapBinding.compassIv.startAnimation(animator);
       
    }
public boolean getWalkingFlag()
{
    return WALKING;
}
    private void getExtras() {

            mapId=getIntent().getStringExtra("mapId");

    }

    private void setConnectedWifiDetails(List<WifiModel> scanResults) {
        if (scanResults != null) {
            mScanResults = new ArrayList<>();
            boolean connectedFlag = false;
            String details = null;
            SpannableStringBuilder spannableStringBuilder = null;
            int linkSpeed=0;int color=0;int linkSpeedColor=0;
            if (scanResults.size() > 0) {
                for (int i = 0; i < scanResults.size(); i++) {

                    boolean isConnected = scanResults.get(i).isConnected();
                    String ssidName = scanResults.get(i).getSsidName();
                     linkSpeed = scanResults.get(i).getLinkSpeed();
                    int signalStrength = scanResults.get(i).getSignalStrength();
                     color = getColorForWifiSignalStrength(signalStrength);
                     linkSpeedColor=getColorForLinkSpeed(linkSpeed);

                   /* Log.i(TAG, "Scan Results changed..." + ssidName + " Link speed " + linkSpeed +
                            "  ss " + signalStrength);*/
                    if (!Utils.checkifavailable(ssidName)) {
                        ssidName = "Unknown";
                    }

                    if (isConnected) {
                        this.connectedSSID=ssidName;
                        connectedFlag = true;
                     //   details = "WiFi (Link " + linkSpeed + "Mbps, " + ssidName + ", Signal level " + signalStrength + "dBm)";
                        details = signalStrength + "dBm";
                        /*try {

                            spannableStringBuilder = new SpannableStringBuilder(details);
                            spannableStringBuilder.setSpan(new BackgroundColorSpan(color),
                                    details.indexOf("Signal"), details.indexOf(")"), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                            if (color == Color.BLUE) {
                                spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.WHITE),
                                        details.indexOf("Signal"), details.indexOf(")"), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/
                        break;

                    }

                }
                if (connectedFlag) {


                    if (Utils.checkifavailable(details)) {
                        //if (spannableStringBuilder != null) {
                            setValues(details,linkSpeed,color,linkSpeedColor);
                        //    heatmapBinding.wifiDetailsTv.setText(spannableStringBuilder);
                       /* } else {
                          *//*  setValues(spannableStringBuilder,linkSpeed);
                            heatmapBinding.wifiDetailsTv.setText(details);*//*
                        }*/
                    } else {
                        heatmapBinding.wifiDetailsTv.setText("Please check your wifi connectivity!");
                    }
                } else {
                    heatmapBinding.wifiDetailsTv.setText("Please check your wifi connectivity!");
                }
            }
        }
    }

    private void setValues(String details, int linkSpeed, int color,int linkSpeedColor) {
        heatmapBinding.ssidTv.setText(connectedSSID);
        heatmapBinding.userIdv.setText(Utils.getMyContactNum(CreateWalkMapActivity.this));
        heatmapBinding.surveyIdTv.setText(mapId);
        if(color==0)
            heatmapBinding.ssTv.setTextColor(Color.BLACK);
        else
        heatmapBinding.ssTv.setTextColor(color);
        heatmapBinding.ssTv.setText(details);
        if(linkSpeedColor==0)
            heatmapBinding.linkSpeedTv.setTextColor(Color.BLACK);
        else
            heatmapBinding.linkSpeedTv.setTextColor(linkSpeedColor);
        heatmapBinding.linkSpeedTv.setText(linkSpeed+"Mbps");
        heatmapBinding.timeStampTv.setText(Utils.getDateTime());

    }

    private void setClickListeners() {
        heatmapBinding.uploadBtn.setOnClickListener(this);
        heatmapBinding.saveBtn.setOnClickListener(this);
        heatmapBinding.shareBtn.setOnClickListener(this);
        heatmapBinding.viewMapBtn.setOnClickListener(this);
        heatmapBinding.moreOptionsBtn.setOnClickListener(this);

    }

    private void setToolbar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ColorDrawable colorDrawable
                = new ColorDrawable(getResources().getColor(R.color.white));

        // Set BackgroundDrawable
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_app_icon);

    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Log.i(TAG,"Saved "+saved);
        if(fragmentManager.getBackStackEntryCount()==0)
              finish();
        else {
            fragmentManager.popBackStack();
            getSupportActionBar().setTitle(getResources().getString(R.string.walk_test));

        }
        /*if(!saved) {
            heatMapImagePopup();
        }
        else
        {
            finish();
        }*/
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:

                /*  NavUtils.navigateUpFromSameTask(this);*/

                Log.i(TAG,"Saved "+saved);
               /* if(!saved) {
                    heatMapImagePopup();
                }
                else
                {*/
                    onBackPressed();
               // }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private Bitmap setMapToImage() {
        if(path!=null)
        {
        File imgFile = new File(path);

        if (imgFile.exists()) {
            java.io.FileInputStream in = null;
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

                return mutableBitmap;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        }
        return null;
    }

    private void getBundleExtras() {
        Bundle bundle = getIntent().getExtras();
        path = bundle.getString("floorMapPath");
        flatType=bundle.getString("flatType");
        componentType=bundle.getString("componentType");
        opening=bundle.getString("opening");
        openingType=bundle.getString("openingType");
        flowType=bundle.getString("flowType");
       /* wifiCoordsX=bundle.getString("wifiCoordsX");
        wifiCoordsY=bundle.getString("wifiCoordsY");

*/

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.moreOptionsBtn:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    CommonAlertDialog.showPopup(v, CreateWalkMapActivity.this, R.menu.walk_map_more_menu, new Interfaces.onMenuButtonClickListener() {
                        @Override
                        public void onMenuButtonClicked(MenuItem v) {
                            performMenuItemsClickOperation(v);
                        }
                    });
                }
                break;
           /* case R.id.wifiListBtn:
                Bundle bundle=new Bundle();
                bundle.putString("flowType",flowType);
                Utils.takeToNextActivity(this,WifiActivity.class,bundle);
                break;*/
            case R.id.viewMapBtn:
                checkPermissionsAndSave();

                break;
            case R.id.shareBtn:

                String textToShare=
                        "SSID : "+connectedSSID +", \n"+"Time Stamp : "+Utils.getDateTime()+", \n"+"Survey Id : "+mapId +", \n"+"User Id : "+Utils.getMyContactNum(CreateWalkMapActivity.this);
                Utils.sendImage(saved, CreateWalkMapActivity.this,
                        heatmapBinding.displayIv,filepath,false,null,null,mapId,textToShare);
               // sendImage();
                break;

            case R.id.uploadBtn:
              /* Intent allIntent = new Intent(MviewApplication.ctx, Upload_service.class);
                startService(allIntent);*/
                break;
            case R.id.undoIv:
                break;
            case R.id.saveBtn:
                checkPermissionsAndSave();
                break;
        }
    }
    private void setTimer() {
        Log.i(TAG,"ok mode "+MODE);
        if(MODE.equalsIgnoreCase("Auto")) {
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    //your method
                 //   if (WALKING) {
                    if(!run)
                    {
                    run=true;
                        WifiHeatMapPoints wifiHeatMapPoint = heatmapBinding.displayIv.getTouchedPoint();
                        if (wifiHeatMapPoint != null) {
                            if (directionList.size() > 0) {
                                try {
                                    count++;

                                    Log.i(TAG, "ok Last dir " + directionList.get(directionList.size() - 2) + "current direc " +
                                            "" + directionList.get(directionList.size() - 1) + " wifi x " + wifiHeatMapPoint.getPoints().get(0).x + " y " +
                                            wifiHeatMapPoint.getPoints().get(0).y);
                                   /* double diff=azimuthList.get(azimuthList.size()-2)-azimuthList.get(azimuthList.size()-1);

                                    double absDif=Math.abs(diff);
                                    double finalDif=0.0;
                                    if(absDif<0)
                                     finalDif=absDif*1000;
                                    else finalDif=absDif;
                                    int finalIntDiff=(int)finalDif;
*/
                                  //  int  finalDiif=(absDif*1000);
                                   // Log.i(TAG,"Diff here is "+diff +" abs diff "+absDif + " int diff "+finalIntDiff +" float diff "+finalDif);

                                    Point point = DirectionPath.getNextPoint(directionList.get(directionList.size() - 1),
                                            directionList.get(directionList.size() - 2), wifiHeatMapPoint.getPoints().get(0), 10);
                                  if(point!=null) {
                                      Log.i(TAG, "ok New point x " + point.x + " y " + point.y);
                                      heatmapBinding.displayIv.setNextPoint(point);
                                     // heatmapBinding.displayIv.addLabelAtLastPoint("("+  count+") ");
                                  }
                                    //lastDirect=cardDirect;
                                    directionList.clear();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                        run=false;
                        }
                    }
               // }
            }, 2000, 4000);//put here time 1000 milliseconds=1 second
        }
    }

    private void performMenuItemsClickOperation(MenuItem v) {
        switch (v.getItemId())
        {
            case R.id.autoMode:
                MODE="AUTO";
                heatmapBinding.displayIv.clearData();
                Utils.showLongToast(CreateWalkMapActivity.this,"Please tap on a location from where you want to start your survey");
setTimer();
                break;
            case R.id.manualMode:
                MODE="MANUAL";
                break;
            case R.id.shareItem:
                String textToShare=
                        "SSID : "+connectedSSID +", \n"+"Time Stamp : "+Utils.getDateTime()+", \n"+"Survey Id : "+mapId +", \n"+"User Id : "+Utils.getMyContactNum(CreateWalkMapActivity.this);
                Utils.sendImage(saved, CreateWalkMapActivity.this,
                        heatmapBinding.displayIv,filepath,false,null,null,mapId,textToShare);
                break;
            case R.id.vdoSpeedTestItem:
                videospeedtest_fragment=new VideoSpeedtest_fragment();
                loadFragment(videospeedtest_fragment,R.string.videotest_title);

                break;
            case R.id.dwnldSpeedTestItem:
                MainActivity.ud="download";
                dwnld_upload_fragment =new Dwnld_upload_fragment();
                loadFragment(dwnld_upload_fragment,R.string.download);

                break;
            case R.id.uploadSpeedTestItem:
                MainActivity.ud="upload";
                dwnld_upload_fragment =new Dwnld_upload_fragment();
                loadFragment(dwnld_upload_fragment,R.string.upload);
                break;
            case R.id.wifiListItem:
                Bundle bundle=new Bundle();
                bundle.putString("flowType",flowType);
                Utils.takeToNextActivity(this,WifiActivity.class,bundle);
                break;
            case R.id.addLabelItem:
                alertType=AlertType.labelsAlert;
                DialogManager.showMyDialog(CreateWalkMapActivity.this,
                        AlertType.labelsAlert, alertDialogViewModel, this, 
                        null, null, null, labelsHistory, "OK", "Cancel");

/*
                Utils.showAlert(true, "Add your label you want to add at last point.", CreateWalkMapActivity.this, new Interfaces.DialogButtonClickListener() {
                    @Override
                    public void onPositiveButtonClicked(String text) {
                        if(Utils.checkifavailable(text)) {
                            heatmapBinding.displayIv.addLabelAtLastPoint(text);
                        }
                    }

                    @Override
                    public void onNegativeButtonClicked(String text) {

                    }

                    @Override
                    public void onDialogDismissed(String text) {

                    }
                });
*/
                break;
        }
    }

    private void loadFragment(Fragment fragment, int title) {
        fragmentManager.beginTransaction().replace(R.id.mainFrame,fragment).addToBackStack("speed_test").commit();
        getSupportActionBar().setTitle(title);
        Config.onbackpress="speedtest";
    }

    private void checkPermissionsAndSave() {
        ss=0;
        String finalPath = null;
        Log.i(TAG,"Step 1");
        PackageManager pm = CreateWalkMapActivity.this.getPackageManager();
        int hasPerm = pm.checkPermission(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                CreateWalkMapActivity.this.getPackageName());
        if (hasPerm != PackageManager.PERMISSION_GRANTED) {
            // do stuff
            Log.i(TAG,"Step 2");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE

            }, 1);
        } else {
            Log.i(TAG,"Step 3");
            bitmap = Utils.getViewBitmap(heatmapBinding.displayIv);
            String fileName="WalkMap_"+Utils.getDateTime()+".png";
            Utils.saveContent(bitmap, CreateWalkMapActivity.this, false, "", new Interfaces.SaveSuccessfullListener() {
                @Override
                public void saveSuccessfull(String name, String path) {
                     CreateWalkMapActivity.this.name=name;
                     CreateWalkMapActivity.this.walkMapPath=path;
                      db_handler.open();
                     list=heatmapBinding.displayIv.getHeatMapDetails();
                     Comparator<WifiHeatMapPoints> comparator = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        List<Integer> xpoints=new ArrayList<>();
                        List<Integer> ypoints=new ArrayList<>();
                        if(list!=null)
                        {
                            if(list.size()>0)
                            {
                                  for(int i=0;i<list.size();i++)
                                   {
                                       ss=ss+list.get(i).getSignalStrength();
                                       xpoints.add(list.get(i).getPoints().get(0).x);
                                       ypoints.add(list.get(i).getPoints().get(0).y);

                                   }
                                int avg=ss/list.size();
                                comparator = Comparator.comparing( WifiHeatMapPoints::getSignalStrength );


                                WifiHeatMapPoints maxSSPoint=  list.stream().max(comparator).get();
                                WifiHeatMapPoints minSSPoint=  list.stream().min(comparator).get();

                              /*  Integer maxX = xpoints
                                        .stream()
                                        .mapToInt(v -> v);
                                Integer maxY=ypoints
                                        .stream()
                                        .mapToInt(v -> v);*/
                                if(xpoints.size()>0 && ypoints.size()>0) {
                                    Integer maxNumberY = ypoints.stream().max(Integer::compare).get();
                                    Integer minNumberY = ypoints.stream().min(Integer::compare).get();
                                    Integer maxNumberX = xpoints.stream().max(Integer::compare).get();
                                    Integer minNumberX = xpoints.stream().min(Integer::compare).get();

                                    Log.i(TAG,"Max x "+maxNumberX +" Max Y "+maxNumberY +" min x "+minNumberX +" max X " +
                                            ""+maxNumberX);
                                    int minXdiff=Math.abs(minNumberX-CoordsBuilder.startX);
                                    int maxXdiff=Math.abs(CoordsBuilder.endX - maxNumberX);
                                    int minYdiff=minNumberY - CoordsBuilder.startY;
                                    int maxYdiff=CoordsBuilder.endY - maxNumberY;
                                    /*Log.i(TAG,"Max x "+maxNumberX +" Max Y "+maxNumberY +" min x "+minNumberX +" max X " +
                                            ""+maxNumberX +" minXdiff "+minXdiff +" maxDiffx "+maxXdiff +" minYdiff "+minYdiff
                                            +" maxYdiff "+maxYdiff);*/
                                    if (minNumberY - CoordsBuilder.startY > 200 || CoordsBuilder.endY - maxNumberY > 200
                                            || CoordsBuilder.endX - maxNumberX > 200 || minNumberX - CoordsBuilder.startX > 200) {
                                        Utils.showLongToast(CreateWalkMapActivity.this, "Partial walk test!");
                                    }
                                }

                                 /* points.stream()
                                        .filter(p -> p.x < CoordsBuilder.endX)
                                        .filter(p -> p.y<)
                                        .max(comparator)
                                        .

                                WifiHeatMapPoints minX=list.stream().m*/
                                int minDiff=avg-minSSPoint.getSignalStrength();
                                int maxDiff=maxSSPoint.getSignalStrength()-avg;
                                int absMin=Math.abs(minSSPoint.getSignalStrength());
                                int absMax=Math.abs(maxSSPoint.getSignalStrength());
                                        int diff=absMax-absMin;
                                        int absDiff=Math.abs(diff);
                                if(absDiff>=15)
                                {
                                    SIGNAL_STRENGTH_FLAG=true;
                                }
/*
                                Utils.showToast(CreateWalkMapActivity.this,
                                        " max ss "+
                                                maxSSPoint.getSignalStrength()
                                                +" min ss "+minSSPoint.getSignalStrength()
                                                +" abs max "+absMax +" abs min "+absMin +" diff "+diff);
*/

/*
                              Utils.showToast(CreateWalkMapActivity.this,
                                       "Avg is "+avg +" max ss "+
                                               maxSSPoint.getSignalStrength()
                                               +" min ss "+minSSPoint.getSignalStrength()
                                               +" maxdiff "+maxDiff +" mindiff "+minDiff);
*/
                             /*   if(minDiff>20 && maxDiff>20)
                                {
                                    SIGNAL_STRENGTH_FLAG=true;
                                }
*/


                            }
                        }

                    }

                   /* int maxSS = list.stream()

                            .max(comparator)
                            .get();*/

                    Gson gson=new Gson();
                     pointsString=gson.toJson(list);
                    if(WALKING || SIGNAL_STRENGTH_FLAG)
                    {
                        walkWarningIgnored=false;
                        Log.i(TAG,"Sending points list size "+list.size());
                        db_handler.updateWalkMapPath(path,mapId,name,pointsString,walkWarningIgnored);
                        viewFinalMap(name,path,list);
                    }
                    else
                    {
                        // by swapnil bansal 09/01/2023
                        sendInvalidSurveyEvent(mapId);
                        DialogManager.showMyDialog(CreateWalkMapActivity.this,
                                AlertType.confirmationAlertDialog, alertDialogViewModel, CreateWalkMapActivity.this, 
                                Constants.NO_WALK_WARNING, "Invalid Survey", null, null,
                                "OK", null);

                    }
                   
                }
            },fileName);

        }
    }

    // by swapnil bansal 09/01/2023
    private void sendInvalidSurveyEvent(String event) {
        DB_handler db_handler = new DB_handler(MviewApplication.ctx);
        db_handler.open();

        try{
            // TODO Auto-generated method stub
            JSONObject jsonobj = new JSONObject();
            jsonobj.put("evt_type","Invalid_walk_map_survey");
            jsonobj.put("desc","the survey id is - " +event);
            System.out.println(" event is map is"+event);
            jsonobj.put("date_time", Config.getDateTime());
            jsonobj.put("additional_info", " ");
            db_handler.insertInLoggingTable("","Invalid_walk_map_survey",jsonobj.toString(),Config.getDateTime(),"init");
        }
        catch (JSONException e1) { // TODO

            e1.printStackTrace();
        }

    }
    private void viewFinalMap(String name, String path, List<WifiHeatMapPoints> list) {
        if(Utils.checkifavailable(label)) {
            HistoryModel historyModel = new HistoryModel();
            historyModel.setTag("label_type");
            historyModel.setValue(label);
            labelsHistoryVM.insertNewEntry(historyModel);
        }
        WALKING=false;SIGNAL_STRENGTH_FLAG=false;
        Bundle bundle=new Bundle();

        Intent intent=new Intent(CreateWalkMapActivity.this,FinalWifiHeatMapActivity.class);
        bundle.putParcelableArrayList("pointsList", (ArrayList<? extends Parcelable>) list);
        bundle.putString("path", this.path);
        intent.putExtra("flatType",flatType);
        intent.putExtra("componentType",componentType);
        intent.putExtra("opening",opening);
        intent.putExtra("openingType",openingType);
        intent.putExtra("ssidName",connectedSSID);
        intent.putExtra("mapId",mapId);
        intent.putExtra("walkMapPath",path);
        intent.putExtra("wifiCoordsX",wifiCoordsX);
        intent.putExtra("wifiCoordsY",wifiCoordsY);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void sendImage() {

        ;
        //  String   finalPath= saveContent(bitmap);
       /* bitmap = Utils.getViewBitmap(heatmapBinding.displayIv);
        Uri finalUri=Utils.getImageUri(this,bitmap);*/
        if(!saved) {
            bitmap = Utils.getViewBitmap(heatmapBinding.displayIv);
            saveContent(bitmap);
        }
        if(filepath!=null) {
            Uri finalUri = Uri.fromFile(new File(filepath));
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
            intent.putExtra(Intent.EXTRA_STREAM,
                    FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider",
                            new File(filepath)));
            intent.putExtra(Intent.EXTRA_STREAM, finalUri);
            intent.putExtra(Intent.EXTRA_TEXT, "WiFi-Map");
            startActivity(Intent.createChooser(intent, "Share Screenshot"));
        }


    }

    public void insertImageInDb(String filename, File file, String filepath) {
        long length = filename.length();
        Log.i(TAG, "File Path : " + file.getPath() + ", File size : " + length + " KB");
        db_handler.open();
        boolean boolForImage = true;
        boolForImage = db_handler.insertInHeatMapTable(file.getPath(), Utils.getRandomID(), filename, Config.getDateTime(), "", "", "image", String.valueOf(length), "", "1", "init");
        if (boolForImage) {
            Utils.showAlert(false, "Image saved successfully!", CreateWalkMapActivity.this, null);
            //   Utils.showAlert("Image saved successfully!", WifiHeatMapActivity.this, filepath, "Cancel");
        }
        db_handler.close();


    }

    private String saveContent(Bitmap bitmap) {
        /* File downloaded_root= new File(WifiHeatMapActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                 Config.directoryname+File.separator+"heat_map");*/
        List<WifiHeatMapPoints> points= heatmapBinding.displayIv.getHeatMapDetails();
        FileOutputStream ostream = null;

        try {
            if (!downloaded_root.exists()) {
                downloaded_root.mkdirs();
            }
            File SDCardRoot = downloaded_root.getAbsoluteFile();

            Log.i(TAG,"Step 4");
            String filename = Utils.getRandomString() + ".png";

            Log.i("DownloadLocal filename:", "" + filename);
            File file = new File(SDCardRoot, filename);
          /*  if (!file.createNewFile()) {
                file.createNewFile();
            }*/
            Log.i(TAG,"Step 5");
            String path = SDCardRoot + "/" + filename;
            ostream = new FileOutputStream(path);
            if (bitmap == null) {
                Log.i(TAG, "NULL bitmap save\n");
            }
            Log.i(TAG,"Step 6");
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, ostream);
            filepath = file.getPath();
            saved=true;
            Utils.showAlert(false, "Image saved successfully to your gallery!", CreateWalkMapActivity.this, null);
            /*heatmapBinding.heatmap.setVisibility(View.VISIBLE);
            heatmapBinding.heatmap.setMinimum(-90);
            heatmapBinding.heatmap.setMaximum(0);
            heatmapBinding.heatmap.setRadius(1000);*/
            //make the colour gradient from pink to yellow

/*
            if(points!=null) {
                Log.i(TAG,"Poins size "+points.size());
                int color2 = doGradient(4 ,0, 100, 0xff00ff00, 0xffff0000);
                for (int i = 0; i < points.size(); i++) {
                    float stop = ((float)i) / 20.0f;
                    WifiHeatMapPoints wifiHeatMapPoints = points.get(i);

                }
                for (int i = 0; i < 21; i++) {
                    float stop = ((float)i) / 20.0f;
                    WifiHeatMapPoints wifiHeatMapPoints = points.get(i);
                    // Log.i(TAG,"Stop "+stop);
                    int color = doGradient(i * 5, 0, 100, 0xff00ff00, 0xffff0000);
                   */
/* int color = doGradient(points.get(i).getSignalStrength(), -90,
                            0, 0xff00ff00, 0xffff0000);*//*


                    int color1=wifiHeatMapPoints.getColor();
                    colors.put(stop, color);
                    //colors.put(stop, color);
                }

//heatmapBinding.heatmap.setColorStops(colors);
            }
*/
/*
            if(points!=null)
            {
                for(int i=0;i<points.size();i++)
                {
                    WifiHeatMapPoints wifiHeatMapPoints = points.get(i);
                    FloatPoint p=wifiHeatMapPoints.getFloatPoints().get(0);
                    Random rand = new Random();

                    float width=p.getX()/heatmapBinding.displayIv.getWidth();
                    float height=p.getY()/heatmapBinding.displayIv.getHeight();
                    HeatMap.DataPoint point =
                            new HeatMap.DataPoint(width, height,
                                    points.get(i).getSignalStrength());
                    heatmapBinding.heatmap.addData(point);

                }
            }

           heatmapBinding.displayIv.clearData();*/

            //finish();
            // by SWAPNIL 10/27/2022
            //  insertImageInDb(filename, file, filepath);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG,"Step 7");
        }
        return filepath;

    }

    private float clamp(float value, float min, float max) {
        return value * (max - min) + min;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            bitmap = Utils.getViewBitmap(heatmapBinding.displayIv);
            saveContent(bitmap);
        }
    }

    private void refreshData() {
        mWifiManager.startScan();
        viewModel.refresh(mWifiManager);
      /*  Log.i(TAG,"Running first at "+ Utils.getDateTime());
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                Log.i(TAG,"Running at "+ Utils.getDateTime());
                mWifiManager.startScan();
                viewModel.refresh(mWifiManager);
                // WifiListRepository.getInstance().addDataSource(mWifiManager);
                handler.postDelayed(runnable, delay*1000);

            }
        }, delay*1000);*/

    }

    @Override
    protected void onStop() {
        super.onStop();
        //handler.removeCallbacks(runnable);
    }


    public void refreshOnTouch(WifiHeatMapPoints scanResults) {
        // Log.i(TAG, "Refreshing data");
        if (scanResults != null) {
            mScanResults = new ArrayList<>();
            boolean connectedFlag = false;
            String details = null;
            SpannableStringBuilder spannableStringBuilder = null;
            // if (scanResults.size() > 0) {
            //    for (int i = 0; i < scanResults.size(); i++) {


            String ssidName = scanResults.getSsidName();
            int linkSpeed = scanResults.getLinkSpeed();
            int signalStrength = scanResults.getSignalStrength();
            int color = getColorForWifiSignalStrength(signalStrength);
            int linkSpeedColor=getColorForLinkSpeed(linkSpeed);

            Log.i(TAG, "Scan Results changed..." + ssidName + " Link speed " + linkSpeed +
                    "  ss " + signalStrength);
            if (!Utils.checkifavailable(ssidName)) {
                ssidName = "Unknown";
            }


            connectedFlag = true;
            details = signalStrength + "dBm";
            try {
               /* spannableStringBuilder = new SpannableStringBuilder(details);
                spannableStringBuilder.setSpan(new BackgroundColorSpan(color), details.indexOf("Signal"), details.indexOf(")"), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                if (color == Color.BLUE) {
                    spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.WHITE),
                            details.indexOf("Signal"), details.indexOf(")"), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

                }*/

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (Utils.checkifavailable(details)) {
               // if (spannableStringBuilder != null) {
                    setValues(details,linkSpeed, color,linkSpeedColor);
                //    heatmapBinding.wifiDetailsTv.setText(spannableStringBuilder);
               /* } else {
                 //   heatmapBinding.wifiDetailsTv.setText(details);
                }*/
            } else {
                heatmapBinding.wifiDetailsTv.setText("Please check your wifi connectivity!");
            }


        } else {
            heatmapBinding.wifiDetailsTv.setText("Please check your wifi connectivity!");
        }
    }

    public void heatMapImagePopup()
    {
        {
            android.app.AlertDialog.Builder updatealert = new android.app.AlertDialog.Builder(CreateWalkMapActivity.this);
            updatealert.setTitle(Html.fromHtml("<font color='#FF0000'>Wifi Coverage Map</font>"));
            updatealert.setMessage("Please select your desired option ");
            updatealert.setPositiveButton(Html.fromHtml("<font color='#FF0000'>SAVE</font>"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    bitmap = Utils.getViewBitmap(heatmapBinding.displayIv);
                    saveContent(bitmap);
                }
            });
            updatealert.setNegativeButton(Html.fromHtml("<font color='#FF0000'>DISCARD</font>"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            }).show();

        }

    }

    public void showDistance(String distanceBetweenPoints) {
        heatmapBinding.distanceTv.setText(distanceBetweenPoints +"");
    }
    public boolean newStepDetected()
    {
        long count=stepDetector.getStepsCount();
       // Utils.appendCrashLog(Utils.getDateTime() +" : "+"Old steps "+steps +" new Steps "+count);
       // Log.i(TAG,"Old steps "+steps +" new Steps "+newSteps);
        if(steps==count) {
            newStepDetected = false;
        }
        else
        {
            steps=count;
            newStepDetected=true;
        }
        return newStepDetected;


    }

    @Override
    public void onStepDetected(long newSteps) {
        Log.i(TAG,"Old steps "+steps +" new Steps "+newSteps);
 /* if(steps==newSteps) {
      newStepDetected = false;
  }
  else
  {
      steps=newSteps;
      newStepDetected=true;
  }
  heatmapBinding.displayIv.setNewStepDetection(newStepDetected);*/

    }


    @Override
    public void alertDialogPositiveButtonClicked(AlertType type, Object details) {
        walkWarningIgnored=true;
        db_handler.updateWalkMapPath(walkMapPath,mapId,name,pointsString, walkWarningIgnored);
        alertDialogViewModel.cancelAlert();
        reAttemptSurvey();

    }



    private void reAttemptSurvey() {
        heatmapBinding.displayIv.clearData();

        MapModel mapDetails=mapModel.readDetailsAtMapId(mapId);
        MapModel newMapEntry=new MapModel();
        newMapEntry.setFloorPlan(mapDetails.getFloorPlan());
        newMapEntry.setFloorPlanPath(mapDetails.getFloorPlanPath());
        newMapEntry.setWifiX(mapDetails.getWifiX());
        newMapEntry.setWifiY(mapDetails.getWifiY());
        newMapEntry.setTechnology(mapDetails.getTechnology());
        newMapEntry.setDeviceId(mapDetails.getDeviceId());
        newMapEntry.setFlatType(mapDetails.getFlatType());
        newMapEntry.setSubscriberName(mapDetails.getSubscriberName());
        newMapEntry.setSubscriberId(mapDetails.getSubscriberId());
        long successfull=mapModel.insertNewMap(newMapEntry);
        if(successfull>0) {
            mapModel.refresh();
        }
        int mapIdInt=Integer.parseInt(mapId);
        int newMapIdInt=mapIdInt+1;
        mapId=newMapIdInt+"";
        heatmapBinding.surveyIdTv.setText(mapId);
        walkWarningIgnored=false;

    }

    @Override
    public void alertDialogNegativeButtonClicked(AlertType type) {
      /* walkWarningIgnored=true;
        db_handler.updateWalkMapPath(walkMapPath,mapId,name,pointsString, walkWarningIgnored);
        alertDialogViewModel.cancelAlert();
        viewFinalMap(name,path,list);*/
    }

    @Override
    public void listOptionClicked(String text) {

        if (Utils.checkifavailable(text)) {
            if (text.equalsIgnoreCase("other")) {
                alertType = AlertType.historyAlert;

                DialogManager.showMyDialog(CreateWalkMapActivity.this,
                        alertType, historyAlertVM, this, "",
                        "", null, labelsHistory, "OK", "Cancel");

            } else {
this.label=text;

                heatmapBinding.displayIv.addLabelAtLastPoint(text);
                alertDialogViewModel.cancelAlert();

            }
        }
    }

    @Override
    public void finishActivity() {

    }

    private class RedrawRxr extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
                WALKING=false;
                SIGNAL_STRENGTH_FLAG=false;
                String newMapId=intent.getStringExtra("newMapId");
                Log.i(TAG,"Finally previous Id is "+CreateWalkMapActivity.this.mapId);
                CreateWalkMapActivity.this.mapId=newMapId;
                Log.i(TAG,"Finally New Id is "+CreateWalkMapActivity.this.mapId);
                heatmapBinding.displayIv.clearData();
            heatmapBinding.surveyIdTv.setText(mapId);


        }
    }


    private class ShowAlertRxr extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
           /* Log.i(TAG,"Broadcast rxd");
            String msg=null;
            db_handler.open();

            MapModel mapModel=db_handler.readMapDataForMapId(mapId).get(0);
            if(mapModel!=null)
            {
                if(!Utils.checkifavailable(mapModel.getFinalMapPath()) && Utils.checkifavailable(mapModel.getLsHeatMapPath()))
                {
                    msg="Please save Signal Strength heat map!";
                }
                else if(Utils.checkifavailable(mapModel.getFinalMapPath()) && !Utils.checkifavailable(mapModel.getLsHeatMapPath()))
                {
                    msg="Please save Link Speed heat map!";
                }
            }
            Utils.showAlert(false,msg,CreateWalkMapActivity.this,null);
*/
        }
    }

}
