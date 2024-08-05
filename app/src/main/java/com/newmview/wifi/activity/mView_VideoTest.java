package com.newmview.wifi.activity;


import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.mview.airtel.R;
import com.newmview.wifi.mView_HealthStatus;
import com.webservice.WebService;

//https://www.sitepoint.com/using-the-youtube-api-to-embed-video-in-an-android-app/
public class mView_VideoTest  extends AppCompatActivity implements YouTubePlayer.OnInitializedListener, YouTubePlayer.PlaybackEventListener,
        YouTubePlayer.PlayerStateChangeListener,View.OnClickListener {

	private static final int RECOVERY_REQUEST = 1;
	private YouTubePlayerView youTubeView;
	private String YOUTUBE_API_KEY = "AIzaSyCJv1rre5KTyJoWOlhZAOs7fZQYKZT1j84";
	
	long totalbufferTime;
	long totaltimeittooktoplay;
	long videostartPlayTime;
	long startbufferingTime;
	int noOfBuffering;

    Button startBtn;
    TextView result,textview;
	YouTubePlayer player1;
	TextView vid_dur,bffering_no,buff_time,play_time;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videotest_new2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
       // youTubeView.initialize(YOUTUBE_API_KEY, this);

        textview=(TextView)findViewById(R.id.textview);
        vid_dur=(TextView)findViewById(R.id.vid_dur);
        bffering_no=(TextView)findViewById(R.id.buffering_no);
        buff_time=(TextView)findViewById(R.id.buffer_time);
        play_time=(TextView)findViewById(R.id.playtime);

       // startBtn=(Button)findViewById(R.id.startBtn);

        if( mView_HealthStatus.connectionTypeIdentifier == 1)
            textview.setText(mView_HealthStatus.OperatorName + "  " + mView_HealthStatus.connectionType);
        else
            textview.setText(mView_HealthStatus.OperatorName + "  " + mView_HealthStatus.connectionType + " " + mView_HealthStatus.strCurrentNetworkState);

        YouTubePlayerSupportFragment frag;
        frag = (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);
        frag.initialize(YOUTUBE_API_KEY, this);

        //result = (TextView) findViewById(R.id.result);
//        startBtn.setEnabled(false);
//
//        startBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                player1.cueVideo("Sg64rEtDd4s");
////                videostartPlayTime = System.currentTimeMillis();
////                player1.play();
//            }
//        });
    }
    
    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
        //Toast.makeText(this, "oninitialization success called", Toast.LENGTH_SHORT).show();
        if (!wasRestored) {
            String [] urlArr = mView_HealthStatus.youtubeurl.split("/?v=");
            if( urlArr.length == 2) {
                player.cueVideo(urlArr[1]); //fhWaJi1Hsfo"); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
                System.out.println("in if block");
            }
            else {
                player.cueVideo("Sg64rEtDd4s");
                System.out.println("in else block");
            }

            player1 = player;
            player.setPlayerStateChangeListener(this);
    		player.setPlaybackEventListener(this);
           // videostartPlayTime = System.currentTimeMillis();
            //startBtn.setEnabled(true);
            //player.play();
        }
    }

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            //String error = String.format(getString(R.string.player_error), errorReason.toString());
            String error = errorReason.toString();
            //Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(YOUTUBE_API_KEY, this);
        }
    }

    @Override
    public void onPlaying() {
        // Called when playback starts, either due to user action or call to play().
        //showMessage("Playing");
        //Toast.makeText(this, "started playing...", Toast.LENGTH_SHORT).show();
        vid_dur.setText("");
        bffering_no.setText("");
        buff_time.setText("");
        play_time.setText("");

        long t = System.currentTimeMillis();
    	totalbufferTime = totalbufferTime + (t - startbufferingTime);
    }

    @Override
    public void onPaused() {
        // Called when playback is paused, either due to user action or call to pause().
        //showMessage("Paused");
    }

    @Override
    public void onStopped() {
        // Called when playback stops for a reason other than being paused.
        //showMessage("Stopped");
        if(loaded) {

            totaltimeittooktoplay = (System.currentTimeMillis() - videostartPlayTime) / 1000;
            System.out.println("here video start time"+" "+videostartPlayTime);
            System.out.println("here current time: "+System.currentTimeMillis());
            System.out.println("here total time it took to play " +"  "+totaltimeittooktoplay);


            float f2 = player1.getDurationMillis();
            float f1 = f2 / 1000.0f;

            mView_HealthStatus.mySpeedTest.video.noOfBuffering = noOfBuffering;
            mView_HealthStatus.mySpeedTest.video.totalBuferingTime = totalbufferTime;
            mView_HealthStatus.mySpeedTest.video.totalPlayTime = totaltimeittooktoplay;
            mView_HealthStatus.mySpeedTest.video.videoDuration = (long) f1; //player1.getDurationMillis();
            try {
                if (WebService.isInternetAvailable())
                    WebService.API_sendVideoTest();
            } catch (Exception e) {

            }


            String res = "Total video duration(in sec) = " + f1 + "\nNo of Buferring = " + noOfBuffering + "\nTotal BufferingTime(in ms) = " + totalbufferTime +
                    "\nTotal PlayTime(sec) = " + totaltimeittooktoplay;
            vid_dur.setText(String.valueOf(f1));
            bffering_no.setText(String.valueOf(noOfBuffering));
            buff_time.setText(String.valueOf(totalbufferTime));
            play_time.setText(String.valueOf(totaltimeittooktoplay));

            //result.setText(res);
        }
    }

    @Override
    public void onBuffering(boolean b) {
        // Called when buffering starts or ends.
        //Toast.makeText(this, "on buffering called "+noOfBuffering++   +" "+"time ", Toast.LENGTH_SHORT).show();
        startbufferingTime = System.currentTimeMillis();
    	noOfBuffering++;
    }
    
    protected Provider getYouTubePlayerProvider() {
        return  youTubeView;
    }

	@Override
	public void onSeekTo(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAdStarted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(ErrorReason arg0) {
		// TODO Auto-generated method stub
		
	}

    boolean loaded;
	@Override
	public void onLoaded(String arg0) {
		// TODO Auto-generated method stub
		int a =0;
        loaded = true;
        //Toast.makeText(this, "onloaded called", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onLoading() {
		// TODO Auto-generated method stub
        //Toast.makeText(this, "onloading called", Toast.LENGTH_SHORT).show();
		int c =0;
	}

	@Override
	public void onVideoEnded() {
		// TODO Auto-generated method stub
        //Toast.makeText(this, "onvideoended called", Toast.LENGTH_SHORT).show();

		int b=0;
	}

	@Override
	public void onVideoStarted() {
		// TODO Auto-generated method stub
		int d =0;
        //Toast.makeText(this, "onvideostarted called", Toast.LENGTH_SHORT).show();
        videostartPlayTime = System.currentTimeMillis();
		d++;
	}


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


	/*@Override
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
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        //Toast.makeText(this, "clicked....!!1", Toast.LENGTH_SHORT).show();
    }
}
