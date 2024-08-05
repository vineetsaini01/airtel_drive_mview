package com.newmview.wifi.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.newmview.wifi.CommonFunctions;
import com.newmview.wifi.MyPhoneStateListener;
import com.mview.airtel.R;
import com.newmview.wifi.SlidingTab.MyTabControl;
import com.newmview.wifi.TinyDB;
import com.newmview.wifi.listenService;
import com.newmview.wifi.mView_HealthStatus;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;
import com.newmview.wifi.other.WifiConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_OK;
import static androidx.core.content.ContextCompat.checkSelfPermission;
import static com.newmview.wifi.fragment.HomeFragment.googleMap;


public class ViewMapFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback {


    public static final String ARG_INITIAL_POSITION = "ARG_INITIAL_POSITION";
    private static final int MY_PERMISSIONS_REQUEST =5 ;
    private static final int STORAGE_PERMISSION =1 ;
    private static final int LOC_PERMISSIONS_REQUEST = 2;


    public ImageView companyLogo;

    public static float deviceWidth = 0;
    Activity parentActivity;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    boolean dontRefreshScreen;
    MapView mMapView;
    TextView textview, latlngtext, test_stats;
    Button btnStartLocationUpdates, clearBtn, saveBtn;
    ToggleButton camerafollowtg;
    LinearLayout rlayout;


    public static final int MAP_SETTINGS_ACTIVITY = 1781;

    private Location mLastLocation;
    private float distanceTravelled;
    LatLng latLng;
    Marker currLocationMarker;
    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters
    public static boolean mRequestingLocationUpdates = false;
    private String TAG = "MAP view";
    private long time_startTest;

    boolean showMarkers = true;
    int showMarkersAfterEverynMeters = 10;

    Handler mHandler;
    int refreshFrequency = 1000; //1 second
    private String imsi;
    private boolean locationPermitted=false;

    public ViewMapFragment() {
        // Required empty public constructor
    }

    Bundle savedInstanceState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    private void seekPermissions() {

        if (getActivity() != null) {
            if (checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION);

            else
            {
                startTest();
            }

        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.savedInstanceState = savedInstanceState;
        View v = inflater.inflate(R.layout.mapview_new, container, false);
        parentActivity = getActivity();
        View view1 = inflater.inflate(R.layout.padding, null);
        initVar(v, view1);
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.mapsettings, menu);
    }

