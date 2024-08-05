package com.newmview.wifi.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.newmview.wifi.AppLauncher;
import com.mview.airtel.R;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.database.DB_handler;
import com.newmview.wifi.helper.SharedPreferencesHelper;
import com.newmview.wifi.other.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AppLaunch extends AppCompatActivity {
    JSONObject jsonObject = new JSONObject();
    float mobileDataInMbBs = 0.0F;
    float wifiDataInMbBs = 0.0F;
    float totalDataBs = 0.0F;
    float mobileDataTxInMbAs = 0.0F;
    float mobileDataRxInMbAs = 0.0F;
    float wifiDataRxInMbAs = 0.0F;
    float wifiDataTxInMbAs = 0.0F;
    float mobileDataTxInMbBs = 0.0F;
    float mobileDataRxInMbBs = 0.0F;
    float wifiDataRxInMbBs = 0.0F;
    float wifiDataTxInMbBs = 0.0F;
    float totalDataRxAs =0.0F;
    float totalDataTxAs =0.0F;
    float totalDataRxBs =0.0F;
    float totalDataTxBs =0.0F;


    float mobileDataInMbAs = 0.0F;
    float wifiDataInMbAs =0.0F;
    float totalDataAs =0.0F;
    String packagename = null;
    String publicip = null;
    boolean panel = false;

    private static final String PREFS_NAME = "AppLaunchPref";
    private static final String KEY_APP_LAUNCHED = "app_launch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
        @SuppressLint("MissingInflatedId")
        EditText packageName = findViewById(R.id.et_packagename);
        Button buttonLaunchAndCapture = findViewById(R.id.btnlaunch);
        panel = getIntent().getBooleanExtra("panel", false);  // Default value is false
        Log.d("TAG", "Panel value: " + panel);

        buttonLaunchAndCapture.setOnClickListener(view -> {
                packagename = packageName.getText().toString().trim();
                if(!packagename.isEmpty()){
                    fetchDataUsageChartBs();
                    boolean launchedSuccessfully = launchChrome(packagename);
                    if (launchedSuccessfully) {
                        // Schedule a task to bring back the user to the app after 10 seconds
//                        Handler handler = new Handler(Looper.getMainLooper());
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                // Bring your app back to the foreground
//                                Intent returnIntent = new Intent(MviewApplication.ctx, AppLaunch.class);
//                                returnIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                                startActivity(returnIntent);
//
//                                // Remove the closed APK from the background
//                                ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//                                activityManager.killBackgroundProcesses("com.google.android.youtube");
//                            }
//                        }, 10000);// Delay of 10 seconds
                        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean(KEY_APP_LAUNCHED, true);
                        editor.apply();
                    } else {
                        Toast.makeText(this, "Invalid package name", Toast.LENGTH_SHORT).show();
                        Log.d("MainActivity", "Failed to launch Chrome");
                    }
                }else {
                    Toast.makeText(this, "Please enter package name", Toast.LENGTH_SHORT).show();

                }

            });
    }

    private boolean launchChrome(String packagename) {
        PackageManager packageManager = getPackageManager();
        try {
            logInstalledPackages();
            // Check if Chrome is installed
            Intent intent = packageManager.getLaunchIntentForPackage(packagename);
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                jsonObject.put("app_opened", "true");
                jsonObject.put("reason","NA");
                jsonObject.put("id", "NA");
                jsonObject.put("package_name",packagename);
                return true;
            } else {
                jsonObject.put("app_opened", "false");
                jsonObject.put("reason","app not found");
                jsonObject.put("id", "NA");
                jsonObject.put("package_name", packagename);
//                jsonObject.put("public_ip", Utils.publicIp());
                jsonObject.put("private_ip", Utils.getPIP());
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                String date_time = now.format(dtf);
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObject);
                Utils.appendLog("ELOG_APP_OPEN_TEST_PANEL: final json is " + jsonArray);

                // Store all the metrics after the loop ends
                DB_handler db_handler = new DB_handler(MviewApplication.ctx);
                db_handler.open();
                db_handler.insertInLoggingAgentTable("AppOpen", "openapp_report", jsonArray.toString(), date_time, "init");
                db_handler.close();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void logInstalledPackages() {
        PackageManager packageManager = getPackageManager();
        List<ApplicationInfo> packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            Log.d("InstalledPackages", "Installed package :" + packageInfo.packageName);
        }
    }

    private void bringAppToFront() {
        Intent returnIntent = new Intent(this, AppLaunch.class);
        returnIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(returnIntent);

        Toast.makeText(this, "Chrome was launched successfully", Toast.LENGTH_SHORT).show();
        Log.d("MainActivity", "Chrome was launched successfully");
    }

