package com.newmview.wifi.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mview.airtel.R;
import com.newmview.wifi.activity.MainActivity;
import com.newmview.wifi.other.Config;
import com.newmview.wifi.other.Constants;

/**
 * Created by functionapps on 10/30/2018.
 */

public class SpeedTestOptions extends Fragment implements View.OnClickListener {

    private View view;
    TextView  webspeed, videospeed, dwnldspeed, uploadspeed;
    ImageView web,video,downlink,uplink;
    LinearLayout weblayout,videolayout,dwnldlayout,uploadlayout;
    private Webtest_fragment webtest_fragment;
    private FragmentManager fragmentManager;
    private VideoSpeedtest_fragment videospeedtest_fragment;
    private Dwnld_upload_fragment dwnld_upload_fragment;
    private String msgshow;
    private int msg;
    private Context context;
    private Bundle args;
    private String issue;
    private Bundle bundle;


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.speedtestoptions,container,false);
        init();

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_new);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Config.onbackpress.equalsIgnoreCase("speedtest")) {
                    getConfirmationFromUser();
                }
                else
                {
                    getActivity().onBackPressed();
                }

            }
        });
        weblayout.setOnClickListener(this);
        videolayout.setOnClickListener(this);
        dwnldlayout.setOnClickListener(this);
        uploadlayout.setOnClickListener(this);
        return view;
    }

    private void getConfirmationFromUser() {
if(getActivity()!=null) {
    final AlertDialog.Builder alertdialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
    alertdialog.setMessage(Constants.USER_CONFIRMATION).
            setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getActivity().onBackPressed();

                }
            })
            .setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
}

    }

    private void init() {
        webspeed=(TextView)view.findViewById(R.id.webspeed);
        videospeed=(TextView)view.findViewById(R.id.videospeed);
        dwnldspeed=(TextView)view.findViewById(R.id.download);
        uploadspeed=(TextView)view.findViewById(R.id.upload);
        web=(ImageView)view.findViewById(R.id.web);
        video=(ImageView)view.findViewById(R.id.video);
        downlink=(ImageView)view.findViewById(R.id.downlink);
        uplink=(ImageView)view.findViewById(R.id.uplink);
        weblayout=(LinearLayout)view.findViewById(R.id.web_layout);
        videolayout=(LinearLayout)view.findViewById(R.id.video_layout);
        dwnldlayout=(LinearLayout)view.findViewById(R.id.download_layout);
        uploadlayout=(LinearLayout)view.findViewById(R.id.upload_layout);
        fragmentManager=getActivity().getSupportFragmentManager();
        args=getArguments();
        bundle=new Bundle();
        if(args!=null) {
            issue = args.getString("type");
            if(issue!=null)
            bundle.putString("type", issue);
        }



    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.web_layout:
                webtest_fragment= new Webtest_fragment();
                fragmentManager.beginTransaction().replace(R.id.frame,webtest_fragment).addToBackStack("speed_test").commit();

                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.webtest_title);
                    if(bundle!=null)
                    webtest_fragment.setArguments(bundle);

                Config.onbackpress="speedtest";
                break;
            case R.id.video_layout:
                videospeedtest_fragment=new VideoSpeedtest_fragment();
                fragmentManager.beginTransaction().replace(R.id.frame,videospeedtest_fragment).addToBackStack("speed_test").commit();
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.videotest_title);
                Config.onbackpress="speedtest";
                if(bundle!=null)
                    videospeedtest_fragment.setArguments(bundle);
                //getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                break;
            case R.id.download_layout:
                dwnld_upload_fragment =new Dwnld_upload_fragment();
                MainActivity.ud="download";
                fragmentManager.beginTransaction().replace(R.id.frame,dwnld_upload_fragment).addToBackStack("speed_test").commit();

                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.download);
                Config.onbackpress="speedtest";
                if(bundle!=null)
                    dwnld_upload_fragment.setArguments(bundle);
                break;
            case R.id.upload_layout:
                dwnld_upload_fragment =new Dwnld_upload_fragment();
                MainActivity.ud="upload";
                fragmentManager.beginTransaction().replace(R.id.frame,dwnld_upload_fragment).addToBackStack("speed_test").commit();

              //  fragmentManager.beginTransaction().replace(R.id.frame,dwnld_upload_fragment).commit();
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.upload);
                Config.onbackpress="speedtest";
                if(bundle!=null)
                    dwnld_upload_fragment.setArguments(bundle);
                break;
        }

    }

 /*   public void sendMessageFn(int i, String s) {
        this.msg=i;
        this.msgshow=s;

        if(dwnld_upload_fragment!=null)
        {
            ((Dwnld_upload_fragment)dwnld_upload_fragment).sendMessageToActivity(msg,msgshow);
        }
        else
        {
            System.out.println("calling send msg from speed test options");
           dwnld_upload_fragment=new Dwnld_upload_fragment();
      //      ((Dwnld_upload_fragment)dwnld_upload_fragment).sendMessageToActivity(msg,msgshow);
            ((Dwnld_upload_fragment)dwnld_upload_fragment).testing();

        }
    }*/
}
