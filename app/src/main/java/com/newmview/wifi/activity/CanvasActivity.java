//package com.newmview.wifi.activity;
//
//import android.Manifest;
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Point;
//import android.graphics.drawable.ColorDrawable;
//import android.hardware.Sensor;
//import android.hardware.SensorManager;
//import android.net.Uri;
//import android.net.wifi.ScanResult;
//import android.net.wifi.WifiManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.animation.AlphaAnimation;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.PopupMenu;
//import android.widget.RelativeLayout;
//import android.widget.SeekBar;
//import android.widget.TextView;
//
//import androidx.activity.result.ActivityResult;
//import androidx.activity.result.ActivityResultCallback;
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.Nullable;
//import androidx.annotation.RequiresApi;
//import androidx.appcompat.app.ActionBarDrawerToggle;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.databinding.DataBindingUtil;
//import androidx.lifecycle.Observer;
//import androidx.lifecycle.ViewModelProvider;
//import androidx.localbroadcastmanager.content.LocalBroadcastManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
//import com.mview.airtel.R;
//import com.newmview.wifi.application.MviewApplication;
//import com.newmview.wifi.bean.HistoryModel;
//import com.newmview.wifi.bean.MapModel;
//import com.newmview.wifi.bean.SubscriberModel;
//import com.newmview.wifi.canvas.CanvasDrawElements;
//import com.newmview.wifi.canvas.RectangleView;
//import com.newmview.wifi.customdialog.MyAlertDialog;
//import com.newmview.wifi.database.DB_handler;
//import com.mview.airtel.databinding.ActivityCanvasBinding;
//import com.newmview.wifi.enumtypes.AlertType;
//import com.newmview.wifi.fragment.ImageViewFragment;
//import com.newmview.wifi.helper.CoordsBuilder;
//import com.newmview.wifi.helper.Interfaces;
//import com.newmview.wifi.helper.RealPathUtil;
//import com.newmview.wifi.listenService;
//import com.newmview.wifi.other.CommonAlertDialog;
//import com.newmview.wifi.other.Constants;
//import com.newmview.wifi.other.DialogManager;
//import com.newmview.wifi.other.Utils;
//import com.newmview.wifi.viewmodel.DialogViewModel;
//import com.newmview.wifi.viewmodel.HistoryViewModel;
//import com.newmview.wifi.viewmodel.MainViewModelFactory;
//import com.newmview.wifi.viewmodel.MapVM;
//import com.newmview.wifi.viewmodel.SubscriberViewModel;
///*import com.theartofdev.edmodo.cropper.CropImage;
//import com.theartofdev.edmodo.cropper.CropImageView;*/
//
//
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//
//import static com.newmview.wifi.other.Utils.saveContent;
//
//public class CanvasActivity extends AppCompatActivity implements View.OnClickListener,
//        SeekBar.OnSeekBarChangeListener, MyAlertDialog.AlertDialogInterface {
//    private static final String TAG = "CanvasActivity";
//    private static final int SELECT_PICTURE = 1;
//    private static final int CAMERA_PIC_REQUEST =2 ;
//    private ImageView circleIv, rectangleIv;
//    private RelativeLayout rootRL;
//    List<ScanResult> mScanResults;
//    private int rectanglesCount = 0;
//    private ArrayList<Point> points = new ArrayList<>();
//    private int rectangleIndex;
//    private Paint paint;
//    private Canvas canvas;
//    private Button undoBtn, saveBtn;
//    private RectangleView rectangleView;
//    private CanvasDrawElements canvasDrawElements;
//    private SeekBar progressBar;
//    private TextView widthValue;
//    private Toolbar toolbar;
//    private ActivityCanvasBinding canvasBinding;
//    private int paintColor;
//    private ActionBarDrawerToggle drawerToggle;
//    private String flatType="NA";
//    private String windowType="NA",componentType="NA";
//    private String opening="NA";
//    private boolean fabHidden=false;
//    private DB_handler db_handler;
//    private String openingType="NA";
//    private String mapId,floorPlanPath;
//    private ImageViewFragment heatMapImageFragment;
//    private List<MapModel> mapList;
//    private ArrayList<ExtendedFloatingActionButton> fabBtns;
//    private float wifiCoordsX,wifiCoordsY;
//    private ActivityResultLauncher<Intent> launcher;
//    private BroadcastReceiver floorPlanRxr;
//    private SensorManager sm;
//    private List<Sensor> sensorlist;
//    private SensorManager mSensorManager;
//    private float currentDegree;
//    private float[] mGravity,mGeomagnetic;
//    private Sensor accelerometer,magnetometer;
//    private float azimut;
//    private int selectedId;
//    private String flowType;
//    private File output;
//    private DialogViewModel alertDialogViewModel,historyAlertVM;
//    private String source;
//    private AlertType alertType;
//    private Animation zoomIn;
//    private Animation zoomOut;
//    private boolean clearAnimation;
//    private MapVM mapModel;
//    private String subscriberId,subscriberName;
//
//    private ArrayList<SubscriberModel> subscribersList;
//    private SubscriberViewModel subscriberVM;
//    private HistoryViewModel floorPlanHistoryVM,subscriberHistoryVM;
//    private ArrayList<HistoryModel> floorPlanHistory,subscriberHistory;
//
//    @Override
//    public void takeScreenShot() {
//
//    }
//
//    @Override
//    public Bitmap getBitmap() {
//        return null;
//    }
//
//    @Override
//    public ScreenShotable onSwitch(Resourceble slideMenuItem, ScreenShotable screenShotable, int position) {
//        switch (slideMenuItem.getName()) {
//
//            case Constants.CLOSE:
//                return screenShotable;
//
//            case  Constants.STRUCTURE:
//                openPopup(0,R.menu.structure_menu);
//                break;
//            case Constants.OPENING:
//                openPopup(1,R.menu.openings_menu);
//                break;
//            case Constants.FLAT:
//                openPopup(2,R.menu.popup_menu);
//                break;
//            case Constants.LABELS:
//                showTextVBoxDialog(CanvasActivity.this);
//                canvasDrawElements.setMode(CanvasDrawElements.Mode.DRAW);
//                canvasDrawElements.setMode(CanvasDrawElements.Mode.TEXT) ;
//                break;
//            case Constants.COMPONENTS:
//                openPopup(4,R.menu.components_menu);
//                break;
//
//
//
//               // return replaceFragment(screenShotable, position);
//        }
//        return null;
//    }
//    private void openPopup(int i, int menu,View view) {
//
//
//        CommonAlertDialog.showPopup(view,
//                CanvasActivity.this, menu,new Interfaces.onMenuButtonClickListener() {
//                    @Override
//                    public void onMenuButtonClicked(MenuItem v) {
//                        implementMenuClickButtons(v);
//                    }
//                });
//
//    }
//
//    private void openPopup(int i, int menu) {
//        View view=null;
//        if(i!=-1)
//        {
//            view=canvasBinding.leftDrawer.getChildAt(i);
//        }
//        else
//        {
//            view=canvasBinding.toolbar;
//        }
//
//        CommonAlertDialog.showPopup(view,
//                CanvasActivity.this, menu,new Interfaces.onMenuButtonClickListener() {
//                    @Override
//                    public void onMenuButtonClicked(MenuItem v) {
//                        implementMenuClickButtons(v);
//                    }
//                });
//
//    }
//
//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//   //     drawerToggle.syncState();
//    }
//
//
//    public void sendHW(float height, float width) {
//        canvasBinding.hw.setText("Height "+height+" width "+width);
//    }
//
//    public void showDistance(double distanceBetweenPoints) {
//        canvasBinding.frontTv.setText(distanceBetweenPoints+"");
//    }
//
//
//    @Override
//    public void alertDialogPositiveButtonClicked(AlertType type, Object details) {
//        if(details instanceof MapModel)
//        {
//            if(type==AlertType.subscriberDetailsAlert)
//            {
//                this.subscriberName=((MapModel) details).getSubscriberName();
//                this.subscriberId=((MapModel) details).getSubscriberId();
//                SubscriberModel subscriberModel=new SubscriberModel();
//                subscriberModel.setSubscriberName(subscriberName);
//                subscriberModel.setSubscriberId(subscriberId);
//                subscriberModel.setId(subscriberName+"_"+subscriberId);
//        /*        ArrayList<String> subscriberNamesList=new ArrayList<>();
//                ArrayList<String> subscriberIdList=new ArrayList<>();
//                for(int i=0;i<subscribersList.size();i++ )
//                {
//                    String name=subscribersList.get(i).getSubscriberName();
//                    if(!Utils.checkIfListContainsString(subscriberNamesList,name))
//                        subscriberNamesList.add(subscribersList.get(i).getSubscriberName());
//
//                }
//                for(int i=0;i<subscribersList.size();i++ )
//                {
//                    String id=subscribersList.get(i).getSubscriberId();
//                    if(!Utils.checkIfListContainsString(subscriberIdList,id))
//                        subscriberIdList.add(subscribersList.get(i).getSubscriberId());
//
//                }*/
//                subscriberVM.insertNewSubsriber(subscriberModel);
//                alertDialogViewModel.cancelAlert();
//                Utils.showAlert(false, "Step 1: Select or draw your floor plan.",CanvasActivity.this, null);
//
//            }
//        }
//
//    }
//
//    @Override
//    public void alertDialogNegativeButtonClicked(AlertType type) {
//
//    }
//
//    @Override
//    public void listOptionClicked(String text) {
//        if(Utils.checkifavailable(text))
//        {
//            if(text.equalsIgnoreCase("other"))
//            {
//                alertType=AlertType.historyAlert;
//
//                DialogManager.showMyDialog(CanvasActivity.this,
//                        alertType, historyAlertVM, this, "",
//                       "", null, floorPlanHistory, "OK", "Cancel");
//
//            }
//            else {
//
//
//                if(historyAlertVM!=null) {
//                    historyAlertVM.cancelAlert();
//                }
//                if(alertDialogViewModel!=null)
//                {
//                    alertDialogViewModel.cancelAlert();
//                }
//                //  Utils.showToast(CanvasActivity.this, "Floor Type " + text);
//                this.flatType = text;
//                save();
//
//            }
//        }
//     //  MyAlertDialog.dismissDialog();
//    }
//
//    @Override
//    public void finishActivity() {
//        Log.i(TAG,"finish called");
//        finish();
//
//    }
//
//
//    public interface CustomClickListener
//    {
//        void onClick(View v);
//    }
//    /*@Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//
//        inflater.inflate(R.menu.wifi_menu, menu);
//        return true;
//    }*/
//
//
//    private void showListOfWifis() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(CanvasActivity.this);
//        LayoutInflater inflater = LayoutInflater.from(CanvasActivity.this);
//        View content = inflater.inflate(R.layout.wifi_list, null);
//        builder.setView(content);
//        RecyclerView mRecyclerView = (RecyclerView) content.findViewById(R.id.wifiRV);
//       List<ScanResult> scanResultList= mWifiManager.getScanResults();
//       /* WifiAdapter wifiAdapter =new WifiAdapter(scanResultList, CanvasActivity.this);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(CanvasActivity.this));
//        mRecyclerView.setAdapter(wifiAdapter);*/
//       AlertDialog dialog= builder.create();
//       dialog.show();
//    }
//
//    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context c, Intent intent) {
//
//            Log.i("Scan", "intent is " + intent.getData());
//            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
//                mScanResults = mWifiManager.getScanResults();
//                Log.i("Scan", "List is " + mScanResults);
//                // add your logic here
//                if (!mScanResults.isEmpty()) {
//                    currentssid = mScanResults.get(0).SSID;
//                 //   Objects.requireNonNull(getSupportActionBar()).setTitle(currentssid);
//                    Log.i("Scan", "List is " + mScanResults);
//                    for (int i = 0; i < mScanResults.size(); i++) {
//
//                        String ssid = mScanResults.get(i).SSID;
//                        Log.i("Ssid", "Ssid is " + ssid);
//
//                        int level = mScanResults.get(i).level;
//                        Log.i("level", "Level is " + level);
//                    }
//
//                }
//                else
//                {
//            //        getSupportActionBar().setTitle("Unknown");
//                }
//            }
//
//
//        }
//    };
//    private WifiManager mWifiManager;
//    private String currentssid;
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if(floorPlanRxr!=null)
//        {
//            LocalBroadcastManager.getInstance(CanvasActivity.this).unregisterReceiver(floorPlanRxr);
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//        {
//            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
//            {
//
//                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 91);
//            }
//            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
//            {
//
//                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 91);
//            }
//            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
//            {
//
//                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 90);
//            }
//            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
//            {
//
//                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 87);
//            }
//            else
//            {
//                Log.i(TAG,"location permission alloted");
//            }
//            if(checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED)
//            {
//                requestPermissions(new String[]{Manifest.permission.ACCESS_WIFI_STATE}, 88);
//            }
//            else
//            {
//                Log.i(TAG,"access wifi state permission alloted");
//            }
//            if(checkSelfPermission(Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED)
//            {
//                requestPermissions(new String[]{Manifest.permission.CHANGE_WIFI_STATE}, 88);
//            }
//            else
//            {
//                Log.i(TAG,"CHANGE_WIFI_STATE permission alloted");
//
//                mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//                registerReceiver(mWifiScanReceiver,
//                        new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//                mWifiManager.startScan();
//            }
//           // if()
//        }
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//       canvasBinding= DataBindingUtil.setContentView(this, R.layout.activity_canvas);
//       getBundleExtras();
//        setClickListeners();
//        init();
//        sensorManagement();
//        registerBroadCastRxr();
//        //instantiate shared ViewModel for alertDialogs
//        initializeDialogVM();
//        initializeSubscriberVM();
//initializeHistoryVM();
////getInfoFromOtherApp();
//
//        setUpLauncher(canvasBinding.getRoot());
//        applyAnimation();
//        mapModel=new ViewModelProvider(this,new MainViewModelFactory()).get(MapVM.class);
//
//
//
//        highlightStructureFab();
//
//    }
//
//    private void getInfoFromOtherApp() {
//        // Get intent, action and MIME type
//        Intent intent = getIntent();
//        String action = intent.getAction();
//        String type = intent.getType();
//        Utils.showToast(CanvasActivity.this,"action "+action);
//
//
//        if (Intent.ACTION_SEND.equals(action) && type != null) {
//            if ("text/plain".equals(type)) {
//                handleSendText(intent); // Handle text being sent
//            } else if (type.startsWith("image/")) {
//               // handleSendImage(intent); // Handle single image being sent
//            }
//        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
//            if (type.startsWith("image/")) {
//                //handleSendMultipleImages(intent); // Handle multiple images being sent
//            }
//        } else {
//            // Handle other intents, such as being started from the home screen
//        }
//
//
//
//    }
//    void handleSendText(Intent intent) {
//       // String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
//        String userId=intent.getStringExtra("user_id");
//        if (userId != null) {
//            Utils.showToast(CanvasActivity.this,"User id is "+userId);
//           // setResult(RESULT_OK, intent);
//            // Update UI to reflect text being shared
//        }
//    }
//
//    private void initializeHistoryVM() {
//        Bundle bundle=new Bundle();
//        bundle.putString("tag","flat_type");
//        floorPlanHistoryVM=new ViewModelProvider(this,new MainViewModelFactory(bundle)).get(HistoryViewModel.class);
//       floorPlanHistoryVM.getHistoryObservable().observe(CanvasActivity.this, new Observer<ArrayList<HistoryModel>>() {
//           @Override
//           public void onChanged(ArrayList<HistoryModel> floorPlanHistory) {
//               CanvasActivity.this.floorPlanHistory=floorPlanHistory;
//           }
//       });
//    }
//
//    private void showSubscriberAlert() {
//        alertType=AlertType.subscriberDetailsAlert;
//        Log.i(TAG,"Subscriber list "+subscribersList);
//        DialogManager.showMyDialog(CanvasActivity.this,
//                alertType, alertDialogViewModel, this, Constants.SUBSCRIBER_DETAILS_DESCRIPION,
//                Constants.SUBSCRIBER_DETAILS_TITLE, subscribersList, null, "OK", "Cancel");
//
//    }
//
//    private void initializeSubscriberVM() {
//
//  /*      Bundle bundle=new Bundle();
//        bundle.putString("tag","subscriber");
//        subscriberHistoryVM=new ViewModelProvider(this,new MainViewModelFactory(bundle)).get(HistoryViewModel.class);
//        subscriberHistoryVM.getHistoryObservable().observe(CanvasActivity.this, new Observer<ArrayList<HistoryModel>>() {
//            @Override
//            public void onChanged(ArrayList<HistoryModel> subscriberHistory) {
//                CanvasActivity.this.subscriberHistory=subscriberHistory;
//                showSubscriberAlert();
//
//            }
//        });*/
//
//        subscriberVM=new ViewModelProvider(this,new MainViewModelFactory()).get(SubscriberViewModel.class);
//        subscriberVM.getSubscriberListObservable().observe(CanvasActivity.this, new Observer<ArrayList<SubscriberModel>>() {
//            @Override
//            public void onChanged(ArrayList<SubscriberModel> subscribersList) {
//                CanvasActivity.this.subscribersList=subscribersList;
//                for(int i=0;i<subscribersList.size();i++) {
//
//                    Log.i(TAG, "Subscriber list here name " + subscribersList.get(i).getSubscriberName());
//                }
//                showSubscriberAlert();
//
//            }
//        });
//
//    }
//
//    private void initializeDialogVM() {
//        alertDialogViewModel = DialogManager.initializeViewModel(this);
//        historyAlertVM=DialogManager.initializeViewModel(this);
//        DialogManager.initializeViewModel(this);
//    }
//
//    private void highlightStructureFab() {
//        hideIcons();
//        selectedId=canvasBinding.drawLL.getId();
//        canvasBinding.structureIconsLL.setVisibility(View.VISIBLE);
//       // canvasBinding.structuresFab.setBackgroundColor(getResources().getColor(R.color.black));
//        canvasBinding.componentsLL.setVisibility(View.GONE);
//        canvasBinding.fixtureIconsLL.setVisibility(View.GONE);
//       setState(canvasBinding.drawLL,true);
//        setState(canvasBinding.selectLL,false);
//     //   canvasBinding.drawIv.getDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY );
//
//    }
//    public  void setBlinkingAnimation(View view)
//    {
//        Animation anim = new AlphaAnimation(0.5f, 1.0f);
//        anim.setDuration(90); //You can manage the blinking time with this parameter
//        anim.setStartOffset(200);
//        anim.setRepeatMode(Animation.REVERSE);
//        anim.setRepeatCount(Animation.INFINITE);
//        view.startAnimation(anim);
//    }
//    private void stopBlinkingAnimation(View view)
//    {
//        view.clearAnimation();
//
//    }
//    private void applyAnimation() {
//setBlinkingAnimation(canvasBinding.selectIcon);
//setBlinkingAnimation(canvasBinding.drawIv);
//        if (!clearAnimation) {
//            zoomIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoomin);
//            zoomOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoomout);
//
//            canvasBinding.helpFab.startAnimation(zoomIn);
//        }
//        zoomIn.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                if (!clearAnimation)
//                    canvasBinding.helpFab.startAnimation(zoomOut);
//                //commentsimgview.startAnimation(zoomIn);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//        zoomOut.setAnimationListener(new Animation.AnimationListener() {
//
//            @Override
//            public void onAnimationStart(Animation arg0) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation arg0) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation arg0) {
//                if (!clearAnimation)
//                    canvasBinding.helpFab.startAnimation(zoomIn);
//
//            }
//        });
//    }
//
//    private void setState(LinearLayout layout,boolean state)
//    {
//        layout.setSelected(state);
//      /*  if(state) {
//
//            layout.setBackground(getResources().getDrawable(R.drawable.selected_background));
//
//        }
//        else
//        {
//            layout.setBackground(getResources().getDrawable(R.drawable.unselected_background));
//        }
//*/
//    }
//    private void sensorManagement() {
//        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
///*
//        SensorEventListener sel = new SensorEventListener(){
//            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
//            public void onSensorChanged(SensorEvent event) {
//              //  float[] values = event.values;
//
//
//                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
//                    mGravity = event.values;
//
//                if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
//                    mGeomagnetic = event.values;
//
//                if (mGravity != null && mGeomagnetic != null) {
//                    float R[] = new float[9];
//                    float I[] = new float[9];
//
//                    if (SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)) {
//
//                        // orientation contains azimut, pitch and roll
//                        float orientation[] = new float[3];
//                        SensorManager.getOrientation(R, orientation);
//
//                        azimut = orientation[0];
//                    }
//                }
//                float degree = Math.round(event.values[0]);
//
//                Utils.showToast(CanvasActivity.this,"Heading: " + Float.toString(degree) + " degrees");
//
//                // create a rotation animation (reverse turn degree degrees)
//                RotateAnimation ra = new RotateAnimation(
//                        currentDegree,
//                        -degree,
//                        Animation.RELATIVE_TO_SELF, 0.5f,
//                        Animation.RELATIVE_TO_SELF,
//                        0.5f);
//
//                // how long the animation will take place
//                ra.setDuration(210);
//
//                // set the animation after the end of the reservation status
//                ra.setFillAfter(true);
//
//                // Start the animation
//               // image.startAnimation(ra);
//                currentDegree = -degree;
//
//
//            }
//        };
//*/
//
//// initialize your android device sensor capabilities
//        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
//
//        /*sensorlist = sm.getSensorList(Sensor.TYPE_ORIENTATION);
//        if(list.size()>0){
//
//            sm.registerListener(sel, (Sensor) sensorlist.get(0), SensorManager.SENSOR_DELAY_NORMAL);
//        }else{
//            Toast.makeText(getBaseContext(), "Error: No Accelerometer.", Toast.LENGTH_LONG).show();
//        }*/
//    }
//
//
//
//    private void registerBroadCastRxr() {
//        Log.i(TAG,"Floor plan Rxr "+floorPlanRxr);
//        LocalBroadcastManager.getInstance(this).registerReceiver(floorPlanRxr, new IntentFilter(SavedFloorPlans.SEND_FLOOR_PLAN));
//    }
//
//    private void init()
//{
//    db_handler=new DB_handler(MviewApplication.ctx);
//    progressBar = findViewById(R.id.progress_paint_size);
//    setActionBar();
//    setMenu();
//    showOrHideOtherFab();
//    progressBar.setOnSeekBarChangeListener(this);
//
//    paint = new Paint();
//    canvas = new Canvas();
//    canvasDrawElements = new CanvasDrawElements(CanvasActivity.this);
//    canvasBinding.rootRL.addView(canvasDrawElements);
//    canvasDrawElements.setBackgroundColor(Color.GREEN);
//    canvasDrawElements.setMode(CanvasDrawElements.Mode.DRAW);
//   // canvasDrawElements.setDrawer(CanvasDrawElements.Drawer.RECTANGLE);
//    canvasDrawElements.drawBaseRectangle(this);
//    if(Utils.checkifavailable(floorPlanPath))
//    {
//        db_handler.open();
//        mapList=db_handler.readMapDataForMapId(mapId);
//        db_handler.close();
//        heatMapImageFragment=new ImageViewFragment().newInstance(this,mapList.get(0), null);
//                 getSupportFragmentManager().beginTransaction().
//                replace(R.id.canvasRL, heatMapImageFragment).addToBackStack(null).commit();
//
//       // canvasBinding.canvasRL
//
//    }
//    if(floorPlanRxr==null) {
//        floorPlanRxr = new BroadcastReceiver() {
//
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                Log.i(TAG, "Broadcast Rxd");
//                if (intent != null) {
//                    if(canvasDrawElements.getBitmapValue()!=null) {
//                        canvasDrawElements.clearSelectedImage();
//                    }
//
//                    String path = intent.getStringExtra("path");
//                    String wifiX=intent.getStringExtra("wifiX");
//                    String wifiY=intent.getStringExtra("wifiY");
//                    if(Utils.checkifavailable(wifiX)) {
//                        if(Utils.checkIfNumeric(wifiX)) {
//                            wifiCoordsX = Float.parseFloat(wifiX);
//                        }
//                    }
//                    if(Utils.checkifavailable(wifiY))
//                    {
//                        if(Utils.checkIfNumeric(wifiY)) {
//                            wifiCoordsY = Float.parseFloat(wifiY);
//                        }
//                    }
//                    Bitmap bitmap = Utils.getBitmap(path);
//                    Log.i(TAG, "Path " + path + " bitmap " + bitmap);
//                    if (bitmap != null) {
//                        canvasDrawElements.setMode(CanvasDrawElements.Mode.SELECT);
//                        if(canvasDrawElements.getBitmapValue()==null) {
//                            canvasDrawElements.sendBitmap(bitmap, CoordsBuilder.startX,CoordsBuilder.startY);
//                            Utils.showAlert(false,Constants.IMAGE_SELECTED,CanvasActivity.this,null);
//                            setFreeDrawingMode();
//                            highlightStructureFab();
//
//                        }
//
//                    }
//                }
//
//                // do your listener event stuff
//            }
//        };
//    }
//
//}
//    private void getBundleExtras() {
//        Bundle bundle=getIntent().getExtras();
//        if(bundle!=null)
//        {
//            mapId=bundle.getString("mapId");
//            floorPlanPath=bundle.getString("floorPlanPath");
//            flowType=bundle.getString("flowType");
//        }
//
//    }
//
//    private void setMenu()
//
//    {
//        canvasBinding.drawerLayout.setScrimColor(Color.TRANSPARENT);
//        //canvasBinding.linearLayout = findViewById(R.id.left_drawer);
//        canvasBinding.leftDrawer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                canvasBinding.drawerLayout.closeDrawers();
//            }
//        });
//
//        createMenuList();
//        viewAnimator = new ViewAnimator(this, list, this, canvasBinding.drawerLayout, this);
//
//    }
//    private void createMenuList() {
//        SlideMenuItem menuItem0 = new SlideMenuItem(Constants.STRUCTURE, R.mipmap.ic_l_shape_structure);
//        list.add(menuItem0);
//        SlideMenuItem menuItem = new SlideMenuItem(Constants.OPENING, R.mipmap.ic_openings);
//        list.add(menuItem);
//        SlideMenuItem menuItem2 = new SlideMenuItem(Constants.FLAT, R.mipmap.ic_flat_type);
//        list.add(menuItem2);
//        SlideMenuItem menuItem3 = new SlideMenuItem(Constants.LABELS, R.mipmap.ic_text);
//        list.add(menuItem3);
//        SlideMenuItem menuItem4 = new SlideMenuItem(Constants.COMPONENTS, R.drawable.ic_action_wifi);
//        list.add(menuItem4);
//        SlideMenuItem menuItem5 = new SlideMenuItem(Constants.CLOSE, R.mipmap.ic_close);
//        list.add(menuItem5);
//
//
//    }
//
//    private void setActionBar() {
//        /*Toolbar toolbar = findViewById(R.id.toolbar);*/
//
//
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            ColorDrawable colorDrawable
//                    = new ColorDrawable(getResources().getColor(R.color.white));
//
//            // Set BackgroundDrawable
//            getSupportActionBar().setBackgroundDrawable(colorDrawable);
//        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_app_icon);
//
//
//    }
//
//    private void setToolbar() {
//     //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//          /*  toolbar =   canvasBinding.toolBarMain.findViewById(R.id.toolbar);
//            toolbar.setNavigationIcon(R.drawable.back_new);
//            toolbar.setTitle("Create FloorPlan");
//            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onBackPressed();
//
//                }
//            });
//            setSupportActionBar(toolbar);*/
//
//
//    }
//
//    @Override
//    public void onBackPressed() {
//
//        if(canvasDrawElements.getMode()==CanvasDrawElements.Mode.SELECT)
//        {
//            if(canvasDrawElements.getBitmapValue()==null)
//                super.onBackPressed();
//            else
//            canvasDrawElements.clearSelectedImage();
//
//        }
//        else
//        {
//            super.onBackPressed();
//        }
//    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        implementToggleButton(menu);
//
//        return true;
//    }
//    private void implementToggleButton(Menu menu) {
//        getMenuInflater().inflate(R.menu.more_options_menu, menu);
//        MenuItem moreOptions = menu.findItem(R.id.more_options);
//        moreOptions.setActionView(R.layout.more_options_layout);
//        ImageView moreMenu = menu.findItem(R.id.more_options).getActionView().findViewById(R.id.menu);
//  //      ImageView moreMenu = menu.findItem(R.id.more_options).getActionView().findViewById(R.id.menu);
//moreMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //     getSupportFragmentManager().beginTransaction();
//                Utils.takeToNextActivity(CanvasActivity.this, BuildingTabActivity.class,null);
//
//                //  getSupportFragmentManager().beginTransaction().replace(R.id.frame, Fragmentclass).addToBackStack("others").commit();
//
//            }
//        });
//
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            // Respond to the action bar's Up/Home button
//            case android.R.id.home:
//                finish();
//              /*  NavUtils.navigateUpFromSameTask(this);*/
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//    private void setClickListeners() {
//        canvasBinding.rectangleLL.setOnClickListener(this);
//        canvasBinding.lineLL.setOnClickListener(this);
//        canvasBinding.selectLL.setOnClickListener(this);
//        canvasBinding.drawLL.setOnClickListener(this);
//        canvasBinding.colorLL.setOnClickListener(this);
//        canvasBinding.circleLL.setOnClickListener(this);
//        canvasBinding.undoCv.setOnClickListener(this);
//        canvasBinding.saveBtn.setOnClickListener(this);
//
//        canvasBinding.redoCv.setOnClickListener(this);
//
//        canvasBinding.wifiBtn.setOnClickListener(this);
//
//        canvasBinding.lButton.setOnClickListener(this);
//        // by swapnil bansal 11/03/2022
//        canvasBinding.flatBtn.setOnClickListener(this);
//        canvasBinding.textBtn.setOnClickListener(this);
//       // canvasBinding.addRouterFab.setOnClickListener(this);
//        canvasBinding.addRouterIcon.setOnClickListener(this);
//        canvasBinding.doorLL.setOnClickListener(this);
//        canvasBinding.windowLL.setOnClickListener(this);
//        canvasBinding.staircaseLL.setOnClickListener(this);
//        canvasBinding.helpFab.setOnClickListener(this);
//
//        //canvasBinding.dra
//
//    }
//
//    private void showTextVBoxDialog(final Context context) {
//        Utils.showAlert(true,"Add Your label", CanvasActivity.this, new Interfaces.DialogButtonClickListener() {
//            @Override
//            public void onPositiveButtonClicked(String text) {
//                Log.i(TAG,"Adding text as "+text);
//                canvasDrawElements.addText(text);
//            }
//
//            @Override
//            public void onNegativeButtonClicked(String text) {
//
//            }
//
//            @Override
//            public void onDialogDismissed(String text) {
//
//            }
//        });
//      /*  AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
//        alertDialog.setTitle("Add Your label");
//
//      //  alertDialog.setMessage(" Text");
//
//        final EditText input = new EditText(context);
//
//
//        alertDialog.setView(input);
//        //alertDialog.setIcon(R.drawable.draw);
//
//        alertDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
//            @TargetApi(Build.VERSION_CODES.GINGERBREAD)
//            public void onClick(DialogInterface dialog, int which) {
//                if (!input.getText().toString().isEmpty()) {
////canvasDrawElements.
//                    canvasDrawElements.addText(input.getText().toString());
//
//                } else {
//                    Toast.makeText(context, "Empty Text", Toast.LENGTH_SHORT).show();
//                }
//
//                dialog.cancel();
//            }
//
//        });
//
//        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//
//        });
//
//        alertDialog.show();*/
//    }
//
//    @Override
//    public void onClick(View v) {
//      //  showOrHideOtherFab();
//        chngTheColorOfButtons(v);
//        switch (v.getId()) {
//
//            case R.id.helpFab:
//              //  canvasBinding.menuFab.shrink();
//              //  canvasBinding.helpFab.setBackgroundColor(getResources().getColor(R.color.app_theme));
//               showOrHideOtherFab();
//                if (zoomIn != null) {
//                    canvasBinding.helpFab.clearAnimation();
//                    clearAnimation = true;
//
//                }
//                if (zoomOut != null) {
//                    canvasBinding.helpFab.clearAnimation();
//                    clearAnimation = true;
//                }
//                break;
//           /* case R.id.structureIconsLL:
//                canvasBinding.structureIconsLL.setVisibility(View.VISIBLE);
//                break;*/
//
//
//            case R.id.windowLL:
//                highlightIcons(canvasBinding.fixtureIconsLL,v.getId());
//                canvasDrawElements.setMode(CanvasDrawElements.Mode.DRAW);
//                canvasDrawElements.setDrawer(CanvasDrawElements.Drawer.L_SHAPE_WITH_ARC);
//
//                this.opening= "Window";
//                break;
//
//            case R.id.addRouterIcon:
//                stopBlinkingAnimation(canvasBinding.addRouterIcon);
//                highlightIcons(canvasBinding.structureIconsLL,v.getId());
//                canvasDrawElements.setMode(CanvasDrawElements.Mode.LOGO);
//                break;
//
//            case R.id.flatBtn:
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
//                    showPopup(v);
//                }
//
//
//                break;
//           /* case R.id.cameraLL:
//                highlightIcons(canvasBinding.structureIconsLL,v.getId());
//                openCamera();
//                break;*/
//
//            case R.id.selectLL:
//stopBlinkingAnimation(canvasBinding.selectIcon);
//stopBlinkingAnimation(canvasBinding.drawIv);
//setBlinkingAnimation(canvasBinding.addRouterIcon);
//                CommonAlertDialog.showPopup(v,
//                        CanvasActivity.this, R.menu.select_menu,new Interfaces.onMenuButtonClickListener() {
//                            @Override
//                            public void onMenuButtonClicked(MenuItem v) {
//                                /*if(v.getItemId()==R.id.savedFloorPlanItem)
//                                {
//                                    highlightIcons(canvasBinding.structureIconsLL,v.getItemId());
//                                }*/
//                                implementMenuClickButtons(v);
//                            }
//                        });
//
//                /*showTextVBoxDialog(CanvasActivity.this);
//                canvasDrawElements.setMode(CanvasDrawElements.Mode.DRAW);
//                canvasDrawElements.setMode(CanvasDrawElements.Mode.TEXT);*/
//                highlightIcons(canvasBinding.structureIconsLL,v.getId());
//               /* canvasDrawElements.setMode(CanvasDrawElements.Mode.SELECT);
//                 imageChooser();*/
//                canvasBinding.saveBtn.setVisibility(View.VISIBLE);
//                break;
//            case R.id.undoCv:
//                canvasDrawElements.undo();
//
//                break;
//            case R.id.redoCv:
//                canvasDrawElements.redo();
//                break;
//           case R.id.circleLL:
//               highlightIcons(canvasBinding.structureIconsLL,v.getId());
//                canvasDrawElements.setMode(CanvasDrawElements.Mode.DRAW);
//                canvasDrawElements.setDrawer(CanvasDrawElements.Drawer.ELLIPSE);
//                break;
//          /*  case R.id.drawLL:
//                showTextVBoxDialog(CanvasActivity.this);
//                canvasDrawElements.setMode(CanvasDrawElements.Mode.DRAW);
//                canvasDrawElements.setMode(CanvasDrawElements.Mode.TEXT) ;
//                break;*/
//
//            case R.id.rectangleLL:
//                highlightIcons(canvasBinding.structureIconsLL,v.getId());
//                canvasDrawElements.setMode(CanvasDrawElements.Mode.DRAW);
//                canvasDrawElements.setDrawer(CanvasDrawElements.Drawer.RECTANGLE);
//                break;
//            case R.id.l_button:
//
//                canvasDrawElements.setMode(CanvasDrawElements.Mode.DRAW);
//                canvasDrawElements.setDrawer(CanvasDrawElements.Drawer.L_SHAPE);
//                break;
//            case R.id.drawLL:
//                stopBlinkingAnimation(canvasBinding.selectIcon);
//                stopBlinkingAnimation(canvasBinding.drawIv);
//                setBlinkingAnimation(canvasBinding.addRouterIcon);
//                alertType=AlertType.drawAlert;
//                highlightIcons(canvasBinding.structureIconsLL,v.getId());
//                CommonAlertDialog.showPopup(v,
//                        CanvasActivity.this, R.menu.structure_menu,new Interfaces.onMenuButtonClickListener() {
//                            @Override
//                            public void onMenuButtonClicked(MenuItem v) {
//                                /*if(v.getItemId()==R.id.savedFloorPlanItem)
//                                {
//                                    highlightIcons(canvasBinding.structureIconsLL,v.getItemId());
//                                }*/
//                                implementMenuClickButtons(v);
//                            }
//                        });
//                canvasBinding.saveBtn.setVisibility(View.VISIBLE);
//
//              /*  canvasDrawElements.setMode(CanvasDrawElements.Mode.DRAW);
//                canvasDrawElements.setDrawer(CanvasDrawElements.Drawer.PEN);*/
//
//                //  paintMode.setText(getResources().getString(R.string.fa_pencil));
//                break;
//            case R.id.saveBtn:
//askUserToAddFloorTypeAndSave();
//
//                break;
//            case R.id.wifiBtn:
//                canvasDrawElements.setMode(CanvasDrawElements.Mode.LOGO);
//                break;
//            case R.id.lineLL:
//                highlightIcons(canvasBinding.structureIconsLL,v.getId());
//                canvasDrawElements.setMode(CanvasDrawElements.Mode.DRAW);
//                canvasDrawElements.setDrawer(CanvasDrawElements.Drawer.LINE);
//                break;
//            case R.id.colorLL:
//                highlightIcons(canvasBinding.structureIconsLL,v.getId());
//                CommonAlertDialog.getColorFromColorPickerDialog(CanvasActivity.this, null,
//                        new Interfaces.ChangeColorListener() {
//                    @Override
//                    public void changeColor(int color, String legend) {
//                        if(canvasDrawElements!=null)
//                        {
//                            Log.i(TAG,"Returned color is "+color);
//                            canvasDrawElements.setPaintFillColor(color);
//                            canvasDrawElements.setPaintStrokeColor(color);
//                        }
//                    }
//                });
//
//
//                break;
//
//
//        }
//    }
//
//    private void askUserToAddFloorTypeAndSave() {
//        try {
//alertType= AlertType.floorPlanAlert;
//            DialogManager.showMyDialog(CanvasActivity.this,
//                    alertType, alertDialogViewModel, this, null, null, null,floorPlanHistory, "OK", "Cancel");
//
//          //  save();
//
//        }
//        catch (Exception e)
//        {
//            //Utils.appendCrashLog("Crash while opening saving floor map "+e.toString());
//            e.printStackTrace();
//        }
//    }
//
//    private void save() {
//        hideIcons();
//
//        List<CanvasDrawElements.Vector2> wifiCoords=  canvasDrawElements.getWifiCoords();
//        if(wifiCoords!=null)
//        {
//            if(wifiCoords.size()>0) {
//                wifiCoordsX = wifiCoords.get(0).x;
//                wifiCoordsY = wifiCoords.get(0).y;
//            }
//        }
//        Bitmap bitmap = Utils.getViewBitmap(canvasDrawElements);
//        //Utils.appendCrashLog("Bitmap while saving floor map "+bitmap);
//        String fileName = "Floor_Plan_" + Utils.getDateTime() + ".png";
//        String filepath = saveContent(bitmap, CanvasActivity.this, false,
//                null, null, fileName);
//      // Utils.appendCrashLog("file path while saving floor map "+filepath);
//        db_handler.open();
//        MapModel mapDetails=new MapModel();
//        mapDetails.setSsidName("");
//        mapDetails.setFloorPlan(fileName);
//        mapDetails.setTechnology("Wifi");
//        mapDetails.setFlatType(flatType);
//        mapDetails.setOpening(opening);
//        mapDetails.setOpeningType(openingType);
//        mapDetails.setComponent(componentType);
//        mapDetails.setSurveyFor("NA");
//        mapDetails.setLocationType("NA");
//        mapDetails.setAddress("NA");
//        if(listenService.gps!=null) {
//            mapDetails.setLatitude(listenService.gps.getLatitude() + "");
//            mapDetails.setLongitude(listenService.gps.getLongitude() + "");
//        }
//        else
//        {
//            mapDetails.setLatitude("0");
//            mapDetails.setLongitude("0");
//        }
//        mapDetails.setDeviceId(Utils.getImsi(CanvasActivity.this));
//        mapDetails.setWorkOrderId("NA");
//        mapDetails.setMapPath("");
//        mapDetails.setFinalMapName("");
//        mapDetails.setFinalMapPath("");
//        mapDetails.setMsisdn(Utils.getMyContactNum(CanvasActivity.this));
//        mapDetails.setFloorPlanPath(filepath);
//        mapDetails.setWifiX(wifiCoordsX+"");
//        mapDetails.setWifiY(wifiCoordsY+"");
//        mapDetails.setSubscriberName(subscriberName);
//        mapDetails.setSubscriberId(subscriberId);
//
//
//
//       long success= mapModel.insertNewMap(mapDetails);
//      /*  int success= db_handler.insertMapData("", fileName,
//                "Wifi", flatType, opening, openingType, componentType,
//                "NA", "NA", "NA",
//                "NA", "NA",
//                Utils.getImsi(CanvasActivity.this), "NA", "",
//                "", "", Utils.getMyContactNum(CanvasActivity.this),
//                filepath,wifiCoordsX,wifiCoordsY);*/
//        if(success>0)
//        {
//            HistoryModel historyModel=new HistoryModel();
//            historyModel.setTag("flat_type");
//            historyModel.setValue(flatType);
//        floorPlanHistoryVM.insertNewEntry(historyModel);
//
//            Utils.showToast(CanvasActivity.this,"Floor Plan Created successfully.");
//            MapModel mapModel= db_handler.readMapData().get(0);
//            Intent intent = new Intent(CanvasActivity.this, CreateWalkMapActivity.class);
//            intent.putExtra("floorMapPath", filepath);
//            intent.putExtra("flatType", flatType);
//            intent.putExtra("componentType", componentType);
//            intent.putExtra("opening", opening);
//            intent.putExtra("mapId",mapModel.getMapId());
//            intent.putExtra("openingType", windowType);
//            intent.putExtra("flowType",flowType);
//            if(wifiCoords!=null)
//            {
//                if(wifiCoords.size()>0) {
//                    Log.i(TAG,"Coord x "+wifiCoords.get(0).x +"coordy "+wifiCoords.get(0).y);
//                    intent.putExtra("wifiCoordsX", wifiCoords.get(0).x);
//                    intent.putExtra("wifiCoordsY", wifiCoords.get(0).y);
//                }
//            }
//            startActivity(intent);
//        }
//        else
//        {
//            Utils.showToast(CanvasActivity.this,"Unable to save this plan.");
//
//        }
//        db_handler.close();
//      //  Utils.appendCrashLog("finishing floor plan and starting new activity ");
//        finish();
//
//    }
//
//    private void chngTheColorOfButtons(View v) {
//        if(v instanceof ExtendedFloatingActionButton && v.getId()!=R.id.helpFab) {
//            for (int i = 0; i < fabBtns.size(); i++) {
//                if (fabBtns.get(i).getId() == v.getId()) {
//                    Log.i(TAG,"Changing color for "+fabBtns.get(i));
//                    fabBtns.get(i).setBackgroundColor(getResources().getColor(R.color.app_theme));
//                } else {
//                    fabBtns.get(i).setBackgroundColor(getResources().getColor(R.color.black));
//                }
//            }
//        }
//    }
//
//    private void showOrHideOtherFab() {
//        if(fabHidden)
//        {
//
//            showIcons();
//        }
//        else
//        {
//            hideIcons();
//        }
//    }
//
//    private  void hideIcons() {
//        canvasBinding.helpTitleLL.setVisibility(View.GONE);
//        canvasBinding.helpStep1LL.setVisibility(View.GONE);
//        canvasBinding.helpStep2LL.setVisibility(View.GONE);
//        canvasBinding.helpStep3LL.setVisibility(View.GONE);
//
//        fabHidden=true;
//    }
//
//    private void showIcons() {
//        canvasBinding.helpTitleLL.setVisibility(View.VISIBLE);
//        canvasBinding.helpStep1LL.setVisibility(View.VISIBLE);
//        canvasBinding.helpStep2LL.setVisibility(View.VISIBLE);
//        canvasBinding.helpStep3LL.setVisibility(View.VISIBLE);
//
//        fabHidden=false;
//
//    }
//    public void highlightIcons(LinearLayout layout, int itemId)
//    {
//        selectedId=itemId;
//        Log.i(TAG,"count "+layout.getChildCount());
//
//        for(int i=0;i<layout.getChildCount();i++)
//        {
//            View v=layout.getChildAt(i);
//            int id=v.getId();
//            if(id!=itemId)
//            {
//                setState((LinearLayout) v,false);
//            }
//            else
//            {
//                Log.i(TAG,"Set state true for id "+id);
//                setState((LinearLayout) v,true);
//            }
//        }
//    }
//
//    public void  implementMenuClickButtons(MenuItem view)
//    {
//        switch (view.getItemId()){
//            case R.id.color:
//                /* highlightIcons(canvasBinding.structureIconsLL,v.getId());*/
//                CommonAlertDialog.getColorFromColorPickerDialog(CanvasActivity.this, null,
//                        new Interfaces.ChangeColorListener() {
//                    @Override
//                    public void changeColor(int color, String legend) {
//                        if(canvasDrawElements!=null)
//                        {
//                            Log.i(TAG,"Returned color is "+color);
//                            canvasDrawElements.setPaintFillColor(color);
//                            canvasDrawElements.setPaintStrokeColor(color);
//                        }
//                    }
//                });
//                 break;
//            case R.id.savedFloorPlanItem:
//                hideIcons();
//
//                canvasBinding.componentsLL.setVisibility(View.GONE);
//                canvasBinding.fixtureIconsLL.setVisibility(View.GONE);
//
//                Utils.takeToNextActivity(CanvasActivity.this,SavedFloorPlans.class,null);
//                break;
//            case R.id.galleryId:
//                canvasDrawElements.setMode(CanvasDrawElements.Mode.SELECT);
//                imageChooser();
//                break;
//            case R.id.cameraId:
//              //  highlightIcons(canvasBinding.structureIconsLL,v.getId());
//                openCamera();
//                break;
//
//            case R.id.bhkOne:
//                this.flatType=String.valueOf(view.getTitle());
//               // i.putExtra("HOUSE","bhkOne");
//                break;
//
//            case R.id.bhkTwo:
//                this.flatType=String.valueOf(view.getTitle());
//              //  i.putExtra("HOUSE","bhkTwo");
//                break;
//            case R.id.bhkThree:
//                this.flatType=String.valueOf(view.getTitle());
//            //    i.putExtra("HOUSE","bhkThree");
//                break;
//            case R.id.bhkFour:
//                this.flatType=String.valueOf(view.getTitle());
//            //    i.putExtra("HOUSE","bhkFour");
//                break;
//            case R.id.bhkFive:
//                this.flatType=String.valueOf(view.getTitle());
//                break;
//            case R.id.rectangleShape:
//                canvasDrawElements.setMode(CanvasDrawElements.Mode.DRAW);
//                canvasDrawElements.setDrawer(CanvasDrawElements.Drawer.RECTANGLE);
//
//                break;
//            case R.id.circleShape:
//                canvasDrawElements.setMode(CanvasDrawElements.Mode.DRAW);
//                canvasDrawElements.setDrawer(CanvasDrawElements.Drawer.ELLIPSE);
//                break;
//            case R.id.line:
//                canvasDrawElements.setMode(CanvasDrawElements.Mode.DRAW);
//                canvasDrawElements.setDrawer(CanvasDrawElements.Drawer.LINE);
//                break;
//            case R.id.lshape:
//                canvasDrawElements.setMode(CanvasDrawElements.Mode.DRAW);
//                canvasDrawElements.setDrawer(CanvasDrawElements.Drawer.L_SHAPE);
//                break;
//            case R.id.draw:
//                setFreeDrawingMode();
//
//                break;
//            case R.id.window:
//                canvasDrawElements.setMode(CanvasDrawElements.Mode.DRAW);
//                canvasDrawElements.setDrawer(CanvasDrawElements.Drawer.L_SHAPE_WITH_ARC);
//                this.opening= String.valueOf(view.getTitle());
//                openPopup(-1,R.menu.window_menu);
//                break;
//            case R.id.door:
//            case R.id.staircase:
//
//                this.opening= String.valueOf(view.getTitle());
//                break;
//
//            case R.id.wifiItem:
//                this.componentType=String.valueOf(view.getTitle());
//                canvasDrawElements.setMode(CanvasDrawElements.Mode.LOGO);
//                break;
//            case R.id.glass:
//            case R.id.officeWindow:
//            case R.id.wood:
//            case R.id.metal:
//                this.windowType=String.valueOf(view.getTitle());
//                canvasDrawElements.setMode(CanvasDrawElements.Mode.DRAW);
//                canvasDrawElements.setDrawer(CanvasDrawElements.Drawer.L_SHAPE_WITH_ARC);
//                break;
//
//
//        }
//
//    }
//
//    private void setFreeDrawingMode() {
//        canvasDrawElements.setMode(CanvasDrawElements.Mode.DRAW);
//        canvasDrawElements.setDrawer(CanvasDrawElements.Drawer.PEN);
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
//    private void showPopup(View v) {
//        PopupMenu popupMenu = null;
//
//        popupMenu = new PopupMenu(CanvasActivity.this,v, Gravity.BOTTOM,0,R.style.OverflowMenuStyle);
//        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
//        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
//            @Override
//            public boolean onMenuItemClick(MenuItem item){
//                Intent i = new Intent(CanvasActivity.this, CreateWalkMapActivity.class);
//                switch (item.getItemId()){
//                    case R.id.bhkOne:
//                        i.putExtra("HOUSE","bhkOne");
//                        break;
//
//                    case R.id.bhkTwo:
//                        i.putExtra("HOUSE","bhkTwo");
//                        break;
//                    case R.id.bhkThree:
//                        i.putExtra("HOUSE","bhkThree");
//                        break;
//                    case R.id.bhkFour:
//                        i.putExtra("HOUSE","bhkFour");
//                        break;
//
//
//                    default:
//                        return false;
//                }
//
//                startActivity(i);
//                finish();
//
//                return true;
//            }
//        });
//
//        popupMenu.show();
//    }
//void openCamera()
//{
//    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//   File dir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//     output=new File(dir,"Cam_"+Utils.getDateTime().replaceAll(" ", "").
//             replaceAll("-","")
//             .replaceAll(":", "")+".png");
//    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(output));
//    launcher.launch(cameraIntent);
//
//  //  startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
//}
//    void imageChooser() {
///*
//        TedImagePicker.with(this)
//                .mediaType(MediaType.IMAGE)
//                .startMultiImage(new OnMultiSelectedListener() {
//                    @Override
//                    public void onSelected(List<? extends Uri> list) {
//
//                    }
//                });
//*/
//
//
///*
//        TedImagePicker.with(CanvasActivity.this)
//                .mediaType(MediaType.IMAGE)
//                .start(new OnSelectedListener() {
//                    @Override
//                    public void onSelected(Uri selectedImageUri) {
//                        if (null != selectedImageUri) {
//                            // finish();
//                            File file = new File(selectedImageUri. getPath());
//                            Log.d(TAG,"image path is "+selectedImageUri.toString());
//                            String path=new FileUtils(CanvasActivity.this).getPath(selectedImageUri);
//                            //  String[] split = file. getPath();
//                            // canvasDrawElements.sendBitmap()
//
//                            // update the preview image in the layout
//                            Bitmap bitmap=Utils.getBitmap(path);
//                            if (bitmap != null) {
//
//                                canvasDrawElements.sendBitmap(bitmap);
//
//                            }
//                        }
//
//                    }
//                });
//*/
//
//
//
//
//
//        // create an instance of the
//        // intent of the type image
//       Intent i = new Intent();
//        i.setType("image/*");
//        i.setAction(Intent.ACTION_PICK);
//        startActivityForResult(i, SELECT_PICTURE);
//        //mStartForResult.launch(Intent.createChooser(i, "Select Picture"));
//    }
//
//    public void setUpLauncher(View view) {
//        launcher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(ActivityResult result) {
//                        if (result.getResultCode() == Activity.RESULT_OK) {
//                            // There are no request codes
//                          /*  Intent data=result.getData();
//                            if(data!=null)
//
//                            {*/
//                              /*  Log.i(TAG,"Camera launched "+"path "+
//                                        Utils.getRealPathFromURI(Uri.fromFile(output),CanvasActivity.this));*/
//
////Bitmap photo=Utils.getBitmap( Utils.getRealPathFromURI(Uri.fromFile(output),CanvasActivity.this));
//
//
//
//
//                            Uri selectedImageUri = Uri.fromFile(output);
//
//                            if (null != selectedImageUri) {
//                                // finish();
////                                Intent intent = CropImage.activity(selectedImageUri)
////                                        .setGuidelines(CropImageView.Guidelines.ON)
////                                        .setFixAspectRatio(false)
////                                        .getIntent(CanvasActivity.this);
////                                startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
////                                source="Camera";
//                            }
//
//
//                             /*   Bitmap photo=Utils.getBitmap( RealPathUtil.getRealPath(CanvasActivity.this,Uri.fromFile(output)));
//                           //     Bitmap photo = (Bitmap) data.getExtras().get("data");
//                                if(photo!=null) {
//                                    Bitmap mutableBitmap = photo.copy(Bitmap.Config.ARGB_8888, true);
//                                    canvasDrawElements.sendBitmap(mutableBitmap, CoordsBuilder.startX, CoordsBuilder.startY);
//                                }*/
//                                /*Uri tempUri = getImageUri(getApplicationContext(), photo);
//                                Bundle bundle = new Bundle();
//                                File finalFile = new File(getRealPathFromURI(tempUri,getApplicationContext()));
//                                bundle.putString("imgUri", String.valueOf(finalFile));
//                                // CALL THIS METHOD TO GET THE ACTUAL PATH
//                                EditImageFragment editImageFragment = EditImageFragment.newInstance(bundle);
//
//                                getSupportFragmentManager().beginTransaction().
//                                        replace(R.id.canvasRL, editImageFragment).addToBackStack(null).commit();*/
//
//                             //   }
//
//                        }
//                    }
//                });
//
//        //Create Intent
//       /* Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/jpg");
//        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
//        //Launch activity to get result
//        someActivityResultLauncher.launch(intent);*/
//    }
//    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
//
//
//    //ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultCallback<>().)
//
//  /*  ActivityResultLauncher<Intent>   mStartForResult= registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>(){
//        @Override
//        public void onActivityResult(ActivityResult result){
//
//        }
//    });*/
///*
//    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//                    if (result.getResultCode() == Activity.RESULT_OK) {
//                        Intent intent = result.getData();
//
//                        Uri selectedImageUri = intent.getData();
//                        if (null != selectedImageUri) {
//                            // update the preview image in the layout
//                            Intent mainIntent=new Intent(CanvasActivity.this,MainActivity.class);
//                            mainIntent.putExtra("floorMapPath",selectedImageUri.toString());
//                            startActivity(intent);
//                        }
//                        // Handle the Intent
//                    }
//                }
//            });
//*/
//
//    private void drawRecUsingRectView() {
//        rectanglesCount++;
//        for (int i = 0; i < 4; i++) {
//            points.add(null);
//        }
//        rectangleIndex++;
//
//
//        rectangleView = new RectangleView(
//                CanvasActivity.this, rectanglesCount, points, rectanglesCount, paint, canvas);
//        rectangleView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
//
//        rectangleView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                // v.setTag(rectanglesCount-1);
//                      /*  int ct=rectanglesCount-1;
//                        View view=rootRL.getChildAt(ct);
//                        view.setTag(ct);
//                        Log.i(TAG,"View "+view+" setTag "+ct);*/
//                     /*   v.setTag(rectangleIndex);
//                        Log.i(TAG,"rectangle clicked.."+(Integer) v.getTag() +" rect object "+v);
//                        rectangleView.setIndexOfRectangleTouched((Integer) v.getTag());*/
//                return false;
//            }
//        });
//
//        rootRL.addView(rectangleView);
//    }
//
//    @Override
//    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        if (canvasDrawElements.getMode() == CanvasDrawElements.Mode.TEXT) {
//            canvasDrawElements.setFontSize(progress);
//        } else {
//            canvasDrawElements.setPaintStrokeWidth(progress);
//        }
//
////        widthValue.setText(String.valueOf(progress) + "/500");
//    }
//
//    @Override
//    public void onStartTrackingTouch(SeekBar seekBar) {
//
//    }
//
//    @Override
//    public void onStopTrackingTouch(SeekBar seekBar) {
//
//    }
//private void setSelectedImageToGallery(String path)
//{
//    Log.i(TAG,"Result path is "+path);
//   /* File file = new File(selectedImageUri. getPath());
//    Log.d(TAG,"image path is "+selectedImageUri.toString());
//    String path=new FileUtils(CanvasActivity.this).getPath(selectedImageUri);*/
//    //  String[] split = file. getPath();
//    // canvasDrawElements.sendBitmap()
//
//    // update the preview image in the layout
//    Bitmap bitmap=Utils.getBitmap(path);
//    if (bitmap != null) {
//
//        canvasDrawElements.sendBitmap(bitmap, CoordsBuilder.startX, CoordsBuilder.startY);
//
//    }
//}
//
//}
