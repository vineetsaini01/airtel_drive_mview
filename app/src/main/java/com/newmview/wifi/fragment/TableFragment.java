package com.newmview.wifi.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.mview.airtel.R;
import com.dashboard.activity.GraphDetailsActivity;
import com.newmview.wifi.other.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class TableFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    TableLayout db_tblLayout;
    Context context;
    private int deviceWidth;
    private int deviceHeight;
    private View view;
    private TableLayout tableLayout;
    private ArrayList<HashMap<String, String>> tablelist;
    private ArrayList<ArrayList<HashMap<String, String>>> graphFinalList;
    private ArrayList<String> colnames;
    private Bundle bundle;
    private Spinner goToSpinner, rowsSpinner;
    private ArrayList<HashMap<String, String>> percentData;
    private TableRow tableRow;
    private int totalCount;
    private ArrayList<Integer> rowsList;
    private EditText goToEt;
    private String upperIndex;
    private String lowerIndex = "1";
    private boolean tableFlag = false;
    private PercentTableFragment percentTable;
    private FrameLayout tableFrame;
    private FragmentManager fragmentManager;
    private PiegraphWithTable pieGraphWithTable;
    private ArrayList<HashMap<String,String>> pieDataColumns;
    private ArrayList<HashMap<String,String>> pieDataNamesList;
    private String item;
    private ArrayList<ArrayList<HashMap<String, String>>> tableAllColumnsDataList;
    private HashMap<String, String> selectedVal;


    public void onCreate(Bundle icici) {
        super.onCreate(icici);
        try {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            deviceWidth = displayMetrics.widthPixels;
            deviceHeight = displayMetrics.heightPixels;
            Log.e("Width, Height:: ", deviceWidth + ", " + deviceHeight);
            Log.e("Width/2, Height/2:: ", deviceWidth / 2 + ", " + deviceHeight / 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.table, container, false);
        tableFrame=view.findViewById(R.id.tableFrame);

        init();
        loadFragment();


        return view;
    }


    private void loadFragment() {


        if(getActivity()!=null) {
            fragmentManager = getActivity().getSupportFragmentManager();
            Bundle bundle = new Bundle();
            if(GraphDetailsActivity.ctype.equalsIgnoreCase("tablewithpercent") ||
                    GraphDetailsActivity.ctype.equalsIgnoreCase("table")) {
                percentTable = new PercentTableFragment();
                fragmentManager.beginTransaction().replace(R.id.tableFrame, percentTable).commit();
                bundle.putSerializable("tablelist", tablelist);
                bundle.putSerializable("colnameslist", colnames);
                bundle.putSerializable("percentDatalist", percentData);
                bundle.putString("totalCount", String.valueOf(totalCount));
                bundle.putSerializable("tableAllColumnsDataList",this.tableAllColumnsDataList);

                percentTable.setArguments(bundle);
            }
            else
            {
                pieGraphWithTable=new PiegraphWithTable();
                fragmentManager.beginTransaction().replace(R.id.tableFrame,pieGraphWithTable).commit();
                bundle.putSerializable("tablelist", tablelist);
                bundle.putSerializable("colnameslist", colnames);
                bundle.putSerializable("percentDatalist", percentData);
                bundle.putString("totalCount", String.valueOf(totalCount));
                bundle.putSerializable("pieDataColums",this.pieDataColumns);
                bundle.putSerializable("pieDataNamesList",this.pieDataNamesList);
                pieGraphWithTable.setArguments(bundle);

            }
        }
    }

    private void init() {
        bundle = getArguments();
        if (Utils.checkContext(getActivity())) {
            goToEt = view.findViewById(R.id.goToEt);
            rowsSpinner = view.findViewById(R.id.rowsSpinner);
           // rowsSpinner.setOnItemSelectedListener(this);
            if (bundle != null) {
                tablelist = (ArrayList<HashMap<String, String>>) bundle.getSerializable("tablelist");
                colnames = (ArrayList<String>) bundle.getSerializable("colnameslist");
                percentData = (ArrayList<HashMap<String, String>>) bundle.getSerializable("percentDatalist");
                String ct=bundle.getString("totalCount");
                if(Utils.checkifavailable(ct))
                totalCount = Integer.valueOf(ct);
               // selectedVal=(HashMap<String,String>)bundle.getSerializable("selectedVal");
                if(GraphDetailsActivity.ctype.equalsIgnoreCase("tablewithpie"))
                {
                    pieDataColumns=(ArrayList<HashMap<String, String>>) bundle.getSerializable("pieDataColums");
                    pieDataNamesList=(ArrayList<HashMap<String, String>>) bundle.getSerializable("pieDataNamesList");


                }
                else
                {

                    tableAllColumnsDataList= (ArrayList<ArrayList<HashMap<String, String>>>)bundle.getSerializable("tableAllColumnsDataList");
                System.out.println("table all columns in getting from bundle "+tableAllColumnsDataList);

                }

                lowerIndex=bundle.getString("lowerIndex");
                upperIndex=bundle.getString("upperIndex");
                if(!Utils.checkifavailable(lowerIndex))
                {
                    lowerIndex="1";
                }


                System.out.println("table list " + tablelist);
                System.out.println("table col names " + colnames);
                System.out.println("table percent data " + percentData);
                System.out.println("table lower index "+lowerIndex);
                System.out.println("table upper index "+upperIndex);
                System.out.println("table all columns "+tableAllColumnsDataList);
                rowsList = new ArrayList<>();

                int count = totalCount / 10;
                int remainder = totalCount % 10;
                if (totalCount <= 100) {
                    for (int i = 1; i < count; i++) {
                        rowsList.add(10 * i);
                    }
                    rowsList.add((10 * count) + remainder);
                }


            }

        }


        goToEt.setText(lowerIndex);
        lowerIndex = goToEt.getText().toString();
       goToEt.addTextChangedListener(textWatcher);
        setValuesToSpinner();
        if(!Utils.checkifavailable(upperIndex))
        {

            upperIndex= String.valueOf(rowsSpinner.getSelectedItem());
        }


    }

    private void setValuesToSpinner() {
        if (rowsList != null && rowsList.size() > 0) {
            SpinnerInteractionListener listener = new SpinnerInteractionListener();
           rowsSpinner.setOnTouchListener(listener);
           rowsSpinner.setOnItemSelectedListener(listener);

           Utils.showToast(getActivity(), "setting values");
            ArrayAdapter<Integer> spinneritems = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, rowsList);
            spinneritems.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            rowsSpinner.setAdapter(spinneritems);
            if(Utils.checkifavailable(lowerIndex)) {
                rowsSpinner.setSelection(Utils.getPosition(rowsList, upperIndex));
            }
            else
            {
                rowsSpinner.setSelection(0);
            }



        }


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        upperIndex = item;
       
       /* if (parent.getId() == R.id.rowsSpinner) {
            Utils.showToast(getActivity(),"item selected");
            upperIndex = rowsSpinner.getSelectedItem().toString();
            tableFlag = true;

        }*/
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private TextWatcher textWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {

        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            if(s.length()>0) {
                tableFlag = true;
                lowerIndex= String.valueOf(s);
                if(item!=null)
                {
                    upperIndex=item;
                }
                try {
                    int i1 = Integer.parseInt(lowerIndex);
                    int i2 = Integer.parseInt(upperIndex);
                    if (i1 <= i2) {


                        ((GraphDetailsActivity) getActivity()).setIndexValues(lowerIndex, upperIndex, tableFlag);
                    } else {
                        Utils.showToast(getActivity(), "Please enter a valid value!!");
                    }
                }
                catch (NumberFormatException e)
                {
                    e.printStackTrace();
                }
            }
        }
    };

    private class SpinnerInteractionListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {

        boolean userSelect = false;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
          //  Utils.showToast(getActivity(),"user touched");
            userSelect = true;
            return false;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (userSelect) {
            //    Utils.showToast(getActivity(),"item selected");
                // Your selection handling code here
                userSelect = false;
                item = parent.getItemAtPosition(position).toString();
                upperIndex = item;
                tableFlag=true;
                ((GraphDetailsActivity) getActivity()).setIndexValues(lowerIndex, upperIndex, tableFlag);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

}
