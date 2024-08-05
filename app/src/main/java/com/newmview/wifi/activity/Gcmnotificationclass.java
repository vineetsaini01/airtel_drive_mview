/*
package com.mcpsinc.mview.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.functionapps.hungama.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hungama.adapters.ScheduleInfoClass;
import com.hungama.database.DB_KeyAdapter;
import com.hungama.fragment.BannerVideoplayerclass;
import com.hungama.fragment.Settings;
import com.hungama.fragment.Youtubefragment;
import com.hungama.helper.ALertDialog;
import com.hungama.helper.AllInOneAsyncTask.AsyncTaskPurpose;
import com.hungama.helper.ApplicationBitmapManager;
import com.hungama.helper.Config;
import com.hungama.helper.Constant;
import com.hungama.helper.ConstantStrings;
import com.hungama.helper.Contactsclass;
import com.hungama.helper.GetterSetter;
import com.hungama.helper.NetworkClass;
import com.hungama.helper.Request_Response;
import com.hungama.helper.Utils;
import com.hungama.interfaces.Interfaces;
import com.hungama.model.Bannermodel;
import com.hungama.services.Peacock;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Gcmnotificationclass extends AppCompatActivity implements
        OnSeekBarChangeListener, Callback,
        OnCompletionListener, OnBufferingUpdateListener, OnClickListener,
        OnSeekCompleteListener, AnimationListener, OnPreparedListener {

    public static final String MyPREFERENCES = "MYPrefgcm";
    private static final String TAG = "androidEx2 = VideoSample";
    public static ArrayList<Bannermodel> Bannerlist = new ArrayList<Bannermodel>();
    public static boolean nameflag;
    public static boolean numberflag;
    static int video_position;
    static String title = null;
    static ProgressDialog pdialog;
    static String video_path;
    private static int millis;
    private static int surfacecheck = 0;
    private static ArrayList<HashMap<String, String>> videoplaylist;
    private static Bitmap bitmap;
    private static View view;
    private static MediaPlayer player;
    private static File picDir;
    private static File picFile;
    TextView floatbtn;
    String gcmId;
    ArrayList<HashMap<String, String>> contacts;
    float x1, x2;
    float y1, y2;
    ImageView banner;

    String imageUrl = null;
    String bannerimgpath, banner_id, bannerurlto_open;

    String vid_id, amount, charge_txt;
    TextView moreimgview;
    RelativeLayout testrelativelayout;
    private ProgressDialog moreprogressdialog;
    private TextView textViewPlayed, titledesc;
    private TextView textViewLength;
    private SeekBar seekBarProgress;
    private SurfaceView surfaceViewFrame;
    private LinearLayout surfaceparentlinear;
    private ImageView imageViewPauseIndicator, thumbnail_vid_id, backbtn;
    private SurfaceHolder holder;
    private ProgressBar progressBarWait;
    private Timer updateTimer;
    private Bundle extras;
    private Animation hideMediaController;
    private LinearLayout linearLayoutMediaController;
    private String description = null;
    private ProgressBar pb;
    private Dialog dialog;
    private SurfaceHolder holderuse;
    private TextView cur_val, text;
    private String videocode;
    private ArrayList<HashMap<String, String>> category_contentList;
    private ProgressDialog progressDialog;
    private ArrayList<String> subcatnamelist = new ArrayList<String>();
    private String cat_id;
    private String sub_cat_name;
    private String galcategory;
    private String act_flag = "0";
    private String act_msg = "none";
    private String lock_flag = "no";
    private String catname = "notification";
    private String comments;
    private String shareby = "";
    private String source_type;
    private String charg_unit;
    private LinearLayout moretextlinearlayout;
    private boolean passwordflag;
    private String more;
    private LinearLayout lowerlineralayout;

    private FragmentManager fragmentManager;
    private String preview_path = "";
    private ArrayList<File> fileList = new ArrayList<File>();
    private String path;
    private ArrayList<String> pathnamelist = new ArrayList<String>();
    private String videoname;
    private ArrayList<String> sendernamelist = new ArrayList<String>();
    private String shareto;
    private String sharetoname;
    private ArrayList<String> numbertosharelist = new ArrayList<String>();
    private String numbertoshare;
    private String nametoshare;
    private String stype;
    private String sendername;
    private ArrayList<String> Sharenamelist;
    private boolean videostart;
    private LinearLayout errorlinearlayout;
    private TextView errortxtview;
    private String bannermediatype;

    private androidx.appcompat.app.ActionBar actionBar;
    private TextView titletxtview;
    private String secondpart;
    private String firstpart;
    private String finalurl;

    private Configuration newConfig;
    private boolean mIsInForegroundMode;
    private String pShare;
    private LinearLayout optionslinearlayout;
    private PopupMenu popup;
    private ImageView menuimgview;
    private RelativeLayout downloadlinearlayout;
    private Menu popupMenu;
    private MenuItem followitem;
    private MenuItem downloaditem;
    private MenuItem galleryitem;
    private MenuItem feedbackitem;
    private TextView commenttextview;
    private TextView descriptiontxtview;
    private FloatingActionButton commentimgview1;
    private AllInOneAsyncTaskpreview allInOneAsyncTaskpreview;

    private String lock_flag_set;
    private String media_type="";

    public static Bitmap createVideoThumbnail(String fDescriptor, int kind, int dur) {

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        ArrayList<Bitmap> bArray = new ArrayList<Bitmap>();
        bArray.clear();

        try {

            if (Build.VERSION.SDK_INT >= 14) {
                retriever.setDataSource(fDescriptor, new HashMap<String, String>());
//		   for(int i=1000000;i<dur*1000;i+=1000000)
//	        {
//		   int i=dur*1000;
                bitmap = retriever.getFrameAtTime(dur * 1000, MediaMetadataRetriever.OPTION_CLOSEST);
                bArray.add(bitmap);
//	        }
            } else {
                retriever.setDataSource(fDescriptor);
                for (int i = 1000000; i < dur * 1000; i += 1000000) {
                    bitmap = retriever.getFrameAtTime(i, MediaMetadataRetriever.OPTION_CLOSEST);
                    bArray.add(bitmap);
                }

            }


        } catch (IllegalArgumentException ex) {
        } catch (RuntimeException ex) {
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
            }
        }

        if (bitmap == null) return null;

        if (kind == MediaStore.Images.Thumbnails.MINI_KIND) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int max = Math.max(width, height);
            if (max > 512) {
                float scale = 512f / max;
                int w = Math.round(scale * width);
                int h = Math.round(scale * height);
                bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
            }
        } else if (kind == MediaStore.Images.Thumbnails.MICRO_KIND) {

        }
        return bitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playerpage);

        // Initialize the resources
        init();

        extras = getIntent().getExtras();
        description = extras.getString("desc").replaceAll("\n", "<br>");
        title = extras.getString("title").replaceAll("\n", "<br>");
        video_path = extras.getString("videourl");
        vid_id = extras.getString("id");
        amount = extras.getString("amount");
        imageUrl = extras.getString("imgurl");
        source_type = extras.getString("source_name");
        pShare = extras.getString("pShare");
        if (source_type != null && (source_type.equalsIgnoreCase("peacock") || source_type.equalsIgnoreCase("cms"))) {
            String secondpart = video_path.substring(video_path.lastIndexOf("/") + 1);
            String firstpart = video_path.substring(0, video_path.lastIndexOf("/") + 1);

            byte[] urlOfVideo = new byte[0];
            try {


                urlOfVideo = Base64.decode(secondpart, Base64.DEFAULT);
                String urlInfo = new String(urlOfVideo);
                finalurl = firstpart + urlInfo;

            } catch (Exception e) {
                finalurl = video_path;
                e.printStackTrace();
            }

        } else {
            finalurl = video_path;
        }


        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        toolbar.setBackgroundResource(R.color.transparent);


        titletxtview = findViewById(R.id.toolbar_title);
        titletxtview.setText("Peacock");

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back));
        actionBar.setDisplayShowCustomEnabled(true);


        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                SharedPreferences pref = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.clear();
                edit.commit();
            }
        });


        //=====================Creating Banners===================================

        adp = new DB_KeyAdapter(Gcmnotificationclass.this);
        adp.open();
        Cursor cur = adp.SelectBannersData();
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                Bannermodel list = new Bannermodel();
                list.setBannerid(cur.getString(0));
                list.setImg_Path(cur.getString(2));
                list.setAct_url(cur.getString(1));
                list.setMedia_type(cur.getString(3));
                Bannerlist.add(list);
            }
            cur.close();
        }
        for (int i = 0; i < Bannerlist.size(); i++) {

            if (video_position < Bannerlist.size()) {
                bannerimgpath = Bannerlist.get(video_position).getImgpath();
                banner_id = Bannerlist.get(video_position).getBannerid();
                bannerurlto_open = Bannerlist.get(video_position).getUrl();
                bannermediatype = Bannerlist.get(video_position).getMedia_type();

            } else {
                bannerimgpath = Bannerlist.get(i).getImgpath();
                banner_id = Bannerlist.get(i).getBannerid();
                bannerurlto_open = Bannerlist.get(i).getUrl();
                bannermediatype = Bannerlist.get(i).getMedia_type();
            }

        }


        ImageLoader imageLoader = ImageLoader.getInstance();

        if (bannerimgpath != null) {
            imageLoader.init(ImageLoaderConfiguration.createDefault(Gcmnotificationclass.this));
            try {
                imageLoader.displayImage(bannerimgpath, banner, null, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        // banner.setImageResource(1);
                        BitmapDrawable ob = new BitmapDrawable(getResources(), loadedImage);
                        Config.ob = ob;
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String arg0, View arg1,
                                                FailReason arg2) {
                        // TODO Auto-generated method stub

                    }
                }, new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current, int total) {

                    }
                });
            } catch (Exception e) {
                Toast.makeText(Gcmnotificationclass.this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }

        floatbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameflag && numberflag) {
                    getscreenshot(finalurl, new Interfaces.getscreenshot() {
                        @Override
                        public void getsuccessscreenshot(String success) {
                            if (success.equalsIgnoreCase("1")) {
                                lock_flag_set = "no";
                                new Contactsclass(video_position,media_type, lock_flag_set, pShare, Gcmnotificationclass.this, description, vid_id, imageUrl, title, amount, video_path, source_type, catname, "player", "", "", "", "","");
                            }
                        }
                    });


                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(Gcmnotificationclass.this);
                    alert.setCancelable(false);
                    alert.setIcon(R.drawable.alert_icon);
                    alert.setTitle((Html.fromHtml("<font color='#000000'>Share</font>")));
                    alert.setMessage(ConstantStrings.getprofileforshare);
                    alert.setPositiveButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            getscreenshot(finalurl, new Interfaces.getscreenshot() {
                                @Override
                                public void getsuccessscreenshot(String success) {
                                    if (success.equalsIgnoreCase("1")) {
                                        sfragment = new Settings();
                                        Bundle args1 = new Bundle();
                                        args1.putString("module_name", "share");
                                        args1.putString("class_type", "player");
                                        args1.putString("source_name", source_type);
                                        args1.putString("catname", catname);
                                        args1.putString("title", title);
                                        args1.putString("video_path", video_path);
                                        args1.putString("img_path", imageUrl);
                                        args1.putString("amount", amount);
                                        args1.putString("id", vid_id);
                                        args1.putString("desc", description);
                                        args1.putSerializable("file", picFile);
                                        sfragment.setArguments(args1);
                                        sfragment.setTargetFragment(sfragment, 90);
                                        fragmentManager.beginTransaction()
                                                .replace(R.id.surfaceparentlinear, sfragment, Config.fragment_tag)
                                                .addToBackStack("null").commit();
                                        Config.backtrace = "settings";
                                    }
                                }
                            });


                        }
                    });
                    AlertDialog al = alert.create();
                    al.show();

                }
            }
        });
        // Banner Click Listener=====================================================
        banner.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Config.isNetworkAvailable(Gcmnotificationclass.this)) {
                    if (Bannerlist != null && Bannerlist.size() > 0) {

                        Config.callEvent("BC", banner_id, Gcmnotificationclass.this, amount, bannermediatype, "", catname, "", "", "", "", "");


                        if (bannermediatype.equalsIgnoreCase("video")) {


                            if (bannerurlto_open != null) {
                                Config.backtrace = "search_item";


                                if (bannerurlto_open.contains(".mp4")) {
                                    if (myfragment != null) {
//                                        getSupportFragmentManager().beginTransaction().remove(myfragment).commit();


                                        surfaceViewFrame.setVisibility(View.GONE);
                                        bannervideoplayerclassfragment = new BannerVideoplayerclass();
                                        Bundle args = new Bundle();
                                        args.putString("videopath", bannerurlto_open);
                                        bannervideoplayerclassfragment.setArguments(args);
                                        getSupportFragmentManager().beginTransaction().remove(myfragment).commit();
                                        fragmentManager
                                                .beginTransaction()
                                                .add(R.id.frameLayoutRootplayer, bannervideoplayerclassfragment).addToBackStack("banner").commit();

                                        Config.backtrace = "bannerplayer";


                                    } else if (player != null) {
                                        if (player.isPlaying()) {
                                            player.stop();
                                            player.release();
                                            player = null;
                                            surfaceViewFrame.setVisibility(View.GONE);
                                            fragment = new BannerVideoplayerclass();
                                            Bundle args = new Bundle();
                                            args.putString("videopath", bannerurlto_open);
                                            fragment.setArguments(args);
                                            fragmentManager
                                                    .beginTransaction()
                                                    .replace(R.id.frameLayoutRootplayer, fragment,
                                                            Config.fragment_tag)
                                                    .commit();
                                        }
                                    } else {
                                        fragment = new BannerVideoplayerclass();
                                        Bundle args = new Bundle();
                                        args.putString("videopath", bannerurlto_open);
                                        fragment.setArguments(args);
                                        fragmentManager
                                                .beginTransaction()
                                                .replace(R.id.frameLayoutRootplayer, fragment,
                                                        Config.fragment_tag)
                                                .commit();
                                    }
                                } else if (bannerurlto_open.contains("www.youtube.com")) {
                                    videocode = bannerurlto_open.substring(22);
                                    myfragment = Youtubefragment.newInstance(videocode, "", "", Gcmnotificationclass.this,null,0);
                                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutRootplayer, myfragment).commit();
                                }
                            }
                        } else if (bannermediatype.equalsIgnoreCase("web")) {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(bannerurlto_open));
                            Gcmnotificationclass.this.startActivity(Intent.createChooser(i, "Choose Via"));
                        } else if (bannermediatype.equalsIgnoreCase("app")) {
                            Intent downloadappintent = new Intent(Gcmnotificationclass.this, Bannerdownloadapp.class);
                            startActivity(downloadappintent);
                        }
                        SharedPreferences sharedpreferences = Gcmnotificationclass.this.getSharedPreferences("bannerfragmentclass", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("bannerclass", "videolist");
                        editor.commit();
                    }
                } else {
                    Toast.makeText(Gcmnotificationclass.this,
                            ConstantStrings.nointernetconn,
                            Toast.LENGTH_LONG).show();
                }
            }
        });


        holder = surfaceViewFrame.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(this);
        holder.setKeepScreenOn(true);
        adp = new DB_KeyAdapter(Gcmnotificationclass.this);
        adp.open();
        Cursor profilecur = adp.selectuserprofile(ConstantStrings.imsi);
        if (profilecur.getCount() > 0) {
            while (profilecur.moveToNext()) {
                charge_txt = profilecur.getString(profilecur.getColumnIndex("charg_txt"));
                charg_unit = profilecur.getString(profilecur.getColumnIndex("charg_unit"));
                Constant.NAME = profilecur.getString(profilecur.getColumnIndex("name"));
                Constant.NUMBER = profilecur.getString(profilecur.getColumnIndex("number"));
                Constant.PASSWORD = profilecur.getString(profilecur.getColumnIndex("lock"));
                Constant.EMAIL = profilecur.getString(profilecur.getColumnIndex("email"));

            }
        }
        profilecur.close();
        adp.close();
        if (Constant.PASSWORD.equals(null)) {
            passwordflag = false;

        } else if (Constant.PASSWORD.equals("null")) {
            passwordflag = false;
        } else passwordflag = !Constant.PASSWORD.equals("");

        if (Constant.NAME.equals("")) {
            nameflag = false;
        } else if (Constant.NAME.equals(null)) {
            nameflag = false;
        } else nameflag = !Constant.NAME.equals("null");

        if (Constant.NUMBER.equals("9999999999")) {
            numberflag = false;
        } else if (Constant.NUMBER.equals("")) {
            numberflag = false;
        } else if (Constant.NUMBER.equals(null)) {
            numberflag = false;
        } else numberflag = !Constant.NUMBER.equals("null");

        moreimgview.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (Config.isNetworkAvailable(Gcmnotificationclass.this)) {


                    getmoredesc(Gcmnotificationclass.this);


                }
            }
        });


        if (imageUrl != "" && imageUrl != null && (!imageUrl.equals(""))) {

            ApplicationBitmapManager.INSTANCE.loadBitmap(imageUrl, thumbnail_vid_id);


        }


        progressBarWait.setVisibility(View.GONE);
        imageViewPauseIndicator.setVisibility(View.VISIBLE);

        titledesc.setText(Html.fromHtml("<b>" + title + "</b>"));
        descriptiontxtview.setText(Html.fromHtml(description));

        linearLayoutMediaController = findViewById(R.id.linearLayoutMediaController);
        linearLayoutMediaController.setVisibility(View.GONE);
        hideMediaController = AnimationUtils.loadAnimation(this,
                R.anim.disapearing);
        hideMediaController.setAnimationListener(this);

        moreimgview.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (Config.isNetworkAvailable(Gcmnotificationclass.this)) {

                    getmoredesc(Gcmnotificationclass.this);

                } else {
                    Toast.makeText(Gcmnotificationclass.this, ConstantStrings.nointernetconn, Toast.LENGTH_SHORT).show();
                }


            }
        });


        if (player != null) {
            if (!player.isPlaying()) {
                imageViewPauseIndicator.setVisibility(View.VISIBLE);
            }
        }

        imageViewPauseIndicator.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                video_path = extras.getString("videourl");

                secondpart = video_path.substring(video_path.lastIndexOf("/") + 1);
                firstpart = video_path.substring(0, video_path.lastIndexOf("/") + 1);

                byte[] urlOfVideo = Base64.decode(secondpart, Base64.DEFAULT);
                String urlInfo = new String(urlOfVideo);
                finalurl = firstpart + urlInfo;


                if (Config.isNetworkAvailable(Gcmnotificationclass.this)) {


                    Config.callEvent("VP", vid_id, Gcmnotificationclass.this,
                            amount, "", "notification", catname, "", title, description, video_path, imageUrl);


                    videocode = video_path.substring(22);
                    if (video_path.contains("www.youtube.com")) {
                        if (amount != null && (!(amount.equals("0.00") || amount.equals("0")))) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(Gcmnotificationclass.this);
                            alert.setMessage(charge_txt + " " + amount + " for this video");
//                            alert.setIcon(R.drawable.alert_icon);

                            alert.setTitle((Html.fromHtml("<font color='#000000'>Charges</font>")));
                            alert.setPositiveButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    // TODO Auto-generated method stub
                                    myfragment = Youtubefragment.newInstance(videocode, "", "", Gcmnotificationclass.this,null,0);
                                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutRootplayer, myfragment).commit();
                                }
                            });
                            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub

                                }
                            });
                            AlertDialog al = alert.create();
                            al.show();
                            int dividerId = al.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
                            View divider = al.findViewById(dividerId);
                            divider.setBackgroundColor(Color.parseColor("#E54026"));


                        } else {
                            Youtubefragment myfragment = Youtubefragment.newInstance(videocode, "", "", Gcmnotificationclass.this,null,0);
                            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutRootplayer, myfragment).commit();
                        }


                    } else {
                        if (player == null) {
                            if (amount != null && (!(amount.equals("0.00") || amount.equals("0")))) {

                                AlertDialog.Builder alert = new AlertDialog.Builder(Gcmnotificationclass.this);
                                alert.setMessage(charge_txt + " " + amount + " for this video");
//                                alert.setIcon(R.drawable.alert_icon);
                                alert.setTitle((Html.fromHtml("<font color='#000000'>Charges</font>")));
                                alert.setPositiveButton(ConstantStrings.positiveobbutton, new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {


                                        thumbnail_vid_id.setVisibility(View.GONE);
                                        progressBarWait.setVisibility(View.VISIBLE);
                                        player = new MediaPlayer();
                                        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                        playVideo(finalurl);

                                        player.setDisplay(holderuse);
                                        imageViewPauseIndicator.setVisibility(View.GONE);
                                        JSONObject jobj = new JSONObject();
                                        Map<String, String> param = null;
                                        try {
                                            jobj.put("msg", "cr");
                                            jobj.put("interface", "CLI");
                                            jobj.put("prod", ConstantStrings.product);
                                            jobj.put("imsi", ConstantStrings.imsi);
                                            jobj.put("msisdn", "9999999999");
                                            jobj.put("contentid", vid_id);
                                            jobj.put("amt", charge_txt);
                                            jobj.put("ver", ConstantStrings.getversionnumber(Gcmnotificationclass.this));
                                            jobj.put("latitude", Peacock.latitude);
                                            jobj.put("longitude", Peacock.longitude);
                                            jobj.put("lacid", ConstantStrings.getlacid(Gcmnotificationclass.this));
                                            jobj.put("cellid", ConstantStrings.getcellid(Gcmnotificationclass.this));
                                            jobj.put("apn_type", ConstantStrings.apntype(Gcmnotificationclass.this));
                                            jobj.put("apn", ConstantStrings.apnname(Gcmnotificationclass.this, ConstantStrings.apntype(Gcmnotificationclass.this)));

                                            param = new HashMap<String, String>();
                                            param.put("req", jobj.toString());

                                        } catch (JSONException e) {
                                            // TODO Auto-generated catch block<
                                            e.printStackTrace();
                                        }
                                        GetterSetter.paramcharging = param;
                                        GetterSetter.chargingreqstflag = true;

                                        Gcmnotificationclass.this.startService(new Intent(Gcmnotificationclass.this, EventService.class));


                                    }
                                });
                                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        // TODO Auto-generated method stub

                                    }
                                });
                                AlertDialog al = alert.create();
                                al.show();


                                adp.close();
                            } else {
                                thumbnail_vid_id.setVisibility(View.GONE);
                                progressBarWait.setVisibility(View.VISIBLE);
                                player = new MediaPlayer();
                                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                playVideo(finalurl);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {


                                        if (videostart == false) {

                                            progressBarWait.setVisibility(View.GONE);

                                            imageViewPauseIndicator.setVisibility(View.VISIBLE);
                                            errorlinearlayout.setVisibility(View.VISIBLE);

                                        }
                                    }
                                }, 30000);
                                player.setDisplay(holderuse);
                                imageViewPauseIndicator.setVisibility(View.GONE);
                            }

                        } else if (player != null) {
                            if (!player.isPlaying()) {
                                player.start();
                                updateMediaProgress();

                                imageViewPauseIndicator.setVisibility(View.GONE);
                                linearLayoutMediaController.setVisibility(View.VISIBLE);
                                hideMediaController();
                            }

                        }
                    }
                } else {
                    Toast.makeText(Gcmnotificationclass.this, ConstantStrings.nointernetconn, Toast.LENGTH_SHORT).show();
                }


            }


        });


    }

    private void init() {
        // TODO Auto-generated method stub
        floatbtn = findViewById(R.id.fab);
        descriptiontxtview = findViewById(R.id.descriptiontxtview);
        commentimgview1 = findViewById(R.id.commentimgview1);
        commentimgview1.setVisibility(View.GONE);
        optionslinearlayout = findViewById(R.id.optionslinearlayout);
        menuimgview = findViewById(R.id.menuimgview);
        view = findViewById(R.id.mainlayoutcapture);//your layout i


        errortxtview = findViewById(R.id.errortxtview);
        errorlinearlayout = findViewById(R.id.errorlinearlayout);

        fragmentManager = getSupportFragmentManager();
        lowerlineralayout = findViewById(R.id.lowerlineralayout);
        testrelativelayout = findViewById(R.id.testlinear);


        moreimgview = findViewById(R.id.moretext);
        textViewPlayed = findViewById(R.id.textViewPlayed);
        textViewLength = findViewById(R.id.textViewLength);
        surfaceViewFrame = findViewById(R.id.surfaceViewFrame);
        surfaceViewFrame.setClickable(false);
        imageViewPauseIndicator = findViewById(R.id.imageViewPauseIndicator);
        imageViewPauseIndicator.setImageResource(R.drawable.play);

        thumbnail_vid_id = findViewById(R.id.thumbnail_vid_id);
        seekBarProgress = findViewById(R.id.seekBarProgress);
        seekBarProgress.setOnSeekBarChangeListener(this);
        seekBarProgress.setProgress(0);
        surfaceparentlinear = findViewById(R.id.surfaceparentlinear);
        progressBarWait = findViewById(R.id.progressBarWait);
        banner = findViewById(R.id.bannersurfaceview);
        titledesc = findViewById(R.id.titledesc1);
        Linkify.addLinks(titledesc, Linkify.WEB_URLS);
        downloadlinearlayout = findViewById(R.id.downloadlayout);

        banner.setVisibility(View.VISIBLE);

        menuimgview.setOnClickListener(this);
        downloadlinearlayout.setOnClickListener(this);
        surfaceViewFrame.setOnClickListener(this);


    }

    private void playVideo(final String videopath2) {

        new Thread(new Runnable() {
            public void run() {
                try {
                    player.setDataSource(videopath2);
                    player.setLooping(true);
                    player.prepare();
                    player.setOnPreparedListener(Gcmnotificationclass.this);
                    player.setOnCompletionListener(Gcmnotificationclass.this);
                    player.setOnBufferingUpdateListener(Gcmnotificationclass.this);
                    player.setOnSeekCompleteListener(Gcmnotificationclass.this);
                    player.setScreenOnWhilePlaying(true);
                } catch (IllegalArgumentException e) {
                    errortxtview.setText(ConstantStrings.illegalstateexceptioninplayer);

                    e.printStackTrace();
                } catch (SecurityException e) {
                    // TODO Auto-generated catch block

                    errortxtview.setText(ConstantStrings.illegalstateexceptioninplayer);

                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    errortxtview.setText(ConstantStrings.illegalstateexceptioninplayer);
                    e.printStackTrace();

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    errortxtview.setText(ConstantStrings.illegalstateexceptioninplayer);

                    e.printStackTrace();


                }


            }


        }).start();

    }

    private void hideMediaController() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(5000);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            linearLayoutMediaController
                                    .startAnimation(hideMediaController);
                        }
                    });
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        if (!fromUser) {
            textViewPlayed.setText(Utils.durationInSecondsToString(progress));
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        if (player.isPlaying()) {
            progressBarWait.setVisibility(View.VISIBLE);
            player.seekTo(seekBar.getProgress() * 1000);
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub

    }

    public void surfaceCreated(SurfaceHolder holder) {
        holderuse = holder;

    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        if (player != null)
            try {
                if (player != null) {
                    if (player.isPlaying())
                        player.stop();
                    player.release();
                    player = null;
                }
            } catch (Exception ex) {
                // showToast("Error while streaming try Again");
                // this.finish();
            }
        surfacecheck = 1;
    }

    public void onPrepared(MediaPlayer mp) {
        int durationfrscr = extras.getInt("duration");
        if (durationfrscr != 0) {
            int duration = mp.getDuration() / 1000;
            mp.seekTo(durationfrscr * 1000);
            // duration in seconds
            seekBarProgress.setMax(duration);
            textViewLength.setText(Utils.durationInSecondsToString(duration));
            progressBarWait.setVisibility(View.GONE);
        } else {
            int duration = mp.getDuration() / 1000; // duration in seconds
            seekBarProgress.setMax(duration);
            textViewLength.setText(Utils.durationInSecondsToString(duration));
            progressBarWait.setVisibility(View.GONE);
        }
        // Get the dimensions of the video
        int videoWidth = player.getVideoWidth();
        int videoHeight = player.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;
//
        // Get the width of the screen
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;
        // Get the SurfaceView layout parameters
        android.view.ViewGroup.LayoutParams lp = surfaceViewFrame
                .getLayoutParams();

        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }

        // Commit the layout parameters
        surfaceViewFrame.setLayoutParams(lp);


        surfaceViewFrame.setClickable(true);

        // Start video
        if (!player.isPlaying()) {
            player.start();
            updateMediaProgress();
            linearLayoutMediaController.setVisibility(View.VISIBLE);
            hideMediaController();
        }

    }

    public void onCompletion(MediaPlayer mp) {
        // updateTimer.cancel();
        player.pause();
        seekBarProgress.setProgress(0);

        if (player != null) {
            if (!player.isPlaying()) {
                player.seekTo(seekBarProgress.getProgress() * 1000);
                updateMediaProgress();
                imageViewPauseIndicator.setVisibility(View.VISIBLE);
                linearLayoutMediaController.setVisibility(View.VISIBLE);
            }

        }
        if (updateTimer != null) {
            updateTimer.cancel();
        }


    }

    private void updateMediaProgress() {
        updateTimer = new Timer("progress Updater");
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            if (player != null && seekBarProgress != null)
                                seekBarProgress.setProgress(player
                                        .getCurrentPosition() / 1000);
                        } catch (Exception ex) {
                            // showToast("Error while streaming try Again");
                            player.release();
                            Gcmnotificationclass.this.finish();

                        }
                    }
                });
            }
        }, 0, 1000);
    }

    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        int progress = (int) ((float) mp.getDuration() * ((float) percent / (float) 100));
        seekBarProgress.setSecondaryProgress(progress / 1000);
    }

    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.menuimgview:


                popup = new PopupMenu(this, menuimgview, Gravity.BOTTOM);


                popup.getMenuInflater().inflate(R.menu.pdpmenu, popup.getMenu());


                popupMenu = popup.getMenu();
                followitem = popupMenu.findItem(R.id.followmenu);
                downloaditem = popupMenu.findItem(R.id.download);
                galleryitem = popupMenu.findItem(R.id.move);
                feedbackitem = popupMenu.findItem(R.id.feedbackmenu);


                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {


                        switch (menuItem.getItemId()) {
                            case R.id.followmenu:
                                // TODO Auto-generated method stub
                                Request_Response.followreq(Gcmnotificationclass.this,"content", vid_id, title, video_path, imageUrl, amount, charg_unit, "notification", source_type, description);
                                break;
                            case R.id.feedbackmenu:
                                ALertDialog.feedbackalert(Gcmnotificationclass.this, vid_id);

                                break;
                            case R.id.sharemenu:
                                lock_flag_set = "no";
                                new Contactsclass(video_position, media_type,lock_flag_set, pShare, Gcmnotificationclass.this, description, vid_id, imageUrl, title, amount, video_path, source_type, "notification", "player", "", "", "", "","");


                                break;
                            case R.id.move:

                                if (ConstantStrings.comedy.equalsIgnoreCase("y")) {
                                    galleryinsert(vid_id,
                                            amount, title, description, imageUrl, video_path, act_flag, act_msg
                                            , charg_unit, lock_flag, "notification", comments, shareby, source_type, "");
                                } else if (ConstantStrings.video.equalsIgnoreCase("y")) {
//                                    showgalerypopupforcategory();
                                    ALertDialog.showgalerypopupforcategory(Gcmnotificationclass.this, "",catname, amount, vid_id, title, description, imageUrl, video_path, act_flag, act_msg, charg_unit, lock_flag, comments, shareby, source_type);
                                }

                                break;

                            case R.id.download:
                                if (Config.isNetworkAvailable(Gcmnotificationclass.this)) {
                                    if (source_type.equalsIgnoreCase("cms") || source_type.equalsIgnoreCase("peacock")) {

                                        Utils.downloadfiles(Gcmnotificationclass.this, video_path, title, vid_id);

                                    } else {
                                        Request_Response.downloadpreview(Gcmnotificationclass.this, vid_id, source_type, title);


                                    }


                                } else {
                                    Toast.makeText(Gcmnotificationclass.this, ConstantStrings.nointernetconn, Toast.LENGTH_SHORT).show();
                                }


                                break;


                        }
                        return true;
                    }
                });

                popup.show();


                break;


            case R.id.surfaceViewFrame:
                if (linearLayoutMediaController.getVisibility() == View.GONE) {
                    linearLayoutMediaController.setVisibility(View.VISIBLE);
                    hideMediaController();

                } else if (player != null) {
                    if (player.isPlaying()) {
                        player.pause();
                        imageViewPauseIndicator.setVisibility(View.VISIBLE);

                    } else {
                        player.start();
                        imageViewPauseIndicator.setVisibility(View.GONE);

                    }
                }

                break;


            case R.id.pauseimg:
                if (surfaceViewFrame.getVisibility() == View.GONE) {

                    Toast.makeText(getApplicationContext(),
                            ConstantStrings.errorreadingserver,
                            Toast.LENGTH_SHORT).show();
                } else if (player != null && !player.isPlaying()) {
                    player.start();
                    imageViewPauseIndicator.setVisibility(View.GONE);
                }
                break;


            case R.id.commentimgview1:


                boolean flag;

                if (shareby.equals(null)) {
                    flag = false;

                } else flag = !shareby.equals("");


                boolean checkflag;
                if (comments.equals("")) {
                    checkflag = false;
                } else if (comments.equals("null")) {
                    checkflag = false;

                } else
                    checkflag = !comments.equals(null);

                if (checkflag && flag) {
                    commenttextview.setText(Html.fromHtml("<b>" + "Comments:" + "</b>" + " " + comments + "<br>" + "<br>"));
                }

                break;

        }

    }

    public void onSeekComplete(MediaPlayer mp) {
        progressBarWait.setVisibility(View.GONE);
    }

    public void onAnimationEnd(Animation animation) {
        // TODO Auto-generated method stub

    }

    public void onAnimationRepeat(Animation animation) {
        // TODO Auto-generated method stub

    }

    public void onAnimationStart(Animation animation) {
        linearLayoutMediaController.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder backalert = new AlertDialog.Builder(Gcmnotificationclass.this);
        backalert.setMessage("Do you wish to add this video in your gallery??");
//        backalert.setIcon(R.drawable.alert_icon);
        backalert.setTitle(ConstantStrings.gobindas);
        backalert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (player != null) {
                    player.release();
                    player = null;
                }
                if (ConstantStrings.comedy.equalsIgnoreCase("y")) {
                    galleryinsert(vid_id,
                            amount, title, description, imageUrl, video_path, act_flag, act_msg
                            , charg_unit, lock_flag, "notification", comments, shareby, source_type, "");
                } else if (ConstantStrings.video.equalsIgnoreCase("y")) {
                    showgalerypopupforcategory();
                }
            }
        });
        backalert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (player != null) {
                    player.release();
                    player = null;
                } else {
                    Intent inte = new Intent(Gcmnotificationclass.this, MainActivity.class);
                    startActivity(inte);
                    finish();
                }
            }
        }).show();

    }

    private void showgalerypopupforcategory() {
        LayoutInflater layoutInflater = LayoutInflater.from(Gcmnotificationclass.this);
        View promptView = layoutInflater.inflate(R.layout.gallerycatelayout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Gcmnotificationclass.this);
//        alertDialogBuilder.setIcon(R.drawable.alert_icon);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setTitle(Html.fromHtml("<font color='#000000'>Peacock</font>"));
        alertDialogBuilder.setMessage("Select the playlist type");
        alertDialogBuilder.setView(promptView);
        final Spinner categoryspinner = promptView.findViewById(R.id.gallcategory);
        subcatnamelist = new ArrayList<String>();
        adp.open();
        final Cursor getcategoryidcur = adp.SelectMyContent("MyContent");
        if (getcategoryidcur.getCount() > 0) {
            while (getcategoryidcur.moveToNext()) {
                cat_id = getcategoryidcur.getString(getcategoryidcur.getColumnIndex("id"));

            }
        }
        getcategoryidcur.close();
        subcatnamelist = new ArrayList<String>();
        Cursor getsubcategoriesofmycontentcursor = adp.selectsubcategorydata(cat_id);
        if (getsubcategoriesofmycontentcursor.getCount() > 0) {
            while (getsubcategoriesofmycontentcursor.moveToNext()) {
                sub_cat_name = getsubcategoriesofmycontentcursor.getString(getsubcategoriesofmycontentcursor.getColumnIndex("subcat_name"));
                subcatnamelist.add(sub_cat_name);

            }

        }
        getsubcategoriesofmycontentcursor.close();
        adp.close();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Gcmnotificationclass.this, android.R.layout.simple_spinner_item, subcatnamelist);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryspinner.setAdapter(dataAdapter);


        alertDialogBuilder.setPositiveButton(ConstantStrings.positiveobbutton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                galcategory = categoryspinner.getSelectedItem().toString();
                galleryinsert(vid_id,
                        amount, title, description, imageUrl, video_path, act_flag, act_msg
                        , charg_unit, lock_flag, catname, comments, shareby, source_type, galcategory);

            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();


    }

    private void galleryinsert(String vidid, String amount, String title, String description, String imageUrl, String videopath1, String act_flag1, String act_msg1, String chargetxt, String lock_flag, String catname, String comments1, String shareby1, String source_type, String galcat) {

        if (lock_flag == null) {
            lock_flag = "no";
        }
        if (comments1 == null) {
            comments1 = "";
        }
        JSONObject jobj = new JSONObject();
        Map<String, String> param = null;
        try {
            jobj.put("msg", "gi");
            jobj.put("interface", "CLI");
            jobj.put("prod", ConstantStrings.product);
            jobj.put("imsi", ConstantStrings.imsi);
            jobj.put("msisdn", ConstantStrings.msisdn);
            jobj.put("category", galcat);
            jobj.put("item_id", vidid);
            jobj.put("amt", amount);
            jobj.put("user_title", StringEscapeUtils.escapeJava(title));
            jobj.put("desc", StringEscapeUtils.escapeJava(description));
            jobj.put("imgurl", imageUrl);
            jobj.put("charg_unit", chargetxt);
            jobj.put("video_path", videopath1);
            jobj.put("lock_flag", lock_flag);
            jobj.put("comments", StringEscapeUtils.escapeJava(comments1));
            jobj.put("source_url", "");
            jobj.put("shareby", shareby1);
            jobj.put("source_type", source_type);
            jobj.put("ver", ConstantStrings.getversionnumber(Gcmnotificationclass.this));
            jobj.put("latitude", Peacock.latitude);
            jobj.put("longitude", Peacock.longitude);
            jobj.put("lacid", ConstantStrings.getlacid(Gcmnotificationclass.this));
            jobj.put("cellid", ConstantStrings.getcellid(Gcmnotificationclass.this));
            jobj.put("apn_type", ConstantStrings.apntype(Gcmnotificationclass.this));
            jobj.put("apn", ConstantStrings.apnname(Gcmnotificationclass.this, ConstantStrings.apntype(Gcmnotificationclass.this)));
            param = new HashMap<String, String>();
            param.put("req", jobj.toString());

        } catch (JSONException e) {
            // TODO Auto-generated catch block<
            e.printStackTrace();
        }
        String jobjstr = jobj.toString();
        if (jobjstr != null && Config.isNetworkAvailable(Gcmnotificationclass.this)) {
            new insertideostogallery().execute(param);
        } else {
            Toast.makeText(Gcmnotificationclass.this, ConstantStrings.nointernetconn, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        this.newConfig = newConfig;
        int videoprogress = 0;

        SharedPreferences pref = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        more = pref.getString("desc", "");
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (player != null) {
                videoprogress = player.getCurrentPosition() / 1000;
            }
            video_path = extras.getString("videourl");
            String[] split = video_path.split("/");
            if (video_path.equals(videocode)) {
                videocode = vid_id;
            } else {
                videocode = split[split.length - 1];
            }

            testrelativelayout.setVisibility(View.GONE);
            banner.setVisibility(View.GONE);
            optionslinearlayout.setVisibility(View.GONE);
            actionBar.hide();
            if (source_type.equalsIgnoreCase("peacock") || source_type.equalsIgnoreCase("cms")) {


                if (player != null) {
                    int videoWidth = player.getVideoWidth();
                    int videoHeight = player.getVideoHeight();
                    float videoProportion = (float) videoWidth / (float) videoHeight;

                    // Get the width of the screen
                    int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
                    int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
                    float screenProportion = (float) screenWidth / (float) screenHeight;
                    // Get the SurfaceView layout parameters
                    android.view.ViewGroup.LayoutParams lp = surfaceViewFrame.getLayoutParams();
                    if (videoProportion > screenProportion) {
                        lp.width = screenWidth;
                        lp.height = (int) ((float) screenWidth / videoProportion);
                    } else {
                        lp.width = (int) (videoProportion * (float) screenHeight);
                        lp.height = screenHeight;
                    }
                    // Commit the layout parameters
                    surfaceViewFrame.setLayoutParams(lp);

                }


            }
        } else if ((newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) && (source_type.equalsIgnoreCase("peacock") || source_type.equalsIgnoreCase("cms") || source_type.equalsIgnoreCase("youtube"))) {
            testrelativelayout.setVisibility(View.VISIBLE);
            banner.setVisibility(View.VISIBLE);
            optionslinearlayout.setVisibility(View.VISIBLE);
            actionBar.show();
        }
    }


    public void onStop() {
        super.onStop();
    }

    public void onRestart() {
        super.onRestart();
    }


    //=======Getting More Description============
    @SuppressWarnings("unchecked")
    public void getmoredesc(Gcmnotificationclass context) {
        // TODO Auto-generated method stub
        JSONObject jobj = new JSONObject();
        Map<String, String> param = null;
        try {
            jobj.put("msg", "gdr");
            jobj.put("interface", "CLI");
            jobj.put("prod", ConstantStrings.product);
            jobj.put("imsi", ConstantStrings.imsi);
            jobj.put("msisdn", ConstantStrings.msisdn);
            jobj.put("search_key", title);
            jobj.put("ver", ConstantStrings.getversionnumber(Gcmnotificationclass.this));
            jobj.put("latitude", Peacock.latitude);
            jobj.put("longitude", Peacock.longitude);
            jobj.put("lacid", ConstantStrings.getcellid(Gcmnotificationclass.this));
            jobj.put("cellid", ConstantStrings.getcellid(Gcmnotificationclass.this));
            jobj.put("apn_type", ConstantStrings.apntype(Gcmnotificationclass.this));
            jobj.put("apn", ConstantStrings.apnname(Gcmnotificationclass.this, ConstantStrings.apntype(Gcmnotificationclass.this)));
            param = new HashMap<String, String>();
            param.put("req", jobj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jobjstr = jobj.toString();
        if (jobjstr != null) {
            new AllInOneAsyncTaskmore(Gcmnotificationclass.this).execute(param);
        }
    }

    //===========Confirmation Message=======
    @SuppressWarnings("unchecked")
    public void actionconfirmation(Gcmnotificationclass context) {
        // TODO Auto-generated method stub
        JSONObject jobj = new JSONObject();
        Map<String, String> param = null;
        try {
            jobj.put("msg", "acr");
            jobj.put("interface", "CLI");
            jobj.put("prod", ConstantStrings.product);
            jobj.put("imsi", ConstantStrings.imsi);
            jobj.put("msisdn", "9999999999");
            jobj.put("stype", "content");
            jobj.put("id", "");
            jobj.put("resp_code", "y");
            jobj.put("ver", ConstantStrings.getversionnumber(Gcmnotificationclass.this));
            jobj.put("latitude", Peacock.latitude);
            jobj.put("longitude", Peacock.longitude);
            jobj.put("lacid", ConstantStrings.getcellid(Gcmnotificationclass.this));
            jobj.put("cellid", ConstantStrings.getcellid(Gcmnotificationclass.this));
            jobj.put("apn_type", ConstantStrings.apntype(Gcmnotificationclass.this));
            jobj.put("apn", ConstantStrings.apnname(Gcmnotificationclass.this, ConstantStrings.apntype(Gcmnotificationclass.this)));
            param.put("req", jobj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jobjstr = jobj.toString();
        if (jobjstr != null) {
            new AllInOneAsyncTaskconfirmation(Gcmnotificationclass.this).execute(param);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        mIsInForegroundMode = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsInForegroundMode = true;
    }

    private String sendQuery(String query) throws IOException {
        String result = "";
        URL sUrl = new URL(query);
        HttpURLConnection httpURLConnection = (HttpURLConnection) sUrl.openConnection();
        httpURLConnection.setRequestMethod("GET");


        httpURLConnection.setRequestProperty("Accept", "application/json");
        int a = httpURLConnection.getResponseCode();
        if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    (httpURLConnection.getInputStream())));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }
            bufferedReader.close();
        }
        return result;
    }

    private ArrayList<HashMap<String, String>> ParseStringResult(String json) throws JSONException {

        JSONObject jsonObject = new JSONObject(json);
        category_contentList = new ArrayList<HashMap<String, String>>();
        JSONArray jsonArray_result = jsonObject.getJSONArray("items");
        for (int k = 0; k < jsonArray_result.length(); k++) {

            HashMap<String, String> hmap = new HashMap<String, String>();
            JSONObject jsonObject1 = jsonArray_result.getJSONObject(k);

            String kind = jsonObject1.getString("kind");
            String title = jsonObject1.getString("title");
            String link = jsonObject1.getString("link");
            String desc = jsonObject1.getString("snippet");

            JSONObject j3 = jsonObject1.getJSONObject("pagemap");

            JSONArray j1 = j3.getJSONArray("cse_image");
            JSONObject j2 = j1.getJSONObject(0);
            String imgpath = j2.getString("src");

            hmap.put("kind", kind);
            hmap.put("title", title);
            hmap.put("link", link);
            hmap.put("img", imgpath);
            hmap.put("desc", desc);
            category_contentList.add(hmap);
        }


        return category_contentList;
    }

    public ArrayList<File> getfile(File dir) {
        File[] listFile = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    fileList.add(listFile[i]);
                    getfile(listFile[i]);

                } else {
                    if (listFile[i].getName().endsWith(".mp4")) {
                        fileList.add(listFile[i]);
                        Log.e("SErvice filelist size", "" + fileList.size());
                    }
                }

            }
        }
        return fileList;
    }

    public void getscreenshot(String url, final Interfaces.getscreenshot successscreenshot) {
        if (player != null) {
            millis = Gcmnotificationclass.player.getCurrentPosition();
            new getscreenshotasync(Gcmnotificationclass.this, millis, url, new Interfaces.getscreenshotmedia() {
                @Override
                public void getscreenshotwhilemedia(String success) {
                    if (success.equalsIgnoreCase("1")) {
                        successscreenshot.getsuccessscreenshot("1");
                    }
                }
            }).execute();


        } else {
            view.getRootView();
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                picDir = new File(Environment.getExternalStorageDirectory() + "/.Peacock/Peacock Images");
                if (!picDir.exists()) {
                    picDir.mkdirs();

                }
                view.setDrawingCacheEnabled(true);
                view.buildDrawingCache(true);
                Bitmap bitmap = view.getDrawingCache();
                String fileName = "Peacock" + title + ".png";
                picFile = new File(picDir + "/" + fileName);
                try {
                    picFile.createNewFile();
                    FileOutputStream picOut = new FileOutputStream(picFile);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), (int) (bitmap.getHeight() / 1.2));
                    boolean saved = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, picOut);
                    if (saved) {

                        successscreenshot.getsuccessscreenshot("1");

                    } else {
                        //Error
                    }
                    picOut.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                view.destroyDrawingCache();
            } else {
                //Error

            }

        }
    }

    private String getResponceForpreview(Map<String, String> param) {
        String response = "";
        try {


            String servelurl = Config.url;

            HttpURLConnection httpURLConnection = NetworkClass.openconnection(Config.url);
            if (httpURLConnection != null) {

                response = NetworkClass.fetchPostDatapreview(param);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return response;

    }

    public static class getscreenshotasync extends AsyncTask<Void, Void, Void> {

        public static ProgressDialog pdialog;
        int mil;
        String videourl;
        Context context;
        boolean saved;
        private Interfaces.getscreenshotmedia successscreenshot;

        public getscreenshotasync(Gcmnotificationclass cts, int millis, String url, Interfaces.getscreenshotmedia getscreenshotmedia) {
            mil = millis;
            videourl = url;
            successscreenshot = getscreenshotmedia;
            context = cts;
            Constant.fromnotificationclass = true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdialog = new ProgressDialog(context);
            pdialog.setMessage(ConstantStrings.readingcontacts);
            pdialog.setCancelable(false);
            pdialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            createVideoThumbnail(videourl, MediaStore.Images.Thumbnails.MINI_KIND, mil);
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                picDir = new File(Environment.getExternalStorageDirectory() + "/.Peacock/Peacock Images");
                if (!picDir.exists()) {
                    picDir.mkdirs();

                }


                String fileName = "Peacock" + "_" + title + ".png";
                picFile = new File(picDir + "/" + fileName);
                try {
                    picFile.createNewFile();
                    FileOutputStream picOut = new FileOutputStream(picFile);
                    saved = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, picOut);
                    picOut.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {

                //Error
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void response) {
            super.onPostExecute(response);

            if (saved) {
                successscreenshot.getscreenshotwhilemedia("1");
                Constant.mediaplayingshare = true;

            } else {
                //Error
                Constant.mediaplayingshare = false;

            }
            pdialog.dismiss();

        }
    }

    private class insertideostogallery extends AsyncTask<Map<String, String>, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String loading = getResources().getString(R.string.loading);
            pdialog = new ProgressDialog(Gcmnotificationclass.this);
            pdialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.loader_anim));
            pdialog.setMessage(Html.fromHtml(loading));
            pdialog.show();

        }

        @Override
        protected String doInBackground(Map<String, String>... params) {
            String response = "";
            try {
                if (!isCancelled()) {
                    String servelurl = Config.url;


                    HttpURLConnection httpURLConnection = NetworkClass.openconnection(servelurl);
                    if (httpURLConnection != null) {

                        response = NetworkClass.fetchPostData(params[0]);
                    }


                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            pdialog.dismiss();
            String status;
            String msg;


            if (response.equals("timeout")) {

                Toast.makeText(Gcmnotificationclass.this, ConstantStrings.servertimeout, Toast.LENGTH_SHORT).show();

            } else if (response.equals("0")) {
                Toast.makeText(Gcmnotificationclass.this, ConstantStrings.errorreadingserver, Toast.LENGTH_SHORT).show();


            } else {
                try {
                    JSONObject jobj1 = new JSONObject(response);
                    msg = jobj1.getString("msg_to_dis");

                    Toast.makeText(Gcmnotificationclass.this, msg, Toast.LENGTH_SHORT).show();


                    if (player != null && player.isPlaying()) {

                    } else {

                        Intent mainactivityintent = new Intent(Gcmnotificationclass.this, MainActivity.class);
                        startActivity(mainactivityintent);
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public class AllInOneAsyncTaskmore extends AsyncTask<Map<String, String>, String, String> {
        AsyncTaskPurpose asyncTaskPurpose;
        ArrayList<Map<String, String>> subreqsts;
        Context context;
        String error = "0", response = "not null";

        String Status;
        String reg_status;
        String charg_text;
        String charg_unit;
        String name;
        String description;
        String img_path;
        String node_type;
        String banners;
        String video_cats;

        String bannerid, act_url, imgpath;
        private String cat_flag;
        private String cat_msg;
        private String itemdetails;
        private String moredesc;


        public AllInOneAsyncTaskmore(
                Gcmnotificationclass mainservice) {
            // TODO Auto-generated constructor stub

            moreprogressdialog = new ProgressDialog(Gcmnotificationclass.this);

            moreprogressdialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.loader_anim));
            moreprogressdialog.setMessage(ConstantStrings.moredilaogtext);
            moreprogressdialog.setCancelable(false);
            moreprogressdialog.setIndeterminate(true);
            moreprogressdialog.show();

        }


        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(Map<String, String>... param) {
            // TODO Auto-generated method stub
            return getResponceForAll(param[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            if (result.equals("0")) {
                Toast.makeText(Gcmnotificationclass.this, ConstantStrings.errorreadingserver, Toast.LENGTH_SHORT).show();

            } else if (result.equals("timeout")) {
                Toast.makeText(Gcmnotificationclass.this, ConstantStrings.servertimeout, Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject jobj = new JSONObject(result);
                    String status = jobj.getString("status");
                    if (status.equals("0")) {
                        String link = jobj.getString("desc");
                        if (link.contains("google")) {
                            new JsonSearchTask().execute();
                        } else {
                            moreprogressdialog.dismiss();
                            Intent morein = new Intent(Gcmnotificationclass.this, MoreWeb.class);
                            morein.putExtra("more", link);
                            startActivity(morein);
                        }
                    } else if (status.equals("404")) {
                        Toast.makeText(Gcmnotificationclass.this, ConstantStrings.nointernetconn, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        }

        private String getResponceForAll(Map<String, String> param) {
            String response = "";
            try {
                if (!isCancelled()) {


                    String servelurl = Config.url;

                    HttpURLConnection httpURLConnection = NetworkClass.openconnection(servelurl);
                    if (httpURLConnection != null) {
                        response = NetworkClass.fetchPostData(param);

                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return response;

        }

    }

    public class AllInOneAsyncTaskconfirmation extends AsyncTask<Map<String, String>, String, String> {
        public String message;
        AsyncTaskPurpose asyncTaskPurpose;
        ArrayList<Map<String, String>> subreqsts;
        Context context;
        String error = "0", response = "not null";
        String Status;
        String reg_status;
        String charg_text;
        String charg_unit;
        String name;
        String description;
        String img_path;
        String node_type;
        String banners;
        String video_cats;
        String bannerid, act_url, imgpath;
        private String cat_flag;
        private String cat_msg;
        private String itemdetails;


        public AllInOneAsyncTaskconfirmation(
                Gcmnotificationclass mainservice) {
            // TODO Auto-generated constructor stub

            pdialog = new ProgressDialog(Gcmnotificationclass.this);
            pdialog.setMessage("Loading");
            pdialog.setCancelable(false);
            pdialog.setIndeterminate(true);
            pdialog.show();

        }


        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(Map<String, String>... param) {
            // TODO Auto-generated method stub
            return getResponceForconfirmation(param[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pdialog.dismiss();
            if (result.equals("0")) {
                Toast.makeText(Gcmnotificationclass.this, ConstantStrings.errorreadingserver, Toast.LENGTH_SHORT).show();
            } else if (result.equals("timeout")) {
                Toast.makeText(Gcmnotificationclass.this, ConstantStrings.servertimeout, Toast.LENGTH_SHORT).show();

            } else {
                try {
                    JSONObject obj = new JSONObject(result);
                    Status = obj.getString("status");
                    if (Status != null) {
                        message = obj.getString("msg_to_dis");
                        Toast.makeText(Gcmnotificationclass.this, message, Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }
        }

        private String getResponceForconfirmation(Map<String, String> param) {
            String response = "";
            try {
                if (!isCancelled()) {


                    String servelurl = Config.url;

                    HttpURLConnection httpURLConnection = NetworkClass.openconnection(servelurl);
                    if (httpURLConnection != null) {
                        response = NetworkClass.fetchPostData(param);

                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return response;

        }

    }

    private class JsonSearchTask extends AsyncTask<Void, Void, Void> {
        ArrayList<HashMap<String, String>> searchResultString = new ArrayList<HashMap<String, String>>();


        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }


        @Override
        protected Void doInBackground(Void... params) {
            try {


                String addition = null;

                if (title.length() >= 20) {
                    addition = title.substring(0, 19);
                } else {
                    addition = title;
                }
                String str = addition.replaceAll(" ", "%20");
                String SearchQuery1 = Config.GOOGLE_CUSTOM_SEARCH_PART1 + Config.GOOGLE_CUSTOM_SEARCH_DEVELOPER_KEY + Config.GOOGLE_CUSTOM_SEARCH_PART2 + str;


                searchResultString = ParseStringResult(sendQuery(SearchQuery1));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;


        }

        @Override
        protected void onPostExecute(Void aVoid) {
            moreprogressdialog.dismiss();
            super.onPostExecute(aVoid);

            if (searchResultString != null && searchResultString.size() > 0) {
                Intent morein = new Intent(Gcmnotificationclass.this, MoreDataList.class);
                morein.putExtra("more", category_contentList);
                morein.putExtra("list", videoplaylist);
                morein.putExtra("source_type", source_type);
                morein.putExtra("catname", catname);
                morein.putExtra("id", vid_id);
                morein.putExtra("chrg_unit", charg_unit);
                morein.putExtra("title", title);
                morein.putExtra("shareby", shareby);
                morein.putExtra("video_path", video_path);
                morein.putExtra("img_path", imageUrl);
                morein.putExtra("desc", description);
                morein.putExtra("amt", amount);
                morein.putExtra("act_flag", act_flag);
                morein.putExtra("act_msg", act_msg);
                morein.putExtra("comments", comments);
                morein.putExtra("lock_flag", lock_flag);

                startActivity(morein);

            } else {
                ALertDialog.Erroralert(Gcmnotificationclass.this, "No more data available against " + title + ". please search with a suitable title");

            }
        }

    }

    public class AllInOneAsyncTaskpreview extends AsyncTask<Map<String, String>, String, String> {
        AsyncTaskPurpose asyncTaskPurpose;
        ArrayList<Map<String, String>> subreqsts;
        Context context;
        String error = "0", response = "not null";

        String Status;
        String videopath;
        String preview_msg;
        private ProgressDialog progressDialog;
        private String preview_path;


        public AllInOneAsyncTaskpreview(
                Gcmnotificationclass mainservice) {
            context = mainservice;
            // TODO Auto-generated constructor stub
        }


        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            progressDialog = new ProgressDialog(context);

            String loading = getResources().getString(R.string.loading);
            progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.loader_anim));
            progressDialog.setMessage(Html.fromHtml(loading));
            progressDialog.setCancelable(true);
            progressDialog.show();


        }


        @Override
        protected String doInBackground(Map<String, String>... param) {
            // TODO Auto-generated method stub
            if (!isCancelled()) {
                return getResponceForpreview(param[0]);
            } else {
                return "cancel";
            }
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            if (result.equals("cancel")) {

            } else if (result.equals("0")) {
                Toast.makeText(Gcmnotificationclass.this, ConstantStrings.errorreadingserver, Toast.LENGTH_SHORT).show();
            } else if (result.equals("timeout")) {
                Toast.makeText(Gcmnotificationclass.this, ConstantStrings.servertimeout, Toast.LENGTH_SHORT).show();
            } else {

                JSONObject jsonObject1 = null;
                try {
                    jsonObject1 = new JSONObject(result);
                    String status = jsonObject1.getString("status");
                    if (status.equals("0")) {
                        preview_path = jsonObject1.getString("vid_path");
                        if (preview_path != null) {


                            Utils.downloadfiles(Gcmnotificationclass.this, preview_path, title, vid_id);
                        } else {

                            Toast.makeText(context, "No Preview Available.", Toast.LENGTH_SHORT).show();
                        }
                    } else if (status.equals("404")) {
                        String msg = jsonObject1.getString("message");

                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, ConstantStrings.errorreadingserver, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        @Override
        protected void onCancelled() {
            progressDialog.dismiss();

            super.onCancelled();
        }
    }

}

*/
