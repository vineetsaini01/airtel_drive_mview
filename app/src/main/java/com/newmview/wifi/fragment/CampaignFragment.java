package com.newmview.wifi.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.mview.airtel.R;
import com.newmview.wifi.activity.BuildingTabActivity;
import com.mview.airtel.databinding.FragmentCampaignBinding;
import com.newmview.wifi.other.Utils;

public class CampaignFragment extends Fragment implements View.OnClickListener {
    private FragmentCampaignBinding campaignBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        campaignBinding= DataBindingUtil.inflate(inflater, R.layout.fragment_campaign, container,
                false);
        setClickListeners();
        return campaignBinding.getRoot();

    }
    private void setClickListeners() {
        campaignBinding.buildingCardView.setOnClickListener(this);
    }
    private void openBuildingTabPage() {
        Utils.takeToNextActivity(getActivity(), BuildingTabActivity.class,null);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buildingCardView:
                openBuildingTabPage();
                break;
        }
    }
}
