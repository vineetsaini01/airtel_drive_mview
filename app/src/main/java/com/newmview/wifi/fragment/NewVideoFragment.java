package com.newmview.wifi.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.mview.airtel.R;
import com.newmview.wifi.activity.MainActivity;
import com.newmview.wifi.helper.AllInOneAsyncTaskForNetwork;
import com.newmview.wifi.helper.RequestResponse;
import com.newmview.wifi.listenService;
import com.newmview.wifi.mView_HealthStatus;
import com.newmview.wifi.other.CommonAlertDialog;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;
import com.webservice.WebService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.newmview.wifi.activity.MainActivity.context;


public class NewVideoFragment extends Fragment implements
        YouTubePlayer.OnInitializedListener, YouTubePlayer.PlaybackEventListener,
        YouTubePlayer.PlayerStateChangeListener,View.OnClickListener
{
    private static final String TAG = "NewVideoTest";
    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    private String YOUTUBE_API_KEY = "AIzaSyCJv1rre5KTyJoWOlhZAOs7fZQYKZT1j84";
     public String cdnIp;
    long totalbufferTime=0;
    long totaltimeittooktoplay;
    long videostartPlayTime;
    long startbufferingTime=0;
    int noOfBuffering=0;
    private float dataUsageBefore;
    private float dataUsageAfter;
    private float dataUsageDiff;
    private String dataUsageDiffVal;
    Button startBtn;
    TextView result,textview;
    YouTubePlayer player1;
    TextView vid_dur,bffering_no,buff_time,play_time,newVideoTest,newLatency,deviceHeight,deviceWidth,buffering_no,buffering_time,timeStamp,youTubeUrl,cdnIpResponse,totalBytes,data_used,stallRatio,totalstallRatio;
    private View view;
    private FragmentManager fragmentManager;
    private long endbufferingtime=0;
    private long time;
    private YouTubePlayerSupportFragment fragment;
    private boolean setValues=false;
    private String type;
    private Bundle args;
    private String lat,lon;
    private Button stoptest;
    private Button vdo_start_btn;
    private RelativeLayout btn_rl;
    private LinearLayout main_ll;
   private long stallratio;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.fragment_new_video,container,false);
        init();
        if( mView_HealthStatus.connectionTypeIdentifier == 1)
            textview.setText(mView_HealthStatus.OperatorName + "  " + mView_HealthStatus.connectionType);
        else
            textview.setText(mView_HealthStatus.OperatorName + "  " + mView_HealthStatus.connectionType + " " + mView_HealthStatus.strCurrentNetworkState);
        return view;
    }



    private void init() {
        args= args=getArguments();
        if(args!=null) type=args.getString("type");
        textview=(TextView) view.findViewById(R.id.textview);
        vid_dur=(TextView) view.findViewById(R.id.vid_dur);
        vdo_start_btn=(Button) view.findViewById(R.id.start);
        bffering_no=(TextView) view.findViewById(R.id.buffering_no);
        buff_time=(TextView) view.findViewById(R.id.buffer_time);
        play_time=(TextView) view.findViewById(R.id.playtime);
        btn_rl=(RelativeLayout) view.findViewById(R.id.start_rl);
        main_ll= view.findViewById(R.id.main_ll);
        newVideoTest= view.findViewById(R.id.newVideoTestPacketLoss);
        newLatency= view.findViewById(R.id.latencyNewVideoTest);
        deviceHeight= view.findViewById(R.id.deviceHeight);
        deviceWidth= view.findViewById(R.id.deviceWidth);
        buffering_no= view.findViewById(R.id.buffering_no);
        buffering_time=view.findViewById(R.id.buffering_time);
        vdo_start_btn.setOnClickListener(this);
        timeStamp=view.findViewById(R.id.timeStamp);
        youTubeUrl=view.findViewById(R.id.youTubeUrl);
        cdnIpResponse=view.findViewById(R.id.cdnIpResponse);
        totalBytes=view.findViewById(R.id.totalBytes);
        data_used=view.findViewById(R.id.data_used);
        stallRatio=view.findViewById(R.id.stallRatio);
        totalstallRatio=view.findViewById(R.id.totalstallRatio);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_new);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("videotest fragmnet navigate");
                getActivity().onBackPressed();
            }
        });
        if(listenService.gps!=null) {
            lon = listenService.gps.getLongitude() + "";
            lat = listenService.gps.getLatitude() + "";
        }
        else
        {
            lat="0.0";
            lon="0.0";
        }
        stoptest=(Button) view.findViewById(R.id.stoptest);
        stoptest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player1.pause();
                dataUsageAfter= fetchDataUsage();
                dataUsageDiff=dataUsageAfter-dataUsageBefore;
                dataUsageDiffVal= Utils.getRoundedOffVal(dataUsageDiff+"",2);
                if (loaded)
                {
                    System.out.println("send results called from 0");
                    sendResults();
                }


            }
        });
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

    private void initializeYouTubeFragment() {
        fragmentManager=getActivity().getSupportFragmentManager();
        fragment=new YouTubePlayerSupportFragment();
        fragment.initialize(YOUTUBE_API_KEY, this);
        fragmentManager.beginTransaction().replace(R.id.youtube_fragment,fragment).commit();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            dataUsageBefore= fetchDataUsage();
            String localUrl="www.youtube.com/embed/HngTaeW9KVs";
            String [] urlArr = mView_HealthStatus.youtubeurl.split("/?v=");
            System.out.println("url before  is"+urlArr);
            System.out.println("url before "+localUrl);
            String url= Utils.getVideocodeinyoutube(localUrl);
            System.out.println("url after "+url);
            player.cueVideo(url);

            try {
                URL url1 = new URL("https://"+localUrl);
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                InetAddress address = InetAddress.getByName(url1.getHost());
                cdnIp = address.getHostAddress();
                System.out.println(" cdnIp is"+cdnIp);
                if(Utils.checkifavailable(cdnIp)) {
                    if (address instanceof Inet4Address) {
                        Log.i("Pinger","Ipv4");
                        ping1(cdnIp,"ipV4");
                    } else if (address instanceof Inet6Address) {
                        Log.i("Pinger","Ipv6");
                        ping1(cdnIp,"ipV6");
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            player1 = player;
            player.setPlayerStateChangeListener(this);
            player.setPlaybackEventListener(this);


        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(getActivity(), RECOVERY_REQUEST).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(YOUTUBE_API_KEY, this);
        }
    }

    @Override
    public void onPlaying() {

        vid_dur.setText("");
        bffering_no.setText("");
        buff_time.setText("");
        play_time.setText("");
        buffering_no.setText("");
        newLatency.setText("");
        newVideoTest.setText("");
        deviceWidth.setText(" ");
        deviceHeight.setText(" ");
        buffering_time.setText(" ");
        timeStamp.setText(" ");
        youTubeUrl.setText(" ");
        cdnIpResponse.setText(" ");
        data_used.setText(" ");
        totalBytes.setText(" ");
        stallRatio.setText(" ");
        totalstallRatio.setText(" ");
        long t = System.currentTimeMillis();
        System.out.println("in playing startbuffering time  "+startbufferingTime + "totalbuffering time "+totalbufferTime +" "+Utils.getDateTime());
        totalbufferTime = totalbufferTime + (t - startbufferingTime);
    }

    @Override
    public void onPaused() {
        System.out.println("youtube activity paused");

    }

    @Override
    public void onStopped() {
        System.out.println("youtube activity stopped");
        System.out.println("activity context is "+getActivity() );
        if (loaded)
        {
            System.out.println("send results called from 1 ");
            sendResults();
        }
    }

    public static String ping1(final String url,String ipType) {

        final String[] str = {""};
        final String[] pckloss = {""};
        final String[] finalVal = {""};
        final String value;
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Process process=null;
                    Process mtrProcess=null;
                    if(Utils.checkifavailable(ipType))
                    {
                        if(ipType.equalsIgnoreCase("ipv6"))
                        {
                            process = Runtime.getRuntime().exec("ping6 -c 5 " + url);
                            Log.i("Pinger", "Command " + "ping6 -c 5 " + url);
                        }
                        else
                        {
                            process = Runtime.getRuntime().exec("ping -c 5 " + url);
                            Log.i("Pinger", "Command " + "ping -c 5 " + url);
                        }
                    }
                    else
                    {
                        process = Runtime.getRuntime().exec("ping -c 5 " + url);
                        Log.i("Pinger", "Command " + "ping -c 5 " + url);
                    }


                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    Log.i("Pinger", "Command " + "ping -c 1 " + url);
                    int i;
                    char[] buffer = new char[4096];
                    StringBuffer output = new StringBuffer();
                    String op[] = new String[64];
                    String delay[] = new String[8];
                    Log.i("Pinger", "Reader " + reader.read(buffer) + " buffer " + buffer);
                    while ((i = reader.read(buffer)) > 0)
                        output.append(buffer, 0, i);
                    reader.close();

                    op = output.toString().split("\n");
                    if (op!=null) {
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
                            mView_HealthStatus.packetLossNewVideoTest= packetlosss;
                            System.out.println(" Packet Loss is"+mView_HealthStatus.packetLossNew);

                        }
                        if (split[3].length() > 0) {
                            split[3] = split[3].trim();
                            String time = split[3];
                            Log.i(TAG, "Webpage load tie is " + time);
                            mView_HealthStatus.webPageLoadTime=time.substring(time.indexOf(" "), time.indexOf("ms"));
                        }

                        String rttstring = split[3].substring(split[3].indexOf("="), split[3].length());
                        rttstring.trim();
                        String[] timesplit = rttstring.split("/");
                        mView_HealthStatus.latencyNewVideoTest=timesplit[1];
                    }
                } catch (IOException e) {

                    e.printStackTrace();
                    System.out.println("exception is " + e.toString());
                } catch (ArrayIndexOutOfBoundsException ae) {
                    System.out.println("exception is " + ae.toString());
                    ae.printStackTrace();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });


        return finalVal[0];
    }

    private void sendResults() {
        {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Date resultdate = new Date(System.currentTimeMillis());
            String displaydate = sdf.format(resultdate);
            //  System.out.println("latest time "+displaydate);
            Date buffer = new Date(startbufferingTime);
            System.out.println("latest time " + displaydate + "  start time " + sdf.format(buffer));
            totaltimeittooktoplay = (System.currentTimeMillis() - startbufferingTime) / 1000;
            System.out.println("here video start time" + " " + videostartPlayTime);
            System.out.println("here current time: " + System.currentTimeMillis());
            System.out.println("here total time it took to play " + "  " + totaltimeittooktoplay);
            float f2 = player1.getDurationMillis();
            float f1 = f2 / 1000.0f;
            float length=f2/1024;
            Toast.makeText(getActivity(), "Video size:"+length+"KB",Toast.LENGTH_LONG).show();
            mView_HealthStatus.mySpeedTest.video.noOfBuffering = noOfBuffering;
            mView_HealthStatus.mySpeedTest.video.totalBuferingTime = totalbufferTime;
            System.out.println(" long video stall is is is"+ mView_HealthStatus.mySpeedTest.video.totalBuferingTime);
            mView_HealthStatus.mySpeedTest.video.totalPlayTime = totaltimeittooktoplay;
            System.out.println(" long video stall is is is is"+ mView_HealthStatus.mySpeedTest.video.totalPlayTime);
            mView_HealthStatus.mySpeedTest.video.videoDuration = (long) f1; //player1.getDurationMillis();
            stallratio=(mView_HealthStatus.mySpeedTest.video.totalBuferingTime)/ mView_HealthStatus.mySpeedTest.video.totalPlayTime;
            System.out.println(" long video stall is is"+stallRatio);
            if (totaltimeittooktoplay >= totalbufferTime) {
               long time_video_stalls= totaltimeittooktoplay - totalbufferTime;
               System.out.println(" long video stall is"+time_video_stalls);
              mView_HealthStatus.timeVideoStalls= String.valueOf(time_video_stalls)+"ms";
            } else {
                mView_HealthStatus.timeVideoStalls= "0ms";
            }
            System.out.println(" stall ratio is"+stallratio);
            mView_HealthStatus.stallRatio= String.valueOf(stallratio);
            sendRequest();
            String res = "Total video duration(in sec) = " + f1 + "\nNo of Buferring = " + noOfBuffering + "\nTotal BufferingTime(in ms) = " + totalbufferTime + "\nTotal PlayTime(sec) = " + totaltimeittooktoplay;
            System.out.println(res);
            setValuesToViews(f1);
            System.out.println("report flag is "+ MainActivity.REPORT_FLAG  +"mainactivity flag "+MainActivity.RESULT_FLAG);
            showAlert();
            noOfBuffering = 0;
            totalbufferTime = 0;
            startbufferingTime = 0;

        }


    }

    private void sendRequest() {
        System.out.println(" entering send request ");
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    //Your code goes here
                    if (WebService.isInternetAvailable()) {
                        JSONObject videoObj = new JSONObject();
//                        videoObj.put("videodurationInSec", mView_HealthStatus.mySpeedTest.video.videoDuration);
//                        videoObj.put("totalBufferingTimeInMs", mView_HealthStatus.mySpeedTest.video.totalBuferingTime);
//                        videoObj.put("totalPlayTimeInSec", mView_HealthStatus.mySpeedTest.video.totalPlayTime);
//                        videoObj.put("noOfBuffering", mView_HealthStatus.mySpeedTest.video.noOfBuffering);
                        videoObj.put("buffering count", String.valueOf(noOfBuffering));
                        videoObj.put("buffering time 1080", mView_HealthStatus.mySpeedTest.video.totalBuferingTime);
                        videoObj.put("bufferTime144","");
                        videoObj.put("bufferTime240","");
                        videoObj.put("bufferTime2K","");
                        videoObj.put("bufferTime360","");
                        videoObj.put("bufferTime480","");
                        videoObj.put("bufferTime4K","");
                        videoObj.put("bufferTime4KPlus","");
                        videoObj.put("bufferTime720","");
                        videoObj.put("data used", dataUsageDiffVal+"MB");
                        videoObj.put("device height", "390");
                        videoObj.put("device width", "640");
                        videoObj.put("latency", mView_HealthStatus.latencyNewVideoTest+"ms");
                        videoObj.put("packetLoss", mView_HealthStatus.packetLossNewVideoTest);
                        videoObj.put("playTime 1080", mView_HealthStatus.mySpeedTest.video.totalPlayTime);
                        videoObj.put(" playTime144","");
                        videoObj.put(" playTime240","");
                        videoObj.put(" playTime2K","");
                        videoObj.put(" playTime360","");
                        videoObj.put(" playTime480","");
                        videoObj.put(" playTime4K","");
                        videoObj.put(" playTime4KPlus","");
                        videoObj.put(" playTime720","");
                        videoObj.put("time Stamp", Utils.getDateTime());
                        videoObj.put("totalBytes",dataUsageDiffVal+"MB");
                        videoObj.put("totalBufferingTime ", mView_HealthStatus.mySpeedTest.video.totalPlayTime);
                        videoObj.put("totalVideoDuration", mView_HealthStatus.mySpeedTest.video.noOfBuffering);
                        videoObj.put("youTubeUrl ", "www.youtube.com/embed/HngTaeW9KVs");
                        videoObj.put("cdnIpResponse", cdnIp);
                        videoObj.put("videoResEnum","");
                        videoObj.put("timeout","");
                        videoObj.put("totalPlaybackTime","");
                        videoObj.put("stall Ratio",mView_HealthStatus.stallRatio);
                        videoObj.put("video Playback Rate ","");
                        videoObj.put(" time Video Stall",mView_HealthStatus.timeVideoStalls);
                        JSONArray videoArray=new JSONArray();
                        videoArray.put(videoObj);
                       // RequestResponse.sendEvent(videoArray, AllInOneAsyncTaskForNetwork.AsyncTaskPurpose.VIDEO_TEST_EVT,"video_test");
                        RequestResponse.sendNewVideoTestEvent(videoArray, AllInOneAsyncTaskForNetwork.AsyncTaskPurpose.NEW_VIDEO_TEST_EVT,"video_test");
//                        WebService.API_sendVideoTest();
//                        new WebService.Async_SendNeighboringCellsInfo().execute();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

    }

    private void showAlert() {
        if(type==null)
        {
            type="";
        }
        {
            String testres=null;
            if (noOfBuffering < 2 && totalbufferTime < 500) {
                testres = "Very Good";
            } else if ((noOfBuffering > 1 && noOfBuffering <= 4) || totalbufferTime <= 1500) {
                testres = "Average";
            } else if ((noOfBuffering >= 5 )|| (totalbufferTime > 1500)) {
                testres = "Poor";
            }
            CommonAlertDialog.showResultAlertDialog("videotest", getActivity(), 0, testres, R.layout.videoresultdialog, type, lat, lon, "Data Bandwidth issue");
            MainActivity.testflag=true;
            loaded=false;
        }


    }

    private void setValuesToViews(float f1) {
        buffering_no.setText(String.valueOf(noOfBuffering));
        vid_dur.setText(String.valueOf(f1));
        bffering_no.setText(String.valueOf(noOfBuffering));
        buff_time.setText(String.valueOf(totalbufferTime)+"ms");
        play_time.setText(String.valueOf(totaltimeittooktoplay)+"sec");
        newLatency.setText(mView_HealthStatus.latencyNewVideoTest+"ms");
        newVideoTest.setText(mView_HealthStatus.packetLossNewVideoTest);
        deviceWidth.setText("640");
        deviceHeight.setText("390");
        buffering_time.setText(String.valueOf(totalbufferTime)+"ms");
        timeStamp.setText(Utils.getDateTime());
        youTubeUrl.setText("www.youtube.com/embed/HngTaeW9KVs");
        cdnIpResponse.setText(cdnIp);
        data_used.setText(dataUsageDiffVal+"MB");
        stallRatio.setText(mView_HealthStatus.stallRatio);
        totalBytes.setText(dataUsageDiffVal+"MB");
        totalstallRatio.setText(mView_HealthStatus.timeVideoStalls);

    }

    @Override
    public void onBuffering(boolean b) {

        // Called when buffering starts or ends.
        if(b)
        {
            noOfBuffering=noOfBuffering+1;
            startbufferingTime=System.currentTimeMillis();
            System.out.println("in buffering "+startbufferingTime +Utils.getDateTime());

        }
        if(!b)
        {
            endbufferingtime=System.currentTimeMillis();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date resultdate = new Date(System.currentTimeMillis());



    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return  youTubeView;
    }

    @Override
    public void onSeekTo(int arg0) {
        // TODO Auto-generated method stub
        System.out.println("seek "+arg0);

    }

    @Override
    public void onAdStarted() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onError(YouTubePlayer.ErrorReason arg0) {
        // TODO Auto-generated method stub

    }

    boolean loaded;
    @Override
    public void onLoaded(String arg0) {
        // TODO Auto-generated method stub
        int a =0;
        loaded = true;
        player1.play();


    }

    @Override
    public void onLoading() {
        // TODO Auto-generated method stub

        int c =0;
    }

    @Override
    public void onVideoEnded() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onVideoStarted() {
        // TODO Auto-generated method stub
        int d =0;

        videostartPlayTime = System.currentTimeMillis();
        //   Utils.showAlert(Constants.TEST_START_MSG,getActivity());


        d++;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("youtube activity player is "+player1);
        if(player1!=null) {

            player1.release();
            System.out.println("youtube activity player is "+player1);
        }


    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            getActivity().onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.start:
               // showAlertDialog(getResources().getString(R.string.videotest_title), Constants.start_vdo_test);
                showCallAlertDialog(getResources().getString(R.string.videotest_title), Constants.start_vdo_test);
                break;
        }
    }
    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder alert=new AlertDialog.Builder(getActivity(),R.style.AlertDialogTheme);
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setCancelable(true);
        alert.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        btn_rl.setVisibility(View.GONE);
                        main_ll.setVisibility(View.VISIBLE);
                        openMainScreen();

                    }
                });
        alert.show();
    }


    public void showCallAlertDialog(String title,String message) {

        android.app.AlertDialog.Builder updatealert = new android.app.AlertDialog.Builder(context);
        updatealert.setTitle(title);
        updatealert.setMessage(message);
        updatealert.setPositiveButton(Html.fromHtml("<font color='#FF0000'>OK</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                btn_rl.setVisibility(View.GONE);
                main_ll.setVisibility(View.VISIBLE);
                openMainScreen();

            }
        });
        updatealert.show();



    }

    private void openMainScreen() {

        initializeYouTubeFragment();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

}