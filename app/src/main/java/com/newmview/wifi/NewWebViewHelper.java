package com.newmview.wifi;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.TrafficStats;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.bean.Pinger;
import com.newmview.wifi.other.Utils;

import org.json.JSONObject;

public class NewWebViewHelper {

    private static final String TAG = "WebViewHelper";
    private static final long COUNT_DOWN_TIMER =40000 ;
    private Pinger pingResponse;
    private Context context;
    private WebView webView;

    private float dataUsageBefore;
    private float dataUsageAfter;
    private float dataUsageDiff;
    private String dataUsageDiffVal;
    private int progress=0;
    private boolean timeOutOccured=false;
    private String eventObtained;
    private boolean resultObtained;
    private long time_between_stalls=0;
    String latency;
    private String packet_loss;
    private long timetofistframe;
    private long videostarttime=0;
    long startplay_time=0;
    private long totalPlaying_time=0;

    public interface JsonResultInterface
    {
        public void   sendJsonResult(JSONObject jsonResult);
    }


    JsonResultInterface sendResult;
    public  NewWebViewHelper(Context cont,String late , String loss) {
        Log.i(TAG,"webview helper class called" );

        webView = new WebView(cont);
        context=cont;
        packet_loss=loss;
        latency=late;
//        pingResponse=pingRespo;
        WebSettings settings = webView.getSettings();
        webView.getSettings().setJavaScriptEnabled(true);
        // Add a JavaScript interface to the WebView
        webView.addJavascriptInterface(new NewWebViewHelper.JavaScriptInterface(), "Android");
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLightTouchEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.setSoundEffectsEnabled(true);
        webView.requestFocus();
        webView.loadUrl("file:///android_asset/youtube_test.html");


        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        //     settings.setPluginState(WebSettings.PluginState.ON);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

//        webView.setPadding(20, 20, 20, 20);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.e(TAG, "WebView Console Message: " + consoleMessage.message());
                return super.onConsoleMessage(consoleMessage);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                Utils.appendLog("Rxd error in webview client "+errorCode +" desc "+description);
                Log.i(TAG,"Error with code "+errorCode +" desc "+description);
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                String title = webView.getTitle();
                // webView.
                Log.d(TAG, "Title: " + title);
            }



        });


        webView.setWebChromeClient(new WebChromeClient(){
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                request.grant(request.getResources());

            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Log.d(TAG, "Progress is : " + newProgress);
                progress=newProgress;
                // Utils.showLongToast(context,"Video Page loaded upto "+newProgress +"%");
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Log.i(TAG,"Message is "+message +" result is "+result +" for url "+url);
                return super.onJsAlert(view, url, message, result);

            }
        });
    }

    public void loadUrl(String url, JsonResultInterface sendResult) {
        dataUsageBefore= fetchDataUsage();
        final String mimeType = "text/html";
        final String encoding = "UTF-8";

        this.sendResult=sendResult;

        videostarttime=System.currentTimeMillis();
        //String html = getHTML();
        // webView.loadDataWithBaseURL(url, url, mimeType, encoding, "");
        setCoundownTimer(url);


    }

    private void setCoundownTimer( String url) {
        new CountDownTimer(COUNT_DOWN_TIMER, 1000) {

            public CountDownTimer init(){

                webView.loadUrl("file:///android_asset/youtube_test.html");
                return this;
            }
            public void onTick(long millisUntilFinished) {
                Log.i(TAG,"seconds remaining: " + millisUntilFinished / 1000);
                Log.i(TAG,"Event obtained on finishing "+eventObtained);
                if(Utils.checkifavailable(eventObtained)) {
                    if (eventObtained.equalsIgnoreCase("Playing"))
                    {
                        cancel();
                    }
                }

                // textView.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                //Sending Final Report even if we don't receive any state or specifically Playing state
                Log.i(TAG,"Event obtained on finishing "+eventObtained);
                if(Utils.checkifavailable(eventObtained))
                {
                    if(!eventObtained.equalsIgnoreCase("Playing"))
                    {
//                        Utils.onFailure(6,"Video Test timeout", f_result);
                        sendEventAfterTimeOut();

                        webView.stopLoading();
                    }
                }
                else
                {
                    webView.stopLoading();
                    sendEventAfterTimeOut();
                }


            }
        }.init().start();

    }


    private float fetchDataUsage() {
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
        String apnName= Utils.getApnType(MviewApplication.ctx);
        if(Utils.checkifavailable(apnName)){
            if(apnName.equalsIgnoreCase("Wifi"))
                return wifiDataInMB;
            else
                return mobileDataInMB;
        }


        return 0;
    }


    private void sendEventAfterTimeOut() {
        timeOutOccured=true;
        dataUsageAfter= fetchDataUsage();
        dataUsageDiff=dataUsageAfter-dataUsageBefore;
        dataUsageDiffVal= Utils.getRoundedOffVal(dataUsageDiff+"",2);
        // Utils.showToast(context,"Video Test Ended...");
        if(sendResult!=null)
        {
            Log.i(TAG,"going to send to interface with zero values");
            sendResult.sendJsonResult((addParamsForVideoKpi( 0, 0)));

        }

    }
    private  JSONObject addParamsForVideoKpi( int duration, long totalPlayingTime) {
        JSONObject object =new JSONObject();
        try {

            object.put("dataUsed",dataUsageDiffVal+"MB");

            object.put("latency",latency);
            object.put("packetLoss",packet_loss);

            object.put("timestamp", Utils.getDateTime());
            object.put("timeout",COUNT_DOWN_TIMER+" ms");
            object.put("totalBytes",dataUsageDiffVal+"MB");
            object.put("totalPlaybackTime",totalPlayingTime+" ms");
            object.put("videoLength",duration+" sec");
            if (duration == totalPlayingTime){
                object.put("result","success");

            }else {
                object.put("result","failed");

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG,"Video Test Json "+object.toString());
        return object;
    }





    // Create a JavaScript interface
    class JavaScriptInterface {
        private int bufferingCount;
        private long totalBufferingTime;
        private int duration;


        @JavascriptInterface
        public void onTimeOutOccured(boolean timeOut)

        {
            timeOutOccured=true;
        }
        @JavascriptInterface
        public void onPlayerStateChanged(String state)
        {
            Log.i(TAG,"State is "+state);
            if(Utils.checkifavailable(state))
            {
                if(state.equalsIgnoreCase("Playing"))
                    eventObtained=state;

            }
        }
        @JavascriptInterface
        public void onObtainResult(final int duration, final int bufferingCount,final long totalBufferingTime,long totalPlayingTime,long startplaytime) {
            Log.i(TAG,"Duration is "+duration +" Buffering count is "+bufferingCount +" total playing time "+totalPlayingTime+" start play time is "+startplaytime);


            this.bufferingCount=bufferingCount;
            this.totalBufferingTime=totalBufferingTime;
            startplay_time=startplaytime;
            this.duration=duration;
            resultObtained=true;

        }
        @JavascriptInterface
        public void sendPlayingTime(final long totalPlayingTime)
        {
            Log.i(TAG,"Total Play time is "+totalPlayingTime);
            Log.i(TAG,"resultObtained value is "+resultObtained);


            if(resultObtained) {
                dataUsageAfter = fetchDataUsage();
                dataUsageDiff = dataUsageAfter - dataUsageBefore;
                dataUsageDiffVal = Utils.getRoundedOffVal(dataUsageDiff + "", 2);


                if(sendResult!=null) {
                    Log.i(TAG, "going to send to interface with actual values");

                    sendResult.sendJsonResult((addParamsForVideoKpi( duration,  totalPlayingTime)));

                }

                resultObtained=false;
            }
        }



    }

}
