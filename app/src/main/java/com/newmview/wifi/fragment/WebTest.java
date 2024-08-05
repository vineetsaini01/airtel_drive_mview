package com.newmview.wifi.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.newmview.wifi.CommonFunctions;
import com.mview.airtel.R;
import com.newmview.wifi.activity.MainActivity;
import com.newmview.wifi.bean.Pinger;
import com.newmview.wifi.helper.AllInOneAsyncTaskForNetwork;
import com.newmview.wifi.helper.Interfaces;
import com.newmview.wifi.helper.RequestResponse;
import com.newmview.wifi.listenService;
import com.newmview.wifi.mView_HealthStatus;
import com.newmview.wifi.other.CommonAlertDialog;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;
import com.newmview.wifi.other.WifiConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import static com.newmview.wifi.other.Utils.getNo_ofhops;


public class WebTest extends Fragment {
    Button startBtn;
    TextView result, textview;
    TextView url_result, speed_result;
    ImageView google, fb, twitter, wikipedia, yahoo;
    TextView google_res, fb_res, twitter_res, wiki_res, yahoo_res;
    TextView google_speed, fb_speed, twitter_speed, wiki_speed, yahoo_speed;
    LinearLayout google_layout, fb_layout, twitter_layout, wiki_layout, yahoo_layout;

    private WebView wv1;

    int currIndex;

    public static String DisplayResultString, DisplayUrl, DisplayResult;
    private boolean opened = false;
    int time = 0;
    private View view;
    private String lat, lon;
    private Bundle args;
    private String type;
    private boolean pagestarted;
    private long prev = 0;
    private TextView google_rt, twitter_rt, wiki_rt, yahoo_rt, fbrt;
    private String response_time;
    private String ping_time;
    private float totalPingTime;
    private boolean webtestfinished = false;
    private static final String TAG = "WebTest";

    private static final int TIMEOUT = 10000;
//    private Handler handler = new Handler();


//    private Runnable timeoutRunnable = new Runnable() {
//        @Override
//        public void run() {
//            Utils.showAlert(false, "Operation timed out. Please try again.", getActivity(), null);
//
//        }
//    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_web_test, container, false);

        init();
        if (listenService.gps != null) {
            lon = listenService.gps.getLongitude() + "";
            lat = listenService.gps.getLatitude() + "";
        } else {
            lat = "0.0";
            lon = "0.0";
        }
        wv1.setWebViewClient(new MyBrowser());
        wv1.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U; Android 2.0; en-us; Droid Build/ESD20) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17");


        if (mView_HealthStatus.connectionTypeIdentifier == 1)
            textview.setText(mView_HealthStatus.connectionType + "(" + WifiConfig.getConnectedWifiDetails().getSsidName() + ")");
            //  textview.setText(mView_HealthStatus.OperatorName + "  " + mView_HealthStatus.connectionType);
        else
            textview.setText(mView_HealthStatus.OperatorName + "  " + mView_HealthStatus.connectionType + " " + mView_HealthStatus.strCurrentNetworkState);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonFunctions.chkDataConnectivity(getActivity()).equalsIgnoreCase("offline")) {

                    showAlertDialog("WebSpeed Test", Constants.NO_INTERNET);
                } else if (!testStarted) {
                    startTest();
//                    handler.postDelayed(timeoutRunnable, TIMEOUT); // Start timeout

                } else {
                    testStarted = false;
                    startBtn.setText("START TEST");
                    startBtn.setEnabled(true);
                    wv1.stopLoading();
//                    handler.postDelayed(timeoutRunnable, TIMEOUT); // Start timeout

                }


            }
        });

        return view;
    }


