package com.newmview.wifi;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.dashboard.roomdb.gagdagent.GagdAgent;
import com.functionapps.mview_sdk2.main.Mview;
import com.newmview.wifi.activity.AppLaunch;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.database.DB_handler;
import com.newmview.wifi.helper.SharedPreferencesHelper;
import com.newmview.wifi.other.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class AppLauncher {

    private JSONObject jsonObject = new JSONObject();
    private float mobileDataInMbBs = 0.0F;
    private float wifiDataInMbBs = 0.0F;
    private float totalDataBs = 0.0F;
    float mobileDataTxInMbBs = 0.0F;
    float mobileDataRxInMbBs = 0.0F;
    float wifiDataRxInMbBs = 0.0F;
    float wifiDataTxInMbBs = 0.0F;
    float totalDataRxBs =0.0F;
    float totalDataTxBs =0.0F;

    private float mobileDataInMbAs = 0.0F;
    private float wifiDataInMbAs = 0.0F;
    private float totalDataAs = 0.0F;
    float mobileDataTxInMbAs = 0.0F;
    float mobileDataRxInMbAs = 0.0F;
    float wifiDataRxInMbAs = 0.0F;
    float wifiDataTxInMbAs = 0.0F;
    float totalDataRxAs =0.0F;
    float totalDataTxAs =0.0F;

    private int currentIndex = 0;
    private String packageName = null;
    private String scanId = null;
    String opened = "false";
    private static final String PREF_NAME = "SaveDataUsage";

    private static final String KEY_MOBILE_DATA_TX_BS = "mobileDataTxInMbBs";
    private static final String KEY_MOBILE_DATA_RX_BS = "mobileDataRxInMbBs";

    private static final String KEY_WIFI_DATA_TX_BS = "wifiDataTxInMbBs";
    private static final String KEY_WIFI_DATA_RX_BS = "wifiDataRxInMbBs";

    private static final String KEY_TOTAL_DATA_TX_BS = "totalDataTxBs";
    private static final String KEY_TOTAL_DATA_RX_BS = "totalDataRxBs";
    private static final String PACKAGE_NAME = "packageName";
    private static final String SCAN_ID = "scanId";
    private static final String OPENED = "opened";



    private SharedPreferences preferences;

    private SharedPreferencesHelper sharedPreferencesHelper;

    private Context context;

    public AppLauncher(Context context) {
        this.context = context;
        this.sharedPreferencesHelper = new SharedPreferencesHelper(context);
        this.preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        loadSavedData();
    }

    private void loadSavedData() {
        mobileDataTxInMbBs = preferences.getFloat(KEY_MOBILE_DATA_TX_BS, 0.0F);
        mobileDataRxInMbBs = preferences.getFloat(KEY_MOBILE_DATA_RX_BS, 0.0F);

        wifiDataTxInMbBs = preferences.getFloat(KEY_WIFI_DATA_TX_BS, 0.0F);
        wifiDataRxInMbBs = preferences.getFloat(KEY_WIFI_DATA_RX_BS, 0.0F);

        totalDataTxBs = preferences.getFloat(KEY_TOTAL_DATA_TX_BS, 0.0F);
        totalDataRxBs = preferences.getFloat(KEY_TOTAL_DATA_RX_BS, 0.0F);

        packageName = preferences.getString(PACKAGE_NAME, null);
        scanId = preferences.getString(SCAN_ID, null);
        opened = preferences.getString(OPENED,null);


    }

    private void saveData() {
        SharedPreferences.Editor editor = preferences.edit();

        editor.putFloat(KEY_MOBILE_DATA_TX_BS, mobileDataTxInMbBs);
        editor.putFloat(KEY_MOBILE_DATA_RX_BS, mobileDataRxInMbBs);

        editor.putFloat(KEY_WIFI_DATA_TX_BS, wifiDataTxInMbBs);
        editor.putFloat(KEY_WIFI_DATA_RX_BS, wifiDataRxInMbBs);

        editor.putFloat(KEY_TOTAL_DATA_TX_BS, totalDataTxBs);
        editor.putFloat(KEY_TOTAL_DATA_RX_BS, totalDataRxBs);

        editor.putString(PACKAGE_NAME, packageName);
        editor.putString(SCAN_ID, scanId);
        editor.putString(OPENED, opened);

        editor.apply();
    }
    public void appOpenTest() {
        Utils.appendLog("ELOG_RUN_APP_OPEN_PANEL: Called");
        DB_handler db_helper = new DB_handler(MviewApplication.ctx);
        db_helper.open();

        String current_status = db_helper.current_status("AppOpen");
        String start_date = db_helper.start_date("AppOpen");
        String end_date = db_helper.end_date("AppOpen");
        Date currentdate = Utils.getcurrentdate();
        Date start_date_ = Utils.convertStringtoDate(start_date);
        Date end_date_ = Utils.convertStringtoDate(end_date);

        Log.i("TAG", "current_status is of agent is " + current_status + " and start date is " + start_date + " and end date is " + end_date);

        if (current_status == null || current_status.equalsIgnoreCase("completed")) {
            Log.d("TAG", "current status is completed");
            if (start_date_ != null && end_date_ != null &&
                    currentdate.after(start_date_) && currentdate.before(end_date_))
            {
                List<GagdAgent> initAgents = db_helper.getAgentsByNameAndStatus("AppOpen","INIT");
                Log.d("Agent list", "onClick: "+initAgents);
                if (!initAgents.isEmpty()) {
                    for (int i = 0; i < initAgents.size(); i++) {

                        GagdAgent agent = initAgents.get(i);
                            Log.d(Mview.TAG, "Going to start AppOpen test for "+agent.getUrl());
                            sharedPreferencesHelper.setPublicIp(Utils.publicIp());
                            execute(agent.getUrl(), agent.getId());

                            // Update status based on event type
                            if (agent.getEventType().equalsIgnoreCase("event")) {
                                db_helper.updateAgentStatus(agent.getId(), "COMPLETED");
                            } else if (agent.getEventType().equalsIgnoreCase("schedule")) {
                                db_helper.updateAgentStatus(agent.getId(), "COMPLETED");
                            }

                            // Insert results into the table (if required)
                            // db.databaseWriteExecutor.execute(() -> insertTestResults(agent.getId(), pingResult, tracerouteResult));
                        waitForCompletion();
                    }
                }else {
                    Utils.appendLog("ELOG_APP_OPEN_PANEL : No package data available in DB for app open test");
                }


            }
            else {
                Utils.appendLog("ELOG_APP_OPEN_PANEL: Status of App open TEST agent is not Active");
            }
        }

        // Reset index to start over
        db_helper.close();

    }

    public void execute(String url, String id) {
        packageName = url;
        scanId = id;
        fetchDataUsageChartBs();
        boolean launchedSuccessfully = launchChrome(url,id);


        if (launchedSuccessfully) {
            // Schedule a task to bring back the user to the app after 10 seconds
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                // Bring your app back to the foreground
                Intent returnIntent = new Intent(context, AppLaunch.class);
                returnIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                returnIntent.putExtra("panel",true);
                context.startActivity(returnIntent);

                // Remove the closed APK from the background
                ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                activityManager.killBackgroundProcesses(url);
            }, 10000); // Delay of 10 seconds

            sharedPreferencesHelper.setAppLaunched(true);

        }
    }

    private boolean launchChrome(String url, String id) {
        PackageManager packageManager = context.getPackageManager();
        try {
            // Check if Chrome is installed
            Intent intent = packageManager.getLaunchIntentForPackage(url);
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                opened = "true";
//                jsonObject.put("package_name", url);
//                jsonObject.put("id", id);

                return true;
            } else {
                jsonObject.put("app_opened", "false");
                jsonObject.put("reason","app not found");
                jsonObject.put("package_name", packageName);
                jsonObject.put("id", scanId);
                jsonObject.put("public_ip", Utils.publicIp());
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

    private synchronized void waitForCompletion() {
        try {
            // Wait for a specified time before proceeding to the next iteration
            wait(15000); // Adjust the delay as per your requirement (e.g., 15 seconds)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
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
        saveData();
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

    public void logDataUsage() {
        boolean appLaunched = sharedPreferencesHelper.isAppLaunched();
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(MviewApplication.ctx);
        String publicip = sharedPreferencesHelper.getPublicIp();
        Log.d("AppLauncher", "logDataUsage: called app open" + appLaunched+" public ip is: "+publicip);

        if (appLaunched) {
            fetchDataUsageChartAs();
            try {
//                float mobileData = (mobileDataInMbAs - mobileDataInMbBs);
//                float wifiData =(wifiDataInMbAs - wifiDataInMbBs);
//                float totalData = (totalDataAs - totalDataBs);
                float txMobileData = (mobileDataTxInMbAs-mobileDataTxInMbBs);
                float rxMobileData = (mobileDataRxInMbAs-mobileDataRxInMbBs);
                float txWifiData = (wifiDataTxInMbAs-wifiDataTxInMbBs);
                float rxWifiData = (wifiDataRxInMbAs-wifiDataRxInMbBs);
                float txTotalData = (totalDataTxAs-totalDataTxBs);
                float rxTotalData = (totalDataRxAs-totalDataRxBs);
//                Log.d("TAG", "mobile data usage is: "+mobileData+"MB");
                jsonObject.put("app_opened", opened);
                jsonObject.put("reason","App opened successfully");
                jsonObject.put("package_name", packageName);
                jsonObject.put("id", scanId);
//                jsonObject.put("mobiledata_usage",mobileData+"MB");
//                jsonObject.put("wifidata_usage", wifiData+"MB");
//                jsonObject.put("totaldata_usage", totalData+"MB");
                jsonObject.put("transmitted_mobiledata", txMobileData+"MB");
                jsonObject.put("received_mobiledata", rxMobileData+"MB");

                jsonObject.put("transmitted_wifidata", txWifiData+"MB");
                jsonObject.put("received_wifidata", rxWifiData+"MB");

                jsonObject.put("transmitted_totaldata", txTotalData+"MB");
                jsonObject.put("received_totaldata", rxTotalData+"MB");
                jsonObject.put("dns_resolution_time", " ");
                jsonObject.put("time_taken", " ");
                jsonObject.put("resolved_ip", " ");
                if (publicip != null){
                    jsonObject.put("public_ip", publicip);

                }else {
                    jsonObject.put("public_ip", " ");
                }
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
                sharedPreferencesHelper.setAppLaunched(false);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
