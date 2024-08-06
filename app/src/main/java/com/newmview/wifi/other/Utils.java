package com.newmview.wifi.other;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.content.FileProvider;

import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Telephony;
import android.telephony.PhoneNumberUtils;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.functionapps.mview_sdk2.main.Mview;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;
import com.newmview.wifi.CapturedPhoneState;
import com.newmview.wifi.MyPhoneStateListener;
import com.newmview.wifi.MySpeedTest;
import com.mview.airtel.R;
import com.newmview.wifi.activity.MainActivity;
import com.newmview.wifi.activity.CreateWalkMapActivity;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.bean.Pinger;
import com.newmview.wifi.customview.TouchImageView;
import com.newmview.wifi.database.DB_handler;
import com.newmview.wifi.helper.AllInOneAsyncTaskForNetwork;
import com.newmview.wifi.helper.RequestResponse;
import com.newmview.wifi.helper.Interfaces;
import com.newmview.wifi.listenService;
import com.newmview.wifi.mView_HealthStatus;
import com.services.AllServices;
//import com.theartofdev.edmodo.cropper.CropImageView;
import com.newmview.wifi.network.NetworkUtil;
import com.webservice.Allwebservice;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;
import java.util.function.Consumer;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.TELEPHONY_SERVICE;
import static android.content.Context.TELEPHONY_SUBSCRIPTION_SERVICE;
import static com.newmview.wifi.helper.CommonUtil.stateslist;
import static com.newmview.wifi.listenService.telMgr;
import static com.newmview.wifi.other.Config.downloaded_root;
import static com.services.AllServices.asyncTasks;

public class Utils {

    private static final String MY_PERMISSION = "1";
    private static final String TAG = "FTP";
    private static long initcount = 0;
    private static String gcmidsharedpref;
    private static Session session;
    private static String srcfinalpath;
    private static boolean upload_exc = false;
    private static boolean down_exc = false;
    private static int bytesavailable;
    private static String WEBTEST = "webtest";
    private Dialog dialog;
    private RadioGroup trendradiogroup;
    static int i = 1;
    static int blue = 255;
    static Interfaces.PingResult pingResult;
    public static final int REQUEST_CHECK_SETTINGS = 214;
    public static final int REQUEST_ENABLE_GPS = 516;
    private static String host = "198.12.250.223", username = "mview_ftp", password = "92zbVZ";
    private static int port=22;
    public static final int MAX_BITMAP_SIZE = 5 * 1024 * 1024;
    public static void addTextOverImage()
    {

    }
    public static String ping2(final String url, final Interfaces.PingResult pingResult,String ipType) {
        String[] finalVal = {""};

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    Process process = null;
                    if (Utils.checkifavailable(ipType)) {
                        if (ipType.equalsIgnoreCase("ipv6")) {
                            process = Runtime.getRuntime().exec(
                                    "ping6 -c 5 " + url);
                            Log.i("Pinger", "Command " + "ping6 -c 5 " + url);


                        } else {
                            process = Runtime.getRuntime().exec(
                                    "ping -c 5 " + url);
                            Log.i("Pinger", "Command " + "ping -c 5 " + url);
                        }
                    } else {
                        process = Runtime.getRuntime().exec(
                                "ping -c 5 -t 64 " + url);
                        Log.i("Pinger", "Command " + "ping -c 5 -t 64 " + url);
                    }

                   /* Process process = Runtime.getRuntime().exec(
                            "ping -c 1 " + url);*/


                    StringBuffer output = new StringBuffer();
                    String linenew;
                    if (process != null) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        while ((linenew = bufferedReader.readLine()) != null) {

                            Log.i(TAG, "Ping line is " + linenew);
                            output.append(linenew);


                        }
                        /*if(pingResult!=null) {
                            pingResult.parsePingResult(parsePingResponse(reader,url));
                        }*/
                        bufferedReader.close();
                    }

