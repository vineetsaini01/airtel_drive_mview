package com.newmview.wifi.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.mview.airtel.R;
import com.dashboard.activity.GraphDetailsActivity;
import com.newmview.wifi.bean.Graphs_Bean;
import com.newmview.wifi.fragment.Graphs_BottomsheetFragment;
import com.newmview.wifi.other.Config;

import java.util.ArrayList;

public class GraphsListAdapter  extends RecyclerView.Adapter implements Filterable {
    private final ArrayList<Graphs_Bean> list;
    private final Context ctx;
    private RecyclerView.ViewHolder viewHolder;
    private View v;
    private GraphsFilter graphsFilter;
    private ArrayList<Graphs_Bean> finalList=new ArrayList<>();
    private ArrayList<Graphs_Bean> filteredList;
    private GraphListViewHolder graphListViewHolder;


    public GraphsListAdapter(ArrayList<Graphs_Bean> graphsList, Context context) {
        this.list=graphsList;
        this.ctx=context;
        System.out.println("graph list size is "+list.size());
        

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.graphs_list, parent, false);
        return new GraphListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        graphListViewHolder=(GraphListViewHolder)holder;
        if(finalList.size()>0)
        {
            showNamesAccordingToList(position,finalList);
        }
        else {
            showNamesAccordingToList(position,list);
        }

    if(Config.listSize>8) {
        holder.itemView.setMinimumHeight((int) (Graphs_BottomsheetFragment.bottomsheetheight / 6));
    }
    else
    {
        holder.itemView.setMinimumHeight((int) (Graphs_BottomsheetFragment.bottomsheetheight / 8));

    }


    }

    private void showNamesAccordingToList(int position, ArrayList<Graphs_Bean> list) {

        final String graphname=list.get(position).getName();
        final String graphId=list.get(position).getId();
        System.out.println("graph name is "+graphname +"  position "+position);
        graphListViewHolder.graph_textview.setText(graphname);
        graphListViewHolder.graph_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphDetailsActivity.graphname=graphname;
                GraphDetailsActivity.graphid=graphId;


                //new GraphDetailsActivity().sendGraphDetailsRequest(graphId);
                ((GraphDetailsActivity)ctx).sendGraphRequest(graphId,graphname);

             //   ((GraphDetailsActivity)ctx).sendGraphDetailsRequest(graphId);


            }
        });
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
    public int getItemCount() {
        if (finalList != null && finalList.size() > 0) {
            System.out.println("returning finallsit");

            return finalList.size();
        } else {
            if (list != null && list.size() > 0) {
                return list.size();
            }
        }
        return 0;

    }

    @Override
    public Filter getFilter() {
        if (graphsFilter == null)
            graphsFilter = new GraphsFilter();

        return graphsFilter;
    }

    private  class GraphListViewHolder extends RecyclerView.ViewHolder {

       private   final TextView graph_textview;
       private    GraphListViewHolder(View itemView) {
            super(itemView);
            graph_textview=(TextView)itemView.findViewById(R.id.graphName);

        }

    }

    private class GraphsFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            finalList = new ArrayList<Graphs_Bean>();
            if (constraint == null || constraint.length() == 0) {
                results.values = list;
                results.count = list.size();
            } else {

                filteredList = new ArrayList<Graphs_Bean>();


                for (int i = 0; i < list.size(); i++) {
                    String name  = list.get(i).getName();
                    String id=list.get(i).getId();
                    Graphs_Bean graphs_bean=new Graphs_Bean();
                    graphs_bean.setName(name);
                    graphs_bean.setId(id);

                    if (name.toUpperCase().contains(constraint.toString().toUpperCase())) {
                        // if `contains` == true then add it
                        // to our filtered list
                        filteredList.add(graphs_bean);
                        System.out.println("filtered name is "+name);
                    }
                }

                // Finally set the filtered values and size/count
                results.values = filteredList;
                results.count = filteredList.size();
            }
            System.out.println("result count  " + results.count + "result values " + results.values);

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            finalList = (ArrayList<Graphs_Bean>) results.values;
//            System.out.println("filtered results " + finalList.get(0).getName());
            notifyDataSetChanged();

        }
    }
}

