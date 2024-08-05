package com.newmview.wifi.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mview.airtel.R;
import com.newmview.wifi.activity.BuildingTabActivity;
import com.newmview.wifi.activity.WifiActivity;
import com.newmview.wifi.adapter.MapGridListAdapter;
import com.newmview.wifi.bean.MapModel;
import com.newmview.wifi.customview.CustomGridLayoutManager;
import com.mview.airtel.databinding.FragmentBuildingSecondTabBinding;
import com.newmview.wifi.helper.Interfaces;
import com.newmview.wifi.other.Utils;
import com.newmview.wifi.viewmodel.MainViewModelFactory;
import com.newmview.wifi.viewmodel.MapVM;

import java.util.List;

import static android.view.View.GONE;

public class BuildingSecondFragmentTab extends Fragment implements View.OnClickListener , Interfaces.EventListener{
    private static final String TAG = "BuildingSecondFragmentTab";
    private FragmentBuildingSecondTabBinding dataBinding;
    private boolean isAllFabsVisible;
    private MapVM viewModel;
    private MapGridListAdapter mapGridListAdapter;
    private Bundle bundle;
    private String path,flatType,opening,openingType,componentType;
    private String wifiCoordsX,wifiCoordsY;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        //StrictMode.setVmPolicy(builder.build());
        dataBinding= DataBindingUtil.inflate(inflater, R.layout.fragment_building_second_tab, container,
                false);
        getBundleExtras();
        viewModel=new ViewModelProvider(this,new MainViewModelFactory()).get(MapVM.class);
        dataBinding.setMapModel(new MapModel());
        viewModel.getMapListObservable().observe(getActivity(), new Observer<List<MapModel>>() {
            private List<MapModel> mapList;

            @Override
            public void onChanged(List<MapModel> mapList) {
                    this.mapList=mapList;
                setAdapters(mapList);
            }
        });
     //   dataBinding.se
        init();
       setClickListeners();

        return dataBinding.getRoot();
    }

    private void getBundleExtras() {
        bundle = getArguments();
        if(bundle!=null) {
            path = bundle.getString("floorMapPath");
            flatType = bundle.getString("flatType");
            componentType = bundle.getString("componentType");
            opening = bundle.getString("opening");
            openingType = bundle.getString("openingType");
            wifiCoordsX=bundle.getString("wifiCoordsX");
            wifiCoordsY=bundle.getString("wifiCoordsY");
            Log.i(TAG,"Coord x "+wifiCoordsX +"coordy "+wifiCoordsY);
        }


    }

    private void setAdapters(List<MapModel> mapList) {
        CustomGridLayoutManager gridLayoutManager = new CustomGridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);
        dataBinding.mapListRv.setLayoutManager(gridLayoutManager);
        dataBinding.mapListRv.setHasFixedSize(true);
        mapGridListAdapter =new MapGridListAdapter(getActivity(),mapList,this);
        dataBinding.mapListRv.setAdapter(mapGridListAdapter);
    }

    private void setClickListeners() {
        dataBinding.fab.setOnClickListener(this);
        dataBinding.indoorSurveyFab.setOnClickListener(this);
    }
private void init()
{
    // make the boolean variable as false, as all the
    // action name texts and all the sub FABs are
    // invisible
    isAllFabsVisible = false;

    // Set the Extended floating action button to
    // shrinked state initially
    dataBinding.fab.shrink();
    hideOtherButtons();
    if(getActivity() instanceof BuildingTabActivity) {
        getActivity().findViewById(R.id.compareBtn).setVisibility(GONE);

    }
}
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
       /* demoCollectionAdapter = new DemoCollectionAdapter(this);
        viewPager = view.findViewById(R.id.pager);
        viewPager.setAdapter(demoCollectionAdapter);*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.fab:
                openOrCloseMenu();
                break;
            case R.id.indoorSurveyFab:
                hideOtherButtons();
                Utils.takeToNextActivity(getActivity(), WifiActivity.class,null);

                break;

        }
    }

    private void openOrCloseMenu() {
        // when isAllFabsVisible becomes
        // true make all the action name
        // texts and FABs VISIBLE.
        if(!isAllFabsVisible)
        {
       // dataBinding.speedTestFab.show();
        dataBinding.indoorSurveyFab.show();
        //dataBinding.speedTestTv.setVisibility(GONE);
        dataBinding.indoorSurveyTv.setVisibility(View.VISIBLE);

        // Now extend the parent FAB, as
        // user clicks on the shrinked
        // parent FAB
        dataBinding.fab.extend();

        // make the boolean variable true as
        // we have set the sub FABs
        // visibility to GONE
        isAllFabsVisible = true;
    } else {

        // when isAllFabsVisible becomes
        // true make all the action name
        // texts and FABs GONE.
            hideOtherButtons();

        // Set the FAB to shrink after user
        // closes all the sub FABs
        dataBinding.fab.shrink();

        // make the boolean variable false
        // as we have set the sub FABs
        // visibility to GONE
        isAllFabsVisible = false;
    }

}

    private void hideOtherButtons() {
    //    dataBinding.speedTestFab.hide();
        dataBinding.indoorSurveyFab.hide();
       // dataBinding.speedTestTv.setVisibility(GONE);
        dataBinding.indoorSurveyTv.setVisibility(GONE);

    }

    public void refresh(Bundle bundle) {
        //this.bundle=bundle;
        viewModel.refresh();
       // mapListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRemovePlanEvent(String id) {
       boolean successfullResult= viewModel.removePlan(id);
       if(successfullResult) {
           Utils.showToast(getActivity(), "Plan deleted successfully!");
       }
       else
       {
           Utils.showToast(getActivity(), "Unable to delete plan!");
       }
    }
}