    public void initVar(View v, View v2) {

        textview = (TextView) v.findViewById(R.id.textview);
        test_stats = (TextView) v.findViewById(R.id.test_stats);


        latlngtext = (TextView) v.findViewById(R.id.latlngtext);
        btnStartLocationUpdates = (Button) v.findViewById(R.id.startBtn);
        clearBtn = (Button) v.findViewById(R.id.clearBtn);
        saveBtn = (Button) v.findViewById(R.id.saveBtn);
        rlayout = (LinearLayout) v.findViewById(R.id.rlayout);
        rlayout.setVisibility(View.GONE);

        camerafollowtg = (ToggleButton) v.findViewById(R.id.camerafollowtg);

        if (mView_HealthStatus.connectionTypeIdentifier == 1)
            textview.setText(mView_HealthStatus.connectionType+"("+ WifiConfig.getConnectedWifiDetails().getSsidName() + ")" );
          //  textview.setText(mView_HealthStatus.OperatorName + "  " + mView_HealthStatus.connectionType);
        else
            textview.setText(mView_HealthStatus.OperatorName + "  " + mView_HealthStatus.connectionType + " " + mView_HealthStatus.strCurrentNetworkState);


        buildGoogleApiClient();
        loadSettings();
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();


        //createLocationRequest();
        seekLocationPermissions();
        mMapView.getMapAsync(this);

        // Toggling the periodic location updates
        btnStartLocationUpdates.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                togglePeriodicLocationUpdates();
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (capturedLocationArray != null) {
                    if (mRequestingLocationUpdates) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                parentActivity, R.style.AlertDialogTheme);
                        alertDialog.setTitle("Message");
                        alertDialog.setMessage("Please stop the test first.");
                        alertDialog.setCancelable(true);

                        alertDialog.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                        alertDialog.show();
                    } else {
                        capturedLocationArray.clear();
                        googleMap.clear();
                        test_stats.setText("");
                        rlayout.setVisibility(View.GONE);
                        atleastonemarkerplotted = false;
                    }
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (capturedLocationArray != null) {
                    /*if (mRequestingLocationUpdates) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                parentActivity, R.style.AlertDialogTheme);
                        alertDialog.setTitle("Message");
                        alertDialog.setMessage("Please stop the test first.");
                        alertDialog.setCancelable(true);

                        alertDialog.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                        alertDialog.show();
                    } else {*/

                    saveTheTest();

                   // }
                }
            }
        });

    }

    private void saveTheTest() {

        int size = capturedLocationArray.size();


        String header = "time" +","+ "Lat" + "," + "Lon" + "," + "Networktype" + "," + "networkproto" + "," + "mcc" + "," + "mnc" + "," +
                "imsi"+","+ " pci"+"," +"earfcn" +  ","+ "4G_ta "+"," +"4Q_CQI"+","+"4G_RSSI"+","+"RSRP"+","+"sinr"+","+"TAC"+","+
                "enb"+","+"4G_cellid"+","+"RSRQ"+","+"psc"+","+"uarfcn"+","+"3G_CQI"+","+"3G_RSSI"+","+"RSCP"+","+"ecno"+","+
                "3G_lac"+","+"NodeBId"+","+"3G_cellid"+","+"arfcn"+","+"2G_ta"+","+"rxlev"+","+"rxqual"+","+"2G_lac"+","+"siteid"+
                ","+"2G_cellid"+","+ "speed" + "," + "distance" + "," + "\n";
                               /* "Psc" + "," + "rnc" + "," + "EVDORSSI" + "," + "CDMARSSI" + "," + "CDMAECIO" + "," + "EVDOSNR" + "," + "ltePCI" + "," + "lteTAC" + "," +
                                "lteRSSI" + "," + "lteRSRQ" + "," + "lteSNR" + "," + "lteRSRP" + "," + "lteCQI" + "," + "lteArfcn" + "," + "speed" + "," + "distance" + "," + "\n";
*/
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        File file = new File(path, "mview");
        if (!file.exists()) {
            file.mkdirs();
        }

        long millis = time_startTest;

        //String hms = String.format("%02d-%02d-%02d", TimeUnit.MILLISECONDS.toHours(millis),
        //     TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
        //   TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_HH_mm_ss");
        Date resultdate = new Date(millis);
        String hms = sdf.format(resultdate);

        file = new File(path, "mview" + "/" + mView_HealthStatus.userid + "_" + hms + "_map.csv");
        try {
            FileWriter out = new FileWriter(file, true);
            out.append(header);
            for (int i = 0; i < size; i++) {
                ViewMapFragment.CapturedLocation c = capturedLocationArray.get(i);
                Location p = c.loc;

                int io = Integer.MAX_VALUE;

                if(Constants.IMSI!=null)
                {
                    imsi=Constants.IMSI;
                }
                else
                {
                    Constants.IMSI= Utils.getImsi(getActivity());
                    imsi=Constants.IMSI;
                }



                String lineitem = c.timestamp + "," + p.getLatitude() + "," + p.getLongitude() + "," + c.networktype + "," + c.networkproto + "," + c.mcc + "," +
                        c.mnc + "," + imsi+","+
                        c.pci_4G +"," +c.earfcn_4G +","+c.ta_4g +"," +c.CQI_4G+","+c.RSSI_4G+","+c.RSRP_4G+","+c.sinr_4G+","+c.TAC_4G+","+
                        c.enb_4G+","+c.cellid_4G+","+c.RSRQ_4G+","+c.psc_3G+","+c.uarfcn_3G+","+c.CQI_3G+","+c.RSSI_3G+","+c.RSCP_3G+","+c.ecno_3G+","+
                        c.lac_3G+","+c.NodeBId_3G+","+c.cellid_3G+","+c.arfcn_2G+","+c.ta_2G+","+c.rxlev_2G+","+c.rxqual_2G+","+c.lac_2G+","+c.siteid_2G+
                        ","+c.cellid_2G+","+c.speedkmph + "," + c.distance +","+"\n";



                                          /* String lineitem = c.timestamp + "," + p.getLatitude() + "," + p.getLongitude() + "," + c.networktype + "," + c.networkproto + "," + c.mcc + "," +
                                        c.mnc + "," + c.Cid + "," + c.Lac + "," + c.rscp + "," +
                                        psc + "," + c.rnc + "," + c.EVDORSSI + "," + c.CDMARSSI + "," + c.CDMAECIO + "," + c.EVDOSNR + "," + c.ltePCI + "," + tac + "," +
                                        c.lteRSSI + "," + c.lteRSRQ + "," + c.lteSNR + "," + c.lteRSRP + "," + cqi + "," + arfcn + "," + c.speedkmph + "," + c.distance +
                                        "," + "\n";*/
                out.append(lineitem);
            }//end for loop
            out.flush();
            out.close();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    parentActivity, R.style.AlertDialogTheme);
            alertDialog.setTitle("Message");
            alertDialog.setMessage("Map Test data saved successfully as " + path + "/mview" + "/" + mView_HealthStatus.userid + "_" + hms + "_map.csv");
            alertDialog.setCancelable(true);

            alertDialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int id) {
                            dialog.cancel();
                        }
                    });
            alertDialog.show();
        } catch (Exception e) {
            System.out.println("excepton in viewmap " + e.toString());
            e.printStackTrace();
        }
        capturedLocationArray.clear();
        googleMap.clear();
        test_stats.setText("");
        rlayout.setVisibility(View.GONE);
    }


    private void initilializeGmap(GoogleMap mMap) {

        googleMap = mMap;


        // For showing a move to my location button
        googleMap.setMyLocationEnabled(true);

        // For dropping a marker at a point on the Map
        LatLng sydney = new LatLng(listenService.gps.getLatitude(), listenService.gps.getLongitude());
        //googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));

        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);

        // For zooming automatically to the location of the marker
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                new LatLng(listenService.gps.getLatitude(), listenService.gps.getLongitude()), 15);
        //CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
        //googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        googleMap.animateCamera(cameraUpdate);
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
//        if (visible) {
//            WarrantyTabsActivity.position = 1;
//        }
    }

    protected synchronized void buildGoogleApiClient() {
        //Toast.makeText(this,"buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(parentActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    private void togglePeriodicLocationUpdates() {

        if (!mRequestingLocationUpdates) {
          //  Utils.showToast(getActivity(),"in if...");
            seekPermissions();


        } else {


         //   Utils.showToast(getActivity(),"in else...");
            // Changing the button text
            btnStartLocationUpdates.setText("Start Test");
            //  btnStartLocationUpdates.setBackgroundColor(Color.parseColor("#7aff24"));
            //btnStartLocationUpdates.getBackground().setColorFilter(0xff7aff24, PorterDuff.Mode.MULTIPLY);
            mRequestingLocationUpdates = false;
            dontRefreshScreen = false;
            // Stopping the location updates
            stopLocationUpdates();
saveTheTest();
            Log.d(TAG, "Periodic location updates stopped!");

        }
    }

    private void startTest() {
        // Changing the button text
        MyTabControl.backpressed="viewmap";
        btnStartLocationUpdates.setText("Stop Test");
        //btnStartLocationUpdates.setBackgroundColor(Color.parseColor("#d32f2f"));
        //btnStartLocationUpdates.getBackground().setColorFilter(0xffd32f2f, PorterDuff.Mode.MULTIPLY);
        mRequestingLocationUpdates = true;
        useHandlerForScreenRefresh();
        dontRefreshScreen = false;
        distanceTravelled = 0;
        time_startTest = System.currentTimeMillis();
        rlayout.setVisibility(View.VISIBLE);
        test_stats.setText("Time Elapsed 00:00:00");
        markercounter = 1;
        // Starting the location updates
        System.out.println("called from 1");
        startLocationUpdates();
          /*  if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {

            }*/


        Log.d(TAG, "Periodic location updates started!");
    }

    /**
     * Starting the location updates
     * */
    protected void startLocationUpdates() {
if(getActivity()!=null) {

    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
           ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
       /* if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

        } else {
*/
            // No explanation needed, we can request the permission.

          requestPermissions( new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST);
            // MY_PERMISSION_REQUEST_READ_FINE_LOCATION is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
       /* ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                 Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST);*/

        //return;
    } else {
    try {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }
    catch (Exception e)
    {
        e.printStackTrace();
    }

    }
}

    private void seekLocationPermissions() {


        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions( new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, LOC_PERMISSIONS_REQUEST);
        }
 else
        {System.out.println("map in else ");
locationPermitted=true;
            initializeMap();


        }



    }

    private void initializeMap() {

        try {
            MapsInitializer.initialize(parentActivity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Creating location request object
     * */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }
    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }catch(Exception e)
        {

        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        displayLocation();
        if (mRequestingLocationUpdates) {
            System.out.println("called from 2");
            startLocationUpdates();
        }

//        Toast.makeText(this,"onConnected",Toast.LENGTH_SHORT).show();
//        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        if (mLastLocation != null) {
//            //place marker at current position
//            //mGoogleMap.clear();
//            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//            MarkerOptions markerOptions = new MarkerOptions();
//            markerOptions.position(latLng);
//            markerOptions.title("Current Position");
//            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
//            currLocationMarker = googleMap.addMarker(markerOptions);
//        }
//
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(UPDATE_INTERVAL); //5 seconds
//        mLocationRequest.setFastestInterval(FATEST_INTERVAL); //3 seconds
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter
//
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(parentActivity,"onConnectionSuspended",Toast.LENGTH_SHORT).show();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(parentActivity,"onConnectionFailed",Toast.LENGTH_SHORT).show();
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    double distanceToLast;

    @Override
    public void onLocationChanged(Location location) {

        if(mLastLocation != null && mRequestingLocationUpdates ) {
            distanceToLast = location.distanceTo(mLastLocation);
            // if less than 10 metres, do not record
            if (distanceToLast < 10.00) {
                Log.i("DISTANCE", "Values too close, so not used.");
            } else {
                distanceTravelled += distanceToLast;

            }
        }


        // Assign the new location
        mLastLocation = location;

        //Toast.makeText(getApplicationContext(), "Location changed!",
        //      Toast.LENGTH_SHORT).show();

        // Displaying the new location on UI
        if(distanceToLast > 0)
            displayLocation();
        //place marker at current position
        //mGoogleMap.clear();
//        if (currLocationMarker != null) {
//            currLocationMarker.remove();
//        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLng);
//        markerOptions.title("Current Position");
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
//        currLocationMarker = googleMap.addMarker(markerOptions);
//
//        Toast.makeText(this,"Location Changed",Toast.LENGTH_SHORT).show();
//
//        //zoom to current position:
        //CameraPosition cameraPosition = new CameraPosition.Builder()
        //        .target(latLng).zoom(14).build();
//
       // String t = camerafollowtg.getText().toString();
      //  if(t.equals("Camera Follows")) {
        if(camerafollowtg.isChecked())
        {
            float zoom = googleMap.getCameraPosition().zoom;

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                    latLng, zoom);
            Toast.makeText(parentActivity, "zoom is "+zoom +"camera update "+cameraUpdate, Toast.LENGTH_SHORT).show();
            System.out.println("zoom is "+zoom +"camera update "+cameraUpdate);
            googleMap.animateCamera(cameraUpdate);//Animates the movement of the camera from the current position
            // to the position defined in the update. During the animation, a call to getCameraPosition() returns an intermediate location of the camera.
        }
        //mMapView.updateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        //If you only need one location, unregister the listener
        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }

    boolean atleastonemarkerplotted = false;
    int markercounter = 0;
    /**
     * Method to display the location on UI
     * */
    private void displayLocation() {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if( capturedLocationArray == null)
        {
            //listLocsToDraw = new ArrayList<Location>();
            capturedLocationArray = new ArrayList<ViewMapFragment.CapturedLocation>();
        }

        if( mView_HealthStatus.connectionTypeIdentifier == 1)
            textview.setText(mView_HealthStatus.connectionType+"("+WifiConfig.getConnectedWifiDetails().getSsidName() + ")" );
          //  textview.setText(mView_HealthStatus.OperatorName + "  " + mView_HealthStatus.connectionType);
        else
            textview.setText(mView_HealthStatus.OperatorName + "  " + mView_HealthStatus.connectionType + " " + mView_HealthStatus.strCurrentNetworkState);

        if (mLastLocation != null) {
            // Random rnd = new Random();
            // int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

            ViewMapFragment.CapturedLocation cl = new ViewMapFragment.CapturedLocation();

            TelephonyManager tel = (TelephonyManager) parentActivity.getSystemService(Context.TELEPHONY_SERVICE);
            String networkOperator = tel.getNetworkOperator();

            if (TextUtils.isEmpty(networkOperator) == false) {
                mView_HealthStatus.mcc = Integer.parseInt(networkOperator.substring(0, 3));
                mView_HealthStatus.mnc = Integer.parseInt(networkOperator.substring(3));
            }

            cl.mcc = mView_HealthStatus.mcc + "";
            cl.mnc = mView_HealthStatus.mnc + "";
            cl.loc = mLastLocation;
            cl.networktype = mView_HealthStatus.iCurrentNetworkState;
            cl.networkproto = mView_HealthStatus.strCurrentNetworkProtocol;
           /* cl.CDMAECIO = mView_HealthStatus.CDMAECIO;
            cl.CDMARSSI = mView_HealthStatus.CDMARSSI;
            //cl.Cid = mView_HealthStatus.Cid;
            cl.EVDOECIO = mView_HealthStatus.EVDOECIO;
            cl.CDMARSSI = mView_HealthStatus.CDMARSSI;
            cl.EVDORSSI = mView_HealthStatus.EVDORSSI;
            cl.Lac = mView_HealthStatus.Lac;*/

            try {
                byte[] l_byte_array = new byte[4];
                l_byte_array = CommonFunctions.convertByteArray__p(Integer.parseInt(mView_HealthStatus.Cid));
                cl.rnc = CommonFunctions.getRNCID_or_CID__p(l_byte_array, CommonFunctions.RNCID_C) + "";
              /*  cl.Cid = CommonFunctions.getRNCID_or_CID__p(l_byte_array, CommonFunctions.CID_C) + "";*/
                if(MyPhoneStateListener.NetworkType == 3)
                {
                    int a = Integer.parseInt(mView_HealthStatus.Cid) & 0xff;
                   // cl.Cid = a + "";
                }
            }catch(Exception e)
            {

            }

         /*   cl.rscp = MyPhoneStateListener.getRxLev() + "";
            cl.lteArfcn = mView_HealthStatus.lteArfcn;
            cl.lteCQI = mView_HealthStatus.lteCQI;
            cl.ltePCI = mView_HealthStatus.ltePCI;
            cl.lteRSRP = mView_HealthStatus.lteRSRP;
            cl.lteRSRQ = mView_HealthStatus.lteRSRQ;
            cl.lteRSSI = mView_HealthStatus.lteRSSI;
            cl.lteSNR = mView_HealthStatus.lteSNR;
            cl.lteTAC = mView_HealthStatus.lteTAC;
            cl.ltePCI = mView_HealthStatus.ltePCI;*/

if(MyPhoneStateListener.getNetworkType()==4) {

    cl.pci_4G = mView_HealthStatus.ltePCI;
    cl.earfcn_4G = mView_HealthStatus.lteArfcn;
    cl.ta_4g = mView_HealthStatus.lteta;
    cl.CQI_4G = MyPhoneStateListener.getCQI();
    cl.RSSI_4G = MyPhoneStateListener.getLTERSSI();
    cl.RSRP_4G = mView_HealthStatus.lteRSRP;
    cl.sinr_4G = MyPhoneStateListener.getSNR();
    cl.TAC_4G = mView_HealthStatus.lteTAC;
    cl.enb_4G = mView_HealthStatus.lteENB;

    if (mView_HealthStatus.Cid != null) {
        int cid1 = Integer.parseInt(mView_HealthStatus.Cid) & 0xff;
        cl.cellid_4G = String.valueOf(cid1);
        cl.RSRQ_4G = mView_HealthStatus.lteRSRQ;

    }
}

         /* 3G*/

else if(MyPhoneStateListener.getNetworkType()==3) {
    cl.psc_3G = mView_HealthStatus.Wcdma_Psc;
    cl.uarfcn_3G = mView_HealthStatus.Uarfcn;
    cl.CQI_3G = MyPhoneStateListener.getCQI();
    cl.RSSI_3G = String.valueOf(MyPhoneStateListener.getRxLev());
    cl.RSCP_3G = mView_HealthStatus.rscp;
    if (mView_HealthStatus.rscp != null) {
        int rscp = Integer.valueOf(mView_HealthStatus.rscp);
        int ecno = rscp - MyPhoneStateListener.getRxLev();
        cl.ecno_3G = String.valueOf(ecno);
    }

    cl.lac_3G = mView_HealthStatus.Lac;
    cl.NodeBId_3G = mView_HealthStatus.nodeb_id;
    byte[] l_byte_array = new byte[4];

    if(mView_HealthStatus.Cid!=null) {
        l_byte_array = CommonFunctions.convertByteArray__p(Integer.parseInt(mView_HealthStatus.Cid));
        int l_real_CID = CommonFunctions.getRNCID_or_CID__p(l_byte_array, CommonFunctions.CID_C);
        cl.cellid_3G = String.valueOf(l_real_CID);
    }
    else
    {
        cl.cellid_3G = "0";
    }


}

            /*2G*/
else if(MyPhoneStateListener.getNetworkType()==2) {
    cl.arfcn_2G = mView_HealthStatus.ARFCN;
    cl.ta_2G = mView_HealthStatus.lteta;
    cl.rxlev_2G = String.valueOf(MyPhoneStateListener.getRxLev());
    cl.rxqual_2G = mView_HealthStatus.rxqualfor2g;
    cl.lac_2G = mView_HealthStatus.Lac;
    byte[] l_byte_array = new byte[4];
    l_byte_array = CommonFunctions.convertByteArray__p(Integer.parseInt(mView_HealthStatus.Cid));
    int l_RNC_ID = CommonFunctions.getRNCID_or_CID__p(l_byte_array, CommonFunctions.RNCID_C);
    int l_real1_CID = CommonFunctions.getRNCID_or_CID__p(l_byte_array, CommonFunctions.CID_C);
    cl.siteid_2G = String.valueOf(l_RNC_ID);
    cl.cellid_2G = String.valueOf(l_real1_CID);
}


            //
       //     Toast.makeText(getActivity(), "gps speed "+listenService.gps.getSpeed(), Toast.LENGTH_SHORT).show();

            cl.speedkmph =  (3.6*listenService.gps.getSpeed()) + "";
            cl.distance = distanceTravelled + "";
            long millis = System.currentTimeMillis();

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Date resultdate = new Date(millis);
            String hms1 = sdf.format(resultdate);

            //String hms = String.format("%02d-%02d-%02d", TimeUnit.MILLISECONDS.toHours(millis),
            //     TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
            //    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            cl.timestamp = hms1;

            capturedLocationArray.add(cl);

            if(showMarkers) {

                if( distanceToLast >= showMarkersAfterEverynMeters || atleastonemarkerplotted == false) {
                    atleastonemarkerplotted = true;
                    drawPrimaryLinePath();
                    double latitude = mLastLocation.getLatitude();
                    double longitude = mLastLocation.getLongitude();

                    latlngtext.setText(latitude + "," + longitude);

                    if (addressFetchInProgress == 0)
                        new ViewMapFragment.Async_getAddress().execute();

                    latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);

                    markercounter++;

                   /* if( mView_HealthStatus.iCurrentNetworkState != 4)
                        markerOptions.title(markercounter + ". " + mView_HealthStatus.strCurrentNetworkProtocol + " Cid:" + cl.cellid_4G + " RNC:" +cl.rnc);
                    else {
                        int cid1 = Integer.parseInt(cl.Cid) & 0xff;
                        markerOptions.title(markercounter + ". " + mView_HealthStatus.strCurrentNetworkProtocol + " - eNB: " +
                                mView_HealthStatus.lteENB + " Cid:" + cid1 + " PCI:" + cl.ltePCI);
                    }
                 */
                    if(MyPhoneStateListener.getNetworkType()==4)
                    {
                        markerOptions.title(markercounter + ". " + mView_HealthStatus.strCurrentNetworkProtocol +
                                " - eNB: " + mView_HealthStatus.lteENB + " Cid:" + cl.cellid_4G + " PCI:" + cl.pci_4G);

                    }
                    else if(MyPhoneStateListener.getNetworkType()==3)
                    {
                        markerOptions.title(markercounter + ". " + mView_HealthStatus.strCurrentNetworkProtocol + " Cid:" + cl.cellid_3G + " RNC:" +cl.rnc);

                    }
                    else if(MyPhoneStateListener.getNetworkType()==2)
                    {
                        markerOptions.title(markercounter + ". " + mView_HealthStatus.strCurrentNetworkProtocol + " Cid:" + cl.cellid_2G + " RNC:" +cl.rnc);

                    }
                    else
                    {

                    }



                    if( MyPhoneStateListener.getNetworkType() == 2)
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    else if( MyPhoneStateListener.getNetworkType() == 3)
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                    else if( MyPhoneStateListener.getNetworkType() == 4)
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    else
                        markerOptions.icon( BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

                    currLocationMarker = googleMap.addMarker(markerOptions);
                }
            }

        } else {
            Toast.makeText(parentActivity, "Couldn't get the location!", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadSettings()
    {
        TinyDB db = new TinyDB(parentActivity);
        String t1 = db.getString("updateinterval");
        if( t1 == null || t1.equals("")) {
            db.putString("updateinterval", "10");
        }else
            UPDATE_INTERVAL = Integer.parseInt(t1) *1000;

        t1 = db.getString("fastestinterval");
        if( t1 == null || t1.equals("")) {
            db.putString("fastestinterval", "5");
        }else
            FATEST_INTERVAL =  Integer.parseInt(t1) *1000;

        t1 = db.getString("displacment");
        if( t1 == null || t1.equals("")) {
            db.putString("displacment", "10");
        }else
            DISPLACEMENT = Integer.parseInt(t1);

        t1 = db.getString("plotMarkerAftermeters");
        if( t1 == null || t1.equals("")) {
            db.putString("plotMarkerAftermeters", "10");
        }else
            showMarkersAfterEverynMeters = Integer.parseInt(t1);

        t1 = db.getString("plotmarkers");
        if( t1 == null || t1.equals("")) {
            db.putString("plotmarkers", "On");
        }else {
            if (t1.equals("On") || t1.equals("ON")) {
                showMarkers = true;
            } else
                showMarkers = false;
        }
        createLocationRequest();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MAP_SETTINGS_ACTIVITY:
                {
                    loadSettings();
                    break;
                }

            }//end switch
        }//end if
    }//end function

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if( mView_HealthStatus.connectionTypeIdentifier == 1) {
                if(mView_HealthStatus.OperatorName!=null && mView_HealthStatus.connectionType!=null) {
                    if(textview!=null)
                    textview.setText(mView_HealthStatus.OperatorName + "  " + mView_HealthStatus.connectionType);
                }
            }
            else {
                if(mView_HealthStatus.OperatorName!=null && mView_HealthStatus.connectionType!=null && mView_HealthStatus.strCurrentNetworkState!=null) {
                    if(textview!=null)
                    textview.setText(mView_HealthStatus.OperatorName + "  " + mView_HealthStatus.connectionType + " " + mView_HealthStatus.strCurrentNetworkState);
                }
            }

        }
        else {
        }
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        stopLocationUpdates();
        dontRefreshScreen = true;
        Log.e("mView MainAct MyPhone", "onPause ");
        // Release the Camera because we don't need it when paused
        // and other activities might need to use it.
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            System.out.println("called from 3");
            startLocationUpdates();
        }

        Log.e("mView MainAct MyPhone", "onResume");
        // Get the Camera instance as the activity achieves full user focus
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient!= null && !mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
    }

    public int addressFetchInProgress = 0;
    public String fetchedAddress = "";

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if(locationPermitted)
        initilializeGmap(googleMap);
    }

    class Async_getAddress extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            addressFetchInProgress = 1;

        }

        @Override
        protected Void doInBackground(Void... params) {

            Geocoder geocoder;
            List<Address> addresses;

            try {
                geocoder = new Geocoder(parentActivity, Locale.getDefault());

                double latitude = mLastLocation.getLatitude();
                double longitude = mLastLocation.getLongitude();
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
                if( postalCode != null && postalCode.equals(""))
                    fetchedAddress = address + " " + city + " " + postalCode;
                else
                    fetchedAddress = address + " " + city;

            }catch(IOException e)
            {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {


            System.out.println("Result-->" + result);
            super.onPostExecute(result);
            addressFetchInProgress = 0;
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            latlngtext.setText(latitude + "," + longitude + " => " + fetchedAddress);
        }
    }//end async task


    public void useHandlerForScreenRefresh() {
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, refreshFrequency);
    }

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {

            if(dontRefreshScreen == false && mRequestingLocationUpdates)
            {
                long curr = System.currentTimeMillis();
                long millis = curr - time_startTest;

                String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                        TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                        TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

                test_stats.setText(" Time Elapsed " + hms + "  " + "\n Distance Travelled " + String.format("%.0f", distanceTravelled) +"m");
                mHandler.postDelayed(mRunnable, refreshFrequency);
            }
        }
    };

    private void drawPrimaryLinePath()
    {
        if ( mMapView == null )
        {
            return;
        }

        if ( capturedLocationArray.size() < 2 )
        {
            return;
        }

        PolylineOptions options = new PolylineOptions();

        PolylineOptions optline = new PolylineOptions();
        PolylineOptions optline2 = new PolylineOptions();
        optline.geodesic(true);
        optline.width(10);
        optline2.geodesic(true);
        optline2.width(10);

        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

        options.color( color) ; //Color.parseColor( "#CC0000FF" ) );
        options.width( 5 );
        options.visible( true );

        int size = capturedLocationArray.size();

        for (int i = 0; i < size - 1; i++)
        //for(CapturedLocation locRecorded : capturedLocationArray)
        //for ( Location locRecorded : listLocsToDraw )
        {
            Location pointD = capturedLocationArray.get(i).loc;
            Location pointA = capturedLocationArray.get(i + 1).loc;
            int green = (int) ((float) 255 - (float) (i / (float) size) * (float) 255);
            int red = (int) ((float) 0 + (float) (i / (float) size) * (float) 255);

            optline.add(new LatLng(pointD.getLatitude(), pointD.getLongitude()), new LatLng(pointA.getLatitude(), pointA.getLongitude()));
            optline2.add(new LatLng(pointD.getLatitude(), pointD.getLongitude()), new LatLng(pointA.getLatitude(), pointA.getLongitude()));

            if(i%2 == 0){
                optline.color(Color.rgb(red, green, 0));
                googleMap.addPolyline(optline);
                optline = new PolylineOptions();
                optline.geodesic(true);
                optline.width(10);
            }
            else{
                optline2.color(Color.rgb(red, green, 0));
                googleMap.addPolyline(optline2);
                optline2 = new PolylineOptions();
                optline2.geodesic(true);
                optline2.width(10);
            }

            // options.add( new LatLng( locRecorded.loc.getLatitude(),
            //         locRecorded.loc.getLongitude() ) );
        }

        googleMap.addPolyline( options );
    }

    ArrayList<ViewMapFragment.CapturedLocation> capturedLocationArray;

    class CapturedLocation {
        public Location loc;
        public int  networktype;
        public String networkproto;
        public String mcc, mnc;
      /*  public String Lac, Cid, Psc, rnc, rscp ;
        public  String ltePCI;
        public  String lteTAC;
        public  String lteRSRP;
        public  String lteRSRQ;
        public  String lteCQI;
        public  String lteRSSI;
        public  String lteSNR;
        public  String lteSINR;
        public  String lteArfcn;

        public int CDMARSSI,CDMAECIO,EVDORSSI,EVDOECIO,EVDOSNR;
*/        public String ulspeed, dlspeed;
        public String speedkmph, distance;
        public String timestamp;
        public String rnc="";



// 11 4g params
          public  String pci_4G="";
        public  String earfcn_4G="";
        public  String ta_4g="";
        public  String CQI_4G="";
        public  String RSSI_4G="";
        public  String RSRP_4G="";
        public  String sinr_4G="";
        public  String TAC_4G="";
        public  String enb_4G="";
        public  String cellid_4G="";
        public  String RSRQ_4G="";



        //9 3g params
        public  String psc_3G="";
        public  String uarfcn_3G="";
        public  String CQI_3G="";
        public  String RSSI_3G="";
        public  String RSCP_3G="";
        public  String ecno_3G="";
        public  String lac_3G="";
        public  String NodeBId_3G="";
        public  String cellid_3G="";
        //7 2g params

        public  String arfcn_2G="";
        public  String ta_2G="";
        public  String rxlev_2G="";
        public  String rxqual_2G="";
        public  String lac_2G="";
        public  String siteid_2G="";
        public  String cellid_2G="";

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST:
                System.out.println("calling from 2");
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startLocationUpdates();

                }
                else
                {

                }
                break;

            case STORAGE_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startTest();
                }
                break;


            case LOC_PERMISSIONS_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("map in onperm ");
                    locationPermitted = true;
                    initializeMap();
                }

                break;



        }

    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//
//        // show menu only when home fragment is selected
//        //sharad - commented to hide teh top right menu
////        if (navItemIndex == 0) {
//        getMenuInflater().inflate(R.menu.mapsettings, menu);
////        }
//
//        // when fragment is notifications, load the menu created for notifications
////		if (navItemIndex == 3) {
////			getMenuInflater().inflate(R.menu.notifications, menu);
////		}
//        return true;
//    }
}
