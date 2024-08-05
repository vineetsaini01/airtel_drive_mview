package com.newmview.wifi.SlidingTab;

/**
 * Created by Sharad Gupta on 12/2/2016.
 */


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.gpsTracker.GPSTracker;
import com.mview.airtel.R;
import com.newmview.wifi.activity.MapSettings;
import com.newmview.wifi.fragment.ViewMapFragment;
import com.newmview.wifi.other.CommonAlertDialog;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;

import static com.newmview.wifi.fragment.HomeFragment.googleMap;
import static com.newmview.wifi.fragment.NetworkMonitorFragment.REQUEST_CHECK_SETTINGS;
import static com.newmview.wifi.fragment.NetworkMonitorFragment.REQUEST_ENABLE_GPS;
import static com.newmview.wifi.listenService.gps;


public class MyTabControl extends AppCompatActivity {
    private static final int LOC_PERMISSIONS_REQUEST = 4;

    // Declaring Your View and Variables

    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[]={"Network Monitor","Map View","Neighboring cell Info","Second Sim Info"};
    int Numboftabs =4;
    public static String backpressed;


    public static final int MAP_SETTINGS_ACTIVITY = 1781;

    @Override
    public void onBackPressed() {

        if(Utils.checkifavailable(backpressed))
        {
            if(backpressed.equalsIgnoreCase("viewmap"))
            {
                CommonAlertDialog.showAlert("View Map", Constants.map_view_test_end,this);
                backpressed="end";
            }
            else {
                super.onBackPressed();
            }
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
seekLocationPermission();
       checkForGps();


        Intent intent = getIntent();
        //String id = intent.getStringExtra("view");

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);


        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);






    }
public void finishActivity()
{
    finish();
}
    private void seekLocationPermission() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,

        }, LOC_PERMISSIONS_REQUEST);
    }

    private void checkForGps() {

        LocationManager locationManager = (LocationManager)this
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


  /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present




        getMenuInflater().inflate(R.menu.mapsettings, menu);
        return true;
    }*/

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
            if(ViewMapFragment.mRequestingLocationUpdates)
            {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        this,R.style.AlertDialogTheme);
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
               // dontRefreshScreen = true;
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
            //Toast.makeText(getApplicationContext(), "UpLoad/Download Settings", Toast.LENGTH_LONG).show();
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
            if(ViewMapFragment.mRequestingLocationUpdates)
            {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        this);
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
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
////        if (id == R.id.action_settings) {
////            return true;
////        }
//
//        return super.onOptionsItemSelected(item);
//    }
private void openGpsEnableSetting() {
    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    startActivityForResult(intent, REQUEST_ENABLE_GPS);
}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       // Utils.showToast(this,"in my tabcontrol");


        if(requestCode==MAP_SETTINGS_ACTIVITY)
        {
            if(resultCode==RESULT_OK)
            {
                adapter.tab2.loadSettings();

            }
        }
        else if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    //Success Perform Task Here
                //    Utils.showToast(this,"ok result");

                        gps = new GPSTracker(getApplicationContext());
                    break;
                case Activity.RESULT_CANCELED:
                    Log.e("GPS","User denied to access location");
                 //   Utils.showToast(this,"user cancelled");
                    //openGpsEnableSetting();
                    break;
            }
        } else if (requestCode == REQUEST_ENABLE_GPS) {
            LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!isGpsEnabled) {
                openGpsEnableSetting();
            } else {
                //navigateToUser();
            }
        }



        //end if
    }//end function
}