//    public static long webPageLoadTime;
//    public static long latencyNew;
//    public static long dnsResolutionTime;
//    public static long dataUsedNew;
//    public static long no_of_redirection;
//    public static long no_of_hopsNew;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public JSONObject callping(String url) {


        JSONObject object=new JSONObject();
        String[] str = {""};
        String[] pckloss = {""};
        String[] finalVal = {""};
        long dnsend = 0L;
        String dnsip = null;
        boolean noofhops = false;
        long dnsstart = System.currentTimeMillis();
        float dataUsageBefore = fetchDataUsage();
        System.out.println(" data used before is"+dataUsageBefore);
        String value;
        try {

//            Process process = Runtime.getRuntime().exec("ping -c 1 " + url);
            Process process = Runtime.getRuntime().exec(new String[]{"sh", "-c", "ping -c 5 -t 64 " + url});
            mView_HealthStatus.webPageUrl=url;
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            Log.i("Pinger","Command "+"ping -c 1 " + url);
            int i;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            String op[] = new String[64];
            String delay[] = new String[8];
            //157.240.198.35
//            while ((i = reader.read(buffer)) > 0)
//            {
//                output.append(buffer, 0, i);
//                System.out.println(" output  of ping is"+output);
//
//
//            }
//            reader.close();
            String linenew;
            while((linenew = reader.readLine()) != null) {
                noofhops = noofhops;
                Log.i(TAG, "Ping line is " + linenew);
                output.append(linenew);
                if (linenew.startsWith("PING")) {
                    dnsend = System.currentTimeMillis();
                    dnsip = linenew.substring(linenew.indexOf("(") + 1, linenew.indexOf(")"));
                    Log.i(TAG, "dns ip is " + dnsip);
                    Log.i(TAG, "Dns end " + dnsend);
                }
            }

            reader.close();
            op = output.toString().split("\n");
            System.out.println(" op is"+op);
            float dataUsageAfter = fetchDataUsage();
            float data = dataUsageAfter - dataUsageBefore;
            String dataUsageDiffVal = Utils.getRoundedOffVal(data + "", 2);
            System.out.println("difference is "+dataUsageDiffVal);
            mView_HealthStatus.dataUsedNew = dataUsageDiffVal + " MB";
            long dnstime = dnsend - dnsstart;
            Log.i(TAG, "Dns time is " + dnstime);
            mView_HealthStatus.dnsResolutionTime = String.valueOf(dnstime);
            mView_HealthStatus.no_of_hopsNew= String.valueOf(getNo_ofhops(dnsip, 1, 15));
            if (output!=null) {

                String finaloutput = output.toString();
                String[] totalsplit = finaloutput.split(",");
                finaloutput = finaloutput.substring(finaloutput.indexOf("ping statistics ---") + 19, finaloutput.length());


                Log.i(TAG, "ping remaining " + finaloutput);
                String[] split = finaloutput.split(",");
                Log.i(TAG, "Remaining for ping for packet loss " + finaloutput);

                if (split[2].length() > 0) {
                    split[2] = split[2].trim();
                    String packetlosss = split[2].substring(0, split[2].indexOf(" "));
                    Log.i(TAG, "packet loss is " + packetlosss);
                    mView_HealthStatus.packetLossNew= packetlosss;
                    System.out.println(" Packet Loss is"+mView_HealthStatus.packetLossNew);
                    object.put("packetLoss", packetlosss);
                }

                object.put("webPageUrl", url);
                if (split[3].length() > 0) {
                    split[3] = split[3].trim();
                    String time = split[3];
                    Log.i(TAG, "Webpage load tie is " + time);

                    object.put("webPageLoadTime", time.substring(time.indexOf(" "), time.indexOf("ms")));
                    mView_HealthStatus.webPageLoadTime=time.substring(time.indexOf(" "), time.indexOf("ms"));
                }
/*if (split[5].length()>0)
{*/
                String rttstring = split[3].substring(split[3].indexOf("="), split[3].length());
                rttstring.trim();
                String[] timesplit = rttstring.split("/");
                mView_HealthStatus.latencyNew=timesplit[1];
                object.put("latency", timesplit[1]);
//}

                //String dnstime = totalsplit[1];
                //System.out.println(" dns time is"+dnstime);
//object.put("dnsResolutionTime",dnstime.substring(dnstime.indexOf("time="),dnstime.indexOf(" ")));
                object.put("dnsResolutionTime", " ");

                object.put("dataUsed", " ");


            }
            else
            {
                object.put("dnsResolutionTime", " ");
                object.put("latency", " ");
                object.put("webPageLoadTime", " ");
                object.put("webPageUrl", url);

                object.put("dataUsed", " ");
            }
                   /* [PING 203.122.58.233 (203.122.58.233) 56(84)
                    bytes of data., , --- 203.122.58.233 ping statistics ---, 1 packets transmitted, 0 received, 100% packet loss, time 0ms]
*/                    int index2 = output.indexOf("% packet loss");
            Log.i("Pinger ","Response "+ Arrays.toString(op));


         /*   if(op.length>4) {
                Log.i("Pinger", "Ping res: " + Arrays.toString(op) + " ping is" + str[0]);
                if (op[1] != null) {
                    delay = op[1].split("time=");
                }

                if (op[4] != null) {
                    String[] loss = op[4].split(",");
                    if (loss[2] != null) {
                        pckloss[0] = loss[2];
                    }
                }

                // String pck_loss=op[index2];


                if (delay[1] != null) {
                    str[0] = delay[1];
                }
            }
            finalVal[0] ="/"+str[0] +"/"+pckloss[0];*/
//            pingResult.onPingResultObtained(finalVal[0]);



            System.out.println("final string "+ finalVal[0]);

        } catch (IOException e) {
            // body.append("Error\n");
            e.printStackTrace();
            System.out.println("exception of ping is "+e.toString());
        }
        catch (ArrayIndexOutOfBoundsException ae)
        {
            System.out.println("exception is "+ae.toString());
            ae.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)



    public float fetchDataUsage() {
        float total;
        //long currentMobileTxBytes = TrafficStats.getMobileTxBytes();
        long currentMobileRxBytes = TrafficStats.getMobileRxBytes();
        //   long totalTxBytes = TrafficStats.getTotalTxBytes();
        long totalRxBytes = TrafficStats.getTotalRxBytes();

        //  float l = currentMobileTxBytes + currentMobileRxBytes;
        float mobileDataInMB = (currentMobileRxBytes) / (1024 * 1024);
        System.out.println("mobile data "+currentMobileRxBytes +"");
        float wifiDataInMB = ((totalRxBytes)/(1024*1024)) - mobileDataInMB;
        Log.i(TAG,"Mobile data usage "+mobileDataInMB +" "+" Wifi Data Usage "+wifiDataInMB);
        if(wifiDataInMB < 0 )
            wifiDataInMB = 0;

        if(mobileDataInMB<0)
            mobileDataInMB=0;
        String apnName= Utils.getApnType(getContext());
        if(Utils.checkifavailable(apnName))
        {
            if(apnName.equalsIgnoreCase("Wifi"))
                return wifiDataInMB;
            else
                return mobileDataInMB;
        }


        return 0;
    }
    private void startTest() {
        Utils.appendLog("ELOG_WEBTEST_UI: Going to start web test");

        testStarted = true;
        startBtn.setText("IN PROGRESS");
        Utils.showAlert(false, Constants.TEST_START_MSG, getActivity(), null);
        startBtn.setEnabled(false);
        setValuesToInvisible();

        running = 0;
        currIndex = 0;
        mView_HealthStatus.mySpeedTest.initWebtest();
        System.out.println("currentindex initially" + currIndex);
        final String url = mView_HealthStatus.mySpeedTest.webtest.websiteArr.get(currIndex).url;

        wv1.getSettings().setLoadsImagesAutomatically(true);
        wv1.getSettings().setJavaScriptEnabled(true);
        wv1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        previousBytes = TrafficStats.getUidRxBytes(android.os.Process.myUid());
        totalBytes = 0;
        Log.e("mView", "Starting bytes ==>   " + previousBytes);
        timeStarted = System.currentTimeMillis();
        final String url1 = url.replace("http://", "");
        System.out.println("webtest time 1 " + Utils.getDateTime());
        DisplayUrl = url1;

        Utils.appendLog("ELOG_WEBTEST_UI: Going to ping url: "+url1);
        Utils.ping1(url1, new Interfaces.PingResult() {

            @Override
            public void onPingResultObtained(final String time) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

//                        handler.removeCallbacks(timeoutRunnable); // Remove timeout
                        prev=System.currentTimeMillis();
                        wv1.loadUrl(url);
                        if(time!=null) {
                            try {
                                String seg[] = time.split("/");
                                if(seg[1]!=null) {
                                    ping_time = seg[0];
                                    System.out.println("ping time is "+ping_time);
                                }
                            }
                            catch (ArrayIndexOutOfBoundsException ae)
                            {
                                ae.printStackTrace();
                            }
                        }
                    }
                });

            }

            @Override
            public void parsePingResult(Pinger response) {

            }
        }, null);
        Handler handler = new Handler();
        handler.postDelayed(

                new Runnable() {
                    @Override
                    public void run() {
    /*wv1.loadUrl(url);*/

                        System.out.println("webtest time 2 " + Utils.getDateTime());

                    }


                }, 1000);
        System.out.println("url to be loaded on btn clck" + url);
    }

    private void showAlertDialog(String title, String message) {
        final AlertDialog.Builder alertdialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        alertdialog.setTitle(title);
        alertdialog.setMessage(message);
        alertdialog.setCancelable(true);
        alertdialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertdialog.show();
    }

    private void setValuesToInvisible() {

        google_speed.setVisibility(View.INVISIBLE);
        fb_speed.setVisibility(View.INVISIBLE);
        twitter_speed.setVisibility(View.INVISIBLE);
        wiki_speed.setVisibility(View.INVISIBLE);
        yahoo_speed.setVisibility(View.INVISIBLE);
        google_rt.setVisibility(View.INVISIBLE);
        fbrt.setVisibility(View.INVISIBLE);
        twitter_rt.setVisibility(View.INVISIBLE);
        wiki_rt.setVisibility(View.INVISIBLE);
        yahoo_rt.setVisibility(View.INVISIBLE);
    }


    private void init() {

        textview = (TextView) view.findViewById(R.id.textview);
        startBtn = (Button) view.findViewById(R.id.startBtn);
        google = (ImageView) view.findViewById(R.id.googlelimg);
        fb = (ImageView) view.findViewById(R.id.fbimg);
        twitter = (ImageView) view.findViewById(R.id.twitter);
        wikipedia = (ImageView) view.findViewById(R.id.wikiimg);
        yahoo = (ImageView) view.findViewById(R.id.yahooimg);

        google_res = (TextView) view.findViewById(R.id.googleurl);
        fb_res = (TextView) view.findViewById(R.id.fburl);
        twitter_res = (TextView) view.findViewById(R.id.twitterurl);
        yahoo_res = (TextView) view.findViewById(R.id.yahoourl);
        wiki_res = (TextView) view.findViewById(R.id.wikiurl);
        google_layout = (LinearLayout) view.findViewById(R.id.googlelayout);
        fb_layout = (LinearLayout) view.findViewById(R.id.fblayout);
        twitter_layout = (LinearLayout) view.findViewById(R.id.twitterlayout);
        yahoo_layout = (LinearLayout) view.findViewById(R.id.yahoolayout);
        wiki_layout = (LinearLayout) view.findViewById(R.id.wikilayout);
        google_speed = (TextView) view.findViewById(R.id.googlespeed);
        fb_speed = (TextView) view.findViewById(R.id.fbspeed);
        twitter_speed = (TextView) view.findViewById(R.id.twitterspeed);
        yahoo_speed = (TextView) view.findViewById(R.id.yahoospeed);
        wiki_speed = (TextView) view.findViewById(R.id.wikispeed);
        google_rt = (TextView) view.findViewById(R.id.googlert);
        fbrt = (TextView) view.findViewById(R.id.fbrt);
        twitter_rt = (TextView) view.findViewById(R.id.twitterrt);
        yahoo_rt = (TextView) view.findViewById(R.id.yahoort);
        wiki_rt = (TextView) view.findViewById(R.id.wikirt);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_new);
        args = getArguments();
        if (args != null)
            type = args.getString("type");


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        wv1 = (WebView) view.findViewById(R.id.webView);

    }

    boolean testStarted = false;
    /*@Override
    public void onBackPressed(){
        Intent returnIntent = new Intent();
        //returnIntent.putExtra("result",result);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();

    }*/
    private int running = 0; // Could be public if you want a timer to check.


    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //     Toast.makeText(getActivity(), "overriding method called", Toast.LENGTH_SHORT).show();

            running++;
            System.out.println("url to be loaded in should override" + url);
            view.loadUrl(url);
            return true;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onPageFinished(WebView view, String url) {
            //     Toast.makeText(getActivity(), "page finished method called", Toast.LENGTH_SHORT).show();
            if (--running == 0) { // just "running--;" if you add a timer.
                try {
                    if (pagestarted) {
                        pagestarted = false;

                        timeFinished = System.currentTimeMillis();
                        long timeTaken = timeFinished - timeStarted;
                        int a = android.os.Process.myUid();

                        final long currentBytes = TrafficStats.getUidRxBytes(a);
                        totalBytes = currentBytes - previousBytes;
                        //long seconds = timeTaken/1000;
                        Log.e("mView", "Page Finished ==>   " + totalBytes + " " + timeTaken + " " + url + "currnt index " + currIndex);
                        Log.e("mView", "Page Finished ==>   " + totalBytes + " " + timeTaken + " " + url + " current " + currentBytes + "  previous " + previousBytes);


                        mView_HealthStatus.mySpeedTest.webtest.websiteArr.get(currIndex).timeTakenInMS = timeTaken;
                        mView_HealthStatus.mySpeedTest.webtest.websiteArr.get(currIndex).bytes = totalBytes;


                        float f1 = (float) totalBytes;
                        float f2 = f1 / 1000.0f;
                        //float f2=f1;
                        String s1 = String.format("%.2f", f2);
                        float t1 = (float) timeTaken;
                        float t2 = t1 / 1000.0f;
                        float speed = f2 / t2;
                      /*  float speed1=(f2*8)/t2;
                        float speed=speed1/1024;*/
                        String speed_value = String.format("%.2f", speed);
                        String s2 = String.format("%.2f", t2);
                        System.out.println("s1 " + s1 + "  " + "s2" + " " + s2);
                        DisplayResultString = speed_value + "kbps";

                        //   DisplayResultString = s1 + "kb " + s2 + "sec";
//                Toast.makeText(getActivity(), "index"+currIndex, Toast.LENGTH_SHORT).show();


                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                running = 0;
                                if (testStarted == false)

                                    return;
                                previousBytes = TrafficStats.getUidRxBytes(android.os.Process.myUid());
                                totalBytes = 0;

                                timeStarted = System.currentTimeMillis();
                                currIndex++;

                                System.out.println("current index in on page finished  on inc  " + currIndex + " " + "and in run block");

                                if (currIndex < mView_HealthStatus.mySpeedTest.webtest.websiteArr.size()) {

                                    prev = 0;
                                    final String url = mView_HealthStatus.mySpeedTest.webtest.websiteArr.get(currIndex).url;
                                    final String url1 = url.replace("http://", "");
                                    int temp = currIndex + 1;
                                    String ind = temp + "";
                                    //DisplayResultString += /*ind + ". " + */"<br>"+url1+"</br>";
                                    DisplayUrl = url1;
                                    System.out.println("url to be loaded in onpagefinished" + url);
                                    System.out.println("webtest time 4 before loading " + Utils.getDateTime() + "index " + currIndex + "in ms " + System.currentTimeMillis()
                                            + "bytes " + TrafficStats.getUidRxBytes(android.os.Process.myUid()));
                                    prev = System.currentTimeMillis();

                                    Utils.ping1(url1, new Interfaces.PingResult() {
                                        @Override
                                        public void onPingResultObtained(final String time) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    prev=System.currentTimeMillis();
                                                    wv1.loadUrl(url);
                                                    if(time!=null) {
                                                        try {
                                                            String seg[] = time.split("/");
                                                            if(seg[1]!=null) {
                                                                ping_time = seg[0];
                                                                System.out.println("ping time is "+ping_time);
                                                            }
                                                        }
                                                        catch (ArrayIndexOutOfBoundsException ae)
                                                        {
                                                            ae.printStackTrace();
                                                        }
                                                    }
                                                }
                                            });

                                        }

                                        @Override
                                        public void parsePingResult(Pinger response) {

                                        }
                                    }, null);
                                    System.out.println("ping is " + ping_time);


                                    /*wv1.loadUrl(url);*/
                                } else {
                                    testStarted = false;
                                    startBtn.setText("START TEST");
                                    //  if (MainActivity.REPORT_FLAG) {
                                    if (type == null) {
                                        type = "";
                                    }
                                    String result = null;
                                    float avgPing = totalPingTime / 5;
                                    if (avgPing < 50) {
                                        result = "Very Good";
                                    } else if (avgPing >= 50 && avgPing < 100) {
                                        result = "Good";
                                    } else if (avgPing >= 100 && avgPing < 150) {
                                        result = "Average";
                                    } else {
                                        result = "Bad";
                                    }
                                    System.out.println("result is " + result + " avg ping " + avgPing);
                                    CommonAlertDialog.
                                            showResultAlertDialog("webtest", getActivity(), avgPing, result, R.layout.webresultdialog, type, lat, lon, "Data Bandwidth issue");
                                    MainActivity.testflag = true;

                                    startBtn.setEnabled(true);
                                    webtestfinished = true;
                                    try {
                                        JSONArray arr = new JSONArray();
                                        for (int i = 0; i < mView_HealthStatus.mySpeedTest.webtest.websiteArr.size(); i++) {
                                            System.out.println(" webtest is"+mView_HealthStatus.mySpeedTest.webtest.websiteArr.get(i).url);
                                            JSONObject webJson = new JSONObject();

                                            webJson.put("url", mView_HealthStatus.mySpeedTest.webtest.websiteArr.get(i).url);
                                            webJson.put("duration_ms", mView_HealthStatus.mySpeedTest.webtest.websiteArr.get(i).timeTakenInMS);
                                            webJson.put("size_bytes", mView_HealthStatus.mySpeedTest.webtest.websiteArr.get(i).bytes);

                                            if(mView_HealthStatus.mySpeedTest.webtest.websiteArr.get(i).url=="http://www.facebook.com") {
                                                webJson.put("webPageurl", mView_HealthStatus.webPageUrl);
                                                webJson.put("webPageLoadTime", mView_HealthStatus.webPageLoadTime);
                                                webJson.put("packetLoss", mView_HealthStatus.packetLossNew);
                                                webJson.put("latency", mView_HealthStatus.latencyNew);
                                                webJson.put("dataUsed", "0.0MB");
                                                webJson.put("dnsResolutionTime",mView_HealthStatus.dnsResolutionTime);
                                                webJson.put("no_of_redirection", "5");
                                                webJson.put("no_of_hops", mView_HealthStatus.no_of_hopsNew);

                                            }
                                            arr.put(webJson);
                                        }
                                        JSONObject urlsObj = new JSONObject();
                                        urlsObj.put("urls_details", arr);
                                        JSONArray array = new JSONArray();
                                        array.put(urlsObj);
                                        RequestResponse.sendNewWebTestEvent(array, AllInOneAsyncTaskForNetwork.AsyncTaskPurpose.NEW_WEB_TEST_EVT, "web_test");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }



                                       /* WebService.API_sendWebTest("view");
                                        new WebService.Async_SendNeighboringCellsInfo().execute();*/

                                }
                            }
                        }, 0);


                        switch (currIndex) {
                            case 0:
                                google_speed.setVisibility(View.VISIBLE);
                                google_rt.setVisibility(View.VISIBLE);
                                //google_res.setText(DisplayUrl);
                                google_speed.setText(DisplayResultString);
                                if (ping_time != null) {
                                    Utils.appendLog("ELOG_WEBTEST_UI: google ping time is: "+ping_time);
                                    google_rt.setText(ping_time);
                                    if (!ping_time.equalsIgnoreCase("NaN")) {
                                        totalPingTime = Float.parseFloat(ping_time.replace("ms", ""));
                                    }
                                }

                                break;
                            case 1:
                                fb_speed.setVisibility(View.VISIBLE);
                                fbrt.setVisibility(View.VISIBLE);
                                //fb_res.setText(DisplayUrl);
                                fb_speed.setText(DisplayResultString);
                                if (ping_time != null) {
                                    Utils.appendLog("ELOG_WEBTEST_UI: facebook ping time is: "+ping_time);
                                    fbrt.setText(ping_time);
                                    if (!ping_time.equalsIgnoreCase("NaN")) {
                                        totalPingTime = Float.parseFloat(ping_time.replace("ms", "")) + totalPingTime;
                                    }
                                }

                                break;
                            case 2:
                                twitter_speed.setVisibility(View.VISIBLE);
                                twitter_rt.setVisibility(View.VISIBLE);
                                //twitter_res.setText(DisplayUrl);
                                twitter_speed.setText(DisplayResultString);
                                if (ping_time != null) {
                                    Utils.appendLog("ELOG_WEBTEST_UI: Gmail ping time is: "+ping_time);
                                    twitter_rt.setText(ping_time);
                                    if (!ping_time.equalsIgnoreCase("NaN")) {
                                        totalPingTime = Float.parseFloat(ping_time.replace("ms", "")) + totalPingTime;
                                    }
                                }
                                break;
                            case 3:
                                wiki_speed.setVisibility(View.VISIBLE);
                                wiki_rt.setVisibility(View.VISIBLE);
                                //wiki_res.setText(DisplayUrl);
                                wiki_speed.setText(DisplayResultString);
                                if (ping_time != null) {
                                    wiki_rt.setText(ping_time);
                                    Utils.appendLog("ELOG_WEBTEST_UI: wikipedia ping time is: "+ping_time);
                                    if (!ping_time.equalsIgnoreCase("NaN")) {
                                        totalPingTime = Float.parseFloat(ping_time.replace("ms", "")) + totalPingTime;
                                    }

                                }
//                                JSONObject pingresult =callping("www.facebook.com");
//                                System.out.println(" Ping result is"+pingresult);
//
//                                try {
//                                    new DialogClassWebTest("Ping").webReport(getContext());
//                                } catch (Exception e) {
//                                    System.out.println(" Exception in WebTest"+e.getMessage());
//                                    e.printStackTrace();
//                                }
                                break;
                            case 4:
                                yahoo_speed.setVisibility(View.VISIBLE);
                                yahoo_rt.setVisibility(View.VISIBLE);
                                // yahoo_res.setText(DisplayUrl);
                                yahoo_speed.setText(DisplayResultString);
                                if (ping_time != null) {
                                    Utils.appendLog("ELOG_WEBTEST_UI: yahoo ping time is: "+ping_time);
                                    yahoo_rt.setText(ping_time);
                                    if (!ping_time.equalsIgnoreCase("NaN")) {
                                        totalPingTime = Float.parseFloat(ping_time.replace("ms", "")) + totalPingTime;
                                    }
                                }


                                break;

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {


            long ms = System.currentTimeMillis() - prev;
            response_time = String.valueOf(ms);
            long differencebytes = TrafficStats.getUidRxBytes(android.os.Process.myUid()) - previousBytes;

            System.out.println("webtest time 3 when page started  " + currIndex + "  " + Utils.getDateTime() + " difference is " + ms + " bytes " + differencebytes);

            running = Math.max(running, 1);
            System.out.println("current index in onpage started is " + currIndex);
            //        Toast.makeText(getActivity(), "page started method called", Toast.LENGTH_SHORT).show();
            pagestarted = true;
            System.out.println();
        }
    }

    long timeStarted;
    long timeFinished;
    long previousBytes;
    long totalBytes;

    private class MyBrowser1 extends WebChromeClient {


        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);

            if (newProgress == 100) {
                int a = android.os.Process.myUid();
                long currentBytes = TrafficStats.getUidRxBytes(a);
                totalBytes = currentBytes - previousBytes;
                timeFinished = System.currentTimeMillis();
                long timeTaken = timeFinished - timeStarted;
                Log.e("mView", "chrome Page Finished ==>   " + totalBytes + " " + timeTaken + "ms");
                //Log.e("mView", "Current Bytes ==>   " + totalBytes
                //   + "   New Progress ==>   " + newProgress);
            }
        }
    }

	  /* @Override
	   public boolean onCreateOptionsMenu(Menu menu) {
	      // Inflate the menu; this adds items to the action bar if it is present.
	      getMenuInflater().inflate(R.menu.menu_main, menu);
	      return true;
	   }

	   @Override
	   public boolean onOptionsItemSelected(MenuItem item) {
	      // Handle action bar item clicks here. The action bar will
	      // automatically handle clicks on the Home/Up button, so long
	      // as you specify a parent activity in AndroidManifest.xml.

	      int id = item.getItemId();

	      //noinspection SimplifiableIfStatement
	      if (id == R.id.action_settings) {
	         return true;
	      }
	      return super.onOptionsItemSelected(item);
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
            getActivity().onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}