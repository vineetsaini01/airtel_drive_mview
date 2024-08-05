package com.newmview.wifi.activity;

import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mview.airtel.R;
import com.newmview.wifi.fragment.ExoPlayerinFragment;
import com.newmview.wifi.other.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class VideoPlayer extends AppCompatActivity {
    VideoView videoView;
    private String video_path;
    private MediaController videoMediaController;
    private String img_path;
    private ImageView imageView;
    private ProgressBar progressBar;
    private ImageView play;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_view);
        init();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON

                        + WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        + WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );
        setImageToView();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExoPlayerinFragment exoPlayerinFragment=new ExoPlayerinFragment();
                Bundle args = new Bundle();
                args.putString("video_path", video_path);
                exoPlayerinFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.main,exoPlayerinFragment).commit();
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              //  VideoFragment videoFragment=new VideoFragment();
                ExoPlayerinFragment exoPlayerinFragment=new ExoPlayerinFragment();
                Bundle args = new Bundle();
                args.putString("video_path", video_path);
               exoPlayerinFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.main,exoPlayerinFragment).commit();
            }
        });

    }
    private void setImageToView() {

        if (Utils.checkifavailable(img_path)) {
            Picasso.with(this).load(img_path).error(R.drawable.default_image).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    progressBar.setVisibility(View.GONE);
                    Log.d("TAG", "onSuccess");
                }

                @Override
                public void onError() {
                    progressBar.setVisibility(View.GONE);

                }
            });

        }

    }


    private void playVideo() {

if(Utils.checkifavailable(video_path)) {
    Uri uri = Uri.parse(video_path);
    videoView.setVideoPath(video_path);
    videoView.start();
  //  videoMediaController = new MediaController(this);
/*
    if(Utils.checkifavailable(String.valueOf(uri))) {
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
        {
            @Override
            public void onPrepared(MediaPlayer mp)
            {
                progressBar.setVisibility(View.GONE);
                videoView.requestFocus();
                videoView.start();
            }
        });

        videoView.setVideoURI(uri);
        videoMediaController.setMediaPlayer(videoView);
        videoView.setMediaController(videoMediaController);

        //videoView.start();

    }
*/
}
    }

    private void init() {
        videoView=findViewById(R.id.vdo_view);
        imageView=findViewById(R.id.imgView);
        progressBar=findViewById(R.id.thumbnailprogress);
        play=findViewById(R.id.play);
       video_path= getIntent().getStringExtra("video");
       img_path=getIntent().getStringExtra("image");
    }
}