//    public void fetchDataUsageChartBs() {
//        long currentMobileTxBytes = TrafficStats.getMobileTxBytes(); // Return number of bytes transmitted across mobile networks since device boot
//        long currentMobileRxBytes = TrafficStats.getMobileRxBytes();
//        long totalTxBytes = TrafficStats.getTotalTxBytes();
//        long totalRxBytes = TrafficStats.getTotalRxBytes();
//
//        long mobileDataBytes = currentMobileTxBytes + currentMobileRxBytes;
//
//        mobileDataTxInMbBs = currentMobileTxBytes/(1024F * 1024F);
//        mobileDataRxInMbBs = currentMobileRxBytes/(1024F * 1024F);
//
//        wifiDataTxInMbBs = totalTxBytes/(1024F * 1024F) - mobileDataTxInMbAs;
//        wifiDataRxInMbBs = totalRxBytes/(1024F * 1024F) - mobileDataRxInMbAs;
//
//        if (wifiDataTxInMbBs < 0) wifiDataTxInMbBs = 0F;
//        if (wifiDataRxInMbBs < 0) wifiDataRxInMbBs = 0F;
//
//
//
//        mobileDataInMbBs = mobileDataBytes / (1024F * 1024F);
//        Log.d("mobile data in byte ", String.valueOf(mobileDataBytes));
//        Log.d("mobile data in mb ", String.valueOf(mobileDataInMbBs));
//
//        wifiDataInMbBs = ((totalTxBytes + totalRxBytes) / (1024F * 1024F)) - mobileDataInMbBs;
//        Log.d("wifi data in mb ", String.valueOf(wifiDataInMbBs));
//        // Bug reported: Ensure Wi-Fi data is not negative
//        if (wifiDataInMbBs < 0) wifiDataInMbBs = 0F;
//
//        totalDataTxBs = wifiDataTxInMbBs + mobileDataTxInMbBs;
//        totalDataRxBs = wifiDataRxInMbBs + mobileDataRxInMbBs;
//        totalDataBs = wifiDataInMbBs + mobileDataInMbBs;
//        Log.d("total data in mb ", String.valueOf(totalDataBs));
//    }
//    public void fetchDataUsageChartAs() {
//        long currentMobileTxBytes = TrafficStats.getMobileTxBytes(); // Return number of bytes transmitted across mobile networks since device boot
//        long currentMobileRxBytes = TrafficStats.getMobileRxBytes();
//        long totalTxBytes = TrafficStats.getTotalTxBytes();
//        long totalRxBytes = TrafficStats.getTotalRxBytes();
//
//        long mobileDataBytes = currentMobileTxBytes + currentMobileRxBytes;
//        mobileDataTxInMbAs = currentMobileTxBytes/(1024F * 1024F);
//        mobileDataRxInMbAs = currentMobileRxBytes/(1024F * 1024F);
//
//        wifiDataTxInMbAs = totalTxBytes/(1024F * 1024F) - mobileDataTxInMbAs;
//        wifiDataRxInMbAs = totalRxBytes/(1024F * 1024F) - mobileDataRxInMbAs;
//        if (wifiDataTxInMbAs < 0) wifiDataTxInMbAs = 0F;
//        if (wifiDataRxInMbAs < 0) wifiDataRxInMbAs = 0F;
//
//
//
//        mobileDataInMbAs = mobileDataBytes / (1024F * 1024F);
//        Log.d("mobile data in byte ", String.valueOf(mobileDataBytes));
//        Log.d("mobile data in mb ", String.valueOf(mobileDataInMbAs));
//
//        wifiDataInMbAs = ((totalTxBytes + totalRxBytes) / (1024F * 1024F)) - mobileDataInMbAs;
//        Log.d("wifi data in mb ", String.valueOf(wifiDataInMbAs));
//        // Bug reported: Ensure Wi-Fi data is not negative
//        if (wifiDataInMbAs < 0) wifiDataInMbAs = 0F;
//        totalDataTxAs = wifiDataTxInMbAs + mobileDataTxInMbAs;
//        totalDataRxAs = wifiDataRxInMbAs + mobileDataRxInMbAs;
//
//        totalDataAs = wifiDataInMbAs + mobileDataInMbAs;
//        Log.d("total data in mb ", String.valueOf(totalDataAs));
//    }
public void fetchDataUsageChartBs() {
    long currentMobileTxBytes = TrafficStats.getMobileTxBytes();
    long currentMobileRxBytes = TrafficStats.getMobileRxBytes();
    long totalTxBytes = TrafficStats.getTotalTxBytes();
    long totalRxBytes = TrafficStats.getTotalRxBytes();

    // Calculate mobile data in MB
    mobileDataTxInMbBs = currentMobileTxBytes / (1024F * 1024F);
    mobileDataRxInMbBs = currentMobileRxBytes / (1024F * 1024F);

    // Calculate Wi-Fi data in MB (assuming total minus mobile is Wi-Fi)
    wifiDataTxInMbBs = (totalTxBytes - currentMobileTxBytes) / (1024F * 1024F);
    wifiDataRxInMbBs = (totalRxBytes - currentMobileRxBytes) / (1024F * 1024F);

    // Ensure Wi-Fi data is not negative
    wifiDataTxInMbBs = Math.max(wifiDataTxInMbBs, 0F);
    wifiDataRxInMbBs = Math.max(wifiDataRxInMbBs, 0F);

    // Calculate total data
    totalDataTxBs = wifiDataTxInMbBs + mobileDataTxInMbBs;
    totalDataRxBs = wifiDataRxInMbBs + mobileDataRxInMbBs;
    Log.d("total data in mb ", String.valueOf(totalDataTxBs + totalDataRxBs));
}

    // For fetchDataUsageChartAs
    public void fetchDataUsageChartAs() {
        long currentMobileTxBytes = TrafficStats.getMobileTxBytes();
        long currentMobileRxBytes = TrafficStats.getMobileRxBytes();
        long totalTxBytes = TrafficStats.getTotalTxBytes();
        long totalRxBytes = TrafficStats.getTotalRxBytes();

        // Calculate mobile data in MB
        mobileDataTxInMbAs = currentMobileTxBytes / (1024F * 1024F);
        mobileDataRxInMbAs = currentMobileRxBytes / (1024F * 1024F);

        // Calculate Wi-Fi data in MB (assuming total minus mobile is Wi-Fi)
        wifiDataTxInMbAs = (totalTxBytes - currentMobileTxBytes) / (1024F * 1024F);
        wifiDataRxInMbAs = (totalRxBytes - currentMobileRxBytes) / (1024F * 1024F);

        // Ensure Wi-Fi data is not negative
        wifiDataTxInMbAs = Math.max(wifiDataTxInMbAs, 0F);
        wifiDataRxInMbAs = Math.max(wifiDataRxInMbAs, 0F);

        // Calculate total data
        totalDataTxAs = wifiDataTxInMbAs + mobileDataTxInMbAs;
        totalDataRxAs = wifiDataRxInMbAs + mobileDataRxInMbAs;
        Log.d("total data in mb ", String.valueOf(totalDataTxAs + totalDataRxAs));
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(MviewApplication.ctx);

        Log.d("TAG", "onResume: panel"+panel+" public ip is: "+publicip);

        if (panel){

            new AppLauncher(MviewApplication.ctx).logDataUsage();
            finish();
        }
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean appLaunched = preferences.getBoolean(KEY_APP_LAUNCHED, false);
        Log.d("TAG", "onResume: called app open"+appLaunched);

        if (appLaunched){
            fetchDataUsageChartAs();
            try {

                float txMobileData = (mobileDataTxInMbAs-mobileDataTxInMbBs);
                float rxMobileData = (mobileDataRxInMbAs-mobileDataRxInMbBs);
                float txWifiData = (wifiDataTxInMbAs-wifiDataTxInMbBs);
                float rxWifiData = (wifiDataRxInMbAs-wifiDataRxInMbBs);
                float txTotalData = (totalDataTxAs-totalDataTxBs);
                float rxTotalData = (totalDataRxAs-totalDataRxBs);


                jsonObject.put("transmitted_mobiledata", txMobileData+"MB");
                jsonObject.put("received_mobiledata", rxMobileData+"MB");

                jsonObject.put("transmitted_wifidata", txWifiData+"MB");
                jsonObject.put("received_wifidata", rxWifiData+"MB");

                jsonObject.put("transmitted_totaldata", txTotalData+"MB");
                jsonObject.put("received_totaldata", rxTotalData+"MB");

                jsonObject.put("dns_resolution_time", " ");
                jsonObject.put("time_taken", " ");
                jsonObject.put("resolved_ip", " ");

                jsonObject.put("public_ip", publicip);
                jsonObject.put("private_ip", Utils.getPIP());
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                String date_time = now.format(dtf);
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObject);
                Utils.appendLog("ELOG_APP_OPEN_TEST_UI: final json is "+jsonArray);
                // Store all the metrics after the loop ends
                DB_handler db_handler = new DB_handler(MviewApplication.ctx);
                db_handler.open();
                db_handler.insertInLoggingAgentTable("AppOpen", "openapp_report", jsonArray.toString(), date_time, "init");
                db_handler.close();

                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(KEY_APP_LAUNCHED, false);
                editor.apply();

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(); // Finish the current activity
    }
}