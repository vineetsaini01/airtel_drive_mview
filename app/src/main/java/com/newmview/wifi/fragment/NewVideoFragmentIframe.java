 package com.newmview.wifi.fragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


import com.gpsTracker.GPSTracker;
import com.mview.airtel.R;
import com.newmview.wifi.TinyDB;
import com.newmview.wifi.activity.MainActivity;
import com.newmview.wifi.bean.TestResults;
import com.newmview.wifi.mView_HealthStatus;
import com.newmview.wifi.network.NetworkUtil;
import com.newmview.wifi.other.Config;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class NewVideoFragmentIframe extends Fragment implements View.OnClickListener {

    //private MainSharedViewModel viewModelNew;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "aa";
    WebView web_view;
    private String mParam1;
    private String mParam2;
    private View view;
    private Button stoptest;
    private Button vdo_start_btn;
    private RelativeLayout btn_rl;
    private LinearLayout main_ll;
    private boolean timeOutOccured = false;
    private String eventObtained;
    private boolean resultObtained;
    TextView vid_dur;
    TextView bffering_no;
    TextView buff_time;
    TextView play_time;
    static ProgressDialog progressDialog;
    private Bundle bundle;
    private String type;
    private String action_type, mapId, source;
    private String orderId;
    private String orgName;
    private List<TestResults> surveyTestResults;
    public static boolean resultsSeen = false;
    private TinyDB tinyDB;
    private Handler timeoutHandler;
    private Runnable timeoutRunnable;
    private static final int TIMEOUT_DURATION = 10000;

    public NewVideoFragmentIframe() {
        // Required empty public constructor
    }

    public interface setDataListener {
        public void setDataToViews();
    }

    public static NewVideoFragmentIframe newInstance(String param1, String param2) {
        NewVideoFragmentIframe fragment = new NewVideoFragmentIframe();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, param1);
        bundle.putString(ARG_PARAM2, param2);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();
        if (bundle != null) {
            type = bundle.getString("type");

            //  color = bundle.getInt("color");
            mapId = bundle.getString("mapId");
            source = bundle.getString("source");
            action_type = bundle.getString("action_type");
            orderId = bundle.getString("orderId");
            orgName = bundle.getString("orgName");
        }
        //viewModelNew = new ViewModelProvider(requireActivity()).get(MainSharedViewModel.class);

        //  SharedPreferences preferences = getContext().getSharedPreferences("MyPreferences", MODE_PRIVATE);
         tinyDB = new TinyDB(getActivity());
        resultsSeen = tinyDB.getBoolean("resultsSeen", false);

        timeoutHandler = new Handler();
        timeoutRunnable = new Runnable() {
            @Override
            public void run() {
                // Handle timeout here
                    if (!resultsSeen){
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                            showTimeoutAlert();
                        }
                    }
            }
        };
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_new_video_iframe, container, false);
        web_view = view.findViewById(R.id.web_view);
        btn_rl = (RelativeLayout) view.findViewById(R.id.start_rl);
        main_ll = view.findViewById(R.id.main_ll);
        vdo_start_btn = (Button) view.findViewById(R.id.start);
        vdo_start_btn.setOnClickListener(this);
        vid_dur = (TextView) view.findViewById(R.id.vid_durNew);
        vdo_start_btn = (Button) view.findViewById(R.id.start);
        bffering_no = (TextView) view.findViewById(R.id.buffering_no);
        buff_time = (TextView) view.findViewById(R.id.buffer_time);
        play_time = (TextView) view.findViewById(R.id.playtimeNew);
        stoptest = (Button) view.findViewById(R.id.stoptest);
        stoptest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // web_view.stopLoading();
                startTimeout();
                web_view.onPause();

                if (!resultsSeen) {

                    progressDialog = showCustomProgressDialog(getActivity(), "Please wait for few  seconds for getting  the result", false);
                    startTimeout();
                }


            }
        });
