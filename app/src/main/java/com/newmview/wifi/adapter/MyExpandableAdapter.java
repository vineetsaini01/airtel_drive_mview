package com.newmview.wifi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mview.airtel.R;
import com.newmview.wifi.activity.MainActivity;
import com.newmview.wifi.bean.Db_Bean;

import java.util.ArrayList;

public class MyExpandableAdapter extends BaseExpandableListAdapter  {
    private  ArrayList<String> _issuesList;
    private ArrayList<Db_Bean> _subCatList;
    private ArrayList<String> _headersList;
    private Context _context;
    private LayoutInflater inflater;
    private TextView textView;
    private ImageView dbimg;


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public MyExpandableAdapter(Context context, ArrayList<String> headersList, ArrayList<Db_Bean> subCatList,ArrayList<String> issues) {
        //Utils.showToast(context,"adapter called!!");
        this._context=context;
        this._headersList=headersList;
        this._subCatList=subCatList;
        this._issuesList=issues;
        System.out.println("headers list is "+headersList);

    }

    public MyExpandableAdapter(MainActivity mainActivity, ArrayList<String> headersList, ArrayList<String> subCatList) {
    }

    @Override
    public int getGroupCount()
    {
        return _issuesList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 0;
        /*if(groupPosition==0) {
           // return _subCatList.size();
         //   return _issuesList.size();//changed for the time being till dashboard class is not updated.
        }
        *//*else if(groupPosition==1)
        {
            return _issuesList.size();
        }*//*
        else {
            return 0;
        }

*/
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
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


        inflater = (LayoutInflater) this._context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView=inflater.inflate(R.layout.subcat,null);



        textView = (TextView) convertView.findViewById(R.id.grouptxt);
        dbimg=convertView.findViewById(R.id.db);
        textView.setText(_issuesList.get(groupPosition));
        //commented for the time being on 05-08-2021
        /*if(groupPosition==0)
        {
            dbimg.setImageDrawable(_context.getResources().getDrawable(R.drawable.dashboard));
        }
        else*/
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)_context).openRespectiveScreens(groupPosition,_issuesList);
            }
        });
    /*    if(groupPosition==0)
        {
            dbimg.setImageDrawable(_context.getResources().getDrawable(R.mipmap.diagnostics));

        }
        else if(groupPosition==2)
        {
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.RESULT_FLAG=true;

                   // ((MainActivity)_context).openVideoTestFragment();
                   ((MainActivity)_context).openSpeedTestList();
                }
            });
        }


        if (isExpanded) {
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.collapsearrow, 0);

        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.expandarrow,0);


        }*/
        return convertView;
    }



    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {





        inflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView=inflater.inflate(R.layout.subcat,null);


//Utils.showToast(_context,"child position is "+childPosition +" group "+ groupPosition);
        textView = (TextView) convertView.findViewById(R.id.grouptxt);
        //commented for time being on 05-08-2021
      /*  if(groupPosition==0) {
            System.out.println("child position is "+childPosition);
            textView.setText(_subCatList.get(childPosition).getDbName());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) _context).openGraphActivity(childPosition);
                }
            });
        }
        else*/
       /* if(groupPosition==0)
        {
            System.out.println("child position is "+childPosition);

            System.out.println("issues lsit " + _issuesList.get(childPosition));
            textView.setText(_issuesList.get(childPosition));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity)_context).openRespectiveScreens(childPosition,_issuesList);
                }
            });
        }
*/
        return convertView;
    }


}
