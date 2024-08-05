package com.newmview.wifi.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.mview.airtel.R;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class PercentTableFragment extends Fragment implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private View view;
    private TableLayout tableLayout;
    private Bundle bundle;
    private ArrayList<HashMap<String, String>> tablelist;
    private ArrayList<HashMap<String, String>> percentData;
    private ArrayList<String> colnames;
    private Integer totalCount;
    private TableRow tableRow;
    private TableRow firstRow;
    private ArrayList<ArrayList<HashMap<String, String>>> tableAllColumnsDataList;
    private Context context;
    private SearchView searchView;
    private String constraint;
    private ArrayList<HashMap<String, String>> localList;
    View viewDivider;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.tablefragment, container, false);
        //context=getActivity();
        init();

       // Utils.showToast(getActivity(),"fragment loaded");

        displayTableData();
        return view;

    }

    private void init() {
        if(getActivity()!=null) {
            searchView= getActivity().findViewById(R.id.search);
        }

        tableLayout = view.findViewById(R.id.db_tblLayout);
        bundle=getArguments();
        if (bundle != null) {
            tablelist = (ArrayList<HashMap<String, String>>) bundle.getSerializable("tablelist");
            colnames = (ArrayList<String>) bundle.getSerializable("colnameslist");
            percentData = (ArrayList<HashMap<String, String>>) bundle.getSerializable("percentDatalist");
            totalCount = Integer.valueOf(bundle.getString("totalCount"));
            tableAllColumnsDataList= (ArrayList<ArrayList<HashMap<String, String>>>)bundle.getSerializable("tableAllColumnsDataList");


        }


        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void displayTableData() {
        try {
            tableLayout.setStretchAllColumns(true);
            tableLayout.setPadding(15, 2, 2, 2);
            tableLayout.setGravity(Gravity.CENTER);
            System.out.println("percent data "+percentData);
            ViewGroup.LayoutParams layoutParams1 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (tableAllColumnsDataList != null &&tableAllColumnsDataList.size() > 0) {


                for (int j = 0; j < tableAllColumnsDataList.size(); j++) {

                    if(j==0)
                    {
                        firstRow = new TableRow(getActivity());
                        firstRow.setBackgroundColor(getResources().getColor(R.color.lightgrey));

                        for(int i=0;i<colnames.size();i++) {
                            TextView tv=new TextView(getActivity());
                            tv.setPadding(10, 20, 10, 20);
                            tv.setGravity(Gravity.START);
                            tv.setText(colnames.get(i));
                            tv.setTextColor(getResources().getColor(R.color.textColor));
                            firstRow.addView(tv);

                        }

                    }


                    tableRow = new TableRow(getActivity());
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    tableRow.setLayoutParams(lp);
                    checkSearchFilter(j);
                    addViewsToTable();
                    if(j==0)
                    {
                        tableLayout.addView(firstRow);
                    }

                    tableLayout.addView(tableRow);

                    }

                viewDivider = new View(getActivity());
                int dividerHeight = (int) (getResources().getDisplayMetrics().density * 2); // 1dp to pixels
                viewDivider.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dividerHeight));
                viewDivider.setBackgroundColor(getResources().getColor(R.color.app_theme));

            } else if (Constants.ERROR == 3) {
                Utils.showToast(getActivity(), Constants.SERVER_ERROR);
                Constants.ERROR=0;
            }
        } catch (Exception e) {
            e.printStackTrace();

            System.out.println("exception on creating table is " + e.toString());
        }
    }

    private void addViewsToTable() {
        for(int k=0;k<localList.size();k++) {
            System.out.println("table all index wise"+localList);

            View viewDiv = new View(getActivity());
            int dividerHeight = (int) (getResources().getDisplayMetrics().density * 1);
            TextView tv_val = new TextView(getActivity());

            tv_val.setPadding(10, 20, 10, 20);
            tv_val.setGravity(Gravity.START);
            System.out.println("table all column name "+localList.get(k).get("y"));
            tv_val.setText(String.format("%s %s", localList.get(k).get("y"),localList.get(k).get("percent")));
            viewDivider = new View(getActivity());

            viewDivider.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dividerHeight));
            viewDivider.setBackgroundColor(getResources().getColor(R.color.lightgrey));

            tableRow.addView(tv_val);
            tableRow.addView(viewDivider);


        }



    }

    private void checkSearchFilter(int j) {

        if(tableAllColumnsDataList!=null && tableAllColumnsDataList.size()>0)
        {
            localList = new ArrayList<>();
            System.out.println("checking table "+tableAllColumnsDataList);
            if (constraint == null || constraint.length() == 0) {
                localList.addAll(tableAllColumnsDataList.get(j));
            } else {

                for(int i=0;i<tableAllColumnsDataList.get(j).size();i++)
                {
System.out.println("name is "+tableAllColumnsDataList.get(j).get(i).get("y"));
                    if (tableAllColumnsDataList.get(j).get(i).get("y").toUpperCase().contains(constraint.toUpperCase())) {
                        System.out.println("name is upon matching  "+tableAllColumnsDataList.get(j).get(i).get("y"));
                        localList.addAll(tableAllColumnsDataList.get(j));
                    }


                }





            }


        }



    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        tableLayout.removeAllViews();
        constraint=newText;
        displayTableData();
        
        
        return false;
    }

    @Override
    public boolean onClose() {
        return false;
    }
}

