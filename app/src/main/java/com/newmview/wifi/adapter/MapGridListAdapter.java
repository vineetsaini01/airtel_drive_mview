package com.newmview.wifi.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.newmview.wifi.bean.MapModel;
import com.mview.airtel.databinding.MapDetailsBinding;
import com.newmview.wifi.fragment.CompareHeatMapFragment;
import com.newmview.wifi.fragment.ImageViewFragment;
import com.newmview.wifi.helper.Interfaces;
import com.newmview.wifi.other.CommonAlertDialog;
import com.newmview.wifi.other.Utils;

import java.util.List;

public class MapGridListAdapter extends RecyclerView.Adapter<MapGridListAdapter.MapVh> {
    private static final String TAG ="MapListAdapter" ;

    private  List<MapModel> mapList;
    private Context mContext;
    private ImageViewFragment heatMapImageFragment;
    private CompareHeatMapFragment compareHmFragment;
    Interfaces.EventListener listener;


    public MapGridListAdapter(Context context, List<MapModel> mapList, Interfaces.EventListener listener) {
        mContext=context;
        this.mapList=mapList;
        this.listener=listener;

    }

    @NonNull
    @Override
    public MapVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MapDetailsBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.map_details, parent, false);


        return new MapGridListAdapter.MapVh(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MapVh holder, int position) {
        MapModel dataModel = mapList.get(position);
  //      Log.i(TAG,"Wifi Names "+dataModel.getSsidName());
        String walkPathMap=dataModel.getMapPath();

        holder.bind(dataModel);




    }

    @Override
    public int getItemCount() {
        return mapList.size();
    }

    public   class MapVh extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView wifiTv;
        public MapDetailsBinding binding;
        public TableLayout mapTable;


        public MapVh(MapDetailsBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
           binding.actionBtn.setOnClickListener(this);

        }
        public void bind(Object obj) {
//          binding.setVariable(BR.viewModel,obj);
            //binding.setVariable(BR.viewMod.l, obj);
            binding.executePendingBindings();
        }

        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.actionBtn:
                    boolean showLinkSpeedBtn=false;
                    String lsPath=mapList.get(getAdapterPosition()).getLsHeatMapPath();
                    if(Utils.checkifavailable(lsPath))
                    {
                        showLinkSpeedBtn = true;
                    }

                    CommonAlertDialog.showCustomPopup(
                            v,showLinkSpeedBtn,R.layout.map_action_layout,mContext,
                            new Interfaces.PopupButtonClickListener() {
                        @Override
                        public void onButtonClicked(View v) {
                         performClickOperation(v,listener);
                        }
                    });
                    break;
            }
        }

        private void performClickOperation(View v, Interfaces.EventListener listener) {
            int pos=getAdapterPosition();
            String finalmapPath=mapList.get(pos).getFinalMapPath();
            String linkSpeedMapPath=mapList.get(pos).getLsHeatMapPath();
            String mapId=mapList.get(pos).getMapId();
            String floorPlanPath=mapList.get(pos).getFloorPlanPath();
            int warningIgonred=mapList.get(pos).getWalkMapWarningIgnored();

            //Utils.appendCrashLog("final map path "+finalmapPath +" mapid "+mapId +" floor plan path "+floorPlanPath);
            switch (v.getId())
            {

                case R.id.floorPlan:
                   // openFloorPlan(mapList.get(pos));

                    openRespectiveImage("FloorPlan",mapList.get(pos));
                    break;
                case R.id.ls_mapTv:
                    try {
                        Log.i(TAG,"Link speed heat map path "+linkSpeedMapPath);
                        if (Utils.checkifavailable(linkSpeedMapPath)) {
                            openCompareHMFragment(mapList.get(pos),"ls_heatmap");

                            //openRespectiveImage("HeatMap", mapList.get(pos));

                        } /*else {
                            openHeatMapActivity(mapId, floorPlanPath);
                        }*/
                    }
                    catch (Exception e)
                    {
                        //Utils.appendCrashLog("Crash while opening heat map "+e.toString());
                        e.printStackTrace();
                    }
                    break;

                case R.id.mapTv:
                    Log.i(TAG,"Final map path "+finalmapPath);
                    try {
                        if(warningIgonred==1)
                        {
                            openCompareHMFragment(mapList.get(pos), "heatmap");


                        }
                        else {
                            if (Utils.checkifavailable(finalmapPath)) {
                                openCompareHMFragment(mapList.get(pos), "heatmap");

                                //openRespectiveImage("HeatMap", mapList.get(pos));

                            } else {
                                openHeatMapActivity(mapId, floorPlanPath);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        //Utils.appendCrashLog("Crash while opening heat map "+e.toString());
                        e.printStackTrace();
                    }
                    break;
                case R.id.walkMap:

try {


    openRespectiveImage("WalkMap", mapList.get(pos));
}
catch (Exception e)
{
    //Utils.appendCrashLog("Crash while opening walk map "+e.toString());
    e.printStackTrace();
}
                    break;
                case R.id.deletePlan:
                    listener.onRemovePlanEvent(mapList.get(pos).getMapId());


                    break;






            }
        }

        private void openHeatMapActivity(String mapId, String floorPlanPath) {
            Intent intent=new Intent(mContext, CreateWalkMapActivity.class);
            intent.putExtra("mapId",mapId);
            intent.putExtra("floorMapPath",floorPlanPath);


            mContext.startActivity(intent);
        }



    }
 private void openRespectiveImage(String source, MapModel mapDetails)
 {
     heatMapImageFragment=new ImageViewFragment().newInstance(mContext,mapDetails,source);
     ((AppCompatActivity)mContext).getSupportFragmentManager().beginTransaction().
             replace(R.id.mainRL, heatMapImageFragment).addToBackStack(null).commit();
 }

    private void openCompareHMFragment(MapModel mapDetails, String source) {
        compareHmFragment=new CompareHeatMapFragment().newInstance(mapDetails,source);
        ((AppCompatActivity)mContext).getSupportFragmentManager().beginTransaction().
                replace(R.id.mainRL, compareHmFragment).addToBackStack(null).commit();
    }
    private void openFloorPlan(MapModel mapDetails) {
        heatMapImageFragment=new ImageViewFragment().newInstance(mContext,mapDetails,"FloorPlan");
        ((AppCompatActivity)mContext).getSupportFragmentManager().beginTransaction().
                replace(R.id.mainRL, heatMapImageFragment).addToBackStack(null).commit();
    }

}
