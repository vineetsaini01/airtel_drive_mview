package com.newmview.wifi.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.newmview.wifi.MyPhoneStateListener;
import com.mview.airtel.R;
import com.newmview.wifi.helper.AllInOneAsyncTaskForNetwork;
import com.newmview.wifi.helper.RequestResponse;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.NeighboringCellsInfo;
import com.newmview.wifi.other.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.newmview.wifi.network.NetworkUtil.getNeighboringCellsInfo;
import static com.newmview.wifi.network.NetworkUtil.getNeighboringCellsInfoForGSM;
import static com.newmview.wifi.network.NetworkUtil.getNeighboringCellsInfoForLte;
import static com.newmview.wifi.network.NetworkUtil.getNeighboringCellsInfoForWcdma;

public class NeighboringCellFragment extends Fragment {
    private static final String TAG ="NeighboringCellFragment" ;
    View v;
    boolean dontRefreshScreen;
    Handler mHandler;
    int refreshFrequency = 30;


    private TableRow tableRow;
    private LinearLayout mainlayout;
    private ProgressDialog progressDialog;
    private View viewDivider;

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG,"Updating ui called from 1");
        updateUI();
        useHandler();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.neighboring_cell_info,container,false);
        showProgress();
        init();
        sendRequest();
        return v;
    }


    private void sendRequest() {
        try {


            JSONObject neighboringJsonObj = new JSONObject();
            if (MyPhoneStateListener.getNetworkType() == 4) {


                neighboringJsonObj.put("neighboring_cells_info", getNeighboringCellsInfoForLte());

            } else if (MyPhoneStateListener.getNetworkType() == 3) {
                neighboringJsonObj.put("neighboring_cells_info", getNeighboringCellsInfoForWcdma());


            } else if (MyPhoneStateListener.getNetworkType() == 2) {
                neighboringJsonObj.put("neighboring_cells_info", getNeighboringCellsInfoForGSM());

            } else {
                neighboringJsonObj.put("neighboring_cells_info", getNeighboringCellsInfo());//27

            }
            JSONArray jsonArray=new JSONArray();
            jsonArray.put(neighboringJsonObj);
            RequestResponse.sendEvent(jsonArray, AllInOneAsyncTaskForNetwork.AsyncTaskPurpose.NEIGHBORING_CELLS_INFO,"neighboring_cells");
        }

      catch (JSONException e)
      {
          e.printStackTrace();
      }

    }

    private void useHandler() {
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable,
                0);
    }


    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            if (getActivity() != null) {
                if (dontRefreshScreen == false) {
                    try {
                        Log.i(TAG,"Updating ui called from 2");
                        updateUI();


                      //  mHandler.postDelayed(mRunnable, refreshFrequency * 1000);
                    } catch (Exception e) {
                        System.out.println("Exception is " + e.toString());
                        e.printStackTrace();
                    } finally {
                        mHandler.postDelayed(mRunnable, refreshFrequency * 1000);
                    }
                }
            }
        }
    };

    private void updateUI() {
        Log.i(TAG,"Updating ui at "+ Utils.getDateTime());
        mainlayout.removeAllViews();
        updateUIWithValues(NeighboringCellsInfo.neighboringCellList,NeighboringCellsInfo.lteParams);
        updateUIWithValues(NeighboringCellsInfo.wcdma_neighboringCellList,NeighboringCellsInfo.wcdmaParams);
        updateUIWithValues(NeighboringCellsInfo.gsm_neighboringCellList,NeighboringCellsInfo.gsmParams);

       /* if(MyPhoneStateListener.getNetworkType()==4) {


            updateUIWithValues(NeighboringCellsInfo.neighboringCellList,NeighboringCellsInfo.lteParams);
        }
       if(MyPhoneStateListener.getNetworkType()==3)
        {

            updateUIWithValues(NeighboringCellsInfo.wcdma_neighboringCellList,NeighboringCellsInfo.wcdmaParams);
        }
       if(MyPhoneStateListener.getNetworkType()==2)
        {

            updateUIWithValues(NeighboringCellsInfo.gsm_neighboringCellList,NeighboringCellsInfo.gsmParams);
        }
        else
        {
        }*/
        dismissProgress();
    }


    @Override
    public void onPause() {
        super.onPause();
        removeCallbacks();

    }

    private void removeCallbacks() {

        try {
            mHandler.removeCallbacks(mRunnable);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeCallbacks();

    }

    private void updateUIWithValues(ArrayList<HashMap<Integer, Integer>> neighboringCellList, ArrayList<String> params) {


        {
            if(((neighboringCellList!=null) && neighboringCellList.size()>0)&&
                    (params!=null && params.size()>0)){

                //mainlayout.removeAllViews();

                for(int i=0;i<neighboringCellList.size();i++) {
                    if (getActivity() != null) {
                        System.out.println("fetching");

                        TableLayout tableLayout = new TableLayout(getActivity());

                        // TableLayout tableLayout=v.findViewById(R.id.cells_table);
                        tableLayout.setStretchAllColumns(true);
                        tableLayout.setPadding(15, 2, 2, 2);
                        tableLayout.setGravity(Gravity.CENTER);
                        ViewGroup.LayoutParams layoutParams1 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                        TextView cell = new TextView(getActivity());
                        cell.setLayoutParams(layoutParams1);
                        cell.setText(String.format("NEIGHBORING CELL %s", String.valueOf(i)));
                        cell.setTextColor(getResources().getColor(R.color.app_theme));
                        cell.setPadding(10, 5, 5, 2);
                        cell.setGravity(Gravity.CENTER);
                        tableLayout.addView(cell);
                        for (int j = 0; j < neighboringCellList.get(0).size(); j++) {
                            if (!params.get(j).equalsIgnoreCase("signalstrength"))
                            {
                                tableRow = new TableRow(getActivity());
                            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                            tableRow.setLayoutParams(lp);


                            TextView tv_name = new TextView(getActivity());

                            // ViewGroup.LayoutParams layoutParams1 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            TextView tv_val = new TextView(getActivity());
                            tableRow.addView(tv_name);
                            tableRow.addView(tv_val);

                            tv_name.setText(params.get(j).substring(3));
                            tv_name.setPadding(15, 2, 1, 1);
                            tv_name.setGravity(Gravity.START);
                            tv_name.setTextColor(getResources().getColor(R.color.textColor));
                            tv_val.setPadding(2, 2, 1, 1);
                            tv_val.setGravity(Gravity.LEFT);
                            String value = String.valueOf(neighboringCellList.get(i).get(j));
                            if (value == null || Integer.parseInt(value) == Integer.MAX_VALUE) {
                                tv_val.setText("0");
                            } else {
                                tv_val.setText(String.valueOf(neighboringCellList.get(i).get(j)));
                            }

                            View viewDivider = new View(getActivity());
                            int dividerHeight = (int) (getResources().getDisplayMetrics().density * 1); // 1dp to pixels
                            viewDivider.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dividerHeight));
                            viewDivider.setBackgroundColor(getResources().getColor(R.color.lightgrey));
                            tableLayout.addView(viewDivider);

                            tableLayout.addView(tableRow);
                        }
                    }
                    viewDivider = new View(getActivity());
                    int dividerHeight = (int) (getResources().getDisplayMetrics().density * 2); // 1dp to pixels
                    viewDivider.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dividerHeight));
                    viewDivider.setBackgroundColor(getResources().getColor(R.color.app_theme));

                    mainlayout.addView(tableLayout);
                    mainlayout.addView(viewDivider);
                    }
                }





            }
        }
    }

    private void dismissProgress() {

        if(progressDialog!=null && progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }
    }

    private void showProgress() {
        if(getActivity()!=null) {
            progressDialog = new ProgressDialog(getActivity(), ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
            progressDialog.setMessage(Constants.LOADING);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

    }

    private void init() {
        mainlayout=(LinearLayout)v.findViewById(R.id.layout);
    }




}
