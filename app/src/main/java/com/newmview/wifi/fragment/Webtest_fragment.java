package com.newmview.wifi.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
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

/**
 * Created by functionapps on 10/30/2018.
 */

public class Webtest_fragment extends Fragment {


    Button startBtn;
    TextView result,textview;
    TextView url_result,speed_result;
    ImageView google,fb,twitter,wikipedia,yahoo;
    TextView google_res,fb_res,twitter_res,wiki_res,yahoo_res;
    TextView google_speed,fb_speed,twitter_speed,wiki_speed,yahoo_speed;
    LinearLayout google_layout,fb_layout,twitter_layout,wiki_layout,yahoo_layout;

    private WebView wv1;

    int currIndex;

    public static String DisplayResultString,DisplayUrl,DisplayResult;
    private boolean opened=false;
    int time=0;
    private View view;
    private String lat,lon;
    private Bundle args;
    private String type;
    private boolean pagestarted;
    private long prev=0;
    private TextView google_rt,twitter_rt,wiki_rt,yahoo_rt,fbrt;
    private String response_time;
    private String ping_time;
    private float totalPingTime;
    private boolean webtestfinished=false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.webtest_new,container,false);

        init();
        if(listenService.gps!=null) {
            lon = listenService.gps.getLongitude() + "";
            lat = listenService.gps.getLatitude() + "";
        }
        else {
            lat="0.0";
            lon="0.0";
        }
        wv1.setWebViewClient(new MyBrowser());
        wv1.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U; Android 2.0; en-us; Droid Build/ESD20) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17");


        if( mView_HealthStatus.connectionTypeIdentifier == 1)
            textview.setText(mView_HealthStatus.connectionType+"("+ WifiConfig.getConnectedWifiDetails().getSsidName() + ")" );
          //  textview.setText(mView_HealthStatus.OperatorName + "  " + mView_HealthStatus.connectionType);
        else
            textview.setText(mView_HealthStatus.OperatorName + "  " + mView_HealthStatus.connectionType + " " + mView_HealthStatus.strCurrentNetworkState);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CommonFunctions.chkDataConnectivity(getActivity()).equalsIgnoreCase("offline"))
                {

                    showAlertDialog("WebSpeed Test", Constants.NO_INTERNET);
                }
                else

                if(!testStarted)
                {
                    startTest();
                }else {
                    testStarted = false;
                    startBtn.setText("START TEST");
                    startBtn.setEnabled(true);
                    wv1.stopLoading();
                }



            }
        });

        return view;
    }

    private void startTest() {

        testStarted = true;
        startBtn.setText("IN PROGRESS");
        Utils.showAlert(false, Constants.TEST_START_MSG,getActivity(), null);
        startBtn.setEnabled(false);
        setValuesToInvisible();

        running = 0;
        currIndex = 0;
        mView_HealthStatus.mySpeedTest.initWebtest();
        System.out.println("currentindex initially"+currIndex);
        final String url = mView_HealthStatus.mySpeedTest.webtest.websiteArr.get(currIndex).url;

        wv1.getSettings().setLoadsImagesAutomatically(true);
        wv1.getSettings().setJavaScriptEnabled(true);
        wv1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        previousBytes = TrafficStats.getUidRxBytes(android.os.Process.myUid());
        totalBytes = 0;
        Log.e("mView", "Starting bytes ==>   " + previousBytes);
        timeStarted = System.currentTimeMillis();
        final String url1 = url.replace("http://", "");
        System.out.println("webtest time 1 "+ Utils.getDateTime());
        DisplayUrl=url1;
        Utils.ping1(url1, new Interfaces.PingResult() {
            @Override
            public void onPingResultObtained(final String time) {
                if(getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (time != null) {
                                try {


                                    String seg[] = time.split("/");
                                    if (seg[1] != null)
                                    {

                                        ping_time = seg[0];
                                    }
                                } catch (ArrayIndexOutOfBoundsException ae) {
                                    ae.printStackTrace();
                                }
                            }
                            System.out.println("ping time 2 is " + ping_time);
                            wv1.loadUrl(url);
                        }
                    });
                }

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

                        System.out.println("webtest time 2 "+ Utils.getDateTime());

                    }


                }, 1000);
        System.out.println("url to be loaded on btn clck"+url);
    }

    private void showAlertDialog(String title, String message) {
        final AlertDialog.Builder alertdialog=new AlertDialog.Builder(getActivity(),R.style.AlertDialogTheme);
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

        textview=(TextView)view.findViewById(R.id.textview);
        startBtn=(Button)view.findViewById(R.id.startBtn);
        google=(ImageView)view.findViewById(R.id.googlelimg);
        fb=(ImageView)view.findViewById(R.id.fbimg);
        twitter=(ImageView)view.findViewById(R.id.twitter);
        wikipedia=(ImageView)view.findViewById(R.id.wikiimg);
        yahoo=(ImageView)view.findViewById(R.id.yahooimg);

        google_res=(TextView)view.findViewById(R.id.googleurl);
        fb_res=(TextView)view.findViewById(R.id.fburl);
        twitter_res=(TextView)view.findViewById(R.id.twitterurl);
        yahoo_res=(TextView)view.findViewById(R.id.yahoourl);
        wiki_res=(TextView)view.findViewById(R.id.wikiurl);
        google_layout=(LinearLayout) view.findViewById(R.id.googlelayout);
        fb_layout=(LinearLayout) view.findViewById(R.id.fblayout);
        twitter_layout=(LinearLayout)view.findViewById(R.id.twitterlayout);
        yahoo_layout=(LinearLayout)view.findViewById(R.id.yahoolayout);
        wiki_layout=(LinearLayout) view.findViewById(R.id.wikilayout);
        google_speed=(TextView)view.findViewById(R.id.googlespeed);
        fb_speed=(TextView)view.findViewById(R.id.fbspeed);
        twitter_speed=(TextView)view.findViewById(R.id.twitterspeed);
        yahoo_speed=(TextView)view.findViewById(R.id.yahoospeed);
        wiki_speed=(TextView)view.findViewById(R.id.wikispeed);
        google_rt=(TextView)view.findViewById(R.id.googlert);
        fbrt=(TextView)view.findViewById(R.id.fbrt);
        twitter_rt=(TextView)view.findViewById(R.id.twitterrt);
        yahoo_rt=(TextView)view.findViewById(R.id.yahoort);
        wiki_rt=(TextView)view.findViewById(R.id.wikirt);
        if (getActivity() != null) {
            Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(R.drawable.back_new);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() != null) {
                        getActivity().onBackPressed();
                    }

                }
            });
        }
        args=getArguments();
        if(args!=null)
            type=args.getString("type");



        wv1=(WebView)view.findViewById(R.id.webView);

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
            System.out.println("url to be loaded in should override"+url);
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            //     Toast.makeText(getActivity(), "page finished method called", Toast.LENGTH_SHORT).show();
            if(--running == 0) { // just "running--;" if you add a timer.
                try {
                    if (pagestarted)
                    {
                        pagestarted=false;

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
                                                showResultAlertDialog("webtest", getActivity(), avgPing, result,
                                                        R.layout.webresultdialog, type, lat, lon, "Data Bandwidth issue");
                                        MainActivity.testflag = true;

                                        startBtn.setEnabled(true);
                                        webtestfinished = true;
                                    try {
                                    JSONArray arr = new JSONArray();
                                    for (int i = 0; i < mView_HealthStatus.mySpeedTest.webtest.websiteArr.size(); i++) {
                                        JSONObject webJson = new JSONObject();

                                            webJson.put("url", mView_HealthStatus.mySpeedTest.webtest.websiteArr.get(i).url);
                                            webJson.put("duration_ms", mView_HealthStatus.mySpeedTest.webtest.websiteArr.get(i).timeTakenInMS);
                                            webJson.put("size_bytes", mView_HealthStatus.mySpeedTest.webtest.websiteArr.get(i).bytes);
                                            arr.put(webJson);


                                    }
                                    JSONObject urlsObj=new JSONObject();
                                    urlsObj.put("urls_details",arr);

                                    JSONArray array=new JSONArray();
                                    array.put(urlsObj);
                                    RequestResponse.sendEvent(array,
                                            AllInOneAsyncTaskForNetwork.AsyncTaskPurpose.
                                                    WEB_TEST_EVENT
                                            , "web_test");
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
                                if(ping_time!=null) {
                                    google_rt.setText(ping_time);
                                    if(!ping_time.equalsIgnoreCase("NaN"))
                                    {
                                        totalPingTime=Float.parseFloat(ping_time.replace("ms",""));
                                    }
                                }

                                break;
                            case 1:
                                fb_speed.setVisibility(View.VISIBLE);
                                fbrt.setVisibility(View.VISIBLE);
                                //fb_res.setText(DisplayUrl);
                                fb_speed.setText(DisplayResultString);
                                if(ping_time!=null) {
                                    fbrt.setText(ping_time);
                                    if(!ping_time.equalsIgnoreCase("NaN"))
                                    {
                                        totalPingTime=Float.parseFloat(ping_time.replace("ms",""))+totalPingTime;
                                    }
                                }
                                break;
                            case 2:
                                twitter_speed.setVisibility(View.VISIBLE);
                                twitter_rt.setVisibility(View.VISIBLE);
                                //twitter_res.setText(DisplayUrl);
                                twitter_speed.setText(DisplayResultString);
                                if(ping_time!=null) {
                                    twitter_rt.setText(ping_time);
                                    if(!ping_time.equalsIgnoreCase("NaN"))
                                    {
                                        totalPingTime=Float.parseFloat(ping_time.replace("ms",""))+totalPingTime;
                                    }
                                }
                                break;
                            case 3:
                                wiki_speed.setVisibility(View.VISIBLE);
                                wiki_rt.setVisibility(View.VISIBLE);
                                //wiki_res.setText(DisplayUrl);
                                wiki_speed.setText(DisplayResultString);
                                if(ping_time!=null) {
                                    wiki_rt.setText(ping_time);

                                    if(!ping_time.equalsIgnoreCase("NaN"))
                                    {
                                        totalPingTime=Float.parseFloat(ping_time.replace("ms",""))+totalPingTime;
                                    }

                                }
                                break;
                            case 4:
                                yahoo_speed.setVisibility(View.VISIBLE);
                                yahoo_rt.setVisibility(View.VISIBLE);
                                // yahoo_res.setText(DisplayUrl);
                                yahoo_speed.setText(DisplayResultString);
                                if(ping_time!=null) {
                                    yahoo_rt.setText(ping_time);
                                    if(!ping_time.equalsIgnoreCase("NaN"))
                                    {
                                        totalPingTime=Float.parseFloat(ping_time.replace("ms",""))+totalPingTime;
                                    }
                                }
                                break;

                        }
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }


        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon){



            long ms=System.currentTimeMillis()-prev;
            response_time=String.valueOf(ms);
            long differencebytes=TrafficStats.getUidRxBytes(android.os.Process.myUid()) - previousBytes;

            System.out.println("webtest time 3 when page started  "+currIndex  +"  " +Utils.getDateTime() +" difference is "+ms +" bytes "+ differencebytes);

            running = Math.max(running, 1);
            System.out.println("current index in onpage started is "+currIndex);
            //        Toast.makeText(getActivity(), "page started method called", Toast.LENGTH_SHORT).show();
            pagestarted=true;
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

            if( newProgress == 100)
            {
                int a = android.os.Process.myUid();
                long currentBytes = TrafficStats.getUidRxBytes(a);
                totalBytes = currentBytes - previousBytes;
                timeFinished = System.currentTimeMillis();
                long timeTaken =  timeFinished - timeStarted;
                Log.e("mView", "chrome Page Finished ==>   " + totalBytes + " " + timeTaken + "ms" );
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
