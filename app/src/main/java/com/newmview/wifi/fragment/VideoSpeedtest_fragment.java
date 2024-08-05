package com.newmview.wifi.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.Toolbar;

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
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.bean.TestResults;
import com.newmview.wifi.database.DB_handler;
import com.newmview.wifi.helper.AllInOneAsyncTaskForNetwork;
import com.newmview.wifi.helper.Interfaces;
import com.newmview.wifi.helper.RequestResponse;
import com.newmview.wifi.listenService;
import com.newmview.wifi.mView_HealthStatus;
import com.newmview.wifi.other.CommonAlertDialog;
import com.newmview.wifi.other.Config;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;
import com.newmview.wifi.other.WifiConfig;
import com.newmview.wifi.viewmodel.TestResultsVM;
import com.webservice.WebService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by functionapps on 10/31/2018.
 */

public class VideoSpeedtest_fragment extends Fragment implements
        YouTubePlayer.OnInitializedListener, YouTubePlayer.PlaybackEventListener,
        YouTubePlayer.PlayerStateChangeListener,View.OnClickListener {


    private static final int RECOVERY_REQUEST = 1;
    private static final String TAG ="VideoSpeedtest_fragment" ;
    private YouTubePlayerView youTubeView;
    private String YOUTUBE_API_KEY = "AIzaSyCJv1rre5KTyJoWOlhZAOs7fZQYKZT1j84";

    long totalbufferTime=0;
    long totaltimeittooktoplay;
    long videostartPlayTime;
    long startbufferingTime=0;
    int noOfBuffering=0;

    Button startBtn;
    TextView result,textview;
    YouTubePlayer player1;
    TextView vid_dur,bffering_no,buff_time,play_time;
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
    private DB_handler dbHandler;
    private float x_coord,y_coord;
    private int color;
    private String mapId;
    private Bundle bundle;
    private Interfaces.RefreshDataListener refreshDataListener;
    private String source;
    private TestResultsVM viewModel;
    private List<TestResults> surveyTestResults;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHandler=new DB_handler(MviewApplication.ctx);
        getExtras();
//        viewModel=new ViewModelProvider(this,new MainViewModelFactory()).get(TestResultsVM.class);
//
//        viewModel.getTestResultsObservableAtId(Integer.parseInt(mapId)).observe(this, new Observer<List<TestResults>>() {
//            @Override
//            public void onChanged(List<TestResults> testResults) {
//                VideoSpeedtest_fragment.this.surveyTestResults=testResults;
//            }
//        });
    }

    private void getExtras() {
         bundle=getArguments();
        if(bundle!=null) {
            x_coord = bundle.getFloat("x");
            y_coord = bundle.getFloat("y");
          //  color = bundle.getInt("color");
            mapId = bundle.getString("mapId");
            source=bundle.getString("source");
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            refreshDataListener = (Interfaces.RefreshDataListener ) context;
        } catch (ClassCastException e) {
           e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {


           view=inflater.inflate(R.layout.videotest_new,container,false);
           init();


          

            if( mView_HealthStatus.connectionTypeIdentifier == 1)
                textview.setText(mView_HealthStatus.connectionType+"("+WifiConfig.getConnectedWifiDetails().getSsidName() + ")" );
            else
                textview.setText(mView_HealthStatus.OperatorName + "  " + mView_HealthStatus.connectionType + " " + mView_HealthStatus.strCurrentNetworkState);



        return view;
    }



    private void init() {

        args= args=getArguments();
        if(args!=null)
            type=args.getString("type");
        textview=(TextView)view.findViewById(R.id.textview);
        vid_dur=(TextView)view.findViewById(R.id.vid_dur);
        vdo_start_btn=(Button)view.findViewById(R.id.start);
        bffering_no=(TextView)view.findViewById(R.id.buffering_no);
        buff_time=(TextView)view.findViewById(R.id.buffer_time);
        play_time=(TextView)view.findViewById(R.id.playtime);
btn_rl=(RelativeLayout)view.findViewById(R.id.start_rl);
main_ll=view.findViewById(R.id.main_ll);

vdo_start_btn.setOnClickListener(this);
if(getActivity() instanceof MainActivity) {
    Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
    toolbar.setNavigationIcon(R.drawable.back_new);


    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            System.out.println("videotest fragmnet navigate");
            if(getActivity()!=null) {
                getActivity().onBackPressed();
            }


        }
    });
}
if(listenService.gps!=null) {
    lon = listenService.gps.getLongitude() + "";
    lat = listenService.gps.getLatitude() + "";
}
else
{
    lat="0.0";
    lon="0.0";
}
        stoptest=(Button)view.findViewById(R.id.stoptest);
        stoptest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player1 != null){
                    player1.pause();

                }
                if (loaded)
                {
                    System.out.println("send results called from 0");
                    sendResults();
                }


            }
        });
    }
    public void showDialog(String title, String message)
    {
        android.app.AlertDialog.Builder updatealert = new android.app.AlertDialog.Builder(getActivity());
        updatealert.setTitle(title);
        updatealert.setMessage(message);
        updatealert.setPositiveButton(Html.fromHtml("<font color='#FF0000'>OK</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                btn_rl.setVisibility(View.GONE);
                main_ll.setVisibility(View.VISIBLE);
                openMainScreen();

            }
        });
        updatealert.show();