                    if (output != null) {
                        String packetlosss = " ";
                        String time = " ";
                        String latency=" ";
                        String finaloutput = output.toString();
                        finaloutput = finaloutput.substring(finaloutput.indexOf("ping statistics ---") + 19, finaloutput.length());
                        Log.i(TAG, "ping remaining " + finaloutput);
                        String[] split = finaloutput.split(",");
                        System.out.println(" split is"+split);
                        Log.i(TAG, "Remaining for ping for packet loss " + finaloutput);
                        if (split[2].length() > 0) {
                            split[2] = split[2].trim();
                            packetlosss = split[2].substring(0, split[2].indexOf(" "));
                            Log.i(TAG, "packet loss is " + packetlosss);
                        }
                        if (split[3].length() > 0) {
                            split[3] = split[3].trim();
                            time = split[3];
                            Log.i(TAG, "Webpage load tie is " + time);


                            String rttstring = split[3].substring(split[3].indexOf("="), split[3].length());
                            rttstring.trim();
                            String[] timesplit = rttstring.split("/");
                            latency = timesplit[1];
                        }
                        finalVal[0] = latency + " ms" + "/" + packetlosss;
                        Log.i("Pinger", " Final array ping " + finalVal[0]);
                        if (pingResult != null) {
                            pingResult.onPingResultObtained(finalVal[0]);

                        }


                    }


                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("exception is " + e.toString());
                } catch (ArrayIndexOutOfBoundsException ae) {
                    System.out.println("exception is " + ae.toString());
                    ae.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        return finalVal[0];
    }

    public static float fetchDataUsage() {
        long currentMobileTxBytes = TrafficStats.getMobileTxBytes();
        long currentMobileRxBytes = TrafficStats.getMobileRxBytes();
        long totalTxBytes = TrafficStats.getTotalTxBytes();
        long totalRxBytes = TrafficStats.getTotalRxBytes();
        float l = (float)(currentMobileTxBytes + currentMobileRxBytes);
        float mobileDataInMB = l / 1048576.0F;
        System.out.println("mobile data " + l + "");
        float wifiDataInMB = (float)((totalTxBytes + totalRxBytes) / 1048576L) - mobileDataInMB;
        Log.i(TAG, "Mobile data usage " + mobileDataInMB + "  Wifi Data Usage " + wifiDataInMB);
        if (wifiDataInMB < 0.0F) {
            wifiDataInMB = 0.0F;
        }

        if (mobileDataInMB < 0.0F) {
            mobileDataInMB = 0.0F;
        }

        String apnName =Utils.getApnType(MviewApplication.ctx);
        if (com.functionapps.mview_sdk2.helper.Utils.checkifavailable(apnName)) {
            return apnName.equalsIgnoreCase("Wifi") ? wifiDataInMB : mobileDataInMB;
        } else {
            return 0.0F;
        }
    }

    public static int getNo_ofhops(String address, int packets, int ttl) {
        String format = "ping -n -c %d -t %d %s";
        int no_hops = 0;
        String command = String.format(format, packets, ttl, address);
        Process process = null;

        try {
            process = Runtime.getRuntime().exec(command);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while((line = bufferedReader.readLine()) != null) {
                int var10000 = no_hops;
                int var10 = no_hops + 1;
                no_hops = var10000;
                Log.i(TAG, "Trace route result getting returned as " + line);
            }
        } catch (IOException var9) {
            Log.i(TAG, "Exception while getting hops " + var9.toString());
            var9.printStackTrace();
        }

        if (no_hops == 0) {
            no_hops = 12;
        }

        return no_hops;
    }
    public static Date getcurrentdate() {
        // TODO Auto-generated method stub

        long millis=System.currentTimeMillis();
        java.util.Date date=new java.util.Date(millis);
        return date;
    }

    public  static Date convertStringtoDate(String date)
    {
        if(date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                return dateFormat.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();

            }

        }
        return null;

    }

    public static String getApnType(Context context)
    {
        {
            String apn = null;
            String networkname = NetworkUtil.getConnectivityStatusString(context);
            Log.i(TAG,"Network name "+networkname);

            if (networkname.equalsIgnoreCase("Wifi enabled")) {
                apn = "Wifi";
        /*    ConstantStrings.networkname = "Wifi";
            WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            ConstantStrings.cnetworkname = wifiInfo.getSSID();*/
            } else if (networkname.equalsIgnoreCase("Mobile data enabled")) {
         /*   ConstantStrings.networkname = "Mobile data";
            ConstantStrings.cnetworkname = tm.getNetworkOperatorName();*/
                apn = "Mobile Data";

            }
            Log.i(TAG,"Apn type in apnType method "+apn);
            return apn;
        }
    }

    public static String getAPN(Context context) {
        Log.d(TAG, "getAPN: called");
        String apn = null;
        Uri uri = Uri.parse("content://telephony/carriers/preferapn");
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                apn = cursor.getString(cursor.getColumnIndex("apn"));
            }
            cursor.close();
        } else {
            Log.d("APN", "Cursor is null, unable to retrieve APN");
        }
        return apn;
    }

    public static String getAccessPointName(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                Log.d(TAG, "getAccessPointName: "+wifiInfo.getSSID());
                return wifiInfo.getSSID();
            }
        }
        return null;
    }

    public static void getNetworkInfo() {
        TelephonyManager telephonyManager = (TelephonyManager) MviewApplication.ctx.getSystemService(Context.TELEPHONY_SERVICE);
        ConnectivityManager connectivityManager = (ConnectivityManager) MviewApplication.ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (telephonyManager != null) {
            String networkOperatorName = telephonyManager.getNetworkOperatorName();
            Log.e("MainActivity", "Network Operator Name: " + networkOperatorName);
        }

        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                String connectionType = networkInfo.getTypeName();
                Log.e("MainActivity", "Connection Type: " + connectionType);
            } else {
                Log.e("MainActivity", "No active network connection");
            }
        }
    }
    public static String getRoundedOffVal(String val, int roundOffUpto) {
        if (Utils.checkIfNumeric(val)) {
            if (Utils.checkifavailable(val)) {
                BigDecimal bd = new BigDecimal(val).setScale(2, RoundingMode.HALF_UP);
                return bd.doubleValue() + "";
            }
            return "0";

        }
        return val;
    }


    public static String getBatteryPercentage(Context context) {

       /* if (Build.VERSION.SDK_INT >= 21) {

            BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
            return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        } else {

            IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, iFilter);

            int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
            int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

            double batteryPct = level / (double) scale;

            return (int) (batteryPct * 100);
        }*/
        float bat=0f;
        String battery="NA";

        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if(batteryIntent!=null) {
            int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            // Error checking that probably isn't needed but I added just in case.
            if (level == -1 || scale == -1) {
                bat= 50.0f;
            }

            bat = ((float) level / (float) scale) * 100.0f;

            battery=String.format("%.0f", bat) + "%";

        }
        return battery;
    }
    public static String isAirplaneModeOn() {
        boolean state;
        state= Settings.System.getInt(MviewApplication.ctx.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        if (state)
        {
            return "yes";
        }
        else
        {
            return "no";
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public static   int getDefaultDataSubscriptionId()  {
        SubscriptionManager subscriptionManager = (SubscriptionManager) MviewApplication.ctx.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= 24)  {
            int nDataSubscriptionId = SubscriptionManager.getDefaultDataSubscriptionId();

            if (nDataSubscriptionId != SubscriptionManager.INVALID_SUBSCRIPTION_ID)  {
                return (nDataSubscriptionId);
            }
        }

        try  {
            Class<?> subscriptionClass = Class.forName(subscriptionManager.getClass().getName());
            try {
                Method getDefaultDataSubscriptionId = subscriptionClass.getMethod("getDefaultDataSubId");

                try {
                    return ((int) getDefaultDataSubscriptionId.invoke(subscriptionManager));
                }
                catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                }
            } catch (NoSuchMethodException e1) {
                e1.printStackTrace();
            }
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }

        return (SubscriptionManager.INVALID_SUBSCRIPTION_ID);
    }

    public static String  getwifistate(Context ctx) {
        String state="off";
        ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            // Do whatever
            state="on";
        }
        return state;
    }



    public static String CIDMccValue(String val) {

        String cid = val;
        String mcc = new String(cid.substring(0,3));
        System.out.println(" substring mcc is  "+mcc);
       // String mnc = new String(cid.substring(3,6));
       // System.out.println(" substring mnc is  "+mnc);
        if(mcc==null)
        {
            return "NA";
        }
        else
        {
            return mcc;
        }
    }

    public static String CIDMncValue(String val) {

        String cid = val;
        String mnc = new String(cid.substring(3,6));
        System.out.println(" substring mnc is  "+mnc);
        if(mnc==null)
        {
            return "NA";
        }
        else
        {
            return mnc;
        }
    }

    public static void loadImage(Context context, View view, String path, ProgressBar progressBar, int drawable, boolean thumb) {


        System.out.println("img path is "+path);


        if(checkContext(context)) {
            if (!((Activity)context).isFinishing()) {
                if (view != null) {
                    if (Utils.checkifavailable(path)) {
                        //  path=getdecodedurl(path);//added by Sonal on 03-02-2021
                        if (path.contains("203.122.58.181") ||
                                path.contains("factabout.in") || path.contains("http") || path.contains("https")) {


                            if (thumb) {
                                float thumbval;


                                    thumbval = .25f;


                                Glide.with(context)
                                        .asBitmap()
                                        .load(path)

                                        .listener(new RequestListener<Bitmap>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                                Log.e("Glide", "GlideException"+e);
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                                return false;
                                            }
                                        })
                                        .thumbnail(thumbval)

                                        .apply(new RequestOptions().dontAnimate().
                                                diskCacheStrategy(DiskCacheStrategy.ALL).
                                                override(450, 450).error(drawable)) //This is important
                                        .into(new BitmapImageViewTarget((ImageView) view) {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource,
                                                                        @Nullable Transition<? super Bitmap> transition) {
                                                super.onResourceReady(resource, transition);
                                                if (progressBar != null)
                                                    if (progressBar.getVisibility() == View.VISIBLE) {
                                                        progressBar.setVisibility(View.GONE);
                                                    }
                                                int bitmapSize = resource.getByteCount();
                                                System.out.println("bitmap size "+bitmapSize);
                                                if (bitmapSize > MAX_BITMAP_SIZE) {
                                                    //  System.out.println("bitmap resizing "+bitmapSize);
                                                  /*  throw new RuntimeException(
                                                            "Canvas: trying to draw too large(" + bitmapSize + "bytes) bitmap.");*/
                                                    resource = resize(resource, resource.getWidth() / 2, resource.getHeight() / 2);
                                                    System.out.println("bitmap resizing "+resource.getByteCount());
                                                }
                                                Log.i(TAG,"Image path for loading is "+path);
                                                Log.i(TAG,"Loading image "+path);
                                                view.setImageBitmap(resource);
                                                if (view instanceof TouchImageView) {
                                                    ((TouchImageView) view).setZoom(1);
                                                }

                                            }

                                            @Override
                                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                                super.onLoadFailed(errorDrawable);
                                                if (progressBar != null) {
                                                    if (progressBar.getVisibility() == View.VISIBLE) {
                                                        progressBar.setVisibility(View.GONE);
                                                    }
                                                }
/*try {
    Glide.with(context).load(path).into(view);
}catch (Exception e)

{
    e.printStackTrace();
}*/
                                            }

                                        });
                            } else {
                                String finalPath = path;
                                Glide.with(context)
                                        .asBitmap()
                                        .load(path)
                                        .apply(new RequestOptions().dontAnimate().
                                                diskCacheStrategy(DiskCacheStrategy.ALL).
                                                override(1600, 1600).
                                                // override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).
                                                        error(drawable)) //This is important
                                        .into(new BitmapImageViewTarget((ImageView) view) {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource,
                                                                        @Nullable Transition<? super Bitmap> transition) {
                                                super.onResourceReady(resource, transition);
                                                if (progressBar != null)
                                                    if (progressBar.getVisibility() == View.VISIBLE) {
                                                        progressBar.setVisibility(View.GONE);
                                                    }
                                                int bitmapSize = resource.getByteCount();
                                                System.out.println("bitmap size "+bitmapSize);
                                                if (bitmapSize > MAX_BITMAP_SIZE) {

                                                  /*  throw new RuntimeException(
                                                            "Canvas: trying to draw too large(" + bitmapSize + "bytes) bitmap.");*/
                                                    resource = resize(resource, resource.getWidth() / 2, resource.getHeight() / 2);
                                                    System.out.println("bitmap resizing "+resource.getByteCount());
                                                }
                                                view.setImageBitmap(resource);
                                                if (view instanceof TouchImageView) {
                                                    ((TouchImageView) view).setZoom(1);
                                                }

                                            }

                                            @Override
                                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                                super.onLoadFailed(errorDrawable);
                                                Log.i("Glide","Error from 1 ");
                                                if (progressBar != null) {
                                                    if (progressBar.getVisibility() == View.VISIBLE) {
                                                        progressBar.setVisibility(View.GONE);
                                                    }
                                                }




                                                //return false;
                                            }

                                        });
                            }
                        } else {
                            System.out.println("thumb " + thumb + "context " + context);
                            if (thumb) {
                                float thumbval;


                                    thumbval = .25f;

                                File imgFile = new File(path);
                                if (imgFile.exists()) {

                                    Glide.with(context)
                                            .asBitmap()
                                            .load(imgFile)
                                            .thumbnail(thumbval)
                                            .apply(new RequestOptions().dontAnimate().centerCrop()
                                                    .placeholder(R.drawable.grey_bg).error(drawable)) //This is important
                                            .into(new BitmapImageViewTarget((ImageView) view) {
                                                @Override
                                                public void onResourceReady(@NonNull Bitmap resource,
                                                                            @Nullable Transition<? super Bitmap> transition) {
                                                    Log.i(TAG,"Loading image "+path);
                                                    Log.i(TAG,"line 0..");
                                                    super.onResourceReady(resource, transition);
                                                    System.out.println("line 1..");
                                                    if (progressBar != null) {
                                                        if (progressBar.getVisibility() == View.VISIBLE) {
                                                            progressBar.setVisibility(View.GONE);
                                                        }
                                                    }
                                                    Log.i(TAG,"line 2..");
                                                    int bitmapSize = resource.getByteCount();
                                                    System.out.println("bitmap size "+bitmapSize);
                                                    if (bitmapSize > MAX_BITMAP_SIZE) {

                                                  /*  throw new RuntimeException(
                                                            "Canvas: trying to draw too large(" + bitmapSize + "bytes) bitmap.");*/
                                                        resource = resize(resource, resource.getWidth() / 2, resource.getHeight() / 2);
                                                        System.out.println("bitmap resizing "+resource.getByteCount());
                                                    }
                                                    view.setImageBitmap(resource);
                                                    Log.i(TAG,"line 3..");
                                                    if (view instanceof TouchImageView) {
                                                        ((TouchImageView) view).setZoom(1);
                                                    }

                                                }

                                                @Override
                                                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                                    super.onLoadFailed(errorDrawable);
                                                    Log.i(TAG,"Load failed for image "+path);
                                                    if (progressBar != null) {
                                                        if (progressBar.getVisibility() == View.VISIBLE) {
                                                            progressBar.setVisibility(View.GONE);
                                                        }
                                                    }
                                                }
                                            });

                                } else {

                                    if (path.contains("gif")) {
                               /* Glide.with(context)
                                        .load("")
                                        .apply(new RequestOptions().dontAnimate().diskCacheStrategy(DiskCacheStrategy.NONE))
                                        .into(new DrawableImageViewTarget(((ImageView)view)));*/

                                        Glide.with(context).load(path).apply(new RequestOptions().dontAnimate().placeholder(R.drawable.placeholder_image)).into(new DrawableImageViewTarget(((ImageView) view)));
                                    } else {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(path)


                                                .apply(new RequestOptions().dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL).override
                                                        (1600, 1600).error(drawable)) //This is important

                                                .into(new BitmapImageViewTarget((ImageView) view) {
                                                    @Override
                                                    public void onResourceReady(@NonNull Bitmap resource,
                                                                                @Nullable Transition<? super Bitmap> transition) {
                                                        System.out.println("line 0..");
                                                        super.onResourceReady(resource, transition);
                                                        System.out.println("line 1..");
                                                        if (progressBar != null) {
                                                            if (progressBar.getVisibility() == View.VISIBLE) {
                                                                progressBar.setVisibility(View.GONE);
                                                            }
                                                        }
                                                        System.out.println("line 2..");
                                                        int bitmapSize = resource.getByteCount();
                                                        System.out.println("bitmap size "+bitmapSize);
                                                        if (bitmapSize > MAX_BITMAP_SIZE) {
                                                            //System.out.println("bitmap resizing "+bitmapSize);
                                                  /*  throw new RuntimeException(
                                                            "Canvas: trying to draw too large(" + bitmapSize + "bytes) bitmap.");*/
                                                            resource = resize(resource, resource.getWidth() / 2, resource.getHeight() / 2);
                                                            System.out.println("bitmap resizing "+resource.getByteCount());
                                                        }
                                                        view.setImageBitmap(resource);
                                                        System.out.println("line 3..");
                                                        if (view instanceof TouchImageView) {
                                                            ((TouchImageView) view).setZoom(1);
                                                        }

                                                    }

                                                    @Override
                                                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                                        super.onLoadFailed(errorDrawable);
                                                        if (progressBar != null) {
                                                            if (progressBar.getVisibility() == View.VISIBLE) {
                                                                progressBar.setVisibility(View.GONE);
                                                            }
                                                        }
                                                    }
                                                });

                                    }
                                }
                            } else {
                                File imgFile = new File(path);
                                if (imgFile.exists()) {

                                    Glide.with(context)
                                            .asBitmap()
                                            .load(imgFile)
                                            .apply(new RequestOptions().dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL).override
                                                    (1600, 1600).error(drawable)) //This is important
                                            .into(new BitmapImageViewTarget((ImageView) view) {
                                                @Override
                                                public void onResourceReady(@NonNull Bitmap resource,
                                                                            @Nullable Transition<? super Bitmap> transition) {
                                                    System.out.println("line 0..");
                                                    super.onResourceReady(resource, transition);
                                                    System.out.println("line 1..");
                                                    if (progressBar != null) {
                                                        if (progressBar.getVisibility() == View.VISIBLE) {
                                                            progressBar.setVisibility(View.GONE);
                                                        }
                                                    }
                                                    System.out.println("line 2..");
                                                    int bitmapSize = resource.getByteCount();
                                                    System.out.println("bitmap size "+bitmapSize);
                                                    if (bitmapSize > MAX_BITMAP_SIZE) {
                                                        // System.out.println("bitmap resizing "+bitmapSize);
                                                  /*  throw new RuntimeException(
                                                            "Canvas: trying to draw too large(" + bitmapSize + "bytes) bitmap.");*/
                                                        resource = resize(resource, resource.getWidth() / 2, resource.getHeight() / 2);
                                                        System.out.println("bitmap resizing "+resource.getByteCount());
                                                    }
                                                    view.setImageBitmap(resource);
                                                    System.out.println("line 3..");
                                                    if (view instanceof TouchImageView) {
                                                        ((TouchImageView) view).setZoom(1);
                                                    }

                                                }

                                                @Override
                                                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                                    super.onLoadFailed(errorDrawable);
                                                    if (progressBar != null) {
                                                        if (progressBar.getVisibility() == View.VISIBLE) {
                                                            progressBar.setVisibility(View.GONE);
                                                        }
                                                    }
                                                }
                                            });

                                } else {

                                    if (path.contains("gif")) {
                               /* Glide.with(context)
                                        .load("")
                                        .apply(new RequestOptions().dontAnimate().diskCacheStrategy(DiskCacheStrategy.NONE))
                                        .into(new DrawableImageViewTarget(((ImageView)view)));*/

                                        Glide.with(context).load(path).apply(new RequestOptions().dontAnimate().placeholder(R.drawable.placeholder_image)).into(new DrawableImageViewTarget(((ImageView) view)));
                                    } else {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(path)


                                                .apply(new RequestOptions().dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL)
                                                        .override(1600, 1600).error(drawable)) //This is important

                                                .into(new BitmapImageViewTarget((ImageView) view) {
                                                    @Override
                                                    public void onResourceReady(@NonNull Bitmap resource,
                                                                                @Nullable Transition<? super Bitmap> transition) {
                                                        System.out.println("line 0..");
                                                        super.onResourceReady(resource, transition);
                                                        System.out.println("line 1..");
                                                        if (progressBar != null) {
                                                            if (progressBar.getVisibility() == View.VISIBLE) {
                                                                progressBar.setVisibility(View.GONE);
                                                            }
                                                        }
                                                        System.out.println("line 2..");
                                                        int bitmapSize = resource.getByteCount();
                                                        System.out.println("bitmap size "+bitmapSize);
                                                        if (bitmapSize > MAX_BITMAP_SIZE) {
                                                            //     System.out.println("bitmap resizing "+bitmapSize);
                                                  /*  throw new RuntimeException(
                                                            "Canvas: trying to draw too large(" + bitmapSize + "bytes) bitmap.");*/
                                                            resource = resize(resource, resource.getWidth() / 2, resource.getHeight() / 2);
                                                            System.out.println("bitmap resizing "+resource.getByteCount());
                                                        }
                                                        view.setImageBitmap(resource);
                                                        System.out.println("line 3..");
                                                        if (view instanceof TouchImageView) {
                                                            ((TouchImageView) view).setZoom(1);
                                                        }

                                                    }

                                                    @Override
                                                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                                        super.onLoadFailed(errorDrawable);
                                                        if (progressBar != null) {
                                                            if (progressBar.getVisibility() == View.VISIBLE) {
                                                                progressBar.setVisibility(View.GONE);
                                                            }
                                                        }
                                                    }
                                                });

                                    }
                                }
                            }
                        }

                    } else {
//added by Sonal on 03-02-2021 to load drawables also using glide on 03-02-2021
                        Glide.with(context).load(drawable)
                                .apply(new RequestOptions()
                                        .dontAnimate()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                                .override(1600, 1600)
                                // apply(new RequestOptions().error(drawable)).
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e,
                                                                Object model, Target<Drawable> target,
                                                                boolean isFirstResource) {
                                        ((ImageView) view).setImageDrawable(context.getResources().getDrawable(drawable));
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        return false;
                                    }
                                })
                                .into((ImageView) view);


                        //  ((ImageView) view).setImageDrawable(context.getResources().getDrawable(drawable));

                    }
                }
            }

        }
    }

    public static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

    public static Bitmap getViewBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public  static String getRandomID() {
        Date date = new Date();
        String idgenerated = new SimpleDateFormat("yyyyMMddHHmmss").format(date);
        return idgenerated;
    }

    public  static  int  getRandomNumber()
    {
       return new Random().nextInt();
    }

    public static String saveContent(Bitmap bitmap, Context context, boolean showAlert,
                                     String message, Interfaces.SaveSuccessfullListener saveSuccessfullListener,
                                     String filename) {

        FileOutputStream ostream = null;
        String filepath=null;
        try {
            if (!downloaded_root.exists()) {
                downloaded_root.mkdirs();
            }
            File SDCardRoot = downloaded_root.getAbsoluteFile();

             if(filename==null)
             filename = Utils.getRandomString() + ".png";
             //else
            filename=filename.replaceAll(" ", ".").replaceAll("-","")
            .replaceAll(":", "");
                 //filename=filename.replaceAll(":", ".");
            Log.i("DownloadLocal filename:", "" + filename);
           // Utils.appendCrashLog("File name to be saved  "+filename);
            File file = new File(SDCardRoot, filename);
          /*  if (!file.createNewFile()) {
                file.createNewFile();
            }*/

            String path=SDCardRoot+"/"+filename;
            ostream = new FileOutputStream(path);
            if (bitmap == null) {
                System.out.println("NULL bitmap save\n");
            }
           // Utils.appendCrashLog("Compress Bitmap while saving floor map "+bitmap);
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, ostream);
           // Utils.appendCrashLog("Bitmap  after compressing "+bitmap);
            filepath=file.getPath();
           // Utils.appendCrashLog("File path  "+filepath);
            if(showAlert) {
                if(Utils.checkifavailable(message)) {
                    Utils.showAlert(false, message, context, null); }
            }
            //Utils.appendCrashLog("calling successfull listener "+bitmap);
            if(saveSuccessfullListener!=null) {
                saveSuccessfullListener.saveSuccessfull(filename, filepath);
                System.out.println("path 2 is " + filepath);
            }
        } catch (Exception e) {
            System.out.println(" Exception while saving "+e.getMessage());
           // Utils.appendCrashLog("Exception  while saving in Utils " + e.toString());
            e.printStackTrace();
        }
        return filepath;

    }

    public static Bitmap getBitmap(Canvas canvas) {
        try {
            java.lang.reflect.Field field=Canvas.class.getDeclaredField("mBitmap");
            field.setAccessible(true);
            return (Bitmap)field.get(canvas);
        }
        catch (Exception e )
        {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getBitmap(String path) {
        if(Utils.checkifavailable(path))
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

    public static  String getRealPathFromURI(Uri uri, Context context) {
        String path = "";
        if (context.getContentResolver() != null) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    public static void drawMarker(Context context, ImageView my_imageview) {
        Bitmap bmp = Bitmap.createBitmap(my_imageview.getWidth(), my_imageview.getHeight(), Bitmap.Config.ARGB_8888);
        // Create the canvas with the bitmap
        Canvas canvas = new Canvas(bmp);
        Paint p=new Paint();
        Bitmap b= BitmapFactory.decodeResource(context.getResources(), R.drawable.marker);
        p.setColor(Color.RED);
        canvas.drawBitmap(b, 0, 0, p);
    }

    public static void drawCircle(Context context, int my_image_id, ImageView my_imageview, int x, int y, int color) {

        //Toast.makeText(context.getApplicationContext(), "Single tap: " + e.getX() + ", " + e.getY(), Toast.LENGTH_SHORT).show();
        Paint paint = new Paint();
        int radius;
        radius = 100;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        // Create a bitmap object of your view size

        Bitmap bmp = Bitmap.createBitmap(my_imageview.getWidth(), my_imageview.getHeight(), Bitmap.Config.ARGB_8888);
        // Create the canvas with the bitmap
        Canvas canvas = new Canvas(bmp);
        Bitmap b= BitmapFactory.decodeResource(context.getResources(), R.drawable.marker);
        paint.setColor(Color.RED);
        canvas.drawBitmap(b, 0, 0, paint);
        // canvas.drawCircle(x, y, radius, paint);
        // Set the bitmap to the imageView
        ImageView iv = (ImageView) my_imageview;
        iv.setImageBitmap(bmp);
    }

    public static void showAlert(String msg, Context context, String filepath, String negativeText) {

        new AlertDialog.Builder(context, R.style.AlertDialogTheme)
                .setMessage(msg)


                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {



                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    public static String getRandomString() {
        return UUID.randomUUID().toString();
    }

    public static boolean checkifnull(String value) {

        if (value != null) {
            if (!value.equals("") && !value.equals(" "))
                return false;
            else
                return true;
        } else {
            return true;
        }


    }

    public static String getBattery(Context context) {

        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float bat = ((float) level / (float) scale) * 100.0f;
        return bat + "";


    }

    public static String toActivityString(int activity) {
        switch (activity) {
            case DetectedActivity.STILL:
                return "STILL";
            case DetectedActivity.WALKING:
                return "WALKING";
            default:
                return "UNKNOWN";
        }
    }

    public static String toTransitionType(int transitionType) {
        switch (transitionType) {
            case ActivityTransition.ACTIVITY_TRANSITION_ENTER:
                return "ENTER";
            case ActivityTransition.ACTIVITY_TRANSITION_EXIT:
                return "EXIT";
            default:
                return "UNKNOWN";
        }
    }

    public static boolean checkIfNumeric(String value) {
        try {

            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }

    }
    public static  void appendCrashLog(String text)
    {
        File logFile = new File("sdcard/mView_Crashlogs.txt");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                System.out.println(" exception is"+e.getMessage());
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public static  void appendLog(String text)
    {
        //for worker
        Log.d("EVT_TAG", " "+text);

        String filename = "airtel_drive_logs"+"_"+Constants.getversionnumber(MviewApplication.ctx)+".txt";
        File downloadsDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P){
//            Uri uri = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
//            Uri uri= MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);

            String data = Utils.getDateTime()+text+"\n";

            // Specify the directory

            if ( downloadsDirectory.exists()) {
                // File already exists, append to it
                Log.d("TAG", "File already exists ");

                try {
                    FileOutputStream outputStream = new FileOutputStream(downloadsDirectory, true); // Append mode
                    outputStream.write(data.getBytes());
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            else {
                // File does not exist, create it

                try {
                    FileOutputStream outputStream = new FileOutputStream(downloadsDirectory);
                    outputStream.write(data.getBytes());
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }


        }else{

            String data = Utils.getDateTime() + text + "\n";

            try {
                File logFile = new File(MviewApplication.ctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), filename);
                FileOutputStream outputStream = new FileOutputStream(logFile, true); // Append mode
                outputStream.write(data.getBytes());
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }





//        File logFile = new File("sdcard/mView_logs.txt");
//        if (!logFile.exists())
//        {
//            try
//            {
//                logFile.createNewFile();
//            }
//            catch (IOException e)
//            {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//        try
//        {
//            //BufferedWriter for performance, true to set append to file flag
//            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
//            buf.append(text);
//            buf.newLine();
//            buf.close();
//        }
//        catch (IOException e)
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }

    public static String chkDataConnectivity(Context c) {
        String s = "";
        if (!Utils.checkifavailable(mView_HealthStatus.connectionType)) {
            final ConnectivityManager connMgr = (ConnectivityManager)
                    c.getSystemService(Context.CONNECTIVITY_SERVICE);
            final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifi.isConnectedOrConnecting()) {
                s = "WiFi";
                WifiManager wm = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wm.getConnectionInfo();
                int linkSpeed = wifiInfo.getLinkSpeed();
                String ss = wifiInfo.getSSID();
                int rs = wifiInfo.getRssi();
                s = "WiFi (Link " + linkSpeed + "Mbps, " + ss + ", " + rs + "dBm)\n";
                mView_HealthStatus.connectionType = "WiFi";
                mView_HealthStatus.connectionTypeIdentifier = 1;
            } else if (mobile.isConnectedOrConnecting()) {


                s = "Mobile data"; //getNetworkTypeString (listenService.telMgr.getNetworkType());
                s += "\n";
                mView_HealthStatus.connectionType = "Mobile data";
                mView_HealthStatus.connectionTypeIdentifier = 2;
            } else {
                s = "Offline";
                mView_HealthStatus.connectionTypeIdentifier = 0;
                //Toast.makeText(this, "No Network ", Toast.LENGTH_LONG).show();
            }
            return mView_HealthStatus.connectionType;
        } else {
            return mView_HealthStatus.connectionType;
        }

    }

    public static String getLatency(String ip) {
        String latency = "NA";

        try {
            // JSONObject jsonObject=new JSONObject();
            String linenew;
            Process process = Runtime.getRuntime().exec(new String[]{"sh", "-c", "ping -c 1 -w 2  -q  " + ip});
            if (process != null) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while ((linenew = bufferedReader.readLine()) != null) {
                    Log.d("TAG", "Latency ping output is " + linenew);
                    if (linenew.contains("min/avg")) {
                        String sublin = linenew.substring(linenew.indexOf("="));
                        sublin = sublin.trim();
                        String[] split = sublin.split("/");
                        if (split[1].length() > 0) {
                            latency = split[1];
                            Log.d("TAG", "Latency is " + latency);
                        }
                    }

                }
            }
        } catch (Exception var19) {
            Log.d("TAG", " exception is" + var19);

        }
        return latency;
    }

    public static String getPacketLoss(String ip) {
        String packetLoss = "NA";
        try {
            String line;
            Process process = Runtime.getRuntime().exec(new String[]{"sh", "-c", "ping -c 10 -s 1000 " + ip});
            StringBuffer output = new StringBuffer();
            if (process != null) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while ((line = bufferedReader.readLine()) != null) {
                    Log.d("TAG", "Ping line is " + line);
                    output.append(line);
                }
                bufferedReader.close();
            }

            Log.d("TAG", "Ping final output is " + output);
            if (output != null) {
                String finalOutput = output.toString();
                int startIndex = finalOutput.indexOf("ping statistics ---");
                if (startIndex != -1) {
                    finalOutput = finalOutput.substring(startIndex + 19).trim();
                    Log.d("TAG", "Ping remaining " + finalOutput);

                    // Check if the output contains duplicates
                    if (finalOutput.contains("duplicates")) {
                        finalOutput = finalOutput.substring(finalOutput.indexOf("duplicates") + "duplicates".length()).trim();
                    }

                    String[] split = finalOutput.split(",");
                    Log.d("TAG", "Remaining for ping for packet loss " + finalOutput);

                    // Iterate through split array to find packet loss
                    for (String part : split) {
                        if (part.contains("packet loss")) {
                            packetLoss = part.trim().split(" ")[0];
                            break;
                        }
                    }

                    Log.d("TAG", "Packet loss is " + packetLoss);
                }
            }

        } catch (Exception e) {
            Log.d("TAG", "Exception is " + e);
        }
        return packetLoss;
    }


    public static void startPowerSaverIntent(final Context context) {
        SharedPreferences settings = context.getSharedPreferences("ProtectedApps", Context.MODE_PRIVATE);
        boolean skipMessage = settings.getBoolean("skipProtectedAppCheck", false);
        if (!skipMessage) {
            final SharedPreferences.Editor editor = settings.edit();
            boolean foundCorrectIntent = false;
            for (final Intent intent : Constants.POWERMANAGER_INTENTS) {
                if (isCallable(context, intent)) {
                    foundCorrectIntent = true;
                    final AppCompatCheckBox dontShowAgain = new AppCompatCheckBox(context);
                    dontShowAgain.setText("Do not show again");
                    dontShowAgain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            editor.putBoolean("skipProtectedAppCheck", isChecked);
                            editor.apply();
                        }
                    });

                    new AlertDialog.Builder(context, R.style.AlertDialogTheme)
                            .setTitle(Build.MANUFACTURER + " Protected Apps")
                            .setMessage(String.format("%s requires to be enabled in 'Protected Apps' to function properly.%n", context.getString(R.string.app_name)))
                            .setView(dontShowAgain)
                            .setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    context.startActivity(intent);
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null)
                            .show();
                    break;
                }
            }
            if (!foundCorrectIntent) {
                editor.putBoolean("skipProtectedAppCheck", true);
                editor.apply();
            }
        }
    }

    private static boolean isCallable(Context context, Intent intent) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;

    }


    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();

    }

    public static Calendar setDate(int month, int year, int date) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.MONTH, month);
        calendar1.set(Calendar.YEAR, year);
        calendar1.set(Calendar.DATE, date);
        return calendar1;
    }

    public static List<String> separateCommaSeparatedString(String value) {

        List<String> finalList = new ArrayList<>();

        String finalVal[] = value.split(",");
        finalList = Arrays.asList(finalVal);
        return finalList;


    }


    /* public static void showAlertDialog(String s, String noInternet) {
    z
         AlertDialog.Builder alert=new AlertDialog.Builder(getActivity(),R.style.AlertDialogTheme);
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
 */
    public static void showToast(Context context, String message) {
        if (context != null)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

    }

    public static void showLongToast(Context context, String message) {
        if (context != null)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();

    }
    public static boolean checkIfListContainsString(ArrayList<String> list, String str)
    {
        if (list.stream().noneMatch(s -> s.equalsIgnoreCase(str))) {
            return false;
        }
        return true;
    }

    public static int enlight(int color, float amount) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = Math.min(1.0f, amount * hsv[2]);
        return Color.HSVToColor(hsv);
    }

    public static int gradientColor(int inc) {

        int j = i++;
        int k = blue++;
        System.out.println("inc is " + inc);
        int color = Color.argb(255, 30, 30, 90 + (3 * inc));
        return color;

    }

    public static int manipulateColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);
        return Color.argb(a,
                Math.min(r, 255),
                Math.min(g, 255),
                Math.min(b, 255));
    }

    public static int getSomeRandomColor() {
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        return color;
    }


    public static int getIndexN(ArrayList<HashMap<String, String>> list, String key, String value) {
        int i;
        for (i = 0; i < list.size(); i++) {
            System.out.println("state value is " + value + "key is " + list.get(i).get(key));
            if (value.replace("&", "and").equalsIgnoreCase(list.get(i).get(key).replace("&", "and"))) {
                return i;
            }
        }
        return -1;
    }


    public static int getIndex(ArrayList<HashMap<String, String>> list, String key, String value) {
        int i;
        for (i = 0; i < list.size(); i++) {
            System.out.println("state value is " + value + "key is " + list.get(i).get(key));
            if (value.replace("_", " ").equalsIgnoreCase(list.get(i).get(key).replace("_", " "))) {
                return i;
            }
        }
        return -1;
    }


    public static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int deviceWidth = displayMetrics.widthPixels;
        return deviceWidth;
    }

    public static int getScreenHeight(Context context) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int deviceHeight = displayMetrics.heightPixels;
        return deviceHeight;
    }

    public static LatLng getLatLongOfState(String state) {
        LatLng latlng = null;
        for (int i = 0; i < stateslist.size(); i++) {
            System.out.println("states list " + stateslist.get(i));
            System.out.println("states key " + stateslist.get(i).get(state) + "name " + state);
            if (stateslist.get(i).containsKey(state)) {
                System.out.println("states contains");
                latlng = stateslist.get(i).get(state);
            }
        }
        return latlng;
    }


    public static int generateRandomNumber() {

        Random r = new Random(System.currentTimeMillis());
        return ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
    }


    public static Date ConvertToDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            //   e.printStackTrace();
        }
        return convertedDate;
    }

    private static GsmCellLocation getCellLocBySlot(Context context, String predictedMethodName, int slotID) {

        GsmCellLocation cloc = null;
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());
            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);

            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimID.invoke(telephony, obParameter);

            if (ob_phone != null) {
                cloc = (GsmCellLocation) ob_phone;

            }
        } catch (Exception e) {
            e.printStackTrace();
            //throw new ITgerMethodNotFoundException(predictedMethodName);
        }

        return cloc;
    }

    public static String parseDateToFormatddmm(String time) {


        //String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd-MM-yy HH:mm:ss";
        //SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = extractTimestampInput(time);
            str = outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static Date extractTimestampInput(String strDate) {
        Date date = null;
        final List<String> dateFormats = Arrays.asList("yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "dd-MM-yy HH:mm:ss");

        for (String format : dateFormats) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            try {
                return sdf.parse(strDate);
            } catch (ParseException e) {
                //intentionally empty
                e.printStackTrace();
            }
        }
        //throw new IllegalArgumentException("Invalid input for date. Given '"+strDate+"', expecting format yyyy-MM-dd HH:mm:ss.SSS or yyyy-MM-dd.");
        return date;
    }

    public static String parseDateTimeFormatWithoutSeconds(String time) {
        String str = null;
        try {
            // str = new SimpleDateFormat("dd-MM-yy HH:mm").format(extractTimestampInput(time));

            str = new SimpleDateFormat("dd-MM-yy HH:mm").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").
                    parse(time));
            return str;
        } catch (Exception e) {
            e.printStackTrace();

            return time;
        }
       /* String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd-MM-yyyy HH:mm";
//String outputPattern="yyyy-MM-dd'T'HH:mm";
        //SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = extractTimestampInput(time);
            str = outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }*/


    }

    private static String getDeviceIdBySlot(Context context, String predictedMethodName, int slotID) {

        String imsi = null;
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());
            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);

            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimID.invoke(telephony, obParameter);

            if (ob_phone != null) {
                imsi = ob_phone.toString();

            }
        } catch (Exception e) {
            e.printStackTrace();
            //throw new ITgerMethodNotFoundException(predictedMethodName);
        }
        System.out.println("imsi being returned as " + imsi);
        return imsi;
    }



