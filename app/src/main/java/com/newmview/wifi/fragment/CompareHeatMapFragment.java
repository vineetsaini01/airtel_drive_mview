package com.newmview.wifi.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.mview.airtel.R;
import com.newmview.wifi.bean.MapModel;
import com.mview.airtel.databinding.FragmentCompareHeatmapBinding;
import com.newmview.wifi.other.Utils;
import com.newmview.wifi.viewmodel.ImageViewModel;

public class CompareHeatMapFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "CompareHeatMapFragment";
    FragmentCompareHeatmapBinding compareHeatmapBinding;
    private FragmentManager fragmentManager;
    private ImageViewFragment walkMapFragment;
    private MapModel mapDetails;
    private ImageViewFragment heatMapFragment;
    private ImageViewFragment imageFragment;
    private ImageViewModel viewModel;
    private String source;
    private ToggleButton toggleButton;
    private CompareHeatMapFragment compareHmFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBundleExtras();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
       /* if(getActivity()!=null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Signal Strength HeatMap");
        }
        implementToggleButton(menu,inflater);
        super.onCreateOptionsMenu(menu, inflater);*/
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        compareHeatmapBinding= DataBindingUtil.inflate(inflater, R.layout.fragment_compare_heatmap, container,
                false);
        init();
        replaceFragments();
        setClickListeners();
        return compareHeatmapBinding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(ImageViewModel.class);
      //  viewModel.setSource("HeatMap");
        viewModel.childFragmentClicked().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String source) {
                if(Utils.checkifavailable(source))
                {
                    openRespectiveImage(source,mapDetails);
                }

            }
        });

    }
    private void implementToggleButton(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.settings_service, menu);
        MenuItem itemswitch = menu.findItem(R.id.switch_action_bar);
        itemswitch.setActionView(R.layout.use_switch);
        toggleButton = menu.findItem(R.id.switch_action_bar).getActionView().findViewById(R.id.switch2);
        toggleButton.setChecked(true);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                     if(buttonView.isPressed()) {
                if (toggleButton.isChecked()) {
                    Log.i(TAG,"Toggle button check true");
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Signal Strength HeatMap");
                    int count=  ((AppCompatActivity)getActivity()).getSupportFragmentManager().getBackStackEntryCount();
                    if(count>0)
                    {
                        ((AppCompatActivity)getActivity()). getSupportFragmentManager().popBackStackImmediate();
                    }
                }
                else {
                    Log.i(TAG,"Toggle button check false");
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Link Speed HeatMap");
                    openCompareHMFragment(mapDetails,"ls_heatmap");
                }
            }

                    }

        });

    }
    private void openCompareHMFragment(MapModel mapDetails, String source) {
        compareHmFragment=new CompareHeatMapFragment().newInstance(mapDetails,source);
        ((AppCompatActivity)getActivity()).getSupportFragmentManager().beginTransaction().
                replace(R.id.mainRL, compareHmFragment).addToBackStack(null).commit();
    }
    private void setClickListeners() {
        compareHeatmapBinding.heatMapFl.setOnClickListener(this);
        compareHeatmapBinding.walkMapFL.setOnClickListener(this);
    }

    private void init() {
        if(getActivity()!=null) {
            fragmentManager = getActivity().getSupportFragmentManager();
        }
    }

    private void getBundleExtras() {
        Bundle bundle=getArguments();
        if(bundle!=null)
        {
            mapDetails=(MapModel)bundle.getSerializable("mapDetails");
            source=bundle.getString("source");
            Log.i(TAG,"Map details are "+mapDetails);
        }
    }
    private void replaceFragments()
    {
        if(fragmentManager!=null)
        {
            walkMapFragment=new ImageViewFragment().newInstance(getActivity(),mapDetails,"WalkMapMini");
            fragmentManager.beginTransaction().
                    add(R.id.walkMapFL, walkMapFragment).commit();
            if(Utils.checkifavailable(source))
            {
                if(source.equalsIgnoreCase("ls_heatmap"))
                {
                    heatMapFragment=new ImageViewFragment().newInstance(getActivity(),mapDetails,"ls_heatmap_mini");
                }
                else
                {
                    heatMapFragment=new ImageViewFragment().newInstance(getActivity(),mapDetails,"HeatMapMini");
                }
            }
            else
            {
                heatMapFragment=new ImageViewFragment().newInstance(getActivity(),mapDetails,"HeatMapMini");
            }

            fragmentManager.beginTransaction().
                    add(R.id.heatMapFl, heatMapFragment).commit();
        }
    }

    public CompareHeatMapFragment newInstance(MapModel mapDetails, String source) {
        Bundle bundle=new Bundle();
        bundle.putSerializable("mapDetails",mapDetails);
        bundle.putSerializable("source",source);
        CompareHeatMapFragment compareHeatMapFragment=new CompareHeatMapFragment();
        compareHeatMapFragment.setArguments(bundle);
        return compareHeatMapFragment;
    }
    public void openRespectiveImage(String source, MapModel mapDetails)
    {

        imageFragment=new ImageViewFragment().newInstance(getActivity(),mapDetails,source);
         fragmentManager.beginTransaction().
                replace(R.id.mainRL, imageFragment).addToBackStack(null).commit();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.heatMapFl:
                if(Utils.checkifavailable(mapDetails.getFinalMapPath())) {
                    openRespectiveImage(source, mapDetails);
                }
                else
                {
                    Utils.showLongToast(getActivity(),"Heat map not saved for this survey id as the survey was invalid.");
                }
/*                Log.i(TAG,"Source is "+source +" toggle button checked "+toggleButton.isChecked() +" ls map"+mapDetails.getLsHeatMapPath());
                if(!toggleButton.isChecked()) {
                    if(Utils.checkifavailable(source))
                    {
                        if(source.contains("ls_heatmap"))
                        {
                           if(Utils.checkifavailable(mapDetails.getLsHeatMapPath()))
                           {
                               openRespectiveImage(source, mapDetails);
                           }
                           else
                           {
                               Utils.showLongToast(getActivity(),"No link speed heat map saved for this survey id.");
                           }
                        }

                    }

                }
                else
                {
if(Utils.checkifavailable(mapDetails.getFinalMapPath())) {
    openRespectiveImage(source, mapDetails);
}
else
{
    Utils.showLongToast(getActivity(),"Heat map not saved for this survey id as the survey was invalid.");
}

                }*/
                break;
            case R.id.walkMapFL:
                openRespectiveImage("WalkMap",mapDetails);
                break;

        }
    }
}