//            updatealert.setNegativeButton(Html.fromHtml("<font color='#FF0000'>LATER</font>"), new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            }).show();

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
          String localUrl="www.youtube.com/embed/HngTaeW9KVs";
        //    String localUrl="https//nwexp.airtel.com/leap/leapYoutubeTest.html";
          //  String localUrl=mView_HealthStatus.youtubeurl;
            String [] urlArr = mView_HealthStatus.youtubeurl.split("/?v=");
            System.out.println("url before "+localUrl);
String url=Utils.getVideocodeinyoutube(localUrl);
            System.out.println("url after "+url);
            player.cueVideo(url);



          //  JSON.stringify(player.getAvailableQualityLevels());
           /* if( urlArr.length == 2) {
                player.cueVideo(urlArr[1]); //fhWaJi1Hsfo"); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
                System.out.println("in if block " +urlArr[1]);
            }
            else {
                player.cueVideo("Sg64rEtDd4s");
                System.out.println("in else block");
            }*/
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
            mView_HealthStatus.mySpeedTest.video.totalPlayTime = totaltimeittooktoplay;
            mView_HealthStatus.mySpeedTest.video.videoDuration = (long) f1; //player1.getDurationMillis();

            sendRequest();
            String res = "Total video duration(in sec) = " + f1 + " , \nNo of Bufferring = " + noOfBuffering + " , \nTotal BufferingTime(in ms) = " + totalbufferTime +
                        " , \nTotal PlayTime(sec) = " + totaltimeittooktoplay;
            System.out.println(res);
            setValuesToViews(f1);
            Log.i(TAG,"Source is "+source +" survey results "+surveyTestResults);
            if(Utils.checkifavailable(source))
            {
                if(source.equalsIgnoreCase("heatmap"))
                {
                    if(bundle!=null) {
                       if(surveyTestResults!=null) {
                           if(surveyTestResults.size()>0)
                           {
                           for (int i = 0; i < surveyTestResults.size(); i++) {

                               TestResults marker = surveyTestResults.get(i);
                               float historyX = marker.getX();
                               float historyY = marker.getY();
                               Log.i(TAG, "History X " + historyX + " history Y " + historyY + " touch X " + x_coord + " touch Y " + y_coord);
                               if ((historyX - 50 <= x_coord && x_coord <= historyX + 50)
                                       && (historyY - 50 <= y_coord && y_coord <= historyY + 50)) {
                                   Log.i(TAG,"Update existing entry mapid "+marker.getMapId() +" test id "+marker.getTestId() +" id "+marker.getId());
                                   viewModel.updateTestResults(marker.getMapId(), marker.getId(), res, marker.getTestId());
                               } else {
                                   insertNewMarker(res);

                               }
                               if (refreshDataListener != null) {
                                   refreshDataListener.refreshOnTouch();
                               }
                           }
                           }
                           else
                           {
                               insertNewMarker(res);
                               if (refreshDataListener != null) {
                                   refreshDataListener.refreshOnTouch();
                               }
                           }

                       }

                    }
                }
            }


