package com.newmview.wifi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mview.airtel.R;
import com.dashboard.activity.GraphDetailsActivity;
import com.newmview.wifi.other.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchAdapter extends RecyclerView.Adapter implements Filterable {
    private final Context context;
    private  TextView textview;
    private FilterValues filterValues;
    private ArrayList<ArrayList<HashMap<String, String>>> list;
    private ArrayList<ArrayList<HashMap<String, String>>> finalList=new ArrayList<>();
    private ArrayList<ArrayList<HashMap<String, String>>> filteredList;
    private View v;
    private SearchListViewHolder searchViewHolder;
    private ArrayList<HashMap<String,String>>  localdata;
    private ArrayList<HashMap<String,String>>  locallist;

    public SearchAdapter(Context context, ArrayList<ArrayList<HashMap<String, String>>> graphFinalList) {
        this.context=context;
        this.list=graphFinalList;
        System.out.println("in search constructor "+list );
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.graphs_list, parent, false);
        return new SearchListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        {
          searchViewHolder =(SearchListViewHolder)holder;
            if(finalList.size()>0)
            {
                showNamesAccordingToList(position,finalList);
            }
            else {
                showNamesAccordingToList(position,list);
            }

            /*if(Config.listSize>8) {
                holder.itemView.setMinimumHeight((int) (Graphs_BottomsheetFragment.bottomsheetheight / 6));
            }
            else
            {
                holder.itemView.setMinimumHeight((int) (Graphs_BottomsheetFragment.bottomsheetheight / 8));

            }
*/

        }

    }

    @Override
    public int getItemCount() {
        if (finalList != null && finalList.size() > 0) {

            System.out.println("returning finallist "+finalList.size());
            return finalList.get(0).size();
        } else {
            if (list != null && list.size() > 0) {
                System.out.println("returning list "+list.size());
                return list.get(0).size();
            }
        }
        System.out.println("returning 0");

        return 0;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        if(filterValues==null)
            filterValues=new FilterValues();
        return filterValues;
    }
    private void showNamesAccordingToList(final int position, ArrayList<ArrayList<HashMap<String, String>>> finalList) {

        localdata = new ArrayList<>();
        localdata.addAll(finalList.get(0));
        System.out.println("local list in adapter "+localdata);
        searchViewHolder.textview.setText(localdata.get(position).get("name"));
     searchViewHolder.textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(context);
                if (GraphDetailsActivity.group.equalsIgnoreCase("Geography") &&
                        (GraphDetailsActivity.subgrp.equalsIgnoreCase("state"))) {

                    ((GraphDetailsActivity)context).sendSelectedState(localdata.get(position));

                }
                else {
                    ((GraphDetailsActivity) context).sendSelectedOption(localdata.get(position));
                }


            }
        });
    }

    private class FilterValues extends Filter{
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            {
                FilterResults results = new FilterResults();
                finalList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    results.values = list;
                    results.count = list.size();
                } else {

                    filteredList = new ArrayList<>();
                    for(int i=0;i<list.size();i++)
                    {
                        locallist=new ArrayList<>();
                        for(int j=0;j<list.get(i).size();j++)
                        {
                            if (list.get(i).get(j).get("name").toUpperCase().contains(constraint.toString().toUpperCase())) {
                                locallist.add(list.get(i).get(j));
                                // if `contains` == true then add it
                                // to our filtered list

                                System.out.println("filtered list is "+filteredList);
                            }

                        }
                        filteredList.add(locallist);
                    }




                    // Finally set the filtered values and size/count
                    results.values = filteredList;
                    results.count = filteredList.size();
                }
                System.out.println("result count  " + results.count + "result values " + results.values);

                return results;
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            finalList = (ArrayList<ArrayList<HashMap<String, String>>>) results.values;

            notifyDataSetChanged();


        }
    }

    private class SearchListViewHolder extends RecyclerView.ViewHolder {
        public TextView textview;

        public SearchListViewHolder(View v) {
            super(v);
            textview=(TextView)itemView.findViewById(R.id.graphName);
        }
    }
}