/*
        stoptest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // web_view.stopLoading();
                web_view.onPause();
                System.out.println(" stop video time is"+Utils.getDateTime());
                progressDialog=showCustomProgressDialog(getActivity(), "Please wait for few  seconds for getting  the result", false);
            }
        });
*/
//        viewModelNew.getData().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(String value) {
//                // Call the method of the Activity
//                ((MainActivity) requireActivity()).sendPerformanceTestResultToAWApp(value);
//            }
//        });

        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start:
                showDialog(getResources().getString(R.string.videotest_title), Constants.start_vdo_test);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Reset resultsSeen to false when the user comes back to the page
        resultsSeen = false;
    }

    private void startTimeout() {
        timeoutHandler.postDelayed(timeoutRunnable, TIMEOUT_DURATION);
    }

    private void cancelTimeout() {
        timeoutHandler.removeCallbacks(timeoutRunnable);
    }

    private void showTimeoutAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Timeout")
                .setMessage("The video test has timed out. Please try again.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void showDialog(String title, String message) {
        AlertDialog.Builder updatealert = new AlertDialog.Builder(getActivity());
        updatealert.setTitle(title);
        updatealert.setMessage(message);
        updatealert.setPositiveButton(Html.fromHtml("<font color='#FF0000'>OK</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.appendLog("ELOG_RUN_VIDEO_UI: Start video test");

                btn_rl.setVisibility(View.GONE);
                main_ll.setVisibility(View.VISIBLE);


                web_view.getSettings().setJavaScriptEnabled(true);
                web_view.addJavascriptInterface(new JavaScriptInterface(buff_time, play_time, bffering_no, vid_dur,
                        type, action_type, mapId, orgName, source, orderId, getActivity(), progressDialog), "Android");
                web_view.getSettings().setMediaPlaybackRequiresUserGesture(false);
                web_view.getSettings().setPluginState(WebSettings.PluginState.ON);
                web_view.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
                web_view.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                web_view.getSettings().setDomStorageEnabled(true);
                web_view.getSettings().setLightTouchEnabled(true);
                web_view.getSettings().setGeolocationEnabled(true);
                web_view.setSoundEffectsEnabled(true);
                web_view.requestFocus();
                web_view.loadUrl("file:///android_asset/youtube_test.html");
                web_view.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        super.onReceivedError(view, errorCode, description, failingUrl);
                        System.out.println("Error with code " + errorCode + " desc " + description);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        String title = web_view.getTitle();
                        System.out.println("Title: " + title);
                    }


                });
                web_view.setWebChromeClient(new WebChromeClient() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onPermissionRequest(final PermissionRequest request) {
                        request.grant(request.getResources());
                    }

                    @Override
                    public void onReceivedTitle(WebView view, String title) {
                        super.onReceivedTitle(view, title);
                        Log.d(TAG, "Title: " + title);
                    }

                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        super.onProgressChanged(view, newProgress);
                        Log.d(TAG, "Progress is : " + newProgress);
                        //progress=newProgress;
                        // Utils.showLongToast(context,"Video Page loaded upto "+newProgress +"%");
                    }

                    @Override
                    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                        Log.i(TAG, "Message is " + message + " result is " + result + " for url " + url);
                        return super.onJsAlert(view, url, message, result);

                    }
                });

            }
        });
        updatealert.show();

    }


    public ProgressDialog showCustomProgressDialog(Context context, String message, boolean cancelable) {
        ProgressDialog pDialog = new ProgressDialog(context, R.style.roundedCornersDialog);
        //  pDialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.new_icon_progress_dialog, null));
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(cancelable);
        pDialog.setMessage(message);
        pDialog.show();
        //JavaScriptInterface jsInterface = new JavaScriptInterface(null,null,null,null,null,null,null,null,null,null,null,pDialog);
        resultsSeen = true;
        tinyDB.putBoolean("resultsSeen", true);
        return pDialog;
    }
}
class JavaScriptInterface {

    private static final String TAG = "IframeVideo";
    public static int totalPlayTime;
    private final Context context;


    private int bufferingCount;
    private long totalBufferingTime;
    private int duration;
    private String eventObtained;
    boolean resultObtained;
    TextView vid_dur;
    TextView bffering_no;
    TextView buff_time;
    TextView play_time;
    private String type;
    private String action_type,mapId,source;
    private String orderId;
    private String orgName;
    private ProgressDialog progressDialog;
   // private MainSharedViewModel viewModelNew;
    private Dialog dialog;
    private JSONObject videoObj;
    private JSONArray videoArray;
    private TextView play_time_dialogTv;

    public JavaScriptInterface(TextView buff_time, TextView play_time, TextView bffering_no, TextView vid_dur, String type,
                               String action_type, String mapId, String orgName, String source, String orderId, Context context
                               , ProgressDialog progressDialog) {
        this.buff_time=buff_time;
        this.play_time=play_time;
        this.bffering_no=bffering_no;
        this.vid_dur=vid_dur;
        this.action_type=action_type;
        this.mapId=mapId;
        this.source=source;
        this.orderId=orderId;
        this.orgName=orgName;
        this.type=type;
        this.context=context;
        this.progressDialog=progressDialog;
      //  this.viewModelNew=viewModelNew;


    }


