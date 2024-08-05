package com.newmview.wifi.activity;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
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
import com.newmview.wifi.TinyDB;
import com.newmview.wifi.listenService;
import com.newmview.wifi.mView_HealthStatus;

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


import static com.newmview.wifi.fragment.HomeFragment.googleMap;

/**
 * Created by Sharad Gupta on 10/31/2016.
 */

public class ViewMyMap extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    boolean dontRefreshScreen;
    MapView mMapView;
    TextView textview, latlngtext, test_stats;
    Button btnStartLocationUpdates, clearBtn, saveBtn;
    ToggleButton camerafollowtg;
    RelativeLayout rlayout;


    public static final int MAP_SETTINGS_ACTIVITY = 1781;

    private Location mLastLocation;
    private float distanceTravelled;
    LatLng latLng;
    Marker currLocationMarker;
    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters
    private boolean mRequestingLocationUpdates = false;
    private String TAG ="MAP view";
    private long time_startTest;

    boolean showMarkers = true;
    int showMarkersAfterEverynMeters = 10;

    Handler mHandler;
    int refreshFrequency = 1000; //1 second

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.mapview_new);

        textview=(TextView)findViewById(R.id.textview);
        test_stats = (TextView)findViewById(R.id.test_stats);


        latlngtext = (TextView)findViewById(R.id.latlngtext);
        btnStartLocationUpdates = (Button)findViewById(R.id.startBtn);
        clearBtn = (Button)findViewById(R.id.clearBtn);
        saveBtn = (Button)findViewById(R.id.saveBtn);
        rlayout = (RelativeLayout) findViewById(R.id.rlayout);
        rlayout.setVisibility(View.GONE);

        camerafollowtg = (ToggleButton)findViewById(R.id.camerafollowtg);

        if( mView_HealthStatus.connectionTypeIdentifier == 1)
            textview.setText(mView_HealthStatus.OperatorName + "  " + mView_HealthStatus.connectionType);
        else
            textview.setText(mView_HealthStatus.OperatorName + "  " + mView_HealthStatus.connectionType + " " + mView_HealthStatus.strCurrentNetworkState);


        buildGoogleApiClient();
        loadSettings();
        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();


        //createLocationRequest();

        try {
            MapsInitializer.initialize(this.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

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

                if(capturedLocationArray != null) {
                    if(mRequestingLocationUpdates)
                    {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                ViewMyMap.this);
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
                    }else {
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
                if(capturedLocationArray != null) {
                    if(mRequestingLocationUpdates)
                    {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                ViewMyMap.this);
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
                    }else {
                        int size = capturedLocationArray.size();

                        String header = "Lat" + "," + "Lon" + "," + "Networktype" + "," + "networkproto" + "," + "mcc" + "," + "mnc" + "," + "Cid" + "," + "Lac" + "," + "rscp" + "," +
                                "Psc" + "," + "rnc" + "," +  "EVDORSSI" + "," + "CDMARSSI" + "," + "CDMAECIO"  + "," + "EVDOSNR" + "," + "ltePCI" + "," + "lteTAC" + "," +
                                "lteRSSI" + "," + "lteRSRQ" + "," + "lteSNR" + "," + "lteRSRP" + "," + "lteCQI" + "," + "lteArfcn" + "," + "speed" + "," + "distance" + "," + "time" + "\n";

                        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                        File file = new File(path, "mview" );
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
                                CapturedLocation c = capturedLocationArray.get(i);
                                Location p = c.loc;

                                String lineitem = p.getLatitude() + "," + p.getLongitude() + "," + c.networktype + "," + c.networkproto + "," + c.mcc + "," + c.mnc + "," + c.Cid + "," + c.Lac + "," + c.rscp + "," +
                                        c.Psc + "," + c.rnc + "," + c.EVDORSSI + "," + c.CDMARSSI + "," + c.CDMAECIO + "," + c.EVDOSNR + "," + c.ltePCI + "," + c.lteTAC + "," +
                                        c.lteRSSI + "," + c.lteRSRQ + "," + c.lteSNR + "," + c.lteRSRP + "," + c.lteCQI + "," + c.lteArfcn + "," + c.speedkmph + "," + c.distance +
                                        "," + c.timestamp + "\n";
                                out.append(lineitem);
                            }//end for loop
                            out.flush();
                            out.close();
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                    ViewMyMap.this);
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
                        }catch(IOException e)
                        {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                    ViewMyMap.this);
                            e.printStackTrace();
                            alertDialog.setTitle("Error");
                            alertDialog.setMessage("Map Test data could not be saved -  " + e.getMessage() );
                            alertDialog.setCancelable(true);

                            alertDialog.setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            dialog.cancel();
                                        }
                                    });
                            alertDialog.show();
                        }
                        capturedLocationArray.clear();
                        googleMap.clear();
                        test_stats.setText("");
                        rlayout.setVisibility(View.GONE);
                    }
                }
            }
        });

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;


                // For showing a move to my location button
                googleMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map
                LatLng sydney = new LatLng(listenService.gps.getLatitude(),  listenService.gps.getLongitude());
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
        });
    }


    protected synchronized void buildGoogleApiClient() {
        //Toast.makeText(this,"buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }



    private void togglePeriodicLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            // Changing the button text
            btnStartLocationUpdates.setText("Stop Test");
            btnStartLocationUpdates.setBackgroundColor(Color.parseColor("#d32f2f"));
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
            startLocationUpdates();

            Log.d(TAG, "Periodic location updates started!");

        } else {
            // Changing the button text
            btnStartLocationUpdates.setText("Start Test");
            btnStartLocationUpdates.setBackgroundColor(Color.parseColor("#7aff24"));
            //btnStartLocationUpdates.getBackground().setColorFilter(0xff7aff24, PorterDuff.Mode.MULTIPLY);
            mRequestingLocationUpdates = false;
            dontRefreshScreen = false;
            // Stopping the location updates
            stopLocationUpdates();

            Log.d(TAG, "Periodic location updates stopped!");
        }
    }

    /**
     * Starting the location updates
     * */
    protected void startLocationUpdates() {


        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

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
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        displayLocation();
        if (mRequestingLocationUpdates) {
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
        Toast.makeText(this,"onConnectionSuspended",Toast.LENGTH_SHORT).show();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this,"onConnectionFailed",Toast.LENGTH_SHORT).show();
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
        String t = camerafollowtg.getText().toString();
        if(t.equals("Camera Follows")) {
            float zoom = googleMap.getCameraPosition().zoom;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                    latLng, zoom);
            googleMap.animateCamera(cameraUpdate);
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
            capturedLocationArray = new ArrayList<CapturedLocation>();
        }

        if (mLastLocation != null) {
           // Random rnd = new Random();
           // int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

            CapturedLocation cl = new CapturedLocation();

            TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String networkOperator = tel.getNetworkOperator();

            if (TextUtils.isEmpty(networkOperator) == false) {
                mView_HealthStatus.mcc = Integer.parseInt(networkOperator.substring(0, 3));
                mView_HealthStatus.mnc = Integer.parseInt(networkOperator.substring(3));
            }

            cl.mcc = mView_HealthStatus.mcc + "";
            cl.mnc = mView_HealthStatus.mnc + "";
            cl.loc = mLastLocation;
            cl.networktype = MyPhoneStateListener.NetworkType;
            cl.networkproto = mView_HealthStatus.strCurrentNetworkProtocol;
            cl.CDMAECIO = mView_HealthStatus.CDMAECIO;
            cl.CDMARSSI = mView_HealthStatus.CDMARSSI;
            //cl.Cid = mView_HealthStatus.Cid;
            cl.EVDOECIO = mView_HealthStatus.EVDOECIO;
            cl.CDMARSSI = mView_HealthStatus.CDMARSSI;
            cl.EVDORSSI = mView_HealthStatus.EVDORSSI;
            cl.Lac = mView_HealthStatus.Lac;

            byte[] l_byte_array = new byte[4];
            l_byte_array = CommonFunctions.convertByteArray__p(Integer.parseInt(mView_HealthStatus.Cid));
            cl.rnc  = CommonFunctions.getRNCID_or_CID__p(l_byte_array, CommonFunctions.RNCID_C) + "";
            cl.Cid = CommonFunctions.getRNCID_or_CID__p(l_byte_array, CommonFunctions.CID_C) + "";

            cl.rscp = MyPhoneStateListener.getRxLev() + "";
            cl.lteArfcn = mView_HealthStatus.lteArfcn;
            cl.lteCQI = mView_HealthStatus.lteCQI;
            cl.ltePCI = mView_HealthStatus.ltePCI;
            cl.lteRSRP = mView_HealthStatus.lteRSRP;
            cl.lteRSRQ = mView_HealthStatus.lteRSRQ;
            cl.lteRSSI = mView_HealthStatus.lteRSSI;
            cl.lteSNR = mView_HealthStatus.lteSNR;
            cl.lteTAC = mView_HealthStatus.lteTAC;
            cl.ltePCI = mView_HealthStatus.ltePCI;

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
                        new Async_getAddress().execute();

                    latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);

                    markercounter++;
                    markerOptions.title(markercounter + ". " + mView_HealthStatus.strCurrentNetworkProtocol + " Cid:" + cl.Cid + " RNC:" +cl.rnc);

                    if( mView_HealthStatus.iCurrentNetworkState == 2)
                         markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    else if( mView_HealthStatus.iCurrentNetworkState == 3)
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                    else if( mView_HealthStatus.iCurrentNetworkState == 4)
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    else
                        markerOptions.icon( BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

                    currLocationMarker = googleMap.addMarker(markerOptions);
                }
            }

        } else {
            Toast.makeText(getApplicationContext(), "Couldn't get the location!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity

//            try {
//                mRunnable.wait();
//            }catch(InterruptedException e)
//            {
//
//            }
            if(mRequestingLocationUpdates)
            {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        ViewMyMap.this);
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
            }else {
                dontRefreshScreen = true;
                onBackPressed();
            }

            return true;
        }
        if (id == R.id.terrain_view) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            //Intent intent3 = new Intent(this, UploadDownloadSettings.class);
            //startActivityForResult(intent3, MAP_SETTINGS_ACTIVITY);
            //Toast.makeText(getApplicationContext(), "UpLoad/Download Settings", Toast.LENGTH_LONG).show();
            return true;
        }else
        if (id == R.id.sat_view) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            //Intent intent3 = new Intent(this, UploadDownloadSettings.class);
            //startActivityForResult(intent3, MAP_SETTINGS_ACTIVITY);
            //Toast.makeText(getApplicationContext(), "UpLoad/Download Settings", Toast.LENGTH_LONG).show();
            return true;
        }else
        if (id == R.id.hybrid_view) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            //Intent intent3 = new Intent(this, UploadDownloadSettings.class);
            //startActivityForResult(intent3, MAP_SETTINGS_ACTIVITY);
            //Toast.makeText(getApplicationContext(), "UpLoad/Download
            // Settings", Toast.LENGTH_LONG).show();
            return true;
        }else
        if (id == R.id.normal_view) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            //Intent intent3 = new Intent(this, UploadDownloadSettings.class);
            //startActivityForResult(intent3, MAP_SETTINGS_ACTIVITY);
            //Toast.makeText(getApplicationContext(), "UpLoad/Download Settings", Toast.LENGTH_LONG).show();
            return true;
        }
        else
        if (id == R.id.settings) {
            if(mRequestingLocationUpdates)
            {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        ViewMyMap.this);
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
            }else {
                Intent intent3 = new Intent(this, MapSettings.class);
                startActivityForResult(intent3, MAP_SETTINGS_ACTIVITY);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadSettings()
    {
        TinyDB db = new TinyDB(ViewMyMap.this);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

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
            startLocationUpdates();
        }

        Log.e("mView MainAct MyPhone", "onResume");
        // Get the Camera instance as the activity achieves full user focus
    }

    @Override
    protected void onStart() {
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
                geocoder = new Geocoder(ViewMyMap.this, Locale.getDefault());

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

    ArrayList<CapturedLocation> capturedLocationArray;

    class CapturedLocation {
        public Location loc;
        public int  networktype;
        public String networkproto;
        public String mcc, mnc;
        public String Lac, Cid, Psc, rnc, rscp ;
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
        public String ulspeed, dlspeed;
        public String speedkmph, distance;
        public String timestamp;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        //sharad - commented to hide teh top right menu
//        if (navItemIndex == 0) {
        getMenuInflater().inflate(R.menu.mapsettings, menu);
//        }

        // when fragment is notifications, load the menu created for notifications
//		if (navItemIndex == 3) {
//			getMenuInflater().inflate(R.menu.notifications, menu);
//		}
        return true;
    }

}
