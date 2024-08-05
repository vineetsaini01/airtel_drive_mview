package com.newmview.wifi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.mview.airtel.R;
import com.newmview.wifi.activity.BuildingTabActivity;
import com.mview.airtel.databinding.ActivityCampaignBinding;
import com.newmview.wifi.other.Utils;

public class CampaignActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityCampaignBinding campaignBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        campaignBinding= DataBindingUtil.setContentView(this, R.layout.activity_campaign);
        setToolbar();
        setClickListeners();

    }
    private void setToolbar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ColorDrawable colorDrawable
                = new ColorDrawable(getResources().getColor(R.color.white));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.airtel_icon);
    }


    private void setClickListeners() {
        campaignBinding.buildingCardView.setOnClickListener(this);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                /*  NavUtils.navigateUpFromSameTask(this);*/
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.buildingCardView:
                openBuildingTabPage();
                break;
        }
    }

    private void openBuildingTabPage() {
        Utils.takeToNextActivity(this, BuildingTabActivity.class,null);
    }
}