package com.newmview.wifi.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.mview.airtel.R;
import com.newmview.wifi.adapter.MapGridListAdapter;
import com.newmview.wifi.adapter.MapTableViewAdapter;
import com.newmview.wifi.bean.Cell;
import com.newmview.wifi.bean.ColumnHeader;
import com.newmview.wifi.bean.MapModel;
import com.newmview.wifi.bean.RowHeader;
import com.mview.airtel.databinding.ActivityHeatMapListBinding;
import com.newmview.wifi.fragment.ImageViewFragment;
import com.newmview.wifi.helper.Interfaces;
import com.newmview.wifi.helper.MyTableViewListener;
import com.newmview.wifi.other.Config;
import com.newmview.wifi.other.Utils;
import com.newmview.wifi.viewmodel.MainViewModelFactory;
import com.newmview.wifi.viewmodel.MapVM;

import java.util.ArrayList;
import java.util.List;

public class HeatMapListActivity extends AppCompatActivity implements Interfaces.Swipelisteners{
    private static final String TAG = "HeatMapListActivity";
    ActivityHeatMapListBinding heatMapListBinding;
    MapVM viewModel;
    private MapGridListAdapter mapGridListAdapter;
    private List<MapModel> mapList;
    private int dividerHeight;
    private MapTableViewAdapter adapter;
    private ImageViewFragment heatMapImageFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //     ActivityWifiBinding wifiBinding=
        init();

    }
    private void init()
    {
        heatMapListBinding= DataBindingUtil.setContentView(this, R.layout.activity_heat_map_list);
        viewModel=new ViewModelProvider(this,new MainViewModelFactory()).get(MapVM.class);
     //   TableView
       // implementDefaultTable();

        heatMapListBinding.setViewModel(new MapModel());

      viewModel.getMapListObservable().observe(HeatMapListActivity.this, new Observer<List<MapModel>>() {
          private List<MapModel> mapList;

          @Override
          public void onChanged(List<MapModel> mapList) {
              Log.i(TAG,"Map  list"+mapList);
              setListToTable(mapList);


          }
      });


    }



    private void implementDefaultTable() {
       /* heatMapListBinding.mapTable.setPadding(15, 2, 2, 2);
        heatMapListBinding.mapTable.setGravity(Gravity.CENTER);*/
        dividerHeight = (int) (getResources().getDisplayMetrics().density * 1);
        ContextThemeWrapper wrappedContext = new ContextThemeWrapper(HeatMapListActivity.this, R.style.table_header);
        TextView siteTv=new TextView(this);
        TextView techTv=new TextView(this);
        TextView locationTypeTv=new TextView(this);
        TextView idTv=new TextView(HeatMapListActivity.this);
        TextView dateTimeTv=new TextView(HeatMapListActivity.this);
        TextView ssidTv=new TextView(HeatMapListActivity.this);
        ImageView menuView=new ImageView(HeatMapListActivity.this);
        siteTv.setGravity(Gravity.CENTER);
        ssidTv.setGravity(Gravity.CENTER);
        techTv.setGravity(Gravity.CENTER);
        idTv.setGravity(Gravity.CENTER);
        locationTypeTv.setGravity(Gravity.CENTER);
        dateTimeTv.setGravity(Gravity.CENTER);
        setAttributesToTextView(siteTv);
        setAttributesToTextView(techTv);
        setAttributesToTextView(locationTypeTv);
        setAttributesToTextView(idTv);
        setAttributesToTextView(dateTimeTv);
        setAttributesToTextView(ssidTv);


        //siteTv.setTextAppearance(this,R.style.table_header);
        //   menuView.setImageResource(R.drawable.mo);
        siteTv.setText("Site Details");
        techTv.setText("Technology");
        locationTypeTv.setText("Location Type");
        idTv.setText("Device Id");
        dateTimeTv.setText("Date Time");
        ssidTv.setText("SSID");
        TableRow headerRow=new TableRow(HeatMapListActivity.this);
        headerRow.setBackgroundColor(getResources().getColor(R.color.lightgrey));
        headerRow.addView(ssidTv);
        headerRow.addView(siteTv);
        headerRow.addView(techTv);
        headerRow.addView(locationTypeTv);
        headerRow.addView(idTv);
        headerRow.addView(dateTimeTv);

        //  headerRow.addView(menuView);



  //      heatMapListBinding.mapTable.addView(headerRow);
    }

    private void setListToTable(List<MapModel> mapList) {
        this.mapList=mapList;
        adapter = new MapTableViewAdapter(this);
        heatMapListBinding.tableView.setAdapter(adapter);
        heatMapListBinding.tableView.setTableViewListener(new MyTableViewListener(this));
       // getColumnHeaderList();
        adapter.setAllItems(getColumnHeaderList(), getRowHeaderList(), getCellList());
/*
        if (tableAllColumnsDataList != null) {
            if (tableAllColumnsDataList.size() > 0) {
                getColumnHeaderList();
                getRowHeaderList();
                getCellList();


            }
        }
*/


    }

    private List<List<Cell>> getCellList() {

            List<List<Cell>> mCellList = new ArrayList<>();
            List<Cell> cellList=null;
            for (int i = 0; i < mapList.size(); i++) {
                cellList = new ArrayList<>();
                cellList.add(new Cell(i +"",mapList.get(i).getFloorPlan(), 1));
                cellList.add(new Cell(i +"",mapList.get(i).getAddress(), 0));
                cellList.add(new Cell(i +"",mapList.get(i).getTechnology(), 0));
                cellList.add(new Cell(i +"",mapList.get(i).getLocationType(), 0));
                cellList.add(new Cell(i +"",mapList.get(i).getDeviceId(), 0));
                cellList.add(new Cell(i +"",mapList.get(i).getDateTime(), 0));
                cellList.add(new Cell(i +"",mapList.get(i).getSsidName(), 0));

                mCellList.add(cellList);

            }

            return mCellList;

    }
    private List<ColumnHeader> getColumnHeaderList() {
        ArrayList<String> columnStringList=Config.getHeatMapHeaders();
        ArrayList<ColumnHeader> columnHeaderList=new ArrayList<>();
        for(int i=0;i< columnStringList.size();i++) {
            ColumnHeader header = new ColumnHeader(String.valueOf(i), columnStringList.get(i));
            columnHeaderList.add(header);
        }

        return columnHeaderList;
    }

    private List<RowHeader> getRowHeaderList() {
         List<RowHeader> rowHeaderList = new ArrayList<>();
        for (long i = 0; i < mapList.size(); i++) {
            long text = i + 1;
            System.out.println("text is " + text);
            RowHeader header = new RowHeader(String.valueOf(i), "" + text);
            rowHeaderList.add(header);

        }
        return rowHeaderList;

    }

