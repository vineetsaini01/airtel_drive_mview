package com.newmview.wifi.activity;

import android.Manifest;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;

import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.mview.airtel.R;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.bean.AlertOptions;
import com.newmview.wifi.bean.MapModel;

import com.newmview.wifi.bean.TestResults;
import com.newmview.wifi.bean.WifiHeatMapPoints;
import com.newmview.wifi.customdialog.MyAlertDialog;
import com.newmview.wifi.database.DB_handler;
import com.mview.airtel.databinding.ActivityFinalWifiHeatMapBinding;
import com.newmview.wifi.enumtypes.AlertType;
import com.newmview.wifi.fragment.Dwnld_upload_fragment;
import com.newmview.wifi.fragment.LinkSpeedHeatMapFragment;
import com.newmview.wifi.fragment.VideoSpeedtest_fragment;
import com.newmview.wifi.helper.CoordsBuilder;
import com.newmview.wifi.helper.Interfaces;
import com.newmview.wifi.other.CommonAlertDialog;
import com.newmview.wifi.other.Config;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.DialogManager;
import com.newmview.wifi.other.Utils;
import com.newmview.wifi.viewmodel.DialogViewModel;
import com.newmview.wifi.viewmodel.MainViewModelFactory;
import com.newmview.wifi.viewmodel.MapVM;
import com.newmview.wifi.viewmodel.TestResultsVM;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.mview.airtel.R.color.dark_green;
import static com.mview.airtel.R.color.light_green;
import static com.mview.airtel.R.color.yellow;
import static com.newmview.wifi.helper.CoordsBuilder.endX;
import static com.newmview.wifi.helper.CoordsBuilder.endY;
import static com.newmview.wifi.helper.CoordsBuilder.startX;
import static com.newmview.wifi.helper.CoordsBuilder.startY;
import static com.newmview.wifi.other.Utils.generateRandomNumber;
import static com.newmview.wifi.other.Utils.saveContent;
import static com.newmview.wifi.other.WifiConfig.getConnectedWifiDetails;

public class FinalWifiHeatMapActivity extends AppCompatActivity implements View.OnClickListener, MyAlertDialog.AlertDialogInterface,Interfaces.RefreshDataListener {

    private static final String TAG ="FinalWifiHeatMap" ;
    public static final String ACTIVITY_FINISHED ="com.activity.FinalWifiHeatMapActivity" ;
    public static final String REDRAW_WALKMAP="com.activity.FinalWifiHeatMapActivity.redraw";
    private static final String LS_HEAT_MAP_TAG = "linkspeedmap";
    private static final String SPEED_TEST ="speed_test" ;
    private ActivityFinalWifiHeatMapBinding heatmapBinding;
    private DB_handler db_handler;
    private List<WifiHeatMapPoints> wifiHeatMapPoints;
    private List<WifiHeatMapPoints> blueWifiPoints,redWifiPoints,yellowWifiPoints,greenWifiPoints;
    private Point[] bluePointsArray;
    private Bitmap bitmap;
    private String path,flatType,opening,openingType,componentType,ssidName;
    private String filepath;
    private boolean saved;
    private int pos;
    private String mapId;
    private String walkMapPath;
    private TestResultsVM viewModel;
    private MapVM mapModel;

    private List<WifiHeatMapPoints> orignalHeatMapPoints=new ArrayList<>();
    private int wifiCoordsX,wifiCoordsY;
    private List<WifiHeatMapPoints> redPoints,lightGreenPoints,darkGreenPoints,yellowPoins;
    //private List<Point> quad1List,lightGreenPoints,darkGreenPoints,yellowPoins;
    private ArrayList<Double> percentList;
    private double y_percentage,lg_percentage,d_percentage,r_percentage;
    private boolean nearCase1Exceeded,nearCase2Exceeded,nearCase3Exceeded,nearCase4Exceeded;
    private LinkSpeedHeatMapFragment lsHeatMapFragment;
    private Bundle bundle;
    private   ToggleButton toggleButton;
    private boolean saveInTable;
    private MapModel mapDetails;
    private String lsHeatMapPathmapDetails,finalMapPath;
    private List<MapModel> mapList;
    private Dwnld_upload_fragment dwnld_upload_fragment;
    private VideoSpeedtest_fragment videospeedtest_fragment ;
    private FragmentManager fragmentManager;
    private DialogViewModel alertDialogViewModel;
    private List<TestResults> testResults;
    private double poorPercent;
    private String typedOtp;
    private String sentOtp;
    private long ss;
    private ArrayList<Object> listPermissionsNeeded;
    private ProgressDialog progressDialog;
    private ArrayList<Object> sentsubid;
    private String msg;
    private PendingIntent sentIntent,deliveredPI;
    private BroadcastReceiver smsreceiversent;
    private SmsManager smsManager;
    private SubscriptionManager subscriptionManager;
    private ArrayList<Integer> simCardList;
    private int smsSubscriptionId;
    private String number;
    private int times;
    private Comparator<WifiHeatMapPoints> comparator;
    private AlertType alertType;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       implementMenuButton(menu);

