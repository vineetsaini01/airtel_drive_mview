package com.newmview.wifi.fragment;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mview.airtel.R;
import com.newmview.wifi.other.Utils;

public class VideoFragment extends Fragment {
    private View view;
    private VideoView videoView;
    private ProgressBar progressBar;
    private String video_path;
    private MediaController videoMediaController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.video_new,container,false);
        init();
        getExtras();
        playVideo();
        return view;

    }

    private void getExtras() {
       Bundle bundle= this.getArguments();
       video_path=bundle.getString("video_path");
       System.out.println("video_path is "+video_path);
    }

    private void init() {


        videoView=view.findViewById(R.id.vdo_view);

        progressBar=view.findViewById(R.id.thumbnailprogress);
    }

    private void playVideo() {

        if(Utils.checkifavailable(video_path)) {

            Uri uri = Uri.parse(video_path);
            videoMediaController = new MediaController(getActivity());
            if(Utils.checkifavailable(String.valueOf(uri))) {
                videoView.setVideoURI(uri);
               // videoView.setVideoPath(video_path);
                videoMediaController.setAnchorView(videoView);
                videoMediaController.setMediaPlayer(videoView);
                videoView.setMediaController(videoMediaController);
                videoView.requestFocus();

                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
                {
                    @Override
                    public void onPrepared(MediaPlayer mp)
                    {
                      //  progressBar.setVisibility(View.GONE);
                        videoView.start();



                    }
                });


            }
        }

    }

}