    @JavascriptInterface
    public void onObtainResult(final int duration, final int bufferingCount, final long totalBufferingTime, long totalPlayingTime, long startplaytime) {
        this.bufferingCount = bufferingCount;
        this.totalBufferingTime = totalBufferingTime;
        this.duration = duration;
        Log.i(TAG, "Total Play time is " + totalPlayingTime);
        Log.i(TAG, "Buffering time is"+totalBufferingTime+ " time is "+ Utils.getDateTime());
        Log.i(TAG, "Video Duration is " + duration + " Buffering count is " + bufferingCount + " total playing time " + totalPlayingTime + " start play time is " + startplaytime);
        NewVideoFragmentIframe.resultsSeen=true;
       // dimissProgress();
        if (NewVideoFragmentIframe.progressDialog != null && NewVideoFragmentIframe.progressDialog.isShowing()) {
            NewVideoFragmentIframe.progressDialog.dismiss();
        }
        mView_HealthStatus.video_duration = duration;
        mView_HealthStatus.buffering_count = bufferingCount;
        mView_HealthStatus.total_buffering_time = totalBufferingTime/1000.0;
      //  mView_HealthStatus.totalPlayTime = (totalPlayingTime / 1000.0);
        //class.play_time.setText(String.valueOf(Utils.getRoundedOffVal(mView_HealthStatus.totalPlayTime + "", 2)));
       vid_dur.setText(String.valueOf(mView_HealthStatus.video_duration));
        bffering_no.setText(String.valueOf(mView_HealthStatus.buffering_count));
       buff_time.setText(String.valueOf(Utils.getRoundedOffVal(mView_HealthStatus.total_buffering_time + "", 2)));
        showAlert();
    }

    private void dimissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @JavascriptInterface
    public void onTimeOutOccured(boolean timeOut) {
        Log.i(TAG, "TimeOut is "+timeOut);

    }
    //1691043859381
    @JavascriptInterface
    public void onPlayerStateChanged(String state) {
        Log.i(TAG, "State is " + state);
        if (Utils.checkifavailable(state)) {
            if (state.equalsIgnoreCase("Playing")) ;
            eventObtained = state;

        }
    }