       return true;
     // return false;
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
                        CommonAlertDialog.showPopup(v, FinalWifiHeatMapActivity.this, R.menu.final_map_more_menu, new Interfaces.onMenuButtonClickListener() {
                            @Override
                            public void onMenuButtonClicked(MenuItem v) {
                                performMenuItemsClickOperation(v);
                            }
                        });
                    }


                }
            });




    }
    private void performMenuItemsClickOperation(MenuItem v) {
        Bundle bundle=new Bundle();
        if(heatmapBinding.displayIv.getLatestTestAreaCoords()!=null) {
            bundle.putFloat("x", heatmapBinding.displayIv.getLatestTestAreaCoords().x);
            bundle.putFloat("y", heatmapBinding.displayIv.getLatestTestAreaCoords().y);
            bundle.putFloat("color", heatmapBinding.displayIv.getLatestTestAreaCoords().color);
            bundle.putString("mapId", mapId);
        }

        switch (v.getItemId())
        {
            case R.id.statsItem:
                showStats();
                break;
            case R.id.shareItem:
                /*String textToShare=
                        "SSID : "+connectedSSID +", " +
                                "\n"+"Time Stamp : "+Utils.getDateTime()+", " +
                                "\n"+"Survey Id : "+mapId +", \n"+"User Id " +
                                ": "+Utils.getMyContactNum(CreateWalkMapActivity.this);*/
                Utils.sendImage(saved, FinalWifiHeatMapActivity.this,
                        heatmapBinding.displayIv,filepath,false,null,
                        null,mapId,"");
                break;
            case R.id.vdoSpeedTestItem:
                if(heatmapBinding.displayIv.getLatestTestAreaCoords()!=null) {
                    videospeedtest_fragment = new VideoSpeedtest_fragment();
bundle.putString("source","heatmap");
                    loadFragment(videospeedtest_fragment, R.string.videotest_title, bundle);
                }
                else
                {
                    Utils.showLongToast(FinalWifiHeatMapActivity.this,"Please tap on the area for which you want to collect test results");
                }

                break;
            case R.id.dwnldSpeedTestItem:
                MainActivity.ud="download";
                dwnld_upload_fragment =new Dwnld_upload_fragment();
                loadFragment(dwnld_upload_fragment,R.string.download,bundle);

                break;
            case R.id.uploadSpeedTestItem:
                MainActivity.ud="upload";
                dwnld_upload_fragment =new Dwnld_upload_fragment();
                loadFragment(dwnld_upload_fragment,R.string.upload,bundle);
                break;
           /* case R.id.wifiListItem:
                Bundle bundle=new Bundle();
                bundle.putString("flowType",flowType);
                Utils.takeToNextActivity(this,WifiActivity.class,bundle);
                break;*/
            /*case R.id.addLabelItem:
                DialogManager.showMyDialog(CreateWalkMapActivity.this,
                        AlertType.labelsAlert, alertDialogViewModel, this);*/


                //break;
        }
    }

    private void loadFragment(Fragment fragment, int title, Bundle bundle) {
        fragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.mainFrame,fragment).addToBackStack(SPEED_TEST).commit();
        getSupportActionBar().setTitle(title);
        Config.onbackpress="speedtest";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(viewModel!=null)
        {
            //viewModel.
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      /*  StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());*/
        heatmapBinding = DataBindingUtil.setContentView(this, R.layout.activity_final_wifi_heat_map);
        viewModel=new ViewModelProvider(this,new MainViewModelFactory()).get(TestResultsVM.class);
        mapModel=new ViewModelProvider(this,new MainViewModelFactory()).get(MapVM.class);
        heatmapBinding.setViewModel(new TestResults());
fragmentManager=getSupportFragmentManager();

viewModel.getTestResultsObservable().observe(FinalWifiHeatMapActivity.this, new Observer<List<TestResults>>() {
    private List<TestResults> testResults;

    @Override
    public void onChanged(List<TestResults> testResults) {
        FinalWifiHeatMapActivity.this.testResults=testResults;
        Log.i(TAG,"Test Results "+testResults);
        if(testResults!=null) {
            if(testResults.size()>0) {
                TestResults results = testResults.get(testResults.size() - 1);
                heatmapBinding.displayIv.setIdToLatestMarker(results.getId(), results.getX(), results.getY());
            }
        }
    }
});
mapModel.getMapListObservable().observe(FinalWifiHeatMapActivity.this, new Observer<List<MapModel>>() {

    @Override
    public void onChanged(List<MapModel> mapList) {
        FinalWifiHeatMapActivity.this.mapList=mapList;
    }
});

        getBundleExtras();
        init();
        initializeDialogVM();
    //    fillSpacesBetweenPoints();
        getSupportActionBar().setTitle("Signal Strength HeatMap");

        setColorsToHeatMap();
        getStats();
        heatmapBinding.displayIv.setMapId(mapId);
        showStats();
        for(int i=0;i<wifiHeatMapPoints.size();i++)
        {
            ss=ss+wifiHeatMapPoints.get(i).getSignalStrength();

        }



    }

    private void fillSpacesBetweenPoints() {
        //add coords closest to wifi
       // addPoints(150,-20);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Comparator<WifiHeatMapPoints> comparator = Comparator.comparing( WifiHeatMapPoints::getSignalStrength );
            addPointsForEachColor("near",0,-30,"green",comparator);
            addPointsForEachColor("far",0,-30,"green",comparator);

            addPointsForEachColor("near",-30,-40,"lightGreen",comparator);
            addPointsForEachColor("far",-30,-40,"lightGreen",comparator);

            addPointsForEachColor("near",-40,-70,"yellow",comparator);
            addPointsForEachColor("far",-40,-70,"yellow",comparator);

            addPointsForEachColor("near",-70,-1000,"red",comparator);
            addPointsForEachColor("far",-70,-1000,"red",comparator);

        }


    }

    private void addPointsForEachColor(String source, int max, int min, String color, Comparator<WifiHeatMapPoints> comparator) {
        WifiHeatMapPoints maxOrMinVal= null;
        boolean checkIfAny=false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            if (source.equalsIgnoreCase("near")) {
                checkIfAny = wifiHeatMapPoints.stream()
                        .filter(p -> p.getSignalStrength() < max)
                        .filter(p -> p.getSignalStrength() >= min)
                        .max(comparator)
                        .isPresent();


                if (checkIfAny) {
                    maxOrMinVal = wifiHeatMapPoints.stream()
                            .filter(p -> p.getSignalStrength() < max)
                            .filter(p -> p.getSignalStrength() >= min)
                            .max(comparator)
                            .get();
                }
            } else if (source.equalsIgnoreCase("far")) {
                checkIfAny = wifiHeatMapPoints.stream()
                        .filter(p -> p.getSignalStrength() < max)
                        .filter(p -> p.getSignalStrength() >= min)
                        .min(comparator)
                        .isPresent();
                if (checkIfAny) {
                    maxOrMinVal = wifiHeatMapPoints.stream()
                            .filter(p -> p.getSignalStrength() < max)
                            .filter(p -> p.getSignalStrength() >= min)
                            .min(comparator)
                            .get();
                }
            }
            if (maxOrMinVal != null) {

                String distance = CoordsBuilder.getDistanceBetweenPointsN(wifiCoordsX, wifiCoordsY, maxOrMinVal.getPoints().get(0).x, maxOrMinVal.getPoints().get(0).y);
                Log.i(TAG, "For " + source + " point x is " + maxOrMinVal.getPoints().get(0).x
                        + " y is " + maxOrMinVal.getPoints().get(0).y +" wifiCoordsX "+wifiCoordsX +" wifiCoordsY "+wifiCoordsY +" distance is "+distance +" for color "+color +" and signal strength "+maxOrMinVal.getSignalStrength() );
                Log.i(TAG, "Distance is " + distance + " for " + source + " and color " + color);
                if (Utils.checkIfNumeric(distance)) {
                    int distanceVal = (int) Double.parseDouble(distance);
                    getPointsCoordinates(distanceVal,maxOrMinVal.getSignalStrength(),source);

                }
            }
        }

    }

    private void getPointsCoordinates(int distanceVal, int signalStrength, String source) {
        WifiHeatMapPoints wifiHeatMapPoints = new WifiHeatMapPoints();
        List<Point> pointList = new ArrayList<>();
      //  Log.i(TAG, "point x is " + maxOrMinVal.getPoints().get(0).x + " y is " + maxOrMinVal.getPoints().get(0).y +" wifiCoordsX "+wifiCoordsX +" wifiCoordsY "+wifiCoordsY);
     int x1=0;
     int y1=0;
    // if(source.equalsIgnoreCase("near"))
         x1=wifiCoordsX-distanceVal;
         y1=wifiCoordsY;
        /*if(x1< startX)
           x1= startX;*/
        if(x1>startX)
            allotQuadIdAndAddPointsInMasterList(x1,y1,signalStrength);
        else
        {
            if(source.equalsIgnoreCase("near"))
                nearCase1Exceeded=true;
            else if(source.equalsIgnoreCase("far")) {
                if(nearCase1Exceeded) {
                    Log.i(TAG, "No need to check for far point as near is already exceeded for case 1");
                    nearCase1Exceeded=false;
                }
                else
                {
                    x1= startX;
                    allotQuadIdAndAddPointsInMasterList(x1,y1,signalStrength);
                }
            }
        }



         x1=wifiCoordsX;
         y1=wifiCoordsY-distanceVal;
         /*if(y1< startY)
             y1= startY;*/
        if(y1>startY)
            allotQuadIdAndAddPointsInMasterList(x1,y1,signalStrength);
         else
        {
            if(source.equalsIgnoreCase("near"))
                nearCase2Exceeded=true;
            else if(source.equalsIgnoreCase("far")) {
                if(nearCase2Exceeded) {
                    Log.i(TAG, "No need to check for far point as near is already exceeded for case 2");
                    nearCase2Exceeded=false;
                }
                else
                {
                    y1=startY;
                    allotQuadIdAndAddPointsInMasterList(x1,y1,signalStrength);
                }
            }
        }
        /*if(y1>startY)
        allotQuadIdAndAddPointsInMasterList(x1,y1,signalStrength);
        else*/



        x1=wifiCoordsX+distanceVal;
        y1=wifiCoordsY;
        /*if(x1> endX)
            x1= endX;*/
        if(x1<endX)
            allotQuadIdAndAddPointsInMasterList(x1,y1,signalStrength);
        else
        {
            if(source.equalsIgnoreCase("near"))
                nearCase3Exceeded=true;
            else if(source.equalsIgnoreCase("far")) {
                if(nearCase3Exceeded) {
                    Log.i(TAG, "No need to check for far point as near is already exceeded for case 3");
                    nearCase3Exceeded=false;
                }
                else
                {
                    x1= endX;
                    allotQuadIdAndAddPointsInMasterList(x1,y1,signalStrength);
                }
            }
        }



        x1=wifiCoordsX;
        y1=wifiCoordsY+distanceVal;
/*
        if(y1> endY)
            y1= endY;*/
        if(y1<endY)
            allotQuadIdAndAddPointsInMasterList(x1,y1,signalStrength);
        else
        {
            if(source.equalsIgnoreCase("near"))
                nearCase4Exceeded=true;
            else if(source.equalsIgnoreCase("far")) {
                if(nearCase4Exceeded) {
                    Log.i(TAG, "No need to check for far point as near is already exceeded for case 4");
                    nearCase4Exceeded=false;
                }
                else
                {
                    y1= endY;
                    allotQuadIdAndAddPointsInMasterList(x1,y1,signalStrength);
                }
            }
        }


    }
    private void allotQuadIdAndAddPointsInMasterList(int x1,int y1,int signalStrength)
    {
 /*       WifiHeatMapPoints wifiHeatMapPoints = new WifiHeatMapPoints();
        List<Point> pointList = new ArrayList<>();

        pointList.add(new Point(x1, y1));
        wifiHeatMapPoints.setPoints(pointList);
        wifiHeatMapPoints.setSignalStrength(signalStrength);
       String quadId=CoordsBuilder.getQuadIdForNearAnFarPoints(startX, startY, endX,
                endY, wifiCoordsX, wifiCoordsY,x1,y1);
        Log.i(TAG,"For adding points x "+x1 +" y "+y1 +" quad id "+quadId);
        if(quadId!=null) {
            wifiHeatMapPoints.setQuadId(quadId);
            this.wifiHeatMapPoints.add(wifiHeatMapPoints);
        }*/

      /*  WifiHeatMapPoints wifiHeatMapPoints = null;

        List<Point> pointList = new ArrayList<>();

        pointList.add(new Point(x1, y1));
        wifiHeatMapPoints.setPoints(pointList);
        wifiHeatMapPoints.setSignalStrength(signalStrength);
*/
        WifiHeatMapPoints wifiHeatMapPoints = null;

        if((x1 >= startX && x1 <= wifiCoordsX) && (y1 >= startY && y1 <= wifiCoordsY)) {
            Log.i(TAG,"For adding points x "+x1 +" y "+y1 +" quad id q1");
            wifiHeatMapPoints = new WifiHeatMapPoints();

            List<Point> pointList = new ArrayList<>();

            pointList.add(new Point(x1, y1));
            wifiHeatMapPoints.setPoints(pointList);
            wifiHeatMapPoints.setSignalStrength(signalStrength);

            wifiHeatMapPoints.setQuadId("quad1");
           this.wifiHeatMapPoints.add(wifiHeatMapPoints);
        }
         if((x1 >= wifiCoordsX && x1 <= endX) && (y1 >= startY && y1 <= wifiCoordsY)) {
             wifiHeatMapPoints = new WifiHeatMapPoints();

             List<Point> pointList = new ArrayList<>();

             pointList.add(new Point(x1, y1));
             wifiHeatMapPoints.setPoints(pointList);
             wifiHeatMapPoints.setSignalStrength(signalStrength);
             Log.i(TAG,"For adding points x "+x1 +" y "+y1 +" quad id q2");
             wifiHeatMapPoints.setQuadId("quad2");
             this.wifiHeatMapPoints.add(wifiHeatMapPoints);
        }
         if((x1 >= wifiCoordsX && x1 <= endX) && (y1 >= wifiCoordsY && y1 <= endY)) {
             wifiHeatMapPoints = new WifiHeatMapPoints();

             List<Point> pointList = new ArrayList<>();

             pointList.add(new Point(x1, y1));
             wifiHeatMapPoints.setPoints(pointList);
             wifiHeatMapPoints.setSignalStrength(signalStrength);
             Log.i(TAG,"For adding points x "+x1 +" y "+y1 +" quad id q3");
             wifiHeatMapPoints.setQuadId("quad3");
             this.wifiHeatMapPoints.add(wifiHeatMapPoints);
        }
         if((x1 >= startX && x1 <= wifiCoordsX) && (y1 >= wifiCoordsY && y1 <= endY)) {
             wifiHeatMapPoints = new WifiHeatMapPoints();

             List<Point> pointList = new ArrayList<>();

             pointList.add(new Point(x1, y1));
             wifiHeatMapPoints.setPoints(pointList);
             wifiHeatMapPoints.setSignalStrength(signalStrength);
             Log.i(TAG,"For adding points x "+x1 +" y "+y1 +" quad id q4");
             wifiHeatMapPoints.setQuadId("quad4");
             this.wifiHeatMapPoints.add(wifiHeatMapPoints);
        }



    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"On Resume called");
        if(viewModel!=null)
        {
            viewModel.refresh();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
             //   sendBroadcast();
                showBackWarning();

              //  finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void sendBroadcast()
    {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTIVITY_FINISHED));
    }
    private void sendRedrawBroadcast()
    {
      String newMapId= mapList.get(0).getMapId();
      Intent intent=new Intent(REDRAW_WALKMAP);
      intent.putExtra("newMapId",newMapId);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    private void setToolbar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ColorDrawable colorDrawable
                = new ColorDrawable(getResources().getColor(R.color.white));

        // Set BackgroundDrawable
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_app_icon);
    }

    private void setColorsToHeatMap() {
        //ArrayList<Map<Integer, Point[]>> allTheLists=new ArrayList<>();
        /*ArrayList<Point[]> allTheLists = new ArrayList<>();
      Point[]  bluePointsArray=getPointsAccToColor(BLUE);
      Point[] greenPointsArray=getPointsAccToColor(GREEN);
      Point[] yellowPointsArray =getPointsAccToColor(YELLOW);
      Point[] redPointsArray=getPointsAccToColor(RED);*/
        /*Map<Integer,Point[]> colorMap=new HashMap<>();
        colorMap.put(BLUE,bluePointsArray);
        colorMap.put(RED,redPointsArray);
        colorMap.put(GREEN,greenPointsArray);
        colorMap.put(YELLOW,yellowPointsArray);*/
    //  allTheLists.add(Color.BLUE,bluePointsArray);
/*
        Collections.sort(colorMap, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return o2.;
            }
        });
*/

      /*  allTheLists.add(bluePointsArray);
        allTheLists.add(greenPointsArray);
        allTheLists.add(yellowPointsArray);
        allTheLists.add(redPointsArray);

      Collections.sort(allTheLists, new Comparator<Point[]>() {
          @Override
          public int compare(Point[] o1, Point[] o2) {

              return o2.length - o1.length;
          }
      });

      for(int i=0;i<allTheLists.size();i++)
      {

          Log.i(TAG,"Size of arrays "+allTheLists.get(i).length);
          heatmapBinding.displayIv.setPoints(allTheLists.get(i));
      }*/
/*
        Collections.sort(allTheLists, new Comparator<ArrayList>(){
            public int compare(ArrayList a1, ArrayList a2) {
                return a2.size() - a1.size(); // assumes you want biggest to smallest

        });


*/
       // CoordsBuilder.newGetGridId()

       /*  getPointsAccToGridId("G1");
        getPointsAccToGridId("G2");
        getPointsAccToGridId("G3");
        getPointsAccToGridId("G4");
        getPointsAccToGridId("G5");
        getPointsAccToGridId("G6");
        getPointsAccToGridId("G7");
        getPointsAccToGridId("G8");*/
      //  List<WifiHeatMapPoints> quad1Points = getPointsAccToGridId("quad1");
        for(int i=0;i<wifiHeatMapPoints.size();i++)
        {
            Log.i(TAG,"Quad id for "+i+" is "+wifiHeatMapPoints.get(i).getQuadId() +" point x "+wifiHeatMapPoints.get(i).getPoints().get(0).x +" y "+wifiHeatMapPoints.get(i).getPoints().get(0).y);
        }
        setPointsToRespectiveLists(getPointsAccToGridId("quad1"),"quad1");
        setPointsToRespectiveLists(getPointsAccToGridId("quad2"),"quad2");
        setPointsToRespectiveLists(getPointsAccToGridId("quad3"),"quad3");
        setPointsToRespectiveLists(getPointsAccToGridId("quad4"),"quad4");
        setPointsToRespectiveLists(getPointsAccToGridId("quad"),"quad");





       /* heatmapBinding.displayIv.setPoorPoints(getPointsAccToColor(getResources().getColor(R.color.red)));
        heatmapBinding.displayIv.setFairPoints(getPointsAccToColor(getResources().getColor(R.color.yellow)));
        heatmapBinding.displayIv.setBluePoints(getPointsAccToColor(getResources().getColor(R.color.light_green)));
        heatmapBinding.displayIv.setGreenPoints(getPointsAccToColor(getResources().getColor(dark_green)));
        Log.i(TAG,"Cpb red "+getResources().getColor(R.color.cpb_red_dark) +" orange "+getResources().getColor(orange)
        +" light green "+getResources().getColor(light_green) +" dark green "+getResources().getColor(dark_green));*/

    }

    private void setPointsToRespectiveLists(List<WifiHeatMapPoints> heatMapPoints,String quadId) {
        redPoints=new ArrayList<>();
        yellowPoins=new ArrayList<>();
        darkGreenPoints=new ArrayList<>();
        lightGreenPoints=new ArrayList<>();
if(heatMapPoints!=null)
{
        for(int i=0;i<heatMapPoints.size();i++) {
            WifiHeatMapPoints point = heatMapPoints.get(i);
            int signalStrength = point.getSignalStrength();
            Log.i(TAG, "Points signal strength " + signalStrength +" label "+point.getLabel());
            if (signalStrength < 0 && signalStrength >= -30) {
              //  darkGreenPoints.add(point.getPoints().get(0));
                darkGreenPoints.add(point);
            } else if (signalStrength < -30 && signalStrength >= -40) {
             //   lightGreenPoints.add(point.getPoints().get(0));
                lightGreenPoints.add(point);
            } else if (signalStrength < -40 && signalStrength >= -70) {
             //   yellowPoins.add(point.getPoints().get(0));
                yellowPoins.add(point);
            } else if (signalStrength < -70) {
                redPoints.add(point);
              //  redPoints.add(point.getPoints().get(0));
            }
        }

        }

//vif(redPoints.size()>)
        Log.i(TAG,"Red points getting added for "+quadId);
        Log.i(TAG,"Light Green points getting added for "+quadId);
        Log.i(TAG,"Yellow points getting added for "+quadId);
        heatmapBinding.displayIv.setWifiCoords(wifiCoordsX,wifiCoordsY);



        heatmapBinding.displayIv.setExcellentPoints(darkGreenPoints);
        heatmapBinding.displayIv.setGoodPoints(lightGreenPoints);
        heatmapBinding.displayIv.setFairPoints(yellowPoins);
        heatmapBinding.displayIv.setPoorPoints(redPoints);


    }

    private void getBundleExtras() {
         bundle= getIntent().getExtras();
      //  bundle.get
       // ArrayList<contact> arraylist = bundle.getParcelableArrayList("VAR1");
        wifiHeatMapPoints= bundle.getParcelableArrayList("pointsList");
        path=getIntent().getStringExtra("path");
        flatType=bundle.getString("flatType");
        componentType=bundle.getString("componentType");
        opening=bundle.getString("opening");
        openingType=bundle.getString("openingType");
        ssidName=bundle.getString("ssidName");
        mapId=bundle.getString("mapId");
        walkMapPath=bundle.getString("walkMapPath");
       wifiCoordsX=bundle.getInt("wifiCoordsX");
        wifiCoordsY=bundle.getInt("wifiCoordsY");

    }
    private Bitmap setMapToImage() {
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
        return null;
    }
    public List<WifiHeatMapPoints> getPointsAccToGridId(String gridId) {

        Log.i(TAG, "QUAD id is " + gridId);
        List<WifiHeatMapPoints> wifiPoints ;

        for (int i = 0; i < wifiHeatMapPoints.size(); i++) {

          /* int x= wifiHeatMapPoints.get(i).getPoints().get(0).x;
            int y= wifiHeatMapPoints.get(i).getPoints().get(0).y;
           String quadId= CoordsBuilder.getQuadId(CoordsBuilder.startX,CoordsBuilder.startY,CoordsBuilder.endX,CoordsBuilder.endY,wifiCoordsX,wifiCoordsY,x,y);
           if(Utils.checkifavailable(quadId))
           {
               if(quadId.equalsIgnoreCase(gridId))
               {
                   wifiPoints.add(wifiHeatMapPoints.get(i))
               }
           }
        }*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (gridId != null) {

                    wifiPoints =
                            wifiHeatMapPoints.stream()
                                    .filter(str -> str.getQuadId().equalsIgnoreCase(gridId))
                                    .collect(Collectors.toList());
                    return wifiPoints;
                }


            }

        }
        return null;
    }

    public Point[] getPointsAccToColor(int color, List<WifiHeatMapPoints> wifiHeatMapPoints)
    {

        List<WifiHeatMapPoints> wifiPoints=null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {


            if(color==getResources().getColor(dark_green))
            {
                Log.i(TAG,"color obtained darkgreen");
                wifiPoints =
                        wifiHeatMapPoints.stream()
                                .filter(p -> p.getSignalStrength() < 0)
                                .filter(p -> p.getSignalStrength() >= -30)
                                //  .flatMap(points::stream)
                                .collect(Collectors.toList());
            }

          else  if(color==getResources().getColor(light_green))
            { Log.i(TAG,"color obtained light green");
                wifiPoints =
                        wifiHeatMapPoints.stream()
                                .filter(p -> p.getSignalStrength() < -30)
                                .filter(p -> p.getSignalStrength() >= -40)
                                //  .flatMap(points::stream)
                                .collect(Collectors.toList());
            }
            else  if(color==getResources().getColor(yellow))
            { Log.i(TAG,"color obtained yellow");
                wifiPoints =
                        wifiHeatMapPoints.stream()
                                .filter(p -> p.getSignalStrength() < -40)
                                .filter(p -> p.getSignalStrength() >= -70)
                                //  .flatMap(points::stream)
                                .collect(Collectors.toList());
            }
          else  if(color==getResources().getColor(R.color.red))
            {

                Log.i(TAG,"color obtained red");
                wifiPoints =
                        wifiHeatMapPoints.stream()
                                .filter(p -> p.getSignalStrength() < -70)

                                //  .flatMap(points::stream)
                                .collect(Collectors.toList());
            }

            if(wifiPoints!=null) {
                Point[] pointsArray = new Point[wifiPoints.size()];
                for (int i = 0; i < wifiPoints.size(); i++) {
                    //Log.i(TAG,"Signal strength for "+color +" is "+wifiPoints);
                    pointsArray[i] = wifiPoints.get(i).getPoints().get(0);
                }

                return pointsArray;
            }
/*
            switch (color) {
                case getResources().getColor(dark_green):
                    Log.i(TAG,"color coming "+color +" color here" + dark_green);
                    wifiPoints =
                            wifiHeatMapPoints.stream()
                                    .filter(p -> p.getSignalStrength() < 0)
                                    .filter(p -> p.getSignalStrength() >= -30)
                                    //  .flatMap(points::stream)
                                    .collect(Collectors.toList());
                   break;
                case R.color.light_green:
                    Log.i(TAG,"color coming "+color +" color here" +R.color.light_green);
                    wifiPoints = wifiHeatMapPoints.stream()
                            .filter(p -> p.getSignalStrength() < -30)
                            .filter(p -> p.getSignalStrength() >= -40)
                            //  .flatMap(points::stream)
                            .collect(Collectors.toList());
                    break;

                case getResources().getColor(R.color.orange):
                    Log.i(TAG,"color coming "+color +" color here" +R.color.orange);
                    wifiPoints =
                            wifiHeatMapPoints.stream()
                                    .filter(p -> p.getSignalStrength() < -40)
                                    .filter(p -> p.getSignalStrength() >= -70)
                                    //  .flatMap(points::stream)
                                    .collect(Collectors.toList());
                    break;
                case R.color.cpb_red_dark:
                    Log.i(TAG,"color coming "+color +" color here" +R.color.cpb_red);
                    wifiPoints =
                            wifiHeatMapPoints.stream()
                                    .filter(p -> p.getSignalStrength() < -70)
                                   */
/* .filter(p -> p.getSignalStrength() >= -60)*//*

                                    //  .flatMap(points::stream)
                                    .collect(Collectors.toList());

                   break;
                default:
                    Log.i(TAG,"color coming "+color +" color here no color");
                    break;

            }
*/
        }
//        Log.i(TAG, " size of  points  " + wifiPoints.size() +" for color "+color);

        return null;

    }
    private void initializeDialogVM() {
        alertDialogViewModel = DialogManager.initializeViewModel(this);
        DialogManager.initializeViewModel(this);
        alertDialogViewModel.getOptions().observe(FinalWifiHeatMapActivity.this, new Observer<AlertOptions>() {
            @Override
            public void onChanged(AlertOptions alertOptions) {
                Log.i(TAG,"Alert options.."+alertOptions.getType() +" alt text "+alertOptions.getAlternativeText());
                // typedOtp=alertOptions.getAlternativeText();
            }
        });
    }
    private void init() {

        db_handler = new DB_handler(MviewApplication.ctx);
        bitmap = setMapToImage();
        setToolbar();


        if (bitmap != null) {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(bitmap, new Matrix(), null);
            heatmapBinding.displayIv.setImageBitmap(bitmap);
        }
        heatmapBinding.displayIv.setVisibility(View.VISIBLE);
        String contactNum=Utils.getMyContactNum(this);
         ssidName=getConnectedWifiDetails().getSsidName();
        if(!Utils.checkifavailable(ssidName))
            ssidName="<Unknown>";
        if(Utils.checkifavailable(contactNum)) {
           // heatmapBinding.wifiDetailsTv.setText("Shared by "+contactNum +" for SSID:- "+ ssidName +" at "+Utils.getDateTime());
            //  heatmapBinding.displayIv.setSSIDName("SSID : "+ssidName);
            heatmapBinding.ssidTv.setText(ssidName);
            heatmapBinding.surveyIdTv.setText(mapId);
            heatmapBinding.userIdv.setText(Utils.getMyContactNum(FinalWifiHeatMapActivity.this));
        }
        setClickListeners();
    }
    private void setClickListeners() {
        heatmapBinding.uploadBtn.setOnClickListener(this);
        heatmapBinding.saveBtn.setOnClickListener(this);
        heatmapBinding.shareBtn.setOnClickListener(this);
        heatmapBinding.statsBtn.setOnClickListener(this);

    }
    private void getStats()
    {
        int yellowColorPoints=heatmapBinding.displayIv.getYellowPoints();
        int lightGreenPoints=heatmapBinding.displayIv.getBluePoints();
        int darkGreenPoints=heatmapBinding.displayIv.getGreenPoints();
        int redPoints=heatmapBinding.displayIv.getRedPointsSize();
        DecimalFormat decimalFormat=new DecimalFormat("0.0");
        int totalPoints=yellowColorPoints + lightGreenPoints + darkGreenPoints + redPoints;
         y_percentage=((double)(yellowColorPoints *100 )/totalPoints);
         lg_percentage= ((double)(lightGreenPoints* 100)/totalPoints);
         d_percentage=  ((double)(darkGreenPoints * 100)/totalPoints);
         r_percentage=  ((double)(redPoints *100) /totalPoints);
        String y_text="Fair";
        String lg_text="Good";
        String dg_tet="Excellent";
        String red_text="Poor";
      percentList=new ArrayList<>();
        percentList.add(y_percentage);
        percentList.add(lg_percentage);
        percentList.add(d_percentage);
        percentList.add(r_percentage);
        this.poorPercent=r_percentage;
        Log.i(TAG,"Poor percent here is "+poorPercent);
        Log.i(TAG,"Count for dg "+darkGreenPoints +" count for lg "+lightGreenPoints +" yellow "+yellowColorPoints +" red "+redPoints +" total "+totalPoints);
        Log.i(TAG,"Percentt for dg "+d_percentage +"  for lg "+lg_percentage +" yellow "+y_percentage +" red "+r_percentage +" total "+totalPoints);
      /*  heatmapBinding.darkGreenTv.setText(decimalFormat.format(d_percentage)+"% ("+dg_tet+")");
        heatmapBinding.lightGreenTv.setText(decimalFormat.format(lg_percentage)+"% ("+lg_text+")");
        heatmapBinding.yellowTv.setText(decimalFormat.format(y_percentage)+"% ("+y_text+")");
        heatmapBinding.redTv.setText(decimalFormat.format(r_percentage)+"% ("+red_text+")");
*/

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.statsBtn:
                showStats();

                        break;


            case R.id.shareBtn:
                showAlertForSSSummary();



                /*String textToShare=null;
                if(getSupportFragmentManager().getBackStackEntryCount()>0)
                    textToShare="Link Speed HeatMap";
                else
                    textToShare="Signal Strength Heat Map";
                Utils.sendImage(saved,FinalWifiHeatMapActivity.this,heatmapBinding.heatMapLL,
                        filepath,false,null, new Interfaces.SaveSuccessfullListener() {
                    @Override
                    public void saveSuccessfull(String name, String path) {

                    }
                }, mapId, textToShare);*/
                break;
            case R.id.saveBtn:
                String finalPath = null;
                Log.i(TAG,"Step 1");
                PackageManager pm = FinalWifiHeatMapActivity.this.getPackageManager();
                int hasPerm = pm.checkPermission(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        FinalWifiHeatMapActivity.this.getPackageName());
                if (hasPerm != PackageManager.PERMISSION_GRANTED) {
                    // do stuff
                    Log.i(TAG,"Step 2");
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE

                    }, 1);
                } else {
                    Log.i(TAG,"Step 3");
                    saveSignalStrengthMap();

/*if(heatmapBinding.saveBtn.getText().equals("Take To Link Speed HeatMap"))
{
    getSupportActionBar().setTitle("Link Speed HeatMap");
    heatmapBinding.saveBtn.setVisibility(View.GONE);
    heatmapBinding.progressBar.setVisibility(View.VISIBLE);
    openLinkSpeedFragment();
    showLinkSpeedButton();


}
else {
    saveBothMaps();
}*/


                }

                break;
        }


    }

    private  void showAlertForSSSummary() {
         if(wifiHeatMapPoints!=null)
                {
                    if(wifiHeatMapPoints.size()>0)
                    {
                       alertType=AlertType.dynamicAlert;
                long avg=ss/wifiHeatMapPoints.size();
                comparator = Comparator.comparing( WifiHeatMapPoints::getSignalStrength );


                WifiHeatMapPoints maxSSPoint=  wifiHeatMapPoints.stream().max(comparator)
                        .get();
                WifiHeatMapPoints minSSPoint=  wifiHeatMapPoints.stream().min(comparator)
                        .get();

               /* Utils.showLongToast(FinalWifiHeatMapActivity.this,
                        "Avg is "+avg +" and points count "+wifiHeatMapPoints.size());*/
                DialogManager.showMyDialog(FinalWifiHeatMapActivity.this,
                        alertType, alertDialogViewModel, FinalWifiHeatMapActivity.this,
                        "Average of Signal Strength range is "+avg+System.lineSeparator()+"Points count is "+wifiHeatMapPoints.size()
                        +System.lineSeparator()+
                                " Maximum Signal Strength "+maxSSPoint.getSignalStrength()+" dBm "+
                                " Minimum Signal Strength : "+minSSPoint.getSignalStrength() +" dBm"
                        ,
                        "Signal Strength Summary", null, null,
                        null,"Dismiss");
                    }
                }
    }

    private void saveSignalStrengthMap() {

        {
new CommonAlertDialog().dismissAlert();
            String currentDateTime=Utils.getDateTime();
            heatmapBinding.timeStampTv.setText(currentDateTime);
            heatmapBinding.displayIv.clearMarkers();
            bitmap = Utils.getViewBitmap(heatmapBinding.heatMapLL);
            String fileName="Heat_Map_"+currentDateTime+".png";
           saveContent(bitmap, FinalWifiHeatMapActivity.this,
                false, "Image saved successfully to your gallery!",
                new Interfaces.SaveSuccessfullListener() {
                    @Override
                    public void saveSuccessfull(String name, String path) {
                        //poorPercent="35";
                        Log.i(TAG,"Poor percent while getting is "+poorPercent);
                        saveDataInTable(name,path,"heatmap");
                            if(poorPercent>=30)
                            {
                                DecimalFormat decimalFormat=new DecimalFormat("0.0");


                                Double d = poorPercent;
                                int poorPercentVal = (int) d.doubleValue();
/*
// or directly:
                                int i2 = d.intValue();*/
                                alertType=AlertType.confirmationAlertDialog;
                                DialogManager.showMyDialog(FinalWifiHeatMapActivity.this,
                                        alertType, alertDialogViewModel, FinalWifiHeatMapActivity.this,
                                        Constants.POOR_PERCENTAGE_ALERT_STEP_1+
                                                poorPercentVal+"%"+System.lineSeparator()+System.lineSeparator()
                                                +Constants.POOR_PERCENTAGE_ALERT_STEP_2,
                                        "Poor Coverage", null, null,"Re-do Survey","Customer Consent");
                            }

                        else
                        {
                            takeToBuildingTab();

                        }


                    /* finish();
                       Intent i=new Intent(FinalWifiHeatMapActivity.this,BuildingTabActivity.class);
                            startActivity(i);*/

                    }
                }, fileName);

        }


    }

    private void showLinkSpeedButton() {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                heatmapBinding.saveBtn.setVisibility(View.VISIBLE);
                heatmapBinding.saveBtn.setText("Save Link Speed Map");
                heatmapBinding.progressBar.setVisibility(View.GONE);
            }
        }, 700);
    }

    private void showStats() {
        if(getSupportFragmentManager().getBackStackEntryCount()==0)
           new CommonAlertDialog().showStatsAlert(this,percentList, null, new Interfaces.DismissDialogInterface() {
                @Override
                public void dismiss() {

                }
            });
        else
        {
            db_handler.open();
            MapModel mapList=db_handler.readMapDataForMapId(mapId).get(0);
            ArrayList<String> lsPercentList=new ArrayList<>();
            if(mapList!=null)
            {
                Log.i(TAG,"Maplist at 0 "+mapList);
                lsPercentList.add(mapList.getLsFairCoveragePercentage());
                lsPercentList.add(mapList.getLsGoodCoveragePercentage());
                lsPercentList.add(mapList.getLsExcellentCoveragePercentage());
                lsPercentList.add(mapList.getLsPoorCoveragePercentage());


                new CommonAlertDialog().showStatsAlert(this,null,lsPercentList, new Interfaces.DismissDialogInterface() {
                    @Override
                    public void dismiss() {
                    }
                });
            }
        }
    }

    private void saveLsHeatMap()
    {

    }

    private void saveBothMaps() {

        String currentDateTime=Utils.getDateTime();
        heatmapBinding.timeStampTv.setText(currentDateTime);
heatmapBinding.displayIv.clearMarkers();
        bitmap = Utils.getViewBitmap(heatmapBinding.heatMapLL);
        String fileName="Heat_Map_"+currentDateTime+".png";
       String ls_fileName="LinkSpeed_Heat_Map_"+currentDateTime+".png";

        saveContent(bitmap, FinalWifiHeatMapActivity.this,
                false, "Image saved successfully to your gallery!",
                new Interfaces.SaveSuccessfullListener() {
                    @Override
                    public void saveSuccessfull(String name, String path) {

                        db_handler.open();


                        mapDetails= db_handler.readMapDataForMapId(mapId).get(0);


                      // db_handler.close();
                        if(mapDetails!=null)
                        {
                             lsHeatMapPathmapDetails=mapDetails.getLsHeatMapPath();
                             finalMapPath=mapDetails.getFinalMapPath();

                            //saveDataInTable(fileName,path,"ls_heatmap");
                            if(Utils.checkifavailable(finalMapPath) && !Utils.checkifavailable(lsHeatMapPathmapDetails))
                            {
                                Log.i(TAG,"Case 1");
                                if(saveInTable)
                                {
                                    Log.i(TAG,"Case 6");
                                    saveDataInTable(name,path,"ls_heatmap");
                                    saveBothMaps();
                                    saveInTable=false;
                                }
                                else {
                                    Log.i(TAG,"Case 7");
                                    Utils.showLongToast(FinalWifiHeatMapActivity.this,Constants.SUCCESSFULL_HEATMAP);
                                    //showAlert("ls_heatmap", Constants.SUCCESSFULL_HEATMAP);
                                    getSupportActionBar().setTitle("Link Speed HeatMap");
                                    heatmapBinding.saveBtn.setVisibility(View.GONE);
                                    heatmapBinding.progressBar.setVisibility(View.VISIBLE);
                                    openLinkSpeedFragment();
                                    showLinkSpeedButton();
                                    saveInTable=true;
                                }


                            }
                            else if(!Utils.checkifavailable(finalMapPath)&& Utils.checkifavailable(lsHeatMapPathmapDetails))
                            {
                                Log.i(TAG,"Case 2");
                                if(saveInTable)
                                {
                                    //Utils.showAlert(false,Constants.SUCCESSFULL_LS_HEATMAP_STEP2,FinalWifiHeatMapActivity.this,null);
                                    saveDataInTable(name,path,"heatmap");
                                    saveBothMaps();
                                    saveInTable=false;
                                }
                                else {
                                    showAlert("heatmap", Constants.SUCCESSFULL_LS_HEATMAP);
                                }

                            }
                            else if(!Utils.checkifavailable(finalMapPath)&& !Utils.checkifavailable(lsHeatMapPathmapDetails))
                            {
                                if(getSupportFragmentManager().getBackStackEntryCount()==0)
                                {
                                    Log.i(TAG,"Case 3");
                                    saveDataInTable(name,path,"heatmap");
                                }
                                else
                                {
                                    Log.i(TAG,"Case 4");
                                    saveDataInTable(name,path,"ls_heatmap");
                                }
                                saveBothMaps();
                            }
                            else
                            {
                                Log.i(TAG,"Case 5");
                             //  Utils.showAlert(false,Constants.CREATE_HEATMAP,FinalWifiHeatMapActivity.this,null);
                              //  Utils.showLongToast(FinalWifiHeatMapActivity.this, Constants.CREATE_HEATMAP);
                                takeToBuildingTab();
                            }
                        }

                    }
                }, fileName);

       /* saveDataInTable(fileName,path,"ls_heatmap");
        Utils.showToast(FinalWifiHeatMapActivity.this,"Entries saved successfully!");
        Intent i=new Intent(
                FinalWifiHeatMapActivity.this,BuildingTabActivity.class);
        startActivity(i);*/

/*
        saveContent(bitmap, FinalWifiHeatMapActivity.this,
                false, "Image saved successfully to your gallery!",
                new Interfaces.SaveSuccessfullListener() {
                    @Override
                    public void saveSuccessfull(String name, String path) {
                     //   Utils.showToast(FinalWifiHeatMapActivity.this, Constants.SUCCESSFULL_HEATMAP);
                        saveDataInTable(ls_fileName,path,"linkspeed_heatmap");
                        //finish();
                            */
/*Intent i=new Intent(
                                    FinalWifiHeatMapActivity.this,BuildingTabActivity.class);
                            startActivity(i);*//*

                    }
                }, fileName);
*/
    }
    private void showAlert(String msg) {
        times++;
        System.out.println("called " + times++);
        final Dialog dialog = new Dialog(FinalWifiHeatMapActivity.this, R.style.AlertDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.alertdialog, null);
        TextView textView = view.findViewById(R.id.msg);
        textView.setText(msg);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        Button okbutton = view.findViewById(R.id.ok);
        okbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                takeUserToSettings();

            }
        });


    }
    private void takeUserToSettings() {
        Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(i);


    }

    private void showAlert(String source, String message) {
        Utils.showAlert(false, message, FinalWifiHeatMapActivity.this, new Interfaces.DialogButtonClickListener() {
            @Override
            public void onPositiveButtonClicked(String text) {
                String fileName="";
                if(source.equalsIgnoreCase("heatmap")) {
                    Log.i(TAG,"Case 8");
                   /* if (!toggleButton.isChecked()) {

                        toggleButton.setChecked(true);
                    }*/
                    getSupportActionBar().setTitle("Signal Strength HeatMap");
                    int count=  getSupportFragmentManager().getBackStackEntryCount();
                    if(count>0)
                    {
                        getSupportFragmentManager().popBackStackImmediate();
                    }

              //      Utils.showAlert(false,Constants.SUCCESSFULL_LS_HEATMAP_STEP2,FinalWifiHeatMapActivity.this,null);
                    saveInTable=true;

                    /*bitmap = Utils.getViewBitmap(heatmapBinding.heatMapLL);
                     fileName="Heat_Map_"+Utils.getDateTime()+".png";*/

                }
                else if(source.equalsIgnoreCase("ls_heatmap"))
                {
                  /*  if (toggleButton.isChecked()) {
                        toggleButton.setChecked(false);
                    }*/
                    Log.i(TAG,"Case 9");
                        getSupportActionBar().setTitle("Link Speed HeatMap");
                    heatmapBinding.saveBtn.setVisibility(View.GONE);
                    heatmapBinding.progressBar.setVisibility(View.VISIBLE);
                        openLinkSpeedFragment();
                       showLinkSpeedButton();
               //     Utils.showAlert(false,Constants.SUCCESSFULL_HEATMAP_STEP2,FinalWifiHeatMapActivity.this,null);
                    saveInTable=true;
                   /* bitmap = Utils.getViewBitmap(heatmapBinding.heatMapLL);
                     fileName="LinkSpeed_Heat_Map_"+Utils.getDateTime()+".png";*/
                }
              /*  String finalFileName = fileName;
                saveContent(bitmap, FinalWifiHeatMapActivity.this,
                        false, "Image saved successfully to your gallery!",
                        new Interfaces.SaveSuccessfullListener() {

                            @Override
                            public void saveSuccessfull(String name, String path) {
                                saveDataInTable(finalFileName,path,source);
                            }
                        }, fileName);
*/
            }

            @Override
            public void onNegativeButtonClicked(String text) {

            }

            @Override
            public void onDialogDismissed(String text) {

            }
        });

    }

    private void openLinkSpeedFragment() {
bundle.putString("mapId",mapId);
        lsHeatMapFragment = LinkSpeedHeatMapFragment.newInstance(bundle);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                R.anim.slide_in,  // enter
                        R.anim.slide_out). // exit
                replace(R.id.mainFL, lsHeatMapFragment).addToBackStack(LS_HEAT_MAP_TAG).commit();
    }

    private void saveDataInTable(String name, String path, String source) {
        if(ssidName==null)
            ssidName="NA";
        if(flatType==null)
            flatType="NA";
        if(opening==null)
            opening="NA";
        if(openingType==null)
            openingType="NA";
        if(componentType==null)
            componentType="NA";

        db_handler.open();
        if(source.equalsIgnoreCase("heatmap")) {
            db_handler.updateFinalMapDetails(ssidName, path, name, mapId, walkMapPath, d_percentage, lg_percentage, y_percentage, r_percentage);
        }

        else
        {
           db_handler.updateLsFinalMapDetails(path,name,mapId);
        }
       /* db_handler.updateMapData(ssidName,name,"Wifi",flatType,opening,openingType,componentType,
                "NA","NA","NA",
                "NA","NA",
                Utils.getImsi(FinalWifiHeatMapActivity.this),"NA", path,name,
                path,"","",mapId);*/
        db_handler.close();

    }
   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        implementToggleButton(menu);

