package com.newmview.wifi.BatteryGraphTypes;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.mview.airtel.R;
import com.newmview.wifi.helper.CustomMarkerView;
import com.newmview.wifi.mView_HealthStatus;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.newmview.wifi.application.MviewApplication.batteryList;
import static com.newmview.wifi.application.MviewApplication.timeStamp;
public class BatteryBarChart extends Fragment {

    private View view;
    private boolean dontRefreshScreen;
    private ProgressDialog progressDialog;
    private float batlevl;
    private BarChart batteryBarChart;

    private int refreshFrequency=5000;

    private Handler nHandler;

    @Override
    public void onResume() {
        super.onResume();
  //      handlerForGettingData();
       // useHandler();
    }

    @Override
    public void onPause() {
        super.onPause();

      /*  try
        {
            mHandler.removeCallbacks(mRunnable);


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }*/
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       view=inflater.inflate(R.layout.battery_bar,container,false);
       init();
      // handlerForGettingData();
      //  Utils.getCapturedData(getActivity());
        useHandler();
        return view;
    }

    private void handlerForGettingData() {
refreshFrequency=2*60*1000;
        nHandler = new Handler();
        nHandler.postDelayed(nRunnable, refreshFrequency);
    }
    private Runnable nRunnable = new Runnable() {

        @Override
        public void run() {

            try {

                getData();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                nHandler.postDelayed(nRunnable, refreshFrequency);
            }



        }
    };

    private void getData() {


        int currSize = timeStamp.size();
        if(currSize  == mView_HealthStatus.MaxPeriodicDataToSaveInDB && currSize >= 1)
        {
            batteryList.remove(0);

            timeStamp.remove(0);
        }
        batteryList.add(Float.valueOf(Utils.getBattery(getActivity())));
        timeStamp.add(Utils.getCurrentHourMin());

    }

    private void init() {
        batteryBarChart=(BarChart)view.findViewById(R.id.batteryBarchart);
        showProgressDialog();
       /* batteryList=new ArrayList<Float>();
        timeStamp=new ArrayList<>();*/
    }

    private void showProgressDialog() {

        progressDialog=new ProgressDialog(getActivity(),ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setMessage(Constants.LOADING);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    Handler mHandler;
    private void useHandler() {
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, 1000);
    }


    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            //Log.e("Handlers", "Calls");
            if(dontRefreshScreen == false)
            {
                try {
                    if(progressDialog!=null && progressDialog.isShowing())
                    {
                        progressDialog.dismiss();
                    }
                    refreshBatteryUsage();
                    mHandler.postDelayed(mRunnable, mView_HealthStatus.updateDashboardUIIntervalInSeconds*1000);
                }catch(Exception e)
                {
                    Log.e("mView MainAct Frag" , e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    };


    private void refreshBatteryUsage() {

        {
            // creating list of entry
            ArrayList<BarEntry> entries = new ArrayList<>();
            ArrayList<String> labels = new ArrayList<String>();
System.out.println("batterylist "+batteryList +"time "+timeStamp);
            if( batteryList!= null && batteryList.size()>0) {

                for (int i = 0; i < batteryList.size(); i++) {
                    float bat =batteryList.get(i);
                    entries.add(new BarEntry(bat, i));

                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                  
                    labels.add(timeStamp.get(i));
                }
            }else {
                batlevl= Float.parseFloat(Utils.getBattery(getActivity()));
                entries.add(new BarEntry(batlevl, 0));
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                Date resultdate = new Date(System.currentTimeMillis());
                String displaydate = sdf.format(resultdate);
                labels.add(Utils.getCurrentHourMin());
            }

            BarDataSet dataset = new BarDataSet(entries, "battery level %");
            chartCustomization(labels,dataset);

        }
    }

    private void chartCustomization(ArrayList<String> labels, BarDataSet dataset) {
        BarData data = new BarData(labels, dataset);
        batteryBarChart.setData(data);
        batteryBarChart.setVisibleXRangeMaximum(10);
        batteryBarChart.setVisibleXRangeMinimum(10);
        batteryBarChart.setExtraLeftOffset(18);
        batteryBarChart.setExtraRightOffset(18);

        dataset.setValueTextSize(12f);
        dataset.setDrawValues(false);
        //  batteryBarChart.animateXY(2000,2000);
        XAxis xAxis = batteryBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLimitLinesBehindData(false);
        xAxis.setTextSize(14f);
        xAxis.setAvoidFirstLastClipping(true);
        batteryBarChart.setDescription("");
        CustomMarkerView mv = new CustomMarkerView(getActivity(), R.layout.custom_marker_view_layout, labels);
        batteryBarChart.setMarkerView(mv);
        batteryBarChart.invalidate();

    }

}
