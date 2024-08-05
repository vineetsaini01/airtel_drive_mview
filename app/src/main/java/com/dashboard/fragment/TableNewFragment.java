package com.dashboard.fragment;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import android.widget.LinearLayout;
import androidx.appcompat.widget.SearchView;

import android.widget.Spinner;
import android.widget.TextView;

import com.dashboard.activity.GraphDetailsActivity;
import com.dashboard.adapter.TableViewAdapter;
import com.dashboard.interfaces.ShowResetSortingButton;
import com.evrencoskun.tableview.TableView;
import com.evrencoskun.tableview.filter.FilterChangedListener;
import com.evrencoskun.tableview.filter.Filter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mview.airtel.R;
import com.newmview.wifi.bean.Cell;
import com.newmview.wifi.bean.ColumnHeader;
import com.newmview.wifi.bean.RowHeader;
import com.newmview.wifi.helper.CommonUtil;
import com.newmview.wifi.helper.MyTableViewListener;
import com.newmview.wifi.other.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class TableNewFragment extends Fragment implements View.OnClickListener, SearchView.OnQueryTextListener, SearchView.OnCloseListener, ShowResetSortingButton {
    private View view;
    private TableView tableView;
    ArrayList<Integer> arrList = new ArrayList<Integer>();
    List<ColumnHeader> mColumnHeaderList;
    private ArrayList<ArrayList<HashMap<String, String>>> tableAllColumnsDataList;
    private ArrayList<String> columns = new ArrayList<>();
    private List<RowHeader> mRowHeaderList = new ArrayList<>();
    private List<List<Cell>> mCellList = new ArrayList<>();
    private EditText goToEt;
    private String upperIndex;
    private String lowerIndex = "1";
    private TextView maxPageTv, minPageTv, dotsTv,arrow;
    private ArrayList<Integer> rowsList;
    private Spinner goToSpinner, rowsSpinner;
    private Button resetSorting;
    private FloatingActionButton scrollButton;
    private TableViewAdapter adapter;
    private Filter tableViewFilter;
    private SearchView searchView;
    private String item;
    private boolean tableFlag;
    private TextView noData;
    private CardView paginationCard;
    private LinearLayout tableLL;
    private String column_mask;
    private ArrayList<HashMap<String, String>> columnsList = new ArrayList<>();
    private ArrayList<ArrayList<HashMap<String, String>>> mainList;
    private Button resetMasking;
    private long totalCount;
    private String length;
    private String selectedVal;
    private long start = 1;
    private TextView startAndEndIndexTv;
    private long initialIndex = 0;
    private String pageNum = "1";
    private String previousLength;
    private LinearLayout pageLL;
    private ArrayList<String> graphIdList, drillXAxisList;
    private int drillIndex;
    private String searchedKeyword;

    private int lastIndex;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_table_new, container, false);
        getExtras();
        init();
        if (totalCount > 1) {
            System.out.println("total count " + totalCount);
            pageLL.setVisibility(View.VISIBLE);
            maxPageTv.setText(totalCount + "");
        } else {
            System.out.println("total count " + totalCount);
            pageLL.setVisibility(View.GONE);
        }
        System.out.println("total count is " + totalCount);
        if (rowsList.size() > 0) {
            if (Utils.checkifavailable(selectedVal)) {
                int count = (int) (totalCount / Integer.parseInt(selectedVal));
                if (rowsList.size() > 0) {
                    if (rowsList.get(0) != 0) {
                        int rem = (int) (totalCount % rowsList.get(0));
                        if (rem != 0) {
                            count++;
                        }
                        length = String.valueOf(count);
                        maxPageTv.setText(String.valueOf(count));
                    }
                }
            } else if (rowsList.get(0) != 0) {
                int count = (int) (totalCount / rowsList.get(0));
                int rem = (int) (totalCount % rowsList.get(0));
                if (rem != 0) {
                    count++;
                }
                selectedVal = String.valueOf(rowsList.get(0));
                length = String.valueOf(count);
                maxPageTv.setText(String.valueOf(count));
            }
        }
        if (Utils.checkifavailable(column_mask)) {
            if (column_mask.equalsIgnoreCase("true")) {
                resetMasking.setVisibility(View.VISIBLE);
            } else {
                resetMasking.setVisibility(View.GONE);
            }
        }
        if (tableAllColumnsDataList != null) {
            if (tableAllColumnsDataList.size() > 0) {
                noData.setVisibility(View.GONE);
                tableLL.setVisibility(View.VISIBLE);
                setFilter();
                setAdapter();
                setValuesToSpinner();
            } else {
                Utils.showAlert(CommonUtil.TABLE_ERROR, getActivity());
                noData.setVisibility(View.VISIBLE);
                tableLL.setVisibility(View.GONE);
            }

        } else {
            Utils.showAlert(CommonUtil.TABLE_ERROR, getActivity());
            noData.setVisibility(View.VISIBLE);
            tableLL.setVisibility(View.GONE);
        }
        startAndEndIndexTv.setText("(" + pageNum + ")");
        return view;
    }

    private void setValuesToSpinner() {
        if (rowsList != null && rowsList.size() > 0) {
            System.out.println("length while setting " + length);
            SpinnerInteractionListener listener = new SpinnerInteractionListener();
            rowsSpinner.setOnTouchListener(listener);
            rowsSpinner.setOnItemSelectedListener(listener);
            ArrayAdapter<Integer> spinneritems = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, rowsList);
            spinneritems.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            rowsSpinner.setAdapter(spinneritems);
            if (Utils.checkifavailable(selectedVal)) {
                rowsSpinner.setSelection(Utils.getPosition(rowsList, selectedVal
                ));
            } else {
                rowsSpinner.setSelection(0);
            }


        }


    }

    private void setFilter() {
        tableViewFilter = new Filter(tableView);
    }

    private void setAdapter() {
        adapter = new TableViewAdapter(getContext());
        tableView.setAdapter(adapter);
        tableView.setTableViewListener(new MyTableViewListener(getActivity()));
        if (tableAllColumnsDataList != null) {
            if (tableAllColumnsDataList.size() > 0) {
                getColumnHeaderList();
                getRowHeaderList();
                getCellList();
                adapter.setAllItems(mColumnHeaderList, mRowHeaderList, mCellList);

            }
        }
    }

    private void getExtras() {
        Bundle bundle = getArguments();
        tableAllColumnsDataList = (ArrayList<ArrayList<HashMap<String, String>>>) bundle.getSerializable("tableAllColumnsDataList");
        System.out.println("table all columns in getting from bundle " + tableAllColumnsDataList);
        mainList = (ArrayList<ArrayList<HashMap<String, String>>>) bundle.getSerializable("mainList");
        column_mask = bundle.getString("column mask");
        totalCount = bundle.getLong("totalCount");
        String str = bundle.getString("start");
        initialIndex = bundle.getLong("initialIndex");
        pageNum = bundle.getString("pageNum");
        graphIdList = bundle.getStringArrayList("graphIdList");
        drillXAxisList = bundle.getStringArrayList("drillXAxisList");
        if (!Utils.checkifavailable(pageNum)) {
            pageNum = "1";
        }
        if (Utils.checkifavailable(str)) {
            start = Long.parseLong(str);
        }
        String len = bundle.getString("length");
        if (Utils.checkifavailable(len)) {
            selectedVal = len;
        }
        System.out.println("start and length initially " + start + " and   " + len);
        addItemsForSpinner();
        System.out.println("main list while getting " + mainList);
        //columnsList= (ArrayList<HashMap<String, String>>) bundle.getSerializable("columnsList");
    }

    private void addItemsForSpinner() {
        rowsList = new ArrayList<>();
        rowsList.add(10);
        rowsList.add(25);
        rowsList.add(50);
        rowsList.add(100);
      /*  if(totalCount>=100) {
            rowsList.add(10);
            rowsList.add(25);
            rowsList.add(50);
            rowsList.add(100);
            System.out.println("rows list in 1 "+rowsList);
        }
        else {
            if (totalCount >= 50) {
                rowsList.add(10);
                rowsList.add(25);
                rowsList.add(50);
                int count = (int) (totalCount - 75);
                       *//* rowsList.add(10);
                        rowsList.add(25);

                        int count=totalCount-35;*//*
                rowsList.add(count);
                System.out.println("rows list in 2 " + rowsList);
            } else if (totalCount >= 25) {
                rowsList.add(10);
                rowsList.add(25);
                int count = (int) (totalCount - 35);
                rowsList.add(count);
                       *//* rowsList.add(10);
                        int count=totalCount-10;
                        rowsList.add(count);*//*
                System.out.println("rows list in 3 " + rowsList);
            } else if (totalCount >= 10) {
                rowsList.add(10);
                int count = (int) (totalCount - 10);
                rowsList.add(count);
                System.out.println("rows list in 4 " + rowsList);
            } else {
                rowsList.add((int) totalCount);
                System.out.println("rows list in 5 " + rowsList);
            }
            }
*/

    }

    private void init() {
        goToEt = view.findViewById(R.id.goToEt);
        rowsSpinner = view.findViewById(R.id.rowsSpinner);
        pageLL = view.findViewById(R.id.pageLL);
        maxPageTv = view.findViewById(R.id.maxPageTv);
        minPageTv = view.findViewById(R.id.minPageTv);
        dotsTv = view.findViewById(R.id.dotsTv);
        tableLL = view.findViewById(R.id.tableLL);
        startAndEndIndexTv = view.findViewById(R.id.startAndEndIndexTv);
        maxPageTv.setOnClickListener(this);
        minPageTv.setOnClickListener(this);
        paginationCard = view.findViewById(R.id.paginationCard);
        dotsTv.setOnClickListener(this);
        noData = view.findViewById(R.id.noData);
        tableView = view.findViewById(R.id.content_container);
        if (getActivity() instanceof GraphDetailsActivity) {
//            resetMasking = getActivity().findViewById(R.id.resetMasking);
//            resetSorting = getActivity().findViewById(R.id.resetSorting);
            searchView = getActivity().findViewById(R.id.search);


//            scrollButton=getActivity().findViewById(R.id.scrollButton);
//            scrollButton.setOnClickListener( new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    lastIndex = getCellIndex(searchedKeyword,tableAllColumnsDataList);
//                    System.out.println("index is "+lastIndex+" serched key "+searchedKeyword);
//                    if(lastIndex!=-1)
//                    {
//                        tableView.scrollToColumnPosition(lastIndex);
//                    }
//
//
//
//
//                }
//
//            });
            searchView.setVisibility(View.VISIBLE);
            searchView.setOnQueryTextListener(this);
            searchView.setOnCloseListener(this); }
        //resetSorting=getActivity().findViewById(R.id.resetSorting);
//        resetSorting.setOnClickListener(this);
        tableView.setIgnoreSelectionColors(true);
        tableView.setSelectedColor(getActivity().getResources().getColor(R.color.app_theme));
//        resetMasking.setOnClickListener(this);
    }

    private void getColumnHeaderList() {
        mColumnHeaderList = new ArrayList<>();
        // Let's set datas of the TableView on the Adapter
        System.out.println("colum mask value " + column_mask);
        addForOthers();
         /*
        if(Util.checkifavailable(column_mask))
        {
            if(column_mask.equalsIgnoreCase("true"))
            {
            if(columnsList!=null)
            {
                if(columnsList.size()>0)
                {
                    for(int i=0;i<columnsList.size();i++)
                    {
                        String title=columnsList.get(i).get("id");
                        String state=columnsList.get(i).get("state");
                        String titleFromTable=tableAllColumnsDataList.get(0).get(i).get("name");
                        if(Util.checkifavailable(state)) {
                            if (state.equalsIgnoreCase("t")) {
                                ColumnHeader header = new ColumnHeader(String.valueOf(i), title);
                                mColumnHeaderList.add(header);
                            }
                            else
                            {
                                if(Util.checkifavailable(title)) {
                                   int index= getIndex(title, tableAllColumnsDataList);
                                   if(index!=-1)
                                   {
                                       tableAllColumnsDataList.remove(i);
                                   }
                                }
                            }
                        }
                    }
                }
            }
        }
            else
            {
                addForOthers();
            }
        }
        else {
            addForOthers();
        }
*/
        System.out.println("colum mask value list 1" + columnsList);
        System.out.println("colum mask value list 2" + mColumnHeaderList);
    }

    private int getIndex(String title, ArrayList<ArrayList<HashMap<String, String>>> tableAllColumnsDataList) {
        for (int i = 0; i < tableAllColumnsDataList.get(0).size(); i++) {
            String titleFromTable = tableAllColumnsDataList.get(0).get(i).get("name");
            if (Utils.checkifavailable(titleFromTable)) {
                if(titleFromTable.toLowerCase().contains(title.toLowerCase()))
                // if (titleFromTable.equalsIgnoreCase(title))
                {
                    return i;
                }
            }
        }

        return -1;
    }

    // swapnil bansal - 12/10/2021
    private int getCellIndex(String title, ArrayList<ArrayList<HashMap<String, String>>> tableAllColumnsDataList)
    {
        try {
            for (int i = 1; i < tableAllColumnsDataList.size(); i++) {
                System.out.println("index row wise " + i);
                for (int j = 0; j < tableAllColumnsDataList.get(0).size(); j++) {
                    System.out.println("Index is " + "[" + i + "]" + "[" + j + "]");
                    String titleFromTable = tableAllColumnsDataList.get(i).get(j).get("value");
                    if (Utils.checkifavailable(titleFromTable)) {
                        if (titleFromTable.toLowerCase().contains(title.toLowerCase())) {
                            if(lastIndex<j) {
                                System.out.println("finally returning index " + j);
                                return j;
                            }

                        }

                    }

                }
            }

           /* for (int i = 0; i < tableAllColumnsDataList.size(); i++) {
                System.out.println("index row wise " + i);
                for (int j = 0; j < tableAllColumnsDataList.get(i).size(); j++) {
                    System.out.println("Index is " + "[" + i + "]" + "[" + j + "]");
                    String titleFromTable = tableAllColumnsDataList.get(i).get(j).get("value");
                    System.out.println("title from table is "+titleFromTable );
                    if (Util.checkifavailable(titleFromTable)) {
                        if (titleFromTable.toLowerCase().contains(title.toLowerCase())) {
                            return j;
                        }
                    }
                }
            }
*/
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return -1;
    }

    private void addForOthers() {
        for (int i = 0; i < tableAllColumnsDataList.get(0).size(); i++) {
            String title = tableAllColumnsDataList.get(0).get(i).get("name");
            String state = tableAllColumnsDataList.get(0).get(i).get("state");
            if (Utils.checkifavailable(state)) {
                if (state.equalsIgnoreCase("t")) {
                    ColumnHeader header = new ColumnHeader(String.valueOf(i), title);
                    mColumnHeaderList.add(header);
                    System.out.println("title is " + title);
                }
            }

        }

    }

    private void getRowHeaderList() {
        mRowHeaderList = new ArrayList<>();
        long len = tableAllColumnsDataList.size() + initialIndex;
        System.out.println("initial index is " + initialIndex);
        for (long i = initialIndex; i < len; i++) {
            long text = i + 1;
            System.out.println("text is " + text);
            RowHeader header = new RowHeader(String.valueOf(i), "" + text);
            mRowHeaderList.add(header);
            //    System.out.println("row header list "+mRowHeaderList.get((int) i).getData());
        }
        for (int i = 0; i < mRowHeaderList.size(); i++) {
            // System.out.println("row header list "+mRowHeaderList.get(i).getData());
        }
    }

    private void getCellList() {
        mCellList = new ArrayList<>();
        for (int i = 0; i < tableAllColumnsDataList.size(); i++) {
            List<Cell> cellList = new ArrayList<>();
            for (int j = 0; j < tableAllColumnsDataList.get(i).size(); j++) {
                // Create dummy id.
                String state = tableAllColumnsDataList.get(0).get(j).get("state");
                String drill = tableAllColumnsDataList.get(i).get(j).get("drill");
                System.out.println("drill state is " + drill);
                String id = j + "-" + i;
                if (Utils.checkifavailable(state)) {
                    if (state.equalsIgnoreCase("t")) {
                        Cell cell;
                        if (Utils.checkifavailable(drill)) {
                            if (drill.equalsIgnoreCase("y")) {
                                System.out.println("yes its y....");
                                cell = new Cell(id, tableAllColumnsDataList.get(i).get(j).get("value"), 1);
                            } else {
                                cell = new Cell(id, tableAllColumnsDataList.get(i).get(j).get("value"), 0);
                            }
                        } else {
                            cell = new Cell(id, tableAllColumnsDataList.get(i).get(j).get("value"), 0);
                        }
                        if (i == 4) {
                            System.out.println("value is " + tableAllColumnsDataList.get(i).get(j).get("value"));
                        }
                        cellList.add(cell);
                    } } }
            int finalI = i;
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    if (finalI == 4) {
                        for (int ij = 0; ij < cellList.size(); ij++) {
                            System.out.println("index " + ij + " cell list is " + cellList.get(ij).getData() + " whereas value " +
                                    tableAllColumnsDataList.get(finalI).get(ij).get("value"));
                        }
                    }
                }
            });

            mCellList.add(cellList);
        }

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.resetMasking:
//                if (getActivity() instanceof GraphDetailsActivity) {
//                    resetMasking.setVisibility(View.GONE);
//                    System.out.println("main list before calling " + mainList);
//                    ((GraphDetailsActivity) getActivity()).showAllTabularColumns(mainList);
//                }
//                break;
            case R.id.minPageTv:
                sendRequestForPage(minPageTv.getText().toString());
                break;
            case R.id.dotsTv:
                openAlertForEnteringPageNum();
                break;
            case R.id.maxPageTv:
                if (Utils.checkifavailable(selectedVal)) {
                    int len = Integer.parseInt(selectedVal);
                    String max = maxPageTv.getText().toString();
                    int maxVal = (Integer.parseInt(max) * len) - len;
                    /*if (maxVal > 10) {
                        maxVal = maxVal - 10;
                    }*/
                    initialIndex = maxVal;
                    maxVal++;

                    sendRequestForPage(maxVal + "");
                }
                break;
