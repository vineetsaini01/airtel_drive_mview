package com.dashboard.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import com.dashboard.adapter.DashboardAdapter;
import com.dashboard.interfaces.OnItemClickListener;
import com.dashboard.model.GraphData;
import com.dashboard.roomdb.DashboardEntity;
import com.dashboard.viewmodel.DashboardViewModel;
import com.mview.airtel.R;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements OnItemClickListener {

    private DashboardAdapter expandableListAdapter;
    private ExpandableListView expandableListView;
    private int lastExpandedPosition=-1;
    private List<DashboardEntity> dashboardsList=new ArrayList<>();
    private ArrayList<GraphData> graphsList= new ArrayList<>();
    private DashboardViewModel viewModel;
    private String dbTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        expandableListView = (ExpandableListView) findViewById(R.id.homeexpandableListView);
        CallDashboardListApi();

    }


    private void setDashboardsAdapterAtHome() {
        Log.d("TAG", "setDashboardsAdapterAtHome: "+dashboardsList);
        expandableListAdapter = new DashboardAdapter(this,dashboardsList);
        expandableListAdapter.setOnItemClickListener(this);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                if(lastExpandedPosition!=-1 && groupPosition!=lastExpandedPosition) {
                    expandableListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition=groupPosition;

            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {


            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                return false;
            }
        });
    }

    private void CallDashboardListApi(){
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        viewModel.fetchDataFromApi();


        viewModel.getAllDashboards().observe(this, dashboards -> {
            if (dashboards != null){
                Log.d("TAG", "CallApi: list "+dashboards);

                dashboardsList.addAll(dashboards);
                setDashboardsAdapterAtHome();

            }
            Log.d("TAG", "CallApi dashboardsList: "+dashboardsList);
            // Update UI with dashboards
        });
    }


    @Override
    public void onItemClick(String graphId, String graphTitle, int position, String dbTitle) {
        Intent intent = new Intent(this, GraphDetailsActivity.class);
        intent.putExtra("graphId", graphId);
        intent.putExtra("graphName", graphTitle);
        intent.putExtra("clickedposition", position);
        intent.putExtra("dbName", dbTitle);
        Log.d("TAG", "onItemClick: "+ "graphName: "+graphTitle +" "+"dbName: "+dbTitle);

        startActivity(intent);
    }
}