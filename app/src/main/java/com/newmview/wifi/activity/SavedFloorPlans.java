
package com.newmview.wifi.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
//import androidx.lifecycle.ViewModelProvider;

import android.view.View;
import android.widget.ImageView;

import com.mview.airtel.R;
import com.newmview.wifi.bean.MapModel;
import com.newmview.wifi.bean.SavedMapsModel;
import com.mview.airtel.databinding.ActivitySavedFloorPlansBinding;
//import com.mcpsinc.mview.helper.PicassoImageLoader;
import com.newmview.wifi.helper.PicassoImageLoader;
import com.newmview.wifi.other.Utils;
import com.newmview.wifi.viewmodel.MainViewModelFactory;
import com.newmview.wifi.viewmodel.MapVM;
import com.veinhorn.scrollgalleryview.MediaInfo;
//import com.veinhorn.scrollgalleryview.MediaInfo;

import java.util.ArrayList;
import java.util.List;

public class SavedFloorPlans extends AppCompatActivity implements View.OnClickListener {
    public static final String SEND_FLOOR_PLAN = "com.activity.SavedFloorPlans.Send_floor_plan";
    ActivitySavedFloorPlansBinding plansBinding;
    private ArrayList<SavedMapsModel> floorPlanList;
    private MapVM viewModel;
    private List<MapModel> mapList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        plansBinding = DataBindingUtil.setContentView(this, R.layout.activity_saved_floor_plans);
        init();


    }

    private void init() {
        setActionBar();
        viewModel = new ViewModelProvider(this, new MainViewModelFactory()).get(MapVM.class);
        plansBinding.setMapModel(new MapModel());
        viewModel.getMapListObservable().observe(this, new Observer<List<MapModel>>() {
            private List<MapModel> mapList;

            @Override
            public void onChanged(List<MapModel> mapList) {
                SavedFloorPlans.this.mapList = mapList;

                setTheViewsAccordingToImages(mapList);

            }
        });
        plansBinding.selectBtn.setOnClickListener(this);
    }

    private void setActionBar() {
        /*Toolbar toolbar = findViewById(R.id.toolbar);*/


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ColorDrawable colorDrawable
                = new ColorDrawable(getResources().getColor(R.color.white));

        // Set BackgroundDrawable
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_app_icon);


    }


/*
    private void readFloorPlans() {
       // File root = new File();
        File[] flist = Config.downloaded_root.listFiles();
        floorPlanList=new ArrayList<>();
        //listFile.clear();
        for (int i = 0; i < flist.length; i++) {
            SavedMapsModel savedMapsModel=new SavedMapsModel();
            File file = flist[i];
            String name=file.getName();
            if(Utils.checkifavailable(name))
            {
                if(name.contains("Floor_Plan_"))
                {
                    savedMapsModel.setName(name);
                    savedMapsModel.setAbsolutePath(file.getAbsolutePath());
                    savedMapsModel.setPath(file.getPath());
                    savedMapsModel.set
                    listFile.add(file.getPath());
                }
            }

        }
    }
*/


    private void setTheViewsAccordingToImages(List<MapModel> mapList) {
        if (mapList != null) {
            if (mapList.size() > 0) {
                plansBinding.noImageIV.setVisibility(View.GONE);
                plansBinding.viewsGalleryView.setVisibility(View.VISIBLE);
                plansBinding.selectBtn.setVisibility(View.VISIBLE);
                plansBinding.titleTv.setText(mapList.get(0).getFloorPlan());
                plansBinding.surveyIdTv.setText(mapList.get(0).getMapId());
                setGalleryView(mapList);
            } else {
                plansBinding.noImageIV.setVisibility(View.VISIBLE);
                plansBinding.viewsGalleryView.setVisibility(View.GONE);
                plansBinding.selectBtn.setVisibility(View.GONE);
            }
        } else {
            plansBinding.noImageIV.setVisibility(View.VISIBLE);
            plansBinding.viewsGalleryView.setVisibility(View.GONE);
            plansBinding.selectBtn.setVisibility(View.GONE);
        }

    }


    private void setGalleryView(List<MapModel> mapList) {
        if (mapList != null && mapList.size() > 0) {
            List<MediaInfo> infos = new ArrayList<>(mapList.size());

            for (int i = 0; i < mapList.size(); i++) {
                String url = mapList.get(i).getFloorPlanPath();
                if(i==0) {
                    plansBinding.titleTv.setText(mapList.get(0).getFloorPlan());
                    plansBinding.surveyIdTv.setText(mapList.get(0).getMapId());
                }
                System.out.println("path list is from 1  " + url);
                if (Utils.checkifavailable(url)) {
                    infos.add(new MediaInfo().mediaLoader(new PicassoImageLoader(url)));

                }
            }


            if (infos.size() > 0) {
                plansBinding.viewsGalleryView
                        .setThumbnailSize(200)
                        .setZoom(true)
                        .setFragmentManager(getSupportFragmentManager())
                        .addMedia(infos);
                plansBinding.viewsGalleryView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        plansBinding.titleTv.setText(mapList.get(position).getFloorPlan());
                        plansBinding.surveyIdTv.setText(mapList.get(position).getMapId());
                      View v=  plansBinding.viewsGalleryView.getChildAt(position);
                      if(v instanceof ImageView)
                      {
                          ((ImageView)v).setColorFilter(getResources().getColor(R.color.red));
                      }
                    }

                    @Override
                    public void onPageSelected(int position) {
plansBinding.titleTv.setText(mapList.get(position).getFloorPlan());
plansBinding.surveyIdTv.setText(mapList.get(position).getMapId());
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

/*

setListeners();
                alreadySetBefore=true;*/

            } else {
/*

 alreadySetBefore=false;
                Utils.showsncakbar(UpdateViews.this,"No default view available!");*/

            }


        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
           case R.id.selectBtn:
               int currentItem=plansBinding.viewsGalleryView.getCurrentItem();
               Intent intent=new Intent(SEND_FLOOR_PLAN);
               intent.putExtra("path",mapList.get(currentItem).getFloorPlanPath());
               intent.putExtra("wifiX",mapList.get(currentItem).getWifiX());
               intent.putExtra("wifiY",mapList.get(currentItem).getWifiY());
               LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
               finish();
               break;

        }
    }
}