/*    public static boolean checkifavailable(
            String myprofilephoto) {
        boolean available;

        if (myprofilephoto != null && !(myprofilephoto.equalsIgnoreCase(null) || myprofilephoto.equalsIgnoreCase(" ") ||
                myprofilephoto.equalsIgnoreCase("") ||myprofilephoto.length()==0) || myprofilephoto.isEmpty()) {
            available = true;
        } else {
            available = false;
        }
        return available;
    }*/

    public static boolean checkifavailable(String text) {
        boolean available;
        if (text != null) {
            if (text.isEmpty() || text.equalsIgnoreCase("null")) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }


    }

//    public static String check5GValue(String text) {
//        String NA = null;
//        if (text != null) {
//            if (text.isEmpty() || text.equalsIgnoreCase("2147483647")) {
//                
//                return NA;
//            } else {
//                
//            }
//                return text;
//            }
//       
//
//
//    }
    public static String returnNAIfMaxValue(int val)
    {
        if(val==Integer.MAX_VALUE)
        {
            return "NA";
        }
        else
        {
            return val+"";
        }
    }
    
    public static void getStateInfo(Context context) {

        try {
            //telInf.isSIM1Ready = getSIMStateBySlot(context, "getSimState", 0);
            System.out.println("sim state is " + getSIMStateBySlot(context, "getSimState", 0));
            if (getSIMStateBySlot(context, "getSimState", 1)) {

                String sim2_MCC = getDeviceIdBySlot(context, "getNetworkCountryIso", 0);
                String sim2_MNC = getDeviceIdBySlot(context, "getNetworkOperatorName", 0);
                System.out.println("sim state " + sim2_MCC + sim2_MNC);

                if (telMgr.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
                    final GsmCellLocation location = getCellLocBySlot(context, "getCellLocation", 0);// telMngr.getCellLocation();
                    if (location != null) {
                        String sim2_LAC = Integer.toString(location.getLac());
                        String sim2_CellID = Integer.toString(location.getCid());
                        System.out.println("sim satte lac " + sim2_LAC + "cell id " + sim2_CellID);
                    }
                }
            }


            //System.out.println("!!");
        } catch (Exception e1) {
            //Call here for next manufacturer's predicted method name if you wish
            e1.printStackTrace();
        }
    }

    private static boolean getSIMStateBySlot(Context context, String predictedMethodName, int slotID) {
        boolean isReady = false;
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());
            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimState = telephonyClass.getMethod(predictedMethodName, parameter);
            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimState.invoke(telephony, obParameter);

            if (ob_phone != null) {
                int simState = Integer.parseInt(ob_phone.toString());
                String sim2_STATE = simState(simState);
                System.out.println("sim state info " + sim2_STATE);
                if ((simState != TelephonyManager.SIM_STATE_ABSENT) && (simState != TelephonyManager.SIM_STATE_UNKNOWN)) {
                    isReady = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //throw new ITgerMethodNotFoundException(predictedMethodName);
        }

        return isReady;
    }

    private static String simState(int simState) {
        switch (simState) {
            case 0:
                return "UNKNOWN";
            case 1:
                return "ABSENT";
            case 2:
                return "REQUIRED";
            case 3:
                return "PUK_REQUIRED";
            case 4:
                return "NETWORK_LOCKED";
            case 5:
                return "READY";
            case 6:
                return "NOT_READY";
            case 7:
                return "PERM_DISABLED";
            case 8:
                return "CARD_IO_ERROR";
        }
        return "??? " + simState;
    }


    public static String formatNumber(String senderno) {
        String formattedPhone = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            formattedPhone = PhoneNumberUtils.formatNumber(senderno, Locale.getDefault().getCountry());
        } else {
            formattedPhone = PhoneNumberUtils.formatNumber(senderno);
        }
        System.out.println("formatted num is " + formattedPhone);
        return formattedPhone;
    }


    public static String getMyContactNum(Context context) {
        String number = " ";
        DB_handler db_handler = new DB_handler(context);
        db_handler.open();
        Cursor cursor = db_handler.getUserNum();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                number = cursor.getString(0);

            }
        }
        cursor.close();
        db_handler.close();
        return number;
    }
    public static String getsimslot(int slot) {
        String slotis;
        if (slot==0)
        {
            slotis="primary";

        }
        else if (slot==1)
        {
            slotis="secondary";
        }
        else if (slot==2)
        {
            slotis="tertiary";
        }
        else {
            slotis="Information not available";
        }
        return slotis;
    }
    public static String getDeviceID() {
        String deviceid;
        try {

            DB_handler helper= new DB_handler(MviewApplication.ctx);
            helper.open();
            deviceid=helper.getDeviceId();
            helper.close();


        } catch (Exception e) {
//            Utils.onFailure(2, "Device ID not found", Mview.f_result);
            Log.d(Mview.TAG,"Exception while capturing device id is "+e.toString());
            deviceid= "NA";

        }

        if (!checkifavailable(deviceid)&&deviceid.equalsIgnoreCase("NA"))
        {
            deviceid=getAndroidId(MviewApplication.ctx);
        }
        return deviceid;
    }
    public static String getstateofgps() {
        String gps=" ";
        LocationManager locationManager = (LocationManager) MviewApplication.ctx.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsis= locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (gpsis)
        {
            gps="GPS is enabled";
        }
//       else {
//           gps="GPS is disabled";
//       }

        return gps;

    }
    public static String getBuildVersion() {
        String manufacturer = Build.VERSION.RELEASE;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }
    public static String getManufacturerName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }
    public static String getDeviceModel() {
        String manufacturer = Build.MODEL;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }
    public static String getProductName() {
        String manufacturer = Build.PRODUCT;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }
    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }
    public static String getMyCircleName(Context context) {
        String name = " ";
        DB_handler db_handler = new DB_handler(context);
        db_handler.open();
        Cursor cursor = db_handler.getCircleName();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                name = cursor.getString(0);

            }
        }
        cursor.close();
        db_handler.close();
        return name;
    }
    public static String getMyUserName(Context context) {
        String name = " ";
        DB_handler db_handler = new DB_handler(context);
        db_handler.open();
        Cursor cursor = db_handler.getUserName();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                name = cursor.getString(0);

            }
        }
        cursor.close();
        db_handler.close();
        return name;
    }

    public static String getMyUserType(Context context) {
        String name = " ";
        DB_handler db_handler = new DB_handler(context);
        db_handler.open();
        Cursor cursor = db_handler.getUserType();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                name = cursor.getString(0);

            }
        }
        cursor.close();
        db_handler.close();
        return name;
    }

    public static String getvoc() {

        boolean na = false;
        String voc;


        if (MviewApplication.ctx != null) {
            try {
                ConnectivityManager cm = (ConnectivityManager) MviewApplication.ctx
                        .getSystemService(CONNECTIVITY_SERVICE);
                // test for connection
                // txt_status.setText("Internet is working");
                // txt_status.setText("Internet Connection Not Present");
                na = cm.getActiveNetworkInfo() != null
                        && cm.getActiveNetworkInfo().isAvailable()
                        && cm.getActiveNetworkInfo().isConnected();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (na) {

            return "I am able to use internet";
        } else {
            return "I am unable to use internet";
        }


    }
    public static String getPIP() {
        String ipAddress = getLocalIpAddress(true);
        if (ipAddress.equals("NA")) {
            ipAddress = getLocalIpAddress(false);
        }
        return ipAddress;
    }


    public static String getLocalIpAddress(boolean ipv4) {

        String ip="NA";
        try {
            boolean ipV4Found = false;
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            outer: while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        if (ipv4) {
                            if (inetAddress instanceof Inet4Address) {
                                System.out.println("INET ADDRESS ipv4");
                                return inetAddress.getHostAddress();
                            }
                        } else {
                            if (inetAddress instanceof Inet6Address) {
                                System.out.println("INET ADDRESS ipv6");
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "NA";

    }

    public static String publicIp(){
        final String[] ip = new String[1];

            try {
                URL url = new URL("https://api.ipify.org");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0"); // Set a User-Agent to avoid HTTP 403 Forbidden error

                try (Scanner s = new Scanner(connection.getInputStream(), "UTF-8").useDelimiter("\\A")) {
                    ip[0] = s.next();
                    Log.d(TAG, "publicIp: "+ip[0]);
                }


                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        return ip[0];
    }

    public static void fetchPublicIpUi(Consumer<String> callback) {
        new Thread(() -> {
            try {
                URL url = new URL("https://api.ipify.org");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");

                try (Scanner s = new Scanner(connection.getInputStream(), "UTF-8").useDelimiter("\\A")) {
                    String ip = s.hasNext() ? s.next() : null;
                    Log.d(TAG, "publicIp: " + ip);

                    // Invoke callback with the fetched IP address
                    if (callback != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            callback.accept(ip);
                        }
                    }
                }

                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static InetAddress getResolverIpv4() throws SocketException {
        Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
        NetworkInterface ni;
        while (nis.hasMoreElements()) {
            ni = nis.nextElement();
            Utils.appendLog("ELOG_IPV4_ELEMENTS: "+ni.getInetAddresses());
//            if (!ni.isLoopback()/*not loopback*/ && ni.isUp()/*it works now*/) {
//                for (InterfaceAddress ia : ni.getInterfaceAddresses()) {
//                    //filter for ipv4/ipv6
//                    if (ia.getAddress().getAddress().length == 4) {
//                        //4 for ipv4, 16 for ipv6
//                        return ia.getAddress();
//                    }
//                }
//            }
        }
        return null;
    }




    public static String getDomainName(String url) {
        String host = url;
        try

        {

            if (isValidIP(url)) {

            } else {

                if (!url.startsWith("http") && !url.startsWith("https")) {
                    url = "http://" + url;
                }

                URL netUrl = new URL(url);
                host = netUrl.getHost();

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();

        }
        return host;

    }



        public static boolean isValidIP(String url) {

        if (url.contains(":") && !url.contains(".")) {
            return true;
        } else if (url.matches("^.[0-9]{1,3}/..[0-9]{1,3}/..[0-9]{1,3}/..[0-9]{1,3}") == true) {
            return true;

        } else {
            return false;
        }
    }
    public static boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) MviewApplication.ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                System.out.println(serviceClass + " service is running of it");
                return true;
            }
        }
        System.out.println(serviceClass + "service is not running");
        return false;
    }
    public static String getVideocodeinyoutube(String videopath) {
        String videocode;

        videocode = videopath.substring(22);

        return videocode;
    }
    public static String getPhoneImsi(Context context) {


        String imsi="";
        if (Config.isPhoneDualSim(context)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                if (Utils.checkifavailable(SimInfoUtility.getSingleImsi(context)))
                {

                    imsi=SimInfoUtility.getSingleImsi(context);
                }
                else
                    if (Utils.checkifavailable(SimInfoUtility.getSecondIMSI(context))) {

        /*
                        ConstantStrings.imsi = Config.getSecondIMSI(context);
        */

                        imsi=SimInfoUtility.getSecondIMSI(context);


                    }

                    else
                    {
        /*
                        ConstantStrings.imsi = Config.getImsi(context);
        */


                        imsi=SimInfoUtility.getImsi(context);

                }
            }
        }
        else
        {


            imsi=SimInfoUtility.getImsi(context);
        }
        return imsi;
    }


    public static String getImsi(Context context) {
        String deviceId = " ";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            deviceId = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            System.out.println("check 1 with device id "+deviceId);
        } else {
           deviceId = getPhoneImsi(context);
          //  deviceId = getImsiForLowerDevices(context);
            System.out.println("check 2 with device id "+deviceId);
        }
        if(!Utils.checkifavailable(deviceId))
        {

            deviceId = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            System.out.println("check 3 with device id "+deviceId);
        }
      /*  String IMSI=null;
        if(context!=null) {
            TelephonyManager operator = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);

            IMSI = operator.getSubscriberId();

            return IMSI;
        }
        return IMSI;*/
        return deviceId;
    }
    public static void sendImage(boolean saved, Context context, View view, String filepath, boolean showAlert, String message, Interfaces.SaveSuccessfullListener saveSuccessfullListener, String mapId, String textToShare) {
/*if(Utils.checkifavailable(mapId))
{
    DB_handler db_handler=new DB_handler(MviewApplication.ctx);
    db_handler.open();
    db_handler.readMapDataForMapId(mapId);
    MapModel mapDetails =db_handler.readMapDataForMapId(mapId);

}*/
        if(!saved) {
            Bitmap bitmap = Utils.getViewBitmap(view);
            filepath=saveContent(bitmap,context,showAlert,message,saveSuccessfullListener, null);
        }
        if(filepath!=null) {
            Uri finalUri = Uri.fromFile(new File(filepath));
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
           /* FileProvider.getUriForFile(Objects.requireNonNull(MviewApplication.ctx),
                    BuildConfig.APPLICATION_ID + ".provider", new File(filepath));*/
//            intent.putExtra(Intent.EXTRA_STREAM,
//                    FileProvider.getUriForFile(Objects.requireNonNull(MviewApplication.ctx),
//                            BuildConfig.APPLICATION_ID + ".provider", new File(filepath)));
            //intent.putExtra(Intent.EXTRA_STREAM, finalUri);
            if(Utils.checkifavailable(textToShare))
                intent.putExtra(Intent.EXTRA_TEXT,textToShare);
                else
            intent.putExtra(Intent.EXTRA_TEXT, "WiFi-Map");
            context.startActivity(Intent.createChooser(intent, "Share Screenshot"));
        }


    }

    public static void takeToNextActivity(Context context, Class nextActivtyClass, Bundle bundle) {

        Intent intent=new Intent(context,nextActivtyClass);
        if(bundle!=null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }


        public static Uri getImageUri(Context inContext, Bitmap inImage) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
            return Uri.parse(path);

    }

    public static void showProgressDialog(Context context, Object progressMessage) {
        ProgressDialog progressDialog=new ProgressDialog(context);
        progressDialog.setTitle("");
   //     progressDialog.set
    }

    private static class GeminiMethodNotFoundException extends Exception {

        private static final long serialVersionUID = -996812356902545308L;

        public GeminiMethodNotFoundException(String info) {
            super(info);
        }
    }

    public static boolean isphonedualsime(Context context) {
        /*TelephonyManager operator = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(context);

        boolean isDualSIM = telephonyInfo.isDualSIM();
return isDualSIM;*/

        String[] simStatusMethodNames = {"getSimStateGemini", "getSimState"};

        boolean first = false, second = false;

        for (String methodName : simStatusMethodNames) {
            // try with sim 0 first
            try {
                first = getSIMStateBySlot(context, methodName, 0);
                // no exception thrown, means method exists
                second = getSIMStateBySlot(context, methodName, 1);
                return first && second;
            } catch (Exception e) {
                // method does not exist, nothing to do but test the next
            }
        }
        return false;

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public static String getsimgleimsi(Context context) {


        String imsi = null;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//
        //      boolean isSIM1Ready = getSIMStateBySlot(context, "getSimState", 0);

        try {
            Method getSubId = TelephonyManager.class.getMethod("getSubscriberId", int.class);
            SubscriptionManager sm = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                imsi = (String) getSubId.invoke(tm, sm.getActiveSubscriptionInfoForSimSlotIndex(0).getSubscriptionId()); // Sim slot 1 IMSI
                return imsi;
            }



            return imsi;
        } catch (IllegalAccessException e) {


            e.printStackTrace();
        } catch (InvocationTargetException e) {


            e.printStackTrace();
        } catch (NoSuchMethodException e) {


            e.printStackTrace();
        }
        catch (Exception e)
        {


            e.printStackTrace();
        }
        return imsi;



    }

    public static String getSecondIMSI(Context context)
    {  String imsi = null;
        Log.i(TAG,"check imsi 5");
        TelephonyManager tm = (TelephonyManager)context. getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Method getSubId = TelephonyManager.class.getMethod("getSubscriberId", int.class);
            SubscriptionManager sm = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                sm = (SubscriptionManager)context. getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                imsi = (String) getSubId.invoke(tm, sm.getActiveSubscriptionInfoForSimSlotIndex(1).getSubscriptionId()); // Sim slot 2 IMSI
            }



            return imsi;
        } catch (IllegalAccessException e) {


            e.printStackTrace();
        } catch (InvocationTargetException e) {


            e.printStackTrace();
        } catch (NoSuchMethodException e) {


            e.printStackTrace();
        }
        catch (Exception e)
        {


            e.printStackTrace();
        }
        return imsi;


    }

    private static String getImsiForLowerDevices(Context context) {
        String imsi=null;
        Log.i(TAG,"check imsi 1");
        if (isphonedualsime(context)) {
            Log.i(TAG,"check imsi 2");
            if (Utils.checkifavailable(getsimgleimsi(context)))
            {
                Log.i(TAG,"check imsi 3");
                imsi=getsimgleimsi(context);
            }
            else if (Utils.checkifavailable(getSecondIMSI(context))) {
                Log.i(TAG,"check imsi 4");
/*
                ConstantStrings.imsi = Config.getSecondIMSI(context);
*/

                imsi=getSecondIMSI(context);


            }

            else
            {
/*
                ConstantStrings.imsi = Config.getImsi(context);
*/
                Log.i(TAG,"check imsi 6");

                imsi=SimInfoUtility.getImsi(context);


            }
        }
        else
        {
            Log.i(TAG,"check imsi 7");
imsi=SimInfoUtility.getImsi(context);
          //  imsi=getImsi(context);
        }
return imsi;
    }

    public static int  getDefaultSimmm(Context context) {

        Object tm = context.getSystemService(Context.TELEPHONY_SERVICE);
        Method method_getDefaultSim;
        int defaultSimm = -1;
        try {
            method_getDefaultSim = tm.getClass().getDeclaredMethod("getDefaultSim");
            method_getDefaultSim.setAccessible(true);
            defaultSimm = (Integer) method_getDefaultSim.invoke(tm);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

       /* Method method_getSmsDefaultSim;
        int smsDefaultSim = -1;
        try {
            method_getSmsDefaultSim = tm.getClass().getDeclaredMethod("getSmsDefaultSim");
            smsDefaultSim = (Integer) method_getSmsDefaultSim.invoke(tm);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
        System.out.println("default sim "+defaultSimm);
        return defaultSimm;
    }
    public static void sendRegistrationToServer(Context context,String refreshedToken) {

        SharedPreferences sharedpreferences = context.getSharedPreferences(
                Constants.gcmid, Context.MODE_PRIVATE);
        if (sharedpreferences != null) {
            gcmidsharedpref = sharedpreferences.getString(Constants.gcmid, null);
        }
        if (gcmidsharedpref != null) {
            if (gcmidsharedpref.matches(refreshedToken)) {
            } else {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(Constants.gcmid, refreshedToken);
                editor.commit();
            }
        } else {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(Constants.gcmid, refreshedToken);
            editor.commit();
        }
System.out.println("gcmid is "+gcmidsharedpref);
    }

    public static String getDefaultSim(Context context) {
        SubscriptionInfo info = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String finalInfo = null;
            SubscriptionManager manager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            int defaultSmsId = 0;
            int defaultId = 0;

            defaultSmsId = manager.getDefaultSmsSubscriptionId();
            defaultId = manager.getDefaultSubscriptionId();
            defaultId = manager.getDefaultVoiceSubscriptionId();


            info = manager.getActiveSubscriptionInfo(defaultId);

            System.out.println("defauult sim slot index " + info.getSimSlotIndex() + "");
            return String.valueOf(info);
        }
        return String.valueOf(info);
    }

    public static ArrayList<String> getSecondImsi(Context context) {
        boolean sim1 = false;
        boolean sim2 = false;
        String imsi = null;
        ArrayList<String> final_info = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            SubscriptionManager sManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                SubscriptionInfo infoSim1 = sManager.getActiveSubscriptionInfoForSimSlotIndex(0);

                SubscriptionInfo infoSim2 = sManager.getActiveSubscriptionInfoForSimSlotIndex(1);
                if (infoSim1 != null) {
                    sim1 = true;
                    if (infoSim2 != null) {
                        sim2 = true;


                        if ((sim1) && (sim2)) {
                            final_info = new ArrayList<String>();
                            try {
                                Method getSubId = TelephonyManager.class.getMethod("getSubscriberId", int.class);
                                SubscriptionManager sm = null;

                                sm = (SubscriptionManager) context.getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE);

                                imsi = (String) getSubId.invoke(tm, sm.getActiveSubscriptionInfoForSimSlotIndex(1).getSubscriptionId());
                                System.out.println("carrier imsi 1" + (String) getSubId.invoke(tm, sm.getActiveSubscriptionInfoForSimSlotIndex(0).getSubscriptionId()));
                                System.out.println("carrier imsi 2" + (String) getSubId.invoke(tm, sm.getActiveSubscriptionInfoForSimSlotIndex(1).getSubscriptionId()));


                                if (!imsi.equals(Constants.IMSI)) {
                                    String carrier_name = (String) infoSim2.getCarrierName();
                                    String roaming_value = String.valueOf(infoSim1.getDataRoaming());
                                    final_info.add(imsi);
                                    final_info.add(carrier_name);
                                    final_info.add(roaming_value);
                                    System.out.println("carrier 1 is " + carrier_name + "imsi is " + imsi);
                                    return final_info;
                                } else {
                                    imsi = (String) getSubId.invoke(tm, sm.getActiveSubscriptionInfoForSimSlotIndex(0).getSubscriptionId());
                                    String carrier_name = (String) infoSim1.getCarrierName();
                                    String roaming_value = String.valueOf(infoSim1.getDataRoaming());
                                    final_info.add(imsi);
                                    final_info.add(carrier_name);
                                    final_info.add(roaming_value);
                                    System.out.println("carrier 2 is " + carrier_name + "imsi is " + imsi);
                                    return final_info;
                                }


                            } catch (Exception e) {

                            }
                        }

                    }

                }

            } else {
              /*  TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (telephonyManager.getSimSerialNumber() != null) {
                    // return imsi;

                }*/
            }

        }


        return final_info;

    }

    /*public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        animalNamesList.clear();
        if (charText.length() == 0) {
            animalNamesList.addAll(arraylist);
        } else {
            for (AnimalNames wp : arraylist) {
                if (wp.getAnimalName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    animalNamesList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }*/

    public static boolean checkContext(Context context) {
        if(context!=null)
        {
            System.out.println("context is "+context);
            return true;

        }
        return false;
    }

    public static int getPosition(ArrayList<Integer> rowsList, String lowerIndex) {
if(rowsList!=null && rowsList.size()>0) {
    if(checkifavailable(lowerIndex))
    for (int i = 0; i < rowsList.size(); i++) {
        System.out.println("index "+lowerIndex + "val "+rowsList.get(i));
        if (Integer.parseInt(lowerIndex) == rowsList.get(i)) {
            System.out.println("returning position "+i);
            return i;

        }
    }
}
        return 0;
    }

    public static void hideKeyboard(Context context) {
        try {
            if (checkContext(context)) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                //Find the currently focused view, so we can grab the correct window token from it.
                View view = ((Activity) context).getCurrentFocus();
                //If no view currently has focus, create a new one, just so we can grab a window token from it
                if (view == null) {
                    view = new View(context);
                }
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }



    }
    public static void openLocationAlertDialog(Context context) {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(new LocationRequest().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY));
        //builder.setAlwaysShow(true);
        LocationSettingsRequest mLocationSettingsRequest = builder.build();
        if (context != null) {
            SettingsClient mSettingsClient = LocationServices.getSettingsClient(context);

            mSettingsClient
                    .checkLocationSettings(mLocationSettingsRequest)
                    .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                        @Override
                        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                            //Success Perform Task Here
                            //Utils.showToast(getActivity(), "successss");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            int statusCode = ((ApiException) e).getStatusCode();
                            //if the location service is off
                            //Utils.showToast(getActivity(), "failed,.." +statusCode);
                            switch (statusCode) {

                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {

                                        ResolvableApiException rae = (ResolvableApiException) e;
                                        rae.startResolutionForResult((AppCompatActivity) context, REQUEST_CHECK_SETTINGS);
                                    } catch (IntentSender.SendIntentException sie) {
                                        Log.e("GPS", "Unable to execute request.");
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    Log.e("GPS", "Location settings are inadequate, and cannot be fixed here. Fix in Settings.");
                            }
                        }
                    })
                    .addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {
                            Log.e("GPS", "checkLocationSettings -> onCanceled");
                            // Utils.showToast(getActivity(), "cancelled");
                        }
                    });
        }
    }
         public static String getGcm_Id() {
        String gcmId=null;
        SharedPreferences sharedpreferences = MainActivity.context.getSharedPreferences(
                Constants.gcmid, Context.MODE_PRIVATE);
        if (sharedpreferences != null) {
            gcmId = sharedpreferences.getString(Constants.gcmid, null);
        } else {
            gcmId = "";
        }

        if (gcmId != null && gcmId != " ") {

        } else {
            gcmId = " ";
        }

        return gcmId;
    }


    public static int startUploadService(Context ctx) {
        Channel channel;
        Session session=null;
       long startTime=System.currentTimeMillis();
       int maxBuffer=500000;
        ChannelSftp sftpChannel=null;
        String startTimeN=Utils.getDateTime();
       String remoteFilePath = "/home/mview_ftp";
        JSch jsch = new JSch();
//Utils.showToast(ctx,"in upload service");
        int status=0;
        try {
            session = jsch.getSession(username, host, port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
            session.connect();
            session.setTimeout(Constants.CONNECTION_TIMEOUT);
            channel = session.openChannel("sftp");
            channel.connect();
            sftpChannel = (ChannelSftp) channel;
            byte[] buffer = new byte[maxBuffer];

          /* inputStream = new ByteArrayInputStream(buffer);
                sftpChannel.put(inputStream, remoteFilePath, new SfProgressMonitor());*/
            for(int i=0;i<=10;i++) {
                writeToFile(buffer, remoteFilePath, sftpChannel,i,ctx,startTime,startTimeN);
            }

            sftpChannel.exit();
            session.disconnect();
        } catch (JSchException e) {
            e.printStackTrace();
            status=-1;
            upload_exc=true;
            Constants.service_started=true;
        } /*catch (SftpException e) {
            e.printStackTrace();

        }*/ catch (Exception e) {
            upload_exc=true;
            e.printStackTrace();
            Constants.service_started=true;
        }
        finally {
            if(sftpChannel!=null && sftpChannel.isConnected())
            {
                sftpChannel.disconnect();
                sftpChannel.exit();
            }
            if(session!=null && session.isConnected())
            {
                session.disconnect();
            }
            if(srcfinalpath!=null) {
                Utils.deletefileFromFileManager(srcfinalpath);
            }


    if (upload_exc) {
        upload_exc = false;
        if (Utils.isNetworkAvailable(MviewApplication.ctx)) {
            Utils.startDownloadService(MviewApplication.ctx);
        } } }
        return status;

    }

    private static void writeToFile(byte[] buffer, String remoteFilePath, ChannelSftp sftpChannel, int i, Context context, long startTime,String startTimeN) {

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        File file = new File(path, "mview");
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(path, "mview" + "/" + "upload.txt");

        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(buffer);
            out.close();
            System.out.println("writing  ");
            srcfinalpath=path+"/"+"mview" + "/" + "upload.txt";
            System.out.println("sftp calling "+ i +"time");
            sftpChannel.put(srcfinalpath, remoteFilePath,new SfProgressMonitor(context,startTime,startTimeN,"upload"));


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            upload_exc=true;
        }
        catch (IOException io)
        {
            io.printStackTrace();
            upload_exc=true;
        } catch (SftpException e) {
            e.printStackTrace();
            upload_exc=true;
        }
        finally {
            if(upload_exc) {
                upload_exc=false;
                if (Utils.isNetworkAvailable(MviewApplication.ctx)) {

                    Utils.startDownloadService(MviewApplication.ctx);
                }
            }
        }


    }
    public static void startWebSpeedService(Context context)
    {
        MySpeedTest.urlArray = new ArrayList<String>();
        MySpeedTest.urlArray.add("http://www.google.com");
        MySpeedTest.urlArray.add("https://www.facebook.com");
        MySpeedTest.urlArray.add("https://www.gmail.com/");
        MySpeedTest.urlArray.add("https://www.wikipedia.org/");
        MySpeedTest.urlArray.add("https://www.quora.com/");
        mView_HealthStatus.mySpeedTest = new MySpeedTest();
        ArrayList<MySpeedTest.website> urllist = new ArrayList<>(mView_HealthStatus.mySpeedTest.webtest.websiteArr);
        if(urllist!=null && urllist.size()>0) {
            int size = mView_HealthStatus.mySpeedTest.webtest.websiteArr.size();
            for (int i = 0; i < urllist.size();i++) {

              String url=urllist.get(i).url;

                try {
                    URL url1=new URL(url);
                    long startTime=System.currentTimeMillis();
                    long urlsize=downloadUrl(url1);
                    long endtime=System.currentTimeMillis();
                    long totalTime=endtime-startTime;
                    mView_HealthStatus.mySpeedTest.webtest.websiteArr.get(i).url=url;
                    mView_HealthStatus.mySpeedTest.webtest.websiteArr.get(i).timeTakenInMS = totalTime;
                    mView_HealthStatus.mySpeedTest.webtest.websiteArr.get(i).bytes = urlsize;

                    System.out.println("webtest details :-"+" time taken is "+ totalTime +" url size is  "+urlsize);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
            System.out.println("webtest called from service");
           Allwebservice.API_sendWebTest("service");
        }
    }


    private static int downloadUrl(URL url) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            byte[] chunk = new byte[1024];
            int bytesRead;
            InputStream stream = url.openStream();
            while ((bytesRead = stream.read(chunk)) > 0) {
                outputStream.write(chunk, 0, bytesRead);
            }
            Log.i(WEBTEST, "output stream is "+outputStream.size());
        } catch (IOException e) {
            e.printStackTrace();
            return outputStream.size(); }
        return outputStream.size();
    }





    public static void toByteSize(InputStream in) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        String inputLine;
        while ((inputLine = bufferedReader.readLine()) != null) {
            System.out.println(inputLine);
        }
        Log.i(WEBTEST, "Length of String is ["+inputLine+"]");
        bufferedReader.close();

        //ByteArrayOutputStream os = new ByteArrayOutputStream();
       /*bytesavailable= in.available();

       // Log.i(WEBTEST,"bytes available are  "+bytesavailable);

        byte[] availablebytes=new byte[bytesavailable];


        //Log.i(WEBTEST,"bytes available are  "+availablebytes);



        int maxbuffersize=1*1024*1024;
        int buffersize=Math.min(bytesavailable,maxbuffersize);
        //Log.i(WEBTEST,"buffer size is "+buffersize);

        //int bytesread=in.read(availablebytes,0,buffersize);

        //Log.i(WEBTEST,"bytes read "+bytesread);

        byte[] buffer = new byte[1024];
        int len;

        // read bytes from the input stream and store them in buffer
        while ((len = in.read(buffer)) != -1) {
            // write bytes from the buffer into output stream


            System.out.println("buffer size "+buffer.length);
        }*/

    }
    private static int getFileSize(URL url) {
        URLConnection conn = null;
        try {


            /*conn = url.openConnection();
            if(conn instanceof HttpURLConnection) {
                ((HttpURLConnection)conn).setRequestMethod("HEAD");
            }
            */
           /*InputStream in= conn.getInputStream();

            toByteSize(in);*/
            return conn.getContentLength();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            /*if(conn instanceof HttpURLConnection) {
                //((HttpURLConnection)conn).disconnect();
            }*/
        }
    }

    public static void startDownloadService(Context ctx) {
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp sftpChannel=null;
        long initCount=0;
        long startTime=System.currentTimeMillis();
        String startTimeN=Utils.getDateTime();
        FileOutputStream f=null;
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        File file = new File(path, "mview" );
        if (!file.exists()) {
            file.mkdirs();
        }
        String fname="dwnld.db";

        //this is where the file will be seen after the download
        String finalPath=path + "/mview/"+fname;
        String src = "/home/mview_ftp/download/test10Mb.db";
        try {
             f = new FileOutputStream(new File(path + "/mview/", fname));
             session = jsch.getSession(username, host, port);
                session.setConfig("StrictHostKeyChecking", "no");
                session.setPassword(password);
                session.connect();
                session.setTimeout(Constants.CONNECTION_TIMEOUT);
                Channel channel = session.openChannel("sftp");
                channel.connect();
                System.out.println("download file stream "+f);
                sftpChannel = (ChannelSftp) channel;
                sftpChannel.get(src, f, new SfProgressMonitor(ctx,startTime,startTimeN,"download"));
                sftpChannel.exit();
                session.disconnect();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            down_exc=true;
            Constants.service_started=true;
        }
        catch (JSchException e) {
            down_exc=true;
            e.printStackTrace();
            Constants.service_started=true;
            // status=-1;//refused connection
        } catch (SftpException e) {
            down_exc=true;
            e.printStackTrace();//file download failed
            Constants.service_started=true;
        }
        catch (Exception e) {
            down_exc=true;
            e.printStackTrace();
            Constants.service_started=true;
        }
        finally {
            if(session!=null && session.isConnected())
            {
                session.disconnect();
            }
            if(sftpChannel!=null && sftpChannel.isConnected())
            {
                sftpChannel.disconnect();
            }
            if(down_exc)
            { down_exc=false;
                Utils.startWebSpeedService(MviewApplication.ctx);
               /*
                CapturedPhoneState obj = Utils.getCapturedData(MviewApplication.ctx);
                if (obj != null) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(Utils.isNetworkAvailable(MviewApplication.ctx)) {
                                WebService.API_sendPeriodicData(obj);
                            }
                        }
                    }, 2000);
                }*/

            }
          /*  if(this.finalPath!=null) {
                //  Utils.deletefileFromFileManager(this.finalPath);
            }*/
        } }

    public static CapturedPhoneState getCapturedData(Context context) {
        CapturedPhoneState obj = new CapturedPhoneState();
        obj.basicPhoneState = new CapturedPhoneState().new BasicPhoneState();
        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float bat = ((float)level / (float)scale) * 100.0f;
        obj.basicPhoneState.captureTime = Utils.getDateTime();
        obj.basicPhoneState.batterylevel = bat +"";
        obj.basicPhoneState.hourMin=getCurrentHourMin();
        long currentMobileTxBytes = TrafficStats.getMobileTxBytes();
        long currentMobileRxBytes = TrafficStats.getMobileRxBytes();
        long totalTxBytes = TrafficStats.getTotalTxBytes();
        long totalRxBytes = TrafficStats.getTotalRxBytes();
        double l = currentMobileTxBytes + currentMobileRxBytes;
        double mobileDataInMB = (l)/(1024*1024);
        double wifiDataInMB = ((totalTxBytes + totalRxBytes)/(1024*1024)) - mobileDataInMB;
        obj.basicPhoneState.wifiDataUsed = wifiDataInMB + "";
        obj.basicPhoneState.simDataUsed = mobileDataInMB + "";
        obj.source="all";
        if(MyPhoneStateListener.lastSignalStrength != null) {
            String s = MyPhoneStateListener.getSignalStrength(MyPhoneStateListener.lastSignalStrength);
            obj.signalStrength = s;
        }if(MyPhoneStateListener.lastServiceState != null)
            obj.roaming = MyPhoneStateListener.lastServiceState.getRoaming();
        if(MyPhoneStateListener.lastCellLocation != null) {
            obj.cellLocation = MyPhoneStateListener.lastCellLocation.toString();
            obj.cellLocationObj = MyPhoneStateListener.lastCellLocation; }
             obj.signalStrengthObj = MyPhoneStateListener.lastSignalStrength;
        if(listenService.telMgr != null) { int networkType = listenService.telMgr.getNetworkType();
        mView_HealthStatus.strCurrentNetworkState = MyPhoneStateListener.getNetworkClass(networkType,true); }
        obj.networkType = mView_HealthStatus.iCurrentNetworkState +"G";
        if( mView_HealthStatus.timeSeriesCapturedData == null) mView_HealthStatus.timeSeriesCapturedData = new ArrayList<CapturedPhoneState.BasicPhoneState>();
        int currSize = mView_HealthStatus.timeSeriesCapturedData.size();
        if(currSize  == mView_HealthStatus.MaxPeriodicDataToSaveInDB && currSize >= 1)
        { mView_HealthStatus.timeSeriesCapturedData.remove(0); }
        mView_HealthStatus.timeSeriesCapturedData.add(obj.basicPhoneState);
        return obj;
    }

    public static void openAutoStartPage(Context context) {


            try {
                Intent intent = new Intent();
                String manufacturer = android.os.Build.MANUFACTURER;
                System.out.println("manufacturer is "+manufacturer);
                if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                    intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                    intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
                } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                    intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                    System.out.println("auto restart in vivo");
                } else if ("oneplus".equalsIgnoreCase(manufacturer)) {
                    intent.setComponent(new ComponentName("com.oneplus.security", "com.oneplus.security.chainlaunch.view.ChainLaunchAppListAct‌​ivity"));
                } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                    intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
                } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                    intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
                }
                else if ("huawei".equalsIgnoreCase(manufacturer)) {
                    intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
                }
                else if ("asus".equalsIgnoreCase(manufacturer)) {
                    intent.setComponent(new ComponentName("com.asus.mobilemanager","com.asus.mobilemanager.autostart.AutoStartActivity"));
                }
                else {
                    Log.e("other phone ", "===>");
                }

                List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                System.out.println("list size in autorestart:"+list.size());

                if (list.size() > 0) {

                    final Dialog dialog = new Dialog(context, R.style.AlertDialogTheme);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.autostart_layout, null);
                    TextView serviceTxt = view.findViewById(R.id.serviceText);
                    serviceTxt.setText(Constants.autostart_msg);
                    Button ok = view.findViewById(R.id.ok);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            context.startActivity(intent); }});
                    dialog.setContentView(view);
                    dialog.show();
                }
            } catch (Exception e) {
                System.out.println("exception in autorestart"+e.toString());
                e.printStackTrace();
            }





    }

    public static void stopCallService(Context mContext) {
    }

    public static void stopAsyncTasks() {
try {
    System.out.println("async task "+asyncTasks);
    if (asyncTasks != null && !asyncTasks.isEmpty()) {
        for (int i = 0; i < asyncTasks.size(); i++) {
            AsyncTask<Object, Void, String> asyncTaskItem = asyncTasks.get(i);
            System.out.println("asyncTask before "+asyncTasks.get(i) +"status "+asyncTasks.get(i).getStatus());

            asyncTaskItem.cancel(true);
System.out.println("asyncTask "+asyncTasks.get(i) +"status "+asyncTasks.get(i).getStatus());
        }
    }

}
catch (Exception e)
{
    e.printStackTrace();
}
    }

    public static void checkForGps(Context context) {

        LocationManager locationManager = (LocationManager)MviewApplication.ctx
                .getSystemService(LOCATION_SERVICE);

        // getting GPS status
        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        boolean  isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if(!isGPSEnabled && !isNetworkEnabled ) {
            CommonAlertDialog.displayPromptForEnablingGPS(context);
        }
    }

    public static String parseDateTimeFormatWithoutSeconds2(String time) {
        String str=null;
        try {
            // str = new SimpleDateFormat("dd-MM-yy HH:mm").format(extractTimestampInput(time));

            str = new SimpleDateFormat("dd-MM-yy").format(new SimpleDateFormat("yyyy-MM-dd").
                    parse(time));
            return str;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return time;
        }

    }


    public boolean isSimAvailable(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            SubscriptionManager sManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                SubscriptionInfo infoSim1 = sManager.getActiveSubscriptionInfoForSimSlotIndex(1);
                SubscriptionInfo infoSim2 = sManager.getActiveSubscriptionInfoForSimSlotIndex(2);
                if (infoSim1 != null || infoSim2 != null) {
                    return true;
                }
            } else {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (telephonyManager.getSimSerialNumber() != null) {
                    return true;
                }
            }

        }
        return false;
    }


    public static String getSim2IMSI(Context context) {
        String imsi = null;
        if (context != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {

                TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
                try {
                    Method getSubId = TelephonyManager.class.getMethod("getSubscriberId", int.class);
                    SubscriptionManager sm = null;

                    sm = (SubscriptionManager) context.getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE);

                    imsi = (String) getSubId.invoke(tm, sm.getActiveSubscriptionInfoForSimSlotIndex(1).getSubscriptionId()); // Sim slot 2 IMSI
                    return imsi;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                return imsi;
            }
        }
        return imsi;
    }

    public static long getFileSize(FTPClient ftp, String filePath) throws Exception {
        long fileSize = 0;
        FTPFile[] files = ftp.listFiles(filePath);
        if (files.length == 1 && files[0].isFile()) {
            fileSize = files[0].getSize();
        }
        Log.i(TAG, "File size = " + fileSize);
        return fileSize;
    }

    /*public static  String getDateAndTime()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }
*/

    public static String ping(String url) {
        String str = "";
        try {
            Process process = Runtime.getRuntime().exec(
                    "/system/bin/ping -c 8 " + url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            int i;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((i = reader.read(buffer)) > 0)
                output.append(buffer, 0, i);
            reader.close();

            // body.append(output.toString()+"\n");
            str = output.toString();
            // Log.d(TAG, str);
        } catch (IOException e) {
            // body.append("Error\n");
            e.printStackTrace();
        }
        System.out.println("ping url is "+str);
        return str;
    }



    public static Pinger parsePingResponse(BufferedReader stdInput1, String url)
    {
        Pinger pingData=new Pinger();
        String sudoScript="";
        String rtt_min = null, rtt_avg = null, rtt_max = null, rtt_mdev = null, rtt_dev = null, time_unit_val = null, packet_loss = null;
        try {
           /* int i;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            String op[] = new String[64];
            while ((i = stdInput1.read(buffer)) > 0)
                output.append(buffer, 0, i);
            op = output.toString().split("\n");
            Log.i("Pinger","Other response is "+ Arrays.toString(op));*/
            while ((sudoScript = stdInput1.readLine()) != null) {
                Log.i("Pinger","Pinger result is "+sudoScript);

                if (sudoScript.contains("rtt min/avg/max/mdev")) {

                    String values = sudoScript.substring(sudoScript.indexOf("=") + 1, sudoScript.length());
                    System.out.println("values for ping" + values);
                    String[] splitvalues = values.split("/");
                    rtt_min = splitvalues[0];
                    System.out.println("rtt is " + rtt_min);
                    pingData.setRttMin(rtt_min);

                    rtt_avg = splitvalues[1];
                    System.out.println("rtt_avg " + rtt_avg);

                    rtt_max = splitvalues[2];
                    System.out.println("rtt max is " + rtt_max);
                    pingData.setRrtMax(rtt_max+"");
                    rtt_mdev = splitvalues[3];
                    System.out.println("rtt_ven " + rtt_mdev);
                    String[] rtt_dev_ = rtt_mdev.split(" ");
                    rtt_dev = rtt_dev_[0];
                    time_unit_val = rtt_dev_[1];
                    pingData.setRttAvg(rtt_avg);
                    pingData.setLatency(rtt_avg);
                    pingData.setTimeUnit(rtt_dev_[1]+"");
                    pingData.setLatency(rtt_avg+"");
                    pingData.setRttDev(rtt_dev_[0]+"");

                }
                if (sudoScript.contains("(")) {



                } else if (sudoScript.contains("packet loss")) {
                    String packetloss = sudoScript.substring(sudoScript.indexOf("packet loss") - 4, sudoScript.indexOf("packet loss") - 1);
                    System.out.println("values for packet loss in second check" + packetloss);
                    String[] splitvalues = packetloss.split("/");
                    packet_loss = splitvalues[0];
                    pingData.setPacketLoss(packet_loss+"");
                    // jsonObject.put("packet_loss", packet_loss);
                    System.out.println("packet loss is " + packet_loss);
                }
                pingData.setHost(url);


            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return pingData;

    }

    public static String ping1(final String url, final Interfaces.PingResult pingResult,String ipType) {
        String[] finalVal = {""};

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    Process process = null;
                    if (Utils.checkifavailable(ipType)) {
                        if (ipType.equalsIgnoreCase("ipv6")) {
                            process = Runtime.getRuntime().exec(
                                    "ping6 -c 5 " + url);
                            Log.i("Pinger", "Command " + "ping6 -c 5 " + url);


                        } else {
                            process = Runtime.getRuntime().exec(
                                    "ping -c 5 " + url);
                            Log.i("Pinger", "Command " + "ping -c 5 " + url);
                        }
                    } else {
                        process = Runtime.getRuntime().exec(
                                "ping -c 5 -t 64 " + url);
                        Log.i("Pinger", "Command " + "ping -c 5 -t 64 " + url);
                    }

                   /* Process process = Runtime.getRuntime().exec(
                            "ping -c 1 " + url);*/


                    StringBuffer output = new StringBuffer();
                    String linenew;
                    if (process != null) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        while ((linenew = bufferedReader.readLine()) != null) {

                            Log.i(TAG, "Ping line is " + linenew);
                            output.append(linenew);


                        }
                        /*if(pingResult!=null) {
                            pingResult.parsePingResult(parsePingResponse(reader,url));
                        }*/
                        bufferedReader.close();
                    }

                    if (output != null) {
                        String packetlosss = " ";
                        String time = " ";
                        String latency=" ";
                        String finaloutput = output.toString();
                        finaloutput = finaloutput.substring(finaloutput.indexOf("ping statistics ---") + 19, finaloutput.length());
                        Log.i(TAG, "ping remaining " + finaloutput);
                        String[] split = finaloutput.split(",");
                        Log.i(TAG, "Remaining for ping for packet loss " + finaloutput);
                        if (split[2].length() > 0) {
                            split[2] = split[2].trim();
                            packetlosss = split[2].substring(0, split[2].indexOf(" "));
                            Log.i(TAG, "packet loss is " + packetlosss);
                        }
                        if (split[3].length() > 0) {
                            split[3] = split[3].trim();
                            time = split[3];
                            Log.i(TAG, "Webpage load tie is " + time);


                            String rttstring = split[3].substring(split[3].indexOf("="), split[3].length());
                            rttstring.trim();
                            String[] timesplit = rttstring.split("/");
                            latency = timesplit[1];
                        }
                        finalVal[0] = latency + " ms" + "/" + packetlosss;
                        Log.i("Pinger", " Final array ping " + finalVal[0]);
                        if (pingResult != null) {
                            pingResult.onPingResultObtained(finalVal[0]);

                        }


                    }


                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("exception is " + e.toString());
                } catch (ArrayIndexOutOfBoundsException ae) {
                    System.out.println("exception is " + ae.toString());
                    ae.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        return finalVal[0];
    }
//

    public static String getDateTimeWithoutMs() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getCurrentHourMin()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "HH:mm", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static long calculateCallDuration(String idleTime, String offhookTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        try {
            Date idleDate = dateFormat.parse(idleTime);
            Date offhookDate = dateFormat.parse(offhookTime);

            // Calculate the duration in milliseconds
            long durationInMillis = idleDate.getTime() - offhookDate.getTime();

            // Convert duration to seconds, minutes, etc., if needed
            long durationInSeconds = durationInMillis / 1000;
            return durationInSeconds;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public static void sendIssue(String msg, String type) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", msg);
            jsonObject.put("type",type);

            JSONArray reportArray=new JSONArray();
            reportArray.put(jsonObject);
            RequestResponse.sendEvent(reportArray, AllInOneAsyncTaskForNetwork.AsyncTaskPurpose.REPORT_ISSUE_EVT,"report_issue");
            //   WebService.API_sendReportIssue(lat, lon, "NONE", type);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void showReportIssueAlert(final String type, final Context context, final String lat, final String lon, final String msg) {


      //  MainActivity.REPORT_FLAG = false;

        final Dialog dialog = new Dialog(context, R.style.AlertDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.report_issue, null);


        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        Button okbutton = view.findViewById(R.id.ok);
        Button cancelbutton = view.findViewById(R.id.cancel);
        final EditText issueet = view.findViewById(R.id.issue_et);
        okbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showAlert(false, msg, context, null);
                sendIssue(issueet.getText().toString(),type);
          //      WebService.API_sendReportIssue(lat, lon, issueet.getText().toString(), type);
               // new WebService.Async_SendNeighboringCellsInfo().execute();
                dialog.dismiss();

            }
        });
        cancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }



    public static int doGradient(double value, double min, double max, int min_color, int max_color) {
        //Log.i(TAG,"Returning enter for color green "+max_color +" min color red "+min_color +" for val "+value);
        //GradientPaint
        if (value >= max) {
         //   Log.i(TAG,"Returning max color "+max_color);
            return max_color;
        }
        else
        if (value <= min) {
         //   Log.i(TAG,"Returning min color "+min_color);
            return min_color;
        }

        float[] hsvmin = new float[3];
        float[] hsvmax = new float[3];
        float frac = (float)((value - min) / (max - min));
        Log.i(TAG,"Value "+value +" min "+min +" max "+max +" frac "+frac);
        Color.RGBToHSV(Color.red(min_color), Color.green(min_color), Color.blue(min_color), hsvmin);
        Color.RGBToHSV(Color.red(max_color), Color.green(max_color), Color.blue(max_color), hsvmax);
        float[] retval = new float[3];
        for (int i = 0; i < 3; i++) {
            retval[i] = interpolate(hsvmin[i], hsvmax[i], frac);
        }
        return Color.HSVToColor(retval);
    }
    private static float interpolate(float a, float b, float proportion) {
        return (a + ((b - a) * proportion));
    }
    public static void getConfirmationFromUserAlert(Context context, String msg, String positiveText, String negativeText, Interfaces.DialogButtonClickListener dialogButtonClickListener) {
        if(context!=null) {
            final androidx.appcompat.app.AlertDialog.Builder alertdialog = new androidx.appcompat.app.AlertDialog.Builder(context, R.style.AlertDialogTheme);
            alertdialog.setMessage(msg).
                    setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(dialogButtonClickListener !=null)
                            {
                                dialogButtonClickListener.onPositiveButtonClicked("");
                            }

                        }
                    })
                    .setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
            alertdialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if(dialogButtonClickListener !=null)
                    {
                        dialogButtonClickListener.onDialogDismissed("");
                    }
                }
            });
        }

    }

    public static void showAlert(String msg, Context context) {

        final Dialog dialog = new Dialog(context,R.style.AlertDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) context
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

            }
        });
    }

    public static void showAlert(boolean editable, String msg, Context context, Interfaces.DialogButtonClickListener dialogButtonClickListener) {

        final Dialog dialog = new Dialog(context, R.style.AlertDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.alertdialog, null);
        TextView textView = view.findViewById(R.id.msg);
        EditText et=view.findViewById(R.id.editText);
        if(editable)
        {
            et.setVisibility(View.VISIBLE);
        }
        else {
            et.setVisibility(View.GONE);
        }
        textView.setText(msg);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        Button okbutton = view.findViewById(R.id.ok);
        okbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String text=et.getText().toString();
               Log.i(TAG,"Text on et is "+text);
if(dialogButtonClickListener !=null)
{
    if(editable)
    {
        if(!Utils.checkifavailable(text))
        {
            Utils.showToast(context,"Please enter some text!");
        }
        else
        {
            dialog.dismiss();
            dialogButtonClickListener.onPositiveButtonClicked(text);

        }
    }
    else
    {
        dialog.dismiss();
        dialogButtonClickListener.onPositiveButtonClicked("");

    }

}
else
{
    dialog.dismiss();
}


            }
        });
    }

    public static void getSimInfo(Context context) {
        System.out.println("Sim info function");

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {


                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("simget imsi is +"+getImsi(context));
                    System.out.println("Sim permission");
                    List<SubscriptionInfo> subscriptionInfos = null;

                    subscriptionInfos = SubscriptionManager.from(MviewApplication.ctx).getActiveSubscriptionInfoList();


                    for (int i = 0; i < subscriptionInfos.size(); i++) {
                        mView_HealthStatus.subSize=subscriptionInfos.size();

                        SubscriptionInfo lsuSubscriptionInfo = subscriptionInfos.get(i);
                        System.out.println("SimgetSlot " + lsuSubscriptionInfo.getSimSlotIndex());
                        String imsi = getImsiSlotWise(lsuSubscriptionInfo.getSimSlotIndex(), context);
                        System.out.println("SimgetImsi " + imsi);
                        if (mView_HealthStatus.subSize > 1)
                        {
                            if (Constants.IMSI != null) {
                                System.out.println("imsi on comparing is " + getImsi(context));
                                if (getImsi(context).equals(getImsiSlotWise(0, context))) {
                                    mView_HealthStatus.sec_carrierMode = 2;
                                    if (mView_HealthStatus.carrier_selection) {
                                        mView_HealthStatus.prim_carrierMode = 1;

                                    } else {
                                        mView_HealthStatus.prim_carrierMode = 0;

                                    }

                                } else {
                                    mView_HealthStatus.prim_carrierMode = 2;
                                    if (mView_HealthStatus.carrier_selection) {
                                        mView_HealthStatus.sec_carrierMode = 1;

                                    } else {
                                        mView_HealthStatus.sec_carrierMode = 0;

                                    }
                                }
                                System.out.println("carrier prime " + mView_HealthStatus.prim_carrierMode);
                                System.out.println("carrier sec " + mView_HealthStatus.sec_carrierMode);
                                // {
                                if (i == 0) {
                                    getPrimaryInfo(lsuSubscriptionInfo, imsi);

                                    //mView_HealthStatus.prim_ss= MyPhoneStateListener.getSignalStrengthForPrim();
                                    //telephonyManager = telMgr.createForSubscriptionId(lsuSubscriptionInfo.getSubscriptionId());

                                } else {

                                    mView_HealthStatus.simPref = "Secondary";
                                    mView_HealthStatus.sec_carrierName = (String) lsuSubscriptionInfo.getCarrierName();
                                    if (lsuSubscriptionInfo.getDataRoaming() == 1) {
                                        mView_HealthStatus.sec_getDataRoaming = "Yes";
                                    } else {
                                        mView_HealthStatus.sec_getDataRoaming = "No";
                                    }
                                    mView_HealthStatus.sec_mcc = lsuSubscriptionInfo.getMcc();
                                    mView_HealthStatus.sec_mnc = lsuSubscriptionInfo.getMnc();
                                    mView_HealthStatus.sec_imsi = imsi;
                                    mView_HealthStatus.sec_slot = lsuSubscriptionInfo.getSimSlotIndex();
                                    System.out.println("simget icc "+lsuSubscriptionInfo.getIccId());
                                    System.out.println("getSim1 roaming"+lsuSubscriptionInfo.getDataRoaming());
                                    //mView_HealthStatus.sec_ss=MyPhoneStateListener.getSignalStrengthForSec();


                                }

                            }
                    }
                        else
                        {

                            mView_HealthStatus.sec_carrierMode = 2;
                            if (mView_HealthStatus.carrier_selection) {
                                mView_HealthStatus.prim_carrierMode = 1;

                            } else {
                                mView_HealthStatus.prim_carrierMode = 0;

                            }
                            getPrimaryInfo(lsuSubscriptionInfo, imsi);
                        }






                    }
                }


            }
            else
            {


                mView_HealthStatus.simPref = "Primary";
                mView_HealthStatus.prim_carrierName = telMgr.getSimOperator();

                if (MyPhoneStateListener.lastServiceState.getRoaming()) {
                    mView_HealthStatus.prim_getDataRoaming = "Yes";
                } else {
                    mView_HealthStatus.prim_getDataRoaming = "No";
                }

                mView_HealthStatus.prim_imsi = Constants.IMSI;
                mView_HealthStatus.prim_Slot=0;

                if (mView_HealthStatus.carrier_selection) {
                    mView_HealthStatus.prim_carrierMode = 1;

                } else {
                    mView_HealthStatus.prim_carrierMode = 0;

                }

               // getOutput(context,"getCarrierName",0);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
            return;
        }

    private static void getPrimaryInfo(SubscriptionInfo lsuSubscriptionInfo, String imsi) {

        mView_HealthStatus.simPref = "Primary";
        mView_HealthStatus.prim_carrierName = (String) lsuSubscriptionInfo.getCarrierName();
        if (lsuSubscriptionInfo.getDataRoaming() == 1) {
            mView_HealthStatus.prim_getDataRoaming = "Yes";
        } else {
            mView_HealthStatus.prim_getDataRoaming = "No";
        }
        mView_HealthStatus.prim_mcc = lsuSubscriptionInfo.getMcc();
        mView_HealthStatus.prim_mnc = lsuSubscriptionInfo.getMnc();
        mView_HealthStatus.prim_imsi = imsi;
        mView_HealthStatus.prim_Slot=lsuSubscriptionInfo.getSimSlotIndex();
        System.out.println("simget icc 1"+lsuSubscriptionInfo.getIccId());
        System.out.println("getSim roaming"+lsuSubscriptionInfo.getDataRoaming());

    }



    public static void printTelephonyManagerMethodNamesForThisDevice(Context context) {

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Class<?> telephonyClass;
        try {
            telephonyClass = Class.forName(telephony.getClass().getName());
            Method[] methods = telephonyClass.getMethods();
            for (int idx = 0; idx < methods.length; idx++) {

                System.out.println("\n" + methods[idx] + " declared by " + methods[idx].getDeclaringClass());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static String getImsiSlotWise(int slot,Context context) {

            String imsi = null;
            imsi =getImsi(context);
            /*TelephonyManager tm = (TelephonyManager)context.getSystemService(TELEPHONY_SERVICE);
            try {
                Method getSubId = TelephonyManager.class.getMethod("getSubscriberId", int.class);
                SubscriptionManager sm = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                    sm = (SubscriptionManager) context.getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE);
                    imsi = (String) getSubId.invoke(tm, sm.getActiveSubscriptionInfoForSimSlotIndex(slot).getSubscriptionId());
                }

                return imsi;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
*/            return imsi;

        }

        public static void deletefileFromFileManager(String path) {
        if(path!=null) {
            try {
                File fdelete = new File(path);
                if (fdelete.exists()) {
                    if (fdelete.delete()) {
                        System.out.println("file Deleted :" + path);
                    } else {
                        System.out.println("file not Deleted :" + path);
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }

    public static  void deleteFileFromStorage(String path) {
            if (path != null) {
                try {

                    String finalpath = Environment.getExternalStorageDirectory() + "/" + path;

// go to your directory
                    File fileList = new File(finalpath);

//check if dir is not null
                    if (fileList != null) {

                        // so we can list all files
                        File[] filenames = fileList.listFiles();

                        // loop through each file and delete
                        for (int i1 = 0; i1 < filenames.length; i1++) {
                            File tmpf = filenames[i1];
                            tmpf.delete();
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

        }

        private static class SfProgressMonitor implements SftpProgressMonitor {

        private  String startTimeN=null;
        private long max = 0;
        private long count = 0;
        private long percent = 0;
        private long tt = 0;
        float bandInbps =0;

        long endtime = 0;
        private Context context;

        private int maxBuffer=500000;
     long startTime;
     String type;

        public SfProgressMonitor(Context context, long startTime,String startTimeN,String type) {
            this.context=context;
            this.startTime=startTime;
            this.startTimeN=startTimeN;
            this.type=type;

        }

        public SfProgressMonitor(String type) {
            this.type=type;
        }


        @Override
        public void init(int op, String src, String dest, long max) {
            this.max = max;

            System.out.println("starting");



            System.out.println("sftp source "+src); // Origin destination
            System.out.println("sftp des "+dest); // Destination path
            System.out.println("sftp max "+max); // Total filesize

        }

        @Override
        public boolean count(long bytes) {


            System.out.println("sftp count "+bytes +"init count "+initcount);

             System.out.println("sftp type is "+type);
            if (type.equalsIgnoreCase("upload")) {
                this.count += bytes;
                initcount+= bytes;
                long percentNow = this.count * 100 / maxBuffer;

                long finalpercentNow=(initcount * 100)/(maxBuffer*10);
                if (percentNow > this.percent) {
                    this.percent = percentNow;


                    System.out.println("sftp progress" + this.percent); // Progress 0,0
                    //  System.out.println("sftp progress max"+max); //Total filesize
                    System.out.println("sftp progress count" + this.count); // Progress in bytes from the total


                    tt = System.currentTimeMillis() - startTime;
                    System.out.println("sftp time " + tt);
                    float bandvalue = 0;
                    try {

                        bandInbps = ((initcount * 8) / ((tt) / 1000));
                        final float currentband = bandInbps / (1024);
                        bandvalue = bandInbps / (1000 * 1000);
                        long ct = initcount / (tt / 1000);
                        System.out.println("sftp band progress mbps " + bandvalue + " count " + ct);
                    } catch (ArithmeticException ae) {
                        ae.printStackTrace();
                    }


                    final String str = String.format("%.2f", bandvalue);
                    final float finalBandvalue = bandvalue;

                    System.out.println("str is " + str + "tt is " + tt + "bps" + bandInbps);



                }
            }
            else if(type.equalsIgnoreCase("download"))
            {

                this.count += bytes;
                long percentNow = this.count * 100 / max;

                if (percentNow > this.percent) {
                    this.percent = percentNow;


                    System.out.println("download progress percent" + this.percent); // Progress 0,0


                    int val1 = (int) ((count * 100) / max);


                    long tt = System.currentTimeMillis() - startTime;
                    System.out.println("download progress count of bytes transferred till now "+this.count  +" time  "+tt); // Progress in bytes from the total

                    float bandvalue=0;
                    try {

                        bandInbps = ((this.count * 8) / ((tt) / 1000));
                        System.out.println("download progress band in bps " + bandInbps);
                        float currentband = bytes / (tt/1000); //KBps
                        System.out.println("Download speed in KBPS "+currentband);
                        bandvalue = bandInbps / (1000 * 1000);//Mbps
                        System.out.println("download progress band in mbps " + bandvalue);

                    }
                    catch (ArithmeticException ae)
                    {
                        ae.printStackTrace();
                    }
                    float time = tt / 1000;


                }

            }






            return (true);

        }

        @Override
        public void end() {
            System.out.println("sftp finished");// The process is over
            System.out.println(this.percent); // Progress
            System.out.println("sftp end "+max); // Total filesize
            System.out.println("sftp count "+this.count); // Process in bytes from the total


if(type.equalsIgnoreCase("upload")) {

    if (initcount == maxBuffer * 10) {System.out.println("init and max are same");
        endtime = System.currentTimeMillis();
        tt = endtime - startTime;
        // long sz = totalBytesTransferred;
        bandInbps = ((initcount * 8) / ((tt) / 1000));
        float band = (bandInbps) / (1000 * 1000); //Mbps
        float szInMB = (float) initcount / (1000 * 1000);
        float ttInSecs = tt / 1000;
        System.out.println("sftp bandinbps " + bandInbps + "maxbuffer " + maxBuffer);
if(mView_HealthStatus.mySpeedTest==null)

        MySpeedTest.urlArray = new ArrayList<String>();
        MySpeedTest.urlArray.add("http://www.google.com");
        MySpeedTest.urlArray.add("http://www.facebook.com");
        MySpeedTest.urlArray.add("http://www.gmail.com");
        MySpeedTest.urlArray.add("http://www.wikipedia.org");
        MySpeedTest.urlArray.add("http://www.yahoo.com");
        mView_HealthStatus.mySpeedTest = new MySpeedTest();
        mView_HealthStatus.mySpeedTest.uploadtest = mView_HealthStatus.mySpeedTest.new UploadDownload();
        mView_HealthStatus.mySpeedTest.uploadtest.isRoaming = mView_HealthStatus.roaming;
        mView_HealthStatus.mySpeedTest.uploadtest.lat = listenService.gps.getLatitude();
        mView_HealthStatus.mySpeedTest.uploadtest.lon = listenService.gps.getLongitude();
        mView_HealthStatus.mySpeedTest.uploadtest.networkType = mView_HealthStatus.iCurrentNetworkState;
        mView_HealthStatus.mySpeedTest.uploadtest.sizeInBytes = maxBuffer * 10;
        mView_HealthStatus.mySpeedTest.uploadtest.startTime = startTimeN;
        mView_HealthStatus.mySpeedTest.uploadtest.timeTakenInMS = tt;
        mView_HealthStatus.mySpeedTest.uploadtest.type = 1;
        mView_HealthStatus.mySpeedTest.uploadtest.protocol = mView_HealthStatus.connectionType;
        mView_HealthStatus.mySpeedTest.uploadtest.source="service";
                   /* Intent sendmsg1 = new Intent("speed_result");
                    sendmsg1.putExtra("msg", "2");
                    sendmsg1.putExtra("msgshow", uploadResponse);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(sendmsg1);*/
        //  status = 1;
        System.out.println("sftp sending ");
           /* ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {*/
        AsyncTask<Object, Void, String> upload= new Allwebservice.Async_SendUporDownloadtestResults().execute(1);
        if(AllServices.asyncTasks!=null )
            AllServices.asyncTasks.add(upload);
        initcount=0;
               /* }

            });
*/
        // new WebService.Async_SendNeighboringCellsInfo().execute();

//                   TinyDB db = new TinyDB(Dwnld_upload_fragment.mContext);
//                    db.putObject("upload", mView_HealthStatus.mySpeedTest.uploadtest);


    }
}

else if(type.equalsIgnoreCase("download"))
{

        endtime = System.currentTimeMillis();
        long tt = endtime - startTime;

        long sz = max;//localFile.length();
        bandInbps = ((sz) / ((endtime - startTime) / 1000));
        float band = (bandInbps*8) / (1000 * 1000); //Mbps
        float szInMB = sz / (1000 * 1000);
        float ttInSecs = tt / 1000;
        String downloadResponse = "/" + String.format("%.2f", ttInSecs) + "/" +
                String.format("%.2f", szInMB) + "/" + String.format("%.2f", band) + "Mbps";


        float time = ttInSecs;

    if(mView_HealthStatus.mySpeedTest==null)
        MySpeedTest.urlArray = new ArrayList<String>();
    MySpeedTest.urlArray.add("http://www.google.com");
    MySpeedTest.urlArray.add("http://www.facebook.com");
    MySpeedTest.urlArray.add("http://www.gmail.com");
    MySpeedTest.urlArray.add("http://www.wikipedia.org");
    MySpeedTest.urlArray.add("http://www.yahoo.com");
        mView_HealthStatus.mySpeedTest = new MySpeedTest();
        mView_HealthStatus.mySpeedTest.downloadtest = mView_HealthStatus.mySpeedTest.new UploadDownload();
        mView_HealthStatus.mySpeedTest.downloadtest.isRoaming = mView_HealthStatus.roaming;
        mView_HealthStatus.mySpeedTest.downloadtest.lat = listenService.gps.getLatitude();
        mView_HealthStatus.mySpeedTest.downloadtest.lon = listenService.gps.getLongitude();
        mView_HealthStatus.mySpeedTest.downloadtest.networkType = mView_HealthStatus.iCurrentNetworkState;
        mView_HealthStatus.mySpeedTest.downloadtest.sizeInBytes = max;
        mView_HealthStatus.mySpeedTest.downloadtest.startTime = startTimeN;
        mView_HealthStatus.mySpeedTest.downloadtest.timeTakenInMS = tt;
        mView_HealthStatus.mySpeedTest.downloadtest.type = 2;
        mView_HealthStatus.mySpeedTest.downloadtest.protocol = mView_HealthStatus.connectionType;
    mView_HealthStatus.mySpeedTest.downloadtest.source="service";


    AsyncTask<Object, Void, String> download=  new Allwebservice.Async_SendUporDownloadtestResults().execute(2);
    if(AllServices.asyncTasks!=null )
        AllServices.asyncTasks.add(download);
    initcount=0;


}
        }

    }

    public static boolean isMaxint(int a) {
        if (a==Integer.MAX_VALUE)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static String DecToHex(int dec) {
//        return String.format("%x", dec);
        return Integer.toHexString(dec);

    }

    public static int HexToDec(String hex) {
        return Integer.parseInt(hex, 16);

    }

    public static int getenb(int cid) {
           /*
                        Getting enb
                         */
        int eNB = 0;
        if (cid != Integer.MAX_VALUE) {
            String cellidHex = DecToHex(cid);
            if (cellidHex != null) {
                //System.out.println("cellidhex..  "+cellidHex);//66
                if (cellidHex.length() > 2) {

                    String eNBHex = cellidHex.substring(0, cellidHex.length() - 2);//last 2 digits represent local cellid
                    //System.out.println("enBhex" + eNBHex);

                    try {
                        eNB = HexToDec(eNBHex);
                    } catch (Exception e) {
                        e.printStackTrace();
                        //System.out.println("exception here  " + e.toString());
                    }
                }


            }
        }
        return eNB;
    }
/*
    private static String getOutput(Context context, String methodName, int slotId) {
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Class<?> telephonyClass;
        String reflectionMethod = null;
        String output = null;
        try {
            telephonyClass = Class.forName(telephony.getClass().getName());
            for (Method method : telephonyClass.getMethods()) {
                String name = method.getName();
                System.out.println("sim method name "+name);
                if (name.contains(methodName)) {
                    Class<?>[] params = method.getParameterTypes();
                    if (params.length == 1 && params[0].getName().equals("int")) {
                        reflectionMethod = name;
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (reflectionMethod != null) {
            try {
                output = getOpByReflection(telephony, reflectionMethod, slotId, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return output;
    }
*/

/*
    private static String getOpByReflection(TelephonyManager telephony, String predictedMethodName, int slotID, boolean isPrivate) {

        //Log.i("Reflection", "Method: " + predictedMethodName+" "+slotID);
        String result = null;

        try {

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimID;
            if (slotID != -1) {
                if (isPrivate) {
                    getSimID = telephonyClass.getDeclaredMethod(predictedMethodName, parameter);
                } else {
                    getSimID = telephonyClass.getMethod(predictedMethodName, parameter);
                }
            } else {
                if (isPrivate) {
                    getSimID = telephonyClass.getDeclaredMethod(predictedMethodName);
                } else {
                    getSimID = telephonyClass.getMethod(predictedMethodName);
                }
            }

            Object ob_phone;
            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            if (getSimID != null) {
                if (slotID != -1) {
                    ob_phone = getSimID.invoke(telephony, obParameter);
                } else {
                    ob_phone = getSimID.invoke(telephony);
                }

                if (ob_phone != null) {
                    result = ob_phone.toString();

                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
        Log.i("Reflection", "Result: " + result);
        return result;
    }
*/


}