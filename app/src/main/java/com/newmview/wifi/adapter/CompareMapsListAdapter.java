package com.newmview.wifi.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

//import com.newmview.wifi.BR;
import com.mview.airtel.R;
import com.newmview.wifi.activity.CreateWalkMapActivity;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.bean.MapModel;
import com.mview.airtel.databinding.CompareMapDetailsBinding;
import com.newmview.wifi.fragment.CompareHeatMapFragment;
import com.newmview.wifi.fragment.ImageFragmentDialog;
import com.newmview.wifi.fragment.ImageViewFragment;
import com.newmview.wifi.other.Utils;

import java.util.List;

public class CompareMapsListAdapter extends RecyclerView.Adapter<CompareMapsListAdapter.MapVh> {
    private static final String TAG = "CompareMapsListAdapter";

    private List<MapModel> mapList;
    private Context mContext;
    private ImageViewFragment heatMapImageFragment;
    private CompareHeatMapFragment compareHmFragment;
    private String source;

    public CompareMapsListAdapter(Context context, List<MapModel> mapList, String source) {
        mContext = context;
        this.mapList = mapList;
        this.source=source;

    }

    @NonNull
    @Override
    public MapVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CompareMapDetailsBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.compare_map_details, parent, false);


        return new MapVh(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MapVh holder, int position) {
        MapModel dataModel = mapList.get(position);
        //      Log.i(TAG,"Wifi Names "+dataModel.getSsidName());
        String walkPathMap = dataModel.getMapPath();

        holder.bind(dataModel);


    }

    @Override
    public int getItemCount() {
        return mapList.size();
    }

    public class MapVh extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView wifiTv;
        public CompareMapDetailsBinding binding;
        public TableLayout mapTable;


        public MapVh(CompareMapDetailsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.walkMapIv.setOnClickListener(this);
            this.binding.heatMapIv.setOnClickListener(this);
            this.binding.lsheatMapIv.setOnClickListener(this);


        }

        public void bind(Object obj) {
//            binding.setVariable(BR.viewModel, obj);
            //binding.setVariable(BR.viewMod.l, obj);
            binding.executePendingBindings();
        }

        @Override
        public void onClick(View v) {
            int pos=getAdapterPosition();
            switch (v.getId()) {
                case R.id.walkMapIv:
                    openRespectiveImage("walkMap",mapList.get(pos));
                    break;
                case R.id.heatMapIv:
                    openRespectiveImage("HeatMap", mapList.get(pos));
                    /*if(source.equalsIgnoreCase("ls_heatmap_mini")) {
                        openRespectiveImage("ls_HeatMap", mapList.get(pos));
                    }
                    else
                    {
                        openRespectiveImage("HeatMap", mapList.get(pos));

                    }*/
                    break;

                case R.id.lsheatMapIv:
                    openRespectiveImage("ls_HeatMap", mapList.get(pos));
                    break;
            }
        }

        private void performClickOperation(View v) {
            int pos = getAdapterPosition();
            String finalmapPath = mapList.get(pos).getFinalMapPath();
            String mapId = mapList.get(pos).getMapId();
            String floorPlanPath = mapList.get(pos).getFloorPlanPath();
           // Utils.appendCrashLog("final map path " + finalmapPath + " mapid " + mapId + " floor plan path " + floorPlanPath);
            switch (v.getId()) {
                case R.id.floorPlan:
                    // openFloorPlan(mapList.get(pos));

                    openRespectiveImage("FloorPlan", mapList.get(pos));
                    break;
                case R.id.mapTv:
                    Log.i(TAG, "Final map path " + finalmapPath);
                    try {
                        if (Utils.checkifavailable(finalmapPath)) {
                        //    openCompareHMFragment(mapList.get(pos));

                            //openRespectiveImage("HeatMap", mapList.get(pos));

                        } else {
                            openHeatMapActivity(mapId, floorPlanPath);
                        }
                    } catch (Exception e) {
                      //  Utils.appendCrashLog("Crash while opening heat map " + e.toString());
                        e.printStackTrace();
                    }
                    break;
                case R.id.walkMap:

                    try {


                        openRespectiveImage("WalkMap", mapList.get(pos));
                    } catch (Exception e) {
                       // Utils.appendCrashLog("Crash while opening walk map " + e.toString());
                        e.printStackTrace();
                    }
                    break;


            }
        }

        private void openHeatMapActivity(String mapId, String floorPlanPath) {
            Intent intent = new Intent(mContext, CreateWalkMapActivity.class);
            intent.putExtra("mapId", mapId);
            intent.putExtra("floorMapPath", floorPlanPath);


            mContext.startActivity(intent);
        }


    }

    private void openRespectiveImage(String source, MapModel mapDetails) {
       /* heatMapImageFragment = new ImageViewFragment().newInstance(mContext, mapDetails, source);
        ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction().
                replace(R.id.mainRL, heatMapImageFragment).addToBackStack(null).commit();*/
        Bundle bundle=new Bundle();
        if (Utils.checkifavailable(source)) {
            String textToShare=
                    "SSID : "+mapDetails.getSsidName() +", \n"+"Time Stamp : "+Utils.getDateTime()+", \n"+"Survey Id : "+mapDetails.getMapId()
                            +", \n"+"User Id : "+Utils.getMyContactNum(MviewApplication.ctx);

            if (source.equalsIgnoreCase("FloorPlan")) {
                bundle.putString("path", mapDetails.getFloorPlanPath());
                bundle.putString("source",source);

            }

            else if(source.equalsIgnoreCase("WalkMap"))
            {


                bundle.putString("path",mapDetails.getMapPath());
                bundle.putString("source",source);
                bundle.putString("textToShare",textToShare);
            }
            else if(source.equalsIgnoreCase("WalkMapMini"))
            {

                bundle.putString("path",mapDetails.getMapPath());
                bundle.putString("source",source);
            }

            else if(source.equalsIgnoreCase("HeatMapMini")||source.equalsIgnoreCase("HeatMap"))
            {
                bundle.putString("path",mapDetails.getFinalMapPath());
                bundle.putString("source",source);
            }
            else if(source.toLowerCase().contains("ls_heatmap".toLowerCase())){
                bundle.putString("path", mapDetails.getLsHeatMapPath());
                bundle.putString("source",source);
            }
            bundle.putString("wifiCoordsX",mapDetails.getWifiX());
            bundle.putString("wifiCoordsY",mapDetails.getWifiY());
            bundle.putSerializable("mapDetails",mapDetails);
        }


        ImageFragmentDialog imageFragmentDialog=new ImageFragmentDialog();
        imageFragmentDialog.setArguments(bundle);
        imageFragmentDialog.show(((AppCompatActivity)mContext).getSupportFragmentManager(), ImageFragmentDialog.TAG);
    }

    private void openCompareHMFragment(MapModel mapDetails) {
        compareHmFragment = new CompareHeatMapFragment().newInstance(mapDetails, "");
        ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction().
                replace(R.id.mainRL, compareHmFragment).addToBackStack(null).commit();
    }

    private void openFloorPlan(MapModel mapDetails) {
        heatMapImageFragment = new ImageViewFragment().newInstance(mContext, mapDetails, "FloorPlan");
        ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction().
                replace(R.id.mainRL, heatMapImageFragment).addToBackStack(null).commit();
    }
}