return true;
    }

    private void implementToggleButton(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_service, menu);
        MenuItem itemswitch = menu.findItem(R.id.switch_action_bar);
        itemswitch.setActionView(R.layout.use_switch);
        toggleButton = menu.findItem(R.id.switch_action_bar).getActionView().findViewById(R.id.switch2);
        toggleButton.setVisibility(View.INVISIBLE);
        toggleButton.setChecked(true);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //     if(buttonView.isPressed()) {
                if (toggleButton.isChecked()) {
                    Log.i(TAG,"Toggle button check true");
                    getSupportActionBar().setTitle("Signal Strength HeatMap");
                    int count=  getSupportFragmentManager().getBackStackEntryCount();
                    if(count>0)
                    {
                        getSupportFragmentManager().popBackStackImmediate();
                    }
                }
                else {
                    Log.i(TAG,"Toggle button check false");
                    getSupportActionBar().setTitle("Link Speed HeatMap");
                    openLinkSpeedFragment();
                }
            }

            //        }

        });

    }
*/
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Fragment currentFragment=getSupportFragmentManager().findFragmentByTag(LS_HEAT_MAP_TAG);
        Log.i(TAG,"Current fragment "+currentFragment);
        Log.i(TAG,"Back Stack count "+getSupportFragmentManager().getBackStackEntryCount());
        if(getSupportFragmentManager().getBackStackEntryCount()==0) {
            Log.i(TAG,"Case 1");
          //  String currentFragment=getSupportFragmentManager().get
            getSupportActionBar().setTitle("Signal Strength HeatMap");

         //   if(currentFragment==null)
            showBackWarning();

        //    sendBroadcast();
        }
        else
        {
          //  Fragment f=getSupportFragmentManager().findFragmentByT
            Log.i(TAG,"Case 2 speed test "+getSupportFragmentManager().findFragmentByTag(SPEED_TEST) +" heat map "+getSupportFragmentManager().findFragmentByTag(LS_HEAT_MAP_TAG));
            if(getSupportFragmentManager().findFragmentByTag(SPEED_TEST)!=null)
            {
                if(viewModel!=null)
                {
                    viewModel.refresh();
                }
            }
            else {
                if (!Utils.checkifavailable(lsHeatMapPathmapDetails) && Utils.checkifavailable(finalMapPath)) {
                    Log.i(TAG, "Case 3");
                    saveInTable = false;
                    getSupportActionBar().setTitle("Signal Strength HeatMap");
                    heatmapBinding.saveBtn.setText("Take To Link Speed HeatMap");
                    saveInTable = true;
                }
            }
             super.onBackPressed();
        }

    }

    private void showBackWarning() {
        Log.i(TAG,"Back press check with mapdetails"+mapDetails);
        if(mapDetails!=null)
        {
            String ss_hmap=mapDetails.getFinalMapPath();
            String ls_hmap=mapDetails.getLsHeatMapPath();
            String msg=null;
            if(Utils.checkifavailable(ss_hmap) && !Utils.checkifavailable(ls_hmap))
            {
                Log.i(TAG,"Back press check 1");
                msg=Constants.HEAT_MAP_MOVE_BACK_WARNING;
            }
            else if(!Utils.checkifavailable(ss_hmap) && Utils.checkifavailable(ls_hmap))
            {
                Log.i(TAG,"Back press check 2");
                msg=Constants.LS_HEAT_MAP_MOVE_BACK_WARNING;
            }
            else
            {
                Log.i(TAG,"Back press check 3");
            }
           if(Utils.checkifavailable(msg)) {
               Log.i(TAG,"Back press check 4");
               getSupportActionBar().setTitle("Signal Strength HeatMap");
               Utils.getConfirmationFromUserAlert(FinalWifiHeatMapActivity.this, msg, "Yes", "No", new Interfaces.DialogButtonClickListener() {
                   @Override
                   public void onPositiveButtonClicked(String text) {
                       takeToBuildingTab();

                   }

                   @Override
                   public void onNegativeButtonClicked(String text) {

                   }

                   @Override
                   public void onDialogDismissed(String text) {

                   }
               });
           }
           else
           {
               Log.i(TAG,"Back press check 5");
               finish();
           }

        }
        else
        {
            Log.i(TAG,"Back press check 6");
            Utils.getConfirmationFromUserAlert(FinalWifiHeatMapActivity.this, Constants.BACK_PRESS_FINAL_HEATMAP,
                    "Yes", "No", new Interfaces.DialogButtonClickListener() {
                @Override
                public void onPositiveButtonClicked(String text) {
                   finish();

                }

                @Override
                public void onNegativeButtonClicked(String text) {

                }

                @Override
                public void onDialogDismissed(String text) {

                }
            });



        }

    }

    private void takeToBuildingTab() {
      //  ((CreateWalkMapActivity)AppCompatActivity)
        sendBroadcast();
       // sendBroadcastToAnotherApp();
       // CreateWalkMapActivity.cwmap.finish();
       /* Intent sendIntent = getPackageManager().
                getLaunchIntentForPackage("com.mcpsinc.mview");

        if (sendIntent != null) {
            sendIntent.setClassName("com.mcpsinc.mview","com.mcpsinc.mview.activity.MainActivity");
            sendIntent.setType("text/plain");
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra("poor_percent",poorPercent+"");
            sendIntent.putExtra("order_id","1");
            startActivity(sendIntent);
        } else {
            Toast.makeText(FinalWifiHeatMapActivity.this,
                    "There is no package available in android", Toast.LENGTH_LONG).show();
        }*/

        finish();
        Utils.takeToNextActivity(FinalWifiHeatMapActivity.this,BuildingTabActivity.class,null);
    }

    private void sendBroadcastToAnotherApp() {
        String CUSTOM_INTENT = "wifi.vision.custom.intent.action.TEST";
        Intent i = new Intent();
        i.putExtra("userid","123");
        i.setAction(CUSTOM_INTENT);
        sendBroadcast(i);


    }

    public void showTestResultAlert(int id) {
     //  TestResults testResult= viewModel.getTestResultsObservableAtId(id).getValue();
        TestResults testResult= viewModel.getTestResultsObservableAtLocation(id).getValue();
       if(testResult!=null)
       {
           String result=testResult.getResult();
           String testId=testResult.getTestId();
           String testTitle="Test Results";
           DialogManager.showMyDialog(FinalWifiHeatMapActivity.this,
               AlertType.testResultAlert, alertDialogViewModel, this,
              result,testTitle, null, null, "OK", "Cancel");

       }
        }

    // by swapnil bansal 09/01/2023
    private void sendReDoSurveyEvent(String event) {
        DB_handler db_handler = new DB_handler(MviewApplication.ctx);
        db_handler.open();

        try{
            // TODO Auto-generated method stub
            JSONObject jsonobj = new JSONObject();
            jsonobj.put("evt_type","Redo_Survey");
            jsonobj.put("desc","the survey id is -"+event);
            jsonobj.put("date_time", Config.getDateTime());
            jsonobj.put("additional_info", " ");
            db_handler.insertInLoggingTable("","Redo_Survey",jsonobj.toString(),Config.getDateTime(),"init");
        }
        catch (JSONException e1) { // TODO

            e1.printStackTrace();
        }

    }
    private void sendCustomerConsentSurveyEvent(String event) {
        DB_handler db_handler = new DB_handler(MviewApplication.ctx);
        db_handler.open();

        try{
            // TODO Auto-generated method stub
            JSONObject jsonobj = new JSONObject();
            jsonobj.put("evt_type","Customer_Consent_Survey");
            jsonobj.put("desc","the survey id is -"+event);
            jsonobj.put("date_time", Config.getDateTime());
            jsonobj.put("additional_info", " ");
            db_handler.insertInLoggingTable("","Customer_Consent_Survey",jsonobj.toString(),Config.getDateTime(),"init");
        }
        catch (JSONException e1) { // TODO

            e1.printStackTrace();
        }

    }

    public void alertDialogPositiveButtonClicked(AlertType type, Object details) {
if(type==AlertType.confirmationAlertDialog)
{
    // by swapnil bansal 09/01/2023
    sendReDoSurveyEvent(mapId);

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
         sendRedrawBroadcast();
         finish();
     }

}
else if(type==AlertType.otpAlert)
{
    if(details instanceof String)
typedOtp= String.valueOf(details);
   if(Utils.checkifavailable(typedOtp))
   {
       if(typedOtp.equals(sentOtp))
       {
           alertDialogViewModel.cancelAlert();
           takeToBuildingTab();
           Utils.showLongToast(FinalWifiHeatMapActivity.this,
                   Constants.SUBSCRIBER_CONSENT_RECEIVED);

       }
       else
       {
           Utils.showLongToast(FinalWifiHeatMapActivity.this,Constants.INCORRECT_OTP);
       }
   }
   else
   {
       Utils.showLongToast(FinalWifiHeatMapActivity.this,Constants.ENTER_OTP);
   }


}
    }

    @Override
    public void alertDialogNegativeButtonClicked(AlertType type) {
      //  takeToBuildingTab();
        alertDialogViewModel.cancelAlert();
        alertDialogViewModel=null;
        initializeDialogVM();
        if(alertType==AlertType.confirmationAlertDialog) {
            // by swapnil bansal 09/01/2023
            sendCustomerConsentSurveyEvent(mapId);
            sentOtp = generateRandomNumber() + "";
            //Add Request for otp
            getRunTimePermissions();
            sendSms(sentOtp);
            Utils.showLongToast(FinalWifiHeatMapActivity.this, "Otp is " + sentOtp);
            DialogManager.showMyDialog(FinalWifiHeatMapActivity.this,
                    AlertType.otpAlert, alertDialogViewModel, this,
                    Constants.ENTER_OTP, "Subscriber Confirmation", null, null, "OK", "Cancel");
        }

    }

    @Override
    public void listOptionClicked(String text) {

    }

    @Override
    public void finishActivity() {

    }

    @Override
    public void refreshOnTouch() {
        if(viewModel!=null)
        {
            viewModel.refresh();
        }
    }
    private void showProgress() {
//        Utils.appendLog(Utils.getDateTime() +" : Reached show progress function...");
        progressDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setMessage("Sending..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    private void showAlertDialog(String title, String message) {
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(this, R.style.AlertDialogTheme);
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

    private void unregisterSMSSentReceiver() {
        try {
            if (smsreceiversent != null)
                unregisterReceiver(smsreceiversent);
        } catch (Exception e) {

        }
    }

    private void dismissProgress() {
        /*if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }*/
    }
    private void sendMsgViaSMSMANAGER() {
        //       Utils.appendLog(Utils.getDateTime() +" : Sending sms via sms manager...");

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            simCardList = new ArrayList<Integer>();
            System.out.println("getting permissions in sms block");
          //  MapModel mapDetails = mapModel.readDetailsAtMapId(mapId);
            db_handler.open();
           MapModel mapDetails= db_handler.readMapDataForMapId(mapId).get(0);
           db_handler.close();
            Log.i(TAG,"Map details "+mapDetails +"mapId "+mapId);

            if (mapDetails != null) {
                 number = mapDetails.getSubscriberId();
                 Log.i(TAG,"Number is "+number  +" name "+mapDetails.getSubscriberName() );
                subscriptionManager = SubscriptionManager.from(FinalWifiHeatMapActivity.this);

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                final List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
                if (subscriptionInfoList != null && subscriptionInfoList.size() > 0) {
                    for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
                        System.out.println("getting permissions in sms block for loop");


                        int subscriptionId = subscriptionInfo.getSubscriptionId();

                        simCardList.add(subscriptionId);
                    }

                    smsSubscriptionId = subscriptionManager.getDefaultSmsSubscriptionId();
                    sentsubid.add(smsSubscriptionId);
                    smsManager = SmsManager.getSmsManagerForSubscriptionId(smsSubscriptionId);

                }

            } else {
                smsManager = SmsManager.getDefault();
            }
            System.out.println("getting sms sending" + smsManager);

            if (smsManager != null) {

if(number!=null)                smsManager.sendTextMessage(number, null, msg, sentIntent, deliveredPI);
            }
        }

    }

    private void sendSms(String otp) {
        //     Utils.appendLog(Utils.getDateTime() +" : Reached sms function...");
        //showProgress();
       // showOTPMainScreen();
        sentsubid = new ArrayList<>();


        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        // String message = smstext.getText().toString();
        MapModel mapDetails=mapModel.readDetailsAtMapId(mapId);
        if(mapDetails!=null)
        {
           String number= mapDetails.getSubscriberId();
            msg = otp + " is your OTP for consent for not reconducting the test . Do not share the OTP with anyone.";
            Intent dl = new Intent(DELIVERED);
            dl.putExtra("num", number);
            dl.putExtra("msg", msg);
        //    otp_tv.setText((Html.fromHtml(Constants.otp + num)));

            sentIntent = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
            deliveredPI = PendingIntent.getBroadcast(this, 0,
                    dl, 0);




            smsreceiversent = new BroadcastReceiver() {
                @Override
                public void onReceive(Context arg0, Intent arg1) {

                    System.out.println("result code is " + getResultCode());
                    int resultCode = getResultCode();
                    //  Utils.appendLog(Utils.getDateTime() +": Result code for sending is...  "+resultCode);
                    switch (resultCode) {

                        case Activity.RESULT_OK:
                            dismissProgress();
                            unregisterSMSSentReceiver();

                            //Utils.showToast(NumberValidationActivity.this, "SMS sent");
                            break;

                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            dismissProgress();
                            if (progressDialog != null) {
                                if (progressDialog.isShowing()) {
                                    if (simCardList != null && simCardList.size() > 0) {
                                        if (simCardList.size() != sentsubid.size()) {
                                            simCardList.remove(smsSubscriptionId);
                                            int size = simCardList.size();
                                            size--;
                                            smsSubscriptionId = simCardList.get(size);
                                            SmsManager sms = null;
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                                                sms = SmsManager.getSmsManagerForSubscriptionId(smsSubscriptionId);
                                            } else {
                                                sms = SmsManager.getDefault();
                                            }

                                            sms.sendTextMessage(number, null, msg, sentIntent, deliveredPI);
                                        }
                                    } else {
                                        dismissProgress();

                                        showAlertDialog("Otp", Constants.GENERIC_FAILURE);
                                        unregisterSMSSentReceiver();
                                    }
                                }
                            }


                            break;

                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                            dismissProgress();
                            showAlertDialog("Otp", Constants.NO_SERVICE);
                            unregisterSMSSentReceiver();
                            break;


                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            dismissProgress();
                            showAlertDialog("Otp", Constants.ENABLE_NTWRK);
                            unregisterSMSSentReceiver();
                            break;

                        default:
                            dismissProgress();
                            showAlertDialog("Otp", "Error occured is... " + resultCode);
                            unregisterSMSSentReceiver();
                            break;


                    }

                }
            };



            registerReceiver(smsreceiversent, new IntentFilter(SENT));
        //    registerReceiver(deliveredsmsreceiver, new IntentFilter(DELIVERED));
            sendMsgViaSMSMANAGER();
          //  registerSMSReceiver();





        }
      

    }

    private void getRunTimePermissions() {
        System.out.println("getting permissions");
        int permissionReadPHoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int permissionSendSms = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        listPermissionsNeeded = new ArrayList<>();
        if (permissionReadPHoneState != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (permissionSendSms != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            System.out.println("getting permissions non empty");
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), Constants.PERMISSIONS_REQUEST);
            //  Utils.showToast(NumberValidationActivity.this,"not empty  ");
        } else {
            System.out.println("getting permissions empty");
            //    Utils.showToast(NumberValidationActivity.this,"empty  ");


        }


    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
           /* case MY_PERMISSIONS_REQUEST_SEND_SMS:
                System.out.println("calling from 2");
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendMsgViaSMSMANAGER();
                }
                else if(!shouldShowRequestPermissionRationale(permissions[0]))
                {
                    dismissProgress();
                   showAlert("This app requires the permissions to move further.Press Ok button to turn on the permissions from settings.");


                } else
                {
                    dismissProgress();
                }
                break;*/


            case Constants.PERMISSIONS_REQUEST:

                if (grantResults.length > 0) {

                    if (grantResults.length == 2) {
                        System.out.println("grant_results are " + Arrays.toString(grantResults) + "and size is " + grantResults.length);

                        if ((grantResults[0] == PackageManager.PERMISSION_GRANTED) && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                            System.out.println("requests approved " + requestCode);
                            sendMsgViaSMSMANAGER();
                        } else if (!shouldShowRequestPermissionRationale(permissions[0]) || !shouldShowRequestPermissionRationale(permissions[1])) {
                            dismissProgress();
                            //   getRunTimePermissions();
                          //  showAlert("This app requires permissions to move further.Press Ok button to turn on the permissions from settings,so please enable all the permissions!!");

                        } else {
                            dismissProgress();
                            getRunTimePermissions();
                        }


                    } else if (grantResults.length == 1) {
                        System.out.println("grant_results are " + Arrays.toString(grantResults) + "and size is " + grantResults.length);
                        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            System.out.println("request approved " + requestCode);
                            sendMsgViaSMSMANAGER();
                        } else if (!shouldShowRequestPermissionRationale(permissions[0])) {
                            dismissProgress();
                           // showAlert("This app requires the permissions to move further.Press Ok button to turn on the permissions from settings.");

                        } else {
                            dismissProgress();
                            getRunTimePermissions();
                        }

                    }
                }
                break;


        }


    }


}