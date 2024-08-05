package com.newmview.wifi.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mview.airtel.R;
import com.newmview.wifi.adapter.BuildingPagerAdapter;
import com.newmview.wifi.bean.TestResults;
import com.newmview.wifi.customdialog.MyAlertDialog;
import com.mview.airtel.databinding.ActivityBuildingTabBinding;
import com.newmview.wifi.enumtypes.AlertType;
import com.newmview.wifi.other.DialogManager;
import com.newmview.wifi.other.Utils;
import com.newmview.wifi.viewmodel.DialogViewModel;
import com.newmview.wifi.viewmodel.ImageViewModel;
import com.newmview.wifi.viewmodel.MainViewModelFactory;
import com.newmview.wifi.viewmodel.TestResultsVM;

public class BuildingTabActivity extends AppCompatActivity implements View.OnClickListener, MyAlertDialog.AlertDialogInterface {
    private static final String TAG = "BuildingTabActivity";
    private ActivityBuildingTabBinding buildingActivityBinding ;
    private BuildingPagerAdapter buildingAdapter;
    private ImageViewModel viewModel;
    private String path,flatType,opening,openingType,componentType;
    private Bundle bundle;
    private String wifiCoordsX,wifiCoordsY;

    private ImageViewModel iv_viewModel;
    private DialogViewModel alertDialogViewModel;
    private TestResultsVM testResultsVM;


    private static void onConfigureTab(TabLayout.Tab tab, int position) {
        tab.setText("OBJECT " + (position + 1));
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        //StrictMode.setVmPolicy(builder.build());
        super.onCreate(savedInstanceState);
        buildingActivityBinding= DataBindingUtil.setContentView(this, R.layout.activity_building_tab);
        getBundleExtras(null);
       setToolBar();
        viewModel = new ViewModelProvider(BuildingTabActivity.this).get(ImageViewModel.class);
        viewModel.childFragmentClicked().observe(BuildingTabActivity.this, new Observer<String>() {
            @Override
            public void onChanged(String source) {
                if(Utils.checkifavailable(source))
                {
                    buildingAdapter.replaceFragment();
                }

            }
        });

        setViewPagerAdapter();
        setClickListeners();
        initializeDialogVM();
     //   testResultsVM = new ViewModelProvider(this).get(TestResultsVM.class);
        setViewModelForMarker();


    }

    private void setViewModelForMarker() {
        testResultsVM= new ViewModelProvider(this,new MainViewModelFactory()).get(TestResultsVM.class);
        Log.i(TAG,"Test Results vm "+testResultsVM);
        testResultsVM.getSelectedMarker().observe(this, testResult -> {
            Log.i(TAG,"Marker result obtained");
            showTestResult(testResult);

        });

    }

    public void showTestResult(TestResults testResult) {
        if(testResult!=null)
        {
            String result=testResult.getResult();
            Log.i(TAG,"Result is "+result);
            DialogManager.showMyDialog(BuildingTabActivity.this,
                    AlertType.testResultAlert, alertDialogViewModel, BuildingTabActivity.this,
                    result,"Test Result", null, null, "OK", "Cancel");

        }

    }

    private void initializeDialogVM() {
        alertDialogViewModel = DialogManager.initializeViewModel(this);
        DialogManager.initializeViewModel(this);
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getBundleExtras(intent);
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
    public void onBackPressed() {
        super.onBackPressed();

        int count=getSupportFragmentManager().getBackStackEntryCount();
        Log.i(TAG,"On back pressed called ,  count "+count);
        if(count==0)
        {
finish();
        }
    }

    private void setToolBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ColorDrawable colorDrawable
                = new ColorDrawable(getResources().getColor(R.color.white));

        // Set BackgroundDrawable
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_app_icon);
    }

    private void getBundleExtras(Intent intent) {
if(intent==null)
             bundle = getIntent().getExtras();
else
    bundle=intent.getExtras();
             if(bundle!=null) {
                 path = bundle.getString("floorMapPath");
                 flatType = bundle.getString("flatType");
                 componentType = bundle.getString("componentType");
                 opening = bundle.getString("opening");
                 openingType = bundle.getString("openingType");
                 wifiCoordsX= String.valueOf(bundle.getFloat("wifiCoordsX"));
                 wifiCoordsY= String.valueOf(bundle.getFloat("wifiCoordsY"));
             }
             if(intent!=null)
             {
                 buildingAdapter.setBundle(bundle);
             }

        Log.i(TAG,"Coord x "+wifiCoordsX +"coordy "+wifiCoordsY);

    }

    private void setViewPagerAdapter() {
      buildingAdapter = new BuildingPagerAdapter(BuildingTabActivity.this,bundle);
        buildingActivityBinding.pager.setAdapter(buildingAdapter);
     //   TabLayout tabLayout = view.findViewById(R.id.tab_layout);
       // new TabLayoutMediator(dataBinding.tab_layout,buildingAdapter ,)

        new TabLayoutMediator(buildingActivityBinding.tabLayout,buildingActivityBinding.pager ,
                (tab, position) -> tab.setText("Tab " + (position + 1)))
        .attach();
    }
    

    private void setClickListeners() {
        buildingActivityBinding.compareBtn.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
      buildingAdapter.refresh(this.bundle);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.compareBtn:

        }
    }



    @Override
    public void alertDialogPositiveButtonClicked(AlertType type, Object details) {

    }

    @Override
    public void alertDialogNegativeButtonClicked(AlertType type) {

    }

    @Override
    public void listOptionClicked(String text) {

    }

    @Override
    public void finishActivity() {

    }
}
