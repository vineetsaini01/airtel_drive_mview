package com.newmview.wifi.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mview.airtel.R;
import com.newmview.wifi.activity.BuildingTabActivity;
import com.newmview.wifi.activity.MainActivity;
import com.mview.airtel.databinding.FragmentSpeedTestBinding;
import com.newmview.wifi.other.Config;
import com.newmview.wifi.other.Utils;


public class SpeedTestFragment extends Fragment implements View.OnClickListener{

    private FragmentSpeedTestBinding speedTestBinding;
    private Webtest_fragment webtest_fragment;
    private FragmentManager fragmentManager;
    private VideoSpeedtest_fragment videospeedtest_fragment;
    private Dwnld_upload_fragment dwnld_upload_fragment;
    private Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        speedTestBinding= DataBindingUtil.inflate(inflater, R.layout.fragment_speed_test, container, false);
        fragmentManager=getActivity().getSupportFragmentManager();
        setClickListeners();
        return speedTestBinding.getRoot();

    }
    private void setClickListeners() {
        speedTestBinding.uploadTest.setOnClickListener(this);
        speedTestBinding.downlaodTest.setOnClickListener(this);
        speedTestBinding.videoStreamTest.setOnClickListener(this);
        speedTestBinding.webSpeedTest.setOnClickListener(this);
    }
    private void openBuildingTabPage() {
        Utils.takeToNextActivity(getActivity(), BuildingTabActivity.class,null);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.uploadTest:
                dwnld_upload_fragment =new Dwnld_upload_fragment();
                MainActivity.ud="upload";
                fragmentManager.beginTransaction().replace(R.id.frame,dwnld_upload_fragment).addToBackStack("speed_test").commit();
                //  fragmentManager.beginTransaction().replace(R.id.frame,dwnld_upload_fragment).commit();
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.upload);
                Config.onbackpress="speedtest";
                if(bundle!=null)
                    dwnld_upload_fragment.setArguments(bundle);
                break;

            case R.id.downlaodTest:
                dwnld_upload_fragment =new Dwnld_upload_fragment();
                System.out.println(" Output is");
                MainActivity.ud="download";
                fragmentManager.beginTransaction().replace(R.id.frame,dwnld_upload_fragment).addToBackStack("speed_test").commit();
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Speed test");
                Config.onbackpress="speedtest";
                Config.onbackpress="speedtest";
                if(bundle!=null)
                    dwnld_upload_fragment.setArguments(bundle);

                break;
            case R.id.videoStreamTest:
                videospeedtest_fragment=new VideoSpeedtest_fragment();
                fragmentManager.beginTransaction().replace(R.id.frame,videospeedtest_fragment).addToBackStack("speed_test").commit();
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.videotest_title);
                Config.onbackpress="speedtest";
                if(bundle!=null)
                    videospeedtest_fragment.setArguments(bundle);
                //getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                break;
            case R.id.webSpeedTest:
                webtest_fragment= new Webtest_fragment();
                fragmentManager.beginTransaction().replace(R.id.frame,webtest_fragment).addToBackStack("speed_test").commit();

                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.webtest_title);
                if(bundle!=null)
                    webtest_fragment.setArguments(bundle);

                Config.onbackpress="speedtest";
                break;
        }
    }
}