package com.newmview.wifi.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mview.airtel.R;
import com.newmview.wifi.adapter.CompareMapsListAdapter;
import com.newmview.wifi.bean.MapModel;
import com.newmview.wifi.bean.TestResults;
import com.newmview.wifi.customdialog.MyAlertDialog;
import com.mview.airtel.databinding.ActivityCompareHeatMapBinding;
import com.newmview.wifi.enumtypes.AlertType;
import com.newmview.wifi.other.DialogManager;
import com.newmview.wifi.other.Utils;
import com.newmview.wifi.viewmodel.DialogViewModel;
import com.newmview.wifi.viewmodel.MainViewModelFactory;
import com.newmview.wifi.viewmodel.MapVM;
import com.newmview.wifi.viewmodel.TestResultsVM;

import java.util.ArrayList;
import java.util.List;

public class CompareMapsActivity extends AppCompatActivity implements MyAlertDialog.AlertDialogInterface {
    private static final String TAG = "CompareMapsActivity";
    ActivityCompareHeatMapBinding compareHeatMapBinding;
    private MapVM viewModel;
    private CompareMapsListAdapter mapListAdapter;
    private List<MapModel> mapList;
    private ArrayList<MapModel> adapterList=new ArrayList<MapModel>();
    private MapModel mapDetails;
    private String source;
    private TestResultsVM testResultsVM;
    private DialogViewModel alertDialogViewModel;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        for(int i=0;i<mapList.size();i++)
        {
            String mapId=mapList.get(i).getMapId();
            if(Utils.checkifavailable(source))
            {
                Log.i(TAG,"Source while adding "+source);
                if(source.toLowerCase().contains("ls_heatmap".toLowerCase()))
                {
                    Log.i(TAG,"Source while adding check in 1");
                    mapList.get(i).setCompareImgPath(mapList.get(i).getLsHeatMapPath());
                }
                else
                {
                    Log.i(TAG,"Source while adding check in 2");
                    mapList.get(i).setCompareImgPath(mapList.get(i).getFinalMapPath());
                }
            }
            else
            {
                mapList.get(i).setCompareImgPath(mapList.get(i).getFinalMapPath());
            }
            if(!mapId.equals(mapDetails.getMapId())) {
                menu.add(0, Integer.parseInt(mapId), Menu.NONE, "Survey Id " + mapId).setCheckable(true);
                String state=mapList.get(i).getState();
                if(Utils.checkifavailable(state)) {
                    if(i>0) {
                        if (mapList.get(i).getState().equalsIgnoreCase("t")) {
                            menu.getItem(i - 1).setChecked(true);
                        } else {
                            menu.getItem(i - 1).setChecked(false);
                        }
                    }
                }
              /*  StateListDrawable stateListDrawable = (StateListDrawable) getResources().getDrawable(R.drawable.selector_drawable);
                int[] state = {menu.getItem(i).isChecked()?android.R.attr.state_checked:android.R.attr.state_empty};
                stateListDrawable.setState(state);
                menu.getItem(i).setIcon(stateListDrawable.getCurrent());*/
            }


        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==android.R.id.home)
        {
            finish();
        }
        else {
            int index = getIndexOfMapList(mapList, item);


Log.i(TAG,"Index is "+index +" with state "+mapList.get(index).getState());
            if (index != -1) {
                int indexn = getIndexOfMapList(adapterList, item);
                //   if(!item.isChecked()) {
                if (mapList.get(index).getState().equalsIgnoreCase("f")) {
                    item.setChecked(true);
                    mapList.get(index).setState("t");
                    adapterList.add(mapList.get(index));
                    mapListAdapter.notifyItemInserted(adapterList.size() - 1);
                }
              else  if(indexn!=-1)
                {
                    item.setChecked(false);
                    mapList.get(index).setState("f");
                    adapterList.remove(indexn);
                    mapListAdapter.notifyItemRemoved(indexn);
                }
            }


                  /* item.setChecked(!item.isChecked());
                   StateListDrawable stateListDrawable = (StateListDrawable) getResources().getDrawable(R.drawable.selector_drawable);
                   int[] state = {item.isChecked()?android.R.attr.state_checked:android.R.attr.state_empty};
                   stateListDrawable.setState(state);
                   item.setIcon(stateListDrawable.getCurrent());
                   if(!item.isChecked()) {

                       adapterList.add(mapList.get(index));
                       mapListAdapter.notifyItemInserted(adapterList.size()-1);
                   }
                   else {

                       adapterList.remove(mapList.get(index));
                       mapListAdapter.notifyItemRemoved(adapterList.size()-1);
                   }*/


        }
        return true;
    }

    private int getIndexOfMapList(List<MapModel> mapList, MenuItem item) {
        int i=-1;
        String value=item.getItemId()+"";
        Log.i(TAG,"Item id of clicked item "+value);
        for (i = 0; i < mapList.size(); i++) {

            //System.out.println("state value is " + value + "key is " + list.get(i).getMapId());
            if(value.equals(mapList.get(i).getMapId()))
            {

            return i;
        }
    }
        return -1;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        compareHeatMapBinding = DataBindingUtil.setContentView(this, R.layout.activity_compare_heat_map);
        getExtras();
        if(mapDetails!=null) {
            adapterList.add(mapDetails);
        }

        setActionBar();
        setAdapter(adapterList);
        viewModel=new ViewModelProvider(this,new MainViewModelFactory()).get(MapVM.class);
        compareHeatMapBinding.setMapModel(new MapModel());
        viewModel.getMapListObservable().observe(CompareMapsActivity.this, new Observer<List<MapModel>>() {
            private List<MapModel> mapList;

            @Override
            public void onChanged(List<MapModel> mapList) {
                CompareMapsActivity.this.mapList=mapList;
                for (int i=0;i<mapList.size();i++)
                {
                    CompareMapsActivity.this.mapList.get(i).setState("f");
                }
               // setAdapter(mapList);
            }
        });
        initializeDialogVM();
        setViewModelForMarker();



    }

    private void getExtras() {
        Bundle bundle=getIntent().getExtras();
        mapDetails=(MapModel)bundle.getSerializable("mapDetails");
        source=bundle.getString("source");
        Log.i(TAG,"Rxd source "+source);
        Log.i(TAG,"Map Details are "+mapDetails);
    }
    private void setActionBar() {
        /*Toolbar toolbar = findViewById(R.id.toolbar);*/


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ColorDrawable colorDrawable
                = new ColorDrawable(getResources().getColor(R.color.white));

        // Set BackgroundDrawable
        getSupportActionBar().setBackgroundDrawable(colorDrawable);


    }


    private void setAdapter(List<MapModel> mapList) {
       // this.mapList=mapList;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        compareHeatMapBinding.mapListRv.setLayoutManager(layoutManager);
        compareHeatMapBinding.mapListRv.setHasFixedSize(true);
        mapListAdapter =new CompareMapsListAdapter(this,mapList,source);
        compareHeatMapBinding.mapListRv.setAdapter(mapListAdapter);
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
            DialogManager.showMyDialog(CompareMapsActivity.this,
                    AlertType.testResultAlert, alertDialogViewModel, CompareMapsActivity.this,
                    result,"Test Result", null, null, "OK", "Cancel");

        }

    }

    private void initializeDialogVM() {
        alertDialogViewModel = DialogManager.initializeViewModel(this);
        DialogManager.initializeViewModel(this);
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
