package com.newmview.wifi.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.mview.airtel.R;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.other.Utils;


import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import static android.view.Gravity.CENTER;

public class ExoPlayerinFragment extends Fragment {

    private View rootView;
    private SimpleExoPlayer exoplayer;
    private PlayerView playerView;
    private DefaultTrackSelector.Parameters trackSelectorParameters;

    private ComponentListener componentListener;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private ProgressBar progress_bar;
    private MediaSource mediaSource;
    private String path;
    private String source_name;
    private String media_type;
    private static final CookieManager DEFAULT_COOKIE_MANAGER;

    private DefaultTrackSelector trackSelector;
    private static final String KEY_WINDOW = "window";
    private static final String KEY_POSITION = "position";
    private static final String KEY_AUTO_PLAY = "auto_play";
    private static final String KEY_TRACK_SELECTOR_PARAMETERS = "track_selector_parameters";
    private DataSource.Factory dataSourceFactory;


    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    private boolean startAutoPlay;
    private int startWindow;
    private long startPosition;
    private boolean haveStartPosition;


    private Dialog mFullScreenDialog;
    private boolean mExoPlayerFullscreen;
    private FrameLayout main_RL;
    public static  boolean songEnd=false;

    private LinearLayout iv_thumbnail_LL;

    private DataSource.Factory buildDataSourceFactory() {
        return ((MviewApplication) getActivity().getApplication()).buildDataSourceFactory();
    }
    private void clearStartPosition() {
        startAutoPlay = true;
        startWindow = C.INDEX_UNSET;
        startPosition = C.TIME_UNSET;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);



    }
    private void applyAspectRatio(FrameLayout container, SimpleExoPlayer exoPlayer) {
        float videoRatio = (float) exoPlayer.getVideoFormat().width/exoPlayer.getVideoFormat().height;

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float displayRatio = (float) size.x/size.y;

        if (videoRatio > displayRatio) {
            container.getLayoutParams().height = Math.round(container.getMeasuredWidth()/videoRatio);
            container.requestLayout();
        } else {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.gravity = CENTER;
            container.setLayoutParams(params);
        }
    }

    private void updateStartPosition() {
        if (exoplayer != null) {
            startAutoPlay = exoplayer.getPlayWhenReady();
            startWindow = exoplayer.getCurrentWindowIndex();
            startPosition = Math.max(0, exoplayer.getContentPosition());
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        updateTrackSelectorParameters();
        updateStartPosition();
        outState.putParcelable(KEY_TRACK_SELECTOR_PARAMETERS, trackSelectorParameters);
        outState.putBoolean(KEY_AUTO_PLAY, startAutoPlay);
        outState.putInt(KEY_WINDOW, startWindow);
        outState.putLong(KEY_POSITION, startPosition);
    }

 /*   @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            hideSystemUiFullScreen();
        } else {
            hideSystemUi();
        }
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState)

    {
        super.onCreate(savedInstanceState);
        if (exoplayer!=null)
        {
            releasePlayer();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.exoplayer_view, container, false);
        getExtras();

        init();






Log.i("EXOfragment","oncreate of fragment");
dataSourceFactory = buildDataSourceFactory();

        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }
        componentListener = new ComponentListener();
        if (savedInstanceState != null) {
            trackSelectorParameters = savedInstanceState.getParcelable(KEY_TRACK_SELECTOR_PARAMETERS);
            startAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY);
            startWindow = savedInstanceState.getInt(KEY_WINDOW);
            startPosition = savedInstanceState.getLong(KEY_POSITION);
        } else {
            trackSelectorParameters = new DefaultTrackSelector.ParametersBuilder().build();
            clearStartPosition();
        }


        if (exoplayer==null) {
            intializeexoplayer();
        }

                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                exoplayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);

        return rootView;


    }

    private void getExtras() {

        path = getArguments().getString("video_path");
        System.out.println("path is "+path);


    }

    private void init() {
        playerView = (PlayerView)rootView.findViewById(R.id.exoplayer);
        progress_bar = (ProgressBar) rootView.findViewById(R.id.prpgresstowait);
        main_RL=(FrameLayout) rootView.findViewById(R.id.main_media_frame);
        iv_thumbnail_LL=(LinearLayout)rootView.findViewById(R.id.iv_thumbnail);

    }



    private MediaSource buildMediaSource(Uri uri) {
       // if (uri != null) {

            String userAgent = "Peacock";
            if (uri.getLastPathSegment().contains("mp3") || uri.getLastPathSegment().contains("mp4")) {

                return new ExtractorMediaSource(uri,
                        new DefaultDataSourceFactory(getActivity(), userAgent),
                        new DefaultExtractorsFactory(), null, null);
            } else if (uri.getLastPathSegment().contains("m3u8")) {


                return new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);


            } else {
                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(), Util.getUserAgent(getActivity(), "Peacock"));
                DashMediaSource dashMediaSource = new DashMediaSource(uri, dataSourceFactory,
                        new DefaultDashChunkSource.Factory(dataSourceFactory), null, null);
                return dashMediaSource;

            }


       // }
    }
       private void playwithexoplayer(String hls_url) {
        mediaSource = buildMediaSource(Uri.parse(hls_url));

        exoplayer.prepare(mediaSource, !haveStartPosition, false);
        exoplayer.addListener(componentListener);
        playerlistener();
        exoplayer.seekTo(currentWindow, playbackPosition);

    }

    private void playerlistener() {

        exoplayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {


             if (playWhenReady&&playbackState==ExoPlayer.STATE_READY)

             {

                 progress_bar.setVisibility(View.GONE);
//                 applyAspectRatio(main_RL,exoplayer);



                 if (getActivity().getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT)
                 {

closeFullscreenDialog();
                 }
                 else if (getActivity().getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE)
                 {

                 }


             }
             else if (playbackState==ExoPlayer.STATE_BUFFERING)
             {
                 progress_bar.setVisibility(View.VISIBLE);

             }
             else
             {
             }

            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

                progress_bar.setVisibility(View.GONE);


            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();


        if (Util.SDK_INT > 23) {
            if (exoplayer==null) {
                intializeexoplayer();
            }
            if (playerView != null) {
                playerView.onResume();
            }
        }
    }
    private void intializeexoplayer() {
        TrackSelection.Factory adaptiveTrackSelection = new AdaptiveTrackSelection.Factory();
        trackSelector=new DefaultTrackSelector(adaptiveTrackSelection);
        trackSelector.setParameters(trackSelectorParameters);
        RenderersFactory renderersFactory =
                ((MviewApplication) getActivity().getApplication()).buildRenderersFactory(true);

        exoplayer=ExoPlayerFactory.newSimpleInstance(getActivity(),renderersFactory,trackSelector,new DefaultLoadControl());
        playerView.setPlayer(exoplayer);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MOVIE)
                .build();
        exoplayer.setAudioAttributes(audioAttributes,true);
        exoplayer.addVideoDebugListener(componentListener);
        exoplayer.addAudioDebugListener(componentListener);
        exoplayer.setPlayWhenReady(playWhenReady);
        haveStartPosition = startWindow != C.INDEX_UNSET;
        if (haveStartPosition) {
            exoplayer.seekTo(startWindow, startPosition);
        }


            String remaining = path.substring(path.lastIndexOf("/") + 1);
            if (Utils.checkifavailable(path)) {

                    playwithexoplayer(path);

            }




    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("EXOFRAGMENT","ondestroy  called");


        releasePlayer();
    }

    private void updateTrackSelectorParameters() {
        if (trackSelector != null) {
            trackSelectorParameters = trackSelector.getParameters();
        }
    }
    private void releasePlayer() {
        if (exoplayer != null) {
            updateTrackSelectorParameters();
            updateStartPosition();
            playbackPosition = exoplayer.getCurrentPosition();
            currentWindow = exoplayer.getCurrentWindowIndex();
            playWhenReady=false;
            playWhenReady = exoplayer.getPlayWhenReady();
            exoplayer.removeListener(componentListener);
            exoplayer.setVideoListener(null);
            exoplayer.removeVideoDebugListener(componentListener);
            exoplayer.removeAudioDebugListener(componentListener);
            exoplayer.release();

            mediaSource=null;
            exoplayer = null;

        }
    }

    public void handleplaypauseexternal(String action)
    {

            if (action!=null&&action.equalsIgnoreCase("pause")) {



                playWhenReady=false;
                Log.i("EXOFRAGMENT","action called and "+playWhenReady);
                if (exoplayer!=null) {

                    exoplayer.setPlayWhenReady(playWhenReady);
                    onPause();
                }

            }
            else if (action!=null&&action.equals("play"))
            {
                playWhenReady = true;
                Log.i("EXOFRAGMENT","action called and "+playWhenReady);
                if (exoplayer!=null) {
                    exoplayer.setPlayWhenReady(playWhenReady);
                }
                onResume();

            }


    }
    private void initFullscreenDialog() {
System.out.println("in full scrren");
        mFullScreenDialog = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);

Utils.showToast(getActivity(),"fulllll");
        ((ViewGroup) playerView.getParent()).removeView(playerView);
        mFullScreenDialog.addContentView(playerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_fullscreen_skrink));
        mExoPlayerFullscreen = true;
        mFullScreenDialog.show();



    }

    public void closeFullscreenDialog() {

        if (playerView != null) {
            ((ViewGroup) playerView.getParent()).removeView(playerView);
            main_RL.addView(playerView);
            mExoPlayerFullscreen = false;
            if (mFullScreenDialog!=null)
            {
            mFullScreenDialog.dismiss();
        }}
    }
   public void openFullscreenDialog() {
       initFullscreenDialog();
   }

    public boolean isplaying() {
        if (exoplayer != null && (playWhenReady)) {
            return true;

        } else {
            return false;
        }
    }


    private class ComponentListener extends Player.DefaultEventListener implements VideoRendererEventListener, AudioRendererEventListener {
        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {

        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady,
                                         int playbackState) {
            String stateString;
            switch (playbackState) {
                case ExoPlayer.STATE_IDLE:
                    stateString = "ExoPlayer.STATE_IDLE      -";
                    playerView.setKeepScreenOn(false);
                    break;
                case ExoPlayer.STATE_BUFFERING:
                    stateString = "ExoPlayer.STATE_BUFFERING -";
                    playerView.setKeepScreenOn(true);
                    break;
                case ExoPlayer.STATE_READY:
                    stateString = "ExoPlayer.STATE_READY     -";
                    playerView.setKeepScreenOn(true);

                    break;
                case ExoPlayer.STATE_ENDED:
                    stateString = "ExoPlayer.STATE_ENDED     -";
                    playerView.setKeepScreenOn(false);



                    break;
                default:
                    stateString = "UNKNOWN_STATE             -";
                    break;
            }
            Log.d("EXOPLAYERWITHUSENOW", "changed state to " + stateString
                    + " playWhenReady: " + playWhenReady);
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {

        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {

        }

        @Override
        public void onPositionDiscontinuity(int reason) {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }

        @Override
        public void onSeekProcessed() {

        }

        @Override
        public void onVideoEnabled(DecoderCounters counters) {

        }

        @Override
        public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

        }

        @Override
        public void onVideoInputFormatChanged(Format format) {

        }

        @Override
        public void onDroppedFrames(int count, long elapsedMs) {

        }

        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

        }

        @Override
        public void onRenderedFirstFrame(@Nullable Surface surface) {

        }

        @Override
        public void onVideoDisabled(DecoderCounters counters) {

        }

        @Override
        public void onAudioEnabled(DecoderCounters counters) {

        }

        @Override
        public void onAudioSessionId(int audioSessionId) {

        }

        @Override
        public void onAudioDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

        }

        @Override
        public void onAudioInputFormatChanged(Format format) {

        }

        @Override
        public void onAudioSinkUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {

        }

        @Override
        public void onAudioDisabled(DecoderCounters counters) {

        }
    }
    @Override
    public void onResume()

    {
        super.onResume();

Log.i("EXOfragment","on resume called");
        if (Util.SDK_INT <= 23 || exoplayer == null) {
            if (exoplayer==null) {
                intializeexoplayer();
            }
            if (playerView != null) {
                playerView.onResume();
            }

        }
    }


    @Override
    public void onPause()
    {
        Log.i("EXOfragment","onpause called");

        super.onPause();
        releasePlayer();
    }
    @Override
    public void onStop()
    {
        super.onStop();
        Log.i("EXOfragment","onstop called");

        releasePlayer();

    }


    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUiFullScreen()
    {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }




}
