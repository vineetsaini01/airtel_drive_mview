package com.newmview.wifi.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.newmview.wifi.fragment.BuildingListFragment;
import com.newmview.wifi.fragment.BuildingSecondFragmentTab;

public class BuildingPagerAdapter extends FragmentStateAdapter {
    private static final int TABS_COUNT = 2;
    private  Bundle bundle;
    private BuildingSecondFragmentTab buildingFragment;
    private BuildingListFragment buildingListFragment;

    public BuildingPagerAdapter(@NonNull FragmentActivity fragmentActivity, Bundle bundle) {
        super(fragmentActivity);
        this.bundle=bundle;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)
        switch (position) {
            case 0:
             buildingFragment = new BuildingSecondFragmentTab();
                buildingFragment.setArguments(bundle);
            return buildingFragment;
            case 1:
                //return null;


                buildingListFragment = new BuildingListFragment();
                buildingListFragment.setArguments(bundle);
                return buildingListFragment;

        }
        return null;
    }

    @Override
    public int getItemCount() {
        return TABS_COUNT;
    }


    public void refresh(Bundle bundle) {
        if(buildingFragment!=null)
        {
            buildingFragment.refresh(bundle);
        }
    }

    public void setBundle(Bundle bundle) {
        this.bundle=bundle;
       // notifyDataSetChanged();

    }

    public void replaceFragment() {
       // buildingListFragment.openFullImage()

    }
}