/*
    private void setValuesToDefaultTable(String ssidName, String address, String technology, String locationType, String dateTime, String deviceId, String name) {
        TableRow mapContentRow=new TableRow(HeatMapListActivity.this);
        TextView siteTv=new TextView(HeatMapListActivity.this);
        TextView techTv=new TextView(HeatMapListActivity.this);
        TextView locationTypeTv=new TextView(HeatMapListActivity.this);
        TextView idTv=new TextView(HeatMapListActivity.this);
        TextView dateTimeTv=new TextView(HeatMapListActivity.this);
        TextView ssidTv=new TextView(HeatMapListActivity.this);
        ssidTv.setText(ssidName);
        siteTv.setText(address);
        techTv.setText(technology);
        locationTypeTv.setText(locationType);
        idTv.setText(deviceId);
        dateTimeTv.setText(dateTime);
        setAttributesToTextView(siteTv);
        setAttributesToTextView(techTv);
        setAttributesToTextView(locationTypeTv);
        setAttributesToTextView(idTv);
        setAttributesToTextView(dateTimeTv);
        mapContentRow.addView(ssidTv);
        mapContentRow.addView(siteTv);
        mapContentRow.addView(techTv);
        mapContentRow.addView(locationTypeTv);
        mapContentRow.addView(idTv);
        mapContentRow.addView(dateTimeTv);
    //    mapContentRow.setLayoutParams(params);
        View viewDivider = new View(this);
        RelativeLayout.LayoutParams layoutParams=new RelativeLayout.
                LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dividerHeight);
        layoutParams.setMarginEnd(2);
        viewDivider.setLayoutParams(layoutParams);
        viewDivider.setBackgroundColor(getResources().getColor(R.color.lightgrey));
        */
/*heatMapListBinding.mapTable.addView(viewDivider);
        heatMapListBinding.mapTable.addView(mapContentRow);*//*

    }
*/

    private void setAttributesToTextView(TextView tv) {
        tv.setPadding(15, 8, 5, 8);


        tv.setTextColor(getResources().getColor(R.color.black));
    }

    private void populateHeatMapList() {
       /* heatMapListBinding.mapRv.setLayoutManager(new LinearLayoutManager(HeatMapListActivity.this));
        mapListAdapter=new MapListAdapter(this.mapList);
        heatMapListBinding.mapRv.setAdapter(wifiAdapter);*/
    }

    public void showHeatMapView(int pos, String title) {
       // String value=mapList.get(pos).getFloorPlan();
        int index=-1;
        for(int i=0;i<mapList.size();i++)
        {
            String name=mapList.get(i).getFloorPlan();
            if(Utils.checkifavailable(name) && Utils.checkifavailable(title))
            {
                if(name.equalsIgnoreCase(title))
                {
                    index=i;
                    break;
                }
            }
        }
        Log.i(TAG,"Index is "+index);
        if(mapList!=null)
        {
            if(mapList.size()>0)
            {
                if(index!=-1)
                openHeatMapImage(mapList.get(index));
            }
        }
    }

    private void openHeatMapImage(MapModel mapDetails) {
        heatMapImageFragment=new ImageViewFragment().newInstance(this,mapDetails, null);
        getSupportFragmentManager().beginTransaction().replace(R.id.tableRL, heatMapImageFragment).addToBackStack(null).commit();

    }

    @Override
    public void swipeleft() {

    }

    @Override
    public void swiperight() {

    }
}
