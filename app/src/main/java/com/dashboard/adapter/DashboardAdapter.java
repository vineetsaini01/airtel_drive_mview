package com.dashboard.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.dashboard.interfaces.OnItemClickListener;
import com.dashboard.roomdb.DashboardEntity;
import com.mview.airtel.R;

import java.util.List;

public class DashboardAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<DashboardEntity> dashboardList;
    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public DashboardAdapter(Context context, List<DashboardEntity> dashboardList) {
        this.context = context;
        this.dashboardList = dashboardList;
    }

    @Override
    public int getGroupCount() {
        return dashboardList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return dashboardList.get(groupPosition).getGraphData().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return dashboardList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return dashboardList.get(groupPosition).getGraphData().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }



    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);
        }

        TextView textView = convertView.findViewById(R.id.listTitle);
        textView.setText(dashboardList.get(groupPosition).getDbName());
        if (isExpanded){
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.up_grey, 0);
        }else {
            textView.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.down_grey,0);

        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listitem, null);
        }

        TextView textView = convertView.findViewById(R.id.expandedListItem);
        final String graphTitle = dashboardList.get(groupPosition).getGraphData().get(childPosition).getGraphName();
        final String graphId = dashboardList.get(groupPosition).getGraphData().get(childPosition).getGraphId();

        final String dbTitle=dashboardList.get(groupPosition).getDbName();
        textView.setText(graphTitle);
        Log.d("TAG", "onItemClick: "+ "graphName: "+graphTitle +" "+"dbName: "+dbTitle+" "+groupPosition+" "+dbTitle);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //((DashboardActivity) mContext).openGraphFrame(groupPosition,dbTitle,childPosition);
                if (listener != null){
                    listener.onItemClick(graphId,graphTitle,groupPosition,dbTitle);

                }
//                ((DashboardActivity) mContext).openGraphActivityFromChild(groupPosition,dbTitle,childPosition);
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
