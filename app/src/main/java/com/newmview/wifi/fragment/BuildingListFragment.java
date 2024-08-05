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
//import com.newmview.wifi.adapter.MapLinearListAdapter;
import com.newmview.wifi.bean.MapModel;
import com.mview.airtel.databinding.FragmentListBuildingBinding;
import com.newmview.wifi.helper.Interfaces;
import com.newmview.wifi.other.Utils;
import com.newmview.wifi.viewmodel.MainViewModelFactory;
import com.newmview.wifi.viewmodel.MapVM;

import java.util.List;

import static android.view.View.GONE;

public class BuildingListFragment extends Fragment implements Interfaces.EventListener {
    private FragmentListBuildingBinding listBuildingBinding;
    private MapVM viewModel;
    private static final String TAG = "BuildingListFragment";
    private Bundle bundle;
    private String path, flatType, opening, openingType, componentType;
    private String wifiCoordsX, wifiCoordsY;
    //private MapLinearListAdapter mapListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       // StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        //StrictMode.setVmPolicy(builder.build());

        getBundleExtras();
        listBuildingBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_list_building, container,
                false);
        init();
        setClickListeners();
        return listBuildingBinding.getRoot();
    }

    private void init() {

        viewModel = new ViewModelProvider(this, new MainViewModelFactory()).get(MapVM.class);
        listBuildingBinding.setMapModel(new MapModel());
        viewModel.getMapListObservable().observe(getActivity(), new Observer<List<MapModel>>() {
            private List<MapModel> mapList;

            @Override
            public void onChanged(List<MapModel> mapList) {
                this.mapList = mapList;
                setAdapters(mapList);
            }
        });
        if (getActivity() instanceof BuildingTabActivity) {
            getActivity().findViewById(R.id.compareBtn).setVisibility(GONE);

        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void getBundleExtras() {
        bundle = getArguments();
        if (bundle != null) {
            path = bundle.getString("floorMapPath");
            flatType = bundle.getString("flatType");
            componentType = bundle.getString("componentType");
            opening = bundle.getString("opening");
            openingType = bundle.getString("openingType");
            wifiCoordsX = bundle.getString("wifiCoordsX");
            wifiCoordsY = bundle.getString("wifiCoordsY");
            Log.i(TAG, "Coord x " + wifiCoordsX + "coordy " + wifiCoordsY);
        }

    }

    private void setAdapters(List<MapModel> mapList) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        listBuildingBinding.mapListRv.setLayoutManager(layoutManager);
        listBuildingBinding.mapListRv.setHasFixedSize(true);
        //mapListAdapter = new MapLinearListAdapter(getActivity(), mapList, this);
        //listBuildingBinding.mapListRv.setAdapter(mapListAdapter);
    }

    private void setClickListeners() {
       /* listBuildingBinding.fab.setOnClickListener(this);
        listBuildingBinding.indoorSurveyFab.setOnClickListener(this);*/
    }

    @Override
    public void onRemovePlanEvent(String id) {
        boolean successfullResult = viewModel.removePlan(id);
        if (successfullResult) {
            Utils.showToast(getActivity(), "Plan deleted successfully!");
        } else {
            Utils.showToast(getActivity(), "Unable to delete plan!");
        }
    }


}