//Utils.showToast(getActivity(),"report flag is "+MainActivity.REPORT_FLAG  +"mainactivity flag "+MainActivity.RESULT_FLAG);
                System.out.println("report flag is "+ MainActivity.REPORT_FLAG  +"mainactivity flag "+MainActivity.RESULT_FLAG);
            showAlert();
             /*  if(MainActivity.REPORT_FLAG) {
                    showAlert();

                }
                else if(MainActivity.RESULT_FLAG)
                {
                    String testres=null;

                    if (noOfBuffering < 2 && totalbufferTime < 500) {
                        testres = "Very Good";
                    } else if ((noOfBuffering > 1 && noOfBuffering <= 4) || totalbufferTime <= 1500) {
                        testres = "Average";
                    } else if (noOfBuffering >= 5 && totalbufferTime > 1500) {
                        testres = "Poor";
                    }


                    CommonAlertDialog.showResultAlertDialog("videotest", getActivity(), 0, testres, R.layout.videoresultdialog,
                            type, lat, lon, "Data Bandwidth issue");


                }

*/
            noOfBuffering = 0;
            totalbufferTime = 0;
            startbufferingTime = 0;

        }


    }

    private void insertNewMarker(String res) {
        Log.i(TAG,"Insert new res "+res +" mapId "+mapId +" test id "+Config.VIDEO_TEST_ID);
        TestResults testResults = new TestResults();
        testResults.setTouch(false);
        testResults.setMapId(mapId);
        testResults.setResult(res);
        testResults.setColor(color);
        testResults.setX(x_coord);
        testResults.setY(y_coord);
        testResults.setTestId(Config.VIDEO_TEST_ID + "");
        viewModel.insertNewTestResult(testResults);

    }

    private void sendRequest() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    //Your code goes here
                    if (WebService.isInternetAvailable()) {
                        JSONObject videoObj = new JSONObject();
                        videoObj.put("videodurationInSec", mView_HealthStatus.mySpeedTest.video.videoDuration);
                        videoObj.put("totalBufferingTimeInMs", mView_HealthStatus.mySpeedTest.video.totalBuferingTime);
                        videoObj.put("totalPlayTimeInSec", mView_HealthStatus.mySpeedTest.video.totalPlayTime);
                        videoObj.put("noOfBuffering", mView_HealthStatus.mySpeedTest.video.noOfBuffering);

                        JSONArray videoArray=new JSONArray();
                        videoArray.put(videoObj);
                        RequestResponse.sendEvent(videoArray, AllInOneAsyncTaskForNetwork.AsyncTaskPurpose.VIDEO_TEST_EVT,"video_test");
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


            CommonAlertDialog.showResultAlertDialog("videotest", getActivity(), 0, testres, R.layout.videoresultdialog,
                    type, lat, lon, "Data Bandwidth issue");
            MainActivity.testflag=true;

            loaded=false;
        }


    }

    private void setValuesToViews(float f1) {

        vid_dur.setText(String.valueOf(f1));
        bffering_no.setText(String.valueOf(noOfBuffering));
        buff_time.setText(String.valueOf(totalbufferTime));
        play_time.setText(String.valueOf(totaltimeittooktoplay));
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
              showDialog(getResources().getString(R.string.videotest_title),Constants.start_vdo_test);
              //showAlertDialog(getResources().getString(R.string.videotest_title),Constants.start_vdo_test);

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
                    public void onClick(DialogInterface dialog,
                                        int id) {
                          btn_rl.setVisibility(View.GONE);
                            main_ll.setVisibility(View.VISIBLE);
                            openMainScreen();

                    }
                });
        alert.show();
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