//            case R.id.resetSorting:
//                setAdapter();
//                resetSorting.setVisibility(View.GONE);
//                // adapter.notifyDataSetChanged();
//                break;
           /* case R.id.resetSorting:
             //   adapter.getTableView().sortColumn(position, SortState.UNSORTED);
                break;*/





        }
    }

    private void openAlertForEnteringPageNum() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.enter_page_num_dialog);
        EditText pageEt = dialog.findViewById(R.id.pageEt);
        Button doneBtn = dialog.findViewById(R.id.doneBtn);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = pageEt.getText().toString();
                pageNum = text;
                if (Utils.checkifavailable(text)) {
                    int index = Integer.parseInt(text);
                    if (index <= Long.parseLong(length) && index >= 1) {
                        if (index == 1) {
                            start = 1;
                            initialIndex = 0;
                        } else {
                            if (selectedVal != null)
                                previousLength = selectedVal;
                            start = Long.parseLong(pageEt.getText().toString());
                            /*long len = Long.parseLong(selectedVal);
                            initialIndex = (index * len)-len;*/
                            long len = Long.parseLong(selectedVal);
                            start = (index * len) - len;
                            initialIndex = start;
                            start++;

                        }
                        System.out.println("start and length is " + start + " and " + selectedVal);
                        ((GraphDetailsActivity) getActivity()).setPaginationValues(String.valueOf(start), selectedVal, initialIndex, pageNum);
                        dialog.dismiss();
                    } else {
                        Utils.showToast(getActivity(), "Please enter a valid number ");
                    }
                } else {
                    Utils.showToast(getActivity(), "Please enter a page number!");
                }
            }
        });
        dialog.show();
    }

    private void sendRequestForPage(String val) {
        if (val.equals("1")) {
            initialIndex = 0; }
        pageNum = val;
        startAndEndIndexTv.setText(val);
        ((GraphDetailsActivity) getActivity()).setPaginationValues(val, selectedVal, initialIndex, pageNum);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query != null) {
            if (query.length() == 0) {
                Utils.showToast(getActivity(), "Please enter something to search.."); }
            else
            { }
        } else {
            Utils.showToast(getActivity(), "Please enter something to search..");
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
//        scrollButton.setVisibility(View.VISIBLE);
        tableViewFilter.set(newText);
        searchedKeyword=newText;
        adapter.setFilter(true, newText);
        return false;
    }

    @Override
    public boolean onClose() {
        adapter.setFilter(false, "");
        return false;
    }

    @Override
    public void showButton() {
        resetSorting.setVisibility(View.VISIBLE);
    }

    private class SpinnerInteractionListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {
        boolean userSelect = false;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //  Util.showToast(getActivity(),"user touched");
            userSelect = true;
            return false;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (userSelect) {
                //    Util.showToast(getActivity(),"item selected");
                // Your selection handling code here
                item = parent.getItemAtPosition(position).toString();
                int length = Integer.parseInt(selectedVal);
                int val = (int) (start / Integer.parseInt(item));
                start = (val * Integer.parseInt(item));
                initialIndex = start;
                int pgNo = (int) (start / (Integer.parseInt(item)));
                pgNo++;
                pageNum = pgNo + "";

/*
                if(length>start)
                {
                    start=(length*Integer.parseInt(pageNum))+1;
                    initialIndex=start;
                }
                else
                {

                }*/
                userSelect = false;
                selectedVal = item;
                //length = item;
                int ind = Integer.parseInt(selectedVal);
                int pages = (int) (totalCount / ind);
                int rem = (int) (totalCount % ind);
                if (rem != 0) {
                    pages++; }
                maxPageTv.setText(String.valueOf(pages));
                System.out.println("start  " + start + " length " + selectedVal);
                ((GraphDetailsActivity) getActivity()).setPaginationValues(String.valueOf(start), selectedVal, initialIndex, pageNum);
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    }

    private FilterChangedListener filterChangedListener = new FilterChangedListener() {
        @Override
        public void onFilterChanged(@NonNull List filteredCellItems, @NonNull List filteredRowHeaderItems) {
            super.onFilterChanged(filteredCellItems, filteredRowHeaderItems); }
        @Override
        public void onFilterCleared(@NonNull List originalCellItems, @NonNull List originalRowHeaderItems) {
            super.onFilterCleared(originalCellItems, originalRowHeaderItems);
        }
    };
}