    @JavascriptInterface
    public void sendPlayingTime(final long totalPlayingTime) {

        int videoDuration =mView_HealthStatus.video_duration;
        System.out.println(" video duration is"+videoDuration);
        if(videoDuration==790) {

            mView_HealthStatus.totalPlayTime = (totalPlayingTime / 1000.0);
            play_time.setText(mView_HealthStatus.totalPlayTime + "");
            if (play_time_dialogTv != null) {
                play_time_dialogTv.setText(mView_HealthStatus.totalPlayTime + "");
            }
        }
        try {
            if(videoObj!=null) {
                videoObj.put("totalPlayTimeInSec", mView_HealthStatus.totalPlayTime);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    private void showAlert() {
        if(type==null)
        {
            type="";
        }
        {
            String testres=null;

            if (mView_HealthStatus.buffering_count < 2 && mView_HealthStatus.total_buffering_time < 500) {
                testres = "Very Good";
            } else if ((mView_HealthStatus.buffering_count > 1 && mView_HealthStatus.buffering_count <= 4) || mView_HealthStatus.total_buffering_time <= 1500) {
                testres = "Average";
            } else if ((mView_HealthStatus.buffering_count >= 5 )|| (mView_HealthStatus.total_buffering_time > 1500)) {
                testres = "Poor";
            }

// by swapnil 6/06/2023
             videoObj = new JSONObject();
             videoArray=new JSONArray();

            try {

                videoObj.put("videodurationInSec", mView_HealthStatus.video_duration);
                videoObj.put("totalBufferingTimeInMs", mView_HealthStatus.total_buffering_time);
                videoObj.put("totalPlayTimeInSec", mView_HealthStatus.totalPlayTime);
                videoObj.put("noOfBuffering", mView_HealthStatus.buffering_count);


                videoArray.put(videoObj);
                Utils.appendLog("ELOG_RUN_VIDEO_UI: Result of video test is "+ videoArray);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            showResultAlertDialogNew("videotest", context, 0, testres, R.layout.videoresultdialog,
                    type, "","", "Data Bandwidth issue",action_type,videoArray,orderId,orgName);
            MainActivity.testflag=true;


        }


    }

//    private void sendFinalResultToCallingApkVideoTest(String action_type, JSONArray json, String orderId, String orgName) {
//        System.out.println("in dialog2");
//        JSONObject jsonObject = null;
//        try {
//            jsonObject = new JSONObject();
//            jsonObject.put("action_type", action_type);
//            jsonObject.put("order_id", orderId);
//            jsonObject.put("org_name", orgName);
//           /* jsonObject.put("viewKey",viewKey);
//            jsonObject.put("intentRequestKey",intentRequestKey);*/
//            jsonObject.put("result", json);
//            JSONObject userDetailsObj = new JSONObject();
//            userDetailsObj.put("user_imsi", Utils.getImsi(AirtelWifiApplication.ctx));
//            userDetailsObj.put("user_number", Utils.getMyContactNum(AirtelWifiApplication.ctx));
//            userDetailsObj.put("product", Config.product);
//            GPSTracker gps = GPSTracker.getGps(AirtelWifiApplication.ctx);
//            if (gps != null) {
//                userDetailsObj.put("latitude", gps.getLatitude() + "");
//                userDetailsObj.put("longitude", gps.getLongitude() + "");
//            } else {
//                userDetailsObj.put("latitude", "0");
//                userDetailsObj.put("longitude", "0");
//            }
//            userDetailsObj.put("ver", Constants.getversionnumber(AirtelWifiApplication.ctx));
//
//            userDetailsObj.put("lacid", Config.getlacid(AirtelWifiApplication.ctx));
//            userDetailsObj.put("cellid", Config.getcellid(AirtelWifiApplication.ctx));
//            userDetailsObj.put("apn_type", Config.getApnType(AirtelWifiApplication.ctx));
//            userDetailsObj.put("apn", NetworkUtil.getApnName(AirtelWifiApplication.ctx));
//
//            jsonObject.put("user_details", userDetailsObj);
//            Log.i(TAG, "Test json " + jsonObject.toString());
//            JSONObject finalJsonObject = jsonObject;
//            ((AppCompatActivity)context).runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        dialog.dismiss();
//                      //  viewModelNew.setData(finalJsonObject.toString());
//                        Log.i(TAG, "Final video json is " + finalJsonObject.toString());
//                    }
//                    catch (Exception e)
//                    {
//                        e.printStackTrace();
//                    }
//
//                }
//            });
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//
//        Intent intent = new Intent();
//
//    }

    public void showResultAlertDialogNew(String test, final Context context, float avg, String result, int dialogview, String s,
                                         final String lat, final String lon, final String type, String action_type, JSONArray json, String orderId, String orgname) {
        System.out.println("in dialog with context "+context +" actionn type "+action_type);
        if (Utils.checkContext(context)) {
   dialog = new Dialog(context, R.style.AlertDialogTheme);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(dialogview, null);
            TextView resultTv = view.findViewById(R.id.finalResult);
            Button yes = view.findViewById(R.id.yes);
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
dimissProgress();
                    if(Utils.checkifavailable(action_type)) {
                        System.out.println("in dialog1 ");
                       // sendFinalResultToCallingApkVideoTest(action_type,json,orderId,orgname);
                    }

                }
            });
            Button no = view.findViewById(R.id.no);
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    NewVideoFragmentIframe.resultsSeen=true;
                    dimissProgress();
                }
            });

            {
                switch (test) {

                    case "videotest":
                        if (result != null) {
                            resultTv.setText(String.format("Video Streaming Quality : %s  ", result));

                        }
                        TextView vid_dur = (TextView) view.findViewById(R.id.vid_dur);
                        TextView bffering_no = (TextView) view.findViewById(R.id.buffering_no);
                        TextView buff_time = (TextView) view.findViewById(R.id.buffer_time);
                         play_time_dialogTv = (TextView) view.findViewById(R.id.playtime);
                        vid_dur.setText(String.valueOf(mView_HealthStatus.video_duration));
                        bffering_no.setText(String.valueOf(mView_HealthStatus.buffering_count));
                        buff_time.setText(String.valueOf(mView_HealthStatus.total_buffering_time));
                        play_time_dialogTv.setText(String.valueOf(mView_HealthStatus.totalPlayTime));
                        dialog.setContentView(view);
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.show();
                        break;


                }


            }


        }


    }

}