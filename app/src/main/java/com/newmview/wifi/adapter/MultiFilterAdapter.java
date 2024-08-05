package com.newmview.wifi.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.mview.airtel.R;
import com.dashboard.activity.GraphDetailsActivity;
import com.newmview.wifi.database.DB_handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MultiFilterAdapter  extends RecyclerView.Adapter implements Filterable {
    private final ArrayList<HashMap<String, String>> list;
    private final ArrayList<String> filterNamesList;
    private final ArrayList<ArrayList<HashMap<String, String>>> allTablesData;
    private final LinearLayout filterLayout;
    private final String filterName;
    private final int index;
    private ArrayList<ArrayList<HashMap<String, String>>> lists;
    private ArrayList<HashMap<String, String>> finalList=new ArrayList<>();
    private Context ctx;
    private View v;


    private ArrayList<HashMap<String, String>> filteredList;
    private ListFilter listFilter;
    private ArrayList<ArrayList<HashMap<String,String>>> newlyTablesList;
    private DB_handler db_handler;
    private ArrayList<String> newFilterNamesList;
    private String filterValue;
    private ArrayList<String> statusVals=new ArrayList<>();
    private ArrayList<String> filterParamList=new ArrayList<>();
    private ArrayList<String> filterValuesList=new ArrayList<>();
    private HashMap<String, String> params=new HashMap<>();
    private static ArrayList<HashMap<String,String>> paramsList=new ArrayList<>();
    private static boolean dontGoAheadFlag1=false;
    private static boolean dontGoAheadFlag2=false;
    HashMap<String,ArrayList<String>> maps;
    private ArrayList<ArrayList<ArrayList<HashMap<String, String>>>> resultsList=new ArrayList<>();
    private int leftIndex;
    private ArrayList<String> checkedVals;

    public MultiFilterAdapter(Context context, ArrayList<HashMap<String, String>> list, ArrayList<String> filterNamesList,
                              ArrayList<ArrayList<HashMap<String, String>>> allTablesData, LinearLayout filterLayout,
                              String filterName, HashMap<String, String> paramHp, int index, ArrayList[] arrayLists, ArrayList<String> checkedValues) {


        this.list=list;
        this.ctx=context;
        db_handler= new DB_handler(this.ctx);
        this.filterNamesList=filterNamesList;
        this.allTablesData=allTablesData;
        this.filterLayout=filterLayout;
        this.filterName=filterName;
        this.params=paramHp;
        this.index=index;
        this.checkedVals=checkedValues;
        System.out.println("checked values set in constructor is "+checkedValues);
        System.out.println("list is "+list);


    }

    @Override
    public Filter getFilter() {

        if (listFilter == null)
            listFilter = new ListFilter();

        return listFilter;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.filters_list, parent, false);
        return new ListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListViewHolder listViewHolder=(ListViewHolder)holder;

        if(finalList.size()>0)
        {
            showNamesAccordingToList(position,finalList,listViewHolder);
        }
        else {
            showNamesAccordingToList(position,list, listViewHolder);
        }

    }

    @Override
    public int getItemCount() {
        if (finalList != null && finalList.size() > 0) {
            System.out.println("returning finallsit"+finalList);

            return finalList.size();
        } else {
            if (list != null && list.size() > 0) {
                return list.size();
            }
        }
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
    private void showNamesAccordingToList(final int position, final ArrayList<HashMap<String, String>> list, final ListViewHolder listViewHolder) {

        final String name=list.get(position).get("name");
        //  final String graphId=list.get(position).getId();
        System.out.println("graph name is "+name +"  position "+position);
        listViewHolder.textview.setText(name);
        //params.put(name,"");
        if(((index!=0) && (dontGoAheadFlag1))) {
            listViewHolder.checkBox.setClickable(false);
        }
        else
        {
            listViewHolder.checkBox.setClickable(true);
        }



        if(list.get(position).get("state").equalsIgnoreCase("f"))
        {
            listViewHolder.checkBox.setChecked(false);


        }
        else if(list.get(position).get("state").equalsIgnoreCase("t"))
        {
            listViewHolder.checkBox.setChecked(true);
            statusVals.add(list.get(position).get("id"));
        }


        listViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setFunctionality(position, isChecked, listViewHolder, name);




            }
        });
      /*  listViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setFunctionality(position,listViewHolder.checkBox.isChecked(),listViewHolder,name);

            }
        });*/
    }

    private void setFunctionality(int position, boolean isChecked, ListViewHolder listViewHolder, String name) {


        if(isChecked) {
            // filterValue=
            listViewHolder.checkBox.setChecked(true);
            System.out.println("status value list "+list);
            statusVals.add(list.get(position).get("id"));
            System.out.println("status value "+statusVals  +"filter values  "+filterValue);
            showSubOptionsForMultiFilter(list, position);


        }
        else
        {
            listViewHolder.checkBox.setChecked(false);
            if(statusVals.contains(list.get(position).get("id"))) {
                statusVals.remove(list.get(position).get("id"));
            }
            showSubOptionsForMultiFilter(list, position);
        }

        HashSet<String> hashSet = new HashSet<String>(statusVals);
        statusVals.clear();

        statusVals.addAll(hashSet);
        if(statusVals.size()>0) {
            filterValue = android.text.TextUtils.join(",", statusVals);
        }
        else
        {
            filterValue=null;
        }
        for(int i=0;i<params.size();i++)

        {
            if(params.containsKey(filterName))
            {
                params.put(filterName,filterValue);
            }
        }
        if((index==0)&& (statusVals.size()>2))
        {
            dontGoAheadFlag1=true;
        }
        if((index!=0) && (statusVals.size()>4))
        {
            dontGoAheadFlag2=true;

        }

        // paramsList.add(params);

        System.out.println("status values and params are "+statusVals  +"params "+    params  );
        ((GraphDetailsActivity)ctx).sendFilterParams(params);



    }

    private void showSubOptionsForMultiFilter(ArrayList<HashMap<String, String>> list, int position) {

        {

            newlyTablesList=new ArrayList<>();
            ArrayList<HashMap<String,String>> newlist=new ArrayList<>();



            // tablesData2.addAll(allTablesData);
            db_handler.open();
            for(int k=0;k<filterNamesList.size();k++) {
                //   finalI2 = k;

                String listName = "list" + k;
                ArrayList<String> lis  = new ArrayList<>();


                if(k<=index) {


                    for (int m = 0; m < allTablesData.get(k).size(); m++) {

                        System.out.println("checked values on checking "+checkedVals);
                        if(checkedVals!=null) {
                            if ((checkedVals.contains(allTablesData.get(k).get(m).get("id"))||
                                    statusVals.contains(allTablesData.get(k).get(m).get("id")))) {
                                allTablesData.get(k).get(m).put("state", "t");
                            } else {
                                allTablesData.get(k).get(m).put("state", "f");
                            }
                        }
                        else
                        {
                            allTablesData.get(k).get(m).put("state", "f");
                        }

                        //if(!lists[0].contains(allTablesData.get(k)))
                    }

                    newlyTablesList.add(allTablesData.get(k));
                    if(k==index) {

                        Cursor cursor = db_handler.getMultiFilterData(list.get(position).get("id"), filterNamesList.get(k + 1).toUpperCase());


                        if (cursor != null) {
                            if (cursor.getCount() > 0) {
                                while (cursor.moveToNext()) {

                                    HashMap<String, String> filterData = new HashMap<>();
                                    String prevId = cursor.getString(0);
                                    String id = cursor.getString(1);
                                    String name = cursor.getString(2);
                                    filterData.put("prevId", prevId);
                                    filterData.put("id", id);
                                    filterData.put("name", name);
                                    System.out.println("status value " + statusVals + " id " + id);
                                    if (statusVals.contains(id)) {
                                        filterData.put("state", "t");
                                    } else {
                                        filterData.put("state", "f");
                                    }
                                    System.out.println("status value filterdata " + filterData);
                                    newlist.add(filterData);


                                }


                                System.out.println("adding in newlist with i  " + k + newlyTablesList);
                            }
                            System.out.println("new revised filter list " + newlist);
                        }

                        newFilterNamesList=new ArrayList<>();
                        for(int l=index+1;l<filterNamesList.size();l++)
                        {
                            newFilterNamesList.add(filterNamesList.get(l));
                        }
                        System.out.println("new filtered names list "+newFilterNamesList);
                    }


                }

/*                if (k == 0) {
                    for(int m=0;m<allTablesData.get(k).size();m++) {
                        if (statusVals.contains(allTablesData.get(k).get(m).get("id"))) {
                            allTablesData.get(k).get(m).put("state", "t");
                        } else {
                            allTablesData.get(k).get(m).put("state", "f");
                        }


                    }

                    // newlist.addAll(allTablesData.get(k));


                    newlyTablesList.add(allTablesData.get(k));

                    System.out.println("clicked val "+list.get(position).get("name"));
                    Cursor cursor = db_handler.getMultiFilterData(list.get(position).get("id"), filterNamesList.get(k+1).toUpperCase());

                    if (cursor != null) {
                        if (cursor.getCount() > 0) {
                            while (cursor.moveToNext()) {

                                HashMap<String, String> filterData = new HashMap<>();
                                String prevId = cursor.getString(0);
                                String id = cursor.getString(1);
                                String name = cursor.getString(2);
                                filterData.put("prevId", prevId);
                                filterData.put("id", id);
                                filterData.put("name", name);
                                System.out.println("status value "+statusVals +" id "+id);
                                if(statusVals.contains(id)) {
                                    filterData.put("state", "t");
                                }
                                else
                                {
                                    filterData.put("state", "f");
                                }
                                System.out.println("status value filterdata "+filterData);
                                newlist.add(filterData);



                            }



                            System.out.println("adding in newlist with i  "+  k + newlyTablesList);
                        }
                        System.out.println("new revised filter list " + newlist);
                    }
                    newFilterNamesList=new ArrayList<>();
                    for(int l=1;l<filterNamesList.size();l++)
                    {
                        newFilterNamesList.add(filterNamesList.get(l));
                    }
                    System.out.println("new filtered names list "+newFilterNamesList);

                    //recreateViewsAndShowData(newlist);
                }*/ else
                {

                    ArrayList<HashMap<String,String>> locallist=new ArrayList<>();
                    for (int j = 0; j <newlist.size(); j++) {
                        db_handler.open();
                        Cursor cursor = db_handler.getMultiFilterData(newlist.get(j).get("id"),filterNamesList.get(k).toUpperCase());
                        if (cursor != null) {
                            if (cursor.getCount() > 0) {
                                while (cursor.moveToNext()) {
                                    HashMap<String, String> filterData = new HashMap<>();
                                    String prevId = cursor.getString(0);
                                    String id = cursor.getString(1);
                                    String name = cursor.getString(2);
                                    filterData.put("prevId", prevId);
                                    filterData.put("id", id);
                                    filterData.put("name", name);
                                    if(statusVals.contains(id)) {
                                        filterData.put("state", "t");
                                    }
                                    else
                                    {
                                        filterData.put("state", "f");
                                    }
                                    // filterData.put("state","f");
                                    locallist.add(filterData);
                                    System.out.println("filter data is "+filterData);
                                    // dialog.dismiss();
                                }

                            }


                        }
                        cursor.close();

                    }
                    newlyTablesList.add(newlist);



                    System.out.println("newlist on adding "+newlyTablesList);
                    newlist=new ArrayList<>();
                    newlist.addAll(locallist);


                    locallist=new ArrayList<>();



                }

            }
//finalListOfAllCheckBoxes.addAll(newlyTablesList);
            resultsList.add(newlyTablesList);
            ArrayList<ArrayList<HashMap<String,String>>> obtainedList = new ArrayList<>();
            System.out.println("newly list size "+newlyTablesList.size() + "result list size "+resultsList.size() +   "and" +resultsList.get(0).size());
            for(int i=0;i<resultsList.get(0).size();i++)
            {
                System.out.println("i is "+ i);
                ArrayList<HashMap<String, String>> locallist=new ArrayList<>();
                for(int j=0;j<resultsList.size();j++) {
                    System.out.println("j and i ["+ j +i +"]");
                    locallist.addAll(resultsList.get(j).get(i));
                }
                obtainedList.add(locallist);
            }



            System.out.println(" obtained list with its size is  "+obtainedList.size() +"    "+obtainedList  );
            recreateAllTheViewsAndSetData(obtainedList);
            System.out.println("nwly tableslist is "+newlyTablesList);





        }




    }

    private void recreateAllTheViewsAndSetData(final ArrayList<ArrayList<HashMap<String, String>>> newlyTablesList) {

        final ArrayList<HashMap<String,String>> list=new ArrayList<>();


        final ViewGroup parentView = (ViewGroup) ((AppCompatActivity)ctx).findViewById(R.id.filterLayout);
        final Button btn = (Button)parentView.getChildAt(0);
      /*  btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("sending filterdata "+ newlyTablesList.get(0));
                ((GraphDetailsActivity)ctx).updateData(newlyTablesList.get(0),  btn.getText().toString(),0);
            }
        });*/
        for(int i=index; i < parentView.getChildCount(); i++) {
            final Button childView = (Button)parentView.getChildAt(i);
            System.out.println("getting the children"+childView );
            int resID = childView.getId();

            final int finalI = i;
            childView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((GraphDetailsActivity)ctx).updateData(newlyTablesList.get(finalI),childView.getText().toString(),finalI,params,newlyTablesList);
                }
            });





        }



    }


   /* @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        listViewHolder.checkBox.setChecked(false);
        super.onViewRecycled(listViewHolder);
    }*/

    private  class ListViewHolder extends RecyclerView.ViewHolder {

        final TextView textview;
        final CheckBox checkBox;
        final LinearLayout linearLayout;
        ListViewHolder(View itemView) {
            super(itemView);
            textview=(TextView)itemView.findViewById(R.id.tv_name);
            checkBox=(CheckBox)itemView.findViewById(R.id.cb);
            linearLayout=(LinearLayout)itemView.findViewById(R.id.textLayout);

        }


    }

    private class ListFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            finalList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                results.values = list;
                results.count = list.size();
            } else {

                filteredList = new ArrayList<>();


                for (int i = 0; i < list.size(); i++) {
                    String name  = list.get(i).get("name");


                    if (name.toUpperCase().contains(constraint.toString().toUpperCase())) {
                        // if `contains` == true then add it
                        // to our filtered list
                        filteredList.add(list.get(i));
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

            finalList = (ArrayList<HashMap<String, String>>) results.values;
            notifyDataSetChanged();

        }
    }

}
